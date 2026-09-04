package com.axonivy.connector.idp.connector.utils;

import static com.axonivy.connector.idp.connector.utils.Constants.STAND_ALONE_URL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.idp.connector.WorkflowType;

import ch.ivyteam.ivy.environment.Ivy;


public class IDPUtils {
	final private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**
	 * Get file name from Response header
	 * 
	 * @param str
	 * @return
	 */
	public static String getFileNameFromHeaderResponse(String str) {
        String regex = "filename=\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            String fileName = matcher.group(1);
           return fileName;
        } else {
            return null;
        }
	}
	
	public static String getFileExtensionFromHeaderResponse(String str) {
		String fileName = getFileNameFromHeaderResponse(str);
		return FilenameUtils.getExtension(fileName);
	}
	
	public static String getFileExtensionFromHeaderResponse(Response response) {
		String fileName = getFileNameFromHeaderResponse(response.getHeaders().get("Content-Disposition").toString());
		return FilenameUtils.getExtension(fileName);
	}

	/**
	 * get the sharing URL of given documentId and its workflowType
	 * @return
	 */
	public static String getSharingUrl(UUID documentId, WorkflowType workflowType, String token) {
		String url = STAND_ALONE_URL.replace("{baseUrl}", Ivy.var().get("idpConnector.standAloneUrl"))
				.replace("{workflowType}", workflowType.getValue())
				.replace("{documentId}", documentId.toString())
				.replace("{token}", token);
		
		String confidenceMinValueStr = Ivy.var().get("idpConnector.confidenceMinValue");
		if (WorkflowType.EXTRACTION.equals(workflowType) && StringUtils.isNotBlank(confidenceMinValueStr)) {
			double confidenceMinValue = Double.parseDouble(confidenceMinValueStr);
			url += "&extraction_confidence=" + confidenceMinValue;
		}
		return url;
	}

	/**
	 * Generate token expired date
	 * 
	 * @return
	 */
	public static String formatDate(LocalDate expiredDate) {
		if (expiredDate == null) {
			return "";
		}
		return expiredDate.format(dateFormatter);
	}
	
	
}
