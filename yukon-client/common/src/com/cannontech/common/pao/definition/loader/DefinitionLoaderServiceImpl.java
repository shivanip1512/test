package com.cannontech.common.pao.definition.loader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.loader.jaxb.CmdType;
import com.cannontech.common.pao.definition.loader.jaxb.CommandType;
import com.cannontech.common.pao.definition.loader.jaxb.ComponentTypeType;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories.Category;
import com.cannontech.common.pao.definition.loader.jaxb.Pao;
import com.cannontech.common.pao.definition.loader.jaxb.Point;
import com.cannontech.common.pao.definition.loader.jaxb.Point.Archive;
import com.cannontech.common.pao.definition.loader.jaxb.Point.Calculation;
import com.cannontech.common.pao.definition.loader.jaxb.Point.Calculation.Components.Component;
import com.cannontech.common.pao.definition.loader.jaxb.PointInfoType;
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

    @Value("classpath:pao/definition/pao.xsd") private Resource paoXsd;
    @Value("classpath:pao/definition/points.xsd") private Resource pointsXsd;
    @Value("classpath:pao/definition/override.xsd") private Resource overrideXsd;
    @Autowired private ResourceLoader loader;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private PointDao pointDao;
    private FileLoader fileLoader;
    
    @PostConstruct
    public void initialize() {
        load();
        override();
    }
    
    @Override
    public void load() {
        fileLoader = new FileLoader(loader, paoXsd, pointsXsd);
    }
    
    @Override
    public void override() {
        fileLoader.override(overrideXsd);
    }

    @Override
    public void cleanUp() {
        fileLoader.cleanUp();
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
            PaoType paoType = PaoType.valueOf(pao.getPaoType());
            if (paoAttributeAttrDefinitionMap.get(paoType) == null) {
                paoAttributeAttrDefinitionMap.put(paoType, new LinkedHashMap<>());
            }
            Map<Attribute, AttributeDefinition> attrDefMap = paoAttributeAttrDefinitionMap.get(paoType);
            if (pao.getPointInfos() != null) {
                Map<String, Point> allPoints = fileLoader.getPoints(pao.getPaoType());
                for (PointInfoType pointInfo : pao.getPointInfos().getPointInfo()) {
                    PointTemplate template = createPointTemplate(pointInfo.getName(), allPoints);
                    if (pointInfo.getAttributes() != null) {
                        List<String> attributes = Arrays.asList(pointInfo.getAttributes().split(","));
                        for (String attributeStr : attributes) {
                            BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr.trim());
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
            if(pao.getPointInfos() != null) {
                Map<String, Point> allPoints =  fileLoader.getPoints(pao.getPaoType());
                PaoType paoType = PaoType.valueOf(pao.getPaoType());
                for (PointInfoType pointInfo : pao.getPointInfos().getPointInfo()) {
                    if(initOnly && !pointInfo.isInit()){
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
        for (Pao pao : fileLoader.getPaos()) {
            PaoType paoType = PaoType.valueOf(pao.getPaoType());
            if (pao.getConfiguration() != null) {
                paoCategoryMap.putAll(paoType, pao.getConfiguration().getCategory());
            }
        }
        
        return paoCategoryMap;
    }

    @Override
    public SetMultimap<PaoType, CommandDefinition> getPaoCommandMap() {
        SetMultimap<PaoType, CommandDefinition> paoCommandMap = HashMultimap.create();
        for (Pao pao : fileLoader.getPaos()) {
            Map<String, Point> allPoints = fileLoader.getPoints(pao.getPaoType());
            if(allPoints != null) {
                PaoType paoType = PaoType.valueOf(pao.getPaoType());
                if(pao.getCommands() != null){
                    for (CommandType command : pao.getCommands().getCommand()) {
                        if(command != null && command.isEnabled()){
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
            PaoType paoType = PaoType.valueOf(pao.getPaoType());
            ImmutableBiMap.Builder<PaoTag, PaoTagDefinition> tags = ImmutableBiMap.builder();
            if (pao.getTags() != null) {
                for (TagType tag : pao.getTags().getTag()) {
                    PaoTagDefinition definition = createTagDefinition(tag);
                    tags.put(definition.getTag(), definition);
                }
            }
            supportedTagsByType.put(paoType, tags.build());
        }
        
        List<PaoType> typesWithoutAPaoFile = new LinkedList<>(Arrays.asList(PaoType.values()));
        typesWithoutAPaoFile.removeAll(supportedTagsByType.keySet());
        //ALPHA_A3, KV, KVII, MCT410GL, LM_CURTAIL_PROGRAM, LM_ENERGY_EXCHANGE_PROGRAM, ROUTE_CCU, ROUTE_TCU, 
        //ROUTE_LCU, ROUTE_MACRO, ROUTE_VERSACOM, ROUTE_SERIES_5_LMI, ROUTE_RTC, LOCAL_DIRECT, LOCAL_SHARED, 
        //LOCAL_RADIO, LOCAL_DIALUP, TSERVER_DIRECT, TCPPORT, UDPPORT, TSERVER_SHARED, TSERVER_RADIO, TSERVER_DIALUP, LOCAL_DIALBACK, DIALOUT_POOL
        // Db editor attempts to get tags for device without a pao file (TSERVER_SHARED).
        typesWithoutAPaoFile.forEach(type -> {
            ImmutableBiMap.Builder<PaoTag, PaoTagDefinition> tags = ImmutableBiMap.builder();
            supportedTagsByType.put(type, tags.build());
        });

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
        PaoType paoType = PaoType.valueOf(pao.getPaoType());
        PaoDefinition paoDefinition = new PaoDefinitionImpl(paoType, pao.getDisplayName(), pao.getDisplayGroup(),
            pao.getChangeGroup(), pao.isCreatable());
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
                    new CalcPointComponent(pointIdentifier, componentType.value(), component.getOperator());
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
