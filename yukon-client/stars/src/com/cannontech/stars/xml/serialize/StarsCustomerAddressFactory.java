package com.cannontech.stars.xml.serialize;

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
}