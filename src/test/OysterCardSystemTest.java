package test;

import java.io.IOException;
import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.vivi.entities.Card;
import com.vivi.service.OysterCardSystem;
import com.vivi.service.impl.OysterCardSystemImpl;

public class OysterCardSystemTest {
	
	private Card card;
	private static final BigDecimal PURCHASE_AMOUNT = BigDecimal.valueOf(30.0);
	private OysterCardSystem oysterCardSystem;
	private static final BigDecimal VALID_INPUT_FILE_SECOND_FINAL_CARD_BALANCE = BigDecimal.valueOf(25.00);
	private static final BigDecimal VALID_INPUT_FILE_FIRST_FINAL_CARD_BALANCE = BigDecimal.valueOf(25.20);
	private static final BigDecimal VALID_INPUT_FILE_THIRD_FINAL_CARD_BALANCE = BigDecimal.valueOf(26.80);
	private static final BigDecimal VALID_INPUT_FILE_FOURTH_FINAL_CARD_BALANCE = BigDecimal.valueOf(28.00);
	private static final String VALID_INPUT_FILE_FIRST = "travelData.txt";
	private static final String VALID_INPUT_FILE_SECOND = "travelData1.txt";
	private static final String VALID_INPUT_FILE_THIRD = "travelData2.txt";
	private static final String VALID_INPUT_FILE_FOURTH = "travelData3.txt";
	private static final String INVALID_INPUT_FILE_FIRST = "invalidTravelData.txt";
	private static final String INVALID_INPUT_FILE_SECOND = "invalidTravelData1.txt";
	
	@BeforeMethod
	public void setup() throws IOException {
		oysterCardSystem = new OysterCardSystemImpl();
		oysterCardSystem.initializeSystem("stationsAndZones.txt", "zonesAndFares.txt");
		this.card = oysterCardSystem.purchaseNewCard(PURCHASE_AMOUNT);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void doTripWithInvalidRandomCard() throws Exception{
		oysterCardSystem.takeTrips(new Card(1, BigDecimal.valueOf(-1.0)), VALID_INPUT_FILE_FIRST);
	}

	@Test(dataProvider="invalidTripInputs", expectedExceptions=IllegalArgumentException.class)
	public void doTripWithInvalidTripInputs(String inputFileName) throws Exception {
		takeTrips(inputFileName);
	}
	
	@Test(dataProvider="validTripInputs")
	public void doTripWithValidTripInputs(String inputFileName, BigDecimal expectedCardBalance) throws Exception {
		takeTrips(inputFileName);
		Assert.assertTrue(card.getBalance().compareTo(expectedCardBalance) == 0);
	}

	@DataProvider(name="validTripInputs")
	private Object[][] getValidTripInputAndExpectedOutputPairs(){
		return new Object[][]{
			{VALID_INPUT_FILE_FIRST, VALID_INPUT_FILE_FIRST_FINAL_CARD_BALANCE},
			{VALID_INPUT_FILE_SECOND, VALID_INPUT_FILE_SECOND_FINAL_CARD_BALANCE},
			{VALID_INPUT_FILE_THIRD, VALID_INPUT_FILE_THIRD_FINAL_CARD_BALANCE},
			{VALID_INPUT_FILE_FOURTH, VALID_INPUT_FILE_FOURTH_FINAL_CARD_BALANCE}
		};
	}

	private OysterCardSystemTest takeTrips(String travelDataFilePath) throws Exception {
		oysterCardSystem.takeTrips(card, travelDataFilePath);
		return this;
	}

	@DataProvider(name="invalidTripInputs")
	private Object[][] getInvalidTripInputAndExpectedOutputPairs(){
		return new Object[][]{
			{INVALID_INPUT_FILE_FIRST},
			{INVALID_INPUT_FILE_SECOND}
		};
	}

}
