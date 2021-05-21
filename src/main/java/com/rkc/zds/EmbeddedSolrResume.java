package com.rkc.zds;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

public class EmbeddedSolrResume {
	
	private static EmbeddedSolrResume INSTANCE = null;
	
	private static EmbeddedSolrServer solrServer = null;
	
	private EmbeddedSolrResume() {
		
	}
	
	public static EmbeddedSolrResume getInstance() {
		if(INSTANCE != null) {
			return INSTANCE;
		}

		INSTANCE = new EmbeddedSolrResume();
		return INSTANCE;
	}
	
	public static void main(String[] args) throws Exception {

		EmbeddedSolrResume app = new EmbeddedSolrResume();

		EmbeddedSolrServer server = app.getEmbeddedSolrServer();
		
		//app.reindexSolr(server);
		
		app.searchSolr("Java");
		
		//Exit
		
	}

	public SolrDocumentList searchSolr(String search) throws Exception {
		SolrQuery query = new SolrQuery();
		String querySearch = "content:" + search;
		query.set("q", querySearch);
		
		EmbeddedSolrResume solr = getInstance();
		
		EmbeddedSolrServer server = solr.getEmbeddedSolrServer();
		
		QueryResponse response = server.query(query);

		SolrDocumentList docList = response.getResults();
		
		return docList;
	}
	
	public void reindexSolr() throws Exception {
		
		EmbeddedSolrResume solr = getInstance();
		
		EmbeddedSolrServer server = solr.getEmbeddedSolrServer();
		
		Collection<File> files = FileUtils.listFiles(new File("/_/data/resumes/storage"), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			String fileName = file.getName();

			String content = ConvertToString(file.getPath());			

			String[] parts = fileName.split("_");
			String userId = parts[0]; //userId
			String firstName = parts[1]; //firstName
			String lastName = parts[2]; //lastName
			
			SolrInputDocument document = new SolrInputDocument();
			document.addField("userId", userId);
			document.addField("firstName", firstName);
			document.addField("lastName", lastName);
			document.addField("fileName", fileName);
			document.addField("content", content);
			document.addField("id", fileName.hashCode());
			server.add(document);
			server.commit();
		}		
		
	}
	
	private String ConvertToString(String filename) throws Exception {
		
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

	public EmbeddedSolrServer getEmbeddedSolrServer() throws Exception {
		
		if(solrServer != null) {
			return solrServer;
		}

		String solrHomePath = "/_/data/resumes/solr";
		String solrCoreName = "newcore";
		//final EmbeddedSolrServer solrServer = new EmbeddedSolrServer(Paths.get(solrHomePath), solrCoreName);
		EmbeddedSolrServer server = new EmbeddedSolrServer(Paths.get(solrHomePath), solrCoreName);
		
		solrServer = server;
		
		reindexSolr();
		
		return solrServer;
		
	}

}
