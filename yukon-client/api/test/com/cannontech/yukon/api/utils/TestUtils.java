package com.cannontech.yukon.api.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.jdom2.Element;
import org.jdom2.transform.JDOMSource;
import org.springframework.core.io.Resource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cannontech.common.util.xml.SimpleXPathTemplate;


public class TestUtils {

    public static void runSuccessAssertion(SimpleXPathTemplate outputTemplate, String serviceResponseName) {
        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS = "/y:" + serviceResponseNameWithNS;
        }        
        assertNotNull(outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/y:success"), "Missing success node.");
    }
    
    public static void runFailureAssertions(SimpleXPathTemplate outputTemplate, String serviceResponseName, String expectedErrorCode) {

        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS = "/y:" + serviceResponseNameWithNS;
        }
        assertNull(outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/y:success"), "Should not have success node.");
        assertNotNull(outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/y:failure"), "No failure node present.");
        assertEquals(expectedErrorCode, outputTemplate.evaluateAsString(serviceResponseNameWithNS + "/y:failure/y:errorCode"),
                "Incorrect errorCode.");
    }
    
    public static void runVersionAssertion(SimpleXPathTemplate outputTemplate,
            String serviceResponseName, String version) {

        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS = "/y:" + serviceResponseNameWithNS;
        }
        assertNotNull(outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/@version"), "No version node present");
        assertEquals(version, outputTemplate.evaluateAsString(serviceResponseNameWithNS + "/@version"),
                "Incorrect Response version");
    }
    
    /**
     * Validates a Jdom Element against the given Schema resource.
     * @param element
     * @param schemaResource
     */
    public static void validateAgainstSchema(Element element,
            Resource schemaResource) {
        ArrayList<String> errorMsgs = new ArrayList<String>();
        SimpleErrorHandler errorHandler = new SimpleErrorHandler();
        boolean processedOk = false;
        try {
            // create a schema validator
            Schema schema = new XMLSchemaFactory().newSchema(schemaResource.getURL());
            Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);

            // run the element XML thru the validator
            validator.validate(new JDOMSource(element));

            assertTrue(!errorHandler.isError(), "Element validation failed");
            processedOk = true;

        } catch (IOException e) {
            errorMsgs.add(e.getMessage());
        } catch (SAXException e) {
            errorMsgs.add(e.getMessage());
        } finally {
            if (!processedOk) {
                errorMsgs.addAll(errorHandler.getErrorMsgs());
                String detailMsg = "Element validation failed: " + StringUtils.join(errorMsgs,
                                                                                    "; ");
                fail(detailMsg);
            }
        }
    }
    
    public static class SimpleErrorHandler implements ErrorHandler {

        ArrayList<String> errorMsgs = new ArrayList<String>();        
        private int errorCount = 0;
        private int fatalErrorCount = 0;
        private int warnCount = 0;

        public void error(SAXParseException exception) {
            errorCount++;
            errorMsgs.add(exception.getMessage());            
        }

        public void fatalError(SAXParseException exception) {
            fatalErrorCount++;
            errorMsgs.add(exception.getMessage());            
        }

        public void warning(SAXParseException exception) {
            warnCount++;
            errorMsgs.add(exception.getMessage());            
        }

        public boolean isError() {
            return (errorCount > 0 || fatalErrorCount > 0);
        }
        
        public boolean isWarn() {
            return (warnCount > 0);
        }

        public ArrayList<String> getErrorMsgs() {
            return errorMsgs;
        }
    }
    
}
