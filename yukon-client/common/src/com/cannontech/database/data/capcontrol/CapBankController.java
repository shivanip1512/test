package com.cannontech.database.data.capcontrol;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;

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

   public Integer copiableAddress() 
   {
      return getDeviceCBC().getSerialNumber();
   }

   public void assignAddress( Integer newAddress )
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

public static PointBase createStatusControlPoint( int cbcDeviceID )
{
	//a status point is automatically added to all capbank controllers
	PointBase newPoint = PointFactory.createPoint(
			com.cannontech.database.data.point.PointTypes.STATUS_POINT);

	Integer pointID = new Integer( com.cannontech.database.db.point.Point.getNextPointID() );


	//set default for point tables
	newPoint = PointFactory.createNewPoint(		
			pointID,
			com.cannontech.database.data.point.PointTypes.STATUS_POINT,
			"BANK STATUS",
			new Integer(cbcDeviceID),
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

}
