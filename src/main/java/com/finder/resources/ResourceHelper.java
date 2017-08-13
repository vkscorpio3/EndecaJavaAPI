package com.finder.resources;

import java.util.ArrayList;
import java.util.HashMap;

import com.finder.helper.EndecaHelper;

/**
 * The Class ResourceHelper. This class helps with setting the meta data.
 */
public class ResourceHelper {

	/**
	 * Gets the meta data.
	 *
	 * @param endeca
	 *            the endeca
	 * @param info
	 *            the info
	 * @return the meta data
	 */
	static public HashMap<String, Object> getMetaData(EndecaHelper endeca, HashMap<String, Object> info) {
		HashMap<String, Object> metaData = new HashMap<>();
		info.put("endecaRequests", endeca.endecaRequestStrings);
		if (!endeca.warnings.isEmpty()) {
			((ArrayList<String>) info.get("warnings")).addAll(endeca.warnings);
		}

		if (!endeca.errors.isEmpty()) {
			((ArrayList<String>) info.get("errors")).addAll(endeca.errors);
		}

		if (endeca.keywordRedirect != null && !endeca.keywordRedirect.isEmpty()) {
			metaData.put("keywordRedirect", endeca.keywordRedirect);
		}
		metaData.put("info", info);
		return metaData;
	}
}
