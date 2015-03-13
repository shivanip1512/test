package com.cannontech.capcontrol;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.FilterType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public enum RegulatorPointMapping implements DisplayableEnum {
    
    AUTO_REMOTE_CONTROL(PointType.Status, PointType.CalcStatus),
    AUTO_BLOCK_ENABLE(PointType.Status, PointType.CalcStatus),
    TAP_UP(PointType.Status, PointType.CalcStatus),
    TAP_DOWN(PointType.Status, PointType.CalcStatus),
    TAP_POSITION(PointType.Analog, PointType.CalcAnalog),
    TERMINATE(PointType.Status, PointType.CalcStatus),
    VOLTAGE_X(PointType.Analog, PointType.CalcAnalog),
    VOLTAGE_Y(PointType.Analog, PointType.CalcAnalog),
    KEEP_ALIVE(PointType.Analog, PointType.CalcAnalog),
    KEEP_ALIVE_TIMER(PointType.Analog, PointType.CalcAnalog),
    FORWARD_SET_POINT(PointType.Analog, PointType.CalcAnalog),
    FORWARD_BANDWIDTH(PointType.Analog, PointType.CalcAnalog),
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
        b.add(VOLTAGE_Y);
        b.add(FORWARD_SET_POINT);
        b.add(FORWARD_BANDWIDTH);
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
    
    private RegulatorPointMapping(PointType... pointTypes) {
        this.pointTypes = pointTypes;
    }
    
    public PointType[] getPointTypes() {
        return pointTypes;
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

}
