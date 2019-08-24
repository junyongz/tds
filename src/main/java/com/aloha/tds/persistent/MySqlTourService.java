package com.aloha.tds.persistent;

import java.util.Date;
import java.util.List;

import com.aloha.tds.Customer;
import com.aloha.tds.Tour;
import com.aloha.tds.TourService;
import com.aloha.tds.Vehicle;

public class MySqlTourService implements TourService {

	@Override
	public void addTour(Tour tour) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long countTours() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Tour> toursByCustomer(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean arrangedVehicle(Vehicle vehicle, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		return false;
	}

}
