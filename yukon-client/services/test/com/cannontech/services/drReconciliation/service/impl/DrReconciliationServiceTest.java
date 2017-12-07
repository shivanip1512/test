package com.cannontech.services.drReconciliation.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.dao.impl.LMGroupDaoImpl;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.dao.ExpressComReportedAddressRelay;
import com.cannontech.dr.dao.impl.ExpressComReportedAddressDaoImpl;
import com.cannontech.maintenance.task.dao.DrReconciliationDao;
import com.cannontech.maintenance.task.service.impl.DrReconciliationServiceImpl;
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
                    lmGroupAddressing.setGroupId(2);
                    lmGroupAddressing.setUsage("SGBFZUPRL");
                    lmGroupAddressing.setSpid(3);
                    lmGroupAddressing.setGeo(4);
                    lmGroupAddressing.setSubstation(9);
                    lmGroupAddressing.setFeeder(1);
                    lmGroupAddressing.setZip(7);
                    lmGroupAddressing.setUser(8);
                    lmGroupAddressing.setProgram(9);
                    lmGroupAddressing.setSplinter(10);
                    lmGroupAddressing.setRelay("167");
                    return lmGroupAddressing;
                } 
                return null;
            }
        }).anyTimes();

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
                }
                return null;
            }
        }).anyTimes();

        drReconciliationDao.getLcrEnrolledInMultipleGroup(EasyMock.<Set<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            Multimap<Integer, Integer> lcrs = HashMultimap.create();
            lcrs.put(3, 1);
            lcrs.put(3, 3);
            return lcrs;
        }).anyTimes();

        replay(drReconciliationDao);
        replay(lmGroupDaoImpl);
        ReflectionTestUtils.setField(dr, "drReconciliationDao", drReconciliationDao);
        ReflectionTestUtils.setField(dr, "lmGroupDaoImpl", lmGroupDaoImpl);
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertTrue("LCR correct address ", conflictingLCR.size() == 0);
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
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
            return lcrAddressing;
            
        }).anyTimes();

        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);

        Set<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertTrue("Group did not match ", conflictingLCR.size() == 0);
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
