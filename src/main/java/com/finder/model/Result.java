package com.finder.model;

import java.util.ArrayList;
import java.util.HashMap;

import io.swagger.annotations.ApiModel;

/**
 * The Class Result. An object that holds a HashMap for filter information, and
 * an ArrayList for records data.
 */
@ApiModel(value="Result", description="Model for storing both Filters and Records.")
public class Result {

	/** The filters. The String key refers to filter groups */
	private HashMap<String, ArrayList<Filter>> filters;

	/** The records. */
	private ArrayList<Record> records;

	/**
	 * Instantiates a new result.
	 */
	public Result() {
	}

	/**
	 * Instantiates a new result.
	 *
	 * @param filters
	 *            the filters
	 * @param records
	 *            the records
	 */
	public Result(HashMap<String, ArrayList<Filter>> filters, ArrayList<Record> records) {
		this.filters = filters;
		this.records = records;
	}

	/**
	 * Gets the filters.
	 *
	 * @return the filters
	 */
	public HashMap<String, ArrayList<Filter>> getFilters() {
		return filters;
	}

	/**
	 * Sets the filters.
	 *
	 * @param filters
	 *            the filters
	 */
	public void setFilters(HashMap<String, ArrayList<Filter>> filters) {
		this.filters = filters;
	}

	/**
	 * Gets the records.
	 *
	 * @return the records
	 */
	public ArrayList<Record> getRecords() {
		return records;
	}

	/**
	 * Sets the records.
	 *
	 * @param records
	 *            the new records
	 */
	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}
}
