package com.moneyview.los.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
	private String authServiceUrl = "http://localhost:8080";
	private String identityServiceUrl = "http://localhost:8080";
	private String communicationServiceUrl = "http://localhost:8080";
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
			String url = authServiceUrl + "/authenticateUser";
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, "Application rejected, User not of age", String.class);
		}
		retrieveUserDetails(user);

		postBanAddress(user);

		//call retriveUserDetails(user) from LoanApplicationController
		//post ban and address
		//calculateAge
		if(checkAge(calculateAge(identityServiceEntity.getDob()))) {
			//age
			String url = communicationServiceUrl + "/closeApplication";
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, "Application rejected, User not of age", String.class);
			//call CommunicationService - Application rejected, User not of age
		}

		//checkPanUserStatus
		if(checkLoanStatus(loanApplicationService.checkUserPanStatus(user.getUserId()).get(0))) {
			String url = communicationServiceUrl + "/closeApplication";
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, "Application rejected, User not of age", String.class);
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

		String url = authServiceUrl + "/verifyToken";

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, user, String.class);

		HttpStatus statusCode = responseEntity.getStatusCode();

		if(statusCode.value()==200) {
			isValid=true;
		}


		return isValid;
	}


	public ResponseEntity<ApiResponse> retrieveUserDetails(UserEntity user) {
		String identityServiceUrl = "http://localhost:8080";
		String url = identityServiceUrl + "/retrieveUserDetails?service=1&id="+user.getUserId();

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

	public static boolean checkLoanStatus(LoanApplicationStatus loanStatus)
	{
		/*
		       Applied - Need to Change Loan Status
		 */

		if(loanStatus == LoanApplicationStatus.OPEN)
		{
			return false;
		}
		else if(loanStatus == LoanApplicationStatus.CLOSE)
		{
			//user can re-apply now because the loan is closed.
			return true;
		}
		else if (loanStatus == LoanApplicationStatus.UNDER_REVIEW)
		{
			//something fishy
			//Hold On
			return true;

		}
		else if (loanStatus == LoanApplicationStatus.APPLIED)
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
