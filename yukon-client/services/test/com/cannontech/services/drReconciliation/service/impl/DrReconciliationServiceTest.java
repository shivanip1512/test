package com.cannontech.services.drReconciliation.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.impl.LMGroupDaoImpl;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.dao.impl.ExpressComReportedAddressDaoImpl;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.maintenance.task.service.impl.DrReconciliationServiceImpl;
import com.cannontech.message.dispatch.message.LitePointData;
import com.cannontech.services.mock.MockAsyncDynamicDataSourceImpl;
import com.cannontech.services.mock.MockAttributeServiceImpl;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.ExpressComAddressView;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DrReconciliationServiceTest {
    private DrReconciliationServiceImpl dr;

    @Before
    public void setUp() throws Exception {
        dr = new DrReconciliationServiceImpl();
        DrReconciliationDao drReconciliationDao = createNiceMock(DrReconciliationDao.class);

        drReconciliationDao.getGroupsWithRfnDeviceEnrolled();
        expectLastCall().andAnswer(() -> {
            List<Integer> groups = new ArrayList<>();
            groups.add(1);
            groups.add(2);
            groups.add(3);
            groups.add(4);
            groups.add(5);
            groups.add(6);
            groups.add(7);
            groups.add(8);
            groups.add(9);
            groups.add(10);
            groups.add(11);
            return groups;
        }).anyTimes();

        LMGroupDaoImpl lmGroupDaoImpl = createNiceMock(LMGroupDaoImpl.class);

        lmGroupDaoImpl.getExpressComAddressing(EasyMock.anyInt());
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                if ((Integer) getCurrentArguments()[0] == 1) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(1);
                    lmGroupAddressing.setUsage("SGBFZUPRL");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(5);
                    lmGroupAddressing.setFeeder(1);
                    lmGroupAddressing.setZip(7);
                    lmGroupAddressing.setUser(8);
                    lmGroupAddressing.setProgram(9);
                    lmGroupAddressing.setSplinter(10);
                    lmGroupAddressing.setRelay("167");
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 2) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(2);
                    lmGroupAddressing.setUsage("SGBFZUPRL");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(5);
                    lmGroupAddressing.setFeeder(1);
                    lmGroupAddressing.setZip(7);
                    lmGroupAddressing.setUser(8);
                    lmGroupAddressing.setProgram(9);
                    lmGroupAddressing.setSplinter(10);
                    lmGroupAddressing.setRelay("167");
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 3) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(3);
                    lmGroupAddressing.setUsage("S");
                    lmGroupAddressing.setSpid(3);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 4) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(4);
                    lmGroupAddressing.setUsage("SG");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 5) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(5);
                    lmGroupAddressing.setUsage("SGB");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(9);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 6) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(6);
                    lmGroupAddressing.setUsage("SGBF");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(9);
                    lmGroupAddressing.setFeeder(1);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 7) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(7);
                    lmGroupAddressing.setUsage("SGBFZ");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(9);
                    lmGroupAddressing.setFeeder(1);
                    lmGroupAddressing.setZip(7);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 8) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(8);
                    lmGroupAddressing.setUsage("SGBFZ");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(9);
                    lmGroupAddressing.setFeeder(1);
                    lmGroupAddressing.setZip(8); 
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 9) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(9);
                    lmGroupAddressing.setUsage("SF");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setFeeder(1);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 10) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(10);
                    lmGroupAddressing.setUsage("SF");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setFeeder(2);
                    return lmGroupAddressing;
                } else if ((Integer) getCurrentArguments()[0] == 11) {
                    ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
                    lmGroupAddressing.setGroupId(11);
                    lmGroupAddressing.setUsage("SF");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setFeeder(3);
                    return lmGroupAddressing;
                } 
                return null;
            }
        }).anyTimes();

        InventoryBaseDao inventoryBaseDao = createNiceMock(InventoryBaseDao.class);
        inventoryBaseDao.getHardwareByDeviceId(EasyMock.anyInt());
        expectLastCall().andAnswer(() -> {
            Integer deviceId = (Integer) getCurrentArguments()[0];
            LiteLmHardwareBase lmhb = new LiteLmHardwareBase();
            switch (deviceId) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                lmhb.setManufacturerSerialNumber(deviceId.toString());
                break;
            }
            return lmhb;
        }).anyTimes();

        SystemEventLogService systemEventLogService = createNiceMock(SystemEventLogService.class);
        systemEventLogService.groupConflictLCRDetected(EasyMock.anyObject());
        expectLastCall().anyTimes();

        drReconciliationDao.getEnrolledRfnLcrForGroup(EasyMock.anyInt());
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                List<Integer> lcrs = new ArrayList<>();
                if ((Integer) getCurrentArguments()[0] == 1) {
                    lcrs.add(1);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 2) {
                    lcrs.add(2);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 3) {
                    lcrs.add(3);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 4) {
                    lcrs.add(3);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 5) {
                    lcrs.add(3);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 6) {
                    lcrs.add(3);
                    lcrs.add(4);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 7) {
                    lcrs.add(4);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 8) {
                    lcrs.add(4);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 9) {
                    lcrs.add(5);
                    lcrs.add(6);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 10) {
                    lcrs.add(5);
                    return lcrs;
                } else if  ((Integer) getCurrentArguments()[0] == 11) {
                    lcrs.add(6);
                    return lcrs;
                }
                
                return null;
            }
        }).anyTimes();

        drReconciliationDao.getLcrEnrolledInMultipleGroup(EasyMock.<Set<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            Multimap<Integer, Integer> lcrs = HashMultimap.create();
            lcrs.put(3, 3);
            lcrs.put(3, 4);
            lcrs.put(3, 5);
            lcrs.put(3, 6);
            lcrs.put(4, 5);
            lcrs.put(4, 6);
            lcrs.put(4, 7);
            lcrs.put(4, 8);
            lcrs.put(5, 9);
            lcrs.put(5, 10);
            lcrs.put(6, 9);
            lcrs.put(6, 11);
            return lcrs;
        }).anyTimes();

        replay(drReconciliationDao);
        replay(lmGroupDaoImpl);
        replay(inventoryBaseDao);
        replay(systemEventLogService);
        ReflectionTestUtils.setField(dr, "drReconciliationDao", drReconciliationDao);
        ReflectionTestUtils.setField(dr, "lmGroupDaoImpl", lmGroupDaoImpl);
        ReflectionTestUtils.setField(dr, "inventoryBaseDao", inventoryBaseDao);
        ReflectionTestUtils.setField(dr, "systemEventLogService", systemEventLogService);

        List<LiteYukonPAObject> paos = new ArrayList<>(7);
        PaoDao paoDao = createNiceMock(PaoDao.class);
        paoDao.getLiteYukonPaos(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<Integer> args = (List<Integer>) getCurrentArguments()[0];
            for (int i = 1; i <= 7; i++) {
                LiteYukonPAObject paoObject = new LiteYukonPAObject(args.get(i - 1));
                if (i == 7 || i == 2) {
                    paoObject.setPaoType(PaoType.LCR6600_RFN);
                } else {
                    paoObject.setPaoType(PaoType.LCR6200_RFN);
                }
                paos.add(paoObject);
            }
            return paos;
        });

        replay(paoDao);
        ReflectionTestUtils.setField(dr, "paoDao", paoDao);
        ReflectionTestUtils.setField(dr, "attributeService", new MockAttributeServiceImpl());
        ReflectionTestUtils.setField(dr, "asyncDynamicDataSource", new MockAsyncDynamicDataSourceImpl() {
            @Override
            public Set<LitePointData> getPointDataOnce(Set<Integer> pointIds) {
                // Setting point data for pointIds : 1 - 7
                Set<LitePointData> pointValues = new HashSet<>();
                for (int i = 1; i <= 7; i++) {
                    LitePointData litePointData = new LitePointData();
                    litePointData.setId(i);
                    switch (i) {
                    case 2:
                    case 5:
                    case 3:
                        litePointData.setValue(1);
                        break;
                    case 6:
                    case 1:
                        litePointData.setValue(2);
                        break;
                    case 4:
                    case 7:
                        litePointData.setValue(0);
                        break;
                    }
                    pointValues.add(litePointData);
                }
                return pointValues;
            }
        });
    }

    @Test
    public void testFindLCRWithConflictingAddressing() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);
        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertTrue("LCR correct address ", conflictingLCR.size() == 1);
    }

    // Incorrect Program Set on one relay
 @Test
    public void incorrectProgramOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findFirst().get().getRelays().stream().forEach(relay -> {
                relay.setProgram(1);
            });
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Splinter Set on one relay
    @Test
    public void incorrectSplinterOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findFirst().get().getRelays().stream().findFirst().get().setSplinter(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Spid Set in addressing
    @Test
    public void incorrectSpidOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findAny().get().setGeo(6);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Geo Set in addressing
    @Test
    public void incorrectGeoOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findAny().get().setGeo(5);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Substation Set in addressing
    @Test
    public void incorrectSubstationOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findAny().get().setSubstation(6);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Feeder Set on one relay
    @Test
    public void incorrectFeederOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findAny().get().setFeeder(10);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Zip Set in addressing
    @Test
    public void incorrectZipOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findAny().get().setZip(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }
    // Incorrect Uda Set in addressing
    @Test
    public void incorrectUdaOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().findAny().get().setUda(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Multiple LCR in single group, correct group addressing
    @Test
    public void correctGroupAddressing() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().filter(e -> e.getDeviceId() != 3);
            return lcrAddressing;
            
        }).anyTimes();

        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);

        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertTrue("Group did not match ", conflictingLCR.size() == 1);
    }
    
    // Multiple LCR in single group, group addressing incorrect - Group 7 and 8 have different zip code
    @Test
    public void inCorrectGroupAddressing() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().filter(e -> e.getDeviceId() != 4);
            return lcrAddressing;
            
        }).anyTimes();

        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);

        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertTrue("Group did not match ", !conflictingLCR.contains(4));
    }

    // Two groups with correct feeders
    @Test
    public void correctFeederInGroupAddressing() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().forEach( e -> {
                if (e.getDeviceId() == 6) {
                    e.setSpid(100);
                }
            }); 
            return lcrAddressing;
            
        }).anyTimes();

        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);

        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertTrue("Group did not match ", conflictingLCR.contains(6));
    }
    
    // Two groups with incorrect feeders
    @Test
    public void inCorrectFeederInGroupAddressing() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            @SuppressWarnings("unchecked")
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing((List<Integer>) EasyMock.getCurrentArguments()[0]);
            lcrAddressing.stream().forEach( e -> {
                if (e.getDeviceId() == 5) {
                    e.setSpid(100);
                }
            }); 
                 return lcrAddressing;
            
        }).anyTimes();

        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        Set<Integer> conflictingLCR = ReflectionTestUtils.invokeMethod(dr, "getLCRWithConflictingAddressing");
        assertTrue("Group did not match ", !conflictingLCR.contains(5));
    }

    @Test
    public void test_compareExpectedServiceStatusWithReportedLcrs() {
        Map<Integer, List<Integer>> lcrsToSendCommand = new HashMap<>(2);
        List<Integer> inServiceExppectedLcrs = new ArrayList<>(4);
        inServiceExppectedLcrs.add(1);
        inServiceExppectedLcrs.add(2);
        inServiceExppectedLcrs.add(3);
        inServiceExppectedLcrs.add(4);

        List<Integer> outOfServiceExppectedLcrs = new ArrayList<>(3);
        outOfServiceExppectedLcrs.add(5);
        outOfServiceExppectedLcrs.add(6);
        outOfServiceExppectedLcrs.add(7);

        Integer[] reportedInServiceLcrs = { 1, 4 };
        Integer[] reportedOutOfServiceLcrs = { 5, 7 };

        lcrsToSendCommand.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL, inServiceExppectedLcrs);
        lcrsToSendCommand.put(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL, outOfServiceExppectedLcrs);

        ReflectionTestUtils.invokeMethod(dr, "compareExpectedServiceStatusWithReportedLcrs", lcrsToSendCommand);
        assertArrayEquals("Devices to send InServcie Message : ", reportedInServiceLcrs,
            lcrsToSendCommand.get(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL).toArray());

        assertArrayEquals("Devices to send Out Of Service Message : ", reportedOutOfServiceLcrs,
            lcrsToSendCommand.get(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL).toArray());
    }

    private List<ExpressComReportedAddress> getDefaultLCRAddressing(List<Integer> lcrs) {
        
        final List<ExpressComReportedAddress> lcrsAddress = new ArrayList<>();
        lcrs.stream().forEach(lcr -> {
            ExpressComReportedAddress lcrAddressing = new ExpressComReportedAddress();
            lcrAddressing.setDeviceId(lcr);
            lcrAddressing.setSpid(3);
            lcrAddressing.setGeo(4);
            lcrAddressing.setSubstation(5);
            lcrAddressing.setFeeder(1);
            lcrAddressing.setZip(7);
            lcrAddressing.setUda(8);
            
            ExpressComReportedAddressRelay relay1 = new ExpressComReportedAddressRelay();
            relay1.setRelayNumber(1);
            relay1.setProgram(9);
            relay1.setSplinter(10);

            ExpressComReportedAddressRelay relay2 = new ExpressComReportedAddressRelay();
            relay2.setRelayNumber(6);
            relay2.setProgram(9);
            relay2.setSplinter(1);

            ExpressComReportedAddressRelay relay3 = new ExpressComReportedAddressRelay();
            relay3.setRelayNumber(7);
            relay3.setProgram(9);
            relay3.setSplinter(125);
            
            Set<ExpressComReportedAddressRelay> relays = new HashSet<>();
            relays.add(relay1);
            relays.add(relay2);
            relays.add(relay3);
            lcrAddressing.setRelays(relays);
            
            lcrsAddress.add(lcrAddressing);
        });
        return lcrsAddress;
    }
}
