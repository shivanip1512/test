package com.cannontech.amr.device.search.model;

public enum DeviceSearchField implements SearchField {
    ID("id", "ypo.paObjectId", false),
    NAME("name", "ypo.paoname", true),
    TYPE("type", "ypo.type", true),
    CATEGORY("category", "ypo.category", false),
    CLASS("class", "ypo.paoClass", false),
    ADDRESS("address", "dcs.address", true),
    METERNUMBER("meterNumber", "dmg.meterNumber", true),
    ROUTE("route", "rypo.paoname", true),
    COMM_CHANNEL("commChannel", "pypo.paoName", true),
    LMGROUP_ROUTE("lmGroupRoute", "lmg.routeName", true),
    LMGROUP_SERIAL("lmGroupSerial", "lmg.serialNumber", true),
    LMGROUP_CAPACITY("lmGroupCapacity", "lmg.kwCapacity", true),
    CBC_SERIAL("cbcSerialNumber", "cbc.serialNumber", true);

    private String fieldName;
    private String queryField;
    private boolean visible;

    private DeviceSearchField(String fieldName, String queryField, boolean visible) {
        this.fieldName = fieldName;
        this.queryField = queryField;
        this.visible = visible;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.commanderSelect.deviceSearchField." + name();
    }

    @Override
    public String getQueryField() {
        return queryField;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}
