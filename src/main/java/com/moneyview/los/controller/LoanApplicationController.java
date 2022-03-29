package com.moneyview.los.controller;

import java.time.LocalDate;
import java.time.Period;
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
import com.moneyview.los.model.IdentityServiceEntity;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.model.ResponseBuilder;
import com.moneyview.los.model.UserEntity;
import com.moneyview.los.service.LoanApplicationService;

@RestController
public class LoanApplicationController {
	
	RestTemplate restTemplate= new RestTemplate();
	private String identityServiceUrl = "http://localhost:8080";
	LoanApplicationEntity loanApplicationEntity = new LoanApplicationEntity();

	@Autowired
	private LoanApplicationService loanApplicationService;

	//get loan amount from user and save to database along with all the other details
	@PostMapping("/openLoanApplication")
	public ResponseEntity<ApiResponse> submitLoanApplication(@RequestBody double loanAmount){
		loanApplicationEntity.setRequestedAmount(loanAmount);
		return ResponseBuilder.buildResponse(200,"Loan application opened successfully", loanApplicationService.saveLoanApplication(loanApplicationEntity));
	}

	//get loan id from payment service after successful payment of loan and change loan status in database
	@PostMapping("/closeLoanApplication")
	public ResponseEntity<ApiResponse> closeLoanApplication(@RequestBody long loanId){
		return ResponseBuilder.buildResponse(200,"Loan application closed successfully", loanApplicationService.closeLoanApplication(loanId));
	}


	//payment service calls to check if loan is open; check database and return value
	@GetMapping("/checkLoanStatus")
	public ResponseEntity<ApiResponse> checkLoanStatus(@RequestBody long loanId){
		return ResponseBuilder.buildResponse(200,"Loan application status fetched successfully", loanApplicationService.checkLoanStatus(loanId));
	}


	

}
