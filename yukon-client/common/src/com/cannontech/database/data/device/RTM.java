/*
 * Created on May 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.device;

import java.util.Vector;
import com.cannontech.database.db.device.DeviceVerification;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.NestedDBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RTM extends IEDBase {

	private Vector deviceVerificationVector = null;

/**
 * RTM constructor comment.
 */
public RTM() {
	super();
}

public void add() throws java.sql.SQLException 
{
	super.add();
	
	//add the RTCs to be verified
	for( int i = 0; i < getDeviceVerificationVector().size(); i++ )
		((DeviceVerification)getDeviceVerificationVector().elementAt(i)).add();
}

public void delete() throws java.sql.SQLException
{
	//get all those pesky RTCs assigned to this RTM
	DeviceVerification.deleteAllVerifications(getPAObjectID(), getDbConnection());
	
	//nuke the dynamic entries
	deleteFromDynamicTables();
	
	super.delete();
	
}

private void deleteFromDynamicTables() throws java.sql.SQLException
{
	delete("DynamicVerification", "receiverID", getPAObjectID() );
}	
	
public Vector getDeviceVerificationVector() 
{
	if( deviceVerificationVector == null )
		deviceVerificationVector = new java.util.Vector();

	return deviceVerificationVector;
}	
	
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();

	//retrieve all the RTCs for this RTM
	java.util.Vector veries = DeviceVerification.getAllVerifications( getPAObjectID(), getDbConnection() );
	for( int i = 0; i < veries.size(); i++ )
		getDeviceVerificationVector().add( veries.elementAt(i) );
}

public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	for( int i = 0; i < getDeviceVerificationVector().size(); i++ )
		((DeviceVerification)getDeviceVerificationVector().elementAt(i)).setDbConnection(conn);

}

public void setDeviceID(Integer deviceID) {
	super.setDeviceID(deviceID);
	
	for( int i = 0; i < getDeviceVerificationVector().size(); i++ )
		((DeviceVerification)getDeviceVerificationVector().elementAt(i)).setReceiverID( deviceID );
}

public void setDeviceVerificationVector(Vector newDeviceVerificationVector) {
	deviceVerificationVector = newDeviceVerificationVector;
}

public void update() throws java.sql.SQLException
{
	super.update();

	java.util.Vector veriesVector = new java.util.Vector();

	//grab all the previous gear entries for this program
	java.util.Vector oldVeries = DeviceVerification.getAllVerifications(getPAObjectID(), getDbConnection());
	
	//unleash the power of the NestedDBPersistent
	veriesVector = CtiUtilities.NestedDBPersistentComparator(oldVeries, getDeviceVerificationVector());

	//throw the gears into the Db
	for( int i = 0; i < veriesVector.size(); i++ )
	{
		((DeviceVerification)veriesVector.elementAt(i)).setReceiverID( getPAObjectID() );
		((NestedDBPersistent)veriesVector.elementAt(i)).executeNestedOp();
	}
}

}
