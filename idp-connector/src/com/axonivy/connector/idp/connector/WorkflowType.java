package com.axonivy.connector.idp.connector;

/**
 * Enum class for processing's workflow type: document-splitting, extraction 
 *
 */
public enum WorkflowType {
	DOCUMENT_SPLITTING("document-splitting"),
	EXTRACTION("extraction")
	;
	private String value;
	
	private WorkflowType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
