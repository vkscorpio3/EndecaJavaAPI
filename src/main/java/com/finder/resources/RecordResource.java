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
import com.finder.helper.InitializeParameters;
import com.finder.model.CustomResponse;
import com.finder.model.CustomResponseObjectMapper;
import com.finder.model.Record;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * The Class RecordResource. This class handles all requests to /search/records/
 */
@Path("/records")
@Api(value = "records")
public class RecordResource {

	/** The context. */
	@Context
	ContainerRequestContext context;

	/**
	 * Gets the records.
	 *
	 * @param recordIdsString
	 *            the record ids string
	 * @return the records
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{recordIds}")
	@ApiOperation(value = "Gets endeca records", notes = "Record requests are to only retrieve record data.", response = Record.class, responseContainer = "list")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "endecaHost", value = "The hostname of the desired endeca instance to query.", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endecaPort", value = "The port integer of the desired endeca instance to query.", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "sortBy", value = "Parameter that specifies what the records should be sorted by. Corresponds to the Ns endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sortDirection", value = "Parameter that specifies what order the records should be sorted in, (1 = desc, 0 = asc). Corresponds to the Nso endeca parameter.", required = false, dataType = "string", paramType = "query", defaultValue = "1", allowableValues = "desc, asc, 1, 0"),
			@ApiImplicitParam(name = "rollup", value = " Parameter that specifies if the records should be \"rolled-up\" by a specific attribute. Corresponds to the Nu or Au endeca parameters.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "aggregateSearch", value = "Parameter that specifies if the record search should be a aggregate record search. If this parameter is specified, then you must also specify a the \"rollup\".", required = false, dataType = "integer", paramType = "query", defaultValue = "0", allowableValues = "1, 0"),
			@ApiImplicitParam(name = "aggregateNavigation", value = "Parameter that specifies a dimension id. This parameter is only applied if the aggregateSearch parameter is specified. In most cases this parameter will not be used. Corresponds to the An endeca parameter.", required = false, dataType = "integer", paramType = "query", defaultValue = "0"),
			@ApiImplicitParam(name = "searchTermKey", value = "Parameter that specifies the property in which you want to search on. It is only required if doing a multi fetch. Corresponds to the Ntk endeca parameter.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "logRequest", value = "Parameter that specifies if this request should be logged to the endeca log server", required = false, dataType = "integer", paramType = "query", defaultValue = "0", allowableValues = "0, 1"),
			@ApiImplicitParam(name = "ticketId", value = "Parameter that specifies the users ticketId, used for logging. This parameter must be specified is the \"logRequest\" parameter is present.", required = false, dataType = "integer", paramType = "query") })
	@ApiResponses({ @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 404, message = "failure - Not Found"),
			@ApiResponse(code = 400, message = "failure - Bad Request"), })
	public CustomResponse getRecords(
			@ApiParam(value = "Unique identifier(s) of the record being retrieved (i.e. sku). If multiple records are requested, their ids should be separated by \"+\" characters. If single value and not aggregateSearch, this will correspond to the R endeca param. If single value and is an aggregateSearch, this will correspond to the A endeca param. If multiple values, this will correspond to the Ntt endeca param sepeated by spaces, you must specify a \"searchTermKey\" query parameter for a multi fetch.", required = true) @PathParam("recordIds") String recordIdsString) {
		EndecaHelper endeca = (EndecaHelper) this.context.getProperty("endeca");

		if (endeca != null) {
			ArrayList<Record> records = new ArrayList<>();
			String[] recordIds = recordIdsString.split("\\+");
			try {
				// determine if this is an Aggregate search or a regular
				// Record search
				if (endeca.queryStringMap.get(InitializeParameters.AGGREGATE_SEARCH_PARAMETER_NAME) != null
						&& endeca.queryStringMap.get(InitializeParameters.AGGREGATE_SEARCH_PARAMETER_NAME)
								.equals("1")) {
					// if there's more than 1 record id then display an error
					// because we can only do an Aggregate search for 1
					// Aggregate record id
					if (recordIds.length > 1) {
						endeca.errors.add("Aggregate search request must have only one record id");
					}
					// get the aggregate records
					else {
						records = endeca.getAggregateRecords(recordIdsString);
					}
				} else {
					// if there's more than 1 record id then do a multi
					// fetch
					if (recordIds.length > 1) {
						records = endeca.getRecords(recordIds);
					}
					// else just get one record
					else {
						Record record = endeca.getSingleRecord(recordIds[0]);
						if (record != null) {
							records.add(record);
						}
					}
				}

			} catch (Exception e) {
				if (endeca != null) {
					endeca.errors.add(e.getMessage());
				}
				e.printStackTrace();
			}

			CustomResponse response = new CustomResponse(getMetaData(endeca),
					new CustomResponseObjectMapper("records", records));
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
		metaData.put("records", recordsMetaData);

		HashMap<String, Object> info = (HashMap<String, Object>) this.context.getProperty("info");
		metaData.putAll(ResourceHelper.getMetaData(endeca, info));
		return metaData;
	}

}