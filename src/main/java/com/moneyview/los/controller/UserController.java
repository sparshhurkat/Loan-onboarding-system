package com.moneyview.los.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moneyview.los.model.UserEntity;

@RestController
public class UserController {
	
	RestTemplate restTemplate= new RestTemplate();
	private String authServiceUrl = "http://localhost:8080";
	
	
	//get userid,authtoken,cibilscore,empstatus,address & BAN from user
	//send userid and authtoken to auth service to check for validity
	@PostMapping("/validateUser")
    public void validateUser(@RequestBody UserEntity user){
		if(authTokenisValid(user)) {
			//call retriveUserDetails(user) from LoanApplicationController
			
		}
    }
	
	
	//send POST request with user entity to auth service
	//if status code is ok, return boolean isValid=true
	public boolean authTokenisValid(UserEntity user) {
		boolean isValid = false;
		
		String url = authServiceUrl + "/verifyToken";
		
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, user, String.class);

        HttpStatus statusCode = responseEntity.getStatusCode();
        System.out.println("status code - " + statusCode);
        String userDetails = responseEntity.getBody();
        System.out.println("response body - " + userDetails);
        HttpHeaders responseHeaders = responseEntity.getHeaders();
        System.out.println("response Headers - " + responseHeaders);
        URI uri = restTemplate.postForLocation(url, user, String.class);
        System.out.println("uri - " + uri);
        
        if(statusCode.value()==200) {
        	isValid=true;
        }
        
        
        return isValid;
	}
	
	
}
