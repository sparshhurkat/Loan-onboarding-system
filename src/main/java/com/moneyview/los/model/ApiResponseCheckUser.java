package com.moneyview.los.model;

public class ApiResponseCheckUser {
	private long userId;
	   private int status;
	   private String message;
	   public ApiResponseCheckUser(long i,int v,String s) {
	       setUserId(i);
	       setStatus(v);
	       setMessage(s);
	   }
	public long getUserId() {
	   return userId;
	}
	public void setUserId(long userId) {
	   this.userId = userId;
	}
	public int getStatus() {
	   return status;
	}
	public void setStatus(int status) {
	   this.status = status;
	}
	public String getMessage() {
	   return message;
	}
	public void setMessage(String message) {
	   this.message = message;
	}
}
