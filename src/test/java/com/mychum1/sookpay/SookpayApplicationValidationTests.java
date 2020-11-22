package com.mychum1.sookpay;

import com.mychum1.sookpay.common.RandomTokenProcessor;
import com.mychum1.sookpay.common.ValidationProcessor;
import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.service.SprayService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SookpayApplicationValidationTests {

	private Integer personnel;
	private Long money;
	private String userId;
	private String roomId;
	private String token;
	private String anotherRequester;

	private Spray spray;
	private List<Receipt> receiptList = new ArrayList<>();

	@BeforeEach
	public void init() {
		personnel=2;
		money=1000L;
		userId="ksko";
		roomId="room5";
		anotherRequester = "another";
		token = RandomTokenProcessor.makeRandomToken();

		spray = new Spray();
		spray.setInitDate(Instant.now().getEpochSecond());
		spray.setPersonnel(personnel);
		spray.setAmountOfMondey(money);
		spray.setRoomId(roomId);
		spray.setRequester(userId);
		spray.setToken(RandomTokenProcessor.makeRandomToken());
		spray.setId(1);

		Receipt receipt = new Receipt();
		receipt.setMoney(money/personnel);
		receipt.setRecipient(anotherRequester);
		receipt.setStatus(false);
		receipt.setRoomId(roomId);
		receipt.setToken(token);
		receipt.setSpray(spray);
		receipt.setReceiptId(1);
		receipt.setReceiptOrder(1);

		receiptList.add(receipt);
	}

	/*
	* 이미 전부 받은 뿌리기 건인지확인한다.
	 */
	@Test
	public void testCheckAllTaken() throws Exception {
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.checkAllTaken(null));
	}

	/*
	 * 뿌리기 요청자인지 확인한다.
	 */
	@Test
	public void testIsOwnerOfSpray(){
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.isOwnerOfSpray("another", spray));
	}

	/**
	 * 유효한 뿌리기 건이 있는지 확인한다.
	 */
	@Test
	public void testIsValidSpray() throws NotValidSprayException {
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.isValidSpray(null));
	}

	/**
	 * 유효시간 내의 요청인지 확인한다.
	 */
	@Test
	public void testIsValidTime() throws NotValidSprayException {
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.isValidTime(spray, 0L));
	}

	/**
	 * 같은 방의 사용자인지 확인한다.
	 */
	@Test
	public void testIsSameRoom() throws NotValidSprayException {
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.isSameRoom(spray, "another"));
	}

	/**
	 * 이미 받은 사용자인지 확인한다.
	 */
	@Test
	public void testIsValidRequester() throws NotValidSprayException {
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.isValidRequester(receiptList, anotherRequester));
	}

	/**
	 * 뿌리지 않은 사용자가 조회 요청을 했을 경우
	 */
	@Test
	public void testIsSameRequester() throws NotValidSprayException {
		assertThrows(NotValidSprayException.class, () -> ValidationProcessor.isSameRequester(spray, userId));
	}


}
