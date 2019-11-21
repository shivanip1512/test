package com.cannontech.rest.api.gear.fields;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MockProgramGear {

    private Integer gearId;
    private String gearName;
    private Integer gearNumber;
    private MockGearControlMethod controlMethod;
    
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "controlMethod") 
    @JsonSubTypes({
        @JsonSubTypes.Type(value = MockBeatThePeakGearFields.class, name = "BeatThePeak"),
        @JsonSubTypes.Type(value = MockEcobeeCycleGearFields.class, name = "EcobeeCycle"),
        @JsonSubTypes.Type(value = MockSetpointGearFields.class, name = "EcobeeSetpoint"),
        @JsonSubTypes.Type(value = MockHoneywellCycleGearFields.class, name = "HoneywellCycle"),
        @JsonSubTypes.Type(value = MockItronCycleGearFields.class, name = "ItronCycle"),
        @JsonSubTypes.Type(value = MockLatchingGearFields.class, name = "Latching"),
        @JsonSubTypes.Type(value = MockMasterCycleGearFields.class, name = "MasterCycle"),
        @JsonSubTypes.Type(value = MockNestCriticalCycleGearFields.class, name = "NestCriticalCycle"),
        @JsonSubTypes.Type(value = MockNestStandardCycleGearFields.class, name = "NestStandardCycle"),
        @JsonSubTypes.Type(value = MockNoControlGearFields.class, name = "NoControl"),
        @JsonSubTypes.Type(value = MockRotationGearFields.class, name = "Rotation"),
        @JsonSubTypes.Type(value = MockSepCycleGearFields.class, name = "SepCycle"),
        @JsonSubTypes.Type(value = MockSepTemperatureOffsetGearFields.class, name = "SepTemperatureOffset"),
        @JsonSubTypes.Type(value = MockSimpleThermostatRampingGearFields.class, name = "SimpleThermostatRamping"),
        @JsonSubTypes.Type(value = MockThermostatSetbackGearFields.class, name = "ThermostatRamping"),
        @JsonSubTypes.Type(value = MockTimeRefreshGearFields.class, name = "TimeRefresh"),
        @JsonSubTypes.Type(value = MockSmartCycleGearFields.class, name = "SmartCycle"),
        @JsonSubTypes.Type(value = MockTrueCycleGearFields.class, name = "TrueCycle"),
        @JsonSubTypes.Type(value = MockMagnitudeCycleGearFields.class, name = "MagnitudeCycle"),
        @JsonSubTypes.Type(value = MockTargetCycleGearFields.class, name = "TargetCycle"),
        @JsonSubTypes.Type(value = MockMeterDisconnectGearFields.class, name = "MeterDisconnect")})
    
    private MockProgramGearFields fields;

}
