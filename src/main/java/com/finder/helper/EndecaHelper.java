/**
 * @author Calebe Maciel
 * @created March 2016
 *
 */
package com.finder.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.endeca.navigation.DimGroup;
import com.endeca.navigation.DimGroupList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.Supplement;
import com.endeca.navigation.SupplementList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;
import com.finder.model.Filter;
import com.finder.model.Record;
import com.finder.model.Result;

/**
 * The Class EndecaAgent.
 */
public class EndecaHelper {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(EndecaHelper.class.getName());

	// CONSTANTS
	/** The default encoding. */
	public String DEFAULT_ENCODING = "UTF-8";

	/** The valid endeca params. */
	public static List<String> VALID_ENDECA_PARAMETERS = Collections.unmodifiableList(Arrays.asList("N", "Ntk", "Ntt",
			"Ntx", "Nu", "Nr", "Ne", "Ns", "Nf", "Nso", "Nao", "No", "R", "A", "An", "Au"));

	// class variables
	/** The endeca connection. */
	public HttpENEConnection endecaConnection;

	/** The endeca host. */
	public String endecaHost;

	/** The endeca port. */
	public Integer endecaPort;

	// class variables accessed from outside this class
	/** The query string map. */
	public HashMap<String, String> queryStringMap;

	/** The errors. */
	public ArrayList<String> errors;

	/** The warnings. */
	public ArrayList<String> warnings;

	/** The endeca Navigation object. */
	public Navigation nav;

	/** The endeca ENEQueryResults object. */
	public ENEQueryResults results;

	/** The current refinements. */
	public HashMap<String, Object> currentNavigationState;

	/** The total endeca network and compute time */
	public double totalNetworkAndComputeTime = 0;

	/** The paging controls. */
	public HashMap<String, String> pagingControls;

	/** The sorting controls. */
	public HashMap<String, Object> sortingControls;

	/** The endeca request strings. */
	public ArrayList<String> endecaRequestStrings;

	/** The should open all filters. */
	public boolean shouldOpenAllFilters = false;

	/** The records per page. */
	public int rpp;

	/** The search term. */
	public String searchTerm;

	/** The has rollup. */
	public boolean hasRollup = false;

	/** The keyword redirect. */
	public String keywordRedirect;

	/** The offset. */
	public int offset;

	/** The Navigation id. */
	public String navigationId;

	/** The Supplemental objects data */
	public HashMap<String, Object> supplementalObjects;

	/**
	 * Instantiates a new endeca helper object.
	 *
	 * @param queryStringMap
	 *            the query string map
	 */
	public EndecaHelper(HashMap<String, String> queryStringMap) {
		this.queryStringMap = queryStringMap;
		this.endecaHost = this.queryStringMap.get("endecaHost");
		this.endecaPort = Integer.parseInt(this.queryStringMap.get("endecaPort"));
		this.endecaConnection = new HttpENEConnection(endecaHost, endecaPort);
		this.errors = new ArrayList<>();
		this.warnings = new ArrayList<>();
		this.pagingControls = new HashMap<>();
		this.sortingControls = new HashMap<>();
		this.endecaRequestStrings = new ArrayList<>();
		this.supplementalObjects = new HashMap<>();
		this.currentNavigationState = new HashMap<>();
	}

	/**
	 * Gets the records.
	 *
	 * @param recordIds
	 *            the record ids
	 * @return the records
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public ArrayList<Record> getRecords(String[] recordIds) throws ENEQueryException {
		EndecaRecordsHelper erh = new EndecaRecordsHelper(this);
		return erh.getRecords(recordIds);
	}

	/**
	 * Gets a single record.
	 *
	 * @param id
	 *            the id for this record
	 * @return a single record
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public Record getSingleRecord(String id) throws ENEQueryException {
		EndecaRecordsHelper erh = new EndecaRecordsHelper(this);
		return erh.getSingleRecord(id);
	}

	/**
	 * Gets the aggregate records.
	 *
	 * @param id
	 *            the id
	 * @return the aggregate records
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public ArrayList<Record> getAggregateRecords(String id) throws ENEQueryException {
		EndecaRecordsHelper erh = new EndecaRecordsHelper(this);
		return erh.getAggregateRecords(id);
	}

	/**
	 * Gets the filters.
	 *
	 * @param filterId
	 *            the filter id
	 * @return the filters
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public HashMap<String, ArrayList<Filter>> getFilters(String filterId) throws ENEQueryException {
		EndecaFiltersHelper efh = new EndecaFiltersHelper(this);
		return efh.getFilters(filterId);
	}

	/**
	 * Gets the results.
	 *
	 * @param filterId
	 *            the filter id
	 * @return the results
	 * @throws ENEQueryException
	 *             the ENE query exception
	 */
	public Result getResults(String filterId) throws ENEQueryException {
		EndecaResultsHelper erh = new EndecaResultsHelper(this);
		return erh.getResults(filterId);
	}

	/**
	 * Sets the ene param.
	 *
	 * @param query
	 *            the query
	 * @param param
	 *            the param
	 * @param value
	 *            the value
	 */
	public void setENEParam(UrlENEQuery query, String param, String value) {
		if (VALID_ENDECA_PARAMETERS.contains(param)) {
			String methodName = "set" + param;
			Class[] classArray = new Class[1];
			classArray[0] = String.class;
			try {
				UrlENEQuery.class.getMethod(methodName, classArray).invoke(query, value);
			} catch (Exception e) {
				String msg = "Unable to set Endeca Parameter " + param + " to " + value;
				LOGGER.log(Level.WARNING, msg);
				this.errors.add(msg);
				e.printStackTrace();
			}
		} else {
			String msg = "Invalid Endeca Parameter: " + param;
			LOGGER.log(Level.WARNING, msg);
			this.warnings.add(msg);
		}
	}

	/**
	 * Sets the endeca parameters.
	 *
	 * @param navigationId
	 *            the new endeca parameters
	 * @return the hash map
	 * @throws UrlENEQueryParseException an exception thrown by endeca
	 */
	public UrlENEQuery buildEndecaQuery(String navigationId) throws UrlENEQueryParseException {
		UrlENEQuery query = new UrlENEQuery("", DEFAULT_ENCODING);

		// set the Navigation param
		this.navigationId = navigationId;
		setENEParam(query, "N", navigationId.replaceAll("\\+", " "));

		// set which filters should be "open"
		if (this.queryStringMap.get(InitializeParameters.EXPANDED_PARAMETER_NAME) != null) {
			if (this.queryStringMap.get(InitializeParameters.EXPANDED_PARAMETER_NAME).equals("all")) {
				query.setNavAllRefinements(true);
			} else {
				setENEParam(query,
						InitializeParameters.parameterConfigurations.get(InitializeParameters.EXPANDED_PARAMETER_NAME)
								.getEndecaParamName(),
						this.queryStringMap.get(InitializeParameters.EXPANDED_PARAMETER_NAME));
			}
		} else {
			query.setNavAllRefinements(true);
		}

		// set the rollup value
		if (this.queryStringMap.get(InitializeParameters.ROLLUP_FLAG_PARAMETER_NAME) != null) {
			this.hasRollup = true;
		}

		// set the searchTerm value
		if (this.queryStringMap.get(InitializeParameters.SEARCH_TERM_PARAMETER_NAME) != null) {
			this.searchTerm = this.queryStringMap.get(InitializeParameters.SEARCH_TERM_PARAMETER_NAME);
		}

		// set the records per page
		this.rpp = Integer.parseInt(InitializeParameters.parameterConfigurations
				.get(InitializeParameters.RECORDS_PER_PAGE_PARAMETER_NAME).getDefaultValue());
		if (this.queryStringMap.get(InitializeParameters.RECORDS_PER_PAGE_PARAMETER_NAME) != null) {
			this.rpp = Integer.parseInt(this.queryStringMap.get(InitializeParameters.RECORDS_PER_PAGE_PARAMETER_NAME));
		}
		this.pagingControls.put("recordsPerPage", Integer.toString(this.rpp));

		// set the offset
		if (this.queryStringMap.get(InitializeParameters.OFFSET_PARAMETER_NAME) != null) {
			this.offset = (Integer.parseInt(this.queryStringMap.get(InitializeParameters.OFFSET_PARAMETER_NAME)));
		} else {
			String page = this.queryStringMap.get(InitializeParameters.PAGE_PARAMETER_NAME) != null
					? this.queryStringMap.get(InitializeParameters.PAGE_PARAMETER_NAME)
					: InitializeParameters.parameterConfigurations.get(InitializeParameters.PAGE_PARAMETER_NAME)
							.getDefaultValue();
			this.offset = this.rpp * (Integer.parseInt(page) - 1);
			this.pagingControls.put(InitializeParameters.PAGE_PARAMETER_NAME, page);
		}
		if (this.hasRollup) {
			setENEParam(query, "Nao", Integer.toString(this.offset));
			query.setNavNumAggrERecs(this.rpp);
		} else {
			setENEParam(query, "No", Integer.toString(this.offset));
			query.setNavNumERecs(this.rpp);
		}

		// for the rest of the parameters, just loop thru and set them
		Iterator<String> iterator = this.queryStringMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (InitializeParameters.parameterConfigurations.containsKey(key)
					&& !InitializeParameters.parameterConfigurations.get(key).getEndecaParamName().isEmpty()) {
				setENEParam(query, InitializeParameters.parameterConfigurations.get(key).getEndecaParamName(),
						this.queryStringMap.get(key));
			}
		}

		return query;
	}

	/**
	 * Builds the filters.
	 *
	 * @param dimGroups
	 *            the dim groups
	 * @param completePath
	 *            the complete path
	 * @return the hash map
	 */
	public HashMap<String, ArrayList<Filter>> buildFilters(DimGroupList dimGroups, boolean completePath) {
		HashMap<String, ArrayList<Filter>> filtersHash = new HashMap<>();
		// loop thru each group and get all the dimensions in each group
		for (Object dg : dimGroups) {
			DimGroup dimGroup = (DimGroup) dg;
			
			if (dimGroup.getName().isEmpty()) {
				continue;
			}
			// check if we already have data for this dim group
			ArrayList<Filter> filters;
			if (filtersHash.containsKey(dimGroup.getName())) {
				filters = filtersHash.get(dimGroup.getName());
			} else {
				filters = new ArrayList<>();
			}

			// loop thru all the dimensions in this group
			for (Object dimension : dimGroup) {
				Dimension d = (Dimension) dimension;

				// create a Filter object for this dimension if we don't already
				// have one
				Filter filter = new Filter(d.getId(), d.getName());
				if (filters.contains(filter)) {
					filter = filters.get(filters.indexOf(filter));
				} else {
					// else, set the filter attributes
					// set its isLeaf, multiSelect, and isNavigable attributes
					if (d.getDescriptor() != null) {
						filter.setIsLeaf(d.getDescriptor().isLeaf());
						filter.setIsNavigable(d.getDescriptor().isNavigable());
						if (d.getDescriptor().isMultiSelectAnd()) {
							filter.setMultiSelect("and");
						} else if (d.getDescriptor().isMultiSelectOr()) {
							filter.setMultiSelect("or");
						}
					}

					// add it to the array
					filters.add(filter);
				}

				// if this dimension had refinements/completePath, add them as
				// children of the current Filter
				ArrayList<DimVal> dimValList = new ArrayList<>();
				if (completePath) {
					dimValList.addAll(d.getAncestors());
					DimVal dimVal = (DimVal) d.getDescriptor();
					dimValList.add(dimVal);
				} else {
					dimValList.addAll(d.getRefinements());
				}

				if (dimValList.size() > 0) {
					// children array
					LinkedHashSet<Filter> children;
					if (!filter.getChildren().isEmpty()) {
						children = filter.getChildren();
					} else {
						children = new LinkedHashSet<>();
					}

					// loop thru all the refinements
					for (DimVal dv : dimValList) {
						if (!dv.getName().equals(d.getName())) {

							// create a Filter Object for this refinement
							String filterName = dv.getName();
							long id = dv.getId();
							Filter child = new Filter(id, filterName);

							// set its isLeaf, multiSelect, and isNavigable
							// attributes
							child.setIsLeaf(dv.isLeaf());
							if (dv.isMultiSelectAnd()) {
								child.setMultiSelect("and");
							} else if (dv.isMultiSelectOr()) {
								child.setMultiSelect("or");
							}
							child.setIsNavigable(dv.isNavigable());

							// add to the children array
							children.add(child);
						}
					}
					filter.setChildren(children);
				}

			}

			// add the dimension group to the hash
			filtersHash.put(dimGroup.getName(), filters);
		}
		return filtersHash;
	}

	/**
	 * Process any of the supplemental objects that were returned.
	 */
	public void processSupplements() {
		SupplementList supList = this.nav.getSupplements();
		for (Object s : supList) {
			Supplement sup = (Supplement) s;
			PropertyMap pm = sup.getProperties();
			ERecList eRecs = sup.getERecs();

			// check for a Title, and use that as the key to the
			// supplementalObjects hash
			String key = pm.containsKey("Title") ? (String) pm.get("Title") : "";

			// build this supplemental object if we have a valid Title
			HashMap<String, Object> supplementalObject = new HashMap<>();
			if (!key.isEmpty()) {
				// set properties
				supplementalObject.put("properties", pm);
				
				// set the records
				EndecaRecordsHelper erh = new EndecaRecordsHelper(this);
				supplementalObject.put("records", erh.getEndecaRecords(eRecs));
				this.supplementalObjects.put(key, supplementalObject);
			}
		}
	}
}