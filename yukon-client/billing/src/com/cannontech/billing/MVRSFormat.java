package com.cannontech.billing;


/**
 * Insert the type's description here.
 * Creation date: (5/18/00 3:46:39 PM)
 * @author: 
 */
public class MVRSFormat extends FileFormatBase
{
/**
 * MVRSFormat constructor comment.
 */
public MVRSFormat()
{
	super();
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveBillingData(String databaseAlias)
{
	return true;	//the data is read from an input file, not from the database
}
}
