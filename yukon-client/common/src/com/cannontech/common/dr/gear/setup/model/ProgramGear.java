package com.cannontech.common.dr.gear.setup.model;

import com.cannontech.common.dr.gear.setup.fields.BeatThePeakGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.LatchingGearFields;
import com.cannontech.common.dr.gear.setup.fields.MagnitudeCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.MasterCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NestCriticalCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NestStandardCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NoControlGearFields;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.fields.RotationGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepTemperatureOffsetGearFields;
import com.cannontech.common.dr.gear.setup.fields.SimpleOnOffGearFields;
import com.cannontech.common.dr.gear.setup.fields.SimpleThermostatRampingGearFields;
import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.TargetCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ThermostatSetbackGearFields;
import com.cannontech.common.dr.gear.setup.fields.TimeRefreshGearFields;
import com.cannontech.common.dr.gear.setup.fields.TrueCycleGearFields;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value={ "gearId"}, allowGetters= true, ignoreUnknown = true)
public class ProgramGear {

    private Integer gearId;
    private String gearName;
    private Integer gearNumber;
    private GearControlMethod controlMethod;
    
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "controlMethod") 
    @JsonSubTypes({
        @JsonSubTypes.Type(value = BeatThePeakGearFields.class, name = "BeatThePeak"),
        @JsonSubTypes.Type(value = EcobeeCycleGearFields.class, name = "EcobeeCycle"),
        @JsonSubTypes.Type(value = HoneywellCycleGearFields.class, name = "HoneywellCycle"),
        @JsonSubTypes.Type(value = ItronCycleGearFields.class, name = "ItronCycle"),
        @JsonSubTypes.Type(value = LatchingGearFields.class, name = "Latching"),
        @JsonSubTypes.Type(value = MasterCycleGearFields.class, name = "MasterCycle"),
        @JsonSubTypes.Type(value = NestCriticalCycleGearFields.class, name = "NestCriticalCycle"),
        @JsonSubTypes.Type(value = NestStandardCycleGearFields.class, name = "NestStandardCycle"),
        @JsonSubTypes.Type(value = NoControlGearFields.class, name = "NoControl"),
        @JsonSubTypes.Type(value = RotationGearFields.class, name = "Rotation"),
        @JsonSubTypes.Type(value = SepCycleGearFields.class, name = "SepCycle"),
        @JsonSubTypes.Type(value = SepTemperatureOffsetGearFields.class, name = "SepTemperatureOffset"),
        @JsonSubTypes.Type(value = SimpleThermostatRampingGearFields.class, name = "SimpleThermostatRamping"),
        @JsonSubTypes.Type(value = ThermostatSetbackGearFields.class, name = "ThermostatRamping"),
        @JsonSubTypes.Type(value = TimeRefreshGearFields.class, name = "TimeRefresh"),
        @JsonSubTypes.Type(value = SmartCycleGearFields.class, name = "SmartCycle"),
        @JsonSubTypes.Type(value = TrueCycleGearFields.class, name = "TrueCycle"),
        @JsonSubTypes.Type(value = MagnitudeCycleGearFields.class, name = "MagnitudeCycle"),
        @JsonSubTypes.Type(value = TargetCycleGearFields.class, name = "TargetCycle"),
        @JsonSubTypes.Type(value = SimpleOnOffGearFields.class, name = "SimpleOnOff")})
    
    private ProgramGearFields fields;

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    public Integer getGearId() {
        return gearId;
    }

    public void setGearId(Integer gearId) {
        this.gearId = gearId;
    }

    public Integer getGearNumber() {
        return gearNumber;
    }

    public void setGearNumber(Integer gearNumber) {
        this.gearNumber = gearNumber;
    }

    public GearControlMethod getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(GearControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }

   

    public ProgramGearFields getFields() {
        return fields;
    }

    public void setFields(ProgramGearFields fields) {
        this.fields = fields;
    }

    public LMProgramDirectGear buildDBPersistent() {

        LMProgramDirectGear programDirectGear = controlMethod.createNewGear();

        programDirectGear.setGearID(getGearId());
        programDirectGear.setGearName(getGearName());
        programDirectGear.setControlMethod(controlMethod);
        if (fields != null) {
            fields.buildDBPersistent(programDirectGear);
        }

        return programDirectGear;

    }

    public void buildModel(LMProgramDirectGear directGear) {

        GearControlMethod controlMethod = directGear.getControlMethod();
        setControlMethod(controlMethod);
        setGearId(directGear.getGearID());
        setGearName(directGear.getGearName());
        setGearNumber(directGear.getGearNumber());
        fields = LMModelFactory.createProgramGearFields(controlMethod);
        if (fields != null) {
            fields.buildModel(directGear);
        }

    }

}
