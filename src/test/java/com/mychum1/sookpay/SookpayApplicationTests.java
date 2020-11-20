package com.mychum1.sookpay;

import com.mychum1.sookpay.controller.ApiController;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.service.SprayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = SookpayApplication.class)
@AutoConfigureMockMvc
class SookpayApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private ApiController apiController;

	@Autowired
	private SprayService sprayService;

	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(apiController)
				.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
				.build();
	}

	/*
	* 돈 뿌리기 정상 시나리오
	 */
	@Test
	public void testPostSpray() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("personnel","3");
		params.add("money","1000");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/spray").params(params)
				.header("X-USER-ID","ksko").header("X-ROOM-ID","room3"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	/**
	 * 돈받기 서비스 정상 시나리오
	 */
	@Test
	public void testGetSprayService() throws NotValidSprayException {

		sprayService.getSpray("6kV","ksko2","room3");
	}

	/*
	 * 뿌리기 상세정보 조회
	 */
	@Test
	public void testGetSprayInfo() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("token","6kV");
		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray/info").params(params)
				.header("X-USER-ID","ksko").header("X-ROOM-ID","room3"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}


}
