package com.cannontech.yukon.api.amr.endpoint.helper;

import java.util.Arrays;

import com.cannontech.common.pao.definition.model.PaoData;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum ResponseDescriptor {
    // PAO-related fields
    METER_NUMBER("meterNumber", PaoData.OptionalField.METER_NUMBER),
    CARRIER_ADDRESS("carrierAddress", PaoData.OptionalField.CARRIER_ADDRESS),
    NAME("name", PaoData.OptionalField.NAME),
    ENABLED("enabled", PaoData.OptionalField.ENABLED),
    PAO_ID("paoId"),
    PAO_TYPE("paoType"),

    // Point-related fields
    POINT_TYPE("pointType"),
    UNIT_OF_MEASURE("uofm"),
    POINT_NAME("pointName"),

    // Point value-related fields
    QUALITY("quality"),
    STATUS_TEXT("statusText"),

    LAST_VALUE_ID("lastValueId"),
    ;

    private final static ImmutableMap<String, ResponseDescriptor> byElementName;
    static {
        Function<ResponseDescriptor, String> keyFunction = new Function<ResponseDescriptor, String>() {
            @Override
            public String apply(ResponseDescriptor from) {
                return from.elementName;
            }
        };
        byElementName = Maps.uniqueIndex(Arrays.asList(values()), keyFunction);
    }

    private final String elementName;
    private final PaoData.OptionalField paoDataField;

    private ResponseDescriptor(String elementName) {
        this(elementName, null);
    }

    private ResponseDescriptor(String elementName, PaoData.OptionalField paoDataField) {
        this.elementName = elementName;
        this.paoDataField = paoDataField;
    }

    public PaoData.OptionalField getPaoDataField() {
        return paoDataField;
    }

    @Override
    public String toString() {
        return elementName;
    }

    public static ResponseDescriptor getByElementName(String elementName) {
        return byElementName.get(elementName);
    }
}
