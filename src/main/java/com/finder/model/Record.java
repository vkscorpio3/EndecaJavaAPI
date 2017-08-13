package com.finder.model;

import java.util.Map;

import io.swagger.annotations.ApiModel;

/**
 * The Class Record. A record object that holds information pertaining to an
 * endeca record
 */
@ApiModel(value="Record", description="Model for storing an Endeca record.")
public class Record {

	/** The id. */
	private String id;
	
	/** The properties. */
	private Map<String, Object> properties;

	/**
	 * Instantiates a new record.
	 */
	public Record() {
	};

	/**
	 * Instantiates a new record.
	 *
	 * @param id the id
	 * @param properties the properties
	 */
	public Record(String id, Map<String, Object> properties) {
		this.id = id;
		this.properties = properties;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the properties
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/** 
	 * A friendly human readable representation of a Record object
	 * 
	 * @return a String
	 */
	public String toString() {
		return "Id: " + getId();
	}
}
