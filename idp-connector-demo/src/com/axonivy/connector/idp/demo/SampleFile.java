package com.axonivy.connector.idp.demo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.scripting.objects.File;

/**
 * Enumeration for list sample files
 *
 */
public enum SampleFile {
	invoice01("Invoice 1"),
	invoice02("Invoice 2"),
	invoice03("Invoice 3"),
	MultipleInvoices("Multiple Invoices")
	;
	private String name;
	
	SampleFile(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public File getFile() {
		try {
			String filePath = "Files/" + this.toString();
			File file = new File(filePath + ".pdf", true);
			if(!file.exists()) {
				return IDPDemoService.exportIvyFileFromCMS(filePath, "pdf");
			} else {
				return file;
			}
		} catch (IOException e) {
			
			Ivy.log().error("Error when create file from CMS.");
		}
		return null;
	}
	
	public String getCms() {
		return "Files/" + this.toString();
	}
	
	public StreamedContent getStreamedContent() {
		StreamedContent file = DefaultStreamedContent
			    .builder()
			    .stream(() -> new ByteArrayInputStream(Ivy.cms().root().child().file(this.getCms(), "pdf").value().get().read().bytes()))
			    .contentType("application/pdf")
			    .name(this.toString())
			    .build();
		return file;
	}
	
	public static List<SampleFile> getAll() {
		return Arrays.asList(invoice01, invoice02, invoice03, MultipleInvoices);
	}
	
	public static List<SampleFile> getDemo(Demo demo) {
		if (demo != null && demo.equals(Demo.SPLITTING_DOCUMENT)) {
			return Arrays.asList(MultipleInvoices);
		}
		return Arrays.asList(invoice01, invoice02, invoice03);
	}
}
