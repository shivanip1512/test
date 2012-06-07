package com.cannontech.stars.xml.serialize;

public class StarsEnergyCompanySettings {
    private int _energyCompanyID;
    private boolean _has_energyCompanyID;
    private StarsEnergyCompany _starsEnergyCompany;
    private StarsWebConfig _starsWebConfig;
    private StarsEnrollmentPrograms _starsEnrollmentPrograms;
    private StarsCustomerSelectionLists _starsCustomerSelectionLists;
    private StarsServiceCompanies _starsServiceCompanies;
    private StarsSubstations _starsSubstations;

    public void deleteEnergyCompanyID() {
        this._has_energyCompanyID= false;
    }

    public int getEnergyCompanyID() {
        return this._energyCompanyID;
    } 

    public StarsCustomerSelectionLists getStarsCustomerSelectionLists() {
        return this._starsCustomerSelectionLists;
    } 

    public StarsEnergyCompany getStarsEnergyCompany() {
        return this._starsEnergyCompany;
    }

    public StarsEnrollmentPrograms getStarsEnrollmentPrograms() {
        return this._starsEnrollmentPrograms;
    }

    public StarsServiceCompanies getStarsServiceCompanies() {
        return this._starsServiceCompanies;
    } 

    public StarsSubstations getStarsSubstations() {
        return this._starsSubstations;
    } 

    public StarsWebConfig getStarsWebConfig() {
        return this._starsWebConfig;
    } 

    public boolean hasEnergyCompanyID() {
        return this._has_energyCompanyID;
    }

    public void setEnergyCompanyID(int energyCompanyID) {
        this._energyCompanyID = energyCompanyID;
        this._has_energyCompanyID = true;
    } 

    public void setStarsCustomerSelectionLists(StarsCustomerSelectionLists starsCustomerSelectionLists) {
        this._starsCustomerSelectionLists = starsCustomerSelectionLists;
    } 

    public void setStarsEnergyCompany(StarsEnergyCompany starsEnergyCompany) {
        this._starsEnergyCompany = starsEnergyCompany;
    } 

    public void setStarsEnrollmentPrograms(StarsEnrollmentPrograms starsEnrollmentPrograms) {
        this._starsEnrollmentPrograms = starsEnrollmentPrograms;
    } 

    public void setStarsServiceCompanies(StarsServiceCompanies starsServiceCompanies) {
        this._starsServiceCompanies = starsServiceCompanies;
    } 

    public void setStarsSubstations(StarsSubstations starsSubstations) {
        this._starsSubstations = starsSubstations;
    }

    public void setStarsWebConfig(StarsWebConfig starsWebConfig) {
        this._starsWebConfig = starsWebConfig;
    } 

}
