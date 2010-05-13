package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.cbc.CapBankDevice;

public class CapBankFormattingService extends AbstractFormattingService<CapBankDevice> {

    @Override
    protected String getWarningFlag(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String color = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_WARNING_IMAGE_COLOR);
        String text = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_WARNING_IMAGE_TEXT);
        return color + text;
    }
    
    @Override
    protected String getWarningFlagMessage(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_WARNING_POPUP);
        return value;
    }

    @Override
    protected String getCBName(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_NAME_COLUMN);
        return value;
    }
    
    @Override
    protected String getCBPhaseABefore(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEA_BEFORE);
        return value;
    }
    
    @Override
    protected String getCBPhaseBBefore(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEB_BEFORE);
        return value;
    }
    
    @Override
    protected String getCBPhaseCBefore(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEC_BEFORE);
        return value;
    }

    @Override
    protected String getCBBeforeTotal(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_BEFORE_TOTAL);
        return value;
    }
    
    @Override
    protected String getCBPhaseAAfter(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEA_AFTER);
        return value;
    }
    
    @Override
    protected String getCBPhaseBAfter(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEB_AFTER);
        return value;
    }
    
    @Override
    protected String getCBPhaseCAfter(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEC_AFTER);
        return value;
    }
    
    @Override
    protected String getCBAfterTotal(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_AFTER_TOTAL);
        return value;
    }
    
    @Override
    protected String getCBPhaseAPercentChange(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEA_PERCENTCHANGE);
        return value;
    }
    
    @Override
    protected String getCBPhaseBPercentChange(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEB_PERCENTCHANGE);
        return value;
    }
    
    @Override
    protected String getCBPhaseCPercentChange(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PHASEC_PERCENTCHANGE);
        return value;
    }
    
    @Override
    protected String getCBPercentChangeTotal(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PERCENTCHANGE_TOTAL);
        return value;
    }
    
    @Override
    protected String getCBStatus(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_STATUS_COLUMN);
        return value;
    }
    
    @Override
    protected String getCBStatusColor(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = cbcDisplay.getHTMLFgColor(latestValue);
        return value;
    }
    
    @Override
    protected String getCBSize(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = latestValue.getBankSize().toString();
        return value;
    }
    
    @Override
    protected String getCBParent(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        LiteYukonPAObject parent = (LiteYukonPAObject) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_PARENT_COLUMN);
        String value = parent.getPaoName();
        return value;
    }
    
    @Override
    protected String getDateTime(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_TIME_STAMP_COLUMN);
        return value;
    }
    
    @Override
    protected String getDailyMaxOps(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_DAILY_MAX_TOTAL_OP_COLUMN);
        return value;
    }

}
