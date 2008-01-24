package com.cannontech.common.device.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.web.input.InputBeanWrapperImpl;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.type.InputType;

/**
 * Implementation class for DeviceConfigurationDao
 */
public class DeviceConfigurationDaoImpl implements DeviceConfigurationDao {

    public static final String DB_CHANGE_OBJECT_TYPE = "config";

    private SimpleJdbcTemplate simpleJdbcTemplate = null;
    private NextValueHelper nextValueHelper = null;

    private PaoGroupsWrapper paoGroupsWrapper;
    private DBPersistentDao dbPersistentDao = null;

    private List<ConfigurationTemplate> configurationTemplateList = null;

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
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
        int typeOfChange = DBChangeMsg.CHANGE_TYPE_ADD;
        if (configuration.getId() == null) {

            // Insert new configuration

            int id = nextValueHelper.getNextValue("DeviceConfiguration");
            configuration.setId(id);
            sql = "INSERT INTO DeviceConfiguration (Name, Type, DeviceConfigurationId) values (?, ?, ?)";

        } else {

            // Update configuration
            sql = "UPDATE DeviceConfiguration set Name = ?, Type = ? WHERE DeviceConfigurationId = ?";

            typeOfChange = DBChangeMsg.CHANGE_TYPE_UPDATE;
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

        DBChangeMsg dbChange = new DBChangeMsg(configuration.getId(),
                                               DBChangeMsg.CHANGE_CONFIG_DB,
                                               DBChangeMsg.CAT_DEVICE_CONFIG,
                                               DB_CHANGE_OBJECT_TYPE,
                                               typeOfChange);

        dbPersistentDao.processDBChange(dbChange);

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

        DBChangeMsg dbChange = new DBChangeMsg(id,
                                               DBChangeMsg.CHANGE_CONFIG_DB,
                                               DBChangeMsg.CAT_DEVICE_CONFIG,
                                               DB_CHANGE_OBJECT_TYPE,
                                               DBChangeMsg.CHANGE_TYPE_DELETE);

        dbPersistentDao.processDBChange(dbChange);
    }

    public List<YukonDevice> getAssignedDevices(ConfigurationBase configuration) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT dcdm.DeviceId paobjectid, ypo.type");
        sql.append("FROM DeviceConfigurationDeviceMap dcdm");
        sql.append("JOIN YukonPaobject ypo on ypo.paobjectid = dcdm.DeviceId");
        sql.append("WHERE dcdm.DeviceConfigurationId = ?");
        List<YukonDevice> deviceList = simpleJdbcTemplate.query(sql.toString(),
                                                                new YukonDeviceRowMapper(paoGroupsWrapper),
                                                                configuration.getId());

        return deviceList;
    }

    @Transactional
    public void assignConfigToDevices(ConfigurationBase configuration,
            Collection<YukonDevice> devices) {

        // Get the device types that the configuration supports
        String[] supportedDeviceTypeList = configuration.getType().getSupportedDeviceTypeList();
        List<Integer> typeList = new ArrayList<Integer>();
        for (String deviceType : supportedDeviceTypeList) {
            int type = paoGroupsWrapper.getDeviceType(deviceType);
            typeList.add(type);
        }

        List<DBChangeMsg> dbChangeList = new ArrayList<DBChangeMsg>();

        for (YukonDevice device : devices) {

            // Clean out any assigned configs - device can only be assigned one
            // config
            unassignConfig(device.getDeviceId());

            // Only add the devices whose type is supported by the configuration
            if (typeList.contains(device.getType())) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("insert into DeviceConfigurationDeviceMap");
                sql.append("(DeviceConfigurationId, DeviceId)");
                sql.append("values");
                sql.append("(?, ?)");

                try {
                    simpleJdbcTemplate.update(sql.toString(),
                                              configuration.getId(),
                                              device.getDeviceId());
                } catch (DataIntegrityViolationException e) {
                    // ignore - tried to insert duplicate
                }
            }

            // TODO add objecttype
            DBChangeMsg dbChange = new DBChangeMsg(device.getDeviceId(),
                                                   DBChangeMsg.CHANGE_CONFIG_DB,
                                                   DBChangeMsg.CAT_DEVICE_CONFIG,
                                                   "device",
                                                   DBChangeMsg.CHANGE_TYPE_UPDATE);
            dbChangeList.add(dbChange);

        }

        // Send DBChangeMsgs
        for (DBChangeMsg msg : dbChangeList) {
            dbPersistentDao.processDBChange(msg);
        }

    }

    public void unassignConfig(Integer deviceId) {

        // Remove any assigned configuration mappings
        String sql = "DELETE FROM DeviceConfigurationDeviceMap WHERE DeviceId = ?";
        simpleJdbcTemplate.update(sql, deviceId);

        // TODO add objecttype
        DBChangeMsg dbChange = new DBChangeMsg(deviceId,
                                               DBChangeMsg.CHANGE_CONFIG_DB,
                                               DBChangeMsg.CAT_DEVICE_CONFIG,
                                               "device",
                                               DBChangeMsg.CHANGE_TYPE_UPDATE);
        dbPersistentDao.processDBChange(dbChange);
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

}
