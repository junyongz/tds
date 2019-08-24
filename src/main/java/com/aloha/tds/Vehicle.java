package com.aloha.tds;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TDS_VEHICLE")
public class Vehicle {

	@Id
	private Long id;

	@Column(name = "PLATE_NUMBER")
	private String plateNumber;

	public Vehicle(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public static Vehicle of(String plateNumber) {
		return new Vehicle(plateNumber);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	@Override
	public String toString() {
		return "Vehicle [plateNumber=" + plateNumber + "]";
	}

}
