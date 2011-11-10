package com.cannontech.web.updater.capcontrol;

import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.user.YukonUserContext;

public class AreaFormattingService extends AbstractAreaFormatingService<Area> {
    
    @Override
    protected String getState(final Area latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String prefix = "yukon.web.modules.capcontrol.";
        String key = "";
        if (latestValue.getDisableFlag()) {
            key = prefix + "disabled";
        } else {
            key = prefix + "enabled";
        }
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        String state = messageSourceAccessor.getMessage(key);
        
        if (latestValue.getOvUvDisabledFlag()) {
            state += messageSourceAccessor.getMessage(prefix + "ovuvDisabled");
        }
        return state;
    }
    
    @Override
    protected String getPFactor(final Area latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String pFactory = updaterHelper.getAreaValueAt(latestValue, UpdaterHelper.UpdaterDataType.AREA_POWER_FACTOR_COLUMN, context);
        return pFactory;
    }
    
    @Override
    protected String getWarningFlag(final Area latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        return updaterHelper.getAreaValueAt(latestValue, UpdaterHelper.UpdaterDataType.AREA_VOLT_REDUCTION, context);
    }
    
}