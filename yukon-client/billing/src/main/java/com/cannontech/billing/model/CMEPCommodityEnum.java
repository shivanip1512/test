package com.cannontech.billing.model;

public enum CMEPCommodityEnum{
    ELECTRIC("E"),
    WATER("W"),
    GAS("G"),
    STEAM("S"),
    ;
    
    private String billingRepresentation;
    
    private CMEPCommodityEnum(String billingRepresentation){
        this.billingRepresentation = billingRepresentation;
    }
    
    public static CMEPCommodityEnum findByDeviceIconType(String deviceIconType) {
        if (deviceIconType.equals("electric_meter") || deviceIconType.equals("plc_electric_meter")) {
            return ELECTRIC;
        } else if (deviceIconType.equals("water_meter")) {
            return WATER;
        } else if (deviceIconType.equals("gas_meter")) {
            return GAS;
        } else if (deviceIconType.equals("steam_meter")) {
            return STEAM;
        }

        return null;
    }
    
    public String getBillingRepresentation() {
        return this.billingRepresentation;
    };
}