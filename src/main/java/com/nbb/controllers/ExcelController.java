package com.nbb.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.nbb.dao.UserDao;
import com.nbb.models.fn.LnkAuditForm;
import com.nbb.models.fn.LnkAuditProblem;
import com.nbb.models.fn.LnkMainUser;
import com.nbb.models.fn.LutForm;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.models.fn.SubAuditOrganization;
import com.nbb.storage.StorageService;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
	
	@Autowired
    private UserDao dao;
	
	@Autowired
	StorageService storageService;
	
	Date d1 = new Date();
	SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
	String special = df.format(d1);
	
	@PostMapping("/upload/form/{id}/{formid}")
	public String handleExcelFormUpload(@RequestParam("file") MultipartFile file,@PathVariable long formid, @PathVariable long id, Model model, HttpServletRequest req) throws IllegalStateException, IOException, NumberFormatException,ParseException, InvalidFormatException, JSONException {
				
			String SAVE_DIR = "upload-dir";
			String furl = "/" + SAVE_DIR ;		
			JSONArray arr=new JSONArray();	
			if(id!=0){
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");
				LutForm lf = (LutForm) dao.getHQLResult("from LutForm t where t.id='"+formid+"'", "current");
				
				LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and t.formid="+formid+"", "current");
				
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				
			
				
				File excelpath = null;
				if(laf.getLevelid()==1 && laf.getData13()==2){
					excelpath = new File(realpath+"/"+main.getExcelurlplan());
				}
				else if(laf.getLevelid()==2 && laf.getData13()==2){
					excelpath = new File(realpath+"/"+main.getExcelurlprocess());
				}
				else{
					if(lf.getData13()==1){
						excelpath = new File(realpath+"/"+main.getExcelurlplan());
					}
					else{
						excelpath = new File(realpath+"/"+main.getExcelurlprocess());
					}
				}
				
				if(!excelpath.exists()){
        			return "false";
        		}

	        	FileInputStream fis = new FileInputStream(excelpath);
	        	Workbook wb = WorkbookFactory.create(fis);
	        	
	    		String newfilename = file.getOriginalFilename();
	    		int newindex=newfilename.lastIndexOf('.');
	    		String newlastOne=(newfilename.substring(newindex +1));
	    	    String newuuid = UUID.randomUUID().toString()+"."+newlastOne;	
	    	  //  storageService.store(file,String.valueOf(id),newuuid);
	    	    
	    //	    File formpath = new File("upload-dir"+File.separator+id+File.separator+newuuid);
	      //  	FileInputStream ffis = new FileInputStream(formpath);
	    	    InputStream ffis=file.getInputStream();
	        	Workbook fbook = new XSSFWorkbook(ffis);
	        		        	
	   /*     	if(lf.getData7().toUpperCase().equalsIgnoreCase("АБ") || lf.getData7().toUpperCase().equalsIgnoreCase("А-5.2.1") || lf.getData7().toUpperCase().equalsIgnoreCase("А-3")){
	        		String[] sheetnames = {"АБ", "А-5.2.1", "А-3"};
	        		for(String item:sheetnames){
	        			Sheet sh = fbook.getSheet(item); 
	        			if(sh!=null){
	        				if(sh.getSheetName().equalsIgnoreCase("А-5.2.1")){
	        					//Row row3 = wb.getSheet(sh.getSheetName()).getRow(3);						
	    						//Cell cell1 = row3.getCell(1);
	    							    						
	    						for(int kk=6;kk<wb.getSheet(sh.getSheetName()).getLastRowNum();kk++){
	    							Row currentRow = wb.getSheet(sh.getSheetName()).getRow(kk);
	    							if(currentRow!=null && currentRow.getCell(1)!=null && fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5)!=null){
	    								Cell cell5 = currentRow.getCell(5);
	    								Cell cell6 = currentRow.getCell(6);
	    								
	    								switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5).getCellTypeEnum()) {
				                    	    case STRING:
				                    	    	cell5.setCellType(CellType.STRING);
				                    	    	cell5.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5).getStringCellValue());
				                    	        break;
				                    	    case NUMERIC:
				                    	    	cell5.setCellType(CellType.NUMERIC);
				                    	    	cell5.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5).getNumericCellValue());
				                    	        break;
	    								}
	    								switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(6).getCellTypeEnum()) {
				                    	    case STRING:
				                    	    	cell6.setCellType(CellType.STRING);
				                    	    	cell6.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(6).getStringCellValue());
				                    	        break;
				                    	    case NUMERIC:
				                    	    	cell6.setCellType(CellType.NUMERIC);
				                    	    	cell6.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(6).getNumericCellValue());
				                    	        break;
				                    	}
	    							}
	    				
	    						}
	    						JSONObject obj=new JSONObject();
	    					
	    						LnkAuditForm la = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and data7='А-5.2.1'", "current");
	    						if(la!=null){
	    							obj.put("fname", "А-5.2.1");
		    						arr.put(obj);
	    							la.setData6(true);
		    						dao.PeaceCrud(la, "LnkMainUser", "update", (long) la.getId(), 0, 0, null);	
	    						}
	    						
	    						
	    						
	        				}
	        				if(sh.getSheetName().equalsIgnoreCase("АБ")){
	    						for(int kk=1;kk<fbook.getSheet(sh.getSheetName()).getLastRowNum()+1;kk++){
	    							Row currentRow = wb.getSheet(sh.getSheetName()).getRow(kk);
	    							if(currentRow!=null){
	    								Cell cell1 = currentRow.getCell(1);
	    								Cell cell2 = currentRow.getCell(2);
	    								
	    								switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(1).getCellTypeEnum()) {
				                    	    case STRING:
				                    	    	cell1.setCellType(CellType.STRING);
				                    	    	cell1.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(1).getStringCellValue());
				                    	        break;
				                    	    case NUMERIC:
				                    	    	cell1.setCellType(CellType.NUMERIC);
				                    	    	cell1.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(1).getNumericCellValue());
				                    	        break;
	    								}
	    								switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(2).getCellTypeEnum()) {
				                    	    case STRING:
				                    	    	cell2.setCellType(CellType.STRING);
				                    	    	cell2.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(2).getStringCellValue());
				                    	        break;
				                    	    case NUMERIC:
				                    	    	cell2.setCellType(CellType.NUMERIC);
				                    	    	cell2.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(2).getNumericCellValue());
				                    	        break;
				                    	}
	    							}
	    				
	    						}
	    						JSONObject obj=new JSONObject();
	    						obj.put("fname", "АБ");
	    						arr.put(obj);
	    						LnkAuditForm la = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and data7='АБ'", "current");
	    						la.setData6(true);
	    						dao.PeaceCrud(la, "LnkMainUser", "update", (long) la.getId(), 0, 0, null);	
	        				}
	        				if(sh.getSheetName().equalsIgnoreCase("А-3")){
	        					for(int kk=7;kk<fbook.getSheet(sh.getSheetName()).getLastRowNum()+1;kk++){
	    							Row currentRow = wb.getSheet(sh.getSheetName()).getRow(kk);
	    							if(currentRow!=null){
	    								Cell cell1 = currentRow.getCell(1);
	    								Cell cell2 = currentRow.getCell(2);
	    								Cell cell3 = currentRow.getCell(3);
	    								Cell cell4 = currentRow.getCell(4);
	    								Cell cell5 = currentRow.getCell(5);
	    								Cell cell6 = currentRow.getCell(6);
	    								Cell cell7 = currentRow.getCell(7);
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(1)!=null){
	    									switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(1).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell1.setCellType(CellType.STRING);
					                    	    	cell1.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(1).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell1.setCellType(CellType.NUMERIC);
					                    	    	cell1.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(1).getNumericCellValue());
					                    	        break;
		    								}
	    								}	    								
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(2)!=null){
	    									switch (fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(2).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell2.setCellType(CellType.STRING);
					                    	    	cell2.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(2).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell2.setCellType(CellType.NUMERIC);
					                    	    	cell2.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(2).getNumericCellValue());
					                    	        break;
					                    	}
	    								}
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(3)!=null){
	    									switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(3).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell3.setCellType(CellType.STRING);
					                    	    	cell3.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(3).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell3.setCellType(CellType.NUMERIC);
					                    	    	cell3.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(3).getNumericCellValue());
					                    	        break;
		    								}
	    								}
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(4)!=null){
	    									switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(4).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell4.setCellType(CellType.STRING);
					                    	    	cell4.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(4).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell4.setCellType(CellType.NUMERIC);
					                    	    	cell4.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(4).getNumericCellValue());
					                    	        break;
					                    	}
	    								}
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(5)!=null){
	    									switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell5.setCellType(CellType.STRING);
					                    	    	cell5.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell5.setCellType(CellType.NUMERIC);
					                    	    	cell5.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(5).getNumericCellValue());
					                    	        break;
											}
	    								}
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(6)!=null){
	    									switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(6).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell6.setCellType(CellType.STRING);
					                    	    	cell6.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(6).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell6.setCellType(CellType.NUMERIC);
					                    	    	cell6.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(6).getNumericCellValue());
					                    	        break;
					                    	}
	    								}
	    								if(fbook.getSheet(sh.getSheetName()).getRow(kk).getCell(7)!=null){
	    									switch (fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(7).getCellTypeEnum()) {
					                    	    case STRING:
					                    	    	cell7.setCellType(CellType.STRING);
					                    	    	cell7.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(7).getStringCellValue());
					                    	        break;
					                    	    case NUMERIC:
					                    	    	cell7.setCellType(CellType.NUMERIC);
					                    	    	cell7.setCellValue(fbook.getSheet(wb.getSheet(sh.getSheetName()).getSheetName()).getRow(kk).getCell(7).getNumericCellValue());
					                    	        break;
					                    	}
	    								}
	    							}	    				
	    						}
	        					JSONObject obj=new JSONObject();
	    						
	        					LnkAuditForm la = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and data7='А-3'", "current");
	        					if(la!=null){
	        						obj.put("fname", "А-3");
		    						arr.put(obj);
	        						la.setData6(true);
		    						dao.PeaceCrud(la, "LnkMainUser", "update", (long) la.getId(), 0, 0, null);	
	        					}
	    						
	        				}	
	        				
	        			}
	        		} 
	        		List<LnkAuditForm> fms=(List<LnkAuditForm>) dao.getHQLResult("from LnkAuditForm where appid="+id+" and data10=0 and data7 in ('АБ','А-3','А-5.2.1') ", "list");
	        		
					if(fms.size()>0){						
						dao.insertBatchSQL("update lnk_audit_forms set data10=0 where appid="+id+" and data10=1");						
					}
	        	}
	        	else */
	        		
        		if(lf.getData7().toUpperCase().equalsIgnoreCase("18.Payroll")){
	        		JSONArray header= new JSONArray();
	        		final List<JSONObject> objs = new ArrayList<JSONObject>();
	        		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
	        		List<String> headerArr = new ArrayList<String>();
	        		int counter=0;
	        		int dcounter=0;
	        		List<String> str= Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
	        		for(String name:str){
	        			Sheet st=fbook.getSheet(name);
	        			if(st!=null){
	        				Row rw=fbook.getSheet(name).getRow(1);
		        			if(rw!=null){
		        				for(int y=0;y<rw.getLastCellNum();y++){
			        				Cell data1=rw.getCell(y);
			        				JSONObject title=new JSONObject();
			        				if(data1!=null){
			        					if(data1.getCellType()==1){
			        						if(!headerArr.contains(data1.getStringCellValue())){
			        							for(int t=y;t<rw.getLastCellNum();t++){
			        								Cell data2=rw.getCell(t);
			        								if(data2!=null){
			        									if(data2.getCellType()==1){
			        										if(data1.getStringCellValue().equalsIgnoreCase(data2.getStringCellValue()) && data1.getColumnIndex()!=data2.getColumnIndex()){
			    	        									JSONObject dub=new JSONObject();
			    	        									dub.put("title", data1.getStringCellValue());
			    	        									dub.put("index", t);
			    	        									objs.add(dub);
			    	        								}
			        									}
			        								}
			        							}
			        							
			        							headerArr.add(data1.getStringCellValue());	       
			        							title.put("title", data1.getStringCellValue());
				        						title.put("index", y);
				        						counter++;
				        						objs.add(title);    							     							
			        						}
				        				}
			        				}
			        			}
		        			}
	        			}	        				        				        			
	        		}
	        		List<JSONObject> orderedList = new ArrayList<JSONObject>();
	        		int ocount=0;
	        		for(int y=0;y<objs.size();y++){
	        			for(JSONObject obj:objs){
	        				if(obj.getInt("index")==y){
	        					obj.put("order", ocount);
	        					orderedList.add(obj);
	        					ocount++;
	        				}
	        			}
	        		}
	        		
	        		/*Sheet payroll =wb.getSheet("18.Payroll");
	        		Row titleRow=payroll.getRow(1);
	        		for(int i=0;i<orderedList.size();i++){
	        			JSONObject obj=orderedList.get(i);
	        			Cell title=titleRow.getCell(i);
	        			if(title==null){
	        				title=titleRow.createCell(i);
	        				title.setCellValue(obj.getString("title"));
	        			}
	        			else{
	        				if(title.getCellType()==1){
	        					title.setCellValue(obj.getString("title"));
	        				}
	        			}
	        		}*/
	        		CellStyle cs=wb.createCellStyle();
	        		cs.setBorderBottom(CellStyle.BORDER_THIN);
	        		cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
	        		cs.setBorderTop(CellStyle.BORDER_THIN);
	        		cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
	        		cs.setBorderLeft(CellStyle.BORDER_THIN);
	        		cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	        		cs.setBorderRight(CellStyle.BORDER_THIN);
	        		cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
	        		cs.setWrapText(true);
	        		cs.setAlignment(CellStyle.ALIGN_CENTER);
	        		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	        		  
	        		
	        		CellStyle style=wb.createCellStyle();
	        		style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index); 
	        		style.setFillPattern(CellStyle.SOLID_FOREGROUND); 
        		    style.setBorderBottom(CellStyle.BORDER_THIN);
        		    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        		    style.setBorderTop(CellStyle.BORDER_THIN);
        		    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        		    style.setBorderLeft(CellStyle.BORDER_THIN);
        		    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        		    style.setBorderRight(CellStyle.BORDER_THIN);
        		    style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        		    style.setWrapText(true);
        		    style.setAlignment(CellStyle.ALIGN_CENTER);
        		    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        			//************ Negtgel **************//
	        		 
        		 
        		        		
        			Sheet nCominSheet =wb.getSheet("negtgel");
        			
        			for(int i=0;i<nCominSheet.getLastRowNum();i++){
        				Row re=nCominSheet.getRow(i);
        				if(re!=null){
        					nCominSheet.removeRow(re);
        				}        				
        			}
        			
        			Row nTitleRowS=nCominSheet.createRow(1);
	        		for(int i=0;i<orderedList.size();i++){
	        			JSONObject obj=orderedList.get(i);
	        			Cell title=nTitleRowS.getCell(i+20);
	        			if(title==null){
	        				title=nTitleRowS.createCell(i+20);
	        				title.setCellValue(obj.getString("title"));
	        				title.setCellStyle(cs);
	        			}
	        			else{
	        				if(title.getCellType()==1){
	        					title.setCellValue(obj.getString("title"));
	        				}
	        			}
	        		}
	        		
	        		Cell titleSar=nTitleRowS.createCell(0);
	        		titleSar.setCellValue("Сар");
	        		titleSar.setCellStyle(style);
	        		
	        		Cell title=nTitleRowS.getCell(1);
	        		title=nTitleRowS.createCell(1);
    				title.setCellValue("Хувь");    				
    				title.setCellStyle(style);
    				
    				Cell title1=nTitleRowS.getCell(2);
	        		title1=nTitleRowS.createCell(2);
    				title1.setCellValue("Аудитаар НДШ");
    				title1.setCellStyle(style);
    				
    				Cell title2=nTitleRowS.getCell(3);
	        		title2=nTitleRowS.createCell(3);
    				title2.setCellValue("Аудитаар ХХОАТ");
    				title2.setCellStyle(style);
    				Cell title3=nTitleRowS.getCell(4);
	        		title3=nTitleRowS.createCell(4);
    				title3.setCellValue("Зөрүү");
    				title3.setCellStyle(style);
    				Cell title4=nTitleRowS.getCell(5);
	        		title4=nTitleRowS.createCell(5);
    				title4.setCellValue("Зөрүү");
    				title4.setCellStyle(style);
    				Cell title5=nTitleRowS.getCell(6);
	        		title5=nTitleRowS.createCell(6);
    				title5.setCellValue("Лист");
    				title5.setCellStyle(style);
    				Cell title6=nTitleRowS.getCell(7);
    				title6=nTitleRowS.createCell(7);
    				title6.setCellValue("Нярай");
    				title6.setCellStyle(style);
    				Cell title7=nTitleRowS.getCell(8);
    				title7=nTitleRowS.createCell(8);
    				title7.setCellValue("Хоол");
    				title7.setCellStyle(style);
    				Cell title8=nTitleRowS.getCell(9);
    				title8=nTitleRowS.createCell(9);
    				title8.setCellValue("Унаа");
    				title8.setCellStyle(style);
    				Cell title9=nTitleRowS.getCell(10);
    				title9=nTitleRowS.createCell(10);
    				title9.setCellValue("Бүгд дүн");
    				title9.setCellStyle(style);
    				Cell title10=nTitleRowS.getCell(11);
    				title10=nTitleRowS.createCell(11);
    				title10.setCellValue("НДШ");
    				title10.setCellStyle(style);
    				Cell title11=nTitleRowS.getCell(12);
    				title11=nTitleRowS.createCell(12);
    				title11.setCellValue("ХХОАТ");
    				title11.setCellStyle(style);
    				Cell title12=nTitleRowS.getCell(13);
    				title12=nTitleRowS.createCell(13);
    				title12.setCellValue("Цалингийн зардал");
    				title12.setCellStyle(style);
    				Cell title13=nTitleRowS.getCell(14);
    				title13=nTitleRowS.createCell(14);
    				title13.setCellValue("НДШ цэвэр");
    				title13.setCellStyle(style);
    				Cell title14=nTitleRowS.getCell(15);
    				title14=nTitleRowS.createCell(15);
    				title14.setCellValue("ХХОАТ цэвэр");
    				title14.setCellStyle(style);
    				Cell title15=nTitleRowS.getCell(16);
    				title15=nTitleRowS.createCell(16);
    				title15.setCellValue("Цалингийн зардал ХХДХ их");
    				title15.setCellStyle(style);
    				Cell title16=nTitleRowS.getCell(17);
    				title16=nTitleRowS.createCell(17);
    				title16.setCellValue("Цалингийн зардал ХХДХ их хувь");
    				title16.setCellStyle(style);
    				Cell title17=nTitleRowS.getCell(18);
    				title17=nTitleRowS.createCell(18);
    				title17.setCellValue("Тооцсон НДШ хувь 7.8 хувь");
    				title17.setCellStyle(style);
    				Cell title18=nTitleRowS.getCell(19);
    				title18=nTitleRowS.createCell(19);
    				title18.setCellValue(" ");
    				title18.setCellStyle(style);
	        		
	        		int rowCounter=0;
	        		
	        		
	        		
	        		for(String shName:str){
	        			Sheet cominSheet =fbook.getSheet(shName);
	        			Sheet sh=wb.getSheet("negtgel");
	        			if(cominSheet!=null){
	        				for(int y=2;y<cominSheet.getLastRowNum();y++){
	        					if(cominSheet.getRow(y)!=null){
	        						Row rw=sh.createRow(y+rowCounter);
		        					for(int j=0;j<cominSheet.getRow(y).getLastCellNum()+10;j++){
		        						if(cominSheet.getRow(y).getCell(j)!=null){
		        							Cell mTitle=cominSheet.getRow(1).getCell(j);
		        							Row wbTitle=sh.getRow(1);
		        							int col=0;
		        							List<Integer> strArray=new ArrayList<>();
		        							for(int c=20;c<sh.getRow(1).getLastCellNum();c++){
		        								Cell wCell=sh.getRow(1).getCell(c);
		        								if(mTitle!=null && wCell!=null){
		        									if(mTitle.getStringCellValue().equalsIgnoreCase(wCell.getStringCellValue())){
			        									if(mTitle.getColumnIndex()==wCell.getColumnIndex()){
			        										col=mTitle.getColumnIndex();			        										
			        									}
			        									else{
			        										col=wCell.getColumnIndex();		
			        										
			        									}	
			        									strArray.add(wCell.getColumnIndex());
			        								}
		        								}
		        								else{
		        									c++;
		        								}
		        							}
		        							for(Integer a:strArray){
		        								if(mTitle!=null){
		        									Cell cl = rw.createCell(a);
					        						switch (cominSheet.getRow(y).getCell(j).getCellTypeEnum()) {
							                    	    case STRING:			
							                    	    	if(NumberUtils.isDigits(cominSheet.getRow(y).getCell(j).getStringCellValue().replace(",", ""))){
							                    	            cl.setCellValue(Double.parseDouble(cominSheet.getRow(y).getCell(j).getStringCellValue().replace(",", "")));
							                    	        }
							                    	    	else if(NumberUtils.isDigits(cominSheet.getRow(y).getCell(j).getStringCellValue().replace(".0", ""))){
							                    	            cl.setCellValue(Double.parseDouble(cominSheet.getRow(y).getCell(j).getStringCellValue().replace(".0", "")));
							                    	        }
							                    	    	else{
							                    	            cl.setCellValue(cominSheet.getRow(y).getCell(j).getStringCellValue().replace(",", ""));
							                    	        }
							                    	        break;
							                    	    case NUMERIC:
															cl.setCellValue(cominSheet.getRow(y).getCell(j).getNumericCellValue());
							                    	        break;
													}
		        								}
		        							}
		        							
		        						}	
		        					}
		        					
		        					
		        					int rownum=y+rowCounter+1;
		        					
		        					Cell titleNum=rw.createCell(0);
		        					titleNum.setCellValue(Integer.parseInt(cominSheet.getSheetName()));		        					
		        			
		        					Cell huvi=rw.createCell(1);
		        					String strFormulahuvi= "IF((K"+rownum+"-G"+rownum+"-H"+rownum+">2400000),10,(L"+rownum+"/(K"+rownum+"-G"+rownum+"-H"+rownum+")*100))";
		        					huvi.setCellFormula(strFormulahuvi);
		        					
		        					                        
		        					
		        					Cell andsh=rw.createCell(2);
		        					String strFormulaandsh= "IF(N"+rownum+">2400000,240000,(N"+rownum+"*S"+rownum+")/100)";
		        			//		andsh.setCellType(Cell.CELL_TYPE_FORMULA);
		        					andsh.setCellFormula(strFormulaandsh);
		        					
		        					Cell hoat=rw.createCell(3);
		        					
		        					String strss="IF(S"+rownum+"=0,0,IF((N"+rownum+"-I"+rownum+")>2400000,((((((N"+rownum+"-I"+rownum+"-J"+rownum+")-240000))*0.1+(I"+rownum+"+J"+rownum+")*0.1)))-7000,((((((N"+rownum+"-I"+rownum+"-J"+rownum+")-(N"+rownum+"-I"+rownum+"-J"+rownum+")*S"+rownum+"/100)))*0.1+(I"+rownum+"+J"+rownum+")*0.1)-7000)))";
		        					String strFormulahoat= "IF((ISNUMBER(U"+rownum+"*1)=CH"+rownum+"),0,(K"+rownum+"-L"+rownum+")*0.1-7000+(I"+rownum+"+J"+rownum+")*0.01)";
		        				//	hoat.setCellType(Cell.CELL_TYPE_FORMULA);
		        					hoat.setCellFormula(strss);
		        					
		        					Cell zuruu=rw.createCell(4);
		        					String strs1="C"+rownum+"-O"+rownum+"";
		        					String strFormulazuruu= "IF((ISNUMBER(U"+rownum+"*1)=CH"+rownum+"),0,C"+rownum+"-L"+rownum+")";
		        			//		zuruu.setCellType(Cell.CELL_TYPE_FORMULA);
		        					zuruu.setCellFormula(strs1);
		        					
		        					Cell zuruu1=rw.createCell(5);
		        					String strFormulazuruu1= "IF((ISNUMBER(U"+rownum+"*1)=CH"+rownum+"),0,D"+rownum+"-M"+rownum+")";
		        					String strs2="D"+rownum+"-P"+rownum+"";
		        			//		zuruu1.setCellType(Cell.CELL_TYPE_FORMULA);
		        					zuruu1.setCellFormula(strs2);
		        					
		        					Cell col=rw.createCell(6);
		        					String strFormulacol= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!B$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!B$2,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!B$3,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!B$4,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!B$5,negtgel!U"+rownum+":BL"+rownum+")";
		        			//		col.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col.setCellFormula(strFormulacol);
		        					
		        					Cell col1=rw.createCell(7);
		        					String strFormulacol1= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!F$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!F$2,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!F$3,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!F$4,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!F$5,negtgel!U"+rownum+":BL"+rownum+")";
		        			//		col1.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col1.setCellFormula(strFormulacol1);
		        					
		        					Cell col2=rw.createCell(8);
		        					String strFormulacol2= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!H$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!H$2,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!H$3,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!H$4,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!H$5,negtgel!U"+rownum+":BL"+rownum+")";
		        		//			col2.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col2.setCellFormula(strFormulacol2);
		        					
		        					Cell col3=rw.createCell(9);
		        					String strFormulacol3= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!J$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!J$2,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!J$3,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!J$4,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!J$5,negtgel!U"+rownum+":BL"+rownum+")";
		        			//		col3.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col3.setCellFormula(strFormulacol3);
		        					
		        					Cell col4=rw.createCell(10);
		        					String strFormulacol4= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!L$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!L$2,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!L$3,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!L$4,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!L$5,negtgel!U"+rownum+":BL"+rownum+")";
		        			//		col4.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col4.setCellFormula(strFormulacol4);
		        					
		        					Cell col5=rw.createCell(11);
		        					String strFormulacol5= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!N$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!N$2,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!N$3,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!N$4,negtgel!U"+rownum+":BL"+rownum+")+SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!N$5,negtgel!U"+rownum+":BL"+rownum+")";
		        			//		col5.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col5.setCellFormula(strFormulacol5);
		        					
		        					Cell col6=rw.createCell(12);
		        					String strFormulacol6= "SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!P$1,negtgel!U"+rownum+":BL"+rownum+") + SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!P$2,negtgel!U"+rownum+":BL"+rownum+")+ SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!P$3,negtgel!U"+rownum+":BL"+rownum+")+ SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!P$4,negtgel!U"+rownum+":BL"+rownum+")+ SUMIF(negtgel!U$2:BL$2,'Tsalin uzuulelt'!P$5,negtgel!U"+rownum+":BL"+rownum+")";
		        			//		col6.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col6.setCellFormula(strFormulacol6);
		        					
		        					Cell col7=rw.createCell(13);
		        					String strFormulacol7= "IF(ISNUMBER(U"+rownum+"*1)=CF"+rownum+",0,K"+rownum+"-H"+rownum+"-G"+rownum+")";
		        			//		col7.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col7.setCellFormula(strFormulacol7);
		        					
		        					Cell col8=rw.createCell(14);
		        					String strFormulacol8= "IF(ISNUMBER(U"+rownum+"*1)=CF"+rownum+",0,L"+rownum+")";
		        			//		col8.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col8.setCellFormula(strFormulacol8);
		        					
		        					Cell col9=rw.createCell(15);
		        					
		        			//		Cell test=cominSheet.getRow(rownum).getCell(cellnum);
		        					
		        					String strFormulacol9= "IF(ISNUMBER(U"+rownum+"*1)=CF"+rownum+",0,M"+rownum+")";
		        			//		col9.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col9.setCellFormula(strFormulacol9);
		        					
		        					Cell col10=rw.createCell(16);
		        					String strFormulacol10= "IF(N"+rownum+">2400000,N"+rownum+",0)";
		        				//	col10.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col10.setCellFormula(strFormulacol10);
		        					
		        					Cell col11=rw.createCell(17);
		        					String strFormulacol11= "IF(L"+rownum+"/Q"+rownum+"*100<3,2,10)";
		        			//		col11.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col11.setCellFormula(strFormulacol11);
		        					
		        					Cell col12=rw.createCell(18);
		        				//	String strFormulacol12= "IF(B"+rownum+">8,10,7.8)"; 
		        					
		        					String strs3="IF(CH"+rownum+"=0,0,IF(B"+rownum+">9,10,IF(B"+rownum+">8,B"+rownum+",IF(B"+rownum+">7.7,7.8,IF(B"+rownum+">3,B"+rownum+",IF(B"+rownum+">1.5,2))))))";
		        					String strFormulacol12= "IF(B"+rownum+">9,10,IF(B"+rownum+">8,B"+rownum+",IF(B"+rownum+">7.7,7.8,IF(B"+rownum+">3,B"+rownum+",IF(B"+rownum+">1.5,2)))))";
		        			//		col12.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col12.setCellFormula(strs3);
		        					
		        					Cell col13=rw.createCell(19);
		        					String strFormulacol13= "IF(Q"+rownum+"=0,S"+rownum+",R"+rownum+")";
		        			//		col13.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col13.setCellFormula(strFormulacol13);
		        					
		        					Cell col14=rw.createCell(85);
		        	
		        					String strFormulacol14= "IFERROR(U"+rownum+"*1,0)";
		        			//		col13.setCellType(Cell.CELL_TYPE_FORMULA);
		        					col14.setCellFormula(strFormulacol14);
		        					
		        				
	        					}	        					
	        				}
	        				rowCounter=rowCounter+cominSheet.getLastRowNum();
	        			}
	        		}
    				
    				//************ Negtgel **************//
	        		
	        		if(orderedList.size()==0){
	        			return "false";
	        		}
	        		
	        		System.out.println("orderedlist"+orderedList.toString());
	        		System.out.println("orderedlist size"+orderedList.size());
	        	}
	        	else{
	        		if(fbook.getSheet(lf.getData7())!=null){
	        			
	        			for(int kk=0;kk<fbook.getSheet(lf.getData7()).getLastRowNum();kk++){
							Row currentRow = wb.getSheet(lf.getData7()).getRow(kk);
							if(currentRow!=null){
								for(int y=0;y<30;y++){
									Cell data1 = currentRow.getCell(y);			
									
									if(fbook.getSheet(lf.getData7()).getRow(kk).getCell(y)!=null){
    									switch (fbook.getSheet(wb.getSheet(lf.getData7()).getSheetName()).getRow(kk).getCell(y).getCellTypeEnum()) {
				                    	    case STRING:			
				                    	    	currentRow.getCell(y).setCellValue(fbook.getSheet(wb.getSheet(lf.getData7()).getSheetName()).getRow(kk).getCell(y).getStringCellValue());
				                    	        break;
				                    	    case NUMERIC:
				                    	    	System.out.println(fbook.getSheet(wb.getSheet(lf.getData7()).getSheetName()).getRow(kk).getCell(y).getNumericCellValue());
				                    	    	currentRow.getCell(y).setCellValue(fbook.getSheet(wb.getSheet(lf.getData7()).getSheetName()).getRow(kk).getCell(y).getNumericCellValue());
				                    	        break;
	    								}
    								}
									
								}								
							}
	        			}
	        			
	        			
	        			JSONObject obj=new JSONObject();
						obj.put("fname", lf.getData7());
						arr.put(obj);
		        		LnkAuditForm la = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and data7='"+lf.getData7()+"'", "current");
						la.setData6(1);
						dao.PeaceCrud(la, "LnkMainUser", "update", (long) la.getId(), 0, 0, null);	
						
						if(lf.getData7().toUpperCase().equalsIgnoreCase("АБ") || lf.getData7().toUpperCase().equalsIgnoreCase("А-5.2.1") || lf.getData7().toUpperCase().equalsIgnoreCase("А-3")){
							List<LnkAuditForm> fms=(List<LnkAuditForm>) dao.getHQLResult("from LnkAuditForm where appid="+id+" and data6=1 and data7 in ('АБ','А-3','А-5.2.1') ", "list");
			        		
							if(fms.size()==3){						
								dao.insertBatchSQL("update lnk_audit_forms set data10=0 where appid="+id+" and data10=1");						
							}
						}
						
	        		}
	        		else{
	        			return "false";
	        		}
	        		
	        	}
	        
				fis.close();
				String uuid = UUID.randomUUID().toString()+".xlsx";
	            FileOutputStream out = new FileOutputStream("upload-dir"+File.separator+id+ File.separator+uuid);
				wb.write(out);
				out.close();
				if(excelpath.exists()){
					excelpath.delete();
				}
				/*if(formpath.exists()){
					formpath.delete();
				}*/
				furl = File.separator + SAVE_DIR + File.separator+id+File.separator + uuid;		
			
				
				if(laf.getLevelid()==1 && laf.getData13()==2){
					main.setExcelurlplan(furl);
				}
				else if(laf.getLevelid()==2 && laf.getData13()==2){
					main.setExcelurlprocess(furl);;
				}
				else{
					if(lf.getData13()==1){
						main.setExcelurlplan(furl);
					}
					else{
						main.setExcelurlprocess(furl);;
					}
				}
				
				dao.PeaceCrud(main, "MainAuditRegistration", "update", (long) id, 0, 0, null);	
				return "true";
				
			}
			return arr.toString();
	}
	
	@GetMapping("/a4/{formid}/{id}")
	public String handleExcelUpload(@PathVariable long id,@PathVariable long formid, Model model, HttpServletRequest req) throws IllegalStateException, IOException, ParseException, InvalidFormatException, JSONException {
					
			MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");		
		
			Path currentRelativePath = Paths.get("");
			String realpath = currentRelativePath.toAbsolutePath().toString();
			
			LutForm lf = (LutForm) dao.getHQLResult("from LutForm t where t.id='"+formid+"'", "current");
			
			File excelpath = null;
			if(lf.getData13()==1){		
				if(main.getExcelurlplan()!=null){
					excelpath = new File(realpath+"/"+main.getExcelurlplan());
				}
				else{
					JSONObject obj= new JSONObject();
					obj.put("plan", false);
					return obj.toString();
				}
			}
			else if(lf.getData13()==3){
				if(main.getExcelurlprocess()!=null){
					excelpath = new File(realpath+"/"+main.getExcelurlprocess());
				}
				else{
					JSONObject obj= new JSONObject();
					obj.put("process", false);
					return obj.toString();
				}
			}
			
			if(!excelpath.exists()){
				JSONObject obj= new JSONObject();
				obj.put("process", false);
				return obj.toString();
			}
			
			FileInputStream ffis = new FileInputStream(excelpath);
			Workbook fbook = new XSSFWorkbook(ffis);
			FormulaEvaluator evaluator = fbook.getCreationHelper().createFormulaEvaluator();
			JSONArray arr= new JSONArray();
			if(fbook.getSheet(lf.getData7())!=null){
				for(int kk=11;kk<17;kk++){
					Row currentRow = fbook.getSheet(lf.getData7()).getRow(kk);
					JSONObject obj= new JSONObject();
					obj.put("data1", currentRow.getCell(0).getStringCellValue());
					
					 if (currentRow.getCell(1) != null && currentRow.getCell(1).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = currentRow.getCell(1).getCellFormula(); 
	                    Cell c=currentRow.getCell(1);
	                    if (formula != null) { 
	                    	currentRow.getCell(1).setCellFormula(formula);
	                    	
	                    	CellValue cellValue = evaluator.evaluate(c);
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	obj.put("data2", cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	obj.put("data2", cellValue.getNumberValue());
	                    	        break;
	                    	}                     
	                    } 
	                } 
					if (currentRow.getCell(2) != null && currentRow.getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = currentRow.getCell(2).getCellFormula(); 
	                    Cell c=currentRow.getCell(2);
	                    if (formula != null) { 
	                    	c.setCellFormula(formula);
	                    	
	                    	CellValue cellValue = evaluator.evaluate(c);
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	obj.put("data3", cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	obj.put("data3", cellValue.getNumberValue());
	                    	        break;
	                    	}                     
	                    } 
	                } 
					if (currentRow.getCell(3) != null && currentRow.getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = currentRow.getCell(3).getCellFormula(); 
	                    Cell c=currentRow.getCell(3);
	                    if (formula != null) { 
	                    	c.setCellFormula(formula);
	                    	
	                    	CellValue cellValue = evaluator.evaluate(c);
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	obj.put("data4", cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	obj.put("data4", cellValue.getNumberValue());
	                    	        break;
	                    	}                     
	                    } 
	                } 
					if (currentRow.getCell(4) != null && currentRow.getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = currentRow.getCell(4).getCellFormula(); 
	                    Cell c=currentRow.getCell(4);
	                    if (formula != null) { 
	                    	c.setCellFormula(formula);
	                    	
	                    	CellValue cellValue = evaluator.evaluate(c);
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	obj.put("data5", cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	obj.put("data5", cellValue.getNumberValue());
	                    	        break;
	                    	}                     
	                    } 
	                } 
					arr.put(obj);
					System.out.println(obj.toString());
				}
			}
			return arr.toString();
	}
	
	@PostMapping("/a4/{formid}/{id}")
	public String handleExcelPost(@PathVariable long id,@PathVariable long formid, Model model,@RequestBody String request, HttpServletRequest req) throws IllegalStateException, IOException, ParseException, InvalidFormatException, JSONException {
					
		MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");	
		
		LutForm lf = (LutForm) dao.getHQLResult("from LutForm t where t.id='"+formid+"'", "current");
		
		LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and t.formid="+formid+"", "current");
		
		laf.setData6(1);
		dao.PeaceCrud(laf, "LnkAuditForm", "update", (long) laf.getId(), 0, 0, null);	
		
		Path currentRelativePath = Paths.get("");
		String realpath = currentRelativePath.toAbsolutePath().toString();
		Workbook wb =null;
		FileInputStream fis=null;
		JSONArray arr= new JSONArray();
		JSONObject str= new JSONObject(request);
		File excelpath = null;
		
		if(lf.getData13()==1){
			excelpath= new File(realpath+"/"+main.getExcelurlplan());
			fis = new FileInputStream(excelpath);
			wb = new XSSFWorkbook(fis);
			if(wb.getSheet("А-4")!=null){
				Row currentRow = wb.getSheet("А-4").getRow(18);
				FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
				Cell cell1 = currentRow.getCell(1);
				Cell cell2 = currentRow.getCell(2);
				Cell cell3 = currentRow.getCell(3);
				cell1.setCellValue(str.getString("dans"));
				double a=0;
				a=str.getDouble("percent");
				a=a/100;
				cell2.setCellValue(a);
				CellValue val=evaluator.evaluate(cell3);
				main.setMatter(String.valueOf(val.getNumberValue()));
				System.out.println("ssss"+str.getDouble("percent")/100);
			}
		}
		else if(lf.getData13()==3){
			excelpath = new File(realpath+"/"+main.getExcelurlprocess());
			fis = new FileInputStream(excelpath);
			wb = new XSSFWorkbook(fis);
			
			if(wb.getSheet(lf.getData7())!=null){
				Row currentRow = wb.getSheet(lf.getData7()).getRow(18);
				Cell cell1 = currentRow.getCell(1);
				Cell cell2 = currentRow.getCell(2);
				cell1.setCellValue(str.getString("dans"));
				double a=0;
				a=str.getDouble("percent");
				a=a/100;
				cell2.setCellValue(a);
			
			}
		}
		
		System.out.println("sss"+lf.getData13());
		
		
		
		System.out.println(str);
	
		
		
		
		String uuid = UUID.randomUUID().toString()+".xlsx";
        FileOutputStream out = new FileOutputStream("upload-dir"+File.separator+id+ File.separator+uuid);
		wb.write(out);
		fis.close();
		out.close();
		if(excelpath.exists()){
			excelpath.delete();
		}
		String SAVE_DIR = "upload-dir";
		String furl = "/" + SAVE_DIR ;		
		furl = File.separator + SAVE_DIR +File.separator+ id +File.separator+ uuid;			
		if(lf.getData13()==1){
			main.setExcelurlplan(furl);
		}
		else{
			main.setExcelurlprocess(furl);;
		}
		dao.PeaceCrud(main, "MainAuditRegistration", "update", (long) id, 0, 0, null);	
		return "true";
	}
	
	@RequestMapping(value="/verify/{type}/{mid}/{formid}",method=RequestMethod.GET)
	public boolean verify(@PathVariable String type, @PathVariable long formid,@PathVariable long mid,HttpServletRequest req,HttpServletResponse response) throws JSONException, DocumentException, Exception {
		JsonObject obj= new JsonObject();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (type.equalsIgnoreCase("nbb")){
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				FileInputStream fis = null;
				LutForm lf = (LutForm) dao.getHQLResult("from LutForm t where t.id='"+formid+"'", "current");
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				File file = null;
				
				LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+mid+"' and t.formid="+formid+"", "current");
							
				System.out.println("level"+laf.getLevelid()+"data : "+laf.getData13());
				
				if(laf.getLevelid()==1 && laf.getData13()==2){
					file = new File(realpath+"/"+main.getExcelurlplan());
				}
				else if(laf.getLevelid()==2 && laf.getData13()==2){
					file = new File(realpath+"/"+main.getExcelurlprocess());
				}
				else if(laf.getLevelid()==2 && laf.getData13()==4){
					file = new File(realpath+"/"+main.getExcelurlplan());
				}
				else if(laf.getLevelid()==1 && laf.getData13()==4){
					file = new File(realpath+"/"+main.getExcelurlprocess());
				}
				else if(laf.getData13()==3){
					file = new File(realpath+"/"+main.getExcelurlprocess());
				}
				else{
					if(lf.getData13()==1){
						file = new File(realpath+"/"+main.getExcelurlplan());
					}
					else{
						file = new File(realpath+"/"+main.getExcelurlprocess());
					}
				}

				if(main.getExcelurlplan()==null && main.getExcelurlprocess()==null){
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
	
	@RequestMapping(value="/export/{type}/{mid}/{id}",method=RequestMethod.GET)
	public boolean checklicense(@PathVariable String type, @PathVariable long id,@PathVariable long mid,HttpServletRequest req,HttpServletResponse response) throws JSONException, DocumentException, Exception {
		JsonObject obj= new JsonObject();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(type.equalsIgnoreCase("problem")){
	    	
			ClassPathResource excelFile = new ClassPathResource("static/files/ab.xlsx");
			File fis=excelFile.getFile();
			Workbook workbook = WorkbookFactory.create(fis);
			List<LnkAuditProblem> aw=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where finish=0 and active=1 and t.appid='"+mid+"'", "list");
			Sheet sht=workbook.getSheet("В-4");
			int i = 7;
			for(LnkAuditProblem item:aw){				
				Row row=  sht.createRow(i);
				System.out.println(item.getAcc());
				row.createCell((short) 0).setCellValue(i-6);
				row.createCell((short) 1).setCellValue(item.getAcc());
				row.createCell((short) 2).setCellValue(item.getProblem());
				row.createCell((short) 3).setCellValue(item.getAmount());
				row.createCell((short) 4).setCellValue(item.getMatter());
				row.createCell((short) 5).setCellValue(item.getCommentType());
				row.createCell((short) 6).setCellValue(item.getAktName());
				row.createCell((short) 7).setCellValue(item.getAktZaalt());
				row.createCell((short) 8).setCellValue(item.getComResult());
				row.createCell((short) 9).setCellValue(item.getComAmount());
				row.createCell((short) 10).setCellValue(item.getComMatter());
				row.createCell((short) 11).setCellValue(item.getResult());
				row.createCell((short) 12).setCellValue(item.getComAktName());
				row.createCell((short) 13).setCellValue(item.getComAktZaalt());
				row.createCell((short) 14).setCellValue(item.getAccCode());
				i++;	
			}
			if(aw.size()>0){
				String xname=aw.get(0).getMainAuditRegistration().getOrgname().trim();
				xname = URLEncoder.encode(xname,"UTF-8"); 
		        try (ServletOutputStream outputStream = response.getOutputStream()) {
					response.setContentType("application/ms-excel; charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
		            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
		            workbook.write(outputStream);
		            outputStream.close();
		        }				
		        catch (Exception e) {
		        	System.out.println("ishe orov");
				}
			}
			else{
				return false;
			}
			
	        System.out.println("excel exported");
			return true;
		}
		else if(type.equalsIgnoreCase("last")){
	    	
			
		//	List<LnkAuditProblem> aw=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.appid='"+mid+"' and t.finish=1", "list");
			
			
			List<Object[]> lobj=  (List<Object[]>) dao.getNativeSQLResult("select acc,problem,sum(amount),sum(com_amount),sum(amount)-sum(com_amount),sum(final_akt_amount)+ sum(final_ash_amount)+sum(final_adv_amount), sum(final_akt_amount), sum(final_ash_amount), sum(final_adv_amount), sum(com_amount)+sum(final_akt_amount)+ sum(final_ash_amount)+sum(final_adv_amount) from lnk_audit_problems where finish=1 and appid="+mid+" GROUP BY acc, problem order by amount desc", "list");
			
			FileInputStream fis = null;
			MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
			Path currentRelativePath = Paths.get("");
			String realpath = currentRelativePath.toAbsolutePath().toString();
			File file = null;
			LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+mid+"' and t.formid="+id+"", "current");
					
			if(laf.getLevelid()==1){
				file = new File(realpath+"/"+main.getExcelurlplan());
			}
			else if(laf.getLevelid()==2){
				file = new File(realpath+"/"+main.getExcelurlprocess());
			}
			
			if(main.getExcelurlplan()==null && main.getExcelurlprocess()==null){
				return false;
			}
			else if(!file.exists()){
				return false;
			}
			else{
				fis = new FileInputStream(file);
				
				Workbook workbook = WorkbookFactory.create(fis);
	        	FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
	        	CellStyle wrapText=workbook.createCellStyle();
	        	wrapText.setWrapText(true);
	        	
	        	CellStyle borderStyle = workbook.createCellStyle();
	        	borderStyle.setBorderBottom(CellStyle.BORDER_THIN);
	        	borderStyle.setBorderLeft(CellStyle.BORDER_THIN);
	        	borderStyle.setBorderRight(CellStyle.BORDER_THIN);
	        	borderStyle.setBorderTop(CellStyle.BORDER_THIN);
	        	borderStyle.setAlignment(CellStyle.ALIGN_CENTER);
	        	borderStyle.setWrapText(true);
	        	borderStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	        	Sheet lsheet= workbook.getSheet("АБ-20");
	        	int y=1;
	        	
	        	if(lobj.size()<lsheet.getLastRowNum()){
	        		for(int a=lobj.size()+2; a<lsheet.getLastRowNum()+1;a++){
	        			Row rw=lsheet.getRow(a);
	        			if(rw!=null){
	        				lsheet.removeRow(rw);
	        			}
		        	}
	        	}
	        		        	
	        	for(Object[] item:lobj){
	        		Row row=lsheet.createRow(y+6);
					row.createCell((short) 0).setCellValue(y);
					row.createCell((short) 1).setCellValue(String.valueOf(item[0]));
					row.createCell((short) 2).setCellValue(String.valueOf(item[1]));
					row.createCell((short) 3).setCellValue(Double.parseDouble(String.valueOf(item[2])));
					row.createCell((short) 4).setCellValue(Double.parseDouble(String.valueOf(item[3])));
					row.createCell((short) 5).setCellValue(Double.parseDouble(String.valueOf(item[4])));
					row.createCell((short) 6).setCellValue(Double.parseDouble(String.valueOf(item[5])));
					row.createCell((short) 7).setCellValue(Double.parseDouble(String.valueOf(item[6])));
					row.createCell((short) 8).setCellValue(Double.parseDouble(String.valueOf(item[7])));
					row.createCell((short) 9).setCellValue(Double.parseDouble(String.valueOf(item[8])));
					row.createCell((short) 10).setCellValue(Double.parseDouble(String.valueOf(item[9])));
					row.getCell(0).setCellStyle(borderStyle);
					row.getCell(1).setCellStyle(borderStyle);
					row.getCell(2).setCellStyle(borderStyle);
					row.getCell(3).setCellStyle(borderStyle);
					row.getCell(4).setCellStyle(borderStyle);
					row.getCell(5).setCellStyle(borderStyle);
					row.getCell(6).setCellStyle(borderStyle);
					row.getCell(7).setCellStyle(borderStyle);
					row.getCell(8).setCellStyle(borderStyle);
					row.getCell(9).setCellStyle(borderStyle);
					row.getCell(10).setCellStyle(borderStyle);
	        		y++;
	        	}
	        	
	        	String uuid = UUID.randomUUID().toString()+".xlsx";
	            FileOutputStream out = new FileOutputStream("upload-dir"+File.separator+mid+ File.separator+uuid);
	            workbook.write(out);
				out.close();
				if(file.exists()){
					file.delete();
				}
				String SAVE_DIR = "upload-dir";
				String furl = "/" + SAVE_DIR ;		
				furl = File.separator  + SAVE_DIR + File.separator+mid+File.separator + uuid;	
				if(laf.getLevelid()==1){
					main.setExcelurlplan(furl);
				}
				else{
					main.setExcelurlprocess(furl);
				}
				
				dao.PeaceCrud(main, "MainAuditRegistration", "update", (long) mid, 0, 0, null);	
		
				return true;
			}			
			
		}
		
		else if(type.equalsIgnoreCase("integration")){
	    	
			FileInputStream fis = null;
			MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
			Path currentRelativePath = Paths.get("");
			String realpath = currentRelativePath.toAbsolutePath().toString();
			File file = null;
			LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+mid+"' and t.formid="+id+"", "current");
					
			if(laf.getLevelid()==1){
				file = new File(realpath+"/"+main.getExcelurlplan());
			}
			else if(laf.getLevelid()==2){
				file = new File(realpath+"/"+main.getExcelurlprocess());
			}
			
			if(main.getExcelurlplan()==null && main.getExcelurlprocess()==null){
				return false;
			}
			else if(!file.exists()){
				return false;
			}
			else{
				fis = new FileInputStream(file);
				
				Workbook workbook = WorkbookFactory.create(fis);
	      
	        	for(int i=workbook.getNumberOfSheets()-1;i>=0;i--){
	                Sheet tmpSheet =workbook.getSheetAt(i);
	                if(!tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("АБ-20")){
	                	workbook.removeSheetAt(i);
	                }
	            } 
				

				String xname=main.getOrgname().trim();
				xname = URLEncoder.encode(xname,"UTF-8"); 
		        try (ServletOutputStream outputStream = response.getOutputStream()) {
					response.setContentType("application/ms-excel; charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
		            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
		            workbook.write(outputStream);
		            outputStream.close();
		        }
				
		        catch (Exception e) {
		        	System.out.println("ishe orov");
				}
		        System.out.println("excel exported");
				return true;
			}			
			
		}
		
		else if(type.equalsIgnoreCase("ash")){
	    	
			ClassPathResource excelFile = new ClassPathResource("static/files/ash.xlsx");
			File fis=excelFile.getFile();
			Workbook workbook = WorkbookFactory.create(fis);
			LnkAuditProblem aw=  (LnkAuditProblem) dao.getHQLResult("from LnkAuditProblem t where t.id='"+id+"'", "current");
			Sheet sht=workbook.getSheet("Sheet1");
			
			Row row4=  sht.getRow(3);			
			row4.getCell(2).setCellValue(aw.getMainAuditRegistration().getAname());
			row4.getCell(6).setCellValue(aw.getMainAuditRegistration().getGencode());
			
			Row row6=  sht.getRow(5);			
			row6.getCell(5).setCellValue(special);
			row6.getCell(7).setCellValue(aw.getAmount());
			
			Row row8=  sht.getRow(7);		
			row8.getCell(3).setCellValue(aw.getProblem());
			
			Row row11 =  sht.getRow(8);		
			row11.getCell(4).setCellValue(aw.getComAktName());
			
			Row row12 =  sht.getRow(9);		
			row12.getCell(4).setCellValue(aw.getComAktZaalt());
			
			for(LnkMainUser item:aw.getMainAuditRegistration().getLnkMainUsers()){
				
			}
			
		/*	Row row28 =  sht.getRow(28);		
			row28.getCell(0).setCellValue(aw.getMainAuditRegistration().getAname());
			row28.getCell(1).setCellValue(aw.getMainAuditRegistration().getApos());
			row28.getCell(3).setCellValue(aw.getMainAuditRegistration().getOrgname());*/
			
			String xname=aw.getMainAuditRegistration().getOrgname().trim();
			xname = URLEncoder.encode(xname,"UTF-8"); 
	        try (ServletOutputStream outputStream = response.getOutputStream()) {
				response.setContentType("application/ms-excel; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
	            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
	            workbook.write(outputStream);
	            outputStream.close();
	        }
			
	        catch (Exception e) {
	        	System.out.println("ishe orov");
			}
	        System.out.println("excel exported");
			return true;
		}
		else if(type.equalsIgnoreCase("akt")){
	    	
			ClassPathResource excelFile = new ClassPathResource("static/files/akt.xlsx");
			File fis=excelFile.getFile();
			Workbook workbook = WorkbookFactory.create(fis);
			LnkAuditProblem aw=  (LnkAuditProblem) dao.getHQLResult("from LnkAuditProblem t where t.id='"+id+"'", "current");
			Sheet sht=workbook.getSheet("В-4-1Акт");
			
			Row row6=  sht.getRow(6);			
			row6.getCell(1).setCellValue(aw.getMainAuditRegistration().getAname());
			row6.getCell(4).setCellValue(aw.getMainAuditRegistration().getGencode());
			
			Row row8=  sht.getRow(8);		
			row8.getCell(3).setCellValue(special);
			row8.getCell(5).setCellValue(aw.getAmount());
			
			Row row11 =  sht.getRow(11);		
			row11.getCell(3).setCellValue(aw.getComAktName());
			
			Row row12 =  sht.getRow(12);		
			row12.getCell(3).setCellValue(aw.getComAktZaalt());
			
			for(LnkMainUser item:aw.getMainAuditRegistration().getLnkMainUsers()){
				
			}
			
			Row row28 =  sht.getRow(28);		
			row28.getCell(0).setCellValue(aw.getMainAuditRegistration().getAname());
			row28.getCell(1).setCellValue(aw.getMainAuditRegistration().getApos());
			row28.getCell(3).setCellValue(aw.getMainAuditRegistration().getOrgname());
			
			String xname=aw.getMainAuditRegistration().getOrgname().trim();
			xname = URLEncoder.encode(xname,"UTF-8"); 
	        try (ServletOutputStream outputStream = response.getOutputStream()) {
				response.setContentType("application/ms-excel; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
	            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
	            workbook.write(outputStream);
	            outputStream.close();
	        }
			
	        catch (Exception e) {
	        	System.out.println("ishe orov");
			}
	        System.out.println("excel exported");
			return true;
		}
		else if(type.equalsIgnoreCase("offer")){
	    	
			ClassPathResource excelFile = new ClassPathResource("static/files/offer.xlsx");
			File fis=excelFile.getFile();
			Workbook workbook = WorkbookFactory.create(fis);
			MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
			List<LnkAuditProblem> aw=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.commentType>0 and t.finish=0 and t.appid='"+mid+"'", "list");
			if(aw.size()>0){
				Date d1 = new Date();
				SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
				String special = df.format(d1);
				dao.getNativeSQLResult("update lnk_audit_problems set is_active=1, offer_date='"+special+"' where appid="+mid+" and finish=0 and comment_type>0 and finish=0", "update");
				dao.getNativeSQLResult("update lnk_audit_problems set is_active=0, offer_date='"+special+"' where appid="+mid+" and finish=0 and comment_type=0 and finish=0", "update");
				Sheet sht=workbook.getSheet("В-4");
				int i = 7;
				for(LnkAuditProblem item:aw){				
					Row row=  sht.createRow(i);
					row.createCell((short) 0).setCellValue(i-6);
					row.createCell((short) 1).setCellValue(item.getAcc());
					row.createCell((short) 2).setCellValue(item.getProblem());
					row.createCell((short) 3).setCellValue(item.getAmount());
					row.createCell((short) 4).setCellValue(item.getMatter());
					row.createCell((short) 5).setCellValue(item.getCommentType());
					row.createCell((short) 6).setCellValue(item.getAktName());
					row.createCell((short) 7).setCellValue(item.getAktZaalt());
					i++;	
				}
				String xname=aw.get(0).getMainAuditRegistration().getOrgname().trim();
				xname = URLEncoder.encode(xname,"UTF-8"); 
		        try (ServletOutputStream outputStream = response.getOutputStream()) {
					response.setContentType("application/ms-excel; charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
		            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
		            workbook.write(outputStream);
		            outputStream.close();
		        }
				
		        catch (Exception e) {
		        	System.out.println("ishe orov");
				}
		        System.out.println("excel exported");
				return true;
			}
			else{
				String xname=main.getOrgname().trim();
				xname = URLEncoder.encode(xname,"UTF-8"); 
		        try (ServletOutputStream outputStream = response.getOutputStream()) {
					response.setContentType("application/ms-excel; charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
		            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
		            workbook.write(outputStream);
		            outputStream.close();
		        }
				return false;
			}
			
		}
		else if (type.equalsIgnoreCase("nbb")){
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				LutForm lf = (LutForm) dao.getHQLResult("from LutForm t where t.id="+id+"", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				File file = null;
				LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+mid+"' and t.formid="+id+"", "current");
				
				System.out.println("##"+laf.getData13());
				System.out.println("##"+laf.getLevelid());
				if(main.getAutype()==2){
					 if(laf.getLevelid()==1){
						file = new File(realpath+"/"+main.getExcelurlplan());
					}
					 else{
						 file = new File(realpath+"/"+main.getExcelurlprocess());
					 }
				}
				else{

					if(laf.getLevelid()==1 && laf.getData13()==2){
						file = new File(realpath+"/"+main.getExcelurlplan());
					}
					else if(laf.getLevelid()==2 && laf.getData13()==2){
						file = new File(realpath+"/"+main.getExcelurlprocess());
					}
					else if(laf.getLevelid()==2 && laf.getData13()==4){
						file = new File(realpath+"/"+main.getExcelurlplan());
					}
					else if(laf.getLevelid()==1 && laf.getData13()==4){
						file = new File(realpath+"/"+main.getExcelurlprocess());
					}
					else if(laf.getData13()==3){
						file = new File(realpath+"/"+main.getExcelurlprocess());
					}
					
					else{
						if(lf.getData13()==1){
							file = new File(realpath+"/"+main.getExcelurlplan());
						}
						else{
							file = new File(realpath+"/"+main.getExcelurlprocess());
						}
					}
				}
				
				
				
				if(main.getExcelurlplan()==null && main.getExcelurlprocess()==null){
					return false;
				}
				else if(!file.exists()){
					return false;
				}
				else{
					fis = new FileInputStream(file);
					
					Workbook workbook = WorkbookFactory.create(fis);
		        	FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		        			        	
		        	if(lf.getData7().toUpperCase().equalsIgnoreCase("АТ")){
		        		for(int i=0;i<workbook.getNumberOfSheets();i++){
					        Sheet sh = workbook.getSheetAt(i); 
					        for(Row r : sh) { 
					        	if(r!=null){
					        		 for(Cell c : r) { 
		    				                if (c != null && c.getCellTypeEnum()== CellType.FORMULA) { 
		    				                    String formula = c.getCellFormula(); 
		    				                    if (formula != null) { 
		    				                    	c.setCellFormula(formula);
		    				                    	CellValue cellValue = evaluator.evaluate(c);
		    				                    	 
		    				                    	switch (cellValue.getCellTypeEnum()) {
		    				                    	    case STRING:
		    				                    	    	c.setCellType(CellType.STRING);
		    				                    	    	c.setCellValue(cellValue.getStringValue());
		    				                    	        break;
		    				                    	    case BOOLEAN:
		    				                    	        System.out.print(cellValue.getBooleanValue());
		    				                    	        break;
		    				                    	    case NUMERIC:
		    				                    	    	c.setCellType(CellType.NUMERIC);
		    				                    	    	c.setCellValue(cellValue.getNumberValue());
		    				                    	        break;
		    				                    	}
		    				                        evaluator.clearAllCachedResultValues();                       
		    				                    } 
		    				                } 
		    				            } 
					        	 }
					        } 
		    				
		    			}
		        	}
		        	else if(lf.getData7().toUpperCase().equalsIgnoreCase("18.Payroll")){
		        		String[] sheetnames = {"negtgel","Tsalin uzuulelt","niit"};
		        		FormulaEvaluator evaluatorNegtgel = workbook.getCreationHelper().createFormulaEvaluator();
		        		for(String item:sheetnames){
		        			Sheet sh = workbook.getSheet(item); 
		        			if(sh.getSheetName().equalsIgnoreCase("niit")){
		        				 for(Row r : sh) { 
							        	if(r!=null){
							        		 for(Cell c : r) { 
				    				                if (c != null && c.getCellTypeEnum()== CellType.FORMULA) { 
				    				                    String formula = c.getCellFormula(); 
				    				                    if (formula != null) { 
				    				                    	c.setCellFormula(formula);
				    				                    
				    				                    	CellValue cellValue = evaluatorNegtgel.evaluate(c);
				    				                    	 
				    				                    	switch (cellValue.getCellTypeEnum()) {
				    				                    	    case STRING:
				    				                    	    	c.setCellType(CellType.STRING);
				    				                    	    	c.setCellValue(cellValue.getStringValue());
				    				                    	        break;
				    				                    	    case BOOLEAN:
				    				                    	        System.out.print(cellValue.getBooleanValue());
				    				                    	        break;
				    				                    	    case NUMERIC:
				    				                    	    	c.setCellType(CellType.NUMERIC);
				    				                    	    	c.setCellValue(cellValue.getNumberValue());
				    				                    	        break;
				    				                    	}
				    				                    	evaluatorNegtgel.clearAllCachedResultValues();                       
				    				                    }
				    				                } 
				    				            } 
							        	 }
							        }
		        			}
		        			//evaluatorNegtgel.evaluateAll();
					      /*  for(Row r : sh) { 
					        	if(r!=null){
					        		 for(Cell c : r) { 
		    				                if (c != null && c.getCellTypeEnum()== CellType.FORMULA) { 
		    				                    String formula = c.getCellFormula(); 
		    				                    if (formula != null) { 
		    				                    	System.out.println(formula);
		    				                    	c.setCellFormula(formula);
		    				                    
		    				                    	CellValue cellValue = evaluatorNegtgel.evaluate(c);
		    				                    	 
		    				                    	switch (cellValue.getCellTypeEnum()) {
		    				                    	    case STRING:
		    				                    	    	c.setCellType(CellType.STRING);
		    				                    	    	c.setCellValue(cellValue.getStringValue());
		    				                    	        break;
		    				                    	    case BOOLEAN:
		    				                    	        System.out.print(cellValue.getBooleanValue());
		    				                    	        break;
		    				                    	    case NUMERIC:
		    				                    	    	c.setCellType(CellType.NUMERIC);
		    				                    	    	c.setCellValue(cellValue.getNumberValue());
		    				                    	        break;
		    				                    	}
		    				                    	evaluatorNegtgel.clearAllCachedResultValues();                       
		    				                    }
		    				                } 
		    				            } 
					        	 }
					        }*/
		        		} 
		        	}
		        	else if(lf.getData7().toUpperCase().equalsIgnoreCase("АБ") || lf.getData7().toUpperCase().equalsIgnoreCase("А-5.2.1") || lf.getData7().toUpperCase().equalsIgnoreCase("А-3")){
		        		String[] sheetnames = {"АБ", "А-5.2.1", "А-3"};
		        		for(String item:sheetnames){
		        			Sheet sh = workbook.getSheet(item); 
					        for(Row r : sh) { 
					        	if(r!=null){
					        		 for(Cell c : r) { 
		    				                if (c != null && c.getCellTypeEnum()== CellType.FORMULA) { 
		    				                    String formula = c.getCellFormula(); 
		    				                    if (formula != null) { 
		    				                    	c.setCellFormula(formula);
		    				                    	
		    				                    	CellValue cellValue = evaluator.evaluate(c);
		    				                    	 
		    				                    	switch (cellValue.getCellTypeEnum()) {
		    				                    	    case STRING:
		    				                    	    	c.setCellType(CellType.STRING);
		    				                    	    	c.setCellValue(cellValue.getStringValue());
		    				                    	        break;
		    				                    	    case BOOLEAN:
		    				                    	        System.out.print(cellValue.getBooleanValue());
		    				                    	        break;
		    				                    	    case NUMERIC:
		    				                    	    	c.setCellType(CellType.NUMERIC);
		    				                    	    	c.setCellValue(cellValue.getNumberValue());
		    				                    	        break;
		    				                    	}
		    				                        evaluator.clearAllCachedResultValues();                       
		    				                    } 
		    				                } 
		    				            } 
					        	 }
					        }
		        		} 
		        	}
		        	
		        	else{
		        		Sheet sh = workbook.getSheet(lf.getData7().trim()); 
		        		for(int y=0;y<workbook.getNumberOfSheets();y++){
		        			System.out.println(workbook.getSheetAt(y).getSheetName());
		        		}
		        		if(sh!=null){
		        			
		        			List<String> tr =Arrays.asList("В-3-1Т","В-3-2Т","В-3-3Т","В-3-4Т","В-3-5Т","В-3-6Т","В-3-7Т","В-3-8Т","В-3-9Т");
		        			
		        			if(!tr.contains(sh.getSheetName())){
		        				 for(Row r : sh) { 
							        	if(r!=null){
							        		 for(Cell c : r) { 
			    				                if (c != null && c.getCellTypeEnum()== CellType.FORMULA) { 
			    				                    String formula = c.getCellFormula(); 
			    				                    if (formula != null) { 
			    				                    	c.setCellFormula(formula);	    				                    	
			    				                    	CellValue cellValue = evaluator.evaluate(c);	    				                    	 
			    				                    	switch (cellValue.getCellTypeEnum()) {
			    				                    	    case STRING:	    				                    	    	
			    				                    	    	c.setCellType(CellType.STRING);
			    				                    	    	c.setCellValue(cellValue.getStringValue());
			    				                    	        break;
			    				                    	    case BOOLEAN:
			    				                    	        System.out.print(cellValue.getBooleanValue());
			    				                    	        break;
			    				                    	    case NUMERIC:
			    				                    	    	c.setCellType(CellType.NUMERIC);
			    				                    	    	c.setCellValue(cellValue.getNumberValue());
			    				                    	        break;
			    				                    	}
			    				                        evaluator.clearAllCachedResultValues();                       
			    				                    } 
			    				                } 
			    				            } 
							        	}
							        }
		        			}
		        			
		        		}
		        		else{
		        			return false;
		        		}
		        	}
		        	
		        	if(!lf.getData7().toUpperCase().equalsIgnoreCase("АТ")){
		        	/*	if(lf.getData7().toUpperCase().equalsIgnoreCase("АБ") || lf.getData7().toUpperCase().equalsIgnoreCase("А-5.2.1") || lf.getData7().toUpperCase().equalsIgnoreCase("А-3")){
		        			
		        			for(int i=workbook.getNumberOfSheets()-1;i>=0;i--){
				                Sheet tmpSheet =workbook.getSheetAt(i);
				                if(tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("АБ") || tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("А-5.2.1") || tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("А-3")){

				                }
				                else{
				                	workbook.removeSheetAt(i);
				                }
				            } 
		        		}
		        		else*/
		        			if(lf.getData7().toUpperCase().equalsIgnoreCase("18.Payroll")){
		        			for(int i=workbook.getNumberOfSheets()-1;i>=0;i--){
				                Sheet tmpSheet =workbook.getSheetAt(i);
				                if(tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("negtgel")  
				                		||tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("niit") ||tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("Tsalin uzuulelt") 
			                		 ){
				                	 if(tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase("Tsalin uzuulelt")){			                		
				                		
				                		 workbook.getSheetAt(i).protectSheet("abc");
			                		     workbook.setSheetHidden(i, HSSFWorkbook.SHEET_STATE_VERY_HIDDEN);
				                	 }
				                }
				                else{
				                	workbook.removeSheetAt(i);
				                }
				            } 
		        		}
		        		else{
		        			for(int i=workbook.getNumberOfSheets()-1;i>=0;i--){
				                Sheet tmpSheet =workbook.getSheetAt(i);
				                if(!tmpSheet.getSheetName().toUpperCase().equalsIgnoreCase(lf.getData7().toUpperCase())){
				                	workbook.removeSheetAt(i);
				                }
				            } 
		        		}
		        	}
		        	
		        	System.out.println("sss"+lf.getData7());
		        	
				//	XSSFSheet newSheet=workbook.cloneSheet(0);
					String xname=main.getOrgname().trim()+"-"+lf.getData7();
					xname = URLEncoder.encode(xname,"UTF-8"); 
			        try (ServletOutputStream outputStream = response.getOutputStream()) {
						response.setContentType("application/ms-excel; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
			            response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".xlsx");
			            workbook.write(outputStream);
			            outputStream.close();
			        }
					
			        catch (Exception e) {
			        	System.out.println("ishe orov");
					}
			        System.out.println("excel exported");
					return true;
				}
	    		
			}
		}
		return false;
	}

}
