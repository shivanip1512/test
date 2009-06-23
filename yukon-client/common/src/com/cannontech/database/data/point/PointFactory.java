package com.cannontech.database.data.point;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.TypeBase;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * This type was created in VisualAge.
 */
public final class PointFactory {
public static final String PTNAME_TAG = "Tag Point";

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
		case PointTypes.CALCULATED_STATUS_POINT:
			retPoint = new CalcStatusPoint();
			retPoint.getPoint().setPointType( PointTypes.getType( PointTypes.CALCULATED_STATUS_POINT ));
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
		Integer pointID, int pointOffset, int pointUnit, int stateGroupId )
{
	com.cannontech.database.data.point.PointBase point =
		com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);
	
	point = PointFactory.createNewPoint(		
			pointID,
			com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
			pointName,
			paoID,
			new Integer(pointOffset) );
	
	point.getPoint().setStateGroupID( stateGroupId);			// new Integer(StateGroupUtils.STATEGROUP_ANALOG) );
	
	//defaults - pointUnit
	((com.cannontech.database.data.point.ScalarPoint)point).setPointUnit(
		new com.cannontech.database.db.point.PointUnit(
			pointID,
			new Integer(pointUnit),
			new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
			new Double(0.0),
			new Double(0.0),
			new Integer (0)));
	
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

public static PointBase createAnalogPoint( String pointName, Integer paoID, 
        Integer pointID, int pointOffset, int pointUnit, double multiplier, int stateGroupId )
{
    com.cannontech.database.data.point.PointBase point =
        com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);
    
    point = PointFactory.createNewPoint(        
            pointID,
            com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
            pointName,
            paoID,
            new Integer(pointOffset) );
    
    point.getPoint().setStateGroupID( stateGroupId);	// new Integer(StateGroupUtils.STATEGROUP_ANALOG) );
    
    //defaults - pointUnit
    ((com.cannontech.database.data.point.ScalarPoint)point).setPointUnit(
        new com.cannontech.database.db.point.PointUnit(
            pointID,
            new Integer(pointUnit),
            new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
            new Double(0.0),
            new Double(0.0),
            new Integer (0)));
    
    //defaults - pointAnalog
    ((com.cannontech.database.data.point.AnalogPoint)point).setPointAnalog(
        new com.cannontech.database.db.point.PointAnalog(
            pointID,
            new Double(-1.0),
            com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.TRANSDUCER_NONE),
            new Double (multiplier),
            new Double(0.0)));

    
    return point;   
}


public static PointBase createDmdAccumPoint( String pointName, Integer paoID, 
      Integer pointID, int pointOffset, int pointUnit, double multiplier, int stateGroupId )
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
   
   point.getPoint().setStateGroupID( stateGroupId);			//new Integer(StateGroupUtils.STATEGROUP_ANALOG) );

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
         new Double(0.0),
         new Integer (0)));
   
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
			new Integer(CtiUtilities.NONE_ZERO_ID)) );

	return newPoint;
}

public static PointBase createPulseAccumPoint( String pointName, Integer paoID, 
      Integer pointID, int pointOffset, int pointUnit, double multiplier, int stateGroupId )
{
   final int defaultDecimalPlaces = (pointOffset != 1) ? 
           com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES : 1;
    
   PointBase point = PointFactory.createNewPoint(  
         pointID,
         PointTypes.PULSE_ACCUMULATOR_POINT,
         pointName,
         paoID,
         new Integer(pointOffset) );
   
   point.getPoint().setStateGroupID(stateGroupId);			// new Integer(StateGroupUtils.STATEGROUP_ANALOG) );

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
         new Integer(defaultDecimalPlaces),
         new Double(0.0),
         new Double(0.0),
         new Integer (0)));
   
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
  PaoDao paoDao = DaoFactory.getPaoDao();
	newVal.addDBPersistent( 
		createBankOpCntPoint(paoDao.getNextPaoId() ) );
}

/**
 * Creates a CapBanks analog op count point automatically
 */
public static synchronized PointBase createBankOpCntPoint( Integer capBankID )
{	
	//defaults pointControl
	//an analog point is created
	return
		PointFactory.createAnalogPoint(
			"OPERATION",
			capBankID,
			null,
			PointTypes.PT_OFFSET_TOTAL_KWH,
			PointUnits.UOMID_COUNTS, 
			StateGroupUtils.STATEGROUP_ANALOG);
}

/**
 * Creates a CapBanks stutus point automatically
 * 
 */
public static synchronized void createBankStatusPt(
		SmartMultiDBPersistent newVal )
{
	PaoDao paoDao = DaoFactory.getPaoDao();
	newVal.addDBPersistent(
			createBankStatusPt(paoDao.getNextPaoId()));		
}

/**
 * Creates a status point
 */
public static synchronized PointBase createStatusPoint( String pointName, Integer paoID, 
	      Integer pointID, int pointOffset, int stateGroupId )
{
	//Create new point
	PointBase newPoint = PointFactory.createPoint(PointTypes.STATUS_POINT);
	newPoint = PointFactory.createNewPoint(		
			pointID,
			PointTypes.STATUS_POINT,
			pointName,
			paoID,
			pointOffset );

	newPoint.getPoint().setStateGroupID( stateGroupId );
	
	//defaults pointStatus
	((StatusPoint) newPoint).setPointStatus( new PointStatus(pointID) );

	return newPoint;
}

/**
 * Creates a CapBanks stutus point automatically
 * 
 */
public static synchronized PointBase createBankStatusPt( Integer capBankID )
{

	//a status point is created
	PointBase newPoint =
		PointFactory.createPoint(PointTypes.STATUS_POINT);

	Integer pointID = null;

	//defaults point
	newPoint = PointFactory.createNewPoint(		
			pointID,
			PointTypes.STATUS_POINT,
			"BANK STATUS",
			capBankID,
			new Integer(1) );

	newPoint.getPoint().setStateGroupID( new Integer(3) );
	
	//defaults pointStatus
	((StatusPoint) newPoint).setPointStatus( new PointStatus(pointID) );

	return newPoint;
}

public static PointBase createCalcStatusPoint (Integer paoId, String name, int stateGroupId){
    
    PointBase newPoint =
        PointFactory.createPoint(PointTypes.STATUS_POINT);

    newPoint = PointFactory.createNewPoint(     
                                        newPoint.getPoint().getPointID(),
                                        PointTypes.CALCULATED_STATUS_POINT,
                                        name,
                                        paoId,
                                        new Integer (TypeBase.POINT_OFFSET) );
    
    
    newPoint.getPoint().setStateGroupID( stateGroupId);				//new Integer (StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));
    
    //defaults pointStatus
    ((CalcStatusPoint) newPoint).setPointStatus( new PointStatus(newPoint.getPoint().getPointID()) );
    ((CalcStatusPoint) newPoint).getCalcBase().setPeriodicRate(new Integer (1));
    ((CalcStatusPoint) newPoint).getCalcBase().setUpdateType("On All Change");
    
    return newPoint;
    
}

public static PointBase createCalculatedPoint(Integer paoId, String name, int stateGroupId){
    PointBase point = createPoint(PointTypes.CALCULATED_POINT);
    
    point = PointFactory.createNewPoint(    
                                        point.getPoint().getPointID(),
                                        PointTypes.CALCULATED_POINT,
                                        name,
                                        paoId,
                                        new Integer (TypeBase.POINT_OFFSET)
                                         );
    
    point.getPoint().setStateGroupID(stateGroupId);			//new Integer (StateGroupUtils.STATEGROUP_ANALOG));
    PointUnit punit = new PointUnit  (point.getPoint().getPointID(),
                                      new Integer (PointUnits.UOMID_UNDEF),
                                      new Integer(PointUnit.DEFAULT_DECIMAL_PLACES),
                                      new Double(0.0),
                                      new Double(0.0),
                                      new Integer (0));
    
    ((ScalarPoint)point).setPointUnit(punit);
                                                                           
    
    //Calculated Point consists of CalcBase and CalcPointBaseline
    CalcBase calcBase = new CalcBase();
    calcBase.setPointID(point.getPoint().getPointID());
    calcBase.setUpdateType(PointTypes.UPDATE_FIRST_CHANGE);
    calcBase.setPeriodicRate(new Integer (1));
    
    ((CalculatedPoint)point).setCalcBase(calcBase);    
    ((CalculatedPoint) point).setBaselineAssigned(false);
    
    
    return point;
}

//creates a tag point for a sub or a feeder
public static synchronized PointBase createTagPoint(Integer objectID, Integer offset) {
    PointBase newPoint =
        PointFactory.createPoint(PointTypes.STATUS_POINT);
    Integer pointID = new Integer (DaoFactory.getPointDao().getNextPointId() );
    newPoint = PointFactory.createNewPoint(     
            pointID,
            PointTypes.STATUS_POINT,
            PTNAME_TAG,
            objectID,
            offset );
    newPoint.getPoint().setStateGroupID( new Integer (StateGroupUtils.STATEGROUPID_CAPBANK) );
    
    ((StatusPoint) newPoint).setPointStatus( new PointStatus(pointID) );

    return newPoint;

}

public static synchronized void addPoint(PointBase point) {
    Connection connection = null;
    try {
        connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        point.setDbConnection(connection);
        point.add();
        DaoFactory.getDbPersistentDao().performDBChange(point, DBChangeMsg.CHANGE_TYPE_ADD);

    } catch (SQLException e) {
        CTILogger.error(e);
    }
    catch (PersistenceException te) {
        CTILogger.error(te);
    }
    if (connection != null)
        try {
            connection.close();
        } catch (SQLException e) {
            CTILogger.error(e);
        }


    
}


}