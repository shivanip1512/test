package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
public class LMProgramCurtailment extends LMProgramBase
{
	private com.cannontech.database.db.device.lm.LMProgramCurtailment curtailmentProgram = null;
/**
 * LMProgramBase constructor comment.
 */
public LMProgramCurtailment() {
	super();
}

@Override
protected PaoType getProgramPaoType() {
	return PaoType.LM_CURTAIL_PROGRAM;
}

/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	getCurtailmentProgram().add();

	for( int i = 0; i < getLmProgramStorageVector().size(); i++ )
		((LMProgramCurtailCustomerList)getLmProgramStorageVector().elementAt(i)).add();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

	//must retrieve all curtailReferenceIDs associtiated with this program in order
	//to delete lmcurtailcustomerActivity table entries - must delete tables in the proper order	
	String[] curtailReferenceID = { "CurtailReferenceID" };
	String[] devID = { "DeviceID" };
	Object[] getDevID = { getPAObjectID() };
	Object[][] results = retrieve(curtailReferenceID, "LMCurtailProgramActivity", devID, getDevID, true);

	if (results.length > 0)
	{
		//delete LMCurtailCustomerActvity
		for (int i = 0; i < results.length; i++)
		{
			delete("LMCurtailCustomerActivity", curtailReferenceID, results[i]);

		}
	}
	//delete LMCurtailProgramActivity
	delete("LMCurtailProgramActivity", "DeviceID", getPAObjectID() );
	//delete all current customerlist entries
	delete(com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList.TABLE_NAME, 
				"ProgramID", getCurtailmentProgram().getDeviceID());

	getCurtailmentProgram().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:47:40 PM)
 * @return com.cannontech.database.db.device.lm.LMProgramCurtailment
 */
public com.cannontech.database.db.device.lm.LMProgramCurtailment getCurtailmentProgram() 
{
	if( curtailmentProgram == null )
		curtailmentProgram = new com.cannontech.database.db.device.lm.LMProgramCurtailment();
		
	return curtailmentProgram;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	getCurtailmentProgram().retrieve();

	LMProgramCurtailCustomerList[] customers = com.cannontech.database.db.device.lm.LMProgramCurtailment.getAllCustomerList( 
					getCurtailmentProgram().getDeviceID(), getDbConnection() );

	for( int i = 0; i < customers.length; i++ )
		getLmProgramStorageVector().add( customers[i] );
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:47:40 PM)
 * @param newCurtailmentProgram com.cannontech.database.db.device.lm.LMProgramCurtailment
 */
public void setCurtailmentProgram(com.cannontech.database.db.device.lm.LMProgramCurtailment newCurtailmentProgram) {
	curtailmentProgram = newCurtailmentProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getCurtailmentProgram().setDbConnection(conn);

	for( int i = 0; i < getLmProgramStorageVector().size(); i++ )
		((LMProgramCurtailCustomerList)getLmProgramStorageVector().elementAt(i)).setDbConnection( conn );
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setPAObjectID(Integer paoID)
{
	super.setPAObjectID(paoID);
	getCurtailmentProgram().setDeviceID(paoID);

	for( int i = 0; i < getLmProgramStorageVector().size(); i++ )
		((LMProgramCurtailCustomerList)getLmProgramStorageVector().elementAt(i)).setDeviceID( paoID );
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	getCurtailmentProgram().update();

	//delete all current customerlist entries
	delete( com.cannontech.database.db.device.lm.LMProgramCurtailCustomerList.TABLE_NAME, 
		"ProgramID",
		getCurtailmentProgram().getDeviceID() );

	for( int i = 0; i < getLmProgramStorageVector().size(); i++ )
	{
		((LMProgramCurtailCustomerList)getLmProgramStorageVector().elementAt(i)).setDeviceID( getCurtailmentProgram().getDeviceID() );
		((LMProgramCurtailCustomerList)getLmProgramStorageVector().elementAt(i)).getLmProgramCurtailCustomerList().setCustomerOrder( new Integer(i+1) );
		((LMProgramCurtailCustomerList)getLmProgramStorageVector().elementAt(i)).add();
	}
}
}
