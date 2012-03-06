package com.cannontech.yukon.api.util;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;

public class XMLFailureGenerator {
    private static Logger log = YukonLogManager.getLogger(XMLFailureGenerator.class);
    private final static Namespace ns = YukonXml.getYukonNamespace();

    public static Element generateFailure(Element request, Throwable t, String errorCode, String errorDescription) {
        return generateFailure(request, t, errorCode, errorDescription, ns);
    }

    public static Element generateFailure(Element request, String errorCode, String errorDescription) {
        return generateFailure(request, null, errorCode, errorDescription, ns);
    }

    public static Element generateFailure(Element request, Throwable t, String errorCode,
                                          String errorDescription, Namespace nsIn) {
        // log error
        Throwable rc = CtiUtilities.getRootCause(t);
        String key = "FK" + RandomStringUtils.randomNumeric(10);
        handleException(request, t, rc, key);

        // generate failure element
        Element failureElement = new Element("failure", nsIn);
        failureElement.addContent(XmlUtils.createStringElement("errorCode", nsIn, errorCode));
        failureElement.addContent(XmlUtils.createStringElement("errorReference", nsIn, key));
        failureElement.addContent(XmlUtils.createStringElement("errorDescription", nsIn, errorDescription));
        
        return failureElement;
    }

    public static Element makeSimple(String errorCode, String errorDescription) {
        Element failureElem = new Element("failure", ns);
        failureElem.addContent(XmlUtils.createStringElement("errorCode", ns, errorCode));
        failureElem.addContent(XmlUtils.createStringElement("errorDescription", ns, errorDescription));
        return failureElem;
    }

    private static void handleException(Element request, Throwable t, Throwable rc, String key) {
        
        Level level = Level.ERROR;
        if (ignoreException(t)) {
            level = Level.DEBUG;
        }
        log.log(level, "Web service Endpoint caught an exception processing {" + key + "}: " + getFailureInfo(request), t);
    }
    
    private static String getFailureInfo(Element request) {
        
        return request.toString();
    }
    
    private static boolean ignoreException(Throwable t) {
        Throwable exception = t;
        while (exception != null) {
            exception = ExceptionUtils.getCause(exception);
        }
        return false;
    }
}
