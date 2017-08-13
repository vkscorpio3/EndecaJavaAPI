package com.finder.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response.Status;

import com.endeca.logging.LogException;
import com.finder.helper.EndecaHelper;
import com.finder.helper.InitializeParameters;
import com.finder.logging.Logging;
import com.finder.model.CustomResponse;

public class ApplicationResponseFilter implements ContainerResponseFilter {
	private static final String CUSTOM_COMPRESSION_HEADER_NAME = "Content-Compression";
	private static final String CUSTOM_COMPRESSION_HEADER_VALUE = "yes";

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		// get response
		CustomResponse cr = null;
		if (responseContext.hasEntity()) {
			if (responseContext.getEntityClass().getName().equals(CustomResponse.class.getName())) {
				responseContext.getHeaders().add(CUSTOM_COMPRESSION_HEADER_NAME, CUSTOM_COMPRESSION_HEADER_VALUE);
				
				cr = (CustomResponse) responseContext.getEntity();

				// response attributes
				String status = "";
				String responseCode = "";
				String duration = "";
				HashMap<String, Object> info = null;

				// set request duration
				Long startTime = (Long) requestContext.getProperty("startTime");

				// check if there are any errors
				if (requestContext.getProperty("hasError") != null
						&& (boolean) requestContext.getProperty("hasError")) {
					// set the response code and status
					responseContext.setStatus(Status.BAD_REQUEST.getStatusCode());
					status = "failure";
					responseCode = Status.BAD_REQUEST.getStatusCode() + " - Bad Request";

					// get errors/warnings
					info = (HashMap<String, Object>) requestContext.getProperty("info");
				} else if (responseContext.getStatus() == Status.NOT_FOUND.getStatusCode()) {
					// set the response code and status
					responseContext.setStatus(Status.NOT_FOUND.getStatusCode());
					status = "failure";
					responseCode = Status.NOT_FOUND.getStatusCode() + " - Not Found";
					info = new HashMap<>();
				} else {
					// check for errors in response
					if (cr != null) {
						info = (HashMap<String, Object>) cr.getMeta().get("info");
						ArrayList<String> errors = (ArrayList<String>) info.get("errors");
						if (!errors.isEmpty()) {
							// set the response code and status
							responseContext.setStatus(Status.BAD_REQUEST.getStatusCode());
							status = "failure";
							responseCode = Status.BAD_REQUEST.getStatusCode() + " - Bad Request";
						} else {
							responseContext.setStatus(Status.OK.getStatusCode());
							status = "success";
							responseCode = Status.OK.getStatusCode() + " - OK";
						}
					} else {
						info = new HashMap<>();
					}

					// get endeca object and log request if logRequest is set
					EndecaHelper endeca = (EndecaHelper) requestContext.getProperty("endeca");
					if (endeca.queryStringMap.containsKey(InitializeParameters.LOG_REQUEST_PARAMETER_NAME)
							&& endeca.queryStringMap.get(InitializeParameters.LOG_REQUEST_PARAMETER_NAME).equals("1")) {
						// make sure we have a ticketId
						if (endeca.queryStringMap.containsKey(InitializeParameters.IDENTIFIER_PARAMETER_NAME)
								&& !endeca.queryStringMap.get(InitializeParameters.IDENTIFIER_PARAMETER_NAME)
										.isEmpty()) {

							// the log server host is the same as the endeca
							// host
							String logHost = endeca.queryStringMap.get(InitializeParameters.ENDECA_HOST_PARAMETER_NAME);
							// the log server port is +2 the endeca port
							Integer logPort = Integer.parseInt(
									endeca.queryStringMap.get(InitializeParameters.ENDECA_PORT_PARAMETER_NAME)) + 2;
							String identifier = endeca.queryStringMap.get(InitializeParameters.IDENTIFIER_PARAMETER_NAME);
							Logging log = new Logging(logHost, logPort);
							try {
								log.logRequest(endeca, identifier, startTime);
							} catch (LogException e) {
								e.printStackTrace();
							}
						}
					}
				}

				// log code execution time
				if (startTime != null) {
					Long endTime = System.nanoTime();
					Long delta = (endTime - startTime) / 1000000;
					duration = delta + "ms";
				}

				info.put("status", status);
				info.put("responseCode", responseCode);
				info.put("requestDuration", duration);

				if (cr != null) {
					cr.getMeta().put("info", info);
				} else {
					cr = new CustomResponse(info, null);
					responseContext.setEntity(cr);
				}
			}
		}
	}
}