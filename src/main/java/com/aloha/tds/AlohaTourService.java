package com.aloha.tds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlohaTourService extends AbstractTourService {

	private List<Tour> tours = new ArrayList<>();
	
	@Override
	protected void doPostAddTour(Tour tour) {
	}
	
	@Override
	protected void doPreAddTour(Tour tour) {
		this.tours.add(tour);
	}

	@Override
	public long countTours() {
		return tours.size();
	}

	@Override
	public List<Tour> toursByCustomer(Customer customer) {
		List<Tour> designated = new ArrayList<>();
		for (Tour tour: this.tours) {
			if (tour.customer().equals(customer)) {
				designated.add(tour);
			}
		}
		return designated;
	}

	@Override
	public boolean arrangedVehicle(Vehicle vehicle, Date fromDate, Date toDate) {
		for (Tour tour : this.tours) {
			if (tour.arrangedVehicle(vehicle, fromDate, toDate)) {
				return true;
			}
		}
		return false;
	}
}
