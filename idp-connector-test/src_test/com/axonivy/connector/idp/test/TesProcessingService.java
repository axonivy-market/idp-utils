package com.axonivy.connector.idp.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.idp.connector.ProcessingServiceData;
import com.axonivy.connector.idp.connector.utils.IDPUtils;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class TesProcessingService extends BaseSetup {
	private static final BpmElement testeeProcessing =
			BpmProcess.path("ProcessingService").elementName("processing(String,File)");
	private static final BpmElement testeeRetrieveResult =
			BpmProcess.path("ProcessingService").elementName("retrieveResult(UUID)");
	private static final BpmElement testeeGetSubPDF =
			BpmProcess.path("ProcessingService").elementName("getSubPdf(UUID,Integer,String)");
	private static final BpmElement testeeShareToken =
			BpmProcess.path("ProcessingService").elementName("shareToken(UUID,String)");
	private static final BpmElement testeeRevokeShareToken =
			BpmProcess.path("ProcessingService").elementName("revokeToken(UUID)");

	@TestTemplate
	public void testProcessing(BpmClient bpmClient) throws IOException {
		ExecutionResult result =
				bpmClient.start().subProcess(testeeProcessing).execute("invoice_extraction", getFile("file/invoice01.pdf"));

		ProcessingServiceData data = result.data().last();
		assertThat(data.getProcessingId()).isNotNull();
		assertThat(data.getError()).isNull();
	}

	@TestTemplate
	public void testDocumentSplittingProcessing(BpmClient bpmClient) throws IOException {
		ExecutionResult result =
				bpmClient.start().subProcess(testeeProcessing).execute("generic_splitting", getFile("file/invoice01.pdf"));

		ProcessingServiceData data = result.data().last();
		assertThat(data.getProcessingId()).isNotNull();
		assertThat(data.getError()).isNull();
	}

	@TestTemplate
	public void testRetreiveResult(BpmClient bpmClient, ExtensionContext context) throws IOException {
		ExecutionResult result = bpmClient.start().subProcess(testeeRetrieveResult)
				.withParam("processingId", UUID.fromString("11111111-1111-1111-1111-111111111111")).execute();
		ProcessingServiceData data = result.data().last();
		if (isRealTest) {
			assertThat(data.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			assertThat(data.getJsonNode()).isNotNull();
			assertThat(data.getJsonNode().has("extractions")).isTrue();
		}
	}

	@TestTemplate
	public void testRetreiveDocumentSplittingResult(BpmClient bpmClient, ExtensionContext context) throws IOException {
		ExecutionResult result = bpmClient.start().subProcess(testeeRetrieveResult)
				.withParam("processingId", UUID.fromString("22222222-2222-2222-2222-222222222222")).execute();
		ProcessingServiceData data = result.data().last();
		if (isRealTest) {
			assertThat(data.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			assertThat(data.getJsonNode()).isNotNull();
			assertThat(data.getJsonNode().has("document_splitting")).isTrue();
		}
	}

	@TestTemplate
	public void testGetSubPDF(BpmClient bpmClient, ExtensionContext context) throws IOException {
		ExecutionResult result = bpmClient.start().subProcess(testeeGetSubPDF)
				.withParam("processingId", UUID.fromString("22222222-2222-2222-2222-222222222222")).withParam("index", 0)
				.withParam("fileName", "yourSubPDF").execute();
		ProcessingServiceData data = result.data().last();
		if (isRealTest) {
			assertThat(data.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			assertThat(data.getFile()).isNotNull();
			assertThat(data.getFile().getName()).isEqualToIgnoringCase("yourSubPDF.pdf");
		}
	}

	@TestTemplate
	public void testShareToken(BpmClient bpmClient, ExtensionContext context) throws IOException {
		ExecutionResult result = bpmClient.start().subProcess(testeeShareToken)
				.withParam("processing_id", UUID.fromString("11111111-1111-1111-1111-111111111111"))
				.withParam("expires_at", IDPUtils.formatDate(LocalDate.now().plusDays(2))).execute();
		ProcessingServiceData data = result.data().last();
		if (isRealTest) {
			assertThat(data.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			assertThat(data.getDocShareTokenInfo()).isNotNull();
			assertThat(data.getDocShareTokenInfo().getToken()).isNotEmpty();
		}

	}

	@TestTemplate
	public void testRevokeShareToken(BpmClient bpmClient, ExtensionContext context) {
		ExecutionResult result = bpmClient.start().subProcess(testeeRevokeShareToken)
				.withParam("tokenUUID", UUID.fromString("01c90b84-243e-4d39-abf1-bda890fc5129")).execute();
		ProcessingServiceData data = result.data().last();
		if (isRealTest) {
			assertThat(data.getError().getAttribute("RestClientResponseStatusCode")).isEqualTo(404);
		} else {
			assertThat(data.getError()).isNull();
		}
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
