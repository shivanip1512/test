package com.cannontech.common.device.definition.dao;

import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.exolab.castor.xml.Unmarshaller;

import com.cannontech.common.device.definition.model.castor.DeviceDefinitions;

/**
 * Test class to test validity of deviceDefinition.xml file
 */
public class DeviceDefinitionXMLTest extends TestCase {

    /**
     * Test which opens and parses the xml file to make sure the xml is
     * compatible with the code
     * @throws Exception - If xml parsing fails
     */
    public void testParseXMLFile() throws Exception {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(this.getClass()
                                               .getClassLoader()
                                               .getResourceAsStream("com/cannontech/common/device/definition/dao/deviceDefinition.xml"));

            // Use castor to parse the xml file
            Unmarshaller.unmarshal(DeviceDefinitions.class, reader);

        } catch (Exception e) {
            throw new Exception("Exception while parsing deviceDefinition.xml. ", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Do nothing - tried to close
            }
        }
    }
}
