package com.aloha.tds;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TDS_TRIP_PASSENGER")
public class TripPassenger {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TRIP_ID")
	private Long tripId;

	@Column(name = "PASSENGER_ID")
	private Long passengerId;
	
	public TripPassenger() {
		// for JPA
	}

	public TripPassenger(Long tripId, Long passengerId) {
		this.tripId = tripId;
		this.passengerId = passengerId;
	}

	public TripPassenger(Trip trip, Passenger passenger) {
		this.tripId = trip.getId();
		this.passengerId = passenger.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTripId() {
		return tripId;
	}

	public void setTripId(Long tripId) {
		this.tripId = tripId;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	@Override
	public String toString() {
		return "TripPassenger [tripId=" + tripId + ", passengerId=" + passengerId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((passengerId == null) ? 0 : passengerId.hashCode());
		result = prime * result + ((tripId == null) ? 0 : tripId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TripPassenger other = (TripPassenger) obj;
		if (passengerId == null) {
			if (other.passengerId != null)
				return false;
		}
		else if (!passengerId.equals(other.passengerId)) {
			return false;
		}
		if (tripId == null) {
			if (other.tripId != null)
				return false;
		}
		else if (!tripId.equals(other.tripId)) {
			return false;
		}
		return true;
	}
	
}
