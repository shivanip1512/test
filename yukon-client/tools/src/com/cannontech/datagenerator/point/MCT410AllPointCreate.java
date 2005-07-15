package com.cannontech.datagenerator.point;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.Point;
/**
 * @author snebben
 *
 * Creates all LoadGroup points for every LoadGroup in the database.
 * There is currently no check to see if the point already exists
 * for a MCT410.
 * 
 */
public class MCT410AllPointCreate extends PointCreate
{
	java.util.Hashtable createPointHashtable = null;
	private class CreatePointList
	{
		boolean voltageLP = true;
		boolean peakKw = true;
		boolean maxVolts = true;
		boolean minVolts = true;
		boolean kw = true;
		boolean voltage = true;
		boolean kWLP= true;
		boolean kWh = true;
		boolean frozenPeakDemand = true;	//offset 21 (demandAcc)
		boolean frozenMaxVolts = true;	//offset 24 (demandAcc)
		boolean frozenMinVolts = true;	//offset 25 (demandAcc)
	}


	/**
	 * Constructor for LoadGroup_ControlPointCreate.
	 */
	public MCT410AllPointCreate()
	{
		super();
	}

	/**
	 * @see com.cannontech.datagenerator.point.PointCreate#create()
	 */
	public boolean create()
	{
		//Points are going to be added to every MCT410.
		CTILogger.info("Starting MCT410 Point creation process...");

		java.util.Vector devicesVector = new java.util.Vector(20);
		getMCT410DeviceVector(devicesVector);

		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		double multiplier = 0.1;
		int addCount = 0;
		int pointID = Point.getNextPointID();
		for( int i = 0; i < devicesVector.size(); i++)
		{
			LiteYukonPAObject litePaobject = (LiteYukonPAObject)devicesVector.get(i);
			int paobjectID = litePaobject.getLiteID();
			CreatePointList createPoint = (CreatePointList)createPointHashtable.get(new Integer(paobjectID));
			
			if( createPoint.voltageLP)
			{
				multi.addDBPersistent(
				    PointFactory.createDmdAccumPoint(
						"Voltage-LP",
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND,
						PointUnits.UOMID_VOLTS,
						multiplier) );
				CTILogger.info("Adding Voltage LP: PointId " + pointID + " to Device: " + litePaobject.getPaoName() );
				pointID++;
				addCount++;
			}
			
			if( createPoint.peakKw)
			{
				multi.addDBPersistent(
					PointFactory.createDmdAccumPoint(
						"Peak kW",
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_PEAK_KW_DEMAND,
						PointUnits.UOMID_KW,
						multiplier) );
				CTILogger.info("Adding Peak kW: PointId " + pointID  + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}

			if( createPoint.maxVolts)
			{
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Max Volts",
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_MAX_VOLT_DEMAND,
						PointUnits.UOMID_VOLTS,
						multiplier) );
				CTILogger.info("Adding Max Volts: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			
			if( createPoint.minVolts)
			{
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Min Volts",
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_MIN_VOLT_DEMAND,
						PointUnits.UOMID_VOLTS,
						multiplier) );
		
				CTILogger.info("Adding Min Volts: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			
			if( createPoint.kw)
			{
			    multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"kW",
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_KW_DEMAND,
						PointUnits.UOMID_KW,
						multiplier) );
				CTILogger.info("Adding kW: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}

			if( createPoint.voltage)
			{
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Voltage",
						new Integer(paobjectID),
						new Integer(pointID),
						PointTypes.PT_OFFSET_VOLTAGE_DEMAND,
						PointUnits.UOMID_VOLTS,
						multiplier) );
				CTILogger.info("Adding Voltage: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			
			if( createPoint.kWLP)
			{
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
					   "kW-LP",
					   new Integer(paobjectID),
					   new Integer(pointID),
					   PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
					   PointUnits.UOMID_KW,
					   multiplier) );
				CTILogger.info("Adding kW-LP: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			if( createPoint.kWh)
			{
				multi.addDBPersistent(
			        PointFactory.createPulseAccumPoint(
					   "kWh",
					   new Integer(paobjectID),
					   new Integer(pointID),
					   PointTypes.PT_OFFSET_TOTAL_KWH,
					   PointUnits.UOMID_KWH,
					   multiplier) );
			    CTILogger.info("Adding kWh: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
				pointID++;
				addCount++;
			}
			if( createPoint.frozenPeakDemand)
			{
				multi.addDBPersistent(
				        PointFactory.createDmdAccumPoint(
						   "Frozen Peak Demand",
						   new Integer(paobjectID),
						   new Integer(pointID),
						   PointTypes.PT_OFFSET_FROZEN_PEAK_DEMAND,
						   PointUnits.UOMID_KW,
						   multiplier) );
				    CTILogger.info("Adding Frozen Peak Demand: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
					pointID++;
					addCount++;
			}
			if( createPoint.frozenMaxVolts)
			{
				multi.addDBPersistent(
				        PointFactory.createDmdAccumPoint(
						   "Frozen Max Volts",
						   new Integer(paobjectID),
						   new Integer(pointID),
						   PointTypes.PT_OFFSET_FROZEN_MAX_VOLT,
						   PointUnits.UOMID_VOLTS,
						   multiplier) );
				    CTILogger.info("Adding Frozen Max Volts: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
					pointID++;
					addCount++;
			}
			if( createPoint.frozenMinVolts)
			{
				multi.addDBPersistent(
				        PointFactory.createDmdAccumPoint(
						   "Frozen Min Volts",
						   new Integer(paobjectID),
						   new Integer(pointID),
						   PointTypes.PT_OFFSET_FROZEN_MIN_VOLT,
						   PointUnits.UOMID_VOLTS,
						   multiplier) );
				    CTILogger.info("Adding Frozen Min Volts: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
					pointID++;
					addCount++;
			}
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
		{
			CTILogger.info(addCount + " MCT410 Points were processed and inserted Successfully");
		}
		else
			CTILogger.info("MCT410 Points failed insertion");
			
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
		//All MCT410s 
		return DeviceTypesFuncs.isMCT410(litePaobject_.getType());
	}

	/**
	 * Returns true if the pointOffset and Pointype combination already exists.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param pointOffset_ int
	 * @param pointType_ int
	 * @return boolean
	 */
	public boolean isPointCreated( LitePoint lp)
	{
		if( lp.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT )
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).voltageLP = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_PEAK_KW_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).peakKw = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_MAX_VOLT_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).maxVolts = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_MIN_VOLT_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).minVolts = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_KW_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).kw = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_VOLTAGE_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).voltage = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).kWLP = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_TOTAL_KWH && lp.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).kWh = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_FROZEN_PEAK_DEMAND && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).frozenPeakDemand = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_FROZEN_MAX_VOLT && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).frozenMaxVolts = false;
		else if( lp.getPointOffset() == PointTypes.PT_OFFSET_FROZEN_MIN_VOLT && lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
			((CreatePointList)createPointHashtable.get(new Integer(lp.getPaobjectID()))).frozenMinVolts = false;
		
		return false;
	}	
	
	/**
	 * Returns a Vector of LiteYukonPaobjects that need a Power Fail point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	protected void getMCT410DeviceVector(java.util.Vector deviceVector)
	{
		DefaultDatabaseCache  cache = DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			java.util.List mcts = cache.getAllMCTs();
			java.util.Collections.sort(mcts, LiteComparators.liteYukonPAObjectIDComparator);
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, LiteComparators.litePointDeviceIDComparator);

			createPointHashtable = new java.util.Hashtable(mcts.size());
			
			for (int i = 0; i < mcts.size(); i++)
			{
				LiteYukonPAObject litePaobject = ((LiteYukonPAObject)mcts.get(i));
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
