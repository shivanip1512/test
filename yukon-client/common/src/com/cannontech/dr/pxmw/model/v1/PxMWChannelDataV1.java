package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWChannelDataV1 implements Serializable {
    private final String id;
    private final String name;
    private final String value;
    private final String unit;
    private final String iconClass;

    @JsonCreator
    public PxMWChannelDataV1(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("value") String value,
            @JsonProperty("unit") String unit, @JsonProperty("iconClass") String iconClass) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.iconClass = iconClass;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("iconClass")
    public String getIconClass() {
        return iconClass;
    }
}
