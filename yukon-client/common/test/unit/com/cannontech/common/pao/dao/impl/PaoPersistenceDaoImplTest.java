package com.cannontech.common.pao.dao.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
@RunWith(SpringJUnit4ClassRunner.class)
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

    @Before
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

        assertNull("paoIdentifier should start out null", pao.getPaoIdentifier());
        paoPersistenceDao.createPao(pao, PaoType.WEATHER_LOCATION);
        assertEquals("did not update paoIdentifier properly", expectedPaoIdentifier, pao.getPaoIdentifier());

        CompleteWeatherLocation fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteWeatherLocation.class);
        assertFalse(fromDb == pao);
        assertEquals("retreived PAO differs from saved one", pao, fromDb);
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
        assertEquals("retreived PAO differs from saved one", pao, fromDb);
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
        assertEquals("retreived PAO differs from saved one", pao, fromDb);
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
        assertEquals("retreived PAO differs from saved one", pao, fromDb);
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
        assertFalse("unit test has same object instances", fromDb == pao);
        assertEquals("retreived PAO differs from saved one", pao, fromDb);
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

        assertFalse("unit test has same object instances", pao1 == pao2);
        assertEquals("retreived PAOs differ", pao1, pao2);

        pao1.setPaoName("new pao name");
        paoPersistenceDao.updatePao(pao1);
        CompleteTwoWayCbc fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);
        assertEquals("retreived PAO differs from updated one", pao1, fromDb);

        pao1.setSerialNumber(7523);
        pao1.setAlarmInhibit(false);
        assertNotEquals("modificatiin to POJO failed", pao1, fromDb);

        paoPersistenceDao.updatePao(pao1);
        fromDb = paoPersistenceDao.retreivePao(expectedPaoIdentifier, CompleteTwoWayCbc.class);
        assertEquals("retreived PAO differs from updated one", pao1, fromDb);
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

        assertEquals("pao already exists", 0, countRows("YukonPaobject", "PaobjectId", weatherLocationPaoId_delete));

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.WEATHER_LOCATION);

        assertEquals("pao not written", 1, countRows("YukonPaobject", "PaobjectId", weatherLocationPaoId_delete));

        paoPersistenceDao.deletePao(expectedPaoIdentifier);
        assertEquals("pao still exists", 0, countRows("YukonPaobject", "PaobjectId", weatherLocationPaoId_delete));
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

        assertEquals("YukonPaobject already exists", 0, countRows("YukonPaobject", "PaobjectId", cbc7020PaoId_delete));
        assertEquals("Device already exists", 0, countRows("Device", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceAddress already exists", 0, countRows("DeviceAddress", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceDirectCommSettings already exists", 0,
            countRows("DeviceDirectCommSettings", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceWindow already exists", 0, countRows("DeviceWindow", "DeviceId", cbc7020PaoId_delete));
        // DevicesCanRate? :-)
        assertEquals("DeviceScanRate already exists", 0, countRows("DeviceScanRate", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceCBC already exists", 0, countRows("DeviceCBC", "DeviceId", cbc7020PaoId_delete));

        expect(paoDao.getNextPaoId()).andReturn(expectedPaoIdentifier.getPaoId());
        replay(paoDao);
        paoPersistenceDao.createPao(pao, PaoType.CBC_7020);

        assertEquals("pao not written", 1, countRows("YukonPaobject", "PaobjectId", cbc7020PaoId_delete));
        assertEquals("Device not written", 1, countRows("Device", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceAddress not written", 1, countRows("DeviceAddress", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceDirectCommSettings not written", 1,
            countRows("DeviceDirectCommSettings", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceWindow not written", 1, countRows("DeviceWindow", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceScanRate not written", 1, countRows("DeviceScanRate", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceCBC not written", 1, countRows("DeviceCBC", "DeviceId", cbc7020PaoId_delete));

        paoPersistenceDao.deletePao(expectedPaoIdentifier);
        assertEquals("pao still exists", 0, countRows("YukonPaobject", "PaobjectId", cbc7020PaoId_delete));
        assertEquals("Device still exists", 0, countRows("Device", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceAddress still exists", 0, countRows("DeviceAddress", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceDirectCommSettings still exists", 0,
            countRows("DeviceDirectCommSettings", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceWindow still exists", 0, countRows("DeviceWindow", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceScanRate still exists", 0, countRows("DeviceScanRate", "DeviceId", cbc7020PaoId_delete));
        assertEquals("DeviceCBC still exists", 0, countRows("DeviceCBC", "DeviceId", cbc7020PaoId_delete));
    }
}
