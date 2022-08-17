package com.cannontech.dr.rfn.dao.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.easymock.EasyMock;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.rfn.dao.PerformanceVerificationDao;
import com.cannontech.dr.rfn.model.DeviceStatus;
import com.cannontech.system.dao.GlobalSettingDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/com/cannontech/common/daoTestContext.xml"})
public class PerformanceVerificationDaoTestImpl {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private PerformanceVerificationDao performanceVerificationDao;
    
    @Before
    public void setUp() throws Exception {
        performanceVerificationDao = new PerformanceVerificationDaoImpl();
        ReflectionTestUtils.setField(performanceVerificationDao, "jdbcTemplate", jdbcTemplate);
        
        prepareBaseData();
    }
    
    @After
    public void cleanUp() throws Exception {
        cleanBaseData();
    }
    
    private void cleanBaseData() {
        jdbcTemplate.update("DELETE FROM InventoryBase");
        jdbcTemplate.update("DELETE FROM LMHardwareBase");
        jdbcTemplate.update("DELETE FROM ECToInventoryMapping");
        jdbcTemplate.update("DELETE FROM CustomerAccount");
        jdbcTemplate.update("DELETE FROM LmHardwareControlGroup");
        jdbcTemplate.update("DELETE FROM RfnBroadcastEventDeviceStatus");
        jdbcTemplate.update("DELETE FROM DynamicLcrCommunications");
    }
    
    private void prepareBaseData() {
        // Inventory Base
        String insertInventorySql = "INSERT INTO InventoryBase (InventoryID, AccountID, CategoryID, DeviceID, CurrentStateID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertInventorySql, 101, 111, 1034, 1, 20176);
        jdbcTemplate.update(insertInventorySql, 102, 111, 1034, 2, 20176);
        jdbcTemplate.update(insertInventorySql, 103, 111, 1034, 3, 20176);
        
        // LM Hardware Base
        String insertLMHardwareSql = "INSERT INTO LMHardwareBase (InventoryID, LMHardwareTypeID, RouteID, ConfigurationID) VALUES (?,?,?,?)";
        jdbcTemplate.update(insertLMHardwareSql, 101, 20601, 0, 0);
        jdbcTemplate.update(insertLMHardwareSql, 102, 20601, 0, 0);
        jdbcTemplate.update(insertLMHardwareSql, 103, 20601, 0, 0);
        
        // EcToInventoryMapping
        String insertEcToInventorySql = "INSERT INTO ECToInventoryMapping VALUES (?,?)";
        jdbcTemplate.update(insertEcToInventorySql, 32, 101);
        jdbcTemplate.update(insertEcToInventorySql, 32, 102);
        jdbcTemplate.update(insertEcToInventorySql, 32, 103);
        
        // CustomerAccount
        String insertCustomerAccountSql = "INSERT INTO CustomerAccount (AccountID, CustomerId) VALUES (?,?)";
        jdbcTemplate.update(insertCustomerAccountSql, 111, 999);
        
        // LmHardwareControlGroup
        String insertLmHardwareControlGroupSql = "INSERT INTO LmHardwareControlGroup (ControlEntryID, InventoryID, LMGroupID, AccountID, Type, Relay, UserIDFirstAction, UserIDSecondAction, ProgramId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertLmHardwareControlGroupSql, 1, 101, 1, 101, 1, 0, -2, -9999, 1);
        jdbcTemplate.update(insertLmHardwareControlGroupSql, 2, 102, 1, 101, 1, 0, -2, -9999, 1);
        jdbcTemplate.update(insertLmHardwareControlGroupSql, 3, 103, 1, 101, 1, 0, -2, -9999, 1);
    }

    /*@Test
    public void testGetFilteredCountForStatus_0() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        
        List<PerformanceVerificationMessageStatus> status = new ArrayList<>();
        status.add(PerformanceVerificationMessageStatus.UNKNOWN);
        assertTrue(performanceVerificationDao.getFilteredCountForStatus(1 , status, new ArrayList<>()) == 1);
    }*/
    
    @Test
    public void testGetUnknownCounts_NewEnrollement() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        
        Instant now = new Instant();
        Date date = now.minus(Duration.standardDays(3)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = 101";
        jdbcTemplate.update(updateEnrollementTimeSql, date);
        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);

        
        Map<DeviceStatus, Integer> unknownCount = performanceVerificationDao.getUnknownCounts(1);
        assertTrue(unknownCount.get(DeviceStatus.NEW_INSTALL_NOT_COMMUNICATING) == 1);
    }
    
    @Test
    public void testGetUnknownCounts_NotCommunicating() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        
        Instant now = new Instant();
        Date date = now.minus(Duration.standardDays(5)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = 101";
        jdbcTemplate.update(updateEnrollementTimeSql, date);
        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);

        
        Map<DeviceStatus, Integer> unknownCount = performanceVerificationDao.getUnknownCounts(1);
        assertTrue(unknownCount.get(DeviceStatus.NOT_COMMUNICATING) == 1);
    }
    
    @Test
    public void testGetUnknownCounts_CommunicatingWithLastCommTime() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        
        Instant now = new Instant();
        Date lastComm = now.minus(Duration.standardHours(1)).toDate();
        String insertDynamicLcrComm = "INSERT INTO DynamicLcrCommunications (DEVICEID, LastCommunication) VALUES (?, ?)"; 
        jdbcTemplate.update(insertDynamicLcrComm, 1, lastComm);

        Date enrollementDate = now.minus(Duration.standardDays(5)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = 101";
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate);
        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);

        
        Map<DeviceStatus, Integer> unknownCount = performanceVerificationDao.getUnknownCounts(1);
        assertTrue(unknownCount.get(DeviceStatus.COMMUNICATING) == 1);
    }
    
    @Test
    public void testGetUnknownCounts_NotCommunicatingWithLastCommTime() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        
        Instant now = new Instant();
        Date lastComm = now.minus(Duration.standardHours(4)).toDate();
        String insertDynamicLcrComm = "INSERT INTO DynamicLcrCommunications (DEVICEID, LastCommunication) VALUES (?, ?)"; 
        jdbcTemplate.update(insertDynamicLcrComm, 1, lastComm);
        
        Date enrollementDate = now.minus(Duration.standardDays(5)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = 101";
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate);
        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);

        
        Map<DeviceStatus, Integer> unknownCount = performanceVerificationDao.getUnknownCounts(1);
        assertTrue(unknownCount.get(DeviceStatus.NOT_COMMUNICATING) == 1);
    }
    
    @Test
    public void testGetFilteredCountForUnknownStatus_0() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "SUCCESS");
        
        Instant now = new Instant();
        Date enrollementDate = now.minus(Duration.standardDays(5)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = 101";
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate);
        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);
        
        assertTrue(performanceVerificationDao.getFilteredCountForUnknownStatus(1, new ArrayList<>()) == 0);
    }
    
    @Test
    public void testGetFilteredCountForUnknownStatus_1() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        
        Instant now = new Instant();
        Date enrollementDate = now.minus(Duration.standardDays(5)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = 101";
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate);

        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);
        
        assertTrue(performanceVerificationDao.getFilteredCountForUnknownStatus(1, new ArrayList<>()) == 1);
    }
    
    @Test
    public void testGetFilteredCountForUnknownStatus_2() {
        String insertSql = "INSERT INTO RfnBroadcastEventDeviceStatus(DeviceId, RfnBroadcastEventId, Result) VALUES (?,?,?)"; 
        jdbcTemplate.update(insertSql, 1, 1, "UNKNOWN");
        jdbcTemplate.update(insertSql, 2, 1, "UNKNOWN");
        jdbcTemplate.update(insertSql, 3, 1, "UNKNOWN");
        
        Instant now = new Instant();
        Date enrollementDate = now.minus(Duration.standardDays(5)).toDate();

        String updateEnrollementTimeSql = "UPDATE LmHardwareControlGroup set GroupEnrollStart = ? WHERE InventoryID = ?";
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate,101);
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate,102);
        jdbcTemplate.update(updateEnrollementTimeSql, enrollementDate,103);

        
        GlobalSettingDao globalSettingDao = createNiceMock(GlobalSettingDao.class);
        globalSettingDao.getInteger(EasyMock.anyObject());
        expectLastCall().andAnswer(() -> {
          return 3;
        }).anyTimes();

        replay(globalSettingDao);
        ReflectionTestUtils.setField(performanceVerificationDao, "globalSettingDao", globalSettingDao);
        
        assertTrue(performanceVerificationDao.getFilteredCountForUnknownStatus(1, new ArrayList<>()) == 3);
    }
    

}

