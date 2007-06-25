/*
 * Created on Dec 7, 2004 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.point;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCT310;
import com.cannontech.database.data.device.MCT310ID;
import com.cannontech.database.data.device.MCT310IDL;
import com.cannontech.database.data.device.MCT310IL;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCT410CL;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.MCT410_KWH_Only;
import com.cannontech.database.data.device.MCT430A;
import com.cannontech.database.data.device.MCT430S4;
import com.cannontech.database.data.device.MCT470;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.TypeBase;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.PointStatus;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author yao To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PointUtil {

    public static DBPersistent generatePointsForMCT(Object val) {
        if (val instanceof MCT310 || val instanceof MCT310IL || val instanceof MCT310ID || val instanceof MCT310IDL || val instanceof MCT410IL || val instanceof MCT470 || val instanceof MCT410_KWH_Only || val instanceof MCT410CL || val instanceof MCT430A || val instanceof MCT430S4) {
            if (val instanceof MCT400SeriesBase) {
                // sloppy way of setting a 400 series load profile default...
                // improve this later
                ((MCT400SeriesBase) val).getDeviceLoadProfile()
                                        .setLoadProfileDemandRate(new Integer(3600));
            }

            com.cannontech.database.data.multi.MultiDBPersistent newVal = new com.cannontech.database.data.multi.MultiDBPersistent();
            PaoDao paoDao = DaoFactory.getPaoDao();
            ((DeviceBase) val).setDeviceID(paoDao.getNextPaoId());

            newVal.getDBPersistentVector().add(val);

            // accumulator point is automatically added

            PointDao pointDao = DaoFactory.getPointDao();
            //int pointID = DaoFactory.getPointDao().getNextPointId();

            double multiplier = 0.01;
            double blinkCountMult = 1.0;
            // multiplier is 0.1 for 410LE, 0.01 for all older MCTs, 1.0 for 470's
            if (val instanceof MCT410_KWH_Only || val instanceof MCT410IL || val instanceof MCT410CL)
            {
                multiplier = 0.1;
            }else if( val instanceof MCT470 )
            {
                multiplier = 1.0;
            }

            Integer deviceID = ((DeviceBase) val).getDevice().getDeviceID();
            if ((val instanceof MCT400SeriesBase) && deviceID != null)
            {
                PointBase outageAnalogPoint = PointFactory.createAnalogPoint("Outages", 
                                                                                  deviceID, 
                                                                                  pointDao.getNextPointId(), 
                                                                                  IPointOffsets.PT_OFFSET_OUTAGE, 
                                                                                  PointUnits.UOMID_SECONDS);
                newVal.getDBPersistentVector().add(outageAnalogPoint);
            }
            // always create the PulseAccum point(except for 430s)
            if (val instanceof MCT310 
                    || val instanceof MCT310IL 
                    || val instanceof MCT310ID 
                    || val instanceof MCT310IDL 
                    || val instanceof MCT410IL 
                    || val instanceof MCT470 
                    || val instanceof MCT410_KWH_Only 
                    || val instanceof MCT410CL)
            {
                newVal.getDBPersistentVector()
                      .add(PointFactory.createPulseAccumPoint("kWh",
                      deviceID,
                      pointDao.getNextPointId(),
                      PointTypes.PT_OFFSET_TOTAL_KWH,
                      com.cannontech.database.data.point.PointUnits.UOMID_KWH,
                      multiplier));
    
                newVal.getDBPersistentVector()
                      .add(PointFactory.createPulseAccumPoint("Blink Count",
                      deviceID,
                      pointDao.getNextPointId(),
                      PointTypes.PT_OFFSET_BLINK_COUNT,
                      com.cannontech.database.data.point.PointUnits.UOMID_COUNTS,
                      blinkCountMult));
            }
            
            // 470s need this demand accum but not the others
            if (val instanceof MCT470) {
                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("kW",
                    deviceID,
                    pointDao.getNextPointId(),                    
                    PointTypes.PT_OFFSET_KW_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_KW,
                    multiplier));
            }

            // only certain devices get this DemandAccum point auto created
            if (val instanceof MCT310IL || val instanceof MCT310IDL || val instanceof MCT410IL || val instanceof MCT410CL || val instanceof MCT470) {
                newVal.getDBPersistentVector()
                      .add(PointFactory.createDmdAccumPoint("kW-LP",
                    deviceID,
                     pointDao.getNextPointId(),                                                          
                    PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_KW,
                    multiplier));
            }

            // only the 410 gets all these points auto-created (demand accumulators)
            if (val instanceof MCT410IL || val instanceof MCT410CL) 
            {
                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Voltage-LP",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Peak kW",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_PEAK_KW_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_KW,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Max Volts",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_MAX_VOLT_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Min Volts",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_MIN_VOLT_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Frozen Peak Demand",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_FROZEN_PEAK_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_KW,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Frozen Max Volts",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_FROZEN_MAX_VOLT,
                    com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Frozen Min Volts",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_FROZEN_MIN_VOLT,
                    com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("kW",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_KW_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_KW,
                    multiplier));

                newVal.getDBPersistentVector()
                    .add(PointFactory.createDmdAccumPoint("Voltage",
                    deviceID,
                    pointDao.getNextPointId(),                                                                                                
                    PointTypes.PT_OFFSET_VOLTAGE_DEMAND,
                    com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
                    multiplier));
            }
            
            // analog points for 470s and 430s
            if (val instanceof MCT470 || val instanceof MCT430A || val instanceof MCT430S4) 
            {
                newVal.getDBPersistentVector()
                    .add(PointFactory.createAnalogPoint("Total KWh",
                    deviceID,
                    pointDao.getNextPointId(),
                    PointTypes.PT_OFFSET_TOTAL_KWH,
                    PointUnits.UOMID_KWH,
                    .0001));


                newVal.getDBPersistentVector()
                    .add(PointFactory.createAnalogPoint("Peak KW (Rate A KW)",
                    deviceID,
                    pointDao.getNextPointId(),                    
                    2,
                    PointUnits.UOMID_KW,
                    .01));


                newVal.getDBPersistentVector()
                    .add(PointFactory.createAnalogPoint("Rate A KWh",
                    deviceID,
                    pointDao.getNextPointId(),                                        
                    3,
                    PointUnits.UOMID_KWH,
                    .0001));


                newVal.getDBPersistentVector()
                    .add(PointFactory.createAnalogPoint("Last Interval KW",
                    deviceID,
                    pointDao.getNextPointId(),
                    10,
                    PointUnits.UOMID_KW,
                    .01));

            }

            if (val instanceof MCT310ID || val instanceof MCT310IDL || val instanceof MCT410IL) {
                // an automatic status point is created for certain devices
                // set default for point tables
                PointBase newPoint2 = PointFactory.createNewPoint(pointDao.getNextPointId(),
                      PointTypes.STATUS_POINT,
                      "DISCONNECT STATUS",
                      deviceID,
                      new Integer(PointTypes.PT_OFFSET_TOTAL_KWH));

                if (val instanceof MCT410IL)
                    newPoint2.getPoint()
                             .setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_MCT410DISC));
                else
                    newPoint2.getPoint()
                             .setStateGroupID(new Integer(StateGroupUtils.STATEGROUP_THREE_STATE_STATUS));

                ((StatusPoint) newPoint2).setPointStatus(new PointStatus(newPoint2.getPoint()
                                                                                  .getPointID()));

                newVal.getDBPersistentVector().add(newPoint2);
            }

            // returns newVal, a vector with MCT310 or MCT310IL & accumulator
            // point & status point if of type MCTID
            return newVal;
        }

        return null;
    }

    
    public static PointBase createPoint(int type, String name, Integer paoId) throws TransactionException {
       MultiDBPersistent dbPersistentVector = new MultiDBPersistent(); 
       PointBase point = new PointBase();
       int validPointOffset = PointOffsetUtils.getValidPointOffset(paoId, type);
	switch (type){
       case PointTypes.ANALOG_POINT:
           point =  PointFactory.createPoint(PointTypes.ANALOG_POINT);
           point =  PointFactory.createAnalogPoint(name,
                                                                      paoId,
                                                                      point.getPoint()
                                                                                 .getPointID(),
                                                                      validPointOffset,
                                     
                                                                      PointUnits.UOMID_VOLTS,
                                                                      1.0);
           dbPersistentVector.getDBPersistentVector().add(point);
           break;
       case PointTypes.STATUS_POINT:
           point = PointFactory.createBankStatusPt(paoId);
           point.getPoint().setPointName(name);
           point.getPoint().setPointOffset( new Integer ( validPointOffset) );
           dbPersistentVector.getDBPersistentVector().add(point);
           break;
           
       case PointTypes.DEMAND_ACCUMULATOR_POINT:
           point = PointFactory.createPoint(PointTypes.DEMAND_ACCUMULATOR_POINT);
           point = PointFactory.createDmdAccumPoint(name, 
                                                    paoId, 
                                                                      point.getPoint().getPointID(),
                                                                      TypeBase.POINT_OFFSET, 
                                                                      PointUnits.UOMID_UNDEF, 
                                                                      0.1);

           
           
           
           dbPersistentVector.getDBPersistentVector().add(point);
           break;
           
       case PointTypes.CALCULATED_POINT:
           point = PointFactory.createCalculatedPoint(paoId, name);
           dbPersistentVector.getDBPersistentVector().add(point);
           break;
           
       case PointTypes.CALCULATED_STATUS_POINT:
           point = PointFactory.createCalcStatusPoint(paoId, name);
           dbPersistentVector.getDBPersistentVector().add(point); 
           break;
           
        default: 
            throw new Error("PointUtil::createPoint - Unrecognized point type");
       }
       
       
       PointUtil.insertIntoDB(dbPersistentVector);
       
       return point;
    }

    public static void insertIntoDB(DBPersistent pointVector) throws TransactionException {
        if (pointVector != null) {
            try {
                pointVector = Transaction.createTransaction(Transaction.INSERT,
                                                                           pointVector)
                                                        .execute();
            
                DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance()
                                                             .createDBChangeMessages((CTIDbChange) pointVector,
                                                                                     DBChangeMsg.CHANGE_TYPE_ADD);
                //make sure we update the cache
                for (int i = 0; i < dbChange.length; i++)
                    DefaultDatabaseCache.getInstance().handleDBChangeMessage( dbChange[i] );
                
            } catch (TransactionException e) {
                
                throw e;
            }
        }
        else
        {
            throw new  TransactionException ("Trying to INSERT empty - insertIntoDB - PointUtil");
        
        }
    }

/** used for cap control
 * used to determine which point types (Analog, DemandAccum, Status, PulseAccum, e.g) sub/feeder setup 
 * need to be scanable 
 * @param pointType
 * @return true if point type is scanable
 */
	public static boolean isScanablePointType(int pointType) {
		for (int i = 0; i < PointTypes.SCANABLE_POINT_TYPES.length; i++) {
			if (PointTypes.SCANABLE_POINT_TYPES[i] == pointType)
				return true;
		}
		return false;
	}
    
        
        
}

