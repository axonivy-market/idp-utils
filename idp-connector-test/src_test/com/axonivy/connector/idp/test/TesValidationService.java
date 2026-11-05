package com.axonivy.connector.idp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.idp.connector.ProcessingServiceData;
import com.axonivy.connector.idp.connector.ValidationServiceData;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class TesValidationService extends BaseSetup {
	private static final BpmElement testeeValidation =
			BpmProcess.path("ValidationService").elementName("validate(UUID,Double)");
	private static final BpmElement RETRIEVE_RESULT_ERROR = BpmElement.pid("191351D442CAD7E7-f12");

	@TestTemplate
	public void testValidation(BpmClient bpmClient, ExtensionContext context) throws NoSuchFieldException {
		ExecutionResult result = bpmClient.start().subProcess(testeeValidation)
				.withParam("processingId", UUID.fromString("11111111-1111-1111-1111-111111111111"))
				.withParam("confidenceMinValue", 0.3).execute();
		if (isRealTest) {
			ProcessingServiceData processingServiceData =
					(ProcessingServiceData) result.data().lastOnElement(RETRIEVE_RESULT_ERROR);
			assertThat(processingServiceData.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			ValidationServiceData data = result.data().last();
			assertThat(data.getConfidencePassed()).isTrue();
		}
	}

	@TestTemplate
	public void testValidationWithConfidenceOne(BpmClient bpmClient, ExtensionContext context) {
		ExecutionResult result = bpmClient.start().subProcess(testeeValidation)
				.withParam("processingId", UUID.fromString("11111111-1111-1111-1111-111111111111"))
				.withParam("confidenceMinValue", 1.0).execute();
		if (isRealTest) {
			ProcessingServiceData processingServiceData =
					(ProcessingServiceData) result.data().lastOnElement(RETRIEVE_RESULT_ERROR);
			assertThat(processingServiceData.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			ValidationServiceData data = result.data().last();
			assertThat(data.getConfidencePassed()).isFalse();
		}
	}

	@TestTemplate
	public void testValidationForSplitting(BpmClient bpmClient, ExtensionContext context) {
		ExecutionResult result = bpmClient.start().subProcess(testeeValidation)
				.withParam("processingId", UUID.fromString("22222222-2222-2222-2222-222222222222"))
				.withParam("confidenceMinValue", 0.5).execute();
		if (isRealTest) {
			ProcessingServiceData processingServiceData =
					(ProcessingServiceData) result.data().lastOnElement(RETRIEVE_RESULT_ERROR);
			assertThat(processingServiceData.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			ValidationServiceData data = result.data().last();
			assertThat(data.getConfidencePassed()).isTrue();
			assertThat(data.getError()).isNull();
		}
	}

	@TestTemplate
	public void testValidationForSplittingWithConnfidenceOne(BpmClient bpmClient, ExtensionContext context) {
		ExecutionResult result = bpmClient.start().subProcess(testeeValidation)
				.withParam("processingId", UUID.fromString("22222222-2222-2222-2222-222222222222"))
				.withParam("confidenceMinValue", 1.0).execute();
		if (isRealTest) {
			ProcessingServiceData processingServiceData =
					(ProcessingServiceData) result.data().lastOnElement(RETRIEVE_RESULT_ERROR);
			assertThat(processingServiceData.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			ValidationServiceData data = result.data().last();
			assertThat(data.getConfidencePassed()).isFalse();
		}
	}
}
