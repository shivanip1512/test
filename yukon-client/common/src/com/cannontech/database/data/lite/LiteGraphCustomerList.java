package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;

/**
 * Insert the type's description here.
 * Creation date: (10/5/00 10:37:38 AM)
 * @author: 
 */
public class LiteGraphCustomerList extends LiteBase 
{
	private int graphDefinitionID;
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:46:59 AM)
 */
public LiteGraphCustomerList() 
{
	setLiteType(LiteTypes.GRAPH_CUSTOMER_LIST);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:46:59 AM)
 */
public LiteGraphCustomerList( int custId ) 
{
	setLiteType(LiteTypes.GRAPH_CUSTOMER_LIST);
	setCustomerID(custId);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:46:59 AM)
 */
public LiteGraphCustomerList(int custID, int gdefID) 
{
	setCustomerID(custID);
	setGraphDefinitionID(gdefID);
	setLiteType(LiteTypes.GRAPH_CUSTOMER_LIST);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:07 AM)
 * @return int
 */
public int getGraphDefinitionID() {
	return graphDefinitionID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:07 AM)
 * @return int
 */
public int getCustomerID() {
	return getLiteID();
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
	com.cannontech.database.SqlStatement s =
		new com.cannontech.database.SqlStatement(
		"SELECT CustomerID, GraphDefinitionID "  +
		" FROM GraphCustomerList where customerID = " + getCustomerID(),
		CtiUtilities.getDatabaseAlias() );
	
	try
	{
		s.execute();
		
		if( s.getRowCount() <= 0 )
			throw new IllegalStateException("Unable to find graphCustomerList with customerID = " + getLiteID() );

		setCustomerID(new Integer(s.getRow(0)[0].toString()).intValue());
		setGraphDefinitionID( new Integer(s.getRow(0)[1].toString()).intValue() );
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
public void setGraphDefinitionID(int newGDefID) 
{
	graphDefinitionID = newGDefID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:38:07 AM)
 * @param newCustomerID int
 */
public void setCustomerID(int newCustID) 
{
	setLiteID(newCustID);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/00 10:39:25 AM)
 * @return java.lang.String
 */
public String toString() {
	return "GraphDefID: " + getGraphDefinitionID() + "  CustomerID: " + getCustomerID();
}
}
