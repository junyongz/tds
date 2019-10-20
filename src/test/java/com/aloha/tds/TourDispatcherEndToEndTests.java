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

import com.aloha.tds.persistent.CustomerRepository;
import com.aloha.tds.persistent.DriverRepository;
import com.aloha.tds.persistent.PassengerRepository;
import com.aloha.tds.persistent.PlaceRepository;
import com.aloha.tds.persistent.TourRepository;
import com.aloha.tds.persistent.TripRepository;
import com.aloha.tds.persistent.VehicleRepository;
import com.aloha.tds.util.DateTimeUtils;

@RunWith(SpringRunner.class)
@SpringBootTest("spring.datasource.url=jdbc:h2:mem:tds")
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
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

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
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

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
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

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
		this.passengerRepository.save(passengerC);

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
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

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
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

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
	public void timeNotMakingSenseForTrip() {
		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 22, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

		try {
			new Trip(fromPlace, toDateTime, toPlace, fromDateTime, passengerA, passengerB);
			fail("should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// ok
		}
	}

	@Test
	public void changeTimeForTrip() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 22, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

		Trip singleTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookSingleTrip(singleTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");
		
		Date newFromDate = DateTimeUtils.fromDateTime(2018, 9, 15, 9, 30);
		Date newToDate = DateTimeUtils.fromDateTime(2018, 9, 15, 20, 30);

		tour.arrangeVehicleWithDriver(starex, driver);
		tour.changeTimeForTrip(singleTrip, newFromDate, newToDate);
		
		assertThat(singleTrip.getFromDate(), is(newFromDate));
		assertThat(singleTrip.getToDate(), is(newToDate));

		assertThat(dispatcher.tourCount(), is(1l));
	}

	@Test
	public void changeTimeForTripThatClashWithOtherTrip() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = new Place("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 9, 30);
		Place toPlace = new Place("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 22, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

		Trip firstTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);
		
		Trip nextTrip = new Trip(toPlace, DateTimeUtils.fromDateTime(2018, 9, 15, 9, 30), fromPlace,
				DateTimeUtils.fromDateTime(2018, 9, 15, 22, 30), passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookTrips(firstTrip, nextTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");

		tour.arrangeVehicleWithDriver(starex, driver);
		try {
			tour.changeTimeForTrip(firstTrip, DateTimeUtils.fromDateTime(2018, 9, 15, 9, 30),
				DateTimeUtils.fromDateTime(2018, 9, 15, 20, 30));
			fail("should have thrown time clash exception");
		}
		catch (TimeClashException ex) {
			// ok
			assertThat(ex.clashingTrip(), is(firstTrip));
			assertThat(ex.clashWith(), is(nextTrip));
		}
	}

	@Test
	public void changeVehicleForTrip() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = newPlace("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 9, 30);
		Place toPlace = newPlace("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 22, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

		Trip firstTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);
		
		Trip nextTrip = new Trip(toPlace, DateTimeUtils.fromDateTime(2018, 9, 15, 9, 30), fromPlace,
				DateTimeUtils.fromDateTime(2018, 9, 15, 22, 30), passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookTrips(firstTrip, nextTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");
		
		vehicleRepository.save(starex);
		driverRepository.save(driver);
		
		tourService.arrangeVehicleWithDriver(tour, starex, driver);
		
		Vehicle anotherCar = new Vehicle("JJ 9982");
		Driver anotherDriver = new Driver("Kong");
		
		vehicleRepository.save(anotherCar);
		driverRepository.save(anotherDriver);
		
		tourService.arrangeVehicleWithDriverForTrip(nextTrip, anotherCar, anotherDriver);
	}

	@Test
	public void changeVehicleForTripButNotAvailable() {
		TourService tourService = new AlohaTourService(tourRepository, tripRepository);

		Place fromPlace = newPlace("Singapore", "Bukit Batok");
		Date fromDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 9, 30);
		Place toPlace = newPlace("Malaysia", "Genting");
		Date toDateTime = DateTimeUtils.fromDateTime(2018, 9, 14, 22, 30);

		Passenger passengerA = new Passenger("Puppa");
		Passenger passengerB = new Passenger("Flower");
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

		Trip firstTrip = new Trip(fromPlace, fromDateTime, toPlace, toDateTime, passengerA, passengerB);
		
		Trip nextTrip = new Trip(toPlace, DateTimeUtils.fromDateTime(2018, 9, 15, 9, 30), fromPlace,
				DateTimeUtils.fromDateTime(2018, 9, 15, 22, 30), passengerA, passengerB);

		TourDispatcher dispatcher = new AlohaTourDispatcher(tourService);
		assertThat(dispatcher.tourCount(), is(0l));

		Customer cust1 = newCustomer("Alice Tan");
		Tour tour = dispatcher.newBooking(cust1);
		tour.bookTrips(firstTrip, nextTrip);

		Vehicle starex = new Vehicle("JJ 9981");
		Driver driver = new Driver("Daniel");
		
		vehicleRepository.save(starex);
		driverRepository.save(driver);
		
		tourService.arrangeVehicleWithDriver(tour, starex, driver);
		
		// use JJ 9982 for a new tour
		
		Vehicle anotherCar = new Vehicle("JJ 9982");
		Driver anotherDriver = new Driver("Kong");
		
		vehicleRepository.save(anotherCar);
		driverRepository.save(anotherDriver);
		
		Passenger passengerC = new Passenger("Book Worm");
		passengerRepository.save(passengerC);
		
		Customer cust2 = newCustomer("Bobby Lim");
		Tour anotherTour = dispatcher.newBooking(cust2);
		anotherTour.bookSingleTrip(new Trip(newPlace("Malaysia", "Kluang"), 
				DateTimeUtils.fromDateTime(2018, 9, 15, 9, 30),
				newPlace("Malaysia", "Kuantan"), 
				DateTimeUtils.fromDateTime(2018, 9, 15, 20, 30), 
				passengerC));
		
		tourService.arrangeVehicleWithDriver(anotherTour, anotherCar, anotherDriver);

		// then arrange the same car for earlier trip
		try {
			tourService.arrangeVehicleWithDriverForTrip(nextTrip, anotherCar, anotherDriver);
			fail("should throw VehicleNotAvailableException");
		}
		catch (VehicleNotAvailableException ex) {
			assertThat(ex.getVehicle().getPlateNumber(), is("JJ 9982"));
		}
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
		passengerRepository.saveAll(Arrays.asList(passengerA, passengerB));

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
		
		assertThat(this.passengerRepository.count(), is(2l));
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
