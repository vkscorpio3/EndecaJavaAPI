package com.finder.resources;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.finder.helper.EndecaHelper;
import com.finder.model.CustomResponse;
import com.finder.model.CustomResponseObjectMapper;
import com.finder.model.Filter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class FilterResource. This class handles all requests to /search/filters/
 */
@Path("/filters")
@Api(value = "filters")
public class FilterResource {

	/** The context. */
	@Context
	ContainerRequestContext context;

	/**
	 * Gets the filters.
	 *
	 * @param filterId
	 *            the navigation id
	 * @return the filters
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{filterIds}")
	@ApiOperation(value = "Gets endeca dimensions", notes = "Filter requests are to only retrieve filter data (This is also referred to as Navigation or Browse data).", response = Filter.class, responseContainer = "CustomResponse")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "endecaHost", value = "The hostname of the desired endeca instance to query.", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endecaPort", value = "The port integer of the desired endeca instance to query.", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "expanded", value = "Parameter that specifies which filters should be expanded. Being expanded means that the filters possible values are shown. The possible expanded values can either be \"all\" or a list of filter ids separated by \"+\" characters. Corresponds to the Ne endeca parameter.", required = false, dataType = "string", paramType = "query", defaultValue = "all"),
			@ApiImplicitParam(name = "searchTerm", value = "Parameter that specifies a search term. Only the relevant filters will be returned that pertain to records that match the \"searchTerm\" will be returned. You must specify the searchTermKey parameter in conjunction with the searchTerm parameter, the request will fail otherwise. Corresponds to the Ntt endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "searchTermKey", value = "Parameter that specifies the property in which you want to search on. It is only required if the \"searchTerm\" parameter is specified. Corresponds to the Ntk endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "searchMode", value = "Parameter that specifies the mode in which you want to search. Only applied if the \"searchTerm\" parameter is specified. Corresponds to the Ntx endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "rangeFilter", value = "Parameter that specifies the any range filters. Corresponds to the Nf endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "recordFilters", value = "Parameter that specifies the any record filters. Corresponds to the Nr endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "logRequest", value = "Parameter that specifies if this request should be logged to the endeca log server", required = false, dataType = "integer", paramType = "query", defaultValue = "0", allowableValues = "0, 1"),
			@ApiImplicitParam(name = "ticketId", value = "Parameter that specifies the users ticketId, used for logging. This parameter must be specified is the \"logRequest\" parameter is present.", required = false, dataType = "integer", paramType = "query") })
	@ApiResponses({ @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 404, message = "failure - Not Found"),
			@ApiResponse(code = 400, message = "failure - Bad Request"), })
	public CustomResponse getFilters(
			@ApiParam(value = "List of filter ids defining which categories to return. Individual filter ids must be separated by \"+\" characters.", required = true) @PathParam("filterIds") String filterId) {
		EndecaHelper endeca = (EndecaHelper) this.context.getProperty("endeca");

		if (endeca != null) {
			HashMap<String, ArrayList<Filter>> filters = new HashMap<>();
			try {
				filterId = filterId.replaceAll("\\+", " ");
				filters = endeca.getFilters(filterId);
			} catch (Exception e) {
				if (endeca != null) {
					endeca.errors.add(e.getMessage());
				}
				e.printStackTrace();
			}

			CustomResponse response = new CustomResponse(getMetaData(endeca),
					new CustomResponseObjectMapper("filters", filters));
			if (!endeca.supplementalObjects.isEmpty()) {
				response.setSupplementalObjects(endeca.supplementalObjects);
			}
			return response;
		} else {
			return null;
		}
	}

	/**
	 * Gets the meta data.
	 *
	 * @param endeca
	 *            the endeca
	 * @return the meta data
	 */
	public HashMap<String, Object> getMetaData(EndecaHelper endeca) {
		HashMap<String, Object> metaData = new HashMap<>();
		metaData.put("currentNavigationState", endeca.currentNavigationState);

		HashMap<String, Object> info = (HashMap<String, Object>) this.context.getProperty("info");
		metaData.putAll(ResourceHelper.getMetaData(endeca, info));
		return metaData;
	}
}