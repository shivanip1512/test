package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;

/**
 * Insert the type's description here.
 * Creation date: (9/5/2001 5:12:39 PM)
 * @author: 
 */
public class LiteDeviceMeterNumber extends LiteBase
{
	String meterNumber = null;
	String collGroup = null;
	String testCollGroup = null;
	String billGroup = null;
/**
 * LiteDeviceMeterNumber constructor comment.
 */
public LiteDeviceMeterNumber() {
	super();
}
/**
 * LiteDeviceMeterNumber constructor comment.
 */
public LiteDeviceMeterNumber(int devID)
{
	super();
	setDeviceID(devID);
	setLiteType(LiteTypes.DEVICE_METERNUMBER);
}
/**
 * LiteDeviceMeterNumber constructor comment.
 */
public LiteDeviceMeterNumber(int devID, String meterNum, String collGrp, String testCollGrp, String billGrp)
{
	super();
	setDeviceID(devID);
	meterNumber = new String(meterNum);
	collGroup = new String(collGrp);
	testCollGroup = new String(testCollGrp);
	billGroup = new String(billGrp);

	setLiteType(LiteTypes.DEVICE_METERNUMBER);	
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getDeviceID() {
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getMeterNumber() {
	return meterNumber;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
   SqlStatement s = new SqlStatement(
         "SELECT METERNUMBER, COLLECTIONGROUP, TESTCOLLECTIONGROUP, BILLINGGROUP FROM DEVICEMETERGROUP WHERE DEVICEID = " + getDeviceID(),
         CtiUtilities.getDatabaseAlias() );

	try 
	{
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find DeviceMeterGroup with deviceID = " + getLiteID() );


      setMeterNumber( s.getRow(0)[0].toString() );
      setCollGroup(s.getRow(0)[1].toString());
      setTestCollGroup( s.getRow(0)[2].toString());
      setBillGroup(s.getRow(0)[3].toString());
	}
	catch( Exception e )
	{
		CTILogger.error( e.getMessage(), e );
	}

}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setDeviceID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setMeterNumber(String newValue) {
	this.meterNumber = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return meterNumber;
}

/**
 * I can be equal to other Lite PAO stuff
 * @return boolean
 */
public boolean equals(Object o)
{
	if( o instanceof LiteYukonPAObject )
	{
		return ( ((LiteBase)o).getLiteID() == this.getLiteID() );
	}
	else
		return super.equals(o);
}

    /**
     * @return Returns the collGroup.
     */
    public String getCollGroup()
    {
        return collGroup;
    }
    /**
     * @param collGroup The collGroup to set.
     */
    public void setCollGroup(String collGroup)
    {
        this.collGroup = new String(collGroup);
    }

	/**
	 * @return
	 */
	public String getBillGroup()
	{
		return billGroup;
	}

	/**
	 * @param string
	 */
	public void setBillGroup(String billGrp)
	{
		billGroup = new String(billGrp);
	}

	/**
	 * @return
	 */
	public String getTestCollGroup()
	{
		return testCollGroup;
	}

	/**
	 * @param string
	 */
	public void setTestCollGroup(String testCollGrp)
	{
		testCollGroup = new String(testCollGrp);
	}

}
