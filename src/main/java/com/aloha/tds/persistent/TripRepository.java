package com.aloha.tds.persistent;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aloha.tds.Trip;
import com.aloha.tds.Vehicle;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

	@Query(value = "select count(*) from tds_trip where vehicle_id = ?1 and from_date <= ?3 and to_date >= ?2",
			nativeQuery = true)
	long countByVehicleArrangedForDates(Long vehicleId, Date fromDate, Date toDate);
	
	List<Trip> findByVehicle(Vehicle vehicle);
}
