package com.cannontech.common.pao.definition.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.definition.loader.jaxb.CommandType;
import com.cannontech.common.pao.definition.loader.jaxb.Pao;
import com.cannontech.common.pao.definition.loader.jaxb.PointInfoType;
import com.cannontech.common.pao.definition.loader.jaxb.PointType;
import com.cannontech.common.pao.definition.loader.jaxb.Points;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point.Calculation;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point.Calculation.Components.Component;
import com.cannontech.common.pao.definition.loader.jaxb.TagType;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.collect.Maps;

public class FileLoader {
   
    private final Logger log = YukonLogManager.getLogger(FileLoader.class);    
    private Map<String, List<Point>> fileNameToPoints = new HashMap<>();
    private List<Pao> paos = new ArrayList<>();
    
    //xml files are located in yukon-shared
    private final String classpath = "classpath*:pao/definition/**/*.xml";
    
    public FileLoader(ResourceLoader loader, Resource paoXsd, Resource pointsXsd) {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(classpath);
            for (Resource resource : resources) {
                if (resource.getURL().getPath().contains("/points/")) {
                    loadPoints(resource, resource.getFilename(), pointsXsd);
                } else {
                    loadPao(resource, paoXsd);
                }
            }
            for (Pao pao : paos) {
                if (pao.getPointFiles() != null) {
                    String file = pao.getPointFiles().getPointFile();
                    Map<String, Point> allPoints = Maps.uniqueIndex(fileNameToPoints.get(file), c -> c.getName());
                    validatePointInfos(pao, allPoints);
                    validateCommands(pao, allPoints);
                    validatePoints(allPoints, file);
                }
                validateTags(pao);
            }
        } catch (IOException e) {
            throw new PaoConfigurationException("Unable to load resorces from " + classpath, e);
        }
    }
        
    /**
     * Validates that all points used in calculation exist, if the point does not exist or disabled throws exception.
     * Checks if unit of measure exits if it doesn't exist throws exception.
     */
    private void validatePoints(Map<String, Point> allPoints, String file) {
        for (Point point : allPoints.values()) {
            if (point.getUnitofmeasure() != null) {
                String unitOfMeasure = point.getUnitofmeasure().getValue().name();
                try {
                    UnitOfMeasure.valueOf(unitOfMeasure);
                } catch (Exception e) {
                    throw new PaoConfigurationException("Unable to load : point=" + point.getName() + " (" + file + ")"
                        + " unit of measure=" + unitOfMeasure + " doesn't exist");
                }
            }
            Calculation calculation = point.getCalculation();
            if (calculation != null) {
                for (Component component : calculation.getComponents().getComponent()) {
                    Point componentPoint = allPoints.get(component.getPoint());
                    if (componentPoint == null) {
                        throw new PaoConfigurationException(
                            "Unable to load : point=" + point.getName() + " component point="+component.getPoint()+" doesn't exist in " + file);
                    }else if (!componentPoint.isEnabled()) {
                        throw new PaoConfigurationException(
                            "Unable to load : point=" + point.getName() + " component point="+component.getPoint()+" is disabled " + file);
                    }
                }
            }
        }
    }
    
    /**
     * Validates that all points used in PointInfos exist, if the point does not exist throws exception. If the point is
     * disabled removes pointInfo from the list.
     */
    void validatePointInfos(Pao pao, Map<String, Point> allPoints){
        String file = pao.getPointFiles().getPointFile();
        ListIterator<PointInfoType> it = pao.getPointInfos().getPointInfo().listIterator();
        while (it.hasNext()) {
            PointInfoType pointInfo = it.next();
            Point point = allPoints.get(pointInfo.getName());
            if (point == null) {
                throw new PaoConfigurationException(
                    "Unable to load :" + pao.getPaoType() + " point=" + pointInfo.getName() + " doesn't exist in " + file);
            }else if (!point.isEnabled()) {
                log.warn("Removing pointInfo :" + pao.getPaoType() + " point=" + pointInfo.getName() + " is disabled in"
                    + file);
                it.remove();
            }
        }
    }
    
    /**
     * Validates that all points used in commands exist, if the point does not exist or disabled throws exception.
     */
    void validateCommands(Pao pao, Map<String, Point> allPoints){
        String file = pao.getPointFiles().getPointFile();
        if(pao.getCommands() != null){
            for (CommandType command : pao.getCommands().getCommand()) {
                for (PointType pointType : command.getPoint()) {
                    String pointName = pointType.getName();
                    Point commandPoint = allPoints.get(pointType.getName());
                    if (commandPoint == null) {
                        throw new PaoConfigurationException("Unable to load :" + pao.getPaoType() + "command="
                            + command.getName() + " point=" + pointName + " doesn't exist in " + file);
                    }else if (!commandPoint.isEnabled()) {
                        throw new PaoConfigurationException("Unable to load :" + pao.getPaoType() + "command="
                            + command.getName() + " point=" + pointName + " is disabled in" + file);
                    }
                }
            }
        }  
    }
    
    /**
     * Validates that all tags have a parsable option if the option exist.
     * <tag name="DLC_ADDRESS_RANGE_ENFORCE" option="0-4194303"/>
     */
    void validateTags(Pao pao){
        if(pao.getTags() != null){
            for (TagType tagType : pao.getTags().getTag()) {
                PaoTag tag = PaoTag.valueOf(tagType.getName());
                String value = tagType.getOption();
                
                if (tag.isTagHasValue()) {
                    Object convertedValue = InputTypeFactory.convertPropertyValue(tag.getValueType(), value);
                    if (convertedValue == null) {
                        throw new PaoConfigurationException(pao.getPaoType() + " has invalid option for a tag="
                            + tagType.getName() + ". Unable to convert the value.");
                    }
                } else {
                    if (StringUtils.isNotBlank(tagType.getOption())) {
                        throw new PaoConfigurationException(pao.getPaoType() + " has invalid option for a tag="
                            + tagType.getName() + ". This tag shouldn't have an option value.");
                    }
                }
            }
        }
    }
    
    /**
     * Validates and loads pao file.
     */
    private void loadPao(Resource resource, Resource paoXsd) {

        String fileUrl = "";
        try {
            fileUrl = resource.getURL().toString();
            log.debug("LOADING: " + resource.getURL());
            JAXBContext jaxbContext = JAXBContext.newInstance(Pao.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            try (InputStream inputStream = resource.getInputStream()) {
                validateXmlSchema(inputStream, paoXsd.getURL());
                Pao pao = (Pao) unmarshaller.unmarshal(resource.getInputStream());
                if (pao.isEnabled()) {
                    paos.add(pao);
                } else {
                    // ignore disabled paos
                    log.debug("Skipping disabled pao: " + pao.getDisplayName());
                }
            }
        } catch (IOException | JAXBException | SAXException | ParserConfigurationException e) {
            throw new PaoConfigurationException("Unable to load file:" + fileUrl + ". File is located in yukon-shared project.", e);
        }
    }

    /**
     * Validates and loads point file.
     */
    private void loadPoints(Resource resource, String fileName, Resource pointsXsd) {

        String fileUrl = "";
        try {
            fileUrl = resource.getURL().toString();
            log.debug("LOADING: " + resource.getURL());
            JAXBContext jaxbContext = JAXBContext.newInstance(Points.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            try (InputStream inputStream = resource.getInputStream()) {
                validateXmlSchema(inputStream, pointsXsd.getURL());
                Points points = (Points) unmarshaller.unmarshal(resource.getInputStream());
                fileNameToPoints.put(fileName, points.getPoint());
            }
        } catch (IOException | JAXBException | SAXException | ParserConfigurationException e) {
            throw new PaoConfigurationException(
                "Unable to load file:" + fileUrl + ". File is located in yukon-shared project.", e);
        }
    }

    private void validateXmlSchema(InputStream stream, URL schemaUrl)
            throws IOException, SAXException, ParserConfigurationException {
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false); // this is for DTD, so we want it off
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        factory.setSchema(schema);

        SAXParser saxParser = factory.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });

        xmlReader.parse(new InputSource(stream));
    }

    /**
     * Returns enabled jaxb paos
     */
    public List<Pao> getPaos() {
        return paos;
    }

    /**
     * Returns all enabled jaxb points in a file
     */
    public Map<String, Point> getPoints(String fileName) {
        return fileNameToPoints.get(fileName)
                               .stream()
                               .filter(Point::isEnabled)
                               .collect(Collectors.toMap(Point::getName, Function.identity()));
    }
}