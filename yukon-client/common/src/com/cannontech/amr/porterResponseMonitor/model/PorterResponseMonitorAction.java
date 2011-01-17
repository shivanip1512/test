package com.cannontech.amr.porterResponseMonitor.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PorterResponseMonitorAction implements DisplayableEnum {
	normal,
	nopower,
	onfire,
	deadzo;

	@Override
	public String getFormatKey() {
		return keyPrefix + name();
	}

	private final static String keyPrefix = "yukon.web.modules.amr.porterResponseMonitor.action.";
}
