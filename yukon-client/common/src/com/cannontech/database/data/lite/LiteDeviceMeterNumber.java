package com.cannontech.database.data.lite;

/**
 * Insert the type's description here.
 * Creation date: (9/5/2001 5:12:39 PM)
 * @author: 
 */
public class LiteDeviceMeterNumber extends LiteBase
{
	String meterNumber = null;
	String collGroup = null;
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
public LiteDeviceMeterNumber(int devID, String meterNum, String collGrp)
{
	super();
	setDeviceID(devID);
	meterNumber = new String(meterNum);
	collGroup = new String(collGrp);

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
	
   com.cannontech.database.SqlStatement s = 
      new com.cannontech.database.SqlStatement(
         "SELECT METERNUMBER, COLLECTIONGROUP FROM DEVICEMETERGROUP WHERE DEVICEID = " + getDeviceID(),
         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

	try 
	{
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find DeviceMeterGroup with deviceID = " + getLiteID() );


      setMeterNumber( s.getRow(0)[0].toString() );
      setCollGroup(s.getRow(0)[1].toString());
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
}
