package com.cannontech.web.multispeak.visualDisplays.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum HourlyDataTypeEnum implements DisplayableEnum {

	TODAY_INTEGRATED_HOURLY_DATA {
		public int getObjectIdForHourEndForPowerSupplier(int hr, PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getTodayIntegratedHourlyIdStart() + (hr - 1);
		}
	},
	PEAK_DAY_INTEGRATED_HOURLY_DATA {
		public int getObjectIdForHourEndForPowerSupplier(int hr, PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getPeakDayIntegratedHourlyIdStart() + (hr - 1);
		}
	},
	TODAY_LOAD_CONTROL_PREDICATION_DATA {
		public int getObjectIdForHourEndForPowerSupplier(int hr, PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getTodayLoadControlPredicationIdStart() + (hr - 1);
		}
	},
	TOMORROW_LOAD_CONTROL_PREDICTION_DATA {
		public int getObjectIdForHourEndForPowerSupplier(int hr, PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getTomorrowLoadControlPredicationIdStart() + (hr - 1);
		}
	};
	
	private final String keyPrefix = "yukon.web.modules.dr.hourlyDataTypeEnum.";
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
	
	public abstract int getObjectIdForHourEndForPowerSupplier(int hr, PowerSuppliersEnum powerSupplierType);
	
}
