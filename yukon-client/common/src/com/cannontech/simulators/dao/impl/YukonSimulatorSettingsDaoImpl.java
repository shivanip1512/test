package com.cannontech.simulators.dao.impl;

import java.beans.PropertyEditor;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.rfn.model.SimulatorSettings;
import com.cannontech.dr.rfn.model.SimulatorSettings.ReportingInterval;

public class YukonSimulatorSettingsDaoImpl implements YukonSimulatorSettingsDao {
    private final Logger log = YukonLogManager.getLogger(YukonSimulatorSettingsDaoImpl.class);

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public boolean initYukonSimulatorSettings() {
        SqlStatementBuilder sql = new SqlStatementBuilder("IF OBJECT_ID(N'dbo.YukonSimulatorSettings', N'U') IS NOT NULL "
                                                        + "SELECT 1 ELSE SELECT 0");
        try {
            int result = yukonJdbcTemplate.queryForInt(sql);
            if (result == 1) {
                log.info("YukonSimulatorSettings table exists in the database.");
                return populateYukonSimulatorSettingsTable();
            }
            else {
                log.info("YukonSimulatorSettings table doesn't exist in the database, attempting to create it...");
                return createYukonSimulatorSettingsTable();
            }
            
        } catch (Exception e) {
            log.error(e);
            log.info("Attempting to create YukonSimulatorSettings table in database.");
            return createYukonSimulatorSettingsTable();
        }
    }
    
    @Override
    public boolean createYukonSimulatorSettingsTable() {
        SqlStatementBuilder sql = new SqlStatementBuilder("create table YukonSimulatorSettings "
                                                        + "(Name                varchar(50)          not null, "
                                                        + "Value                text                 not null, "
                                                        + "constraint PK_YukSimSet primary key (Name)); select 'success';");
        try {
            yukonJdbcTemplate.queryForString(sql);
            log.info("YukonSimulatorSettings table successfully created. Setting default values...");
            return populateYukonSimulatorSettingsTable();
        } catch (Exception e){
            log.error("Could not create YukonSimulatorSettings table " + e);
            return false;
        }
    }
    
    @Override
    public boolean populateYukonSimulatorSettingsTable() {
        try {
            for (YukonSimulatorSettingsKey property : YukonSimulatorSettingsKey.values()) {
                SqlStatementBuilder sql = new SqlStatementBuilder("if exists"
                                                                + "(select value from YukonSimulatorSettings "
                                                                + "where name = '" + property.name() + "') "
                                                                + "select 1 else select 0");
                int fieldExists = yukonJdbcTemplate.queryForInt(sql);
                if (fieldExists == 0) {
                    setValue(property, property.getDefaultValue());
                }
            }
            log.info("Successfully set default values of previously unpopulated rows in the YukonSimulatorSettings table.");
            return true;
        } catch (Exception e) {
            log.error("Error while trying to set default values in the YukonSimulatorSettings table " + e);
            return false;
        }
    }
    
    public SimulatorSettings getSimulatorSettings(SimulatorType simType) {
        SimulatorSettings settings = new SimulatorSettings();
        try {
            if (initYukonSimulatorSettings()) {
                if (simType == SimulatorType.RFN_METER) {
                    settings.setPaoType(getStringValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_METER_TYPE, simType));
                    settings.setPercentOfDuplicates(getIntegerValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE, simType));
                    settings.setReportingInterval(ReportingInterval.valueOf(getStringValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_REPORTING_INTERVAL, simType)));
                    settings.setRunOnStartup(getBooleanValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_RUN_ON_STARTUP, simType));
                    return settings;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Unable to get settings from YukonSimulatorSettings table " + e);
            return null;   
        }
    }
    
    public boolean setSimulatorSettings(SimulatorSettings settings, SimulatorType simType) {
        try {
            if (initYukonSimulatorSettings()) {
                if (simType == SimulatorType.RFN_METER) {
                    setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_METER_TYPE, settings.getPaoType());
                    setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_DUPLICATE_PERCENTAGE, settings.getPercentOfDuplicates());
                    setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_REPORTING_INTERVAL, settings.getReportingInterval());
                    setValue(YukonSimulatorSettingsKey.RFN_METER_SIMULATOR_RUN_ON_STARTUP, settings.getRunOnStartup());
                }
            }
        } catch (Exception e) {
            log.error("Unable to set YukonSimulatorSettings in database, " + e);
            return false;
        }
        return true;
    }
    
    public void setValue(YukonSimulatorSettingsKey property, Object value) {
        PropertyEditor propertyEditor = property.getInputType().getPropertyEditor();
        propertyEditor.setValue(value);
        String asText = propertyEditor.getAsText();
        
        // try update
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update YukonSimulatorSettings");
        sql.append("set Value").eq(asText);
        sql.append("where Name").eq(property);
        int rows = yukonJdbcTemplate.update(sql);
        if (rows == 1) {
            return;
        }
        // try insert
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("insert into YukonSimulatorSettings");
        sql2.append("values (").appendArgument(property).append(",").appendArgument(asText).append(")");
        yukonJdbcTemplate.update(sql2);
    }
    
    @Override
    public boolean getBooleanValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Boolean convertedValue = getConvertedValue(property, Boolean.class, simulatorType);
        return convertedValue.booleanValue();
    }

    @Override
    public double getDoubleValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Number convertedValue = getConvertedValue(property, Number.class, simulatorType);
        return convertedValue.doubleValue();
    }

    @Override
    public float getFloatValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Number convertedValue = getConvertedValue(property, Number.class, simulatorType);
        return convertedValue.floatValue();
    }

    @Override
    public int getIntegerValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Number convertedValue = getConvertedValue(property, Number.class, simulatorType);
        return convertedValue.intValue();
    }

    @Override
    public long getLongValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Number convertedValue = getConvertedValue(property, Number.class, simulatorType);
        return convertedValue.longValue();
    }

    @Override
    public String getStringValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Object convertedValue = getConvertedValue(property, Object.class, simulatorType);
        return convertedValue.toString();
    }
    
    @Override
    public Instant getInstantValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        Instant convertedValue = getConvertedValue(property, Instant.class, simulatorType);
        return convertedValue;
    }
    
    public <T> T getConvertedValue(YukonSimulatorSettingsKey property, Class<T> returnType, SimulatorType simulatorType) throws BadSimulatorSettingTypeException {
        if (log.isDebugEnabled()) {
            log.debug("getting converted value of " + property + " as " + returnType.getSimpleName());
        }
        Validate.isTrue(returnType.isAssignableFrom(property.getInputType().getTypeClass()), "can't convert " + property + " to " + returnType);
        String stringValue = getPropertyValue(property, simulatorType);
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
    
    public String getPropertyValue(YukonSimulatorSettingsKey property, SimulatorType simulatorType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select Value");
        sql.append("from YukonSimulatorSettings");
//        sql.append("where Name").eq(property.name());
//        sql.append("and Name").contains(simulatorType.name());
        sql.append("where Name = '" + property.name() + "' and Name like '%" + simulatorType.name() + "%';");
        
        String result;
        try {
            result = yukonJdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        
        return result;
    }
    
    public Object convertPropertyValue(YukonSimulatorSettingsKey property, String value) throws BadSimulatorSettingTypeException {
        try {
            return InputTypeFactory.convertPropertyValue(property.getInputType(), value);
        } catch (Exception e) {
            throw new BadSimulatorSettingTypeException(property, value, e);
        }
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}
