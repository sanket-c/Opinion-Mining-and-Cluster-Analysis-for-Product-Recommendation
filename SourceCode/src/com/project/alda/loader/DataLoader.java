package com.project.alda.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.project.alda.Bean.DataBean;

public class DataLoader 
{
	private List<DataBean> dataBeanList = null;
	private static ObjectMapper mapper = null;

	public DataLoader()
	{
		initialize();
	}

	public List<DataBean> loadData(String path) throws Exception 
	{
		try
		{
			File inputFolder = new File(path);
			readFilesForFolder(inputFolder);
			return dataBeanList;
		}
		catch(Exception e)
		{
			System.out.println("Failed while reading data");
			throw e;
		}
	}

	private void initialize() 
	{
		if(dataBeanList == null)
		{
			dataBeanList = new ArrayList<DataBean>();
		} 	
		if(mapper == null)
		{
			mapper = new ObjectMapper();
		}
	}

	public void readFilesForFolder(File folder) throws Exception 
	{
		if(folder.listFiles() != null)
		{
			for (File file : folder.listFiles()) 
			{
				if (file.isDirectory()) 
				{
					readFilesForFolder(file);
				} 
				else 
				{
					String data = FileUtils.readFileToString(file);
					addDataToList(data,file.getName().substring(0, file.getName().lastIndexOf(".")));
				}
			}
		}
	}

	private void addDataToList(String data, String category) throws Exception 
	{
		String[] dataJsons = data.split("\n");
		for(int i = 0; i < dataJsons.length; i++)
		{
			JsonNode dataJson = mapper.readTree(dataJsons[i]);
			if(dataJson.path("Content").getTextValue() != null && !dataJson.path("Content").getTextValue().trim().equalsIgnoreCase(""))
			{
				DataBean objDataBean = new DataBean();
				
				objDataBean.setReviewId(dataJson.path("ReviewID").getTextValue());
				objDataBean.setReviewerName(dataJson.path("Author").getTextValue());
				objDataBean.setContent(dataJson.path("Content").getTextValue());
				objDataBean.setSummary(dataJson.path("Title").getTextValue());
				objDataBean.setRating(dataJson.path("Overall").getTextValue());
				objDataBean.setCategory(category);

				dataBeanList.add(objDataBean);
			}
		}
	}
}