package com.cannontech.rest.api.gear.fields;


import com.cannontech.rest.api.common.model.MockPaoType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public enum MockGearControlMethod {
    TimeRefresh(MockPaoType.LM_DIRECT_PROGRAM),
    SmartCycle(MockPaoType.LM_DIRECT_PROGRAM),
    SepCycle(MockPaoType.LM_SEP_PROGRAM),
    EcobeeCycle(MockPaoType.LM_ECOBEE_PROGRAM),
    HoneywellCycle(MockPaoType.LM_HONEYWELL_PROGRAM),
    ItronCycle(MockPaoType.LM_ITRON_PROGRAM),
    NestCriticalCycle(MockPaoType.LM_NEST_PROGRAM),
    NestStandardCycle(MockPaoType.LM_NEST_PROGRAM),
    SepTemperatureOffset(MockPaoType.LM_SEP_PROGRAM),
    MasterCycle(MockPaoType.LM_DIRECT_PROGRAM),
    Rotation(MockPaoType.LM_DIRECT_PROGRAM),
    Latching(MockPaoType.LM_DIRECT_PROGRAM),
    TrueCycle(MockPaoType.LM_DIRECT_PROGRAM),
    MagnitudeCycle(MockPaoType.LM_DIRECT_PROGRAM),
    TargetCycle(MockPaoType.LM_DIRECT_PROGRAM),
    ThermostatRamping(MockPaoType.LM_DIRECT_PROGRAM),
    SimpleThermostatRamping(MockPaoType.LM_DIRECT_PROGRAM),
    BeatThePeak(MockPaoType.LM_DIRECT_PROGRAM),
    MeterDisconnect(MockPaoType.LM_METER_DISCONNECT_PROGRAM),
    NoControl(MockPaoType.LM_DIRECT_PROGRAM, MockPaoType.LM_SEP_PROGRAM);

    static ImmutableSetMultimap<MockPaoType, MockGearControlMethod> gearTypesByProgramType;
    static {
        
            ImmutableSetMultimap.Builder<MockPaoType, MockGearControlMethod> builder = ImmutableSetMultimap.builder();
            for (MockGearControlMethod controlMethod : values()) {
                for (MockPaoType type : controlMethod.programTypes) {
                    builder.put(type, controlMethod);
                }
            }
            gearTypesByProgramType = builder.build();
 
    }
    
    private ImmutableSet<MockPaoType> programTypes;

    private MockGearControlMethod(MockPaoType... programTypes) {
        this.programTypes = ImmutableSet.copyOf(programTypes);
    }

}