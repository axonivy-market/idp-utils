package com.axonivy.connector.idp.connector.service;

import java.util.Map.Entry;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

public class ValidationService {
	private static final String CONFIDENCE = "confidence";
	private static final String VALIDATION_PROBLEM = "validation_problem";
	private static final String DOCUMENT_SPLITTING = "document_splitting";
	private static final String SPLIT_POINT_CONFIDENCES = "split_point_confidences";

	/**
	 * Recursive function to check if  the object only needs to have 1 field that does not satisfy the condition.
	 * then return false (doesn't pass the validate of confidence)
	 * @param extractionsNode
	 * @return
	 */
	public static boolean validate(JsonNode extractionsNode, double confidenceNumber) {
		if (extractionsNode.isObject()) {
			for (Entry<String, JsonNode> field : extractionsNode.properties()) {
				String fieldName = field.getKey();
				JsonNode fieldValue = field.getValue();
				if (CONFIDENCE.equals(fieldName) && fieldValue.asDouble() < confidenceNumber) {
					return false;
				}
				if(!validate(fieldValue, confidenceNumber)) {
					return false;
				}
			}
		} else if (extractionsNode.isArray()) {
			for (JsonNode arrayNode : extractionsNode) {
				if (!validate(arrayNode, confidenceNumber)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Recursive function to check if confidence less than confidence_number
	 * then update validation_problem to true
	 * 
	 * @param jsonNode
	 */
	public static void validateAndUpdateValidationProblem(JsonNode jsonNode, double confidenceNumber) {
		if (jsonNode.isObject()) {
			for (Entry<String, JsonNode> field : jsonNode.properties()) {
				String fieldName = field.getKey();
				JsonNode fieldValue = field.getValue();
				if (CONFIDENCE.equals(fieldName) && jsonNode.has(VALIDATION_PROBLEM)) {
					boolean validationProblem = fieldValue.asDouble() < confidenceNumber;
					((ObjectNode) jsonNode).put(VALIDATION_PROBLEM, validationProblem);
				}
				
				validate(fieldValue, confidenceNumber);
			}
		} else if (jsonNode.isArray()) {
			for (JsonNode arrayElement : jsonNode) {
				validateAndUpdateValidationProblem(arrayElement, confidenceNumber);
			}
		}
	}
	
	/**
	 * Check if document_splitting.split_point_confidences greater than or equals confidence_number
	 * @param documentSplittingNode
	 * @return true if all split_point_confidences values greater than or equals confidence_number
	 *         false if no found split_point_confidences or there is 1 value is less than confidence_number
	 */
	public static boolean checkSplitPointConfidences(JsonNode jsonNode, double confidenceNumber) {
		JsonNode splitpointConfidencesNode = jsonNode.path(DOCUMENT_SPLITTING).path(SPLIT_POINT_CONFIDENCES);
		if (splitpointConfidencesNode.isArray()) {
			for (JsonNode splitpointConfindence : splitpointConfidencesNode) {
				if (splitpointConfindence.asDouble() < confidenceNumber) {
					return false;
				}	
			}
			return true;
		}
		// not found split_point_confidences
		return false;
	}
}
