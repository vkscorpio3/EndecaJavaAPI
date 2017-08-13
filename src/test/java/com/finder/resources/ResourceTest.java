package com.finder.resources;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONObject;
import org.junit.Assert;

import com.finder.filter.ApplicationRequestFilter;
import com.finder.resources.FilterResource;
import com.finder.resources.RecordResource;
import com.finder.resources.ResultResource;

public class ResourceTest extends JerseyTest {
	public String ENDECA_HOST_PARAM = "endecaHost";
	public String ENDECA_HOST_VALUE = "SOME_ENDECA_HOST";
	public String ENDECA_PORT_PARAM = "endecaPort";
	public String ENDECA_PORT_VALUE = "SOME_ENDECA_PORT";
	
	@Override
	protected Application configure() {
		return new ResourceConfig(RecordResource.class, FilterResource.class, ResultResource.class,
				ApplicationRequestFilter.class);
	}

	public void checkResponseCode(Response response, String msg) {
		Assert.assertEquals(msg + " - Response code - ", 200, response.getStatus());
	}

	public void checkMetaData(JSONObject json) {

	}

}
