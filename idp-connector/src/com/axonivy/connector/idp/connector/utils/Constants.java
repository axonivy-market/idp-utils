package com.axonivy.connector.idp.connector.utils;

public class Constants {
	public static final String ALLOW_ALL_COOKIE = "allowCookies=all";
	public static final String AND = "&";
	public static final String STAND_ALONE_URL = "{baseUrl}/workflows/{workflowType}/stand-alone/{documentId}?token={token}&allowCookies=all";
	public static final String PROCESSING_ID = "processing_id";
	public static final String EXTRACTIONS = "extractions";
	public static final String EXTRACTION = "extraction";
	public static final String CONFIDENCE = "confidence";
	public static final String VALIDATION_PROBLEM = "validation_problem";
	public static final String WORKFLOW_ID = "workflow_id";
	public static final String WORKFLOW_NAME = "workflow_name";
	
	public static final String SUB_FOLDER_PATH = "subTemp";
	
	// Fields to include in the response. Allowed value: "document-splitting", "ocr", "page-images", "thumbnail", "pdf", "hocr", "sub-pdfs"
	public static final String DOCUMENT_SPLITTING = "document-splitting";
	public static final String OCR = "ocr";
	public static final String PAGE_IMAGES = "page-images";
	public static final String THUMBNAIL = "thumbnail";
	public static final String PDF = "pdf";
	public static final String HOCR = "hocr";
	public static final String SUB_PDFS = "sub-pdfs";
}
