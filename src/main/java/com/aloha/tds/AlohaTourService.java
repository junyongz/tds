package com.aloha.tds;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aloha.tds.persistent.TourRepository;
import com.aloha.tds.persistent.TripRepository;

@Service
@Transactional
public class AlohaTourService extends AbstractTourService {

	private final TourRepository tourRepository;

	private final TripRepository tripRepository;

	@Autowired
	public AlohaTourService(TourRepository tourRepository, TripRepository tripRepository) {
		this.tourRepository = tourRepository;
		this.tripRepository = tripRepository;
	}

	@Override
	protected void doPostAddTour(Tour tour) {
	}

	@Override
	protected void doPreAddTour(Tour tour) {
		this.tourRepository.save(tour);
		this.tripRepository.saveAll(tour.getAllTrips());
	}

	@Override
	public long countTours() {
		return tourRepository.count();
	}

	@Override
	public List<Tour> toursByCustomer(Customer customer) {
		return this.tourRepository.findByCustomerId(customer.getId());
	}

	@Override
	public boolean arrangedVehicle(Vehicle vehicle, Date fromDate, Date toDate) {
		return this.tripRepository.countByVehicleArrangedForDates(vehicle.getId(), fromDate, toDate) > 0;
	}

	@Override
	public void arrangeVehicleWithDriver(Tour tour, Vehicle vehicle, Driver driver) throws VehicleNotAvailableException {
		tour.arrangeVehicleWithDriver(vehicle, driver);
		this.tourRepository.save(tour);
		this.tripRepository.saveAll(tour.getAllTrips());
	}
}
