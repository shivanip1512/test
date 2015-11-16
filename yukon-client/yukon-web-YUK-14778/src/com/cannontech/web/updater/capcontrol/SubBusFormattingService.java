package com.cannontech.web.updater.capcontrol;

import java.util.Map;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.cbc.util.UpdaterHelper.UpdaterDataType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;

public class SubBusFormattingService extends AbstractFormattingService<SubBus> {

    @Override
    protected String getName(final SubBus latestValue, final UpdaterHelper updaterHelper) {
        String name = latestValue.getCcName();
        return name;
    }

    @Override
    protected String getState(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String state = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_CURRENT_STATE_COLUMN, context);
        return state;
    }
    
    @Override
    protected Map<String, Object> getStateFlags(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        return (Map<String, Object>)  updaterHelper.getSubBusValueAt(latestValue, UpdaterDataType.STATE_FLAGS, context);
    }

    @Override
    protected String getTargetPeakLead(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_TARGET_COLUMN_PEAKLEAD, context);
        return value;
    }
    
    @Override
    protected String getTargetPeakLag(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_TARGET_COLUMN_PEAKLAG, context);
        return value;
    }
    
    @Override
    protected String getTargetCloseOpenPercent(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_TARGET_COLUMN_CLOSEOPENPERCENT, context);
        return value;
    }
    
    @Override
    protected String getTarget(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String target = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_TARGET_COLUMN, context);
        return target;
    }
    
    @Override
    protected String getTargetMessage(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String target = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_TARGET_POPUP, context);
        return target;
    }
    
    @Override
    protected String getDailyMaxOps(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_DAILY_OPERATIONS_COLUMN, context);
        return value;
    }
    
    @Override
    protected boolean getWarningFlag(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        return (boolean) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_WARNING_FLAG, context);
    }
    
    @Override
    protected String getWarningFlagMessage(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_WARNING_POPUP, context);
        return value;
    }
    
    @Override
    protected String getKVarLoad(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_VAR_LOAD_COLUMN, context);
        return value;
    }
    
    @Override
    protected String getKVarLoadEst(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_VAR_EST_LOAD_COLUMN, context);
        return value;
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getKVarLoadQuality(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_VAR_LOAD_QUALITY, context);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getVoltQuality(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_VOLT_QUALITY, context);
        return value;    
    }
    
    /**
     * Returns a css class to be updated. 
     */
    @Override
    protected String getWattQuality(final SubBus latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_WATT_QUALITY, context);
        return value;    
    }
    
    @Override
    protected String getKVarLoadMessage(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_VAR_LOAD_POPUP, context);
        return value;
    }
    
    @Override
    protected String getDateTime(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_TIME_STAMP_COLUMN, context);
        return value;
    }

    @Override
    protected String getPFactor(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_POWER_FACTOR_COLUMN, context);
        return value;
    }

    @Override
    protected String getKw(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_WATTS_COLUMN, context);
        return value;
    }

    @Override
    protected String getVolts(final SubBus subBus, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String value = (String) updaterHelper.getSubBusValueAt(subBus, UpdaterHelper.UpdaterDataType.SUB_VOLTS_COLUMN, context);
        return value;
    }
    
    @Override
    protected boolean getVerificationFlag(SubBus latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        return latestValue.getVerificationFlag();
    }
    
    @Override
    protected boolean getDualBus(SubBus latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        return latestValue.getDualBusEnabled() && (latestValue.getPrimaryBusFlag() || latestValue.getSwitchOverStatus()) ;
    }
    
    @Override
    protected String getDualBusMessage(SubBus latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        String ret = "";
        int subbusId = latestValue.getAlternateBusId();
        
        if (subbusId > 0) {
            LiteYukonUser user = context.getYukonUser();
            CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
            SubBus otherBus = cache.getSubBus(subbusId);
            
            if (latestValue.getPrimaryBusFlag()) {
                ret = accessor.getMessage("yukon.web.modules.capcontrol.dualBusPrimaryMsg", otherBus.getCcName());
            } else if (latestValue.getSwitchOverStatus()) {
                ret = accessor.getMessage("yukon.web.modules.capcontrol.dualBusAlternateMsg", otherBus.getCcName());
            }
        }
        return ret;
    }
    
}