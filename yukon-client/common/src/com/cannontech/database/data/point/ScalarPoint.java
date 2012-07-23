package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.database.db.point.PointUnit;

public class ScalarPoint extends PointBase {

	//contains <Integer:limitNumber, PointLimit>
	private Map<Integer, PointLimit> pointLimitsMap = null;

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

	Iterator<PointLimit> it = getPointLimitsMap().values().iterator();
	while( it.hasNext() ) {
		it.next().add();
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:48:35 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {
	
	Iterator<PointLimit> it = getPointLimitsMap().values().iterator();
	while( it.hasNext() )
		it.next().add();

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
 * A map of PointLimits
 */
public Map<Integer, PointLimit> getPointLimitsMap() {

	if( pointLimitsMap == null )
		pointLimitsMap = new HashMap<Integer, PointLimit>();
	
	return pointLimitsMap;
}

/**
 * Convienence method to get the first limit. Returns null
 * if no limit is set.
 */
public PointLimit getLimitOne() {
    PointLimit plOne = getPointLimitsMap().get( new Integer(1) );
    if(plOne == null) {
        return new PointLimit(getPoint().getPointID(), new Integer(1), new Double(0.0), new Double(0.0), new Integer(0));
    }
	return plOne;
}

/**
 * Convienence method to get the second limit. Returns null
 * if no limit is set.
 */
public PointLimit getLimitTwo() {
    PointLimit plTwo = getPointLimitsMap().get( new Integer(2) );
    if(plTwo == null) {
        return new PointLimit(getPoint().getPointID(), new Integer(2), new Double(0.0), new Double(0.0), new Integer(0));
    }
    return plTwo;
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
		com.cannontech.database.db.point.PointLimit plArray[] = com.cannontech.database.db.point.PointLimit.getPointLimits( getPoint().getPointID(), CtiUtilities.getDatabaseAlias() );

		for( int i = 0; i < plArray.length; i++ )
			getPointLimitsMap().put( plArray[i].getLimitNumber(), plArray[i] );
		
	}
	catch(java.sql.SQLException e )
	{		//not necessarily an error 	
	}

	Iterator<PointLimit> it = getPointLimitsMap().values().iterator();
	while( it.hasNext() ) {
		PointLimit o = it.next();
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


	Iterator<PointLimit> it = getPointLimitsMap().values().iterator();
	while( it.hasNext() )
		it.next().setDbConnection(conn);		
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {
	super.setPointID(pointID);
	
	getPointUnit().setPointID(pointID);

	Iterator<PointLimit> it = getPointLimitsMap().values().iterator();
	while( it.hasNext() )
		it.next().setPointID(pointID);
}

/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setPointLimitsMap(Map<Integer, PointLimit> newValue) {
	this.pointLimitsMap = newValue;
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

	PointLimit.deletePointLimits( getPoint().getPointID(), getDbConnection() );
	
	Iterator<PointLimit> it = getPointLimitsMap().values().iterator();
	while( it.hasNext() ) {
		PointLimit pointLimit = it.next();
		pointLimit.setPointID(getPoint().getPointID());
		pointLimit.add();
	}
}

}
