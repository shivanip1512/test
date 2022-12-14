package com.cannontech.development.model;

public enum DevCCU {
    CART_711("* CCU 711 CART", 2, DevCommChannel.DIGI_PORT_2),
    SIM_711("* CCU 711 SIM", 2, DevCommChannel.SIM),
    ;

    private final String name;
    private final int address;
    private final DevCommChannel commChannel;
    
    private DevCCU(String name, int address, DevCommChannel commChannel) {
        this.name = name;
        this.address = address;
        this.commChannel = commChannel;
    }

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public DevCommChannel getCommChannel() {
        return commChannel;
    }

}
