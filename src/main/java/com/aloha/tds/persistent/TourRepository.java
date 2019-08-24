package com.aloha.tds.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloha.tds.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>{

}
