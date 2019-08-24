package com.aloha.tds;

public abstract class AbstractTourService implements TourService {

	@Override
	public final void addTour(Tour tour) {
		doPreAddTour(tour);
		doPostAddTour(tour);
	}
	
	protected abstract void doPreAddTour(Tour tour);

	protected abstract void doPostAddTour(Tour tour);

}
