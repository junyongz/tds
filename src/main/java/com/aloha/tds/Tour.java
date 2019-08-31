package com.aloha.tds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.util.ObjectUtils;

@Entity
@Table(name = "TDS_TOUR")
public class Tour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private List<Trip> trips = new ArrayList<>();

	@Column
	private boolean booked;

	@Column
	private boolean arranged;

	@Column
	private boolean cancelled = false;

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;

	@Transient
	private TourDispatcher tourDispatcher;

	public Tour() {
	}

	private Tour(Customer customer) {
		this(customer, null);
	}

	Tour(Customer customer, TourDispatcher tourDispatcher) {
		this.customer = customer;
		this.tourDispatcher = tourDispatcher;
	}

	void usingDispatcher(TourDispatcher tourDispatcher) {
		this.tourDispatcher = tourDispatcher;
	}

	public static Tour ofCustomer(Customer customer) {
		return new Tour(customer);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void bookSingleTrip(Trip trip) {
		this.bookTrips(trip);
	}

	public void bookTrips(Trip... trips) {
		this.trips.addAll(Arrays.asList(trips));
		this.booked = true;
	}

	public Customer customer() {
		return this.customer;
	}

	public void cancel() {
		for (Trip trip : this.trips) {
			trip.cancel();
		}
		this.cancelled = true;
		this.arranged = false;
	}

	public int tripCount() {
		return this.trips.size();
	}

	public boolean isBooked() {
		return booked;
	}

	public boolean isArranged() {
		return arranged;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	List<Trip> getAllTrips() {
		return trips;
	}

	/**
	 * @param vehicle
	 * @param driver
	 * @throws VehicleNotAvailableException if the vehicle not available for
	 *         trips timing
	 */
	public void arrangeVehicleWithDriver(Vehicle vehicle, Driver driver) throws VehicleNotAvailableException {
		for (Trip trip : this.trips) {
			// check vehicle availability before arrange
			tourDispatcher.checkVehicleAvailability(vehicle, trip.getFromDate(), trip.getToDate());
			trip.arrangeVehicleWithDriver(vehicle, driver);
		}
		this.arranged = true;
	}

	// TODO: method visibility rethink
	boolean arrangedVehicle(Vehicle vehicle, Date fromDate, Date toDate) {
		for (Trip trip : this.trips) {
			if (trip.getVehicle() == null) {
				continue;
			}

			if (ObjectUtils.nullSafeEquals(trip.getVehicle().getPlateNumber(), vehicle.getPlateNumber())) {
				// (StartA <= EndB) and (EndA >= StartB)
				if (trip.getFromDate().compareTo(toDate) <= 0 && trip.getToDate().compareTo(fromDate) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public void arrangeVehicleWithDriver(Trip trip, Vehicle vehicle, Driver driver)
			throws VehicleNotAvailableException {
		tourDispatcher.checkVehicleAvailability(vehicle, trip.getFromDate(), trip.getToDate());
		trip.arrangeVehicleWithDriver(vehicle, driver);
		checkIfAllTripsArranged();
	}

	private void checkIfAllTripsArranged() {
		this.arranged = true;
		for (Trip trip : this.trips) {
			if (trip.getVehicle() == null) {
				this.arranged = false;
			}
		}
	}

}
