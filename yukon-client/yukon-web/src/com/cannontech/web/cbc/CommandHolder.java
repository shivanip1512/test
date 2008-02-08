package com.cannontech.web.cbc;

import com.cannontech.yukon.cbc.CBCCommand;

public enum CommandHolder {
    
    CONFIRM_AREA(CBCCommand.CONFIRM_AREA, "Confirm_Area", "Confirm Area"),
    CONFIRM_SUBSTATION(CBCCommand.CONFIRM_SUBSTATION, "Confirm_Sub", "Confirm Sub"),
    CONFIRM_SUBBUS(CBCCommand.CONFIRM_SUB, "Confirm_Sub", "Confirm Sub"),
    
    ENABLE_AREA(CBCCommand.ENABLE_AREA, "Enable_Area", "Enable Area"),
    ENABLE_SUBSTATION(CBCCommand.ENABLE_AREA, "Enable_Sub", "Enable Sub"),
    ENABLE_SUBBUS(CBCCommand.ENABLE_SUBBUS, "Enable_Sub", "Enable Sub"),
    ENABLE_FEEDER(CBCCommand.ENABLE_FEEDER, "Enable_Feeder", "Enable Feeder"),
    ENABLE_CAPBANK(CBCCommand.ENABLE_CAPBANK, "Enable_CapBank", "Enable CapBank"),
    
    DISABLE_AREA(CBCCommand.DISABLE_AREA, "Disable_Area", "Disable Area"),
    DISABLE_SUBSTATION(CBCCommand.DISABLE_AREA, "Disable_Sub", "Disable Sub"),
    DISABLE_SUBBUS(CBCCommand.DISABLE_SUBBUS, "Disable_Sub", "Disable Sub"),
    DISABLE_FEEDER(CBCCommand.DISABLE_FEEDER, "Disable_Feeder", "Disable Feeder"),
    DISABLE_CAPBANK(CBCCommand.DISABLE_CAPBANK, "Disable_CapBank", "Disable CapBank"),
    
    SEND_ALL_OPEN(CBCCommand.SEND_ALL_OPEN, "Open_All_CapBanks", "Open All CapBanks"),
    SEND_ALL_CLOSED(CBCCommand.SEND_ALL_CLOSE, "Close_All_CapBanks", "Close All CapBanks"),
    SEND_ENABLE_OVUV(CBCCommand.SEND_ALL_ENABLE_OVUV, "Enable_OV/UV", "Enable OV/UV"),
    SEND_DISABLE_OVUV(CBCCommand.SEND_ALL_DISABLE_OVUV, "Disable_OV/UV", "Disable OV/UV"),
    SEND_ALL_TIMESYNC(CBCCommand.SEND_TIMESYNC, "Send_All_TimeSync", "Send All TimeSync"),
    
    SCAN_2WAY(CBCCommand.SCAN_2WAY_DEV, "Scan_All_2way_CBCs", "Scan All 2way CBCs"),
    RESET_OP_COUNTS(CBCCommand.RESET_OPCOUNT, "Reset_Op_Counts", "Reset Op Counts"),
    
    VERIFY_STOP(CBCCommand.CMD_DISABLE_VERIFY, "Verify_Stop", "Verify Stop"),
    VERIFY_ALL_BANKS(CBCCommand.CMD_ALL_BANKS, "Verify_All_Banks", "Verify All Banks"),
    VERIFY_FQ_BANKS(CBCCommand.CMD_FQ_BANKS, "Verify_Failed_And_Questionable_Banks", "Verify Failed And Questionable Banks"),
    VERIFY_FAILED_BANKS(CBCCommand.CMD_FAILED_BANKS, "Verify_Failed_Banks", "Verify Failed Banks"),
    VERIFY_Q_BANKS(CBCCommand.CMD_QUESTIONABLE_BANKS, "Verify_Questionable_Banks", "Verify Questionable Banks"),
    VERIFY_SA_BANKS(CBCCommand.CMD_STANDALONE_VERIFY, "Verify_Standalone_Banks", "Verify Standalone Banks"),
    
    CAPBANK_OPEN(CBCCommand.OPEN_CAPBANK, "Open_Capacitor", "Open Capacitor"),
    CAPBANK_CLOSE(CBCCommand.CLOSE_CAPBANK, "Close_Capacitor", "Close Capacitor"),
    CAPBANK_CONFIRM_OPEN(CBCCommand.CONFIRM_OPEN, "Confirm", "Confirm Open"),
    CAPBANK_CONFIRM_CLOSE(CBCCommand.CONFIRM_CLOSE, "Confirm", "Confirm Closed"),
    CAPBANK_ENABLE_OVUV(CBCCommand.BANK_ENABLE_OVUV, "Enable_OV/UV", "Enable OV/UV"),
    CAPBANK_DISABLE_OVUV(CBCCommand.BANK_DISABLE_OVUV, "Disable_OV/UV", "Disable OV/UV"),
    CAPBANK_SCAN_2WAY(CBCCommand.SCAN_2WAY_DEV, "Init_Scan", "Init Scan"),
    CAPBANK_TIMESYNC(CBCCommand.SEND_TIMESYNC, "Send_TimeSync", "Send TimeSync"),
    CAPBANK_FLIP(CBCCommand.FLIP_7010_CAPBANK, "Flip", "Flip");
    
    private final int cmdId;
    private final String commandName;
    private final String displayName;
    
    private CommandHolder(int cmdId, String commandName, String displayName) {
        this.cmdId = cmdId;
        this.commandName = commandName;
        this.displayName = displayName;
    }
    
    public int getCmdId() {
        return this.cmdId;
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
}
