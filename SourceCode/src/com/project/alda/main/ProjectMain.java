package com.project.alda.main;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.project.alda.Bean.ClassifierBean;
import com.project.alda.Bean.DataBean;
import com.project.alda.Bean.InfoBean;
import com.project.alda.calculator.Calculator;
import com.project.alda.classifier.Classifier;
import com.project.alda.clustering.ClusterGenerator;
import com.project.alda.loader.DataLoader;
import com.project.alda.opinionExtractor.OpinionExtraction;
import com.project.alda.posteriorCalculator.PosteriorCalculator;
import com.project.alda.serializer.DeSerializeClassifier;
import com.project.alda.serializer.SerializeClassifier;
import com.project.alda.utils.CommonProperties;

public class ProjectMain 
{	
	private static WritableWorkbook workbook = null;
	private static WritableSheet sheet = null;
	private static WritableCellFormat timesBoldUnderline;
	private static WritableCellFormat times;
	private static int row = 1;
	
	private static int total = 0;
	private static int good = 0;
	private static int bad = 0;
	private static int good_bad = 0;
	private static int bad_good= 0;
	
	public ProjectMain() throws Exception 
	{
		try
		{
			initialize();
		}
		catch(Exception e)
		{
			System.out.println("Failed while initializing Main.");
			throw e;
		}
	}
	
	private void initialize() throws Exception 
	{
		new CommonProperties();
	}

	public static void main(String[] args) 
	{
		try
		{
			ProjectMain objProjectMain = new ProjectMain();
			objProjectMain.createClassifier();
			objProjectMain.testClassifier();
			objProjectMain.extractOpinionPhrases();
			objProjectMain.formCluster();
		}
		catch(Exception e)
		{
			System.out.println("Failed while processig");
			e.printStackTrace();
		}
	}

	public void formCluster() throws Exception
	{
		ClusterGenerator objClusterGenerator = new ClusterGenerator();
		DataLoader loader = new DataLoader();
		List<DataBean> testData = loader.loadData(CommonProperties.TESTING_DATA);
		Map<String, List<Long>> clusters = objClusterGenerator.generateClusters(testData);
		for(String cluster : clusters.keySet())
		{
			writeExcelForClusters(cluster, clusters.get(cluster).toString());
		}
		workbook.write();
		workbook.close();
		workbook = null;
		row = 1;
		testData = null;
	}

	public void extractOpinionPhrases() throws Exception 
	{
		OpinionExtraction objOpinionExtraction = new OpinionExtraction();
		DataLoader loader = new DataLoader();
		List<DataBean> testData = loader.loadData(CommonProperties.TESTING_DATA);
		for(DataBean data : testData)
		{
			String phrases = objOpinionExtraction.extractOpinionPhrases(data.getContent()).toString();
			writeExcelForEntities(data.getContent(), phrases);
		}
		workbook.write();
		workbook.close();
		workbook = null;
		row = 1;
		testData = null;
	}

	public void createClassifier() throws Exception 
	{
		DataLoader loader = new DataLoader();
		List<DataBean> data = loader.loadData(CommonProperties.TRAINING_DATA);
		Classifier classifier = new Classifier();
		Map<String,Map<String,InfoBean>> c = classifier.classify(data);
		Calculator calculator = new Calculator();
		ClassifierBean objClassifierBean = calculator.calculate(c, data.size(), Classifier.wordVector.size());
		SerializeClassifier serializer = new SerializeClassifier();
		serializer.serializeClassifier(objClassifierBean);
		data = null;
		c = null;
		objClassifierBean = null;
	}
	
	public void testClassifier() throws Exception 
	{
		DeSerializeClassifier deSerializer = new DeSerializeClassifier();
		ClassifierBean objClassifierBean = deSerializer.deSerializeClassifier();
		PosteriorCalculator objPosteriorCalculator = new PosteriorCalculator(objClassifierBean);
		DataLoader loader = new DataLoader();
		List<DataBean> testData = loader.loadData(CommonProperties.TESTING_DATA);
		for(DataBean data : testData)
		{
			String rating = objPosteriorCalculator.getCategories(data.getContent()).toString();
			writeExcel(data.getContent(), data.getRating(), rating);
		}
		writeConfusionMatrix();
		workbook.write();
		workbook.close();
		workbook = null;
		row = 1;
		testData = null;
		objClassifierBean = null;
	}
	
	private void writeConfusionMatrix() throws Exception 
	{
		workbook.createSheet("Analysis", 1);
		sheet = workbook.getSheet(1);
		
		addCaption(sheet, 0, 0, "Total Documents = "+total);
		addCaption(sheet, 1, 0, "Predicted Good");
		addCaption(sheet, 2, 0, "Predicted Bad");
		addCaption(sheet, 0, 1, "Actual Good");
		addCaption(sheet, 0, 2, "Actual Bad");
		
		addLabel(sheet, 1, 1, ""+good);
		addLabel(sheet, 2, 1, ""+good_bad);
		addLabel(sheet, 1, 2, ""+bad_good);
		addLabel(sheet, 2, 2, ""+bad);
		
		addCaption(sheet, 0, 4, "Accuracy");
		addLabel(sheet, 1, 4, ""+((float)(good+bad)/total)*100);
		
		addCaption(sheet, 0, 5, "Error Rate");
		addLabel(sheet, 1, 5, ""+((float)(good_bad+bad_good)/total)*100);
	}

	private static void writeExcel(String content, String actualRating, String predictedRating) throws Exception 
	{
		if(workbook == null)
		{
			initializeWorkBook("rating");
		}
		total++;
		predictedRating = predictedRating.substring(1,predictedRating.length()-1);
		if(actualRating.trim().equalsIgnoreCase(predictedRating) && actualRating.trim().equalsIgnoreCase("good"))
			good++;
		if(actualRating.trim().equalsIgnoreCase(predictedRating) && actualRating.trim().equalsIgnoreCase("bad"))
			bad++;
		if(!actualRating.trim().equalsIgnoreCase(predictedRating) && actualRating.trim().equalsIgnoreCase("bad"))
			bad_good++;
		if(!actualRating.trim().equalsIgnoreCase(predictedRating) && actualRating.trim().equalsIgnoreCase("good"))
			good_bad++;
		addLabel(sheet, 0, row, content);
		addLabel(sheet, 1, row, actualRating);
		addLabel(sheet, 2, row, predictedRating);
		row++;
	}
	
	private static void writeExcelForClusters(String cluster, String documents) throws Exception 
	{
		if(workbook == null)
		{
			initializeWorkBook("cluster");
		}	
		addLabel(sheet, 0, row, cluster);
		addLabel(sheet, 1, row, documents);
		row++;
	}
	
	private static void writeExcelForEntities(String content, String phrases) throws Exception 
	{
		if(workbook == null)
		{
			initializeWorkBook("entity");
		}	
		addLabel(sheet, 0, row, content);
		addLabel(sheet, 1, row, phrases);
		row++;
	}

	private static void addLabel(WritableSheet sheet, int column, int row, String s) throws Exception
	{
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}

	private static void initializeWorkBook(String type) throws Exception 
	{
		try 
		{
			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale(new Locale("en", "EN"));
			if(type.equalsIgnoreCase("rating"))
			{
				workbook = Workbook.createWorkbook(new File(CommonProperties.OUTPUT_FILE_PATH), wbSettings);
			}
			else if(type.equalsIgnoreCase("entity"))
			{
				workbook = Workbook.createWorkbook(new File(CommonProperties.OUTPUT_FILE_PATH_PHRASES), wbSettings);
			}
			else if(type.equalsIgnoreCase("cluster"))
			{
				workbook = Workbook.createWorkbook(new File(CommonProperties.OUTPUT_FILE_PATH_CLUSTERS), wbSettings);
			}
			
			workbook.createSheet("Result", 0);
			sheet = workbook.getSheet(0);
			createLabel(sheet, type);
		} 
		catch (Exception e) 
		{
			System.out.println("Failed to initialize execl");
			throw e;
		}

	}

	private static void createLabel(WritableSheet sheet, String type) throws Exception 
	{
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		times = new WritableCellFormat(times10pt);
		times.setWrap(true);
		WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		timesBoldUnderline.setWrap(true);
		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);
		
		if(type.equalsIgnoreCase("rating"))
		{
			addCaption(sheet, 0, 0, "Review");
			addCaption(sheet, 1, 0, "Actual Rating");
			addCaption(sheet, 2, 0, "Predicted Rating");
		}
		else if(type.equalsIgnoreCase("entity"))
		{
			addCaption(sheet, 0, 0, "Review");
			addCaption(sheet, 1, 0, "Phrases");
		}
		else if(type.equalsIgnoreCase("cluster"))
		{
			addCaption(sheet, 0, 0, "Cluster");
			addCaption(sheet, 1, 0, "Documents");
		}
	}

	private static void addCaption(WritableSheet sheet, int column, int row, String s) throws Exception 
	{
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		sheet.addCell(label);
	}
}