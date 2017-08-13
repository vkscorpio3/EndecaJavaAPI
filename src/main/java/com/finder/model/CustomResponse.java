package com.finder.model;

import java.util.HashMap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The Class CustomResponse. This is the Java object that gets returned by all
 * Resource classes. It gets converted into Json automatically by Jersey and
 * Jackson.
 */
public class CustomResponse {

	/** The meta. */
	private HashMap<String, Object> meta;

	/** The data. */
	private CustomResponseObjectMapper data;
	
	/** The Supplemental objects data */
	private HashMap<String, Object> supplementalObjects;

	/**
	 * Instantiates a new custom response.
	 *
	 * @param meta
	 *            the meta
	 * @param data
	 *            the data
	 */
	public CustomResponse(HashMap<String, Object> meta, CustomResponseObjectMapper data) {
		this.meta = meta;
		this.data = data;
	}

	/**
	 * Gets the meta.
	 *
	 * @return the meta
	 */
	public HashMap<String, Object> getMeta() {
		return meta;
	}

	/**
	 * Sets the meta.
	 *
	 * @param meta
	 *            the meta
	 */
	public void setMeta(HashMap<String, Object> meta) {
		this.meta = meta;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	@JsonSerialize(using = CustomNameSerializer.class)
	public CustomResponseObjectMapper getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data
	 *            the new data
	 */
	public void setData(CustomResponseObjectMapper data) {
		this.data = data;
	}

	/**
	 * @return the supplementalObjects object
	 */
	public HashMap<String, Object> getSupplementalObjects() {
		return supplementalObjects;
	}

	/**
	 * @param supplementalObjects the supplementalObjects object
	 */
	public void setSupplementalObjects(HashMap<String, Object> supplementalObjects) {
		this.supplementalObjects = supplementalObjects;
	}
	
	
}
