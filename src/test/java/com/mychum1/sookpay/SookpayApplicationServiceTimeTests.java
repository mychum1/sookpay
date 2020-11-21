package com.mychum1.sookpay;

import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.service.SprayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SookpayApplication.class)
@ActiveProfiles("test")
class SookpayApplicationServiceTimeTests {

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

	/**
	 * 2.2번 테스트 케이스 : 요청 가능 시간 후 요청 시나리오
	 */
	@Test
	public void testGetSpray() throws Exception {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();
		assertThrows(NotValidSprayException.class, () ->sprayService.getSpray(token, userId, roomId));
	}

	/**
     * 3.3 케이스 : 조회 기간 후에 조회 요청 시나리오
	 * @throws Exception
	 */
	@Test
	public void testGetSprayInfo() throws Exception {
		Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
		String token = spray.getToken();
		assertThrows(NotValidSprayException.class, () ->sprayService.getSprayDetail(token, userId, roomId));

	}


}
