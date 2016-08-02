package com.project.alda.posteriorCalculator;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.project.alda.Bean.ClassifierBean;
import com.project.alda.utils.CommonUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosteriorCalculator 
{
	private ClassifierBean objClassifierBean = null;
	private MaxentTagger tagger;
	private static Set<String> stopwords = new HashSet<String>();

	public PosteriorCalculator(ClassifierBean objClassifierBean) 
	{
		this.objClassifierBean = objClassifierBean;
		initialize();
		loadStopWords();
	}

	private void initialize() 
	{
		try
		{
			tagger = new MaxentTagger(CommonUtils.getModelFolderPath()+"english-left3words-distsim.tagger");
		}
		catch(Exception e)
		{
			System.out.println("Failed to initialize Posterior Calculator.");
			e.printStackTrace();
		}
	}
	
	private void loadStopWords() 
	{
		try 
		{	
			String stopword = FileUtils.readFileToString(new File(CommonUtils.getConfigFolderPath()+"\\stopWords.txt"));
			stopwords.addAll(Arrays.asList(stopword.split("\r\n")));	
		} 
		catch (Exception e)
		{
			stopwords = null;
			e.printStackTrace();
			System.out.println("Error while reading stopword file");
		}	
	}
	
	public List<String> getCategories(String text)
	{
		Map<String, Double> categoryScore = null;
		List<String> words = getWords(text);
		categoryScore = calculateCategoryScore(words);
		return sortCategories(categoryScore);
	}
	
	public List<String> sortCategories(Map<String, Double> categoryScore)
	{
		List<String> catgories = new ArrayList<String>();
		double score = 0;
		for(Entry<String, Double> entry : categoryScore.entrySet())
		{
			if(entry.getValue()>score)
			{	
				catgories.clear();
				score = entry.getValue();
				catgories.add(entry.getKey());
			}
			else if(score== entry.getValue())
			{
				catgories.add(entry.getKey());
			}
		}
		return catgories;
	}

	private List<String> getWords(String text)
	{
		List<List<HasWord>> sentences = null;
		
		List<String> wordsList = new ArrayList<String>();
		sentences = MaxentTagger.tokenizeText(new StringReader(text.toLowerCase()));

		if(sentences != null)
		{
			for (List<HasWord> sentence : sentences)
			{
				ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
				for(int i=0;i< taggedSentence.size();i++)
				{
					if(taggedSentence.get(i).tag().toUpperCase().contains("JJ") || taggedSentence.get(i).tag().toUpperCase().contains("RB"))
					{
						if(stopwords != null)
						{
							if(stopwords.contains(taggedSentence.get(i).word()))
								continue;
							wordsList.add(taggedSentence.get(i).word().toLowerCase());
						}
						else
						{
							wordsList.add(taggedSentence.get(i).word().toLowerCase());
						}
					}
				}
			}
		}
		return wordsList;
	}
	
	private Map<String, Double> calculateCategoryScore(List<String> words) 
	{	
		double score = 0;
		Set<String> categoryWords ;
		Map<String, Double> categoryScore = new HashMap<String, Double>();
		

		if(objClassifierBean.getCategoryList() != null)
		{
			for(String category : objClassifierBean.getCategoryList())
			{
				categoryWords = objClassifierBean.getWords(category) ;
				score = objClassifierBean.getCategoryProbability(category);
				for(String word : words)
				{
					if(categoryWords.contains(word))
					{
						score = score * objClassifierBean.getWordProbability(category, word);
					}
					/*else
					{
						score = score * ( 1.0 / (  objClassifierBean.getTotalWordsInCategoryIncludingFrequency(category) + objClassifierBean.getWordVector()));
					}*/
				}
				categoryScore.put(category,score);
			}
		}
		return categoryScore;
	}
}