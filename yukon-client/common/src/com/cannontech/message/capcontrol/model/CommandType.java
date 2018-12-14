package com.cannontech.message.capcontrol.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableSet;

public enum CommandType implements DisplayableEnum {
    
    ENABLE_SUBSTATION_BUS(0),
    DISABLE_SUBSTATION_BUS(1),
    ENABLE_FEEDER(2),
    DISABLE_FEEDER(3),
    ENABLE_CAPBANK(4),
    DISABLE_CAPBANK(5),
    
    CONFIRM_OPEN(8),
    CONFIRM_CLOSE(9),
    REQUEST_ALL_DATA(10),
    RETURN_CAP_TO_ORIGINAL_FEEDER(11),
    RESET_DAILY_OPERATIONS(12),
    ENABLE_SUBSTATION(13),
    DISABLE_SUBSTATION(14),
    CONFIRM_FEEDER(15),
    RESET_SYSTEM_OP_COUNTS(16),
    
    DELETE_ITEM(19),
    CONFIRM_SUBSTATION_BUS(20),
    CONFIRM_AREA(21),
    ENABLE_AREA(22),
    DISABLE_AREA(23),
    
    ENABLE_SYSTEM(25),
    DISABLE_SYSTEM(26),
    FLIP_7010_CAPBANK(27),
    SYSTEM_STATUS(28),
    SEND_OPEN_ALL_CAPBANKS(29),
    SEND_CLOSE_ALL_CAPBANKS(30),
    SEND_OPEN_CAPBANK(29),
    SEND_CLOSE_CAPBANK(30),
    SEND_ENABLE_OVUV(31),
    SEND_DISABLE_OVUV(32),
    SEND_SCAN_2WAY_DEVICE(33),
    SEND_TIME_SYNC(34),
    CHANGE_OP_STATE(35),
    AUTO_ENABLE_OVUV(36),
    AUTO_DISABLE_OVUV(37),
    RETURN_FEEDER_TO_ORIGINAL_SUBBUS(38),
    CONFIRM_SUBSTATION(39),
    SEND_ENABLE_TEMPCONTROL(40),
    SEND_DISABLE_TEMPCONTROL(41),
    SEND_ENABLE_VARCONTROL(42),
    SEND_DISABLE_VARCONTROL(43),
    SEND_ENABLE_TIMECONTROL(44),
    SEND_DISABLE_TIMECONTROL(45),
    
    SEND_SYNC_CBC_CAPBANK_STATE(53),
    MOVE_BANK(54),
    MANUAL_ENTRY(55),
    
    /* Verify Commands */
    VERIFY_ALL_BANKS(56),
    VERIFY_FQ_BANKS(57),
    VERIFY_FAILED_BANKS(58),
    VERIFY_Q_BANKS(59),
    VERIFY_INACTIVE_BANKS(60),
    VERIFY_SA_BANKS(61),
    STOP_VERIFICATION(62),
    EMERGENCY_VERIFICATION_STOP(63),
    VERIFY_SELECTED_BANK(64),


    /* Regulator Commands */
    VOLTAGE_REGULATOR_INTEGRITY_SCAN(70),
    VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE(71),
    VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE(72),
    VOLTAGE_REGULATOR_TAP_POSITION_RAISE(73),
    VOLTAGE_REGULATOR_TAP_POSITION_LOWER(74),
    VOLTAGE_REGULATOR_KEEP_ALIVE_ENABLE(75),
    VOLTAGE_REGULATOR_KEEP_ALIVE_DISABLE(76),
    
    DYNAMIC(77),
    SCHEDULE_DMV_TEST(80);
    
    private int commandId;
    
    private final static ImmutableSet<CommandType> requiresComment;
    private final static ImmutableSet<CommandType> schedulableCommands;
    private final static ImmutableSet<CommandType> verifyCommands;
    private final static ImmutableSet<CommandType> fieldOperationCommands;
    private final static ImmutableSet<CommandType> nonOperationCommands;
    private final static ImmutableSet<CommandType> yukonActions;

    
    static {
        ImmutableSet.Builder<CommandType> builder = ImmutableSet.builder();
        builder.add(CommandType.SEND_OPEN_ALL_CAPBANKS);
        builder.add(CommandType.SEND_CLOSE_ALL_CAPBANKS);
        builder.add(CommandType.CONFIRM_AREA);
        builder.add(CommandType.EMERGENCY_VERIFICATION_STOP);
        builder.add(CommandType.CONFIRM_SUBSTATION);
        builder.add(CommandType.CONFIRM_SUBSTATION_BUS);
        builder.add(CommandType.VERIFY_ALL_BANKS);
        builder.add(CommandType.VERIFY_FQ_BANKS);
        builder.add(CommandType.VERIFY_FAILED_BANKS);
        builder.add(CommandType.VERIFY_Q_BANKS);
        builder.add(CommandType.VERIFY_SA_BANKS);
        builder.add(CommandType.STOP_VERIFICATION);
        builder.add(CommandType.CONFIRM_FEEDER);
        builder.add(CommandType.SEND_OPEN_CAPBANK);
        builder.add(CommandType.SEND_CLOSE_CAPBANK);
        builder.add(CommandType.CONFIRM_CLOSE);
        builder.add(CommandType.CONFIRM_OPEN);
        builder.add(CommandType.SEND_ENABLE_OVUV);
        builder.add(CommandType.SEND_DISABLE_OVUV);
        builder.add(CommandType.FLIP_7010_CAPBANK);
        builder.add(CommandType.VERIFY_SELECTED_BANK);
        builder.add(CommandType.SEND_ENABLE_TEMPCONTROL);
        builder.add(CommandType.SEND_DISABLE_TEMPCONTROL);
        builder.add(CommandType.SEND_ENABLE_VARCONTROL);
        builder.add(CommandType.SEND_DISABLE_VARCONTROL);
        builder.add(CommandType.SEND_ENABLE_TIMECONTROL);
        builder.add(CommandType.SEND_DISABLE_TIMECONTROL);
        builder.add(CommandType.VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE);
        builder.add(CommandType.VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE);
        builder.add(CommandType.VOLTAGE_REGULATOR_TAP_POSITION_RAISE);
        builder.add(CommandType.VOLTAGE_REGULATOR_TAP_POSITION_LOWER);
        fieldOperationCommands = builder.build();
        
        builder = ImmutableSet.builder();
        builder.add(CommandType.SEND_SCAN_2WAY_DEVICE);
        builder.add(CommandType.SEND_TIME_SYNC);
        builder.add(CommandType.VOLTAGE_REGULATOR_INTEGRITY_SCAN);
        nonOperationCommands = builder.build();
        
        builder = ImmutableSet.builder();
        builder.add(CommandType.ENABLE_AREA);
        builder.add(CommandType.ENABLE_SUBSTATION);
        builder.add(CommandType.ENABLE_SUBSTATION_BUS);
        builder.add(CommandType.ENABLE_FEEDER);
        builder.add(CommandType.ENABLE_CAPBANK);
        builder.add(CommandType.DISABLE_AREA);
        builder.add(CommandType.DISABLE_SUBSTATION);
        builder.add(CommandType.DISABLE_SUBSTATION_BUS);
        builder.add(CommandType.DISABLE_FEEDER);
        builder.add(CommandType.DISABLE_CAPBANK);
        builder.add(CommandType.SEND_SYNC_CBC_CAPBANK_STATE);
        builder.add(CommandType.RESET_DAILY_OPERATIONS);
        yukonActions = builder.build();
        
        builder = ImmutableSet.builder();
        builder.add(CommandType.VERIFY_ALL_BANKS);
        builder.add(CommandType.VERIFY_FAILED_BANKS);
        builder.add(CommandType.VERIFY_FQ_BANKS);
        builder.add(CommandType.VERIFY_SA_BANKS);
        builder.add(CommandType.VERIFY_Q_BANKS);
        builder.add(CommandType.VERIFY_INACTIVE_BANKS);
        builder.add(CommandType.EMERGENCY_VERIFICATION_STOP);
        builder.add(CommandType.STOP_VERIFICATION);
        verifyCommands = builder.build();
        
        builder = ImmutableSet.builder();
        builder.add(CommandType.VERIFY_ALL_BANKS);
        builder.add(CommandType.VERIFY_FAILED_BANKS);
        builder.add(CommandType.VERIFY_FQ_BANKS);
        builder.add(CommandType.VERIFY_SA_BANKS);
        builder.add(CommandType.VERIFY_Q_BANKS);
        builder.add(CommandType.VERIFY_INACTIVE_BANKS);
        builder.add(CommandType.CONFIRM_SUBSTATION_BUS);
        builder.add(CommandType.SEND_TIME_SYNC);
        schedulableCommands = builder.build();
        
        builder = ImmutableSet.builder();
        builder.add(CommandType.ENABLE_AREA);
        builder.add(CommandType.ENABLE_SUBSTATION);
        builder.add(CommandType.ENABLE_SUBSTATION_BUS);
        builder.add(CommandType.ENABLE_FEEDER);
        builder.add(CommandType.ENABLE_CAPBANK);
        builder.add(CommandType.DISABLE_AREA);
        builder.add(CommandType.DISABLE_SUBSTATION);
        builder.add(CommandType.DISABLE_SUBSTATION_BUS);
        builder.add(CommandType.DISABLE_FEEDER);
        builder.add(CommandType.DISABLE_CAPBANK);
        builder.add(CommandType.SEND_ENABLE_OVUV);
        builder.add(CommandType.SEND_DISABLE_OVUV);
        builder.add(CommandType.SEND_ENABLE_TEMPCONTROL);
        builder.add(CommandType.SEND_DISABLE_TEMPCONTROL);
        builder.add(CommandType.SEND_ENABLE_VARCONTROL);
        builder.add(CommandType.SEND_DISABLE_VARCONTROL);
        builder.add(CommandType.SEND_ENABLE_TIMECONTROL);
        builder.add(CommandType.SEND_DISABLE_TIMECONTROL);
        requiresComment = builder.build();
    }
    
    private CommandType(int commandId) {
        this.commandId = commandId;
    }
    
    public int getCommandId() {
        return commandId;
    }
    
    public static CommandType getForId(int commandId) {
        for (CommandType type : values()) {
            if (type.getCommandId() == commandId) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid commandId: " + commandId);
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.capcontrol.command." + name();
    }
    
    public boolean isCommentRequired() {
        return requiresComment.contains(this);
    }
    
    public boolean isSchedulingSupported() {
        return schedulableCommands.contains(this);
    }

    public static ImmutableSet<CommandType> getSchedulablecommands() {
        return schedulableCommands;
    }

    public boolean isVerifyCommand() {
        return verifyCommands.contains(this);
    }
    
    public boolean isYukonAction() {
        return yukonActions.contains(this);
    }
    
    public boolean isNonOperationCommand() {
        return nonOperationCommands.contains(this);
    }
    
    public boolean isFieldOperationCommand() {
        return fieldOperationCommands.contains(this);
    }
    
}