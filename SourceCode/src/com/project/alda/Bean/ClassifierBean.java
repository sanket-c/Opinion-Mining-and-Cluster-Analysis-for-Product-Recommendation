package com.project.alda.Bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassifierBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Map<String,Map<String,InfoBean>> classifier;
	private long wordVector = 0;
	
	public ClassifierBean(Map<String,Map<String,InfoBean>> classifier, long wordVector)
	{
		this.classifier = classifier;
		this.setWordVector(wordVector);
	}
	
	public Set<String> getCategoryList()
	{
		Set<String> categoryList = new HashSet<String>();
		
		if(classifier != null)
		{
			categoryList.addAll(classifier.keySet());
		}
		
		return categoryList;
	}
	
	public Set<String> getWords(String category)
	{
		Set<String> wordList = new HashSet<String>();
		
		if(classifier != null && classifier.get(category.trim().toLowerCase()) != null)
		{
			wordList.addAll(classifier.get(category.trim().toLowerCase()).keySet());
			wordList.remove("documentCount");
			wordList.remove("wordCount");
		}
		
		return wordList;
	}
	
	public double getCategoryProbability(String category)
	{
		double probability = 0.0;
		if(classifier != null && classifier.get(category.trim().toLowerCase()) != null)
		{
			probability = ((InfoBean)classifier.get(category.trim().toLowerCase()).get("documentCount")).getProbability();
		}
		return probability;
	}
	
	public double getWordProbability(String category, String word)
	{
		double probability = 0.0;
		
		if(classifier != null && classifier.get(category.trim().toLowerCase()) != null && classifier.get(category.trim().toLowerCase()).get(word.trim().toLowerCase()) != null)
		{
			probability = ((InfoBean)classifier.get(category.trim().toLowerCase()).get(word.trim().toLowerCase())).getProbability();
		}
		
		return probability;
	}
	
	public Map<String,Map<String,InfoBean>> getClassifier()
	{
		return classifier;
	}
	public void setClassifier(Map<String,Map<String,InfoBean>> classifier)
	{
		this.classifier = classifier;
	}

	public long getWordVector() {
		return wordVector;
	}

	public void setWordVector(long wordVector) {
		this.wordVector = wordVector;
	}	
}