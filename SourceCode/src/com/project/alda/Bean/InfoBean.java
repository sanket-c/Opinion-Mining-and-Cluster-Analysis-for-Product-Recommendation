package com.project.alda.Bean;

import java.io.Serializable;

public class InfoBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private long count;
	private double probability;
	private long docCount=0;
	
	public long getCount()
	{
		return count;
	}
	public void setCount(long count) 
	{
		this.count = count;
	}
	public double getProbability() 
	{
		return probability;
	}
	public void setProbability(double probability) 
	{
		this.probability = probability;
	}
	public long getDocCount() {
		return docCount;
	}
	public void setDocCount(long docCount) {
		this.docCount = docCount;
	}
}