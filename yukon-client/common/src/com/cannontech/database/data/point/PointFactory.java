package com.cannontech.database.data.point;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;

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




public static PointBase createAnalogPoint( String pointName, Integer paoID, 
		Integer pointID, int pointOffset, int pointUnit )
{
	com.cannontech.database.data.point.PointBase point =
		com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);
	
	point = PointFactory.createNewPoint(		
			pointID,
			com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
			pointName,
			paoID,
			new Integer(pointOffset) );
	
	point.getPoint().setStateGroupID( 
		new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG) );
	
	//defaults - pointUnit
	((com.cannontech.database.data.point.ScalarPoint)point).setPointUnit(
		new com.cannontech.database.db.point.PointUnit(
			pointID,
			new Integer(pointUnit),
			new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
			new Double(0.0),
			new Double(0.0)));
	
	//defaults - pointAnalog
	((com.cannontech.database.data.point.AnalogPoint)point).setPointAnalog(
		new com.cannontech.database.db.point.PointAnalog(
			pointID,
			new Double(-1.0),
			com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.TRANSDUCER_NONE),
			new Double(1.0),
			new Double(0.0)));

	
	return point;	
}


public static PointBase createDmdAccumPoint( String pointName, Integer paoID, 
      Integer pointID, int pointOffset, int pointUnit, double multiplier )
{
   com.cannontech.database.data.point.PointBase point =
      com.cannontech.database.data.point.PointFactory.createPoint(
            com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT );
   
   point = PointFactory.createNewPoint(    
         pointID,
         com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT,
         pointName,
         paoID,
         new Integer(pointOffset) );
   
   point.getPoint().setStateGroupID( 
      new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ACCUMULATOR) );

   //defaults - pointAccumulator   
   com.cannontech.database.db.point.PointAccumulator accumPt = 
      new com.cannontech.database.db.point.PointAccumulator(
         pointID, new Double(multiplier), new Double(0.0) );
   
   ((AccumulatorPoint)point).setPointAccumulator( accumPt );  
   
   //defaults - pointUnit
   ((com.cannontech.database.data.point.ScalarPoint)point).setPointUnit(
      new com.cannontech.database.db.point.PointUnit(
         pointID,
         new Integer(pointUnit),
         new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
         new Double(0.0),
         new Double(0.0)));
   
   return point;  
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static PointBase createNewPoint( Integer pointID, int pointType, String pointName, Integer paoID, Integer offset )
{	
	//A point is automatically created here
	PointBase newPoint =
		com.cannontech.database.data.point.PointFactory.createPoint( pointType );

	
	//set default point values for point tables		
	newPoint.setPoint(
		new com.cannontech.database.db.point.Point(
			pointID,
			PointTypes.getType(pointType),
			pointName,
			paoID,
			PointLogicalGroups.getLogicalGrp(PointLogicalGroups.LGRP_DEFAULT),
			new Integer(0),
			com.cannontech.common.util.CtiUtilities.getFalseCharacter(),
			com.cannontech.common.util.CtiUtilities.getFalseCharacter(),
			offset,
			"None",
			new Integer(0)));

	newPoint.setPointAlarming(
		new com.cannontech.database.db.point.PointAlarming(
			pointID,
			com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES,
			com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY,
			"N",
			new Integer(com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID),
			new Integer(CtiUtilities.NONE_ID)) );

	return newPoint;
}

public static PointBase createPulseAccumPoint( String pointName, Integer paoID, 
      Integer pointID, int pointOffset, int pointUnit, double multiplier )
{
   PointBase point = PointFactory.createNewPoint(  
         pointID,
         PointTypes.PULSE_ACCUMULATOR_POINT,
         pointName,
         paoID,
         new Integer(pointOffset) );
   
   point.getPoint().setStateGroupID( 
      new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ACCUMULATOR) );

   //defaults - pointAccumulator   
   com.cannontech.database.db.point.PointAccumulator accumPt = 
      new com.cannontech.database.db.point.PointAccumulator(
         pointID, new Double(multiplier), new Double(0.0) );
   
   ((AccumulatorPoint)point).setPointAccumulator( accumPt );  
   
   //defaults - pointUnit
   ((com.cannontech.database.data.point.ScalarPoint)point).setPointUnit(
      new com.cannontech.database.db.point.PointUnit(
         pointID,
         new Integer(pointUnit),
         new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
         new Double(0.0),
         new Double(0.0)));
   
   return point;  
}


/**
 * Creates a CapBanks analog op count point automatically
 * 
 */
public static synchronized void createBankOpCntPoint(
		SmartMultiDBPersistent newVal )
{	
	//defaults pointControl
	//an analog point is created

	newVal.addDBPersistent( 
		PointFactory.createAnalogPoint(
			"OPERATION",
			com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID(),
			null,
			PointTypes.PT_OFFSET_TOTAL_KWH,
			com.cannontech.database.data.point.PointUnits.UOMID_COUNTS) );
}

/**
 * Creates a CapBanks stutus point automatically
 * 
 */
public static synchronized void createBankStatusPt(
		SmartMultiDBPersistent newVal )
{

	//a status point is created
	com.cannontech.database.data.point.PointBase newPoint =
		com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.STATUS_POINT);
	Integer pointID = null;

	//defaults point
	newPoint = PointFactory.createNewPoint(		
			pointID,
			com.cannontech.database.data.point.PointTypes.STATUS_POINT,
			"BANK STATUS",
			com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID(),
			new Integer(1) );

	newPoint.getPoint().setStateGroupID( new Integer(3) );
	
	//defaults pointStatus
	((com.cannontech.database.data.point.StatusPoint) newPoint).setPointStatus(
		new com.cannontech.database.db.point.PointStatus(pointID) );

	newVal.addDBPersistent(newPoint);		
}

}