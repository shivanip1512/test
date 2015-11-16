package com.cannontech.web.updater.capcontrol;

import java.util.Map;

import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.cbc.util.UpdaterHelper.UpdaterDataType;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.user.YukonUserContext;

public class FeederFormattingService extends AbstractFormattingService<Feeder> {

    @Override
    protected boolean getWarningFlag(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        return (boolean) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_WARNING_FLAG, context);
    }
    
    @Override
    protected String getWarningFlagMessage(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_WARNING_POPUP, context);
        return value;
    }
    
    @Override
    protected String getState(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_CURRENT_STATE_COLUMN, context);
        return value;    
    }
    
    @Override
    protected  Map<String, Object> getStateFlags(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        return (Map<String, Object>) updaterHelper.getFeederValueAt(latestValue, UpdaterDataType.STATE_FLAGS, context);
    }

    @Override
    protected String getTargetPeakLead(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_TARGET_COLUMN_PEAKLEAD, context);
        return value;    
    }
    
    @Override
    protected String getTargetPeakLag(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_TARGET_COLUMN_PEAKLAG, context);
        return value;    
    }
    
    @Override
    protected String getTargetCloseOpenPercent(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_TARGET_COLUMN_CLOSEOPENPERCENT, context);
        return value;    
    }
    
    @Override
    protected String getTarget(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_TARGET_COLUMN, context);
        return value;    
    }
    
    @Override
    protected String getTargetMessage(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_TARGET_POPUP, context);
        return value;    
    }
    
    @Override
    protected String getKVarLoad(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_VAR_LOAD_COLUMN, context);
        return value;    
    }
  
    @Override
    protected String getKVarLoadEst(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_VAR_EST_LOAD_COLUMN, context);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getKVarLoadQuality(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_VAR_LOAD_QUALITY, context);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getVoltQuality(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_VOLT_QUALITY, context);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getWattQuality(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_WATT_QUALITY, context);
        return value;    
    }
    
    @Override
    protected String getKVarLoadMessage(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_VAR_LOAD_POPUP, context);
        return value;    
    }
    
    @Override
    protected String getDateTime(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_TIME_STAMP_COLUMN, context);
        return value;    
    }
    
    @Override
    protected String getPFactor(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_POWER_FACTOR_COLUMN, context);
        return value;    
    }
    
    @Override
    protected String getKw(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_WATTS_COLUMN, context);
        return value;    
    }
    
    @Override
    protected String getVolts(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_VOLTS_COLUMN, context);
        return value;    
    }

    @Override
    protected String getDailyMaxOps(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getFeederValueAt(latestValue, UpdaterHelper.UpdaterDataType.FDR_DAILY_OPERATIONS_COLUMN, context);
        return value;    
    }

    @Override
    protected boolean getDualBus(final Feeder latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        //This is how we determine if a feeder is moved (by dual bus) in CapControlWebUtilsServiceImpl.createViewableFeeder
        return latestValue.getOriginalParentId() > 0;
    }
}