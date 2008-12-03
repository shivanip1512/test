package com.cannontech.yukon.api.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.springframework.core.io.Resource;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;


public class TestUtils {

    private static Logger log = YukonLogManager.getLogger(TestUtils.class);
    
    public static void runSuccessAssertion(SimpleXPathTemplate outputTemplate, String serviceResponseName) {
        
        Assert.assertNotNull("Missing success node.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:success"));
    }
    
    public static void runFailureAssertions(SimpleXPathTemplate outputTemplate, String serviceResponseName, String expectedErrorCode) {
        
        Assert.assertNull("Should not have success node.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:success"));
        Assert.assertNotNull("No failure node present.", outputTemplate.evaluateAsNode("/y:" + serviceResponseName + "/y:failure"));
        Assert.assertEquals("Incorrect errorCode.", expectedErrorCode, outputTemplate.evaluateAsString("/y:" + serviceResponseName + "/y:failure/y:errorCode"));
    }
    
    public static void runVersionAssertion(SimpleXPathTemplate outputTemplate,
            String serviceResponseName, String version) {

        String serviceResponseNameWithNS = serviceResponseName;
        if (!serviceResponseNameWithNS.startsWith("/y:")) {
            serviceResponseNameWithNS += "/y:";
        }
        Assert.assertNotNull("No version node present",
                             outputTemplate.evaluateAsNode(serviceResponseNameWithNS + "/@version"));
        Assert.assertEquals("Incorrect Response version",
                            version,
                            outputTemplate.evaluateAsString(serviceResponseNameWithNS + "/@version"));
    }
    
    public static void validateResponse(Element respElement,
            Resource schemaResource) {
        try {
            // setup the validating builder
            Assert.assertTrue("Response Schema Resource not found", schemaResource.exists());
            String schemaLocation = "http://yukon.cannontech.com/api " + schemaResource.getURI();            

            SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
            builder.setFeature("http://apache.org/xml/features/validation/schema", true);
            builder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", schemaLocation);
            
            SimpleErrorHandler errorHandler = new SimpleErrorHandler();
            builder.setErrorHandler(errorHandler);

            // get the xml from the respElement
            StringWriter strWriter = new StringWriter();
            XMLOutputter xmlOut = new XMLOutputter();
            xmlOut.output(respElement, strWriter);
            log.info("Response XML=[" + strWriter + "]");

            // run the respElement XML thru the validating builder
            Document doc = builder.build(new StringReader(strWriter.toString()));

            // here is the respElement back again
            Element respElementBack = doc.getRootElement();
            Assert.assertNotNull("Response element rebuild failed", respElementBack);

            // just to see the rebuilt element content back
            StringWriter strWriterBack = new StringWriter();
            xmlOut.output(respElementBack, strWriterBack);
            log.info("Re-converted Response XML =[" + strWriterBack + "]");

            Assert.assertTrue("Response element validation failed", !errorHandler.isError());

        } catch (IOException e) {
            log.error("error: " + e.getMessage());
        } catch (JDOMException e) {
            log.error("error: " + e.getMessage());
        }
    }

    public static class SimpleErrorHandler implements ErrorHandler {

        private int errorCount = 0;
        private int fatalErrorCount = 0;
        private int warnCount = 0;

        public void error(SAXParseException exception) {
            errorCount++;
            log.error("error: " + exception.getMessage());
        }

        public void fatalError(SAXParseException exception) {
            fatalErrorCount++;
            log.fatal("fatalError: " + exception.getMessage());
        }

        public void warning(SAXParseException exception) {
            warnCount++;
            log.warn("warning: " + exception.getMessage());
        }

        public boolean isError() {
            return (errorCount > 0 || fatalErrorCount > 0);
        }
        
        public boolean isWarn() {
            return (warnCount > 0);
        }        
    }
    
}
