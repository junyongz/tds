package com.aloha.tds;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "TDS_TRIP")
public class Trip {

	@Id
	private Long id;

	private Long tourId;

	@ManyToOne
	@JoinColumn(name = "FROM_PLACE_ID")
	private Place fromPlace;

	@Column
	private Date fromDate;

	@ManyToOne
	@JoinColumn(name = "TO_PLACE_ID")
	private Place toPlace;

	@Column
	private Date toDate;

	private Passenger[] passengers;

	@ManyToOne
	@JoinColumn(name = "VEHICLE_ID")
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "DRIVER_ID")
	private Driver driver;

	Trip(Place fromPlace, Date fromDate, Place toPlace, Date toDate, Passenger... passengers) {
		this.fromPlace = fromPlace;
		this.fromDate = fromDate;
		this.toPlace = toPlace;
		this.toDate = toDate;
		this.passengers = passengers;
	}

	public static TripBuilder startFrom(Place fromPlace, Date fromDate) {
		return new TripBuilder(fromPlace, fromDate);
	}

	public Place getFromPlace() {
		return fromPlace;
	}

	public void setFromPlace(Place fromPlace) {
		this.fromPlace = fromPlace;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Place getToPlace() {
		return toPlace;
	}

	public void setToPlace(Place toPlace) {
		this.toPlace = toPlace;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Passenger[] getPassengers() {
		return passengers;
	}

	public void setPassengers(Passenger[] passengers) {
		this.passengers = passengers;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public Driver getDriver() {
		return driver;
	}

	void arrangeVehicleWithDriver(Vehicle vehicle, Driver driver) {
		this.vehicle = vehicle;
		this.driver = driver;
	}

	public void cancel() {
		this.vehicle = null;
		this.driver = null;
	}

	static class TripBuilder {
		private Place fromPlace;

		private Date fromDate;

		private Place toPlace;

		private Date toDate;

		private Passenger[] passengers;

		public TripBuilder(Place fromPlace, Date fromDate) {
			this.fromPlace = fromPlace;
			this.fromDate = fromDate;
		}

		public TripBuilder to(Place toPlace, Date toDate) {
			this.toPlace = toPlace;
			this.toDate = toDate;
			return this;
		}

		public TripBuilder passengers(Passenger... passengers) {
			this.passengers = passengers;
			return this;
		}

		public Trip done() {
			return new Trip(this.fromPlace, this.fromDate, this.toPlace, this.toDate, this.passengers);
		}
	}

}
