package com.aloha.tds;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import com.aloha.tds.persistent.CustomerRepository;
import com.aloha.tds.persistent.DriverRepository;
import com.aloha.tds.persistent.PassengerRepository;
import com.aloha.tds.persistent.PlaceRepository;
import com.aloha.tds.persistent.TourRepository;
import com.aloha.tds.persistent.TripRepository;
import com.aloha.tds.persistent.VehicleRepository;
import com.aloha.tds.util.DateTimeUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:tds")
public class TourDispatcherEndToEndTests {

	@Autowired
	private TourRepository tourRepository;

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private PassengerRepository passengerRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void beforeTest() {
		this.tourRepository.deleteAll();
		this.tripRepository.deleteAll();
		this.customerRepository.deleteAll();
		this.vehicleRepository.deleteAll();
		this.placeRepository.deleteAll();
		this.driverRepository.deleteAll();
		this.passengerRepository.deleteAll();
	}

	@Test
	public void bookSingleTripTour() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 6, 28, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 6, 28, 13, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookSingleTrip(singleTrip);
		assertThat(tour.isBooked(), is(true));
		assertThat(tour.isArranged(), is(false));
		assertThat(tour.tripCount(), is(1));

		assertThat(dispatcher.tourCount(), is(1l));
	}

	@Test
	public void book4Days3NightsTour() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

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

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookTrips(trip1, trip2, trip3);
		assertThat(tour.isBooked(), is(true));
		assertThat(tour.isArranged(), is(false));
		assertThat(tour.tripCount(), is(3));

		assertThat(dispatcher.tourCount(), is(1l));
	}

	@Test
	public void arrangeVehicleAndDriverForSingleTrip() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = new Date();
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = new Date();

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
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
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = newPlace("Singapore", "Bukit Batok");
		Date fromDateTime = new Date();
		Place toPlace = newPlace("Malaysia", "Genting");
		Date toDateTime = new Date();

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");
		this.passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		assertThat(tour.isBooked(), is(false));
		assertThat(tour.isArranged(), is(false));
		tour.bookSingleTrip(singleTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");

		this.vehicleRepository.save(starex);
		this.driverRepository.save(driver);

		tourService.arrangeVehicleWithDriver(tour, starex, driver);

		Customer cust2 = newCustomer("Abang Kow");
		Tour anotherTour = dispatcher.newBooking(cust2);

		Passenger passengerC = new Passenger("Nono");

		Trip trip2 = Trip.startFrom(fromPlace, fromDateTime).to(toPlace, toDateTime).passengers(passengerC, passengerB)
				.done();
		anotherTour.bookSingleTrip(trip2);
		try {
			tourService.arrangeVehicleWithDriver(anotherTour, starex, driver);
			fail("should fail");
		}
		catch (VehicleNotAvailableException ex) {
			assertThat(ex.getVehicle().getPlateNumber(), is("JJ 9981"));
		}
	}

	@Test
	public void arrangeDifferentVehiclesForDifferentTrips() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

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

		Customer cust1 = newCustomer("Alice Tan");
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
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 13, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
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
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2019, 7, 14, 13, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookSingleTrip(singleTrip);

		Tour tour2 = dispatcher.newBooking(cust1);
		tour2.bookSingleTrip(Trip.startFrom(fromPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 9, 30))
				.to(toPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 12, 30)).done());

		Customer cust2 = newCustomer("Sharon Tan");
		Tour tour3 = dispatcher.newBooking(cust2);
		tour3.bookSingleTrip(Trip.startFrom(fromPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 9, 30))
				.to(toPlace, DateTimeUtils.fromDateTime(2019, 8, 14, 12, 30)).done());

		TourLibrary tourLibrary = new AlohaTourLibrary(tourService);
		List<Tour> tours = tourLibrary.toursByCustomer(cust1);
		assertThat(tours.size(), is(2));
		tours = tourLibrary.toursByCustomer(cust2);
		assertThat(tours.size(), is(1));
	}

	private Customer newCustomer(String customerName) {
		Customer customer = new Customer(customerName);
		this.customerRepository.save(customer);
		return customer;
	}

	private Place newPlace(String country, String city) {
		Place place = new Place(country, city);
		this.placeRepository.save(place);
		return place;
	}

}
