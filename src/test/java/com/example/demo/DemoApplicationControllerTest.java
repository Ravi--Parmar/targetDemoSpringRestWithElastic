package com.example.demo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.controller.DemoApplicationController;
import com.example.demo.dao.ElasticSearchDao;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DemoApplicationController.class, secure = false)
public class DemoApplicationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	ElasticSearchDao elasticSearchDao;
	
	String exampleCourseJson = 
	"{\"index\":\"testindex\",\"id\":\"testid\",\"data\":{\"name\":\"testentity\",\"prize\":\"testprize\"}}";

	@Test
	public void addDocumentTest() throws Exception {
			// Send course as body to /students/Student1/courses
			RequestBuilder requestBuilder = MockMvcRequestBuilders
					.put("/target")
					.accept(MediaType.APPLICATION_JSON).content(exampleCourseJson)
					.contentType(MediaType.APPLICATION_JSON);

			MvcResult result = mockMvc.perform(requestBuilder).andReturn();
			
			MockHttpServletResponse response = result.getResponse();
			System.out.println(result.getResponse());
			assertEquals(HttpStatus.OK.value(), response.getStatus());
			/*String expected = "{\"errorMessage\":\"\",\"successMessage\":\"UPDATED\",\"id\":\"testid\",\"data\":{\"name\":\"testentity\",\"prize\":\"testprize\"}}";
			System.out.println(result.getResponse().getContentAsString());
			JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), true);*/

		

		
	}
	@Test
	public void getDocumentTest() throws Exception {
			// Send course as body to /students/Student1/courses
			RequestBuilder requestBuilder = MockMvcRequestBuilders
					.get("/target/testindex/testid");

			MvcResult result = mockMvc.perform(requestBuilder).andReturn();
			
			MockHttpServletResponse response = result.getResponse();
			System.out.println(result.getResponse());
			assertEquals(HttpStatus.OK.value(), response.getStatus());
			/*String expectedString="{\"errorMessage\":\"\",\"successMessage\":\"Fetched\",\"id\":\"testid\",\"data\":{\"name\":\"testentity\",\"prize\":\"testprize\"}}";
			System.out.println(result.getResponse().getContentAsString());
			JSONAssert.assertEquals(expectedString, result.getResponse().getContentAsString(), true);*/

		

		
	}




}