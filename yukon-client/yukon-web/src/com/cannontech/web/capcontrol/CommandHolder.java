package com.cannontech.web.capcontrol;

import com.cannontech.yukon.cbc.CapControlCommand;

public enum CommandHolder {
    
    CONFIRM_AREA(CapControlCommand.CONFIRM_AREA, "Confirm Area", false),
    CONFIRM_SUBSTATION(CapControlCommand.CONFIRM_SUBSTATION, "Confirm Substation", false),
    CONFIRM_SUBBUS(CapControlCommand.CONFIRM_SUB, "Confirm Subbus", false),
    CONFIRM_FEEDER(CapControlCommand.CONFIRM_FEEDER, "Confirm Feeder", false),
    
    ENABLE_AREA(CapControlCommand.ENABLE_AREA, "Enable Area", true),
    ENABLE_SUBSTATION(CapControlCommand.ENABLE_AREA, "Enable Substation", true),
    ENABLE_SUBBUS(CapControlCommand.ENABLE_SUBBUS, "Enable Subbus", true),
    ENABLE_FEEDER(CapControlCommand.ENABLE_FEEDER, "Enable Feeder", true),
    ENABLE_CAPBANK(CapControlCommand.ENABLE_CAPBANK, "Enable CapBank", true),
    
    DISABLE_AREA(CapControlCommand.DISABLE_AREA, "Disable Area", true),
    DISABLE_SUBSTATION(CapControlCommand.DISABLE_AREA, "Disable Substation", true),
    DISABLE_SUBBUS(CapControlCommand.DISABLE_SUBBUS, "Disable Subbus", true),
    DISABLE_FEEDER(CapControlCommand.DISABLE_FEEDER, "Disable Feeder", true),
    DISABLE_CAPBANK(CapControlCommand.DISABLE_CAPBANK, "Disable CapBank", true),
    
    SEND_ALL_OPEN(CapControlCommand.SEND_ALL_OPEN, "Open All CapBanks", false),
    SEND_ALL_CLOSED(CapControlCommand.SEND_ALL_CLOSE, "Close All CapBanks", false),
    SEND_ENABLE_OVUV(CapControlCommand.SEND_ALL_ENABLE_OVUV, "Enable OV/UV", true),
    SEND_DISABLE_OVUV(CapControlCommand.SEND_ALL_DISABLE_OVUV, "Disable OV/UV", true),
    SEND_ALL_TIMESYNC(CapControlCommand.SEND_TIMESYNC, "Send All TimeSync", false),
    
    SEND_ENABLE_TEMPCONTROL(CapControlCommand.SEND_ALL_ENABLE_TEMPCONTROL,"Enable Temp Control",false),
    SEND_DISABLE_TEMPCONTROL(CapControlCommand.SEND_ALL_DISABLE_TEMPCONTROL,"Disable Temp Control",false),
    SEND_ENABLE_VARCONTROL(CapControlCommand.SEND_ALL_ENABLE_VARCONTROL,"Enable Var Control",false),
    SEND_DISABLE_VARCONTROL(CapControlCommand.SEND_ALL_DISABLE_VARCONTROL,"Disable Var Control",false),
    SEND_ENABLE_TIMECONTROL(CapControlCommand.SEND_ALL_ENABLE_TIMECONTROL,"Enable Time Control",false),
    SEND_DISABLE_TIMECONTROL(CapControlCommand.SEND_ALL_DISABLE_TIMECONTROL,"Disable Time Control",false),
    
    SEND_ALL_SCAN_2WAY(CapControlCommand.SEND_ALL_SCAN_2WAY, "Scan All 2way CBCs", false),
    RESET_OP_COUNTS(CapControlCommand.RESET_OPCOUNT, "Reset Op Counts", false),
    
    VERIFY_STOP(CapControlCommand.CMD_DISABLE_VERIFY, "Verify Stop", false),
    VERIFY_ALL_BANKS(CapControlCommand.CMD_ALL_BANKS, "Verify All Banks", false),
    VERIFY_FQ_BANKS(CapControlCommand.CMD_FQ_BANKS, "Verify Failed And Questionable Banks", false),
    VERIFY_FAILED_BANKS(CapControlCommand.CMD_FAILED_BANKS, "Verify Failed Banks", false),
    VERIFY_Q_BANKS(CapControlCommand.CMD_QUESTIONABLE_BANKS, "Verify Questionable Banks", false),
    VERIFY_SA_BANKS(CapControlCommand.CMD_STANDALONE_VERIFY, "Verify Standalone Banks", false),
    
    CBC_OPEN(CapControlCommand.OPEN_CAPBANK, "Open Capacitor", false),
    CBC_CLOSE(CapControlCommand.CLOSE_CAPBANK, "Close Capacitor", false),
    CBC_CONFIRM_OPEN(CapControlCommand.CONFIRM_OPEN, "Confirm Open", false),
    CBC_CONFIRM_CLOSE(CapControlCommand.CONFIRM_CLOSE, "Confirm Closed", false),
    CBC_ENABLE_OVUV(CapControlCommand.BANK_ENABLE_OVUV, "Enable OV/UV", true),
    CBC_DISABLE_OVUV(CapControlCommand.BANK_DISABLE_OVUV, "Disable OV/UV", true),
    CBC_SCAN_2WAY(CapControlCommand.SCAN_2WAY_DEV, "Init Scan", false),
    CBC_TIMESYNC(CapControlCommand.SEND_TIMESYNC, "Send TimeSync", false),
    CBC_FLIP(CapControlCommand.FLIP_7010_CAPBANK, "Flip", false),
    
    CBC_ENABLE_TEMPCONTROL(CapControlCommand.BANK_ENABLE_TEMPCONTROL, "Enable Temp Control",false),
    CBC_DISABLE_TEMPCONTROL(CapControlCommand.BANK_DISABLE_TEMPCONTROL, "Disable Temp Control",false),
    CBC_ENABLE_VARCONTROL(CapControlCommand.BANK_ENABLE_VARCONTROL, "Enable Var Control",false),
    CBC_DISABLE_VARCONTROL(CapControlCommand.BANK_DISABLE_VARCONTROL, "Disable Var Control",false),
    CBC_ENABLE_TIMECONTROL(CapControlCommand.BANK_ENABLE_TIMECONTROL, "Enable Time Control",false),
    CBC_DISABLE_TIMECONTROL(CapControlCommand.BANK_DISABLE_TIMECONTROL, "Disable Time Control",false),    
    
    OPERATIONAL_STATECHANGE(CapControlCommand.OPERATIONAL_STATECHANGE, "Change Operational State", false),
    
    LTC_SCAN_INTEGRITY(CapControlCommand.LTC_SCAN_INTEGRITY, "Scan Ltc Integrity",false),
    LTC_REMOTE_ENABLE(CapControlCommand.LTC_REMOTE_ENABLE, "Enable Remote Control",false),
    LTC_REMOTE_DISABLE(CapControlCommand.LTC_REMOTE_DISABLE, "Disable Remote Control",false),
    LTC_TAP_POSITION_RAISE(CapControlCommand.LTC_TAP_POSITION_RAISE, "Raise Tap Position",false),
    LTC_TAP_POSITION_LOWER(CapControlCommand.LTC_TAP_POSITION_LOWER, "Lower Tap Position",false);
    
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
