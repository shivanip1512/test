package com.cannontech.web.updater.capcontrol;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;

public class SubStationFormattingService extends AbstractAreaFormatingService<SubStation> {

    @Override
    protected String getPFactor(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String pFactory = (String) updaterHelper.getSubstationValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_POWER_FACTOR_COLUMN, context);
        return pFactory;
    }

    @Override
    protected String getState(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String state = (String) updaterHelper.getSubstationValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_CURRENT_STATE_COLUMN, context);
        return state;
    }
    
    @Override
    protected String getSpecialAreaEnabledMsg(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String spEnabled = (String)updaterHelper.getSubstationValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_SP_AREA_ENABLED_MSG, context);
        return spEnabled;
    }
    
    @Override
    protected String getSpecialAreaEnabled(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        String spEnabled = (String)updaterHelper.getSubstationValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_SP_AREA_ENABLED, context);
        return spEnabled;
    }
    
    @Override
    protected String getKVarsAvailable(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        SubStation subStation = getSubStation(latestValue.getCcId(), user);
        String varsAvailable = CapControlUtils.format(CapControlUtils.calcVarsAvailableForSubStation(subStation, user));
        return varsAvailable;
    }
    
    @Override
    protected String getKVarsUnavailable(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        SubStation subStation = getSubStation(latestValue.getCcId(), user);
        String varsUnavailable =  CapControlUtils.format (CapControlUtils.calcVarsUnavailableForSubStation(subStation, user) );
        return varsUnavailable;
    }
    
    @Override
    protected String getKVarsClosed(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        List<CapBankDevice> capBankList = getCapBankList(latestValue.getCcId(), user);
        String closedVars = CapControlUtils.format(CapControlUtils.calcVarsClosedForCapBanks(capBankList, user));
        return closedVars;
    }
    
    @Override
    protected String getKVarsTripped(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        List<CapBankDevice> capBankList = getCapBankList(latestValue.getCcId(), user);
        String trippedVars = CapControlUtils.format(CapControlUtils.calcVarsTrippedForCapBanks(capBankList, user));
        return trippedVars;
    }
    
    @Override
    protected String getWarningFlag(final SubStation latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        return (String)updaterHelper.getSubstationValueAt(latestValue, UpdaterHelper.UpdaterDataType.SUB_VOLT_REDUCTION, context);
    }

    private SubStation getSubStation(final int paoId, final LiteYukonUser user) {
        CapControlCache capControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubStation subStation = capControlCache.getSubstation(paoId);
        return subStation;
    }
    
    private List<CapBankDevice> getCapBankList(final int paoId, final LiteYukonUser user) {
        CapControlCache capControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubStation subStation = capControlCache.getSubstation(paoId);
        List<CapBankDevice> capBankList = capControlCache.getCapBanksBySubStation(subStation);
        return capBankList;
    }
    
}