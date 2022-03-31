package com.moneyview.los.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.Mapping;

import com.moneyview.los.constants.LoanApplicationStatus;
import com.moneyview.los.model.LoanApplicationEntity;

@EnableJpaRepositories
@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long>{
	@Query("select loanStatus from LoanApplicationEntity where userId = :ID and loanStatus = 'open'")
	List<String> findLoanApplicationStatusById(long userId);
	
	
	@Query("select loanStatus from LoanApplicationEntity where userId = :ID and loanStatus = 'open'")
	List<String> checkLoanStatusById(@Param("ID") long userId);
	
	@Transactional
	  @Modifying
	@Query("UPDATE LoanApplicationEntity lae SET lae.loanStatus=:loan_status WHERE lae.id=:loan_id")
	void setLoanStatusByLoanId(@Param("loan_id") long id, @Param("loan_status") String loanStatus);
}
