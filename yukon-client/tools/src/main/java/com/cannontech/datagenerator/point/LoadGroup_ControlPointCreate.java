package com.cannontech.datagenerator.point;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
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
	Hashtable<Integer, CreatePointList> createPointHashtable = null;
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
	 * @see com.cannontech.datagenerator.point.PointCreate#create()
	 */
	@Override
    public boolean create() {
		//Points are going to be added to every load group.
		CTILogger.info("Starting Load Group Point creation process...");

		Vector<LiteYukonPAObject> devicesVector = getLoadGroupVector();

		//create an object to hold all of our DBPersistant objects
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// if this is not set to false it will create its own PointIDs
		multi.setCreateNewPAOIDs( false );
	
		int addCount = 0;
		PointDao pointDao = YukonSpringHook.getBean(PointDao.class);
		
		for (LiteYukonPAObject litePaobject: devicesVector) {
	
			int paobjectID = litePaobject.getLiteID();
			CreatePointList createPoint = createPointHashtable.get(new Integer(paobjectID));
			
			if( createPoint.dailyhistory)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(PointFactory.createAnalogPoint("DAILY HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_DAILY_HISTORY,
						UnitOfMeasure.COUNTS.getId(), 
						StateGroupUtils.STATEGROUP_ANALOG));
				CTILogger.info("Adding DAILY_HISTROY PointId " + pointID + " to Device: " + litePaobject.getPaoName() );
				addCount++;
			}
			
			if( createPoint.monthlyHistory)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(PointFactory.createAnalogPoint("MONTHLY HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_MONTHLY_HISTORY,
						UnitOfMeasure.COUNTS.getId(),
						StateGroupUtils.STATEGROUP_ANALOG));
				CTILogger.info("Adding MONTHLY_HISTROY PointId " + pointID  + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}

			if( createPoint.seasonalHistory)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(PointFactory.createAnalogPoint("SEASON HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_SEASONAL_HISTORY,
						UnitOfMeasure.COUNTS.getId(), 
						StateGroupUtils.STATEGROUP_ANALOG));
				CTILogger.info("Adding SEASONAL_HISTROY PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
			
			if( createPoint.annualHistory)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(PointFactory.createAnalogPoint("ANNUAL HISTORY", 
						new Integer(paobjectID),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_ANNUAL_HISTORY,
						UnitOfMeasure.COUNTS.getId(),
						StateGroupUtils.STATEGROUP_ANALOG));
				CTILogger.info("Adding ANNUAL_HISTROY PointId " + pointID + " to Device: " + litePaobject.getPaoName());
				addCount++;
			}
			
			if( createPoint.controlStatus)
			{
			    int pointID = pointDao.getNextPointId();
				PointBase pointBase = PointFactory.createNewPoint(
						new Integer(pointID),
						PointType.Status.getPointTypeId(),
						"CONTROL STATUS",
						new Integer(paobjectID),
						new Integer(0) );
				pointBase.getPoint().setStateGroupID(new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));
				((StatusPoint)pointBase).getPointStatusControl().setControlOffset(1);
				((StatusPoint)pointBase).getPointStatusControl().setControlType(StatusControlType.NORMAL.getControlName());
				multi.addDBPersistent(pointBase);
				CTILogger.info("Adding CONTROL_STATUS PointId " + pointID + " to Device: " + litePaobject.getPaoName());
	
				addCount++;
			}

			if( createPoint.controlCountdown)
			{
			    int pointID = pointDao.getNextPointId();
				multi.addDBPersistent(PointFactory.createAnalogPoint("CONTROL COUNTDOWN", 
						new Integer(litePaobject.getLiteID()),
						new Integer(pointID),
						PointOffsets.PT_OFFSET_CONTROL_COUNTDOWN,
						UnitOfMeasure.COUNTS.getId(),
						StateGroupUtils.STATEGROUP_ANALOG));
				CTILogger.info("Adding CONTROL_COUNTDOWN PointId " + pointID + " to Device ID" + litePaobject.getYukonID());
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
	@Override
    public boolean isDeviceValid( LiteYukonPAObject litePaobject_ )
	{
		//All Groups except Macro groups //
		return
			litePaobject_.getPaoType().isLoadGroup()
			&& litePaobject_.getPaoType() != PaoType.MACRO_GROUP;
	}

	/**
	 * Returns true if the pointOffset and Pointype combination already exists.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @param pointOffset_ int
	 * @param pointType_ int
	 * @return boolean
	 */
	@Override
    public boolean isPointCreated( com.cannontech.database.data.lite.LitePoint lp)
	{
		if( lp.getPointOffset() == PointOffsets.PT_OFFSET_DAILY_HISTORY && lp.getPointTypeEnum() == PointType.Analog)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).dailyhistory = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_MONTHLY_HISTORY && lp.getPointTypeEnum() == PointType.Analog)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).monthlyHistory = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_SEASONAL_HISTORY && lp.getPointTypeEnum() == PointType.Analog)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).seasonalHistory = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_ANNUAL_HISTORY && lp.getPointTypeEnum() == PointType.Analog)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).annualHistory = false;
		else if( lp.getPointOffset() == 0 && lp.getPointTypeEnum() == PointType.Status)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).controlStatus = false;
		else if( lp.getPointOffset() == PointOffsets.PT_OFFSET_CONTROL_COUNTDOWN && lp.getPointTypeEnum() == PointType.Analog)
			createPointHashtable.get(new Integer(lp.getPaobjectID())).controlCountdown = false;
			
		return false;
	}	
	
	/**
	 * Returns a Vector of LiteYukonPaobjects that need a Power Fail point added to them.
	 * Creation date: (4/22/2002 4:11:23 PM)
	 * @return java.util.Vector
	 */
	protected Vector<LiteYukonPAObject> getLoadGroupVector()
	{
		Vector<LiteYukonPAObject> loadGroups = new Vector<LiteYukonPAObject>();
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
		synchronized (cache)
		{
			List<LiteYukonPAObject> groups = cache.getAllLoadManagement();
            PointDao pointDao = YukonSpringHook.getBean(PointDao.class);

			createPointHashtable = new Hashtable<Integer, CreatePointList>(groups.size());
			
			for (LiteYukonPAObject litePaobject: groups) {

				if( isDeviceValid( litePaobject ) ) {
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
                        loadGroups.add(litePaobject);
                    }
				}
			}
			CTILogger.info(loadGroups.size() + " Total Devices needing points added.");
		}
		return loadGroups;
	}
}
