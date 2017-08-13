package com.finder.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class CustomResponseObjectMapper. This class is used in conjunction with
 * CustomNameSerializer to help map an object that is passed in to the Custom
 * Response to Json. Because Jesey and Jackson have their own standards for
 * converting a Java object to Json (POJO) We had to write this class to
 * customize our Json a bit.
 */
public class CustomResponseObjectMapper {

	/** The name. */
	@JsonIgnore
	private String name;

	/** The data. */
	private Object data;

	/**
	 * Instantiates a new custom response object mapper.
	 *
	 * @param name
	 *            the name
	 * @param data
	 *            the data
	 */
	public CustomResponseObjectMapper(String name, Object data) {
		this.name = name;
		this.data = data;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data
	 *            the new data
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
