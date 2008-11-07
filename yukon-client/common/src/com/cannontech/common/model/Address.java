package com.cannontech.common.model;

public class Address {
    private String cityName;
    private String stateCode;
    private String zipCode;
    private String locationAddress1;
    private String locationAddress2;
    private String country;

    public Address() {
        
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(final String cityName) {
        this.cityName = cityName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(final String stateCode) {
        this.stateCode = stateCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLocationAddress1() {
        return locationAddress1;
    }

    public void setLocationAddress1(final String locationAddress1) {
        this.locationAddress1 = locationAddress1;
    }

    public String getLocationAddress2() {
        return locationAddress2;
    }

    public void setLocationAddress2(final String locationAddress2) {
        this.locationAddress2 = locationAddress2;
    }
    
    public String getCounty() {
        return country;
    }

    public void setCounty(final String country) {
        this.country = country;
    }
   
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Address)) return false;
        Address obj = (Address) o;
        return ((this.cityName.equals(obj.cityName)) &&
                (this.locationAddress1.equals(obj.locationAddress1)) &&
                (this.locationAddress2.equals(obj.locationAddress2)) &&
                (this.stateCode.equals(obj.stateCode)) &&
                (this.zipCode.equals(obj.zipCode)) &&
                (this.country.equals(obj.country)));   
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + this.cityName.hashCode();
        result = result * 37 + this.stateCode.hashCode();
        result = result * 37 + this.zipCode.hashCode();
        result = result * 37 + this.locationAddress1.hashCode();
        result = result * 37 + this.locationAddress2.hashCode();
        result = result * 37 + this.country.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return Address.class.getName() + ": " +
            this.locationAddress1 + " " +
            this.locationAddress2;
    }
    
}
