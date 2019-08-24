package com.aloha.tds;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TDS_PASSENGER")
public class Passenger {

	@Id
	private Long id;

	@Column(name = "FULL_NAME")
	private String fullName;

	public Passenger(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Passenger [fullName=" + fullName + "]";
	}

}
