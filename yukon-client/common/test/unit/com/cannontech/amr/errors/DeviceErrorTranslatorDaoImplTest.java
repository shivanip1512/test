package com.cannontech.amr.errors;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorCategory;
import com.cannontech.amr.errors.dao.impl.DeviceErrorTranslatorDaoImpl;
import com.cannontech.amr.errors.model.DeviceErrorDescription;

public class DeviceErrorTranslatorDaoImplTest {
    private DeviceErrorTranslatorDaoImpl translator;
    private Document errorCodeDocument, deviceErrorsDocument;

    @Before
    public void setUp() throws Exception {
        //setup for test_TranslateErrorCode
        translator = new DeviceErrorTranslatorDaoImpl();
        ClassLoader classLoader = getClass().getClassLoader();
        String path = "com/cannontech/amr/errors/dao/impl/error-code.xml";
        InputStream resourceAsStream = classLoader.getResourceAsStream(path);
        translator.setErrorDefinitions(resourceAsStream);
        translator.initialize();
        
        //setup for test_ErrorDefinitions
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        errorCodeDocument = builder.build(new File("src/com/cannontech/amr/errors/dao/impl/error-code.xml"));
        deviceErrorsDocument = builder.build(new File("i18n/en_US/com/cannontech/yukon/common/deviceErrors.xml"));
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
        Map<Integer, DeviceErrorDescription> errorCodeMap = new HashMap<Integer, DeviceErrorDescription>();
        Map<Integer, DeviceErrorDescription> deviceErrorsMap = new HashMap<Integer, DeviceErrorDescription>();
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
            String[] escapers = {"&gt;", "&lt;", "&quot;", "&amp;", "&#xD;", "&#x9;", "&#xA;"};
            String[] escapees = {">"   , "<"   , "\""    , "&"    , "\r"   , "\t"   , "\n"   };
            troubleshooting = StringUtils.replaceEachRepeatedly(troubleshooting, escapers, escapees);
            
            DeviceErrorDescription deviceErrorDescription = new DeviceErrorDescription(deviceError, porter, description, troubleshooting);
            errorCodeMap.put(errorCode, deviceErrorDescription);
        }
        
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
            if ( StringUtils.substring(keyName, 16, 24).equals("category")) {
                //check DeviceErrorCategory against deviceErrors.xml category entry and makes sure it is not a duplicate
                DeviceErrorCategory deviceErrorCategory = DeviceErrorCategory.getByName(elementValue);
                assertNotNull("An error category entry with value " + elementValue + " in deviceErrors.xml is not defined in DeviceErrorCategory.java", deviceErrorCategory);
                String keyCategoryText = keyName.substring(25);
                assertEquals("Category entries in deviceErrors.xml must be of the form yukon.web.error.category.<DeviceErrorCategory value> and map to the DeviceErrorCategory value provided. Entry with key " + keyName + " violates this rule.", deviceErrorCategory, DeviceErrorCategory.valueOf(keyCategoryText));
                assertFalse("Duplicate category entries in deviceErrors.xml for category " + keyCategoryText, deviceErrorsCategories.contains(deviceErrorCategory));
                deviceErrorsCategories.add(deviceErrorCategory);
            } else {
                //parse and validate entries in deviceErrors.xml
                String[] stringArgs = StringUtils.split(keyName.substring(21), ".");
                Integer errorCode = Integer.valueOf(stringArgs[0]);
                DeviceError deviceError = DeviceError.getErrorByCode(errorCode);
                assertFalse("Error code " + errorCode + " has an entry in deviceErrors.xml but not in DeviceError.java", deviceError.equals(DeviceError.UNKNOWN)  && errorCode != -1);
                String typeOfInfo = stringArgs[1];
                DeviceErrorDescription deviceErrorDescription = deviceErrorsMap.get(errorCode);
                //if an entry (porter, description, or troubleshooting) for this error code has already been parsed
                if (deviceErrorDescription != null) { 
                    switch (typeOfInfo) {
                        case "porter":
                            assertNull("Duplicate key in deviceErrors.xml for error code " + errorCode + " and info type porter", deviceErrorDescription.getPorter());
                            assertEquals("Porter entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).getPorter(), elementValue);
                            deviceErrorDescription.setPorter(elementValue);
                            break;
                        case "description":
                            assertNull("Duplicate key in deviceErrors.xml for error code " + errorCode + " and info type description", deviceErrorDescription.getDescription());
                            assertEquals("Description entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).getDescription(), elementValue);
                            deviceErrorDescription.setDescription(elementValue);
                            break;
                        case "troubleshooting":
                            assertNull("Duplicate key in deviceErrors.xml for error code " + errorCode + " and info type troubleshooting", deviceErrorDescription.getTroubleshooting());
                            assertEquals("Troubleshooting entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).getTroubleshooting(), elementValue);
                            deviceErrorDescription.setTroubleshooting(elementValue);
                            break;
                        default:
                            assertTrue("Invalid key in deviceErrors.xml, keys must be of the form yukon.web.error.code.<error code number>.<porter, description, or troubleshooting> or yukon.web.error.category.<category name>",false);
                    }
                //when no entries (porter, description, or troubleshooting) have been parsed for this error code
                } else {
                    switch (typeOfInfo) {
                        case "porter":
                            assertEquals("Porter entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).getPorter(), elementValue);
                            deviceErrorsMap.put(errorCode, new DeviceErrorDescription(DeviceError.getErrorByCode(errorCode), elementValue, null, null));
                            break;
                        case "description":
                            assertEquals("Description entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).getDescription(), elementValue);
                            deviceErrorsMap.put(errorCode, new DeviceErrorDescription(DeviceError.getErrorByCode(errorCode), null, elementValue, null));
                            break;
                        case "troubleshooting":
                            assertEquals("Troubleshooting entry for error code " + errorCode + " in deviceErrors.xml does not match the corresponding entry in error-code.xml", errorCodeMap.get(errorCode).getTroubleshooting(), elementValue);
                            deviceErrorsMap.put(errorCode, new DeviceErrorDescription(DeviceError.getErrorByCode(errorCode), null, null, elementValue));
                            break;
                        default:
                            assertTrue("Invalid key in deviceErrors.xml, keys must be of the form yukon.web.error.code.<error code number>.<porter, description, or troubleshooting> or yukon.web.error.category.<category name>",false);
                    }
                }
            }
        }
        //make sure all of the DeviceErrorCategory values have been added to deviceErrors.xml
        DeviceErrorCategory[] categories = DeviceErrorCategory.values();
        if (deviceErrorsCategories.size() != categories.length) {
            Arrays.asList(categories).stream().forEach(category -> {
                assertTrue("DeviceErrorCategory " + category + " is defined in DeviceErrorCategory.java but not in deviceErrors.xml", deviceErrorsCategories.remove(category));
            });
        }
    }
}
