package com.project.alda.clustering;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.project.alda.Bean.DataBean;
import com.project.alda.Bean.Document;
import com.project.alda.utils.CommonProperties;
import com.project.alda.utils.CommonUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ClusterGenerator 
{
	private MaxentTagger tagger;
	private Set<String> stopwords = new HashSet<String>();
	private long docId;
	private Map<Long, Document> documents = null;
	private static Map<String, Long> wordDocCount = null;
	
	public ClusterGenerator()
	{
		try 
		{
			initialize();
		} 
		catch (Exception e) 
		{
			System.out.println("Failed to initialize classifier.");
			e.printStackTrace();
		}
	}
	
	private void initialize() throws Exception 
	{
		docId = 0;
		wordDocCount = new HashMap<String, Long>();
		tagger = new MaxentTagger(CommonUtils.getModelFolderPath()+"english-left3words-distsim.tagger");
		loadStopWords();
	}
	
	private void loadStopWords() 
	{
		try
		{
			if(stopwords == null)
			{
				stopwords = new HashSet<String>();
			}
			File stopWordFile = new File(CommonUtils.getConfigFolderPath()+"\\stopWords.txt");
			String stopWordsList = FileUtils.readFileToString(stopWordFile);
			stopwords.addAll(Arrays.asList(stopWordsList.split("\r\n")));
		}
		catch(Exception e)
		{
			System.out.println("Failed to load stopwords.");
			stopwords = null;
		}
	}

	public Map<String, List<Long>> generateClusters(List<DataBean> data) 
	{
		List<List<HasWord>> sentences = null;
		List<String> docWords = null;
		if(data != null)
		{
			for(DataBean objDataBean : data)
			{
				docWords = new ArrayList<String>();
				sentences = MaxentTagger.tokenizeText(new StringReader(objDataBean.getContent()));
				if(sentences != null)
				{
					for (List<HasWord> sentence : sentences)
					{
						ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
						for(int i=0;i< taggedSentence.size();i++)
						{
							if(taggedSentence.get(i).tag().toUpperCase().contains("NN"))
							{
								if(stopwords!=null)
								{
									if(stopwords.contains(taggedSentence.get(i).word()))
										continue;
									docWords.add(taggedSentence.get(i).word().toLowerCase());
								}
								else
									docWords.add(taggedSentence.get(i).word().toLowerCase());
							}
						}
					}
					sentences = null;
				}
				addToDocument(objDataBean, docWords);
			}
		}
		return generateClusters(CommonProperties.NUMBER_OF_CLUSTERS);
	}

	private Map<String, List<Long>> generateClusters(int k) 
	{
		int c = getRandomCentroid();
		Map<String, Document> centroids = new HashMap<String, Document>();
		centroids.put("cluster_1",documents.get((long)c));
		documents.get((long)c).setCentroid(true);
		while(centroids.size() < k)
		{
			Document farthestDocument = findFarthestDocument(centroids);
			centroids.put("cluster_"+(centroids.size()+1), farthestDocument);
			farthestDocument.setCentroid(true);
		}
		Map<String, List<Long>> clusters = null;
		for(int i = 0; i < CommonProperties.NUMBER_OF_ITERATIONS; i++)
		{
			clusters = new HashMap<String, List<Long>>();
			for(long id : documents.keySet())
			{
				String cluster = findNearestCluster(centroids, documents.get(id));
				if(clusters.get(cluster) == null)
				{
					List<Long> docInCluster = new ArrayList<Long>();
					docInCluster.add(id);
					clusters.put(cluster, docInCluster);
				}
				else
				{
					clusters.get(cluster).add(id);
				}
			}
			if(i != CommonProperties.NUMBER_OF_ITERATIONS - 1)
				updateCentroids(centroids, clusters);
		}
		return clusters;
	}

	private void updateCentroids(Map<String, Document> centroids, Map<String, List<Long>> clusters) 
	{
		for(String cluster : clusters.keySet())
		{
			Document centroid = new Document();
			for(Long docId : clusters.get(cluster))
			{
				Document d = documents.get(docId);
				if(centroid.getWordFreq() == null)
				{
					Map<String, Double> wordFreq = new HashMap<String, Double>();
					wordFreq.putAll(d.getWordFreq());
					centroid.setWordFreq(wordFreq);
				}
				else
				{
					for(String word : d.getWordFreq().keySet())
					{
						if(centroid.getWordFreq().get(word) == null)
						{
							centroid.getWordFreq().put(word, d.getWordFreq().get(word));
						}
						else
						{
							double wordCount = centroid.getWordFreq().get(word);
							centroid.getWordFreq().put(word, wordCount + d.getWordFreq().get(word));
						}
					}
				}
			}
			for(String word : centroid.getWordFreq().keySet())
			{
				double wordCount = centroid.getWordFreq().get(word);
				centroid.getWordFreq().put(word,wordCount/documents.size());
			}
			centroids.put(cluster, centroid);
		}
	}

	private Document findFarthestDocument(Map<String, Document> centroids) 
	{
		Document farthestDocument = null;
		double farthestDistance = Double.MIN_VALUE;
		for (Document document : documents.values()) 
		{
			if (!document.isCentroid()) 
			{
				double documentDistance = 1 - calculateDistance(document, centroids);
				if (documentDistance > farthestDistance) 
				{
					farthestDistance = documentDistance;
					farthestDocument = document;
				}
			}
		}
		return farthestDocument;
	}
	
	public double calculateDistance(Document document, Map<String, Document> centroids) 
	{
		double distance = Double.MAX_VALUE;
		for (Document centroid : centroids.values()) 
		{
			distance = Math.min(distance, calculateCosineSimilarity(document, centroid));
		}
		return distance;
	}

	private double calculateCosineSimilarity(Document document1, Document document2) 
	{
		double dotProduct = 0;
		double magnitude1 = 0;
		double magnitude2 = 0;
		for(String word : document2.getWordFreq().keySet())
		{
			if(document1.getWordFreq().get(word) != null)
			{
				dotProduct += document1.getWordFreq().get(word) * document2.getWordFreq().get(word);
			}
			magnitude2 += document2.getWordFreq().get(word) * document2.getWordFreq().get(word);
		}
		for(String word : document1.getWordFreq().keySet())
		{
			magnitude1 += document1.getWordFreq().get(word) * document1.getWordFreq().get(word);
		}
		return (dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2)));
	}

	private String findNearestCluster(Map<String, Document> centroids, Document document) 
	{
		String nearestCluster = null;
		double nearestDistance = Double.MAX_VALUE;
		for (String cluster : centroids.keySet()) 
		{
			double clusterDistance = 1 - calculateCosineSimilarity(document, centroids.get(cluster));
			if (clusterDistance < nearestDistance) 
			{
				nearestDistance = clusterDistance;
				nearestCluster = cluster;
			}
		}
		return nearestCluster;
	}

	private int getRandomCentroid() 
	{
		Random randomGenerator = new Random();
		int c = randomGenerator.nextInt(documents.size());
		return c;
	}

	private void addToDocument(DataBean objDataBean, List<String> docWords) 
	{
		if(documents == null)
		{
			documents = new HashMap<Long, Document>();
		}
		Document d = new Document();
		d.setId(++docId);
		d.setContent(objDataBean.getContent());
		Map<String, Double> wordFreq = new HashMap<String, Double>();
		for(String word : docWords)
		{
			if(wordFreq.get(word) == null)
			{
				wordFreq.put(word, 1.0);
			}
			else
			{
				double wordCount = wordFreq.get(word);
				wordFreq.put(word, wordCount + 1);
			}
			
			if(wordDocCount.get(word) == null)
			{
				wordDocCount.put(word, 1L);
			}
			else
			{
				long wordDocumentCount = wordDocCount.get(word);
				wordDocCount.put(word, wordDocumentCount + 1);
			}
		}
		d.setWordFreq(wordFreq);
		documents.put(docId, d);
	}
}