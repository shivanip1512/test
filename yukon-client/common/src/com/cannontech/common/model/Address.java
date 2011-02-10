package com.cannontech.common.model;

import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.lite.LiteAddress;

public class Address {
    private String cityName = "";
    private String stateCode = "";
    private String zipCode = "";
    private String locationAddress1 = "";
    private String locationAddress2 = "";
    private String county = "";
    
    public Address() {
    }
    
    public Address(String locationAddress1, String locationAddress2, String cityName, String stateCode, String zipCode, String county) {
    	
    	this.locationAddress1 = locationAddress1;
    	this.locationAddress2 = locationAddress2;
    	this.cityName = cityName;
    	this.stateCode = stateCode;
    	this.zipCode = zipCode;
    	this.county = county;
    }

    public Address(LiteAddress liteAddress) {
    	
    	this.setLocationAddress1(liteAddress.getLocationAddress1());
    	this.setLocationAddress2(liteAddress.getLocationAddress2());
    	this.setCityName(liteAddress.getCityName());
    	this.setStateCode(liteAddress.getStateCode());
    	this.setZipCode(liteAddress.getZipCode());
    	this.setCounty(liteAddress.getCounty());
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
        return county;
    }

    public void setCounty(final String county) {
        this.county = county;
    }
    
    public static Address getDisplayableAddress(LiteAddress liteAddress) {
        Address address = new Address();
        address.setLocationAddress1(StringUtils.stripNone(liteAddress.getLocationAddress1()));
        address.setLocationAddress2(StringUtils.stripNone(liteAddress.getLocationAddress2()));
        address.setCityName(StringUtils.stripNone(liteAddress.getCityName()));
        address.setStateCode(StringUtils.stripNone(liteAddress.getStateCode()));
        address.setZipCode(StringUtils.stripNone(liteAddress.getZipCode()));
        address.setCounty(StringUtils.stripNone(liteAddress.getCounty()));
        return address;
    }
    
    public LiteAddress getLiteAddress(int addressId) {
        LiteAddress liteAddress = new LiteAddress(addressId, getLocationAddress1(),
                        getLocationAddress2(),
                        getCityName(),
                        getStateCode(),
                        getZipCode());
        liteAddress.setCounty(getCounty());
        
        return liteAddress;
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
                (this.county.equals(obj.county)));   
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = result * 37 + this.cityName.hashCode();
        result = result * 37 + this.stateCode.hashCode();
        result = result * 37 + this.zipCode.hashCode();
        result = result * 37 + this.locationAddress1.hashCode();
        result = result * 37 + this.locationAddress2.hashCode();
        result = result * 37 + this.county.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return Address.class.getName() + ": " +
            this.locationAddress1 + " " +
            this.locationAddress2;
    }
    
}
