package com.ideaportal;

import org.aspectj.lang.annotation.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class IdeaPortalTextApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before(value = "")
	public void setUp()
	{
		setMockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());

	}
	public MockMvc getMockMvc() {
		return mockMvc;
	}
	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

}
