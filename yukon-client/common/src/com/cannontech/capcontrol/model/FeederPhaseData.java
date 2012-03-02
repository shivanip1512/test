package com.cannontech.capcontrol.model;

public class FeederPhaseData {
    private final int currentVarLoadPointID;
    private final int phaseB;
    private final int phaseC;
    private final boolean usePhaseData;

    public FeederPhaseData(int currentVarLoadPointId, int phaseB, int phaseC, boolean usePhaseData) {
        this.currentVarLoadPointID = currentVarLoadPointId;
        this.phaseB = phaseB;
        this.phaseC = phaseC;
        this.usePhaseData = usePhaseData;
    }

    public int getCurrentVarLoadPointId() {
        return currentVarLoadPointID;
    }

    public int getPhaseB() {
        return phaseB;
    }

    public int getPhaseC() {
        return phaseC;
    }
    
    public boolean getUsePhaseData() {
        return usePhaseData;
    }
}
