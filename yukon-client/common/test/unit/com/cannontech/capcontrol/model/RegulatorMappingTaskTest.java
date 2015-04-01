package com.cannontech.capcontrol.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Multimap;

public class RegulatorMappingTaskTest {
    private SimpleDevice successRegulator = new SimpleDevice(1, PaoType.GANG_OPERATED);
    private SimpleDevice partSuccessRegulator = new SimpleDevice(2, PaoType.PHASE_OPERATED);
    private SimpleDevice failRegulator = new SimpleDevice(3, PaoType.LOAD_TAP_CHANGER);
    
    @Test
    public void test_startTime() {
        DeviceCollection mockRegulators = getMockDeviceCollection();
        Instant before = Instant.now();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        Instant after = Instant.now();
        
        Assert.assertNotNull("Start time is null.", task.getStart());
        
        Range<Instant> range = Range.inclusive(before, after);
        Assert.assertTrue("Incorrect start time.", range.intersects(task.getStart()));
    }
    
    @Test
    public void test_regulatorMappingTask() {
        DeviceCollection mockRegulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.SUCCESS);
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.deviceComplete(successRegulator);
        
        Assert.assertEquals("Incorrect success count.", 1, task.getSuccessCount());
        Assert.assertEquals("Incorrect partial success count.", 0, task.getPartialSuccessCount());
        Assert.assertEquals("Incorrect fail count.", 0, task.getFailedCount());
        Assert.assertEquals("Incorrect completion count.", 1, task.getCompleteCount());
        Assert.assertFalse("Task should not be complete. Only 1 of 3 regulators done.", task.isComplete());
        
        task.addResult(partSuccessRegulator, RegulatorPointMapping.FORWARD_BANDWIDTH, RegulatorPointMappingResult.SUCCESS);
        task.addResult(partSuccessRegulator, RegulatorPointMapping.FORWARD_SET_POINT, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.addResult(partSuccessRegulator, RegulatorPointMapping.KEEP_ALIVE, RegulatorPointMappingResult.NO_POINTS_FOUND);
        task.addResult(partSuccessRegulator, RegulatorPointMapping.KEEP_ALIVE_TIMER, RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND);
        task.deviceComplete(partSuccessRegulator);
        
        Assert.assertEquals("Incorrect success count.", 1, task.getSuccessCount());
        Assert.assertEquals("Incorrect partial success count.", 1, task.getPartialSuccessCount());
        Assert.assertEquals("Incorrect fail count.", 0, task.getFailedCount());
        Assert.assertEquals("Incorrect completion count.", 2, task.getCompleteCount());
        Assert.assertFalse("Task should not be complete. Only 2 of 3 regulators done.", task.isComplete());
        
        task.addResult(failRegulator, RegulatorPointMapping.TAP_DOWN, RegulatorPointMappingResult.NO_POINTS_FOUND);
        task.addResult(failRegulator, RegulatorPointMapping.TAP_UP, RegulatorPointMappingResult.MULTIPLE_POINTS_FOUND);
        task.deviceComplete(failRegulator);
        
        Assert.assertEquals("Incorrect success count.", 1, task.getSuccessCount());
        Assert.assertEquals("Incorrect partial success count.", 1, task.getPartialSuccessCount());
        Assert.assertEquals("Incorrect fail count.", 1, task.getFailedCount());
        Assert.assertEquals("Incorrect completion count.", 3, task.getCompleteCount());
        Assert.assertTrue("Task should be complete. 3 of 3 regulators done.", task.isComplete());
        
        Collection<RegulatorMappingResult> results = task.getResults();
        Multimap<RegulatorMappingResultType, RegulatorMappingResult> resultsByType = task.getResultsByType();
        
        Assert.assertEquals("There should be 3 results.", 3, results.size());
        Assert.assertEquals("There should be 1 successful result.", 1, resultsByType.get(RegulatorMappingResultType.SUCCESSFUL).size());
        Assert.assertEquals("There should be 1 partially successful result.", 1, resultsByType.get(RegulatorMappingResultType.PARTIALLY_SUCCESSFUL).size());
        Assert.assertEquals("There should be 1 failed result.", 1, resultsByType.get(RegulatorMappingResultType.FAILED).size());
    }
    
    @Test
    public void test_cancel() {
        DeviceCollection mockRegulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.SUCCESS);
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.deviceComplete(successRegulator);
        
        Assert.assertFalse("Task was not canceled, but shows canceled.", task.isCanceled());
        task.cancel();
        Assert.assertTrue("Task was canceled, but shows not canceled.", task.isCanceled());
    }
    
    @Test
    public void test_error() {
        DeviceCollection mockRegulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(mockRegulators, YukonUserContext.system);
        
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_BLOCK_ENABLE, RegulatorPointMappingResult.SUCCESS);
        task.addResult(successRegulator, RegulatorPointMapping.AUTO_REMOTE_CONTROL, RegulatorPointMappingResult.SUCCESS_WITH_OVERWRITE);
        task.deviceComplete(successRegulator);
        
        Assert.assertFalse("Task shows an error when no error occurred.", task.isErrorOccurred());
        Exception e = new Exception("Everything is broken!");
        task.errorOccurred(e);
        Assert.assertTrue("Task shows no error when an error did occur.", task.isErrorOccurred());
        Assert.assertSame("Thrown exception does not match exception from task.", e, task.getError());
    }
    
    private DeviceCollection getMockDeviceCollection() {
        DeviceCollection regulators = new DeviceCollection() {
            
            @Override
            public int getDeviceCount() {
                return 3;
            }
            
            //UNIMPLEMENTED
            @Override public List<SimpleDevice> getDeviceList() {return null;}
            @Override public Iterator<SimpleDevice> iterator() {return null;}
            @Override public List<SimpleDevice> getDevices(int start, int size) {return null;}
            @Override public Map<String, String> getCollectionParameters() {return null;}
            @Override public MessageSourceResolvable getDescription() {return null;}
            @Override public DeviceCollectionType getCollectionType() {return null;}
        };
        
        return regulators;
    }
}