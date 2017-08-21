package com.vivi.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vivi.entities.Card;
import com.vivi.entities.Station;
import com.vivi.entities.TransporationMode;
import com.vivi.entities.Trip;
import com.vivi.service.OysterCardSystem;


public class OysterCardSystemImpl implements OysterCardSystem {
	

	private StationManager stationManager = new StationManager();
	private FareManager fareManager = new FareManager();
	private CardManager cardManager = new CardManager();
	private static final Pattern COMPILED_TUBE_JOURNEY_PATTERN = Pattern.compile("^Tube ", Pattern.CASE_INSENSITIVE);
	private static final Pattern COMPILED_BUS_JOURNEY_PATTERN = Pattern.compile("^([1-9][0-9]*) bus from ", Pattern.CASE_INSENSITIVE);
	
	@Override
	public void initializeSystem(String stationsAndZonesFilePath, String zonesAndFaresFilePath) throws IOException{
		initStations(stationsAndZonesFilePath);
		initFares(zonesAndFaresFilePath);
		System.out.println("Initialized System...");
	}

	private void initStations(String stationsAndZonesFilePath) throws IOException {
		InputStream inputStream = new FileInputStream(stationsAndZonesFilePath);
		Map<String, List<Integer>> stationNameWithZoneIds = new HashMap<>();
		Properties properties = new Properties();
		properties.load(inputStream);
		inputStream.close();
		Enumeration<Object> enumKeys = properties.keys();
		while (enumKeys.hasMoreElements()) {
			String stationName = (String) enumKeys.nextElement();
			String value = properties.getProperty(stationName);
			StringTokenizer tokens = new StringTokenizer(value, ",");
			int counter = tokens.countTokens();
			List<Integer> zoneList = new ArrayList<>();
			for (int i = 0; i < counter; i++) {
				String element = tokens.nextToken();
				zoneList.add(Integer.valueOf(element));
			}
			stationNameWithZoneIds.put(stationName, zoneList);
		}
		stationManager.initialize(stationNameWithZoneIds);
	}
	
	private void initFares(String zonesAndFaresFilePath) throws IOException {
		Map<String, String> zonesFaresAndTransportationModes = new HashMap<>();
		InputStream inputStream = new FileInputStream(zonesAndFaresFilePath);
		Properties properties = new Properties();
		properties.load(inputStream);
		inputStream.close();
		Enumeration<Object> enumKeys = properties.keys();
		while (enumKeys.hasMoreElements()) {
			String key = (String) enumKeys.nextElement();
			String value = properties.getProperty(key);
			StringTokenizer zoneGroups = new StringTokenizer(key, ";");
			int fareGroupsCount = zoneGroups.countTokens();
			for (int i = 0; i < fareGroupsCount; i++) {
				String zoneGroup = zoneGroups.nextToken();
				zonesFaresAndTransportationModes.put(zoneGroup, value);
			}
		}
		fareManager.initialize(zonesFaresAndTransportationModes);
	}
	
	private Trip initTrip(String exactTripString, TransporationMode transporationMode) {
		int toindex = exactTripString.indexOf(" to ");
		if(toindex==-1){
			throw new IllegalArgumentException("Provided Trip data in invalid");
		}
		String fromStationName = exactTripString.substring(0, toindex);
		String toStationName = exactTripString.substring(toindex+4, exactTripString.length());
		Station from = stationManager.get(fromStationName);
		Station to = stationManager.get(toStationName);
		if((from == null || to == null) && !fareManager.isGenericFareApplicable(transporationMode)){
			throw new IllegalArgumentException("Provided From/To Station in the trip is not known to the system and a generic fare for transportationMode: "+transporationMode+" is not available too");
		}
		return new Trip(from, to, transporationMode, exactTripString);
	}
	
	@Override
	public void takeTrips(Card card, String travelDataFilePath) throws Exception {
		List<Trip> trips = extractTripsFrom(travelDataFilePath);
		takeTrips(card, trips);
	}

	@Override
	public void takeTrips(Card card, List<Trip> trips) {
		List<Trip> tripsTakenTillNow = new ArrayList<>();
		BigDecimal lastTripCost ;
		BigDecimal tripCost = BigDecimal.valueOf(0.0);
		System.out.println("Taking trips now");
		for(int i =0; i < trips.size(); i++){
			cardManager.deductForTrip(card, fareManager.getMaxFare());
			Trip trip = trips.get(i);
			System.out.println("Deducted max applicable fare for trip: "+trip.getJourneyDetails()+". "+card);
			tripsTakenTillNow.add(trip);
			lastTripCost = tripCost;
			tripCost = fareManager.calculateCost(tripsTakenTillNow);
			System.out.println("Total trip cost until now: "+tripCost);
			BigDecimal refund = fareManager.getMaxFare().subtract(tripCost.subtract(lastTripCost));
			cardManager.applyRefund(card,  refund);
			System.out.println("Made adjustments after this trip. "+card);
		}
	}

	private List<Trip> extractTripsFrom(String travelDataFilePath) throws IOException{
		InputStream inputStream = new FileInputStream(travelDataFilePath);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		List<Trip> trips = new ArrayList<>();
		String journeyDetails;
		while ((journeyDetails = bufferedReader.readLine()) != null) {
			Trip trip;
			Matcher tubeJourneymatcher = COMPILED_TUBE_JOURNEY_PATTERN.matcher(journeyDetails);
			Matcher busJourneymatcher = COMPILED_BUS_JOURNEY_PATTERN.matcher(journeyDetails);
			if (tubeJourneymatcher.find()) {
				String exactTripString = COMPILED_TUBE_JOURNEY_PATTERN.matcher(journeyDetails).replaceAll("");
				trip = initTrip(exactTripString, TransporationMode.TUBE);
			}
			else if (busJourneymatcher.find()) {
				String exactJourneyString = COMPILED_BUS_JOURNEY_PATTERN.matcher(journeyDetails).replaceAll("");
				trip = initTrip(exactJourneyString, TransporationMode.BUS);
			}
			else {
				throw new IllegalArgumentException("Provided Trip details are not in a proper format. Refer: "+journeyDetails);
			}
			trips.add(trip);
		}
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		return trips;
	}

	@Override
	public void topUp(Card card, BigDecimal topUp) {
		cardManager.topUp(card, topUp);
	}

	@Override
	public Card purchaseNewCard(BigDecimal initialAmount) {
		return cardManager.getNew(initialAmount);
	}

}