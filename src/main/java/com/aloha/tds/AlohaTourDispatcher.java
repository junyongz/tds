package com.aloha.tds;

import java.util.Date;

public class AlohaTourDispatcher implements TourDispatcher {
	
	private TourService tourService;
	
	public AlohaTourDispatcher(TourService tourService) {
		this.tourService = tourService;
	}
	
	@Override
	public Tour newBooking(Customer customer) {
		Tour tour = new Tour(customer, this);
		this.tourService.addTour(tour);
		return tour;
	}

	@Override
	public long tourCount() {
		return this.tourService.countTours();
	}
	
	@Override
	public void checkVehicleAvailability(Vehicle vehicle, Date fromDate, Date toDate)
			throws VehicleNotAvailableException {
		if (tourService.vehicleArrangedForDates(vehicle, fromDate, toDate)) {
			throw new VehicleNotAvailableException(vehicle);
		}
	}

}
