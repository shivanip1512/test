package com.eaton.builders.drsetup.gears;

import java.util.Random;

public class GearEnums {

    public enum GearType {
        TimeRefresh("TimeRefresh"),
        SmartCycle("SmartCycle"),
        SepCycle("SepCycle"),
        EcobeeCycle("EcobeeCycle"),
        EcobeeSetpoint("EcobeeSetpoint"),
        HoneywellCycle("HoneywellCycle"),
        HoneywellSetpoint("HoneywellSetpoint"),
        ItronCycle("ItronCycle"),
        NestCriticalCycle("NestCriticalCycle"),
        NestStandardCycle("NestStandardCycle"),
        SepTemperatureOffset("SepTemperatureOffset"),
        MasterCycle("MasterCycle"),
        Rotation("Rotation"),
        Latching("Latching"),
        TrueCycle("TrueCycle"),
        MagnitudeCycle("MagnitudeCycle"),
        TargetCycle("TargetCycle"),
        ThermostatRamping("ThermostatRamping"),
        SimpleThermostatRamping("SimpleThermostatRamping"),
        BeatThePeak("BeatThePeak"),
        MeterDisconnect("MeterDisconnect"),
        NoControl("NoControl");

        private final String gearType;

        GearType(String gearType) {
            this.gearType = gearType;
        }

        public String getGearType() {
            return this.gearType;
        }

        public static GearType getRandomGearType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum HowToStopControl {
        Restore("Restore"),
        TimeIn("TimeIn"),
        StopCycle("StopCycle"),
        RampOutTimeIn("RampOutTimeIn"),
        RampOutRestore("RampOutRestore");

        private final String howToStopControl;

        HowToStopControl(String howToStopControl) {
            this.howToStopControl = howToStopControl;
        }

        public String getHowToStopControl() {
            return this.howToStopControl;
        }

        public static HowToStopControl getRandomHowToStopControl() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum WhenToChange {
        ManualOnly("None"),
        AfterADuration("Duration"),
        PriorityChange("Priority"),
        AboveTrigger("TriggerOffset");

        private final String whenToChange;

        WhenToChange(String whenToChange) {
            this.whenToChange = whenToChange;
        }

        public String getWhenToChange() {
            return this.whenToChange;
        }

        public static WhenToChange getRandomWhenToChange() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum Mode {
        COOL("COOL"),
        HEAT("HEAT");

        private final String mode;

        Mode(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return this.mode;
        }

        public static Mode getRandomMode() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum DutyCycleType {
    	StandardCycle("STANDARD"),
    	TrueCycle("TRUE_CYCLE"),
    	SmartCycle("SMART_CYCLE");

        private final String dutyCycleType;

        DutyCycleType(String dutyCycleType) {
            this.dutyCycleType = dutyCycleType;
        }

        public String getDutyCycleType() {
            return this.dutyCycleType;
        }

        public static DutyCycleType getRandomDutyCycleType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum RefreshShedType {
    	FixedShedTime("FixedShedTime"),
    	DynamicShedTime("DynamicShedTime");

        private final String refreshShedType;

    	RefreshShedType(String refreshShedType) {
            this.refreshShedType = refreshShedType;
        }

        public String getRefreshShedType() {
            return this.refreshShedType;
        }

        public static RefreshShedType getRandomRefreshShedType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum GroupSelectionMethod {
    	LastControlled("LastControlled"),
    	AlwaysFirstGroup("AlwaysFirstGroup");

        private final String groupSelectionMethod;

        GroupSelectionMethod(String groupSelectionMethod) {
            this.groupSelectionMethod = groupSelectionMethod;
        }

        public String getGroupSelectionMethod() {
            return this.groupSelectionMethod;
        }

        public static GroupSelectionMethod getRandomGroupSelectionMethod() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }    

}
