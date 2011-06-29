package com.cannontech.web.support.development.database.objects;

public enum DevCCU {
    CCU_711_CART_1("* CCU 711 CART", 2, DevCommChannel.COMM_CHANNEL_1),
    CCU_711_SIM("* CCU 711 SIM", 2, DevCommChannel.COMM_CHANNEL_SIM),
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
