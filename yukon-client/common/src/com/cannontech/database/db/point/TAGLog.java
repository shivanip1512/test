package com.cannontech.database.db.point;

import java.util.Date;

/**
 * This type was created in VisualAge.
 */
public class TAGLog extends com.cannontech.database.db.DBPersistent 
{
	private Integer logID = null;
	private Integer instanceID = null;
	private Integer pointID = null;
	private Integer tagID = null;
	private String userName = null;
	private String action = null;
	private String description = null;	
	private Date tagTime = null;	
	private String refStr = null;	
	private String forStr = null;	


	public static final String CONSTRAINT_COLUMNS[] = { "LogID" };
	public static final String COLUMNS[] = 
	{	
		"PointID", "InstanceID", "TagID", "UserName", "Action",
		"Description", "TagTime", "RefStr", "ForStr"
	};


	public final static String TABLE_NAME = "TAGLog";

/**
 * TAGLog constructor comment.
 */
public TAGLog() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = 
	{ 
		getLogID(), getInstanceID(), getPointID(), getTagID(), getUserName(),
		getAction(), getDescription(),
		getTagTime(), getRefStr(), getForStr()
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
		setInstanceID( (Integer) results[1] );
		setTagID( (Integer) results[2] );
		setUserName( (String) results[3] );
		setAction( (String) results[4] );
		setDescription( (String) results[5] );
		setTagTime( (Date) results[6] );
		setRefStr( (String) results[7] );
		setForStr( (String) results[8] );
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
		getPointID(), getInstanceID(), getTagID(), getUserName(),
		getAction(), getDescription(),
		getTagTime(), getRefStr(), getForStr()
	};
	
	Object constraintValues[] = { getLogID() };

	update( TABLE_NAME, COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}


	/**
	 * @return
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @return
	 */
	public String getForStr()
	{
		return forStr;
	}

	/**
	 * @return
	 */
	public String getRefStr()
	{
		return refStr;
	}

	/**
	 * @return
	 */
	public Integer getTagID()
	{
		return tagID;
	}

	/**
	 * @return
	 */
	public Date getTagTime()
	{
		return tagTime;
	}

	/**
	 * @return
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * @param string
	 */
	public void setAction(String string)
	{
		action = string;
	}

	/**
	 * @param string
	 */
	public void setForStr(String string)
	{
		forStr = string;
	}

	/**
	 * @param string
	 */
	public void setRefStr(String string)
	{
		refStr = string;
	}

	/**
	 * @param integer
	 */
	public void setTagID(Integer integer)
	{
		tagID = integer;
	}

	/**
	 * @param date
	 */
	public void setTagTime(Date date)
	{
		tagTime = date;
	}

	/**
	 * @param string
	 */
	public void setUserName(String string)
	{
		userName = string;
	}

	/**
	 * @return
	 */
	public Integer getInstanceID() {
		return instanceID;
	}

	/**
	 * @param integer
	 */
	public void setInstanceID(Integer integer) {
		instanceID = integer;
	}

}
