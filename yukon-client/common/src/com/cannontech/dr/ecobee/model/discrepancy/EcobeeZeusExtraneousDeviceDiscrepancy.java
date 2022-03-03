package com.cannontech.dr.ecobee.model.discrepancy;

import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;

/**
 * A discrepancy that indicates an ecobee device is extraneous.
 */
public class EcobeeZeusExtraneousDeviceDiscrepancy extends EcobeeZeusDiscrepancy {
    private final String serialNumber;
    private final String group;

    public EcobeeZeusExtraneousDeviceDiscrepancy(String serialNumber, String group) {
        super(EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE);
        this.serialNumber = serialNumber;
        this.group = group;
    }

    public EcobeeZeusExtraneousDeviceDiscrepancy(int errorId, String serialNumber, String group) {
        super(errorId, EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE);
        this.serialNumber = serialNumber;
        this.group = group;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    public String getGroup() {
        return group;
    }

}
