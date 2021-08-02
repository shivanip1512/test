package com.cannontech.web.api.i18n;

public class I18nKeyValue {
    
    private String nameKey;
    private String[] args;
    private String i18nValue;
    
    public String getNameKey() {
        return nameKey;
    }
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }
    public String[] getArgs() {
        return args;
    }
    public void setArgs(String[] args) {
        this.args = args;
    }
    public String getI18nValue() {
        return i18nValue;
    }
    public void setI18nValue(String i18nValue) {
        this.i18nValue = i18nValue;
    }

}
