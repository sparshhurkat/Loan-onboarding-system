package com.moneyview.los.service.impl;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.moneyview.los.constants.LoanApplicationStatus;
import com.moneyview.los.constants.PartnerConstants;
import com.moneyview.los.exception.ResourceNotFoundException;
import com.moneyview.los.model.LoanApplicationEntity;
import com.moneyview.los.model.LoanPaymentScheduleEntity;
import com.moneyview.los.repository.LoanApplicationRepository;
import com.moneyview.los.service.LoanApplicationService;

@Service(value="loanApplicationService")
public class LoanApplicationServiceImpl implements LoanApplicationService {
	
	@Autowired
	private LoanApplicationRepository loanApplicationRepository;
	
	RestTemplate restTemplate= new RestTemplate();
	
	@Override
    public LoanApplicationEntity saveLoanApplication(LoanApplicationEntity loanApplication) {
	    LoanApplicationEntity newLoanApplication = new LoanApplicationEntity();
	    LocalDate currentDate = LocalDate.now();
	    
		
	    
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
	    newLoanApplication.setLoanAppliedDate(currentDate);
	    
	    
	    
	    //refIdGeneration();
	    //TODO: add update to database
	    int refId = Integer.parseInt(UUID.randomUUID().toString());
	    newLoanApplication.setRefId(refId);
	    
	    loanApplicationRepository.save(newLoanApplication);
	    
	    double interest=PartnerConstants.getPartnerModelList().get(newLoanApplication.getPartnerId()).getInterest();
	    double principal=newLoanApplication.getRequestedAmount()/12;
	    
	    generatePaymentSchedule(newLoanApplication.getLoanId(), newLoanApplication.getUserId(), principal, interest, newLoanApplication.getLoanAppliedDate(), newLoanApplication.getPartnerId(), newLoanApplication.getBankAccountNumber());
	    
	    
	    //send to ps
	    String paymentServiceUrl = "http://localhost:8080" + "/sendRepaymentSchedule";
	    ResponseEntity<LoanPaymentScheduleEntity> paymentSchedule = restTemplate.postForEntity(paymentServiceUrl, "Repayment Schedule sent successfully", LoanPaymentScheduleEntity.class);
	    
	    //post to cs
	    String communicationServiceUrl = "http://localhost:8080" + "/sendLoanAgreement";
	    ResponseEntity<LoanPaymentScheduleEntity> agreementSchedule = restTemplate.postForEntity(communicationServiceUrl, "Send Loan agreement to the user", LoanPaymentScheduleEntity.class);
	    
	    
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
		//update status in DB
		LoanApplicationEntity existingApplication = loanApplicationRepository.findById(loanId).orElseThrow(
				() -> new ResourceNotFoundException("Loan", "Id", loanId)); 
		
		existingApplication.setLoanStatus(
				//LoanApplicationStatus.CLOSE.toString()
				"close")
		;
		
		// save existing employee to DB
		loanApplicationRepository.save(existingApplication);
		
		
		//call CS for loan closing email
		String communicationServiceUrl = "http://localhost:8080" + "/closeLoanApplication";
		ResponseEntity<LoanPaymentScheduleEntity> agreementSchedule = restTemplate.postForEntity(communicationServiceUrl, "Send Loan closed to the user", LoanPaymentScheduleEntity.class);
		return existingApplication;
    }
	
	@Override
    public LoanApplicationEntity checkLoanStatus(long loanId) {
		return loanApplicationRepository.findById(loanId).orElseThrow(() 
				-> new ResourceNotFoundException("Loan", "Id", loanId));
    }
	
	@Override
	public List<String> checkUserPanStatus(long userId) {
		List<String> panStatus=loanApplicationRepository.checkLoanStatusById(userId);
		return panStatus;
	}
	
	public static LoanPaymentScheduleEntity generatePaymentSchedule(long loanId,long userId, double principal, double interest, LocalDate loanAppliedDate,int partnerId, String bankAccount)
	{
	    double amount = calculateAmount(principal,interest);
	  return new LoanPaymentScheduleEntity(
	    loanId,
	    userId,amount,
	    getFirstEmi(amount),
	    getLastEmi(amount),
	    principal,
	    interest,
	    getDate(loanAppliedDate),
	    partnerId,
	    bankAccount
	  );
	}
	
	public static double calculateInterest(double principalAmount, double interestRate)
	{
	    double simple_interest = (principalAmount * interestRate)/100;
	    return  simple_interest;
	}

	public  static double calculateAmount(double principalAmount,double interestRate)
	{
	    return principalAmount + calculateInterest(principalAmount,interestRate);
	}

	public static double getFirstEmi(double totalAmount)
	{
	return (int)totalAmount/12;
	}

	public  static  double getLastEmi(double totalAmount)
	{
	    return (int)(totalAmount*12) - (int)(Math.round(totalAmount)*11);
	}

	public static LocalDate getDate(LocalDate loanDate)
	{
	    int day = loanDate.getDayOfMonth();
	    Month month=null;
	    if(day>=6 && day<=25)
	        month = loanDate.getMonth().plus(1);
	    else if (day>=26 && day<=31)
	        month = loanDate.getMonth().plus(2);
	    else
	     month = loanDate.getMonth().plus(1);

	    int monthNo = month.getValue();

	   // return monthNo;
	    int year = loanDate.getYear();

	    if(monthNo >= 2 && monthNo <=12)
	    {
	        return LocalDate.of(year, month, 5);
	    }
	    else
	    return LocalDate.of(year+1, month, 5);

	}
	
}
