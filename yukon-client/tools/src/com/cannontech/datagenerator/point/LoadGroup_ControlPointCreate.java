package com.cannontech.datagenerator.point;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointStatus;
/**
 * @author snebben
 *
 * Creates all LoadGroup points for every LoadGroup in the database.
 * There is currently no check to see if the point already exists
 * for a load group paobject.
 * 
 */
public class LoadGroup_ControlPointCreate extends PointCreate
{
	java.util.Hashtable createPointHashtable = null;
	private class CreatePointList
	{
		boolean dailyhistory = true;
		boolean monthlyHistory = true;
		boolean seasonalHistory = true;
		boolean annualHistory = true;
		boolean controlStatus = true;
		boolean controlCountdown = true;
	}


	/**
	 * Constructor for LoadGroup_ControlPointCreate.
	 */
	public LoadGroup_ControlPointCreate()
	{
		super();
	}

	/**
	 * @see com.cannontech.datagenerator.point.PointCreate#create()
	 */
	public boolean create()
	{
		//Points are going to be added to every load group.
		CTILogger.info("Starting Load Group Point creation process...");

		java.util.Vector devicesVector = new java.util.Vector(20);
		getLoadGroupVector(devicesVector);

		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		int pointID = Point.getNextPointID();
		for( int i = 0; i < devicesVector.size(); i++)
		{
			LiteYukonPAObject litePaobject = (LiteYukonPAObject)devicesVector.get(i);
			int paobjectID = litePaobject.getLiteID();
			CreatePointList createPoint = (CreatePointList)createPointHashtable.get(new Integer(paobjectID));
			
			if( createPoint.dailyhistory)
			{
				multi.addDBPersistent(PointFactory.createAnalogPoint("DAILY HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_DAILY_HISTORY,
						PointUnits.UOMID_COUNTS));
				CTILogger.info("Adding DAILY_HISTROY PointId " + pointID + " to Device: " + litePaobject.getPaoName() );
				pointID++;
				addCount++;
			}
			
			if( createPoint.monthlyHistory)
			{
				multi.addDBPersistent(PointFactory.createAnalogPoint("MONTHLY HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_MONTHLY_HISTORY,
						PointUnits.UOMID_COUNTS));
				CTILogger.info("Adding MONTHLY_HISTROY PointId " + pointID  + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}

			if( createPoint.seasonalHistory)
			{
				multi.addDBPersistent(PointFactory.createAnalogPoint("SEASON HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_SEASONAL_HISTORY,
						PointUnits.UOMID_COUNTS));
				CTILogger.info("Adding SEASONAL_HISTROY PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			
			if( createPoint.annualHistory)
			{
				multi.addDBPersistent(PointFactory.createAnalogPoint("ANNUAL HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_ANNUAL_HISTORY,
						PointUnits.UOMID_COUNTS));
				CTILogger.info("Adding ANNUAL_HISTROY PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			
			if( createPoint.controlStatus)
			{
				PointBase pointBase = PointFactory.createNewPoint(new Integer(pointID),
						PointTypes.STATUS_POINT,
						"CONTROL STATUS",
						new Integer(paobjectID),
						new Integer(0) );
				pointBase.getPoint().setStateGroupID(new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));
				((StatusPoint)pointBase).setPointStatus( new PointStatus( new Integer(pointID)));
				((StatusPoint)pointBase).getPointStatus().setControlOffset(	new Integer(1));
				((StatusPoint)pointBase).getPointStatus().setControlType( PointTypes.getType(PointTypes.CONTROLTYPE_NORMAL));
				multi.addDBPersistent(pointBase);
				CTILogger.info("Adding CONTROL_STATUS PointId " + pointID + " to Device: " + litePaobject.getPaoName());
	
				pointID++;
				addCount++;
			}

			if( createPoint.controlCountdown)
			{
				multi.addDBPersistent(PointFactory.createAnalogPoint("CONTROL COUNTDOWN", 
						new Integer(((LiteYukonPAObject)devicesVector.get(i)).getLiteID()),
						new Integer(pointID),
						PointTypes.PT_OFFSET_CONTROL_COUNTDOWN,
						PointUnits.UOMID_COUNTS));
				CTILogger.info("Adding CONTROL_COUNTDOWN PointId " + pointID + " to Device ID" + devicesVector.get(i));
				pointID++;
				addCount++;
			}
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
		{
			CTILogger.info(addCount + " Load Group Control Points were processed and inserted Successfully");
		}
		else
			CTILogger.info("Load Group Control Points failed insertion");
			
		return success;
	}

	/**
	 * Returns true if the Device is a valid container of LMGroup Control points.
	 * A valid type_ is of type all groups except Macro Groups
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param _type int
	 * @return boolean
	 */
	public boolean isDeviceValid( LiteYukonPAObject litePaobject_ )
	{
		//All Groups except Macro groups //
		return
			DeviceTypesFuncs.isLmGroup(litePaobject_.getType())
			&& litePaobject_.getType() != DeviceTypesFuncs.MACRO_GROUP;
	}

	/**
	 * Returns true if the pointOffset and Pointype combination already exists.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param pointOffset_ int
	 * @param pointType_ int
	 * @return boolean
	 */
	public boolean isPointCreated( com.cannontech.database.data.lite.LitePoint lp)
	{
		if( lp.getPointOffset() == PointTypes.PT_OFFSET_DAILY_HISTORY && lp.getPointType() == PointTypes.ANALOG_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).dailyhistory = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_MONTHLY_HISTORY && lp.getPointType() == PointTypes.ANALOG_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).monthlyHistory = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_SEASONAL_HISTORY && lp.getPointType() == PointTypes.ANALOG_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).seasonalHistory = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_ANNUAL_HISTORY && lp.getPointType() == PointTypes.ANALOG_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).annualHistory = false;
		else if( lp.getPointOffset() == 0 && lp.getPointType() == PointTypes.STATUS_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).controlStatus = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_CONTROL_COUNTDOWN && lp.getPointType() == PointTypes.ANALOG_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).controlCountdown = false;
			
		return false;
	}	
	
	/**
	 * Returns a Vector of LiteYukonPaobjects that need a Power Fail point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	protected void getLoadGroupVector(java.util.Vector deviceVector)
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			java.util.List groups = cache.getAllLoadManagement();
			java.util.Collections.sort(groups, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectIDComparator);
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);

			createPointHashtable = new java.util.Hashtable(groups.size());
			
			for (int i = 0; i < groups.size(); i++)
			{
				LiteYukonPAObject litePaobject = ((LiteYukonPAObject)groups.get(i));
				if( isDeviceValid( litePaobject ) )
				{
					int paobjectID = litePaobject.getLiteID();
										
					CreatePointList item = new CreatePointList();
					createPointHashtable.put(new Integer(paobjectID), item);
					
					//makes a list of points devices to add control history points to
					if( addPointToDevice( points, paobjectID ))
					{
						deviceVector.addElement(litePaobject);
					}
				}
			}
			CTILogger.info(deviceVector.size() + " Total Devices needing points added.");
		}
	}
}
