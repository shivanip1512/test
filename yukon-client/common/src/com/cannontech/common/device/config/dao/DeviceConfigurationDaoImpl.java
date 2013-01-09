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
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
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
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private List<ConfigurationTemplate> configurationTemplateList;

    public List<ConfigurationTemplate> getConfigurationTemplateList() {
        return configurationTemplateList;
    }

    public void setConfigurationTemplateList(List<ConfigurationTemplate> configurationTemplateList) {
        this.configurationTemplateList = configurationTemplateList;
    }

    @Override
    public List<ConfigurationTemplate> getAllConfigurationTemplates() {
        return configurationTemplateList;
    }

    @Override
    public ConfigurationTemplate getConfigurationTemplate(String name) {

        for (ConfigurationTemplate currTemplate : configurationTemplateList) {

            if (name.equals(currTemplate.getName())) {
                return currTemplate;
            }
        }

        return null;
    }

    @Override
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

        dbChangeManager.processDbChange(configuration.getId(),
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        DB_CHANGE_OBJECT_TYPE,
                                        dbChangeType);
    }

    @Override
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
    
    @Override
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

    @Override
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
    
    @Override
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

    @Override
    @Transactional
    public void delete(int id) {
        if (id == DNPConfiguration.DEFAULT_DNP_CONFIG_ID) {
            // Don't allow users to delete the default configuration under any circumstances.
            throw new InvalidConfigurationRemovalException("Cannot delete the default DNP configuration!");
        }
        
        ConfigurationBase configuration = getConfiguration(id);
        if (configuration != null && 
            configuration.getType() == ConfigurationType.DNP && 
            !getAssignedDevices(configuration).isEmpty()) {
            // This is a DNP configuration with assigned devices. Deletion is not allowable.
            throw new InvalidConfigurationRemovalException("Cannot delete a DNP configuration with assigned devices!");
        }

        // Remove any existing items
        SqlStatementBuilder itemDelete = new SqlStatementBuilder();
        itemDelete.append("DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationId").eq(id);
        jdbcTemplate.update(itemDelete);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceConfiguration WHERE DeviceConfigurationId").eq(id);
        jdbcTemplate.update(sql);

        dbChangeManager.processDbChange(id,
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        DB_CHANGE_OBJECT_TYPE,
                                        DbChangeType.DELETE);
    }

    @Override
    public List<SimpleDevice> getAssignedDevices(ConfigurationBase configuration) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT dcdm.DeviceId paobjectid, ypo.type");
        sql.append("FROM DeviceConfigurationDeviceMap dcdm");
        sql.append("JOIN YukonPaobject ypo on ypo.paobjectid = dcdm.DeviceId");
        sql.append("WHERE dcdm.DeviceConfigurationId").eq(configuration.getId());
        
        return jdbcTemplate.query(sql, new YukonDeviceRowMapper());
    }

    @Override
    @Transactional
    public void assignConfigToDevice(ConfigurationBase configuration, YukonDevice device) throws InvalidDeviceTypeException {
        if (configuration == null || device == null) {
            throw new InvalidDeviceTypeException("Unable to assign device configuration with a null " +
                                                 "device or a null configuration.");
        }

        // Get the device types that the configuration supports
        PaoTag tag = configuration.getType().getSupportedDeviceTag();
        
        // Only add the devices whose type is supported by the configuration
        if (!paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), tag)) {
            throw new InvalidDeviceTypeException("Device type: " 
                    + device.getPaoIdentifier().getPaoType().name() 
                    + " is invalid for config: " + configuration.getName());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params;
        DbChangeType dbChangeType;
        boolean isConfigUpdate = findConfigurationForDevice(device) != null;
        if (isConfigUpdate) {
            params = sql.update("DeviceConfigurationDeviceMap");
            params.addValue("DeviceConfigurationId", configuration.getId());
            sql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
            dbChangeType = DbChangeType.UPDATE;
        } else {
            params = sql.insertInto("DeviceConfigurationDeviceMap");
            params.addValue("DeviceID", device.getPaoIdentifier().getPaoId());
            params.addValue("DeviceConfigurationId", configuration.getId());
            dbChangeType = DbChangeType.ADD;
        }
        
        jdbcTemplate.update(sql);

        // Send DBChangeMsgs
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(),
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        "device",
                                        dbChangeType);
    }

    @Override
    @Transactional
    public void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException {
        boolean dnpDevice = paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), PaoTag.DEVICE_CONFIGURATION_DNP);

        if (dnpDevice) {
            throw new InvalidDeviceTypeException("Device type: " 
                    + device.getPaoIdentifier().getPaoType().name() 
                    + " cannot be unassigned from a configuration.");
        }
        
        SqlStatementBuilder removeSql = new SqlStatementBuilder();
        removeSql.append("DELETE FROM DeviceConfigurationDeviceMap");
        removeSql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(removeSql);
        
        // Send DBChangeMsgs
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(),
                                        DBChangeMsg.CHANGE_CONFIG_DB,
                                        DBChangeMsg.CAT_DEVICE_CONFIG,
                                        "device",
                                        DbChangeType.DELETE);
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

            @Override
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

        @Override
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
        return getConfiguration(DNPConfiguration.DEFAULT_DNP_CONFIG_ID);
    }

    @Override
    public boolean checkForNameConflict(String name, Integer id){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM deviceconfiguration");
        sql.append("WHERE name").eq(name);
        if(id != null){
            sql.append("AND deviceconfigurationId").neq(id);
        }
        int otherIds = jdbcTemplate.queryForInt(sql);
        return otherIds != 0;
    }
}
