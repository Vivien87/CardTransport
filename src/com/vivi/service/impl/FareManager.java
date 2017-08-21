package com.vivi.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vivi.entities.Fare;
import com.vivi.entities.Station;
import com.vivi.entities.TransporationMode;
import com.vivi.entities.Trip;
import com.vivi.entities.Zone;

import java.util.Set;
import java.util.StringTokenizer;

public class FareManager {
	
	private static final String FARE_TRANSPORTATION_MODE_DELIM = "@";
	private static final String WILD_CARD = "*";
	private List<Fare> fares = new ArrayList<>();
	private BigDecimal maxFare = new BigDecimal(0);
	private Map<TransporationMode, Fare> genericFares = new HashMap<>();

	public void initialize(Map<String, String> zonesFaresAndTransportationModes){
		if (zonesFaresAndTransportationModes == null || zonesFaresAndTransportationModes.size() == 0){
			return;
		}
		for(Entry<String, String> mapEntry: zonesFaresAndTransportationModes.entrySet()){
			String key = mapEntry.getKey();
			boolean isGenericFare = false;
			Set<Zone> zoneList = null;
			if(WILD_CARD.equals(key)){
				isGenericFare = true;
			}
			else {
				zoneList = getZoneList(key);
			}
			StringTokenizer fareAndTransportationModes = new StringTokenizer(mapEntry.getValue(), FARE_TRANSPORTATION_MODE_DELIM);
			BigDecimal fareAmount = getFareAmount(fareAndTransportationModes);
			String transporationMode = fareAndTransportationModes.nextToken();
			TransporationMode mode = TransporationMode.getValueOf(transporationMode);
			if(mode==null){
		         throw new IllegalArgumentException("Fares are not provided correctly. Invalid transportation Mode");
		    }
			Fare fare = new Fare(zoneList, fareAmount, isGenericFare);
			if(isGenericFare){
				genericFares.put(mode, fare);
			}
			fares.add(fare);
			if(maxFare.compareTo(fareAmount) < 0){
				maxFare = fareAmount;
			}
		}
	}

	private BigDecimal getFareAmount(StringTokenizer fareAndTransportationModes) {
		String fare = fareAndTransportationModes.nextToken();
		if(fare==null || fare.isEmpty()){
			throw new IllegalArgumentException("Fares are not provided correctly. Invalid Fare");
		}
		return new BigDecimal(fare);
	}

	private Set<Zone> getZoneList(String key) {
		Set<Zone> zoneList = new HashSet<>();
		StringTokenizer zones = new StringTokenizer(key, ",");
		int zoneCount = zones.countTokens();
		for (int i = 0; i < zoneCount; i++) {
			String zoneId = zones.nextToken();
			if(zoneId==null || zoneId.isEmpty()){
				throw new IllegalArgumentException("Fares are not provided correctly. Invalid Zone list");
			}
			Zone zone = new Zone(Integer.valueOf(zoneId));
			zoneList.add(zone);
		}
		return zoneList;
	}
	
	public BigDecimal getMaxFare(){
		return maxFare;
	}

	public BigDecimal calculateCost(List<Trip> trips) {
		BigDecimal totalCost = BigDecimal.valueOf(0.0);
		boolean applySpecificFare = false;
		Set<Station> uniqueStations = new HashSet<>();
		for(Trip trip: trips){
			TransporationMode transporationMode = trip.getTransporationMode();
			Fare genericFare = genericFares.get(transporationMode);
			if(genericFare!=null){
				totalCost = totalCost.add(genericFare.getAmount());
			}
			else{
				applySpecificFare = true;
				uniqueStations.add(trip.getFrom());
				uniqueStations.add(trip.getTo());
			}
		}
		BigDecimal minFare = BigDecimal.valueOf(0.0);
		if (applySpecificFare) {
			for (Fare fare : fares) {
				if (isFareLessAndCoversAllZones(uniqueStations, minFare, fare)) {
					minFare = fare.getAmount();
				}
			}
			totalCost = totalCost.add(minFare);
		}
		return totalCost;
	}

	private boolean isFareLessAndCoversAllZones(Set<Station> uniqueStations, BigDecimal minFare, Fare fare) {
		if(fare.isGenericFare() || (minFare.compareTo(BigDecimal.valueOf(0.0)) != 0 && minFare.compareTo(fare.getAmount()) < 0)){
			return false;
		}
		for(Station station: uniqueStations){
			if(Collections.disjoint(station.getZones(), fare.getZones())){
				return false;
			}
		}
		return true;
	}

	public boolean isGenericFareApplicable(TransporationMode transporationMode){
		return genericFares.get(transporationMode)!=null;
	}

}