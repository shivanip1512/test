package com.cannontech.web.stars.dr.operator.energyCompany;

import com.cannontech.common.model.Address;
import com.cannontech.database.data.lite.LiteContactNotification;

public class EnergyCompanyInfoFragment {
    private int energyCompanyId;
    private String companyName;
    private Address address;
    private LiteContactNotification phone;
    private LiteContactNotification fax;
    
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

    public LiteContactNotification getPhone() {
        return phone;
    }

    public void setPhone(LiteContactNotification phone) {
        this.phone = phone;
    }

    public LiteContactNotification getFax() {
        return fax;
    }

    public void setFax(LiteContactNotification fax) {
        this.fax = fax;
    }

}