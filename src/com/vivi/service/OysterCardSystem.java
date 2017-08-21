package com.vivi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.vivi.entities.Card;
import com.vivi.entities.Trip;

public interface OysterCardSystem {

	Card purchaseNewCard(BigDecimal initialAmount);

	void initializeSystem(String stationsAndZonesFilePath, String zonesAndFaresFilePath) throws IOException;

	void takeTrips(Card card, String travelDataFilePath) throws Exception;


	void takeTrips(Card card, List<Trip> trips);

	void topUp(Card card, BigDecimal topUp);

}