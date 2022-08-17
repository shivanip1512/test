package com.cannontech.dr.rfn.model;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.google.common.collect.Lists;

public class PqrConfigResultTest {
    
    @Test
    public void test_pqrConfigCounts() {
        PqrConfigCounts counts = new PqrConfigCounts();
        assertEquals("Count not zero after initialization", 0, counts.getFailed());
        assertEquals("Count not zero after initialization", 0, counts.getInProgress());
        assertEquals("Count not zero after initialization", 0, counts.getSuccess());
        assertEquals("Count not zero after initialization", 0, counts.getUnsupported());
        
        counts.addResult(PqrConfigCommandStatus.UNSUPPORTED);
        assertEquals("Incorrect unsupported count", 1, counts.getUnsupported());
        assertEquals("Incorrect failed count", 0, counts.getFailed());
        assertEquals("Incorrect in progress count", 0, counts.getInProgress());
        assertEquals("Incorrect success count", 0, counts.getSuccess());
        
        counts.addResult(PqrConfigCommandStatus.FAILED);
        counts.addResult(PqrConfigCommandStatus.FAILED);
        counts.addResult(PqrConfigCommandStatus.FAILED);
        assertEquals("Incorrect unsupported count", 1, counts.getUnsupported());
        assertEquals("Incorrect failed count", 3, counts.getFailed());
        assertEquals("Incorrect in progress count", 0, counts.getInProgress());
        assertEquals("Incorrect success count", 0, counts.getSuccess());
        
        counts.addResult(PqrConfigCommandStatus.IN_PROGRESS);
        counts.addResult(PqrConfigCommandStatus.IN_PROGRESS);
        assertEquals("Incorrect unsupported count", 1, counts.getUnsupported());
        assertEquals("Incorrect failed count", 3, counts.getFailed());
        assertEquals("Incorrect in progress count", 2, counts.getInProgress());
        assertEquals("Incorrect success count", 0, counts.getSuccess());
        
        counts.addResult(PqrConfigCommandStatus.SUCCESS);
        counts.addResult(PqrConfigCommandStatus.FAILED);
        assertEquals("Incorrect unsupported count", 1, counts.getUnsupported());
        assertEquals("Incorrect failed count", 4, counts.getFailed());
        assertEquals("Incorrect in progress count", 2, counts.getInProgress());
        assertEquals("Incorrect success count", 1, counts.getSuccess());
    }
    
    @Test
    public void test_pqrDeviceConfigResult() {
        LiteLmHardwareBase hardware = new LiteLmHardwareBase();
        PqrDeviceConfigResult unsupportedResult = PqrDeviceConfigResult.unsupportedResult(hardware);
        
        assertTrue("Unsupported result not marked as unsupported", unsupportedResult.isUnsupported());
        assertEquals("Unsupported result status is not 'unsupported'", PqrConfigCommandStatus.UNSUPPORTED, unsupportedResult.getOverallStatus());
        assertTrue("Unsupported result not marked as complete", unsupportedResult.isComplete());
        assertEquals("Result hardware is incorrect", hardware, unsupportedResult.getHardware());
        
        LiteLmHardwareBase hardware2 = new LiteLmHardwareBase();
        PqrConfig config = getConfig();
        
        PqrDeviceConfigResult result = new PqrDeviceConfigResult(hardware, config);
        
        assertFalse("Supported result marked as unsupported", result.isUnsupported());
        assertEquals("In progress result status is incorrect", PqrConfigCommandStatus.IN_PROGRESS, result.getOverallStatus());
        assertFalse("In progress result incorrectly marked as complete", result.isComplete());
        assertEquals("Result hardware is incorrect", hardware2, result.getHardware());
        
        Map<LmHardwareCommandType, PqrConfigCommandStatus> statuses = result.getCommandStatuses();
        
        assertTrue("Enable status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_ENABLE));
        assertTrue("Event separation status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_EVENT_SEPARATION));
        assertTrue("LOV parameters status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_LOV_PARAMETERS));
        assertTrue("LOV event duration status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_LOV_EVENT_DURATION));
        assertTrue("LOV delay duration status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_LOV_DELAY_DURATION));
        assertFalse("LOF parameters status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_LOF_PARAMETERS));
        assertFalse("LOF event duration status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_LOF_EVENT_DURATION));
        assertFalse("LOF delay duration status not set correctly", statuses.keySet().contains(LmHardwareCommandType.PQR_LOF_DELAY_DURATION));
        
        for (Map.Entry<LmHardwareCommandType, PqrConfigCommandStatus> entry : statuses.entrySet()) {
            assertEquals(entry.getKey() + " status not set correctly", PqrConfigCommandStatus.IN_PROGRESS, entry.getValue());
        }
        
        result.setStatus(LmHardwareCommandType.PQR_ENABLE, PqrConfigCommandStatus.SUCCESS);
        result.complete();
        
        assertEquals(".setStatus did not change status", 
                     PqrConfigCommandStatus.SUCCESS, result.getCommandStatuses().get(LmHardwareCommandType.PQR_ENABLE));
        assertEquals(".complete did not change IN_PROGRESS to FAILED", 
                     PqrConfigCommandStatus.FAILED, result.getCommandStatuses().get(LmHardwareCommandType.PQR_EVENT_SEPARATION));
        assertEquals(".complete did not change IN_PROGRESS to FAILED", 
                     PqrConfigCommandStatus.FAILED, result.getCommandStatuses().get(LmHardwareCommandType.PQR_EVENT_SEPARATION));
        assertTrue("Result not complete after calling .complete", result.isComplete());
        assertEquals("Result not marked as failed when some commands failed", 
                     PqrConfigCommandStatus.FAILED, result.getOverallStatus());
        
        for (Map.Entry<LmHardwareCommandType, PqrConfigCommandStatus> entry : statuses.entrySet()) {
            entry.setValue(PqrConfigCommandStatus.SUCCESS); // Set all commands as successful
        }
        
        assertTrue("Result not complete after all commands were successful", result.isComplete());
        assertEquals("Result not marked as successful after all commands were successful", 
                            PqrConfigCommandStatus.SUCCESS, result.getOverallStatus());
        
    }
    
    @Test
    public void test_pqrConfigResult() {
        LiteLmHardwareBase hw = new LiteLmHardwareBase(1);
        LiteLmHardwareBase unsupportedHw = new LiteLmHardwareBase(2);
        PqrConfigResult result = new PqrConfigResult(Lists.newArrayList(hw), Lists.newArrayList(unsupportedHw), getConfig());
        
        assertFalse("Incomplete result shows as complete", result.isComplete());
        assertEquals("Incorrect 'in progress' count", 1, result.getCounts().getInProgress());
        assertEquals("Incorrect 'unsupported' count", 1, result.getCounts().getUnsupported());
        assertEquals("Incorrect 'failed' count", 0, result.getCounts().getFailed());
        assertEquals("Incorrect 'success' count", 0, result.getCounts().getSuccess());
        
        PqrDeviceConfigResult hwResult1 = result.getForInventoryId(1);
        assertFalse("Incomplete device result marked as complete", hwResult1.isComplete());
        assertEquals("Incorrect hardware from device result", hw, hwResult1.getHardware());
        assertFalse("Incomplete device result marked as unsupported", hwResult1.isUnsupported());
        assertEquals("In progress device result status is not in progress", 
                     PqrConfigCommandStatus.IN_PROGRESS, hwResult1.getOverallStatus());
        
        PqrDeviceConfigResult hwResult2 = result.getForInventoryId(2);
        assertTrue("Unsupported device result not marked as complete", hwResult2.isComplete());
        assertEquals("Incorrect hardware from device result", unsupportedHw, hwResult2.getHardware());
        assertTrue("Unsupported device result not marked as unsupported", hwResult2.isUnsupported());
        assertEquals("Unsupported device result status is not unsupported", 
                     PqrConfigCommandStatus.UNSUPPORTED, hwResult2.getOverallStatus());
        
        result.complete();
        
        assertTrue("Complete result shows as incomplete", result.isComplete());
        assertEquals("Incorrect 'in progress' count", 0, result.getCounts().getInProgress());
        assertEquals("Incorrect 'unsupported' count", 1, result.getCounts().getUnsupported());
        assertEquals("Incorrect 'failed' count", 1, result.getCounts().getFailed());
        assertEquals("Incorrect 'success' count", 0, result.getCounts().getSuccess());
        
        for (Map.Entry<LmHardwareCommandType, PqrConfigCommandStatus> entry : result.getForInventoryId(1).getCommandStatuses().entrySet()) {
            entry.setValue(PqrConfigCommandStatus.SUCCESS);
        }
        
        assertTrue("Complete result shows as incomplete", result.isComplete());
        assertEquals("Incorrect 'in progress' count", 0, result.getCounts().getInProgress());
        assertEquals("Incorrect 'unsupported' count", 1, result.getCounts().getUnsupported());
        assertEquals("Incorrect 'failed' count", 0, result.getCounts().getFailed());
        assertEquals("Incorrect 'success' count", 1, result.getCounts().getSuccess());
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
