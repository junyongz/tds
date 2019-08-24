package com.aloha.tds;

import java.util.Date;

public interface TourDispatcher {

	Tour newBooking(Customer customer);

	long tourCount();

	void checkVehicleAvailability(Vehicle vehicle, Date fromDate, Date toDate) throws VehicleNotAvailableException;

}
