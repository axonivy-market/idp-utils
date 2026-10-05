package com.axonivy.connector.idp.test;

import java.io.File;
import java.net.URL;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;
import ch.ivyteam.ivy.rest.client.RestClients;

@IvyProcessTest(enableWebServer = true)
public class TestIdpConnector {
	private static final String REST_UUID = "c316f4d1-daa6-4ca2-b3e0-68133e54eb99";
	
	@BeforeEach
	protected void prepareRestClient(IApplication app, AppFixture fixture) {
		fixture.var("idpConnector.apiUrl", "TESTHOSTURL");
		fixture.var("idpConnector.apiKeySecret", "TESTKEY");
		fixture.var("idpConnector.waitFor", "120");
		RestClient restClient = RestClients.of(app).find(UUID.fromString(REST_UUID));

		Builder builder = RestClient.create(restClient.name()).uuid(restClient.uniqueId())
				.uri("http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.request.application}/api/idpMock")
				.description(restClient.description()).properties(restClient.properties());
		restClient = builder.toRestClient();
		RestClients.of(app).set(restClient);
	}
	
	
	protected File getFile(String path) {
		URL resource = this.getClass().getResource(path);
		if (resource != null) {
			return new File(resource.getFile());
		} else {
			throw new RuntimeException("Failed to get resource file : " + path);
		}
	}
}
