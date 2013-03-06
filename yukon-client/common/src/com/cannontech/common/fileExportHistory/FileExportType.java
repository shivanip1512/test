package com.cannontech.common.fileExportHistory;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Represents parts of Yukon that can generate export files (and keep records
 * in File Export History).
 */
public enum FileExportType implements DisplayableEnum {
	BILLING,
	ARCHIVED_DATA_EXPORT,
	;
	
	private static final String prefix = "yukon.web.modules.support.fileExportHistory.types.";
	
	@Override
	public String getFormatKey() {
		return prefix + name();
	}
}
