package com.vivi.entities;

public class Trip {

    private Station from;
    private Station to;
    private TransporationMode transporationMode;
	private String tripDetails;
	
	public Trip(Station from, Station to, TransporationMode transporationMode, String tripDetails) {
		this.from = from;
		this.to = to;
		this.transporationMode = transporationMode;
		if(tripDetails == null){
			throw new IllegalArgumentException("Trip details can not be null");
		}
		this.tripDetails = tripDetails;
	}
	
	public Station getFrom() {
		return from;
	}

	public Station getTo() {
		return to;
	}

	public TransporationMode getTransporationMode() {
		return transporationMode;
	}

	public String getJourneyDetails() {
		return tripDetails;
	}

	@Override
	public String toString() {
		return this.tripDetails;
	}

}