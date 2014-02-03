package com.cannontech.amr.device.search.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * For usage of the queryField values,
 * @see com.cannontech.amr.device.search.dao.impl.DeviceSearchRowMapper
 * @author macourtois
 */
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

    private final String fieldName;
    private final String queryField;
    private final boolean visible;

    private final static Map<String, DeviceSearchField> byDbColumnName;
    static {
        Builder<String, DeviceSearchField> builder = ImmutableMap.builder();
        for (DeviceSearchField deviceSearchField : values()) {
            builder.put(deviceSearchField.fieldName.toLowerCase(), deviceSearchField);
        }
        byDbColumnName = builder.build();
    }

    private DeviceSearchField(String fieldName, String queryField, boolean visible) {
        this.fieldName = fieldName;
        this.queryField = queryField;
        this.visible = visible;
    }

    public static DeviceSearchField getByDatabaseColumnName(String dbColumnName) {
        return byDbColumnName.get(dbColumnName.toLowerCase());
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.commander.select.deviceSearchField." + name();
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
