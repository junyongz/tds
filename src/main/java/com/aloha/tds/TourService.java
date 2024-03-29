package com.aloha.tds;

import java.util.Date;
import java.util.List;

public interface TourService {

	void addTour(Tour tour);

	long countTours();

	List<Tour> toursByCustomer(Customer customer);

	/**
	 * Whether the vehicle is arranged for any tour for the given time frame.
	 * @param vehicle vehicle for sending passenger round and round
	 * @param fromDate from this date
	 * @param toDate to this date
	 * @return whether the vehicle is arranged for any tour for the given time frame.
	 */
	boolean vehicleArrangedForDates(Vehicle vehicle, Date fromDate, Date toDate);

	void arrangeVehicleWithDriver(Tour tour, Vehicle vehicle, Driver driver) throws VehicleNotAvailableException;

	void arrangeVehicleWithDriverForTrip(Trip trip, Vehicle vehicle, Driver driver)
			throws VehicleNotAvailableException;
}
