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

import org.apache.log4j.Logger;
import org.junit.Test;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.definition.model.jaxb.AttributesType;
import com.cannontech.common.pao.definition.model.jaxb.AttributesType.Attribute;
import com.cannontech.common.pao.definition.model.jaxb.CommandsType;
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
public class PaoDefinitionXMLTest {
    private static final Logger log = YukonLogManager.getLogger(PaoDefinitionXMLTest.class);

    /**
     * Test which opens and parses the xml file to make sure the xml is
     * compatible with the code
     * @throws Exception - If xml parsing fails
     */
    @Test
    public void testParseXMLFile() throws Exception {
        readPaoDefinitionXMLFile();
    }

    /**
     * Test which validates all paos having the attributes that do not have
     * command identified for that
     * @throws Exception
     */
    @Test
    public void testValidateCommandExistsForEveryReadableAttribute() throws Exception {
        Map<String, List<Attribute>> paoAttributeResultMap = new HashMap<String, List<Attribute>>();
        Set<String> pointNameSet = new HashSet<String>();
        List<Command> paoCommandList = null;
        List<Attribute> paoAttributeList = null;
        PaoDefinitions paoDefinitions = readPaoDefinitionXMLFile();
        List<Pao> paoList = paoDefinitions.getPao();

        // Scan through all pao in paoDefinition.xml file to retrieve all
        // attributes that does not possess any command
        for (Pao pao : paoList) {
            Map<String, List<Attribute>> pointAttributeMap = new HashMap<String, List<AttributesType.Attribute>>();
            Map<String, List<Command>> pointCommandsMap = new HashMap<String, List<Command>>();
            PointsType pointType = pao.getPoints();

            // pointNameSet contains all unique points for a pao
            if (pointType != null) {
                List<Point> pointsList = pointType.getPoint();
                for (Point point : pointsList) {
                    pointNameSet.add(point.getName());
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

            // pointCommandsMap contains point as key and Command list for that
            // point as the value
            if (pao.getCommands() != null) {
                paoCommandList = pao.getCommands().getCommand();
                for (Command command : paoCommandList) {
                    List<Command> commandList = null;
                    List<PointRef> pointRefList = command.getPointRef();
                    for (PointRef pointRef : pointRefList) {
                        if (pointCommandsMap.get(pointRef.getName()) == null) {
                            commandList = new ArrayList<CommandsType.Command>();
                        } else {
                            commandList = pointCommandsMap.get(pointRef.getName());
                        }
                        commandList.add(command);
                        pointCommandsMap.put(pointRef.getName(), commandList);
                    }
                }
            }

            // paoAttributeResultMap is the resultant map which contains pao id
            // as the key and its attributes that do not have any command or
            // only disabled command is associated with it as the value.
            for (String pointName : pointNameSet) {
                List<Attribute> attributeList = pointAttributeMap.get(pointName);
                if (attributeList != null) {
                    List<Command> commandList = pointCommandsMap.get(pointName);

                    boolean validCommandExist = false;
                    if (commandList != null) {
                        for (Command cmd : commandList) {
                            if (cmd != null && cmd.isEnabled()) {
                                validCommandExist = true;
                                break;
                            }
                        }
                    }
                    if (!validCommandExist) {
                        paoAttributeResultMap.put(pao.getId(), attributeList);
                    }
                }

            }
        }
        try {
            String paoAttributeResultJson = JsonUtils.toJson(paoAttributeResultMap);
            log.warn("paos and attributes that do not have associated command for them : " + paoAttributeResultJson);
        } catch (JsonProcessingException e) {
            throw new Exception("Error while converting paoAttributeMap to Json", e);
        }

    }

    /**
     * This method opens and parses the paoDefinition.xml file
     * @return
     * @throws Exception
     */
    private PaoDefinitions readPaoDefinitionXMLFile() throws Exception {
        InputStreamReader reader = null;
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
