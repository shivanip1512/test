package com.cannontech.database.data.lite;

/*
 */
public class LiteStateGroup extends LiteBase
{
	String stateGroupName = null;
	java.util.ArrayList statesList = null;
/**
 * LiteDevice
 */
public LiteStateGroup( int sgID ) 
{
	super();
	
	setLiteID(sgID);
	setLiteType(LiteTypes.STATEGROUP);
}
/**
 * LiteDevice
 */
public LiteStateGroup( int sgID, String sgName ) {
	super();
	setLiteID(sgID);
	stateGroupName = new String(sgName);
	setLiteType(LiteTypes.STATEGROUP);
}
/**
 * LiteDevice
 */
public LiteStateGroup( int sgID, String sgName, java.util.List stList ) {
	super();
	setLiteID(sgID);
	stateGroupName = new String(sgName);
	statesList = new java.util.ArrayList(stList);
	setLiteType(LiteTypes.STATEGROUP);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getStateGroupID() {
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getStateGroupName() {
	return stateGroupName;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public java.util.ArrayList getStatesList() {
	if( statesList == null )
		statesList = new java.util.ArrayList(6);
	return statesList;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) {

 	com.cannontech.database.SqlStatement stmt =
 		new com.cannontech.database.SqlStatement("SELECT Name FROM StateGroup WHERE StateGroupID = " + Integer.toString(getStateGroupID()), databaseAlias);

 	try
 	{
 		stmt.execute();
		stateGroupName = ((String) stmt.getRow(0)[0]);

		stmt = new com.cannontech.database.SqlStatement("SELECT RawState, Text FROM State WHERE StateGroupID = " + Integer.toString(getStateGroupID()) + 
								" AND RAWSTATE >= 0", databaseAlias);
		stmt.execute();

		statesList = null;
		LiteState ls = null;
		for(int i=0;i<stmt.getRowCount();i++)
		{
			ls = new LiteState( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue(), ((String) stmt.getRow(i)[1]) );

			if( ls.getStateRawState() >= 0 )
				getStatesList().add(ls);
		}
 	}
 	catch( Exception e )
 	{
 		e.printStackTrace();
 	}
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStateGroupID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStateGroupName(String newValue) {
	this.stateGroupName = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setStatesList(java.util.List newList) {
	this.statesList = new java.util.ArrayList(newList);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return stateGroupName;
}
}
