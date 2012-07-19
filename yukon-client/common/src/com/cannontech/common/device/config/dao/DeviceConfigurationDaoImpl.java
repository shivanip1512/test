package com.cannontech.common.device.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.input.InputBeanWrapperImpl;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.type.InputType;

public class DeviceConfigurationDaoImpl implements DeviceConfigurationDao {
    public static final String DB_CHANGE_OBJECT_TYPE = "config";
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private List<ConfigurationTemplate> configurationTemplateList;

    public List<ConfigurationTemplate> getConfigurationTemplateList() {
        return configurationTemplateList;
    }

    public void setConfigurationTemplateList(List<ConfigurationTemplate> configurationTemplateList) {
        this.configurationTemplateList = configurationTemplateList;
    }

    public List<ConfigurationTemplate> getAllConfigurationTemplates() {
        return configurationTemplateList;
    }

    public ConfigurationTemplate getConfigurationTemplate(String name) {

        for (ConfigurationTemplate currTemplate : configurationTemplateList) {

            if (name.equals(currTemplate.getName())) {
                return currTemplate;
            }
        }

        return null;
    }

    @Transactional
    public void save(ConfigurationBase configuration) {

        String sql = null;
        DbChangeType dbChangeType = DbChangeType.ADD;
        if (configuration.getId() == null) {

            // Insert new configuration

            int id = nextValueHelper.getNextValue("DeviceConfiguration");
            configuration.setId(id);
            sql = "INSERT INTO DeviceConfiguration (Name, Type, DeviceConfigurationId) values (?, ?, ?)";

        } else {

            // Update configuration
            sql = "UPDATE DeviceConfiguration set Name = ?, Type = ? WHERE DeviceConfigurationId = ?";

            dbChangeType = DbChangeType.UPDATE;
        }

        // Save Configuration
        jdbcTemplate.update(sql, 
                            configuration.getName(), 
                            configuration.getType().toString(),
                            configuration.getId());

        // Remove any existing items
        String itemDelete = "DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationId = ?";
        jdbcTemplate.update(itemDelete, configuration.getId());

        // Save configuration items
        String itemSql = "INSERT INTO DeviceConfigurationItem values(?, ?, ?, ?)";

        String templateName = configuration.getType().getConfigurationTemplateName();
        ConfigurationTemplate template = getConfigurationTemplate(templateName);
        Properties dbNameMapping = template.getDbNameMapping();

        InputBeanWrapperImpl wrapper = new InputBeanWrapperImpl(configuration);

        // Register the input property editors
        InputRoot inputRoot = new InputRoot();
        inputRoot.setInputList(template.getInputList());
        this.registerEditors(wrapper, inputRoot);

        for (Object dbFieldName : dbNameMapping.keySet()) {

            String path = (String) dbNameMapping.get(dbFieldName);

            String value = wrapper.getValueAsText(path);

            if (value != null) {

                int itemId = nextValueHelper.getNextValue("DeviceConfigurationItem");
                jdbcTemplate.update(itemSql,
                                    itemId,
                                    configuration.getId(),
                                    dbFieldName,
                                    value);
            }
        }

        DBChangeMsg dbChange = new DBChangeMsg(configuration.getId(),
                                               DBChangeMsg.CHANGE_CONFIG_DB,
                                               DBChangeMsg.CAT_DEVICE_CONFIG,
                                               DB_CHANGE_OBJECT_TYPE,
                                               dbChangeType);

        dbPersistentDao.processDBChange(dbChange);

    }

    public ConfigurationBase getConfiguration(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM DeviceConfiguration");
        sql.append("WHERE DeviceConfigurationId").eq(id);

        try {
            // Load configuration
            ConfigurationBase configuration = jdbcTemplate.queryForObject(sql, new ConfigurationBaseRowMapper());
            
            // Load configuration items
            loadConfigurationItems(configuration);
            
            return configuration;
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
    
    public ConfigurationBase findConfigurationForDevice(YukonDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM DeviceConfiguration DC");
        sql.append("   JOIN DeviceConfigurationDeviceMap DCDM ON");
		sql.append("   DC.DeviceConfigurationId = DCDM.DeviceConfigurationId");
		sql.append("WHERE DCDM.DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
		try {
            return jdbcTemplate.queryForObject(sql, new ConfigurationBaseRowMapper());
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public List<ConfigurationBase> getAllConfigurations() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM DeviceConfiguration");
        sql.append("ORDER BY Name");
        
        // Load configurations
        List<ConfigurationBase> configList = jdbcTemplate.query(sql, new ConfigurationBaseRowMapper());

        // Load configuration items
        for (ConfigurationBase configuration : configList) {
            loadConfigurationItems(configuration);
        }

        return configList;
    }
    
    public List<ConfigurationBase> getAllConfigurationsByType(ConfigurationType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM DeviceConfiguration");
        sql.append("WHERE Type").eq(type.toString());
        sql.append("ORDER BY Name");
        
        List<ConfigurationBase> configList = jdbcTemplate.query(sql, new ConfigurationBaseRowMapper());

        for (ConfigurationBase configuration : configList) {
            loadConfigurationItems(configuration);
        }

        return configList;
    }

    @Transactional
    public void delete(int id) {
        ConfigurationBase configuration = getConfiguration(id);
        if (configuration != null && 
            configuration.getType() == ConfigurationType.DNP && 
            !getAssignedDevices(configuration).isEmpty()) {
            // This is a DNP configuration with assigned devices. Deletion is not allowable.
            throw new RuntimeException("Cannot delete a DNP configuration with assigned devices!");
        }

        // Remove any existing items
        SqlStatementBuilder itemDelete = new SqlStatementBuilder();
        itemDelete.append("DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationId").eq(id);
        jdbcTemplate.update(itemDelete);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceConfiguration WHERE DeviceConfigurationId").eq(id);
        jdbcTemplate.update(sql);

        DBChangeMsg dbChange = 
                new DBChangeMsg(id,
                                DBChangeMsg.CHANGE_CONFIG_DB,
                                DBChangeMsg.CAT_DEVICE_CONFIG,
                                DB_CHANGE_OBJECT_TYPE,
                                DbChangeType.DELETE);

        dbPersistentDao.processDBChange(dbChange);
    }

    public List<SimpleDevice> getAssignedDevices(ConfigurationBase configuration) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT dcdm.DeviceId paobjectid, ypo.type");
        sql.append("FROM DeviceConfigurationDeviceMap dcdm");
        sql.append("JOIN YukonPaobject ypo on ypo.paobjectid = dcdm.DeviceId");
        sql.append("WHERE dcdm.DeviceConfigurationId").eq(configuration.getId());
        
        return jdbcTemplate.query(sql, new YukonDeviceRowMapper());
    }

    public void assignConfigToDevice(ConfigurationBase configuration, YukonDevice device) throws InvalidDeviceTypeException {
        boolean dnpDevice = paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), PaoTag.DEVICE_CONFIGURATION_DNP);
        
        if (dnpDevice && (configuration == null)) {
            throw new InvalidDeviceTypeException("Device type: " 
                    + device.getPaoIdentifier().getPaoType().name() 
                    + " cannot be unassigned from a configuration.");
        }
        
        SqlStatementBuilder removeSql = new SqlStatementBuilder();
        removeSql.append("DELETE FROM DeviceConfigurationDeviceMap");
        removeSql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
        // Clean out any assigned configs - device can only be assigned one config
        jdbcTemplate.update(removeSql);

        // Check if we need to assign a new config or if it was just a removal.
        if (configuration != null) {
            // Get the device types that the configuration supports
            PaoTag tag = configuration.getType().getSupportedDeviceTag();
            
            // Only add the devices whose type is supported by the configuration
            if (!paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), tag)) {
                throw new InvalidDeviceTypeException("Device type: " 
                        + device.getPaoIdentifier().getPaoType().name() 
                        + " is invalid for config: " + configuration.getName());
            }
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("DeviceConfigurationDeviceMap");
            params.addValue("DeviceConfigurationId", configuration.getId());
            params.addValue("DeviceID", device.getPaoIdentifier().getPaoId());
            
            jdbcTemplate.update(sql);
        }

        DBChangeMsg dbChange = new DBChangeMsg(device.getPaoIdentifier().getPaoId(),
                                               DBChangeMsg.CHANGE_CONFIG_DB,
                                               DBChangeMsg.CAT_DEVICE_CONFIG,
                                               "device",
                                               DbChangeType.UPDATE);
        // Send DBChangeMsgs
        dbPersistentDao.processDBChange(dbChange);
    }

    public void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException {
        assignConfigToDevice(null, device);
    }

    /**
     * Helper method to load all of the configuration items for a given
     * configuration
     * @param configuration - Configuration to load items for
     */
    private void loadConfigurationItems(ConfigurationBase configuration) {

        String itemSql = "SELECT * FROM DeviceConfigurationItem WHERE DeviceConfigurationId = ?";

        String templateName = configuration.getType().getConfigurationTemplateName();
        ConfigurationTemplate template = getConfigurationTemplate(templateName);
        final Properties dbNameMapping = template.getDbNameMapping();
        final BeanWrapper wrapper = new BeanWrapperImpl(configuration);

        // Register the input property editors
        InputRoot inputRoot = new InputRoot();
        inputRoot.setInputList(template.getInputList());
        this.registerEditors(wrapper, inputRoot);

        JdbcOperations ops = jdbcTemplate.getJdbcOperations();
        ops.query(itemSql, new Object[] { configuration.getId() }, new RowCallbackHandler() {

            public void processRow(ResultSet rs) throws SQLException {

                String field = rs.getString("FieldName");

                String path = dbNameMapping.getProperty(field);

                Object value = rs.getObject("value");

                wrapper.setPropertyValue(path, value);

            }
        });
    }

    /**
     * Helper method to register property editors for all of the inputs in an
     * input root on a given registry
     * @param registry - registry to register editors on
     * @param inputRoot - input root to register editors for
     */
    private void registerEditors(PropertyEditorRegistry registry, InputRoot inputRoot) {

        Map<String, ? extends InputSource<?>> inputMap = inputRoot.getInputMap();

        InputSource<?> input = null;
        InputType<?> inputType = null;
        for (String fieldPath : inputMap.keySet()) {

            input = inputMap.get(fieldPath);
            inputType = input.getType();

            // Register property editor for this input
            registry.registerCustomEditor(inputType.getTypeClass(),
                                          fieldPath,
                                          inputType.getPropertyEditor());

        }
    }

    /**
     * Mapping class to process a result set row into a configuration
     */
    public static class ConfigurationBaseRowMapper implements
            ParameterizedRowMapper<ConfigurationBase> {

        public ConfigurationBase mapRow(ResultSet rs, int rowNum) throws SQLException {

            String type = rs.getString("type");
            ConfigurationType configType = ConfigurationType.valueOf(type);

            ConfigurationBase config = configType.getConfigurationClass();

            config.setId(rs.getInt("deviceConfigurationId"));
            config.setName(rs.getString("name"));

            return config;
        }
    }
    
    @Override
    public String getValueForFieldName(int configId, String fieldName){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Value");
        sql.append("FROM DeviceConfigurationItem");
        sql.append("WHERE DeviceConfigurationID").eq(configId).append("AND FieldName").eq(fieldName);
        
        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public ConfigurationBase getDefaultDNPConfiguration() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MIN(DeviceConfigurationId)");
        sql.append("FROM DeviceConfiguration");
        sql.append("WHERE Type").eq("DNP");
        
        int defaultID = jdbcTemplate.queryForInt(sql);
        
        return getConfiguration(defaultID);
    }
}
