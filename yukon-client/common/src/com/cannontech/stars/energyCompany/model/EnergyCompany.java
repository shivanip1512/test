package com.cannontech.stars.energyCompany.model;

import com.cannontech.database.data.lite.LiteYukonUser;


public final class EnergyCompany implements YukonEnergyCompany {

    private final int ecId;
    private final String name;
    private final LiteYukonUser user;
    private final int contactId;
    
    public EnergyCompany(int ecId, String name, LiteYukonUser user, int contactId) {
        this.ecId = ecId;
        this.name = name;
        this.user = user;
        this.contactId = contactId;
    }
    
    @Override
    public int getEnergyCompanyId() {
        return ecId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LiteYukonUser getEnergyCompanyUser() {
        return user;
    }

    public int getContactId() {
        return contactId;
    }
}
