package com.moneyview.los.model;

public class PostBanAddress {
	private String address;
	private String bankAccountNumber;
	private long userId;
	
	public PostBanAddress(String address, String bankAccountNumber, long userId)
	{
		setAddress(address);
		setBankAccountNumber(bankAccountNumber);
		setUserId(userId);
	}

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
}
