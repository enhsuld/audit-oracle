package com.nbb.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nbb.dao.UserDao;
import com.nbb.models.DataSourceResult;
import com.nbb.models.LnkMenurole;
import com.nbb.models.LnkUserrole;
import com.nbb.models.LutMenu;
import com.nbb.models.LutRole;
import com.nbb.models.LutUser;
import com.nbb.models.bs.FinJournal;
import com.nbb.models.fn.LnkAuditForm;
import com.nbb.models.fn.LnkAuditProblem;
import com.nbb.models.fn.LnkMainUser;
import com.nbb.models.fn.LutAuditLevel;
import com.nbb.models.fn.LutCategory;
import com.nbb.models.fn.LutDepartment;
import com.nbb.models.fn.LutForm;
import com.nbb.models.fn.LutPosition;
import com.nbb.models.fn.LutReason;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.repository.LnkMenuRepository;
import com.nbb.services.FormService;
import com.nbb.services.Services;
import com.nbb.services.SmtpMailSender;

@RestController
@RequestMapping("/fin")
public class FinController {
		
	@Autowired
    private UserDao dao;
	 
    @Autowired
    private LnkMenuRepository lpo;	
    
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @RequestMapping(value = "/plan/{id}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public @ResponseBody String treePlan(@PathVariable long id) {
    	try{

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				
				 LutDepartment aw=  (LutDepartment) dao.getHQLResult("from LutDepartment t where t.id='"+id+"'", "current");
				 JSONObject obj=new JSONObject();   
				 obj.put("id", aw.getId());
				 obj.put("text",  aw.getDepartmentname());
				 obj.put("count",  aw.getAuditCount());
				 return obj.toString();
			}		
       
		}
		catch(Exception e){
			e.printStackTrace();			
		}
		return null;
	}
    
    @RequestMapping(value = "/problem/{id}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public @ResponseBody String problem(@PathVariable long id) {
    	try{

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				 Date d1 = new Date();
				 SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
				 String special = df.format(d1);
				 List<LnkAuditProblem> aw=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.result>0 and finish=0 and t.appid='"+id+"'", "list");
				 if(aw.size()>0){
					dao.getNativeSQLResult("update lnk_audit_problems set finish=1, fin_date='"+special+"' where appid="+id+" and finish=0 and result>0", "update");
					return "true";
				 }
				 else{
					 return "false"; 
				 }				 
			}		
       
		}
		catch(Exception e){
			e.printStackTrace();			
		}
		return null;
	}
    
	@RequestMapping(value = "/list/{domain}", method= RequestMethod.POST)
    public @ResponseBody DataSourceResult customers(@PathVariable String domain, @RequestBody String request, HttpServletRequest req) throws HttpRequestMethodNotSupportedException, JSONException {
		Long count=(long) 0;
		List<?> rs = null;
		UserDetails userDetail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LutUser loguser= (LutUser) dao.getHQLResult("from LutUser t where t.username='"+userDetail.getUsername()+"'", "current");

		DataSourceResult result = new DataSourceResult();	
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			
			if(domain.equalsIgnoreCase("LutMenu")){
				List<LutMenu> wrap = new ArrayList<LutMenu>();
				
				rs= dao.kendojson(request, domain);
				count=(long) dao.resulsetcount(request, domain);
								
				for(int i=0;i<rs.size();i++){
					LutMenu or=(LutMenu) rs.get(i);
					LutMenu cor=new LutMenu();
					cor.setId(or.getId());
					cor.setUicon(or.getUicon());
					cor.setIsactive(or.getIsactive());
					cor.setMenuname(or.getMenuname());
					cor.setStateurl(or.getStateurl());
					cor.setOrderid(or.getOrderid());
					wrap.add(cor);
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_NULL);
				result.setData(rs);	
				result.setTotal(count);
			}
			else if(domain.equalsIgnoreCase("FinJournalPivot")){
				JSONObject str= new JSONObject(request);
				rs= (List<?>) dao.getHQLResult("from FinJournal t where t.planid="+str.getInt("planid")+" order by t.id asc", "list");
				result.setData(rs);	
				result.setTotal(rs.size());
			}
			else if(domain.equalsIgnoreCase("MainAuditRegistrationAu")){
				List<MainAuditRegistration> wrap = new ArrayList<MainAuditRegistration>();
				
				domain="MainAuditRegistration";				
				JSONObject str= new JSONObject(request);
				List<Object[]> cc=null;
				//List<Object[]> cr=  (List<Object[]>) dao.getHQLResult("select t.appid, m.gencode from LnkMainUser t, MainAuditRegistration m where m.id=t.appid and m.isactive=1 and m.isenabled=1 and t.userid='"+str.getString("userid")+"'", "list");
				if(loguser.getAutype()==2){
					cc=  (List<Object[]>) dao.getHQLResult("select t.id from  MainAuditRegistration t left join t.lnkMainUsers l  where t.autype in(3,4) and  t.id=l.appid and t.isactive=1 and t.isenabled=1  and l.userid='"+str.getInt("userid")+"' group by t.id", "list");
				}
				else{
					cc=  (List<Object[]>) dao.getHQLResult("select t.id from  MainAuditRegistration t left join t.lnkMainUsers l  where t.autype in(1,2) and  t.id=l.appid and t.isactive=1 and t.isenabled=1  and l.userid='"+str.getInt("userid")+"' group by t.id", "list");
				}
				
				ArrayList<Object[]> arr = (ArrayList<Object[]>) cc;
				String ids="";
				for(Object item:cc){
					if(ids.length()>0){
						ids=ids+","+item;
					}
					else{
						ids=String.valueOf(item);
					}
				}
				System.out.println("ids"+ids);
				if(ids.length()>0){
					str.put("custom", " where id in ("+ids.substring(0, ids.length())+") and isactive=1 and isenabled=1");
				}
				else{
					str.put("custom", " where id in (0) and isactive=1 and isenabled=1");
				}
				
				//str.put("custom", " , LnkMainUser l where t.id = l.appid and l.userid="+str.getString("userid")+" and t.isactive=1 and t.isenabled=1 and t.stepid>2");
			
				rs= dao.kendojson(str.toString(), domain);
				count=(long) dao.resulsetcount(str.toString(), domain);
				if(rs.size()>0){
					for(int i=0;i<rs.size();i++){
						MainAuditRegistration or=(MainAuditRegistration) rs.get(i);
						MainAuditRegistration cor=new MainAuditRegistration();
						String terguuleh="";
						String auditors="";
						String checkers="";
						String managers="";
						
						if(or.getLnkMainUsers().size()>0){
							for(int j=0;j<or.getLnkMainUsers().size();j++){
								if(or.getLnkMainUsers().get(j).getLutUser().getPositionid()==4|| or.getLnkMainUsers().get(j).getLutUser().getPositionid()==2 || or.getLnkMainUsers().get(j).getLutUser().getPositionid()==1){
									terguuleh=terguuleh+or.getLnkMainUsers().get(j).getLutUser().getFamilyname().substring(0, 1)+"."+or.getLnkMainUsers().get(j).getLutUser().getGivenname()+" , ";
								}
								else if(or.getLnkMainUsers().get(j).getLutUser().getPositionid()==7 || or.getLnkMainUsers().get(j).getLutUser().getPositionid()==6 || or.getLnkMainUsers().get(j).getLutUser().getPositionid()==8){
									if(or.getLnkMainUsers().get(j).getLutUser().getFamilyname()!=null){
										auditors=auditors+or.getLnkMainUsers().get(j).getLutUser().getFamilyname().substring(0, 1)+"."+or.getLnkMainUsers().get(j).getLutUser().getGivenname()+" , ";
									}
									else{
										auditors=auditors+or.getLnkMainUsers().get(j).getLutUser().getGivenname()+" , ";
									}
									
								}
								else if(or.getLnkMainUsers().get(j).getLutUser().getPositionid()==8){
									checkers=checkers+or.getLnkMainUsers().get(j).getLutUser().getFamilyname().substring(0, 1)+"."+or.getLnkMainUsers().get(j).getLutUser().getGivenname()+" , ";
								}
								else if(or.getLnkMainUsers().get(j).getLutUser().getPositionid()==5){
									managers=managers+or.getLnkMainUsers().get(j).getLutUser().getFamilyname().substring(0, 1)+"."+or.getLnkMainUsers().get(j).getLutUser().getGivenname()+" , ";
								}
								
							}
						}
						if(or.getIsactive()){
							cor.setId(or.getId());					
							cor.setGencode(or.getGencode());
							cor.setOrgtype(or.getOrgtype());
							cor.setStepid(or.getStepid());
							cor.setOrgname(or.getOrgname());
							cor.setIsactive(or.getIsactive());
							cor.setStartdate(or.getStartdate());
							cor.setEnddate(or.getEnddate());
							cor.setAper(or.getAper());
							cor.setA2per(or.getA2per());
							cor.setA3per(or.getA3per());
							cor.setMper(or.getMper());
							cor.setM2per(or.getM2per());
							cor.setM3per(or.getM3per());
							cor.setTper(or.getTper());
							cor.setT2per(or.getT2per());
							cor.setT3per(or.getT3per());
							cor.setLnkMainUsers(or.getLnkMainUsers());
							cor.setRegnum(or.getRegnum());
							cor.setDirector(or.getDirector());
							cor.setChpos(or.getChpos());
							cor.setChname(or.getChname());
							cor.setApos(or.getApos());
							cor.setAname(or.getAname());
							cor.setExcelurlplan(or.getExcelurlplan());
							cor.setExcelurlprocess(or.getExcelurlprocess());
							cor.setAuditname(or.getAuditname());
							if(terguuleh.length()>0){
								cor.setTerguuleh(terguuleh.substring(0, terguuleh.length()-2));
							}
							if(auditors.length()>0){
								cor.setAuditors(auditors.substring(0, auditors.length()-2));
							}
							if(checkers.length()>0){
								cor.setCheckers(checkers.substring(0, checkers.length()-2));
							}
							/*if(managers.length()>0){
								cor.setManagers(managers.substring(0, managers.length()-2));
							}*/
							cor.setAutype(or.getAutype());
							cor.setDepid(or.getDepid());
							cor.setAudityear(or.getAudityear());						
							wrap.add(cor);
						}
						
					}
				}		
				result.setData(wrap);	
				result.setTotal(count);
			}
			else{
				rs= dao.kendojson(request, domain);
				count=(long) dao.resulsetcount(request, domain);
				
			
				result.setData(rs);	
				result.setTotal((long) count);
			}
			return  result;
		}
		return null;
	}
	

	@RequestMapping(value = "/resource/{domain}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public @ResponseBody String tree(@PathVariable String domain) {
		try{
				
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();  
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			JSONArray arr=new JSONArray();
			
			UserDetails userDetail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			LutUser loguser= (LutUser) dao.getHQLResult("from LutUser t where t.username='"+userDetail.getUsername()+"'", "current");
			if (!(auth instanceof AnonymousAuthenticationToken)) {
				
				
			}		    	
	        return arr.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
    @RequestMapping(value = "/resource/{domain}/{id}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public @ResponseBody String treeReource(@PathVariable String domain,@PathVariable long id) {
	try{

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();  
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JSONArray arr=new JSONArray();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			
			if(domain.equalsIgnoreCase("LutDepartment")){
				List<LutDepartment> aw=  (List<LutDepartment>) dao.getHQLResult("from LutDepartment t where t.id='"+id+"'", "list");
				 for(int i=0; i<aw.size();i++){
					 JSONObject obj=new JSONObject();   
					 obj.put("id", aw.get(i).getId());
					 obj.put("text",  aw.get(i).getDepartmentname());
					 
					 arr.put(obj);
				 }	
			}
			else if(domain.equalsIgnoreCase("LutUser")){
				 List<LutUser> rs=(List<LutUser>) dao.getHQLResult("from LutUser t where t.iscompany=0 and givenname!=null and familyname!=null and t.departmentid="+id+" order by t.id", "list");
				 for(int i=0;i<rs.size();i++){
					 	JSONObject obj=new JSONObject();      	
					 	obj.put("value", rs.get(i).getId());
					 	if(rs.get(i).getFamilyname()!=null && rs.get(i).getGivenname()!=null){
					 		obj.put("text", rs.get(i).getFamilyname().substring(0, 1)+ "."+ rs.get(i).getGivenname());
					 	}else{
					 		obj.put("text", rs.get(i).getUsername());
					 	}
					 	if(rs.get(i).getPositionid()!=0){
					 		LutPosition ps=(LutPosition) dao.getHQLResult("from LutPosition t where t.id="+rs.get(i).getPositionid()+" order by t.id", "current");
						 	obj.put("position", ps.getPositionname());	   
					 	}
					 	     		
		        		arr.put(obj);        	
		        	}		
			}
			else if(domain.equalsIgnoreCase("LutReason")){
				 List<LutReason> rs=(List<LutReason>) dao.getHQLResult("from LutReason t order by t.id", "list");
				 for(int i=0;i<rs.size();i++){
					 	JSONObject obj=new JSONObject();      	
					 	obj.put("value", rs.get(i).getId());
					 	obj.put("text", rs.get(i).getName());			        		
		        		arr.put(obj);        	
		        	}		
			}
			else if(domain.equalsIgnoreCase("LutForm")){
				 LutForm rs=(LutForm) dao.getHQLResult("from LutForm t where t.id="+id+" order by t.id", "current");
				        
	        		
			 	 ObjectMapper mapper = new ObjectMapper();
				 mapper.setSerializationInclusion(Include.NON_NULL);
				
				 return mapper.writeValueAsString(rs);				
			}
			else if(domain.equalsIgnoreCase("MainAuditRegistration")){
				MainAuditRegistration aw=(MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");
				if(aw!=null){
					UserDetails userDetail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
					LutUser loguser= (LutUser) dao.getHQLResult("from LutUser t where t.username='"+userDetail.getUsername()+"'", "current");
					boolean lnk=false;
					for(LnkMainUser usr:aw.getLnkMainUsers()){
						if(usr.getUserid()==loguser.getId()){
							lnk=true;
						}
					}
					if(lnk){
						ObjectMapper mapper = new ObjectMapper();
						return mapper.writeValueAsString(aw);
					}
					else{
						return "false";
					}
					
				}
				else{
					return "false";
				}
				
			}

		}
        return arr.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
    
    @RequestMapping(value = "/survey/{domain}/{id}/{amount}/{planid}/{stepid}/{debCre}", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public @ResponseBody String totalAmount(@PathVariable String domain,@PathVariable long id,@PathVariable long planid,@PathVariable double amount, @PathVariable long stepid, @PathVariable(required=false) long debCre) {
	try{

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();  
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JSONArray arr=new JSONArray();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			if(domain.equalsIgnoreCase("accAmount")){
				List<Object> aw= new ArrayList<>();
				if(id==0){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where (t.planid="+planid+" and  t.data10>"+amount+" and t.stepid="+stepid+") or (t.planid="+planid+" and  t.data10>"+amount+" and t.stepid="+stepid+")  group by t.data10", "list");
				}
				else if(id==0 && amount==0){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t order by t.id asc", "list");
				}
				else if(id==800){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where  t.planid="+planid+"  and t.data10>"+amount+" and t.stepid="+stepid+"  group by t.data10", "list");
				}
				else{
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where ( t.planid="+planid+" and  t.data8 like '"+id+"%' and t.data10>"+amount+" and t.stepid="+stepid+") or ( t.planid="+planid+" and  t.data9 like '"+id+"%'  and t.data10>"+amount+" and t.stepid="+stepid+")  group by t.data10", "list");
				}
				Double sum=(double) 0;
				for (Object aRow : aw) {
				    sum = sum + Double.parseDouble(aRow.toString());
				}
				System.out.println("##"+sum);
				return String.valueOf(sum);
			}
		/*	if(domain.equalsIgnoreCase("accAmountSearchType2")){
				List<Object> aw= new ArrayList<>();
				if(id==0){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where (t.data10>"+amount+") or (t.data10>"+amount+")  group by t.data10 order by t.id asc", "list");
				}
				else if(id==0 && amount==0){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t order by t.id asc", "list");
				}
				else{
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where (t.data8 like '"+id+"%' and t.data10>"+amount+") or (t.data9 like '"+id+"%'  and t.data10>"+amount+")  group by t.data10 order by t.id asc", "list");
				}
				Double sum=(double) 0;
				for (Object aRow : aw) {
				    sum = sum + Double.parseDouble(aRow.toString());
				}
				System.out.println("##"+sum);
				return String.valueOf(sum);
			}*/
			
			else if(domain.equalsIgnoreCase("searchType3")){
				List<Object> ass;
				List<Object> aw;
			
				
				if(id==0){
					ass=  (List<Object>) dao.getHQLResult("from FinJournal t where t.planid="+planid+"  and t.stepid="+stepid+" order by t.id asc", "list");
					Double sum=(double) 0;
					for (Object aRow : ass) {
					    sum = sum + Double.parseDouble(aRow.toString());
					}
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where  (t.planid="+planid+" and t.data10>"+sum*amount/100+"  and t.stepid="+stepid+") or (t.planid="+planid+" and t.data10>"+sum*amount/100+" and t.stepid="+stepid+")  group by t.data10", "list");
				}
				else if(id==800){
					ass=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where t.planid="+planid+" and  t.stepid="+stepid+" group by t.data10", "list");
					
					Double sum=(double) 0;
					for (Object aRow : ass) {
					    sum = sum + Double.parseDouble(aRow.toString());
					}
					System.out.println("p"+sum*amount/100);
					aw=  (List<Object>) dao.getHQLResult("select t.data10 from FinJournal t where t.planid="+planid+" and t.data10>"+sum*amount/100+" and t.stepid="+stepid+"  order by t.id asc", "list");
				}
				else{
					ass=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where (t.planid="+planid+" and  t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and  t.data9 like '"+id+"%' and t.stepid="+stepid+")  group by t.data10", "list");
					
					Double sum=(double) 0;
					for (Object aRow : ass) {
					    sum = sum + Double.parseDouble(aRow.toString());
					}
					System.out.println("p"+sum*amount/100);
					aw=  (List<Object>) dao.getHQLResult("select t.data10 from FinJournal t where (t.planid="+planid+" and t.data10>"+sum*amount/100+" and  t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and t.data10>"+sum*amount/100+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")  order by t.id asc", "list");
				}
				
				Double sum=(double) 0;
				for (Object aRow : aw) {
				    sum = sum + Double.parseDouble(aRow.toString());
				}
				System.out.println("##"+sum);
				return String.valueOf(sum);
			}
			else if(domain.equalsIgnoreCase("searchType4")){
				List<Object[]> aw;
				List<FinJournal> ss;
				if(id==800){
					ss = (List<FinJournal>) dao.getHQLResult("from FinJournal t where t.planid="+planid+" and t.stepid="+stepid+"", "list");
				}else{
					ss = (List<FinJournal>) dao.getHQLResult("from FinJournal t where (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")", "list");
				}
				
				if(id==0){
					aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount from FinJournal t order by rand()", null);
				}
				else if(id==800){
					aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount, t.id from FinJournal t where (t.planid="+planid+" and t.stepid="+stepid+") or (t.planid="+planid+" and t.stepid="+stepid+") order by rand()", null);
				}
				else{
					if(debCre==1){
						aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount, t.id from FinJournal t where (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") ", null);
					}else{
						aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount, t.id from FinJournal t where (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+") ", null);
					}					
				}
				
				Double sum=(double) 0;
				Double sumError=(double) 0;
				String str="";
				for (Object[] aRow : aw) {
				    sum = sum + Double.parseDouble(aRow[0].toString());
				    sumError = sumError + Double.parseDouble(aRow[1].toString());
				    str=str+","+aRow[2].toString();
				}
				JSONObject robj=new JSONObject();
				robj.put("sum", sum);
				robj.put("countSel", amount);
				robj.put("count", ss.size());
				robj.put("sumError", sumError);
				robj.put("ids", str.substring(1,str.length()));
				return robj.toString();
			}
			else if(domain.equalsIgnoreCase("searchType5")){
				List<Object[]> aw;
			
				
				if(id==0){
					aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount from FinJournal t order by t.data10 desc", null);
				}
				else if(id==800){
					aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount, t.id from FinJournal t where (t.planid="+planid+" and t.stepid="+stepid+") or (t.planid="+planid+" and t.stepid="+stepid+")  order by t.data10 desc", null);
				}
				else{
					if(debCre==1){
						aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount, t.id from FinJournal t where (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") order by t.data10 desc", null);
					}else{
						aw=  (List<Object[]>) dao.jData((int) amount, 0, "", "select t.data10, t.amount, t.id from FinJournal t where (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")  order by t.data10 desc", null);
					}	
					
				}
				String str="";
				Double sum=(double) 0;
				Double sumError=(double) 0;
				for (Object[] aRow : aw) {
				    sum = sum + Double.parseDouble(aRow[0].toString());
				    sumError = sumError + Double.parseDouble(aRow[1].toString());
				    str=str+","+aRow[2].toString();
				}
				JSONObject robj=new JSONObject();
				robj.put("sum", sum);
				robj.put("sumError", sumError);
				robj.put("ids", str.substring(1,str.length()));
				return robj.toString();
			}
			else if(domain.equalsIgnoreCase("searchType1")){
				List<Object> ass;
				List<Object[]> aw;
			System.out.println("id"+id);
				if(id==800){
					aw=  (List<Object[]>) dao.getHQLResult("select t.data10, t.amount, t.id from FinJournal t where  t.planid="+planid+" and data10>"+amount+"  and t.stepid="+stepid+" order by t.id desc", "list");
				}
				else{
					if(debCre==1){
						aw=  (List<Object[]>) dao.getHQLResult("select t.data10, t.amount, t.id from FinJournal t where  t.planid="+planid+" and data10>"+amount+"  and t.stepid="+stepid+" and (t.data8 like '"+id+"%') order by t.id desc", "list");
					}else{
						aw=  (List<Object[]>) dao.getHQLResult("select t.data10, t.amount, t.id from FinJournal t where  t.planid="+planid+" and data10>"+amount+"  and t.stepid="+stepid+" and (t.data9 like '"+id+"%') order by t.id desc", "list");
					}
					
				}
				
				
				Double sum=(double) 0;
				Double sumError=(double) 0;
				String str="";
				for (Object[] aRow : aw) {
				    sum = sum + Double.parseDouble(aRow[0].toString());
				    sumError = sumError + Double.parseDouble(aRow[1].toString());
				    str=str+","+aRow[2].toString();
				}
				JSONObject robj=new JSONObject();
				robj.put("sum", sum);
				robj.put("sumError", sumError);
				if(str.length()>0){
					robj.put("ids", str.substring(1,str.length()));
				}
				else{
					robj.put("ids", 0);
				}	
				
				return robj.toString();
			}
			else if(domain.equalsIgnoreCase("searchType2")){
				List<Object> ass;
				List<Object[]> aw;
			
				
				if(id==0){
					aw=  (List<Object[]>) dao.jData((int) amount, 0,  "", "select t.data10, t.amount from FinJournal t order by rand()", null);
				}
				else if(id==800){
					aw=  (List<Object[]>) dao.jData((int) amount, 0,  "", "select t.data10, t.amount, t.id from FinJournal t where  (t.planid="+planid+" and t.stepid="+stepid+") or (t.planid="+planid+" and t.stepid="+stepid+") order by rand()", null);
				}
				else{
					if(debCre==1){
						aw=  (List<Object[]>) dao.jData((int) amount, 0,  "", "select t.data10, t.amount, t.id from FinJournal t where  (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+")  order by rand()", null);
					}else{
						aw=  (List<Object[]>) dao.jData((int) amount, 0,  "", "select t.data10, t.amount, t.id from FinJournal t where  (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+") order by rand()", null);
					}					
				}
				
				Double sum=(double) 0;
				Double sumError=(double) 0;
				String str="";
				for (Object[] aRow : aw) {
				    sum = sum + Double.parseDouble(aRow[0].toString());
				    sumError = sumError + Double.parseDouble(aRow[1].toString());
				    str=str+","+aRow[2].toString();
				}
				JSONObject robj=new JSONObject();
				robj.put("sum", sum);
				robj.put("sumError", sumError);
				robj.put("ids", str.substring(1,str.length()));
				return robj.toString();
			}
			else if(domain.equalsIgnoreCase("accTotalAmount")){
				List<Object> aw;
				if(id==0){
					 aw=  (List<Object>) dao.getHQLResult("select sum(t.amount) from FinJournal t where  (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")  group by t.data10 ", "list");
				}
				else if(id==800){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where (t.planid="+planid+" and  t.stepid="+stepid+") or (t.planid="+planid+" and t.stepid="+stepid+")  group by t.data10 ", "list");
				}
				else{
					if(debCre==1){
						aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where (t.planid="+planid+" and  t.data8 like '"+id+"%' and t.stepid="+stepid+")   group by t.data10 ", "list");
					}else if(debCre==2){
						aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where  (t.planid="+planid+" and  t.data9 like '"+id+"%' and t.stepid="+stepid+")  group by t.data10 ", "list");
					}	
					else{
						aw=  (List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where  (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")  group by t.data10 ", "list");
					}
				}
				Double sum=(double) 0;
				for (Object aRow : aw) {
				    sum = sum + Double.parseDouble(aRow.toString());
				}
				return String.valueOf(sum);
			}
			else if(domain.equalsIgnoreCase("amount")){
				List<Object> aw=(List<Object>) dao.getHQLResult("select sum(t.data10) from FinJournal t where t.planid="+planid+"  and t.stepid="+stepid+" group by t.data10", "list");
				Double sum=(double) 0;
				for (Object aRow : aw) {
				   sum = sum + Double.parseDouble(aRow.toString());
				}				
				return String.valueOf(sum);		
			}
			else if(domain.equalsIgnoreCase("totalAccError")){
				List<Object> aw;
				if(id==0){
					 aw=  (List<Object>) dao.getHQLResult("select sum(t.amount) from FinJournal t where  (t.planid="+planid+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")  group by t.data10 ", "list");
				}
				else if(id==800){
					aw=  (List<Object>) dao.getHQLResult("select sum(t.amount) from FinJournal t where (t.planid="+planid+" and t.data10>"+amount+" and t.stepid="+stepid+") or (t.planid="+planid+" and t.data10>"+amount+" and t.stepid="+stepid+")  group by t.amount", "list");
				}
				else{
					 aw=  (List<Object>) dao.getHQLResult("select sum(t.amount) from FinJournal t where (t.planid="+planid+" and t.data10>"+amount+" and t.data8 like '"+id+"%' and t.stepid="+stepid+") or (t.planid="+planid+" and t.data10>"+amount+" and t.data9 like '"+id+"%' and t.stepid="+stepid+")  group by t.amount", "list");
				}
				Double sum=(double) 0;
				for (Object aRow : aw) {
					   sum = sum + Double.parseDouble(aRow.toString());
				}				
				return String.valueOf(sum);			
			}
			else if(domain.equalsIgnoreCase("totalError")){
				 List<Object[]> aw=(List<Object[]>) dao.getHQLResult("select sum(t.a), sum(t.b), sum(t.c), sum(t.d), sum(t.e) from FinJournal t  where t.planid="+planid+" and t.stepid="+stepid+" and t.a=0 or t.b=0 or t.c=0 or t.d=0 or t.e=0  group by t.a, t.b ,t.c ,t.d ,t.e  order by t.id asc", "list");
				 int sum=(int) 0;
				 for (Object[] aRow : aw) {
					 if(aRow[0].toString().equalsIgnoreCase("false")){
						 sum=sum+1;
					 }
					 if(aRow[1].toString().equalsIgnoreCase("false")){
						 sum=sum+1;
					 }
					 if(aRow[2].toString().equalsIgnoreCase("false")){
						 sum=sum+1;
					 }
					 if(aRow[3].toString().equalsIgnoreCase("false")){
						 sum=sum+1;
					 }
					 if(aRow[4].toString().equalsIgnoreCase("false")){
						 sum=sum+1;
					 }
				    //sum = sum + Integer.parseInt(aRow[0].toString())+Integer.parseInt(aRow[1].toString())+Integer.parseInt(aRow[2].toString())+Integer.parseInt(aRow[3].toString())+Integer.parseInt(aRow[4].toString());
				 }
				 System.out.println("@"+sum);
				 return String.valueOf(sum);		
			}

		}
        return arr.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
    @RequestMapping(value="/survey/export/{id}/{stepid}",method=RequestMethod.POST)
	public boolean checklicense(@PathVariable long id, @PathVariable int stepid,HttpServletRequest req, @RequestBody String request, HttpServletResponse response) throws JSONException, DocumentException, Exception {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			FileInputStream fis = null;
			JSONObject obj= new JSONObject(request);
			MainAuditRegistration main = (MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+id+"'", "current");
			Path currentRelativePath = Paths.get("");
			String realpath = currentRelativePath.toAbsolutePath().toString();
			File file = null;
			
			System.out.println("url"+realpath+File.separator+main.getExcelurlplan());
			if(stepid==1){
				file = new File(realpath+File.separator+main.getExcelurlplan());
			}
			else{
				file = new File(realpath+File.separator+main.getExcelurlprocess());
			}
			if(!file.exists()){
				return false;
			}
			else{
				fis = new FileInputStream(file);
				
				Workbook workbook = WorkbookFactory.create(fis);
	        	FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        	    Sheet sh = workbook.getSheet(obj.getString("lavlagaa")); 
        	    
        	    if(sh!=null){
        	    	 String str="";
             	    if(obj.getInt("searchType")==1){
             	    	str=" >"+obj.getInt("t1");
             	    }
             	    if(obj.getInt("searchType")==2){
             	    	str=obj.getInt("t2")+"%";
             	    }
             	    if(obj.getInt("searchType")==3){
             	    	str=obj.getDouble("t3")+"%";
             	    }
             	    if(obj.getInt("searchType")==4){
             	    	str=obj.getInt("t4")+"ш";
             	    }
             	    if(obj.getInt("searchType")==5){
             	    	str=obj.getInt("t5")+"ш";
             	    }
             	    System.out.println(obj.getString("lavlagaa"));
             	    System.out.println(sh.getSheetName());
             	    Row currentRow = sh.getRow(3);
             	    if(currentRow!=null){
             			Cell cell1 = currentRow.getCell(3);
             			Cell cell2 = currentRow.getCell(8);    			
             			cell1.setCellValue(obj.getString("lavlagaa"));
             			cell2.setCellValue(obj.getLong("totalAccAmount"));
             	    }
             		List<FinJournal> fin=null;
             	    if(obj.has("ids")){
        				fin = (List<FinJournal>) dao.getHQLResult("from FinJournal t where t.stepid="+stepid+" and t.planid='"+obj.getInt("planid")+"' and t.id in("+obj.getString("ids")+")  order by t.id desc", "list");
        			}
        			else{
        				fin = (List<FinJournal>) dao.getHQLResult("from FinJournal t where t.stepid="+stepid+" and t.planid='"+obj.getInt("planid")+"' and (data8 like '"+obj.getInt("account")+"%') or (data9 like '"+obj.getInt("account")+"%') order by t.id desc", "list");
        			}
        			for(int i=13;i<sh.getLastRowNum()+1;i++){    			
        				Row r = sh.getRow(i);
        				if(r!=null){
        					sh.removeRow(r);   				
        				}					
        			}
         			Row row4 = sh.getRow(4);
          			Cell cell3 = row4.getCell(3);
          			Cell cell4 = row4.getCell(8);
          			System.out.println("##"+main.getOrgname());
          			//cell3.setCellValue(main.getOrgname());
          			CellValue cellValue = evaluator.evaluate(cell3);	    				                    	 
                	switch (cellValue.getCellTypeEnum()) {
                	    case STRING:	    				                    	    	
                	    	cell3.setCellType(CellType.STRING);
                	    	cell3.setCellValue(cellValue.getStringValue());
                	        break;
                	    case BOOLEAN:
                	        System.out.print(cellValue.getBooleanValue());
                	        break;
                	    case NUMERIC:
                	    	cell3.setCellType(CellType.NUMERIC);
                	    	cell3.setCellValue(cellValue.getNumberValue());
                	        break;
                	}
          			
          			
          			long rownum=14+fin.size();
          			String formula="sum(n14:n"+rownum+")";
          			System.out.println("sss"+formula);
          			cell4.setCellFormula(formula);
         			//cell4.setCellValue(obj.getLong("totalError"));
         			    			
         			Row row5 = sh.getRow(5);
          			Cell cell6 = row5.getCell(3);
          			Cell cell7 = row5.getCell(8);
          			cell6.setCellValue(obj.getString("dans"));
          			String formulaError="i5/i4*100";
          			cell7.setCellFormula(formulaError);
         			//cell7.setCellValue(obj.getDouble("errorPercentage"));
         			
         			Row row6 = sh.getRow(6);
          			Cell cell8 = row6.getCell(3);
          			Cell cell9 = row6.getCell(8);
          			cell8.setCellValue(obj.getInt("year"));
         			//cell9.setCellValue(obj.getString("totalAccAmount"));
          			CellValue cellValue8 = evaluator.evaluate(cell8);	    				                    	 
                	switch (cellValue8.getCellTypeEnum()) {
                	    case STRING:	    				                    	    	
                	    	cell8.setCellType(CellType.STRING);
                	    	cell8.setCellValue(cellValue8.getStringValue());
                	        break;
                	    case BOOLEAN:
                	        System.out.print(cellValue8.getBooleanValue());
                	        break;
                	    case NUMERIC:
                	    	cell8.setCellType(CellType.NUMERIC);
                	    	cell8.setCellValue(cellValue8.getNumberValue());
                	        break;
                	}
          			
          			
         			Row row7 = sh.getRow(7);
          			Cell cell10 = row7.getCell(3);
          			Cell cell11 = row7.getCell(8);
          			cell10.setCellValue(obj.getInt("psize"));
         			cell11.setCellValue(obj.getLong("totalAmount"));
         			
         			Row row8 = sh.getRow(8);
          			Cell cell12 = row8.getCell(3);
          			Cell cell13 = row8.getCell(8);
          			cell12.setCellValue(obj.getString("searchText")+" "+str);
          			String formulaharitsuulsan="i8*i6/100";
          			cell13.setCellFormula(formulaharitsuulsan);
         			//cell13.setCellValue(obj.getLong("totalAccError"));
         		
         			
         			List<LnkAuditProblem> bat= new ArrayList<LnkAuditProblem>();
         			List<LnkAuditProblem>  ap=  (List<LnkAuditProblem>) dao.getHQLResult("from LnkAuditProblem t where t.appid='"+id+"' and stepid=3 order by id", "list");
         			boolean chcker=false;
             	    for(int i=0;i<fin.size();i++){
             	    	
             	    	LnkAuditProblem pr = new LnkAuditProblem();
             	    	pr.setProblem(fin.get(i).getDescription());
             	    	pr.setAmount(fin.get(i).getAmount());
             	    	pr.setAcc(obj.getString("dans"));
     				
     					pr.setAppid(id);
     					pr.setStepid(3);
     					if(fin.get(i).getAmount()>0){
     						bat.add(pr);	
     					}
     				
             	    	
             	    	Row row14 =null;
             	    	if(sh.getRow(i+13)==null){
             	    		row14= sh.createRow(i+13);
             	    	}else{
             	    		row14= sh.getRow(i+13);
             	    	}
             	    	Cell c1 = null;
             	    	if(row14.getCell(0)==null){
             	    		c1=row14.createCell(0);
             	    	}
             	    	else{
             	    		c1=row14.getCell(0);
             	    	}
             	    	
             	    	Cell c2 = null;
             	    	if(row14.getCell(1)==null){
             	    		c2=row14.createCell(1);
             	    	}
             	    	else{
             	    		c2=row14.getCell(1);
             	    	}
             	    	
             	    	Cell c3 = null;
             	    	if(row14.getCell(2)==null){
             	    		c3=row14.createCell(2);
             	    	}
             	    	else{
             	    		c3=row14.getCell(2);
             	    	}
             	    	
             	    	Cell c4 = null;
             	    	if(row14.getCell(3)==null){
             	    		c4=row14.createCell(3);
             	    	}
             	    	else{
             	    		c4=row14.getCell(3);
             	    	}
             	    	
             	    	Cell c5 = null;
             	    	if(row14.getCell(4)==null){
             	    		c5=row14.createCell(4);
             	    	}
             	    	else{
             	    		c5=row14.getCell(4);
             	    	}
             	    	
             	    	Cell c6 = null;
             	    	if(row14.getCell(5)==null){
             	    		c6=row14.createCell(5);
             	    	}
             	    	else{
             	    		c6=row14.getCell(5);
             	    	}
             	    	
             	    	Cell c7 = null;
             	    	if(row14.getCell(6)==null){
             	    		c7=row14.createCell(6);
             	    	}
             	    	else{
             	    		c7=row14.getCell(6);
             	    	}
             	    	
             	    	Cell c8 = null;
             	    	if(row14.getCell(7)==null){
             	    		c8=row14.createCell(7);
             	    	}
             	    	else{
             	    		c8=row14.getCell(7);
             	    	}
             	    	
             	    	Cell c9 = null;
             	    	if(row14.getCell(8)==null){
             	    		c9=row14.createCell(8);
             	    	}
             	    	else{
             	    		c9=row14.getCell(8);
             	    	}
             	    	
             	    	Cell c10 = null;
             	    	if(row14.getCell(9)==null){
             	    		c10=row14.createCell(9);
             	    	}
             	    	else{
             	    		c10=row14.getCell(9);
             	    	}
             	    	
             	    	Cell c11 = null;
             	    	if(row14.getCell(10)==null){
             	    		c11=row14.createCell(10);
             	    	}
             	    	else{
             	    		c11=row14.getCell(10);
             	    	}
             	    	
             	    	Cell c12 = null;
             	    	if(row14.getCell(11)==null){
             	    		c12=row14.createCell(11);
             	    	}
             	    	else{
             	    		c12=row14.getCell(11);
             	    	}
             	    	
             	    	Cell c13 = null;
             	    	if(row14.getCell(12)==null){
             	    		c13=row14.createCell(12);
             	    	}
             	    	else{
             	    		c13=row14.getCell(12);
             	    	}
             	    	
             	    	Cell c14 = null;
             	    	if(row14.getCell(13)==null){
             	    		c14=row14.createCell(13);
             	    	}
             	    	else{
             	    		c14=row14.getCell(13);
             	    	}
             	    	
             	    	Cell c15 = null;
             	    	if(row14.getCell(14)==null){
             	    		c15=row14.createCell(14);
             	    	}
             	    	else{
             	    		c15=row14.getCell(14);
             	    	}
             	    	        	    	        	    	
             	    	c1.setCellValue(i+1);
             	    	c2.setCellValue(fin.get(i).getData1());
             	    	c3.setCellValue(fin.get(i).getData2());
             	    	c4.setCellValue(fin.get(i).getData16());
             	    	//c5.setCellValue(fin.get(i).getData4());
             	    	c6.setCellValue(fin.get(i).getData8());
             	    	c7.setCellValue(fin.get(i).getData9());
             	    	c8.setCellValue(fin.get(i).getData10());
             	    	if(fin.get(i).isA()){
             	    		c9.setCellValue(1);
             	    	}
             	    	else{
             	    		c9.setCellValue(0);
             	    	}
             	    	if(fin.get(i).isB()){
             	    		c10.setCellValue(1);
             	    	}
             	    	else{
             	    		c10.setCellValue(0);
             	    	}
             	    	if(fin.get(i).isC()){
             	    		c11.setCellValue(1);
             	    	}
             	    	else{
             	    		c11.setCellValue(0);
             	    	}
             	    	if(fin.get(i).isD()){
             	    		c12.setCellValue(1);
             	    	}
             	    	else{
             	    		c12.setCellValue(0);
             	    	}
             	    	if(fin.get(i).isE()){
             	    		c13.setCellValue(1);
             	    	}
             	    	else{
             	    		c13.setCellValue(0);
             	    	}
             	    	
             	    	c14.setCellValue(fin.get(i).getAmount());
             	    	c15.setCellValue(fin.get(i).getDescription());       	    	
             	    }
     		  
             	    dao.inserBatch(bat,"lnkAuditProblem",id);  
             	    
         			fis.close();
     				String uuid = UUID.randomUUID().toString()+".xlsx";
     	            FileOutputStream out = new FileOutputStream("upload-dir"+File.separator+id+ File.separator+uuid);
     	            workbook.write(out);
     				out.close();
     				if(file.exists()){
     					file.delete();
     				}
     				String SAVE_DIR = "upload-dir";
     				String furl = "/" + SAVE_DIR ;		
     				furl = File.separator  + SAVE_DIR + File.separator+id+File.separator + uuid;	
     				if(stepid==1){
     					main.setExcelurlplan(furl);
     				}
     				else{
     					main.setExcelurlprocess(furl);
     				}
     				
     				
     				LnkAuditForm laf = (LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.appid='"+id+"' and t.formid="+obj.getInt("formid")+"", "current");
     				
     				laf.setData6(1);
     				dao.PeaceCrud(laf, "LnkAuditForm", "update", (long) laf.getId(), 0, 0, null);	
     				
     				dao.PeaceCrud(main, "MainAuditRegistration", "update", (long) id, 0, 0, null);	
     				return true;
        	    }
        	   
			}
    		
		}
		return false;
	}
	
	 
  
	@RequestMapping(value = "/{action}/{domain}", method=RequestMethod.POST)
    public @ResponseBody String update(Model model,@RequestBody String jsonString, @PathVariable String action,@PathVariable String domain) throws JSONException,ClassCastException{
       System.out.println("json STR "+jsonString);	
       try{
    	   Class<?> classtoConvert;
		   JSONObject obj = new JSONObject(jsonString);    		
		   JSONObject resp= new JSONObject();
		   
		   String domainName=domain;
		   System.out.println(domain);
		   classtoConvert=Class.forName(domain);
		   Gson gson = new Gson();
		   Object object = gson.fromJson(obj.toString(),classtoConvert);	
		   
		   if(action.equals("update")){
			  
			   if(!obj.has("models")){
				   
				   		
				   if(domainName.equalsIgnoreCase("com.nbb.models.fn.MainAuditRegistration")){
					   
				    	  JSONObject str= new JSONObject(jsonString);
				    	  
				    	  MainAuditRegistration aw=(MainAuditRegistration) dao.getHQLResult("from MainAuditRegistration t where t.id='"+str.getInt("id")+"'", "current");
				    	
				    	  dao.PeaceCrud(object, domainName, "update", (long) str.getInt("id"), 0, 0, null);		
				    	  dao.PeaceCrud(null, "LnkMainUser", "delete", (long) str.getLong("id"), 0, 0, "appid");
				    	  JSONArray arr= str.getJSONArray("lnkUsers");
				    	  for(int i=0;i<arr.length();i++){
				    		  LnkMainUser la= new LnkMainUser(); 
				    		  la.setUserid(Long.parseLong(arr.get(i).toString()));
				    		  la.setAppid(aw.getId());
				    		  dao.PeaceCrud(la, "LnkMainUser", "save", (long) 0, 0, 0, null);	
				    	  }
				   }
				   else if(domainName.equalsIgnoreCase("com.nbb.models.fn.LnkAuditForm")){
					   
				    	  JSONObject str= new JSONObject(jsonString);
				    	  
				    	  LnkAuditForm aw=(LnkAuditForm) dao.getHQLResult("from LnkAuditForm t where t.id='"+str.getInt("id")+"'", "current");
				    	
				    	  dao.PeaceCrud(object, domainName, "update", (long) str.getInt("id"), 0, 0, null);		
				    	
				    	  if(aw.getParentid()==null){
				    		  if(str.getInt("stepid")==0 || str.getInt("stepid")==5){
				    			  dao.getNativeSQLResult("update  lnk_audit_forms set stepid="+str.getInt("stepid")+", levelid="+str.getInt("levelid")+", data14=1  where appid="+aw.getAppid()+" and parentid="+aw.getFormid()+"", "update");
				    		  }else{
				    			  dao.getNativeSQLResult("update  lnk_audit_forms set stepid="+str.getInt("stepid")+", levelid="+str.getInt("levelid")+", data14=0  where appid="+aw.getAppid()+" and parentid="+aw.getFormid()+"", "update");
				    		  }
				    	  }
				   }
				   else{
				    	  int id=(int)obj.getInt("id");
						  dao.PeaceCrud(object, domainName, "update", (long) id, 0, 0, null);
				   }			 
			   }  
			   
			   else{
				   JSONArray rs=(JSONArray) obj.get("models");
				   System.out.println("rs obj "+rs);
				   for(int i=0;i<rs.length();i++){
					   String str=rs.get(i).toString();
					   JSONObject batchobj= new JSONObject(str);  
					   Object bobj = gson.fromJson(batchobj.toString(),classtoConvert);
					   int upid=batchobj.getInt("id");					   
					   dao.PeaceCrud(bobj, domainName, "update", (long) upid, 0, 0, null); 					  
				   }
				  
			   }
		   }
		   else if(action.equals("delete")){
			   
			   if(domainName.equalsIgnoreCase("com.netgloo.models.LutDepartment")){
				 			   			   
			   }
			   else{
				   dao.PeaceCrud(object, domainName, "delete", obj.getLong("id"), 0, 0, null);	
			   }
			   		  
		   }
		   else if(action.equals("create")){
			   
			   if(domainName.equalsIgnoreCase("com.nbb.models.fn.MainAuditRegistration")){
				   
			    	  JSONObject str= new JSONObject(jsonString);
			    	  
			    	  List<MainAuditRegistration> aw=(List<MainAuditRegistration>) dao.getHQLResult("from MainAuditRegistration t where t.autype="+str.getInt("autype")+" and t.audityear='"+str.getInt("audityear")+"' and t.regnum="+str.getInt("regnum")+"", "list");
			    	  
			    	  if(aw.size()==0){
			    		  MainAuditRegistration ma= new MainAuditRegistration();
				    	  ma.setGencode(str.getString("gencode"));
				    	  ma.setOrgname(str.getString("orgname"));
				    	  ma.setRegnum(str.getInt("regnum"));
				    	  ma.setAuditname(str.getString("auditname"));
				    	  ma.setDepid(str.getInt("depid"));
				    	  ma.setDirector(str.getString("director"));
				    	  ma.setManager(str.getString("manager"));
				    	  ma.setChpos(str.getString("chpos"));
				    	  ma.setChname(str.getString("chname"));
				    	  ma.setDpos(str.getString("dpos"));
				    	  ma.setApos(str.getString("apos"));
				    	  ma.setAname(str.getString("aname"));
				    	  ma.setAudityear(str.getInt("audityear"));
				    	  ma.setOrgtype(str.getLong("orgtype"));
				    	  ma.setAutype(str.getInt("autype"));
				    	  ma.setIsenabled(true);
				    	  ma.setIsactive(true);
				    	  dao.PeaceCrud(ma, domainName, "save", (long) 0, 0, 0, null);		
				    	  
				    	  LutDepartment dp=(LutDepartment) dao.getHQLResult("from LutDepartment t where t.id='"+str.getInt("depid")+"'", "current");
				    	  dp.setAuditCount(dp.getAuditCount()-1);
				    	  dao.PeaceCrud(dp, "LutDepartment", "update", (long) dp.getId(), 0, 0, null);		
				    	  
				    	  
				    	  List<LutUser> us=(List<LutUser>) dao.getHQLResult("from LutUser t where t.username='"+str.getInt("regnum")+"'", "list");
				    	  if(us.size()==0){
				    		  LutUser usr= new LutUser();
				    		  usr.setUsername(String.valueOf(str.getInt("regnum")));
				    		  usr.setPassword(passwordEncoder.encode(String.valueOf(str.getInt("regnum"))));
				    		  usr.setIsactive(true);
				    		  usr.setIscompany(true);
				    		  usr.setPositionid(7);
				    		  if(str.getInt("autype")==3 || str.getInt("autype")==4){
				    			  usr.setAutype(2);
				    		  }
				    		  else{
				    			  usr.setAutype(1);
				    		  }
				    		  usr.setDepartmentid(str.getInt("depid"));
				    		  dao.PeaceCrud(usr, "com.nbb.models.LutUser", "save", (long) 0, 0, 0, null);		
				    		  
				    		  List<LutRole> rl=(List<LutRole>) dao.getHQLResult("from LutRole t where upper(t.roleauth)=upper('Role_customer')", "list");
				    		  if(rl.size()>0){
				    			  LnkUserrole rusr=new LnkUserrole();	
								  rusr.setRoleid(rl.get(0).getId());
								  rusr.setUserid(usr.getId());
								  dao.PeaceCrud(rusr, "LnkUserrole", "save",  (long) 0, 0, 0, null);	
				    		  }
				    		  
				    		  LnkMainUser la= new LnkMainUser(); 
				    		  la.setUserid(usr.getId());
				    		  la.setAppid(ma.getId());
				    		  dao.PeaceCrud(la, "LnkMainUser", "save", (long) 0, 0, 0, null);	
				    		
				    	  }else{
				    		  LnkMainUser la= new LnkMainUser(); 
				    		  la.setUserid(us.get(0).getId());
				    		  la.setAppid(ma.getId());
				    		  dao.PeaceCrud(la, "LnkMainUser", "save", (long) 0, 0, 0, null);	
				    	  }
				    	  
				    	  
				    	  List<LutForm> lf=(List<LutForm>) dao.getHQLResult("from LutForm t where t.data11="+str.getInt("autype")+" order by t.orderid asc", "list");
				    	  List<LnkAuditForm> larr = new ArrayList<LnkAuditForm>();
				    	  for(LutForm item:lf){
				    		  LnkAuditForm la= new LnkAuditForm();
				    		  la.setAppid(ma.getId());
				    		  la.setData1(item.getData1());
				    		  la.setData2(item.getData2());
				    		  la.setData3(item.getData3());
				    		  la.setData4(0);
				    		  la.setData5(item.isData5());
				    		  la.setData6(0);
				    		  la.setData7(item.getData7());
				    		  la.setData8(item.getData8());
				    		  la.setData10(item.isData10());
				    		  la.setData13(item.getData13());
				    		  la.setFormid(item.getId());
				    		  la.setParentid(item.getParentid());
				    		  la.setOrderid(item.getOrderid());
				    		  larr.add(la);
				    	  }
				    	  dao.inserBatch(larr, "LnkAuditForm", ma.getId());
				    	 
				    	  if(str.has("lnkUsers")){
				    		  JSONArray arr= str.getJSONArray("lnkUsers");
				    		  for(int i=0;i<arr.length();i++){
					    		  LnkMainUser la= new LnkMainUser(); 
					    		  la.setUserid(Long.parseLong(arr.get(i).toString()));
					    		  la.setAppid(ma.getId());
					    		  dao.PeaceCrud(la, "LnkMainUser", "save", (long) 0, 0, 0, null);	
					    	  }
				    	  }
				    	  
				    	  return "true";
			    	  }
			    	  
			    	  else{
			    		  return "false";
			    	  }
			   }

			    else{
			    	dao.PeaceCrud(object, domainName, "save", (long) 0, 0, 0, null);			   
			    }
			   
		   }		  
		   return "true";
       }
       catch(Exception  e){
    	   e.printStackTrace();
    		return null;
       }
      
    }
}
