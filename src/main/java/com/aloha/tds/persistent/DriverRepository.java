package com.aloha.tds.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aloha.tds.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

}
