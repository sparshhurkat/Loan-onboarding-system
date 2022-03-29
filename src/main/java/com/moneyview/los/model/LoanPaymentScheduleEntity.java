package com.moneyview.los.model;

import java.time.LocalDate;

public class LoanPaymentScheduleEntity {
	private long loan_id;
	private long user_id;
	private double disbursal_amount;
	private double first_emi;
	private double last_emi;
	private double principal_amount;
	private double interest_component;
	private LocalDate due_date;
	private int partner_id;
	private String bank_account_no;

	public LoanPaymentScheduleEntity(long loan_id, long user_id, double disbursal_amount, double first_emi, double last_emi,double principal_amount,double interest_component, LocalDate due_date,int partner_id,String bank_account_no )
	{
		this.setLoanId(loan_id);
		this.setUserId(user_id);
		this.setDisbursalAmount(disbursal_amount);
		this.setFirstEmi(first_emi);
		this.setLastEmi(last_emi);
		this.setPartnerId(partner_id);
		this.setPrincipalAmount(principal_amount);
		this.setInterestComponent(interest_component);
		this.setDueDate(due_date);
		this.setBankAccountNo(bank_account_no);
	}

	public long getLoanId() {
		return loan_id;
	}

	public void setLoanId(long loan_id) {
		this.loan_id = loan_id;
	}

	public long getUserId() {
		return user_id;
	}

	public void setUserId(long user_id) {
		this.user_id = user_id;
	}

	public double getDisbursalAmount() {
		return disbursal_amount;
	}

	public void setDisbursalAmount(double disbursal_amount) {
		this.disbursal_amount = disbursal_amount;
	}

	public double getFirstEmi() {
		return first_emi;
	}

	public void setFirstEmi(double first_emi) {
		this.first_emi = first_emi;
	}

	public double getLastEmi() {
		return last_emi;
	}

	public void setLastEmi(double last_emi) {
		this.last_emi = last_emi;
	}

	public double getPrincipalAmount() {
		return principal_amount;
	}

	public void setPrincipalAmount(double principal_amount) {
		this.principal_amount = principal_amount;
	}

	public double getInterestComponent() {
		return interest_component;
	}

	public void setInterestComponent(double interest_component2) {
		this.interest_component = interest_component2;
	}

	public LocalDate getDueDate() {
		return due_date;
	}

	public void setDueDate(LocalDate due_date) {
		this.due_date = due_date;
	}

	public int getPartnerId() {
		return partner_id;
	}

	public void setPartnerId(int partner_id) {
		this.partner_id = partner_id;
	}

	public String getBankAccountNo() {
		return bank_account_no;
	}

	public void setBankAccountNo(String bank_account_no) {
		this.bank_account_no = bank_account_no;
	}
}
