package com.axonivy.connector.idp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClient.Builder;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.security.ISession;

@IvyProcessTest(enableWebServer = true)
public class TestIdpDemo {
	private static final String REST_UUID = "c316f4d1-daa6-4ca2-b3e0-68133e54eb99";
	
	@Test
	public void testOrganizations(BpmClient bpmClient, ISession session, AppFixture fixture, IApplication app) {
		ExecutionResult result = bpmClient.start().process("IDPDemo/workflows.ivp").execute();
		com.axonivy.connector.idp.connector.demo.Data data = result.data().last();
		assertThat(data.getWorkflows()).isNotEmpty();
		
	}

	@BeforeEach
	private void prepareRestClient(IApplication app, AppFixture fixture) {
		fixture.var("idpConnector.apiUrl", "{ivy.app.baseurl}/api/idpMock");
		fixture.var("idpConnector.apiKeySecret", "TESTKEY");
		fixture.var("idpConnector.waitFor", "120");
		RestClient restClient = RestClients.of(app).find(UUID.fromString(REST_UUID));

		Builder builder = RestClient.create(restClient.name()).uuid(restClient.uniqueId())
				.uri("http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.request.application}/api/idpMock")
				.description(restClient.description()).properties(restClient.properties());
		restClient = builder.toRestClient();
		RestClients.of(app).set(restClient);
	}
}
