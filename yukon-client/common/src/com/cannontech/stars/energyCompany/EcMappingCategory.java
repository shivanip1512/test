package com.cannontech.stars.energyCompany;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum EcMappingCategory implements DatabaseRepresentationSource {
    APPLIANCE_CATEGORY("ApplianceCategory"),
    MEMBER("Member"),
    MEMBER_LOGIN("MemberLogin"),
    SERVICE_COMPANY("ServiceCompany"),
    THERMOSTAT_SCHEDULE("LMThermostatSchedule");

    private String dbString;

    private EcMappingCategory(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }
}
