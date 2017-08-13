package com.finder.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * The Class CustomNameSerializer. This class is used in conjunction with
 * CustomResponseObjectMapper to help map an object that is passed in to the
 * Custom Response to Json. Because Jesey and Jackson have their own standards
 * for converting a Java object to Json (POJO) We had to write this class to
 * customize our Json a bit.This class
 */
public class CustomNameSerializer extends JsonSerializer<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object,
	 * com.fasterxml.jackson.core.JsonGenerator,
	 * com.fasterxml.jackson.databind.SerializerProvider)
	 */
	@Override
	public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		// cast value to a CustomResponseObjectMapper
		CustomResponseObjectMapper crom = (CustomResponseObjectMapper) value;
		if (!crom.getName().isEmpty()) {
			// remove the results part of the json for more consistency
			if (crom.getName().equals("results")) {
				Result result = (Result) crom.getData();
				jgen.writeStartObject();
				jgen.writeObjectField("filters", result.getFilters());
				jgen.writeObjectField("records", result.getRecords());
				jgen.writeEndObject();
			} else {
				jgen.writeStartObject();
				jgen.writeObjectField(crom.getName(), crom.getData());
				jgen.writeEndObject();
			}
		}
		else {
			jgen.writeStartObject();
			jgen.writeEndObject();
		}
	}
}