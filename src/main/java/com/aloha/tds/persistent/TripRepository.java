package com.aloha.tds.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloha.tds.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

	
	
}
