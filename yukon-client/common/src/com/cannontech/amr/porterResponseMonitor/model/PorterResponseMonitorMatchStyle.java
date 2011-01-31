package com.cannontech.amr.porterResponseMonitor.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.MatchStyle;

public enum PorterResponseMonitorMatchStyle implements DisplayableEnum {

	none(MatchStyle.none),
	any(MatchStyle.any),
	all(MatchStyle.all),
	;

	private final MatchStyle matchStyle;

    private PorterResponseMonitorMatchStyle(MatchStyle matchStyle) {
        this.matchStyle = matchStyle;
	    
	}

    public MatchStyle getMatchStyle() {
        return matchStyle;
    }
    
	@Override
	public String getFormatKey() {
		return keyPrefix + name();
	}

	private final static String keyPrefix = "yukon.web.modules.amr.porterResponseMonitor.matchStyle.";
}
