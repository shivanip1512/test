package com.cannontech.web.updater.capcontrol;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.SubStation;
import com.cannontech.core.dao.DaoFactory;;

public class SubStationFormattingService extends AbstractAreaFormatingService<SubStation> {

    @Override
    protected List<CapBankDevice> getAreaCapBanks(int paoId, LiteYukonUser user) {
        throw new UnsupportedOperationException("Not Supported at this level.");
    }

    @Override
    protected List<SubStation> getAreaStations(int paoId, LiteYukonUser user) {
        throw new UnsupportedOperationException("Not Supported at this level.");
    }

    @Override
    protected String getPFactor(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        String pFactory = (String) cbcDisplay.getSubstationValueAt(latestValue, CBCDisplay.SUB_POWER_FACTOR_COLUMN);
        return pFactory;
    }

    @Override
    protected String getState(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        String state = (String) cbcDisplay.getSubstationValueAt(latestValue, CBCDisplay.SUB_CURRENT_STATE_COLUMN);
        return state;
    }
    
    @Override
    protected String getSpecialAreaEnabled(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        String spEnabled = (String)cbcDisplay.getSubstationValueAt(latestValue, CBCDisplay.SUB_SP_AREA_ENABLED);
        return spEnabled;
    }
    
    @Override
    protected String getKVarsAvailable(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        SubStation subStation = getSubStation(latestValue.getCcId(), user);
        String varsAvailable = CBCUtils.format(CBCUtils.calcVarsAvailableForSubStation(subStation, user));
        return varsAvailable;
    }
    
    @Override
    protected String getKVarsUnavailable(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        SubStation subStation = getSubStation(latestValue.getCcId(), user);
        String varsUnavailable =  CBCUtils.format (CBCUtils.calcVarsUnavailableForSubStation(subStation, user) );
        return varsUnavailable;
    }
    
    @Override
    protected String getKVarsClosed(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        List<CapBankDevice> capBankList = getCapBankList(latestValue.getCcId(), user);
        String closedVars = CBCUtils.format(CBCUtils.calcVarsClosedForCapBanks(capBankList, user));
        return closedVars;
    }
    
    @Override
    protected String getKVarsTripped(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        List<CapBankDevice> capBankList = getCapBankList(latestValue.getCcId(), user);
        String trippedVars = CBCUtils.format(CBCUtils.calcVarsTrippedForCapBanks(capBankList, user));
        return trippedVars;
    }
    
    @Override
    protected String getWarningFlag(final SubStation latestValue, final CBCDisplay cbcDisplay) {
        Boolean flag = latestValue.getVoltReductionFlag();
        return flag.toString();
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
