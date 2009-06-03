/*
 * Created on Dec 7, 2004 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.point;

import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.TypeBase;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author yao To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PointUtil {

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

	/**
	 * Helper method to change pointBase to the newPointTemplate type.
	 * If the pointBase type and the newPointTemplate type are the same, pointBase is returned unchanged.
	 * Else, the pointBase object is returned with the newPointTemplate data set.
	 * @param pointBase
	 * @param newPointTemplate
	 * @return
	 * @throws TransactionException 
	 */
	public static PointBase changePointType(PointBase pointBase, PointTemplate newPointTemplate) throws TransactionException {
	
		int oldType = PointTypes.getType(pointBase.getPoint().getPointType());
		
		// actual changing of point type, delete the old point and re-add it with new type
		if (oldType != newPointTemplate.getType()) {
			
			pointBase.getPoint().setPointType(PointTypes.getType(newPointTemplate.getType()));
			PointAlarming savePointAlarming = pointBase.getPointAlarming();
			Point savePoint = pointBase.getPoint();
		
			// Delete partial point data so type can be changed.
			Transaction<PointBase> t = Transaction.createTransaction(
					Transaction.DELETE_PARTIAL,
					pointBase);
			pointBase = t.execute();
			
			
			//Create a new point for new point type
			pointBase = PointFactory.createPoint(newPointTemplate.getType());
		
			//Set old point data that can transfer over to new point
			pointBase.setPoint(savePoint);
			pointBase.setPointAlarming(savePointAlarming);
			pointBase.setPointID(savePoint.getPointID());
		
			//Set new point defaults from tempalte
			pointBase.getPoint().setPointType(PointTypes.getType(newPointTemplate.getType()));
			
			// Add the updated (partial) point information.
		    t = Transaction.createTransaction(
					Transaction.ADD_PARTIAL,
					pointBase);

			pointBase = t.execute();
		}
	
		// update additional point data (multipier/uom, etc)
		boolean hasChange = false;
		if (pointBase instanceof AnalogPoint) {
        	
			AnalogPoint analogPoint = (AnalogPoint)pointBase;
			
			hasChange = analogPoint.getPoint().getPointOffset() != newPointTemplate.getOffset()
						|| analogPoint.getPointAnalog().getMultiplier() != newPointTemplate.getMultiplier()
						|| analogPoint.getPointUnit().getUomID() != newPointTemplate.getUnitOfMeasure()
						|| analogPoint.getPoint().getStateGroupID() != newPointTemplate.getStateGroupId();
        	
        	analogPoint.getPoint().setPointOffset(newPointTemplate.getOffset());
        	analogPoint.getPointAnalog().setMultiplier(newPointTemplate.getMultiplier());
        	analogPoint.getPointUnit().setUomID(newPointTemplate.getUnitOfMeasure());
        	analogPoint.getPoint().setStateGroupID(newPointTemplate.getStateGroupId());
        	
        } else if (pointBase instanceof StatusPoint) {
        	
        	StatusPoint statusPoint = (StatusPoint)pointBase;
        	
        	hasChange = statusPoint.getPoint().getPointOffset() != newPointTemplate.getOffset()
						|| statusPoint.getPoint().getStateGroupID() != newPointTemplate.getStateGroupId();
        	
        	statusPoint.getPoint().setPointOffset(newPointTemplate.getOffset());
        	statusPoint.getPoint().setStateGroupID(newPointTemplate.getStateGroupId());
        	
        } else if (pointBase instanceof AccumulatorPoint) {
        	
        	AccumulatorPoint accumulatorPoint = (AccumulatorPoint)pointBase;
        	
        	hasChange = accumulatorPoint.getPoint().getPointOffset() != newPointTemplate.getOffset()
						|| accumulatorPoint.getPointAccumulator().getMultiplier() != newPointTemplate.getMultiplier()
						|| accumulatorPoint.getPointUnit().getUomID() != newPointTemplate.getUnitOfMeasure()
						|| accumulatorPoint.getPoint().getStateGroupID() != newPointTemplate.getStateGroupId();
        	
        	accumulatorPoint.getPoint().setPointOffset(newPointTemplate.getOffset());
        	accumulatorPoint.getPointAccumulator().setMultiplier(newPointTemplate.getMultiplier());
        	accumulatorPoint.getPointUnit().setUomID(newPointTemplate.getUnitOfMeasure());
        	accumulatorPoint.getPoint().setStateGroupID(newPointTemplate.getStateGroupId());
        }
		
		// update
		if (hasChange) {
		    Transaction<PointBase> t = Transaction.createTransaction(Transaction.UPDATE, pointBase);
	        pointBase = t.execute();
		}
		
        return pointBase;
	}
}