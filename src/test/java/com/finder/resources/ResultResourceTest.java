package com.finder.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class ResultResourceTest extends ResourceTest {
	@Test
	public void resultsTest() {
		Response resultResponse = target("/results/0").queryParam(ENDECA_HOST_PARAM, ENDECA_HOST_VALUE)
				.queryParam(ENDECA_PORT_PARAM, ENDECA_PORT_VALUE).queryParam("expanded", "1000000")
				.queryParam("term", "\"NIV Bible\"").request(MediaType.APPLICATION_JSON).get();

		// make sure we get a 200 response code
		checkResponseCode(resultResponse, "Result Test");
	}
}
