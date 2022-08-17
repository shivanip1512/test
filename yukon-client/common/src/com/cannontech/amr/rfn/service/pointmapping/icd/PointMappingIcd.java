package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointMappingIcd {

    @JsonProperty("Meter models")
    public Map<MeterClass, List<ManufacturerModel>> meterModels;
    
    @JsonProperty("Meter combinations")
    public List<List<ManufacturerModel>> meterCombinations;
    
    @JsonProperty("Units of measure")
    public Map<Units, String> unitsOfMeasure;
    
    @JsonProperty("Modifiers")
    public Map<String, List<Modifiers>> modifiers;
    
    @JsonProperty("Centron C1SX and C2SX with Advanced Metrology")
    public List<Named<ModelPointDefinition>> itronCentronWithAdvancedMetrology;

    @JsonProperty("Centron C2SX prior to Advanced Metrology")
    public List<Named<PointDefinition>> itronCentronC2sxPriorToAdvancedMetrology;

    @JsonProperty("RFN-420 Node in L & G Focus AX with Advanced Metrology")
    public List<Named<PointDefinition>> lgyrFocusAxRfn420WithAdvancedMetrology;
    
    @JsonProperty("RFN-420 Node in L & G Focus AX Prior to Advanced Metrology")
    public List<Named<PointDefinition>> lgyrFocusAxRfn420PriorToAdvancedMetrology;

    @JsonProperty("RFN-500 and RFN-420 Node in L & G Focus kWh with Advanced Metrology")
    public List<Named<PointDefinition>> lgyrFocusKwhWithAdvancedMetrology;

    @JsonProperty("RFN-420 Node in L & G Focus kWh Prior to Advanced Metrology")
    public List<Named<PointDefinition>> lgyrFocusKwhPriorToAdvancedMetrology;

    @JsonProperty("Elster A3")
    public List<Named<ElsterA3PointDefinition>> elsterA3;

    @JsonProperty("ELO")
    public List<Named<ModelPointDefinition>> elo;

    @JsonProperty("Itron Sentinel")
    public List<Named<SentinelPointDefinition>> itronSentinel;

    @JsonProperty("RFN-500 Node in Landis & Gyr S4e and S4x")
    public List<Named<ModelPointDefinition>> lgyrS4_rfn500;

    @JsonProperty("RFN-500 Node in L & G Focus AX and RX (with Advanced Metrology)")
    public List<Named<ModelPointDefinition>> lgyrFocusAxRx_rfn500;

    @JsonProperty("Next Gen Water Node")
    public List<Named<WaterNodePointDefinition>> nextGenWaterNode;

    @JsonProperty("Metric IDs")
    public Map<Integer, MetricDefinition> metricIds;
}