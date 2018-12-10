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
 * Creates all Frozen MCT10 points (frozen peak demand, frozen min volts, frozen max volts for every MCT410 type in the database.
 */
public class MCT410FrozenPointCreate extends PointCreate
{
	java.util.Hashtable<Integer, CreatePointList> createPointHashtable = null;
	private class CreatePointList
	{
		boolean frozenPeakKw = true;  //Demand Acc offset 21
		boolean frozenMaxVolts = true;    //Demand Acc offset 24
		boolean frozenMinVolts = true;    //Demand Acc offset 25
	}

	/**
	 * Constructor for LoadGroup_ControlPointCreate.
	 */
	public MCT410FrozenPointCreate()
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
		CTILogger.info("Starting MCT410 Frozen Point creation process...");

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
			
			if( createPoint.frozenPeakKw) {
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
					PointFactory.createDmdAccumPoint(
						"Frozen Peak Demand",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_FROZEN_PEAK_DEMAND,
						UnitOfMeasure.KW.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
                        PointArchiveType.NONE,
                        PointArchiveInterval.ZERO) );
				CTILogger.info("Adding Frozen Peak Demand: PointId " + pointID  + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
			if( createPoint.frozenMaxVolts) {
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Frozen Max Volts",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_FROZEN_MAX_VOLT,
						UnitOfMeasure.VOLTS.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
                        PointArchiveType.NONE,
                        PointArchiveInterval.ZERO) );
				CTILogger.info("Adding Frozen Max Volts: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
			
			if( createPoint.frozenMinVolts)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(
			        PointFactory.createDmdAccumPoint(
						"Frozen Min Volts",
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_FROZEN_MIN_VOLT,
						UnitOfMeasure.VOLTS.getId(),
						multiplier,
						StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
                        PointArchiveType.NONE,
                        PointArchiveInterval.ZERO) );
		
				CTILogger.info("Adding Frozen Min Volts: PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
		}
	
		boolean success = writeToSQLDatabase(multi);
	
		if( success )
			CTILogger.info(addCount + " MCT410 Frozen Points were processed and inserted Successfully");
		else
			CTILogger.info("MCT410 Frozen Points failed insertion");
			
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
		if( lp.getPointOffset() == PointOffsets.PT_OFFSET_FROZEN_PEAK_DEMAND && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).frozenPeakKw = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_FROZEN_MAX_VOLT && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).frozenMaxVolts = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_FROZEN_MIN_VOLT && lp.getPointTypeEnum() == PointType.DemandAccumulator)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).frozenMinVolts = false;
		
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
