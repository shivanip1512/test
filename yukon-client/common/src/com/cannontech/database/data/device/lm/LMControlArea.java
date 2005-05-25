package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import java.util.Vector;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.NestedDBPersistent;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.NestedDBPersistentComparators;
/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
public class LMControlArea extends com.cannontech.database.data.pao.YukonPAObject implements com.cannontech.common.editor.EditorPanel
{
	private com.cannontech.database.db.device.lm.LMControlArea controlArea = null;

	//contains com.cannontech.database.db.device.lm.LMControlAreaTrigger
	private Vector lmControlAreaTriggerVector = null;

	//contains com.cannontech.database.db.device.lm.LMControlAreaProgramList
	private Vector lmControlAreaProgramVector = null;
/**
 * LMProgramBase constructor comment.
 */
public LMControlArea() {
	super();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_LM_CONTROL_AREA[0] );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	if( getPAObjectID() == null )
		setPAObjectID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

	super.add();
	getControlArea().add();

	// add all the triggers for this control area
	if( getLmControlAreaTriggerVector() != null )
		for( int i = 0; i < getLmControlAreaTriggerVector().size(); i++ )
			((LMControlAreaTrigger) getLmControlAreaTriggerVector().elementAt(i)).add();

			
	// add all the associated LMPrograms for this control area
	if( getLmControlAreaProgramVector() != null )
		for( int i = 0; i < getLmControlAreaProgramVector().size(); i++ )
			((LMControlAreaProgram) getLmControlAreaProgramVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	setDbConnection( getControlArea().getDbConnection() );

	deleteFromDynamicTables();
	
	// delete all the triggers for this control area
	com.cannontech.database.db.device.lm.LMControlAreaTrigger.deleteAllControlAreaTriggers( getControlArea().getDeviceID(), getDbConnection() );
			
	// delete all the associated LMPrograms for this control area
	com.cannontech.database.db.device.lm.LMControlAreaProgram.deleteAllControlAreaProgramList( getControlArea().getDeviceID(), getDbConnection() );

	getControlArea().delete();
	
	super.delete();
	
	setDbConnection(null);
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 11:11:51 AM)
 */
private void deleteFromDynamicTables() throws java.sql.SQLException
{
	delete("DynamicLMControlAreaTrigger", "deviceID", getPAObjectID() );
	delete("DynamicLMControlArea", "deviceID", getPAObjectID() );
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/00 3:57:41 PM)
 * @return com.cannontech.database.db.device.lm.LMProgram
 */
public com.cannontech.database.db.device.lm.LMControlArea getControlArea() 
{
	if( controlArea == null )
		controlArea = new com.cannontech.database.db.device.lm.LMControlArea();
		
	return controlArea;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:15:21 PM)
 * @return java.util.Vector
 */
public Vector getLmControlAreaProgramVector() 
{
	if( lmControlAreaProgramVector == null )
		lmControlAreaProgramVector = new Vector(10);
		
	return lmControlAreaProgramVector;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 1:56:30 PM)
 * @return java.util.Vector
 */
public Vector getLmControlAreaTriggerVector() 
{
	if( lmControlAreaTriggerVector == null )
		lmControlAreaTriggerVector = new Vector(5);
		
	return lmControlAreaTriggerVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	getControlArea().retrieve();

	try
	{
		com.cannontech.database.db.device.lm.LMControlAreaTrigger triggers[] = com.cannontech.database.db.device.lm.LMControlAreaTrigger.getAllControlAreaTriggers( getControlArea().getDeviceID() );
		for( int i = 0; i < triggers.length; i++ )
		{
			triggers[i].setDbConnection(getDbConnection());
			getLmControlAreaTriggerVector().addElement( triggers[i] );
		}

		com.cannontech.database.db.device.lm.LMControlAreaProgram programs[] = com.cannontech.database.db.device.lm.LMControlAreaProgram.getAllControlAreaList( getControlArea().getDeviceID(), getDbConnection() );
		for( int i = 0; i < programs.length; i++ )
		{
			programs[i].setDbConnection(getDbConnection());
			getLmControlAreaProgramVector().addElement( programs[i] );
		}

	}
	catch(java.sql.SQLException e )
	{
		//not necessarily an error
	}

}
/**
 * Insert the method's description here.
 * Creation date: (12/6/00 3:57:41 PM)
 * @param newProgram com.cannontech.database.db.device.lm.LMProgram
 */
public void setControlArea(com.cannontech.database.db.device.lm.LMControlArea newArea) {
	controlArea = newArea;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getControlArea().setDbConnection(conn);

	// do all the triggers for this control area
	if( getLmControlAreaTriggerVector() != null )
		for( int i = 0; i < getLmControlAreaTriggerVector().size(); i++ )
			((LMControlAreaTrigger) getLmControlAreaTriggerVector().elementAt(i)).setDbConnection(conn);

			
	// do all the associated LMPrograms for this control area
	if( getLmControlAreaProgramVector() != null )
		for( int i = 0; i < getLmControlAreaProgramVector().size(); i++ )
			((LMControlAreaProgram) getLmControlAreaProgramVector().elementAt(i)).setDbConnection(conn);

}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:15:21 PM)
 * @param newLmControlAreaProgramVector java.util.Vector
 */
public void setLmControlAreaProgramVector(java.util.Vector newLmControlAreaProgramVector) {
	lmControlAreaProgramVector = newLmControlAreaProgramVector;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setName( String name )
{
	getYukonPAObject().setPaoName( name );
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setPAObjectID(Integer paoID)
{
	super.setPAObjectID(paoID);
	getControlArea().setDeviceID(paoID);

	// do all the triggers for this control area
	if( getLmControlAreaTriggerVector() != null )
		for( int i = 0; i < getLmControlAreaTriggerVector().size(); i++ )
			((LMControlAreaTrigger) getLmControlAreaTriggerVector().elementAt(i)).setDeviceID(paoID);

			
	// do all the associated LMPrograms for this control area
	if( getLmControlAreaProgramVector() != null )
		for( int i = 0; i < getLmControlAreaProgramVector().size(); i++ )
			((LMControlAreaProgram) getLmControlAreaProgramVector().elementAt(i)).setDeviceID(paoID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	getControlArea().update();
	
	setPAObjectID( controlArea.getDeviceID() );

	//grab all the previous trigger entries for this control area
	Vector oldTrigs = LMControlAreaTrigger.getAllTriggersForAnArea(getPAObjectID(), getDbConnection());
	
	//unleash the power of the NestedDBPersistent
	Vector comparedTrigs = NestedDBPersistentComparators.NestedDBPersistentCompare(oldTrigs, getLmControlAreaTriggerVector(), NestedDBPersistentComparators.lmControlAreaTriggerComparator);

	//throw the triggers at the Db
	for( int i = 0; i < comparedTrigs.size(); i++ )
	{
		((NestedDBPersistent)comparedTrigs.elementAt(i)).executeNestedOp();
	}
	
	//grab all the previous program entries for this scenario
	java.util.Vector oldProgs = LMControlAreaProgram.getAllProgramsForAnArea(getPAObjectID(), getDbConnection());
	
	//unleash the power of the NestedDBPersistent
	Vector comparedPrograms = NestedDBPersistentComparators.NestedDBPersistentCompare(oldProgs, getLmControlAreaProgramVector(), NestedDBPersistentComparators.lmControlAreaProgramComparator);

	//throw the programs into the Db
	for( int i = 0; i < comparedPrograms.size(); i++ )
	{
		((LMControlAreaProgram)comparedPrograms.elementAt(i)).setDeviceID( getPAObjectID() );
		((NestedDBPersistent)comparedPrograms.elementAt(i)).executeNestedOp();

	}
}
}
