package com.project.alda.utils;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

public class CommonProperties 
{
	public static String TRAINING_DATA;
	public static String CLASSIFIER_PATH;
	public static String TESTING_DATA;
	public static String OUTPUT_FILE_PATH;
	public static String OUTPUT_FILE_PATH_PHRASES;
	public static int NUMBER_OF_CLUSTERS;
	public static int NUMBER_OF_ITERATIONS;
	public static String OUTPUT_FILE_PATH_CLUSTERS;
	
	public static String propertyFileName = "configuration.properties";
	
	public CommonProperties() throws Exception 
	{
		try
		{
			Properties properties = new Properties();
			properties.load(new FileInputStream(CommonUtils.getConfigFolderPath()+ propertyFileName));
			Enumeration<Object> bundleKeys = properties.keys();

			while (bundleKeys.hasMoreElements()) 
			{
				String key = bundleKeys.nextElement().toString();
				String value = properties.getProperty(key);

				if (key.equals("TRAINING_DATA")) 
				{
					CommonProperties.TRAINING_DATA = value;
				} 
				else if (key.equals("CLASSIFIER_PATH")) 
				{
					CommonProperties.CLASSIFIER_PATH = value;
				} 
				else if (key.equals("TESTING_DATA")) 
				{
					CommonProperties.TESTING_DATA = value;
				}
				else if (key.equals("OUTPUT_FILE_PATH")) 
				{
					CommonProperties.OUTPUT_FILE_PATH = value;
				}
				else if (key.equals("OUTPUT_FILE_PATH_PHRASES")) 
				{
					CommonProperties.OUTPUT_FILE_PATH_PHRASES = value;
				}
				else if (key.equals("OUTPUT_FILE_PATH_CLUSTERS")) 
				{
					CommonProperties.OUTPUT_FILE_PATH_CLUSTERS = value;
				}
				else if (key.equals("NUMBER_OF_CLUSTERS")) 
				{
					CommonProperties.NUMBER_OF_CLUSTERS = Integer.parseInt(value.trim());
				}
				else if (key.equals("NUMBER_OF_ITERATIONS")) 
				{
					CommonProperties.NUMBER_OF_ITERATIONS = Integer.parseInt(value.trim());
				}
			}
		}
		catch (Exception e) 
		{
			System.out.println("Failed to load property file.");
			throw e;
		}
	}
}