package com.project.alda.serializer;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import com.project.alda.Bean.ClassifierBean;
import com.project.alda.utils.CommonProperties;

public class SerializeClassifier 
{

	public void serializeClassifier(ClassifierBean objClassifierBean) 
	{
		if(objClassifierBean != null)
		{
			try 
			{
				createSerializedClassifier(objClassifierBean);
			} 
			catch (Exception e) 
			{
				System.out.println("Failed to create serialized classsifier : ");
				e.printStackTrace();
			}	
		}
	}
	
	private void createSerializedClassifier(ClassifierBean objClassifierBean) throws Exception
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try
		{
			fos = new FileOutputStream(CommonProperties.CLASSIFIER_PATH); 
			oos = new ObjectOutputStream(fos);
			oos.writeObject(objClassifierBean);
			System.out.println("Serialized Classifier Saved");
		}
		catch(Exception e)
		{
			System.out.println("Failed to create serialized classsifier : ");
			e.printStackTrace();
		}
		finally
		{
			if(oos != null)
				oos.close();
			if(fos != null)
				fos.close();
		}
	}
}