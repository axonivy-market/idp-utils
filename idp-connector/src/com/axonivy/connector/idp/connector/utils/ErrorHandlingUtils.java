package com.axonivy.connector.idp.connector.utils;

import jakarta.ws.rs.core.Response;

import com.axonivy.connector.idp.connector.exception.RestException;

public class ErrorHandlingUtils {

	public static RestException buildRestException(Response response, String workflowId, String errorMessage) {
		if (response != null && !is2xxStatus(response.getStatus())) {
			return new RestException(response, workflowId, errorMessage);
		}
		return null;
	}

	public static boolean is2xxStatus(int code) {
		return code >= 200 && code < 300;
	}
}