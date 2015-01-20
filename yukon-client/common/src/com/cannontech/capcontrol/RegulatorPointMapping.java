package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RegulatorPointMapping implements DisplayableEnum {
    
    AUTO_REMOTE_CONTROL("Auto/Remote Control", PointType.Status, PointType.CalcStatus),
    AUTO_BLOCK_ENABLE("Auto Block Enable", PointType.Status, PointType.CalcStatus),
    TAP_UP("Raise Tap Position", PointType.Status, PointType.CalcStatus),
    TAP_DOWN("Lower Tap Position", PointType.Status, PointType.CalcStatus),
    TAP_POSITION("Tap Position", PointType.Analog, PointType.CalcAnalog),
    TERMINATE("Terminate", PointType.Status, PointType.CalcStatus),
    VOLTAGE_X("Voltage X", PointType.Analog, PointType.CalcAnalog),
    VOLTAGE_Y("Voltage Y", PointType.Analog, PointType.CalcAnalog),
    KEEP_ALIVE("Keep Alive", PointType.Analog, PointType.CalcAnalog),
    KEEP_ALIVE_TIMER("Keep Alive Timer", PointType.Analog, PointType.CalcAnalog);
    
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
        b.add(VOLTAGE_Y);
        ltcRegulatorMappings = b.build();
        
        b.add(AUTO_BLOCK_ENABLE);
        b.add(TERMINATE);
        b.add(VOLTAGE_X);
        b.add(KEEP_ALIVE_TIMER);
        phaseAndGangRegulatorMappings = b.build();
        
        regulatorPointMappings = ImmutableMap.of(PaoType.LOAD_TAP_CHANGER, ltcRegulatorMappings, 
                                                                  PaoType.PHASE_OPERATED, phaseAndGangRegulatorMappings, 
                                                                  PaoType.GANG_OPERATED, phaseAndGangRegulatorMappings);
    }
    
    private PointType[] pointTypes;
    private String description;
    
    private RegulatorPointMapping(String description, PointType... pointTypes) {
        this.pointTypes = pointTypes;
        this.description = description;
    }
    
    public PointType[] getPointTypes() {
        return pointTypes;
    }
    
    public PointType getFilterPointType() {
        return pointTypes[0];
    }
    
    /**
     * Still needed until we can i18n (or delete!) JSF
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String getFormatKey() {
        // capcontrol/root.xml
        return "yukon.web.modules.capcontrol.regulatorPointMapping." + name();
    }
    
    public static ImmutableSet<RegulatorPointMapping> getPointMappingsForPaoType(PaoType paoType) {
        return regulatorPointMappings.get(paoType);
    }
    
}