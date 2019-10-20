package com.aloha.tds;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TDS_TRIP")
public class Trip {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TOUR_ID")
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

	@Transient
	private Passenger[] passengers = new Passenger[0];

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "TRIP_ID")
	private Set<TripPassenger> tripPassengers;

	@ManyToOne
	@JoinColumn(name = "VEHICLE_ID")
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "DRIVER_ID")
	private Driver driver;
	
	public Trip() {
		// for JPA
	}

	Trip(Place fromPlace, Date fromDate, Place toPlace, Date toDate, Passenger... passengers) {
		this.fromPlace = fromPlace;
		this.fromDate = fromDate;
		this.toPlace = toPlace;
		this.toDate = toDate;
		this.passengers = passengers;
		checkTiming();
		prepareTripPassengers();
	}

	public static TripBuilder startFrom(Place fromPlace, Date fromDate) {
		return new TripBuilder(fromPlace, fromDate);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	public void addPassenger(Passenger passenger) {
		System.arraycopy(new Passenger[] { passenger }, 0, this.passengers, this.passengers.length - 1, 1);
		this.tripPassengers.add(new TripPassenger(this, passenger));
	}

	public Long getTourId() {
		return tourId;
	}

	public void setTourId(Long tourId) {
		this.tourId = tourId;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Set<TripPassenger> getTripPassengers() {
		return tripPassengers;
	}

	public void setTripPassengers(Set<TripPassenger> tripPassengers) {
		this.tripPassengers = tripPassengers;
	}

	void arrangeVehicleWithDriver(Vehicle vehicle, Driver driver) {
		this.vehicle = vehicle;
		this.driver = driver;
	}

	public void cancel() {
		this.vehicle = null;
		this.driver = null;
	}

	private void prepareTripPassengers() {
		this.tripPassengers = new HashSet<>();
		for (Passenger passenger : this.passengers) {
			if (passenger.getId() == null) {
				throw new IllegalArgumentException("should have saved passenger records first");
			}

			this.tripPassengers.add(new TripPassenger(this, passenger));
		}
	}
	
	private void checkTiming() {
		if (this.toDate.before(fromDate)) {
			throw new IllegalArgumentException("from date shouldn't after to date");
		}
	}

	static class TripBuilder {
		private Place fromPlace;

		private Date fromDate;

		private Place toPlace;

		private Date toDate;

		private Passenger[] passengers = new Passenger[0];

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
