package com.project.alda.utils;

import java.io.File;

public class CommonUtils 
{
	public static String getConfigFolderPath() throws Exception 
	{
		String configFolderPath = null;
		File configFolder = new File("config");
		try 
		{
			configFolderPath = configFolder.getAbsolutePath()+"\\";
		}
		catch (Exception e) 
		{
			throw e;
		}

		return configFolderPath;
	}
	
	public static String getModelFolderPath() throws Exception 
	{
		String modelFolderPath = null;
		File modelFolder = new File("model");
		try 
		{
			modelFolderPath = modelFolder.getAbsolutePath()+"\\";
		}
		catch (Exception e) 
		{
			throw e;
		}

		return modelFolderPath;
	}
}