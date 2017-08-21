package com.vivi.demo.client;

import java.math.BigDecimal;

import com.vivi.entities.Card;
import com.vivi.service.OysterCardSystem;
import com.vivi.service.impl.OysterCardSystemImpl;


public class OysterCardSystemAppClient {

	private static final String ZONES_AND_FARES_TXT = "zonesAndFares.txt";
	private static final String STATIONS_AND_ZONES_TXT = "stationsAndZones.txt";
	private static final String TRAVEL_DATA_TXT = "travelData.txt";
	private OysterCardSystemAppClient(){}
	
	public static void main(String args[]) throws Exception {
		
		OysterCardSystem oysterCardSystemManager = new OysterCardSystemImpl();
		oysterCardSystemManager.initializeSystem(STATIONS_AND_ZONES_TXT, ZONES_AND_FARES_TXT);

		System.out.println("*********************************************************************************************************************");
		Card card = oysterCardSystemManager.purchaseNewCard(BigDecimal.valueOf(0.0));
		
		System.out.println("*********************************************************************************************************************");
		BigDecimal topUp = new BigDecimal(30);
		oysterCardSystemManager.topUp(card, topUp);
		System.out.println("*********************************************************************************************************************");
		
		oysterCardSystemManager.takeTrips(card, TRAVEL_DATA_TXT);
		
		System.out.println("*********************************************************************************************************************");
		System.out.println("All trips completed. "+card);
	}

}