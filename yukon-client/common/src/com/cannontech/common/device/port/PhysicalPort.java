package com.cannontech.common.device.port;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PhysicalPort implements DisplayableEnum {

    COM_1("com1"),
    COM_2("com2"),
    COM_3("com3"),
    COM_4("com4"),
    COM_5("com5"),
    COM_6("com6"),
    COM_7("com7"),
    COM_8("com8"),
    COM_50("com50"),
    COM_99("com99"),
    OTHER("Other");

    private String baseKey = "yukon.web.modules.operator.commChannel.physicalPort.";
    private String physicalPort;

    PhysicalPort(String physicalPort) {
        this.physicalPort = physicalPort;
    }

    public String getPhysicalPort() {
        return physicalPort;
    }

    public static PhysicalPort getByDbString(String dbString) {
        for (PhysicalPort value : PhysicalPort.values()) {
            if (value.getPhysicalPort().equalsIgnoreCase(dbString)) {
                return value;
            }
        }
        return OTHER;
    }

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
