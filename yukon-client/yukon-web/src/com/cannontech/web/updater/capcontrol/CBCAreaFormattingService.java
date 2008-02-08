package com.cannontech.web.updater.capcontrol;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.SubStation;

public class CBCAreaFormattingService extends AbstractAreaFormatingService<CBCArea> {
    
    @Override
    protected String getState(final CBCArea latestValue, final CBCDisplay cbcDisplay) {
        String state = (latestValue.getDisableFlag()) ? "DISABLED" : "ENABLED";
        if (latestValue.getOvUvDisabledFlag()) state += "-V";
        return state;
    }
    
    @Override
    protected String getPFactor(final CBCArea latestValue, final CBCDisplay cbcDisplay) {
        String pFactory = cbcDisplay.getAreaValueAt(latestValue, CBCDisplay.AREA_POWER_FACTOR_COLUMN);
        return pFactory;
    }
    
    @Override
    protected String getWarningFlag(final CBCArea latestValue, CBCDisplay cbcDisplay) {
        Boolean flag = latestValue.getVoltReductionFlag();
        return flag.toString();
    }
    
    @Override
    protected List<SubStation> getAreaStations(final int paoId, final LiteYukonUser user) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        List<SubStation> areaStations = filterCapControlCache.getSubstationsByArea(paoId);
        return areaStations;
    }
    
    @Override
    protected List<CapBankDevice> getAreaCapBanks(final int paoId, final LiteYukonUser user) {
        CapControlCache filterCapControlCache = filterCacheFactory.createUserAccessFilteredCache(user);
        List<CapBankDevice> areaCapBanks = filterCapControlCache.getCapBanksByArea(paoId);
        return areaCapBanks;
    }
    
}
