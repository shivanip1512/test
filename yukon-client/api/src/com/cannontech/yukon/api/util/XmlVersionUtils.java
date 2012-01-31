package com.cannontech.yukon.api.util;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;

public class XmlVersionUtils {

    // Request constants
    public static final String YUKON_MSG_VERSION_1_0 = "1.0";
    public static final String YUKON_MSG_VERSION_1_1 = "1.1";
    public static final String YUKON_MSG_VERSION_1_2 = "1.2";
    
    static final String versionStr = "version";

    /**
     * Gets version of the given Yukon Message Request element
     * @param reqElement
     * @return reqElement version
     */
    public static String getYukonMessageVersion(Element reqElement) {

        // get request xml version        
    	String reqVersion = reqElement.getAttributeValue(versionStr, (String)null);
        if (StringUtils.isBlank(reqVersion)) {
            throw new RuntimeException("XML Request Message version is not specified");
        }
        return reqVersion;
    }

    /**
     * Verifies that the given Yukon Message Request element version is one of
     * the given supported versions.
     * @param reqElement
     * @param supportedVersions
     */
    public static void verifyYukonMessageVersion(Element reqElement,
            String... supportedVersions) {
        boolean match = false;
        // check request xml version
        String reqVersion = getYukonMessageVersion(reqElement);
        for (String supportedVersion : supportedVersions) {
            if (reqVersion.equals(supportedVersion)) {
                match = true;
                break;
            }
        }
        if (!match) {
            throw new RuntimeException("XML Request Message version is not valid");
        }
    }

    /**
     * Adds a version attribute on the given Yukon Message Element
     * @param element  
     * @param versionValue
     */
    public static void addVersionAttribute(Element element, String versionValue) {
        Attribute versionAttr = new Attribute(versionStr, versionValue);
        element.setAttribute(versionAttr);
    }
}
