package com.cannontech.stars.xml.serialize;

public class StarsBriefCustAccountInfo {
    private int _accountID;
    private boolean _has_accountID;
    private int _energyCompanyID;
    private boolean _has_energyCompanyID;

    public StarsBriefCustAccountInfo() {
        
    }

    public void deleteEnergyCompanyID() {
        this._has_energyCompanyID = false;
    } 

    public int getAccountID() {
        return this._accountID;
    }

    public int getEnergyCompanyID() {
        return this._energyCompanyID;
    }

    public boolean hasAccountID() {
        return this._has_accountID;
    }

    public boolean hasEnergyCompanyID() {
        return this._has_energyCompanyID;
    }

    public void setAccountID(int accountID) {
        this._accountID = accountID;
        this._has_accountID = true;
    }

    public void setEnergyCompanyID(int energyCompanyID) {
        this._energyCompanyID = energyCompanyID;
        this._has_energyCompanyID = true;
    }

}
