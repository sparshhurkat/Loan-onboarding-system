package com.moneyview.los.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moneyview.los.model.ApiResponse;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.model.PartnerEntity;
import com.moneyview.los.model.ResponseBuilder;
import com.moneyview.los.service.LoanApplicationService;

@RestController
public class LoanApplicationController {
	
	RestTemplate restTemplate= new RestTemplate();
	LoanApplicationEntity loanApplicationEntity = new LoanApplicationEntity();

	@Autowired
	private LoanApplicationService loanApplicationService;
	//Map<String, Double> responseBody = new HashMap<>();
	//get loan amount from user and save to database along with all the other details
	@PostMapping("/openLoanApplication")
	public ResponseEntity<ApiResponse> submitLoanApplication(@RequestBody HashMap<String,Double> loanAmount){
		loanApplicationEntity.setRequestedAmount(loanAmount.get("hello"));
		return ResponseBuilder.buildResponse(200,"Loan application opened successfully", loanApplicationService.saveLoanApplication(loanApplicationEntity));
	}

	//get loan id from payment service after successful payment of loan and change loan status in database
	@PostMapping("/closeLoanApplication")
	public ResponseEntity<ApiResponse> closeLoanApplication(@RequestBody HashMap<String,Long> loanId){
		return ResponseBuilder.buildResponse(200,"Loan application closed successfully", loanApplicationService.closeLoanApplication(loanId.get("loanId")));
	}


	//payment service calls to check if loan is open; check database and return value
	@GetMapping("/checkLoanStatus")
	public ResponseEntity<ApiResponse> checkLoanStatus(@RequestBody HashMap<String,String> loanId){
		return ResponseBuilder.buildResponse(200,"Loan application status fetched successfully", loanApplicationService.checkLoanStatus(Long.parseLong(loanId.get("loanId"))));
	}
	
	
	
	//dummy apis
		@GetMapping("http://localhost:8080/authenticateUser")
		public ResponseEntity<ApiResponse> dummyAuth(@RequestBody PartnerEntity a){
			return ResponseBuilder.buildResponse(200,"Auth works", "");
		}
		
		@GetMapping("http://localhost:8080/api/user/'10'?serviceId=los")
		public ResponseEntity<ApiResponse> dummyIdentity(@RequestBody PartnerEntity a){
			return ResponseBuilder.buildResponse(200,"IS works", "");
		}
		
		@GetMapping("http://localhost:8080/closeApplication")
		public ResponseEntity<ApiResponse> dummyCommunication(@RequestBody PartnerEntity a){
			return ResponseBuilder.buildResponse(200,"communication works", "");
		}
		
		@GetMapping("http://localhost:8080/addBanAddress")
		public ResponseEntity<ApiResponse> dummyIdentity2(@RequestBody PartnerEntity a){
			return ResponseBuilder.buildResponse(200,"IS works", "");
		}

	

}
