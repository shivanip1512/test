package com.cannontech.database.db.point;

/**
 * This type was created in VisualAge.
 */
public class UnitMeasure extends com.cannontech.database.db.DBPersistent 
{
	// ** This class was created for the sole purpose of defining the table's name.
	//  You can not add/delete/retrieve/update this class in any way.  The columns
	//  are predefined in the database and are not to be altered by code.
	//  Please see CTI's DBA for further instructions.	SN 7/10/02.
	
	public static final String CONSTRAINT_COLUMNS[] = { "UOMID" };

	public static final String SETTER_COLUMNS[] = 
	{ 
		"UomID", "UomName", "CalcType", "LongName", "Formula"
	};

	public final static String TABLE_NAME = "UnitMeasure";

/**
 * add method comment.
 */
public void add() throws java.sql.SQLException
{
	throw new Error(getClass() + "You cannot perform this task on UnitMeasure objects.  Please talk to Ryan/Stacey.");}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	throw new Error(getClass() + "You cannot perform this task on UnitMeasure objects.  Please talk to Ryan/Stacey.");}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	throw new Error(getClass() + "You cannot perform this task on UnitMeasure objects.  Please talk to Ryan/Stacey.");
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	throw new Error(getClass() + "You cannot perform this task on UnitMeasure objects.  Please talk to Ryan/Stacey.");
}
}