package com.nbb.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;

import com.google.gson.JsonObject;
import com.nbb.dao.UserDao;
import com.nbb.models.FileUpload;
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
import com.nbb.models.fn.LnkAuditReport;
import com.nbb.models.fn.LutStaus;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.repository.JournalRepository;
import com.nbb.repository.LutStausRepository;
import com.nbb.storage.StorageService;


@Service("ExcelUploadService")
public class ExcelUploadService {

    @Autowired
    LutStausRepository excelRepository;

    @Autowired
    JournalRepository journalRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    private UserDao dao;

    // Retrieve file
    public LutStaus findByFilename(String filename) {
        return excelRepository.findByFilename(filename);
    }

    // Upload the file
    public JSONObject uploadFile(MultipartFile mfile, String path, String ayear, String orgcode, long orgtype, String downdir, long mid, int stepid) throws IOException, ParseException, InvalidFormatException, JSONException {

        String filename = mfile.getOriginalFilename();
        int index = filename.lastIndexOf('.');
        String lastOne = (filename.substring(index + 1));
        String newfilename = mfile.getOriginalFilename();
        int newindex = newfilename.lastIndexOf('.');
        String newlastOne = (newfilename.substring(newindex + 1));
        String newuuid = UUID.randomUUID().toString() + "." + newlastOne;

        storageService.store(mfile, String.valueOf(mid), newuuid);

        if (FilenameUtils.getExtension(mfile.getOriginalFilename()).equalsIgnoreCase("xlsx") || FilenameUtils.getExtension(mfile.getOriginalFilename()).equalsIgnoreCase("xls")) {
            File excelpath = new File("upload-dir" + File.separator + mid + File.separator + newuuid);
            FileInputStream fis = new FileInputStream(excelpath);
            Workbook workbook = WorkbookFactory.create(fis);

            FileInputStream zagwar = null;
            File files = null;
            Path currentRelativePath = Paths.get("");
            String realpath = currentRelativePath.toAbsolutePath().toString();

            MainAuditRegistration mn = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='" + mid + "'", "current");

            List<String> str = Arrays.asList("1.Info", "2.CT1A", "3.CT2A", "4.CT3A", "5.CT4A", "6.CTT1", "7.CTT2", "8.CTT3", "9.CTT4", "10.CTT5", "11.CTT6", "12.CTT7", "13.CTT8", "14.CTT9", "15.Journal", "16.Assets", "17.Inventory", "18.Payroll", "19.Budget", "20.TGT1", "21.TGT1A", "22.NT2", "23.TRIAL BALANCE", "24.ABWS", "25.CBWS");

            if (!str.contains(workbook.getSheetName(0))) {
                JSONObject robj = new JSONObject();
                robj.put("support", false);
                robj.put("excel", false);
                robj.put("file", false);
                return robj;
            }

            JSONObject arr = new JSONObject();
            JSONObject err = new JSONObject();
            List<FileUpload> fl;
            if (mn.getExcelurlplan() != null) {
                files = new File(realpath + File.separator + mn.getExcelurlplan());

                System.out.println("#####################" + realpath + File.separator + mn.getExcelurlplan());
                if (files.exists()) {
                    System.out.println("Тuluvlult zagwar copy");
                    zagwar = new FileInputStream(files);
                } else {
                    System.out.println("Тuluvlult tailan ustsan");
                    if (mn.getAutype() == 2) {
                        fl = (List<FileUpload>) dao.getHQLResult("from FileUpload t where t.aan=2 and t.payroll=" + mn.getOrgtype() + " order by t.id desc", "list");
                    } else {
                        fl = (List<FileUpload>) dao.getHQLResult("from FileUpload t where t.autype=1 and t.aan=" + mn.getOrgtype() + " order by t.id desc", "list");
                    }
                    if (fl.size() > 0) {
                        files = new File(realpath + fl.get(0).getFileurlAdmin());
                        if (files.exists()) {
                            zagwar = new FileInputStream(files);
                        } else {
                            JSONObject robj = new JSONObject();
                            robj.put("support", true);
                            robj.put("excel", false);
                            robj.put("error", arr);
                            robj.put("file", false);
                            return robj;
                        }
                    } else {
                        JSONObject robj = new JSONObject();
                        robj.put("support", true);
                        robj.put("excel", false);
                        robj.put("error", arr);
                        robj.put("file", false);
                        return robj;
                    }
                }
            } else {
                if (mn.getAutype() == 2) {
                    fl = (List<FileUpload>) dao.getHQLResult("from FileUpload t where t.aan=2 and t.payroll=" + mn.getOrgtype() + " order by t.id desc", "list");
                } else {
                    fl = (List<FileUpload>) dao.getHQLResult("from FileUpload t where t.autype=1 and t.aan=" + mn.getOrgtype() + " order by t.id desc", "list");
                }
                if (fl.size() > 0) {
                    files = new File(realpath + fl.get(0).getFileurlAdmin());
                    if (files.exists()) {
                        zagwar = new FileInputStream(files);
                    } else {
                        JSONObject robj = new JSONObject();
                        robj.put("support", true);
                        robj.put("excel", false);
                        robj.put("error", arr);
                        robj.put("file", false);
                        return robj;
                    }
                } else {
                    JSONObject robj = new JSONObject();
                    robj.put("support", true);
                    robj.put("excel", false);
                    robj.put("error", arr);
                    robj.put("file", false);
                    return robj;
                }
            }


            int count = 0;


            Workbook zbook = WorkbookFactory.create(zagwar);
            JSONArray errList = new JSONArray();
            JSONArray sheetList = new JSONArray();
            for (int i = 0; i < workbook.getNumberOfSheets() - 6; i++) {
                Sheet sht = zbook.getSheet(workbook.getSheetName(i));
                if (sht != null) {
                    JSONObject eobj = new JSONObject();
                    Row drow = workbook.getSheetAt(i).getRow(6);
                    Row zrow = sht.getRow(6);
                    if (workbook.getSheetName(i).equalsIgnoreCase("15.Journal") || workbook.getSheetName(i).equalsIgnoreCase("Journal")) {
                        drow = workbook.getSheetAt(i).getRow(3);
                        zrow = sht.getRow(3);
                    }
                    if (workbook.getSheetName(i).equalsIgnoreCase("16.Assets") || workbook.getSheetName(i).equalsIgnoreCase("Assets")
                            || workbook.getSheetName(i).equalsIgnoreCase("17.Inventory") || workbook.getSheetName(i).equalsIgnoreCase("19.Budget")) {
                        drow = workbook.getSheetAt(i).getRow(4);
                        zrow = sht.getRow(4);
                    }
                    if (workbook.getSheetName(i).equalsIgnoreCase("18.Payroll") || workbook.getSheetName(i).equalsIgnoreCase("Payroll")) {
                        drow = workbook.getSheetAt(i).getRow(1);
                        zrow = sht.getRow(1);
                    }
                    if (workbook.getSheetName(i).equalsIgnoreCase("12.CTT7") || workbook.getSheetName(i).equalsIgnoreCase("12.CTT7")) {
                        drow = workbook.getSheetAt(i).getRow(7);
                        zrow = sht.getRow(7);
                    }
                    if (drow != null) {
                        for (int y = 0; y < drow.getLastCellNum(); y++) {
                            Cell cl = drow.getCell(y);
                            if (zrow != null) {
                                Cell zcl = zrow.getCell(y);
                                if (cl != null && zcl != null) {
                                    JSONObject errObj = new JSONObject();
                                    if (workbook.getSheetName(i).equalsIgnoreCase("2.CT1A") || workbook.getSheetName(i).equalsIgnoreCase("CT1A") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("3.CT2A") || workbook.getSheetName(i).equalsIgnoreCase("CT2A") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("4.CT3A") || workbook.getSheetName(i).equalsIgnoreCase("CT3A") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("5.CT4A") || workbook.getSheetName(i).equalsIgnoreCase("CT4A") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("6.CTT1") || workbook.getSheetName(i).equalsIgnoreCase("7.CTT2") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("8.CTT3") || workbook.getSheetName(i).equalsIgnoreCase("9.CTT4") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("10.CTT5") || workbook.getSheetName(i).equalsIgnoreCase("11.CTT6") ||
                                            workbook.getSheetName(i).equalsIgnoreCase("15.Journal") || workbook.getSheetName(i).equalsIgnoreCase("Journal")) {
                                        if (!cl.getRichStringCellValue().getString().trim().equalsIgnoreCase(zcl.getRichStringCellValue().getString().trim())) {
                                            errObj.put("sheetname", cl.getSheet().getSheetName());
                                            errObj.put("bagana", cl.getRichStringCellValue().getString());
                                            errObj.put("bagana2", zcl.getRichStringCellValue().getString());
                                            errList.put(errObj);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        JSONObject errObj = new JSONObject();
                        errObj.put("sheetname", workbook.getSheetName(i));
                        sheetList.put(errObj);
                        fis.close();
                        if (excelpath.delete()) {
                            System.out.println("deleted");
                        } else {
                            System.out.println("failed to delete");
                        }
                    }
                } else {
                    JSONObject errObj = new JSONObject();
                    errObj.put("sheetname", workbook.getSheetName(i));
                    sheetList.put(errObj);
                }
            }


            JSONArray arr1 = new JSONArray();
            FormulaEvaluator evaluator = zbook.getCreationHelper().createFormulaEvaluator();

            FormulaEvaluator wevaluator = workbook.getCreationHelper().createFormulaEvaluator();


            if (sheetList.length() > 0) {
                err.put("additionalSheet", sheetList);
                err.put("excel", false);
                err.put("support", false);
                return err;
            } else {
                Sheet hch = zbook.getSheet("ЧХ");
                if (hch != null) {
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
                    cell41.setCellValue(mn.getDpos());
                    cell4.setCellValue(mn.getDirector());
                    cell5.setCellValue(mn.getManager());
                    cell6.setCellValue(mn.getOrgname());
                    cell7.setCellValue(mn.getAudityear());
                    cell8.setCellValue(mn.getGencode());
                    cell12.setCellValue(mn.getChpos());
                    cell13.setCellValue(mn.getChname());
                    cell14.setCellValue(mn.getApos());
                    cell15.setCellValue(mn.getAname());

                }
                here:
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    //FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    Sheet sheet = zbook.getSheet(workbook.getSheetAt(i).getSheetName().trim());
                    FormulaEvaluator evaluatorZbook = zbook.getCreationHelper().createFormulaEvaluator();
                    Sheet dataSheet = workbook.getSheetAt(i);
                    if (sheet != null) {
                        System.out.println("sheetname" + sheet.getSheetName());

                        if (sheet.getSheetName().equalsIgnoreCase("23.TRIAL BALANCE")) {
                            //	dao.PeaceCrud(null, "FinTrialBalance", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
                            List<FinTrialBalance> datas = new ArrayList<FinTrialBalance>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 5; k <= sheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinTrialBalance form = new FinTrialBalance();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());

                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    //		dao.inserBatch(datas,"23.TRIAL BALANCE",mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                        } else if (sheet.getSheetName().equalsIgnoreCase("25.CBWS")) {
                            //	dao.PeaceCrud(null, "FinCbw", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
                            List<FinCbw> datas = new ArrayList<FinCbw>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 6; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCbw form = new FinCbw();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    //dao.inserBatch(datas,"25.CBWS",mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    //dao.PeaceCrud(form, "FinCbw", "save", (long) 0, 0, 0, null);
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            //dao.inserBatch(datas,"25.CBWS",mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("24.ABWS")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_ABWS WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            //	dao.PeaceCrud(null, "FinAbw", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
                            List<FinAbw> datas = new ArrayList<FinAbw>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 5; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinAbw form = new FinAbw();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    //	dao.inserBatch(datas,"24.ABWS",mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            //	dao.inserBatch(datas,"24.ABWS",mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("21.TGT1A")) {
                            //dao.PeaceCrud(null, "FinTgt1a", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
                            List<FinTgt1a> datas = new ArrayList<FinTgt1a>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }

                            for (int k = 5; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);


                                try {
                                    FinTgt1a form = new FinTgt1a();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }

                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    //	dao.inserBatch(datas,"21.TGT1A",mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            //dao.inserBatch(datas,"21.TGT1A",mid);
                        }
                        else if (sheet.getSheetName().equalsIgnoreCase("16.Assets")) {
                            dao.PeaceCrud(null, "FinAsset", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinAsset> datas = new ArrayList<FinAsset>();
                            for (int k = 5; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                FinAsset form = new FinAsset();
                                form.setStepid(stepid);
                                System.out.println("rowNum"+k);
                                if (row.getCell(0)!=null){
                                    if (row.getCell(0).getCellType() == 1) {
                                        form.setData1(row.getCell(0).getStringCellValue());
                                    }
                                    else{
                                        form.setData1(NumberToTextConverter.toText(row.getCell(0).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(1)!=null){
                                    if (row.getCell(1).getCellType() == 1) {
                                        form.setData2(row.getCell(1).getStringCellValue());
                                    }
                                    else{
                                        form.setData2(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue()));
                                    }
                                }

                                if (row.getCell(2)!=null) {
                                    if (row.getCell(2).getCellType() == 1) {
                                        form.setData3(row.getCell(2).getStringCellValue());
                                    } else {
                                        form.setData3(NumberToTextConverter.toText(row.getCell(2).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(3)!=null) {
                                    if (row.getCell(3).getCellType() == 1) {
                                        form.setData4(row.getCell(3).getStringCellValue());
                                    } else {
                                        form.setData4(NumberToTextConverter.toText(row.getCell(3).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(4)!=null) {
                                    if (row.getCell(4).getCellType() == 1) {
                                        form.setData5(row.getCell(4).getStringCellValue());
                                    } else {
                                        form.setData5(NumberToTextConverter.toText(row.getCell(4).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(5)!=null) {
                                    if (row.getCell(5).getCellType() == 1) {
                                        form.setData6(row.getCell(5).getStringCellValue());
                                    } else {
                                        form.setData6(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(6)!=null) {
                                    if (row.getCell(6).getCellType() == 1) {
                                        form.setData7(row.getCell(6).getStringCellValue());
                                    } else {
                                        form.setData7(NumberToTextConverter.toText(row.getCell(6).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(7)!=null) {
                                    if (row.getCell(7).getCellType() == 1) {
                                        form.setData8(row.getCell(7).getStringCellValue());
                                    } else {
                                        form.setData8(NumberToTextConverter.toText(row.getCell(7).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(8)!=null) {
                                    if (row.getCell(8).getCellType() == 1) {
                                        form.setData9(row.getCell(8).getStringCellValue());
                                    } else {
                                        form.setData9(NumberToTextConverter.toText(row.getCell(8).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(9)!=null) {
                                    if (row.getCell(9).getCellType() == 1) {
                                        form.setData10(row.getCell(9).getStringCellValue());
                                    } else {
                                        form.setData10(NumberToTextConverter.toText(row.getCell(9).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(10)!=null) {
                                    if (row.getCell(10) != null && row.getCell(10).getCellType() == 1) {
                                        form.setData11(row.getCell(10).getStringCellValue());
                                    } else {
                                        form.setData11(NumberToTextConverter.toText(row.getCell(10).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(11)!=null) {
                                    if (row.getCell(11) != null && row.getCell(11).getCellType() == 1) {
                                        form.setData12(row.getCell(11).getStringCellValue());
                                    } else {
                                        form.setData12(NumberToTextConverter.toText(row.getCell(11).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(12)!=null) {
                                    if (row.getCell(12).getCellType() == 1) {
                                        form.setData13(row.getCell(12).getStringCellValue());
                                    } else {
                                        form.setData13(NumberToTextConverter.toText(row.getCell(12).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(13)!=null) {
                                    if (row.getCell(13).getCellType() == 1) {
                                        form.setData14(row.getCell(13).getStringCellValue());
                                    } else {
                                        form.setData14(NumberToTextConverter.toText(row.getCell(13).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(14)!=null) {
                                    if (row.getCell(14).getCellType() == 1) {
                                        form.setData15(row.getCell(14).getStringCellValue());
                                    } else {
                                        form.setData15(NumberToTextConverter.toText(row.getCell(14).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(15)!=null) {
                                    if (row.getCell(15).getCellType() == 1) {
                                        form.setData16(row.getCell(15).getStringCellValue());
                                    } else {
                                        form.setData16(NumberToTextConverter.toText(row.getCell(15).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(16)!=null) {
                                    if (row.getCell(16).getCellType() == 1) {
                                        form.setData17(row.getCell(16).getStringCellValue());
                                    } else {
                                        form.setData17(NumberToTextConverter.toText(row.getCell(16).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(17)!=null) {
                                    if (row.getCell(17).getCellType() == 1) {
                                        form.setData18(row.getCell(17).getStringCellValue());
                                    } else {
                                        form.setData18(NumberToTextConverter.toText(row.getCell(17).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(18)!=null) {
                                    if (row.getCell(18).getCellType() == 1) {
                                        form.setData19(row.getCell(18).getStringCellValue());
                                    } else {
                                        form.setData19(NumberToTextConverter.toText(row.getCell(18).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(19)!=null) {
                                    if (row.getCell(19).getCellType() == 1) {
                                        form.setData20(row.getCell(19).getStringCellValue());
                                    } else {
                                        form.setData20(NumberToTextConverter.toText(row.getCell(19).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(20)!=null) {
                                    if (row.getCell(20).getCellType() == 1) {
                                        form.setData21(row.getCell(20).getStringCellValue());
                                    } else {
                                        form.setData21(NumberToTextConverter.toText(row.getCell(20).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(21)!=null) {
                                    if (row.getCell(21).getCellType() == 1) {
                                        form.setData22(row.getCell(21).getStringCellValue());
                                    } else {
                                        form.setData22(NumberToTextConverter.toText(row.getCell(21).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(22)!=null) {
                                    if (row.getCell(22).getCellType() == 1) {
                                        form.setData23(row.getCell(22).getStringCellValue());
                                    } else {
                                        form.setData23(NumberToTextConverter.toText(row.getCell(22).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(23)!=null) {
                                    if (row.getCell(23).getCellType() == 1) {
                                        form.setData24(row.getCell(23).getStringCellValue());
                                    } else {
                                        form.setData24(NumberToTextConverter.toText(row.getCell(23).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(24)!=null) {
                                    if (row.getCell(24).getCellType() == 1) {
                                        form.setData25(row.getCell(24).getStringCellValue());
                                    } else {
                                        form.setData25(NumberToTextConverter.toText(row.getCell(24).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(25)!=null) {
                                    if (row.getCell(25).getCellType() == 1) {
                                        form.setData26(row.getCell(25).getStringCellValue());
                                    } else {
                                        form.setData26(NumberToTextConverter.toText(row.getCell(25).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(26)!=null) {
                                    if (row.getCell(26).getCellType() == 1) {
                                        form.setData27(row.getCell(26).getStringCellValue());
                                    } else {
                                        form.setData27(NumberToTextConverter.toText(row.getCell(26).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(27)!=null) {
                                    if (row.getCell(27).getCellType() == 1) {
                                        form.setData28(row.getCell(27).getStringCellValue());
                                    } else {
                                        form.setData28(NumberToTextConverter.toText(row.getCell(27).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(28)!=null) {
                                    if (row.getCell(28).getCellType() == 1) {
                                        form.setData29(row.getCell(28).getStringCellValue());
                                    } else {
                                        form.setData29(NumberToTextConverter.toText(row.getCell(28).getNumericCellValue()));
                                    }
                                }
                                form.setOrgcode(orgcode);
                                form.setStepid(stepid);
                                form.setCyear(ayear);
                                form.setPlanid(mid);
                                form.setOrgcatid(orgtype);
                                if (row.getCell(0)!=null) {
                                    datas.add(form);
                                }
                                count = count + 1;
                            }
                            dao.inserBatch(datas, "16.Assets", mid);
                        }
                        else if (sheet.getSheetName().equalsIgnoreCase("17.Inventory")) {
                            dao.PeaceCrud(null, "FinInventory", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinInventory> datas = new ArrayList<FinInventory>();
                            for (int k = 5; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                FinInventory form = new FinInventory();
                                form.setStepid(stepid);
                                Iterator cellIterator = row.cellIterator();
								System.out.println("rowNum"+k);
                                if (row.getCell(0)!=null){
                                    if (row.getCell(0).getCellType() == 1) {
                                        form.setData1(row.getCell(0).getStringCellValue());
                                    }
                                    else{
                                        form.setData1(NumberToTextConverter.toText(row.getCell(0).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(1)!=null){
                                    if (row.getCell(1).getCellType() == 1) {
                                        form.setData2(row.getCell(1).getStringCellValue());
                                    }
                                    else{
                                        form.setData2(NumberToTextConverter.toText(row.getCell(1).getNumericCellValue()));
                                    }
                                }

                                if (row.getCell(2)!=null) {
                                    if (row.getCell(2).getCellType() == 1) {
                                        form.setData3(row.getCell(2).getStringCellValue());
                                    } else {
                                        form.setData3(NumberToTextConverter.toText(row.getCell(2).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(3)!=null) {
                                    if (row.getCell(3).getCellType() == 1) {
                                        form.setData4(row.getCell(3).getStringCellValue());
                                    } else {
                                        form.setData4(NumberToTextConverter.toText(row.getCell(3).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(4)!=null) {
                                    if (row.getCell(4).getCellType() == 1) {
                                        form.setData5(row.getCell(4).getStringCellValue());
                                    } else {
                                        form.setData5(NumberToTextConverter.toText(row.getCell(4).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(5)!=null) {
                                    if (row.getCell(5).getCellType() == 1) {
                                        form.setData6(row.getCell(5).getStringCellValue());
                                    } else {
                                        form.setData6(NumberToTextConverter.toText(row.getCell(5).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(6)!=null) {
                                    if (row.getCell(6).getCellType() == 1) {
                                        form.setData7(row.getCell(6).getStringCellValue());
                                    } else {
                                        form.setData7(NumberToTextConverter.toText(row.getCell(6).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(7)!=null) {
                                    if (row.getCell(7).getCellType() == 1) {
                                        form.setData8(row.getCell(7).getStringCellValue());
                                    } else {
                                        form.setData8(NumberToTextConverter.toText(row.getCell(7).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(8)!=null) {
                                    if (row.getCell(8).getCellType() == 1) {
                                        form.setData9(row.getCell(8).getStringCellValue());
                                    } else {
                                        form.setData9(NumberToTextConverter.toText(row.getCell(8).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(9)!=null) {
                                    if (row.getCell(9).getCellType() == 1) {
                                        form.setData10(row.getCell(9).getStringCellValue());
                                    } else {
                                        form.setData10(NumberToTextConverter.toText(row.getCell(9).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(10)!=null) {
                                    if (row.getCell(10) != null && row.getCell(10).getCellType() == 1) {
                                        form.setData11(row.getCell(10).getStringCellValue());
                                    } else {
                                        form.setData11(NumberToTextConverter.toText(row.getCell(10).getNumericCellValue()));
                                    }
                                }
                                if (row.getCell(11)!=null) {
                                    if (row.getCell(11) != null && row.getCell(11).getCellType() == 1) {
                                        form.setData12(row.getCell(11).getStringCellValue());
                                    } else {
                                        form.setData12(NumberToTextConverter.toText(row.getCell(11).getNumericCellValue()));
                                    }
                                }
                                form.setOrgcode(orgcode);
                                form.setStepid(stepid);
                                form.setCyear(ayear);
                                form.setPlanid(mid);
                                form.setOrgcatid(orgtype);
                                if (row.getCell(0)!=null) {
                                    datas.add(form);
                                }
                                count = count + 1;
                            }
                            dao.inserBatch(datas, "17.Inventory", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("4.Assets")) {
                            //dao.PeaceCrud(null, "FinAsset", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
                            List<FinAsset> datas = new ArrayList<FinAsset>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 5; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);

                                FinAsset form = new FinAsset();
                                form.setStepid(stepid);
                                Iterator cellIterator = row.cellIterator();

                                while (cellIterator.hasNext()) {

                                    Cell cell = (Cell) cellIterator.next();
                                    Cell zcell = crow.getCell(cell.getColumnIndex());
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_STRING:
                                            zcell.setCellValue(cell.getStringCellValue());
                                            if (cell.getColumnIndex() == 0) {
                                                form.setData1(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 1) {
                                                form.setData2(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 2) {
                                                form.setData3(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 3) {
                                                form.setData4(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 4) {
                                                form.setData5(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 5) {
                                                form.setData6(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 6) {
                                                form.setData7(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 7) {
                                                form.setData8(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 8) {
                                                form.setData9(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 9) {
                                                form.setData10(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 10) {
                                                form.setData11(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 11) {
                                                form.setData12(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 12) {
                                                form.setData13(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 13) {
                                                form.setData14(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 14) {
                                                form.setData15(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 15) {
                                                form.setData16(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 16) {
                                                form.setData17(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 17) {
                                                form.setData18(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 18) {
                                                form.setData19(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 19) {
                                                form.setData20(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 20) {
                                                form.setData21(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 21) {
                                                form.setData22(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 22) {
                                                form.setData23(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 23) {
                                                form.setData24(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 24) {
                                                form.setData25(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 25) {
                                                form.setData26(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 26) {
                                                form.setData27(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 27) {
                                                form.setData28(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 28) {
                                                form.setData29(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 29) {
                                                form.setData30(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 30) {
                                                form.setData31(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 31) {
                                                form.setData32(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 32) {
                                                form.setData33(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 33) {
                                                form.setData34(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 34) {
                                                form.setData35(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 35) {
                                                form.setData36(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 36) {
                                                form.setData37(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 37) {
                                                form.setData38(cell.getStringCellValue());
                                            }
                                            if (cell.getColumnIndex() == 38) {
                                                form.setData39(cell.getStringCellValue());
                                            }
                                            break;
                                        case Cell.CELL_TYPE_NUMERIC:
                                            zcell.setCellValue(0);
                                            zcell.setCellValue(0);
                                            zcell.setCellValue(cell.getNumericCellValue());
                                            if (cell.getColumnIndex() == 0) {
                                                form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 1) {
                                                form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 2) {
                                                form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 3) {
                                                form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 4) {
                                                form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 5) {
                                                form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 6) {
                                                form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 7) {
                                                form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 8) {
                                                form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 9) {
                                                form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 10) {
                                                form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 11) {
                                                form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 12) {
                                                form.setData13(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 13) {
                                                form.setData14(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 14) {
                                                form.setData15(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 15) {
                                                form.setData16(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 16) {
                                                form.setData17(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 17) {
                                                form.setData18(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 18) {
                                                form.setData19(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 19) {
                                                form.setData20(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 20) {
                                                form.setData21(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 21) {
                                                form.setData22(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 22) {
                                                form.setData23(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 23) {
                                                form.setData24(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 24) {
                                                form.setData25(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 25) {
                                                form.setData26(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 26) {
                                                form.setData27(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 27) {
                                                form.setData28(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 28) {
                                                form.setData29(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 29) {
                                                form.setData30(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 30) {
                                                form.setData31(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 31) {
                                                form.setData32(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 32) {
                                                form.setData33(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 33) {
                                                form.setData34(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 34) {
                                                form.setData35(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 35) {
                                                form.setData36(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 36) {
                                                form.setData37(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 37) {
                                                form.setData38(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            if (cell.getColumnIndex() == 38) {
                                                form.setData39(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                            }
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            if (cell.getColumnIndex() == 1) {

                                                //	dao.inserBatch(datas,"4.Assets",mid);
                                                continue here;
                                            }
                                            break;
                                    }
                                }
                                form.setOrgcode(orgcode);
                                form.setStepid(stepid);
                                form.setCyear(ayear);
                                form.setPlanid(mid);
                                form.setOrgcatid(orgtype);
                                datas.add(form);
                                count = count + 1;
                            }
                            //dao.inserBatch(datas,"4.Assets",mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("19.Budget")) {
                            //dao.PeaceCrud(null, "FinBudget", "multidelete", (long) 0, 0, 0, "where planid="+mid+" and stepid="+stepid+"");
                            List<FinBudget> datas = new ArrayList<FinBudget>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 5; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);

                                try {
                                    FinBudget form = new FinBudget();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 16) {
                                                    form.setData17(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 16) {
                                                    form.setData17(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    //	dao.inserBatch(datas,"19.Budget",mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            //dao.inserBatch(datas,"19.Budget",mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("2.CT1A") || sheet.getSheetName().equalsIgnoreCase("СБД")) {
                            dao.PeaceCrud(null, "FinCt1a", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCt1a> datas = new ArrayList<FinCt1a>();
                            int mnCount = 4;
                            if (mn.getOrgtype() == 1) {
                                mnCount = mnCount + 4;
                            }
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = mnCount; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);

                                try {
                                    FinCt1a form = new FinCt1a();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();


                                        Cell zcell = crow.getCell(cell.getColumnIndex());

                                        switch (cell.getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    if (form.getData1().length() > 0) {
                                        datas.add(form);
                                    }

                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                            dao.inserBatch(datas, "st1a", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("6.CTT1")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CTT1 WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCtt1", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt1> datas = new ArrayList<FinCtt1>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt1 form = new FinCtt1();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {
                                                    dao.inserBatch(datas, "6.CTT1", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    //dao.PeaceCrud(form, "FinCtt1", "save", (long) 0, 0, 0, null);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                            dao.inserBatch(datas, "6.CTT1", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("7.CTT2")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CTT2 WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCtt2", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt2> datas = new ArrayList<FinCtt2>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt2 form = new FinCtt2();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    dao.inserBatch(datas, "7.CTT2", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    //dao.PeaceCrud(form, "FinCtt8", "save", (long) 0, 0, 0, null);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "7.CTT2", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("8.CTT3")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CTT3 WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCtt3", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt3> datas = new ArrayList<FinCtt3>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt3 form = new FinCtt3();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 16) {
                                                    form.setData17(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 16) {
                                                    form.setData17(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {
                                                    dao.inserBatch(datas, "8.CTT3", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setCyear(ayear);
                                    form.setOrgcatid(orgtype);
                                    //dao.PeaceCrud(form, "FinCtt2", "save", (long) 0, 0, 0, null);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                            dao.inserBatch(datas, "8.CTT3", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("9.CTT4")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CTT4 WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCtt4", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt4> datas = new ArrayList<FinCtt4>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt4 form = new FinCtt4();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    dao.inserBatch(datas, "9.CTT4", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    //dao.PeaceCrud(form, "FinCtt3", "save", (long) 0, 0, 0, null);
                                    datas.add(form);
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "9.CTT4", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("10.CTT5")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CTT5 WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCtt5", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt5> datas = new ArrayList<FinCtt5>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 8; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt5 form = new FinCtt5();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 16) {
                                                    form.setData17(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 16) {
                                                    form.setData17(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {
                                                    dao.inserBatch(datas, "10.CTT5", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "10.CTT5", mid);
                        }
                        if (sheet.getSheetName().equalsIgnoreCase("11.CTT6")) {
                            dao.PeaceCrud(null, "FinCtt6", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt6> datas = new ArrayList<FinCtt6>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt6 form = new FinCtt6();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }

                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    dao.inserBatch(datas, "11.CTT6", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "11.CTT6", mid);

                        } else if (sheet.getSheetName().equalsIgnoreCase("12.CTT7")) {
                            dao.PeaceCrud(null, "FinCtt7", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt7> datas = new ArrayList<FinCtt7>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 9; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt7 form = new FinCtt7();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();

                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 8) {
                                                    form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 9) {
                                                    form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 10) {
                                                    form.setData11(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 11) {
                                                    form.setData12(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 12) {
                                                    form.setData13(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 13) {
                                                    form.setData14(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 14) {
                                                    form.setData15(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 15) {
                                                    form.setData16(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    dao.inserBatch(datas, "12.CTT7", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "12.CTT7", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("13.CTT8")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CTT8 WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCtt8", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt8> datas = new ArrayList<FinCtt8>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt8 form = new FinCtt8();
                                    Iterator cellIterator = row.cellIterator();
                                    form.setStepid(stepid);
                                    while (cellIterator.hasNext()) {

                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    dao.inserBatch(datas, "13.CTT8", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "13.CTT8", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("14.CTT9")) {
                            dao.PeaceCrud(null, "FinCtt9", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCtt9> datas = new ArrayList<FinCtt9>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCtt9 form = new FinCtt9();
                                    Iterator cellIterator = row.cellIterator();
                                    form.setStepid(stepid);
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {

                                                    dao.inserBatch(datas, "14.CTT9", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "14.CTT9", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("20.TGT1")) {
                            dao.PeaceCrud(null, "FinTgt1", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinTgt1> datas = new ArrayList<FinTgt1>();
                            System.out.println("sheet.getLastRowNum() ===== " + dataSheet.getLastRowNum());
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 7; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);

                                try {
                                    FinTgt1 form = new FinTgt1();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());

                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData4(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData5(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    long a = 0;
                                                    CellValue cellValue = evaluator.evaluate(cell);
                                                    a = (long) cellValue.getNumberValue();

                                                    //form.setData6("as");
                                                    form.setData6(cell.getStringCellValue());
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 4) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 5) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 6) {
                                                    form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 7) {
                                                    long a = 0;
                                                    CellValue cellValue = evaluator.evaluate(cell);
                                                    a = (long) cellValue.getNumberValue();

                                                    form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_FORMULA:
                                                if (cell.getColumnIndex() == 7) {
                                                    long a = 0;
                                                    CellValue cellValue = evaluator.evaluate(cell);
                                                    a = (long) cellValue.getNumberValue();
                                                    zcell.setCellValue(String.valueOf(a));
                                                    form.setData6(String.valueOf(a));
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    datas.add(form);
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                            dao.inserBatch(datas, "20.TGT1", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("22.NT2")) {
                            dao.PeaceCrud(null, "FinNt2", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinNt2> datas = new ArrayList<FinNt2>();
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = 6; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinNt2 form = new FinNt2();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (evaluator.evaluateInCell(cell).getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                zcell.setCellValue(cell.getStringCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(cell.getStringCellValue());
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(cell.getStringCellValue());
                                                }

                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                zcell.setCellValue(0);
                                                zcell.setCellValue(cell.getNumericCellValue());
                                                if (cell.getColumnIndex() == 0) {
                                                    form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 1) {
                                                    form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 2) {
                                                    form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                if (cell.getColumnIndex() == 3) {
                                                    form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                }
                                                break;
                                            case Cell.CELL_TYPE_BLANK:
                                                if (cell.getColumnIndex() == 1) {
                                                    dao.inserBatch(datas, "22.NT2", mid);
                                                    continue here;
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    //dao.PeaceCrud(form, "FinNt2", "save", (long) 0, 0, 0, null);
                                    datas.add(form);
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                            dao.inserBatch(datas, "22.NT2", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("4.CT3A") || sheet.getSheetName().equalsIgnoreCase("МГТ")) {
                            dao.PeaceCrud(null, "FinCt3a", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCt3a> datas = new ArrayList<FinCt3a>();
                            int mnCount = 4;
                            if (mn.getOrgtype() == 1) {
                                mnCount = mnCount + 4;
                            }
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = mnCount; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);

                                try {
                                    FinCt3a form = new FinCt3a();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (cell.getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                }
                                                break;
                                            case Cell.CELL_TYPE_FORMULA:
                                                final CellValue cellValue = evaluator.evaluate(cell);

                                                if (mn.getOrgtype() == 1) {
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData3(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData3(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    if (form.getData1().length() > 0) {
                                        datas.add(form);
                                    }
                                    count = count + 1;

                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "4.CT3A", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("5.CT4A") || sheet.getSheetName().equalsIgnoreCase("ӨӨТ")) {
                            dao.PeaceCrud(null, "FinCt4a", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCt4a> datas = new ArrayList<FinCt4a>();
                            int mnCount = 4;
                            if (mn.getOrgtype() == 1) {
                                mnCount = mnCount + 3;
                            }
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = mnCount; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCt4a form = new FinCt4a();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (cell.getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        System.out.println("cell 0: " + cell.getStringCellValue());
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData5(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 5) {
                                                        form.setData6(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 6) {
                                                        form.setData7(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 7) {
                                                        form.setData8(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 8) {
                                                        form.setData9(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 9) {
                                                        form.setData10(cell.getStringCellValue());
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 5) {
                                                        form.setData5(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 5) {
                                                        form.setData6(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 6) {
                                                        form.setData7(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 7) {
                                                        form.setData8(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 8) {
                                                        form.setData9(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 9) {
                                                        form.setData10(cell.getStringCellValue());
                                                    }
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 5) {
                                                        form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 6) {
                                                        form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 7) {
                                                        form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 8) {
                                                        form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 9) {
                                                        form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 5) {
                                                        form.setData5(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 6) {
                                                        form.setData6(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 7) {
                                                        form.setData7(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 8) {
                                                        form.setData8(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 9) {
                                                        form.setData9(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 10) {
                                                        form.setData10(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    if (form.getData1().length() > 0) {
                                        datas.add(form);
                                    }
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }

                            dao.inserBatch(datas, "5.CT4A", mid);
                        } else if (sheet.getSheetName().equalsIgnoreCase("3.CT2A") || sheet.getSheetName().equalsIgnoreCase("ОДТ")) {
                            //dao.getNativeSQLResult("DELETE FROM FIN_CT2A WHERE PLANID='" + mid + "' and STEPID='"+stepid+"'", "delete");
                            dao.PeaceCrud(null, "FinCt2a", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");
                            List<FinCt2a> datas = new ArrayList<FinCt2a>();
                            int mnCount = 4;
                            if (mn.getOrgtype() == 1) {
                                mnCount = mnCount + 4;
                            }
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            for (int k = mnCount; k <= dataSheet.getLastRowNum(); k++) {
                                Row row = dataSheet.getRow(k);
                                Row crow = sheet.getRow(k);
                                try {
                                    FinCt2a form = new FinCt2a();
                                    form.setStepid(stepid);
                                    Iterator cellIterator = row.cellIterator();
                                    while (cellIterator.hasNext()) {
                                        Cell cell = (Cell) cellIterator.next();
                                        Cell zcell = crow.getCell(cell.getColumnIndex());
                                        switch (cell.getCellType()) {
                                            case Cell.CELL_TYPE_STRING:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(cell.getStringCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {

                                                        form.setData3(cell.getStringCellValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(cell.getStringCellValue());
                                                    }
                                                }
                                                break;
                                            case Cell.CELL_TYPE_NUMERIC:
                                                if (mn.getOrgtype() == 1) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 0) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    zcell.setCellValue(0);
                                                    zcell.setCellValue(cell.getNumericCellValue());
                                                    if (cell.getColumnIndex() == 1) {
                                                        form.setData1(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData2(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData3(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(NumberToTextConverter.toText(cell.getNumericCellValue()));
                                                    }
                                                }
                                                break;
                                            case Cell.CELL_TYPE_FORMULA:
                                                final CellValue cellValue = evaluator.evaluate(cell);

                                                if (mn.getOrgtype() == 1) {
                                                    if (cell.getColumnIndex() == 2) {
                                                        form.setData3(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData4(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                } else if (mn.getOrgtype() == 2) {
                                                    if (cell.getColumnIndex() == 3) {
                                                        form.setData3(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                    if (cell.getColumnIndex() == 4) {
                                                        form.setData4(String.valueOf(cellValue.getNumberValue()));
                                                        zcell.setCellValue(cellValue.getNumberValue());
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    form.setOrgcode(orgcode);
                                    form.setStepid(stepid);
                                    form.setCyear(ayear);
                                    form.setPlanid(mid);
                                    form.setOrgcatid(orgtype);
                                    //dao.PeaceCrud(form, "FinSt2a", "save", (long) 0, 0, 0, null);
                                    if (form.getData1().length() > 0) {
                                        datas.add(form);
                                    }
                                    count = count + 1;
                                } catch (Exception e) {
                                    arr.put("count", count - 1);
                                    arr.put("response", true);
                                }
                            }
                            dao.inserBatch(datas, "st2a", mid);
                        } else if (sheet != null && sheet.getSheetName().trim().equals("15.Journal") || sheet.getSheetName().trim().equals("Journal")) {
                            List<FinJournal> customers = new ArrayList<FinJournal>();
                            int mnCount = 4;
                            System.out.println("orgtype" + mn.getOrgtype());
                            if (mn.getOrgtype() == 2) {
                                mnCount = mnCount + 5;
                            }
                            for (int k = 0; k <= sheet.getLastRowNum(); k++) {
                                Row crow = sheet.getRow(k);
                                if (crow != null) {
                                    for (Cell cl : crow) {
                                        if (cl != null && cl.getColumnIndex() != 1 && cl.getColumnIndex() != 0) {
                                            switch (cl.getCellType()) {
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    cl.setCellValue(0);
                                            }
                                        }
                                    }
                                }

                            }
                            dao.PeaceCrud(null, "FinJournal", "multidelete", (long) 0, 0, 0, "where planid=" + mid + " and stepid=" + stepid + "");

                            for (int kk = mnCount; kk <= dataSheet.getLastRowNum(); kk++) {
                                Row currentRow = dataSheet.getRow(kk);
                                try {
                                    if (currentRow != null) {
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

                                        FinJournal newFile = new FinJournal();
                                        newFile.setStepid(stepid);
                                        Row crow = workbook.getSheetAt(i).getRow(kk);
                                        Cell cell1 = null;
                                        if (crow.getCell(0) != null) {
                                            cell1 = crow.getCell(0);
                                        } else {
                                            cell1 = crow.createCell(0);
                                        }

                                        Cell cell2 = null;
                                        if (crow.getCell(1) != null) {
                                            cell2 = crow.getCell(1);
                                        } else {
                                            cell2 = crow.createCell(1);
                                        }

                                        Cell cell3 = null;
                                        if (crow.getCell(2) != null) {
                                            cell3 = crow.getCell(2);
                                        } else {
                                            cell3 = crow.createCell(2);
                                        }

                                        Cell cell4 = null;
                                        if (crow.getCell(3) != null) {
                                            cell4 = crow.getCell(3);
                                        } else {
                                            cell4 = crow.createCell(3);
                                        }

                                        Cell cell5 = null;
                                        if (crow.getCell(4) != null) {
                                            cell5 = crow.getCell(4);
                                        } else {
                                            cell5 = crow.createCell(4);
                                        }

                                        Cell cell18 = null;
                                        if (crow.getCell(17) != null) {
                                            cell18 = crow.getCell(17);
                                        } else {
                                            cell18 = crow.createCell(17);
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

                                        if (data1 != null) {
                                            switch (wevaluator.evaluateInCell(data1).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData1(data1.getStringCellValue());
                                                    cell1.setCellValue(data1.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    long a = (long) data1.getNumericCellValue();
                                                    newFile.setData1(String.valueOf(a));
                                                    cell1.setCellValue(data1.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = evaluator.evaluate(data1);
                                                    cell1.setCellValue(cellValue.getStringValue());
                                                    newFile.setData1(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }

                                        if (data2 != null) {
                                            switch (wevaluator.evaluateInCell(data2).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData2(data2.getStringCellValue());
                                                    cell2.setCellValue(data2.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData2(String.valueOf(data2.getNumericCellValue()));
                                                    cell2.setCellValue(data2.getNumericCellValue());

                                                    if (HSSFDateUtil.isCellDateFormatted(data2)) {
                                                        Date d1 = data2.getDateCellValue();
                                                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY");
                                                        String formattedDate = df.format(d1);
                                                        newFile.setData2(formattedDate);
                                                        cell2.setCellValue(formattedDate);
                                                    }
                                                    break;
                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data2);
                                                    cell2.setCellValue(cellValue.getStringValue());
                                                    newFile.setData2(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }

                                        if (data3 != null) {
                                            switch (wevaluator.evaluateInCell(data3).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData3(data3.getStringCellValue());
                                                    cell3.setCellValue(data3.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    long a = (long) data3.getNumericCellValue();
                                                    newFile.setData3(String.valueOf(a));
                                                    cell3.setCellValue(data3.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data3);
                                                    cell3.setCellValue(cellValue.getStringValue());
                                                    newFile.setData3(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }

                                        if (data4 != null) {
                                            switch (wevaluator.evaluateInCell(data4).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData4(data4.getStringCellValue());
                                                    cell4.setCellValue(data4.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData4(String.valueOf(data4.getNumericCellValue()));
                                                    cell4.setCellValue(data4.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data4);
                                                    cell4.setCellValue(cellValue.getStringValue());
                                                    newFile.setData4(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data5 != null) {
                                            switch (wevaluator.evaluateInCell(data5).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData5(data5.getStringCellValue());
                                                    cell5.setCellValue(data5.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData5(String.valueOf(data5.getNumericCellValue()));
                                                    cell5.setCellValue(data5.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data5);
                                                    cell5.setCellValue(cellValue.getStringValue());
                                                    newFile.setData5(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data6 != null) {
                                            switch (wevaluator.evaluateInCell(data6).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData6(data6.getStringCellValue());
                                                    cell6.setCellValue(data6.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData6(String.valueOf(data6.getNumericCellValue()));
                                                    cell6.setCellValue(data6.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data6);
                                                    cell6.setCellValue(cellValue.getStringValue());
                                                    newFile.setData6(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data7 != null) {
                                            switch (wevaluator.evaluateInCell(data7).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData7(data7.getStringCellValue());
                                                    cell7.setCellValue(data7.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData7(String.valueOf(data7.getNumericCellValue()));
                                                    cell7.setCellValue(data7.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data7);
                                                    cell7.setCellValue(cellValue.getStringValue());
                                                    newFile.setData7(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }

                                        if (data8 != null) {
                                            switch (wevaluator.evaluateInCell(data8).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData8(data8.getStringCellValue());
                                                    cell8.setCellValue(data8.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    long a = (long) data8.getNumericCellValue();
                                                    cell8.setCellValue(data8.getNumericCellValue());
                                                    newFile.setData8(String.valueOf(a));
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data8);
                                                    cell8.setCellValue(cellValue.getStringValue());
                                                    newFile.setData8(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data9 != null) {
                                            switch (wevaluator.evaluateInCell(data9).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData9(data9.getStringCellValue());
                                                    cell9.setCellValue(data9.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    long a = (long) data9.getNumericCellValue();
                                                    cell9.setCellValue(data9.getNumericCellValue());
                                                    newFile.setData9(String.valueOf(a));
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data9);
                                                    cell9.setCellValue(cellValue.getStringValue());
                                                    newFile.setData9(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data10 != null) {
                                            switch (wevaluator.evaluateInCell(data10).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData10(Long.parseLong(data10.getStringCellValue()));
                                                    cell10.setCellValue(data10.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    long a = (long) data10.getNumericCellValue();
                                                    cell10.setCellValue(data10.getNumericCellValue());
                                                    newFile.setData10(a);
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data10);
                                                    cell10.setCellValue(cellValue.getStringValue());
                                                    newFile.setData10((long) cellValue.getNumberValue());
                                                    break;
                                            }
                                        }
                                        if (data11 != null) {
                                            switch (wevaluator.evaluateInCell(data11).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData11(data11.getStringCellValue());
                                                    cell11.setCellValue(data11.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData11(String.valueOf(data11.getNumericCellValue()));
                                                    cell11.setCellValue(data11.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data11);
                                                    cell11.setCellValue(cellValue.getStringValue());
                                                    newFile.setData11(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data12 != null) {
                                            switch (wevaluator.evaluateInCell(data12).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData12(data12.getStringCellValue());
                                                    cell12.setCellValue(data12.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData12(String.valueOf(data12.getNumericCellValue()));
                                                    cell12.setCellValue(data12.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data12);
                                                    cell12.setCellValue(cellValue.getStringValue());
                                                    newFile.setData12(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data13 != null) {
                                            switch (wevaluator.evaluateInCell(data13).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData13(data13.getStringCellValue());
                                                    cell13.setCellValue(data13.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData13(String.valueOf(data13.getNumericCellValue()));
                                                    cell13.setCellValue(data13.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data13);
                                                    cell13.setCellValue(cellValue.getStringValue());
                                                    newFile.setData13(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data14 != null) {
                                            switch (wevaluator.evaluateInCell(data14).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData14(data14.getStringCellValue());
                                                    cell14.setCellValue(data14.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData14(String.valueOf(data14.getNumericCellValue()));
                                                    cell14.setCellValue(data14.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data14);
                                                    cell14.setCellValue(cellValue.getStringValue());
                                                    newFile.setData14(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data15 != null) {
                                            switch (wevaluator.evaluateInCell(data15).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData15(data15.getStringCellValue());
                                                    cell15.setCellValue(data15.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData15(String.valueOf(data15.getNumericCellValue()));
                                                    cell15.setCellValue(data15.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data15);
                                                    cell15.setCellValue(cellValue.getStringValue());
                                                    newFile.setData15(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data16 != null) {
                                            switch (wevaluator.evaluateInCell(data16).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData16(data16.getStringCellValue());
                                                    cell16.setCellValue(data16.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData16(String.valueOf(data16.getNumericCellValue()));
                                                    cell16.setCellValue(data16.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data16);
                                                    cell16.setCellValue(cellValue.getStringValue());
                                                    newFile.setData16(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data17 != null) {
                                            switch (wevaluator.evaluateInCell(data17).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData17(data17.getStringCellValue());
                                                    cell17.setCellValue(data17.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData17(String.valueOf(data17.getNumericCellValue()));
                                                    cell17.setCellValue(data17.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data17);
                                                    cell17.setCellValue(cellValue.getStringValue());
                                                    newFile.setData17(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data18 != null) {
                                            switch (wevaluator.evaluateInCell(data18).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData18(data18.getStringCellValue());
                                                    cell18.setCellValue(data18.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData18(String.valueOf(data18.getNumericCellValue()));
                                                    cell18.setCellValue(data18.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data18);
                                                    cell18.setCellValue(cellValue.getStringValue());
                                                    newFile.setData18(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data19 != null) {
                                            switch (wevaluator.evaluateInCell(data19).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData19(data19.getStringCellValue());
                                                    cell19.setCellValue(data19.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData19(String.valueOf(data19.getNumericCellValue()));
                                                    cell19.setCellValue(data19.getNumericCellValue());
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data19);
                                                    cell19.setCellValue(data18.getStringCellValue());
                                                    newFile.setData19(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data20 != null) {
                                            switch (wevaluator.evaluateInCell(data20).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData20(data20.getStringCellValue());
                                                    cell20.setCellValue(data20.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData20(String.valueOf(data20.getNumericCellValue()));
                                                    cell20.setCellValue(data20.getNumericCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_BLANK:
                                                    newFile.setData20("");
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data20);
                                                    cell20.setCellValue(cellValue.getStringValue());
                                                    newFile.setData20(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }
                                        if (data21 != null) {
                                            switch (wevaluator.evaluateInCell(data21).getCellType()) {
                                                case Cell.CELL_TYPE_STRING:
                                                    newFile.setData21(data21.getStringCellValue());
                                                    //cell21.setCellValue(data21.getStringCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_NUMERIC:
                                                    newFile.setData21(String.valueOf(data21.getNumericCellValue()));
                                                    //	cell21.setCellValue(data21.getNumericCellValue());
                                                    break;
                                                case Cell.CELL_TYPE_BLANK:
                                                    newFile.setData21("");
                                                    break;

                                                case Cell.CELL_TYPE_FORMULA:
                                                    final CellValue cellValue = wevaluator.evaluate(data21);
                                                    newFile.setData21(String.valueOf(cellValue.getNumberValue()));
                                                    break;
                                            }
                                        }

                                        newFile.setA(true);
                                        newFile.setB(true);
                                        newFile.setC(true);
                                        newFile.setD(true);
                                        newFile.setE(true);
                                        //journalRepository.save(newFile);
                                        if (newFile.getData1() != null) {
                                            customers.add(newFile);
                                        }
                                    }
                                } catch (NumberFormatException e) {

                                }

                            }

                            dao.inserBatch(customers, "FinJournal", mid);
                        } else if (sheet != null && dataSheet.getSheetName().trim().equals("1.Info")) {
                            System.out.println("" + dataSheet.getLastRowNum());
                            for (int kk = 2; kk < dataSheet.getLastRowNum(); kk++) {
                                Row currentRow = dataSheet.getRow(kk);
                                Row backRow = sheet.getRow(2);
                                if (currentRow != null) {
                                    if (currentRow.getCell(0) != null) {

                                        Row r = dataSheet.getRow(kk);
                                        if (r == null) {
                                            r = sheet.createRow(kk);
                                        }

                                        for (int p = 0; p < 6; p++) {
                                            if (sheet.getRow(kk) != null) {
                                                Cell columnHeaderCell = sheet.getRow(kk).getCell(p);
                                                if (currentRow.getCell(p) != null) {
                                                    switch (wevaluator.evaluateInCell(currentRow.getCell(p)).getCellType()) {
                                                        case Cell.CELL_TYPE_STRING:
                                                            columnHeaderCell.setCellValue(currentRow.getCell(p).getStringCellValue());
                                                            break;
                                                        case Cell.CELL_TYPE_NUMERIC:
                                                            columnHeaderCell.setCellValue(currentRow.getCell(p).getNumericCellValue());
                                                            break;
                                                        case Cell.CELL_TYPE_FORMULA:
                                                            final CellValue cellValue = wevaluator.evaluate(currentRow.getCell(p));
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
					/*	else if(sheet!=null && dataSheet.getSheetName().trim().equals("18.Payroll")){
							System.out.println(""+dataSheet.getLastRowNum());
							for(int kk=2;kk<dataSheet.getLastRowNum();kk++){
								Row currentRow= dataSheet.getRow(kk);
								Row backRow = sheet.getRow(2);
								if(currentRow!=null){
									if(currentRow.getCell(0)!=null){

										Row r = sheet.getRow(kk);
										if (r == null) {
										    r = sheet.createRow(kk);
										}

									    for (int p = 0; p < 40; p++) {
						                    Cell columnHeaderCell = r.createCell(p);
						                    if(currentRow.getCell(p)!=null){
						                    	 switch (evaluator.evaluateInCell(currentRow.getCell(p)).getCellType())
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

						}*/

                    } else {
                        JSONObject robj = new JSONObject();
                        robj.put("excel", false);
                        robj.put("support", false);
                        robj.put("sheetname", workbook.getSheetAt(i).getSheetName().trim());
                        return robj;
                    }

                }


                Sheet haritsuulalt = zbook.getSheet("В-1-1");
                if (haritsuulalt != null) {
                    Row rw = haritsuulalt.getRow(9);
                    if (rw != null) {
                        rw.getCell(1).setCellValue(mn.getMatter());
                    }
                }

                if (mn.getAutype() != 2) {
                    Sheet sh = zbook.getSheet("1");
                    if (sh != null) {
                        if (sh.getSheetName().equalsIgnoreCase("1")) {
                            for (Row r : sh) {
                                if (r != null && r.getRowNum() > 0) {
                                    for (Cell c : r) {
                                        if (c.getColumnIndex() == 3) {
                                            final CellValue cell3Value = evaluator.evaluate(r.getCell(3));
                                            if (cell3Value != null) {
                                                switch (cell3Value.getCellType()) {
                                                    case Cell.CELL_TYPE_STRING:
                                                        break;
                                                    case Cell.CELL_TYPE_NUMERIC:
                                                        if (cell3Value.getNumberValue() != 0) {
                                                            if (cell3Value.getNumberValue() == 1) {
                                                                JSONObject obj = new JSONObject();
                                                                if (r.getCell(0) != null) {
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
                                                                                    if (cellValue.getStringValue() != null) {
                                                                                        obj.put("code2", cellValue.getStringValue());
                                                                                    } else {
                                                                                        obj.put("code2", "null");
                                                                                    }
                                                                                    break;
                                                                                case Cell.CELL_TYPE_NUMERIC:
                                                                                    if (cellValue.getNumberValue() != 0) {
                                                                                        obj.put("code2", cellValue.getNumberValue());
                                                                                    } else {
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
                    }
                }

                JSONObject robj = new JSONObject();

                if (errList.length() > 0 || sheetList.length() > 0) {
                    err.put("prefilter", errList);
                    err.put("additionalSheet", sheetList);
                    err.put("excel", false);
                    err.put("support", false);
                    return err;
                }

                if (arr1.length() > 0) {
                    robj.put("support", false);
                    robj.put("excel", false);
                    robj.put("error", arr1);
                    return robj;
                }


                if (err.length() == 0 && arr1.length() == 0) {

                    String uuid = UUID.randomUUID().toString() + ".xlsx";
                    FileOutputStream fout = new FileOutputStream("upload-dir" + File.separator + mn.getId() + File.separator + uuid);

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

                    if (zbook.getSheet("15.Journal") != null) {
                        for (int i = 0; i < zbook.getSheet("15.Journal").getLastRowNum() + 1; i++) {
                            Row currentRow = zbook.getSheet("15.Journal").getRow(i);
                            if (currentRow != null) {
                                if (currentRow.getCell(0) == null) {
                                    zbook.getSheet("15.Journal").removeRow(currentRow);
                                }
                            }
                        }
                    }
                    if (zbook.getSheet("Journal") != null) {
                        for (int i = 0; i < zbook.getSheet("Journal").getLastRowNum() + 1; i++) {
                            Row currentRow = zbook.getSheet("Journal").getRow(i);
                            if (currentRow != null) {
                                if (currentRow.getCell(0) == null) {
                                    zbook.getSheet("Journal").removeRow(currentRow);
                                }
                            }
                        }
                    }
                    zbook.write(fout);
                    fout.close();
                    fis.close();


                    Date d1 = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
                    String formattedDate = df.format(d1);

                    LutStaus newFile = new LutStaus();
                    newFile.setFilename(filename);
                    newFile.setFileurl("upload-dir" + File.separator + mid + File.separator + uuid);
                    newFile.setSavedname(uuid);
                    newFile.setCreateDate(formattedDate);
                    excelRepository.save(newFile);

                    LnkAuditReport newReport = new LnkAuditReport();
                    newReport.setFilename(filename);
                    newReport.setStepid(stepid);
                    newReport.setFileurl("upload-dir" + File.separator + mid + File.separator + newuuid);
                    newReport.setFname(uuid);
                    newReport.setCreateDate(formattedDate);
                    newReport.setAppid(mid);
                    newReport.setFsize(mfile.getSize());

                    dao.PeaceCrud(newReport, "LnkAuditReport", "save", (long) 0, 0, 0, null);

                    robj.put("excel", true);
                    robj.put("url", newFile.getFileurl());
                    robj.put("error", arr);
                } else {
                    robj.put("support", true);
                    robj.put("excel", false);
                    robj.put("error", arr);
                }
                return robj;
            }

        }
        return null;
    }

}