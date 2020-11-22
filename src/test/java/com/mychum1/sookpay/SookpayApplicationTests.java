package com.mychum1.sookpay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mychum1.sookpay.controller.ApiController;
import com.mychum1.sookpay.domain.Response;
import com.mychum1.sookpay.service.SprayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = SookpayApplication.class)
@AutoConfigureMockMvc
class SookpayApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private ApiController apiController;

	@Autowired
	private SprayService sprayService;

	private ObjectMapper objectMapper;

	private String personnel;
	private String money;
	private String userId;
	private String roomId;

	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(apiController)
				.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
				.build();
		objectMapper=new ObjectMapper();
		personnel="3";
		money="1000";
		userId="ksko";
		roomId="room5";
	}

	/*
	* 1번 케이스 : 돈 뿌리기 정상 시나리오
	 */
	@Test
	public MvcResult testPostSpray() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("personnel",personnel);
		params.add("money",money);
		return mockMvc.perform(MockMvcRequestBuilders.post("/api/spray").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID", roomId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

	}

	/**
	 * 1.1 케이스 : 받기 인원을 0인으로 지정했을 경우
	 * @throws Exception
	 */
	@Test
	public void testPostSprayParamPersonnel() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("personnel","0");
		params.add("money","1000");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/spray").params(params)
				.header("X-USER-ID","ksko").header("X-ROOM-ID","room4"))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();
	}

	/**
	 * 돈뿌리기 등록 서비스 테스트
	 */
	@Test
	public void testPostSprayService() {
		sprayService.postSpray(userId, roomId, Long.parseLong(money), Integer.parseInt(personnel));
	}

	/**
	 * 2번 테스트 케이스 : 돈 받기 성공 시나리오
	 */
	@Test
	public void testGetSpray() throws Exception {


		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID","ksko1").header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
	}

	private String testPostAndGetToken() throws Exception {
		Response<String> spray = getResponseFromResult(testPostSpray());
		return spray.getData();
	}

	private Response<String> getResponseFromResult(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
		return objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
	}

	/**
	 * 2.1 테스트 케이스 : 같은 방 사용자가 아닐 경우
	 */
	@Test
	public void testGetSprayNotSameRoom() throws Exception {

		MvcResult result = testPostSpray();
		Response<String> spray = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
		String token = spray.getData();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID","ksko1").header("X-ROOM-ID","room-another"))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();
	}



	/**
	 * 2.3 테스트 케이스 : 돈을 뿌린 사용자가 받기 요청을 했을 경우
	 */
	@Test
	public void testGetSpraySameRequester() throws Exception {

		MvcResult result = testPostSpray();
		Response<String> spray = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
		String token = spray.getData();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();
	}

	/**
	 * 2.4 테스트 케이스 : 이미 받은 사용자가 다시 요청을 했을 경우
	 * @throws Exception
	 */
	@Test
	public void testGetSprayAlreadyTaken() throws Exception {
		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID","ksko1").header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID","ksko1").header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();
	}

	/**
	 * 2.5 테스트 케이스 : 이미 전부 받았을 경우
	 * @throws Exception
	 */
	@Test
	public void testGetSprayAllTaken() throws Exception {
		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		for (int i = 0; i < Integer.parseInt(personnel); i++) {
			mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
					.header("X-USER-ID","ksko"+i).header("X-ROOM-ID",roomId))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andReturn();
		}
		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID","another").header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();

	}


	/*
	 * 3번 케이스 : 뿌리기 상세정보 조회 성공 시나리오
	 */
	@Test
	public void testGetSprayInfo() throws Exception {
		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID","ksko1").header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray/info").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
	}

	/*
	 * 3.1번 케이스 : 조회할 뿌리기 정보가 없을 경우
	 */
	@Test
	public void testGetSprayInfoNoSpray() throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token","test");

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray/info").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();
	}

	/*
	 * 3.2번 케이스 : 뿌리지 않은 사용자가 조회를 요청했을 경우
	 */
	@Test
	public void testGetSprayInfoNoSprayUser() throws Exception {
		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray/info").params(params)
				.header("X-USER-ID","another").header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andReturn();
	}




}
