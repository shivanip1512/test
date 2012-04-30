package com.cannontech.enums;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RegulatorPointMapping implements DisplayableEnum {
    
    AUTO_REMOTE_CONTROL(PointType.Status, "Auto/Remote Control"),
    AUTO_BLOCK_ENABLE(PointType.Status, "Auto Block Enable"),
    TAP_UP(PointType.Status, "Raise Tap Position"),
    TAP_DOWN(PointType.Status, "Lower Tap Position"),
    TAP_POSITION(PointType.Analog, "Tap Position"),
    TERMINATE(PointType.Status, "Terminate"),
    VOLTAGE_X(PointType.Analog, "Voltage X"),
    VOLTAGE_Y(PointType.Analog, "Voltage Y"),
    KEEP_ALIVE(PointType.Analog, "Keep Alive"),
    KEEP_ALIVE_TIMER(PointType.Analog, "Keep Alive Timer");
    
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
    
    private PointType pointType;
    private String description;
    
    private RegulatorPointMapping(PointType pointType, String description) {
        this.pointType = pointType;
        this.description = description;
    }
    
    public PointType getPointType() {
        return pointType;
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