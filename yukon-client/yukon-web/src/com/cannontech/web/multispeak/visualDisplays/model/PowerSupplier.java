package com.cannontech.web.multispeak.visualDisplays.model;

import java.util.ArrayList;
import java.util.List;

public class PowerSupplier {

	private PowerSuppliersEnum powerSupplierType;
	
	private int currentLoadPointId;
	private int currentIhPointId;
	private int loadToPeakPointId;
	private int peakIhLoadPointId;
	private int peakDayTimestampPointId;
	
	private List<Integer> todayIntegratedHourlyDataObjectIdList = new ArrayList<Integer>();
	private List<Integer> todayIntegratedHourlyDataPointIdList = new ArrayList<Integer>();
	
	private List<Integer> peakDayIntegratedHourlyDataObjectIdList = new ArrayList<Integer>();
	private List<Integer> peakDayIntegratedHourlyDataPointIdList = new ArrayList<Integer>();
	
	private List<Integer> todayLoadControlPredictionObjectIdList = new ArrayList<Integer>();
	private List<Integer> todayLoadControlPredictionPointIdList = new ArrayList<Integer>();
	
	private List<Integer> tomorrowLoadControlPredictionObjectIdList = new ArrayList<Integer>();
	private List<Integer> tomorrowLoadControlPredictionPointIdList = new ArrayList<Integer>();
	
	// PowerSupplier object should always be created using the PowerSupplierFactory or else they won't have
	// object ID lists, point ID lists, or other point IDs set.
	public PowerSupplier(PowerSuppliersEnum type) {
		this.powerSupplierType = type;
	}
	
	public int getPointIdForHourEnd(int hr, HourlyDataTypeEnum dataType) throws IllegalArgumentException {
		
		if (hr < 1 || hr > 24) {
			throw new IllegalArgumentException("Hour must be between 1-24");
		}

		try {
			
			if (dataType.equals(HourlyDataTypeEnum.TODAY_INTEGRATED_HOURLY_DATA)) {
				return this.getTodayIntegratedHourlyDataPointIdList().get(hr - 1);
			} else if (dataType.equals(HourlyDataTypeEnum.PEAK_DAY_INTEGRATED_HOURLY_DATA)) {
				return this.getPeakDayIntegratedHourlyDataPointIdList().get(hr - 1);
			} else if (dataType.equals(HourlyDataTypeEnum.TODAY_LOAD_CONTROL_PREDICATION_DATA)) {
				return this.getTodayLoadControlPredictionPointIdList().get(hr - 1);
			} else if (dataType.equals(HourlyDataTypeEnum.TOMORROW_LOAD_CONTROL_PREDICTION_DATA)) {
				return this.getTomorrowLoadControlPredictionPointIdList().get(hr - 1);
			} else {
				throw new IllegalArgumentException("Unsupported HourlyDataTypeEnum: " + dataType.toString());
			}
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("No point id for hour " + hr + ". PowerSupplier objects should be created using the PowerSupplierFactory.");
		}
	}

	
	public PowerSuppliersEnum getPowerSupplierType() {
		return powerSupplierType;
	}
	

	public int getCurrentLoadPointId() {
		return currentLoadPointId;
	}
	public int getCurrentIhPointId() {
		return currentIhPointId;
	}
	public int getLoadToPeakPointId() {
		return loadToPeakPointId;
	}
	public int getPeakIhLoadPointId() {
		return peakIhLoadPointId;
	}
	public int getPeakDayTimestampPointId() {
		return peakDayTimestampPointId;
	}
	

	public void setCurrentLoadPointId(int currentLoadPointId) {
		this.currentLoadPointId = currentLoadPointId;
	}
	public void setCurrentIhPointId(int currentIhPointId) {
		this.currentIhPointId = currentIhPointId;
	}
	public void setLoadToPeakPointId(int loadToPeakPointId) {
		this.loadToPeakPointId = loadToPeakPointId;
	}
	public void setPeakIhLoadPointId(int peakIhLoadPointId) {
		this.peakIhLoadPointId = peakIhLoadPointId;
	}
	public void setPeakDayTimestampPointId(int peakDayTimestampPointId) {
		this.peakDayTimestampPointId = peakDayTimestampPointId;
	}


	public List<Integer> getTodayIntegratedHourlyDataObjectIdList() {
		return todayIntegratedHourlyDataObjectIdList;
	}

	public List<Integer> getTodayIntegratedHourlyDataPointIdList() {
		return todayIntegratedHourlyDataPointIdList;
	}

	public List<Integer> getPeakDayIntegratedHourlyDataObjectIdList() {
		return peakDayIntegratedHourlyDataObjectIdList;
	}

	public List<Integer> getPeakDayIntegratedHourlyDataPointIdList() {
		return peakDayIntegratedHourlyDataPointIdList;
	}

	public List<Integer> getTodayLoadControlPredictionObjectIdList() {
		return todayLoadControlPredictionObjectIdList;
	}

	public List<Integer> getTodayLoadControlPredictionPointIdList() {
		return todayLoadControlPredictionPointIdList;
	}

	public List<Integer> getTomorrowLoadControlPredictionObjectIdList() {
		return tomorrowLoadControlPredictionObjectIdList;
	}

	public List<Integer> getTomorrowLoadControlPredictionPointIdList() {
		return tomorrowLoadControlPredictionPointIdList;
	}
}
