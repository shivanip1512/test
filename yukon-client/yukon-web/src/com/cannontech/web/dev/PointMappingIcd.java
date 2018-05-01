package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class PointMappingIcd {

    public static class Model {
    
        @JsonCreator
        public Model(String manufacturerModel) {
            
            this.original = manufacturerModel;

            // Split the manufacturer+model string on the first space.
            int firstSpace = manufacturerModel.indexOf(' ');
            
            if (firstSpace <= 0) {
                throw new IllegalArgumentException("Argument must have manufacturer and model, separated by a space");
            }
            
            String manufacturer = manufacturerModel.substring(0, firstSpace);
            String model = manufacturerModel.substring(firstSpace + 1);

            RfnIdentifier rfnId = new RfnIdentifier(null, manufacturer, model);

            mm = Optional.ofNullable(RfnManufacturerModel.of(rfnId))
                    .orElseThrow(() -> new IllegalArgumentException("Unknown manufacturer/model combination: " + manufacturerModel));
        }
        
        public String original;
        public RfnManufacturerModel mm;
    }
    
    public enum Units {
        WATT_HOURS          ("Wh",                  UnitOfMeasure.KWH,   0.001),
        VAR_HOURS           ("Varh",                UnitOfMeasure.KVARH, 0.001),
        Q_HOURS             ("Qh"),
        VOLT_AMP_HOURS      ("VAh",                 UnitOfMeasure.KVAH,  0.001),
        SECONDS             ("s"),
        VOLTS               ("V",                   UnitOfMeasure.VOLTS),
        AMPS                ("A",                   UnitOfMeasure.AMPS),
        POWER_FACTOR        ("PF",                  UnitOfMeasure.PF),
        VOLTAGE_ANGLE       ("V degree",            UnitOfMeasure.DEGREES, 0.1),
        CURRENT_ANGLE       ("A degree",            UnitOfMeasure.DEGREES, 0.1),
        POWER_FACTOR_ANGLE  ("PF degree",           UnitOfMeasure.DEGREES, 0.1),
        GALLONS             ("gal",                 UnitOfMeasure.GALLONS),
        CUBIC_FEET          ("ft^3",                UnitOfMeasure.CUBIC_FEET),
        CUBIC_METERS        ("m^3",                 UnitOfMeasure.CUBIC_METERS),
        NONE                ("none"),
        WATTS               ("W",                   UnitOfMeasure.KW,   0.001),
        VARS                ("Var",                 UnitOfMeasure.KVAR, 0.001),
        Q                   ("Q",                   UnitOfMeasure.KQ,   0.001),
        VOLT_AMPS           ("VA",                  UnitOfMeasure.KVA,  0.001),
        DEGREES_CELSIUS     ("°C",                  UnitOfMeasure.TEMP_C),
        UNKNOWN             ("-"),
        OUTAGE_COUNT        ("Outage Count",        UnitOfMeasure.COUNTS),
        RESTORE_COUNT       ("Restore Count",       UnitOfMeasure.COUNTS),
        OUTAGE_BLINK_COUNT  ("Outage Blink Count",  UnitOfMeasure.COUNTS),
        RESTORE_BLINK_COUNT ("Restore Blink Count", UnitOfMeasure.COUNTS),
        DEMAND_RESET        ("Demand Reset");  //  seemingly only in rfnPointMapping.xml 
        
        private static final Map<String, Units> nameLookup;
        
        static {
            nameLookup = Stream.of(values())
                .collect(StreamUtils.mapToSelf(e -> e.commonName));
        }

        String commonName;
        UnitOfMeasure yukonUom;
        double multiplier;

        Units(String commonName) {
            this(commonName, UnitOfMeasure.UNDEF);
        }

        Units(String commonName, UnitOfMeasure uom) {
            this(commonName, uom, 1.0);
        }

        Units(String commonName, UnitOfMeasure uom, double multiplier) {
            this.commonName = commonName;
            this.yukonUom = uom;
            this.multiplier = multiplier;
        }
        
        @JsonCreator
        public static Units getByCommonName(String commonName) {
            return Optional.ofNullable(nameLookup.get(commonName))
                    .orElseThrow(() -> new RuntimeException("Unknown unit name " + commonName));
        }
    }
    
    public enum Modifiers {
        PRIMARY              ("Primary"),
        SECONDARY            ("Secondary"),

        PHASE_A              ("Phase A"),
        PHASE_B              ("Phase B"),
        PHASE_C              ("Phase C"),

        QUADRANT_1           ("Quadrant 1"),
        QUADRANT_2           ("Quadrant 2"),
        QUADRANT_3           ("Quadrant 3"),
        QUADRANT_4           ("Quadrant 4"),

        FUNDAMENTAL          ("Fundamental"),
        HARMONIC             ("Harmonic"),

        LEADING              ("Leading"),
        LAGGING              ("Lagging"),

        NET_FLOW             ("Net Flow"),
        MINIMUM              ("Min"),
        AVERAGE              ("Avg"),
        MAXIMUM              ("Max"),
        PREVIOUS             ("Previous"),
        DAILY_MAXIMUM        ("Daily Max"),
        CUMULATIVE           ("Cumulative"),
        CONTINUOUS_CUMULATIVE("Continuous Cumulative"),

        A_TO_B               ("A to B"),
        B_TO_C               ("B to C"),
        C_TO_A               ("C to A"),
        NEUTRAL_TO_GROUND    ("Phase Neutral->Ground"),
        A_TO_NEUTRAL         ("A to Neutral"),
        B_TO_NEUTRAL         ("B to Neutral"),
        C_TO_NEUTRAL         ("C to Neutral"),

        TOU_RATE_A           ("TOU Rate A"),
        TOU_RATE_B           ("TOU Rate B"),
        TOU_RATE_C           ("TOU Rate C"),
        TOU_RATE_D           ("TOU Rate D"),
        TOU_RATE_E           ("TOU Rate E"),
        TOU_RATE_F           ("TOU Rate F"),
        TOU_RATE_G           ("TOU Rate G"),

        GIGA                 ("Giga"),
        MEGA                 ("Mega"),
        KILO                 ("Kilo"),
        MILLI                ("milli"),
        MICRO                ("Micro"),
        DECI                 ("Tenths"),
        OVERFLOW             ("Overflow"),

        COINCIDENT_VALUE_1   ("Coincident Value 1"),
        COINCIDENT_VALUE_2   ("Coincident Value 2"),
        COINCIDENT_VALUE_3   ("Coincident Value 3"),
        COINCIDENT_VALUE_4   ("Coincident Value 4"),
        COINCIDENT_VALUE_5   ("Coincident Value 5"),
        COINCIDENT_VALUE_6   ("Coincident Value 6"),
        COINCIDENT_VALUE_7   ("Coincident Value 7");
        
        private static final Map<String, Modifiers> nameLookup;
        
        static {
            nameLookup = Stream.of(values())
                .collect(StreamUtils.mapToSelf(e -> e.commonName));
        }
        
        String commonName;
        
        Modifiers(String commonName) {
            this.commonName = commonName;
        }
        
        @JsonCreator
        public static Modifiers getByCommonName(String commonName) {
            return Optional.ofNullable(nameLookup.get(commonName))
                    .orElseThrow(() ->
                        new RuntimeException("Unknown modifier name " + commonName));
        }
    }

    @JsonProperty("Meter models")
    public Map<String, List<Model>> meterModels;
    
    @JsonProperty("Units of measure")
    public Map<Units, String> unitsOfMeasure;
    
    public static class PointDefinition {
        public Units unit;
        @JsonProperty("Unit Modifiers")
        public List<Modifiers> modifiers = new ArrayList<>();
        @Override
        public String toString() {
            String unitString = Optional.ofNullable(unit)
                    .map(Object::toString)
                    .orElse("NULL_UOM");
            if (modifiers != null && !modifiers.isEmpty()) {
                return unitString + modifiers;
            }
            return unitString;
        }
        public int compareTo(PointDefinition other) {
            if (unit == null) {
                return other.unit == null ? 0 : -1;
            }
            if (other == null || other.unit == null) {
                return 1;
            }
            return unit.name().compareTo(other.unit.name());
        }
    }
    
    public static class ModelPointDefinition extends PointDefinition {
        @JsonProperty("Models")
        public List<Model> models;
    }
    
    public static class ElsterA3PointDefinition extends ModelPointDefinition {
                
        @JsonProperty("Other names")
        public List<String> otherNames;
    }
    
    public static class SentinelPointDefinition extends ModelPointDefinition {
        
        @JsonProperty("Itron name")
        public String itronName;
    }
    
    public static class WaterNodePointDefinition extends PointDefinition {
        
        @JsonProperty("Metric ID")
        public Integer metricId;
    }
    
    public static class MetricDefinition extends PointDefinition {
        public String name;
    }
    
    public static class Named <T extends PointDefinition> {
        @JsonAnySetter
        void set(String name, T value) {
            this.name = name;
            this.value = value;
        }
        
        public Named() {
            this.name = "none";
            this.value = null;
        }
        
        public String name;
        public T value;
    }
    
    @JsonProperty("Centron C1SX and C2SX with Advanced Metrology")
    List<Named<ModelPointDefinition>> centronWithAdvancedMetrology;

    @JsonProperty("Centron C2SX prior to Advanced Metrology")
    List<Named<PointDefinition>> centronPriorToAdvancedMetrology;

    @JsonProperty("RFN-420 Node in L & G Focus AX with Advanced Metrology")
    List<Named<PointDefinition>> focusAxWithAdvancedMetrology;
    
    @JsonProperty("RFN-420 Node in L & G Focus AX Prior to Advanced Metrology")
    List<Named<PointDefinition>> focusAxPriorToAdvancedMetrology;

    @JsonProperty("RFN-500 and RFN-420 Node in L & G Focus kWh with Advanced Metrology")
    List<Named<PointDefinition>> focusKwhWithAdvancedMetrology;

    @JsonProperty("RFN-420 Node in L & G Focus kWh Prior to Advanced Metrology")
    List<Named<PointDefinition>> focusKwhPriorToAdvancedMetrology;

    @JsonProperty("Elster A3")
    List<Named<ElsterA3PointDefinition>> elsterA3;

    @JsonProperty("ELO")
    List<Named<PointDefinition>> elo;

    @JsonProperty("Itron Sentinel")
    List<Named<SentinelPointDefinition>> itronSentinel;

    @JsonProperty("RFN-500 Node in Landis & Gyr S4e and S4x")
    List<Named<ModelPointDefinition>> rfn500LgyrS4;

    @JsonProperty("RFN-500 Node in L & G Focus AX and RX (with Advanced Metrology)")
    List<Named<ModelPointDefinition>> rfn500LgyrFocusAx;

    @JsonProperty("Next Gen Water Node")
    List<Named<WaterNodePointDefinition>> nextGenWaterNode;

    @JsonProperty("Metric IDs")
    Map<Integer, MetricDefinition> metricIds;
}