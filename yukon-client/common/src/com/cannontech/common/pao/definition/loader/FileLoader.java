package com.cannontech.common.pao.definition.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.RemoteLoginSession;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.loader.jaxb.CategoryType;
import com.cannontech.common.pao.definition.loader.jaxb.CommandType;
import com.cannontech.common.pao.definition.loader.jaxb.Configurations;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories.Category;
import com.cannontech.common.pao.definition.loader.jaxb.OverrideCategory;
import com.cannontech.common.pao.definition.loader.jaxb.OverridePointInfo;
import com.cannontech.common.pao.definition.loader.jaxb.OverrideTag;
import com.cannontech.common.pao.definition.loader.jaxb.Overrides;
import com.cannontech.common.pao.definition.loader.jaxb.Pao;
import com.cannontech.common.pao.definition.loader.jaxb.PaoTypes;
import com.cannontech.common.pao.definition.loader.jaxb.Point;
import com.cannontech.common.pao.definition.loader.jaxb.Point.Calculation;
import com.cannontech.common.pao.definition.loader.jaxb.Point.Calculation.Components.Component;
import com.cannontech.common.pao.definition.loader.jaxb.PointInfoType;
import com.cannontech.common.pao.definition.loader.jaxb.PointInfos;
import com.cannontech.common.pao.definition.loader.jaxb.PointInfosType;
import com.cannontech.common.pao.definition.loader.jaxb.PointType;
import com.cannontech.common.pao.definition.loader.jaxb.Points;
import com.cannontech.common.pao.definition.loader.jaxb.TagType;
import com.cannontech.common.pao.definition.loader.jaxb.Tags;
import com.cannontech.common.pao.definition.loader.jaxb.TagsType;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

public class FileLoader {
   
    private final static String OVERRIDE_FILE_LOCATION= CtiUtilities.getYukonBase() + "/Server/Config/deviceDefinition.xml";
    private final static File overrideFile = new File(OVERRIDE_FILE_LOCATION);
    private final Logger log = YukonLogManager.getLogger(FileLoader.class);    
    private List<Pao> paos = new ArrayList<>();
    private Map<String, List<Point>> paoTypeToPoints = new HashMap<>();
    private List<String> allPaoTypes = Arrays.stream(PaoType.values()).map(PaoType::name).collect(Collectors.toList());       
    //xml files are located in yukon-shared
    private final String classpath = "classpath*:pao/definition/**/*.xml";
    
    enum Action {
        ADD, UPDATE, REMOVE
    }

    /**
     * Loads and validates pao definition files.
     */
    public FileLoader(ResourceLoader loader, Resource paoXsd, Resource pointsXsd) {
        loadPaosAndPoints(loader, paoXsd, pointsXsd);
        validate();
    }
    
    private void loadPaosAndPoints(ResourceLoader loader, Resource paoXsd, Resource pointsXsd) {
        log.info("Loading device definitions.");
        Map<String, List<Point>> fileNameToPoints = new HashMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(classpath);
            for (Resource resource : resources) {
                if (resource.getURL().getPath().contains("/points/")) {
                    loadPoints(resource, resource.getFilename(), pointsXsd, fileNameToPoints);
                } else {
                    loadPao(resource, paoXsd);
                }
            }
            paos.forEach(pao -> {
                if (pao.getPointFiles() != null) {
                    String file = pao.getPointFiles().getPointFile();
                    paoTypeToPoints.put(pao.getPaoType(), new ArrayList<>(fileNameToPoints.get(file)));
                }
            });
            
        } catch (IOException e) {
            throw new PaoConfigurationException("Unable to load resorces from " + classpath, e);
        }
        log.info("Loading device definitions complete.");
    }
    
    private void validate(){
        log.info("Validating device definitions.");
        paos.forEach(pao -> {
            if (paoTypeToPoints.get(pao.getPaoType()) != null) {
                validatePointInfos(pao);
                validateCommands(pao);
                validatePoints(pao);
            }
            validateTags(pao);
        });
        log.info("Validating device definitions complete.");
    }
        
    /**
     * Validates that all points used in calculation exist, if the point does not exist or disabled throws exception.
     * Checks if unit of measure exits if it doesn't exist throws exception.
     */
    private void validatePoints(Pao pao) {
        String file = "";
        if(pao.getPointFiles() != null){
            file = pao.getPointFiles().getPointFile();
        } else {
            //point is in override file, device doesn't have point file. Example: VIRTUAL_SYSTEM
            file = OVERRIDE_FILE_LOCATION;
        }
        for (Point point : paoTypeToPoints.get(pao.getPaoType())) {
            if (point.getUnitofmeasure() != null) {
                String unitOfMeasure = point.getUnitofmeasure().getValue().name();
                try {
                    UnitOfMeasure.valueOf(unitOfMeasure);
                } catch (Exception e) {
                    throw new PaoConfigurationException(pao.getPaoType() + " unable to load: point=" + point.getName()
                        + " (" + file + ")" + " unit of measure=" + unitOfMeasure + " doesn't exist");
                }
            }
            Calculation calculation = point.getCalculation();
            if (calculation != null) {
                for (Component component : calculation.getComponents().getComponent()) {
                    Point componentPoint = findPointByName(pao, component.getPoint());
                    if (componentPoint == null) {
                        throw new PaoConfigurationException(
                            pao.getPaoType() + " unable to load: point=" + point.getName() + " component point="
                                + component.getPoint() + " doesn't exist in " + file);
                    } else if (!componentPoint.isEnabled()) {
                        throw new PaoConfigurationException(pao.getPaoType() + " unable to load: point=" + point.getName()
                            + " component point=" + component.getPoint() + " is disabled " + file);
                    }
                }
            }
        }
    }
    
    /**
     * Validates that all points used in PointInfos exist, if the point does not exist throws exception. If the point is
     * disabled removes pointInfo from the list.
     */
    void validatePointInfos(Pao pao) {
        pao.getPointInfos().getPointInfo().removeIf(pointInfo -> !validatePointInfo(pao, pointInfo.getName()));
    }
    
    /**
     * Returns false if the point is disabled in a points file.
     */
    private boolean validatePointInfo(Pao pao, String pointName) {
        Point point = findPointByName(pao, pointName);
        if (point == null) {
            throw new PaoConfigurationException(pao.getPaoType() + " unable to load: " + pointName + " point="
                + pointName + " doesn't exist in " + pao.getPointFiles().getPointFile());
        } else if (!point.isEnabled()) {
            log.warn("Removing pointInfo :" + pao.getPaoType() + " point=" + pointName + " is disabled in"
                + pao.getPointFiles().getPointFile());
            return false;
        }
        return true;
    }
    
    private Point findPointByName(Pao pao, String pointToFind){
        return paoTypeToPoints.get(pao.getPaoType())
                                      .stream()
                                      .filter(p -> p.getName().equals(pointToFind))
                                      .findFirst()
                                      .orElse(null);
    }

    /**
     * Validates that all points used in commands exist, if the point does not exist or disabled throws
     * exception.
     */
    void validateCommands(Pao pao) {
        if (pao.getCommands() != null) {
            String file = pao.getPointFiles().getPointFile();
            for (CommandType command : pao.getCommands().getCommand()) {
                for (PointType pointType : command.getPoint()) {
                    String pointName = pointType.getName();
                    Point commandPoint = findPointByName(pao, pointName);
                    if (commandPoint == null) {
                        throw new PaoConfigurationException(pao.getPaoType() + " unable to load: " + pao.getPaoType()
                            + "command=" + command.getName() + " point=" + pointName + " doesn't exist in " + file);
                    } else if (!commandPoint.isEnabled()) {
                        throw new PaoConfigurationException(pao.getPaoType() + " unable to load: " + pao.getPaoType()
                            + "command=" + command.getName() + " point=" + pointName + " is disabled in" + file);
                    }
                }
            }
        }
    }
    
    /**
     * Validates that all tags have a parsable option if the option exist.
     * <tag name="DLC_ADDRESS_RANGE_ENFORCE" option="0-4194303"/>
     */
    private void validateTags(Pao pao) {
        Stream.of(pao.getTags())
              .filter(Objects::nonNull)
              .map(TagsType::getTag)
              .flatMap(List::stream)
              .forEach(tag -> validateTag(tag, pao.getPaoType()));
    }
    
    private void validateTag(TagType tagType, String paoType) {
        PaoTag tag = PaoTag.valueOf(tagType.getName());
        String value = tagType.getOption();

        if (tag.isTagHasValue()) {
            Object convertedValue = InputTypeFactory.convertPropertyValue(tag.getValueType(), value);
            if (convertedValue == null) {
                throw new PaoConfigurationException(
                    paoType + " has invalid option for a tag=" + tagType.getName() + ". Unable to convert the value.");
            }
        } else {
            if (StringUtils.isNotBlank(tagType.getOption())) {
                throw new PaoConfigurationException(paoType + " has invalid option for a tag=" + tagType.getName()
                    + ". This tag shouldn't have an option value.");
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
            throw new PaoConfigurationException("Unable to parse file:" + fileUrl + ". File is located in yukon-shared project.", e);
        }
    }

    /**
     * Validates and loads point file.
     */
    private void loadPoints(Resource resource, String fileName, Resource pointsXsd, Map<String, List<Point>> fileNameToPoints) {

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
     * Loads and applies custom overrides.
     */
    public void override(Resource deviceDefinitionXsd) {
        if (overrideFile.exists()) {
            Overrides overrides = loadOverrideFile(deviceDefinitionXsd);
            if (overrides != null) {
                applyOverrides(overrides);
                validate();
            }
        }
    }

    private void applyOverrides(Overrides overrides) {
        log.info("- Applying custom overrides to device definitions.");

        Map<String, Pao> typeToPao =  Maps.uniqueIndex(paos, Pao::getPaoType);
        overrides.getOverride().forEach(override -> {  
            Map<Boolean, List<String>> validPaoTypes = 
                    Optional.ofNullable(override.getPaoTypes()).stream()
                            .flatMap(pt -> pt.getPaoType().stream())
                            .collect(Collectors.partitioningBy(allPaoTypes::contains));
            
            List<OverrideCategory> overrideCategories = 
                    Optional.ofNullable(override.getConfigurations())
                            .map(Configurations::getCategory)
                            .orElse(Collections.emptyList());
            List<OverrideTag> overrideTags = 
                    Optional.ofNullable(override.getTags())
                            .map(Tags::getTag)
                            .orElse(Collections.emptyList());
            List<OverridePointInfo> overridePointInfos = 
                    Optional.ofNullable(override.getPointInfos())
                            .map(PointInfos::getPointInfo)
                            .orElse(Collections.emptyList());

            validPaoTypes.get(true).forEach(paoType -> {
                try {
                    log.info("");
                    log.info("-- Applying custom overrides for device " + paoType + "");
                    Pao pao = typeToPao.get(paoType);
                    overrideCreatable(pao, override.isCreatable());
                    overrideTags(pao, overrideTags);
                    overrideConfigurations(pao, overrideCategories);
                    overidePointInfo(pao, overridePointInfos);
                    log.info("-- Applying custom overrides for device " + paoType + " is complete.");
                } catch (Exception e) {
                    log.error("Can't parse " + paoType + " override in " + OVERRIDE_FILE_LOCATION, e);
                }
            });
            
            validPaoTypes.get(false).stream()
                                    .reduce((a,b) -> a + "," + b)
                                    .ifPresent(invalidPaoTypes -> log.error("Ignoring invalid types: " + invalidPaoTypes));
        });

        log.info("- Applying custom overrides to device definitions complete.");
    }

    private void overrideCreatable(Pao pao, Boolean creatable) {
        if (creatable != null) {
            pao.setCreatable(creatable);
            logOverride(Action.UPDATE, pao.getPaoType(), creatable.toString(), "creatable", null, null);
        }
    }
    
    private void overidePointInfo(Pao pao, List<OverridePointInfo> pointInfos) {
        pointInfos.forEach(override -> {
            switch (Action.valueOf(override.getAction().toUpperCase())) {
            case REMOVE:
                removeExistingPointInfo(pao, override, Action.REMOVE);
                break;
            case ADD:
                createUpdatePointInfo(pao, override, Action.ADD);
                break;
            case UPDATE:
                createUpdatePointInfo(pao, override, Action.UPDATE);
                break;
            }
        });
    }

    private void removeExistingPointInfo(Pao pao, OverridePointInfo override, Action action) {
        String pointNameToRemove = override.getName();
        boolean isRemoved = Optional.ofNullable(pao.getPointInfos()).map(PointInfosType::getPointInfo)
                                                                    .map(t -> t.removeIf(i -> i.getName().equals(pointNameToRemove)))
                                                                    .orElse(false);
        if (isRemoved) {
            logOverride(Action.REMOVE, pao.getPaoType(), pointNameToRemove, "pointInfo", null, null);
        }
        if (!isRemoved && action == Action.REMOVE) {
            logOverride(Action.REMOVE, pao.getPaoType(), pointNameToRemove, "pointInfo", "pointInfo doesn't exist.", Level.WARN);
        }
    }
    
    private void logOverride(Action action, String paoType, String value, String item, String msgToDisplay,
            Level level) {
        String msg = "---- " + paoType + " " + action.name().toLowerCase() + " " + item + "(" + value + ")";
        if (msgToDisplay == null) {
            log.info(msg + " <SUCCESS>");
        } else {
            log.info(msg + " <" + level + ":" + msgToDisplay + ">");
        }
    }

    private void createUpdatePointInfo(Pao pao, OverridePointInfo override, Action action) {
        String newName = override.getName();
        boolean pointInfoExists = Stream.of(pao.getPointInfos())
                                    .filter(Objects::nonNull)
                                    .map(p -> p.getPointInfo())
                                    .flatMap(List::stream)
                                    .anyMatch(p -> p.getName().equals(newName));
        
        if (pointInfoExists && action == Action.ADD) {
            logOverride(action, pao.getPaoType(), newName, "pointInfo", "pointInfo already exists.", Level.WARN);
            return;
        }

        PointInfoType pointInfo = createPointInfo(pao, override);
        if (pointInfo != null) {
            if (pointInfoExists) {
                removeExistingPointInfo(pao, override, action);
            }
            removeMappedAttribute(pao, pointInfo, action); 
            if(pao.getPointInfos() == null){
                pao.setPointInfos(new PointInfosType()); 
            }
            pao.getPointInfos().getPointInfo().add(pointInfo);
            logOverride(Action.ADD, pao.getPaoType(), newName, "pointInfo", null, null);
        } else {
            logOverride(Action.ADD, pao.getPaoType(), newName, "pointInfo",
                "New pointInfo is invalid. Check that the point exist in the point file or in override file and the point is enabled.",
                Level.ERROR);
        }
    }
    
    /**
     * Looks if an attribute is on other pointInfo and attempts to remove it.
     * Specific attribute can be mapped to exactly one point for the same device.
     */
    private void removeMappedAttribute(Pao pao, PointInfoType newPointInfo, Action action) {
        if (newPointInfo.getAttributes() == null) {
            return;
        }
        List<String> newAttributes = new ArrayList<>(Arrays.asList(newPointInfo.getAttributes().split(",")));
        pao.getPointInfos().getPointInfo().forEach(pointInfo -> {
            if (pointInfo.getAttributes() != null) {
                List<String> attributes = new ArrayList<>(Arrays.asList(pointInfo.getAttributes().split(",")));
                // common attributes
                List<String> common = attributes.stream().filter(newAttributes::contains).collect(Collectors.toList());
                if (!common.isEmpty()) {
                    // remove all common attributes from the existing attributes list
                    attributes.removeAll(common);
                    log.info("^^^^^^" + action.name() + log(pao, newPointInfo));
                    log.info("^^^^^^Found another point with attribute:" + common + log(pao, pointInfo));
                    // override the list of existing attributes
                    pointInfo.setAttributes(attributes.isEmpty() ? null : Joiner.on(",").join(attributes));
                    log.info("^^^^^^Removed attribute:" + common + " from" + log(pao, pointInfo));
                }
            }
        });
    }
    
    /**
     * logs pao type, point name and attribute.
     */
    private String log(Pao pao, PointInfoType pointInfo) {
        return " paoType:" + pao.getPaoType() + " point:(" + pointInfo.getName() + ") attributes:"
            + pointInfo.getAttributes();
    }

    private PointInfoType createPointInfo(Pao pao, OverridePointInfo overridePointInfo) {
        PointInfoType newPointInfo = new PointInfoType();
        newPointInfo.setName(overridePointInfo.getName());
        newPointInfo.setAttributes(overridePointInfo.getAttributes());
        newPointInfo.setInit(overridePointInfo.isInit());
        addNewPoint(pao.getPaoType(), overridePointInfo.getPoint());
        try {
            //checks if the point that matches pointInfo name exists and it is enabled.
            if (validatePointInfo(pao, newPointInfo.getName())) {
                return newPointInfo;
            }
        } catch (PaoConfigurationException e) {
            //ignore
        }
        return null;
    }
    
    private void addNewPoint(String paoType, Point overridePoint){ 
        if (overridePoint != null) {
            List<Point> paoPoints = paoTypeToPoints.computeIfAbsent(paoType, s -> new ArrayList<>());
            if (paoPoints.removeIf(p -> p.getName().equals(overridePoint.getName()))) {
                logOverride(Action.REMOVE, paoType, overridePoint.getName(), "POINT", null, null);
            }
            logOverride(Action.ADD, paoType, overridePoint.getName(), "POINT", null, null);
            paoPoints.add(overridePoint);
        }
    }

    private void overrideTags(Pao pao, List<OverrideTag> overrideTags) {
        overrideTags.forEach(overrideTag -> {
            switch (Action.valueOf(overrideTag.getAction().toUpperCase())) {
            case REMOVE:
                removeExistingTag(pao, overrideTag, Action.REMOVE);
                break;
            case ADD:
                createUpdateTag(pao, overrideTag, Action.ADD);
                break;
            case UPDATE:
                createUpdateTag(pao, overrideTag, Action.UPDATE);
                break;
            }
        });
    }
    
    private void createUpdateTag(Pao pao, OverrideTag overrideTag, Action action) {
        String newTagName = overrideTag.getName();
         boolean tagExists = Stream.of(pao.getTags())
                                   .filter(Objects::nonNull)
                                   .map(t -> t.getTag())
                                   .flatMap(List::stream)
                                   .anyMatch(t -> t.getName().equals(newTagName));
        if (tagExists && action == Action.ADD) {
            logOverride(action, pao.getPaoType(), newTagName, "TAG", "Tag already exists.", Level.WARN);
            return;
        }
        
        TagType newTag = createNewTag(overrideTag, pao.getPaoType());
        if (newTag != null) {
            if (tagExists) {
                removeExistingTag(pao, overrideTag, action);
            }
            if(pao.getTags() == null){
                pao.setTags(new TagsType());
            }
            pao.getTags().getTag().add(newTag);
            logOverride(Action.ADD, pao.getPaoType(), newTagName, "TAG", null, null);
        } else {
            logOverride(Action.ADD, pao.getPaoType(), newTagName, "TAG", "New tag is invalid.", Level.ERROR);
        }
    }
    
    private void removeExistingTag(Pao pao, OverrideTag overrideTag, Action action) {
        String tagToRemove = overrideTag.getName();
        boolean isRemoved = Optional.ofNullable(pao.getTags()).map(TagsType::getTag)
                                                              .map(t -> t.removeIf(i -> i.getName().equals(tagToRemove)))
                                                              .orElse(false);
        if (isRemoved) {
            logOverride(Action.REMOVE, pao.getPaoType(), tagToRemove, "TAG", null, null);
        }else if (action == Action.REMOVE && !isRemoved) {
            logOverride(Action.REMOVE, pao.getPaoType(), tagToRemove, "TAG", "Tag doesn't exists.", Level.WARN);
        }
    }
        
    private TagType createNewTag(OverrideTag overrideTag, String paoType){
        TagType newTag = new TagType();
        newTag.setName(overrideTag.getName());
        newTag.setOption(overrideTag.getOption());
        newTag.setValue(overrideTag.getValue());
        try{
            validateTag(newTag, paoType);
            return newTag;
        }catch(PaoConfigurationException e){
            log.error("Error creating new tag", e);
            return null;
        }
    }
        
    private void overrideConfigurations(Pao pao, List<OverrideCategory> categories) {
        for (OverrideCategory category : categories) {
            try {
                CategoryType overrideCategyType = CategoryType.fromValue(category.getType());
                switch (Action.valueOf(category.getAction().toUpperCase())) {
                case REMOVE:
                    removeExistingConfigurationCategory(pao, overrideCategyType, Action.REMOVE);
                    break;
                case ADD:
                    createUpdateConfigurationCategory(pao, overrideCategyType, Action.ADD);
                    break;
                case UPDATE:
                    createUpdateConfigurationCategory(pao, overrideCategyType, Action.UPDATE);
                    break;
                }
            } catch (Exception e) {
                log.error("Error overriding configuration", e);
            }
        }
    }
       
    private void createUpdateConfigurationCategory(Pao pao, CategoryType overrideCategyType, Action action) {
        boolean configurationExists = Stream.of(pao.getConfiguration())
                                            .filter(Objects::nonNull)
                                            .map(c -> c.getCategory())
                                            .flatMap(List::stream)
                                            .anyMatch(t -> t.getType() == overrideCategyType);
        if (!configurationExists) {
            Category category = new Category();
            category.setType(overrideCategyType);
            if(pao.getConfiguration() == null){
                pao.setConfiguration(new DeviceCategories());
            }
            pao.getConfiguration().getCategory().add(category);
            logOverride(Action.ADD, pao.getPaoType(), overrideCategyType.name(), "Configuration Category", null, null);
        } else {
            logOverride(Action.ADD, pao.getPaoType(), overrideCategyType.name(), "Configuration Category",
                "Configuration Category already exists.", Level.WARN);
        }
    }
    
    private void removeExistingConfigurationCategory(Pao pao, CategoryType typeToRemove, Action action) {         
        boolean isRemoved = Optional.ofNullable(pao.getConfiguration()).map(DeviceCategories::getCategory)
                                                                       .map(t -> t.removeIf(i -> i.getType() == typeToRemove))
                                                                       .orElse(false);
        if (isRemoved) {
            logOverride(Action.REMOVE, pao.getPaoType(), typeToRemove.name(), "Configuration Category", null, null);
        } else if (!isRemoved && action == Action.REMOVE) {
            logOverride(Action.REMOVE, pao.getPaoType(), typeToRemove.name(), "Configuration Category", "Configuration Category doesn't exist.", Level.WARN);
        }
    }

    private Overrides loadOverrideFile(Resource deviceDefinitionXsd) {
        Overrides overrides = null;
        boolean isValidSchema = false;
        if (ClientSession.isRemoteSession()) {
            RemoteLoginSession remoteSession = ClientSession.getRemoteLoginSession();
            try (InputStream inputStream =
                remoteSession.getInputStreamForUrl("/common/config/deviceDefinition", true)) {
                validateXmlSchema(inputStream, deviceDefinitionXsd.getURL());
                isValidSchema = true;
            } catch (IOException | SAXException | ParserConfigurationException e) {
                log.error("Unable to validate schema (remote session) for " + OVERRIDE_FILE_LOCATION, e);
            }
            if (isValidSchema) {
                try (InputStream inputStream =
                    remoteSession.getInputStreamForUrl("/common/config/deviceDefinition", true)) {
                    log.info("Parsing (remote session) " + OVERRIDE_FILE_LOCATION);
                    JAXBContext jaxbContext = JAXBContext.newInstance(Overrides.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    overrides = (Overrides) unmarshaller.unmarshal(inputStream);
                } catch (IOException | JAXBException e) {
                    log.error("Unable to parse (remote session) " + OVERRIDE_FILE_LOCATION, e);
                }
            }
        } else {
            try (InputStream inputStream = new FileInputStream(overrideFile)) {
                validateXmlSchema(inputStream, deviceDefinitionXsd.getURL());
                isValidSchema = true;
            } catch (IOException | SAXException | ParserConfigurationException e) {
                log.error("Unable to validate schema for " + OVERRIDE_FILE_LOCATION, e);
            }
            if (isValidSchema) {
                try (InputStream inputStream = new FileInputStream(overrideFile)) {
                    log.info("Parsing " + OVERRIDE_FILE_LOCATION);
                    JAXBContext jaxbContext = JAXBContext.newInstance(Overrides.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    overrides = (Overrides) unmarshaller.unmarshal(inputStream);
                } catch (IOException | JAXBException e) {
                    log.error("Unable to parse " + OVERRIDE_FILE_LOCATION, e);
                }
            }
        }
        return overrides;
    }

    /**
     * Returns enabled jaxb paos
     */
    public List<Pao> getPaos() {
        return paos;
    }

    /**
     * Returns all enabled jaxb points for PaoType
     */
    public Map<String, Point> getPoints(String paoType) {
        return Optional.ofNullable(paoTypeToPoints.get(paoType))
                             .orElse(Collections.emptyList()).stream()
                             .filter(Point::isEnabled)
                             .collect(Collectors.toMap(Point::getName, Function.identity()));
    }
    
    /**
     * Clears memory
     */
    public void cleanUp(){
        paos.clear();
        paoTypeToPoints.clear();
        allPaoTypes.clear();
    }
}