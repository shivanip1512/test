package com.cannontech.web.bulk;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.SingleProcessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.StatusPoint;

public class UpdatePointsProcessorFactory {

    private Logger log = YukonLogManager.getLogger(UpdatePointsProcessorFactory.class);

    private PointService pointService;
    private DBPersistentDao dbPersistentDao;


    /* Adjust Multiplier Points Processor */
    public Processor<YukonDevice> getAdjustMultiplierProcessor(final Map<PaoType, Set<PointTemplate>> pointTemplatesMap, final Double setValue) {

        SingleProcessor<YukonDevice> updatePointsProcessor = new UpdatePointsProcessorBase(pointTemplatesMap) {

            @Override
            protected void processAccumulatorPoint(AccumulatorPoint accumulatorPoint) {
                Double currentMultiplier = accumulatorPoint.getPointAccumulator().getMultiplier();
                Double newMultiplier = currentMultiplier * setValue;
                accumulatorPoint.getPointAccumulator().setMultiplier(newMultiplier);
            }

            @Override
            protected void processAnalogPoint(AnalogPoint analogPoint) {
                Double currentMultiplier = analogPoint.getPointAnalog().getMultiplier();
                Double newMultiplier = currentMultiplier * setValue;
                analogPoint.getPointAnalog().setMultiplier(newMultiplier);
            }

            @Override
            protected void processStatusPoint(StatusPoint statusPoint) {
            }
            
        };
        return updatePointsProcessor;
    }
    
    /* Explicit Multiplier Points Processor */
    public Processor<YukonDevice> getExplicitMultiplierProcessor(final Map<PaoType, Set<PointTemplate>> pointTemplatesMap, final Double setValue) {

        SingleProcessor<YukonDevice> updatePointsProcessor = new UpdatePointsProcessorBase(pointTemplatesMap) {

            @Override
            protected void processAccumulatorPoint(AccumulatorPoint accumulatorPoint) {
                accumulatorPoint.getPointAccumulator().setMultiplier(setValue);
            }

            @Override
            protected void processAnalogPoint(AnalogPoint analogPoint) {
                analogPoint.getPointAnalog().setMultiplier(setValue);
            }

            @Override
            protected void processStatusPoint(StatusPoint statusPoint) {
            }
            
        };
        return updatePointsProcessor;
    }
    
    /* Decimal Places Points Processor */
    public Processor<YukonDevice> getDecimalPlacesProcessor(final Map<PaoType, Set<PointTemplate>> pointTemplatesMap, final Integer setValue) {

        SingleProcessor<YukonDevice> updatePointsProcessor = new UpdatePointsProcessorBase(pointTemplatesMap) {

            @Override
            protected void processAccumulatorPoint(AccumulatorPoint accumulatorPoint) {
                accumulatorPoint.getPointUnit().setDecimalPlaces(setValue);
            }

            @Override
            protected void processAnalogPoint(AnalogPoint analogPoint) {
                analogPoint.getPointUnit().setDecimalPlaces(setValue);
            }

            @Override
            protected void processStatusPoint(StatusPoint statusPoint) {
            }
            
        };
        return updatePointsProcessor;
    }
    
    private abstract class UpdatePointsProcessorBase extends SingleProcessor<YukonDevice> {
        private final Map<PaoType, Set<PointTemplate>> pointTemplatesMap;

        private UpdatePointsProcessorBase(Map<PaoType, Set<PointTemplate>> pointTemplatesMap) {
            this.pointTemplatesMap = pointTemplatesMap;
        }

        @Override
        public final void process(YukonDevice device) throws ProcessingException {

            PaoType deviceType = device.getPaoIdentifier().getPaoType();
            if (pointTemplatesMap.containsKey(deviceType)) {
                Set<PointTemplate> pointSet = pointTemplatesMap.get(deviceType);
                for (PointTemplate pointTemplate : pointSet) {

                	boolean pointExistsForDevice = pointService.pointExistsForPao(device, pointTemplate.getPointIdentifier());
                	if (pointExistsForDevice) {
                	
	                    LitePoint litePoint = pointService.getPointForPao(device, pointTemplate.getPointIdentifier());
	
	                    PointBase pointBase = (PointBase)dbPersistentDao.retrieveDBPersistent(litePoint);
	
	                    if (pointBase instanceof AnalogPoint) {
	                        AnalogPoint analogPoint = (AnalogPoint)pointBase;
	
	                        processAnalogPoint(analogPoint);
	
	                        dbPersistentDao.performDBChange(analogPoint, Transaction.UPDATE);
	
	                    } else if (pointBase instanceof StatusPoint) {
	
	                        StatusPoint statusPoint = (StatusPoint)pointBase;
	                        processStatusPoint(statusPoint);
	
	                    } else if (pointBase instanceof AccumulatorPoint) {
	
	                        AccumulatorPoint accumulatorPoint = (AccumulatorPoint)pointBase;
	
	                        processAccumulatorPoint(accumulatorPoint);
	
	                        dbPersistentDao.performDBChange(accumulatorPoint, Transaction.UPDATE);
	
	                    } else {
	
	                        log.debug("Point type not supported, not updating: deviceId=" + device.getPaoIdentifier().getPaoId() + " pointId=" + litePoint.getLiteID() + " pointType=" + litePoint.getLiteType());
	                    }
	                    
                	} else {
                		log.debug("Point does not exist for device, not updating: point=" + pointTemplate + " deviceId=" + device.getPaoIdentifier().getPaoId());
                	}
                }

            } else {

                log.debug("No points selected for device type, none added or updated: deviceId=" + device.getPaoIdentifier().getPaoId());
            }
        }

        protected abstract  void processAccumulatorPoint(AccumulatorPoint accumulatorPoint);

        protected abstract void processStatusPoint(StatusPoint statusPoint);

        protected abstract void processAnalogPoint(AnalogPoint analogPoint);

    }

    @Autowired
    public void setPointService(PointService pointService){
        this.pointService = pointService;
    }
    
    @Autowired
    public void setDBPersistentDao(DBPersistentDao dbPersistentDao){
        this.dbPersistentDao = dbPersistentDao;
    }
}