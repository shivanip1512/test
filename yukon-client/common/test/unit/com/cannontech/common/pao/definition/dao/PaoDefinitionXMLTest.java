package com.cannontech.common.pao.definition.dao;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.cannontech.common.pao.definition.model.jaxb.PaoDefinitions;

import junit.framework.TestCase;

/**
 * Test class to test validity of deviceDefinition.xml file
 */
public class PaoDefinitionXMLTest extends TestCase {

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
                                               .getResourceAsStream("com/cannontech/common/pao/definition/dao/paoDefinition.xml"));

            // Use jaxb to parse the xml file
            JAXBContext jaxbContext = JAXBContext.newInstance(PaoDefinitions.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.unmarshal(reader);

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
