package com.cannontech.web.stars.dr.operator;

public enum AccountImportFields {
    
    ACCOUNT_NO("a"),
    CUST_ACTION("b"),
    LAST_NAME(null),
    FIRST_NAME(null),
    HOME_PHONE(null),
    WORK_PHONE(null),
    EMAIL(null),
    STREET_ADDR1(null),
    STREET_ADDR2(null),
    CITY(null),
    STATE(null),
    COUNTY(null),
    ZIP_CODE(null),
    MAP_NO(null),
    SUBSTATION(null),
    FEEDER(null),
    POLE(null),
    TRFM_SIZE(null),
    SERV_VOLT(null),
    USERNAME(null),
    PASSWORD(null),
    USER_GROUP("c"),
    COMPANY_NAME(null),
    IVR_PIN(null),
    IVR_USERNAME(null);
    
    private String legendKey;
    
    private AccountImportFields(String legendKey) {
        this.legendKey = legendKey;
    }

    public String getLegendKey() {
        return legendKey;
    }

    public String getName() {
        return this.name();
    }
}