package com.axonivy.connector.idp.demo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;

import com.axonivy.connector.idp.connector.model.GenericSplittingProcessingCompleted;
import com.axonivy.connector.idp.connector.model.StringExtraction;
import com.axonivy.connector.idp.connector.model.SubDocument1;
import com.axonivy.connector.idp.demo.dto.Extraction;
import com.axonivy.connector.idp.demo.dto.SubDocument;

import ch.ivyteam.ivy.environment.Ivy;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static com.axonivy.connector.idp.connector.utils.Constants.EXTRACTIONS;

public class IDPDemoUtils {
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private static final String LINEITEM = "line_item";
	private static final String LINEITEMS = "line_items";

	private static final JsonMapper MAPPER = JsonMapper.builder()
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
			.build();

	/**
	 * Maps a splitting result into the demo sub document list.
	 *
	 * @throws IllegalArgumentException when the json has no document_splitting
	 */
	public static List<SubDocument> toSubDocumentList(JsonNode json) {
		if (!json.has("document_splitting")) {
			throw new IllegalArgumentException("No document_splitting in response.");
		}
		GenericSplittingProcessingCompleted processingCompleted = MAPPER.treeToValue(json,
				GenericSplittingProcessingCompleted.class);
		return toSubDocumentList(processingCompleted);
	}

	public static List<SubDocument> toSubDocumentList(GenericSplittingProcessingCompleted result) {
		if (result == null || result.getDocumentSplitting() == null
				|| CollectionUtils.isEmpty(result.getDocumentSplitting().getSubDocuments())) {
			return null;
		}

		List<SubDocument> subDocuments = new ArrayList<SubDocument>();
		for (int i = 0; i < result.getDocumentSplitting().getSubDocuments().size(); i++) {
			SubDocument1 subDoc1 = result.getDocumentSplitting().getSubDocuments().get(i);
			subDocuments.add(new SubDocument(subDoc1.getName(), subDoc1.getPages(), null));
		}
		for (int i = 0; i < result.getSubPdfs().size(); i++) {
			subDocuments.get(i).setSubPdf(result.getSubPdfs().get(i));
		}
		return subDocuments;
	}

	public static List<SubDocument> fromSubDocumentAndPDF(List<SubDocument1> subDocList, List<String> subPdfs) {
		List<SubDocument> subDocuments = new ArrayList<SubDocument>();
		for (int i = 0; i < subPdfs.size(); i++) {
			subDocuments
					.add(new SubDocument(subDocList.get(i).getName(), subDocList.get(i).getPages(), subPdfs.get(i)));
		}
		return subDocuments;
	}

	/**
	 * Maps the extractions of a processing result into the demo extraction list.
	 *
	 * @throws IllegalArgumentException when the json has no document_type
	 */
	public static List<Extraction> toExactionList(JsonNode jsonNode) {
		JsonNode extractionNode = jsonNode;
		if (jsonNode.has(EXTRACTIONS)) {
			extractionNode = jsonNode.get(EXTRACTIONS);
		}
		if (!extractionNode.has("document_type")) {
			throw new IllegalArgumentException("No document_type in json.");
		}
		List<Extraction> extractions = new ArrayList<>();

		for (Entry<String, JsonNode> field : extractionNode.properties()) {
			if (field.getValue() == null) {
				continue;
			}
			JsonNode stringExtractionNode = field.getValue();
			if (field.getValue().has(NAME)) {
				stringExtractionNode = field.getValue().get(NAME);
			}
			if (stringExtractionNode.has(VALUE)) {
				StringExtraction stringExtraction = MAPPER.treeToValue(stringExtractionNode, StringExtraction.class);
				extractions.add(new Extraction(field.getKey(), stringExtraction));
			}
		}
		return extractions;
	}

	/**
	 * Returns the line item table headers, taken from the first line item.
	 *
	 * @param onlyNoNull keep only headers whose value is not null
	 */
	public static List<String> extractLineItemHeader(JsonNode jsonNode, boolean onlyNoNull) {
		String lineitemKey = getLineItemKey(jsonNode);

		if (lineitemKey == null) {
			Ivy.log().warn("No line_item or line_items in json.");
			return new ArrayList<>();
		}

		List<String> header = new ArrayList<>();
		if (!jsonNode.get(lineitemKey).isArray() && !jsonNode.get(lineitemKey).isEmpty()) {
			return header;
		}
		JsonNode lineItem = jsonNode.get(lineitemKey).get(0);
		for (String fieldName : lineItem.propertyNames()) {
			if (!onlyNoNull || lineItem.get(fieldName).has(VALUE)) {
				header.add(fieldName);
			}
		}
		return header;
	}

	private static String getLineItemKey(JsonNode jsonNode) {
		String lineitemKey = null;
		if (jsonNode.has(LINEITEM)) {
			lineitemKey = LINEITEM;
		}
		if (lineitemKey == null && jsonNode.has(LINEITEMS)) {
			lineitemKey = LINEITEMS;
		}
		return lineitemKey;
	}

	/** Returns the line item rows as field name to value maps. */
	public static List<Map<String, String>> extractLineItems(JsonNode jsonNode) {
		String lineitemKey = getLineItemKey(jsonNode);

		if (lineitemKey == null) {
			Ivy.log().warn("No line_item or line_items in json.");
			return null;
		}
		List<Map<String, String>> lineitems = new ArrayList<>();
		for (JsonNode itemNode : jsonNode.get(lineitemKey)) {
			Map<String, String> map = new HashMap<>();
			for (Entry<String, JsonNode> field : itemNode.properties()) {
				if (field.getValue() != null && field.getValue().has(VALUE)) {
					map.put(field.getKey(), field.getValue().get(VALUE).asString());
				}
			}
			lineitems.add(map);
		}
		return lineitems;
	}
}
