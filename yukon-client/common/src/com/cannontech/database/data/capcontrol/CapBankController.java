package com.cannontech.database.data.capcontrol;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.capcontrol.CapBank;
import com.cannontech.database.db.pao.YukonPAObject;

/**
 * This type was created in VisualAge.
 */
public abstract class CapBankController extends CapControlDeviceBase implements com.cannontech.database.db.DBCopiable, ICapBankController 
{
	private com.cannontech.database.db.capcontrol.DeviceCBC deviceCBC = null;
   
/**
 */
public CapBankController() {
	super();
}

   public Integer getAddress() 
   {
      return getDeviceCBC().getSerialNumber();
   }

   public void setAddress( Integer newAddress )
   {
      getDeviceCBC().setSerialNumber( newAddress );
   }
   
   public void setCommID( Integer comID )
   {
   	getDeviceCBC().setRouteID( comID );
   }

	public Integer getCommID()
	{
		return getDeviceCBC().getRouteID();
	}
  
  
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceCBC().add();
}

public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	getDeviceCBC().add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException{
	getDeviceCBC().delete();
	super.delete();
}
/**
 * This method was created in VisualAge.
 */
public com.cannontech.database.db.capcontrol.DeviceCBC getDeviceCBC() {
	if( deviceCBC == null )
	{
		deviceCBC = new com.cannontech.database.db.capcontrol.DeviceCBC();
	}
	
	return deviceCBC;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();
	getDeviceCBC().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) {
	super.setDbConnection(conn);
	getDeviceCBC().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 */
public void setDeviceCBC(com.cannontech.database.db.capcontrol.DeviceCBC newValue) {
	this.deviceCBC = newValue;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	getDeviceCBC().setDeviceID(deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException{
	super.update();
	getDeviceCBC().update();
}

public static PointBase createStatusControlPoint( Integer cbcDeviceID )
{
	//a status point is automatically added to all capbank controllers
	PointBase newPoint = PointFactory.createPoint(
			com.cannontech.database.data.point.PointTypes.STATUS_POINT);

	Integer pointID = DaoFactory.getPointDao().getNextPointId();

	//set default for point tables
	newPoint = PointFactory.createNewPoint(		
			pointID,
			com.cannontech.database.data.point.PointTypes.STATUS_POINT,
			"BANK STATUS",
			cbcDeviceID,
			new Integer(1) );

	newPoint.getPoint().setStateGroupID( 
         new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );

	((com.cannontech.database.data.point.StatusPoint)newPoint).getPointStatus().setControlOffset(
			new Integer(1) );

	((com.cannontech.database.data.point.StatusPoint)newPoint).getPointStatus().setControlType(
			com.cannontech.database.data.point.PointTypes.getType(
			com.cannontech.database.data.point.PointTypes.CONTROLTYPE_NORMAL) );
	
	((com.cannontech.database.data.point.StatusPoint) newPoint).setPointStatus(
			new com.cannontech.database.db.point.PointStatus(pointID));

	return newPoint;
}

public static PointBase createStatusControlPoint( int cbcDeviceID )
{
	return createStatusControlPoint( new Integer(cbcDeviceID) );
}

/**
 * This method returns all the cbc IDs that are not assgined
 *  to a CapBank.
 */
public static int[] getUnassignedDeviceCBCIds()
{
	NativeIntVector returnVector = new NativeIntVector(16);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	//curerntly, CBCs are the only PAOs that have a category=DEVICE and class=CAPCONTROL
	String sql =
		"SELECT PAObjectID FROM " + YukonPAObject.TABLE_NAME + " where " +
			"Category = '" + PAOGroups.STRING_CAT_DEVICE + "' " + 
			"and PAOClass = '" + PAOGroups.getPAOClass(PAOGroups.CAT_CAPCONTROL, PAOGroups.CLASS_CAPCONTROL) +
			"' and type like 'CBC%'" +
			" and PAObjectID not in (select ControlDeviceID from " + CapBank.TABLE_NAME +
			") ORDER BY PAOName";

	try
	{		
		conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			rset = pstmt.executeQuery();
	
			while( rset.next() ) {
				returnVector.addElement( rset.getInt(1) );
			}
		}		
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt, conn );
	}

	return returnVector.toArray();
}


}
