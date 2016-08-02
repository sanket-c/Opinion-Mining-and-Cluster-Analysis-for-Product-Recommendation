package com.project.alda.opinionExtractor;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.project.alda.utils.CommonUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class OpinionExtraction 
{
	private MaxentTagger tagger;
	private Set<String> stopwords = new HashSet<String>();
	private List<String> stopwordPOStagset = null;
	
	public OpinionExtraction() 
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
		stopwordPOStagset = Arrays.asList("CC", "DT", "EX", "IN", "LS", "TO", "UH", "WDT", "WP" ,"WP$", "WRB", "PDT", "POS", "-LRB-", "-RRB-");
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

	public Set<String> extractOpinionPhrases(String text) throws Exception 
	{
		Set<String> phrases = new HashSet<String>();
		List<List<HasWord>> sentences = null;
		if(text != null && !text.trim().equalsIgnoreCase(""))
		{
			sentences = MaxentTagger.tokenizeText(new StringReader(text));
			if(sentences != null)
			{
				for (List<HasWord> sentence : sentences)
				{
					ArrayList<TaggedWord> taggedSentence = tagger.tagSentence(sentence);
					for(int i=0;i< taggedSentence.size();i++)
					{
						String phrase = null;
						if(i < taggedSentence.size()-3)
						{
							phrase = formPhraseUsingPOSTagging(taggedSentence.get(i),taggedSentence.get(i+1),taggedSentence.get(i+2),taggedSentence.get(i+3));
							if(phrase != null && !phrase.trim().equalsIgnoreCase(""))
							{
								phrases.add(phrase);
								i+=4;
							}
						}
						if(phrase == null && i < taggedSentence.size()-2)
						{
							phrase = formPhraseUsingPOSTagging(taggedSentence.get(i),taggedSentence.get(i+1),taggedSentence.get(i+2));
							if(phrase != null && !phrase.trim().equalsIgnoreCase(""))
							{
								phrases.add(phrase);
								i+=3;
							}
						}
						if(phrase == null && i < taggedSentence.size()-1)
						{
							phrase = formPhraseUsingPOSTagging(taggedSentence.get(i),taggedSentence.get(i+1));
							if(phrase != null && !phrase.trim().equalsIgnoreCase(""))
							{
								phrases.add(phrase);
								i+=2;
							}
						}
					}
				}
			}
		}
		return phrases;
	}
	
	private String formPhraseUsingPOSTagging(TaggedWord token1,	TaggedWord token2, TaggedWord token3, TaggedWord token4)  throws Exception
	{
		String phrase = null;

		String word1 = token1.word();
		String pos1 = token1.tag();
		String word2 = token2.word();
		String pos2 = token2.tag();
		String word3 = token3.word();
		String pos3 = token3.tag();
		String word4 = token4.word();
		String pos4 = token4.tag();

		try
		{
			if (word1.length()<2 || word2.length() <2 || word3.length() <2 || word4.length() <2)
			{
				return phrase;
			}

			if (stopwords.contains(word1.toLowerCase()) || stopwords.contains(word4.toLowerCase()) || stopwordPOStagset.contains(pos1) || stopwordPOStagset.contains(pos4))
			{
				return phrase;
			}

			if (pos4.contains("RB") || pos4.contains("JJ") || pos4.contains("VB"))
				return phrase;

			if (pos1.contains("VB") && pos2.contains("VB") && pos3.contains("VB") && pos4.contains("VB"))
				return phrase;

			phrase = word1 + " " + word2 + " " + word3 + " " + word4; 
		}
		catch (Exception e) 
		{
			throw e;
		}
		return phrase;
	}
	
	private String formPhraseUsingPOSTagging(TaggedWord token1,	TaggedWord token2, TaggedWord token3)  throws Exception
	{
		String phrase = null;

		String word1 = token1.word();
		String pos1 = token1.tag();
		String word2 = token2.word();
		String pos2 = token2.tag();
		String word3 = token3.word();
		String pos3 = token3.tag();

		try
		{
			if (word1.length()<2 || word2.length() <2 || word3.length() <2)
			{
				return phrase;
			}

			if (stopwords.contains(word1.toLowerCase()) || stopwords.contains(word3.toLowerCase()) || stopwordPOStagset.contains(pos1) || stopwordPOStagset.contains(pos3))
			{
				return phrase;
			}

			if (pos3.contains("RB") || pos3.contains("JJ") || pos3.contains("VB"))
				return phrase;

			if (pos1.contains("VB") && pos2.contains("VB") && pos3.contains("VB"))
				return phrase;

			phrase = word1 + " " + word2 + " " + word3; 
		}
		catch (Exception e) 
		{
			throw e;
		}
		return phrase;
	}
	
	private String formPhraseUsingPOSTagging(TaggedWord token1,	TaggedWord token2)  throws Exception
	{
		String phrase = null;

		String word1 = token1.word();
		String pos1 = token1.tag();
		String word2 = token2.word();
		String pos2 = token2.tag();

		try
		{
			if (word1.length()<2 || word2.length() <2)
			{
				return phrase;
			}

			if (stopwords.contains(word1.toLowerCase()) || stopwords.contains(word2.toLowerCase()) || stopwordPOStagset.contains(pos1) || stopwordPOStagset.contains(pos2))
			{
				return phrase;
			}

			if (pos2.contains("RB") || pos2.contains("JJ") || pos2.contains("VB"))
				return phrase;

			if (pos1.contains("VB") && pos2.contains("VB"))
				return phrase;

			phrase = word1 + " " + word2; 
		}
		catch (Exception e) 
		{
			throw e;
		}
		return phrase;
	}
}