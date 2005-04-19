/*
 * Created on Dec 7, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.point;

import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCT310;
import com.cannontech.database.data.device.MCT310ID;
import com.cannontech.database.data.device.MCT310IDL;
import com.cannontech.database.data.device.MCT310IL;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.MCT470;
import com.cannontech.database.data.device.MCT410_KWH_Only;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PointUtil {
	
	public static DBPersistent generatePointsForMCT(Object val) {
		if( val instanceof MCT310 
			 || val instanceof MCT310IL 
			 || val instanceof MCT310ID
			 || val instanceof MCT310IDL
			 || val instanceof MCT410IL
			 || val instanceof MCT470
			 || val instanceof MCT410_KWH_Only )
		{
			if(val instanceof MCT400SeriesBase)
			{
				//sloppy way of setting a 400 series load profile default...
				//improve this later
				((MCT400SeriesBase)val).getDeviceLoadProfile().setLoadProfileDemandRate(new Integer(3600));	
			}
			
			com.cannontech.database.data.multi.MultiDBPersistent newVal = new com.cannontech.database.data.multi.MultiDBPersistent();
			((DeviceBase) val).setDeviceID(com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID());

			newVal.getDBPersistentVector().add(val);

			//accumulator point is automatically added
			com.cannontech.database.data.point.PointBase newPoint = com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT);

			int pointID = com.cannontech.database.cache.functions.PointFuncs.getMaxPointID();

			double multiplier = 0.01;
			//multiplier is 0.1 for 410LE, 0.01 for all older MCTs
			if(val instanceof MCT410_KWH_Only || val instanceof MCT410IL)
				multiplier = 0.1;
			
			//always create the PulseAccum point
			newVal.getDBPersistentVector().add( 
				PointFactory.createPulseAccumPoint(
				   "kWh",
				   ((DeviceBase) val).getDevice().getDeviceID(),
				   new Integer(++pointID),
				   PointTypes.PT_OFFSET_TOTAL_KWH,
				   com.cannontech.database.data.point.PointUnits.UOMID_KWH,
				   multiplier) );

			//only certain devices get this DemandAccum point auto created
			if( val instanceof MCT310IL
				 || val instanceof MCT310IDL
				 || val instanceof MCT410IL)
			{
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
					   "kW-LP",
					   ((DeviceBase) val).getDevice().getDeviceID(),
					   new Integer(++pointID),
					   PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
					   com.cannontech.database.data.point.PointUnits.UOMID_KW,
					   multiplier) );
			}
			
			//only the 410 gets all these points auto-created
			if( val instanceof MCT410IL ) 
			{
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Voltage-LP",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						multiplier) );
					   	
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Peak kW",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_PEAK_KW_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_KW,
						multiplier) );
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Max Volts",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_MAX_VOLT_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						multiplier) );
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Min Volts",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_MIN_VOLT_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						multiplier) );
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Frozen Peak Demand",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_FROZEN_PEAK_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_KW,
						multiplier) );			
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Frozen Max Volts",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_FROZEN_MAX_VOLT,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						multiplier) );
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Frozen Min Volts",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_FROZEN_MIN_VOLT,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						multiplier) );
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"kW",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_KW_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_KW,
						multiplier) );
						
				newVal.getDBPersistentVector().add( 
					PointFactory.createDmdAccumPoint(
						"Voltage",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(++pointID),
						PointTypes.PT_OFFSET_VOLTAGE_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						multiplier) );
			}

			if (val instanceof MCT310ID
				 || val instanceof MCT310IDL ) 
			{
				 	
				//an automatic status point is created for certain devices
				//set default for point tables
				PointBase newPoint2 = PointFactory.createNewPoint(
				   new Integer(++pointID),
						PointTypes.STATUS_POINT,
						"DISCONNECT STATUS",
						((DeviceBase) val).getDevice().getDeviceID(),
						new Integer(PointTypes.PT_OFFSET_TOTAL_KWH));

				newPoint2.getPoint().setStateGroupID(
						new Integer(StateGroupUtils.STATEGROUP_THREE_STATE_STATUS) );

				((StatusPoint) newPoint2).setPointStatus(
								new PointStatus(newPoint2.getPoint().getPointID()) );

				newVal.getDBPersistentVector().add(newPoint2);
			}

			//returns newVal, a vector with MCT310 or MCT310IL & accumulator point & status point if of type MCTID
			return newVal;
		}
		
		return null;
	}

}
