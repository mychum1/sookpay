package com.mychum1.sookpay;

import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.service.SprayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SookpayApplicationServiceTests {

	@Autowired
	private SprayService sprayService;

	private Integer personnel;
	private Long money;
	private String userId;
	private String roomId;

	@BeforeEach
	public void init() {
		personnel=3;
		money=1000L;
		userId="ksko";
		roomId="room5";
	}

	/*
	* 돈 뿌리기 정상 시나리오
	 */
	@Test
	public void testPostSpray() throws Exception {
		assertNotNull(sprayService.postSpray(userId, roomId, money, personnel));
	}

	/*
	 * 돈 받기 - 같은 방에 있는 사용자가 아닌 경우
	 */
	@Test
	public void testGetSprayNoSameRoom(){
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();
		assertThrows(NotValidSprayException.class, () ->sprayService.getSpray(token, "another", "another"));
	}

	/**
	 * 돈 받기 - 돈을 뿌린 사람이 요청을 했을 경우
	 */
	@Test
	public void testGetSprayNoValidRequester() {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();
		assertThrows(NotValidSprayException.class, () ->sprayService.getSpray(token, userId, roomId));
	}

	/**
	 * 돈 받기 - 돈을 뿌린 사람이 요청을 했을 경우
	 */
	@Test
	public void testGetSprayAlreadyTaken() throws NotValidSprayException {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();
		sprayService.getSpray(token, "another", roomId);
		assertThrows(NotValidSprayException.class, () ->sprayService.getSpray(token, "another", roomId));
	}

	/**
	 * 돈 받기 - 이미 전부 받았을 경우
	 */
	@Test
	public void testGetSprayAllTaken() throws NotValidSprayException {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();
		for (int i = 0; i < personnel; i++) {
			sprayService.getSpray(token, "another"+i, roomId);
		}
		assertThrows(NotValidSprayException.class, () ->sprayService.getSpray(token, "another", roomId));

	}

	/**
	 * 뿌리기 정보 조회
	 */
	@Test
	public void testGetSprayInfo() throws NotValidSprayException {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();

		assertNotNull(sprayService.getSprayDetail(token, userId, roomId));
	}

	/**
	 * 조회할 뿌리기 정보가 없을 경우
	 */
	@Test
	public void testGetSprayInfoNoData() throws NotValidSprayException {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);

		assertThrows(NotValidSprayException.class, () ->sprayService.getSprayDetail("another", userId, roomId));
	}

	/**
	 * 뿌리지 않은 사용자가 조회 요청을 했을 경우
	 */
	@Test
	public void testGetSprayInfoNoRequester() throws NotValidSprayException {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();

		assertThrows(NotValidSprayException.class, () ->sprayService.getSprayDetail(token, "another", roomId));
	}


}
