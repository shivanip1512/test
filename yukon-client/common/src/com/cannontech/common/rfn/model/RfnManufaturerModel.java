package com.cannontech.common.rfn.model;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public enum RfnManufaturerModel {
    
    RFN_LCR_6200(PaoType.LCR6200_RFN, "CPS", "RFN-LCR6200"),
    RFN_LCR_6600(PaoType.LCR6600_RFN, "CPS", "RFN-LCR6600"),
    
    RFN_430A3R(PaoType.RFN430A3, "EE", "A3R"),
    RFN_430A3D(PaoType.RFN430A3, "EE", "A3D"),
    RFN_430A3T(PaoType.RFN430A3, "EE", "A3T"),

    RFN_WATER_SENSOR(PaoType.RFWMETER, "Eka", "water_sensor"),
    RFN_WATER_NODE(PaoType.RFWMETER, "Eka", "water_node"),
    
    RFN_430KV(PaoType.RFN430KV, "GE", "kV2"),
    
    RFN_420FL(PaoType.RFN420FL, "LGYR", "FocuskWh"),
    RFN_420FX(PaoType.RFN420FX, "LGYR", "FocusAXD"),
    RFN_420FD(PaoType.RFN420FD, "LGYR", "FocusAXR-SD"),
    
    RFN_420CL(PaoType.RFN420CL, "ITRN", "C2SX"),
    RFN_420CD(PaoType.RFN420CD, "ITRN", "C2SX-SD"),
    
    RFN_420ELO(PaoType.RFN420ELO, "ELO", "2131T"),
    RFN_430ELO(PaoType.RFN430ELO, "ELO", "2131xT");
    
    private PaoType type;
    private String manufacturer;
    private String model;
    
    private RfnManufaturerModel(PaoType type, String manufacturer, String model) {
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
    }
    
    public static List<RfnManufaturerModel> getForType(PaoType type) {
        List<RfnManufaturerModel> list = Lists.newArrayList();
        for (RfnManufaturerModel item : values()) {
            if (item.type == type) list.add(item);
        }
        
        if (list.isEmpty()) throw new IllegalArgumentException("Unknown template for type: " + type);
        
        return list;
    }
    
    public PaoType getType() {
        return type;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public String getModel() {
        return model;
    }
    
}