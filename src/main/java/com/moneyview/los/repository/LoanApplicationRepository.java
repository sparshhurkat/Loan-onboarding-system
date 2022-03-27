package com.moneyview.los.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moneyview.los.model.LoanApplicationEntity;

public interface LoanApplicationRepository extends JpaRepository<LoanApplicationEntity, Long>{

}
