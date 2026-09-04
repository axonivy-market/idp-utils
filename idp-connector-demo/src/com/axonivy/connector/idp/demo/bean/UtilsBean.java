package com.axonivy.connector.idp.demo.bean;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class UtilsBean {

	public String snakeCaseToWords(String snakeCase) {
        String[] words = snakeCase.split("_");
        
        // Capitalize each word and join them with spaces
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0))) // Capitalize first letter
                      .append(word.substring(1).toLowerCase())       // Convert rest to lowercase
                      .append(" ");                                // Add space between words
            }
        }
        
        // Remove the trailing space and return
        return result.toString().trim();
    }
}
