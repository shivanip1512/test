package com.cannontech.amr.errors;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorCategory;
import com.cannontech.amr.errors.dao.impl.DeviceErrorTranslatorDaoImpl;
import com.cannontech.amr.errors.model.DeviceErrorDescription;

public class DeviceErrorTranslatorDaoImplTest {
    private DeviceErrorTranslatorDaoImpl translator;
    private Document errorCodeDocument, deviceErrorsDocument;
    
    /**
     * NOTE: If you are running this test individually and it is failing to find resources, it is 
     *       because the i18n files are not on its classpath. Either run All Common Tests or add the 
     *       i18n files to DeviceErrorTranslatorDaoImplTest.launch to resolve the classpath issue.
     */
    ApplicationContext ctx = new ClassPathXmlApplicationContext();
    Resource errorCodeResource = ctx.getResource("classpath:error-code.xml");
    
    private static final String deviceErrorsPath = "com/cannontech/yukon/common/deviceErrors.xml";

    private static class ErrorDescription {
        public String porter;
        public String description;
        public String troubleshooting;
        
        public ErrorDescription(String porter, String description, String troubleshooting) {
            this.porter = porter;
            this.description = description;
            this.troubleshooting = troubleshooting;
        }
    }

    @Before
    public void setUp() throws Exception {
        //setup for test_TranslateErrorCode
        translator = new DeviceErrorTranslatorDaoImpl();
        ClassLoader classLoader = getClass().getClassLoader();
        translator.setErrorDefinitions(errorCodeResource.getInputStream());
        translator.initialize();
        
        //setup for test_ErrorDefinitions
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        errorCodeDocument    = builder.build(errorCodeResource.getInputStream());
        deviceErrorsDocument = builder.build(classLoader.getResourceAsStream(deviceErrorsPath));
    }

    @Test
    public void test_TranslateErrorCode() {
        DeviceErrorDescription description = translator.translateErrorCode(15);
        assertNotNull("returned null data", description);
        assertNotNull("returned null description", description.getDescription());
        assertNotNull("returned null trouble", description.getTroubleshooting());

        description = translator.translateErrorCode(23475845); // no way this is a real error!
        assertNotNull("returned null data", description);
        assertNotNull("returned null description", description.getDescription());
        assertNotNull("returned null trouble", description.getTroubleshooting());
    }
    
    /**
     * There are two different files that contain porter device error definitions. These files are
     * deviceErrors.xml and error-code.xml. According to a comment in DeviceErrorTranslotorDaoImpl,
     * we need two separate xml files because the EIM Server does not have access to deviceErrors.xml.
     * The purpose of this test is to ensure that the following items happen when a new error is added:
     * 
     *      the error is defined in deviceErrors.xml with valid key names and matches error-code.xml
     *      the error is defined in error-code.xml with a valid category and matches deviceErrors.xml
     *      an entry for the error code is added to DeviceError.java with a valid category
     *      for each DeviceErrorCategory there is a corresponding entry in deviceErrors.xml
     */
    @Test
    public void test_ErrorDefinitions() throws JDOMException, IOException {   
        
        XMLOutputter xmlOut = new XMLOutputter(Format.getRawFormat());
        Map<Integer, ErrorDescription> errorCodeMap = new HashMap<Integer, ErrorDescription>();
        Map<Integer, ErrorDescription> deviceErrorsMap = new HashMap<Integer, ErrorDescription>();
        List<DeviceErrorCategory> deviceErrorsCategories = new ArrayList<DeviceErrorCategory>();

        //build a map of error-code.xml entries, validate category, and validate DeviceError.java entry
        Element rootElement = errorCodeDocument.getRootElement();
        List<Element> children = rootElement.getChildren("error");
        for (Element errorEl : children) {
            //check if this error-code.xml entry has a corresponding DeviceError.java
            Integer errorCode = Integer.valueOf((errorEl.getAttributeValue("code"))).intValue();
            DeviceError deviceError = DeviceError.getErrorByCode(errorCode);
            assertFalse("Error code " + errorCode + " has an entry in error-code.xml but not in DeviceError.java", deviceError.equals(DeviceError.UNKNOWN) && errorCode != -1);
            
            //check DeviceError category against error-code.xml category field
            String categoryText = errorEl.getChildText("category");
            DeviceErrorCategory deviceErrorCategory = DeviceErrorCategory.getByName(categoryText);
            assertNotNull("Entry with error code " + errorCode + " and category " + categoryText + " in error-code.xml contains a category that is not defined in DeviceErrorCategory.java", deviceErrorCategory);
            
            //build the rest of the DeviceErrorDescription and put it in map
            String porter = Text.normalizeString(errorEl.getChildText("porter"));
            String description = Text.normalizeString(errorEl.getChildText("description"));
            assertFalse("Description for error code " + errorCode + " in error-code.xml cannot be empty.", description.isEmpty());
            
            //doing a replace after parsing the XML is gross, but the XMLOutputter forces escaping of the characters >,<,\",&,\r,\t,\n
            String troubleshooting = Text.normalizeString(xmlOut.outputElementContentString(errorEl.getChild("troubleshooting")));
            troubleshooting = StringEscapeUtils.unescapeXml(troubleshooting);
            
            ErrorDescription errorDescription = new ErrorDescription(porter, description, troubleshooting);
            errorCodeMap.put(errorCode, errorDescription);
        }
        
        //only need to check that each DeviceError.java has an entry in error-code.xml because
        //each error-code.xml entry was confirmed to exist in DeviceError.java above
        if (DeviceError.values().length != errorCodeMap.size()) {
            DeviceError.getErrorsMap().keySet().stream().forEach(errorNum -> {
                assertTrue("Error code " + errorNum + " is defined in DeviceError.java but not in error-code.xml", errorCodeMap.containsKey(errorNum));
            });
        }

        //build deviceErrors.xml map and check that it matches the map built from error-code.xml
        rootElement = deviceErrorsDocument.getRootElement();
        children = rootElement.getChildren();
        for (Element errorEl : children) {
            //check if this deviceErrors.xml entry is a duplicate and if it has a corresponding DeviceError.java
            String keyName = errorEl.getAttributeValue("key");
            String elementValue = errorEl.getTextNormalize();
            String[] keyElements = keyName.split("\\.");
            assertTrue("Key " + keyName + " too short, must follow the form yukon.web.error.<code or category>", keyElements.length > 4);
            assertEquals(keyElements[0], "yukon");
            assertEquals(keyElements[1], "web");
            assertEquals(keyElements[2], "error");
            switch (keyElements[3]) {
                case "category":
                    //check DeviceErrorCategory.java against deviceErrors.xml category entry and makes sure it is not a duplicate
                    DeviceErrorCategory deviceErrorCategory = DeviceErrorCategory.getByName(elementValue);
                    assertNotNull("An error category entry with value " + elementValue + " in deviceErrors.xml is not defined in DeviceErrorCategory.java", deviceErrorCategory);
                    String keyCategoryText = keyElements[4];
                    assertEquals(keyName + " and " + elementValue +  " in deviceErrors.xml do not map to the same DeviceErrorCategory", deviceErrorCategory, DeviceErrorCategory.valueOf(keyCategoryText));
                    assertFalse("Duplicate category entries in deviceErrors.xml for category " + keyCategoryText, deviceErrorsCategories.contains(deviceErrorCategory));
                    deviceErrorsCategories.add(deviceErrorCategory);
                    break;
                case "code":
                    //parse and validate entries in deviceErrors.xml
                    Integer errorCode = Integer.valueOf(keyElements[4]);
                    DeviceError deviceError = DeviceError.getErrorByCode(errorCode);
                    assertFalse("Error code " + errorCode + " has an entry in deviceErrors.xml but not in DeviceError.java", deviceError.equals(DeviceError.UNKNOWN)  && errorCode != -1);
                    String typeOfInfo = keyElements[5];
                    ErrorDescription errorDescription = deviceErrorsMap.get(errorCode);
                    //if an entry (porter, description, or troubleshooting) for this error code has already been parsed
                    if (errorDescription != null) { 
                        switch (typeOfInfo) {
                            case "porter":
                                assertNull("Duplicate key in deviceErrors.xml for error code " + errorCode + " and info type porter", errorDescription.porter);
                                assertEquals("Porter entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).porter, elementValue);
                                errorDescription.porter = elementValue;
                                break;
                            case "description":
                                assertNull("Duplicate key in deviceErrors.xml for error code " + errorCode + " and info type description", errorDescription.description);
                                assertEquals("Description entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).description, elementValue);
                                errorDescription.description = elementValue;
                                break;
                            case "troubleshooting":
                                assertNull("Duplicate key in deviceErrors.xml for error code " + errorCode + " and info type troubleshooting", errorDescription.troubleshooting);
                                assertEquals("Troubleshooting entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).troubleshooting, elementValue);
                                errorDescription.troubleshooting = elementValue;
                                break;
                            default:
                                fail("Invalid key in deviceErrors.xml, keys must be of the form yukon.web.error.code.<error code number>.<porter, description, or troubleshooting> or yukon.web.error.category.<category name>");
                        }
                    //when no entries (porter, description, or troubleshooting) have been parsed for this error code
                    } else {
                        switch (typeOfInfo) {
                            case "porter":
                                assertEquals("Porter entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).porter, elementValue);
                                deviceErrorsMap.put(errorCode, new ErrorDescription(elementValue, null, null));
                                break;
                            case "description":
                                assertEquals("Description entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).description, elementValue);
                                deviceErrorsMap.put(errorCode, new ErrorDescription(null, elementValue, null));
                                break;
                            case "troubleshooting":
                                assertEquals("Troubleshooting entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).troubleshooting, elementValue);
                                deviceErrorsMap.put(errorCode, new ErrorDescription(null, null, elementValue));
                                break;
                            default:
                                assertTrue("Invalid key in deviceErrors.xml, keys must be of the form yukon.web.error.code.<error code number>.<porter, description, or troubleshooting> or yukon.web.error.category.<category name>",false);
                        }
                    }
                break;
                default:
                    fail("Unknown key element in deviceErrors.xml: " + keyElements[3]);
            }
        }
        
        //only need to check that each DeviceError.java has an entry in deviceError.xml because
        //each deviceError.xml entry was confirmed to exist in DeviceError.java above
        //this also confirms the # of deviceError.xml entries = the # of error-code.xml entries
        if (DeviceError.values().length != deviceErrorsMap.size()) {
            DeviceError.getErrorsMap().keySet().stream().forEach(errorNum -> {
                assertTrue("Error code " + errorNum + " is defined in DeviceError.java but not in deviceError.xml", deviceErrorsMap.containsKey(errorNum));
            });
        }
        
        //make sure all of the DeviceErrorCategory.java values have been added to deviceErrors.xml
        DeviceErrorCategory[] categories = DeviceErrorCategory.values();
        if (deviceErrorsCategories.size() != categories.length) {
            Arrays.asList(categories).stream().forEach(category -> {
                assertTrue("DeviceErrorCategory " + category + " is defined in DeviceErrorCategory.java but not in deviceErrors.xml", deviceErrorsCategories.remove(category));
            });
        }
    }
}
