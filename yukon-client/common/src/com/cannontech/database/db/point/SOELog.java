package com.cannontech.database.db.point;

import java.util.Date;

/**
 * This type was created in VisualAge.
 */
public class SOELog extends com.cannontech.database.db.DBPersistent 
{
	private Integer logID = null;
	private Integer pointID = null;
	private Date soeDateTime = null;
	private Integer millis = null;
	private String description = null;
	private String additionalInfo = null;


	public static final String CONSTRAINT_COLUMNS[] = { "LogID" };
	public static final String COLUMNS[] = 
	{	
		"PointID", "SOEDateTime", 
		"Millis", "Description", "AdditionalInfo"
	};


	public final static String TABLE_NAME = "SOELog";

/**
 * SOELog constructor comment.
 */
public SOELog() {
	super();
}
/**
 * PointUnit constructor comment.
 */
public SOELog(Integer pointID) {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = 
	{ 
		getLogID(), getPointID(), 
		getSoeDateTime(), getMillis(),
		getDescription(), getAdditionalInfo() 
	};

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	delete( TABLE_NAME, "LogID", getLogID() );
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLogID() {
	return logID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getPointID() {
	return pointID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getLogID() };

	Object results[] = retrieve( COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == COLUMNS.length )
	{
		setPointID( (Integer) results[0] );
		setSoeDateTime( (Date) results[1] );
		setMillis( (Integer) results[2] );
		setDescription( (String) results[3] );
		setAdditionalInfo( (String) results[4] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:07:44 AM)
 * @param newLogID java.lang.Integer
 */
public void setLogID(java.lang.Integer newLogID) {
	logID = newLogID;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setPointID(Integer newValue) {
	this.pointID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = 
	{ 
		getPointID(), 
		getSoeDateTime(), getMillis(),
		getDescription(), getAdditionalInfo() 
	};
	
	Object constraintValues[] = { getLogID() };

	update( TABLE_NAME, COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * @return
	 */
	public String getAdditionalInfo()
	{
		return additionalInfo;
	}

	/**
	 * @return
	 */
	public Integer getMillis()
	{
		return millis;
	}

	/**
	 * @return
	 */
	public Date getSoeDateTime()
	{
		return soeDateTime;
	}

	/**
	 * @param string
	 */
	public void setAdditionalInfo(String string)
	{
		additionalInfo = string;
	}

	/**
	 * @param integer
	 */
	public void setMillis(Integer integer)
	{
		millis = integer;
	}

	/**
	 * @param date
	 */
	public void setSoeDateTime(Date date)
	{
		soeDateTime = date;
	}

}
