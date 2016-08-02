package com.project.alda.Bean;

public class DataBean 
{
	private String reviewId;
	private String reviewerName;
	private String content;
	private String summary;
	private String rating;
	private String category;

	public String getContent() 
	{
		return content;
	}

	public void setContent(String content) 
	{
		this.content = content;
	}

	public String getReviewId() 
	{
		return reviewId;
	}

	public void setReviewId(String reviewId) 
	{
		this.reviewId = reviewId;
	}

	public String getReviewerName() 
	{
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) 
	{
		this.reviewerName = reviewerName;
	}

	public String getSummary() 
	{
		return summary;
	}

	public void setSummary(String summary) 
	{
		this.summary = summary;
	}

	public String getRating() 
	{
		return rating;
	}

	public void setRating(String rating) 
	{
		this.rating = rating;
	}

	public String getCategory() 
	{
		return category;
	}

	public void setCategory(String category) 
	{
		this.category = category;
	}
}