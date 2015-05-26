package com.cannontech.web.multispeak.visualDisplays.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DataTypeEnum implements DisplayableEnum {
	
	CURRENT_LOAD {
		public int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getCurrentLoadId();
		}
	},
	CURRENT_IH {
		public int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getCurrentIhId();
		}
	},
	LOAD_TO_PEAK {
		public int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getLoadToPeakId();
		}
	},
	PEAK_IH_LOAD {
		public int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getPeakIhLoadId();
		}
	},
	PEAK_DAY_TIMESTAMP {
		public int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType) {
			return powerSupplierType.getPeakDayTimestampId();
		}
	};
	
	private final String keyPrefix = "yukon.web.modules.dr.dataTypeEnum.";
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

	public abstract int getObjectIdForPowerSupplierType(PowerSuppliersEnum powerSupplierType);
}
