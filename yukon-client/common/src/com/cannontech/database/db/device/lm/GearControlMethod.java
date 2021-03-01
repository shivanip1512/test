package com.cannontech.database.db.device.lm;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.data.device.lm.EatonCloudCycleGear;
import com.cannontech.database.data.device.lm.EcobeeCycleGear;
import com.cannontech.database.data.device.lm.EcobeeSetpointGear;
import com.cannontech.database.data.device.lm.HoneywellCycleGear;
import com.cannontech.database.data.device.lm.HoneywellSetpointGear;
import com.cannontech.database.data.device.lm.ItronCycleGear;
import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.data.device.lm.MagnitudeCycleGear;
import com.cannontech.database.data.device.lm.MasterCycleGear;
import com.cannontech.database.data.device.lm.MeterDisconnectGear;
import com.cannontech.database.data.device.lm.NestCriticalCycleGear;
import com.cannontech.database.data.device.lm.NestStandardCycleGear;
import com.cannontech.database.data.device.lm.NoControlGear;
import com.cannontech.database.data.device.lm.RotationGear;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.data.device.lm.SepTemperatureOffsetGear;
import com.cannontech.database.data.device.lm.SimpleThermostatRampingGear;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.data.device.lm.TargetCycleGear;
import com.cannontech.database.data.device.lm.ThermostatSetbackGear;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.data.device.lm.TrueCycleGear;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public enum GearControlMethod implements DatabaseRepresentationSource, DisplayableEnum {
    TimeRefresh(TimeRefreshGear.class, "Time Refresh", PaoType.LM_DIRECT_PROGRAM),
    SmartCycle(SmartCycleGear.class, "Smart Cycle", PaoType.LM_DIRECT_PROGRAM),
    SepCycle(SepCycleGear.class, "SEP Cycle", PaoType.LM_SEP_PROGRAM),
    EcobeeCycle(EcobeeCycleGear.class, "ecobee Cycle", PaoType.LM_ECOBEE_PROGRAM),
    EcobeeSetpoint(EcobeeSetpointGear.class, "ecobee Setpoint", PaoType.LM_ECOBEE_PROGRAM),
    HoneywellCycle(HoneywellCycleGear.class, "Honeywell Cycle", PaoType.LM_HONEYWELL_PROGRAM),
    HoneywellSetpoint(HoneywellSetpointGear.class, "Honeywell Setpoint", PaoType.LM_HONEYWELL_PROGRAM),
    ItronCycle(ItronCycleGear.class, "Itron Cycle", PaoType.LM_ITRON_PROGRAM),
    NestCriticalCycle(NestCriticalCycleGear.class, "Nest Critical Cycle", PaoType.LM_NEST_PROGRAM),
    NestStandardCycle(NestStandardCycleGear.class, "Nest Standard Cycle", PaoType.LM_NEST_PROGRAM),
    SepTemperatureOffset(SepTemperatureOffsetGear.class, "SEP Temperature Offset", PaoType.LM_SEP_PROGRAM),
    MasterCycle(MasterCycleGear.class, "Master Cycle", PaoType.LM_DIRECT_PROGRAM),
    Rotation(RotationGear.class, "Rotation", PaoType.LM_DIRECT_PROGRAM),
    Latching(LatchingGear.class, "Latching", PaoType.LM_DIRECT_PROGRAM),
    TrueCycle(TrueCycleGear.class, "True Cycle", PaoType.LM_DIRECT_PROGRAM),
    MagnitudeCycle(MagnitudeCycleGear.class, "Magnitude Cycle", PaoType.LM_DIRECT_PROGRAM),
    TargetCycle(TargetCycleGear.class, "Target Cycle", PaoType.LM_DIRECT_PROGRAM),
    ThermostatRamping(ThermostatSetbackGear.class, "Thermostat Ramping", PaoType.LM_DIRECT_PROGRAM),
    SimpleThermostatRamping(SimpleThermostatRampingGear.class, "Simple Thermostat Ramping", PaoType.LM_DIRECT_PROGRAM),
    BeatThePeak(BeatThePeakGear.class, "Beat The Peak", PaoType.LM_DIRECT_PROGRAM),
    MeterDisconnect(MeterDisconnectGear.class, "Meter Disconnect", PaoType.LM_METER_DISCONNECT_PROGRAM),
    EatonCloudCycle(EatonCloudCycleGear.class, "Eaton Cloud Cycle", PaoType.LM_EATON_CLOUD_PROGRAM),
    NoControl(NoControlGear.class, "No Control", PaoType.LM_EATON_CLOUD_PROGRAM, PaoType.LM_DIRECT_PROGRAM, PaoType.LM_SEP_PROGRAM);

    private static final Logger log = YukonLogManager.getLogger(GearControlMethod.class);
    static ImmutableSetMultimap<PaoType, GearControlMethod> gearTypesByProgramType;
    static {
        try {
            ImmutableSetMultimap.Builder<PaoType, GearControlMethod> builder = ImmutableSetMultimap.builder();
            for (GearControlMethod controlMethod : values()) {
                for (PaoType type : controlMethod.programTypes) {
                    builder.put(type, controlMethod);
                }

            }
            gearTypesByProgramType = builder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building Gear type Map for Program Types", e);
            throw e;
        }
    }
    
    private Class<?> gearClass;
    private String displayName;
    private ImmutableSet<PaoType> programTypes;
    private String baseKey = "yukon.web.modules.dr.gearControlMethod.";

    private GearControlMethod(Class<?> gear, String displayName, PaoType... programTypes) {
        gearClass = gear;
        this.displayName = displayName;
        this.programTypes = ImmutableSet.copyOf(programTypes);
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GearControlMethod getGearControlMethod(String value) {
        try {
            return GearControlMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            return NoControl;
        }
    }

    public boolean isRamping() {
        return this == SimpleThermostatRamping || this == ThermostatRamping;
    }

    public LMProgramDirectGear createNewGear() {
        try {
            return (LMProgramDirectGear) gearClass.getConstructor().newInstance();
        } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException
                    |NoSuchMethodException|SecurityException e) {
            
            throw new IllegalStateException("An error occurred creating new gear.", e);
        }
    }

    /** Use the actual enum to write to the db, not the display string. **/
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

    public ImmutableSet<PaoType> getProgramTypes() {
        return programTypes;
    }

    public static ImmutableSet<GearControlMethod> getGearTypesByProgramType(PaoType programType) {
        return gearTypesByProgramType.get(programType);
    }
    
    public static ImmutableSet<GearControlMethod> getGearTypesByProgramType(Set<PaoType> programTypes) {
        ImmutableSet.Builder<GearControlMethod> gearTypes = ImmutableSet.builder(); 
        PaoType.getDirectLMProgramTypes().forEach(paoType -> {
            gearTypes.addAll(getGearTypesByProgramType(paoType));
            
        });
        return gearTypes.build();
    }
}