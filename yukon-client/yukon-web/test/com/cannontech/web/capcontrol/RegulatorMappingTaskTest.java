package com.cannontech.web.capcontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;
import org.junit.jupiter.api.Test;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.model.RegulatorPointMappingResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.Range;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResult;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingResultType;
import com.cannontech.web.capcontrol.regulator.setup.model.RegulatorMappingTask;
import com.google.common.collect.Multimap;

public class RegulatorMappingTaskTest {
    private SimpleDevice successRegulator = new SimpleDevice(1, PaoType.GANG_OPERATED);
    private SimpleDevice partSuccessRegulator = new SimpleDevice(2, PaoType.PHASE_OPERATED);
    private SimpleDevice failRegulator = new SimpleDevice(3, PaoType.LOAD_TAP_CHANGER);
    
    @Test
    public void test_startTime() {
        List<YukonPao> mockRegulators = getMockDeviceCollection();
        Instant before = Instant.now();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        Instant after = Instant.now();
        
        assertNotNull(task.getStart(), "Start time is null.");
        
        Range<Instant> range = Range.inclusive(before, after);
        assertTrue(range.intersects(task.getStart()), "Incorrect start time.");
    }
    
    @Test
    public void test_regulatorMappingTask() {
        List<YukonPao> mockRegulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.SUCCESS);
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.deviceComplete(successRegulator);
        
        assertEquals(1, task.getSuccessCount(), "Incorrect success count.");
        assertEquals(0, task.getPartialSuccessCount(), "Incorrect partial success count.");
        assertEquals(0, task.getFailedCount(), "Incorrect fail count.");
        assertEquals(1, task.getCompleteCount(), "Incorrect completion count.");
        assertFalse(task.isComplete(), "Task should not be complete. Only 1 of 3 regulators done.");
        
        task.addResult(partSuccessRegulator, RegulatorPointMapping.FORWARD_BANDWIDTH, RegulatorPointMappingResult.SUCCESS);
        task.addResult(partSuccessRegulator, RegulatorPointMapping.FORWARD_SET_POINT, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.addResult(partSuccessRegulator, RegulatorPointMapping.KEEP_ALIVE, RegulatorPointMappingResult.NO_POINTS_FOUND);
        task.addResult(partSuccessRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND);
        task.deviceComplete(partSuccessRegulator);
        
        assertEquals(1, task.getSuccessCount(), "Incorrect success count.");
        assertEquals(1, task.getPartialSuccessCount(), "Incorrect partial success count.");
        assertEquals(0, task.getFailedCount(), "Incorrect fail count.");
        assertEquals(2, task.getCompleteCount(), "Incorrect completion count.");
        assertFalse(task.isComplete(), "Task should not be complete. Only 2 of 3 regulators done.");
        
        task.addResult(failRegulator, RegulatorPointMapping.TAP_DOWN, RegulatorPointMappingResult.NO_POINTS_FOUND);
        task.addResult(failRegulator, RegulatorPointMapping.TAP_UP, RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND);
        task.deviceComplete(failRegulator);
        
        assertEquals(1, task.getSuccessCount(), "Incorrect success count.");
        assertEquals(1, task.getPartialSuccessCount(), "Incorrect partial success count.");
        assertEquals(1, task.getFailedCount(), "Incorrect fail count.");
        assertEquals(3, task.getCompleteCount(), "Incorrect completion count.");
        assertTrue(task.isComplete(), "Task should be complete. 3 of 3 regulators done.");
        
        Collection<RegulatorMappingResult> results = task.getResults();
        Multimap<RegulatorMappingResultType, RegulatorMappingResult> resultsByType = task.getResultsByType();
        
        assertEquals(3, results.size(), "There should be 3 results.");
        assertEquals(1, resultsByType.get(RegulatorMappingResultType.SUCCESSFUL).size(), "There should be 1 successful result.");
        assertEquals(1, resultsByType.get(RegulatorMappingResultType.PARTIALLY_SUCCESSFUL).size(), "There should be 1 partially successful result.");
        assertEquals(1, resultsByType.get(RegulatorMappingResultType.FAILED).size(), "There should be 1 failed result.");
    }
    
    @Test
    public void test_cancel() {
        List<YukonPao> mockRegulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.SUCCESS);
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.deviceComplete(successRegulator);
        
        assertFalse( task.isCanceled(), "Task was not canceled, but shows canceled.");
        task.cancel();
        assertTrue(task.isCanceled(), "Task was canceled, but shows not canceled.");
    }
    
    @Test
    public void test_error() {
        List<YukonPao> mockRegulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.SUCCESS);
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.deviceComplete(successRegulator);
        
        assertFalse(task.isErrorOccurred(), "Task shows an error when no error occurred.");
        Exception e = new Exception("Everything is broken!");
        task.errorOccurred(e);
        assertTrue(task.isErrorOccurred(), "Task shows no error when an error did occur.");
        assertSame(e, task.getError(), "Thrown exception does not match exception from task.");
    }
    
    private List<YukonPao> getMockDeviceCollection() {
        
        List<YukonPao> regulators = new ArrayList<>();
        regulators.add(new SimpleDevice(1, PaoType.PHASE_OPERATED));
        regulators.add(new SimpleDevice(2, PaoType.PHASE_OPERATED));
        regulators.add(new SimpleDevice(3, PaoType.PHASE_OPERATED));
        
        return regulators;
    }
}