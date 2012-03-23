package com.cannontech.selenium.core.locators;

import org.apache.commons.lang3.StringUtils;

public class LocatorEscapeUtils {
    
    private static final String[] ESCAPED_CSS_CHARACTERS = {".", "[", "]", ":"};
    
    /**
     * This method escapes the string to allow the characters to be escaped in java and then css. 
     */
    public static String escapeCss(String preEscapedString) {
        String escapedString = preEscapedString.trim();
        
        for (String espaceCharacter : ESCAPED_CSS_CHARACTERS) {
            escapedString = escapeCssCharacters(escapedString, espaceCharacter);
        }
        
        return escapedString;
    }
    
    /**
     * This method escapes the css string in java so it will be escaped in java and then in css.
     */
    private static String escapeCssCharacters(String preEscapedString, String character) {
        return StringUtils.replace(preEscapedString, character, "\\"+character);
    }
}