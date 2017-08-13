package com.finder.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.finder.helper.EndecaHelper;
import com.finder.helper.InitializeParameters;
import com.finder.helper.ParameterConfig;

/**
 * The Class ApplicationRequestFilter.
 */
@Provider
public class ApplicationRequestFilter implements ContainerRequestFilter {

	/** The Constant LOGGER. */
	static final Logger LOGGER = LogManager.getLogger(ApplicationRequestFilter.class.getName());

	/** The results path. */
	private static String RESULTS_PATH = "results";

	/** The records path. */
	private static String RECORDS_PATH = "records";

	/** The filters path. */
	private static String FILTERS_PATH = "filters";

	// class vairables
	/** The endeca. */
	EndecaHelper endeca;

	/** The query params. */
	HashMap<String, String> queryParams;

	/** The info secyion of the meta data. */
	HashMap<String, Object> info;

	/** The errors. */
	ArrayList<String> errors;

	/** The warnings. */
	ArrayList<String> warnings;

	/** The has error. */
	boolean hasError;

	/**
	 * This method sets up the Parameter configuration and creates the
	 * EndecaHelper object needed to process and query endeca
	 * 
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// START TIME
		Long startTime = System.nanoTime();
		requestContext.setProperty("startTime", startTime);

		// initialize parameters
		InitializeParameters.init();

		// initialize class vars
		this.queryParams = new HashMap<>();
		this.info = new HashMap<>();
		this.errors = new ArrayList<>();
		this.warnings = new ArrayList<>();
		this.hasError = false;

		// get all the query string parameter values
		for (String key : requestContext.getUriInfo().getQueryParameters().keySet()) {
			this.queryParams.put(key, requestContext.getUriInfo().getQueryParameters().get(key).get(0));
		}

		// set gzip flag for intercepter
		if (requestContext.getUriInfo().getQueryParameters().keySet().contains("gzip")) {
			requestContext.setProperty("gzip", true);
		} else {
			requestContext.setProperty("gzip", false);
		}

		// sanitize parameters
		ArrayList<String> validParamsForRequest = new ArrayList<>();
		validParamsForRequest.addAll(validParamsForAllRequest);
		String requestedResource = requestContext.getUriInfo().getPath().replaceFirst("/.*", "");
		if (requestedResource.equals(RECORDS_PATH)) {
			validParamsForRequest.addAll(validParamsForRecordRequest);
		} else if (requestedResource.equals(FILTERS_PATH)) {
			validParamsForRequest.addAll(validParamsForFilterRequest);
		} else if (requestedResource.equals(RESULTS_PATH)) {
			validParamsForRequest.addAll(validParamsForResultRequest);
		} else {
			this.errors.add("Invalid Resource Request: " + requestedResource);
		}
		sanitizeQueryParameters(validParamsForRequest);

		// add errors and warnings to hash
		this.info.put("errors", errors);
		this.info.put("warnings", warnings);
		requestContext.setProperty("info", this.info);

		// create endeca object if there's no errors
		if (!this.hasError) {
			this.endeca = new EndecaHelper(this.queryParams);
			requestContext.setProperty("endeca", this.endeca);
		}
		requestContext.setProperty("hasError", this.hasError);
	}

	/**
	 * Sanitize query parameters.
	 *
	 * @param validParams
	 *            the valid params
	 */
	public void sanitizeQueryParameters(ArrayList<String> validParams) {
		// check for required parameters
		for (String param : InitializeParameters.requiredParams) {
			if (!this.queryParams.containsKey(param)) {
				this.hasError = true;
				String msg = "Missing required parameter '" + param + "'";
				this.errors.add(msg);
				LOGGER.error(msg);
			}
		}

		// only continue to sanitize parameters if there were no errors
		if (!this.hasError) {
			// sanitize each parameter
			Iterator<String> iterator = this.queryParams.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				// check if this is a valid parameter
				if (InitializeParameters.parameterConfigurations.containsKey(key)) {
					if (validParams.contains(key)) {
						String value = this.queryParams.get(key);
						ParameterConfig pc = InitializeParameters.parameterConfigurations.get(key);
						// first check if the value is in the possible values
						// array for this param
						if (!pc.getPossibleValues().contains(value)) {
							// if not, then check if there's a regex and if it
							// matches
							if (pc.hasRegex() && !value.matches(pc.getRegex())) {
								// the value does not match the regex Therefore,
								// set the parameter to the default value if it
								// has one. Else, remove the parameter.
								String msg = "Invalid value '" + value + "' for '" + key + "' parameter";
								if (!(pc.getDefaultValue() == null || pc.getDefaultValue().isEmpty())) {
									this.queryParams.put(key, pc.getDefaultValue());
									msg += " - Setting it to the default value '" + pc.getDefaultValue() + "'";
								} else {
									iterator.remove();
									this.queryParams.remove(key);
									msg += " - Removing parameter";
								}
								warnings.add(msg);
								LOGGER.warn(msg);
							} else {
								// the value matches the regex, so don't do
								// anything
							}
						} else {
							// the value is one of the valid possible values, so
							// don't do anything
						}
					} else {
						iterator.remove();
						this.warnings.add("Invalid parameter for this request '" + key + "' - Removing parameter");
					}
				} else {
					iterator.remove();
					this.warnings.add("Invalid parameter '" + key + "' - Removing parameter");
				}
			}
		}

		LOGGER.info(this.queryParams.toString());
	}

	/** the valid parameters for a record request. */
	private static List<String> validParamsForRecordRequest = Collections.unmodifiableList(Arrays.asList(
			InitializeParameters.SORT_BY_PARAMETER_NAME, InitializeParameters.SORT_DIRECTION_PARAMETER_NAME,
			InitializeParameters.ROLLUP_FLAG_PARAMETER_NAME, InitializeParameters.AGGREGATE_SEARCH_PARAMETER_NAME,
			InitializeParameters.AGGREGATE_NAVIGATION_PARAMETER_NAME));

	/** the valid parameters for a filter request. */
	private static List<String> validParamsForFilterRequest = Collections.unmodifiableList(Arrays.asList(
			InitializeParameters.EXPANDED_PARAMETER_NAME, InitializeParameters.SEARCH_TERM_PARAMETER_NAME,
			InitializeParameters.RANGE_FILTERS_PARAMETER_NAME, InitializeParameters.RECORD_FILTERS_PARAMETER_NAME));

	/** the valid parameters for a result request. */
	private static List<String> validParamsForResultRequest = Collections.unmodifiableList(Arrays.asList(
			InitializeParameters.EXPANDED_PARAMETER_NAME, InitializeParameters.SORT_BY_PARAMETER_NAME,
			InitializeParameters.SORT_DIRECTION_PARAMETER_NAME, InitializeParameters.ROLLUP_FLAG_PARAMETER_NAME,
			InitializeParameters.RECORDS_PER_PAGE_PARAMETER_NAME, InitializeParameters.PAGE_PARAMETER_NAME,
			InitializeParameters.OFFSET_PARAMETER_NAME, InitializeParameters.SEARCH_TERM_PARAMETER_NAME,
			InitializeParameters.RANGE_FILTERS_PARAMETER_NAME, InitializeParameters.RECORD_FILTERS_PARAMETER_NAME));

	/** the valid parameters for all requests. */
	private static List<String> validParamsForAllRequest = Collections
			.unmodifiableList(Arrays.asList(InitializeParameters.ENDECA_HOST_PARAMETER_NAME,
					InitializeParameters.ENDECA_PORT_PARAMETER_NAME, InitializeParameters.LOG_REQUEST_PARAMETER_NAME,
					InitializeParameters.IDENTIFIER_PARAMETER_NAME, InitializeParameters.SEARCH_TERM_KEY_PARAMETER_NAME,
					InitializeParameters.SEARCH_MODE_PARAMETER_NAME, InitializeParameters.GZIP_PARAMETER_NAME));

}