package com.cannontech.web.capcontrol;

import com.cannontech.yukon.cbc.CBCCommand;

public enum CommandHolder {
    
    CONFIRM_AREA(CBCCommand.CONFIRM_AREA, "Confirm Area", false),
    CONFIRM_SUBSTATION(CBCCommand.CONFIRM_SUBSTATION, "Confirm SubStation", false),
    CONFIRM_SUBBUS(CBCCommand.CONFIRM_SUB, "Confirm SubBus", false),
    
    ENABLE_AREA(CBCCommand.ENABLE_AREA, "Enable Area", true),
    ENABLE_SUBSTATION(CBCCommand.ENABLE_AREA, "Enable SubStation", true),
    ENABLE_SUBBUS(CBCCommand.ENABLE_SUBBUS, "Enable SubBus", true),
    ENABLE_FEEDER(CBCCommand.ENABLE_FEEDER, "Enable Feeder", true),
    ENABLE_CAPBANK(CBCCommand.ENABLE_CAPBANK, "Enable CapBank", true),
    
    DISABLE_AREA(CBCCommand.DISABLE_AREA, "Disable Area", true),
    DISABLE_SUBSTATION(CBCCommand.DISABLE_AREA, "Disable SubStation", true),
    DISABLE_SUBBUS(CBCCommand.DISABLE_SUBBUS, "Disable SubBus", true),
    DISABLE_FEEDER(CBCCommand.DISABLE_FEEDER, "Disable Feeder", true),
    DISABLE_CAPBANK(CBCCommand.DISABLE_CAPBANK, "Disable CapBank", true),
    
    SEND_ALL_OPEN(CBCCommand.SEND_ALL_OPEN, "Open All CapBanks", false),
    SEND_ALL_CLOSED(CBCCommand.SEND_ALL_CLOSE, "Close All CapBanks", false),
    SEND_ENABLE_OVUV(CBCCommand.SEND_ALL_ENABLE_OVUV, "Enable OV/UV", true),
    SEND_DISABLE_OVUV(CBCCommand.SEND_ALL_DISABLE_OVUV, "Disable OV/UV", true),
    SEND_ALL_TIMESYNC(CBCCommand.SEND_TIMESYNC, "Send All TimeSync", false),
    
    SEND_ALL_SCAN_2WAY(CBCCommand.SEND_ALL_SCANN_2WAY, "Scan All 2way CBCs", false),
    RESET_OP_COUNTS(CBCCommand.RESET_OPCOUNT, "Reset Op Counts", false),
    
    VERIFY_STOP(CBCCommand.CMD_DISABLE_VERIFY, "Verify Stop", false),
    VERIFY_ALL_BANKS(CBCCommand.CMD_ALL_BANKS, "Verify All Banks", false),
    VERIFY_FQ_BANKS(CBCCommand.CMD_FQ_BANKS, "Verify Failed And Questionable Banks", false),
    VERIFY_FAILED_BANKS(CBCCommand.CMD_FAILED_BANKS, "Verify Failed Banks", false),
    VERIFY_Q_BANKS(CBCCommand.CMD_QUESTIONABLE_BANKS, "Verify Questionable Banks", false),
    VERIFY_SA_BANKS(CBCCommand.CMD_STANDALONE_VERIFY, "Verify Standalone Banks", false),
    
    CBC_OPEN(CBCCommand.OPEN_CAPBANK, "Open Capacitor", false),
    CBC_CLOSE(CBCCommand.CLOSE_CAPBANK, "Close Capacitor", false),
    CBC_CONFIRM_OPEN(CBCCommand.CONFIRM_OPEN, "Confirm Open", false),
    CBC_CONFIRM_CLOSE(CBCCommand.CONFIRM_CLOSE, "Confirm Closed", false),
    CBC_ENABLE_OVUV(CBCCommand.BANK_ENABLE_OVUV, "Enable OV/UV", false),
    CBC_DISABLE_OVUV(CBCCommand.BANK_DISABLE_OVUV, "Disable OV/UV", false),
    CBC_SCAN_2WAY(CBCCommand.SCAN_2WAY_DEV, "Init Scan", false),
    CBC_TIMESYNC(CBCCommand.SEND_TIMESYNC, "Send TimeSync", false),
    CBC_FLIP(CBCCommand.FLIP_7010_CAPBANK, "Flip", false);
    
    private final int cmdId;
    private final String commandName;
    private final boolean reasonRequired;
    
    private CommandHolder(int cmdId, String commandName, boolean reasonRequired) {
        this.cmdId = cmdId;
        this.commandName = commandName;
        this.reasonRequired = reasonRequired;
    }
    
    public int getCmdId() {
        return this.cmdId;
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public boolean isReasonRequired() {
        return this.reasonRequired;
    }
}
