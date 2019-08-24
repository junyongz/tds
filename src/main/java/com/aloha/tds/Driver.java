package com.aloha.tds;

import javax.persistence.Entity;

@Entity
public class Driver {

	private String fullName;

	public Driver(String fullName) {
		this.fullName = fullName;
	}
	
	public static Driver of(String fullName) {
		return new Driver(fullName);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public String toString() {
		return "Driver [fullName=" + fullName + "]";
	}

}
