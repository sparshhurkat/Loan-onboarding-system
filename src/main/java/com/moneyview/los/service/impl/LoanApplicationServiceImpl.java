package com.moneyview.los.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.repository.LoanApplicationRepository;
import com.moneyview.los.service.LoanApplicationService;

@Service(value="userService")
public class LoanApplicationServiceImpl implements LoanApplicationService {
	
	@Autowired
	private LoanApplicationRepository loanApplicationRepository;
	
	@Override
    public LoanApplicationEntity saveLoanApplication(LoanApplicationEntity loanApplication) {
	    LoanApplicationEntity newLoanApplication = new LoanApplicationEntity();
	    
	    newLoanApplication.setAuthToken(loanApplication.getAuthToken());
	    newLoanApplication.setLoanId(loanApplication.getLoanId());
	    newLoanApplication.setRefId(loanApplication.getRefId());
	    newLoanApplication.setUserId(loanApplication.getUserId());
	    newLoanApplication.setEmail(loanApplication.getEmail());
	    newLoanApplication.setPhoneNumber(loanApplication.getPhoneNumber());
	    newLoanApplication.setPartnerId(loanApplication.getPartnerId());
	    newLoanApplication.setCibilScore(loanApplication.getCibilScore());
	    newLoanApplication.setDob(loanApplication.getDob());
	    newLoanApplication.setEmpStatus(loanApplication.isEmpStatus());
	    newLoanApplication.setRequestedAmount(loanApplication.getRequestedAmount());
	    newLoanApplication.setAddress(loanApplication.getAddress());
	    newLoanApplication.setPan(loanApplication.getPan());
	    newLoanApplication.setBankAccountNumber(loanApplication.getBankAccountNumber());
	    newLoanApplication.setLoanStatus(loanApplication.isLoanStatus());
	    
        return loanApplicationRepository.save(newLoanApplication);
    }

	@Override
	public LoanApplicationEntity findByLoanId(long loanId) {
		return null;
		/*Optional<User> optionalUser = loanApplicationRepository.findById(loanId);
		return optionalUser.isPresent() ? optionalUser.get() : null;*/
	}
	
	@Override
    public LoanApplicationEntity closeLoanApplication(long loanId) {
		return null;
		/*LoanApplicationEntity user = findById(loanApplicationEntity.getId());
        if(user != null) {
            BeanUtils.copyProperties(loanApplicationEntity, user, "password");
            userDao.save(user);
        }
        return userDto;*/
    }
	
	@Override
    public LoanApplicationEntity checkLoanStatus(long loanId) {
		return null;
		/*LoanApplicationEntity user = findById(loanApplicationEntity.getId());
        if(user != null) {
            BeanUtils.copyProperties(loanApplicationEntity, user, "password");
            userDao.save(user);
        }
        return userDto;*/
    }
}
