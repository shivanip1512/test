package com.cannontech.common.pao.definition.loader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.loader.jaxb.CmdType;
import com.cannontech.common.pao.definition.loader.jaxb.CommandType;
import com.cannontech.common.pao.definition.loader.jaxb.ComponentTypeType;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories.Category;
import com.cannontech.common.pao.definition.loader.jaxb.Pao;
import com.cannontech.common.pao.definition.loader.jaxb.PointInfoType;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point.Archive;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point.Calculation;
import com.cannontech.common.pao.definition.loader.jaxb.Points.Point.Calculation.Components.Component;
import com.cannontech.common.pao.definition.loader.jaxb.TagType;
import com.cannontech.common.pao.definition.loader.jaxb.UpdateTypeType;
import com.cannontech.common.pao.definition.model.CalcPointComponent;
import com.cannontech.common.pao.definition.model.CalcPointInfo;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinitionImpl;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

public class DefinitionLoaderServiceImpl implements DefinitionLoaderService{

    private final Logger log = YukonLogManager.getLogger(DefinitionLoaderServiceImpl.class);
    @Value("classpath:pao/definition/pao.xsd") private Resource paoXsd;
    @Value("classpath:pao/definition/points.xsd") private Resource pointsXsd;
    @Autowired private ResourceLoader loader;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private PointDao pointDao;
    private FileLoader fileLoader;
    
    @PostConstruct
    public void initialize() {
        log.info("Loading device defintions.");
        fileLoader = new FileLoader(loader, paoXsd, pointsXsd);
        log.info("Loading device defintions complete.");
    }
    
    @Override
    public void reload(){
        initialize();
    }
    
    @Override
    public BiMap<PaoType, PaoDefinition> getPaoTypeMap() {
        BiMap<PaoType, PaoDefinition> paoTypeMap = EnumHashBiMap.create(PaoType.class);
        fileLoader.getPaos().forEach(pao -> {
            PaoDefinition paoDefinition = createPaoDefinition(pao);
            paoTypeMap.put(paoDefinition.getType(), paoDefinition);
        });
        return paoTypeMap;
    }
    
    @Override
    public Map<PaoType, Map<Attribute, AttributeDefinition>> getPaoAttributeAttrDefinitionMap() {
        Map<PaoType, Map<Attribute, AttributeDefinition>> paoAttributeAttrDefinitionMap = new HashMap<>();
        for (Pao pao : fileLoader.getPaos()) {
            PaoType paoType = PaoType.valueOf(pao.getAaid());
            if (paoAttributeAttrDefinitionMap.get(paoType) == null) {
                paoAttributeAttrDefinitionMap.put(paoType, new HashMap<>());
            }
            Map<Attribute, AttributeDefinition> attrDefMap = paoAttributeAttrDefinitionMap.get(paoType);
            if (pao.getPointFiles() != null) {
                Map<String, Point> allPoints = fileLoader.getPoints(pao.getPointFiles().getPointFile());
                for (PointInfoType pointInfo : pao.getPointInfos().getPointInfo()) {
                    PointTemplate template = createPointTemplate(pointInfo.getName(), allPoints);
                    if (pointInfo.getZzattributes() != null) {
                        List<String> attributes = Arrays.asList(pointInfo.getZzattributes().split(","));
                        for (String attributeStr : attributes) {
                            BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
                            AttributeDefinition definition = new AttributeDefinition(attribute, template, pointDao);
                            attrDefMap.put(attribute, definition);
                        }
                    }
                }
            }
            paoAttributeAttrDefinitionMap.put(paoType, attrDefMap);
        }
        return paoAttributeAttrDefinitionMap;
    }
    
    @Override
    public SetMultimap<PaoType, PointTemplate> getPointTemplateMap(boolean initOnly) {
        SetMultimap<PaoType, PointTemplate> paoAllPointTemplateMap = HashMultimap.create();
        
        for(Pao pao: fileLoader.getPaos()) {
            if(pao.getPointFiles() != null) {
                PaoType paoType = PaoType.valueOf(pao.getAaid());
                Map<String, Point> allPoints =  fileLoader.getPoints(pao.getPointFiles().getPointFile());
                for (PointInfoType pointInfo : pao.getPointInfos().getPointInfo()) {
                    if(initOnly && !pointInfo.isYyinit()){
                        continue;
                    }
                    PointTemplate template = createPointTemplate(pointInfo.getName(), allPoints);
                    paoAllPointTemplateMap.put(paoType, template);
                }
            }
        }
        
        return paoAllPointTemplateMap;
    }
    
    @Override
    public SetMultimap<PaoType, Category> getPaoCategoryMap() {
        SetMultimap<PaoType, Category> paoCategoryMap = HashMultimap.create();
        
        for(Pao pao: fileLoader.getPaos()){
           PaoType paoType = PaoType.valueOf(pao.getAaid());
           if(pao.getConfiguration() != null){
               for(Category category: pao.getConfiguration().getCategory()){
                   paoCategoryMap.put(paoType, category);
               }
           }
        }
        
        return paoCategoryMap;
    }
    
    @Override
    public SetMultimap<PaoType, CommandDefinition> getPaoCommandMap() {
        SetMultimap<PaoType, CommandDefinition> paoCommandMap = HashMultimap.create();
        for (Pao pao : fileLoader.getPaos()) {
            if(pao.getPointFiles() != null) {
                Map<String, Point> allPoints = fileLoader.getPoints(pao.getPointFiles().getPointFile());
                PaoType paoType = PaoType.valueOf(pao.getAaid());
                if(pao.getCommands() != null){
                    for (CommandType command : pao.getCommands().getCommand()) {
                        if(command != null && command.isZzenabled()){
                            CommandDefinition commandDefinition = new CommandDefinition(command.getName());
                            for (CmdType cmd : command.getCmd()) {
                                commandDefinition.addCommandString(cmd.getText());
                            }
                            for (com.cannontech.common.pao.definition.loader.jaxb.PointType pointType : command.getPoint()) {
                                Point point = allPoints.get(pointType.getName());
                                PointIdentifier pointIdentifier = createPointIdentifier(point);
                                commandDefinition.addAffectedPoint(pointIdentifier);
                            }
                            paoCommandMap.put(paoType, commandDefinition);
                        }
                    }
                }
            }
        }
        
        return paoCommandMap;
    }
    
    @Override
    public Map<PaoType, ImmutableBiMap<PaoTag, PaoTagDefinition>> getSupportedTagsByType() {
        Map<PaoType, ImmutableBiMap<PaoTag, PaoTagDefinition>> supportedTagsByType = new HashMap<>();
        for (Pao pao : fileLoader.getPaos()) {
            PaoType paoType = PaoType.valueOf(pao.getAaid());
            ImmutableBiMap.Builder<PaoTag, PaoTagDefinition> tags = ImmutableBiMap.builder();
            if (pao.getTags() != null) {
                for (TagType tag : pao.getTags().getTag()) {
                    PaoTagDefinition definition = createTagDefinition(tag);
                    tags.put(definition.getTag(), definition);
                }
            }
            supportedTagsByType.put(paoType, tags.build());
        }

        return supportedTagsByType;
    }
    
    /**
     * Creates PaoTagDefinition from jaxb object.
     */
    private PaoTagDefinition createTagDefinition(TagType tagType) {
        PaoTag tag = PaoTag.valueOf(tagType.getName());
        String value = tagType.getOption();
        Object convertedValue =
            tag.isTagHasValue() ? InputTypeFactory.convertPropertyValue(tag.getValueType(), value) : null;
            
        return new PaoTagDefinition(tag, convertedValue);
    }

    
    /**
     * Creates PaoDefinition from jaxb object.
     */
    private PaoDefinition createPaoDefinition(Pao pao) {
        PaoType paoType = PaoType.valueOf(pao.getAaid());
        // delete javaConstant?
        PaoDefinition paoDefinition = new PaoDefinitionImpl(paoType, pao.getDisplayName(), pao.getDisplayGroup(),
            paoType.name(), pao.getChangeGroup(), pao.isCreatable());
        
        return paoDefinition;
    }
    
    /**
     * Creates PointTemplate from jaxb objects.
     */
    private PointTemplate createPointTemplate(String pointName,  Map<String, Point> allPoints) {
        Point point = allPoints.get(pointName);
        PointIdentifier pointIdentifier = createPointIdentifier(point);
        PointTemplate template = new PointTemplate(pointIdentifier);
        template.setName(point.getName());
     
        if (point.getMultiplier() != null) {
            template.setMultiplier(point.getMultiplier().getValue().doubleValue());
        }
        if (point.getDataOffset() != null) {
            template.setDataOffset(point.getDataOffset().getValue().doubleValue());
        }
        if (point.getUnitofmeasure() != null) {
            String unitOfMeasure = point.getUnitofmeasure().getValue().name();
            template.setUnitOfMeasure(UnitOfMeasure.valueOf(unitOfMeasure).getId());
        }
        
        if (point.getDecimalplaces() != null) {
            template.setDecimalPlaces(point.getDecimalplaces().getValue());
        }
       
        addStatusInfo(point, template);
        addCalculationInfo(point, template, allPoints);
        
        return template;
    }

    /**
     * Populates PointTemplate with status point info.
     */
    private void addStatusInfo(Point point, PointTemplate template){
        Archive archive = point.getArchive();
        if (archive != null) {
            template.setPointArchiveType(PointArchiveType.valueOf(archive.getType()));
            template.setPointArchiveInterval(PointArchiveInterval.valueOf(archive.getInterval()));
        }
    
        template.setStateGroupId(StateGroupUtils.SYSTEM_STATEGROUPID);
        template.setInitialState(StateGroupUtils.DEFAULT_STATE);
        
        if (point.getStategroup() != null) {
            if (point.getControlOffset() != null) {
                template.setControlOffset(point.getControlOffset().getValue());
            }
            if (point.getControlType() != null) {
                StatusControlType controlType = StatusControlType.valueOf(point.getControlType().getValue().name());
                template.setControlType(controlType);
            }
            if (point.getStateZeroControl() != null) {
                ControlStateType stateZeroControl =
                    ControlStateType.valueOf(point.getStateZeroControl().getValue().value());
                template.setStateZeroControl(stateZeroControl);
            }
            if (point.getStateOneControl() != null) {
                ControlStateType stateOneControl =
                    ControlStateType.valueOf(point.getStateOneControl().getValue().value());
                template.setStateOneControl(stateOneControl);
            }
            if (point.getStategroup() != null) {
                addStateInfo(point.getStategroup().getValue(), point.getStategroup().getInitialState(), template);
            }
        }
        
        if (point.getAnalogstategroup() != null) {
            addStateInfo(point.getAnalogstategroup().getValue(), point.getAnalogstategroup().getInitialState(), template);
        }
    }

    /**
     * Populates PointTemplate with calculation info.
     */
    private void addCalculationInfo(Point point, PointTemplate template, Map<String, Point> allPoints){
        Calculation calculation = point.getCalculation();
        if (calculation != null) {
            int periodicRate = calculation.getPeriodicRate();
            boolean forceQualityNormal = calculation.isForceQualityNormal();
            UpdateTypeType updateType = calculation.getUpdateType();
            CalcPointInfo calcPointInfo = new CalcPointInfo(updateType.value(), periodicRate, forceQualityNormal);

            List<CalcPointComponent> calcPointComponents = Lists.newArrayList();
            for (Component component : calculation.getComponents().getComponent()) {
                ComponentTypeType componentType = component.getComponentType();
                Point componentPoint = allPoints.get(component.getPoint());
                PointIdentifier pointIdentifier = createPointIdentifier(componentPoint);
                CalcPointComponent calcPointComponent =
                    new CalcPointComponent(pointIdentifier, componentType.toString(), component.getOperator());
                calcPointComponents.add(calcPointComponent);
            }

            calcPointInfo.setComponents(calcPointComponents);
            template.setCalcPointInfo(calcPointInfo);
        }
    }
   
    /**
     * Populates PointTemplate with state info.
     */
    private void addStateInfo(String stateGroupName, String initialState, PointTemplate template) {
        LiteStateGroup stateGroup = null;
        try {
            stateGroup = stateGroupDao.getStateGroup(stateGroupName);
            template.setStateGroupId(stateGroup.getStateGroupID());
            if (initialState != null) {
                List<LiteState> states = stateGroup.getStatesList();

                LiteState state = states.stream()
                                        .filter(x -> x.getStateText().equals(initialState))
                                        .findFirst()
                                        .orElse(null);
                if (state != null) {
                    template.setInitialState(state.getStateRawState());
                } else {
                    throw new IllegalArgumentException(
                        "Initial State was not found in the State Group: " + initialState + ". Template=" + template);
                }
            }
        } catch (NotFoundException e) {
            throw new NotFoundException("State group does not exist: " + stateGroupName + ". Template=" + template, e);
        }
    }
    
    /**
     * Creates PointIdentifier from jaxb object.
     */
    private PointIdentifier createPointIdentifier(Point point) {
        PointType type = PointType.getForString(point.getType());
        PointIdentifier pointIdentifier = new PointIdentifier(type, point.getOffset());
        return pointIdentifier;
    }
}
