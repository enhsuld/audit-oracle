package com.nbb.controllers;

import java.io.IOException;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nbb.dao.UserDao;
import com.nbb.models.CustomUserDetails;
import com.nbb.models.LnkUserrole;
import com.nbb.models.LutRole;
import com.nbb.models.LutUser;
import com.nbb.models.fn.LnkAuditForm;
import com.nbb.models.fn.LnkAuditFormComment;
import com.nbb.models.fn.LnkMainUser;
import com.nbb.models.fn.LutForm;
import com.nbb.models.fn.MainAuditRegistration;
import com.nbb.repository.LnkMenuRepository;
import com.nbb.repository.UserRepository;
import com.nbb.services.RestfulClient;
import com.nbb.services.Services;
import com.nbb.services.SmtpMailSender;
import com.nbb.services.UserService;


@RestController
public class MainController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
    private UserDao dao;
	
	@Autowired
	Services services;
	
    @Autowired
    private UserRepository upo;
    
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@RequestMapping(value="/user", method = RequestMethod.GET)
	public Principal user(Principal pr) {		
        return pr;      
	}
	

	@RequestMapping(value="refreshData", method = RequestMethod.GET)
	public String sda(Principal pr) {		
		
		RestfulClient restfulClient = new RestfulClient("admin", "sgt123");
		/**
		 * GET ENTITY
		 */
		String jsonStr=restfulClient.getEntity(pr.getName());
		
		JSONArray arr = new JSONArray(jsonStr);
		
		/*for(int i=0;i<arr.length();i++){
			JSONObject str=(JSONObject) arr.get(i);
			System.out.println(str.getString("gencode"));
			
			MainAuditRegistration ma= new MainAuditRegistration();
	    	ma.setGencode(str.getString("gencode"));
	    	ma.setOrgname(str.getString("orgname"));
	    	ma.setRegnum(str.getInt("regnum"));
	    	//ma.setAuditname(str.getString("auditname"));
	    	ma.setDepid(str.getInt("depid"));
	    	ma.setDirector(str.getString("director"));
	    	ma.setManager(str.getString("manager"));
	    	ma.setChpos(str.getString("chpos"));
	    	ma.setChname(str.getString("chname"));
	    	ma.setDpos(str.getString("dpos"));
	    	ma.setApos(str.getString("apos"));
	    	ma.setAname(str.getString("aname"));
	    	//ma.setAudityear(str.getInt("audityear"));
	    	ma.setOrgtype(str.getLong("orgtype"));
	    	ma.setAutype(str.getInt("autype"));
	    	ma.setIsenabled(true);
	    	ma.setIsactive(true);
	    	dao.PeaceCrud(ma, "MainAuditRegistration", "save", (long) 0, 0, 0, null);	
		}*/
		
        return jsonStr;      
	}
	
	@RequestMapping(value = "/changeUserPassword", method = RequestMethod.POST)
	public boolean changeUserPassword(@RequestBody String jsonStr) {
		JSONObject jsonObj = new JSONObject(jsonStr);
		UserDetails userDetail = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LutUser loguser=(LutUser) dao.getHQLResult("from LutUser t where t.username='"+userDetail.getUsername()+"'", "current");
		if (loguser != null && jsonObj.has("old_password") && jsonObj.has("new_password") && jsonObj.has("new_password_confirm") && jsonObj.getString("new_password").equals(jsonObj.getString("new_password_confirm"))){
			if (passwordEncoder.matches(jsonObj.getString("old_password"), loguser.getPassword())){		
				loguser.setPassword(passwordEncoder.encode(jsonObj.getString("new_password")));
				dao.PeaceCrud(loguser, "LutUser", "save", (long) 0, 0, 0, null);
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(value = "/api/twitter/{formid}/{appid}", method = RequestMethod.GET)
	public String comments(@PathVariable int formid,@PathVariable int appid) {
		List<LnkAuditFormComment> coms=(List<LnkAuditFormComment>) dao.getHQLResult("from LnkAuditFormComment t where t.formid='"+formid+"' and t.appid="+appid+" order by t.id desc", "list");
		JSONArray arr=new JSONArray();
		for(LnkAuditFormComment item:coms){
			JSONObject obj=new JSONObject();
			obj.put("username", item.getUsername());
			obj.put("createdDate", item.getCreateDate());
			obj.put("comment", item.getComtext());
			arr.put(obj);
		}
		return arr.toString();
	}
	
	@RequestMapping(value = "/api/twitter", method = RequestMethod.POST)
	public boolean twitter(@RequestBody String jsonStr) {
		JSONObject jsonObj = new JSONObject(jsonStr);
		LutUser loguser=(LutUser) dao.getHQLResult("from LutUser t where t.id='"+jsonObj.getInt("userid")+"'", "current");
		if (loguser!=null){
			LnkAuditFormComment cm=new LnkAuditFormComment();
			cm.setComtext(jsonObj.getString("comment"));
			if(loguser.getFamilyname()!=null && loguser.getGivenname()!=null){
				cm.setUsername(loguser.getFamilyname().substring(0, 1)+"."+loguser.getGivenname());
			}
			else{
				cm.setUsername(loguser.getUsername());
			}
			Date d1 = new Date();
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
			String special = df.format(d1);
			cm.setFormid(jsonObj.getInt("formid"));
			cm.setAppid(jsonObj.getInt("appid"));
			cm.setCreateDate(special);
			dao.PeaceCrud(cm, "LnkAuditFormComment", "save", (long) 0, 0, 0, null);		
			return true;
		}
		return false;
	}
	
	@GetMapping(value = "/api/user")
    public LutUser apiuser(Principal pr){
        return userService.getUser(pr.getName());
    }
	
	@RequestMapping(value="/userDetail", method = RequestMethod.GET)
	public CustomUserDetails userDetail(Principal pr) {		
		LutUser user = upo.findByUserName(pr.getName());
        return user != null ? new CustomUserDetails(user) : null;        
	}
	    
	
	@RequestMapping(value = "/api/work/{mid}", method=RequestMethod.POST)
    public @ResponseBody String update(Model model,@RequestBody String jsonString, @PathVariable long mid) throws JSONException,ClassCastException{
       System.out.println("json STR "+jsonString);	
       try{
    	   Class<?> classtoConvert;
		   JSONObject obj = new JSONObject(jsonString);    	
		   classtoConvert=Class.forName("com.nbb.models.fn.MainAuditRegistration");
		   Gson gson = new Gson();
		   Object object = gson.fromJson(obj.toString(),classtoConvert);	
		   JSONObject str= new JSONObject(jsonString);
    	   dao.PeaceCrud(object, "MainAuditRegistration", "update", (long) str.getInt("id"), 0, 0, null);			  
		   return "true";
       }
       catch(Exception  e){
    	   e.printStackTrace();
    		return null;
       }
      
    }   

}
