package com.cannontech.stars.xml.serialize;

import com.cannontech.database.db.customer.CustomerAddress;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class StarsCustomerAddressFactory {

    public StarsCustomerAddressFactory() {
    }

    public static StarsCustomerAddress newStarsCustomerAddress(StarsCustomerAddress addr, Class type) {
        try {
            StarsCustomerAddress newAddr = (StarsCustomerAddress) type.newInstance();

            newAddr.setStreetAddr1( addr.getStreetAddr1() );
            newAddr.setStreetAddr2( addr.getStreetAddr2() );
            newAddr.setCity( addr.getCity() );
            newAddr.setState( addr.getState() );
            newAddr.setZip( addr.getZip() );

            return newAddr;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static void setCustomerAddress(CustomerAddress addr, StarsCustomerAddress starsAddr) {
    	addr.setLocationAddress1( starsAddr.getStreetAddr1() );
    	addr.setLocationAddress2( starsAddr.getStreetAddr2() );
    	addr.setCityName( starsAddr.getCity() );
    	addr.setStateCode( starsAddr.getState() );
    	addr.setZipCode( starsAddr.getZip() );
    }
}