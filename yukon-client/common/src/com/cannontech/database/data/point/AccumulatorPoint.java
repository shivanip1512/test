package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.point.*;

public class AccumulatorPoint extends ScalarPoint {
	private PointAccumulator pointAccumulator = null;
/**
 * AccumulatorPoint constructor comment.
 */
public AccumulatorPoint() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {

	super.add();

	getPointAccumulator().add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:56:14 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {

	getPointAccumulator().add();
	super.addPartial();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

	getPointAccumulator().delete();

	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:56:45 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {


	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointAccumulator
 */
public PointAccumulator getPointAccumulator() {
	if( pointAccumulator == null )
		pointAccumulator = new PointAccumulator();
		
	return pointAccumulator;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{

	super.retrieve();

	getPointAccumulator().retrieve();
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPointAccumulator().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.PointAccumulator
 */
public void setPointAccumulator(PointAccumulator newValue) {
	this.pointAccumulator = newValue;
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {
	super.setPointID(pointID);

	getPointAccumulator().setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {

	super.update();

	getPointAccumulator().update();
}
}
