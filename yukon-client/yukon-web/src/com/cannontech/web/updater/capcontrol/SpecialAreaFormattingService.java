package com.cannontech.web.updater.capcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;

public class SpecialAreaFormattingService extends AbstractAreaFormatingService<SpecialArea> {

    @Override
    protected String getState(final SpecialArea latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        String state = "";
        if (latestValue.getDisableFlag()) {
            state = accessor.getMessage("yukon.web.modules.capcontrol.disabled");
        } else {
            state = accessor.getMessage("yukon.web.modules.capcontrol.enabled");
        }
        
        if (latestValue.getOvUvDisabledFlag()) {
            state += accessor.getMessage("yukon.web.modules.capcontrol.ovuvDisabled");
        }
        return state;
    }

    @Override
    protected Map<String, Object> getStateFlags(final SpecialArea latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {

        Map<String, Object> flags = new HashMap<>();

        flags.put("enabled", !latestValue.getDisableFlag());
        flags.put("ovuvDisabled", latestValue.getOvUvDisabledFlag());

        return flags;
    }

    @Override
    protected String getPFactor(final SpecialArea latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        int paoId = latestValue.getCcId();
        List<SubStation> areaStations = getAreaStations(paoId, context.getYukonUser());
        String currPF = updaterHelper.getPowerFactorText(CapControlUtils.calcAvgPF(areaStations), true, context.getYukonUser(), accessor);
        String estPF = updaterHelper.getPowerFactorText(CapControlUtils.calcAvgEstPF(areaStations), true, context.getYukonUser(), accessor);
        return accessor.getMessage("yukon.web.modules.capcontrol.pfEstPf", currPF, estPF);
    }
    
    @Override
    protected boolean getWarningFlag(final SpecialArea latestValue, final UpdaterHelper updaterHelper, YukonUserContext context) {
        return latestValue.getVoltReductionFlag();
    }
    
    @Override
    protected List<SubStation> getAreaStations(final int paoId, final LiteYukonUser user) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        List<SubStation> areaStations = filterCapControlCache.getSubstationsBySpecialArea(paoId);
        return areaStations;
    }
    
    @Override
    protected List<CapBankDevice> getAreaCapBanks(final int paoId, final LiteYukonUser user) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        List<CapBankDevice> areaCapBanks = filterCapControlCache.getCapBanksBySpecialArea(paoId);
        return areaCapBanks;
    }

}
