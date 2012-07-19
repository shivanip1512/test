package com.cannontech.database.data.point;

import com.cannontech.database.db.point.PointAnalog;
import com.cannontech.database.db.point.PointAnalogControl;

public class AnalogPoint extends ScalarPoint {
	
    private PointAnalog pointAnalog = null;
    private PointAnalogControl pointAnalogControl = null;
/**
 * AnalogPoint constructor comment.
 */
public AnalogPoint() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();

	getPointAnalog().add();
	getPointAnalogControl().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:51:40 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	
	getPointAnalog().add();
	getPointAnalogControl().add();

	super.addPartial();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

    getPointAnalogControl().delete();
	getPointAnalog().delete();
	
	super.delete();		
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:52:09 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	
	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointAnalogSetting
 */
public PointAnalog getPointAnalog() {
	if (pointAnalog == null) {
		pointAnalog = new PointAnalog();
	}
	return pointAnalog;
}

public PointAnalogControl getPointAnalogControl() {
    if (pointAnalogControl == null) {
        pointAnalogControl = new PointAnalogControl();
    }
    return pointAnalogControl;
}

/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();

	getPointAnalog().retrieve();
	getPointAnalogControl().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPointAnalog().setDbConnection(conn);
	getPointAnalogControl().setDbConnection(conn);
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {

	super.setPointID(pointID);

	getPointAnalog().setPointID(pointID);
	getPointAnalogControl().setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();

	getPointAnalog().update();
	getPointAnalogControl().update();
}
}
