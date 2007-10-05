package com.cannontech.common.device.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.input.InputBeanWrapperImpl;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.type.InputType;

/**
 * Implementation class for DeviceConfigurationDao
 */
public class DeviceConfigurationDaoImpl implements DeviceConfigurationDao {

    private SimpleJdbcTemplate simpleJdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;

    private List<ConfigurationTemplate> configurationTemplateList = null;

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

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
        if (configuration.getId() == null) {

            // Insert new configuration

            int id = nextValueHelper.getNextValue("DeviceConfiguration");
            configuration.setId(id);
            sql = "INSERT INTO DeviceConfiguration (Name, Type, DeviceConfigurationId) values (?, ?, ?)";

        } else {

            // Update configuration
            sql = "UPDATE DeviceConfiguration set Name = ?, Type = ? WHERE DeviceConfigurationId = ?";

        }

        // Save Configuration
        simpleJdbcTemplate.update(sql,
                                  configuration.getName(),
                                  configuration.getType().toString(),
                                  configuration.getId());

        // Remove any existing items
        String itemDelete = "DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationId = ?";
        simpleJdbcTemplate.update(itemDelete, configuration.getId());

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
                simpleJdbcTemplate.update(itemSql,
                                          itemId,
                                          configuration.getId(),
                                          dbFieldName,
                                          value);
            }
        }
    }

    public ConfigurationBase getConfiguration(int id) {

        // Load configuration
        String sql = "SELECT * FROM DeviceConfiguration WHERE DeviceConfigurationId = ?";

        ConfigurationBase configuration = simpleJdbcTemplate.queryForObject(sql,
                                                                            new ConfigurationBaseRowMapper(),
                                                                            id);

        // Load configuration items
        loadConfigurationItems(configuration);

        return configuration;
    }

    public List<ConfigurationBase> getAllConfigurations() {

        // Load configurations
        String sql = "SELECT * FROM DeviceConfiguration ORDER BY name";
        List<ConfigurationBase> configList = simpleJdbcTemplate.query(sql,
                                                                      new ConfigurationBaseRowMapper());

        // Load configuration items
        for (ConfigurationBase configuration : configList) {
            loadConfigurationItems(configuration);
        }

        return configList;
    }

    @Transactional
    public void delete(int id) {

        // Remove any existing items
        String itemDelete = "DELETE FROM DeviceConfigurationItem WHERE DeviceConfigurationId = ?";
        simpleJdbcTemplate.update(itemDelete, id);

        String sql = "DELETE FROM DeviceConfiguration WHERE DeviceConfigurationId = ?";
        simpleJdbcTemplate.update(sql, id);

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

        JdbcOperations ops = simpleJdbcTemplate.getJdbcOperations();
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

        Map<String, ? extends InputSource> inputMap = inputRoot.getInputMap();

        InputSource input = null;
        InputType inputType = null;
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

}
