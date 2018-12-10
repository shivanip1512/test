package com.cannontech.datagenerator.point;

import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
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
	java.util.Hashtable<Integer, CreatePointList> createPointHashtable = null;
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
		boolean blinkCount = true;		//offset 20 (pulseAcc)
		boolean outageLog = true;		//offset 100 (analog)
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
	@Override
    public boolean create()
	{
		//Points are going to be added to every MCT410.
		CTILogger.info("Starting MCT410 Point creation process...");

		Vector<LiteYukonPAObject> devicesVector = getMCT410DeviceVector();

		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		double multiplier = 0.1;
		int addCount = 0;
		PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
		
		for (LiteYukonPAObject litePaobject : devicesVector) {

			int paobjectID = litePaobject.getLiteID();
			CreatePointList createPoint = createPointHashtable.get(new Integer(paobjectID));
			
			if( createPoint.voltageLP)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
				    PointFactory.createDmdAccumPoint(
						"Voltage Profile",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND,
						UnitOfMeasure.VOLTS.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) );
				CTILogger.info("Adding Voltage LP: PointId " + pointID + " to Device: " + litePaobject.getPaoName() );
				addCount++;
			}
			
			if( createPoint.peakKw)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
					PointFactory.createDmdAccumPoint(
						"Peak kW",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_PEAK_KW_DEMAND,
						UnitOfMeasure.KW.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) );
				CTILogger.info("Adding Peak kW: PointId " + pointID  + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}

			if( createPoint.maxVolts)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Max Volts",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_MAX_VOLT_DEMAND,
						UnitOfMeasure.VOLTS.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) );
				CTILogger.info("Adding Max Volts: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
			
			if( createPoint.minVolts)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Min Volts",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_MIN_VOLT_DEMAND,
						UnitOfMeasure.VOLTS.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) );
		
				CTILogger.info("Adding Min Volts: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
			
			if( createPoint.kw)
			{
			    int pointID = pointDao.getNextPointId();
			    multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"kW",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_KW_DEMAND,
						UnitOfMeasure.KW.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) );
				CTILogger.info("Adding kW: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}

			if( createPoint.voltage)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Voltage",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_VOLTAGE_DEMAND,
						UnitOfMeasure.VOLTS.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) );
				CTILogger.info("Adding Voltage: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
				addCount++;
			}
			
			if( createPoint.kWLP)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
					   "kW-LP",
					   new Integer(paobjectID),
					   new Integer(pointID),
					   PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND,
					   UnitOfMeasure.KW.getId(),
					   multiplier,
					   StateGroupUtils.STATEGROUP_ANALOG,
                       PointUnit.DEFAULT_DECIMAL_PLACES,
					   PointArchiveType.NONE,
					   PointArchiveInterval.ZERO) );
				CTILogger.info("Adding kW-LP: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
				addCount++;
			}
			if( createPoint.kWh)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createPulseAccumPoint(
					   "kWh",
					   new Integer(paobjectID),
					   new Integer(pointID),
					   PointOffsets.PT_OFFSET_TOTAL_KWH,
					   UnitOfMeasure.KWH.getId(),
					   multiplier,
					   StateGroupUtils.STATEGROUP_ANALOG,
                       PointUnit.DEFAULT_DECIMAL_PLACES,
                       PointArchiveType.NONE,
                       PointArchiveInterval.ZERO) );
			    CTILogger.info("Adding kWh: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
				addCount++;
			}

			if( createPoint.blinkCount)
			{
			    int pointID = pointDao.getNextPointId();
			    multi.addDBPersistent(
				        PointFactory.createPulseAccumPoint(
						   "Blink Count",
						   new Integer(paobjectID),
						   new Integer(pointID),
						   PointOffsets.PT_OFFSET_BLINK_COUNT,
						   UnitOfMeasure.COUNTS.getId(),
						   1.0,
						   StateGroupUtils.STATEGROUP_ANALOG,
                           PointUnit.DEFAULT_DECIMAL_PLACES,
						   PointArchiveType.NONE,
						   PointArchiveInterval.ZERO) );
				    CTILogger.info("Adding Blink Count: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
					addCount++;
			}
			if( createPoint.outageLog)
			{
				int pointID = pointDao.getNextPointId();
			    multi.addDBPersistent(
				        PointFactory.createAnalogPoint(
				        	"Outages", 
							new Integer(paobjectID), 
                            new Integer(pointID), 
                            PointOffsets.PT_OFFSET_OUTAGE, 
                            UnitOfMeasure.SECONDS.getId(),
    						StateGroupUtils.STATEGROUP_ANALOG) );
				    CTILogger.info("Adding Outage Log: PointId " + pointID + " to Device ID" + litePaobject.getPaoName());
					addCount++;
			}
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " MCT410 Points were processed and inserted Successfully");
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
	@Override
    public boolean isDeviceValid( LiteYukonPAObject litePaobject_ )
	{
		//All MCT410s 
		return DeviceTypesFuncs.isMCT410(litePaobject_.getPaoType());
	}

	/**
	 * Returns true if the pointOffset and Pointype combination already exists.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param pointOffset_ int
	 * @param pointType_ int
	 * @return boolean
	 */
	@Override
    public boolean isPointCreated( LitePoint lp)
	{
		if( lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).voltageLP = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_PEAK_KW_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).peakKw = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_MAX_VOLT_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).maxVolts = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_MIN_VOLT_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).minVolts = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_KW_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).kw = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_VOLTAGE_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).voltage = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_LPROFILE_KW_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).kWLP = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_TOTAL_KWH && lp.getPointTypeEnum() == PointType.PulseAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).kWh = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_BLINK_COUNT && lp.getPointTypeEnum() == PointType.PulseAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).blinkCount = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_OUTAGE && lp.getPointTypeEnum() == PointType.Analog)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).outageLog = false;
		
		return false;
	}	
	
	/**
	 * Returns a Vector of LiteYukonPaobjects that need a Power Fail point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	protected Vector<LiteYukonPAObject> getMCT410DeviceVector()
	{
		Vector<LiteYukonPAObject> paobjects = new Vector<LiteYukonPAObject>();
		
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			List<LiteYukonPAObject> mcts = cache.getAllMCTs();
            PointDao pointDao = YukonSpringHook.getBean(PointDao.class);

			createPointHashtable = new java.util.Hashtable<Integer, CreatePointList>(mcts.size());

			for (LiteYukonPAObject litePaobject: mcts) {

				if( isDeviceValid( litePaobject ) )
				{
					int paobjectID = litePaobject.getLiteID();
										
					CreatePointList item = new CreatePointList();
					createPointHashtable.put(new Integer(paobjectID), item);
					
                    boolean foundPoint = false;
                    List<LitePoint> devicePoints = pointDao.getLitePointsByPaObjectId(paobjectID);
                    for (LitePoint point : devicePoints) {
                        if(isPointCreated(point)) {
                            foundPoint = true;
                        }
                    }
                    //makes a list of points devices to add control history points to
                    if(!foundPoint) {
                        paobjects.add(litePaobject);
                    }
				}
			}
			CTILogger.info(paobjects.size() + " Total Devices needing points added.");
		}
		return paobjects;
	}
}