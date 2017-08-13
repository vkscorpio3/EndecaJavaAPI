package com.finder.resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class RecordResourceTest extends ResourceTest {
	private String BASE_URI = "/records/";

	@Test
	public void multiRecordsTest() {
		// test multiple record request
		Response multiRecordResponse = target(BASE_URI + "331933+212121+3000eb")
				.queryParam(ENDECA_HOST_PARAM, ENDECA_HOST_VALUE).queryParam(ENDECA_PORT_PARAM, ENDECA_PORT_VALUE)
				.queryParam("searchTermKey", "item_no").request(MediaType.APPLICATION_JSON).get();

		// make sure we get a 200 response code
		checkResponseCode(multiRecordResponse, "Multi Record Test");

		// verify we get multiple items back
		String jsonString = multiRecordResponse.readEntity(String.class);
		JSONObject json = new JSONObject(jsonString);
		JSONArray records = json.getJSONObject("data").getJSONArray("records");
		Assert.assertTrue("Multi Record Test - didn't get more than 1 item - ", records.length() > 1);

		for (int i = 0; i < records.length(); i++) {
			Map<String, String> checkProperties = new HashMap<>();
			String sku = records.getJSONObject(i).getString("id");
			checkProperties.put("item_no", sku);
			checkProperties.put("product.product_desc", "string");
			checkSkuProperties(sku, checkProperties, records.getJSONObject(i).getJSONObject("properties"));
		}
	}

	@Test
	public void regularProductTest() {
		// test regular product (Not a Fan)
		Map<String, String> checkProperties = new HashMap<>();
		checkProperties.put("item_no", "331933");
		checkProperties.put("product.product_desc", "string");
		doSingleRecordTestBySku("331933", true, checkProperties);
	}

	@Test
	public void downloadProductTest() {
		// test music download (WOW Hits 2016)
		Map<String, String> checkProperties = new HashMap<>();
		checkProperties.put("item_no", "DL155107-CP");
		checkProperties.put("product.product_desc", "string");
		checkProperties.put("track_listings", "array-39");
		checkProperties.put("product.download_flag", "1");

		checkProperties.put("dlm_relationship.parent_sku", "CD65373");
		doSingleRecordTestBySku("DL155107-CP", true, checkProperties);
	}

	@Test
	public void cdProductTest() {
		// test music CD (WOW Hits 2016)
		Map<String, String> checkProperties = new HashMap<>();
		checkProperties.put("item_no", "CD65373");
		checkProperties.put("product.product_desc", "string");
		doSingleRecordTestBySku("CD65373", true, checkProperties);
	}

	@Test
	public void ebookProductTest() {
		// test eBook (Jesus Calling)
		Map<String, String> checkProperties = new HashMap<>();
		checkProperties.put("item_no", "9689EB");
		checkProperties.put("product.product_desc", "string");
		checkProperties.put("product.ebook_flag", "1");
		checkProperties.put("dlm_relationship.parent_sku", "451884");
		doSingleRecordTestBySku("9689EB", true, checkProperties);
	}

	@Test
	public void invalidProductTest() {
		// test invalid sku
		doSingleRecordTestBySku("abcdef", false, null);
	}

	public void doSingleRecordTestBySku(String sku, boolean isValid, Map<String, String> checkProperties) {
		Response singleRecordResponse = target(BASE_URI + sku).queryParam(ENDECA_HOST_PARAM, ENDECA_HOST_VALUE)
				.queryParam(ENDECA_PORT_PARAM, ENDECA_PORT_VALUE).request(MediaType.APPLICATION_JSON).get();

		// make sure we get a 200 response code
		checkResponseCode(singleRecordResponse, "Single Record Test: sku = " + sku);
		String jsonString = singleRecordResponse.readEntity(String.class);
		JSONObject json = new JSONObject(jsonString);

		if (isValid && checkProperties != null) {
			// verify we only go 1 item back
			Assert.assertEquals("Single Record Test - check for one item - sku = " + sku,
					json.getJSONObject("data").getJSONArray("records").length(), 1);

			// verify the id is what we expect
			Assert.assertEquals("Single Record Test - ID mismatch - sku = " + sku,
					json.getJSONObject("data").getJSONArray("records").getJSONObject(0).getString("id"), sku);

			JSONObject properties = json.getJSONObject("data").getJSONArray("records").getJSONObject(0)
					.getJSONObject("properties");
			checkSkuProperties(sku, checkProperties, properties);
		} else {
			// verify we don't get any items back
			Assert.assertEquals("Single Record Test - check for 0 items - sku = " + sku,
					json.getJSONObject("data").getJSONArray("records").length(), 0);
		}
	}

	public void checkSkuProperties(String sku, Map<String, String> checkProperties, JSONObject properties) {
		for (String prop : checkProperties.keySet()) {
			if (checkProperties.get(prop).toLowerCase().equals("string")) {
				Assert.assertTrue("Single Record Test - checking " + prop + " property for sku " + sku,
						properties.getString(prop).length() > 10);
			} else if (checkProperties.get(prop).startsWith("array")) {
				int length = Integer.parseInt(checkProperties.get(prop).split("-")[1]);
				Assert.assertTrue("Single Record Test - checking " + prop + " property for sku " + sku,
						properties.getJSONArray(prop).length() == length);
			} else {
				Assert.assertEquals("Single Record Test - checking " + prop + " property for sku " + sku,
						properties.getString(prop), checkProperties.get(prop));
			}
		}
	}
}
