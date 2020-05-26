package com.cannontech.common.device.port;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum PhysicalPort implements DatabaseRepresentationSource, DisplayableEnum {

    COM_1("com1"),
    COM_2("com2"),
    COM_3("com3"),
    COM_4("com4"),
    COM_5("com5"),
    COM_6("com6"),
    COM_7("com7"),
    COM_8("com8"),
    COM_50("com50"),
    COM_89("com89"),
    COM_90("com90"),
    OTHER("Other");

    private String baseKey = "yukon.web.modules.operator.commChannel.physicalPort.";
    private String physicalPort;

    PhysicalPort(String physicalPort) {
        this.physicalPort = physicalPort;
    }

    public String getPhysicalPort() {
        return physicalPort;
    }

    public static PhysicalPort getByDisplayName(String name) {
        for (PhysicalPort value : PhysicalPort.values()) {
            if (value.getPhysicalPort().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return OTHER;
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

    @Override
    public Object getDatabaseRepresentation() {
        return this.physicalPort;
    }
}
