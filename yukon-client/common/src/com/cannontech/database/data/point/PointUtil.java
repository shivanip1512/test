/*
 * Created on Dec 7, 2004 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.point;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.CalcPointComponent;
import com.cannontech.common.pao.definition.model.CalcPointInfo;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.pao.TypeBase;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

/**
 * @author yao To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PointUtil {
    
    private static PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    private static PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);

    public static PointBase createPoint(int type, String name, Integer paoId, boolean disabled) 
        throws TransactionException {
       MultiDBPersistent dbPersistentVector = new MultiDBPersistent(); 
       PointBase point = new PointBase();
       
       // Service flag of 'Y' means disabled, 'N' means enabled.
       char disabledChar = disabled ? CtiUtilities.trueChar : CtiUtilities.falseChar;
       int validPointOffset = PointOffsetUtils.getValidPointOffset(paoId, type);

	switch (type){
       case PointTypes.ANALOG_POINT:
           point =  PointFactory.createAnalogPoint(name,
                                                   paoId,
                                                   point.getPoint().getPointID(),
                                                   validPointOffset,
                                                   PointUnits.UOMID_VOLTS,
                                                   1.0, 
                                                   StateGroupUtils.STATEGROUP_ANALOG,
                                                   PointUnit.DEFAULT_DECIMAL_PLACES,
                                                   PointArchiveType.NONE,
                                                   PointArchiveInterval.ZERO);
           break;
       case PointTypes.STATUS_POINT:
           point = PointFactory.createBankStatusPt(paoId);
           point.getPoint().setPointName(name);
           point.getPoint().setPointOffset( new Integer ( validPointOffset) );
           break;
           
       case PointTypes.DEMAND_ACCUMULATOR_POINT:
           point = PointFactory.createDmdAccumPoint(name, 
                                                    paoId, 
                                                    point.getPoint().getPointID(),
                                                    TypeBase.POINT_OFFSET, 
                                                    PointUnits.UOMID_UNDEF, 
                                                    0.1, 
                                                    StateGroupUtils.STATEGROUP_ANALOG,
                                                    PointUnit.DEFAULT_DECIMAL_PLACES,
                                                    PointArchiveType.NONE,
                                                    PointArchiveInterval.ZERO);
       
           break;
           
       case PointTypes.CALCULATED_POINT:
           YukonPao yukonPao = paoDao.getYukonPao(paoId);
           point = PointFactory.createCalculatedPoint(yukonPao.getPaoIdentifier(), 
                                                      name, 
                                                      StateGroupUtils.STATEGROUP_ANALOG);
           break;
           
       case PointTypes.CALCULATED_STATUS_POINT:
           point = PointFactory.createCalcStatusPoint(paoId, name, StateGroupUtils.STATEGROUP_ANALOG);
           break;
           
        default: 
            throw new Error("PointUtil::createPoint - Unrecognized point type");
       }
       
       point.getPoint().setServiceFlag(disabledChar);
   
       dbPersistentVector.getDBPersistentVector().add(point);
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
                                                                                     DbChangeType.ADD);
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
		
			//Set new point defaults from template
			pointBase.getPoint().setPointType(PointTypes.getType(newPointTemplate.getType()));
			
			// Add the updated (partial) point information.
		    t = Transaction.createTransaction(
					Transaction.ADD_PARTIAL,
					pointBase);

			pointBase = t.execute();
		}
		
		PointTemplate existingPointTemplate = createPointTemplate(pointBase);
		if (!existingPointTemplate.equals(newPointTemplate)) {
			
			applyPointTemplate(pointBase, newPointTemplate);
			
			Transaction<PointBase> t = Transaction.createTransaction(Transaction.UPDATE, pointBase);
	        pointBase = t.execute();
		}
	
        return pointBase;
	}
	
	private static PointTemplate createPointTemplate(PointBase pointBase) throws IllegalArgumentException {
		
		int pointType = PointTypes.getType(pointBase.getPoint().getPointType());
		int pointOffset = pointBase.getPoint().getPointOffset();
		
		PointTemplate pointTemplate = new PointTemplate(pointType, pointOffset);
		pointTemplate.setName(pointBase.getPoint().getPointName());
		
		if (pointBase instanceof AnalogPoint) {
        	
			AnalogPoint analogPoint = (AnalogPoint)pointBase;
			
			pointTemplate.setMultiplier(analogPoint.getPointAnalog().getMultiplier());
			pointTemplate.setStateGroupId(analogPoint.getPoint().getStateGroupID());
			pointTemplate.setUnitOfMeasure(analogPoint.getPointUnit().getUomID());
        	
        } else if (pointBase instanceof StatusPoint) {
        	
        	StatusPoint statusPoint = (StatusPoint)pointBase;
        	
			pointTemplate.setStateGroupId(statusPoint.getPoint().getStateGroupID());
        	
        } else if (pointBase instanceof AccumulatorPoint) {
        	
        	AccumulatorPoint accumulatorPoint = (AccumulatorPoint)pointBase;
        	
        	pointTemplate.setMultiplier(accumulatorPoint.getPointAccumulator().getMultiplier());
			pointTemplate.setStateGroupId(accumulatorPoint.getPoint().getStateGroupID());
			pointTemplate.setUnitOfMeasure(accumulatorPoint.getPointUnit().getUomID());
			
        } else if (pointBase instanceof CalculatedPoint) {
            CalculatedPoint calcPoint = (CalculatedPoint)pointBase;
            
            pointTemplate.setStateGroupId(calcPoint.getPoint().getStateGroupID());
            pointTemplate.setUnitOfMeasure(calcPoint.getPointUnit().getUomID());
            pointTemplate.setDecimalPlaces(calcPoint.getPointUnit().getDecimalPlaces());
            pointTemplate.setPointArchiveType(PointArchiveType.getByDisplayName(calcPoint.getPoint().getArchiveType()));
            pointTemplate.setPointArchiveInterval(PointArchiveInterval.getIntervalBySeconds(calcPoint.getPoint().getArchiveInterval()));
            
            CalcPointInfo calcPointInfo = new CalcPointInfo(calcPoint.getCalcBase().getUpdateType(), 
                                                            calcPoint.getCalcBase().getPeriodicRate(), false);
            List<CalcPointComponent> components = Lists.newArrayListWithExpectedSize(calcPoint.getCalcComponents().size());
            
            for (CalcComponent calcComponent : calcPoint.getCalcComponents()) {
                int pointId = calcComponent.getComponentPointID();
                String componentType = calcComponent.getComponentType();
                String operation = calcComponent.getOperation();
                
                LitePoint litePoint = pointDao.getLitePoint(pointId);
                PointIdentifier pointIdentifier = new PointIdentifier(PointType.getForId(litePoint.getPointType()),
                                                                      litePoint.getPointOffset());
                
                CalcPointComponent newComponent = new CalcPointComponent(pointIdentifier, componentType, operation);
                components.add(newComponent);
            }
            calcPointInfo.setComponents(components);
            pointTemplate.setCalcPointInfo(calcPointInfo);
        } else {
        	throw new IllegalArgumentException("Unsupported PointBase type: " + pointBase);
        }
		
		return pointTemplate;
	}
	
	public static void applyPointTemplate(PointBase pointBase, PointTemplate pointTemplate) {
		
		if (PointTypes.getType(pointBase.getPoint().getPointType()) != pointTemplate.getType()) {
			throw new IllegalArgumentException("Method not intended to be used to change point type.");
		}
		
		pointBase.getPoint().setPointOffset(pointTemplate.getOffset());
		pointBase.getPoint().setPointName(pointTemplate.getName());
		
		if (pointBase instanceof AnalogPoint) {
        	
			AnalogPoint analogPoint = (AnalogPoint)pointBase;
			
        	analogPoint.getPointAnalog().setMultiplier(pointTemplate.getMultiplier());
        	analogPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
        	analogPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
        	
        } else if (pointBase instanceof StatusPoint) {
        	
        	StatusPoint statusPoint = (StatusPoint)pointBase;
        	
        	statusPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
        	statusPoint.getPointStatus().setControlOffset(pointTemplate.getControlOffset());
        	statusPoint.getPointStatus().setControlType(pointTemplate.getControlType().getControlName());
        	statusPoint.getPointStatus().setStateZeroControl(pointTemplate.getStateZeroControl().getControlCommand());
        	statusPoint.getPointStatus().setStateOneControl(pointTemplate.getStateOneControl().getControlCommand());
        	
        } else if (pointBase instanceof AccumulatorPoint) {
        	
        	AccumulatorPoint accumulatorPoint = (AccumulatorPoint)pointBase;
        	
        	accumulatorPoint.getPointAccumulator().setMultiplier(pointTemplate.getMultiplier());
        	accumulatorPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
        	accumulatorPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
			
        } else if (pointBase instanceof CalculatedPoint) {
            addCalcPointTemplateAttributesToPointBase(pointTemplate, (CalculatedPoint)pointBase);
        } else {
        	throw new IllegalArgumentException("Unsupported PointBase type: " + pointBase);
        }
	}

	// TODO: Change the very similar PointFactory.createCalculatedPoint code to use this method
    private static void addCalcPointTemplateAttributesToPointBase(PointTemplate pointTemplate, CalculatedPoint calcPoint) {
        calcPoint.getPoint().setStateGroupID(pointTemplate.getStateGroupId());
        calcPoint.getPointUnit().setUomID(pointTemplate.getUnitOfMeasure());
        calcPoint.getPointUnit().setDecimalPlaces(pointTemplate.getDecimalPlaces());
        calcPoint.getPoint().setArchiveType(pointTemplate.getPointArchiveType().getPointArchiveTypeName());
        calcPoint.getPoint().setArchiveInterval(pointTemplate.getPointArchiveInterval().getSeconds());
        
        CalcBase calcBase = new CalcBase();
        calcBase.setUpdateType(pointTemplate.getCalcPointInfo().getUpdateType());
        calcBase.setPeriodicRate(pointTemplate.getCalcPointInfo().getPeriodicRate());
        calcBase.setCalculateQuality(pointTemplate.getCalcPointInfo().isForceQualityNormal() ? CtiUtilities.trueChar : CtiUtilities.falseChar);
        calcPoint.setCalcBase(calcBase);
        
        int order = 1;
        List<CalcComponent> calcComponents = Lists.newArrayListWithExpectedSize(pointTemplate.getCalcPointInfo().getComponents().size());
        for (CalcPointComponent calcPointComponent: pointTemplate.getCalcPointInfo().getComponents()) {
            if (calcPointComponent.getPointId() == null) {
                // If this CalcPointComponent's pointId isn't set by now... we assume it's pointIdentifier refers to this same paoIdentifier
                YukonPao yukonPao = paoDao.getYukonPao(calcPoint.getPoint().getPaoID());
                LitePoint litePoint = pointDao.getLitePoint(new PaoPointIdentifier(yukonPao.getPaoIdentifier(), calcPointComponent.getPointIdentifier()));
                calcPointComponent.setPointId(litePoint.getPointID());
            }
            Integer componentPointId = calcPointComponent.getPointId();
            String componentType = calcPointComponent.getCalcComponentType();
            String operation = calcPointComponent.getOperation();
            
            calcComponents.add(new CalcComponent( calcPoint.getPointUnit().getPointID(), order++, componentType, componentPointId, operation, 0.0, CtiUtilities.STRING_NONE ) );                
        }
        calcPoint.setCalcComponents(calcComponents);
    }
	
}