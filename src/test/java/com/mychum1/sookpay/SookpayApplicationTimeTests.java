package com.mychum1.sookpay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mychum1.sookpay.controller.ApiController;
import com.mychum1.sookpay.domain.Response;
import com.mychum1.sookpay.service.SprayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

@SpringBootTest(classes = SookpayApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SookpayApplicationTimeTests {

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

	/**
	 * 2.2번 테스트 케이스 : 요청 가능 시간 후 요청 시나리오
	 */
	@Test
	public void testGetSpray() throws Exception {


		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	private String testPostAndGetToken() throws Exception {
		MvcResult result = testPostSpray();
		Response<String> spray = objectMapper.readValue(result.getResponse().getContentAsString(), Response.class);
		return spray.getData();
	}
	public MvcResult testPostSpray() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("personnel",personnel);
		params.add("money",money);
		return mockMvc.perform(MockMvcRequestBuilders.post("/api/spray").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID", roomId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

	}

	/**
	 * 3.3 케이스 : 조회 기간 후에 조회 요청 시나리오
	 * @throws Exception
	 */
	@Test
	public void testGetSprayInfo() throws Exception {
		String token = testPostAndGetToken();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token",token);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray/info").params(params)
				.header("X-USER-ID",userId).header("X-ROOM-ID",roomId))
				.andExpect(MockMvcResultMatchers.status().is5xxServerError())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}


}
