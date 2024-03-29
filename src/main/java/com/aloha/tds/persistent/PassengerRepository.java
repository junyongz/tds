package com.aloha.tds.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloha.tds.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

}
