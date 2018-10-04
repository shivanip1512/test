package com.cannontech.dr.pxwhite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a single data channel. The "tag" field is typically used to address channels. The "writable" field
 * determines whether it is permissable to send data or commands to that channel, as well as read it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceChannel {
    private final String tag;
    private final String name;
    private final String longName;
    private final String shortName;
    private final String abbreviatedName;
    private final PxWhiteValueType valueType;
    private final String category;
    private final String longCategoryName;
    private final String shortCategoryName;
    private final String unit;
    private final String longUnitName;
    private final String shortUnitName;
    private final boolean writable;
    private final String customProperties;
    private final List<PxWhiteEnum> enums;
    
    @JsonCreator
    public PxWhiteDeviceChannel(@JsonProperty("tag") String tag,
                                @JsonProperty("name") String name,
                                @JsonProperty("lname") String longName,
                                @JsonProperty("sname") String shortName,
                                @JsonProperty("aname") String abbreviatedName,
                                @JsonProperty("vtype") PxWhiteValueType valueType,
                                @JsonProperty("category") String category,
                                @JsonProperty("lcategory") String longCategoryName,
                                @JsonProperty("scategory") String shortCategoryName,
                                @JsonProperty("unit") String unit,
                                @JsonProperty("lunit") String longUnitName,
                                @JsonProperty("sunit") String shortUnitName,
                                @JsonProperty("writable") boolean writable,
                                @JsonProperty("customProperties") String customProperties,
                                @JsonProperty("enums") List<PxWhiteEnum> enums) {
        this.tag = tag;
        this.name = name;
        this.longName = longName;
        this.shortName = shortName;
        this.abbreviatedName = abbreviatedName;
        this.valueType = valueType;
        this.category = category;
        this.longCategoryName = longCategoryName;
        this.shortCategoryName = shortCategoryName;
        this.unit = unit;
        this.longUnitName = longUnitName;
        this.shortUnitName = shortUnitName;
        this.writable = writable;
        this.customProperties = customProperties;
        this.enums = enums;
    }
    
    public String getTag() {
        return tag;
    }
    
    public String getName() {
        return name;
    }
    
    @JsonProperty("lname")
    public String getLongName() {
        return longName;
    }
    
    @JsonProperty("sname")
    public String getShortName() {
        return shortName;
    }
    
    @JsonProperty("aname")
    public String getAbbreviatedName() {
        return abbreviatedName;
    }
    
    @JsonProperty("vtype")
    public PxWhiteValueType getValueType() {
        return valueType;
    }
    
    public String category() {
        return category;
    }
    
    @JsonProperty("lcategory")
    public String getLongCategoryName() {
        return longCategoryName;
    }
    
    @JsonProperty("scategory")
    public String getShortCategoryName() {
        return shortCategoryName;
    }
    
    public String getUnit() {
        return unit;
    }
    
    @JsonProperty("lunit")
    public String getLongUnitName() {
        return longUnitName;
    }
    
    @JsonProperty("sunit")
    public String getShortUnitName() {
        return shortUnitName;
    }
    
    @JsonProperty("writable")
    public boolean isWritable() {
        return writable;
    }
    
    public String getCustomProperties() {
        return customProperties;
    }
    
    public List<PxWhiteEnum> getEnums() {
        return enums;
    }
}
