package com.mychum1.sookpay;

import com.mychum1.sookpay.controller.ApiController;
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
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

@SpringBootTest(classes = SookpayApplication.class)
@AutoConfigureMockMvc
class SookpayApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private ApiController apiController;

	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders.standaloneSetup(apiController)
				.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
				.build();
	}

//	@Test
	public void testPostSpray() throws Exception {

//		mockMvc.perform(MockMvcRequestBuilders.get("/api/spray"))
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andDo(MockMvcResultHandlers.print())
//				.andReturn();

	}

}
