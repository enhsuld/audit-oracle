package com.nbb.services;

import java.security.Principal;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.nbb.configs.RestTemplateBuilder;
import com.nbb.models.fn.MainAuditRegistration;




public class RestfulClient {
	
	RestTemplate restTemplate;
	
	public RestfulClient(String username, String password){
		restTemplate = RestTemplateBuilder.securityRestTemplateBuilder(username, password);
	}
	

	/**
	 * POST ENTITYs
	 */
/*	public void postEntity(){
	    System.out.println("Begin /POST request!");
	        // replace http://localhost:8080 by your restful services
	    String postUrl = "http://localhost:8080/post";
	    Customer customer = new Customer(123, "Jack", 23);
	    ResponseEntity<String> postResponse = restTemplate.postForEntity(postUrl, customer, String.class);
	    System.out.println("======================================Response for Post Request: " + postResponse.getBody());
	}*/
	
	/**
	 * GET ENTITY
	 *  restTemplate.getForEntity(getUrl, MainAuditRegistration[].class);
	 * 
	 * ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(urlGETList, Object[].class);
Object[] objects = responseEntity.getBody();
MediaType contentType = responseEntity.getHeaders().getContentType();
HttpStatus statusCode = responseEntity.getStatusCode();
	 */
	public String getEntity(String username){
	    System.out.println("Begin /GET request!"+username);
	    String getUrl = "http://localhost:80/au/get?username='"+username+"'";
	    ResponseEntity<String> postResponse = restTemplate.getForEntity(getUrl, String.class);
	    System.out.println("======================================Response for Get Request: " + postResponse.getBody());
	    return postResponse.getBody();
	   
	}
}
