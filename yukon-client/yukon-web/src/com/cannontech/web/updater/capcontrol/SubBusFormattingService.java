package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.yukon.cbc.SubBus;

public class SubBusFormattingService extends AbstractFormattingService<SubBus> {

    @Override
    protected String getName(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String name = latestValue.getCcName();
        return name;
    }

    @Override
    protected String getState(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String state = (String) cbcDisplay.getSubBusValueAt(latestValue, CBCDisplay.SUB_CURRENT_STATE_COLUMN);
        return state;
    }
    
    @Override
    protected String getTarget(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String target = (String) cbcDisplay.getSubBusValueAt(latestValue, CBCDisplay.SUB_TARGET_COLUMN);
        return target;
    }
    
    @Override
    protected String getTargetMessage(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String target = (String) cbcDisplay.getSubBusValueAt(latestValue, CBCDisplay.SUB_TARGET_POPUP);
        return target;
    }
    
    @Override
    protected String getDailyMaxOps(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(latestValue, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN);
        return value;
    }
    
    @Override
    protected String getWarningFlag(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(latestValue, CBCDisplay.SUB_WARNING_IMAGE);
        return value;
    }
    
    @Override
    protected String getWarningFlagMessage(final SubBus latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(latestValue, CBCDisplay.SUB_WARNING_POPUP);
        return value;
    }
    
    @Override
    protected String getKVarLoad(final SubBus subBus, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_COLUMN);
        return value;
    }
  
    @Override
    protected String getKVarLoadMessage(final SubBus subBus, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_POPUP);
        return value;
    }
    
    @Override
    protected String getDateTime(final SubBus subBus, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(subBus, CBCDisplay.SUB_TIME_STAMP_COLUMN);
        return value;
    }

    @Override
    protected String getPFactor(final SubBus subBus, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(subBus, CBCDisplay.SUB_POWER_FACTOR_COLUMN);
        return value;
    }

    @Override
    protected String getKWVolts(final SubBus subBus, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getSubBusValueAt(subBus, CBCDisplay.SUB_WATTS_COLUMN);
        return value;
    }
    
    @Override
    protected String getVerificationFlag(SubBus latestValue, CBCDisplay cbcDisplay) {
        String value = latestValue.getVerificationFlag().toString();
        return value;
    }

}
