package com.cannontech.web.updater.capcontrol;

import java.util.List;

import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubStation;

public abstract class AbstractAreaFormatingService<E extends StreamableCapObject> extends AbstractFormattingService<E> {

    protected abstract List<SubStation> getAreaStations(final int paoId, final LiteYukonUser user);
    
    protected abstract List<CapBankDevice> getAreaCapBanks(final int paoId, final LiteYukonUser user);
    
    @Override
    protected String getName(E latestValue, CBCDisplay cbcDisplay) {
        String areaName = latestValue.getCcName();
        return areaName;
    }
    
    @Override
    protected String getSetup(E latestValue, CBCDisplay cbcDisplay) {
        List<SubStation> areaStations = getAreaStations(latestValue.getCcId(), cbcDisplay.getUser());
        String setup = Integer.toString(areaStations.size());
        return setup;
    }

    @Override
    protected String getKVarsAvailable(E latestValue, CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        List<SubStation> areaStations = getAreaStations(latestValue.getCcId(), user);
        String varsAvailable = CBCUtils.format(CBCUtils.calcVarsAvailableForSubStations(areaStations, user));
        return varsAvailable;
    }
    
    @Override
    protected String getKVarsUnavailable(E latestValue, CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        List<SubStation> areaStations = getAreaStations(latestValue.getCcId(), user);
        String varsUnavailable =  CBCUtils.format (CBCUtils.calcVarsUnavailableForSubStations(areaStations, user) );
        return varsUnavailable;
    }
    
    @Override
    protected String getKVarsClosed(E latestValue, CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        List<CapBankDevice> areaCapBanks = getAreaCapBanks(latestValue.getCcId(), user);
        String closedVars = CBCUtils.format(CBCUtils.calcVarsClosedForCapBanks(areaCapBanks, user));
        return closedVars;
    }
    
    @Override
    protected String getKVarsTripped(E latestValue, CBCDisplay cbcDisplay) {
        LiteYukonUser user = cbcDisplay.getUser();
        List<CapBankDevice> areaCapBanks = getAreaCapBanks(latestValue.getCcId(), user);
        String trippedVars = CBCUtils.format(CBCUtils.calcVarsTrippedForCapBanks(areaCapBanks, user));
        return trippedVars;
    }

    @Override
    protected String getWarningFlagMessage(E latestValue, CBCDisplay cbcDisplay) {
        return "Voltage Reduction is Active";
    }
}
