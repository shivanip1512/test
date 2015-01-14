package com.cannontech.stars.energyCompany;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum EcMappingCategory implements DatabaseRepresentationSource {
    APPLIANCE_CATEGORY("ApplianceCategory"),
    MEMBER_LOGIN("MemberLogin"),
    SERVICE_COMPANY("ServiceCompany"),
    ;

    private String dbString;

    private EcMappingCategory(String dbString) {
        this.dbString = dbString;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return dbString;
    }
}
