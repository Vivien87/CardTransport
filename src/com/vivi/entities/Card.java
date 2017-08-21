package com.vivi.entities;

import java.math.BigDecimal;

public class Card {
	
	private long id;
	private BigDecimal balance;
	
	public Card(long id, BigDecimal initialValue){
		this.id = id;
		balance = initialValue;
		if (initialValue == null || BigDecimal.ZERO.compareTo(initialValue) > 0){
			throw new IllegalArgumentException("InitialValue: "+initialValue+" should be a positive value");
		}
	}
	
	public void debit(BigDecimal amount){
		if(balance.compareTo(amount) < 0){
			throw new IllegalArgumentException("Not sufficient funds in the card. Card balance: "+this.balance+", amount seeked: "+amount);
		}
		this.balance = this.balance.subtract(amount);
	}
	
	public void credit(BigDecimal amount){
		this.balance = this.balance.add(amount);
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", balance=" + balance + "]";
	}

}