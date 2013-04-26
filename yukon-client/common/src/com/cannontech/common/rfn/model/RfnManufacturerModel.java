package com.cannontech.common.rfn.model;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public enum RfnManufacturerModel {
    
    RFN_LCR_6200(PaoType.LCR6200_RFN, "CPS", "RFN-LCR6200"),
    RFN_LCR_6600(PaoType.LCR6600_RFN, "CPS", "RFN-LCR6600"),
    
    RFN_430A3D(PaoType.RFN430A3D, "EE", "A3D"),
    RFN_430A3T(PaoType.RFN430A3T, "EE", "A3T"),
    RFN_430A3K(PaoType.RFN430A3K, "EE", "A3K"),
    RFN_430A3R(PaoType.RFN430A3R, "EE", "A3R"),

    RFN_WATER_SENSOR(PaoType.RFWMETER, "Eka", "water_sensor"),
    RFN_WATER_NODE(PaoType.RFWMETER, "Eka", "water_node"),
    
    RFN_430KV(PaoType.RFN430KV, "GE", "kV2"),
    
    RFN_420FL(PaoType.RFN420FL, "LGYR", "FocuskWh"),
    RFN_420FX(PaoType.RFN420FX, "LGYR", "FocusAXD"),
    RFN_420FD(PaoType.RFN420FD, "LGYR", "FocusAXR-SD"),
    
    RFN_410CL(PaoType.RFN410CL, "ITRN", "C1SX"),
    RFN_420CL(PaoType.RFN420CL, "ITRN", "C2SX"),
    RFN_420CD(PaoType.RFN420CD, "ITRN", "C2SX-SD"),
    
    RFN_420ELO(PaoType.RFN420ELO, "ELO", "2131T"),
    RFN_430ELO(PaoType.RFN430ELO, "ELO", "2131xT"),
    
    RFN_410FL(PaoType.RFN410FL, "LGYR", "FocuskWh"),
    RFN_410FX_D(PaoType.RFN410FX, "LGYR", "FocusAXD"),
    RFN_410FX_R(PaoType.RFN410FX, "LGYR", "FocusAXR"),
    RFN_410FD_D(PaoType.RFN410FD, "LGYR", "FocusAXR-SD"),
    RFN_410FD_R(PaoType.RFN410FD, "LGYR", "FocusAXD-SD");
    
    private PaoType type;
    private String manufacturer;
    private String model;
    
    private RfnManufacturerModel(PaoType type, String manufacturer, String model) {
        this.type = type;
        this.manufacturer = manufacturer;
        this.model = model;
    }
    
    public static List<RfnManufacturerModel> getForType(PaoType type) {
        List<RfnManufacturerModel> list = Lists.newArrayList();
        for (RfnManufacturerModel item : values()) {
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