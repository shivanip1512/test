package com.cannontech.database.data.point;

import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.point.PointStatusControl;

public class StatusPoint extends PointBase 
{
	private PointStatus pointStatus = null;
	private PointStatusControl pointStatusControl = null;

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
	getPointStatusControl().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:49:25 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException 
{
	getPointStatus().add();
	getPointStatusControl().add();

	super.addPartial();
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void delete() throws java.sql.SQLException 
{
	getPointStatusControl().delete();
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

public PointStatusControl getPointStatusControl() {
    if( pointStatusControl == null ) {
        pointStatusControl = new PointStatusControl();
    }
    return pointStatusControl;
}

/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void retrieve() throws java.sql.SQLException 
{
	super.retrieve();
		
	getPointStatus().retrieve();
	getPointStatusControl().retrieve();
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
	getPointStatusControl().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) 
{
	super.setPointID(pointID);

	getPointStatus().setPointID(pointID);
	getPointStatusControl().setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	super.update();
	
	getPointStatus().update();
    getPointStatusControl().update();
}
}
