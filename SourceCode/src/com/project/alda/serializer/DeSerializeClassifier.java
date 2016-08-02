package com.project.alda.serializer;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import com.project.alda.Bean.ClassifierBean;
import com.project.alda.utils.CommonProperties;

public class DeSerializeClassifier 
{
	public ClassifierBean deSerializeClassifier()
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		ClassifierBean objClassifierBean = null;
		try
		{
			fis = new FileInputStream(CommonProperties.CLASSIFIER_PATH);
			ois = new ObjectInputStream(fis);
			objClassifierBean = (ClassifierBean) ois.readObject();
		}
		catch(Exception e)
		{
			System.out.println("Failed to read serialized Classifier : ");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(ois != null)
					ois.close();
				if(fis != null)
					fis.close();
			}
			catch(Exception ex)
			{
				System.out.println("Failed to close readers : ");
				ex.printStackTrace();
			}
		}
		return objClassifierBean;
	}
}