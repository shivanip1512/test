package com.cannontech.yukon.api.util;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;

public class XmlVersionUtils {

    // Request constants
    public static final String YUKON_MSG_VERSION_1_0 = "1.0";
    static final String reqVersionStr = "/@version";

    // Response constants
    static final String respVersionStr = "version";

    /**
     * Gets version of the given Yukon Message Request element
     * @param reqElement
     * @return reqElement version
     */
    public static String getYukonMessageVersion(Element reqElement) {
        String reqVersion = null;

        // form the xpath expression to version attribute
        String xpathToVersion = null;
        if (reqElement.getNamespacePrefix().isEmpty()) {
            xpathToVersion = StringUtils.join(new String[] { "/",
                    YukonXml.getYukonNamespace().getPrefix(), ":",
                    reqElement.getName(), reqVersionStr });
        } else {
            xpathToVersion = StringUtils.join(new String[] { "/",
                    reqElement.getQualifiedName(), reqVersionStr });
        }
        
        // check request xml version
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(reqElement);        
        reqVersion = template.evaluateAsString(xpathToVersion);
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
     * Creates a version attribute to go on the outgoing Yukon Message Response
     * @param version value
     * @return Attribute
     */
    public static Attribute createVersionAttribute(String version) {
        Attribute versionAttr = new Attribute(respVersionStr, version);
        return versionAttr;
    }
}
