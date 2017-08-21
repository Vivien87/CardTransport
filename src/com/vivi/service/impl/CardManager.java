package com.vivi.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.vivi.entities.Card;

public class CardManager {

	private Map<Long, Card> cards = new HashMap<>();
	private long range = 12345l;
	private Random r = new Random();
	
	public Card getNew(BigDecimal initialAmount){
		long id = (long)(r.nextDouble()*range);
		Card card = new Card(id, initialAmount);
		cards.put(id, card);
		System.out.println("New card has been purchased from the system. "+card);
		return card;
	}

	public void topUp(Card card, BigDecimal topUp){
		lookup(card).credit(topUp);
		System.out.println("Top up done for amount: "+topUp+". "+card);
	}

	private Card lookup(Card card) {
		Card cardInSystem = cards.get(card.getId());
		if(cardInSystem==null){
			throw new IllegalArgumentException("Provided card is not known to the system");
		}
		return cardInSystem;
	}
	
	public void deductForTrip(Card card, BigDecimal maxFare) {
		lookup(card).debit(maxFare);
	}

	public BigDecimal getBalance(Card card) {
		return lookup(card).getBalance();
	}

	public void applyRefund(Card card, BigDecimal refund) {
		lookup(card).credit(refund);
	}

}