package com.moneyview.los.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.moneyview.los.model.ApiResponse;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.service.LoanApplicationService;

@RestController
public class LoanApplicationController {
	
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
	
}
