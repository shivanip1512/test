package com.cannontech.cbc.dao;

import com.cannontech.message.capcontrol.model.CommandType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CommentAction {
    ENABLED,
    DISABLED,
    ENABLED_OVUV,
    DISABLED_OVUV,
    USER_COMMENT,
    SEND_ALL_CONTROL, 
    CAPBANK_CONTROL, 
    VERIFY_CONTROL,
    SYNC_CAPBANK_STATE,
    STANDALONE_REASON,
    LTC_CONTROL;
    
    private final static ImmutableMap<CommandType, CommentAction> commands;
    static {
        Builder<CommandType, CommentAction> builder = ImmutableMap.builder();
        builder.put(CommandType.ENABLE_AREA, ENABLED);
        builder.put(CommandType.ENABLE_SUBSTATION, ENABLED);
        builder.put(CommandType.ENABLE_SUBSTATION_BUS, ENABLED);
        builder.put(CommandType.ENABLE_FEEDER, ENABLED);
        builder.put(CommandType.ENABLE_CAPBANK, ENABLED);
        
        builder.put(CommandType.DISABLE_AREA, DISABLED);
        builder.put(CommandType.DISABLE_SUBSTATION, DISABLED);
        builder.put(CommandType.DISABLE_SUBSTATION_BUS, DISABLED);
        builder.put(CommandType.DISABLE_FEEDER, DISABLED);
        builder.put(CommandType.DISABLE_CAPBANK, DISABLED);
        
        builder.put(CommandType.SEND_ENABLE_OVUV, ENABLED_OVUV);
        
        builder.put(CommandType.SEND_DISABLE_OVUV, DISABLED_OVUV);
        
        builder.put(CommandType.SEND_ENABLE_TEMPCONTROL, SEND_ALL_CONTROL);
        builder.put(CommandType.SEND_DISABLE_TEMPCONTROL, SEND_ALL_CONTROL);
        builder.put(CommandType.SEND_ENABLE_VARCONTROL, SEND_ALL_CONTROL);
        builder.put(CommandType.SEND_DISABLE_VARCONTROL, SEND_ALL_CONTROL);
        builder.put(CommandType.SEND_ENABLE_TIMECONTROL, SEND_ALL_CONTROL);
        builder.put(CommandType.SEND_DISABLE_TIMECONTROL, SEND_ALL_CONTROL);
        builder.put(CommandType.SEND_TIME_SYNC, SEND_ALL_CONTROL);
        builder.put(CommandType.CONFIRM_AREA, SEND_ALL_CONTROL);
        builder.put(CommandType.CONFIRM_SUBSTATION, SEND_ALL_CONTROL);
        builder.put(CommandType.CONFIRM_SUBSTATION_BUS, SEND_ALL_CONTROL);
        builder.put(CommandType.CONFIRM_FEEDER, SEND_ALL_CONTROL);
        builder.put(CommandType.RESET_DAILY_OPERATIONS, SEND_ALL_CONTROL);
        
        builder.put(CommandType.SEND_OPEN_CAPBANK, CAPBANK_CONTROL);
        builder.put(CommandType.SEND_CLOSE_CAPBANK, CAPBANK_CONTROL);
        builder.put(CommandType.SEND_OPEN_ALL_CAPBANKS, CAPBANK_CONTROL);
        builder.put(CommandType.SEND_CLOSE_ALL_CAPBANKS, CAPBANK_CONTROL);
        builder.put(CommandType.CONFIRM_OPEN, CAPBANK_CONTROL);
        builder.put(CommandType.CONFIRM_CLOSE, CAPBANK_CONTROL);
        builder.put(CommandType.SEND_SCAN_2WAY_DEVICE, CAPBANK_CONTROL);
        builder.put(CommandType.FLIP_7010_CAPBANK, CAPBANK_CONTROL);
        
        builder.put(CommandType.VERIFY_ALL_BANKS, VERIFY_CONTROL);
        builder.put(CommandType.VERIFY_FQ_BANKS, VERIFY_CONTROL);
        builder.put(CommandType.VERIFY_FAILED_BANKS, VERIFY_CONTROL);
        builder.put(CommandType.VERIFY_Q_BANKS, VERIFY_CONTROL);
        builder.put(CommandType.STOP_VERIFICATION, VERIFY_CONTROL);
        builder.put(CommandType.EMERGENCY_VERIFICATION_STOP, VERIFY_CONTROL);
        builder.put(CommandType.VERIFY_SA_BANKS, VERIFY_CONTROL);
        builder.put(CommandType.VERIFY_SELECTED_BANK, VERIFY_CONTROL);
        
        builder.put(CommandType.SEND_SYNC_CBC_CAPBANK_STATE, SYNC_CAPBANK_STATE);
        
        builder.put(CommandType.CHANGE_OP_STATE, STANDALONE_REASON);
        
        builder.put(CommandType.VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE, LTC_CONTROL);
        builder.put(CommandType.VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE, LTC_CONTROL);
        builder.put(CommandType.VOLTAGE_REGULATOR_INTEGRITY_SCAN, LTC_CONTROL);
        builder.put(CommandType.VOLTAGE_REGULATOR_TAP_POSITION_LOWER, LTC_CONTROL);
        builder.put(CommandType.VOLTAGE_REGULATOR_TAP_POSITION_RAISE, LTC_CONTROL);
        
        commands = builder.build();
    }
    
    public static CommentAction getForCommand(CommandType command) {
        return commands.get(command);
    }
}