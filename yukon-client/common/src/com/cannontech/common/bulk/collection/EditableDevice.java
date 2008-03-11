package com.cannontech.common.bulk.collection;

/**
 * Data transfer object for editing a device. For use with bulk device editing.
 */
public class EditableDevice {

    private int id = 0;
    private String address = null;
    private String name = null;
    private String routeName = null;
    private String meterNumber = null;
    private String collectionGroup = null;
    private String altGroup = null;
    private String templateName = null;
    private String billingGroup = null;
    private String substationName = null;
    private String type = null;

    public EditableDevice() {
    };

    public EditableDevice(int id, String address, String name,
            String routeName, String meterNumber, String collectionGroup,
            String altGroup, String templateName, String billingGroup,
            String substationName, String type) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.routeName = routeName;
        this.meterNumber = meterNumber;
        this.collectionGroup = collectionGroup;
        this.altGroup = altGroup;
        this.templateName = templateName;
        this.billingGroup = billingGroup;
        this.substationName = substationName;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getCollectionGroup() {
        return collectionGroup;
    }

    public void setCollectionGroup(String collectionGroup) {
        this.collectionGroup = collectionGroup;
    }

    public String getAltGroup() {
        return altGroup;
    }

    public void setAltGroup(String altGroup) {
        this.altGroup = altGroup;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getBillingGroup() {
        return billingGroup;
    }

    public void setBillingGroup(String billingGroup) {
        this.billingGroup = billingGroup;
    }

    public String getSubstationName() {
        return substationName;
    }

    public void setSubstationName(String substationName) {
        this.substationName = substationName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
