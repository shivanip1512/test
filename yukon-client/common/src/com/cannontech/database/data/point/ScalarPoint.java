package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.PointUnit;

public class ScalarPoint extends PointBase {
	private java.util.Vector pointLimitsVector = null;
	private PointUnit pointUnit = null;
/**
 * ScalarPoint constructor comment.
 */
public ScalarPoint() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();

	getPointUnit().add();

	for( int i = 0; i < getPointLimitsVector().size(); i++ )
		((PointLimit) getPointLimitsVector().elementAt(i)).add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:48:35 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	
	for (int i = 0; i < getPointLimitsVector().size(); i++)
		 ((PointLimit) getPointLimitsVector().elementAt(i)).add();
	getPointUnit().add();
	super.addPartial();

}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {

	getPointUnit().delete();

	//delete all the associated PointLimits
	delete( PointLimit.TABLE_NAME, "POINTID", getPointUnit().getPointID() );
	//for( int i = 0; i < getPointLimitsVector().size(); i++ )
		//((PointLimit) getPointLimitsVector().elementAt(i)).delete();

	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:48:56 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public java.util.Vector getPointLimitsVector() {

	if( pointLimitsVector == null )
		pointLimitsVector = new Vector();
	
	return pointLimitsVector;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.PointUnit
 */
public PointUnit getPointUnit() {
	if( pointUnit == null )
		pointUnit = new PointUnit();
		
	return pointUnit;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {

	super.retrieve();

	getPointUnit().retrieve();

	try
	{		
		com.cannontech.database.db.point.PointLimit plArray[] = com.cannontech.database.db.point.PointLimit.getPointLimits( getPoint().getPointID(), getDbConnection().toString() );

		for( int i = 0; i < plArray.length; i++ )
			pointLimitsVector.addElement( plArray[i] );
		
	}
	catch(java.sql.SQLException e )
	{		//not necessarily an error 	
	}

	for( int i = 0; i < getPointLimitsVector().size(); i++ )
	{	
		com.cannontech.database.db.DBPersistent o = ((com.cannontech.database.db.DBPersistent) getPointLimitsVector().elementAt(i));
		o.setDbConnection( getDbConnection() );
		o.retrieve();
		o.setDbConnection(null);
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getPointUnit().setDbConnection(conn);

	Vector v = getPointLimitsVector();

	if( v != null )
	{
		for( int i = 0; i < v.size(); i++ )
			((com.cannontech.database.db.DBPersistent) v.elementAt(i)).setDbConnection(conn);
	}
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {
	super.setPointID(pointID);
	
	getPointUnit().setPointID(pointID);
	
	for( int i =0; i < getPointLimitsVector().size(); i++ )
		((PointLimit) getPointLimitsVector().elementAt(i)).setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setPointLimitsVector(java.util.Vector newValue) {
	this.pointLimitsVector = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.PointUnit
 */
public void setPointUnit(PointUnit newValue) {
	this.pointUnit = newValue;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {

	super.update();

	getPointUnit().update();

	PointLimit.deletePointLimits(getPoint().getPointID());
	for( int i = 0 ; i < getPointLimitsVector().size(); i++ )
		((PointLimit) getPointLimitsVector().elementAt(i)).add();
}
}
