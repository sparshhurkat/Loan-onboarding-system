package com.moneyview.los.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moneyview.los.constants.LoanApplicationStatus;
import com.moneyview.los.constants.PartnerConstants;
import com.moneyview.los.model.ApiResponse;
import com.moneyview.los.model.BanAddress;
import com.moneyview.los.model.IdentityServiceEntity;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.model.PartnerEntity;
import com.moneyview.los.model.ResponseBuilder;
import com.moneyview.los.model.UserEntity;
import com.moneyview.los.service.LoanApplicationService;

@RestController
public class UserController {

	RestTemplate restTemplate= new RestTemplate();
	private String authServiceUrl = "10.70.4.172:8080/validateToken";
	private String idServicePostUrl = "http://localhost:8080/api/user/'10'?serviceId=los";
	private String idServiceGetUrl = "http://localhost:8080/api/user/'10'?serviceId=los";
	private String communicationServiceUrl = "http://localhost:8080/closeApplication";
	LoanApplicationEntity loanApplicationEntity = new LoanApplicationEntity();
	IdentityServiceEntity identityServiceEntity = new IdentityServiceEntity();

	@Autowired
	private LoanApplicationService loanApplicationService;


	//get userid,authtoken,cibilscore,empstatus,address & BAN from user
	//send userid and authtoken to auth service to check for validity
	@PostMapping("/validateUser")
	public ResponseEntity<ApiResponse> validateUser(@RequestBody UserEntity user){

		
		if(!authTokenisValid(user)) {
			//callAuthService - invalid auth token
			String url = "10.70.4.172:8080/validateToken";
			HashMap<String,Object> hashmap = new HashMap<>();
			hashmap.put("auth_token" , user.getAuthToken());
			hashmap.put("user_id" , user.getUserId());
			ResponseEntity<String> responseEntity = restTemplate.postForObject(authServiceUrl,hashmap, ResponseEntity.class);
			System.out.println(responseEntity.getBody());
		}
		
		
		//get details from identity service
		retrieveUserDetails(user);

		//post ban and address
		postBanAddress(user);
		
		//calculateAge
		if(checkAge(calculateAge(identityServiceEntity.getDob()))) {
			//call CommunicationService - Application rejected, User not of age
			String url = "10.70.4.178:8180/sendSMS";
			HashMap<String,Object> hashmap = new HashMap<>();
			HashMap<String,Object> details = new HashMap<>();
			hashmap.put("requestType" , "APPR");
			details.put("userID" , identityServiceEntity.getUserId());
			details.put("reason" , "Application rejected, User not of age");
			hashmap.put("details" , details);
			ResponseEntity<String> responseEntity = restTemplate.postForObject(url,hashmap, ResponseEntity.class);
			System.out.println(responseEntity.getBody());
			
		}

		
		//checkPanUserStatus
		if(checkLoanStatus(loanApplicationService.checkUserPanStatus(user.getUserId()).get(0))) {
			//call CommunicationService - Application rejected, User alreaady has active loan
			String url = "10.70.4.178:8180/sendSMS";
			HashMap<String,Object> hashmap = new HashMap<>();
			HashMap<String,Object> details = new HashMap<>();
			hashmap.put("requestType" , "APPR");
			details.put("userID" , identityServiceEntity.getUserId());
			details.put("reason" , "Application rejected, User has active loan");
			hashmap.put("details" , details);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, "Application rejected, User alreaady has active loan", String.class);
		}


		//partnerOnboarding
		PartnerEntity partnerEntity= fetchPartnerToOnboard(loanApplicationEntity.getCibilScore(), loanApplicationEntity.isEmpStatus());


		Map<String, Double> responseBody = new HashMap<>();
		responseBody.put("Lower limit", partnerEntity.getLowLimit());
		responseBody.put("Upper limit", partnerEntity.getUpperLimit());

		return ResponseBuilder.buildResponse(200,"Hello", responseBody);
	}


	//send POST request with user entity to auth service
	//if status code is ok, return boolean isValid=true
	public boolean authTokenisValid(UserEntity user) {
		boolean isValid = false;

		String url = authServiceUrl;

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, user, String.class);

		HttpStatus statusCode = responseEntity.getStatusCode();

		if(statusCode.value()==200) {
			isValid=true;
		}


		return isValid;
	}


	public ResponseEntity<ApiResponse> retrieveUserDetails(UserEntity user) {
		String url = "http://localhost:8080/"+user.getUserId()+"?serviceId=los";

		ResponseEntity<IdentityServiceEntity> responseEntity = restTemplate.getForEntity(url, IdentityServiceEntity.class);


		loanApplicationEntity.setFirstName(identityServiceEntity.getFirstName());
		loanApplicationEntity.setLastName(identityServiceEntity.getLastName());
		loanApplicationEntity.setDob(identityServiceEntity.getDob());
		loanApplicationEntity.setPan(identityServiceEntity.getPan());
		loanApplicationEntity.setAadhar(identityServiceEntity.getAadhar());
		loanApplicationEntity.setEmail(identityServiceEntity.getEmail());
		loanApplicationEntity.setPhoneNumber(identityServiceEntity.getPhoneNumber());



		loanApplicationEntity.setUserId(user.getUserId());
		loanApplicationEntity.setCibilScore(user.getCibilScore());
		loanApplicationEntity.setEmpStatus(user.isEmpStatus());
		loanApplicationEntity.setBankAccountNumber(user.getBankAccountNumber());
		loanApplicationEntity.setAddress(user.getAddress());
		//user(user details from user entity)+userDetails(response body from identity service)+loanAmount

		return ResponseBuilder.buildResponse(200, "Successfully recieved");
	}

	public ResponseEntity<ApiResponse> postBanAddress(UserEntity user) {
		String identityServiceUrl = "http://localhost:8080";
		String url = identityServiceUrl + "/addBanAddress";
		BanAddress banAddress= new BanAddress();


		ResponseEntity<IdentityServiceEntity> responseEntity = restTemplate.postForEntity(url, banAddress, IdentityServiceEntity.class);

		return ResponseBuilder.buildResponse(responseEntity.getStatusCodeValue(), "Successfully posted");
	}


	public static int calculateAge(LocalDate dob) {
		LocalDate currentDate = LocalDate.now();
		if ((dob != null) && (currentDate != null)) {
			return Period.between(dob, currentDate).getYears();
		} else {
			return 0;
		}

	}
	public static boolean checkAge(int age)
	{
		// Check age within range to display.
		if(age < 18 && age >60)
		{
			return true;
		}
		return false;
	}

	public static boolean checkLoanStatus(String loanStatus)
	{
		/*
		       Applied - Need to Change Loan Status
		 */

		if(loanStatus == "open")
		{
			return false;
		}
		else if(loanStatus == "close")
		{
			//user can re-apply now because the loan is closed.
			return true;
		}
		else if (loanStatus == "under_review")
		{
			//something fishy
			//Hold On
			return true;

		}
		else if (loanStatus == "under_review")
		{
			return true;

		}
		return true;
	}

	public static PartnerEntity fetchPartnerToOnboard(int cibilScore, boolean empStatus)
	{

		if(cibilScore< 600 && empStatus)
		{
			return PartnerConstants.getPartnerModelList().get(0);
		}

		else if(cibilScore>=600 && cibilScore<=700 && !empStatus)
		{
			return PartnerConstants.getPartnerModelList().get(1);
		}
		else if(cibilScore>=600 && cibilScore<=700 && empStatus)
		{
			return PartnerConstants.getPartnerModelList().get(2);
		}
		else if(cibilScore>=700 && cibilScore<=800 &&  !empStatus)
		{
			return PartnerConstants.getPartnerModelList().get(3);
		}
		else if(cibilScore>=700 && cibilScore<800 && empStatus)
		{

			return PartnerConstants.getPartnerModelList().get(4);
		}
		//(cibilScore>800)
		else
		{
			return  PartnerConstants.getPartnerModelList().get(5);
		}

	}

}
