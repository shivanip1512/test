package com.cannontech.database.data.lite;

/**
 * Insert the type's description here.
 * Creation date: (10/5/00 10:37:38 AM)
 * @author: 
 */
public class LiteGraphDefinition extends LiteBase 
{
	private java.lang.String name;
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:46:59 AM)
 */
public LiteGraphDefinition() 
{
	setLiteType(LiteTypes.GRAPHDEFINITION);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:46:59 AM)
 */
public LiteGraphDefinition( int id ) 
{
	setLiteType(LiteTypes.GRAPHDEFINITION);
	setGraphDefinitionID(id);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:46:59 AM)
 */
public LiteGraphDefinition(int id, String name) 
{
	setGraphDefinitionID(id);
	setName(name);
	setLiteType(LiteTypes.GRAPHDEFINITION);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:07 AM)
 * @return int
 */
public int getGraphDefinitionID() {
	return getLiteID();
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:24 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
   com.cannontech.database.SqlStatement s = 
      new com.cannontech.database.SqlStatement(
         "SELECT GraphDefinitionID,Name "  + 
         "FROM GraphDefinition where GraphDefinitionID = " + getGraphDefinitionID() +
         " ORDER BY Name",
         com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try 
   {
      s.execute();

      if( s.getRowCount() <= 0 )
         throw new IllegalStateException("Unable to find DeviceMeterGroup with deviceID = " + getLiteID() );


      setGraphDefinitionID( new Integer(s.getRow(0)[0].toString()).intValue() );
      setName( s.getRow(0)[1].toString() );
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:07 AM)
 * @param newGraphDefinitionID int
 */
public void setGraphDefinitionID(int newGraphDefinitionID) 
{
	setLiteID(newGraphDefinitionID);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:24 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:39:25 AM)
 * @return java.lang.String
 */
public String toString() {
	return name;
}
}
