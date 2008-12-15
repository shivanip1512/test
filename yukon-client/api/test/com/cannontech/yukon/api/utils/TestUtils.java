package com.cannontech.yukon.api.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.jaxp.validation.XMLSchemaFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.junit.Assert;
import org.springframework.core.io.Resource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cannontech.yukon.api.util.SimpleXPathTemplate;


public class TestUtils {

    public static void runSuccessAssertion(SimpleXPathTemplate outputTemplate, String serviceResponseName) {
        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS = "/y:" + serviceResponseNameWithNS;
        }        
        Assert.assertNotNull("Missing success node.", outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/y:success"));
    }
    
    public static void runFailureAssertions(SimpleXPathTemplate outputTemplate, String serviceResponseName, String expectedErrorCode) {

        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS = "/y:" + serviceResponseNameWithNS;
        }
        Assert.assertNull("Should not have success node.", outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/y:success"));
        Assert.assertNotNull("No failure node present.", outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/y:failure"));
        Assert.assertEquals("Incorrect errorCode.", expectedErrorCode, outputTemplate.evaluateAsString(serviceResponseNameWithNS + "/y:failure/y:errorCode"));
    }
    
    public static void runVersionAssertion(SimpleXPathTemplate outputTemplate,
            String serviceResponseName, String version) {

        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS = "/y:" + serviceResponseNameWithNS;
        }
        Assert.assertNotNull("No version node present",
                             outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/@version"));
        Assert.assertEquals("Incorrect Response version",
                            version,
                            outputTemplate.evaluateAsString(serviceResponseNameWithNS + "/@version"));
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

            Assert.assertTrue("Element validation failed", !errorHandler.isError());
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
                Assert.fail(detailMsg);
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
