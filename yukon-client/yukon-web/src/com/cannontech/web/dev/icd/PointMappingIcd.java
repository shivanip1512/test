package com.cannontech.web.dev.icd;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointMappingIcd {

    @JsonProperty("Meter models")
    public Map<String, List<Model>> meterModels;
    
    @JsonProperty("Meter combinations")
    public List<List<Model>> meterCombinations;
    
    @JsonProperty("Units of measure")
    public Map<Units, String> unitsOfMeasure;
    
    @JsonProperty("Modifiers")
    public Map<String, List<Modifiers>> modifiers;
    
    @JsonProperty("Centron C1SX and C2SX with Advanced Metrology")
    public List<Named<ModelPointDefinition>> centronWithAdvancedMetrology;

    @JsonProperty("Centron C2SX prior to Advanced Metrology")
    public List<Named<PointDefinition>> centronPriorToAdvancedMetrology;

    @JsonProperty("RFN-420 Node in L & G Focus AX with Advanced Metrology")
    public List<Named<PointDefinition>> focusAxWithAdvancedMetrology;
    
    @JsonProperty("RFN-420 Node in L & G Focus AX Prior to Advanced Metrology")
    public List<Named<PointDefinition>> focusAxPriorToAdvancedMetrology;

    @JsonProperty("RFN-500 and RFN-420 Node in L & G Focus kWh with Advanced Metrology")
    public List<Named<PointDefinition>> focusKwhWithAdvancedMetrology;

    @JsonProperty("RFN-420 Node in L & G Focus kWh Prior to Advanced Metrology")
    public List<Named<PointDefinition>> focusKwhPriorToAdvancedMetrology;

    @JsonProperty("Elster A3")
    public List<Named<ElsterA3PointDefinition>> elsterA3;

    @JsonProperty("ELO")
    public List<Named<PointDefinition>> elo;

    @JsonProperty("Itron Sentinel")
    public List<Named<SentinelPointDefinition>> itronSentinel;

    @JsonProperty("RFN-500 Node in Landis & Gyr S4e and S4x")
    public List<Named<ModelPointDefinition>> rfn500LgyrS4;

    @JsonProperty("RFN-500 Node in L & G Focus AX and RX (with Advanced Metrology)")
    public List<Named<ModelPointDefinition>> rfn500LgyrFocusAx;

    @JsonProperty("Next Gen Water Node")
    public List<Named<WaterNodePointDefinition>> nextGenWaterNode;

    @JsonProperty("Metric IDs")
    public Map<Integer, MetricDefinition> metricIds;
}