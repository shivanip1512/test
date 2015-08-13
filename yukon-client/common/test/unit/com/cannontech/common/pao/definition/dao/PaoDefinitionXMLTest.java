package com.cannontech.common.pao.definition.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import com.cannontech.common.pao.definition.model.jaxb.AttributesType;
import com.cannontech.common.pao.definition.model.jaxb.AttributesType.Attribute;
import com.cannontech.common.pao.definition.model.jaxb.CommandsType.Command;
import com.cannontech.common.pao.definition.model.jaxb.CommandsType.Command.PointRef;
import com.cannontech.common.pao.definition.model.jaxb.Pao;
import com.cannontech.common.pao.definition.model.jaxb.PaoDefinitions;
import com.cannontech.common.pao.definition.model.jaxb.PointsType;
import com.cannontech.common.pao.definition.model.jaxb.PointsType.Point;
import com.cannontech.common.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

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

    /**
     * Test which validates all paos having the attributes that do not have
     * command identified for that
     * @throws Exception
     */
    public void testValidateCommandExistsForEveryReadableAttribute() throws Exception {
        InputStreamReader reader = null;
        Map<String, List<Attribute>> paoAttributeResultMap = new HashMap<String, List<Attribute>>();
        Set<String> pointNameSet = new HashSet<String>();
        List<Command> paoCommandList = null;
        List<Attribute> paoAttributeList = null;
        PaoDefinitions paoDefinitions = null;
        try {
            reader = new InputStreamReader(this.getClass()
                                               .getClassLoader()
                                               .getResourceAsStream("com/cannontech/common/pao/definition/dao/paoDefinition.xml"));

            // Use jaxb to parse the xml file
            JAXBContext jaxbContext = JAXBContext.newInstance(PaoDefinitions.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            paoDefinitions = (PaoDefinitions) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new Exception("Exception while parsing deviceDefinition.xml. ", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                throw new Exception("Exception while closing the file.", e);
            }
        }
        List<Pao> paoList = paoDefinitions.getPao();

        // Scan through all pao in paoDefinition.xml file to retrieve all
        // attributes that does not possess any command
        for (Pao pao : paoList) {
            Map<String, List<Attribute>> pointAttributeMap = new HashMap<String, List<AttributesType.Attribute>>();
            Map<String, Command> pointCommandsMap = new HashMap<String, Command>();
            PointsType pointType = pao.getPoints();

            // pointNameSet contains all unique points for a pao
            if (pointType != null) {
                List<Point> pointsList = pointType.getPoint();
                for (Point pt : pointsList) {
                    pointNameSet.add(pt.getName());
                }
            }

            // pointAttributeMap contains point as key and Attribute list for
            // that point as the value
            if (pao.getAttributes() != null) {
                paoAttributeList = pao.getAttributes().getAttribute();
                for (Attribute attribute : paoAttributeList) {
                    List<Attribute> attributeList = null;
                    if (pointAttributeMap.get(attribute.getBasicLookup().getPoint()) == null) {
                        attributeList = new ArrayList<AttributesType.Attribute>();
                    } else {
                        attributeList = pointAttributeMap.get(attribute.getBasicLookup().getPoint());
                    }
                    attributeList.add(attribute);
                    pointAttributeMap.put(attribute.getBasicLookup().getPoint(), attributeList);

                }
            }

            // pointCommandsMap contains point as key and Command for that point
            // as the value
            if (pao.getCommands() != null) {
                paoCommandList = pao.getCommands().getCommand();
                for (Command command : paoCommandList) {
                    List<PointRef> pointRefList = command.getPointRef();
                    for (PointRef pointRef : pointRefList) {
                        pointCommandsMap.put(pointRef.getName(), command);
                    }
                }
            }

            // paoAttributeResultMap is the resultant map which contains pao id
            // as the key and its attributes that do not have any command or
            // only disabled command is associated with it as the value.
            for (String pointName : pointNameSet) {
                List<Attribute> attributeList = pointAttributeMap.get(pointName);
                if (attributeList != null) {
                    Command command = pointCommandsMap.get(pointName);
                    if (command == null || !command.isEnabled()) {
                        paoAttributeResultMap.put(pao.getId(), attributeList);
                    }

                }

            }
        }
        try {
            String jsonResult = JsonUtils.toJson(paoAttributeResultMap);
        } catch (JsonProcessingException e) {
            throw new Exception("Error while converting paoAttributeMap to Json", e);
        }

    }
}
