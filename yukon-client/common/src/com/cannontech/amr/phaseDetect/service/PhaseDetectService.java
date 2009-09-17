package com.cannontech.amr.phaseDetect.service;

import java.util.List;

import com.cannontech.amr.phaseDetect.data.Phase;
import com.cannontech.amr.phaseDetect.data.PhaseDetectData;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectState;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PhaseDetectService {

    public void startPhaseDetect(List<Integer> routeIds, LiteYukonUser user, Phase phase);
    
    public CommandResultHolder clearPhaseData(LiteYukonUser user) throws CommandCompletionException;

    public void readPhaseDetect(List<SimpleDevice> devices, Phase constrainToPhase, LiteYukonUser user);

    public void setPhaseDetectData(PhaseDetectData data);
    
    public void setPhaseDetectState(PhaseDetectState state);

    public void setPhaseDetectResult(PhaseDetectResult result);
    
    public PhaseDetectData getPhaseDetectData();

    public PhaseDetectState getPhaseDetectState();
    
    public PhaseDetectResult getPhaseDetectResult();

    public void initializeResult();

}
