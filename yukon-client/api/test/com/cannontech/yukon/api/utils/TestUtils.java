package com.cannontech.yukon.api.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Assert;

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
    
    /**
     * Creates and returns a temp file from the resource on the classpath
     * @param resourceName
     * @param clazz
     * @return tempFile
     */
    public static File getResourceAsTempFile(String resourceName, Class clazz) {
        File tempFile = null;
        InputStream in = null;
        OutputStream out = null;
        boolean processedOk = false;
        try {
            String prefix = FilenameUtils.getBaseName(resourceName);
            String suffix = FilenameUtils.getExtension(resourceName);

            in = clazz.getResourceAsStream(resourceName);
            tempFile = File.createTempFile(prefillPrefix(prefix), "." + suffix);
            out = new FileOutputStream(tempFile);
            IOUtils.copy(in, out);
            processedOk = true;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            if (!processedOk) {
                deleteQuietly(tempFile);
            }
        }

        return tempFile;
    }

    private static String prefillPrefix(String prefix) {
        String outPrefix = prefix;
        if (StringUtils.isBlank(prefix) || prefix.trim().length() < 3) {
            outPrefix = "tmp";
        }
        return outPrefix;
    }

    public static void deleteQuietly(File tmpFile) {
        try {
            tmpFile.delete();
        } catch (SecurityException e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * Constructs JDOM from the given file and returns the root element
     * @param tempFile
     * @return root element
     */
    public static Element getRootElement(File tempFile) {
        Element rootElement = null;
        try {
            if (tempFile != null) {
                Document doc = null;
                SAXBuilder builder = new SAXBuilder();
                if (builder != null) {
                    doc = builder.build(tempFile);
                    rootElement = doc.getRootElement();
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);

        } catch (JDOMException e) {
            log.error(e.getMessage(), e);
        }
        return rootElement;
    }    
}
