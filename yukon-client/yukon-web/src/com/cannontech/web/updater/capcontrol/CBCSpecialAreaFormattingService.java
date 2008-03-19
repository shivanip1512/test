package com.cannontech.web.updater.capcontrol;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.SubStation;

public class CBCSpecialAreaFormattingService extends AbstractAreaFormatingService<CCSpecialArea> {

    @Override
    protected String getState(final CCSpecialArea latestValue, final CBCDisplay cbcDisplay) {
        String state = (latestValue.getDisableFlag()) ? "DISABLED" : "ENABLED";
        if (latestValue.getOvUvDisabledFlag()) state += "-V";
        return state;
    }

    @Override
    protected String getPFactor(final CCSpecialArea latestValue, final CBCDisplay cbcDisplay) {
        int paoId = latestValue.getCcId();
        List<SubStation> areaStations = getAreaStations(paoId, cbcDisplay.getUser());
        String currPF = cbcDisplay.getPowerFactorText(CBCUtils.calcAvgPF(areaStations), true);
        String estPF = cbcDisplay.getPowerFactorText(CBCUtils.calcAvgEstPF(areaStations), true);
        String result = currPF + " / " +  estPF;
        return result;
    }
    
    @Override
    protected String getWarningFlag(final CCSpecialArea latestValue, final CBCDisplay cbcDisplay) {
        Boolean flag = latestValue.getVoltReductionFlag();
        return flag.toString();
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
