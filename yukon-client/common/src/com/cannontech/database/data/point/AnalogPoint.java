package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.point.*;

public class AnalogPoint extends ScalarPoint {
	
	private PointAnalog pointAnalog = null;
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

	//check to see if the point is real or pseudo
	//if pseudo it shouldn't have any 'PointAnalog' info
	//in the db
	getPointAnalog().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:51:40 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	
	getPointAnalogDefaults().add();
	super.addPartial();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

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
	if( pointAnalog == null )
		pointAnalog = new PointAnalog();
		
	return pointAnalog;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 12:20:32 PM)
 * @return com.cannontech.database.db.point.PointAnalog
 */
public PointAnalog getPointAnalogDefaults() {
	

	getPointAnalog().setDeadband(new Double(-1));
	getPointAnalog().setTransducerType(PointTypes.getType(PointTypes.TRANSDUCER_NONE)); 
	getPointAnalog().setMultiplier(new Double(1.0));
	getPointAnalog().setDataOffset(new Double(0.0));

	return getPointAnalog();
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {
	super.retrieve();

	getPointAnalog().retrieve();
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
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.PointAnalogSetting
 */
public void setPointAnalog(PointAnalog newValue) {
	this.pointAnalog = newValue;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {

	super.setPointID(pointID);

	getPointAnalog().setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();

	getPointAnalog().update();
}
}
