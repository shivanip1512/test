package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
public final class PointFactory {
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.point.PointBase
 * @param type int
 */
public final static PointBase createPoint(int type) {

	PointBase retPoint = null;
	switch(type)
	{
		case PointTypes.ANALOG_POINT:
			retPoint = new AnalogPoint();
			retPoint.getPoint().setPointType( PointTypes.getType( PointTypes.ANALOG_POINT  ));
			break;
		case PointTypes.PULSE_ACCUMULATOR_POINT:
			retPoint =  new AccumulatorPoint();
			retPoint.getPoint().setPointType( PointTypes.getType( PointTypes.PULSE_ACCUMULATOR_POINT ));
			break;
		case PointTypes.DEMAND_ACCUMULATOR_POINT:
			retPoint =  new AccumulatorPoint();
			retPoint.getPoint().setPointType( PointTypes.getType( PointTypes.DEMAND_ACCUMULATOR_POINT ));
			break;
		case PointTypes.STATUS_POINT:
			retPoint = new StatusPoint();
			retPoint.getPoint().setPointType( PointTypes.getType( PointTypes.STATUS_POINT ));
			break;
		case PointTypes.CALCULATED_POINT:
			retPoint = new CalculatedPoint();
			retPoint.getPoint().setPointType( PointTypes.getType( PointTypes.CALCULATED_POINT  ));
			break;
		default: //this is bad
			throw new Error("PointFactory::createPoint - Unrecognized point type");
	}
	
	return retPoint;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/99 2:34:02 PM)
 * @return com.cannontech.database.data.point.PointBase
 * @param id java.lang.Integer
 */
public static final PointBase retrievePoint(Integer id) throws java.sql.SQLException {

	return retrievePoint( id, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/99 2:34:02 PM)
 * @return com.cannontech.database.data.point.PointBase
 * @param id java.lang.Integer
 */
public static final PointBase retrievePoint(Integer id, String databaseAlias) throws java.sql.SQLException {

	java.sql.Connection conn = null;
	PointBase returnVal = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		com.cannontech.database.db.point.Point p = new com.cannontech.database.db.point.Point();
		p.setPointID( id );

		p.setDbConnection(conn);
		p.retrieve();
		p.setDbConnection(null);

		returnVal = createPoint( PointTypes.getType(p.getPointType()) );
		returnVal.setPointID( id );

	
		returnVal.setDbConnection( conn );
		returnVal.retrieve();
		returnVal.setDbConnection( null );
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try {
		if( conn != null ) conn.close();
		} catch( java.sql.SQLException e2 ) { }
	}

	return returnVal;	
}
}
