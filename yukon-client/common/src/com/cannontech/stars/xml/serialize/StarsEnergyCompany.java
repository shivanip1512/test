package com.cannontech.stars.xml.serialize;

public class StarsEnergyCompany {
    private java.lang.String _companyName;
    private java.lang.String _mainPhoneNumber;
    private java.lang.String _mainFaxNumber;
    private java.lang.String _email;
    private CompanyAddress _companyAddress;
    private java.lang.String _timeZone;

    public StarsEnergyCompany() {

    }

    public CompanyAddress getCompanyAddress()
    {
        return this._companyAddress;
    } 

    public java.lang.String getCompanyName()
    {
        return this._companyName;
    } 

    public java.lang.String getEmail()
    {
        return this._email;
    } 

    public java.lang.String getMainFaxNumber()
    {
        return this._mainFaxNumber;
    }

    public java.lang.String getMainPhoneNumber()
    {
        return this._mainPhoneNumber;
    } 

    public java.lang.String getTimeZone()
    {
        return this._timeZone;
    }

    public void setCompanyAddress(CompanyAddress companyAddress)
    {
        this._companyAddress = companyAddress;
    }

    public void setCompanyName(java.lang.String companyName)
    {
        this._companyName = companyName;
    }

    public void setEmail(java.lang.String email)
    {
        this._email = email;
    } 

    public void setMainFaxNumber(java.lang.String mainFaxNumber)
    {
        this._mainFaxNumber = mainFaxNumber;
    }

    public void setMainPhoneNumber(java.lang.String mainPhoneNumber)
    {
        this._mainPhoneNumber = mainPhoneNumber;
    }

    public void setTimeZone(java.lang.String timeZone)
    {
        this._timeZone = timeZone;
    }

}
