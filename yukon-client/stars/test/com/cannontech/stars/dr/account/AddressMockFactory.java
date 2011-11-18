package com.cannontech.stars.dr.account;

import com.cannontech.common.model.Address;

public class AddressMockFactory{
    public static Address getAddress1() {
        Address address1 = new Address();
        address1.setLocationAddress1("12345 Bob Street NE");
        address1.setLocationAddress2("");
        address1.setCityName("Minneapolis");
        address1.setStateCode("MN");
        address1.setZipCode("55374");
        address1.setCounty("Hennepin");

        return address1;
    }
    
    public static Address getAddress2() {
        Address address2 = new Address();

        address2.setLocationAddress1("505 Hwy 55");
        address2.setLocationAddress2("");
        address2.setCityName("Plymouth");
        address2.setStateCode("MN");
        address2.setZipCode("55414");
        address2.setCounty("Hennepin");

        return address2;
    }
}