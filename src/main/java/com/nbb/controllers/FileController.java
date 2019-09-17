package com.nbb.controllers;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.nbb.dao.UserDao;
import com.nbb.models.FileConverted;
import com.nbb.models.FileUpload;
import com.nbb.models.LutUser;
import com.nbb.models.bs.FinAbw;
import com.nbb.models.bs.FinAsset;
import com.nbb.models.bs.FinBudget;
import com.nbb.models.bs.FinCbw;
import com.nbb.models.bs.FinCt1a;
import com.nbb.models.bs.FinCt2a;
import com.nbb.models.bs.FinCt3a;
import com.nbb.models.bs.FinCt4a;
import com.nbb.models.bs.FinCtt1;
import com.nbb.models.bs.FinCtt2;
import com.nbb.models.bs.FinCtt3;
import com.nbb.models.bs.FinCtt4;
import com.nbb.models.bs.FinCtt5;
import com.nbb.models.bs.FinCtt6;
import com.nbb.models.bs.FinCtt7;
import com.nbb.models.bs.FinCtt8;
import com.nbb.models.bs.FinCtt9;
import com.nbb.models.bs.FinInventory;
import com.nbb.models.bs.FinJournal;
import com.nbb.models.bs.FinNt2;
import com.nbb.models.bs.FinTgt1;
import com.nbb.models.bs.FinTgt1a;
import com.nbb.models.bs.FinTrialBalance;
import com.nbb.models.fn.LnkAuditFile;
import com.nbb.models.fn.LnkAuditForm;
import com.nbb.models.fn.LnkAuditFormFile;
import com.nbb.models.fn.LnkAuditReport;
import com.nbb.models.fn.LutForm;
import com.nbb.models.fn.LutStaus;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.services.ExcelUploadService;
import com.nbb.services.FileUploadService;
import com.nbb.storage.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
public class FileController {

	@Autowired
	StorageService storageService;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	ExcelUploadService excelUploadService;
	
	@Autowired
	private UserDao dao;
	
	@GetMapping("/api/excel/delete/attach/{id}")
	@ResponseBody
	public boolean deleteFormFile(@PathVariable long id) {
		LnkAuditFormFile main = (LnkAuditFormFile) dao.getHQLResult("from LnkAuditFormFile t where t.id='"+id+"'", "current");
		Path currentRelativePath = Paths.get("");
		String realpath = currentRelativePath.toAbsolutePath().toString();
		File file = new File(realpath+File.separator+main.getFileurl());
		if(file.exists()){
			file.delete();
			dao.PeaceCrud(main, "LnkAuditFormFile", "delete", (long) id, 0, 0, null);
			return true;
		}
		else{
			return false;
		}
	}
	
	@PostMapping("/api/checker")
	public String checker(@RequestParam("file") MultipartFile file,Principal pr,HttpServletRequest req) throws IllegalStateException, IOException, NumberFormatException,ParseException, InvalidFormatException, JSONException {
				
			String SAVE_DIR = "upload-dir";
			String furl = File.separator + SAVE_DIR ;
			String filename = file.getOriginalFilename();
    		String newfilename = file.getOriginalFilename();
    		int newindex=newfilename.lastIndexOf('.');
    		String newlastOne=(newfilename.substring(newindex +1));
    	    String newuuid = UUID.randomUUID().toString()+"."+newlastOne;	
    	    //storageService.store(file,pr.getName(),newuuid);
    	    
    	    if(FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx") || FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")){

            	InputStream stt= file.getInputStream();
            	Workbook workbook = WorkbookFactory.create(stt); 
            	
    			FileInputStream zagwar = null;
    			File files = null;
    			Path currentRelativePath = Paths.get("");
    			String realpath = currentRelativePath.toAbsolutePath().toString();
    			
    			List<FileUpload> fl=(List<FileUpload>) dao.getHQLResult("from FileUpload t where t.autype=1 and t.aan=1 order by t.id desc", "list");
    		
    			
    			JSONObject arr= new JSONObject();
    			JSONObject err= new JSONObject();
    			int count=0;
    			if(fl.size()>0){
    				files = new File(realpath+fl.get(0).getFileurlAdmin());				
    				if(files.exists()){
    					zagwar = new FileInputStream(files);
    				}
    				else{
    					JSONObject robj=new JSONObject();
    					robj.put("support", true);
    		    		robj.put("excel", false);
    		    		robj.put("error", arr);
    		    		robj.put("file", false);
    		    		return robj.toString(); 
    				}
    			}
    			else{
    				JSONObject robj=new JSONObject();
    				robj.put("support", true);
    	    		robj.put("excel", false);
    	    		robj.put("error", arr);
    	    		robj.put("file", false);
    	    		return robj.toString(); 
    			}
    			
    			
    			Workbook zbook = WorkbookFactory.create(zagwar); 
    			JSONArray errList= new JSONArray();
    			JSONArray sheetList= new JSONArray();
    			for(int i=0;i<workbook.getNumberOfSheets()-6;i++){
    				Sheet sht=zbook.getSheet(workbook.getSheetName(i));
    				if(sht!=null){
    					Row drow = workbook.getSheetAt(i).getRow(6);
    					Row zrow = sht.getRow(6);
    					if(workbook.getSheetName(i).equalsIgnoreCase("15.Journal") || workbook.getSheetName(i).equalsIgnoreCase("Journal")){
    						drow = workbook.getSheetAt(i).getRow(3);
    						zrow = sht.getRow(3);
    					}
    					if(workbook.getSheetName(i).equalsIgnoreCase("16.Assets") || workbook.getSheetName(i).equalsIgnoreCase("Assets") 
    							|| workbook.getSheetName(i).equalsIgnoreCase("17.Inventory") || workbook.getSheetName(i).equalsIgnoreCase("19.Budget")){
    						drow = workbook.getSheetAt(i).getRow(4);
    						zrow = sht.getRow(4);
    					}
    					if(workbook.getSheetName(i).equalsIgnoreCase("18.Payroll") || workbook.getSheetName(i).equalsIgnoreCase("Payroll")){
    						drow = workbook.getSheetAt(i).getRow(1);
    						zrow = sht.getRow(1);						 
    					}
    					if(workbook.getSheetName(i).equalsIgnoreCase("12.CTT7") || workbook.getSheetName(i).equalsIgnoreCase("12.CTT7")){
    						drow = workbook.getSheetAt(i).getRow(7);
    						zrow = sht.getRow(7);
    					}
    					if(drow!=null){
    						for(int y=0;y<drow.getLastCellNum();y++){
    							Cell cl = drow.getCell(y);
    							if(zrow!=null){
    								Cell zcl = zrow.getCell(y);
    								if(cl!=null && zcl!=null){		
    									JSONObject errObj= new JSONObject();
    									if(workbook.getSheetName(i).equalsIgnoreCase("2.CT1A") || workbook.getSheetName(i).equalsIgnoreCase("CT1A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("3.CT2A") || workbook.getSheetName(i).equalsIgnoreCase("CT2A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("4.CT3A") || workbook.getSheetName(i).equalsIgnoreCase("CT3A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("5.CT4A") || workbook.getSheetName(i).equalsIgnoreCase("CT4A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("6.CTT1") || workbook.getSheetName(i).equalsIgnoreCase("7.CTT2") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("8.CTT3") || workbook.getSheetName(i).equalsIgnoreCase("9.CTT4") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("10.CTT5") || workbook.getSheetName(i).equalsIgnoreCase("11.CTT6") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("15.Journal") || workbook.getSheetName(i).equalsIgnoreCase("Journal")){
    										if(!String.valueOf(cl.getRichStringCellValue().getString().trim()).equalsIgnoreCase(String.valueOf(zcl.getRichStringCellValue().getString().trim()))){									
    											errObj.put("sheetname", cl.getSheet().getSheetName());
    											errObj.put("bagana", cl.getRichStringCellValue().getString());
    											errObj.put("bagana2", zcl.getRichStringCellValue().getString());
    											errList.put(errObj);
    										}								
    									}
    								}
    							}												
    						}
    					}
    					else{
    						JSONObject errObj= new JSONObject();
    						errObj.put("sheetname", workbook.getSheetName(i));
    						sheetList.put(errObj);
    						
    					}
    				}
    				else{
    					JSONObject errObj= new JSONObject();
    					errObj.put("sheetname", workbook.getSheetName(i));
    					sheetList.put(errObj);
    				}
    			}
    			
    			
    			JSONArray arr1= new JSONArray();
    			FormulaEvaluator evaluator = zbook.getCreationHelper().createFormulaEvaluator();
    		    			
    			if(sheetList.length()>0){
    				err.put("additionalSheet", sheetList);
    				err.put("excel", false);
    				err.put("support", false);				
    				return  err.toString();
    			}
    			else{
    				Sheet hch=zbook.getSheet("ЧХ");
    				if(hch!=null){
    					Row row4 = hch.getRow(4);
    					Row row5 = hch.getRow(5);
    					Row row6 = hch.getRow(6);
    					Row row7 = hch.getRow(7);
    					Row row8 = hch.getRow(8);
    					Row row12 = hch.getRow(12);
    					Row row13 = hch.getRow(13);
    					Row row14 = hch.getRow(14);
    					Row row15 = hch.getRow(15);
    					
    					Cell cell41 = row4.getCell(1);
    					Cell cell4 = row4.getCell(2);
    					Cell cell5 = row5.getCell(2);
    					Cell cell6 = row6.getCell(2);
    					Cell cell7 = row7.getCell(2);
    					Cell cell8 = row8.getCell(2);
    					Cell cell12 = row12.getCell(2);
    					Cell cell13 = row13.getCell(2);
    					Cell cell14 = row14.getCell(2);
    					Cell cell15 = row15.getCell(2);
    					
    				}
    				here: for(int i=0;i<workbook.getNumberOfSheets();i++){
    					//FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    					Sheet sheet = zbook.getSheet(workbook.getSheetAt(i).getSheetName().trim());				
    					FormulaEvaluator evaluatorZbook = zbook.getCreationHelper().createFormulaEvaluator();
    					FormulaEvaluator eval= workbook.getCreationHelper().createFormulaEvaluator();
    					Sheet dataSheet = workbook.getSheetAt(i);
    					if(sheet!=null){
    						System.out.println("sheetname"+sheet.getSheetName());
    						
    						if(sheet.getSheetName().equalsIgnoreCase("23.TRIAL BALANCE")){
    						//	dao.PeaceCrud(null, "FinTrialBalance", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
    							List<FinTrialBalance> datas = new ArrayList<FinTrialBalance>(); 
    									
    							for(int k=5; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}    									
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    							
    						//	dao.inserBatch(datas,"23.TRIAL BALANCE",mid); 

    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("25.CBWS")){
    						//	dao.PeaceCrud(null, "FinCbw", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
    							List<FinCbw> datas = new ArrayList<FinCbw>(); 
    								
    							for(int k=6; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    										
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}
    									//dao.PeaceCrud(form, "FinCbw", "save", (long) 0, 0, 0, null);
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    							
    							//dao.inserBatch(datas,"25.CBWS",mid); 
    						}

    						else if(sheet.getSheetName().equalsIgnoreCase( "24.ABWS" )){
    											
    							for(int k=5; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}    								
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}

    						else if(sheet.getSheetName().equalsIgnoreCase( "21.TGT1A" )){
    							for(int k=5; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);

    								
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());	
    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());	
    											
    											break;
    										}
    									}
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}

    		
    						else if(sheet.getSheetName().equalsIgnoreCase( "5.Inventory" )){
    							for(int k=5; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									FinInventory form = new FinInventory();    								
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());
    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}

    						else if(sheet.getSheetName().equalsIgnoreCase("4.Assets")){
    							for(int k=5; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								Iterator cellIterator = row.cellIterator();

    								while (cellIterator.hasNext()) {

    									Cell cell = (Cell) cellIterator.next();
    									Cell zcell =crow.getCell(cell.getColumnIndex());
    									switch (evaluator.evaluateInCell(cell).getCellType()) 
    									{
    									case Cell.CELL_TYPE_STRING:
    										zcell.setCellValue(cell.getStringCellValue());
    										
    										break;
    									case Cell.CELL_TYPE_NUMERIC:
    										zcell.setCellValue(cell.getNumericCellValue());
    										
    										break;
    									}
    								}
    								count = count + 1;
    							}
    							//dao.inserBatch(datas,"4.Assets",mid);     
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("19.Budget")){
    							for(int k=5; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    													
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());
    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());
    											
    											break;
    										case Cell.CELL_TYPE_BLANK:
    											if (cell.getColumnIndex() == 1) {
    												
    											//	dao.inserBatch(datas,"19.Budget",mid);
    												continue here;
    											}
    											break;
    										}
    									}
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    							
    							//dao.inserBatch(datas,"19.Budget",mid);
    						}
    					
    						
    						else if(sheet.getSheetName().equalsIgnoreCase("2.CT1A") || sheet.getSheetName().equalsIgnoreCase("СБД")){
    							int mnCount=4;
    							for(int k=mnCount; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    					
    								try {    									
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										
    										
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										
    										switch (cell.getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:	
    											zcell.setCellValue(cell.getNumericCellValue());
    											break;											
    										}
    									}
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("6.CTT1")){ 
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}    									
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("7.CTT2")){
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}    									
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("8.CTT3")){
    							List<FinCtt3> datas = new ArrayList<FinCtt3>(); 
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;    										
    										}
    									}    									
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("9.CTT4")){
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}    									
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true); 
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("10.CTT5")){
    							for(int k=8; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true); 
    								}
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("11.CTT6")){
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());
    											

    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    										
    											break;
    										}
    									}
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}

    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("12.CTT7")){
    							for(int k=9; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();

    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());
    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("13.CTT8")){    							
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {

    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    										
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}    								
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("14.CTT9")){
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    										
    											break;
    										}
    									}    								
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("20.TGT1")){
    							System.out.println("sheet.getLastRowNum() ===== " + dataSheet.getLastRowNum());
    							for(int k=7; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    						
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());    											
    											break;
    										}
    									}
    								
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}   
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("22.NT2")){
    							for(int k=6; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {										
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (evaluator.evaluateInCell(cell).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											zcell.setCellValue(cell.getNumericCellValue());
    											break;    											
    										}
    									}    									
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("4.CT3A") || sheet.getSheetName().equalsIgnoreCase("МГТ")){    							
    							int mnCount=4;
    							for(int k=mnCount; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									FinCt3a form = new FinCt3a();
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (cell.getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());
    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:	
    											zcell.setCellValue(cell.getNumericCellValue());    													
    											break;
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(cell);
    											zcell.setCellValue(cellValue.getNumberValue());    											
    											break;
    										}
    									}
    									count = count + 1;

    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("5.CT4A") || sheet.getSheetName().equalsIgnoreCase("ӨӨТ")){
    							int mnCount=4;
    							for(int k=mnCount; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {    									
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (cell.getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:	
    											zcell.setCellValue(cell.getNumericCellValue());
    													
    											break;
    										}
    									}
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						else if(sheet.getSheetName().equalsIgnoreCase("3.CT2A") || sheet.getSheetName().equalsIgnoreCase("ОДТ") ){
    							int mnCount=4;
    							for(int k=mnCount; k <= dataSheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								try {
    									Iterator cellIterator = row.cellIterator();
    									while (cellIterator.hasNext()) {
    										Cell cell = (Cell) cellIterator.next();
    										Cell zcell =crow.getCell(cell.getColumnIndex());
    										switch (cell.getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											zcell.setCellValue(cell.getStringCellValue());    											
    											break;
    										case Cell.CELL_TYPE_NUMERIC:	
    											zcell.setCellValue(cell.getNumericCellValue());
    																			
    											break;
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(cell);    											
    											zcell.setCellValue(cellValue.getNumberValue());
    											break;
    										}
    									}
    									count = count + 1;
    								}  catch (Exception e) {
    									arr.put("count",count-1);
    									arr.put("response",true);
    								}
    							}
    						}
    						
    						
    						else if(sheet!=null && sheet.getSheetName().trim().equals("15.Journal") || sheet.getSheetName().trim().equals("Journal")){						
    			
    							int mnCount=4;
    							
    							for(int kk=mnCount; kk <= dataSheet.getLastRowNum();kk++){
    								Row currentRow = dataSheet.getRow(kk);
    								if(currentRow!=null){
    									Cell data1 = currentRow.getCell(0);								
    									Cell data2 = currentRow.getCell(1);
    									Cell data3 = currentRow.getCell(2);
    									Cell data4 = currentRow.getCell(3);
    									Cell data5 = currentRow.getCell(4);
    									Cell data6 = currentRow.getCell(5);
    									Cell data7 = currentRow.getCell(6);
    									Cell data8 = currentRow.getCell(7);
    									Cell data9 = currentRow.getCell(8);
    									Cell data10 = currentRow.getCell(9);
    									Cell data11 = currentRow.getCell(10);
    									Cell data12 = currentRow.getCell(11);
    									Cell data13 = currentRow.getCell(12);
    									Cell data14 = currentRow.getCell(13);
    									Cell data15 = currentRow.getCell(14);
    									Cell data16 = currentRow.getCell(15);
    									Cell data17 = currentRow.getCell(16);
    									Cell data18 = currentRow.getCell(17);
    									Cell data19 = currentRow.getCell(18);
    									Cell data20 = currentRow.getCell(19);
    									Cell data21 = currentRow.getCell(20);
    									Cell data22 = currentRow.getCell(21);
    									
    									Row crow = workbook.getSheetAt(i).getRow(kk);
    									Cell cell1 = null;
    									if(crow.getCell(0)!=null){
    										cell1 = crow.getCell(0);
    									}
    									else{
    										cell1=crow.createCell(0);
    									}
    									
    									Cell cell2 =null;
    									if(crow.getCell(1)!=null){
    										cell2 = crow.getCell(1);
    									}
    									else{
    										cell2=crow.createCell(1);
    									}
    									
    									Cell cell3 =null;
    									if(crow.getCell(2)!=null){
    										cell3 = crow.getCell(2);
    									}
    									else{
    										cell3=crow.createCell(2);
    									}
    									
    									Cell cell4 =null;
    									if(crow.getCell(3)!=null){
    										cell4 = crow.getCell(3);
    									}
    									else{
    										cell4=crow.createCell(3);
    									}
    									
    									Cell cell5 =null;
    									if(crow.getCell(4)!=null){
    										cell5 = crow.getCell(4);
    									}
    									else{
    										cell5=crow.createCell(4);
    									}
    									
    									Cell cell18 =null;
    									if(crow.getCell(17)!=null){
    										cell18 = crow.getCell(17);
    									}
    									else{
    										cell18=crow.createCell(17);
    									}
    							
    									Cell cell6 = crow.getCell(5);
    									Cell cell7 = crow.getCell(6);
    									Cell cell8 = crow.getCell(7);
    									Cell cell9 = crow.getCell(8);
    									Cell cell10 = crow.getCell(9);
    									Cell cell11 = crow.getCell(10);
    									Cell cell12 = crow.getCell(11);
    									Cell cell13 = crow.getCell(12);
    									Cell cell14 = crow.getCell(13);
    									Cell cell15 = crow.getCell(14);
    									Cell cell16 = crow.getCell(15);
    									Cell cell17 = crow.getCell(16);
    									Cell cell19 = crow.getCell(18);
    									Cell cell20 = crow.getCell(19);
    									Cell cell21 = crow.getCell(20);
    									
    									if(data1!=null){
    										switch (evaluator.evaluateInCell(data1).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell1.setCellValue(data1.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											long a = (long) data1.getNumericCellValue();
    											cell1.setCellValue(data1.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data1);
    											cell1.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									
    									if(data2!=null){
    										switch (evaluator.evaluateInCell(data2).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell2.setCellValue(data2.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:								
    											cell2.setCellValue(data2.getNumericCellValue());
    											
    											if (HSSFDateUtil.isCellDateFormatted(data2)) {
    										  		Date d1 = data2.getDateCellValue();
    									    		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY");
    									            String formattedDate = df.format(d1);
    												cell2.setCellValue(formattedDate);
    										    }																					
    											break;										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data2);
    											cell2.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									
    									if(data3!=null){
    										switch (evaluator.evaluateInCell(data3).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell3.setCellValue(data3.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											long a = (long) data3.getNumericCellValue();
    											cell3.setCellValue(data3.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data3);
    											cell3.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    								
    									if(data4!=null){
    										switch (evaluator.evaluateInCell(data4).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell4.setCellValue(data4.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell4.setCellValue(data4.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data4);
    											cell4.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data5!=null){
    										switch (evaluator.evaluateInCell(data5).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell5.setCellValue(data5.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell5.setCellValue(data5.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data5);
    											cell5.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data6!=null){
    										switch (evaluator.evaluateInCell(data6).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell6.setCellValue(data6.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell6.setCellValue(data6.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 									
    											final CellValue cellValue = evaluator.evaluate(data6);
    											cell6.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data7!=null){
    										switch (evaluator.evaluateInCell(data7).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell7.setCellValue(data7.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell7.setCellValue(data7.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data7);
    											cell7.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									
    									if(data8!=null){
    										switch (evaluator.evaluateInCell(data8).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell8.setCellValue(data8.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											long a = (long) data8.getNumericCellValue();
    											cell8.setCellValue(data8.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data8);
    											cell8.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data9!=null){
    										switch (evaluator.evaluateInCell(data9).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell9.setCellValue(data9.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											long a = (long) data9.getNumericCellValue();
    											cell9.setCellValue(data9.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data9);
    											cell9.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data10!=null){
    										switch (evaluator.evaluateInCell(data10).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell10.setCellValue(data10.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											long a = (long) data10.getNumericCellValue();
    											cell10.setCellValue(data10.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data10);
    											cell10.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data11!=null){
    										switch (evaluator.evaluateInCell(data11).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell11.setCellValue(data11.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell11.setCellValue(data11.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data11);
    											cell11.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data12!=null){
    										switch (evaluator.evaluateInCell(data12).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell12.setCellValue(data12.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell12.setCellValue(data12.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data12);
    											cell12.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data13!=null){
    										switch (evaluator.evaluateInCell(data13).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell13.setCellValue(data13.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell13.setCellValue(data13.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data13);
    											cell13.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data14!=null){
    										switch (evaluator.evaluateInCell(data14).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell14.setCellValue(data14.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell14.setCellValue(data14.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data14);
    											cell14.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data15!=null){
    										switch (evaluator.evaluateInCell(data15).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell15.setCellValue(data15.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell15.setCellValue(data15.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data15);
    											cell15.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data16!=null){
    										switch (evaluator.evaluateInCell(data16).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell16.setCellValue(data16.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell16.setCellValue(data16.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 											
    											final CellValue cellValue = evaluator.evaluate(data16);
    											cell16.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data17!=null){
    										switch (evaluator.evaluateInCell(data17).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell17.setCellValue(data16.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell17.setCellValue(data17.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data17);
    											cell17.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data18!=null){
    										switch (evaluator.evaluateInCell(data18).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell18.setCellValue(data18.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell18.setCellValue(data18.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data18);
    											cell18.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data19!=null){
    										switch (evaluator.evaluateInCell(data19).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell19.setCellValue(data19.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell19.setCellValue(data19.getNumericCellValue());
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data19);
    											cell19.setCellValue(data18.getStringCellValue());
    								            break;
    										}
    									}
    									if(data20!=null){
    										switch (evaluator.evaluateInCell(data20).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											cell20.setCellValue(data20.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											cell20.setCellValue(data20.getNumericCellValue());
    											break;
    										case Cell.CELL_TYPE_BLANK:
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data20);
    											cell20.setCellValue(cellValue.getStringValue());
    								            break;
    										}
    									}
    									if(data21!=null){
    										switch (evaluator.evaluateInCell(data21).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											//cell21.setCellValue(data21.getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    										//	cell21.setCellValue(data21.getNumericCellValue());
    											break;
    										case Cell.CELL_TYPE_BLANK:
    											break;
    										
    										case Cell.CELL_TYPE_FORMULA: 
    											final CellValue cellValue = evaluator.evaluate(data21);
    								            break;
    										}
    									}						
    						    	
    								}
    							}							
    						}

    					}
    					else{
    						JSONObject robj=new JSONObject();
    						robj.put("excel", false);
    						robj.put("support", false);
    						robj.put("sheetname", workbook.getSheetAt(i).getSheetName().trim());
    			    		return robj.toString();
    					}
    								
    				}
    			
    			
		            Sheet sh = zbook.getSheet("1"); 
			        if(sh.getSheetName().equalsIgnoreCase("1")){
			        	 for(Row r : sh) { 
				        	if(r!=null && r.getRowNum()>0){
				        		for(Cell c : r) {
				        			if(c.getColumnIndex()==3){
				        				final CellValue cell3Value = evaluator.evaluate(r.getCell(3));
				        				if(cell3Value!=null){
				        					switch (cell3Value.getCellType()) {
				                    	    case Cell.CELL_TYPE_STRING:
				                    	        break;
				                    	    case Cell.CELL_TYPE_NUMERIC:
				                    	    	if(cell3Value.getNumberValue()!=0){
				                    	    		if(cell3Value.getNumberValue()==1){
				    		        					JSONObject obj= new JSONObject();
				    		        					if(r.getCell(0)!=null){
				    		        						switch (r.getCell(1).getCellType()) {
				    				                    	    case Cell.CELL_TYPE_STRING:
				    				                    	    	obj.put("code1", r.getCell(1).getStringCellValue());
				    				                    	        break;
				    				                    	    case Cell.CELL_TYPE_NUMERIC:
				    				                    	    	obj.put("code1", r.getCell(1).getNumericCellValue());
				    				                    	        break;
				    		        						}
				    		        						switch (r.getCell(2).getCellType()) {
				    			                    	    case Cell.CELL_TYPE_STRING:
				    			                    	    	obj.put("code2", r.getCell(2).getStringCellValue());
				    			                    	        break;
				    			                    	    case Cell.CELL_TYPE_NUMERIC:
				    			                    	    	obj.put("code2", r.getCell(2).getNumericCellValue());
				    			                    	        break;
				    			                    	    case Cell.CELL_TYPE_FORMULA:
				    			                    	    	final CellValue cellValue = evaluator.evaluate(r.getCell(2));
				    			                    	    	switch (cellValue.getCellType()) {
				    					                    	    case Cell.CELL_TYPE_STRING:
				    					                    	    	if(cellValue.getStringValue()!=null){
				    					                    	    		obj.put("code2", cellValue.getStringValue());
				    					                    	    	}
				    					                    	    	else{
				    					                    	    		obj.put("code2", "null");
				    					                    	    	}
				    					                    	        break;
				    					                    	    case Cell.CELL_TYPE_NUMERIC:
				    					                    	    	if(cellValue.getNumberValue()!=0){
				    					                    	    		obj.put("code2", cellValue.getNumberValue());
				    					                    	    	}
				    					                    	    	else{
				    					                    	    		obj.put("code2", "null");
				    					                    	    	}
				    					                    	        break;
				    			        						}
				    			                    	        break;
				    		        						}
				    			        					final CellValue cellValue = evaluator.evaluate(r.getCell(3));
				    			        					obj.put("sheet", r.getCell(0).getStringCellValue());
				    			        					switch (cellValue.getCellType()) {
				    				                    	    case Cell.CELL_TYPE_STRING:
				    				                    	    	obj.put("dif", cellValue.getStringValue());
				    				                    	        break;
				    				                    	    case Cell.CELL_TYPE_NUMERIC:
				    				                    	    	obj.put("dif", cellValue.getNumberValue());
				    				                    	        break;
				    		        						}
				    			        					obj.put("dif", cell3Value.getNumberValue());
				    			        					arr1.put(obj);
				    		        					}
				    		        				}
				                    	    	}
				                    	        break;
				        					}
				        				}
				        			}
					            } 
				        	}
				        } 
			        }
    		            
    		        
    				JSONObject robj=new JSONObject();
    				
    				if(errList.length()>0 || sheetList.length()>0){
    					err.put("prefilter", errList);
    					err.put("additionalSheet", sheetList);
    					err.put("excel", false);
    					err.put("support", false);				
    					return  err.toString();
    				}
    				
    				if(arr1.length()>0){
    					robj.put("support", false);
    		    		robj.put("excel", false);
    		    		robj.put("error", arr1);
    		    		return robj.toString();
    				}
    				
    				
    				if(err.length()==0 && arr1.length()==0){
    					
    					String uuid = UUID.randomUUID().toString()+".xlsx";
    		            FileOutputStream fout = new FileOutputStream("upload-dir"+File.separator+pr.getName()+ File.separator+uuid);
    		            
    		            String incuid = UUID.randomUUID().toString()+".xlsx";
    		            FileOutputStream incfout = new FileOutputStream("upload-dir"+File.separator+pr.getName()+ File.separator+incuid);

    					/*for(int y=0;y<zbook.getNumberOfSheets();y++){
    						for(int i=0;i<zbook.getSheetAt(y).getLastRowNum()+1;i++){
    							Row currentRow = zbook.getSheetAt(y).getRow(i);
    							if(currentRow!=null){
    								if(currentRow.getCell(0)==null){
    									zbook.getSheetAt(y).removeRow(currentRow);
    								}
    							}						
    						}
    					}*/
    		       
    		            if(zbook.getSheet("15.Journal")!=null){
    		            	for(int i=0;i<zbook.getSheet("15.Journal").getLastRowNum()+1;i++){
    							Row currentRow = zbook.getSheet("15.Journal").getRow(i);
    							if(currentRow!=null){
    								if(currentRow.getCell(0)==null){
    									zbook.getSheet("15.Journal").removeRow(currentRow);
    								}
    							}						
    						}
    		            }
    		            if(zbook.getSheet("Journal")!=null){
    		            	for(int i=0;i<zbook.getSheet("Journal").getLastRowNum()+1;i++){
    							Row currentRow = zbook.getSheet("Journal").getRow(i);
    							if(currentRow!=null){
    								if(currentRow.getCell(0)==null){
    									zbook.getSheet("Journal").removeRow(currentRow);
    								}
    							}						
    						}
    		            }
    		            zbook.write(fout);
    		            fout.close();
    		            workbook.write(incfout);
    		            incfout.close();
    		          //  fis.close();
    				
    		    	           
    		    		Date d1 = new Date();
    		    		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
    		            String formattedDate = df.format(d1);
    		            
    		    		
    		    	/*	robj.put("excel", true);
    		    		robj.put("url", newFile.getFileurl());
    		    		robj.put("error", arr);*/
    				}
    				else{
    					robj.put("support", true);
    		    		robj.put("excel", false);
    		    		robj.put("error", arr);
    				}
    	    		return robj.toString();
    			}
    		
    		}
    	    
    	    
    	    
    	    
    	    
    	    
    	    
    	    
    	    
    	    
    	    furl = furl+File.separator+pr.getName()+ File.separator+newuuid ;		
    		Date d1 = new Date();
    		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
            String formattedDate = df.format(d1);
    /*	    LnkAuditFormFile newFile = new LnkAuditFormFile();
    		newFile.setName(newuuid);
    		newFile.setSize(file.getSize()/1024);
    		newFile.setFilename(filename);
    		newFile.setCreateDate(formattedDate);
    		newFile.setFormid(formid);
    		newFile.setFileurl(furl);
    		dao.PeaceCrud(newFile, "LnkAuditFile", "save", (long) 0, 0, 0, null);*/
    	    
			return "true";
	}
	
	@PostMapping("/api/nyabo")
	public String nyaboFormUpload(@RequestParam("file") MultipartFile file,Principal pr,HttpServletRequest req) throws IllegalStateException, IOException, NumberFormatException,ParseException, InvalidFormatException, JSONException {
				
			String SAVE_DIR = "upload-dir";
			String furl = File.separator + SAVE_DIR ;
			String filename = file.getOriginalFilename();
    		String newfilename = file.getOriginalFilename();
    		int newindex=newfilename.lastIndexOf('.');
    		String newlastOne=(newfilename.substring(newindex +1));
    	    String newuuid = UUID.randomUUID().toString()+"."+newlastOne;	
    	    //storageService.store(file,pr.getName(),newuuid);
    	    
    	    if(FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xlsx") || FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("xls")){

            	InputStream stt= file.getInputStream();
            	Workbook workbook = WorkbookFactory.create(stt); 
            	
    			FileInputStream zagwar = null;
    			File files = null;
    			Path currentRelativePath = Paths.get("");
    			String realpath = currentRelativePath.toAbsolutePath().toString();
    			
    			List<FileUpload> fl=(List<FileUpload>) dao.getHQLResult("from FileUpload t where t.autype=1 and t.aan=1 order by t.id desc", "list");
    		
    			
    			JSONObject arr= new JSONObject();
    			JSONObject err= new JSONObject();
    			int count=0;
    			if(fl.size()>0){
    				files = new File(realpath+fl.get(0).getFileurlAdmin());				
    				if(files.exists()){
    					zagwar = new FileInputStream(files);
    				}
    				else{
    					JSONObject robj=new JSONObject();
    					robj.put("support", true);
    		    		robj.put("excel", false);
    		    		robj.put("error", arr);
    		    		robj.put("file", false);
    		    		return robj.toString(); 
    				}
    			}
    			else{
    				JSONObject robj=new JSONObject();
    				robj.put("support", true);
    	    		robj.put("excel", false);
    	    		robj.put("error", arr);
    	    		robj.put("file", false);
    	    		return robj.toString(); 
    			}
    			
    			
    			Workbook zbook = WorkbookFactory.create(zagwar); 
    			JSONArray errList= new JSONArray();
    			JSONArray sheetList= new JSONArray();
    			for(int i=0;i<workbook.getNumberOfSheets()-6;i++){
    				Sheet sht=zbook.getSheet(workbook.getSheetName(i));
    				if(sht!=null){
    					Row drow = workbook.getSheetAt(i).getRow(6);
    					Row zrow = sht.getRow(6);
    					if(workbook.getSheetName(i).equalsIgnoreCase("15.Journal") || workbook.getSheetName(i).equalsIgnoreCase("Journal")){
    						drow = workbook.getSheetAt(i).getRow(3);
    						zrow = sht.getRow(3);
    					}
    					if(workbook.getSheetName(i).equalsIgnoreCase("16.Assets") || workbook.getSheetName(i).equalsIgnoreCase("Assets") 
    							|| workbook.getSheetName(i).equalsIgnoreCase("17.Inventory") || workbook.getSheetName(i).equalsIgnoreCase("19.Budget")){
    						drow = workbook.getSheetAt(i).getRow(4);
    						zrow = sht.getRow(4);
    					}
    					if(workbook.getSheetName(i).equalsIgnoreCase("18.Payroll") || workbook.getSheetName(i).equalsIgnoreCase("Payroll")){
    						drow = workbook.getSheetAt(i).getRow(1);
    						zrow = sht.getRow(1);						 
    					}
    					if(workbook.getSheetName(i).equalsIgnoreCase("12.CTT7") || workbook.getSheetName(i).equalsIgnoreCase("12.CTT7")){
    						drow = workbook.getSheetAt(i).getRow(7);
    						zrow = sht.getRow(7);
    					}
    					if(drow!=null){
    						for(int y=0;y<drow.getLastCellNum();y++){
    							Cell cl = drow.getCell(y);
    							if(zrow!=null){
    								Cell zcl = zrow.getCell(y);
    								if(cl!=null && zcl!=null){		
    									JSONObject errObj= new JSONObject();
    									if(workbook.getSheetName(i).equalsIgnoreCase("2.CT1A") || workbook.getSheetName(i).equalsIgnoreCase("CT1A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("3.CT2A") || workbook.getSheetName(i).equalsIgnoreCase("CT2A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("4.CT3A") || workbook.getSheetName(i).equalsIgnoreCase("CT3A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("5.CT4A") || workbook.getSheetName(i).equalsIgnoreCase("CT4A") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("6.CTT1") || workbook.getSheetName(i).equalsIgnoreCase("7.CTT2") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("8.CTT3") || workbook.getSheetName(i).equalsIgnoreCase("9.CTT4") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("10.CTT5") || workbook.getSheetName(i).equalsIgnoreCase("11.CTT6") ||
    									   workbook.getSheetName(i).equalsIgnoreCase("15.Journal") || workbook.getSheetName(i).equalsIgnoreCase("Journal")){
    										if(!String.valueOf(cl.getRichStringCellValue().getString().trim()).equalsIgnoreCase(String.valueOf(zcl.getRichStringCellValue().getString().trim()))){									
    											errObj.put("sheetname", cl.getSheet().getSheetName());
    											errObj.put("bagana", cl.getRichStringCellValue().getString());
    											errObj.put("bagana2", zcl.getRichStringCellValue().getString());
    											errList.put(errObj);
    										}								
    									}
    								}
    							}												
    						}
    					}
    					else{
    						JSONObject errObj= new JSONObject();
    						errObj.put("sheetname", workbook.getSheetName(i));
    						sheetList.put(errObj);
    						
    					}
    				}
    				else{
    					JSONObject errObj= new JSONObject();
    					errObj.put("sheetname", workbook.getSheetName(i));
    					sheetList.put(errObj);
    				}
    			}
    			
    			
    			JSONArray arr1= new JSONArray();
    			FormulaEvaluator evaluator = zbook.getCreationHelper().createFormulaEvaluator();
    			
    			FormulaEvaluator wevaluator = workbook.getCreationHelper().createFormulaEvaluator();
    		
    	        JSONArray errMsg= new JSONArray();	
    			
    			if(sheetList.length()>0){
    				err.put("additionalSheet", sheetList);
    				err.put("excel", false);
    				err.put("support", false);				
    				return  err.toString();
    			}
    			else{
    				Sheet hch=zbook.getSheet("ЧХ");
    				if(hch!=null){
    					Row row4 = hch.getRow(4);
    					Row row5 = hch.getRow(5);
    					Row row6 = hch.getRow(6);
    					Row row7 = hch.getRow(7);
    					Row row8 = hch.getRow(8);
    					Row row12 = hch.getRow(12);
    					Row row13 = hch.getRow(13);
    					Row row14 = hch.getRow(14);
    					Row row15 = hch.getRow(15);
    					
    					Cell cell41 = row4.getCell(1);
    					Cell cell4 = row4.getCell(2);
    					Cell cell5 = row5.getCell(2);
    					Cell cell6 = row6.getCell(2);
    					Cell cell7 = row7.getCell(2);
    					Cell cell8 = row8.getCell(2);
    					Cell cell12 = row12.getCell(2);
    					Cell cell13 = row13.getCell(2);
    					Cell cell14 = row14.getCell(2);
    					Cell cell15 = row15.getCell(2);
    					
    				}
    				here: for(int i=0;i<workbook.getNumberOfSheets();i++){
    					//FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    					Sheet sheet = zbook.getSheet(workbook.getSheetAt(i).getSheetName().trim());				
    					FormulaEvaluator evaluatorZbook = zbook.getCreationHelper().createFormulaEvaluator();
    					FormulaEvaluator eval= workbook.getCreationHelper().createFormulaEvaluator();
    					Sheet dataSheet = workbook.getSheetAt(i);
    					if(sheet!=null){
    						System.out.println("sheetname"+sheet.getSheetName());
    						
    						if(sheet.getSheetName().equalsIgnoreCase("23.TRIAL BALANCE")){    									
    							for(int k=5; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
									    }
    									for(int y=0;y<10;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										
    										String formula="VLOOKUP("+crow.getCell(0).getNumericCellValue()+",A6:L600,"+cc+",FALSE)";
    										ss.setCellFormula(formula);    										
    										Cell zcell =crow.getCell(cc-1);
    										CellValue cellValue = eval.evaluate(ss);    	
    										if(cellValue.getNumberValue()!=0){
    											zcell.setCellValue(cellValue.getNumberValue());    	 
    										}    										
    									}
    								}    								
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("2.CT1A")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								
    								
    								if(row!=null){
    									if(row.getCell(0)!=null){
        									Cell codeCell = dataSheet.getRow(k).getCell(0);
        									try {
        										if(row.getCell(0)!=null){
                									String str="";
            										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
            										{
            										case Cell.CELL_TYPE_STRING:
            											str=row.getCell(0).getStringCellValue();
            											break;
            										case Cell.CELL_TYPE_NUMERIC:
            											str=String.valueOf(row.getCell(0).getNumericCellValue());
            											break;
            										}
            										if(str.length()>0){
            											String formula="value("+str+")";
            											codeCell.setCellFormula(formula);  
            											CellValue cellValue = eval.evaluate(codeCell);    	
            										}											
            									}
        									}
        									catch (FormulaParseException e) {
        										JSONObject errObject = new JSONObject();
        										errObject.put("sheet", sheet.getSheetName());
        										errObject.put("error", e.getMessage());
        										errMsg.put(errObject);
    									    }											
    									}
    								}
    								
    								if(row!=null){
    									for(int y=0;y<3;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:D300,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("3.CT2A")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<3;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:D300,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}    								
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("4.CT3A")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<3;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:D350,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}    								
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("5.CT4A")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow= sheet.getRow(k);
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									System.out.println("ss"+codeCell.getStringCellValue());
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C01")){
    										crow= sheet.getRow(7);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C02")){
    										crow= sheet.getRow(8);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C03")){
    										crow= sheet.getRow(9);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C04")){
    										crow= sheet.getRow(10);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C05")){
    										crow= sheet.getRow(11);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C06")){
    										crow= sheet.getRow(12);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C07")){
    										crow= sheet.getRow(13);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("C08")){
    										crow= sheet.getRow(14);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D01")){
    										crow= sheet.getRow(15);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D02")){
    										crow= sheet.getRow(16);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D03")){
    										crow= sheet.getRow(17);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D04")){
    										crow= sheet.getRow(18);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D05")){
    										crow= sheet.getRow(19);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D06")){
    										crow= sheet.getRow(20);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D07")){
    										crow= sheet.getRow(21);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D08")){
    										crow= sheet.getRow(22);
    									}
    									if(codeCell.getStringCellValue().equalsIgnoreCase("D09")){
    										crow= sheet.getRow(23);
    									}
        								/*if(row.getCell(0)!=null){
        									String str="";
    										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											str=row.getCell(0).getStringCellValue();
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											str=String.valueOf(row.getCell(0).getNumericCellValue());
    											break;
    										}
    										if(str.length()>0){
    											String formula="value("+str+")";
    											codeCell.setCellFormula(formula);  
    											CellValue cellValue = eval.evaluate(codeCell);    	
    										}											
    									}*/
    									for(int y=0;y<4;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											System.out.println("@ : "+str);
    											int r=k+1;
    											if(str.length()>0){
    												String formula="VLOOKUP(A"+r+",A8:G30,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    	
    												System.out.println("#"+formula);
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}    								
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("6.CTT1")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								if(row!=null){
    									if(row.getCell(0)!=null){
        									Cell codeCell = dataSheet.getRow(k).getCell(0);
        									try {
        										if(row.getCell(0)!=null){
                									String str="";
            										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
            										{
            										case Cell.CELL_TYPE_STRING:
            											str=row.getCell(0).getStringCellValue();
            											break;
            										case Cell.CELL_TYPE_NUMERIC:
            											str=String.valueOf(row.getCell(0).getNumericCellValue());
            											break;
            										}
            										if(str.length()>0){
            											String formula="value("+str+")";
            											codeCell.setCellFormula(formula);  
            											CellValue cellValue = eval.evaluate(codeCell);    	
            										}											
            									}
        									}
        									catch (FormulaParseException e) {
        										JSONObject errObject = new JSONObject();
        										errObject.put("sheet", sheet.getSheetName());
        										errObject.put("error", e.getMessage());
        										errMsg.put(errObject);
    									    }										
    									}
    									if(crow!=null){
    										for(int y=0;y<6;y++){		
        										Cell ss = dataSheet.getRow(0).createCell(100+y);
        										int cc=y+3;
        										String str="";
        										if(row.getCell(0)!=null && crow.getCell(0)!=null){
        											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
        											{
        											case Cell.CELL_TYPE_STRING:
        												str=crow.getCell(0).getStringCellValue();
        												break;
        											case Cell.CELL_TYPE_NUMERIC:
        												str=String.valueOf(crow.getCell(0).getNumericCellValue());
        												break;
        											}
        											if(str.length()>0){
        												String formula="VLOOKUP("+str+",A8:F30,"+cc+",FALSE)";
        												ss.setCellFormula(formula);    										
        												Cell zcell =crow.getCell(cc-1);
        												CellValue cellValue = eval.evaluate(ss);    	
        												if(cellValue.getNumberValue()!=0){
        													zcell.setCellValue(cellValue.getNumberValue());    	 
        												} 
        											}											
        										}										   										
        									}
    									}    								
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("7.CTT2")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    								
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<6;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:F12,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("8.CTT3")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    								
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
        								if(crow!=null){
        									for(int y=0;y<6;y++){		
        										Cell ss = dataSheet.getRow(0).createCell(100+y);
        										int cc=y+3;
        										String str="";
        										if(row.getCell(0)!=null && crow.getCell(0)!=null){
        											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
        											{
        											case Cell.CELL_TYPE_STRING:
        												str=crow.getCell(0).getStringCellValue();
        												break;
        											case Cell.CELL_TYPE_NUMERIC:
        												str=String.valueOf(crow.getCell(0).getNumericCellValue());
        												break;
        											}
        											if(str.length()>0){
        												String formula="VLOOKUP("+str+",A8:F45,"+cc+",FALSE)";
        												ss.setCellFormula(formula);    										
        												Cell zcell =crow.getCell(cc-1);
        												CellValue cellValue = eval.evaluate(ss);    	
        												if(cellValue.getNumberValue()!=0){
        													zcell.setCellValue(cellValue.getNumberValue());    	 
        												} 
        											}											
        										}										   										
        									}
        								}
    									
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("9.CTT4")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    							
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<6;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:F20,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("10.CTT5")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    							
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<14;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:Q25,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("11.CTT6")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);
    								
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<6;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:F30,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("12.CTT7")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    							
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<13;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:P40,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("13.CTT8")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    							
    								if(row!=null && crow!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
        								
    									for(int y=0;y<6;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null && crow.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:F70,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("14.CTT9")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    							
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(0);
    									try {
    										if(row.getCell(0)!=null){
            									String str="";
        										switch (eval.evaluateInCell(row.getCell(0)).getCellType()) 
        										{
        										case Cell.CELL_TYPE_STRING:
        											str=row.getCell(0).getStringCellValue();
        											break;
        										case Cell.CELL_TYPE_NUMERIC:
        											str=String.valueOf(row.getCell(0).getNumericCellValue());
        											break;
        										}
        										if(str.length()>0){
        											String formula="value("+str+")";
        											codeCell.setCellFormula(formula);  
        											CellValue cellValue = eval.evaluate(codeCell);    	
        										}											
        									}
    									}
    									catch (FormulaParseException e) {
    										JSONObject errObject = new JSONObject();
    										errObject.put("sheet", sheet.getSheetName());
    										errObject.put("error", e.getMessage());
    										errMsg.put(errObject);
									    }
    									for(int y=0;y<6;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(0)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(0)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(0).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(0).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:F40,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("19.Budget")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    							
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(2);
        								/*if(row.getCell(2)!=null){
        									int str=0;
    										switch (evaluator.evaluateInCell(row.getCell(2)).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											str=Integer.parseInt(row.getCell(2).getStringCellValue());
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											str=(int) row.getCell(2).getNumericCellValue();
    											break;
    										}
    										if(str>0){
    											String formula="value("+str+")";
    											codeCell.setCellFormula(formula);  
    											CellValue cellValue = eval.evaluate(codeCell);    	
    										}											
    									}*/
    									/*for(int y=0;y<14;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+3;
    										String str="";
    										if(row.getCell(2)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(2)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(2).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(2).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",A8:Q120,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}*/
    								}									
    							}
    						}
    						if(sheet.getSheetName().equalsIgnoreCase("20.TGT1")){    									
    							for(int k=7; k <= sheet.getLastRowNum();k++){
    								Row row = dataSheet.getRow(k);
    								Row crow = sheet.getRow(k);    								
    								if(row!=null){
    									Cell codeCell = dataSheet.getRow(k).getCell(2);
        								if(row.getCell(2)!=null){
        									String str="";
    										switch (evaluator.evaluateInCell(row.getCell(2)).getCellType()) 
    										{
    										case Cell.CELL_TYPE_STRING:
    											str=row.getCell(2).getStringCellValue();
    											break;
    										case Cell.CELL_TYPE_NUMERIC:
    											str=String.valueOf(row.getCell(2).getNumericCellValue());
    											break;
    										}
    										if(str.length()>0){
    											String formula="value("+str+")";
    											codeCell.setCellFormula(formula);  
    											CellValue cellValue = eval.evaluate(codeCell);    	
    										}											
    									}
    									for(int y=0;y<6;y++){		
    										Cell ss = dataSheet.getRow(0).createCell(100+y);
    										int cc=y+5;
    										String str="";
    										if(row.getCell(2)!=null){
    											switch (evaluator.evaluateInCell(crow.getCell(2)).getCellType()) 
    											{
    											case Cell.CELL_TYPE_STRING:
    												str=crow.getCell(2).getStringCellValue();
    												break;
    											case Cell.CELL_TYPE_NUMERIC:
    												str=String.valueOf(crow.getCell(2).getNumericCellValue());
    												break;
    											}
    											if(str.length()>0){
    												String formula="VLOOKUP("+str+",C8:H200,"+cc+",FALSE)";
    												ss.setCellFormula(formula);    										
    												Cell zcell =crow.getCell(cc-1);
    												CellValue cellValue = eval.evaluate(ss);    	
    												if(cellValue.getNumberValue()!=0){
    													zcell.setCellValue(cellValue.getNumberValue());    	 
    												} 
    											}											
    										}										   										
    									}
    								}									
    							}
    						}    						
    						
    						if(sheet!=null && dataSheet.getSheetName().trim().equals("1.Info") || sheet!=null && dataSheet.getSheetName().trim().equals("16.Assets") 
    								|| sheet!=null && dataSheet.getSheetName().trim().equals("15.Journal") || sheet!=null && dataSheet.getSheetName().trim().equals("Journal")
    								|| sheet!=null && dataSheet.getSheetName().trim().equals("17.Inventory") || sheet!=null && dataSheet.getSheetName().trim().equals("18.Payroll")
    								|| sheet!=null && dataSheet.getSheetName().trim().equals("19.Budget") 
    								|| sheet!=null && dataSheet.getSheetName().trim().equals("21.TGT1A") || sheet!=null && dataSheet.getSheetName().trim().equals("22.NT2")
    								|| sheet!=null && dataSheet.getSheetName().trim().equals("23.TRIAL BALANCE") || sheet!=null && dataSheet.getSheetName().trim().equals("24.ABWS")
    								|| sheet!=null && dataSheet.getSheetName().trim().equals("25.CBWS")){

								for(int kk=2;kk<dataSheet.getLastRowNum();kk++){
									Row currentRow= dataSheet.getRow(kk);	
									if(currentRow!=null){
										if(currentRow.getCell(0)!=null){											
											Row r = dataSheet.getRow(kk);	
											Row dr=null;
											if(sheet.getRow(kk)!=null){
												dr=sheet.getRow(kk);
											}
											else{
												dr=sheet.createRow(kk);
											}
												for (int p = 0; p < 60; p++) {
								                    Cell columnHeaderCell =null;
								                    if(sheet.getRow(kk).getCell(p)!=null){
								                    	columnHeaderCell = sheet.getRow(kk).getCell(p);
								                    }
								                    else{
								                    	columnHeaderCell = sheet.getRow(kk).createCell(p);
								                    }
								                    if(currentRow.getCell(p)!=null){
							                    	    switch (wevaluator.evaluateInCell(currentRow.getCell(p)).getCellType()) 
														{
														case Cell.CELL_TYPE_STRING:
															columnHeaderCell.setCellValue(currentRow.getCell(p).getStringCellValue());
															break;
														case Cell.CELL_TYPE_NUMERIC:
															columnHeaderCell.setCellValue(currentRow.getCell(p).getNumericCellValue());
															break;
									                    case Cell.CELL_TYPE_FORMULA:
									                    	final CellValue cellValue = evaluator.evaluate(currentRow.getCell(p));
									                    	columnHeaderCell.setCellValue(String.valueOf(cellValue.getNumberValue()));
												            break;
														}
								                    }					                   
								                }
											}										    
										
									}	
								}							
							}
    					}
    					else{
    						JSONObject robj=new JSONObject();
    						robj.put("excel", false);
    						robj.put("support", false);
    						robj.put("sheetname", workbook.getSheetAt(i).getSheetName().trim());
    			    		return robj.toString();
    					}
    								
    				}
    		
    		        
    				JSONObject robj=new JSONObject();
    				
    				if(errList.length()>0 || sheetList.length()>0){
    					err.put("prefilter", errList);
    					err.put("additionalSheet", sheetList);
    					err.put("excel", false);
    					err.put("support", false);				
    					return  err.toString();
    				}
    				
    				if(arr1.length()>0){
    					robj.put("support", false);
    		    		robj.put("excel", false);
    		    		robj.put("error", arr1);
    		    		return robj.toString();
    				}
    				
    				if(errMsg.length()>0){
    					robj.put("support", false);
    		    		robj.put("excel", false);
    		    		robj.put("formula", errMsg);
    		    		return robj.toString();
    				}
    				
    				if(err.length()==0 && arr1.length()==0){
    					
    					String uuid = UUID.randomUUID().toString()+".xlsx";

    					File directory = new File("upload-dir"+File.separator+pr.getName());
    					if (! directory.exists()){
    					        directory.mkdir();
					    }

    		            FileOutputStream fout = new FileOutputStream("upload-dir"+File.separator+pr.getName()+ File.separator+uuid);
    		            LutUser loguser= (LutUser) dao.getHQLResult("from LutUser t where t.username='"+pr.getName()+"'", "current");
    		            String incuid = UUID.randomUUID().toString()+".xlsx";    		            
    		            
    		    	    furl = furl+File.separator+pr.getName()+ File.separator+uuid ;		
    		    		Date d1 = new Date();
    		    		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
    		            String formattedDate = df.format(d1);
    		     	    FileConverted newFile = new FileConverted();
    		    		newFile.setName(file.getOriginalFilename());
    		    		newFile.setFsize(file.getSize()/1024);    		    	
    		    		newFile.setFdate(formattedDate);
    		    		newFile.setUserid(loguser.getId());
    		    		newFile.setFlurl(furl);
    		    		dao.PeaceCrud(newFile, "FileConverted", "save", (long) 0, 0, 0, null);
    		            
    		          //  FileOutputStream incfout = new FileOutputStream("upload-dir"+File.separator+pr.getName()+ File.separator+incuid);
    		    		robj.put("support", true);
    		    		robj.put("excel", true);

    		            if(zbook.getSheet("15.Journal")!=null){
    		            	for(int i=0;i<zbook.getSheet("15.Journal").getLastRowNum()+1;i++){
    							Row currentRow = zbook.getSheet("15.Journal").getRow(i);
    							if(currentRow!=null){
    								if(currentRow.getCell(0)==null){
    									zbook.getSheet("15.Journal").removeRow(currentRow);
    								}
    							}						
    						}
    		            }
    		            if(zbook.getSheet("Journal")!=null){
    		            	for(int i=0;i<zbook.getSheet("Journal").getLastRowNum()+1;i++){
    							Row currentRow = zbook.getSheet("Journal").getRow(i);
    							if(currentRow!=null){
    								if(currentRow.getCell(0)==null){
    									zbook.getSheet("Journal").removeRow(currentRow);
    								}
    							}						
    						}
    		            }
    		            zbook.write(fout);
    		            fout.close();
    		            
    				}
    				else{
    					robj.put("support", true);
    		    		robj.put("excel", false);
    		    		robj.put("error", arr);
    				}
    	    		return robj.toString();
    			}
    		
    		}
    	    
    	    
    	    furl = furl+File.separator+pr.getName()+ File.separator+newuuid ;		
    		Date d1 = new Date();
    		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
            String formattedDate = df.format(d1);
    /*	    LnkAuditFormFile newFile = new LnkAuditFormFile();
    		newFile.setName(newuuid);
    		newFile.setSize(file.getSize()/1024);
    		newFile.setFilename(filename);
    		newFile.setCreateDate(formattedDate);
    		newFile.setFormid(formid);
    		newFile.setFileurl(furl);
    		dao.PeaceCrud(newFile, "LnkAuditFile", "save", (long) 0, 0, 0, null);*/
    	    
			return "true";
	}
	
	@GetMapping("/api/file/download/{id}")
	@ResponseBody
	public void getFileConv(@PathVariable long id, Principal pr,HttpServletRequest req,HttpServletResponse response) throws EncryptedDocumentException, InvalidFormatException, IOException {
		FileConverted fl=  (FileConverted) dao.getHQLResult("from FileConverted t where t.id='"+id+"'", "current");
		Path currentRelativePath = Paths.get("");
		String realpath = currentRelativePath.toAbsolutePath().toString();
		
		File con=new File(realpath+fl.getFlurl());
		FileInputStream str= new FileInputStream(con);
		Workbook workbook = WorkbookFactory.create(str); 
		
		List<String> shArr= Arrays.asList("1.Info", "2.CT1A", "3.CT2A", "4.CT3A", "5.CT4A", "6.CTT1", "7.CTT2", "8.CTT3", "9.CTT4", "10.CTT5", "11.CTT6", "12.CTT7","13.CTT8","14.CTT9","15.Journal","16.Assets","17.Inventory","18.Payroll","19.Budget","20.TGT1","21.TGT1A","22.NT2","23.TRIAL BALANCE","24.ABWS","25.CBWS");
				
		for(int i=workbook.getNumberOfSheets()-1;i>=0;i--){
			Sheet st=workbook.getSheetAt(i);		
			boolean sheet=false;
			for(String nm:shArr){
				if(nm.toUpperCase().equalsIgnoreCase(st.getSheetName().trim().toUpperCase())){
					 sheet=true;
				}
			}
			if(!sheet){
				workbook.removeSheetAt(i);
			}
		}

        try (ServletOutputStream outputStream = response.getOutputStream()) {
			response.setContentType("application/ms-excel; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+"Audit-it-"+fl.getName()+".xlsx");
            workbook.write(outputStream);
            outputStream.close();
        }
	}

	@PostMapping("/api/excel/upload/afl/{id}/{formid}")
	public String handleExcelFormUpload(@RequestParam("file") MultipartFile file,@PathVariable long formid, @PathVariable long id, Model model, HttpServletRequest req) throws IllegalStateException, IOException, NumberFormatException,ParseException, InvalidFormatException, JSONException {
				
			String SAVE_DIR = "upload-dir";
			String furl = File.separator + SAVE_DIR ;		
			JSONArray arr=new JSONArray();	
			if(id!=0){					        	
	        	String filename = file.getOriginalFilename();
	    		String newfilename = file.getOriginalFilename();
	    		int newindex=newfilename.lastIndexOf('.');
	    		String newlastOne=(newfilename.substring(newindex +1));
	    	    String newuuid = UUID.randomUUID().toString()+"."+newlastOne;	
	    	    storageService.store(file,String.valueOf(id),newuuid);
	    	    
	    	    furl = furl+File.separator+id+ File.separator+newuuid ;		
	    		Date d1 = new Date();
	    		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
	            String formattedDate = df.format(d1);
	    	    LnkAuditFormFile newFile = new LnkAuditFormFile();
	    		newFile.setFname(newuuid);
	    		newFile.setFsize(file.getSize()/1024);
	    		newFile.setFilename(filename);
	    		newFile.setCreateDate(formattedDate);
	    		newFile.setFormid(formid);
	    		newFile.setFileurl(furl);
	    		dao.PeaceCrud(newFile, "LnkAuditFile", "save", (long) 0, 0, 0, null);
	    	    
				return "true";
				
			}
			return arr.toString();
	}
	
	@RequestMapping(value="/api/excel/verify/report/{mid}/{id}",method=RequestMethod.GET)
	public boolean verify(@PathVariable long id,@PathVariable long mid,HttpServletRequest req,HttpServletResponse response) throws JSONException, DocumentException, Exception {
		JsonObject obj= new JsonObject();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			FileInputStream fis = null;
			LnkAuditReport main = (LnkAuditReport) dao.getHQLResult("from LnkAuditReport t where t.id='"+id+"'", "current");
			Path currentRelativePath = Paths.get("");
			String realpath = currentRelativePath.toAbsolutePath().toString();
			if(main!=null){
				File file = new File(realpath+File.separator+main.getFileurl());
				if(main.getFileurl()==null){
					return false;
				}
				else if(!file.exists()){
					return false;
				}
				else{
					return true;
				}
			}			
    		
		}
		return false;
	}
	
	@GetMapping("formfile/{id}")
	@ResponseBody
	public ResponseEntity<Resource> getFileName(@PathVariable int id) {
		LnkAuditFormFile fl=  (LnkAuditFormFile) dao.getHQLResult("from LnkAuditFormFile t where t.id='"+id+"'", "current");
		Resource file = storageService.loadAsResource(fl.getFileurl());
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFname() + "\"")
				.body(file);
	}
	
	@GetMapping("/api/excel/export/report/{appid}/{id}")
	@ResponseBody
	public ResponseEntity<Resource> getFileNotloh(@PathVariable long id,@PathVariable long appid) {
		LnkAuditReport fl=  (LnkAuditReport) dao.getHQLResult("from LnkAuditReport t where t.id='"+id+"'", "current");
		Resource file = storageService.loadAsResource(fl.getFileurl());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fl.getFilename() + "\"")
				.body(file);
	}
	
	@GetMapping("/api/excel/delete/report/{appid}/{id}")
	@ResponseBody
	public boolean deleteReport(@PathVariable long id,@PathVariable long appid) {
		LnkAuditReport main = (LnkAuditReport) dao.getHQLResult("from LnkAuditReport t where t.id='"+id+"'", "current");
		Path currentRelativePath = Paths.get("");
		String realpath = currentRelativePath.toAbsolutePath().toString();
		File file = new File(realpath+File.separator+main.getFileurl());
		if(file.exists()){
			file.delete();
			dao.PeaceCrud(main, "LnkAuditReport", "delete", (long) id, 0, 0, null);
			return true;
		}
		else{
			return false;
		}
	}

}
