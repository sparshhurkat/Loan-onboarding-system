package com.moneyview.los.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PartnerEntity {
	private int id;
    double low_limit;
    double upper_limit;
    double interest;
    String name;
    
    public PartnerEntity(
            int id, double low_limit, double upper_limit, double interest, String name
        ) {
            this.setId(id);
           this.low_limit=low_limit;
            this.upper_limit = upper_limit;
            this.interest =interest;
            this.name = name;
        }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public double getLowLimit() {
		return low_limit;
	}

	public void setLowLimit(double low_limit) {
		this.low_limit = low_limit;
	}
	
	public double getUpperLimit() {
		return upper_limit;
	}

	public void setUpperLimit(double upper_limit) {
		this.upper_limit = upper_limit;
	}
	
	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}
