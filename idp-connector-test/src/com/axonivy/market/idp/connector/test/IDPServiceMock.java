package com.axonivy.market.idp.connector.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.axonivy.connector.idp.connector.PrebuiltWorkfow;

import io.swagger.v3.oas.annotations.Hidden;

@Path("idpMock")
@PermitAll
@Hidden
public class IDPServiceMock {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("processing/workflows")
	public Response workflows(@Context HttpServletRequest req) {
		String path = "json/workflows.json";
		return Response.ok(load(path)).type(MediaType.APPLICATION_JSON).build();
	}
		
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("processing/results/{processing_id}")
	public Response result(@Context HttpServletRequest req, @PathParam("processing_id") String processingId, @QueryParam(value = "exclude") List<String> excludes) {
		String path = "json/extraction.json";
		if (processingId.equals(Constants.documentId2)) {
			path = "json/document-splitting-result.json";
		}
		return Response.ok(load(path)).type(MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("processing/results/{processing_id}/sub-pdfs/{index}")
	public Response subPdf(@Context HttpServletRequest req,  @PathParam("processing_id") String processingId, @PathParam(value = "index") Integer index) throws IOException {
		String filename = "subDoc" + String.valueOf(index);
		java.nio.file.Path pdf = Files.createTempFile(filename, ".pdf");
	    Files.writeString(pdf, "sample content", StandardOpenOption.CREATE);
		Response response = Response.ok()
				.entity(pdf.toFile())
				.header(processingId, processingId)
				.header("Content-Disposition", "attachment; filename=\"" + filename + ".pdf\"")
				.build();
		return response;
	}

	@POST
	@Consumes
	@Produces(MediaType.APPLICATION_JSON)
	@Path("processing/{workflow_id}")
	public Response processing(@Context HttpServletRequest req, @PathParam("workflow_id") String workflowId) {
		if (workflowId.equalsIgnoreCase(PrebuiltWorkfow.generic_splitting.toString())) {
			return Response.ok().entity(load("json/document-splitting-result.json")).build();
		}
		return Response.ok().entity(load("json/extraction.json")).build();
	}
	

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("share-tokens/documents")
	public Response sharingToken(@Context HttpServletRequest req) {
		return Response.ok().entity(load("json/doc-share-token.json")).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("share-tokens/documents")
	public Response deleteSharingToken(@Context HttpServletRequest req, @QueryParam(value = "token_uuid") UUID tokenUUID) {
		return Response.ok().build();
	}

	private String load(String path) {
		try (InputStream is = IDPServiceMock.class.getResourceAsStream(path)) {
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to read resource: " + path);
		}
	}
}
