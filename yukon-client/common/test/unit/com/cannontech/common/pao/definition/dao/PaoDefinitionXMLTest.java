package com.cannontech.common.pao.definition.dao;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.cannontech.common.pao.definition.model.jaxb.PaoDefinitions;

/**
 * Test class to test validity of paoDefinition.xml file
 */
public class PaoDefinitionXMLTest {

    /**
     * Test which opens and parses the xml file to make sure the xml is
     * compatible with the code
     * 
     * @throws Exception - If xml parsing fails
     */
    @Test
    public void testParseXMLFile() throws Exception {
        readPaoDefinitionXMLFile();
    }

    /**
     * This method opens and parses the paoDefinition.xml file
     * 
     * @return
     * @throws Exception
     */
    private PaoDefinitions readPaoDefinitionXMLFile() throws Exception {
        InputStreamReader reader = null;
        PaoDefinitions paoDefinitions = null;
        try {
            reader =
                new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(
                    "com/cannontech/common/pao/definition/dao/paoDefinition.xml"));

            // Use jaxb to parse the xml file
            JAXBContext jaxbContext = JAXBContext.newInstance(PaoDefinitions.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            paoDefinitions = (PaoDefinitions) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new Exception("Exception while parsing paoDefinition.xml. ", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new Exception("Exception while closing the file.", e);
            }
        }
        return paoDefinitions;
    }
}
