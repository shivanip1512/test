/*
 * Created on May 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.data.stars;

import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StarsAMRDetail
{
    private MeterAndPointData meterPointData = null;
    private String accountNumber = null;
    private Integer customerID = null;
    private String mapNumber = null;
    private LiteContact litePrimaryContact = null;
    /**
     * @param meterPointData
     * @param accountNumber
     * @param primaryContactID
     * @param mapNumber
     */
    public StarsAMRDetail(MeterAndPointData meterPointData, String accountNumber, Integer customerID, String mapNumber) {
        super();
        this.meterPointData = meterPointData;
        this.accountNumber = accountNumber;
        this.customerID = customerID;
        this.mapNumber = mapNumber;
    }
    /**
     * @return Returns the accountNumber.
     */
    public String getAccountNumber()
    {
        return accountNumber;
    }
    /**
     * @return Returns the mapNumber.
     */
    public String getMapNumber()
    {
        return mapNumber;
    }
    /**
     * @return Returns the meterPointData.
     */
    public MeterAndPointData getMeterPointData()
    {
        return meterPointData;
    }
    /**
     * @return Returns the customerID.
     */
    public Integer getCustomerID()
    {
        return customerID;
    }
    public LiteContact getLitePrimaryContact()
    {
        if (litePrimaryContact == null)
        {
            litePrimaryContact = CustomerFuncs.getPrimaryContact(getCustomerID().intValue());
        }
        return litePrimaryContact;
    }
    public LiteYukonPAObject getLitePaobject()
    {
        return getMeterPointData().getLitePaobject();
    }
    public LiteDeviceMeterNumber getLiteDeviceMeterNumber()
    {
        return getMeterPointData().getLiteDeviceMeterNumber();
    }
}
