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
	String sqlString = "SELECT GraphDefinitionID,Name "  + 
		"FROM GraphDefinition where GraphDefinitionID = " + getGraphDefinitionID() +
		" ORDER BY Name";

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try 
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			setGraphDefinitionID( rset.getInt(1) );
			setName( rset.getString(2) );
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}

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
