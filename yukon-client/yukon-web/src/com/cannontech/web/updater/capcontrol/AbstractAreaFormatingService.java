package com.cannontech.web.updater.capcontrol;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;

public abstract class AbstractAreaFormatingService<E extends StreamableCapObject> extends AbstractFormattingService<E> {

    protected List<SubStation> getAreaStations(final int paoId, final LiteYukonUser user) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        List<SubStation> areaStations = filterCapControlCache.getSubstationsByArea(paoId);
        return areaStations;
    }
    
    protected List<CapBankDevice> getAreaCapBanks(final int paoId, final LiteYukonUser user) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        List<CapBankDevice> areaCapBanks = filterCapControlCache.getCapBanksByArea(paoId);
        return areaCapBanks;
    }
    
    @Override
    protected String getName(E latestValue, UpdaterHelper updaterHelper) {
        String areaName = latestValue.getCcName();
        return areaName;
    }
    
    @Override
    protected String getChildCount(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        List<SubStation> areaStations = getAreaStations(latestValue.getCcId(), context.getYukonUser());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        return messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.areas.stationCount", areaStations.size());
    }

    @Override
    protected String getKVarsAvailable(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        List<SubStation> areaStations = getAreaStations(latestValue.getCcId(), user);
        String varsAvailable = CapControlUtils.format(CapControlUtils.calcVarsAvailableForSubStations(areaStations, user));
        return varsAvailable;
    }
    
    @Override
    protected String getKVarsUnavailable(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        List<SubStation> areaStations = getAreaStations(latestValue.getCcId(), user);
        String varsUnavailable =  CapControlUtils.format (CapControlUtils.calcVarsUnavailableForSubStations(areaStations, user) );
        return varsUnavailable;
    }
    
    @Override
    protected String getKVarsClosed(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        List<CapBankDevice> areaCapBanks = getAreaCapBanks(latestValue.getCcId(), user);
        String closedVars = CapControlUtils.format(CapControlUtils.calcVarsClosedForCapBanks(areaCapBanks, user));
        return closedVars;
    }
    
    @Override
    protected String getKVarsTripped(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        LiteYukonUser user = context.getYukonUser();
        List<CapBankDevice> areaCapBanks = getAreaCapBanks(latestValue.getCcId(), user);
        String trippedVars = CapControlUtils.format(CapControlUtils.calcVarsTrippedForCapBanks(areaCapBanks, user));
        return trippedVars;
    }

    @Override
    protected String getWarningFlagMessage(E latestValue, UpdaterHelper updaterHelper, YukonUserContext context) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        return messageSourceAccessor.getMessage("yukon.web.modules.capcontrol.voltReductionActive");
    }
    
}