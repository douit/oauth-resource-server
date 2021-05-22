package com.rkc.zds.resource.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.tika.exception.TikaException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.rkc.zds.resource.entity.SkillEntity;
import com.rkc.zds.resource.repository.SkillRepository;
import com.rkc.zds.resource.service.impl.FileStorageServiceImpl;
import com.rkc.zds.resource.service.ResumeParserService;
import com.rkc.zds.resource.service.SkillService;
import com.rkc.zds.resource.web.controller.ResponseWrapper;
import com.rkc.zds.resource.web.controller.ResumeParserProgram;

import gate.util.GateException;

@Service
public class ResumeParserServiceImpl implements ResumeParserService {
	
	@Autowired
	@Qualifier("pcmEntityManager")
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Autowired
	private ResumeParserProgram resumeParserProgram;
	
    @Autowired
    private FileStorageServiceImpl fileStorageService;
    
	@Autowired
	private SkillService skillService;

	@Override
	public ResponseWrapper parseResume(HttpServletRequest request, MultipartFile file) {
		
		ResponseWrapper responseWrapper = null;
		File tikkaConvertedHTMLFile = null;
		
		String shortFileName = file.getOriginalFilename();
		
		String fileName = fileStorageService.storeFile(request,file);
		
		String filePrefix = "/_/servers/www/www.zdslogic.com/html/data/files/uploads/";

		String fullFileName = filePrefix+fileName;
		
		String wwwPdfFileName = "https://www.zdslogic.com/"+fileName;
		
		String pdfFileName;
		
		String fileText;
		
		try {
			//tikkaConvertedFile = resumeParserProgram.parseToHTMLUsingApacheTikka(path.toAbsolutePath().toString());
			tikkaConvertedHTMLFile = resumeParserProgram.parseToHTMLUsingApacheTikka(fullFileName);
			
			pdfFileName = resumeParserProgram.changeExtensionToPDF(fullFileName);
			wwwPdfFileName = resumeParserProgram.changeExtensionToPDF(fileName);
			wwwPdfFileName = "https://www.zdslogic.com/data/files/uploads/"+ wwwPdfFileName;
			String arg1 = fullFileName;
			String arg2 = pdfFileName;
			String[] args = {arg1,arg2};
			
			resumeParserProgram.convertToPDF(args);
			
			fileText = resumeParserProgram.convertToString(arg1);

		} catch (Exception exception) {
			throw new RuntimeException(exception.getMessage());

		}
		JSONObject parsedJSON = null;
		if (tikkaConvertedHTMLFile != null) {
			try {
				parsedJSON = resumeParserProgram.loadGateAndAnnie(tikkaConvertedHTMLFile);
			} catch (GateException | IOException exception) {
				throw new RuntimeException(exception.getMessage());

			}
			responseWrapper = new ResponseWrapper();
			responseWrapper.setStatus(200);
			responseWrapper.setJsonData(parsedJSON.toJSONString());
			responseWrapper.setOriginalFileName(fullFileName);
			responseWrapper.setShortFileName(shortFileName);
			responseWrapper.setPdfFileName(wwwPdfFileName);
			//responseWrapper.setHtmlData(tikkaConvertedFile);
			responseWrapper.setTextData(fileText);
			

			
			responseWrapper.setMessage("Successfully parsed Resume!");
		}
		return responseWrapper;
	}

}
