package com.aloha.tds;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.aloha.tds.util.DateTimeUtils;

public class TourDispatcherEndToEndTests {

	@Test
	public void bookSingleTripTour() {
		TourService tourService = new AlohaTourService();

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 6, 28, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 6, 28, 13, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookSingleTrip(singleTrip);
		assertThat(tour.isBooked(), is(true));
		assertThat(tour.isArranged(), is(false));
		assertThat(tour.tripCount(), is(1));

		assertThat(dispatcher.tourCount(), is(1l));
	}

	@Test
	public void book4Days3NightsTour() {
		TourService tourService = new AlohaTourService();
		
		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = new Date();
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = new Date();

		Trip trip1 = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		Place fromPlace2 = new Place("Singapore", "Bukit Batok");
		Date fromDateTime2 = new Date();
		Place toPlace2 = new Place("Malaysia", "Genting");
		Date toDateTime2 = new Date();

		Trip trip2 = new Trip(fromPlace2, fromDateTime2, toPlace2, toDateTime2, passengerA, passengerB);

		Place fromPlace3 = new Place("Singapore", "Bukit Batok");
		Date fromDateTime3 = new Date();
		Place toPlace3 = new Place("Malaysia", "Genting");
		Date toDateTime3 = new Date();

		Trip trip3 = Trip.startFrom(fromPlace3, fromDateTime3).to(toPlace3, toDateTime3)
				.passengers(passengerA, passengerB).done();

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookTrips(trip1, trip2, trip3);
		assertThat(tour.isBooked(), is(true));
		assertThat(tour.isArranged(), is(false));
		assertThat(tour.tripCount(), is(3));

		assertThat(dispatcher.tourCount(), is(1l));
	}

	@Test
	public void arrangeVehicleAndDriverForSingleTrip() {
		TourService tourService = new AlohaTourService();
		
		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = new Date();
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = new Date();

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		assertThat(tour.isBooked(), is(false));
		assertThat(tour.isArranged(), is(false));
		tour.bookSingleTrip(singleTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");

		tour.arrangeVehicleWithDriver(starex, driver);

		assertThat(tour.isBooked(), is(true));
		assertThat(tour.isArranged(), is(true));
		assertThat(tour.tripCount(), is(1));

		assertThat(dispatcher.tourCount(), is(1l));
	}

	@Test
	public void arrangeNonAvailableVehicleForSingleTripHitError() {
		TourService tourService = new AlohaTourService();		
		
		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = new Date();
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = new Date();

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		assertThat(tour.isBooked(), is(false));
		assertThat(tour.isArranged(), is(false));
		tour.bookSingleTrip(singleTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");

		tour.arrangeVehicleWithDriver(starex, driver);

		Customer cust2 = new Customer("Abang Kow");
		Tour anotherTour = dispatcher.newBooking(cust2);

		Passenger passengerC = new Passenger("Nono");

		Trip trip2 = Trip.startFrom(fromPlace, fromDateTime)
				.to(toPlace, toDateTime)
				.passengers(passengerC, passengerB)
				.done();
		anotherTour.bookSingleTrip(trip2);
		try {
			anotherTour.arrangeVehicleWithDriver(starex, driver);
			fail("should fail");
		}
		catch (VehicleNotAvailableException ex) {
			assertThat(ex.getVehicle().getPlateNumber(), is("JJ 9981"));
		}
	}

	@Test
	public void arrangeDifferentVehiclesForDifferentTrips() {
		TourService tourService = new AlohaTourService();

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 6, 28, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 6, 28, 13, 30);

		Trip trip1 = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		Place fromPlace2 = new Place("Malaysia", "Genting");
		Date fromDateTime2 = DateTimeUtils.fromDateTime(2019, 6, 29, 9, 30);
		Place toPlace2 = new Place("Malaysia", "KL");
		Date toDateTime2 = DateTimeUtils.fromDateTime(2019, 6, 29, 11, 30);

		Trip trip2 = new Trip(fromPlace2, fromDateTime2, toPlace2, toDateTime2, passengerA, passengerB);

		Place fromPlace3 = new Place("Malaysia", "KL");
		Date fromDateTime3 = DateTimeUtils.fromDateTime(2019, 6, 30, 12, 30);
		Place toPlace3 = new Place("Singapore", "Bukit Batok");
		Date toDateTime3 = DateTimeUtils.fromDateTime(2019, 6, 30, 16, 30);

		Trip trip3 = Trip.startFrom(fromPlace3, fromDateTime3).to(toPlace3, toDateTime3)
				.passengers(passengerA, passengerB).done();

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookTrips(trip1, trip2, trip3);
		tour.arrangeVehicleWithDriver(trip1, Vehicle.of("JSR 6611"), Driver.of("Michael"));
		tour.arrangeVehicleWithDriver(trip3, Vehicle.of("JSR 6611"), Driver.of("Tim"));

		assertThat(tour.isBooked(), is(true));
		assertThat(tour.isArranged(), is(false));

		tour.arrangeVehicleWithDriver(trip2, Vehicle.of("JSR 6622"), Driver.of("John"));
		assertThat(tour.isArranged(), is(true));
	}

	@Test
	public void cancelArrangedTourAndReleaseTheVehicle() {
		TourService tourService = new AlohaTourService();
		
		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 13, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookSingleTrip(singleTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");

		// no effect at all, cancel before arrange a vehicle
		tour.cancel();
		tour.arrangeVehicleWithDriver(starex, driver);

		assertThat(tour.isArranged(), is(true));

		tour.cancel();
		assertThat(tour.isArranged(), is(false));
		assertThat(tour.isCancelled(), is(true));

		dispatcher.checkVehicleAvailability(starex, fromDateTime, toDateTime);
	}
	
	@Test
	public void tourLibrary() {
		TourService tourService = new AlohaTourService();
		
		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 13, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = new Customer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookSingleTrip(singleTrip);
		
		Tour tour2 = dispatcher.newBooking(cust1);
		tour2.bookSingleTrip(Trip
				.startFrom(fromPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 9, 30))
				.to(toPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 12, 30))
				.done());
		
		Customer cust2 = new Customer("Sharon Tan");
		Tour tour3 = dispatcher.newBooking(cust2);
		tour3.bookSingleTrip(Trip
				.startFrom(fromPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 9, 30))
				.to(toPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 12, 30))
				.done());
		
		TourLibrary tourLibrary = new AlohaTourLibrary(tourService);
		List<Tour> tours = tourLibrary.toursByCustomer(cust1);
		assertThat(tours.size(), is(2));
		tours = tourLibrary.toursByCustomer(cust2);
		assertThat(tours.size(), is(1));
	}

}
