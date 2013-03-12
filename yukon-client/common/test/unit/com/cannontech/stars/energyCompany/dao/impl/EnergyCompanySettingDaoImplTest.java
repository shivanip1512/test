package com.cannontech.stars.energyCompany.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.service.EventLogMockServiceFactory;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.mock.MockDataSource;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.impl.MockAsyncDynamicDataSourceImpl;
import com.cannontech.core.roleproperties.enums.SerialNumberValidation;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;

public class EnergyCompanySettingDaoImplTest {

    private MockDatabase mockDatabase;
    private EnergyCompanySettingDaoImpl impl;
    private SimpleTableAccessTemplate<EnergyCompanySetting> insertTemplate;
    
    @Before
    public void setUp() throws Exception {

        
        insertTemplate = new SimpleTableAccessTemplate<EnergyCompanySetting>(mockDatabase, null);
        
        impl = new EnergyCompanySettingDaoImpl();

        DbChangeManager mockDbChangeManager = new DbChangeManager() {
            @Override public void processPaoDbChange(YukonPao pao, DbChangeType changeType) { }
            @Override public void processDbChange(DbChangeType type, DbChangeCategory category, int primaryKey) {}
            @Override public void processDbChange(DBChangeMsg dbChange) { }
            @Override public void processDbChange(int id, int database, String category, DbChangeType dbChangeType) { }
            @Override public void processDbChange(int id, int database, String category, String objectType, DbChangeType dbChangeType) { }
        };

        StarsEventLogService mockStarsEventLogService = EventLogMockServiceFactory.getEventLogMockService(StarsEventLogService.class);

        AsyncDynamicDataSource mockAsyncDynamicDataSource = new MockAsyncDynamicDataSourceImpl() {
          // Implement whatever is used
        };

        NextValueHelper mockNextValueHelper = new NextValueHelper() {
            @Override
            public int getNextValue(String tableName) {
                return 0;
            }
        };

        mockDatabase = new MockDatabase(new MockDataSource() {});

        ReflectionTestUtils.setField(impl, "dbChangeManager", mockDbChangeManager);
        ReflectionTestUtils.setField(impl, "starsEventLogService", mockStarsEventLogService);
        ReflectionTestUtils.setField(impl, "asyncDynamicDataSource", mockAsyncDynamicDataSource);
        ReflectionTestUtils.setField(impl, "nextValueHelper", mockNextValueHelper);
        ReflectionTestUtils.setField(impl, "yukonJdbcTemplate", mockDatabase);
        ReflectionTestUtils.setField(impl,  "insertTemplate", insertTemplate);
    }

    public class MockDatabase extends YukonJdbcTemplate {
        public Map<String,Map<Integer,EnergyCompanySetting>> settings = new HashMap<>();

        public void setSettings(Map<String,Map<Integer,EnergyCompanySetting>> settings) {
            this.settings = settings;
        }

        public void addSetting(EnergyCompanySetting setting) {
            if (!settings.containsKey(setting.getType().name())) {
                Map<Integer, EnergyCompanySetting> innerMap = new HashMap<>();
                innerMap.put(setting.getEnergyCompanyId(), setting);
                settings.put(setting.getType().name(), innerMap);
            } else {
                settings.get(setting.getType().name()).put(setting.getEnergyCompanyId(), setting);
            }
        }

        public void clearAll() {
            settings.clear();
        }

        public MockDatabase(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public <T> T queryForObject(SqlFragmentSource sql,
                                    YukonRowMapper<T> rm) throws DataAccessException {
            String settingType = (String)sql.getArguments()[0];
            Integer ecId = (Integer)sql.getArguments()[1];
            EnergyCompanySetting setting;
            try {
                setting = settings.get(settingType).get(ecId);
            } catch (NullPointerException e) {
                throw new EmptyResultDataAccessException(0);
            }
            return (T) setting;
        }
    }

    /**
     * Test an existing setting has the correct values (value, enabled, and comment)
     * Test a setting not in db setting has the correct default values
     * Test the setting returned is a different instance for each call (a copy is returned)
     */
    @Test
    public void test_getSetting() {
        mockDatabase.clearAll();

        EnergyCompanySetting settingInDb1 = new EnergyCompanySetting() {{
            setComments("i'm in the database");
            setEnabled(false);
            setEnergyCompanyId(99);
            setId(100);
            setType(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY);
            setValue("settingInDb1 value");
        }};
        mockDatabase.addSetting(settingInDb1);

        EnergyCompanySetting settingInDb2 = new EnergyCompanySetting() {{
            setComments("i'm also in the database");
            setEnabled(true);
            setEnergyCompanyId(100);
            setId(101);
            setType(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT);
            setValue("settingInDb2 value");
        }};
        mockDatabase.addSetting(settingInDb2);


        //** Testing existing settings
        EnergyCompanySetting settingInDb_1_test = impl.getSetting(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 99);
        assertNotSame(settingInDb_1_test, settingInDb1);
        assertEqualSetting(settingInDb_1_test, settingInDb1);

        EnergyCompanySetting settingInDb_1_testFail = impl.getSetting(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 7); // Wrong ecId
        assertNotSame(settingInDb_1_testFail, settingInDb1);
        assertEquals(false, settingInDb1.getEnergyCompanyId().equals(settingInDb_1_testFail.getEnergyCompanyId()));

        EnergyCompanySetting settingInDb_2_test = impl.getSetting(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT, 100);
        assertNotSame(settingInDb_2_test, settingInDb2);
        assertEqualSetting(settingInDb_2_test, settingInDb2);

        EnergyCompanySetting settingInDb_2_testFail = impl.getSetting(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 100); // Wrong type
        assertNotSame(settingInDb_2_testFail, settingInDb2);
        assertEquals(false, settingInDb2.getType().equals(settingInDb_2_testFail.getType()));


        //** Test setting not in db
        EnergyCompanySetting settingNotInDb_1 = impl.getSetting(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 102);
        EnergyCompanySetting settingNotInDb_1_test = EnergyCompanySetting.getDefault(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 102);
        assertNotSame(settingNotInDb_1, settingNotInDb_1_test);
        assertEqualSetting(settingNotInDb_1, settingNotInDb_1_test);

        EnergyCompanySetting settingNotInDb_2 = impl.getSetting(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND, 103);
        EnergyCompanySetting settingNotInDb_2_test = EnergyCompanySetting.getDefault(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND, 103);
        assertNotSame(settingNotInDb_2, settingNotInDb_2_test);
        assertEqualSetting(settingNotInDb_2, settingNotInDb_2_test);

        //** Test the setting returned is a different instance for each call
        EnergyCompanySetting setting1 = impl.getSetting(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, 104);
        EnergyCompanySetting setting2 = impl.getSetting(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, 104);
        assertNotSame(setting1, setting2);
    }

    @Test
    public void test_getString() {
        mockDatabase.clearAll();

        // type is stringType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS;
        
        EnergyCompanySetting setting_valueEmail = new EnergyCompanySetting() {{
            setEnergyCompanyId(1);
            setType(settingType);
            setValue("admin@email.com");
        }};
        EnergyCompanySetting setting_valueInteger = new EnergyCompanySetting() {{
            setEnergyCompanyId(2);
            setType(settingType);
            setValue(10);
        }};
        EnergyCompanySetting setting_valueBoolean = new EnergyCompanySetting() {{
            setEnergyCompanyId(3);
            setType(settingType);
            setValue(false);
        }};
        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting() {{
            setEnergyCompanyId(4);
            setType(settingType);
            setValue(null);
        }};

        mockDatabase.addSetting(setting_valueEmail);
        mockDatabase.addSetting(setting_valueInteger);
        mockDatabase.addSetting(setting_valueBoolean);
        mockDatabase.addSetting(setting_valueNull);

        assertEquals("admin@email.com", impl.getString(settingType, setting_valueEmail.getEnergyCompanyId()));
        assertEquals("10", impl.getString(settingType, setting_valueInteger.getEnergyCompanyId()));
        assertEquals("false", impl.getString(settingType, setting_valueBoolean.getEnergyCompanyId()));
        // if value is null, an empty string is returned
        assertEquals("", impl.getString(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals("NUMERIC", impl.getString(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION, 9999));
    }

    @Test
    public void test_getBoolean() {
        mockDatabase.clearAll();

        // type is booleanType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_ALLOW_DESIGNATION_CODE;

        EnergyCompanySetting setting_valueBoolean = new EnergyCompanySetting() {{
            setEnergyCompanyId(3);
            setType(settingType);
            setValue(false);
        }};
        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting() {{
            setEnergyCompanyId(4);
            setType(settingType);
            setValue(null);
        }};

        mockDatabase.addSetting(setting_valueBoolean);
        mockDatabase.addSetting(setting_valueNull);

        assertEquals(false, impl.getBoolean(settingType, setting_valueBoolean.getEnergyCompanyId()));
        
        // Null values return false
        assertEquals(false, impl.getBoolean(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(true, impl.getBoolean(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, 9999));
    }

    @Test
    public void test_getInteger() {
        mockDatabase.clearAll();

        // type is stringType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH;
        
        EnergyCompanySetting setting_valueInteger = new EnergyCompanySetting() {{
            setEnergyCompanyId(2);
            setType(settingType);
            setValue(10);
        }};
        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting() {{
            setEnergyCompanyId(4);
            setType(settingType);
            setValue(null);
        }};

        mockDatabase.addSetting(setting_valueInteger);
        mockDatabase.addSetting(setting_valueNull);

        assertEquals(10, impl.getInteger(settingType, setting_valueInteger.getEnergyCompanyId()));

        // Null values return 0
        assertEquals(0, impl.getInteger(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(0, impl.getInteger(EnergyCompanySettingType.ROTATION_DIGIT_LENGTH, 9999));
    }

    @Test
    public void test_getEnum() {
        mockDatabase.clearAll();

        // type is stringType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION;

        EnergyCompanySetting setting_valueEnum = new EnergyCompanySetting() {{
            setEnergyCompanyId(2);
            setType(settingType);
            setValue(SerialNumberValidation.ALPHANUMERIC);
        }};
        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting() {{
            setEnergyCompanyId(4);
            setType(settingType);
            setValue(null);
        }};

        mockDatabase.addSetting(setting_valueEnum);
        mockDatabase.addSetting(setting_valueNull);

        assertEquals(SerialNumberValidation.ALPHANUMERIC, impl.getEnum(settingType, SerialNumberValidation.class, setting_valueEnum.getEnergyCompanyId()));
        assertEquals(null, impl.getEnum(settingType, SerialNumberValidation.class, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(SerialNumberValidation.NUMERIC, impl.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION, SerialNumberValidation.class, 9999));
    }
    
    @Test
    public void test_checkSetting() {
        mockDatabase.clearAll();

        // type is booleanType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_ALLOW_DESIGNATION_CODE;

        EnergyCompanySetting setting_valueBoolean = new EnergyCompanySetting() {{
            setEnergyCompanyId(3);
            setType(settingType);
            setValue(false);
        }};
        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting() {{
            setEnergyCompanyId(4);
            setType(settingType);
            setValue(null);
        }};

        mockDatabase.addSetting(setting_valueBoolean);
        mockDatabase.addSetting(setting_valueNull);

        assertEquals(false, impl.getBoolean(settingType, setting_valueBoolean.getEnergyCompanyId()));
        // null here should return false
        assertEquals(false, impl.getBoolean(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(true, impl.getBoolean(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, 9999));
    }

    @Test
    public void test_verifyUpdate() {
        mockDatabase.clearAll();

    }
    
    @Test
    public void test_verifySetting() {
        mockDatabase.clearAll();

        // type is booleanType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_ALLOW_DESIGNATION_CODE;

        EnergyCompanySetting setting_valueNotAuthorized = new EnergyCompanySetting() {{
            setEnergyCompanyId(1);
            setType(settingType);
            setValue(false);
        }};
        EnergyCompanySetting setting_valueAuthorized = new EnergyCompanySetting() {{
            setEnergyCompanyId(2);
            setType(settingType);
            setValue(true);
        }};
        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting() {{
            setEnergyCompanyId(3);
            setType(settingType);
            setValue(null);
        }};

        mockDatabase.addSetting(setting_valueNotAuthorized);
        mockDatabase.addSetting(setting_valueAuthorized);
        mockDatabase.addSetting(setting_valueNull);

        try {
            impl.verifySetting(settingType, setting_valueNotAuthorized.getEnergyCompanyId());
            fail();
        } catch(NotAuthorizedException e) {/*expected*/}

        try {
            // null here should return false
            impl.verifySetting(settingType, setting_valueNull.getEnergyCompanyId());
            fail();
        } catch(NotAuthorizedException e) {/*expected*/}
        
        try {
            // Testing a default
            impl.verifySetting(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, setting_valueAuthorized.getEnergyCompanyId());
        } catch(NotAuthorizedException e) {
            fail();
        }
        
        try {
            // Testing a default
            impl.verifySetting(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, 9999);
        } catch(NotAuthorizedException e) {
            fail();
        }
    }
    
    /**
     * Helper assert two energyCompanySetting's are equal
     */
    private void assertEqualSetting(EnergyCompanySetting s1, EnergyCompanySetting s2) {
        assertEquals(s1.getEnabled(), s2.getEnabled());
        assertEquals(s1.getComments(), s2.getComments());
        assertEquals(s1.getEnergyCompanyId(), s2.getEnergyCompanyId());
        assertEquals(s1.getId(), s2.getId());
        assertEquals(s1.getLastChanged(), s2.getLastChanged());
        assertEquals(s1.getType(), s2.getType());
        assertEquals(s1.getValue(), s2.getValue());
    }
}
