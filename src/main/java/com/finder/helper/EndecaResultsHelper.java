/**
 * @author Calebe Maciel
 * @created April 2016
 *
 */
package com.finder.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.UrlENEQuery;
import com.finder.model.Filter;
import com.finder.model.Record;
import com.finder.model.Result;

/**
 * The Class EndecaResultsHelper. This class is used to handle /finder/results/
 * requests.
 */
public class EndecaResultsHelper {

	/** The Constant LOGGER. */
	protected final static Logger LOGGER = Logger.getLogger(EndecaResultsHelper.class.getName());

	/** The endeca object. */
	private EndecaHelper endeca;

	/**
	 * Instantiates a new endeca results helper.
	 *
	 * @param endeca
	 *            the endeca
	 */
	public EndecaResultsHelper(EndecaHelper endeca) {
		this.endeca = endeca;
	}

	/**
	 * Gets the results data.
	 *
	 * @param filterId
	 *            the filter id
	 * @return a Result object
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public Result getResults(String filterId) throws ENEQueryException {
		// return value
		Result result;

		// initialize some needed variables
		HashMap<String, ArrayList<Filter>> filters = new HashMap<>();
		ArrayList<Record> records = new ArrayList<>();

		// build the query, make the request, get the nav, and finally build the
		// result
		UrlENEQuery query = this.endeca.buildEndecaQuery(filterId);
		if (this.endeca.hasRollup) {
			query.setNavNumAggrERecs(this.endeca.rpp);
		} else {
			query.setNavNumERecs(this.endeca.rpp);
		}
		this.endeca.endecaRequestStrings.add(query.toString());
		this.endeca.results = this.endeca.endecaConnection.query(query);
		this.endeca.totalNetworkAndComputeTime = this.endeca.results.getTotalNetworkAndComputeTime();
		this.endeca.nav = this.endeca.results.getNavigation();

		// get the records
		EndecaRecordsHelper erh = new EndecaRecordsHelper(this.endeca);
		if (this.endeca.hasRollup) {
			records.addAll(erh.getAggregateEndecaRecords());
		} else {
			records.addAll(erh.getEndecaRecords());
		}

		// get the filters
		EndecaFiltersHelper efh = new EndecaFiltersHelper(this.endeca);
		filters = efh.getEndecaFilters();

		// create the result
		result = new Result(filters, records);

		// check for supplemental objects
		this.endeca.processSupplements();
		return result;
	}
}