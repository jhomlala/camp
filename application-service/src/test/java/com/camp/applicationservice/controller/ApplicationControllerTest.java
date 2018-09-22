package com.camp.applicationservice.controller;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import com.camp.applicationservice.domain.ApplicationCreateRequest;
import com.camp.applicationservice.domain.ApplicationUpdateRequest;
import com.camp.applicationservice.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationControllerTest {

	private static final ObjectMapper mapper = new ObjectMapper();

	@InjectMocks
	private ApplicationController accountController;

	@Mock
	private ApplicationService applicationService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
	}

	@Test
	public void shouldSaveValidApplication() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName("TestApp");
		applicationCreateRequest.setOs("Android");
		applicationCreateRequest.setPackageName("com.testapp");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
	
	@Test
	public void shouldFailedOnValidationTryingToSaveApplicationWithNullOs() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName("TestApp");
		applicationCreateRequest.setOs("Blackberry");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void shouldFailedOnValidationTryingToSaveApplicationWithEmptyOs() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName("TestApp");
		applicationCreateRequest.setOs("");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void shouldFailedOnValidationTryingToSaveApplicationWithInvalidOs() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName("TestApp");
		applicationCreateRequest.setOs("BlackBerry");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	public void shouldFailedOnValidationTryingToSaveApplicationWithInvalidName() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName("");
		applicationCreateRequest.setOs("BlackBerry");
		applicationCreateRequest.setPackageName("com.testapp");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
	
	
	@Test
	public void shouldFailedOnValidationTryingToSaveApplicationWithINullName() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName(null);
		applicationCreateRequest.setOs("BlackBerry");
		applicationCreateRequest.setPackageName("com.testapp");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void shouldFailedOnValidationTryingToSaveApplicationWithTooLongName() throws Exception {

		ApplicationCreateRequest applicationCreateRequest = new ApplicationCreateRequest();
		applicationCreateRequest.setName("123456789012345678901234567890");
		applicationCreateRequest.setOs("BlackBerry");
		applicationCreateRequest.setPackageName("com.testapp");
		String json = mapper.writeValueAsString(applicationCreateRequest);

		mockMvc.perform(post("/applications/").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void shouldFailedOnValidationTryingToFindApplicationWithInvalidApplicationId() throws Exception {
		mockMvc.perform(get("/applications/1")).andExpect(status().isNotFound());
	}
	
	@Test
	public void shouldFailedOnValidationTryingToUpdateApplicationWithInvalidApplicationId() throws Exception {
		ApplicationUpdateRequest applicationUpdateRequest = new ApplicationUpdateRequest();
		applicationUpdateRequest.setFirebaseKey("0");
		applicationUpdateRequest.setName("");
		String json = mapper.writeValueAsString(applicationUpdateRequest);

		mockMvc.perform(put("/applications/1").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void shouldFindAllApplications() throws Exception {
		mockMvc.perform(get("/applications/")).andExpect(status().isOk());
	}
	
}