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
                    lmGroupAddressing.setFeeder(6);
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
                    lmGroupAddressing.setFeeder(6);
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
        expectLastCall().andAnswer(() -> {
            List<Integer> lcrs = new ArrayList<>();
            lcrs.add(1);
            return lcrs;
        }).anyTimes();

        drReconciliationDao.getLcrEnrolledInMultipleGroup(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            Multimap<Integer, Integer> lcrs = HashMultimap.create();

            lcrs.put(1, 1);
            lcrs.put(1, 2);

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
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertTrue("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Program Set on one relay
    @Test
    public void incorrectProgramOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findFirst().get().getRelays().stream().forEach(relay -> {
                relay.setProgram(1);
            });
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Splinter Set on one relay
    @Test
    public void incorrectSplinterOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findFirst().get().getRelays().stream().findFirst().get().setSplinter(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Spid Set in addressing
    @Test
    public void incorrectSpidOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setGeo(6);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Geo Set in addressing
    @Test
    public void incorrectGeoOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setGeo(5);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Substation Set in addressing
    @Test
    public void incorrectSubstationOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setSubstation(6);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Feeder Set on one relay
    @Test
    public void incorrectFeederOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setFeeder(5);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Zip Set in addressing
    @Test
    public void incorrectZipOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setZip(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Incorrect Uda Set in addressing
    @Test
    public void incorrectUdaOnLcr() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setUda(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    // Multiple LCR in single group, incorrect group addressing
    @Test
    public void incorrectGroupAddressing() {
        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);

        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            List<ExpressComReportedAddress> lcrAddressing = getDefaultLCRAddressing();
            lcrAddressing.stream().findAny().get().setUda(1);
            return lcrAddressing;
        }).anyTimes();
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertFalse("LCR correct address ", conflictingLCR.size() == 0);
    }

    private List<ExpressComReportedAddress> getDefaultLCRAddressing() {
        ExpressComReportedAddress lcrAddressing1 = new ExpressComReportedAddress();
        ExpressComReportedAddress lcrAddressing2 = new ExpressComReportedAddress();

        lcrAddressing1.setDeviceId(1);
        lcrAddressing1.setSpid(3);
        lcrAddressing1.setGeo(4);
        lcrAddressing1.setSubstation(5);
        lcrAddressing1.setFeeder(6);
        lcrAddressing1.setZip(7);
        lcrAddressing1.setUda(8);

        lcrAddressing2.setDeviceId(2);
        lcrAddressing2.setSpid(3);
        lcrAddressing2.setGeo(4);
        lcrAddressing2.setSubstation(5);
        lcrAddressing2.setFeeder(6);
        lcrAddressing2.setZip(7);
        lcrAddressing2.setUda(8);

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
        // Adding same relays to all LCR, these can be reset with each test case
        lcrAddressing1.setRelays(relays);
        lcrAddressing2.setRelays(relays);

        List<ExpressComReportedAddress> lcrs = new ArrayList<>();
        lcrs.add(lcrAddressing1);
        lcrs.add(lcrAddressing2);
        return lcrs;
    }
}
