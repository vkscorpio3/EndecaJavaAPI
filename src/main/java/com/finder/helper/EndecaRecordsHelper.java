/**
 * @author Calebe Maciel
 * @created April 2016
 *
 */
package com.finder.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.endeca.navigation.AggrERec;
import com.endeca.navigation.AggrERecList;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.ERecSortKey;
import com.endeca.navigation.ERecSortKeyList;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlENEQuery;
import com.finder.model.Record;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class EndecaRecordsHelper. This class is used to handle /finder/records/
 * requests.
 */
public class EndecaRecordsHelper {

	/** The Constant LOGGER. */
	protected final static Logger LOGGER = LogManager.getLogger(EndecaRecordsHelper.class.getName());

	/** The endeca. */
	private EndecaHelper endeca;

	// constants
	/** The Constant DEFAULT_N_PARAMETER. */
	public final static String DEFAULT_N_PARAMETER = "0";

	/** The Constant DEFAULT_SEARCH_MODE. */
	public final static String DEFAULT_SEARCH_MODE = "mode+matchboolean";

	/** The Constant MAX_RECORDS_REQUEST. */
	public final static int MAX_RECORDS_REQUEST = 30;

	// class variables
	/** The total records. */
	public long totalRecords = 0;

	/** The records displayed. */
	public int recordsDisplayed = 0;

	/**
	 * Instantiates a new endeca records helper.
	 *
	 * @param endeca
	 *            the endeca
	 */
	public EndecaRecordsHelper(EndecaHelper endeca) {
		this.endeca = endeca;
	}

	/**
	 * Gets the records.
	 *
	 * @param recordIds
	 *            the record ids
	 * @return An ArrayList of Record objects
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public ArrayList<Record> getRecords(String[] recordIds) throws ENEQueryException {
		// return value
		ArrayList<Record> records = new ArrayList<>();

		// make sure we have a searchTermKey parameter
		if (!this.endeca.queryStringMap.containsKey(InitializeParameters.SEARCH_TERM_KEY_PARAMETER_NAME)) {
			this.endeca.errors.add("You must specify the 'searchTermKey' parameter for a multi record request.");
			return null;
		}

		// if the number of records is more than 10 we need to make multiple
		// requests to endeca for 10 items at a time because we only allow a
		// maximum of 10 search terms.
		// don't allow more than 30 search terms for multiple record fetches
		if (recordIds.length > MAX_RECORDS_REQUEST) {
			recordIds = Arrays.copyOfRange(recordIds, 0, MAX_RECORDS_REQUEST);
		}

		int chunkSize = 10;
		// split the recordIds array into chunks of 10
		for (int i = 0; i < recordIds.length; i += chunkSize) {
			String[] chunk;
			if ((i + chunkSize) > recordIds.length) {
				chunk = Arrays.copyOfRange(recordIds, i, recordIds.length);
			} else {
				chunk = Arrays.copyOfRange(recordIds, i, i + chunkSize);
			}
			
			// this will set the this.eh.endecaParams HashMap which I can use later
			UrlENEQuery query = this.endeca.buildEndecaQuery(DEFAULT_N_PARAMETER);
			// set default search mode
			query.setNtx(DEFAULT_SEARCH_MODE);
			
			query.setNtt(String.join(" or ", chunk));
			if (query != null) {
				this.endeca.endecaRequestStrings.add(query.toString());
				this.endeca.results = this.endeca.endecaConnection.query(query);
				this.endeca.totalNetworkAndComputeTime += this.endeca.results.getTotalNetworkAndComputeTime();
				this.endeca.nav = this.endeca.results.getNavigation();
				if (this.endeca.hasRollup) {
					records.addAll(getAggregateEndecaRecords());
				} else {
					records.addAll(getEndecaRecords());
				}
			}
		}

		return records;
	}

	/**
	 * Gets a single record for a given record id.
	 *
	 * @param id
	 *            the record id
	 * @return a single Record object
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public Record getSingleRecord(String id) throws ENEQueryException {
		Record record = null;

		// make sure the id is upper cased
		id = id.toUpperCase();

		// this request only needs the R parameter
		UrlENEQuery query = new UrlENEQuery("", this.endeca.DEFAULT_ENCODING);
		query.setR(id);
		if (query != null) {
			try {
				this.endeca.endecaRequestStrings.add(query.toString());
				this.endeca.results = this.endeca.endecaConnection.query(query);
				this.endeca.totalNetworkAndComputeTime = this.endeca.results.getTotalNetworkAndComputeTime();
				record = createRecord(this.endeca.results.getERec());
			} catch (ENEQueryException e) {
				this.endeca.errors.add("Request for record \"" + id + "\" failed.");
			}
		}
		return record;
	}

	/**
	 * Gets the aggregate records for a given aggregate record id.
	 *
	 * @param id
	 *            the aggregate record id
	 * @return An ArrayList of Record objects
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public ArrayList<Record> getAggregateRecords(String id) throws ENEQueryException {
		// return value
		ArrayList<Record> records = new ArrayList<>();
		id = id.toUpperCase();

		// set the A parameter
		UrlENEQuery query = new UrlENEQuery("", this.endeca.DEFAULT_ENCODING);
		query.setA(id);
		
		// make sure we have a rollup value or set an error and return null
		if (this.endeca.queryStringMap.get(InitializeParameters.ROLLUP_FLAG_PARAMETER_NAME) != null) {
			query.setAu(this.endeca.queryStringMap.get(InitializeParameters.ROLLUP_FLAG_PARAMETER_NAME));
		} else {
			this.endeca.errors.add("You must specify a rollup value for an Aggregate Search request");
			return null;
		}

		// set the An parameter if we have one, or the default ("0") if one
		// wasn't specified
		if (this.endeca.queryStringMap.get(InitializeParameters.AGGREGATE_NAVIGATION_PARAMETER_NAME) != null) {
			query.setAn(this.endeca.queryStringMap.get(InitializeParameters.AGGREGATE_NAVIGATION_PARAMETER_NAME));
		} else {
			query.setAn(DEFAULT_N_PARAMETER);
		}

		try {
			this.endeca.endecaRequestStrings.add(query.toString());
			this.endeca.results = this.endeca.endecaConnection.query(query);
			this.endeca.totalNetworkAndComputeTime = this.endeca.results.getTotalNetworkAndComputeTime();
			AggrERec aRec = this.endeca.results.getAggrERec();
			records.addAll(getEndecaRecords(aRec));
		} catch (ENEQueryException e) {
			this.endeca.errors.add("Request for record \"" + id + "\" failed.");
		}

		return records;
	}

	/**
	 * Gets the endeca records for a given aggregate record (AggrRec) object.
	 *
	 * @param aRec
	 *            the a rec
	 * @return An ArrayList of Record objects
	 */
	protected ArrayList<Record> getEndecaRecords(AggrERec aRec) {
		ArrayList<Record> records = new ArrayList<>();
		this.totalRecords += aRec.getTotalNumERecs();
		this.endeca.pagingControls.put("totalRecords", Long.toString(this.totalRecords));

		this.recordsDisplayed = (int) this.totalRecords;
		this.endeca.pagingControls.put("recordsDisplayed", Integer.toString(this.recordsDisplayed));

		// set the page
		setPage();

		// Go through all the records and retrieve a record.
		ERecList eList = aRec.getERecs();
		for (Object er : eList) {
			ERec endecaRecord = (ERec) er;
			Record record = createRecord(endecaRecord);
			if (record != null) {
				records.add(record);
			}
		}

		return records;
	}

	
	/**
	 * Gets the aggregate endeca records.
	 *
	 * @return An ArrayList of Record objects
	 */
	protected ArrayList<Record> getAggregateEndecaRecords() {
		ArrayList<Record> records = new ArrayList<>();
		this.totalRecords += this.endeca.nav.getTotalNumAggrERecs();
		this.endeca.pagingControls.put("totalRecords", Long.toString(this.totalRecords));
		AggrERecList aggrRecords = this.endeca.nav.getAggrERecs();

		this.recordsDisplayed += aggrRecords.size();
		this.endeca.pagingControls.put("recordsDisplayed", Integer.toString(this.recordsDisplayed));

		// set the page
		setPage();

		// Go through all the aggregate records and retrieve a record.
		for (Object aggrRecord : aggrRecords) {
			AggrERec ar = (AggrERec) aggrRecord;
			// Get the products associated with this aggregate record.
			ERecList products = ar.getERecs();
			if (products.size() > 0) {
				ERec endecaRecord = ((ERec) products.get(0));
				Record record = createRecord(endecaRecord);
				if (record != null) {
					records.add(record);
				}
			}
		}

		// set the sorting controls
		setSortingControls();

		return records;
	}

	
	/**
	 * Gets the endeca records.
	 *
	 * @return An ArrayList of Record objects
	 */
	protected ArrayList<Record> getEndecaRecords() {
		ArrayList<Record> records = new ArrayList<>();
		this.totalRecords += this.endeca.nav.getTotalNumERecs();
		this.endeca.pagingControls.put("totalRecords", Long.toString(totalRecords));
		ERecList eRecords = this.endeca.nav.getERecs();

		this.recordsDisplayed += eRecords.size();
		this.endeca.pagingControls.put("recordsDisplayed", Integer.toString(this.recordsDisplayed));

		// set the page
		setPage();

		for (Object endecaRecord : eRecords) {
			ERec er = (ERec) endecaRecord;
			Record record = createRecord(er);
			if (record != null) {
				records.add(record);
			}
		}

		// set the sorting controls
		setSortingControls();

		return records;
	}
	
	/**
	 * Gets the endeca records.
	 * @param eRecords - a list of endeca records
	 * 
	 * @return An ArrayList of Record objects
	 */
	protected ArrayList<Record> getEndecaRecords(ERecList eRecords) {
		ArrayList<Record> records = new ArrayList<>();

		for (Object endecaRecord : eRecords) {
			ERec er = (ERec) endecaRecord;
			Record record = createRecord(er);
			if (record != null) {
				records.add(record);
			}
		}

		return records;
	}

	/**
	 * Creates a record object given an endeca ERec object.
	 *
	 * @param endecaRecord
	 *            a ERec endeca object
	 * @return a Record object
	 */
	protected Record createRecord(ERec endecaRecord) {
		PropertyMap recordProperties = endecaRecord.getProperties();

		String id = endecaRecord.getSpec();
		// create custom hash map because recordProperties may have duplicate
		// property keys
		HashMap<String, Object> properties = new HashMap<>();
		for (Object key : recordProperties.keySet()) {
			if (recordProperties.getValues((String) key).size() > 1) {
				properties.put((String) key, recordProperties.getValues((String) key));
			} else {
				properties.put((String) key, recordProperties.get(key));
			}
		}

		Record record = new Record(id, properties);
		return record;
	}

	/**
	 * Sets the sorting controls.
	 */
	protected void setSortingControls() {
		// get all the possible sort options
		ERecSortKeyList sortKeys = this.endeca.nav.getSortKeys();
		ArrayList<String> sortingOptions = new ArrayList<>();
		for (Object sortKey : sortKeys) {
			ERecSortKey sk = (ERecSortKey) sortKey;
			sortingOptions.add(sk.getName());
		}
		this.endeca.sortingControls.put("sortOptions", sortingOptions);

		// get the active sort option, there might not be one if the sortBy (Ns)
		// parameter is not set
		ERecSortKeyList activeSortKeys = this.endeca.nav.getActiveSortKeys();
		for (Object activeSortKey : activeSortKeys) {
			ERecSortKey ask = (ERecSortKey) activeSortKey;
			this.endeca.sortingControls.put("activeSortOption", ask.getName());
			String sortDirection = ask.getOrder() == ERecSortKey.ASCENDING ? "asc" : "desc";
			this.endeca.sortingControls.put("sortDirection", sortDirection);
		}
	}

	/**
	 * Sets the page.
	 */
	public void setPage() {
		if (!this.endeca.pagingControls.containsKey(InitializeParameters.PAGE_PARAMETER_NAME)) {
			double offset = this.endeca.offset;
			double rpp = this.endeca.rpp;
			int page = (int) Math.ceil((offset + 1) / rpp);
			this.endeca.pagingControls.put(InitializeParameters.PAGE_PARAMETER_NAME, Integer.toString(page));
		}
	}
}
