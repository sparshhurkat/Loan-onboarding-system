package com.moneyview.los.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moneyview.los.constants.LoanApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name="loan_application")
public class LoanApplicationEntity {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	//Details from Identity Service
	@Column(name = "first_name",length=10)
	private String firstName;
	
	@Column(name = "last_name",length=10)
	private String lastName;
	
	@Column(name="dob")
	@JsonFormat(pattern="yyyy/MM/dd")
    private LocalDate dob;
	
	@Column(name = "pan")
	private String pan;
	
	@Column(name="aadhar")
	private String aadhar;
	
	@Column(name = "email")
	private String email;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	
	//We generate

	
	@Column(name = "ref_id")
	private int refId;
	
	@Column(name = "partner_id")
	private int partnerId;
	
	@Column(name = "loan_status")
	private String loanStatus;
	
	@Column(name = "loan_applied_date")
	private LocalDate loanAppliedDate;
	
	
	//Details from User
	
	//jsonignore
	//private String authToken;
	
	@Column(name = "user_id")
	private long userId;
	
	@Column(name = "cibil_score")
	private int cibilScore;
	
	@Column(name = "emp_status")
	private boolean empStatus;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "bank_account_number")
	private String bankAccountNumber;
	
	@Column(name = "requested_amount")
	private double requestedAmount;
	

	public long getLoanId() {
		return id;
	}

	public void setLoanId(long loanId) {
		this.id = loanId;
	}

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}

	public int getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(int cibilScore) {
		this.cibilScore = cibilScore;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(String dob) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		  

		  //convert String to LocalDate
		  LocalDate localDate = LocalDate.parse(dob, formatter);
		this.dob = localDate;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public boolean isEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(boolean empStatus) {
		this.empStatus = empStatus;
	}

	public double getRequestedAmount() {
		return requestedAmount;
	}

	public void setRequestedAmount(double requestedAmount) {
		this.requestedAmount = requestedAmount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public LocalDate getLoanAppliedDate() {
		return loanAppliedDate;
	}

	public void setLoanAppliedDate(LocalDate loanAppliedDate) {
		this.loanAppliedDate = loanAppliedDate;
	}
	
	
}
