package com.moneyview.los.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moneyview.los.constants.PartnerConstants;
import com.moneyview.los.model.ApiResponse;
import com.moneyview.los.model.ApiResponseCheckUser;
import com.moneyview.los.model.IdentityServiceEntity;
import com.moneyview.los.model.LoanAmountEntity;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.model.PartnerEntity;
import com.moneyview.los.model.PostBanAddress;
import com.moneyview.los.model.ResponseBuilder;
import com.moneyview.los.model.UserEntity;
import com.moneyview.los.service.LoanApplicationService;

@RestController
public class UserController {

	RestTemplate restTemplate= new RestTemplate();
	private String authServiceUrl = "http://localhost:8080/validateToken";
	private String idServicePostUrl = "http://localhost:8080/api/user/postBankAndAddress";
	private String communicationServiceUrl = "http://localhost:8080/sendSMS";
	LoanApplicationEntity loanApplicationEntity = new LoanApplicationEntity();
	IdentityServiceEntity identityServiceEntity = new IdentityServiceEntity();

	@Autowired
	private LoanApplicationService loanApplicationService;


	//get userid,authtoken,cibilscore,empstatus,address & BAN from user
	@PostMapping("/validateUser")
	public ResponseEntity<ApiResponse> validateUser(@RequestBody UserEntity user){

		
		int authCode=authTokenStatusCode(user.getAuthToken(),user.getUserId());
		
		if(authCode!=200){
			return ResponseBuilder.buildResponse(authCode,"Auth Error");
		}


		//get details from identity service
		int ISGetCode=retrieveUserDetails(user.getUserId());
		
		if(ISGetCode!=200){
			return ResponseBuilder.buildResponse(ISGetCode,"Error getting User details");
		}
		
		loanApplicationEntity.setUserId(user.getUserId());
		loanApplicationEntity.setCibilScore(user.getCibilScore());
		loanApplicationEntity.setEmpStatus(user.isEmpStatus());
		loanApplicationEntity.setBankAccountNumber(user.getBankAccountNumber());
		loanApplicationEntity.setAddress(user.getAddress());

		//post ban and address
		int ISPostCode=postBanAddress(user.getUserId(),user.getBankAccountNumber(),user.getAddress());
		if(ISPostCode!=200){
			return ResponseBuilder.buildResponse(ISPostCode,"Error posting User details");
		}
		
		//calculateAge
		if(checkAge(calculateAge(identityServiceEntity.getDob()))) {
			//call CommunicationService - Application rejected, User not of age
			HashMap<String,Object> hashmap = new HashMap<>();
			HashMap<String,Object> details = new HashMap<>();
			hashmap.put("requestType" , "APPR");
			details.put("userID" , identityServiceEntity.getUserId());
			details.put("reason" , "User not of age");
			hashmap.put("details" , details);
			HashMap<String,String> responseEntity = restTemplate.postForObject(communicationServiceUrl,hashmap, HashMap.class);
			
			
			int communicationCode=Integer.parseInt(responseEntity.get("statusCode"));
			if(communicationCode!=200){
				return ResponseBuilder.buildResponse(communicationCode,"Error sending rejection to communication service");
			}
		}


		//checkPanUserStatus
		if(checkLoanStatus(loanApplicationService.checkUserPanStatus(user.getUserId()).get(0))) {
			//call CommunicationService - Application rejected, User alreaady has active loan
			HashMap<String,Object> hashmap = new HashMap<>();
			HashMap<String,Object> details = new HashMap<>();
			hashmap.put("requestType" , "APPR");
			details.put("userID" , identityServiceEntity.getUserId());
			details.put("reason" , "User has active loan");
			hashmap.put("details" , details);
			HashMap<String,String> responseEntity = restTemplate.postForObject(communicationServiceUrl, hashmap, HashMap.class);
			
			int communicationCode=Integer.parseInt(responseEntity.get("statusCode"));
			if(communicationCode!=200){
				return ResponseBuilder.buildResponse(communicationCode,"Error sending rejection to communication service");
			}
		}


		//partnerOnboarding
		PartnerEntity partnerEntity= fetchPartnerToOnboard(loanApplicationEntity.getCibilScore(), loanApplicationEntity.isEmpStatus());


		HashMap<String, Double> responseBody = new HashMap<>();
		responseBody.put("Lower limit", partnerEntity.getLowLimit());
		responseBody.put("Upper limit", partnerEntity.getUpperLimit());
		
		loanApplicationEntity.setLoanStatus("PROCESSING");
		
		loanApplicationService.saveLoanApplication(loanApplicationEntity);

		return ResponseBuilder.buildResponse(200, "API works", responseBody);
		//save to db here
	}

	//get loan amount from user and save to database along with all the other details
	
	@PostMapping("/openLoanApplication")
	public ResponseEntity<ApiResponse> submitLoanApplication(@RequestBody LoanAmountEntity loanAmountEntity){
		//update here
		LocalDate currentDate = LocalDate.now();
		loanApplicationEntity.setRequestedAmount(loanAmountEntity.getLoanAmount());
		loanApplicationEntity.setLoanStatus("OPEN");
		loanApplicationEntity.setLoanAppliedDate(currentDate);
		return ResponseBuilder.buildResponse(200,"Loan application opened successfully", loanApplicationService.updateLoanApplication(loanApplicationEntity.getLoanId(), "OPEN"));
	}


	//send POST request with user entity to auth service
	//if status code is ok, return boolean isValid=true
	public int authTokenStatusCode(String authToken,long userId) {
		boolean isValid = false;
		HashMap<String,String> hashmap = new HashMap<>();
		hashmap.put("auth_token" , authToken);
		hashmap.put("user_id" , String.valueOf(userId));

		HashMap<String,String> responseEntity = restTemplate.postForObject(authServiceUrl,hashmap, HashMap.class);

//		return Integer.parseInt(responseEntity.get("statusCode"));
		return 200;
	}


	public int retrieveUserDetails(long userId) {
		//TODO:cross verify
		String url = "http://192.168.171.210:8080/api/user/" +userId+ "?serviceId=los";

		//HashMap<String,Object> hashmap = new HashMap<>();
		HashMap<String,String> identityServiceEntity = restTemplate.getForObject(url, HashMap.class);
		
		//HashMap<String, String> responseEntity = restTemplate.getForEntity(url, HashMap.class);

		loanApplicationEntity.setFirstName(identityServiceEntity.get("fName"));
		loanApplicationEntity.setLastName(identityServiceEntity.get("lName"));
		loanApplicationEntity.setDob(identityServiceEntity.get("dob"));
		loanApplicationEntity.setPan(identityServiceEntity.get("pan"));
		loanApplicationEntity.setAadhar(identityServiceEntity.get("adhaar"));
		loanApplicationEntity.setEmail(identityServiceEntity.get("email"));
		loanApplicationEntity.setPhoneNumber(identityServiceEntity.get("mobile"));
		
		
		return Integer.parseInt(identityServiceEntity.get("statusCode"));
	}

	public int postBanAddress(long userId, String ban, String address) {
		
		PostBanAddress pba=new PostBanAddress(address, ban, userId);
		ApiResponseCheckUser responseEntity = restTemplate.postForObject(idServicePostUrl, pba, ApiResponseCheckUser.class);
		return responseEntity.getStatus();
	}


	public int calculateAge(LocalDate dob) {
		LocalDate currentDate = LocalDate.now();
		if ((dob != null) && (currentDate != null)) {
			return Period.between(dob, currentDate).getYears();
		} else {
			return 0;
		}

	}
	public boolean checkAge(int age)
	{
		// Check age within range to display.
		if(age < 18 && age >60)
		{
			return true;
		}
		return false;
	}

	public boolean checkLoanStatus(String loanStatus)
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

	public PartnerEntity fetchPartnerToOnboard(int cibilScore, boolean empStatus)
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
