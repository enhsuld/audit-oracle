package com.nbb.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger.OutputField;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
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
import com.nbb.models.fn.LutForm;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.models.fn.SubAuditOrganization;
import com.nbb.storage.StorageService;

@RestController
@RequestMapping("/api/word")
public class WordController {
	
	@Autowired
    private UserDao dao;
	
	@Autowired
	StorageService storageService;
	
	@SuppressWarnings("unused")
	@RequestMapping(value="/export/{type}/{mid}/{id}",method=RequestMethod.GET)
	public void checklicense(@PathVariable String type, @PathVariable long id,@PathVariable long mid,HttpServletRequest req,HttpServletResponse response) throws JSONException, DocumentException, Exception {
		JsonObject obj= new JsonObject();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if(type.equalsIgnoreCase("report")){
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				System.out.println("real path"+realpath);
				
				ClassPathResource wordFile = new ClassPathResource("static/files/plannig_FA_zagvar.docx");
				File file=wordFile.getFile();
			
				boolean mergedOutput = false;
				File excelpath = new File(realpath+File.separator+main.getExcelurlplan());
		    	FileInputStream efis = new FileInputStream(excelpath);
		    	Workbook workbook = WorkbookFactory.create(efis); 
				
				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
				//    							System.getProperty("user.dir") + "/template.docx"));

				List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();
				Sheet sht=workbook.getSheet("ЧХ");
				// Instance 1
				Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();


				map.put(new DataFieldName("C4"), sht.getRow(3).getCell(2).getStringCellValue());
				map.put(new DataFieldName("G4"), sht.getRow(3).getCell(6).getStringCellValue());
				map.put(new DataFieldName("J4"), sht.getRow(3).getCell(9).getStringCellValue());
				map.put(new DataFieldName("I4"), sht.getRow(3).getCell(8).getStringCellValue());
				
				map.put(new DataFieldName("B5"), sht.getRow(4).getCell(1).getStringCellValue());					
				map.put(new DataFieldName("C5"), sht.getRow(4).getCell(2).getStringCellValue());
				map.put(new DataFieldName("H5"), String.valueOf((int) sht.getRow(4).getCell(7).getNumericCellValue()));
				
				map.put(new DataFieldName("B6"), sht.getRow(5).getCell(1).getStringCellValue());
				map.put(new DataFieldName("C6"), sht.getRow(5).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C7"), sht.getRow(6).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C8"), String.valueOf((int) sht.getRow(7).getCell(2).getNumericCellValue()));
				map.put(new DataFieldName("C9"), sht.getRow(8).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C15"), sht.getRow(14).getCell(2).getStringCellValue());
				map.put(new DataFieldName("D15"), sht.getRow(14).getCell(3).getStringCellValue());
				map.put(new DataFieldName("C16"), sht.getRow(15).getCell(2).getStringCellValue());
				
			    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			    
			    if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(18).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(19).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(20).getCell(2) != null && sht.getRow(20).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(20).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(20).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(20).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C21"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C21"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(28).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(29).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(30).getCell(2) != null && sht.getRow(30).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(30).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(30).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(30).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C31"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C31"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(3) != null && sht.getRow(36).getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(3).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(3).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(3));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("D37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("D37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(32).getCell(2) != null && sht.getRow(32).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(32).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(32).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(32).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C33"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C33"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(33).getCell(2) != null && sht.getRow(33).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(33).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(33).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(33).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C34"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C34"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(35).getCell(2) != null && sht.getRow(35).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(35).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(35).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(35).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C36"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C36"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(4) != null && sht.getRow(36).getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(4).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(4).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(4));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("E37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("E37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(2) != null && sht.getRow(36).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(37).getCell(2) != null && sht.getRow(37).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(37).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(37).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(37).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C38"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C38"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(38).getCell(2) != null && sht.getRow(38).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(38).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(38).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(38).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C39"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C39"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(39).getCell(2) != null && sht.getRow(39).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(39).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(39).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(39).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C40"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C40"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(44).getCell(2) != null && sht.getRow(44).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(44).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(44).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(44).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C45"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C45"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(45).getCell(2) != null && sht.getRow(45).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(45).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(45).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(45).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C46"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C46"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(46).getCell(2) != null && sht.getRow(46).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(46).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(46).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(46).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C47"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C47"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(47).getCell(2) != null && sht.getRow(47).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(47).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(47).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(47).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C48"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C48"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(48).getCell(2) != null && sht.getRow(48).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(48).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(48).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(48).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C49"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C49"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(49).getCell(2) != null && sht.getRow(49).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(49).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(49).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(49).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C50"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C50"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    
			    for(int i=50;i<70;i++){
			    	if(sht.getRow(i)!=null){
			    	 if (sht.getRow(i).getCell(2) != null && sht.getRow(i).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
		                    String formula = sht.getRow(i).getCell(2).getCellFormula(); 
		                    System.out.println(formula+i);
		                    if (formula != null) { 
		                    	sht.getRow(i).getCell(2).setCellFormula(formula);
		                    	CellValue cellValue = evaluator.evaluate(sht.getRow(i).getCell(2));
		                    	 
		                    	 int y=i+1;
		                    	 switch (cellValue.getCellTypeEnum()) {
		                    	    case STRING:
		                    	    	map.put(new DataFieldName("C"+y), cellValue.getStringValue());
		                    	        break;
		                    	    case NUMERIC:
		                    	    	map.put(new DataFieldName("C"+y), String.valueOf(cellValue.getNumberValue()));
		                    	        break;
		                    	}
		                                              
		                    } 
		                }
			    	}
			    }
				
				data.add(map);		

				String xname=main.getOrgname().trim()+"-"+type;
				xname = URLEncoder.encode(xname,"UTF-8"); 

				if (mergedOutput) {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
					WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, true);
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						response.setContentType("application/ms-word; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
						output.save(outputStream);  
						outputStream.close();
					}
					catch (Exception e) {
						System.out.println("ishe orov");
					}
				}
				else {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

					int i = 1;
					for (Map<DataFieldName, String> thismap : data) {
						org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, thismap, true);
						try (ServletOutputStream outputStream = response.getOutputStream()) {
							response.setContentType("application/ms-word; charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
							wordMLPackage.save(outputStream);  
							outputStream.close();
						}
						catch (Exception e) {
							System.out.println("ishe orov");
						}
						i++;
					}			
				}
			}
			else if(type.equalsIgnoreCase("АГ")){
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				System.out.println("real path"+realpath);
				
				ClassPathResource wordFile = new ClassPathResource("static/files/opinion-1.docx");
				File file=wordFile.getFile();
				
				File excelpath = new File(realpath+File.separator+main.getExcelurlprocess());
		    	FileInputStream efis = new FileInputStream(excelpath);
		    	Workbook workbook = WorkbookFactory.create(efis); 
		    	 FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		    	Sheet sht=workbook.getSheet("ЧХ");
			
				boolean mergedOutput = false;
				
				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
				//    							System.getProperty("user.dir") + "/template.docx"));

				List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

				// Instance 1
				Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();

				map.put(new DataFieldName("C4"), sht.getRow(3).getCell(2).getStringCellValue());
				map.put(new DataFieldName("G4"), sht.getRow(3).getCell(6).getStringCellValue());
				map.put(new DataFieldName("J4"), sht.getRow(3).getCell(9).getStringCellValue());
				map.put(new DataFieldName("I4"), sht.getRow(3).getCell(8).getStringCellValue());
				
				map.put(new DataFieldName("B5"), sht.getRow(4).getCell(1).getStringCellValue());					
				map.put(new DataFieldName("C5"), sht.getRow(4).getCell(2).getStringCellValue());
				map.put(new DataFieldName("H5"), String.valueOf((int) sht.getRow(4).getCell(7).getNumericCellValue()));
				
				map.put(new DataFieldName("B6"), sht.getRow(5).getCell(1).getStringCellValue());
				map.put(new DataFieldName("C6"), sht.getRow(5).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C7"), sht.getRow(6).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C8"), String.valueOf((int) sht.getRow(7).getCell(2).getNumericCellValue()));
				map.put(new DataFieldName("C9"), sht.getRow(8).getCell(2).getStringCellValue());
				if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(18).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(19).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(20).getCell(2) != null && sht.getRow(20).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(20).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(20).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(20).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C21"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C21"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(28).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(29).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(30).getCell(2) != null && sht.getRow(30).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(30).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(30).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(30).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C31"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C31"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(3) != null && sht.getRow(36).getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(3).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(3).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(3));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("D37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("D37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(32).getCell(2) != null && sht.getRow(32).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(32).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(32).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(32).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C33"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C33"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(33).getCell(2) != null && sht.getRow(33).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(33).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(33).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(33).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C34"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C34"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(35).getCell(2) != null && sht.getRow(35).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(35).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(35).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(35).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C36"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C36"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(4) != null && sht.getRow(36).getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(4).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(4).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(4));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("E37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("E37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(2) != null && sht.getRow(36).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(37).getCell(2) != null && sht.getRow(37).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(37).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(37).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(37).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C38"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C38"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(38).getCell(2) != null && sht.getRow(38).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(38).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(38).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(38).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C39"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C39"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(39).getCell(2) != null && sht.getRow(39).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(39).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(39).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(39).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C40"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C40"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(44).getCell(2) != null && sht.getRow(44).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(44).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(44).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(44).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C45"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C45"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(45).getCell(2) != null && sht.getRow(45).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(45).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(45).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(45).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C46"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C46"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(46).getCell(2) != null && sht.getRow(46).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(46).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(46).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(46).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C47"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C47"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(47).getCell(2) != null && sht.getRow(47).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(47).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(47).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(47).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C48"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C48"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(48).getCell(2) != null && sht.getRow(48).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(48).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(48).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(48).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C49"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C49"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(49).getCell(2) != null && sht.getRow(49).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(49).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(49).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(49).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C50"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C50"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    
			    for(int i=50;i<70;i++){
			    	if(sht.getRow(i)!=null){
			    		if (sht.getRow(i).getCell(2) != null && sht.getRow(i).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
		                    String formula = sht.getRow(i).getCell(2).getCellFormula(); 
		                    System.out.println(formula+i);
		                    if (formula != null) { 
		                    	sht.getRow(i).getCell(2).setCellFormula(formula);
		                    	CellValue cellValue = evaluator.evaluate(sht.getRow(i).getCell(2));
		                    	 int y=i+1;
		                    	 switch (cellValue.getCellTypeEnum()) {
		                    	    case STRING:
		                    	    	map.put(new DataFieldName("C"+y), cellValue.getStringValue());
		                    	        break;
		                    	    case NUMERIC:
		                    	    	map.put(new DataFieldName("C"+y), String.valueOf(cellValue.getNumberValue()));
		                    	        break;
		                    	}
		                                              
		                    } 
		                }
			    	}
			    	 
			    }
				data.add(map);		

				String xname=main.getOrgname().trim()+"-"+type;
				xname = URLEncoder.encode(xname,"UTF-8"); 

				if (mergedOutput) {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
					WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, true);
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						response.setContentType("application/ms-word; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
						output.save(outputStream);  
						outputStream.close();
					}
					catch (Exception e) {
						System.out.println("ishe orov");
					}
				}
				else {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

					int i = 1;
					for (Map<DataFieldName, String> thismap : data) {
						org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, thismap, true);
						try (ServletOutputStream outputStream = response.getOutputStream()) {
							response.setContentType("application/ms-word; charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
							wordMLPackage.save(outputStream);  
							outputStream.close();
						}
						catch (Exception e) {
							System.out.println("ishe orov");
						}
						i++;
					}			
				}
			}
			else if(type.equalsIgnoreCase("АГ-Х")){
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				System.out.println("real path"+realpath);
				
				ClassPathResource wordFile = new ClassPathResource("static/files/opinion-2.docx");
				File file=wordFile.getFile();
			
				File excelpath = new File(realpath+File.separator+main.getExcelurlprocess());
		    	FileInputStream efis = new FileInputStream(excelpath);
		    	Workbook workbook = WorkbookFactory.create(efis); 
		    	 FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		    	Sheet sht=workbook.getSheet("ЧХ");
			
				boolean mergedOutput = false;
				
				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
				//    							System.getProperty("user.dir") + "/template.docx"));

				List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

				// Instance 1
				Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();

				map.put(new DataFieldName("C4"), sht.getRow(3).getCell(2).getStringCellValue());
				map.put(new DataFieldName("G4"), sht.getRow(3).getCell(6).getStringCellValue());
				map.put(new DataFieldName("J4"), sht.getRow(3).getCell(9).getStringCellValue());
				map.put(new DataFieldName("I4"), sht.getRow(3).getCell(8).getStringCellValue());
				
				map.put(new DataFieldName("B5"), sht.getRow(4).getCell(1).getStringCellValue());					
				map.put(new DataFieldName("C5"), sht.getRow(4).getCell(2).getStringCellValue());
				map.put(new DataFieldName("H5"), String.valueOf((int) sht.getRow(4).getCell(7).getNumericCellValue()));
				
				map.put(new DataFieldName("B6"), sht.getRow(5).getCell(1).getStringCellValue());
				map.put(new DataFieldName("C6"), sht.getRow(5).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C7"), sht.getRow(6).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C8"), String.valueOf((int) sht.getRow(7).getCell(2).getNumericCellValue()));
				map.put(new DataFieldName("C9"), sht.getRow(8).getCell(2).getStringCellValue());
				if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(18).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(19).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(20).getCell(2) != null && sht.getRow(20).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(20).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(20).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(20).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C21"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C21"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(28).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(29).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(30).getCell(2) != null && sht.getRow(30).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(30).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(30).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(30).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C31"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C31"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(3) != null && sht.getRow(36).getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(3).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(3).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(3));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("D37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("D37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(32).getCell(2) != null && sht.getRow(32).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(32).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(32).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(32).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C33"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C33"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(33).getCell(2) != null && sht.getRow(33).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(33).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(33).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(33).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C34"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C34"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(35).getCell(2) != null && sht.getRow(35).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(35).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(35).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(35).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C36"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C36"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(4) != null && sht.getRow(36).getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(4).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(4).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(4));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("E37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("E37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(2) != null && sht.getRow(36).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(37).getCell(2) != null && sht.getRow(37).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(37).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(37).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(37).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C38"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C38"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(38).getCell(2) != null && sht.getRow(38).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(38).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(38).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(38).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C39"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C39"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(39).getCell(2) != null && sht.getRow(39).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(39).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(39).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(39).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C40"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C40"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(44).getCell(2) != null && sht.getRow(44).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(44).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(44).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(44).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C45"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C45"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(45).getCell(2) != null && sht.getRow(45).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(45).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(45).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(45).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C46"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C46"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(46).getCell(2) != null && sht.getRow(46).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(46).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(46).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(46).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C47"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C47"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(47).getCell(2) != null && sht.getRow(47).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(47).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(47).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(47).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C48"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C48"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(48).getCell(2) != null && sht.getRow(48).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(48).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(48).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(48).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C49"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C49"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(49).getCell(2) != null && sht.getRow(49).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(49).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(49).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(49).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C50"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C50"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    
			    for(int i=50;i<70;i++){
			    	if(sht.getRow(i)!=null){
			    	 if (sht.getRow(i).getCell(2) != null && sht.getRow(i).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
		                    String formula = sht.getRow(i).getCell(2).getCellFormula(); 
		                    System.out.println(formula+i);
		                    if (formula != null) { 
		                    	sht.getRow(i).getCell(2).setCellFormula(formula);
		                    	CellValue cellValue = evaluator.evaluate(sht.getRow(i).getCell(2));
		                    	 
		                    	 int y=i+1;
		                    	 switch (cellValue.getCellTypeEnum()) {
		                    	    case STRING:
		                    	    	map.put(new DataFieldName("C"+y), cellValue.getStringValue());
		                    	        break;
		                    	    case NUMERIC:
		                    	    	map.put(new DataFieldName("C"+y), String.valueOf(cellValue.getNumberValue()));
		                    	        break;
		                    	}
		                                              
		                    } 
		                }
			    	}
			    }
				data.add(map);		

				String xname=main.getOrgname().trim()+"-"+type;
				xname = URLEncoder.encode(xname,"UTF-8"); 

				if (mergedOutput) {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
					WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, true);
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						response.setContentType("application/ms-word; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
						output.save(outputStream);  
						outputStream.close();
					}
					catch (Exception e) {
						System.out.println("ishe orov");
					}
				}
				else {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

					int i = 1;
					for (Map<DataFieldName, String> thismap : data) {
						org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, thismap, true);
						try (ServletOutputStream outputStream = response.getOutputStream()) {
							response.setContentType("application/ms-word; charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
							wordMLPackage.save(outputStream);  
							outputStream.close();
						}
						catch (Exception e) {
							System.out.println("ishe orov");
						}
						i++;
					}			
				}
			}
			else if(type.equalsIgnoreCase("АГ-С")){
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				System.out.println("real path"+realpath);
				
				ClassPathResource wordFile = new ClassPathResource("static/files/opinion-3.docx");
				File file=wordFile.getFile();
			
				File excelpath = new File(realpath+File.separator+main.getExcelurlprocess());
		    	FileInputStream efis = new FileInputStream(excelpath);
		    	Workbook workbook = WorkbookFactory.create(efis); 
		    	 FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		    	Sheet sht=workbook.getSheet("ЧХ");
			
				boolean mergedOutput = false;
				
				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
				//    							System.getProperty("user.dir") + "/template.docx"));

				List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

				// Instance 1
				Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();

				map.put(new DataFieldName("C4"), sht.getRow(3).getCell(2).getStringCellValue());
				map.put(new DataFieldName("G4"), sht.getRow(3).getCell(6).getStringCellValue());
				map.put(new DataFieldName("J4"), sht.getRow(3).getCell(9).getStringCellValue());
				map.put(new DataFieldName("I4"), sht.getRow(3).getCell(8).getStringCellValue());
				
				map.put(new DataFieldName("B5"), sht.getRow(4).getCell(1).getStringCellValue());					
				map.put(new DataFieldName("C5"), sht.getRow(4).getCell(2).getStringCellValue());
				map.put(new DataFieldName("H5"), String.valueOf((int) sht.getRow(4).getCell(7).getNumericCellValue()));
				
				map.put(new DataFieldName("B6"), sht.getRow(5).getCell(1).getStringCellValue());
				map.put(new DataFieldName("C6"), sht.getRow(5).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C7"), sht.getRow(6).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C8"), String.valueOf((int) sht.getRow(7).getCell(2).getNumericCellValue()));
				map.put(new DataFieldName("C9"), sht.getRow(8).getCell(2).getStringCellValue());
				if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(18).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(19).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(20).getCell(2) != null && sht.getRow(20).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(20).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(20).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(20).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C21"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C21"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(28).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(29).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(30).getCell(2) != null && sht.getRow(30).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(30).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(30).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(30).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C31"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C31"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(3) != null && sht.getRow(36).getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(3).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(3).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(3));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("D37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("D37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(32).getCell(2) != null && sht.getRow(32).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(32).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(32).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(32).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C33"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C33"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(33).getCell(2) != null && sht.getRow(33).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(33).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(33).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(33).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C34"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C34"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(35).getCell(2) != null && sht.getRow(35).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(35).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(35).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(35).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C36"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C36"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(4) != null && sht.getRow(36).getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(4).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(4).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(4));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("E37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("E37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(2) != null && sht.getRow(36).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(37).getCell(2) != null && sht.getRow(37).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(37).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(37).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(37).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C38"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C38"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(38).getCell(2) != null && sht.getRow(38).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(38).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(38).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(38).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C39"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C39"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(39).getCell(2) != null && sht.getRow(39).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(39).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(39).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(39).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C40"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C40"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(44).getCell(2) != null && sht.getRow(44).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(44).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(44).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(44).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C45"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C45"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(45).getCell(2) != null && sht.getRow(45).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(45).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(45).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(45).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C46"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C46"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(46).getCell(2) != null && sht.getRow(46).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(46).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(46).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(46).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C47"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C47"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(47).getCell(2) != null && sht.getRow(47).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(47).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(47).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(47).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C48"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C48"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(48).getCell(2) != null && sht.getRow(48).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(48).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(48).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(48).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C49"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C49"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(49).getCell(2) != null && sht.getRow(49).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(49).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(49).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(49).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C50"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C50"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    
			    for(int i=50;i<70;i++){
			    	if(sht.getRow(i)!=null){
			    	 if (sht.getRow(i).getCell(2) != null && sht.getRow(i).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
		                    String formula = sht.getRow(i).getCell(2).getCellFormula(); 
		                    System.out.println(formula+i);
		                    if (formula != null) { 
		                    	sht.getRow(i).getCell(2).setCellFormula(formula);
		                    	CellValue cellValue = evaluator.evaluate(sht.getRow(i).getCell(2));
		                    	 int y=i+1;
		                    	 switch (cellValue.getCellTypeEnum()) {
		                    	    case STRING:
		                    	    	map.put(new DataFieldName("C"+y), cellValue.getStringValue());
		                    	        break;
		                    	    case NUMERIC:
		                    	    	map.put(new DataFieldName("C"+y), String.valueOf(cellValue.getNumberValue()));
		                    	        break;
		                    	}
		                                              
		                    } 
		                }
			    	}
			    }
				data.add(map);		

				String xname=main.getOrgname().trim()+"-"+type;
				xname = URLEncoder.encode(xname,"UTF-8"); 

				if (mergedOutput) {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
					WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, true);
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						response.setContentType("application/ms-word; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
						output.save(outputStream);  
						outputStream.close();
					}
					catch (Exception e) {
						System.out.println("ishe orov");
					}
				}
				else {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

					int i = 1;
					for (Map<DataFieldName, String> thismap : data) {
						org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, thismap, true);
						try (ServletOutputStream outputStream = response.getOutputStream()) {
							response.setContentType("application/ms-word; charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
							wordMLPackage.save(outputStream);  
							outputStream.close();
						}
						catch (Exception e) {
							System.out.println("ishe orov");
						}
						i++;
					}			
				}
			}
			else if(type.equalsIgnoreCase("АГ-Т")){
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				System.out.println("real path"+realpath);
				
				ClassPathResource wordFile = new ClassPathResource("static/files/opinion-4.docx");
				File file=wordFile.getFile();
			
				File excelpath = new File(realpath+File.separator+main.getExcelurlprocess());
		    	FileInputStream efis = new FileInputStream(excelpath);
		    	Workbook workbook = WorkbookFactory.create(efis); 
		    	 FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		    	Sheet sht=workbook.getSheet("ЧХ");
			
				boolean mergedOutput = false;
				
				WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
				//    							System.getProperty("user.dir") + "/template.docx"));

				List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

				// Instance 1
				Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();

				map.put(new DataFieldName("C4"), sht.getRow(3).getCell(2).getStringCellValue());
				map.put(new DataFieldName("G4"), sht.getRow(3).getCell(6).getStringCellValue());
				map.put(new DataFieldName("J4"), sht.getRow(3).getCell(9).getStringCellValue());
				map.put(new DataFieldName("I4"), sht.getRow(3).getCell(8).getStringCellValue());
				
				map.put(new DataFieldName("B5"), sht.getRow(4).getCell(1).getStringCellValue());					
				map.put(new DataFieldName("C5"), sht.getRow(4).getCell(2).getStringCellValue());
				map.put(new DataFieldName("H5"), String.valueOf((int) sht.getRow(4).getCell(7).getNumericCellValue()));
				
				map.put(new DataFieldName("B6"), sht.getRow(5).getCell(1).getStringCellValue());
				map.put(new DataFieldName("C6"), sht.getRow(5).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C7"), sht.getRow(6).getCell(2).getStringCellValue());
				map.put(new DataFieldName("C8"), String.valueOf((int) sht.getRow(7).getCell(2).getNumericCellValue()));
				map.put(new DataFieldName("C9"), sht.getRow(8).getCell(2).getStringCellValue());
				if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(18).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(19).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(20).getCell(2) != null && sht.getRow(20).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(20).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(20).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(20).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C21"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C21"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(28).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(29).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(30).getCell(2) != null && sht.getRow(30).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(30).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(30).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(30).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C31"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C31"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(3) != null && sht.getRow(36).getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(3).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(3).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(3));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("D37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("D37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(32).getCell(2) != null && sht.getRow(32).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(32).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(32).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(32).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C33"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C33"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(33).getCell(2) != null && sht.getRow(33).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(33).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(33).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(33).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C34"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C34"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(35).getCell(2) != null && sht.getRow(35).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(35).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(35).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(35).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C36"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C36"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(4) != null && sht.getRow(36).getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(4).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(4).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(4));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("E37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("E37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(36).getCell(2) != null && sht.getRow(36).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(36).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(36).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C37"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C37"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(37).getCell(2) != null && sht.getRow(37).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(37).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(37).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(37).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C38"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C38"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                } 
			    if (sht.getRow(38).getCell(2) != null && sht.getRow(38).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(38).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(38).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(38).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C39"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C39"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(39).getCell(2) != null && sht.getRow(39).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(39).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(39).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(39).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C40"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C40"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(44).getCell(2) != null && sht.getRow(44).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(44).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(44).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(44).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C45"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C45"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(45).getCell(2) != null && sht.getRow(45).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(45).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(45).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(45).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C46"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C46"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(46).getCell(2) != null && sht.getRow(46).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(46).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(46).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(46).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C47"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C47"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(47).getCell(2) != null && sht.getRow(47).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(47).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(47).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(47).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C48"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C48"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(48).getCell(2) != null && sht.getRow(48).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(48).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(48).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(48).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C49"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C49"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    if (sht.getRow(49).getCell(2) != null && sht.getRow(49).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
                    String formula = sht.getRow(49).getCell(2).getCellFormula(); 
                    if (formula != null) { 
                    	sht.getRow(49).getCell(2).setCellFormula(formula);
                    	CellValue cellValue = evaluator.evaluate(sht.getRow(49).getCell(2));
                    	 
                    	switch (cellValue.getCellTypeEnum()) {
                    	    case STRING:
                    	    	map.put(new DataFieldName("C50"), cellValue.getStringValue());
                    	        break;
                    	    case NUMERIC:
                    	    	map.put(new DataFieldName("C50"), String.valueOf(cellValue.getNumberValue()));
                    	        break;
                    	}
                                              
                    } 
                }
			    
			    for(int i=50;i<70;i++){
			    	if(sht.getRow(i)!=null){
			    	 if (sht.getRow(i).getCell(2) != null && sht.getRow(i).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
		                    String formula = sht.getRow(i).getCell(2).getCellFormula(); 
		                    System.out.println(formula+i);
		                    if (formula != null) { 
		                    	sht.getRow(i).getCell(2).setCellFormula(formula);
		                    	CellValue cellValue = evaluator.evaluate(sht.getRow(i).getCell(2));
		                    	 
		                    	 int y=i+1;
		                    	 switch (cellValue.getCellTypeEnum()) {
		                    	    case STRING:
		                    	    	map.put(new DataFieldName("C"+y), cellValue.getStringValue());
		                    	        break;
		                    	    case NUMERIC:
		                    	    	map.put(new DataFieldName("C"+y), String.valueOf(cellValue.getNumberValue()));
		                    	        break;
		                    	}
		                                              
		                    } 
		                }
			    	}
			    }

				data.add(map);		

				String xname=main.getOrgname().trim()+"-"+type;
				xname = URLEncoder.encode(xname,"UTF-8"); 

				if (mergedOutput) {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
					WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, true);
					try (ServletOutputStream outputStream = response.getOutputStream()) {
						response.setContentType("application/ms-word; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
						output.save(outputStream);  
						outputStream.close();
					}
					catch (Exception e) {
						System.out.println("ishe orov");
					}
				}
				else {
					org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

					int i = 1;
					for (Map<DataFieldName, String> thismap : data) {
						org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, thismap, true);
						try (ServletOutputStream outputStream = response.getOutputStream()) {
							response.setContentType("application/ms-word; charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
							wordMLPackage.save(outputStream);  
							outputStream.close();
						}
						catch (Exception e) {
							System.out.println("ishe orov");
						}
						i++;
					}			
				}
			}
			else if(type.equalsIgnoreCase("СТХ")){
				FileInputStream fis = null;
				MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+mid+"'", "current");
				Path currentRelativePath = Paths.get("");
				String realpath = currentRelativePath.toAbsolutePath().toString();
				System.out.println("real path"+realpath);
				
				ClassPathResource wordFile = new ClassPathResource("static/files/ADT.docx");
				File file=wordFile.getFile();
				
				
				File excelpath = new File(realpath+File.separator+main.getExcelurlprocess());
		    	FileInputStream efis = new FileInputStream(excelpath);
		    	Workbook workbook = WorkbookFactory.create(efis); 
		    	
		    	Sheet sht=workbook.getSheet("ЧХ");
		    	if(sht!=null){
		    		boolean mergedOutput = false;
					
					WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file);
					//    							System.getProperty("user.dir") + "/template.docx"));

					List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();

					// Instance 1
					Map<DataFieldName, String> map = new HashMap<DataFieldName, String>();

					
					map.put(new DataFieldName("C4"), sht.getRow(3).getCell(2).getStringCellValue());
					map.put(new DataFieldName("G4"), sht.getRow(3).getCell(6).getStringCellValue());
					map.put(new DataFieldName("J4"), sht.getRow(3).getCell(9).getStringCellValue());
					map.put(new DataFieldName("I4"), sht.getRow(3).getCell(8).getStringCellValue());
					
					map.put(new DataFieldName("B5"), sht.getRow(4).getCell(1).getStringCellValue());					
					map.put(new DataFieldName("C5"), sht.getRow(4).getCell(2).getStringCellValue());
					map.put(new DataFieldName("H5"), String.valueOf((int) sht.getRow(4).getCell(7).getNumericCellValue()));
					
					map.put(new DataFieldName("B6"), sht.getRow(5).getCell(1).getStringCellValue());
					map.put(new DataFieldName("C6"), sht.getRow(5).getCell(2).getStringCellValue());
					map.put(new DataFieldName("C7"), sht.getRow(6).getCell(2).getStringCellValue());
					map.put(new DataFieldName("C8"), String.valueOf((int) sht.getRow(7).getCell(2).getNumericCellValue()));
					map.put(new DataFieldName("C9"), sht.getRow(8).getCell(2).getStringCellValue());
					//map.put(new DataFieldName("C15"), sht.getRow(14).getCell(2).getStringCellValue());
					//map.put(new DataFieldName("C16"), "16");
					//map.put(new DataFieldName("C17"), "17");
					//map.put(new DataFieldName("C19"), "19");
					//map.put(new DataFieldName("C20"), "20");
					map.put(new DataFieldName("C26"), "26");
					map.put(new DataFieldName("C27"), "27");
					map.put(new DataFieldName("C28"), "28");
					map.put(new DataFieldName("C29"), "29");
					
					FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
				   if (sht.getRow(14).getCell(2) != null && sht.getRow(14).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(14).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(14).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(14).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C15"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C15"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                } 
				    if (sht.getRow(15).getCell(2) != null && sht.getRow(15).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(15).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(15).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(15).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C16"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C16"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(16).getCell(2) != null && sht.getRow(16).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(16).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(16).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(16).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C17"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C17"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(18).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				     if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(19).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				      if (sht.getRow(25).getCell(2) != null && sht.getRow(25).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(25).getCell(2).getCellFormula(); 
	                    System.out.println(formula);
	                    if (formula != null) { 
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(25).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C26"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C26"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(26).getCell(2) != null && sht.getRow(26).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(26).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(26).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C27"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C27"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(27).getCell(2) != null && sht.getRow(27).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(27).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(27).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C28"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C28"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    
				    if (sht.getRow(18).getCell(2) != null && sht.getRow(18).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(18).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(18).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(18).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C19"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C19"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(19).getCell(2) != null && sht.getRow(19).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(19).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(19).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(19).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C20"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C20"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(20).getCell(2) != null && sht.getRow(20).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(20).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(20).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(20).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C21"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C21"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(28).getCell(2) != null && sht.getRow(28).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(28).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(28).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(28).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C29"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C29"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(29).getCell(2) != null && sht.getRow(29).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(29).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(29).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(29).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C30"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C30"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(30).getCell(2) != null && sht.getRow(30).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(30).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(30).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(30).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C31"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C31"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(36).getCell(3) != null && sht.getRow(36).getCell(3).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(36).getCell(3).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(36).getCell(3).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(3));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("D37"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("D37"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                } 
				    if (sht.getRow(32).getCell(2) != null && sht.getRow(32).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(32).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(32).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(32).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C33"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C33"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(33).getCell(2) != null && sht.getRow(33).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(33).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(33).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(33).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C34"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C34"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(35).getCell(2) != null && sht.getRow(35).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(35).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(35).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(35).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C36"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C36"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(36).getCell(4) != null && sht.getRow(36).getCell(4).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(36).getCell(4).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(36).getCell(4).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(4));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("E37"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("E37"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(36).getCell(2) != null && sht.getRow(36).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(36).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(36).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(36).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C37"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C37"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                } 
				    if (sht.getRow(37).getCell(2) != null && sht.getRow(37).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(37).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(37).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(37).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C38"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C38"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                } 
				    if (sht.getRow(38).getCell(2) != null && sht.getRow(38).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(38).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(38).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(38).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C39"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C39"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(39).getCell(2) != null && sht.getRow(39).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(39).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(39).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(39).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C40"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C40"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(44).getCell(2) != null && sht.getRow(44).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(44).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(44).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(44).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C45"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C45"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(45).getCell(2) != null && sht.getRow(45).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(45).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(45).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(45).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C46"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C46"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(46).getCell(2) != null && sht.getRow(46).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(46).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(46).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(46).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C47"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C47"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(47).getCell(2) != null && sht.getRow(47).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(47).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(47).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(47).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C48"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C48"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(48).getCell(2) != null && sht.getRow(48).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(48).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(48).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(48).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C49"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C49"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    if (sht.getRow(49).getCell(2) != null && sht.getRow(49).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
	                    String formula = sht.getRow(49).getCell(2).getCellFormula(); 
	                    if (formula != null) { 
	                    	sht.getRow(49).getCell(2).setCellFormula(formula);
	                    	CellValue cellValue = evaluator.evaluate(sht.getRow(49).getCell(2));
	                    	 
	                    	switch (cellValue.getCellTypeEnum()) {
	                    	    case STRING:
	                    	    	map.put(new DataFieldName("C50"), cellValue.getStringValue());
	                    	        break;
	                    	    case NUMERIC:
	                    	    	map.put(new DataFieldName("C50"), String.valueOf(cellValue.getNumberValue()));
	                    	        break;
	                    	}
	                                              
	                    } 
	                }
				    
				    for(int i=50;i<70;i++){
				    	if(sht.getRow(i)!=null){
				    	 if (sht.getRow(i).getCell(2) != null && sht.getRow(i).getCell(2).getCellTypeEnum()== CellType.FORMULA) { 
			                    String formula = sht.getRow(i).getCell(2).getCellFormula(); 
			                    System.out.println(formula+i);
			                    if (formula != null) { 
			                    	sht.getRow(i).getCell(2).setCellFormula(formula);
			                    	CellValue cellValue = evaluator.evaluate(sht.getRow(i).getCell(2));
			                    	 
			                    	 int y=i+1;
			                    	 switch (cellValue.getCellTypeEnum()) {
			                    	    case STRING:
			                    	    	map.put(new DataFieldName("C"+y), cellValue.getStringValue());
			                    	        break;
			                    	    case NUMERIC:
			                    	    	map.put(new DataFieldName("C"+y), String.valueOf(cellValue.getNumberValue()));
			                    	        break;
			                    	}
			                                              
			                    } 
			                }
				    	}
				    }
					
					data.add(map);		

					String xname=main.getOrgname().trim()+"-"+type;
					xname = URLEncoder.encode(xname,"UTF-8"); 

					if (mergedOutput) {
						org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);
						WordprocessingMLPackage output = org.docx4j.model.fields.merge.MailMerger.getConsolidatedResultCrude(wordMLPackage, data, true);
						try (ServletOutputStream outputStream = response.getOutputStream()) {
							response.setContentType("application/ms-word; charset=UTF-8");
							response.setCharacterEncoding("UTF-8");
							response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
							output.save(outputStream);  
							outputStream.close();
						}
						catch (Exception e) {
							System.out.println("ishe orov");
						}
					}
					else {
						org.docx4j.model.fields.merge.MailMerger.setMERGEFIELDInOutput(OutputField.KEEP_MERGEFIELD);

						int i = 1;
						for (Map<DataFieldName, String> thismap : data) {
							org.docx4j.model.fields.merge.MailMerger.performMerge(wordMLPackage, thismap, true);
							try (ServletOutputStream outputStream = response.getOutputStream()) {
								response.setContentType("application/ms-word; charset=UTF-8");
								response.setCharacterEncoding("UTF-8");
								response.setHeader("Content-Disposition","attachment; filename*=UTF-8''"+xname+".docx");   
								wordMLPackage.save(outputStream);  
								outputStream.close();
							}
							catch (Exception e) {
								System.out.println("ishe orov");
							}
							i++;
						}			
					}
		    	}				
			}
		}
	}

}
