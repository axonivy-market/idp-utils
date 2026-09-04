package com.axonivy.connector.idp.connector.auth;

import java.io.IOException;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import ch.ivyteam.ivy.rest.client.FeatureConfig;

public class IDPAuthFeature implements Feature {

	@Override
	public boolean configure(FeatureContext context) {
		context.register(new AuthFilter(), Priorities.AUTHENTICATION);
		return true;
	}

	private static class AuthFilter implements ClientRequestFilter {

		@Override
		public void filter(ClientRequestContext ctxt) throws IOException {
			var config = new FeatureConfig(ctxt.getConfiguration(), IDPAuthFeature.class);
			String key = config.readMandatory("AUTH.apiKey");
			ctxt.getHeaders().putSingle("Authorization", "ApiKey " + key);
			ctxt.getHeaders().putSingle("X-Requested-By", "ivy");
		}
	}
}
