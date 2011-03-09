package com.cannontech.yukon.api.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.xml.XmlUtils;

public class XMLFailureGenerator {

    private static Logger log = YukonLogManager.getLogger(XMLFailureGenerator.class);
    private static Set<Class<? extends Throwable>> exceptionToIgnore = new HashSet<Class<? extends Throwable>>();
    {
        // add any ignorable Exceptions to exceptionToIgnore here
        // ignorable Exceptions will be logged as DEBUG instead of ERROR
    }
    
    public static Element generateFailure(Element request, Throwable t, String errorCode, String errorDescription) {
        return generateFailure(request, t, errorCode, errorDescription, YukonXml.getYukonNamespace());
    }
    
    public static Element generateFailure(Element request, Throwable t, String errorCode, String errorDescription, Namespace ns) {
        
        // log error
        Throwable rc = CtiUtilities.getRootCause(t);
        String key = "FK" + RandomStringUtils.randomNumeric(10);
        handleException(request, t, rc, key);
        
        // generate failure element
        Element failureElement = new Element("failure", ns);
        failureElement.addContent(XmlUtils.createStringElement("errorCode", ns, errorCode));
        failureElement.addContent(XmlUtils.createStringElement("errorReference", ns, key));
        failureElement.addContent(XmlUtils.createStringElement("errorDescription", ns, errorDescription));
        
        return failureElement;
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
            if (exceptionToIgnore.contains(exception.getClass())) {
                return true;
            }
            exception = ExceptionUtils.getCause(exception);
        }
        return false;
    }
}
