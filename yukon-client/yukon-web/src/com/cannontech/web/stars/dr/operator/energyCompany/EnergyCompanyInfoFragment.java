package com.cannontech.web.stars.dr.operator.energyCompany;

import com.cannontech.common.model.Address;

public class EnergyCompanyInfoFragment {
    private int energyCompanyId;
    private String companyName;
    private Address address;
    
    public EnergyCompanyInfoFragment(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}