package com.project.alda.Bean;

import java.util.Map;

public class Document 
{
	private long id;
	private String content;
	private Map<String, Double> wordFreq;
	private String cluster;
	private boolean isCentroid;
	
	public long getId() 
	{
		return id;
	}
	
	public void setId(long id) 
	{
		this.id = id;
	}
	
	public String getContent() 
	{
		return content;
	}
	
	public void setContent(String content) 
	{
		this.content = content;
	}

	public Map<String, Double> getWordFreq() 
	{
		return wordFreq;
	}

	public void setWordFreq(Map<String, Double> wordFreq) 
	{
		this.wordFreq = wordFreq;
	}

	public String getCluster() 
	{
		return cluster;
	}

	public void setCluster(String cluster) 
	{
		this.cluster = cluster;
	}

	public boolean isCentroid() 
	{
		return isCentroid;
	}

	public void setCentroid(boolean isCentroid) 
	{
		this.isCentroid = isCentroid;
	}
}