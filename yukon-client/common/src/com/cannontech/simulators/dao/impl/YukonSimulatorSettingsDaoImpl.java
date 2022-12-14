package com.cannontech.simulators.dao.impl;

import java.beans.PropertyEditor;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public class YukonSimulatorSettingsDaoImpl implements YukonSimulatorSettingsDao {
    @Autowired private DatabaseVendorResolver databaseConnectionVendorResolver;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    private final Logger log = YukonLogManager.getLogger(YukonSimulatorSettingsDaoImpl.class);

    @Override
    public void initYukonSimulatorSettings() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonSimulatorSettings");
        try {
            yukonJdbcTemplate.queryForInt(sql);
            log.debug("YukonSimualatorSettings table exists in the database.");
            return;
        } catch (Exception e) {
            log.debug("YukonSimulatorSettings table doesn't exist in the database, attempting to create it... " + e);
            SqlStatementBuilder sqlCreate = new SqlStatementBuilder();
            DatabaseVendor databaseVendor = databaseConnectionVendorResolver.getDatabaseVendor();
            if (databaseVendor.isOracle()) {
                sqlCreate.append("CREATE TABLE YukonSimulatorSettings");
                sqlCreate.append("(Name VARCHAR2(50) NOT NULL,");
                sqlCreate.append("Value CLOB NOT NULL,");
                sqlCreate.append("CONSTRAINT PK_YukSimSet PRIMARY KEY(Name))");
            } else {
                sqlCreate.append("CREATE TABLE YukonSimulatorSettings");
                sqlCreate.append("(Name VARCHAR(50) NOT NULL,");
                sqlCreate.append("Value TEXT NOT NULL,");
                sqlCreate.append("CONSTRAINT PK_YukSimSet PRIMARY KEY (Name))");
            }
            yukonJdbcTemplate.update(sqlCreate);
            return;
        }
    }

    @Override
    public void setValue(YukonSimulatorSettingsKey property, Object value) {
        PropertyEditor propertyEditor = property.getInputType().getPropertyEditor();
        propertyEditor.setValue(value);
        String asText = propertyEditor.getAsText();

        // try update
        SqlStatementBuilder sqlUpdate = new SqlStatementBuilder();
        sqlUpdate.update("YukonSimulatorSettings");
        sqlUpdate.append("Value").eq(SqlUtils.convertStringToDbValue(asText));
        sqlUpdate.append("WHERE Name").eq_k(property);
        int rows = yukonJdbcTemplate.update(sqlUpdate);
        if (rows == 1) {
            return;
        }
        // try insert
        SqlStatementBuilder sqlInsert = new SqlStatementBuilder();
        sqlInsert.append("INSERT INTO YukonSimulatorSettings");
        sqlInsert.values(property, SqlUtils.convertStringToDbValue(asText));
        yukonJdbcTemplate.update(sqlInsert);
    }

    @Override
    public boolean getBooleanValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, Boolean.class);
    }

    @Override
    public double getDoubleValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, Double.class);
    }

    @Override
    public float getFloatValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, Float.class);
    }

    @Override
    public int getIntegerValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, Integer.class);
    }

    @Override
    public long getLongValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, Long.class);
    }

    @Override
    public String getStringValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, String.class);
    }

    @Override
    public Instant getInstantValue(YukonSimulatorSettingsKey property) {
        return getConvertedValue(property, Instant.class);
    }

    private <T> T getConvertedValue(YukonSimulatorSettingsKey property, Class<T> returnType) throws BadSimulatorSettingTypeException {
        if (log.isDebugEnabled()) {
            log.debug("getting converted value of " + property + " as " + returnType.getSimpleName());
        }
        Validate.isTrue(returnType.isAssignableFrom(property.getInputType().getTypeClass()), "can't convert " + property + " to " + returnType);
        String stringValue = getPropertyValue(property);
        Object convertedValue = convertPropertyValue(property, stringValue);
        if (convertedValue == null) {
            log.debug("convertedValue was null, using default");
            convertedValue = property.getDefaultValue();
        }
        if (log.isDebugEnabled()) {
            log.debug("returning: " + convertedValue);
        }
        T result = returnType.cast(convertedValue);
        return result;
    }

    private String getPropertyValue(YukonSimulatorSettingsKey property) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Value");
        sql.append("FROM YukonSimulatorSettings");
        sql.append("WHERE Name").eq_k(property);

        String result;
        try {
            result = yukonJdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return result;
    }

    private Object convertPropertyValue(YukonSimulatorSettingsKey property, String value) throws BadSimulatorSettingTypeException {
        try {
            return InputTypeFactory.convertPropertyValue(property.getInputType(), value);
        } catch (Exception e) {
            throw new BadSimulatorSettingTypeException(property, value, e);
        }
    }
}
