package com.cannontech.web.visualDisplays.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.cannontech.web.visualDisplays.model.DataTypeEnum;
import com.cannontech.web.visualDisplays.model.HourlyDataTypeEnum;
import com.cannontech.web.visualDisplays.model.PowerSupplier;
import com.cannontech.web.visualDisplays.model.PowerSuppliersEnum;

public class PowerSupplierFactory {

	private FdrTranslationDao fdrTranslationDao;
	private MultispeakLMService multispeakLMService;
	
	public PowerSupplier getPowerSupplierForType(PowerSuppliersEnum powerSupplierType) {
		
		PowerSupplier powerSupplier = new PowerSupplier(powerSupplierType);
		
		// SET DATA POINT IDS
		int currentLoadObjectId = getObjectIdForDataType(powerSupplierType, DataTypeEnum.CURRENT_LOAD);
		int currentIhObjectId = getObjectIdForDataType(powerSupplierType, DataTypeEnum.CURRENT_IH);
		int loadToPeakObjectId = getObjectIdForDataType(powerSupplierType, DataTypeEnum.LOAD_TO_PEAK);
		int peakIhLoadObjectId = getObjectIdForDataType(powerSupplierType, DataTypeEnum.PEAK_IH_LOAD);
		int peakDayTimestampObjectId = getObjectIdForDataType(powerSupplierType, DataTypeEnum.PEAK_DAY_TIMESTAMP);
		
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
				
				int objectId = getObjectIdForHourEnd(powerSupplierType, i, dataType);
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
		
		String translation = multispeakLMService.buildFdrMultispeakLMTranslation((new Integer(objectId)).toString());
		List<FdrTranslation> fdrTranslationList = fdrTranslationDao.getByInterfaceTypeAndTranslation(FdrInterfaceType.MULTISPEAK_LM, translation);
		
		int pointId = -1;
		if (fdrTranslationList.size() > 0) {
			FdrTranslation fdrTranslation = fdrTranslationList.get(0);
			pointId = fdrTranslation.getPointId();
		}
		
		return pointId;
	}
	
	private int getObjectIdForDataType(PowerSuppliersEnum powerSupplierType, DataTypeEnum dataType) {
		
		if (dataType.equals(DataTypeEnum.CURRENT_LOAD)) {
			return powerSupplierType.getCurrentLoadId();
		} else if (dataType.equals(DataTypeEnum.CURRENT_IH)) {
			return powerSupplierType.getCurrentIhId();
		} else if (dataType.equals(DataTypeEnum.LOAD_TO_PEAK)) {
			return powerSupplierType.getLoadToPeakId();
		} else if (dataType.equals(DataTypeEnum.PEAK_IH_LOAD)) {
			return powerSupplierType.getPeakIhLoadId();
		} else if (dataType.equals(DataTypeEnum.PEAK_DAY_TIMESTAMP)) {
			return powerSupplierType.getPeakDayTimestampId();
		} else {
			throw new IllegalArgumentException("Unsupported DataTypeEnum: " + dataType.toString());
		}
		
	}

	private int getObjectIdForHourEnd(PowerSuppliersEnum powerSupplierType, int hr, HourlyDataTypeEnum dataType) throws IllegalArgumentException {

		int startObjectId;
		
		if (dataType.equals(HourlyDataTypeEnum.TODAY_INTEGRATED_HOURLY_DATA)) {
			startObjectId = powerSupplierType.getTodayIntegratedHourlyIdStart();
		} else if (dataType.equals(HourlyDataTypeEnum.PEAK_DAY_INTEGRATED_HOURLY_DATA)) {
			startObjectId = powerSupplierType.getPeakDayIntegratedHourlyIdStart();
		} else if (dataType.equals(HourlyDataTypeEnum.TODAY_LOAD_CONTROL_PREDICATION_DATA)) {
			startObjectId = powerSupplierType.getTodayLoadControlPredicationIdStart();
		} else if (dataType.equals(HourlyDataTypeEnum.TOMORROW_LOAD_CONTROL_PREDICTION_DATA)) {
			startObjectId = powerSupplierType.getTomorrowLoadControlPredicationIdStart();
		} else {
			throw new IllegalArgumentException("Unsupported HourlyDataTypeEnum: " + dataType.toString());
		}
		
		return startObjectId + (hr - 1);
	}
	
	@Autowired
	public void setFdrTranslationDao(FdrTranslationDao fdrTranslationDao) {
		this.fdrTranslationDao = fdrTranslationDao;
	}
	
	@Autowired
	public void setMultispeakLMService(MultispeakLMService multispeakLMService) {
		this.multispeakLMService = multispeakLMService;
	}
}
