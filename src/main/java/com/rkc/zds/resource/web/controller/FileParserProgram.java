package com.rkc.zds.resource.web.controller;

import static gate.Utils.stringFor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.Paragraph;
//import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.rkc.zds.resource.util.Converter;
import com.rkc.zds.resource.util.DocToPDFConverter;
import com.rkc.zds.resource.util.DocxToPDFConverter;
import com.rkc.zds.resource.util.OdtToPDF;
//import com.rkc.zds.resource.util.PptToPDFConverter;
//import com.rkc.zds.resource.util.PptxToPDFConverter;
//import com.rkc.zds.resource.util.MainClass.CommandLineValues;
//import com.rkc.zds.resource.util.MainClass.DOC_TYPE;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.util.GateException;
import gate.util.Out;

@Component
public class FileParserProgram {
	
	public static final String VERSION_STRING = "\nDocs to PDF Converter Version 1.7 (8 Dec 2013)\n\nThe MIT License (MIT)\nCopyright (c) 2013-2014 Yeo Kheng Meng";
	
	public enum DOC_TYPE {
		DOC,
		DOCX,
		PPT,
		PPTX,
		ODT
	}
	
	public File parseToHTMLUsingApacheTikka(String file) throws IOException, SAXException, TikaException {
		String ext = FilenameUtils.getExtension(file);
		String outputFileFormat = "";
		if (ext.equalsIgnoreCase("html") | ext.equalsIgnoreCase("pdf") | ext.equalsIgnoreCase("doc")
				| ext.equalsIgnoreCase("docx")) {
			outputFileFormat = ".html";
		} else if (ext.equalsIgnoreCase("txt") | ext.equalsIgnoreCase("rtf")) {
			outputFileFormat = ".txt";
		} else {
			System.out.println("Input format of the file " + file + " is not supported.");
			return null;
		}
		String OUTPUT_FILE_NAME = FilenameUtils.removeExtension(file) + outputFileFormat;
		ContentHandler handler = new ToXMLContentHandler();
		InputStream stream = new FileInputStream(file);
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		try {
			parser.parse(stream, handler, metadata);
			FileWriter htmlFileWriter = new FileWriter(OUTPUT_FILE_NAME);
			htmlFileWriter.write(handler.toString());
			htmlFileWriter.flush();
			htmlFileWriter.close();
			return new File(OUTPUT_FILE_NAME);
		} finally {
			stream.close();
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject loadGateAndAnnie(File file) throws GateException, IOException {
		System.setProperty("gate.site.config", System.getProperty("user.dir") + "/GATEFiles/gate.xml");
		if (Gate.getGateHome() == null)
			Gate.setGateHome(new File(System.getProperty("user.dir") + "/GATEFiles"));
		if (Gate.getPluginsHome() == null)
			Gate.setPluginsHome(new File(System.getProperty("user.dir") + "/GATEFiles/plugins"));
		Gate.init();

		Annie annie = new Annie();
		annie.initAnnie();

		Corpus corpus = Factory.newCorpus("Annie corpus");
		URL u = file.toURI().toURL();
		FeatureMap params = Factory.newFeatureMap();
		params.put("sourceUrl", u);
		params.put("preserveOriginalContent", new Boolean(true));
		params.put("collectRepositioningInfo", new Boolean(true));
		Out.prln("Creating doc for " + u);
		Document gateFile = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
		corpus.add(gateFile);

		annie.setCorpus(corpus);
		annie.execute();

		Iterator iter = corpus.iterator();
		JSONObject parsedJSON = new JSONObject();
		Out.prln("Started parsing...");
		if (iter.hasNext()) {
			JSONObject profileJSON = new JSONObject();
			Document doc = (Document) iter.next();
			AnnotationSet defaultAnnotSet = doc.getAnnotations();

			AnnotationSet curAnnSet;
			Iterator it;
			Annotation currAnnot;

			// Name
			curAnnSet = defaultAnnotSet.get("NameFinder");
			if (curAnnSet.iterator().hasNext()) {
				currAnnot = (Annotation) curAnnSet.iterator().next();
				String gender = (String) currAnnot.getFeatures().get("gender");
				if (gender != null && gender.length() > 0) {
					profileJSON.put("gender", gender);
				}

				// Needed name Features
				JSONObject nameJson = new JSONObject();
				String[] nameFeatures = new String[] { "firstName", "middleName", "surname" };
				for (String feature : nameFeatures) {
					String s = (String) currAnnot.getFeatures().get(feature);
					if (s != null && s.length() > 0) {
						nameJson.put(feature, s);
					}
				}
				profileJSON.put("name", nameJson);
			}

			curAnnSet = defaultAnnotSet.get("TitleFinder");
			if (curAnnSet.iterator().hasNext()) {
				currAnnot = (Annotation) curAnnSet.iterator().next();
				String title = stringFor(doc, currAnnot);
				if (title != null && title.length() > 0) {
					profileJSON.put("title", title);
				}
			}

			String[] annSections = new String[] { "EmailFinder", "AddressFinder", "PhoneFinder", "URLFinder" };
			String[] annKeys = new String[] { "email", "address", "phone", "url" };
			for (short i = 0; i < annSections.length; i++) {
				String annSection = annSections[i];
				curAnnSet = defaultAnnotSet.get(annSection);
				it = curAnnSet.iterator();
				JSONArray sectionArray = new JSONArray();
				while (it.hasNext()) {
					currAnnot = (Annotation) it.next();
					String s = stringFor(doc, currAnnot);
					if (s != null && s.length() > 0) {
						sectionArray.add(s);
					}
				}
				if (sectionArray.size() > 0) {
					profileJSON.put(annKeys[i], sectionArray);
				}
			}
			if (!profileJSON.isEmpty()) {
				parsedJSON.put("basics", profileJSON);
			}
			
			//String[] otherSections = new String[] {"education_and_training", "skills", "accomplishments",

			String[] otherSections = new String[] { "summary", "education_and_training", "skills", "accomplishments",
					"awards", "credibility", "extracurricular", "misc" };
			for (String otherSection : otherSections) {
				curAnnSet = defaultAnnotSet.get(otherSection);
				it = curAnnSet.iterator();
				JSONArray subSections = new JSONArray();
				while (it.hasNext()) {
					JSONObject subSection = new JSONObject();
					currAnnot = (Annotation) it.next();
					String key = (String) currAnnot.getFeatures().get("sectionHeading");
					String value = stringFor(doc, currAnnot);
					if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
						subSection.put(key, value);
					}
					if (!subSection.isEmpty()) {
						subSections.add(subSection);
					}
				}
				if (!subSections.isEmpty()) {
					parsedJSON.put(otherSection, subSections);
				}
			}

			curAnnSet = defaultAnnotSet.get("work_experience");
			it = curAnnSet.iterator();
			JSONArray workExperiences = new JSONArray();
			while (it.hasNext()) {
				JSONObject workExperience = new JSONObject();
				currAnnot = (Annotation) it.next();
				String key = (String) currAnnot.getFeatures().get("sectionHeading");
				if (key.equals("work_experience_marker")) {
					// JSONObject details = new JSONObject();
					String[] annotations = new String[] { "date_start", "date_end", "jobtitle", "organization" };
					for (String annotation : annotations) {
						String v = (String) currAnnot.getFeatures().get(annotation);
						if (!StringUtils.isBlank(v)) {
							workExperience.put(annotation, v);
						}
					}
					key = "text";

				}
				String value = stringFor(doc, currAnnot);
				if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
					workExperience.put(key, value);
				}
				if (!workExperience.isEmpty()) {
					workExperiences.add(workExperience);
				}

			}
			if (!workExperiences.isEmpty()) {
				parsedJSON.put("work_experience", workExperiences);
			}

		}
		Out.prln("Completed parsing...");
		return parsedJSON;
	}

	public static Converter processArguments(String[] args) throws Exception{
		CommandLineValues values = new CommandLineValues();
		CmdLineParser parser = new CmdLineParser(values);

		Converter converter = null;
		try {
			//parser.parseArgument(args);

			boolean version = values.version;

			if(version){
				System.out.println(VERSION_STRING);
				System.exit(0);
			}


			//String inPath = values.inFilePath;
			//String outPath = values.outFilePath;
			
			String inPath = args[0];
			String outPath = args[1];
			
			boolean shouldShowMessages = values.verbose;

			if(inPath == null){
				parser.printUsage(System.err);
				throw new IllegalArgumentException();
			}

			if(outPath == null){
				outPath = changeExtensionToPDF(inPath);
			}


			String lowerCaseInPath = inPath.toLowerCase();
			
			InputStream inStream = getInFileStream(inPath);
			OutputStream outStream = getOutFileStream(outPath);
			
			if(values.type == null){
				if(lowerCaseInPath.endsWith("doc")){
					converter = new DocToPDFConverter(inStream, outStream, shouldShowMessages, true);
				} else if (lowerCaseInPath.endsWith("docx")){
					converter = new DocxToPDFConverter(inStream, outStream, shouldShowMessages, true);
//				} else if(lowerCaseInPath.endsWith("ppt")){
//					converter = new PptToPDFConverter(inStream, outStream, shouldShowMessages, true);
//				} else if(lowerCaseInPath.endsWith("pptx")){
//					converter = new PptxToPDFConverter(inStream, outStream, shouldShowMessages, true);
				} else if(lowerCaseInPath.endsWith("odt")){
					converter = new OdtToPDF(inStream, outStream, shouldShowMessages, true);
				} else {
					converter = null;
				}


			} else {

				switch(values.type){
				case DOC: converter = new DocToPDFConverter(inStream, outStream, shouldShowMessages, true);
				break; 
				case DOCX: converter = new DocxToPDFConverter(inStream, outStream, shouldShowMessages, true);
				break;
//				case PPT:  converter = new PptToPDFConverter(inStream, outStream, shouldShowMessages, true);
//				break;
//				case PPTX: converter = new PptxToPDFConverter(inStream, outStream, shouldShowMessages, true);
//				break;
				case ODT: converter = new OdtToPDF(inStream, outStream, shouldShowMessages, true);
				break;
				default: converter = null;
				break;

				}


			}

			
		} catch (Exception e) {
			// handling of wrong arguments
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}

		return converter;

	}
	
	//From http://stackoverflow.com/questions/941272/how-do-i-trim-a-file-extension-from-a-string-in-java
	public static String changeExtensionToPDF(String originalPath) {

//		String separator = System.getProperty("file.separator");
		String filename = originalPath;

//		// Remove the path upto the filename.
//		int lastSeparatorIndex = originalPath.lastIndexOf(separator);
//		if (lastSeparatorIndex == -1) {
//			filename = originalPath;
//		} else {
//			filename = originalPath.substring(lastSeparatorIndex + 1);
//		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");

		String removedExtension;
		if (extensionIndex == -1){
			removedExtension =  filename;
		} else {
			removedExtension =  filename.substring(0, extensionIndex);
		}
		String addPDFExtension = removedExtension + ".pdf";

		return addPDFExtension;
	}
	
	protected static InputStream getInFileStream(String inputFilePath) throws FileNotFoundException{
		File inFile = new File(inputFilePath);
		FileInputStream iStream = new FileInputStream(inFile);
		return iStream;
	}
	
	protected static OutputStream getOutFileStream(String outputFilePath) throws IOException{
		File outFile = new File(outputFilePath);
		
		try{
			//Make all directories up to specified
			outFile.getParentFile().mkdirs();
		} catch (NullPointerException e){
			//Ignore error since it means not parent directories
		}
		
		outFile.createNewFile();
		FileOutputStream oStream = new FileOutputStream(outFile);
		return oStream;
	}
	
	public static void convertToPDF(String[] args){
		Converter converter;

		try{
			converter = processArguments(args);
		} catch (Exception e){
			System.out.println("\n\nInput\\Output file not specified properly.");
			return;
		}


		if(converter == null){
			System.out.println("Unable to determine type of input file.");
		} else {
			try {
				converter.convert();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static class CommandLineValues {

		@Option(name = "-type", aliases = "-t", required = false, usage = "Specifies doc converter. Leave blank to let program decide by input extension.")
		public DOC_TYPE type = null;


		@Option(name = "-inputPath", aliases = {"-i", "-in", "-input"}, required = false,  metaVar = "<path>",
				usage = "Specifies a path for the input file.")
		public String inFilePath = null;


		@Option(name = "-outputPath", aliases = {"-o", "-out", "-output"}, required = false, metaVar = "<path>",
				usage = "Specifies a path for the output PDF.")
		public String outFilePath = null;

		@Option(name = "-verbose", aliases = {"-v"}, required = false, usage = "To see intermediate processing messages.")
		public boolean verbose = false;

		@Option(name = "-version", aliases = {"-ver"}, required = false, usage = "To view version code.")
		public boolean version = false;


	}

	public static String convertToString(String filename) throws Exception {
		
	    ParseContext context = new ParseContext();
	    Detector detector = new DefaultDetector();
	    Parser parser = new AutoDetectParser(detector);
	    Metadata metadata = new Metadata();
	    OutputStream outputstream = new ByteArrayOutputStream();
	    String extractedText;
		     
        context.set(Parser.class, parser);
        
        URL url;
        File file = new File(filename);
        if (file.isFile()) {
            url = file.toURI().toURL();
        } else {
            url = new URL(filename);
        }
        InputStream input = TikaInputStream.get(url, metadata);
        ContentHandler handler = new BodyContentHandler(outputstream);
        parser.parse(input, handler, metadata, context); 
        input.close();
        
        extractedText = outputstream.toString();
        
        return extractedText;
	}
}
