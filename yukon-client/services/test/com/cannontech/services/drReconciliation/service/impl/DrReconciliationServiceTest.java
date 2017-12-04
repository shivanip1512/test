package com.cannontech.services.drReconciliation.service.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
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
        expectLastCall().andAnswer(() -> {
            ExpressComAddressView lmGroupAddressing = new ExpressComAddressView();
            lmGroupAddressing.setGroupId(1);
            lmGroupAddressing.setUsage("SGBFZUPRL");
            lmGroupAddressing.setSerialNumber("1");
            lmGroupAddressing.setRouteId(2);
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
        }).anyTimes();

        drReconciliationDao.getEnrolledRfnLcrForGroup(EasyMock.anyInt());
        expectLastCall().andAnswer(() -> {
            List<Integer> lcrs = new ArrayList<>();
            lcrs.add(1);
            return lcrs;
        }).anyTimes();

        ExpressComReportedAddressDaoImpl expressComDaoImpl = createNiceMock(ExpressComReportedAddressDaoImpl.class);
        expressComDaoImpl.getCurrentAddresses(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {

            ExpressComReportedAddress lcrAddressing = new ExpressComReportedAddress();
            lcrAddressing.setDeviceId(1);
            lcrAddressing.setSpid(3);
            lcrAddressing.setGeo(4);
            lcrAddressing.setSubstation(5);
            lcrAddressing.setFeeder(6);
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
            relay3.setProgram(910);
            relay3.setSplinter(125);

            Set<ExpressComReportedAddressRelay> relays = new HashSet<>();
            relays.add(relay1);
            relays.add(relay2);
            relays.add(relay3);
            lcrAddressing.setRelays(relays);

            List<ExpressComReportedAddress> lcrs = new ArrayList<>();
            lcrs.add(lcrAddressing);

            return lcrs;
        }).anyTimes();
       
        drReconciliationDao.getLcrEnrolledInMultipleGroup(EasyMock.<List<Integer>> anyObject());
        expectLastCall().andAnswer(() -> {
            Multimap<Integer, Integer> lcrs = HashMultimap.create();

            return lcrs;
        }).anyTimes();

        replay(drReconciliationDao);
        replay(lmGroupDaoImpl);
        replay(expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "drReconciliationDao", drReconciliationDao);
        ReflectionTestUtils.setField(dr, "expressComDaoImpl", expressComDaoImpl);
        ReflectionTestUtils.setField(dr, "lmGroupDaoImpl", lmGroupDaoImpl);
    }

    @Test
    public void testFindLCRWithConflictingAddressing() {
        List<Integer> conflictingLCR = dr.getLCRWithConflictingAddressing();
        assertTrue("LCR correct address ", conflictingLCR.size() == 0);
    }
}
