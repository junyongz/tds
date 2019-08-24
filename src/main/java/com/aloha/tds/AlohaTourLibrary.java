package com.aloha.tds;

import java.util.List;

public class AlohaTourLibrary implements TourLibrary {

	private TourService tourService;

	public AlohaTourLibrary(TourService tourService) {
		this.tourService = tourService;
	}

	@Override
	public List<Tour> toursByCustomer(Customer customer) {
		return this.tourService.toursByCustomer(customer);
	}

}
