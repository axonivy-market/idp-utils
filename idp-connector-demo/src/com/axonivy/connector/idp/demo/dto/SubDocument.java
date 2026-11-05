package com.axonivy.connector.idp.demo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for Splitting Document result item
 *
 */
public class SubDocument implements Serializable{
	private static final long serialVersionUID = 317637312269047156L;

	private String subPdf;
	
	private String name = null;
	
	private List<Integer> pages = new ArrayList<>();
	
	public SubDocument(String name, List<Integer> pages, String subPdf) {
		this.name = name;
		this.pages = pages;
		this.subPdf = subPdf;
	}
	
	public String getPageImagePath() {
		return "/demoPageImages/" + name;
	}
	
	public String getSubPdf() {
		return subPdf;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getPages() {
		return pages;
	}

	public void setSubPdf(String subPdf) {
		this.subPdf = subPdf;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPages(List<Integer> pages) {
		this.pages = pages;
	}
	
}
