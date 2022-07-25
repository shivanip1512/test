package com.cannontech.web.stars.signalTransmitter;
import com.cannontech.common.device.terminal.dao.PagingTerminalDao.SortBy;
import com.cannontech.common.i18n.DisplayableEnum;

public enum SignalTransmitterSortBy implements DisplayableEnum {

    name(SortBy.NAME),
    type(SortBy.TYPE),
    status(SortBy.STATUS);

    private final SortBy value;
    
    SignalTransmitterSortBy(SortBy value) {
        this.value = value;
    }

    @Override
    public String getFormatKey() {
        if (this == name) {
            return "yukon.web.modules.operator.signalTransmitter." + name();
        }
        return "yukon.common." + name();
    }

    public SortBy getValue() {
        return value;
    }
}
