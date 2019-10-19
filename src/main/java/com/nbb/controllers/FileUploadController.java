package com.nbb.controllers;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbb.dao.UserDao;
import com.nbb.models.FileUpload;
import com.nbb.models.bs.FinTrialBalance;
import com.nbb.models.fn.LnkAuditFile;
import com.nbb.models.fn.LnkAuditForm;
import com.nbb.models.fn.LnkAuditProblem;
import com.nbb.models.fn.LnkAuditReport;
import com.nbb.models.fn.LutStaus;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.services.ExcelUploadService;
import com.nbb.services.FileUploadService;
import com.nbb.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
public class FileUploadController {

	@Autowired
	StorageService storageService;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	ExcelUploadService excelUploadService;
	
	@Autowired
	private UserDao dao;

	List<String> files = new ArrayList<String>();

	@GetMapping("/api/excel/problem/{id}")
	@ResponseBody
	public String getAuditProblem(@PathVariable long id) throws EncryptedDocumentException, InvalidFormatException, IOException {
		MainAuditRegistration fl=  (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"' order by id", "current");
		
		List<LnkAuditReport> plfl=  (List<LnkAuditReport>) dao.getHQLResult("from LnkAuditReport t where t.appid='"+id+"' and t.stepid=1 order by id desc", "list");
		
		List<LnkAuditReport> prfl=  (List<LnkAuditReport>) dao.getHQLResult("from LnkAuditReport t where t.appid='"+id+"' and t.stepid=3 order by id desc", "list");
		
		if(plfl.size()>0){
			System.out.println(plfl.get(0).getFname());
		}
		if(prfl.size()>0){
			System.out.println(prfl.get(0).getFname());
		}
		File plan =null;
		File process =null;
		FileInputStream plfis=null;
		FileInputStream prfis =null;
		Workbook plWorkbook=null;
		Workbook prWorkbook = null;
		List<LnkAuditProblem> bat= new ArrayList<LnkAuditProblem>();
		Date d1 = new Date();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
        String formattedDate = df.format(d1);
		
		if(fl.getExcelurlplan()!=null){
			List<LnkAuditProblem>  ap=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.appid='"+id+"' and t.reportId="+plfl.get(0).getId()+" and stepid=1 order by id", "list");
			Path currentRelativePath = Paths.get("");
			File pl=new File(currentRelativePath+File.separator+fl.getExcelurlplan());
			if(pl.exists()){
				Resource file = storageService.loadAsResource(fl.getExcelurlplan());
				if(file.exists()){
					plan=file.getFile();
					
					if(plan.exists() && ap.size()==0){				
						plfis = new FileInputStream(plan);
						plWorkbook = WorkbookFactory.create(plfis); 
						FormulaEvaluator evaluator = plWorkbook.getCreationHelper().createFormulaEvaluator();
						
						JSONArray arr=new JSONArray();
						
						/*Sheet sh3 = plWorkbook.getSheet("3");
						Row sh3Row = sh3.getRow(0);
						for(int i=8;i<9;i++){
							Cell cell5 = sh3Row.getCell(i);		
							if(cell5!=null){
								switch (cell5.getCellTypeEnum()) {
					        	    case FORMULA:	       
					        	    	
					        	    	System.out.println(cell5.getCellFormula());
					    	    	CellValue cellValue = evaluator.evaluate(cell5);
					    	    	
					    	    	String str=cellValue.getStringValue();
					    	    	System.out.println("@@"+str);
					    	        break;
								}
							}
						}*/
									
						Cell rowsStr1 = plWorkbook.getSheet("2").getRow(0).getCell(11);							
						Cell rowsStr2 = plWorkbook.getSheet("2").getRow(0).getCell(12);		
						
						if(rowsStr1!=null){
							switch (rowsStr1.getCellTypeEnum()) {
		                	    case FORMULA:	                    	    	
		            	    	CellValue cellValue = evaluator.evaluate(rowsStr1);	  
		            	    	
		            	    	switch (evaluator.evaluateInCell(rowsStr1).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									System.out.println("str"+cellValue.getStringValue());				

									break;
								case Cell.CELL_TYPE_NUMERIC:
									System.out.println("num"+cellValue.getNumberValue());
								
								}
							}
						}
						if(rowsStr2!=null){
							switch (rowsStr2.getCellTypeEnum()) {
		                	    case FORMULA:	                    	    	
		            	    	CellValue cellValue = evaluator.evaluate(rowsStr2);	  
		            	    	
		            	    	switch (evaluator.evaluateInCell(rowsStr2).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									System.out.println("str"+cellValue.getStringValue());				

									break;
								case Cell.CELL_TYPE_NUMERIC:
									System.out.println("num"+cellValue.getNumberValue());
								
								}
							}
						}
						
						Sheet sh = plWorkbook.getSheet("2");
						if(sh!=null && sh.getSheetName().equalsIgnoreCase("2")){
							
							for(int kk=1;kk<plWorkbook.getSheet(sh.getSheetName()).getLastRowNum();kk++){					
								Row currentRow = plWorkbook.getSheet(sh.getSheetName()).getRow(kk);
								if(currentRow!=null && currentRow.getCell(1)!=null){
									Cell cell5 = currentRow.getCell(5);							
									Cell cell9 = currentRow.getCell(9);
									if(cell5!=null){
										switch (cell5.getCellTypeEnum()) {
				                    	    case FORMULA:	                    	    	
			                    	    	CellValue cellValue = evaluator.evaluate(cell5);	
			                    	    	
			                    	    	CellValue cellValue1 = evaluator.evaluate(currentRow.getCell(3));	
			                    	    	CellValue cellValue2 = evaluator.evaluate(currentRow.getCell(4));	
			                    	    	
			                    	    	if(cellValue.getNumberValue()!=0){
			                    	    		System.out.println("formula1"+cellValue1.getNumberValue());
			                    	    		System.out.println("formula2"+cellValue2.getNumberValue());
			                    	    		
			                    	    		System.out.println("formula"+cell5.getCellFormula());
			                    	    		JSONObject obj = new JSONObject();
			                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
			                    	    		obj.put("zuruu", cellValue.getNumberValue());
			                    	    		System.out.println("ev value"+cellValue.getNumberValue());
			                    	    		System.out.println("dans"+ currentRow.getCell(18).getStringCellValue());
			                    	    		if(currentRow.getCell(28)!=null){
			                    	    			obj.put("aktName", currentRow.getCell(28).getStringCellValue());
			                    	    		}
			                    	    		else{
			                    	    			obj.put("aktName", "");
			                    	    		}
			                    	    		if(currentRow.getCell(29)!=null){
			                    	    			obj.put("aktZaalt", currentRow.getCell(29).getStringCellValue());
			                    	    		}
			                    	    		else{
			                    	    			obj.put("aktZaalt", "");
			                    	    		}	                    	    		
			                    	    		obj.put("acccode", currentRow.getCell(1).getNumericCellValue());
			                    	    		CellValue cellValueTailbar = evaluator.evaluate(currentRow.getCell(6));                    	    		
			                    	    		if(cellValueTailbar !=null && cellValueTailbar.getStringValue()!=null){
			                    	    			obj.put("tailbar", cellValueTailbar.getStringValue());
			                    	    		}                    	    		
			                    	    		obj.put("cell5", true);
			                    	    		arr.put(obj);
			                    	    	}
			                    	        break;
										}
									}	
									if(cell9!=null){
										switch (cell9.getCellTypeEnum()) {
				                    	    case FORMULA:	                    	    	
			                    	    	CellValue cellValue = evaluator.evaluate(cell9);		  
			                    	    	if(cellValue.getNumberValue()!=0){
			                    	    		JSONObject obj = new JSONObject();
			                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
			                    	    		obj.put("acccode", currentRow.getCell(1).getNumericCellValue());
			                    	    		obj.put("zuruu", cellValue.getNumberValue());
			                    	    		if(currentRow.getCell(28)!=null){
			                    	    			obj.put("aktName", currentRow.getCell(28).getStringCellValue());
			                    	    		}
			                    	    		else{
			                    	    			obj.put("aktName", "");
			                    	    		}
			                    	    		if(currentRow.getCell(29)!=null){
			                    	    			obj.put("aktZaalt", currentRow.getCell(29).getStringCellValue());
			                    	    		}
			                    	    		else{
			                    	    			obj.put("aktZaalt", "");
			                    	    		}
			                    	    		CellValue cellValueTailbar = evaluator.evaluate(currentRow.getCell(10));                    	    		
			                    	    		if(cellValueTailbar !=null && cellValueTailbar.getStringValue()!=null){
			                    	    			String str= cellValueTailbar.getStringValue();
			                    	    			if(str!=null){
			                    	    				obj.put("tailbar", str);
			                    	    			}
			                    	    		}                    	    		
			                    	    		obj.put("cell9", true);
			                    	    		arr.put(obj);
			                    	    	}
			                    	        break;
										}
									}
								}
					
							}
						}
					/*	Sheet niit = plWorkbook.getSheet("niit");
						if(niit!=null && niit.getSheetName().equalsIgnoreCase("niit")){
							for(int kk=4;kk<plWorkbook.getSheet(niit.getSheetName()).getLastRowNum();kk++){
								
								Row currentRow = plWorkbook.getSheet(niit.getSheetName()).getRow(kk);
								if(currentRow!=null && currentRow.getCell(1)!=null){
									Cell cell5 = currentRow.getCell(6);							
									Cell cell9 = currentRow.getCell(7);
									if(cell5!=null){
										switch (cell5.getCellTypeEnum()) {
				                    	    case FORMULA:	                    	    	
			                    	    	CellValue cellValue = evaluator.evaluate(cell5);	  
			                    	    	if(cellValue.getNumberValue()!=0){
			                    	    		JSONObject obj = new JSONObject();
			                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
			                    	    		obj.put("zuruu", cellValue.getNumberValue());
			                    	    		if(currentRow.getCell(26).getStringCellValue()!=null){
			                    	    			obj.put("acccode", currentRow.getCell(26).getStringCellValue());
			                    	    		}
			                    	    		
			                    	    		obj.put("tailbar",  plWorkbook.getSheet(niit.getSheetName()).getRow(5).getCell(18).getStringCellValue());                  	    		
			                    	    		obj.put("cell5", true);
			                    	    		arr.put(obj);
			                    	    	}
			                    	        break;
										}
									}	
									if(cell9!=null){
										switch (cell9.getCellTypeEnum()) {
				                    	    case FORMULA:	                    	    	
			                    	    	CellValue cellValue = evaluator.evaluate(cell9);		  
			                    	    	if(cellValue.getNumberValue()!=0){
			                    	    		JSONObject obj = new JSONObject();
			                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
			                    	    		obj.put("zuruu", cellValue.getNumberValue());
			                    	    		if(currentRow.getCell(26).getStringCellValue()!=null){
			                    	    			obj.put("acccode", currentRow.getCell(26).getStringCellValue());
			                    	    		}
			                    	    		
			                    	    		obj.put("tailbar",  plWorkbook.getSheet(niit.getSheetName()).getRow(5).getCell(18).getStringCellValue());                     	    		
			                    	    		obj.put("cell9", true);
			                    	    		arr.put(obj);
			                    	    	}
			                    	        break;
										}
									}
								}
					
							}
						}*/
						
						Sheet mat = plWorkbook.getSheet("В-1");
						
						CellValue cellValueTailbar = evaluator.evaluate(mat.getRow(18).getCell(3));                    	    		
			    		System.out.println("mat : "+cellValueTailbar.getNumberValue());
						
			    		
			    		boolean chcker=false;
						if(ap.size()==0){
							for(int i =0; i<arr.length();i++){
								JSONObject obj= (JSONObject) arr.get(i);
								LnkAuditProblem pr = new LnkAuditProblem();
								pr.setAcc(obj.getString("dans"));
								if(obj.has("tailbar")){
									pr.setProblem(obj.getString("tailbar"));
								}
								pr.setAktName(obj.getString("aktName"));
								pr.setAktZaalt(obj.getString("aktZaalt"));
								pr.setComAktName(obj.getString("aktName"));
								pr.setComAktZaalt(obj.getString("aktZaalt"));
								pr.setAmount(obj.getInt("zuruu"));
								pr.setInsDate(formattedDate);
								pr.setReportId(plfl.get(0).getId());
								pr.setAppid(id);
								if(obj.has("acccode")){
									pr.setAccCode(obj.getInt("acccode"));
								}
								
								pr.setStepid(1);
								if(cellValueTailbar!=null){
									if(obj.getInt("zuruu") > cellValueTailbar.getNumberValue()*1000000){
										pr.setMatter(1);
									}
									else{
										pr.setMatter(0);
									}
								}	
								bat.add(pr);
								/*List<LnkAuditProblem>  wap=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.appid='"+id+"' and stepid=1  order by id", "list");
								for(LnkAuditProblem item:wap){
									if(item.getAcc().equalsIgnoreCase(obj.getString("dans")) && item.getAmount()==obj.getInt("zuruu") && item.getStepid()==1 && item.getAccCode()==obj.getInt("acccode")){
										chcker=true;
									}
								}
								
								if(!chcker){
									bat.add(pr);
									
									//dao.PeaceCrud(pr, "LnkAuditProblem", "save", (long) 0, 0, 0, null);	
								}	*/			
							}	
						}	    		
					}
				}
				else{
					return "false";
				}
			}			
		
		}
		
		if(fl.getExcelurlprocess()!=null){
			 List<LnkAuditProblem>  ap=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.appid='"+id+"' and reportId="+prfl.get(0).getId()+" and stepid=2  order by id", "list");
			 Resource file = storageService.loadAsResource(fl.getExcelurlprocess());
			 process=file.getFile();	
			 if(process.exists() && ap.size()==0){	
				 prfis = new FileInputStream(process);
				 prWorkbook = WorkbookFactory.create(prfis); 
				 
					FormulaEvaluator evaluator = prWorkbook.getCreationHelper().createFormulaEvaluator();
					
					JSONArray arr=new JSONArray();
					
					Sheet sh = prWorkbook.getSheet("2");
					if(sh!=null && sh.getSheetName().equalsIgnoreCase("2")){
						
					/*	Cell rowsStr1 = plWorkbook.getSheet("2").getRow(0).getCell(11);							
						Cell rowsStr2 = plWorkbook.getSheet("2").getRow(0).getCell(12);		
						
						if(rowsStr1!=null){
							switch (rowsStr1.getCellTypeEnum()) {
		                	    case FORMULA:	                    	    	
		            	    	CellValue cellValue = evaluator.evaluate(rowsStr1);	  
		            	    	
		            	    	switch (evaluator.evaluateInCell(rowsStr1).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									System.out.println("str"+cellValue.getStringValue());				

									break;
								case Cell.CELL_TYPE_NUMERIC:
									System.out.println("num"+cellValue.getNumberValue());
								
								}
							}
						}
						if(rowsStr2!=null){
							switch (rowsStr2.getCellTypeEnum()) {
		                	    case FORMULA:	                    	    	
		            	    	CellValue cellValue = evaluator.evaluate(rowsStr2);	  
		            	    	
		            	    	switch (evaluator.evaluateInCell(rowsStr2).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									System.out.println("str"+cellValue.getStringValue());				

									break;
								case Cell.CELL_TYPE_NUMERIC:
									System.out.println("num"+cellValue.getNumberValue());
								
								}
							}
						}*/
						
						for(int kk=1;kk<prWorkbook.getSheet(sh.getSheetName()).getLastRowNum();kk++){
							
							Row currentRow = prWorkbook.getSheet(sh.getSheetName()).getRow(kk);
							if(currentRow!=null && currentRow.getCell(1)!=null){
								Cell cell5 = currentRow.getCell(5);							
								Cell cell9 = currentRow.getCell(9);
								if(cell5!=null){
									switch (cell5.getCellTypeEnum()) {
			                    	    case FORMULA:	                    	    	
		                    	    	CellValue cellValue = evaluator.evaluate(cell5);	  
		                    	    	if(cellValue.getNumberValue()!=0){
		                    	    		JSONObject obj = new JSONObject();
		                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
		                    	    		obj.put("zuruu", cellValue.getNumberValue());
		                    	    		obj.put("acccode", currentRow.getCell(1).getNumericCellValue());
		                    	    		if(currentRow.getCell(28)!=null){
		                    	    			obj.put("aktName", currentRow.getCell(28).getStringCellValue());
		                    	    		}
		                    	    		else{
		                    	    			obj.put("aktName", "");
		                    	    		}
		                    	    		if(currentRow.getCell(29)!=null){
		                    	    			obj.put("aktZaalt", currentRow.getCell(29).getStringCellValue());
		                    	    		}
		                    	    		else{
		                    	    			obj.put("aktZaalt", "");
		                    	    		}	
		                    	    		CellValue cellValueTailbar = evaluator.evaluate(currentRow.getCell(6));                    	    		
		                    	    		if(cellValueTailbar !=null && cellValueTailbar.getStringValue()!=null){
		                    	    			String str=cellValueTailbar.getStringValue();
		                    	    			if(str!=null){
		                    	    				obj.put("tailbar", str);
		                    	    			}	                    	    			
		                    	    		}                    	    		
		                    	    		obj.put("cell5", true);
		                    	    		arr.put(obj);
		                    	    	}
		                    	        break;
									}
								}	
								if(cell9!=null){
									switch (cell9.getCellTypeEnum()) {
			                    	    case FORMULA:	                    	    	
		                    	    	CellValue cellValue = evaluator.evaluate(cell9);		  
		                    	    	if(cellValue.getNumberValue()!=0){
		                    	    		JSONObject obj = new JSONObject();
		                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
		                    	    		obj.put("zuruu", cellValue.getNumberValue());
		                    	    		obj.put("acccode", currentRow.getCell(1).getNumericCellValue());
		                    	    		if(currentRow.getCell(28)!=null){
		                    	    			obj.put("aktName", currentRow.getCell(28).getStringCellValue());
		                    	    		}
		                    	    		else{
		                    	    			obj.put("aktName", "");
		                    	    		}
		                    	    		if(currentRow.getCell(29)!=null){
		                    	    			obj.put("aktZaalt", currentRow.getCell(29).getStringCellValue());
		                    	    		}
		                    	    		else{
		                    	    			obj.put("aktZaalt", "");
		                    	    		}
		                    	    		CellValue cellValueTailbar = evaluator.evaluate(currentRow.getCell(10));                    	    		
		                    	    		if(cellValueTailbar !=null && cellValueTailbar.getStringValue()!=null){
		                    	    			obj.put("tailbar", cellValueTailbar.getStringValue());
		                    	    		}                    	    		
		                    	    		obj.put("cell9", true);
		                    	    		arr.put(obj);
		                    	    	}
		                    	        break;
									}
								}
							}
				
						}
					}
			/*		Sheet niit = prWorkbook.getSheet("niit");
					if(niit!=null && niit.getSheetName().equalsIgnoreCase("niit")){
						for(int kk=4;kk<prWorkbook.getSheet(niit.getSheetName()).getLastRowNum();kk++){
							
							Row currentRow = prWorkbook.getSheet(niit.getSheetName()).getRow(kk);
							if(currentRow!=null && currentRow.getCell(1)!=null){
								Cell cell5 = currentRow.getCell(6);							
								Cell cell9 = currentRow.getCell(7);
								if(cell5!=null){
									switch (cell5.getCellTypeEnum()) {
			                    	    case FORMULA:	                    	    	
		                    	    	CellValue cellValue = evaluator.evaluate(cell5);	  
		                    	    	if(cellValue.getNumberValue()!=0){
		                    	    		JSONObject obj = new JSONObject();
		                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
		                    	    		obj.put("zuruu", cellValue.getNumberValue());
		                    	    		if(currentRow.getCell(26).getStringCellValue()!=null){
		                    	    			obj.put("acccode", currentRow.getCell(26).getStringCellValue());
		                    	    		}
		                    	    		
		                    	    		obj.put("tailbar",  prWorkbook.getSheet(niit.getSheetName()).getRow(5).getCell(18).getStringCellValue());                  	    		
		                    	    		obj.put("cell5", true);
		                    	    		arr.put(obj);
		                    	    	}
		                    	        break;
									}
								}	
								if(cell9!=null){
									switch (cell9.getCellTypeEnum()) {
			                    	    case FORMULA:	                    	    	
		                    	    	CellValue cellValue = evaluator.evaluate(cell9);		  
		                    	    	if(cellValue.getNumberValue()!=0){
		                    	    		JSONObject obj = new JSONObject();
		                    	    		obj.put("dans", currentRow.getCell(18).getStringCellValue());
		                    	    		obj.put("zuruu", cellValue.getNumberValue());
		                    	    		if(currentRow.getCell(26).getStringCellValue()!=null){
		                    	    			obj.put("acccode", currentRow.getCell(26).getStringCellValue());
		                    	    		}
		                    	    		
		                    	    		obj.put("tailbar",  prWorkbook.getSheet(niit.getSheetName()).getRow(5).getCell(18).getStringCellValue());                     	    		
		                    	    		obj.put("cell9", true);
		                    	    		arr.put(obj);
		                    	    	}
		                    	        break;
									}
								}
							}
				
						}
					}*/
					
					Sheet mat = prWorkbook.getSheet("В-1");
					
					CellValue cellValueTailbar = evaluator.evaluate(mat.getRow(18).getCell(3));                    	    		
		    		System.out.println("mat : "+cellValueTailbar.getNumberValue());
		    		
		    		
		    		boolean chcker=false;
		    		if(ap.size()==0){
		    			for(int i =0; i<arr.length();i++){
							JSONObject obj= (JSONObject) arr.get(i);
							LnkAuditProblem pr = new LnkAuditProblem();
						
							if(obj.has("tailbar")){
								pr.setProblem(obj.getString("tailbar"));
							}
							pr.setAcc(obj.getString("dans"));
							if(obj.has("acccode")){
								pr.setAccCode(obj.getInt("acccode"));
							}
							pr.setInsDate(formattedDate);
							pr.setAmount(obj.getInt("zuruu"));
							pr.setAppid(id);
							pr.setStepid(2);
							pr.setReportId(prfl.get(0).getId());
							pr.setAktName(obj.getString("aktName"));
							pr.setAktZaalt(obj.getString("aktZaalt"));
							pr.setComAktName(obj.getString("aktName"));
							pr.setComAktZaalt(obj.getString("aktZaalt"));
							if(cellValueTailbar!=null){
								if(obj.getInt("zuruu") > cellValueTailbar.getNumberValue()*1000000){
									pr.setMatter(1);
								}
								else{
									pr.setMatter(0);
								}
							}	
							//List<LnkAuditProblem>  wap=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.appid='"+id+"' and stepid=2  order by id", "list");
							bat.add(pr);
							/*for(LnkAuditProblem item:wap){
								if(item.getAcc().equalsIgnoreCase(obj.getString("dans")) && item.getAmount()==obj.getInt("zuruu") && item.getStepid()==2 && item.getAccCode()==obj.getInt("acccode")){
									chcker=true;								
								}
							}
							
							if(!chcker){
								bat.add(pr);
								//dao.PeaceCrud(pr, "LnkAuditProblem", "save", (long) 0, 0, 0, null);	
							}*/				
					   }
		    	  }
					
			 }			
		}
		
		System.out.println("sss"+bat.size());
		
		dao.inserBatch(bat,"lnkAuditProblem",id);     
	 	
		return "true";
	}
	
	@GetMapping("/api/excel/report/{id}/{stepid}")
	@ResponseBody
	public ResponseEntity<List<LnkAuditReport>> getAuditExcel(@PathVariable long id, @PathVariable long stepid) {
		List<LnkAuditReport> fl=  (List<LnkAuditReport>) dao.getHQLResult("from LnkAuditReport t where t.appid='"+id+"' and t.stepid="+stepid+" order by id", "list");
		if (fl.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
		return new ResponseEntity<List<LnkAuditReport>>(fl, HttpStatus.OK);
	}
	
	@PostMapping("/api/excel/upload/zagwarExcel/{id}/{stepid}")
	public String handleExcelUpload(@RequestParam("file") MultipartFile file, @PathVariable long id, @PathVariable int stepid, Model model, HttpServletRequest req) throws IllegalStateException, IOException, ParseException, InvalidFormatException, JSONException {

	//	storageService.store(file);
		String filename = "";
		String SAVE_DIR = "upload-dir";
		String furl = "/" + SAVE_DIR + "/" + filename;		
		MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");		
		if(main!=null){
			JSONObject excelurl= excelUploadService.uploadFile(file, SAVE_DIR, String.valueOf(main.getAudityear()),main.getOrgcode(),main.getOrgtype(),furl, id,stepid);		
			if(id!=0){
				if(excelurl.getBoolean("excel")){		
					if(stepid==1){
						main.setExcelurlplan(excelurl.getString("url"));
					}
					else if (stepid==3){
						main.setExcelurlprocess(excelurl.getString("url"));
					}
					
					dao.PeaceCrud(main, "MainAuditRegistration", "update", (long) id, 0, 0, null);	

					/*List<LnkAuditForm> fms=(List<LnkAuditForm>) dao.getHQLResult("from LnkAuditForm where appid="+id+" and data7 in ('АБ','А-3','А-5.2.1') ", "list");
					for(int i=0;i<fms.size();i++){
						LnkAuditForm fo=fms.get(i);
						fo.setData10(false);
						dao.PeaceCrud(fo, "LnkAuditForm", "update", (long) fo.getId(), 0, 0, null);
					}
					if(main.getAutype()==2){
						dao.getNativeSQLResult("update lnk_audit_forms set data10=0 where appid="+id+"", "update");
					}*/
					dao.getNativeSQLResult("update lnk_audit_forms set data10=0 where appid="+id+"", "update");	
				}
			}
			return excelurl.toString();
		}
		else{
			return null;
		}
		
	}
	
	@PostMapping("/api/excel/upload/offer/{id}")
	public String handleExcelOffer(@RequestParam("file") MultipartFile file,@PathVariable long id, HttpServletRequest req) throws IllegalStateException, IOException, ParseException, InvalidFormatException, JSONException {
				
	//	storageService.store(file);
		String filename = "";
		String SAVE_DIR = "upload-dir";
		String furl = "/" + SAVE_DIR + "/" + filename;		
		MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");		
		if(main!=null){
			InputStream fis = file.getInputStream();
	    	Workbook wb = WorkbookFactory.create(fis); 
	    	FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
	    	JSONArray arr=new JSONArray();
			if(wb!=null){
				if(wb.getSheet("В-4")!=null){
					dao.getNativeSQLResult("delete from lnk_audit_problems where appid="+id+"", "delete");	
					
					for(int i=7;i<wb.getSheet("В-4").getLastRowNum()+1;i++){
						JSONObject obj = new JSONObject();
						Row rw=wb.getSheet("В-4").getRow(i);
						if(rw!=null){
							LnkAuditProblem la =new LnkAuditProblem();
							la.setAppid(id);
							la.setActive(true);
							la.setFinish(false);
							if(rw.getCell(1)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(1)).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									la.setAcc(rw.getCell(1).getStringCellValue());
									break;
								}							
							}
							if(rw.getCell(2)!=null){							
								switch (evaluator.evaluateInCell(rw.getCell(2)).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									la.setProblem(rw.getCell(2).getStringCellValue());
									break;
								}
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(3)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(3)).getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									la.setAmount(rw.getCell(3).getNumericCellValue());
									break;
								}
								
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(4)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(4)).getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									la.setMatter((int) rw.getCell(4).getNumericCellValue());
									break;
								}							
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(5)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(5)).getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									la.setCommentType((int) rw.getCell(5).getNumericCellValue());
									break;
								}
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(6)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(6)).getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									la.setComAmount(rw.getCell(6).getNumericCellValue());
									break;
								}							
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(7)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(7)).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									la.setAktName(rw.getCell(7).getStringCellValue());
									break;
								}							
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(8)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(8)).getCellType()) 
								{
								case Cell.CELL_TYPE_STRING:
									la.setAktZaalt(rw.getCell(8).getStringCellValue());
									break;
								}							
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(9)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(9)).getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									la.setComMatter((int) rw.getCell(9).getNumericCellValue());
									break;
								}							
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							if(rw.getCell(10)!=null){
								switch (evaluator.evaluateInCell(rw.getCell(10)).getCellType()) 
								{
								case Cell.CELL_TYPE_NUMERIC:
									la.setResult((int) rw.getCell(10).getNumericCellValue());
									break;
								}							
							}
							else{
								obj.put("error", "Мөр: "+i + " Багана: "+0);
							}
							
							dao.PeaceCrud(la, "LnkAuditProblem", "save", (long) 0, 0, 0, null);		
						}										
						
					}
					return "true";
				}
				else{
					return  "false";
				}
			}
			
	    	
		}
		return  "false";
	}
	
	@PostMapping("/api/excel/upload/b4/{id}")
	public String b4Upload(@RequestParam("files[]") MultipartFile mfile, @PathVariable long id, Model model, HttpServletRequest req) throws IllegalStateException, IOException, ParseException, InvalidFormatException, JSONException {
					
		MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");		
		
		String filename = mfile.getOriginalFilename();
		String newfilename = mfile.getOriginalFilename();
		int newindex=newfilename.lastIndexOf('.');
		String newlastOne=(newfilename.substring(newindex +1));
	    String newuuid = UUID.randomUUID().toString()+"."+newlastOne;	
	    
	    storageService.store(mfile,String.valueOf(id),newuuid);
	    File excelpath = new File("upload-dir"+File.separator+id+File.separator+newuuid);
    	FileInputStream fis = new FileInputStream(excelpath);
    	Workbook workbook = WorkbookFactory.create(fis); 
    	
    	Sheet sht=workbook.getSheet("В-4");
    	if(sht!=null){
    		List<LnkAuditProblem> datas = new ArrayList<LnkAuditProblem>(); 
    		
    		outerloop: for(int k=7; k <= sht.getLastRowNum();k++){
    		     Row myRow = sht.getRow(k);
                 LnkAuditProblem form = new LnkAuditProblem();
                 form.setStepid(3);
                 Iterator<Cell> cellIter = myRow.cellIterator();
                 while(cellIter.hasNext()){
                     Cell cell = (Cell) cellIter.next();
                     switch (cell.getCellType()) 
					 {
					 case Cell.CELL_TYPE_STRING:
							if (cell.getColumnIndex() == 0) {												
								form.setAcc(cell.getStringCellValue());
								if(cell.getStringCellValue()==null){
									 System.out.println("Breaking");
							         break outerloop;
								}
							}
							if (cell.getColumnIndex() == 1) {
								form.setAcc(cell.getStringCellValue());
							}
							if (cell.getColumnIndex() == 2) {
								form.setProblem(cell.getStringCellValue());
							}
							/*if (cell.getColumnIndex() == 3) {
								form.setAcc(cell.getStringCellValue());
							}*/
						break;
					 case Cell.CELL_TYPE_NUMERIC:	
							if (cell.getColumnIndex() == 0) {
								form.setAcc(NumberToTextConverter.toText(cell.getNumericCellValue()));
								if(cell.getNumericCellValue()==0){
									 System.out.println("Breaking");
							         break outerloop;
								}
							}
							if (cell.getColumnIndex() == 3) {
								form.setAmount(cell.getNumericCellValue());
							}
						break;											
					 }
                 } 
                 if(form.getAcc()!=null){
                	 form.setMatter(0);
                	 form.setResult(1);
                	 form.setAnswer(0);
                	 form.setCommentType(0);
                	 datas.add(form);
                 }
                
             }
    		 dao.inserBatch(datas,"lnkAuditProblem",id); 
             System.out.println("la size:"+datas.size());
    	}
    	return "true";		
	}
	
	@PostMapping("/api/file/upload/audit/{id}")
	public String handleNotlohZuil(@RequestParam("file") MultipartFile file,@RequestParam("description") String description, @PathVariable long id, Model model, HttpServletRequest req) throws IllegalStateException, IOException, ParseException, InvalidFormatException, JSONException {
				
	//	storageService.store(file);
		String filename = "";
		String SAVE_DIR = "upload-dir/"+id;
		String furl = "/" + SAVE_DIR + "/" + filename;		
		MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");	
		
		LnkAuditFile fu= fileUploadService.handleNotlohZuil(file, SAVE_DIR, furl,description,id);			
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(fu);
		
	}
	
	@GetMapping("/api/excel/files/{id}")
	@ResponseBody
	public ResponseEntity<Resource> getExcelFile(@PathVariable long id) {
		LutStaus fl=  (LutStaus) dao.getHQLResult("from LutStaus t where t.id='"+id+"'", "current");
		Resource file = storageService.loadAsResource(fl.getSavedname());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping("/api/file/upload/{path}/{aan}/{payroll}")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,@RequestParam("fileAdmin") MultipartFile fileAdmin,@PathVariable String path, @PathVariable int  aan, @PathVariable int  payroll,Model model, HttpServletRequest req) throws IllegalStateException,Exception, IOException {

			String filename = "";
			String SAVE_DIR = "upload-dir";
			JSONObject result = new JSONObject();
			String furl = "/" + SAVE_DIR + "/" + filename;
			List<FileUpload> fl=  (List<FileUpload>) dao.getHQLResult("from FileUpload t where t.aan='"+aan+"' and payroll="+payroll+"", "list");
			
			if (fl.size()>0){
				for(FileUpload fle:fl){
					// File delfile = new File(fle.getFileurl());
					 Resource resfile = storageService.loadAsResource(fle.getFileurl());
			         if(resfile.exists()) { 
			        	 File delfile = resfile.getFile();
			        	 if(delfile.delete()){
			     			System.out.println(delfile.getName() + " is deleted!");
			     		}else{
			     			System.out.println("Delete operation is failed.");
			     		}
			         } else {
			        	 System.out.println("File not found.");
		    		 }
					 dao.PeaceCrud(null, "FileUpload", "delete", (long) fle.getId(), 0, 0, null);
				}
			}
						
			FileUpload fu= fileUploadService.uploadFile(file,fileAdmin, SAVE_DIR, furl,aan,payroll);			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(fu);
			
	}

	@GetMapping("/gellallfiles")
	public String getListFiles(Model model) {
		model.addAttribute("files",
				files.stream()
						.map(fileName -> MvcUriComponentsBuilder
								.fromMethodName(FileUploadController.class, "getFile", fileName).build().toString())
						.collect(Collectors.toList()));
		model.addAttribute("totalFiles", "TotalFiles: " + files.size());
		return "listFiles";
	}

	@GetMapping("/api/files/{id}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable long id) {
		FileUpload fl=  (FileUpload) dao.getHQLResult("from FileUpload t where t.id='"+id+"'", "current");
		Resource file = storageService.loadAsResource(fl.getFileurl());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilename() + "\"")
				.body(file);
	}
	
	@GetMapping("/api/file/download/{appid}/{id}")
	@ResponseBody
	public ResponseEntity<Resource> getFileNotloh(@PathVariable long id,@PathVariable long appid) {
		LnkAuditFile fl=  (LnkAuditFile) dao.getHQLResult("from LnkAuditFile t where t.id='"+id+"'", "current");
		Resource file = storageService.loadAsResource(fl.getFileurl());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilename() + "\"")
				.body(file);
	}
	
	@GetMapping("/api/files/{ftype}/{name}")
	@ResponseBody
	public ResponseEntity<Resource> getFileNAme(@PathVariable String name,@PathVariable int ftype) {
		if(ftype==1){
			FileUpload fl=  (FileUpload) dao.getHQLResult("from FileUpload t where t.name='"+name+"'", "current");
			Resource file = storageService.loadAsResource(fl.getFileurl());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilename() + "\"")
					.body(file);
		}
		else if(ftype==2){
			FileUpload fl=  (FileUpload) dao.getHQLResult("from FileUpload t where t.nameAdmin='"+name+"'", "current");
			Resource file = storageService.loadAsResource(fl.getFileurlAdmin());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilenameAdmin() + "\"")
					.body(file);
		}
		else if(ftype==3){
			FileUpload fl=  (FileUpload) dao.getHQLResult("from FileUpload t where t.aan='"+name+"'", "current");
			Resource file = storageService.loadAsResource(fl.getFileurl());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilename()+ "\"")
					.body(file);
		}
		else{
			LnkAuditFile fl=  (LnkAuditFile) dao.getHQLResult("from LnkAuditFile t where t.id='"+name+"'", "current");
			Resource file = storageService.loadAsResource(fl.getFileurl());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilename()+ "\"")
					.body(file);	
		}
		
	}
	
	

}
