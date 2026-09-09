package com.axonivy.connector.idp.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Strings;

import com.axonivy.connector.idp.demo.dto.SubDocument;

import ch.ivyteam.ivy.cm.ContentObjectValue;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.scripting.objects.Binary;
import ch.ivyteam.ivy.scripting.objects.File;
import jakarta.ws.rs.core.Response;

public class IDPDemoService {
	private static final String REST_CLIENT_KEY = "IDP";

	public static java.io.File exportFromCMS(String cmsUri, String ext) throws IOException {
		String file = Strings.CS.removeStart(cmsUri, "/") + "." + ext;
		java.io.File tempFile = new File(file, true).getJavaFile();
		tempFile.getParentFile().mkdirs();
		ContentObjectValue cov = Ivy.cms().root().child().file(cmsUri, ext).value().get();
		
		try (var in = cov.read().inputStream(); var fos = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, fos);
		}
		return tempFile;
	}
	
	public static File exportIvyFileFromCMS(String cmsUri, String ext) throws IOException {
		String file = Strings.CS.removeStart(cmsUri, "/") + "." + ext;
		File tempFile = new File(file, true);
		tempFile.createNewFile();
		ContentObjectValue cov = Ivy.cms().root().child().file(cmsUri, ext).value().get();
		tempFile.writeBinary(new Binary(cov.read().bytes()));
		return tempFile;
	}
	
	/**
	 * Create all page images from subDocument to path /demoPageImages/pageNum
	 * @param processingId
	 * @param subDocuments
	 * @throws IOException
	 */
	public static void createSubDocumentImages(String processingId, List<SubDocument> subDocuments) throws IOException {
		for(SubDocument subDoc : subDocuments) {
			getPageImage(processingId, subDoc.getPages().get(0), "/demoPageImages/" + subDoc.getName());
		}
	}
	
	/** Writes the page image of a processing result to the CMS file <cmsPath>.jpeg. */
	public static void getPageImage(String processingId, Integer pageNum, String cmsPath) {
		Response response = Ivy.rest().client(REST_CLIENT_KEY)
				.path("/processing/results/{processing_id}/page-images/{page_num}")
				.resolveTemplate("processing_id", processingId).resolveTemplate("page_num", pageNum)
				.request().header("Authorization", "ApiKey " + Ivy.var().get("idpConnector.apiKeySecret"))
				.get();
		if (response.getStatus() != 200) {
			Ivy.log().error("Error when get PageImage.");
			return;
		}
		try (InputStream inputStream = response.readEntity(InputStream.class)) {
			Ivy.cms().root().child().file(cmsPath, "jpeg").value().get("").write().inputStream(inputStream);
			Ivy.log().debug("cms:{0}", cmsPath);
		} catch (Exception e) {
			Ivy.log().error("Error when create PageImage.", e);
		}
	}
}
