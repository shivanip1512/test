package com.cannontech.amr.disconnect.model;

import java.util.stream.Stream;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.i18n.DisplayableEnum;

public enum DisconnectCommand implements DisplayableEnum {

    ARM("control connect", RfnMeterDisconnectCmdType.ARM, CollectionAction.ARM),
    CONNECT("control connect", RfnMeterDisconnectCmdType.RESUME, CollectionAction.CONNECT),
    DISCONNECT("control disconnect", RfnMeterDisconnectCmdType.TERMINATE, CollectionAction.DISCONNECT);

    private final String plcCommand;
    private final RfnMeterDisconnectCmdType rfnStatusType;
    private CollectionAction action;

    private DisconnectCommand(String plcCommand, RfnMeterDisconnectCmdType statusType, CollectionAction action) {
        this.plcCommand = plcCommand;
        this.rfnStatusType = statusType;
        this.action = action;
    }

    public String getPlcCommand() {
        return plcCommand;
    }
    
    public static DisconnectCommand getDisconnectCommand(CollectionAction action) {
        return Stream.of(DisconnectCommand.values())
                .filter(command -> command.getCollectionAction() == action)
                .findFirst()
                .get();
    }

    public CollectionAction getCollectionAction() {
        return action;
    }

    public RfnMeterDisconnectCmdType getRfnMeterDisconnectStatusType() {
        return rfnStatusType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.disconnect.command." + name();
    }
}
