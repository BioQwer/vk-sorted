package com.bioqwer.analyse.config;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;

@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

	@Override
	public Response toResponse(final JsonProcessingException exception) {

		Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST)
				.entity(exception.getStackTrace())
				.type(MediaType.APPLICATION_JSON);

		return builder.build();
	}
}

