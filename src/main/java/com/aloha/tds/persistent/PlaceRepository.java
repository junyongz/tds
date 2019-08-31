package com.aloha.tds.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloha.tds.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

}
