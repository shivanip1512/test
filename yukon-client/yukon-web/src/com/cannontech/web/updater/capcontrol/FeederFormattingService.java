package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.yukon.cbc.Feeder;

public class FeederFormattingService extends AbstractFormattingService<Feeder> {

    @Override
    protected String getWarningFlag(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_WARNING_IMAGE);
        return value;
    }
    
    @Override
    protected String getWarningFlagMessage(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_WARNING_POPUP);
        return value;
    }
    
    @Override
    protected String getState(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_CURRENT_STATE_COLUMN);
        return value;    
    }
    
    @Override
    protected String getTargetPeakLead(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_TARGET_COLUMN_PEAKLEAD);
        return value;    
    }
    
    @Override
    protected String getTargetPeakLag(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_TARGET_COLUMN_PEAKLAG);
        return value;    
    }
    
    @Override
    protected String getTargetCloseOpenPercent(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_TARGET_COLUMN_CLOSEOPENPERCENT);
        return value;    
    }
    
    @Override
    protected String getTarget(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_TARGET_COLUMN);
        return value;    
    }
    
    @Override
    protected String getTargetMessage(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_TARGET_POPUP);
        return value;    
    }
    
    @Override
    protected String getKVarLoad(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_VAR_LOAD_COLUMN);
        return value;    
    }
  
    @Override
    protected String getKVarLoadEst(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_VAR_EST_LOAD_COLUMN);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getKVarLoadQuality(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_VAR_LOAD_QUALITY);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getVoltQuality(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_VOLT_QUALITY);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getWattQuality(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_WATT_QUALITY);
        return value;    
    }
    
    @Override
    protected String getKVarLoadMessage(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_VAR_LOAD_POPUP);
        return value;    
    }
    
    @Override
    protected String getDateTime(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_TIME_STAMP_COLUMN);
        return value;    
    }
    
    @Override
    protected String getPFactor(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_POWER_FACTOR_COLUMN);
        return value;    
    }
    
    @Override
    protected String getKw(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_WATTS_COLUMN);
        return value;    
    }
    
    @Override
    protected String getVolts(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_VOLTS_COLUMN);
        return value;    
    }

    @Override
    protected String getDailyMaxOps(final Feeder latestValue, final CBCDisplay cbcDisplay) {
        String value = (String) cbcDisplay.getFeederValueAt(latestValue, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN);
        return value;    
    }

}
