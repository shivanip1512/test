package com.cannontech.web.multispeak.visualDisplays.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DataTypeEnum implements DisplayableEnum {
	
	CURRENT_LOAD,
	CURRENT_IH,
	LOAD_TO_PEAK,
	PEAK_IH_LOAD,
	PEAK_DAY_TIMESTAMP;
	
	private final String keyPrefix = "yukon.web.modules.visualDisplays.dataTypeEnum.";
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

	public int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType) {
		
		if (this.equals(CURRENT_LOAD)) {
			return powerSupplierType.getCurrentLoadId();
		} else if (this.equals(CURRENT_IH)) {
			return powerSupplierType.getCurrentIhId();
		} else if (this.equals(LOAD_TO_PEAK)) {
			return powerSupplierType.getLoadToPeakId();
		} else if (this.equals(PEAK_IH_LOAD)) {
			return powerSupplierType.getPeakIhLoadId();
		} else if (this.equals(PEAK_DAY_TIMESTAMP)) {
			return powerSupplierType.getPeakDayTimestampId();
		} else {
			throw new IllegalArgumentException("Unsupported DataTypeEnum: " + this.toString());
		}
	}
}
