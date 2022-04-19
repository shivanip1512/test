package com.cannontech.core.dao;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface LMDao {
    LiteYukonPAObject[] getAllLMScenarios();

    LiteLMProgScenario[] getLMScenarioProgs(int scenarioID);

    // I renamed this because I am not brave enough to change it and I don't want anyone to ever use it.
    Set<LiteYukonPAObject> getAllProgramsForCommercialCurtailment();

    int getStartingGearForScenarioAndProgram(int programId, int scenarioId);

    /**
     * @return a loader for all LM PAOs
     */
    PaoLoader<DeviceCollectionReportDevice> getDeviceCollectionReportDeviceLoader();

    /**
     * @param identifiers PAObjectIDs of LM PAOs
     * @return map of PAObjectIDs and names for provided identifiers
     */
    Map<PaoIdentifier, String> getNamesForLMPAOs(Iterable<PaoIdentifier> identifiers);
}
