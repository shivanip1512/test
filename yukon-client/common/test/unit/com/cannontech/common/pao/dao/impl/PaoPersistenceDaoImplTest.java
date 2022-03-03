package com.cannontech.common.pao.dao.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoPersistenceTypeHelper;
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.model.CompleteTwoWayCbc;
import com.cannontech.common.pao.model.CompleteWeatherLocation;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.YukonJdbcTemplate;

/**
 * This unit test uses an in-memory H2 database to test PaoPersistenceDaoImpl.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/com/cannontech/common/daoTestContext.xml",
    "/com/cannontech/common/pao/completePao.xml"})
@DirtiesContext
public class PaoPersistenceDaoImplTest {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoPersistenceTypeHelper paoPersistenceTypeHelper;

    private static int nextPaoId = 2;
    private static final int weatherLocationPaoId_create = nextPaoId++;
    private static final int weatherLocationPaoId_delete = nextPaoId++;
    private static final int ecobeeSmartSiPaoId_create = nextPaoId++;
    private static final int cbc7020PaoId_create = nextPaoId++;
    private static final int cbc7020PaoId_update = nextPaoId++;
    private static final int cbc7020PaoId_delete = nextPaoId++;
    private static final int ecobee3PaoId_create = nextPaoId++;
    private static final int ecobeeSmartPaoId_create = nextPaoId++;

    private PaoPersistenceDaoImpl paoPersistenceDao;
    private PaoDao paoDao;
    private UserPageDao userPageDao;

    @BeforeEach
    public void setUp() throws Exception {
        paoPersistenceDao = new PaoPersistenceDaoImpl(paoPersistenceTypeHelper);

        ReflectionTestUtils.setField(paoPersistenceDao, "jdbcTemplate", jdbcTemplate);

        paoDao = createMock(PaoDao.class);
        ReflectionTestUtils.setField(paoPersistenceDao, "paoDao", paoDao);

        userPageDao = createMock(UserPageDao.class);
        ReflectionTestUtils.setField(paoPersistenceDao, "userPageDao", userPageDao);
    }

    @Test
    public void testCreateAndRetreive_WEATHER_LOCATION() throws SQLException {
        CompleteWeatherLocation pao = new CompleteWeatherLocation();
        pao.setPaoName("awesomely cool PAO");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setStatistics("some stats");

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(weatherLocationPaoId_create, PaoType.WEATHER_LOCATION);

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);

        assertNull(pao.getPaoIdentifier(), "paoIdentifier should start out null");
        paoPersistenceDao.createPao(pao, PaoType.WEATHER_LOCATION);
        assertEquals(expectedPaoIdentifier, pao.getPaoIdentifier(), "did not update paoIdentifier properly");

        CompleteWeatherLocation fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteWeatherLocation.class);
        assertFalse(fromDb == pao);
        assertEquals(pao, fromDb, "retreived PAO differs from saved one");
    }

    @Test
    public void testCreateAndRetreive_ECOBEE_SMART_SI() {
        CompleteDevice pao = new CompleteDevice();
        pao.setPaoName("ecobeeSmartSi");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setAlarmInhibit(true);
        pao.setControlInhibit(true);
        pao.setStatistics("some stats");

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(ecobeeSmartSiPaoId_create, PaoType.ECOBEE_SMART_SI);

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.ECOBEE_SMART_SI);

        CompleteDevice fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteDevice.class);
        assertFalse(fromDb == pao);
        assertEquals(pao, fromDb, "retreived PAO differs from saved one");
    }
    
    @Test
    public void testCreateAndRetreive_ECOBEE_3() {
        CompleteDevice pao = new CompleteDevice();
        pao.setPaoName("ecobee3");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setAlarmInhibit(true);
        pao.setControlInhibit(true);
        pao.setStatistics("some stats");

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(ecobee3PaoId_create, PaoType.ECOBEE_3);

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.ECOBEE_3);

        CompleteDevice fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteDevice.class);
        assertFalse(fromDb == pao);
        assertEquals(pao, fromDb, "retreived PAO differs from saved one");
    }
    
    @Test
    public void testCreateAndRetreive_ECOBEE_SMART() {
        CompleteDevice pao = new CompleteDevice();
        pao.setPaoName("ecobeeSmart");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setAlarmInhibit(true);
        pao.setControlInhibit(true);
        pao.setStatistics("some stats");

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(ecobeeSmartPaoId_create, PaoType.ECOBEE_SMART);

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.ECOBEE_SMART);

        CompleteDevice fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteDevice.class);
        assertFalse(fromDb == pao);
        assertEquals(pao, fromDb, "retreived PAO differs from saved one");
    }

    @Test
    public void testCreateAndRetreive_CBC_7020() {
        CompleteTwoWayCbc pao = new CompleteTwoWayCbc();
        pao.setPaoName("awesomely cool PAO");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setAlarmInhibit(true);
        pao.setControlInhibit(true);
        pao.setStatistics("some stats");
        pao.setMasterAddress(5);

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(cbc7020PaoId_create, PaoType.CBC_7020);

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.CBC_7020);

        CompleteTwoWayCbc fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);
        assertFalse(fromDb == pao, "unit test has same object instances");
        assertEquals(pao, fromDb, "retreived PAO differs from saved one");
    }

    @Test
    public void testUpdatePao() {
        CompleteTwoWayCbc pao = new CompleteTwoWayCbc();
        pao.setPaoName("awesomely cool PAO");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setAlarmInhibit(true);
        pao.setControlInhibit(true);
        pao.setStatistics("some stats");
        pao.setMasterAddress(5);

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(cbc7020PaoId_update, PaoType.CBC_7020);

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.CBC_7020);

        CompleteTwoWayCbc pao1 = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);
        CompleteTwoWayCbc pao2 = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);

        assertFalse(pao1 == pao2, "unit test has same object instances");
        assertEquals(pao1, pao2, "retreived PAOs differ");

        pao1.setPaoName("new pao name");
        paoPersistenceDao.updatePao(pao1);
        CompleteTwoWayCbc fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);
        assertEquals(pao1, fromDb, "retreived PAO differs from updated one");

        pao1.setSerialNumber(7523);
        pao1.setAlarmInhibit(false);
        assertNotEquals(pao1, fromDb, "modificatiin to POJO failed");

        paoPersistenceDao.updatePao(pao1);
        fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);
        assertEquals(pao1, fromDb, "retreived PAO differs from updated one");
    }

    private int countRows(String tableName, String idColumn, int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*) from").append(tableName);
        sql.append("where").append(idColumn).eq(id);
        return jdbcTemplate.queryForInt(sql);
    }

    @Test
    public void testDeletePao_WEATHER_LOCATION() {
        CompleteWeatherLocation pao = new CompleteWeatherLocation();
        pao.setPaoName("weatherLocationPaoId_delete");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setStatistics("some stats");

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(weatherLocationPaoId_delete, PaoType.WEATHER_LOCATION);

        assertEquals(0, countRows("YukonPaobject", "PaobjectId", weatherLocationPaoId_delete), "pao already exists");

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.WEATHER_LOCATION);

        assertEquals(1, countRows("YukonPaobject", "PaobjectId", weatherLocationPaoId_delete), "pao not written");

        paoPersistenceDao.deletePao(expectedPaoIdentifier);
        assertEquals(0, countRows("YukonPaobject", "PaobjectId", weatherLocationPaoId_delete), "pao still exists");
    }

    public void testDeletePao_CBC_7020() {
        CompleteTwoWayCbc pao = new CompleteTwoWayCbc();
        pao.setPaoName("cbc7020PaoId_delete");
        pao.setDescription("I'm a little description, short and stout.");
        pao.setAlarmInhibit(true);
        pao.setControlInhibit(true);
        pao.setStatistics("some stats");
        pao.setMasterAddress(5);

        PaoIdentifier expectedPaoIdentifier = new PaoIdentifier(cbc7020PaoId_delete, PaoType.CBC_7020);

        assertEquals(0, countRows("YukonPaobject", "PaobjectId", cbc7020PaoId_delete), "YukonPaobject already exists");
        assertEquals(0, countRows("Device", "DeviceId", cbc7020PaoId_delete), "Device already exists");
        assertEquals(0, countRows("DeviceAddress", "DeviceId", cbc7020PaoId_delete), "DeviceAddress already exists");
        assertEquals(0,countRows("DeviceDirectCommSettings", "DeviceId", cbc7020PaoId_delete),
                "DeviceDirectCommSettings already exists");
        assertEquals(0, countRows("DeviceWindow", "DeviceId", cbc7020PaoId_delete), "DeviceWindow already exists");
        // DevicesCanRate? :-)
        assertEquals(0, countRows("DeviceScanRate", "DeviceId", cbc7020PaoId_delete), "DeviceScanRate already exists");
        assertEquals(0, countRows("DeviceCBC", "DeviceId", cbc7020PaoId_delete), "DeviceCBC already exists");

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.CBC_7020);

        assertEquals(1, countRows("YukonPaobject", "PaobjectId", cbc7020PaoId_delete), "pao not written");
        assertEquals(1, countRows("Device", "DeviceId", cbc7020PaoId_delete), "Device not written");
        assertEquals(1, countRows("DeviceAddress", "DeviceId", cbc7020PaoId_delete), "DeviceAddress not written");
        assertEquals(1, countRows("DeviceDirectCommSettings", "DeviceId", cbc7020PaoId_delete),
                "DeviceDirectCommSettings not written");
        assertEquals(1, countRows("DeviceWindow", "DeviceId", cbc7020PaoId_delete), "DeviceWindow not written");
        assertEquals(1, countRows("DeviceScanRate", "DeviceId", cbc7020PaoId_delete), "DeviceScanRate not written");
        assertEquals(1, countRows("DeviceCBC", "DeviceId", cbc7020PaoId_delete), "DeviceCBC not written");

        paoPersistenceDao.deletePao(expectedPaoIdentifier);
        assertEquals(0, countRows("YukonPaobject", "PaobjectId", cbc7020PaoId_delete), "pao still exists");
        assertEquals(0, countRows("Device", "DeviceId", cbc7020PaoId_delete), "Device still exists");
        assertEquals(0, countRows("DeviceAddress", "DeviceId", cbc7020PaoId_delete), "DeviceAddress still exists");
        assertEquals(0, countRows("DeviceDirectCommSettings", "DeviceId", cbc7020PaoId_delete),
                "DeviceDirectCommSettings still exists");
        assertEquals(0, countRows("DeviceWindow", "DeviceId", cbc7020PaoId_delete), "DeviceWindow still exists");
        assertEquals(0, countRows("DeviceScanRate", "DeviceId", cbc7020PaoId_delete), "DeviceScanRate still exists");
        assertEquals(0, countRows("DeviceCBC", "DeviceId", cbc7020PaoId_delete), "DeviceCBC still exists");
    }
}
