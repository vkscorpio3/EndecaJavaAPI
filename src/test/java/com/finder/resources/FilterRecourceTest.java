package com.finder.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class FilterRecourceTest extends ResourceTest {
	@Test
	public void filtersTest() {
		Response filterResponse = target("/filters/1000000").queryParam(ENDECA_HOST_PARAM, ENDECA_HOST_VALUE)
				.queryParam(ENDECA_PORT_PARAM, ENDECA_PORT_VALUE).request(MediaType.APPLICATION_JSON).get();

		// make sure we get a 200 response code
		checkResponseCode(filterResponse, "Filter Test");
		String jsonString = filterResponse.readEntity(String.class);
		JSONObject json = new JSONObject(jsonString);

		// verify category filter is open (a.k.a. children size > 0)
		JSONObject filters = json.getJSONObject("data").getJSONObject(("filters"));
		JSONArray children = filters.getJSONArray("Browse Products").getJSONObject(0).getJSONArray("children");
		Assert.assertTrue("Error - Category filter is open - ", (children.length() > 0));
	}
}
