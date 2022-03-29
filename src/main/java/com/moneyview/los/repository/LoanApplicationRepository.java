package com.moneyview.los.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moneyview.los.constants.LoanApplicationStatus;
import com.moneyview.los.model.LoanApplicationEntity;

public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long>{
	LoanApplicationStatus findLoanApplicationStatusById(long userId);
	
	@Query("SELECT loan_status FROM loan_application WHERE user_id = :userId")
	List<LoanApplicationStatus> checkLoanStatusById(@Param("userId") long userId);
}
