package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.point.*;

public class StatusPoint extends PointBase 
{
	private PointStatus pointStatus = null;
/**
 * StatusPoint constructor comment.
 */
public StatusPoint() {
	super();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	super.add();
	
	getPointStatus().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:49:25 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	getPointStatus().add();
	super.addPartial();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	getPointStatus().delete();

	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:50:17 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointStatus
 */
public PointStatus getPointStatus() {
	if( pointStatus == null )
		pointStatus = new PointStatus();
	return pointStatus;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
		
	getPointStatus().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPointStatus().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) 
{
	super.setPointID(pointID);

	getPointStatus().setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.PointStatus
 */
public void setPointStatus(PointStatus newValue) {
	this.pointStatus = newValue;
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	
	getPointStatus().update();
}
}
