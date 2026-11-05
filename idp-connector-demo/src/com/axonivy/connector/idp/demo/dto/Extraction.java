package com.axonivy.connector.idp.demo.dto;

import java.io.Serializable;

import com.axonivy.connector.idp.connector.model.StringExtraction;

/**
 * Class for Extraction result item with name
 *
 */
public class Extraction implements Serializable {
	private static final long serialVersionUID = -4634433464625051007L;
	
	public Extraction(String name, StringExtraction stringExtraction) {
		this.name = name;
		this.extraction = stringExtraction;
	}
	
	private String name;
	private StringExtraction extraction;
	
	public String getName() {
		return name;
	}
	public StringExtraction getExtraction() {
		return extraction;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setExtraction(StringExtraction extraction) {
		this.extraction = extraction;
	}
}
