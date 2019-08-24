package com.aloha.tds;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends CrudRepository<Tour, UUID> {

}
