package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.spring.YukonSpringHook;

public class LiteDeviceMeterNumber extends LiteBase
{
	private String meterNumber = null;
	private PaoType paoType;
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
public LiteDeviceMeterNumber(int devID, String meterNum, PaoType paoType)
{
	super();
	setDeviceID(devID);
	setMeterNumber(meterNum);
	setLiteType(LiteTypes.DEVICE_METERNUMBER);
	setPaoType(paoType);
}

public int getDeviceID() {
	return getLiteID();
}

public String getMeterNumber() {
	return meterNumber;
}

public PaoType getPaoType() {
    if(paoType == null) {
        // Hope we never get here, but protection in case we do.
        // LiteDeviceMeterNumber has paoType, but (heavy) DeviceMeterGroup does not
        PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
        paoType = paoDao.getYukonPao(getDeviceID()).getPaoIdentifier().getPaoType();
    }
    return paoType;
}

public void retrieve(String databaseAlias) 
{
   SqlStatement s = new SqlStatement(
         "SELECT MeterNumber, Type FROM DeviceMeterGroup dmg JOIN YukonPaobject pao ON dmg.DeviceId = pao.PaobjectId " +
                 " WHERE DeviceId = " + getDeviceID(),
         CtiUtilities.getDatabaseAlias() );

	try 
	{
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find DeviceMeterGroup with deviceID = " + getLiteID() );

      setMeterNumber(s.getRow(0)[0].toString());
      setPaoType(PaoType.getForDbString(s.getRow(0)[1].toString()));
	}
	catch( Exception e )
	{
		CTILogger.error( "Unable to retrieve LiteDeviceMeterNumber for deviceId=" + getDeviceID(), e );
	}

}

public void setDeviceID(int newValue) 
{
	setLiteID(newValue);
}

public void setMeterNumber(String newValue) {
	this.meterNumber = new String(newValue);
}

public void setPaoType(PaoType paoType) {
    this.paoType = paoType;
}

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

}
