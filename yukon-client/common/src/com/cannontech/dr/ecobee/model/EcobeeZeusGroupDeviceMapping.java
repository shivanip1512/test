package com.cannontech.dr.ecobee.model;

import java.util.List;

public class EcobeeZeusGroupDeviceMapping {
    private String groupId;
    private String parentGroupId;
    private List<String> thermostatsSerialNumber;

    public EcobeeZeusGroupDeviceMapping(String groupId, List<String> thermostatsSerialNumber) {
        super();
        this.groupId = groupId;
        this.thermostatsSerialNumber = thermostatsSerialNumber;
    }

    public EcobeeZeusGroupDeviceMapping() {

    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getThermostatsSerialNumber() {
        return thermostatsSerialNumber;
    }

    public void setThermostatsSerialNumber(List<String> thermostatsSerialNumber) {
        this.thermostatsSerialNumber = thermostatsSerialNumber;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

}
