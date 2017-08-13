/**
 * @author Calebe Maciel
 * @created April 2016
 *
 */
package com.finder.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.endeca.navigation.DimGroupList;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.UrlENEQuery;
import com.finder.model.Filter;

/**
 * The Class EndecaFiltersHelper. This class is used to handle /finder/filters/
 * requests.
 */
public class EndecaFiltersHelper {

	/** The Constant LOGGER. */
	protected final static Logger LOGGER = Logger.getLogger(EndecaFiltersHelper.class.getName());

	/** The endeca. */
	private EndecaHelper endeca;

	/**
	 * Instantiates a new endeca filters helper.
	 *
	 * @param endeca
	 *            the endeca
	 */
	public EndecaFiltersHelper(EndecaHelper endeca) {
		this.endeca = endeca;
	}

	/**
	 * Gets the filters.
	 *
	 * @param filterId
	 *            the filter id
	 * @return A HashMap
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public HashMap<String, ArrayList<Filter>> getFilters(String filterId) throws ENEQueryException {
		// return value
		HashMap<String, ArrayList<Filter>> filters = new HashMap<>();
		UrlENEQuery query = this.endeca.buildEndecaQuery(filterId.replaceAll("\\+", " "));

		if (query != null) {
			this.endeca.endecaRequestStrings.add(query.toString());
			this.endeca.results = this.endeca.endecaConnection.query(query);
			this.endeca.totalNetworkAndComputeTime = this.endeca.results.getTotalNetworkAndComputeTime();
			this.endeca.nav = this.endeca.results.getNavigation();
			filters = getEndecaFilters();
		}
		return filters;
	}

	
	/**
	 * Dissects the endeca Navigation object to retrieve the dimensions and data
	 * for each dimension.
	 *
	 * @return the endeca filters
	 */
	protected HashMap<String, ArrayList<Filter>> getEndecaFilters() {
		// check for warnings
		if (this.endeca.searchTerm != null) {
			// if there's a search term check for searchTermKey and searchMode
			// parameters
			if (!this.endeca.queryStringMap.containsKey(InitializeParameters.SEARCH_TERM_KEY_PARAMETER_NAME)) {
				this.endeca.errors.add(
						"'searchTerm' parameter specified without a 'searchTermKey', there may be unexpected results.");
				return null;
			}
			if (!this.endeca.queryStringMap.containsKey(InitializeParameters.SEARCH_MODE_PARAMETER_NAME)) {
				this.endeca.warnings.add(
						"'searchTerm' parameter specified without a 'searchMode', there may be unexpected results.");
			}
		}

		// set the currentNavigationState with the DescriptorDimGroups
		DimGroupList descriptorDimGroups = this.endeca.nav.getDescriptorDimGroups();
		this.endeca.currentNavigationState.putAll(this.endeca.buildFilters(descriptorDimGroups, true));
		// add searchTerm to currentNavigationState
		if (this.endeca.searchTerm != null && !this.endeca.searchTerm.isEmpty()) {
			this.endeca.currentNavigationState.put("searchTerm", this.endeca.searchTerm);
		}

		// get all the dimension groups returned from the query
		DimGroupList refinementDimGroups = this.endeca.nav.getRefinementDimGroups();
		return this.endeca.buildFilters(refinementDimGroups, false);
	}
}
