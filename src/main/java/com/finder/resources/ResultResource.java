package com.finder.resources;

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
import com.finder.model.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class ResultResource. This class handles all requests to /search/results/
 */
@Path("/results")
@Api(value = "results")
public class ResultResource {

	/** The context. */
	@Context
	ContainerRequestContext context;

	/**
	 * Gets the results.
	 *
	 * @param filterId
	 *            the filter id
	 * @return the results
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{filterIds}")
	@ApiOperation(value = "Gets endeca records and filters", notes = "Result requests are to retrieve both record and filter data.", response = Result.class, responseContainer = "CustomResponse")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "endecaHost", value = "The hostname of the desired endeca instance to query.", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endecaPort", value = "The port integer of the desired endeca instance to query.", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "expanded", value = "Parameter that specifies which filters should be expanded. Being expanded means that the filters possible values are shown. The possible expanded values can either be \"all\" or a list of filter ids separated by \"+\" characters. Corresponds to the Ne endeca parameter.", required = false, dataType = "string", paramType = "query", defaultValue = "all"),
			@ApiImplicitParam(name = "searchTerm", value = "Parameter that specifies a search term. Only the relevant filters will be returned that pertain to records that match the \"searchTerm\" will be returned. You must specify the searchTermKey parameter in conjunction with the searchTerm parameter, the request will fail otherwise. Corresponds to the Ntt endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "searchTermKey", value = "Parameter that specifies the property in which you want to search on. It is only required if the \"searchTerm\" parameter is specified. Corresponds to the Ntk endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "searchMode", value = "Parameter that specifies the mode in which you want to search. Only applied if the \"searchTerm\" parameter is specified. Corresponds to the Ntx endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "rangeFilter", value = "Parameter that specifies the any range filters. Corresponds to the Nf endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "recordFilters", value = "Parameter that specifies the any record filters. Corresponds to the Nr endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sortBy", value = "Parameter that specifies what the records should be sorted by. Corresponds to the Ns endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sortDirection", value = "Parameter that specifies what order the records should be sorted in, (1 = desc, 0 = asc). Corresponds to the Nso endeca parameter.", required = false, dataType = "string", paramType = "query", defaultValue = "1", allowableValues = "desc, asc, 1, 0"),
			@ApiImplicitParam(name = "rollup", value = "Parameter that specifies if the records should be \"rolled-up\" by a specific attribute. Corresponds to the Nu or Au endeca parameters.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "recordsPerPage", value = "Parameter that specifies how many records per page you'd like returned. Use to set the No or Nao endeca parameters along with the \"page\" parameter.", required = false, dataType = "integer", paramType = "query", defaultValue = "25", allowableValues = "range[1,50]"),
			@ApiImplicitParam(name = "page", value = "Parameter that specifies which page of records you'd like returned. Use to set the No or Nao endeca parameters along with the \"page\" parameter.", required = false, dataType = "integer", paramType = "query", defaultValue = "1"),
			@ApiImplicitParam(name = "offset", value = "Parameter that specifies the offset of the records result. If set, the \"page\" parameter will be ignored. Corresponds to the No or Nao endeca parameters.", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "logRequest", value = "Parameter that specifies if this request should be logged to the endeca log server", required = false, dataType = "integer", paramType = "query", defaultValue = "0", allowableValues = "0, 1"),
			@ApiImplicitParam(name = "ticketId", value = "Parameter that specifies the users ticketId, used for logging. This parameter must be specified is the \"logRequest\" parameter is present.", required = false, dataType = "integer", paramType = "query"), })

	@ApiResponses({ @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 404, message = "failure - Not Found"),
			@ApiResponse(code = 400, message = "failure - Bad Request"), })
	public CustomResponse getResults(
			@ApiParam(value = "List of filter ids defining which categories to return. Individual filter ids must be separated by \"+\" characters.", required = true) @PathParam("filterIds") String filterId) {
		EndecaHelper endeca = (EndecaHelper) this.context.getProperty("endeca");
		if (endeca != null) {
			Result result = new Result();
			try {
				result = endeca.getResults(filterId);
			} catch (Exception e) {
				if (endeca != null) {
					endeca.errors.add(e.getMessage());
				}
				e.printStackTrace();
			}

			CustomResponse response = new CustomResponse(getMetaData(endeca),
					new CustomResponseObjectMapper("results", result));
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
		HashMap<String, Object> recordsMetaData = new HashMap<>();
		recordsMetaData.put("pagingControls", endeca.pagingControls);
		recordsMetaData.put("sortingControls", endeca.sortingControls);

		metaData.put("currentNavigationState", endeca.currentNavigationState);
		metaData.put("records", recordsMetaData);

		HashMap<String, Object> info = (HashMap<String, Object>) this.context.getProperty("info");
		metaData.putAll(ResourceHelper.getMetaData(endeca, info));
		return metaData;
	}
}