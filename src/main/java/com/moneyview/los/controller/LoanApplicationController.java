package com.moneyview.los.controller;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.moneyview.los.model.ApiResponse;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.model.UserEntity;
import com.moneyview.los.service.LoanApplicationService;

@RestController
public class LoanApplicationController {
	
	RestTemplate restTemplate= new RestTemplate();
	private String identityServiceUrl = "http://localhost:8080";

	@Autowired
	private LoanApplicationService loanApplicationService;

	//get loan amount from user and save to database along with all the other details
	@PostMapping("/openLoanApplication")
	public ApiResponse<LoanApplicationEntity> submitLoanApplication(@RequestBody LoanApplicationEntity loanApplicationEntity){
		return new ApiResponse<>(HttpStatus.OK.value(), "Loan application opened successfully",loanApplicationService.saveLoanApplication(loanApplicationEntity));
	}

	//get loan id from payment service after successful payment of loan and change loan status in database
	@PostMapping("/closeLoanApplication")
	public ApiResponse<LoanApplicationEntity> closeLoanApplication(@RequestBody long loanId){
		return new ApiResponse<>(HttpStatus.OK.value(), "Loan application closed successfully",loanApplicationService.closeLoanApplication(loanId));
	}


	//payment service calls to check if loan is open; check database and return value
	@GetMapping("/checkLoanStatus")
	public ApiResponse<LoanApplicationEntity> checkLoanStatus(@RequestBody long loanId){
		return new ApiResponse<>(HttpStatus.OK.value(), "Loan application status fetched successfully",loanApplicationService.checkLoanStatus(loanId));
	}


	//send GET request to identity service with user id and service id(for LOS=1)
	//also scanner input loan amount from user
	//then send the object to service layer for storing
	public ApiResponse<LoanApplicationEntity> retrieveUserDetails(UserEntity user) {
		Scanner sc=new Scanner(System.in);
		LoanApplicationEntity loanApplicationEntity = new LoanApplicationEntity();

		String url = identityServiceUrl + "/retrieveUserDetails?service=1&id="+user.getUserId();

		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

		HttpStatus statusCode = responseEntity.getStatusCode();
		System.out.println("status code - " + statusCode);
		String userDetails = responseEntity.getBody();
		System.out.println("response body - " + userDetails);
		HttpHeaders responseHeaders = responseEntity.getHeaders();
		System.out.println("response Headers - " + responseHeaders);

		double loanAmount=sc.nextDouble();
		
		//user(user details from user entity)+userDetails(response body from identity service)+loanAmount


		return new ApiResponse<>(statusCode.value(), "User is authorized",loanApplicationService.saveLoanApplication(loanApplicationEntity));
	}

}
