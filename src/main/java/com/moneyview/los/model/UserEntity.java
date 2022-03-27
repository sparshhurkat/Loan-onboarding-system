package com.moneyview.los.model;

public class UserEntity {
	
	private String authToken;
	
	private long userId;
	
	private int cibilScore;
	
	private boolean empStatus;
	
	private String address;
	
	private double bankAccountNumber;

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getCibilScore() {
		return cibilScore;
	}

	public void setCibilScore(int cibilScore) {
		this.cibilScore = cibilScore;
	}

	public boolean isEmpStatus() {
		return empStatus;
	}

	public void setEmpStatus(boolean empStatus) {
		this.empStatus = empStatus;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(double bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
}
