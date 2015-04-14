package com.cannontech.database.data.lite;


public class LiteConfig extends LiteBase {
    private String configName;
    private Integer configType;

    public LiteConfig() {
        super();
        setLiteType(LiteTypes.CONFIG);
    }

    public LiteConfig(int configID) {
        super();
        setLiteID(configID);
        setLiteType(LiteTypes.CONFIG);
    }

    public LiteConfig(int configID, String conName_) {
        this(configID);
        setConfigName(conName_);
    }

    public LiteConfig(int configID, String conName_, Integer type) {
        this(configID);
        setConfigName(conName_);
        setConfigType(type);
    }

    public int getConfigID() {
        return getLiteID();
    }

    public String getConfigName() {
        return configName;
    }

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigID(int conID) {
        setLiteID(conID);
    }

    public void setConfigName(String name) {
        configName = name;
    }

    public void setConfigType(Integer type) {
        configType = type;
    }

    @Override
    public String toString() {
        return getConfigName();
    }
}