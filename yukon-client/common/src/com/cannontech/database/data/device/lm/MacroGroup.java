package com.cannontech.database.data.device.lm;

/**
 * This type was created in VisualAge.
 */
public class MacroGroup extends LMGroup
{
	//will contain only instances of com.cannontech.database.db.macro.GenericMacro
	private java.util.Vector macroGroupVector = null;

/**
 * MacroRoute constructor comment.
 */
public MacroGroup() {
	super();

	getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_MACRO_GROUP[0] );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	super.add();

	for( int i = 0; i < getMacroGroupVector().size(); i++ ) {
		((com.cannontech.database.db.macro.GenericMacro) getMacroGroupVector().elementAt(i)).setOwnerID( getPAObjectID() );
		((com.cannontech.database.db.macro.GenericMacro) getMacroGroupVector().elementAt(i)).add();
	}

}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	com.cannontech.database.db.macro.GenericMacro.deleteAllGenericMacros( 
			getDevice().getDeviceID(), 
			com.cannontech.database.db.macro.MacroTypes.GROUP,
			getDbConnection() );

	super.delete();
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public java.util.Vector getMacroGroupVector() {

	if( macroGroupVector == null )
		macroGroupVector = new java.util.Vector();
	
	return macroGroupVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();

		macroGroupVector = new java.util.Vector();

		try
		{
			
			com.cannontech.database.db.macro.GenericMacro rArray[] = 
				com.cannontech.database.db.macro.GenericMacro.getGenericMacros( 
					getDevice().getDeviceID(),
					com.cannontech.database.db.macro.MacroTypes.GROUP,
					getDbConnection() );

			for( int i = 0; i < rArray.length; i++ )
			{
				//Since we are in the process of doing a retrieve
				//we need to make sure the new macro routes have a database connection to use
				//otherwise we bomb below
				rArray[i].setDbConnection( getDbConnection() );
				macroGroupVector.addElement( rArray[i] );
			}
		
		}
		catch(java.sql.SQLException e )
		{
			//not necessarily an error
		}
	
	//This necessary??
	for( int i = 0; i < getMacroGroupVector().size(); i++ )
		((com.cannontech.database.db.macro.GenericMacro) getMacroGroupVector().elementAt(i)).retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	for( int i = 0; i < getMacroGroupVector().size(); i++ )
		((com.cannontech.database.db.macro.GenericMacro) getMacroGroupVector().elementAt(i)).setDbConnection(conn);

}
/**
 * This method was created in VisualAge.
 * @param routeID java.lang.Integer
 */
public void setDeviceID(Integer deviceID) {

	super.setDeviceID(deviceID);

	for( int i = 0; i < getMacroGroupVector().size(); i++ )
		((com.cannontech.database.db.macro.GenericMacro) getMacroGroupVector().elementAt(i)).setOwnerID(deviceID);
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setMacroGroupVector(java.util.Vector newValue) {
	this.macroGroupVector = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();

	com.cannontech.database.db.macro.GenericMacro.deleteAllGenericMacros(
			getDevice().getDeviceID(), 
			com.cannontech.database.db.macro.MacroTypes.GROUP,
			getDbConnection() );
	
	for( int i = 0; i < getMacroGroupVector().size(); i++ )
		((com.cannontech.database.db.macro.GenericMacro) getMacroGroupVector().elementAt(i)).add();
}
}
