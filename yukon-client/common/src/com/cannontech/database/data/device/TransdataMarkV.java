package com.cannontech.database.data.device;

import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;


/**
 * This type was created in VisualAge.
 */
public class TransdataMarkV extends IEDMeter {
/**
 * Alpha constructor comment.
 */
public TransdataMarkV() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
}


/**
 * This method is placed on this device since it differs greatly from all the
 * other devices. This is not the norm!
 * @param paoID
 * @return
 */
public static synchronized SmartMultiDBPersistent createPoints( Integer paoID )
{
	SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
	int[] ids = com.cannontech.database.db.point.Point.getNextPointIDs(2);

	//add all ther point to the smart object
	smartDB.addDBPersistent( 
		PointFactory.createAnalogPoint(
			"Total kWh",
			paoID,
			new Integer(ids[0]),
			PointTypes.PT_OFFSET_TOTAL_KWH,
			com.cannontech.database.data.point.PointUnits.UOMID_KWH) );
	
	smartDB.addDBPersistent( 
		PointFactory.createAnalogPoint(
			"Total kVArh",
			paoID,
			new Integer(ids[1]),
			2,
			com.cannontech.database.data.point.PointUnits.UOMID_KVARH) );
		
	
	return smartDB;	
}

}
