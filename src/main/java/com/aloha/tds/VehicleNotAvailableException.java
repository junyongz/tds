package com.aloha.tds;

public class VehicleNotAvailableException extends RuntimeException {
	
	private static final long serialVersionUID = -1625907381639819850L;

	private final transient Vehicle vehicle;

	public VehicleNotAvailableException(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public Vehicle getVehicle() {
		return this.vehicle;
	}
	
}
