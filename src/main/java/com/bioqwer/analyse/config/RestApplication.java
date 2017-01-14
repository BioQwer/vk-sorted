package com.bioqwer.analyse.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {

	public RestApplication() {
		register(JsonProcessingExceptionMapper.class);
		register(JacksonFeature.class);
	}

}
