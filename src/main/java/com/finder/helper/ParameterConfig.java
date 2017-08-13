package com.finder.helper;

import java.util.Set;

/**
 * The Class ParameterConfig. This object is used to define a valid parameter
 * that may be passed in to this API
 */
public class ParameterConfig {

	/** The param name. */
	private String paramName;

	/** The endeca param name. */
	private String endecaParamName;

	/** The default value. */
	private String defaultValue;

	/** The regex. */
	private String regex;

	/** The possible values. */
	private Set<String> possibleValues;

	/** The has regex. */
	private boolean hasRegex;

	/**
	 * Instantiates a new parameter config.
	 *
	 * @param paramName
	 *            the param name
	 * @param defaultValue
	 *            the default value
	 * @param regex
	 *            the regex
	 * @param possibleValues
	 *            the possible values
	 * @param endecaParamName
	 *            the endeca param name
	 */
	public ParameterConfig(String paramName, String defaultValue, String regex, Set<String> possibleValues,
			String endecaParamName) {
		this.paramName = paramName;
		this.defaultValue = defaultValue;
		this.regex = regex;
		this.possibleValues = possibleValues;
		this.endecaParamName = endecaParamName;
		this.hasRegex = !(regex == null || regex.isEmpty());
	}

	/**
	 * Gets the param name.
	 *
	 * @return the param name
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * Gets the regex.
	 *
	 * @return the regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * Gets the possible values.
	 *
	 * @return the possible values
	 */
	public Set<String> getPossibleValues() {
		return possibleValues;
	}

	/**
	 * Checks for regex.
	 *
	 * @return true, if successful
	 */
	public boolean hasRegex() {
		return hasRegex;
	}

	/**
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Gets the endeca param name.
	 *
	 * @return the endeca param name
	 */
	public String getEndecaParamName() {
		return endecaParamName;
	}
}