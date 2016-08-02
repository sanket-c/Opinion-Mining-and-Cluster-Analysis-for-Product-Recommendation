package com.project.alda.calculator;

import java.util.Map;
import java.util.Map.Entry;

import com.project.alda.Bean.ClassifierBean;
import com.project.alda.Bean.InfoBean;

public class Calculator 
{
	public ClassifierBean calculate(Map<String, Map<String, InfoBean>> classifier, long totalDocs, long wordVector) 
	{
		double categoryProbability = 0;
		long docsInCategory = 0;
		long wordsInCategory = 0;
		double wordLikelihood = 0;
		ClassifierBean objClassifierBean = null;

		if(classifier !=null)
		{
			for(Entry<String,Map<String,InfoBean>> entrySet : classifier.entrySet())
			{
				docsInCategory = entrySet.getValue().get("documentCount").getCount();
				
				wordsInCategory = entrySet.getValue().get("wordCount").getCount();

				categoryProbability =(float)docsInCategory/totalDocs;

				entrySet.getValue().get("documentCount").setProbability(categoryProbability);

				for(Entry<String,InfoBean> wordEntrySet : entrySet.getValue().entrySet())
				{
					if(wordEntrySet.getKey().equalsIgnoreCase("documentCount"))
						continue;
					if(wordEntrySet.getKey().equalsIgnoreCase("wordCount"))
						continue;

					long freqOfWord = wordEntrySet.getValue().getCount();

					wordLikelihood =(float) (freqOfWord + 1) / (wordsInCategory + wordVector);

					wordEntrySet.getValue().setProbability(wordLikelihood);
				}
			}

			objClassifierBean = new ClassifierBean(classifier, wordVector);
		}
		return objClassifierBean;
	}
}