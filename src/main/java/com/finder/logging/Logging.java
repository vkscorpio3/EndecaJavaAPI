package com.finder.logging;

import java.util.ArrayList;
import java.util.HashMap;

import com.endeca.logging.LogConnection;
import com.endeca.logging.LogEntry;
import com.endeca.logging.LogException;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.Supplement;
import com.endeca.navigation.SupplementList;
import com.finder.helper.EndecaHelper;
import com.finder.helper.InitializeParameters;

public class Logging {
	private LogConnection lc;

	public Logging(String loggingHost, Integer loggingPort) {
		// establish connection to log server
		this.lc = new LogConnection(loggingHost, loggingPort);
	}

	public void logRequest(EndecaHelper endeca, String identifier, Long startTime) throws LogException {
		// create entry for log server
		LogEntry entry = new LogEntry();
		entry.putString("SESSION_ID", identifier);

		// Set the type of Endeca query to be performed.
		boolean isNavQueryType = !endeca.queryStringMap
				.containsKey(InitializeParameters.AGGREGATE_SEARCH_PARAMETER_NAME);

		Navigation nav = null;
		if (isNavQueryType && endeca.nav != null) {
			nav = endeca.nav;
		}

		// number of results
		if (nav != null) {
			entry.putString("NUM_RECORDS", Long.toString(nav.getTotalNumERecs()));
		}

		// request type
		String actionCode = interpretActionCode(endeca.queryStringMap, endeca);
		entry.putString("TYPE", actionCode);

		// Only set search terms for search-only requests
		if (actionCode.equals("S")) {
			entry.putString("SEARCH_KEY",
					endeca.queryStringMap.get(InitializeParameters.SEARCH_TERM_KEY_PARAMETER_NAME));

			entry.putString("SEARCH_TERMS", endeca.searchTerm);

			String matchMode = endeca.queryStringMap.get(InitializeParameters.SEARCH_MODE_PARAMETER_NAME);

			if (matchMode != null && matchMode.contains("mode")) {
				matchMode = matchMode.substring(matchMode.indexOf(" ") + 1, matchMode.length());
				entry.putString("SEARCH_MODE", matchMode);
			} else {
				entry.putString("SEARCH_MODE", "matchall");
			}
		}

		// sort
		if (endeca.queryStringMap.containsKey(InitializeParameters.SORT_BY_PARAMETER_NAME)) {
			String sortBy = endeca.queryStringMap.get(InitializeParameters.SORT_BY_PARAMETER_NAME);

			// Strip out sort direction
			if (sortBy.contains("|")) {
				sortBy = sortBy.substring(0, sortBy.indexOf("|") - 1);
			}
			entry.putString("SORT_KEY", sortBy);
		}

		// refinements & dimensions list
		if (nav != null) {
			DimensionList descDimensionsBC = nav.getDescriptorDimensions();

			ArrayList<String> refinementsList = new ArrayList<>();
			ArrayList<String> dimensionList = new ArrayList<>();

			for (Object d : descDimensionsBC) {
				Dimension dim = (Dimension) d;
				DimVal root = dim.getRoot();
				DimValList path = dim.getAncestors();
				DimVal desc = dim.getDescriptor();

				String refinmentPath = "/" + root.getName() + "/";
				dimensionList.add(root.getName());

				// Loop through the dimension path
				for (Object dv : path) {
					// Get specific path value
					DimVal anc = (DimVal) dv;

					// Check if ancestor is navigable
					if (anc.isNavigable()) {
						refinmentPath += anc.getName() + "/";
					}
				}

				refinmentPath += desc.getName();
				refinementsList.add(refinmentPath);
			}

			if (!refinementsList.isEmpty()) {
				entry.putList("DVALS", refinementsList);
				entry.putList("DIMS", dimensionList);
				entry.putInt("NUMREFINEMENTS", descDimensionsBC.size());
			}
		}

		// record request. use the "Name" property unless displayKey is in the
		// querystring
		if (nav != null && !nav.getERecs().isEmpty()) {
			ERecList recs = nav.getERecs();
			ArrayList<String> recNames = new ArrayList<>();

			String propName = "Name";

			for (Object e : recs) {
				ERec rec = (ERec) e;
				PropertyMap propsMap = rec.getProperties();

				if (propsMap.containsKey(propName) && ((String) propsMap.get(propName)).isEmpty()) {
					recNames.add("Record" + rec.getSpec());
				} else {
					recNames.add((String) propsMap.get(propName));
				}
			}

			entry.put("RECORD_NAMES", recNames);
		}

		// supplemental-based actions
		if (nav != null) {
			SupplementList sups = nav.getSupplements();
			ArrayList<String> merchRuleNames = new ArrayList<>();

			for (Object s : sups) {
				Supplement sup = (Supplement) s;
				PropertyMap propsMap = sup.getProperties();

				// merchandising rules engaged?
				if (propsMap.containsKey("DGraph.SeeAlsoMerchId")
						&& !((String) propsMap.get("DGraph.SeeAlsoMerchId")).isEmpty()) {
					merchRuleNames.add((String) propsMap.get("Title"));
				}

				// only set "did you mean" and autocorrection for search-only
				// queries
				if (actionCode.equals("S")) {

					// "did you mean" engaged?
					if (propsMap.containsKey("DGraph.saType") && propsMap.get("DGraph.saType").equals("didYouMean")) {
						entry.put("DYM_TO", propsMap.get("DGraph.suggestion1"));
					}

					// auto-correct engaged?
					if (propsMap.containsKey("DGraph.saType")
							&& propsMap.get("DGraph.saType").equals("autoSuggestions")) {
						entry.put("AUTOCORRECT_TO", propsMap.get("DGraph.suggestion1"));
					}
				}
			}

			if (!merchRuleNames.isEmpty()) {
				entry.putList("MERCH_RULES", merchRuleNames);
			}
		}

		// Total page render time in milliseconds (including nav engine)
		Long endTime = System.nanoTime();
		Long delta = (endTime - startTime) / 1000000;
		entry.putString("PAGE_TIME", delta.toString());

		// Total ENE processing time (convert seconds to ms)
		double eneTime = endeca.totalNetworkAndComputeTime * 1000;
		entry.putDouble("ENE_TIME", eneTime);

		// Log entry
		lc.log(entry);
	}

	/**
	 * Interpret action code.
	 * -------------------------------------------------------------
	 * InterpretActionCode Determines the correct action code when creating a
	 * new query: N - Navigation-Only, S - Search-Only, SN -
	 * Search-Then-Navigation, T - Root-Request
	 * -------------------------------------------------------------
	 *
	 * @param queryParams
	 *            the query params
	 * @param endeca
	 *            the endeca
	 * @return the string
	 */
	public String interpretActionCode(HashMap<String, String> queryParams, EndecaHelper endeca) {
		String navigationId = endeca.navigationId;
		if (!navigationId.isEmpty()) {
			// Exception for Range filter query and for paging query
			if (queryParams.containsKey(InitializeParameters.RANGE_FILTERS_PARAMETER_NAME) || endeca.offset > 0) {
				return "UNKNOWN";
			}

			if (navigationId.equals("0")) {
				if (queryParams.containsKey(InitializeParameters.EXPANDED_PARAMETER_NAME)
						&& !queryParams.get(InitializeParameters.EXPANDED_PARAMETER_NAME).equals("0")) {
					if (!endeca.searchTerm.isEmpty()) {
						return "SN";
					} else {
						return "N";
					}
				} else {
					if (!endeca.searchTerm.isEmpty()) {
						return "S";
					} else {
						return "T";
					}
				}
			} else {
				if (!endeca.searchTerm.isEmpty()) {
					return "SN";
				} else {
					return "N";
				}
			}
		} else {
			return "T";
		}
	}
}
