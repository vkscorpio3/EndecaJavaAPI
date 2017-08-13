package com.finder.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.endeca.navigation.ENEQuery;

/**
 * The Class InitializeParameters.
 */
public class InitializeParameters {

	// regular expressions
	/** The Constant NUMERIC_REGEX. */
	private final static String NUMERIC_REGEX = "^\\d+$";

	/** The Constant ALPHANUMERIC_REGEX. */
	private final static String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9\\._():\\|\\+\\s]+$";

	// static parameter names
	/** The Constant ENDECA_HOST_PARAMETER_NAME. */
	public final static String ENDECA_HOST_PARAMETER_NAME = "endecaHost";

	/** The Constant ENDECA_PORT_PARAMETER_NAME. */
	public final static String ENDECA_PORT_PARAMETER_NAME = "endecaPort";

	/** The Constant EXPANDED_PARAMETER_NAME. */
	public final static String EXPANDED_PARAMETER_NAME = "expanded";

	/** The Constant PAGE_PARAMETER_NAME. */
	public final static String PAGE_PARAMETER_NAME = "page";

	/** The Constant PAGE_PARAMETER_NAME. */
	public final static String OFFSET_PARAMETER_NAME = "offset";

	/** The Constant SORT_BY_PARAMETER_NAME. */
	public final static String SORT_BY_PARAMETER_NAME = "sortBy";

	/** The Constant SORT_DIRECTION_PARAMETER_NAME. */
	public final static String SORT_DIRECTION_PARAMETER_NAME = "sortDirection";

	/** The Constant SEARCH_TERM_PARAMETER_NAME. */
	public final static String SEARCH_TERM_PARAMETER_NAME = "searchTerm";

	/** The Constant SEARCH_TERM_KEY_PARAMETER_NAME. */
	public final static String SEARCH_TERM_KEY_PARAMETER_NAME = "searchTermKey";

	/** The Constant SEARCH_MODE_PARAMETER_NAME. */
	public final static String SEARCH_MODE_PARAMETER_NAME = "searchMode";

	/** The Constant RECORDS_PER_PAGE_PARAMETER_NAME. */
	public final static String RECORDS_PER_PAGE_PARAMETER_NAME = "recordsPerPage";

	/** The Constant ROLLUP_FLAG_PARAMETER_NAME. */
	public final static String ROLLUP_FLAG_PARAMETER_NAME = "rollup";

	/** The Constant RANGE_FILTERS_PARAMETER_NAME. */
	public final static String RANGE_FILTERS_PARAMETER_NAME = "rangeFilters";

	/** The Constant RECORD_FILTERS_PARAMETER_NAME. */
	public final static String RECORD_FILTERS_PARAMETER_NAME = "recordFilters";

	/** The Constant AGGREGATE_SEARCH_PARAMETER_NAME. */
	public final static String AGGREGATE_SEARCH_PARAMETER_NAME = "aggregateSearch";

	/** The Constant AGGREGATE_NAVIGATION_PARAMETER_NAME. */
	public final static String AGGREGATE_NAVIGATION_PARAMETER_NAME = "aggregateNavigation";

	/** The Constant LOG_REQUEST_PARAMETER_NAME. */
	public final static String LOG_REQUEST_PARAMETER_NAME = "logRequest";

	/** The Constant TICKET_ID_PARAMETER_NAME. */
	public final static String IDENTIFIER_PARAMETER_NAME = "identifier";
	
	/** The Constant GZIP_PARAMETER_NAME. */
	public final static String GZIP_PARAMETER_NAME = "gzip";

	// static objects to be used by other Classes
	/** The parameter configurations. */
	public static HashMap<String, ParameterConfig> parameterConfigurations = new HashMap<>();

	/** The required params. */
	public static List<String> requiredParams = Collections
			.unmodifiableList(Arrays.asList(ENDECA_HOST_PARAMETER_NAME, ENDECA_PORT_PARAMETER_NAME));

	/**
	 * Method that inits this object. All it does is call setupParams.
	 */
	public static void init() {
		if (InitializeParameters.parameterConfigurations.isEmpty()) {
			setupParams();
		}
	}

	/**
	 * Sets up the parameters' configurations. This method is called only once,
	 * with the first time a request is made to this API. The
	 * paramterConfigurations object will them be stored in memory for
	 * subsequent request.
	 */
	private static void setupParams() {
		InitializeParameters.parameterConfigurations = new HashMap<>();

		// endecaHost
		InitializeParameters.parameterConfigurations.put(ENDECA_HOST_PARAMETER_NAME,
				new ParameterConfig(ENDECA_HOST_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), ""));

		// endecaPort
		InitializeParameters.parameterConfigurations.put(ENDECA_PORT_PARAMETER_NAME,
				new ParameterConfig(ENDECA_PORT_PARAMETER_NAME, "", NUMERIC_REGEX, new HashSet<String>(), ""));

		// expanded (Ne) (setNavAllRefinements)
		// example regex match: "1000000 1002020 3303010"
		InitializeParameters.parameterConfigurations.put(EXPANDED_PARAMETER_NAME, new ParameterConfig(
				EXPANDED_PARAMETER_NAME, "all", "(\\d+\\s?)+", new HashSet<String>(Arrays.asList("all")), ""));

		// page (No/Nao) (offset)
		InitializeParameters.parameterConfigurations.put(PAGE_PARAMETER_NAME,
				new ParameterConfig(PAGE_PARAMETER_NAME, "1", NUMERIC_REGEX, new HashSet<String>(), ""));

		// page (No/Nao) (offset)
		InitializeParameters.parameterConfigurations.put(OFFSET_PARAMETER_NAME,
				new ParameterConfig(OFFSET_PARAMETER_NAME, "0", NUMERIC_REGEX, new HashSet<String>(), ""));

		// sortBy (Ns)
		InitializeParameters.parameterConfigurations.put(SORT_BY_PARAMETER_NAME,
				new ParameterConfig(SORT_BY_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), "Ns"));

		// sortDirection (Nso)
		// 1 = desc, 0 = asc
		InitializeParameters.parameterConfigurations.put(SORT_DIRECTION_PARAMETER_NAME,
				new ParameterConfig(SORT_DIRECTION_PARAMETER_NAME, Integer.toString(ENEQuery.NAV_SORT_DESCENDING), "",
						new HashSet<String>(Arrays.asList("desc", "asc", "1", "0")), "Nso"));

		// searchTerm (Ntt)
		InitializeParameters.parameterConfigurations.put(SEARCH_TERM_PARAMETER_NAME,
				new ParameterConfig(SEARCH_TERM_PARAMETER_NAME, "", "", new HashSet<String>(), "Ntt"));

		// searchTermKey (Ntk)
		InitializeParameters.parameterConfigurations.put(SEARCH_TERM_KEY_PARAMETER_NAME, new ParameterConfig(
				SEARCH_TERM_KEY_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), "Ntk"));

		// searchMode (Ntx)
		InitializeParameters.parameterConfigurations.put(SEARCH_MODE_PARAMETER_NAME,
				new ParameterConfig(SEARCH_MODE_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), "Ntx"));

		// recordsPerPage (setNavNumAggrERecs)
		// TODO Discuss with Sharon
		InitializeParameters.parameterConfigurations.put(RECORDS_PER_PAGE_PARAMETER_NAME, new ParameterConfig(
				RECORDS_PER_PAGE_PARAMETER_NAME, "25", "^([1-9]|[1-4][0-9]|50)$", new HashSet<String>(), ""));

		// rollup (Nu)
		InitializeParameters.parameterConfigurations.put(ROLLUP_FLAG_PARAMETER_NAME,
				new ParameterConfig(ROLLUP_FLAG_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), "Nu"));

		// rangeFilters (Nf)
		InitializeParameters.parameterConfigurations.put(RANGE_FILTERS_PARAMETER_NAME,
				new ParameterConfig(RANGE_FILTERS_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), "Nf"));

		// recordFilters (Nr)
		InitializeParameters.parameterConfigurations.put(RECORD_FILTERS_PARAMETER_NAME, new ParameterConfig(
				RECORD_FILTERS_PARAMETER_NAME, "", ALPHANUMERIC_REGEX, new HashSet<String>(), "Nr"));

		// aggregateSearch
		InitializeParameters.parameterConfigurations.put(AGGREGATE_SEARCH_PARAMETER_NAME, new ParameterConfig(
				AGGREGATE_SEARCH_PARAMETER_NAME, "0", NUMERIC_REGEX, new HashSet<String>(Arrays.asList("0", "1")), ""));

		// aggregateNavigation (An)
		InitializeParameters.parameterConfigurations.put(AGGREGATE_NAVIGATION_PARAMETER_NAME, new ParameterConfig(
				AGGREGATE_NAVIGATION_PARAMETER_NAME, "", NUMERIC_REGEX, new HashSet<String>(), "An"));

		// logRequest
		InitializeParameters.parameterConfigurations.put(LOG_REQUEST_PARAMETER_NAME, new ParameterConfig(
				LOG_REQUEST_PARAMETER_NAME, "0", NUMERIC_REGEX, new HashSet<String>(Arrays.asList("0", "1")), ""));

		// ticketId
		InitializeParameters.parameterConfigurations.put(IDENTIFIER_PARAMETER_NAME,
				new ParameterConfig(IDENTIFIER_PARAMETER_NAME, "", NUMERIC_REGEX, new HashSet<String>(), ""));
		
		// gzip
		InitializeParameters.parameterConfigurations.put(GZIP_PARAMETER_NAME, new ParameterConfig(
				GZIP_PARAMETER_NAME, "0", NUMERIC_REGEX, new HashSet<String>(Arrays.asList("0", "1")), ""));
	}
}