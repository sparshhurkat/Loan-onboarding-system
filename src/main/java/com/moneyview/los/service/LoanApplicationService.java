package com.moneyview.los.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.moneyview.los.constants.LoanApplicationStatus;
import com.moneyview.los.model.LoanApplicationEntity;


public interface LoanApplicationService {
	LoanApplicationEntity saveLoanApplication(LoanApplicationEntity loanApplicationEntity);
	
	
	LoanApplicationEntity closeLoanApplication(long loanId);

	LoanApplicationEntity findByLoanId(long loanId);
	
	LoanApplicationEntity checkLoanStatus(long loanId);
	
	List<String> checkUserPanStatus(long userId);
}
