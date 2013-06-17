package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.user.YukonUserContext;

public class CapBankFormattingService extends AbstractFormattingService<CapBankDevice> {

    @Override
    protected String getWarningFlag(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String color = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_WARNING_IMAGE_COLOR, context);
        String text = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_WARNING_IMAGE_TEXT, context);
        return color + text;
    }
    
    @Override
    protected String getWarningFlagMessage(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_WARNING_POPUP, context);
        return value;
    }

    @Override
    protected String getCBName(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_NAME_COLUMN, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseABefore(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEA_BEFORE, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseBBefore(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEB_BEFORE, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseCBefore(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEC_BEFORE, context);
        return value;
    }

    @Override
    protected String getCBBeforeTotal(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_BEFORE_TOTAL, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseAAfter(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEA_AFTER, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseBAfter(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEB_AFTER, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseCAfter(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEC_AFTER, context);
        return value;
    }
    
    @Override
    protected String getCBAfterTotal(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_AFTER_TOTAL, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseAPercentChange(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEA_PERCENTCHANGE, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseBPercentChange(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEB_PERCENTCHANGE, context);
        return value;
    }
    
    @Override
    protected String getCBPhaseCPercentChange(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PHASEC_PERCENTCHANGE, context);
        return value;
    }
    
    @Override
    protected String getCBPercentChangeTotal(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PERCENTCHANGE_TOTAL, context);
        return value;
    }
    
    @Override
    protected String getCBStatus(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_STATUS_COLUMN, context);
        return value;
    }
    
    @Override
    protected String getCBStatusColor(CapBankDevice latestValue, UpdaterHelper updaterHelper) {
        String value = updaterHelper.getHTMLFgColor(latestValue);
        return value;
    }
    
    @Override
    protected String getCBSize(CapBankDevice latestValue, UpdaterHelper updaterHelper) {
        String value = latestValue.getBankSize().toString();
        return value;
    }
    
    @Override
    protected String getCBParent(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_PARENT_COLUMN, context);
        return value;
    }
    
    @Override
    protected String getDateTime(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_TIME_STAMP_COLUMN, context);
        return value;
    }
    
    @Override
    protected String getDailyMaxOps(CapBankDevice latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getCapBankValueAt(latestValue, UpdaterHelper.UpdaterDataType.CB_DAILY_MAX_TOTAL_OP_COLUMN, context);
        return value;
    }

}
