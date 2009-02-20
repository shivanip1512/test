package com.cannontech.web.multispeak.visualDisplays.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum HourlyDataTypeEnum implements DisplayableEnum {

	TODAY_INTEGRATED_HOURLY_DATA,
	PEAK_DAY_INTEGRATED_HOURLY_DATA,
	TODAY_LOAD_CONTROL_PREDICATION_DATA,
	TOMORROW_LOAD_CONTROL_PREDICTION_DATA;
	
	private final String keyPrefix = "yukon.web.modules.visualDisplays.hourlyDataTypeEnum.";
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
	
	public int getObjectIdForHourEndForPowerSupplier(int hr, PowerSuppliersEnum powerSupplierType) {
		
		int startObjectId;
		
		if (this.equals(TODAY_INTEGRATED_HOURLY_DATA)) {
			startObjectId = powerSupplierType.getTodayIntegratedHourlyIdStart();
		} else if (this.equals(PEAK_DAY_INTEGRATED_HOURLY_DATA)) {
			startObjectId = powerSupplierType.getPeakDayIntegratedHourlyIdStart();
		} else if (this.equals(TODAY_LOAD_CONTROL_PREDICATION_DATA)) {
			startObjectId = powerSupplierType.getTodayLoadControlPredicationIdStart();
		} else if (this.equals(TOMORROW_LOAD_CONTROL_PREDICTION_DATA)) {
			startObjectId = powerSupplierType.getTomorrowLoadControlPredicationIdStart();
		} else {
			throw new IllegalArgumentException("Unsupported HourlyDataTypeEnum: " + this.toString());
		}
		
		return startObjectId + (hr - 1);
	}
}
