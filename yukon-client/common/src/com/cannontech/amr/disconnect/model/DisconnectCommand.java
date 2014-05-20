package com.cannontech.amr.disconnect.model;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.common.i18n.DisplayableEnum;

public enum DisconnectCommand implements DisplayableEnum {

    ARM("control connect", RfnMeterDisconnectStatusType.ARM),
    CONNECT("control connect", RfnMeterDisconnectStatusType.RESUME),
    DISCONNECT("control disconnect", RfnMeterDisconnectStatusType.TERMINATE);

    private final String plcCommand;
    private final RfnMeterDisconnectStatusType rfnStatusType;

    private DisconnectCommand(String plcCommand, RfnMeterDisconnectStatusType statusType) {
        this.plcCommand = plcCommand;
        this.rfnStatusType = statusType;

    }

    public String getPlcCommand() {
        return plcCommand;
    }

    public RfnMeterDisconnectStatusType getRfnMeterDisconnectStatusType() {
        return rfnStatusType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.disconnect.command." + name();
    }
}
