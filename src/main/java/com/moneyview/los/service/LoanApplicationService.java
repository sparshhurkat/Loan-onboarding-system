package com.moneyview.los.service;

import com.moneyview.los.model.LoanApplicationEntity;

public interface LoanApplicationService {
	LoanApplicationEntity saveLoanApplication(LoanApplicationEntity loanApplicationEntity);
	
	
	LoanApplicationEntity closeLoanApplication(long loanId);

	LoanApplicationEntity findByLoanId(long loanId);
	
	LoanApplicationEntity checkLoanStatus(long loanId);
}
