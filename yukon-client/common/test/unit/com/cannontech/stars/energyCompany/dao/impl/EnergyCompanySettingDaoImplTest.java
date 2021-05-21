package com.cannontech.stars.energyCompany.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/com/cannontech/common/daoTestContext.xml")
@DirtiesContext
public class EnergyCompanySettingDaoImplTest {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private EnergyCompanySettingDaoImpl impl;
    private static int nextValue;

    @BeforeEach
    public void setUp() throws Exception {
        jdbcTemplate.update(new SqlStatementBuilder("delete from EnergyCompanySetting"));
        impl = new EnergyCompanySettingDaoImpl();

        AsyncDynamicDataSource mockAsyncDynamicDataSource = EasyMock.createNiceMock(AsyncDynamicDataSource.class);
        DbChangeManager mockDbChangeManager = EasyMock.createNiceMock(DbChangeManager.class);
        StarsEventLogService mockStarsEventLogService = EasyMock.createNiceMock(StarsEventLogService.class);
        NextValueHelper mockNextValueHelper = EasyMock.createNiceMock(NextValueHelper.class);
        EasyMock.expect(mockNextValueHelper.getNextValue(EasyMock.anyObject(String.class))).andAnswer(
            new IAnswer<Integer>() {
                @Override
                public Integer answer() throws Throwable {
                    return nextValue++;
                }
            }).anyTimes();
        EasyMock.replay(mockAsyncDynamicDataSource, mockDbChangeManager, mockStarsEventLogService, mockNextValueHelper);

        ReflectionTestUtils.setField(impl, "dbChangeManager", mockDbChangeManager);
        ReflectionTestUtils.setField(impl, "starsEventLogService", mockStarsEventLogService);
        ReflectionTestUtils.setField(impl, "asyncDynamicDataSource", mockAsyncDynamicDataSource);
        ReflectionTestUtils.setField(impl, "nextValueHelper", mockNextValueHelper);
        ReflectionTestUtils.setField(impl, "yukonJdbcTemplate", jdbcTemplate);
        impl.init();
    }

    @Test
    public void test_emptyDatabase() {
        List<EnergyCompanySetting> allSettings = impl.getAllSettings(99);
        for (EnergyCompanySetting setting : allSettings) {
            assertEquals(setting.getValue(), setting.getType().getDefaultValue());
        }
        allSettings = impl.getAllSettings(100);
        for (EnergyCompanySetting setting : allSettings) {
            assertEquals(setting.getValue(), setting.getType().getDefaultValue());
        }
    }

    // /**
    // * Test an existing setting has the correct values (value, enabled, and comment)
    // * Test a setting not in db setting has the correct default values
    // * Test the setting returned is a different instance for each call (a copy is returned)
    // */
    @Test
    public void test_getSetting() {
        EnergyCompanySetting settingInDb1 = new EnergyCompanySetting();
        settingInDb1.setComments("i'm in the database");
        settingInDb1.setEnabled(true);
        settingInDb1.setEnergyCompanyId(99);
        settingInDb1.setType(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY);
        settingInDb1.setValue(false);
        impl.updateSetting(settingInDb1, null, settingInDb1.getEnergyCompanyId());

        EnergyCompanySetting settingInDb2 = new EnergyCompanySetting();
        settingInDb2.setComments("i'm also in the database");
        settingInDb2.setEnabled(true);
        settingInDb2.setEnergyCompanyId(100);
        settingInDb2.setType(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT);
        settingInDb2.setValue(TemperatureUnit.CELSIUS);
        impl.updateSetting(settingInDb2, null, settingInDb2.getEnergyCompanyId());

        // ** Testing existing settings
        EnergyCompanySetting settingInDb_1_test =
            impl.getSetting(settingInDb1.getType(), settingInDb1.getEnergyCompanyId());
        assertNotSame(settingInDb_1_test, settingInDb1);
        assertEquals(settingInDb_1_test, settingInDb1);

        EnergyCompanySetting settingInDb_1_testFail =
            impl.getSetting(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 7); // Wrong ecId
        assertNotSame(settingInDb_1_testFail, settingInDb1);
        assertNotEquals(settingInDb1.getEnergyCompanyId(), settingInDb_1_testFail.getEnergyCompanyId());

        EnergyCompanySetting settingInDb_2_test =
            impl.getSetting(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT, 100);
        assertNotSame(settingInDb_2_test, settingInDb2);
        assertEquals(settingInDb_2_test, settingInDb2);

        EnergyCompanySetting settingInDb_2_testFail =
            impl.getSetting(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, 100); // Wrong type
        assertNotSame(settingInDb_2_testFail, settingInDb2);
        assertEquals(false, settingInDb2.getType().equals(settingInDb_2_testFail.getType()));

        // ** Test setting not in db
        EnergyCompanySetting settingNotInDb_1 = impl.getSetting(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 102);
        EnergyCompanySetting settingNotInDb_1_test =
            EnergyCompanySetting.getDefault(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, 102);
        assertNotSame(settingNotInDb_1, settingNotInDb_1_test);
        assertEquals(settingNotInDb_1, settingNotInDb_1_test);

        EnergyCompanySetting settingNotInDb_2 =
            impl.getSetting(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND, 103);
        EnergyCompanySetting settingNotInDb_2_test =
            EnergyCompanySetting.getDefault(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND,
                103);
        assertNotSame(settingNotInDb_2, settingNotInDb_2_test);
        assertEquals(settingNotInDb_2, settingNotInDb_2_test);

        // ** Test the setting returned is a different instance for each call
        EnergyCompanySetting setting1 = impl.getSetting(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, 104);
        EnergyCompanySetting setting2 = impl.getSetting(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, 104);
        assertNotSame(setting1, setting2);
    }

    @Test
    public void test_getString() {
        // type is stringType()
        EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS;

        EnergyCompanySetting setting_valueEmail = new EnergyCompanySetting();
        setting_valueEmail.setEnergyCompanyId(1);
        setting_valueEmail.setType(settingType);
        setting_valueEmail.setValue("admin@email.com");
        impl.updateSetting(setting_valueEmail, null, setting_valueEmail.getEnergyCompanyId());

        EnergyCompanySetting setting_valueInteger = new EnergyCompanySetting();
        setting_valueInteger.setEnergyCompanyId(2);
        setting_valueInteger.setType(settingType);
        setting_valueInteger.setValue(10);
        impl.updateSetting(setting_valueInteger, null, setting_valueInteger.getEnergyCompanyId());

        EnergyCompanySetting setting_valueBoolean = new EnergyCompanySetting();
        setting_valueBoolean.setEnergyCompanyId(3);
        setting_valueBoolean.setType(settingType);
        setting_valueBoolean.setValue(false);
        impl.updateSetting(setting_valueBoolean, null, setting_valueBoolean.getEnergyCompanyId());

        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting();
        setting_valueNull.setEnergyCompanyId(4);
        setting_valueNull.setType(settingType);
        setting_valueNull.setValue(null);
        impl.updateSetting(setting_valueNull, null, setting_valueNull.getEnergyCompanyId());

        assertEquals("admin@email.com", impl.getString(settingType, setting_valueEmail.getEnergyCompanyId()));
        assertEquals("10", impl.getString(settingType, setting_valueInteger.getEnergyCompanyId()));
        assertEquals("FALSE", impl.getString(settingType, setting_valueBoolean.getEnergyCompanyId()));
        // if value is null, an empty string is returned
        assertEquals("", impl.getString(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals("NUMERIC", impl.getString(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION, 9999));
    }

    @Test
    public void test_getBoolean() {
        EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_ALLOW_DESIGNATION_CODE;

        EnergyCompanySetting setting_valueBoolean = new EnergyCompanySetting();
        setting_valueBoolean.setEnergyCompanyId(3);
        setting_valueBoolean.setType(settingType);
        setting_valueBoolean.setValue(false);
        impl.updateSetting(setting_valueBoolean, null, setting_valueBoolean.getEnergyCompanyId());

        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting();
        setting_valueNull.setEnergyCompanyId(4);
        setting_valueNull.setType(settingType);
        setting_valueNull.setValue(null);
        impl.updateSetting(setting_valueNull, null, setting_valueNull.getEnergyCompanyId());

        assertEquals(false, impl.getBoolean(settingType, setting_valueBoolean.getEnergyCompanyId()));

        // Null values return false
        assertEquals(false, impl.getBoolean(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(true, impl.getBoolean(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, 9999));
    }

    @Test
    public void test_getInteger() {
        EnergyCompanySettingType settingType = EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH;

        EnergyCompanySetting setting_valueInteger = new EnergyCompanySetting();
        setting_valueInteger.setEnergyCompanyId(2);
        setting_valueInteger.setType(settingType);
        setting_valueInteger.setValue(10);
        impl.updateSetting(setting_valueInteger, null, setting_valueInteger.getEnergyCompanyId());

        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting();
        setting_valueNull.setEnergyCompanyId(4);
        setting_valueNull.setType(settingType);
        setting_valueNull.setValue(null);
        impl.updateSetting(setting_valueNull, null, setting_valueNull.getEnergyCompanyId());

        assertEquals(10, impl.getInteger(settingType, setting_valueInteger.getEnergyCompanyId()));

        // Null values return 0
        assertEquals(0, impl.getInteger(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(0, impl.getInteger(EnergyCompanySettingType.ROTATION_DIGIT_LENGTH, 9999));
    }

    @Test
    public void test_getEnum() {
        // type is stringType()
        EnergyCompanySettingType settingType = EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION;

        EnergyCompanySetting setting_valueEnum = new EnergyCompanySetting();
        setting_valueEnum.setEnergyCompanyId(2);
        setting_valueEnum.setType(settingType);
        setting_valueEnum.setValue(SerialNumberValidation.ALPHANUMERIC);
        impl.updateSetting(setting_valueEnum, null, setting_valueEnum.getEnergyCompanyId());

        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting();
        setting_valueNull.setEnergyCompanyId(4);
        setting_valueNull.setType(settingType);
        setting_valueNull.setValue(null);
        impl.updateSetting(setting_valueNull, null, setting_valueNull.getEnergyCompanyId());

        assertEquals(SerialNumberValidation.ALPHANUMERIC,
            impl.getEnum(settingType, SerialNumberValidation.class, setting_valueEnum.getEnergyCompanyId()));
        assertEquals(null,
            impl.getEnum(settingType, SerialNumberValidation.class, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(SerialNumberValidation.NUMERIC,
            impl.getEnum(EnergyCompanySettingType.SERIAL_NUMBER_VALIDATION, SerialNumberValidation.class, 9999));
    }

    @Test
    public void test_checkSetting() {
        // type is booleanType()
        EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_ALLOW_DESIGNATION_CODE;

        EnergyCompanySetting setting_valueBoolean = new EnergyCompanySetting();
        setting_valueBoolean.setEnergyCompanyId(3);
        setting_valueBoolean.setType(settingType);
        setting_valueBoolean.setValue(false);
        impl.updateSetting(setting_valueBoolean, null, setting_valueBoolean.getEnergyCompanyId());

        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting();
        setting_valueNull.setEnergyCompanyId(4);
        setting_valueNull.setType(settingType);
        setting_valueNull.setValue(null);
        impl.updateSetting(setting_valueNull, null, setting_valueNull.getEnergyCompanyId());

        assertEquals(false, impl.getBoolean(settingType, setting_valueBoolean.getEnergyCompanyId()));
        // null here should return false
        assertEquals(false, impl.getBoolean(settingType, setting_valueNull.getEnergyCompanyId()));

        // Testing a default
        assertEquals(true, impl.getBoolean(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, 9999));
    }

    @Test
    public void test_verifySetting() {
        // type is booleanType()
        final EnergyCompanySettingType settingType = EnergyCompanySettingType.ADMIN_ALLOW_DESIGNATION_CODE;

        EnergyCompanySetting setting_valueNotAuthorized = new EnergyCompanySetting();
        setting_valueNotAuthorized.setEnergyCompanyId(1);
        setting_valueNotAuthorized.setType(settingType);
        setting_valueNotAuthorized.setValue(false);
        impl.updateSetting(setting_valueNotAuthorized, null, setting_valueNotAuthorized.getEnergyCompanyId());

        EnergyCompanySetting setting_valueAuthorized = new EnergyCompanySetting();
        setting_valueAuthorized.setEnergyCompanyId(2);
        setting_valueAuthorized.setType(settingType);
        setting_valueAuthorized.setValue(true);
        impl.updateSetting(setting_valueAuthorized, null, setting_valueAuthorized.getEnergyCompanyId());

        EnergyCompanySetting setting_valueNull = new EnergyCompanySetting();
        setting_valueNull.setEnergyCompanyId(3);
        setting_valueNull.setType(settingType);
        setting_valueNull.setValue(null);
        impl.updateSetting(setting_valueNull, null, setting_valueNull.getEnergyCompanyId());

        try {
            impl.verifySetting(settingType, setting_valueNotAuthorized.getEnergyCompanyId());
            fail();
        } catch (NotAuthorizedException e) {/* expected */}

        try {
            // null here should return false
            impl.verifySetting(settingType, setting_valueNull.getEnergyCompanyId());
            fail();
        } catch (NotAuthorizedException e) {/* expected */}

        try {
            // Testing a default
            impl.verifySetting(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL,
                setting_valueAuthorized.getEnergyCompanyId());
        } catch (NotAuthorizedException e) {
            fail();
        }

        try {
            // Testing a default
            impl.verifySetting(EnergyCompanySettingType.ADMIN_ALLOW_THERMOSTAT_SCHEDULE_ALL, 9999);
        } catch (NotAuthorizedException e) {
            fail();
        }
    }
}
