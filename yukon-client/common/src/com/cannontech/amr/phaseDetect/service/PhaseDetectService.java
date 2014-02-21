package com.cannontech.amr.phaseDetect.service;

import java.util.List;

import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectState;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.enums.Phase;

public interface PhaseDetectService {

    public void startPhaseDetect(LiteYukonUser user, Phase phase);
    
    public void readPhaseDetect(List<SimpleDevice> devices, Phase constrainToPhase, LiteYukonUser user);

    public void setPhaseDetectState(PhaseDetectState state);

    public void setPhaseDetectResult(PhaseDetectResult result);
    
    public PhaseDetectState getPhaseDetectState();
    
    public PhaseDetectResult getPhaseDetectResult();

    public void initializeResult();

    public void cancelReadPhaseDetect(LiteYukonUser user);

    public void cancelTest(LiteYukonUser user);

    public String cacheResults();

    public String getLastCachedResultKey();

    public void clearPhaseData(LiteYukonUser user);

}