package com.project.alda.classifier;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.project.alda.Bean.DataBean;
import com.project.alda.Bean.InfoBean;
import com.project.alda.utils.CommonUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Classifier 
{
	private MaxentTagger tagger;

	public static Set<String> wordVector = new HashSet<String>();

	private Set<String> stopwords = new HashSet<String>();

	private Map<String,Map<String,InfoBean>> classifier = null;

	public Classifier()
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

	public Map<String,Map<String,InfoBean>> classify(List<DataBean> data) 
	{
		List<List<HasWord>> sentences = null;
		List<String> categoryWords = null;
		if(data != null)
		{
			for(DataBean objDataBean : data)
			{
				categoryWords = new ArrayList<String>();
				sentences = MaxentTagger.tokenizeText(new StringReader(objDataBean.getContent()));
				if(sentences != null)
				{
					for (List<HasWord> sentence : sentences)
					{
						ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
						for(int i=0;i< taggedSentence.size();i++)
						{
							if(taggedSentence.get(i).tag().toUpperCase().contains("RB") || taggedSentence.get(i).tag().toUpperCase().contains("JJ"))
							{
								if(stopwords!=null)
								{
									if(stopwords.contains(taggedSentence.get(i).word()))
										continue;
									categoryWords.add(taggedSentence.get(i).word().toLowerCase());
								}
								else
									categoryWords.add(taggedSentence.get(i).word().toLowerCase());

							}
						}
					}
					sentences = null;
				}
				addToClassifier(objDataBean.getRating(), categoryWords);
			}
		}
		return classifier;
	}

	private void addToClassifier(String rating, List<String> categoryWords) 
	{
		if(classifier == null)
		{
			classifier = new HashMap<String,Map<String,InfoBean>>();
		}

		if(classifier.get(rating.trim().toLowerCase()) == null)
		{
			Map<String,InfoBean> words = new HashMap<String, InfoBean>();

			InfoBean docInfoBean = new InfoBean();
			docInfoBean.setCount(1);
			words.put("documentCount", docInfoBean);

			InfoBean wordInfoBean = new InfoBean();
			wordInfoBean.setCount(categoryWords.size());
			words.put("wordCount", wordInfoBean);

			classifier.put(rating.trim().toLowerCase(),words);	
		}
		else
		{
			long docCount = ((InfoBean)classifier.get(rating.trim().toLowerCase()).get("documentCount")).getCount();
			((InfoBean)classifier.get(rating.trim().toLowerCase()).get("documentCount")).setCount(docCount+1);
			long wordCount = ((InfoBean)classifier.get(rating.trim().toLowerCase()).get("wordCount")).getCount();
			((InfoBean)classifier.get(rating.trim().toLowerCase()).get("wordCount")).setCount(wordCount+categoryWords.size());
		}

		for(String word : categoryWords)
		{
			if(classifier.get(rating.trim().toLowerCase()).get(word.trim().toLowerCase()) == null)
			{
				InfoBean wordInfoBean = new InfoBean(); 
				wordInfoBean.setCount(1);
				classifier.get(rating.trim().toLowerCase()).put(word.trim().toLowerCase(),wordInfoBean);
			}
			else
			{
				long wordCategoryCount = ((InfoBean)classifier.get(rating.trim().toLowerCase()).get(word.trim().toLowerCase())).getCount();
				((InfoBean)classifier.get(rating.trim().toLowerCase()).get(word.trim().toLowerCase())).setCount(wordCategoryCount+1);
			}

			wordVector.add(word.trim().toLowerCase());
		}
	}
}