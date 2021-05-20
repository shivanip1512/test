package com.cannontech.dr.rfn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.google.common.collect.Lists;

public class PqrConfigResultTest {
    
    @Test
    public void test_pqrConfigCounts() {
        PqrConfigCounts counts = new PqrConfigCounts();
        assertEquals(0, counts.getFailed(), "Count not zero after initialization");
        assertEquals(0, counts.getInProgress(), "Count not zero after initialization");
        assertEquals(0, counts.getSuccess(), "Count not zero after initialization");
        assertEquals( 0, counts.getUnsupported(), "Count not zero after initialization");
        
        counts.addResult(PqrConfigCommandStatus.UNSUPPORTED);
        assertEquals(1, counts.getUnsupported(), "Incorrect unsupported count");
        assertEquals(0, counts.getFailed(), "Incorrect failed count");
        assertEquals(0, counts.getInProgress(), "Incorrect in progress count");
        assertEquals(0, counts.getSuccess(), "Incorrect success count");
        
        counts.addResult(PqrConfigCommandStatus.FAILED);
        counts.addResult(PqrConfigCommandStatus.FAILED);
        counts.addResult(PqrConfigCommandStatus.FAILED);
        assertEquals(1, counts.getUnsupported(), "Incorrect unsupported count");
        assertEquals(3, counts.getFailed(), "Incorrect failed count");
        assertEquals(0, counts.getInProgress(), "Incorrect in progress count");
        assertEquals(0, counts.getSuccess(), "Incorrect success count");
        
        counts.addResult(PqrConfigCommandStatus.IN_PROGRESS);
        counts.addResult(PqrConfigCommandStatus.IN_PROGRESS);
        assertEquals(1, counts.getUnsupported(), "Incorrect unsupported count");
        assertEquals(3, counts.getFailed(), "Incorrect failed count");
        assertEquals(2, counts.getInProgress(), "Incorrect in progress count");
        assertEquals(0, counts.getSuccess(), "Incorrect success count");
        
        counts.addResult(PqrConfigCommandStatus.SUCCESS);
        counts.addResult(PqrConfigCommandStatus.FAILED);
        assertEquals(1, counts.getUnsupported(), "Incorrect unsupported count");
        assertEquals(4, counts.getFailed(), "Incorrect failed count");
        assertEquals(2, counts.getInProgress(), "Incorrect in progress count");
        assertEquals(1, counts.getSuccess(), "Incorrect success count");
    }
    
    @Test
    public void test_pqrDeviceConfigResult() {
        LiteLmHardwareBase hardware = new LiteLmHardwareBase();
        PqrDeviceConfigResult unsupportedResult = PqrDeviceConfigResult.unsupportedResult(hardware);
        
        assertTrue(unsupportedResult.isUnsupported(), "Unsupported result not marked as unsupported");
        assertEquals(PqrConfigCommandStatus.UNSUPPORTED, unsupportedResult.getOverallStatus(), "Unsupported result status is not 'unsupported'");
        assertTrue(unsupportedResult.isComplete(), "Unsupported result not marked as complete");
        assertEquals(hardware, unsupportedResult.getHardware(), "Result hardware is incorrect");
        
        LiteLmHardwareBase hardware2 = new LiteLmHardwareBase();
        PqrConfig config = getConfig();
        
        PqrDeviceConfigResult result = new PqrDeviceConfigResult(hardware, config);
        
        assertFalse(result.isUnsupported(), "Supported result marked as unsupported");
        assertEquals(PqrConfigCommandStatus.IN_PROGRESS, result.getOverallStatus(), "In progress result status is incorrect");
        assertFalse(result.isComplete(), "In progress result incorrectly marked as complete");
        assertEquals(hardware2, result.getHardware(), "Result hardware is incorrect");
        
        Map<LmHardwareCommandType, PqrConfigCommandStatus> statuses = result.getCommandStatuses();
        
        assertTrue(statuses.keySet().contains(LmHardwareCommandType.PQR_ENABLE), "Enable status not set correctly");
        assertTrue(statuses.keySet().contains(LmHardwareCommandType.PQR_EVENT_SEPARATION), "Event separation status not set correctly");
        assertTrue(statuses.keySet().contains(LmHardwareCommandType.PQR_LOV_PARAMETERS), "LOV parameters status not set correctly");
        assertTrue(statuses.keySet().contains(LmHardwareCommandType.PQR_LOV_EVENT_DURATION), "LOV event duration status not set correctly");
        assertTrue(statuses.keySet().contains(LmHardwareCommandType.PQR_LOV_DELAY_DURATION), "LOV delay duration status not set correctly");
        assertFalse(statuses.keySet().contains(LmHardwareCommandType.PQR_LOF_PARAMETERS), "LOF parameters status not set correctly");
        assertFalse(statuses.keySet().contains(LmHardwareCommandType.PQR_LOF_EVENT_DURATION), "LOF event duration status not set correctly");
        assertFalse(statuses.keySet().contains(LmHardwareCommandType.PQR_LOF_DELAY_DURATION), "LOF delay duration status not set correctly");
        
        for (Map.Entry<LmHardwareCommandType, PqrConfigCommandStatus> entry : statuses.entrySet()) {
            assertEquals(PqrConfigCommandStatus.IN_PROGRESS, entry.getValue(), entry.getKey() + " status not set correctly");
        }
        
        result.setStatus(LmHardwareCommandType.PQR_ENABLE, PqrConfigCommandStatus.SUCCESS);
        result.complete();
        
        assertEquals(PqrConfigCommandStatus.SUCCESS, result.getCommandStatuses().get(LmHardwareCommandType.PQR_ENABLE),
                ".setStatus did not change status");
        assertEquals(PqrConfigCommandStatus.FAILED, result.getCommandStatuses().get(LmHardwareCommandType.PQR_EVENT_SEPARATION),
                ".complete did not change IN_PROGRESS to FAILED");
        assertEquals(PqrConfigCommandStatus.FAILED, result.getCommandStatuses().get(LmHardwareCommandType.PQR_EVENT_SEPARATION),
                ".complete did not change IN_PROGRESS to FAILED");
        assertTrue(result.isComplete(), "Result not complete after calling .complete");
        assertEquals(PqrConfigCommandStatus.FAILED, result.getOverallStatus(), "Result not marked as failed when some commands failed");
        
        for (Map.Entry<LmHardwareCommandType, PqrConfigCommandStatus> entry : statuses.entrySet()) {
            entry.setValue(PqrConfigCommandStatus.SUCCESS); // Set all commands as successful
        }
        
        assertTrue(result.isComplete(), "Result not complete after all commands were successful");
        assertEquals(PqrConfigCommandStatus.SUCCESS, result.getOverallStatus(),
                "Result not marked as successful after all commands were successful");
        
    }
    
    @Test
    public void test_pqrConfigResult() {
        LiteLmHardwareBase hw = new LiteLmHardwareBase(1);
        LiteLmHardwareBase unsupportedHw = new LiteLmHardwareBase(2);
        PqrConfigResult result = new PqrConfigResult(Lists.newArrayList(hw), Lists.newArrayList(unsupportedHw), getConfig());
        
        assertFalse(result.isComplete(), "Incomplete result shows as complete");
        assertEquals(1, result.getCounts().getInProgress(), "Incorrect 'in progress' count");
        assertEquals(1, result.getCounts().getUnsupported(), "Incorrect 'unsupported' count");
        assertEquals(0, result.getCounts().getFailed(), "Incorrect 'failed' count");
        assertEquals(0, result.getCounts().getSuccess(), "Incorrect 'success' count");
        
        PqrDeviceConfigResult hwResult1 = result.getForInventoryId(1);
        assertFalse(hwResult1.isComplete(), "Incomplete device result marked as complete");
        assertEquals(hw, hwResult1.getHardware(), "Incorrect hardware from device result");
        assertFalse(hwResult1.isUnsupported(), "Incomplete device result marked as unsupported");
        assertEquals(PqrConfigCommandStatus.IN_PROGRESS, hwResult1.getOverallStatus(), "In progress device result status is not in progress");
        
        PqrDeviceConfigResult hwResult2 = result.getForInventoryId(2);
        assertTrue(hwResult2.isComplete(), "Unsupported device result not marked as complete");
        assertEquals(unsupportedHw, hwResult2.getHardware(), "Incorrect hardware from device result");
        assertTrue(hwResult2.isUnsupported(), "Unsupported device result not marked as unsupported");
        assertEquals(PqrConfigCommandStatus.UNSUPPORTED, hwResult2.getOverallStatus(), "Unsupported device result status is not unsupported");
        
        result.complete();
        
        assertTrue(result.isComplete(), "Complete result shows as incomplete");
        assertEquals(0, result.getCounts().getInProgress(), "Incorrect 'in progress' count");
        assertEquals(1, result.getCounts().getUnsupported(), "Incorrect 'unsupported' count");
        assertEquals(1, result.getCounts().getFailed(), "Incorrect 'failed' count");
        assertEquals(0, result.getCounts().getSuccess(), "Incorrect 'success' count");
        
        for (Map.Entry<LmHardwareCommandType, PqrConfigCommandStatus> entry : result.getForInventoryId(1).getCommandStatuses().entrySet()) {
            entry.setValue(PqrConfigCommandStatus.SUCCESS);
        }
        
        assertTrue(result.isComplete(), "Complete result shows as incomplete");
        assertEquals(0, result.getCounts().getInProgress(), "Incorrect 'in progress' count");
        assertEquals(1, result.getCounts().getUnsupported(), "Incorrect 'unsupported' count");
        assertEquals(0, result.getCounts().getFailed(), "Incorrect 'failed' count");
        assertEquals(1, result.getCounts().getSuccess(), "Incorrect 'success' count");
    }
    
    private PqrConfig getConfig() {
        PqrConfig config = new PqrConfig();
        config.setPqrEnable(true);
        config.setLovTrigger(1.0);
        config.setLovRestore(2.0);
        config.setLovTriggerTime((short) 3);
        config.setLovRestoreTime((short)4);
        config.setLovMinEventDuration((short)5);
        config.setLovMaxEventDuration((short)6);
        config.setLovStartRandomTime((short)7);
        config.setLovEndRandomTime((short)8);
        config.setMinimumEventSeparation((short)9);
        return config;
    }
}
