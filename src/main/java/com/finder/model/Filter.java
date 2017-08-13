package com.finder.model;

import java.util.LinkedHashSet;

import io.swagger.annotations.ApiModel;

/**
 * The Class Filter. A filter object that holds information pertaining to an
 * endeca dimension
 */
@ApiModel(value="Filter", description="Model for storing endeca Filter/Dimension data.")
public class Filter {

	/** The id. */
	private long id;

	/** The name. */
	private String name;

	/** The children. */
	private LinkedHashSet<Filter> children;

	/** The is leaf. */
	private Boolean isLeaf;

	/** The is navigable. */
	private Boolean isNavigable;

	/** The multi select. */
	private String multiSelect;

	/**
	 * Instantiates a new filter.
	 */
	public Filter() {

	}

	/**
	 * Instantiates a new filter.
	 *
	 * @param id
	 *            the id
	 * @param name
	 *            the name
	 */
	public Filter(long id, String name) {
		this.id = id;
		this.name = name;
		this.children = new LinkedHashSet<>();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(long id) {
		this.id = id;
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
	 * Gets the children.
	 *
	 * @return an ArrayList of Filters
	 */
	public LinkedHashSet<Filter> getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 *
	 * @param children
	 *            the new children
	 */
	public void setChildren(LinkedHashSet<Filter> children) {
		this.children = children;
	}

	/**
	 * Gets is leaf.
	 *
	 * @return bollean
	 */
	public Boolean getIsLeaf() {
		return this.isLeaf;
	}

	/**
	 * Sets is leaf.
	 *
	 * @param isLeaf
	 *            boolean
	 */
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * Gets the multi select.
	 *
	 * @return the multi select String
	 */
	public String getMultiSelect() {
		return multiSelect;
	}

	/**
	 * Sets the multi select.
	 *
	 * @param multiSelect
	 *            String should be either "and" or "or" the new multi select
	 */
	public void setMultiSelect(String multiSelect) {
		this.multiSelect = multiSelect;
	}

	/**
	 * Gets the is navigable.
	 *
	 * @return a boolean
	 */
	public Boolean getIsNavigable() {
		return this.isNavigable;

	}

	/**
	 * Sets the checks if is navigable.
	 *
	 * @param isNavigable
	 *            a boolean
	 */
	public void setIsNavigable(boolean isNavigable) {
		this.isNavigable = isNavigable;
	}

	/**
	 * A friendly human readable representation of a Filter object
	 * 
	 * @return a String
	 */
	public String toString() {
		return "Name: " + getName() + ", Id: " + getId();
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
	public boolean equals(Object filter) {
		if (!(filter instanceof Filter)) {
			return false;
		} else if (filter == this) {
			return true;
		} else {
			Filter f = (Filter) filter;
			if (f.id == this.id) {
				return true;
			} else {
				return false;
			}
		}
	}
}
