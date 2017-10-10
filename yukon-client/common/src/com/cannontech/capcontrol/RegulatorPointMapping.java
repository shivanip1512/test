package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RegulatorPointMapping implements DisplayableEnum {
    
    AUTO_REMOTE_CONTROL("Auto Remote Control", PointType.Status, PointType.CalcStatus),
    AUTO_BLOCK_ENABLE("Auto Block Enable", PointType.Status, PointType.CalcStatus),
    TAP_UP("Tap Up", PointType.Status, PointType.CalcStatus),
    TAP_DOWN("Tap Down", PointType.Status, PointType.CalcStatus),
    TAP_POSITION("Tap Position", PointType.Analog, PointType.CalcAnalog),
    TERMINATE("Terminate", PointType.Status, PointType.CalcStatus),
    SOURCE_VOLTAGE("Source Voltage", PointType.Analog, PointType.CalcAnalog),
    VOLTAGE("Voltage", PointType.Analog, PointType.CalcAnalog),
    KEEP_ALIVE("Keep Alive", PointType.Analog, PointType.CalcAnalog),
    FORWARD_SET_POINT("Forward Set Point", PointType.Analog, PointType.CalcAnalog),
    FORWARD_BANDWIDTH("Forward Bandwidth", PointType.Analog, PointType.CalcAnalog),
    REVERSE_BANDWIDTH("Reverse Bandwidth", PointType.Analog, PointType.CalcAnalog),
    REVERSE_SET_POINT("Reverse Set Point", PointType.Analog, PointType.CalcAnalog),
    REVERSE_FLOW_INDICATOR("Reverse Flow Indicator", PointType.Status, PointType.CalcStatus),
    CONTROL_MODE("Control Mode", PointType.Analog, PointType.CalcAnalog),
    ;
    
    private final static ImmutableSet<RegulatorPointMapping> phaseAndGangRegulatorMappings;
    private final static ImmutableSet<RegulatorPointMapping> ltcRegulatorMappings;
    private final static ImmutableMap<PaoType, ImmutableSet<RegulatorPointMapping>> regulatorPointMappings;
    
    static {
        Builder<RegulatorPointMapping> b = ImmutableSet.builder();
        b.add(AUTO_REMOTE_CONTROL);
        b.add(KEEP_ALIVE);
        b.add(TAP_DOWN);
        b.add(TAP_UP);
        b.add(TAP_POSITION);
        b.add(VOLTAGE);
        b.add(FORWARD_SET_POINT);
        b.add(FORWARD_BANDWIDTH);
        b.add(REVERSE_BANDWIDTH);
        b.add(REVERSE_SET_POINT);
        b.add(REVERSE_FLOW_INDICATOR);
        b.add(CONTROL_MODE);
        ltcRegulatorMappings = b.build();
        
        b.add(AUTO_BLOCK_ENABLE);
        b.add(TERMINATE);
        b.add(SOURCE_VOLTAGE);
        phaseAndGangRegulatorMappings = b.build();
        
        regulatorPointMappings = ImmutableMap.of(PaoType.LOAD_TAP_CHANGER, ltcRegulatorMappings,
                                                 PaoType.PHASE_OPERATED, phaseAndGangRegulatorMappings,
                                                 PaoType.GANG_OPERATED, phaseAndGangRegulatorMappings);
    }
    
    private PointType[] pointTypes;
    private String mappingString;
    
    private RegulatorPointMapping(String mappingString, PointType... pointTypes) {
        this.mappingString = mappingString;
        this.pointTypes = pointTypes;
    }
    
    public PointType[] getPointTypes() {
        return pointTypes;
    }
    
    public String getMappingString() {
        return mappingString;
    }
    
    public FilterType getFilterType() {
        return FilterType.getForPointType(pointTypes[0]);
    }

    @Override
    public String getFormatKey() {
        // capcontrol/root.xml
        return "yukon.web.modules.capcontrol.regulatorPointMapping." + name();
    }
    
    public static ImmutableSet<RegulatorPointMapping> getPointMappingsForPaoType(PaoType paoType) {
        return regulatorPointMappings.get(paoType);
    }
    
    public static ImmutableMap<PaoType, ImmutableSet<RegulatorPointMapping>> getMappingsByPaoType() {
        return regulatorPointMappings;
    }
    
    public static RegulatorPointMapping getForMappingString(String mappingString) {
        for (RegulatorPointMapping mapping : values()) {
            if (mapping.mappingString.equals(mappingString)) {
                return mapping;
            }
        }
        throw new IllegalArgumentException("Invalid regulator point mapping string.");
    }
}
