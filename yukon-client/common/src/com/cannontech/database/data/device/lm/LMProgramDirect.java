package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.NestedDBPersistent;

public class LMProgramDirect extends LMProgramBase
{
	private com.cannontech.database.db.device.lm.LMProgramDirect directProgram = null;

	//this vector should only contain com.cannontech.database.db.device.lm.LMProgramDirectGear
	private java.util.Vector lmProgramDirectGearVector = null;
	private java.util.Vector lmProgramDirectNotifyGroupVector = null;
/**
 * LMProgramBase constructor comment.
 */
public LMProgramDirect() {
	super();
	
	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_LM_DIRECT_PROGRAM[0] );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	getDirectProgram().add();

	//delete all the current gears from the DB
	//LMProgramDirectGear.deleteAllDirectGears( getDevice().getDeviceID() );

	//add the gears
	for( int i = 0; i < getLmProgramDirectGearVector().size(); i++ )
		((LMProgramDirectGear)getLmProgramDirectGearVector().elementAt(i)).add();


	//add all the Groups for this DirectProgram
	for( int i = 0; i < getLmProgramStorageVector().size(); i++ )
	{
		((com.cannontech.database.db.device.lm.LMProgramDirectGroup)getLmProgramStorageVector().elementAt(i)).setDeviceID( getPAObjectID() );
		((com.cannontech.database.db.device.lm.LMProgramDirectGroup)getLmProgramStorageVector().elementAt(i)).setDbConnection( getDbConnection() );		
		((com.cannontech.database.db.device.lm.LMProgramDirectGroup)getLmProgramStorageVector().elementAt(i)).add();
	}
	
	//add the customers
	for( int i = 0; i < getLmProgramDirectNotifyGroupVector().size(); i++ )
		((LMDirectNotificationGroupList)getLmProgramDirectNotifyGroupVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	
	//delete all of our direct customers first
	  delete(
		  LMDirectNotificationGroupList.TABLE_NAME,
		  LMDirectNotificationGroupList.CONSTRAINT_COLUMNS[0],
		  getPAObjectID() );
	
	LMProgramDirectGear.deleteAllDirectGears( getPAObjectID(), getDbConnection() );
   
	com.cannontech.database.db.device.lm.LMProgramDirectGroup.deleteAllDirectGroups( 
         getPAObjectID(), getDbConnection() );
		
	deleteFromDynamicTables();
	
	getDirectProgram().delete();
	super.delete();
}

/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 11:11:51 AM)
 */
private void deleteFromDynamicTables() throws java.sql.SQLException
{
	delete("DynamicLMProgramDirect", "deviceID", getPAObjectID() );
	delete("DynamicLMGroup", "LMProgramID", getPAObjectID() );
}

/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 3:46:23 PM)
 * @return com.cannontech.database.db.device.lm.LMProgramDirect
 */
public com.cannontech.database.db.device.lm.LMProgramDirect getDirectProgram() 
{
	if( directProgram == null )
		directProgram = new com.cannontech.database.db.device.lm.LMProgramDirect();

	return directProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 5:12:30 PM)
 * @return java.util.Vector
 */
public java.util.Vector getLmProgramDirectGearVector() 
{
	if( lmProgramDirectGearVector == null )
		lmProgramDirectGearVector = new java.util.Vector(10);

	return lmProgramDirectGearVector;
}

public java.util.Vector getLmProgramDirectNotifyGroupVector()
{
	if( lmProgramDirectNotifyGroupVector == null )
		lmProgramDirectNotifyGroupVector = new java.util.Vector();
		
	return lmProgramDirectNotifyGroupVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	getDirectProgram().retrieve();

	//retrieve all the gears for this Program
	java.util.Vector gears = LMProgramDirectGear.getAllDirectGears( getPAObjectID(), getDbConnection() );
	for( int i = 0; i < gears.size(); i++ )
		getLmProgramDirectGearVector().add( gears.elementAt(i) );

	//retrieve all the Groups for this Program
	com.cannontech.database.db.device.lm.LMProgramDirectGroup[] groups = com.cannontech.database.db.device.lm.LMProgramDirectGroup.getAllDirectGroups( getPAObjectID() );
	for( int i = 0; i < groups.length; i++ )
		getLmProgramStorageVector().add( groups[i] );
		
	LMDirectNotificationGroupList[] customers = com.cannontech.database.db.device.lm.LMProgramDirect.getAllNotificationGroupsList( 
		getPAObjectID(), getDbConnection() );

	for( int i = 0; i < customers.length; i++ )
		getLmProgramDirectNotifyGroupVector().add( customers[i] );
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getDirectProgram().setDbConnection(conn);

	for( int i = 0; i < getLmProgramDirectGearVector().size(); i++ )
		((LMProgramDirectGear)getLmProgramDirectGearVector().elementAt(i)).setDbConnection(conn);
		
	for( int i = 0; i < getLmProgramDirectNotifyGroupVector().size(); i++ )
		((LMDirectNotificationGroupList)getLmProgramDirectNotifyGroupVector().elementAt(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 3:46:23 PM)
 * @param newDirectProgram com.cannontech.database.db.device.lm.LMProgramDirect
 */
public void setDirectProgram(com.cannontech.database.db.device.lm.LMProgramDirect newDirectProgram) {
	directProgram = newDirectProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 5:12:30 PM)
 * @param newLmProgramDirectGearVector java.util.Vector
 */
public void setLmProgramDirectGearVector(java.util.Vector newLmProgramDirectGearVector) {
	lmProgramDirectGearVector = newLmProgramDirectGearVector;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setPAObjectID(Integer paoID)
{
	super.setPAObjectID(paoID);
	getDirectProgram().setDeviceID(paoID);

	for( int i = 0; i < getLmProgramDirectGearVector().size(); i++ )
		((LMProgramDirectGear)getLmProgramDirectGearVector().elementAt(i)).setDeviceID( paoID );
	
	for( int i = 0; i < getLmProgramDirectNotifyGroupVector().size(); i++ )
		((LMDirectNotificationGroupList)getLmProgramDirectNotifyGroupVector().elementAt(i)).setDeviceID(paoID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	getDirectProgram().update();
	java.util.Vector gearVector = new java.util.Vector();

	//grab all the previous gear entries for this program
	java.util.Vector oldGears = LMProgramDirectGear.getAllDirectGears(getPAObjectID(), getDbConnection());
	
	//unleash the power of the NestedDBPersistent
	gearVector = CtiUtilities.NestedDBPersistentComparator(oldGears, getLmProgramDirectGearVector());

	//throw the gears into the Db
	for( int i = 0; i < gearVector.size(); i++ )
	{
		((LMProgramDirectGear)gearVector.elementAt(i)).setDeviceID( getPAObjectID() );
		((NestedDBPersistent)gearVector.elementAt(i)).executeNestedOp();

	}

	//delete all the current associated groups from the DB
	com.cannontech.database.db.device.lm.LMProgramDirectGroup.deleteAllDirectGroups( 
         getPAObjectID(), getDbConnection() );


	//add the groups
	for( int i = 0; i < getLmProgramStorageVector().size(); i++ )
	{
		((com.cannontech.database.db.device.lm.LMProgramDirectGroup)getLmProgramStorageVector().elementAt(i)).setDeviceID( getPAObjectID() );
		((com.cannontech.database.db.device.lm.LMProgramDirectGroup)getLmProgramStorageVector().elementAt(i)).setDbConnection( getDbConnection() );
		((com.cannontech.database.db.device.lm.LMProgramDirectGroup)getLmProgramStorageVector().elementAt(i)).add();
	}
	
	//delete all of our energy exchange customers first
	delete( 
		LMDirectNotificationGroupList.TABLE_NAME, 
		LMDirectNotificationGroupList.CONSTRAINT_COLUMNS[0],
		getPAObjectID() );

	for( int i = 0; i < getLmProgramDirectNotifyGroupVector().size(); i++ )
	{
		((LMDirectNotificationGroupList)getLmProgramDirectNotifyGroupVector().elementAt(i)).setDeviceID( getDirectProgram().getDeviceID() );
		((LMDirectNotificationGroupList)getLmProgramDirectNotifyGroupVector().elementAt(i)).add();
	 }

}
}
