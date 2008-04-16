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
    protected String getCBStatusMessage(CapBankDevice latestValue, CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getCapBankValueAt(latestValue, CBCDisplay.CB_STATUS_POPUP);
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
