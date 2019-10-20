package com.aloha.tds;

public class TimeClashException extends RuntimeException {

	private static final long serialVersionUID = -2114433887411952123L;

	private final transient Trip clashingTrip;
	
	private final transient Trip clashWith;
	
	public TimeClashException(Trip clashingTrip, Trip clashWith) {
		super(String.format("Trip %s is clashing with another trip %s, please check the time again", clashingTrip, clashWith));
		this.clashingTrip = clashingTrip;
		this.clashWith = clashWith;
	}
	
	public Trip clashingTrip() {
		return this.clashingTrip;
	}
	
	public Trip clashWith() {
		return this.clashWith;
	}
}
