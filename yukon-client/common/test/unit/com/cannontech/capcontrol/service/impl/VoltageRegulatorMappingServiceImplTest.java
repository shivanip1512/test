package com.cannontech.capcontrol.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.model.RegulatorMappingTask;
import com.cannontech.capcontrol.service.VoltageRegulatorMappingService;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.mock.MockPointDao;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.StubServerDatabaseCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class VoltageRegulatorMappingServiceImplTest {
    
    private final LiteYukonPAObject pao1 = new LiteYukonPAObject(1, "GoRegulator", PaoCategory.DEVICE, 
                                                                 PaoClass.CAPCONTROL, PaoType.GANG_OPERATED, 
                                                               "Reg1Description", "F");
    private final LiteYukonPAObject pao2 = new LiteYukonPAObject(2, "PoRegulator", PaoCategory.DEVICE, 
                                                                 PaoClass.CAPCONTROL, PaoType.PHASE_OPERATED, 
                                                               "Reg2Description", "F");
    private final LiteYukonPAObject pao3 = new LiteYukonPAObject(3, "LTC", PaoCategory.DEVICE, 
                                                                 PaoClass.CAPCONTROL, PaoType.LOAD_TAP_CHANGER, 
                                                                 "Reg3Description", "F");
    
    private Map<YukonPao, ImmutableMap<RegulatorPointMapping, Integer>> paoToPointMappingToPointId = 
            new ImmutableMap.Builder<YukonPao, ImmutableMap<RegulatorPointMapping, Integer>>()
                .put(pao1, new ImmutableMap.Builder<RegulatorPointMapping, Integer>()
                               .put(RegulatorPointMapping.AUTO_REMOTE_CONTROL, 101)
                               .put(RegulatorPointMapping.AUTO_BLOCK_ENABLE, 102)
                               .put(RegulatorPointMapping.TAP_UP, 103)
                               .put(RegulatorPointMapping.TAP_DOWN, 104)
                               .put(RegulatorPointMapping.TAP_POSITION, 105)
                               .put(RegulatorPointMapping.TERMINATE, 106)
                               .put(RegulatorPointMapping.VOLTAGE_X, 107)
                               .put(RegulatorPointMapping.VOLTAGE_Y, 108)
                               .put(RegulatorPointMapping.KEEP_ALIVE, 109)
                               .put(RegulatorPointMapping.KEEP_ALIVE_TIMER, 110)
                               .put(RegulatorPointMapping.FORWARD_SET_POINT, 111)
                               .put(RegulatorPointMapping.FORWARD_BANDWIDTH, 112)
                               .build())
                .put(pao2, new ImmutableMap.Builder<RegulatorPointMapping, Integer>()
                               .put(RegulatorPointMapping.AUTO_REMOTE_CONTROL, 201)
                               .put(RegulatorPointMapping.AUTO_BLOCK_ENABLE, 202)
                               .put(RegulatorPointMapping.TAP_UP, 203)
                               .put(RegulatorPointMapping.TAP_DOWN, 204)
                               .put(RegulatorPointMapping.TAP_POSITION, 205)
                               .put(RegulatorPointMapping.TERMINATE, 206)
                               .put(RegulatorPointMapping.VOLTAGE_X, 207)
                               .put(RegulatorPointMapping.VOLTAGE_Y, 208)
                               .put(RegulatorPointMapping.KEEP_ALIVE, 209)
                               .put(RegulatorPointMapping.KEEP_ALIVE_TIMER, 210)
                               .put(RegulatorPointMapping.FORWARD_SET_POINT, 211)
                               .put(RegulatorPointMapping.FORWARD_BANDWIDTH, 212)
                               .build())
                .build();
            
    private Map<String, Integer> pointNameToIdMap = 
            new ImmutableMap.Builder<String, Integer>().put("GoRegulator-Auto Remote Control", 101) //Will overwrite a pointId of 1
                                                       .put("GoRegulator-Auto Block Enable", 102) //Will "overwrite" itself
                                                       .put("GoRegulator-Tap Up", 103)
                                                       .put("GoRegulator-Tap Down", 104)
                                                       .put("GoRegulator-Tap Position", 105)
                                                       .put("GoRegulator-Terminate", 106)
                                                       .put("GoRegulator-Voltage X", 107)
                                                       .put("GoRegulator-Voltage Y", 108)
                                                       .put("GoRegulator-Keep Alive", 109)
                                                       .put("GoRegulator-Keep Alive Timer", 110)
                                                       .put("GoRegulator-Forward Set Point", 111)
                                                       .put("GoRegulator-Forward Bandwidth", 112)
                                                       .put("PoRegulator-Auto Block Enable", 202)
                                                       .put("PoRegulator-Tap Up", 203)
                                                       .put("PoRegulator-Tap Down", 204)
                                                       .put("PoRegulator-Tap Position", 205)
                                                       .put("PoRegulator-Terminate", 206)
                                                       .put("PoRegulator-Voltage X", 207)
                                                       .put("PoRegulator-Voltage Y", 208)
                                                       .put("PoRegulator-Keep Alive", 209)
                                                       .put("PoRegulator-Keep Alive Timer", 210)
                                                       .put("PoRegulator-Forward Set Point", 211)
                                                       .put("PoRegulator-Forward Bandwidth", 212)
                                                       .build();
    
    @SuppressWarnings("unchecked")
    @Test
    public void test_initiateTask() {
        
        //Set up the mock executor to expect a single call to .execute(RegulatorMappingProcessor)
        Executor mockExecutor = EasyMock.createStrictMock(Executor.class);
        mockExecutor.execute(EasyMock.anyObject(VoltageRegulatorMappingServiceImpl.RegulatorMappingProcessor.class));
        EasyMock.replay(mockExecutor);
        
        //Set up the mock recent results cache to expect a single call to .addResult(RegulatorMappingTask)
        //and return a task id string
        RecentResultsCache<RegulatorMappingTask> mockCache = EasyMock.createStrictMock(RecentResultsCache.class);
        EasyMock.expect(mockCache.addResult(EasyMock.anyObject(RegulatorMappingTask.class))).andReturn("TASKID");
        EasyMock.replay(mockCache);
        
        //Create other dependencies
        DeviceCollection regulators = getMockDeviceCollection();
        
        //Create service
        VoltageRegulatorMappingService service = new VoltageRegulatorMappingServiceImpl();
        ReflectionTestUtils.setField(service, "executor", mockExecutor);
        ReflectionTestUtils.setField(service, "resultsCache", mockCache);
        
        //Method call
        service.initiateTask(regulators, YukonUserContext.system);
    }
    
    @Test
    public void test_getAllTasks() {
        
        //Create dependencies
        Executor mockExecutor = EasyMock.createNiceMock(Executor.class);
        EasyMock.replay(mockExecutor);
        
        RecentResultsCache<RegulatorMappingTask> cache =  new RecentResultsCache<>();
        
        DeviceCollection regulators = getMockDeviceCollection();
        DeviceCollection regulators2 = getMockDeviceCollection();
        
        //Create service
        VoltageRegulatorMappingService service = new VoltageRegulatorMappingServiceImpl();
        ReflectionTestUtils.setField(service, "executor", mockExecutor);
        ReflectionTestUtils.setField(service, "resultsCache", cache);
        
        //Initiate two tasks
        String taskId1 = service.initiateTask(regulators, YukonUserContext.system);
        String taskId2 = service.initiateTask(regulators2, YukonUserContext.system);
        
        //Get all tasks. Should be ordered by their start time
        List<RegulatorMappingTask> allTasks = service.getAllTasks();
        Assert.assertEquals("Cache should return 2 tasks in getAllTasks() call.", 2, allTasks.size());
        
        RegulatorMappingTask task1 = allTasks.get(0);
        RegulatorMappingTask task2 = allTasks.get(1);
        Assert.assertEquals("Tasks should be ordered by start time.", task1.getTaskId(), taskId1);
        Assert.assertEquals("Tasks should be ordered by start time.", task2.getTaskId(), taskId2);
    }
    
    @Test
    public void test_processor() {
        
        //Mock cache will return 3 paos
        IDatabaseCache serverDatabaseCache = getMockDatabaseCache();
        
        //Mock pointDao.findAllPointsForName returns a single point id from the pointNameToIdMap
        //Returns no points for the LTC pao
        //Returns no points for PoRegulator-Auto Remote Control
        PointDao pointDao = getMockPointDao();
        
        //This is where the magic happens. This mock checks calls to addAssignment to ensure that they match what
        //we expect from the test data.
        ExtraPaoPointAssignmentDao eppaDao = getMockEppaDao();
        
        //This mock expects to have 2 methods hit twice, for both Voltage Y points that will be mapped.
        CcMonitorBankListDao ccmblDao = EasyMock.createStrictMock(CcMonitorBankListDao.class);
        EasyMock.expect(ccmblDao.deleteNonMatchingRegulatorPoint(1, 108)).andReturn(true);
        EasyMock.expect(ccmblDao.addRegulatorPoint(1)).andReturn(1);
        EasyMock.expect(ccmblDao.deleteNonMatchingRegulatorPoint(2, 208)).andReturn(true);
        EasyMock.expect(ccmblDao.addRegulatorPoint(2)).andReturn(2);
        EasyMock.replay(ccmblDao);
        
        RecentResultsCache<RegulatorMappingTask> cache =  new RecentResultsCache<>();
        
        //Create task
        DeviceCollection regulators = getMockDeviceCollection();
        RegulatorMappingTask task = new RegulatorMappingTask(regulators, YukonUserContext.system);
        
        //Create service and inject dependencies
        VoltageRegulatorMappingServiceImpl service = new VoltageRegulatorMappingServiceImpl();
        ReflectionTestUtils.setField(service, "resultsCache", cache);
        ReflectionTestUtils.setField(service, "serverDatabaseCache", serverDatabaseCache);
        ReflectionTestUtils.setField(service, "pointDao", pointDao);
        ReflectionTestUtils.setField(service, "extraPaoPointAssignmentDao", eppaDao);
        ReflectionTestUtils.setField(service, "ccMonitorBankListDao", ccmblDao);
        
        //Create processor
        VoltageRegulatorMappingServiceImpl.RegulatorMappingProcessor processor = 
                service.new RegulatorMappingProcessor(task);
        
        //Do the point mapping
        processor.run();
    }
    
    private ExtraPaoPointAssignmentDao getMockEppaDao() {
        return new ExtraPaoPointAssignmentDao() {
            
            @Override
            public void addAssignment(YukonPao pao, int pointId, RegulatorPointMapping regulatorMapping,
                                      boolean overwriteExistingPoint) {
                
                //First check that the pao is one we expected (i.e. has a value in our expected values map)
                Map<RegulatorPointMapping, Integer> expectedValuesForPao = paoToPointMappingToPointId.get(pao);
                Assert.assertNotNull("Unexpected pao: " + pao, expectedValuesForPao);
                
                //Check that the regulatorPointMapping is expected for this pao
                Integer expectedPointId = expectedValuesForPao.get(regulatorMapping);
                Assert.assertNotNull("Unexpected regulator mapping passed for pao: " + pao + ", "
                                     + regulatorMapping, expectedPointId);
                
                //Check that the mapping being added matches the expected mapping
                Assert.assertEquals("Incorrect pointId for pao and mapping: " + pao + ", "
                                    + regulatorMapping, expectedPointId.intValue(), pointId);
            }

            @Override
            public Map<RegulatorPointMapping, Integer> getAssignments(PaoIdentifier paoIdentifier) {
                if (paoIdentifier == pao1.getPaoIdentifier()) {
                    Map<RegulatorPointMapping, Integer> map = new HashMap<>();
                    map.put(RegulatorPointMapping.AUTO_REMOTE_CONTROL, 1); //This is a different mapping
                    map.put(RegulatorPointMapping.AUTO_BLOCK_ENABLE, 102); //Same mapping that will be attempted
                    return map;
                } else {
                    //return empty map - no existing assignments
                    return new HashMap<RegulatorPointMapping, Integer>();
                }
            }
            
            //Unimplemented
            @Override public int getPointId(YukonPao pao, RegulatorPointMapping regulatorPointMapping) {return 0;}
            @Override public void saveAssignments(YukonPao pao, Map<RegulatorPointMapping, Integer> pointMappings) {}
            @Override public void removeAssignments(YukonPao pao) {}
            @Override public LitePoint getLitePoint(YukonPao regulator, RegulatorPointMapping regulatorMapping) {return null;}
            @Override public boolean removeAssignment(PaoIdentifier paoIdentifier, RegulatorPointMapping regulatorMapping) {return false;}
        };
    }
    
    private PointDao getMockPointDao() {
        return new MockPointDao() {
            @Override
            public List<LitePoint> findAllPointsWithName(String name) {
                List<LitePoint> points = new ArrayList<>();
                if (name.startsWith("LTC") || name.equals("PoRegulator-Auto Remote Control")) {
                    //add no points
                } else {
                    Integer pointId = pointNameToIdMap.get(name);
                    Assert.assertNotNull("Point name not mapped in test: " + name, pointId);
                    points.add(new LitePoint(pointId));
                }
                return points;
            }
        };
    }
    
    private IDatabaseCache getMockDatabaseCache() {
        return new StubServerDatabaseCache() {
            @Override
            public Map<Integer, LiteYukonPAObject> getAllPaosMap() {
                Map<Integer, LiteYukonPAObject> allPaos = new HashMap<>();
                
                allPaos.put(1, pao1);
                allPaos.put(2, pao2);
                allPaos.put(3, pao3);
                return allPaos;
            }
        };
    }
    
    private DeviceCollection getMockDeviceCollection() {
        DeviceCollection regulators = new DeviceCollection() {
            List<SimpleDevice> devices = ImmutableList.of(new SimpleDevice(pao1), 
                                                          new SimpleDevice(pao2), 
                                                          new SimpleDevice(pao3));
            
            @Override
            public int getDeviceCount() {
                return devices.size();
            }
            
            @Override 
            public List<SimpleDevice> getDeviceList() {
                return devices;
            }
            
            @Override 
            public Iterator<SimpleDevice> iterator() {
                return devices.iterator();
            }
            
            //UNIMPLEMENTED
            @Override public List<SimpleDevice> getDevices(int start, int size) {return null;}
            @Override public Map<String, String> getCollectionParameters() {return null;}
            @Override public MessageSourceResolvable getDescription() {return null;}
            @Override public DeviceCollectionType getCollectionType() {return null;}
        };
        return regulators;
    }
}
