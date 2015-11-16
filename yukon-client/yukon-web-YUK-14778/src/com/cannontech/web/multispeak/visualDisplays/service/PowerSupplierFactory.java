package com.cannontech.web.multispeak.visualDisplays.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.cannontech.web.multispeak.visualDisplays.model.DataTypeEnum;
import com.cannontech.web.multispeak.visualDisplays.model.HourlyDataTypeEnum;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSupplier;
import com.cannontech.web.multispeak.visualDisplays.model.PowerSuppliersEnum;

public class PowerSupplierFactory {
    
    @Autowired private FdrTranslationDao fdrTranslationDao;
    @Autowired private MultispeakLMService multispeakLMService;
    
    private static final FdrInterfaceType lm = FdrInterfaceType.MULTISPEAK_LM;
    
    public PowerSupplier getPowerSupplierForType(PowerSuppliersEnum powerSupplierType) {
        
        PowerSupplier powerSupplier = new PowerSupplier(powerSupplierType);
        
        // SET DATA POINT IDS
        int currentLoadObjectId = DataTypeEnum.CURRENT_LOAD.getObjectIdForPowerSupplierType(powerSupplierType);
        int currentIhObjectId = DataTypeEnum.CURRENT_IH.getObjectIdForPowerSupplierType(powerSupplierType);
        int loadToPeakObjectId = DataTypeEnum.LOAD_TO_PEAK.getObjectIdForPowerSupplierType(powerSupplierType);
        int peakIhLoadObjectId = DataTypeEnum.PEAK_IH_LOAD.getObjectIdForPowerSupplierType(powerSupplierType);
        int peakDayTimestampObjectId = DataTypeEnum.PEAK_DAY_TIMESTAMP.getObjectIdForPowerSupplierType(powerSupplierType);
        
        powerSupplier.setCurrentLoadPointId(getPointIdForObjectId(currentLoadObjectId));
        powerSupplier.setCurrentIhPointId(getPointIdForObjectId(currentIhObjectId));
        powerSupplier.setLoadToPeakPointId(getPointIdForObjectId(loadToPeakObjectId));
        powerSupplier.setPeakIhLoadPointId(getPointIdForObjectId(peakIhLoadObjectId));
        powerSupplier.setPeakDayTimestampPointId(getPointIdForObjectId(peakDayTimestampObjectId));
        
        // SET HOURLY DATA POINT IDS
        List<Integer> todayIntegratedHourlyDataObjectIdList = powerSupplier.getTodayIntegratedHourlyDataObjectIdList();
        List<Integer> todayIntegratedHourlyDataPointIdList = powerSupplier.getTodayIntegratedHourlyDataPointIdList();
        
        List<Integer> peakDayIntegratedHourlyDataObjectIdList = powerSupplier.getPeakDayIntegratedHourlyDataObjectIdList();
        List<Integer> peakDayIntegratedHourlyDataPointIdList = powerSupplier.getPeakDayIntegratedHourlyDataPointIdList();
        
        List<Integer> todayLoadControlPredictionObjectIdList = powerSupplier.getTodayLoadControlPredictionObjectIdList();
        List<Integer> todayLoadControlPredictionPointIdList = powerSupplier.getTodayLoadControlPredictionPointIdList();
        
        List<Integer> tomorrowLoadControlPredictionObjectIdList = powerSupplier.getTomorrowLoadControlPredictionObjectIdList();
        List<Integer> tomorrowLoadControlPredictionPointIdList = powerSupplier.getTomorrowLoadControlPredictionPointIdList();
        
        // loop per hour
        for (int i = 1; i <= 24; i++) {
            
            // loop per data type
            for (HourlyDataTypeEnum dataType : HourlyDataTypeEnum.values()) {
                
                int objectId = dataType.getObjectIdForHourEndForPowerSupplier(i, powerSupplierType);
                int pointId = getPointIdForObjectId(objectId);
                
                // setup objectId to pointId maps
                if (dataType.equals(HourlyDataTypeEnum.TODAY_INTEGRATED_HOURLY_DATA)) {
                    todayIntegratedHourlyDataObjectIdList.add(objectId);
                    todayIntegratedHourlyDataPointIdList.add(pointId);
                } else if (dataType.equals(HourlyDataTypeEnum.PEAK_DAY_INTEGRATED_HOURLY_DATA)) {
                    peakDayIntegratedHourlyDataObjectIdList.add(objectId);
                    peakDayIntegratedHourlyDataPointIdList.add(pointId);
                } else if (dataType.equals(HourlyDataTypeEnum.TODAY_LOAD_CONTROL_PREDICATION_DATA)) {
                    todayLoadControlPredictionObjectIdList.add(objectId);
                    todayLoadControlPredictionPointIdList.add(pointId);
                } else if (dataType.equals(HourlyDataTypeEnum.TOMORROW_LOAD_CONTROL_PREDICTION_DATA)) {
                    tomorrowLoadControlPredictionObjectIdList.add(objectId);
                    tomorrowLoadControlPredictionPointIdList.add(pointId);
                } else {
                    throw new IllegalArgumentException("Unsupported powerSupplierType: " + dataType.toString());
                }
            }
        }
        
        return powerSupplier;
    }
    
    private int getPointIdForObjectId(int objectId) {
        
        String objectString = Integer.valueOf(objectId).toString();
        String translation = multispeakLMService.buildFdrMultispeakLMTranslation(objectString);
        List<FdrTranslation> translations = fdrTranslationDao.getByInterfaceTypeAndTranslation(lm, translation);
        
        int pointId = -1;
        if (translations.size() > 0) {
            FdrTranslation fdrTranslation = translations.get(0);
            pointId = fdrTranslation.getPointId();
        }
        
        return pointId;
    }
    
}