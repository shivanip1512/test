package com.cannontech.common.pao.definition.model;

public enum PaoTypeIcon {
    
    ELECTRIC_METER("icon-electric-meter"),
    WATER_METER("icon-water-meter"),
    GAS_METER("icon-gas-meter"),
    TRANSMITTER("icon-transmitter"),
    RFN_RELAY("icon-rfn-relay"),
    ;
    
    private String icon;
    private PaoTypeIcon(String icon) {
        this.icon = icon;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public PaoTypeIcon getForName(String name) {
        for (PaoTypeIcon icon : values()) {
            if (name.equalsIgnoreCase(icon.getIcon())) return icon;
        }
        throw new IllegalArgumentException("No value found for name: " + name);
    }
    
}