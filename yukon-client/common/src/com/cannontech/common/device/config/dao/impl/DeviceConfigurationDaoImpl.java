package com.cannontech.common.device.config.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidConfigurationRemovalException;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.DisplayableConfigurationCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.model.jaxb.DeviceConfigurationCategories;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class DeviceConfigurationDaoImpl implements DeviceConfigurationDao {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationDaoImpl.class);
    
    public static final String CONFIG_OBJECT_TYPE = "config";
    public static final String DEVICE_OBJECT_TYPE = "device";
    public static final String CATEGORY_OBJECT_TYPE = "category";
    
    public static final int DEFAULT_DNP_CONFIG_ID = -1;
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private Resource inputFile;
    private Resource schemaFile;
    
    private Map<CategoryType, Category> typeToCategoryMap;
    
    @Override
    public Category getCategoryByType(CategoryType categoryType) {
        return typeToCategoryMap.get(categoryType);
    }
    
    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }
    
    public void setSchemaFile(Resource schemaFile) {
        this.schemaFile = schemaFile;
    }
    
    @PostConstruct
    public void initialize() throws IOException, SAXException, ParserConfigurationException, JAXBException {
        validateSchema(inputFile);
        
        InputStreamReader reader = new InputStreamReader(inputFile.getInputStream());
        JAXBContext jaxbContext = JAXBContext.newInstance(DeviceConfigurationCategories.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        
        DeviceConfigurationCategories categories = (DeviceConfigurationCategories) unmarshaller.unmarshal(reader);
        
        Builder<CategoryType, Category> mapBuilder = new Builder<>();
        
        for (Category category : categories.getCategory()) {
            mapBuilder.put(category.getType(), category);
        }
        
        typeToCategoryMap = mapBuilder.build();
        
        if (log.isDebugEnabled()) {
            for (Category category : categories.getCategory()) {
                log.info("Loaded category " + category.getType().value());
            }
        }
    }
    
    /**
     * Validate an XML file against the schema.
     * @param inputFile
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private void validateSchema(Resource inputFile) throws IOException, SAXException, ParserConfigurationException {
        InputStream is = inputFile.getInputStream();
        URL schemaUrl = schemaFile.getURL();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false); // this is for DTD, so we want it off
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        factory.setSchema(schema);

        SAXParser saxParser = factory.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new ErrorHandler() {
            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        
        xmlReader.parse(new org.xml.sax.InputSource(is));
        is.close();
    }
    
    private static class ItemByCategoryRowMapper implements YukonRowCallbackHandler {
        private final ListMultimap<Integer, DeviceConfigCategoryItem> itemsByCategoryId = ArrayListMultimap.create();
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            int categoryId = rs.getInt("DeviceConfigCategoryId");
            String itemName = rs.getString("ItemName");
            String itemValue = rs.getString("ItemValue");
            
            DeviceConfigCategoryItem item = new DeviceConfigCategoryItem(categoryId, itemName, itemValue);
            
            itemsByCategoryId.put(item.getCategoryId(), item);
        }
    }
    
    private static class CategoryRowMapper implements YukonRowCallbackHandler {
        private final ListMultimap<Integer, DeviceConfigCategoryItem> itemsByCategoryId;
        private final Map<Integer, DeviceConfigCategory> categoriesById = new TreeMap<>();
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            int categoryId = rs.getInt("DeviceConfigCategoryId");
            String categoryType = rs.getString("CategoryType");
            String categoryName = rs.getString("Name");
            List<DeviceConfigCategoryItem> categoryItems = itemsByCategoryId.get(categoryId);
            
            DeviceConfigCategory category = 
                new DeviceConfigCategory(categoryId, categoryType, categoryName, categoryItems);
            
            categoriesById.put(categoryId, category);
        }
        
        public CategoryRowMapper(ListMultimap<Integer, DeviceConfigCategoryItem> itemsByCategoryId) {
            this.itemsByCategoryId = itemsByCategoryId;
        }
    }
    
    private static class ConfigurationToCategoryRowMapper implements YukonRowCallbackHandler {
        private final ListMultimap<Integer, Integer> configurationToCategoryMap = ArrayListMultimap.create();
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            int configurationId = rs.getInt("DeviceConfigurationId");
            int categoryId = rs.getInt("DeviceConfigCategoryId");
            
            configurationToCategoryMap.put(configurationId, categoryId);
        }
    }
    
    private static class ConfigurationToDeviceTypesRowMapper implements YukonRowCallbackHandler {
        private final SetMultimap<Integer, PaoType> configurationToDeviceTypesMap = TreeMultimap.create();
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            int configurationId = rs.getInt("DeviceConfigurationId");
            PaoType paoType = rs.getEnum("PaoType", PaoType.class);
            
            configurationToDeviceTypesMap.put(configurationId, paoType);
        }
    }
    
    private static class ConfigurationRowMapper implements YukonRowMapper<DeviceConfiguration> {
        private final ListMultimap<Integer, Integer> configToCategoryMap;
        private final SetMultimap<Integer, PaoType> configToDeviceTypesMap;
        private final Map<Integer, DeviceConfigCategory> categoriesById;
        
        public ConfigurationRowMapper(ListMultimap<Integer, Integer> configToCategoryMap,
                                      SetMultimap<Integer, PaoType> configToDeviceTypesMap,
                                      Map<Integer, DeviceConfigCategory> categoriesById) {
            this.configToCategoryMap = configToCategoryMap;
            this.configToDeviceTypesMap = configToDeviceTypesMap;
            this.categoriesById = categoriesById;
        }
        
        @Override
        public DeviceConfiguration mapRow(YukonResultSet rs) throws SQLException {
            int configurationId = rs.getInt("DeviceConfigurationId");
            String name = rs.getString("Name");
            
            List<Integer> categoryIds = configToCategoryMap.get(configurationId);
            
            List<DeviceConfigCategory> categories = new ArrayList<>();
            for (Integer categoryId : categoryIds) {
                categories.add(categoriesById.get(categoryId));
            }
            
            Set<PaoType> supportedTypes = configToDeviceTypesMap.get(configurationId);
            
            return new DeviceConfiguration(configurationId, name, categories, supportedTypes);
        }
    }
    
    private static class LightConfigurationRowMapper implements YukonRowMapper<LightDeviceConfiguration> {
        @Override
        public LightDeviceConfiguration mapRow(YukonResultSet rs) throws SQLException {
            int configurationId = rs.getInt("DeviceConfigurationId");
            String name = rs.getString("Name");
            
            return new LightDeviceConfiguration(configurationId, name);
        }
    }
    
    @Override
    public List<DeviceConfiguration> getAllDeviceConfigurations() {
        // Find the items for the configuration.
        ItemByCategoryRowMapper itemByCategoryRowMapper = getAllDeviceConfigurationItems();
        
        // We have the items, get the categories.
        CategoryRowMapper categoryRowMapper = getAllCategories(itemByCategoryRowMapper);
        
        ConfigurationToCategoryRowMapper configurationToCategoryRowMapper = 
            getAllConfigurationCategoryMappings(categoryRowMapper);
        
        // Get the supported Device Types.
        ConfigurationToDeviceTypesRowMapper configurationToDeviceTypesRowMapper =
            getAllConfigurationDeviceTypeMappings();
        
        // Got the categories, wrap it all up into a configuration
        SqlStatementBuilder configSql = new SqlStatementBuilder();
        configSql.append("SELECT DeviceConfigurationId, Name");
        configSql.append("FROM DeviceConfiguration");
        
        List<DeviceConfiguration> configurations = 
            jdbcTemplate.query(
                configSql, 
                new ConfigurationRowMapper(
                    configurationToCategoryRowMapper.configurationToCategoryMap,
                    configurationToDeviceTypesRowMapper.configurationToDeviceTypesMap,
                    categoryRowMapper.categoriesById));
        
        return configurations;
    }
    
    @Override
    public List<LightDeviceConfiguration> getAllLightDeviceConfigurations() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceConfigurationId, Name");
        sql.append("FROM DeviceConfiguration");
        
        List<LightDeviceConfiguration> configurations = jdbcTemplate.query(sql, new LightConfigurationRowMapper());
        
        return configurations;
    }
    
    @Override
    public List<DisplayableConfigurationCategory> getAllDeviceConfigurationCategories() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DCC.Name, DCC.CategoryType, DCC.DeviceConfigCategoryId,");
        sql.append("    (SELECT COUNT(*)");
        sql.append("     FROM DeviceConfigCategoryMap DCCM");
        sql.append("     WHERE DCCM.DeviceConfigCategoryId = DCC.DeviceConfigCategoryId) AS NumConfigs");
        sql.append("FROM DeviceConfigCategory DCC");
        
        List<DisplayableConfigurationCategory> displayables = 
            jdbcTemplate.query(sql, new YukonRowMapper<DisplayableConfigurationCategory>() {
                @Override
                public DisplayableConfigurationCategory mapRow(YukonResultSet rs) throws SQLException {
                    int categoryId = rs.getInt("DeviceConfigCategoryId");
                    String name = rs.getString("Name");
                    String type = rs.getString("CategoryType");
                    int numConfigs = rs.getInt("NumConfigs");
                    
                    return new DisplayableConfigurationCategory(categoryId, name, type, numConfigs);
                }
        });
        
        return displayables;
    }
    
    @Override
    public DeviceConfigCategory getDeviceConfigCategory(int categoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DCI.DeviceConfigCategoryId, DCI.ItemName, DCI.ItemValue");
        sql.append("FROM DeviceConfigCategoryItem DCI");
        sql.append("   JOIN DeviceConfigCategory DCC");
        sql.append("      ON DCI.DeviceConfigCategoryId = DCC.DeviceConfigCategoryId");
        sql.append("WHERE DCC.DeviceConfigCategoryId").eq(categoryId);
        sql.append("ORDER BY DCI.ItemName ASC");
        
        ItemByCategoryRowMapper itemByCategoryRowMapper = new ItemByCategoryRowMapper();
        jdbcTemplate.query(sql, itemByCategoryRowMapper);
        
        SqlStatementBuilder categorySql = new SqlStatementBuilder();
        categorySql.append("SELECT DCC.DeviceConfigCategoryId, DCC.CategoryType, DCC.Name");
        categorySql.append("FROM DeviceConfigCategory DCC");
        categorySql.append("WHERE DCC.DeviceConfigCategoryId").eq(categoryId);
        
        CategoryRowMapper categoryRowMapper = new CategoryRowMapper(itemByCategoryRowMapper.itemsByCategoryId);
        jdbcTemplate.query(categorySql, categoryRowMapper);
        
        if (categoryRowMapper.categoriesById.size() != 1) {
            // Problem
        }
        
        return categoryRowMapper.categoriesById.get(categoryId);
    }
    
    @Override
    public DeviceConfiguration getDeviceConfiguration(int configId) throws NotFoundException {
        // Find the items for the configuration.
        ItemByCategoryRowMapper itemByCategoryRowMapper = getItemsForConfiguration(configId);
        
        // We have the items, get the categories.
        CategoryRowMapper categoryRowMapper = getCategories(itemByCategoryRowMapper, configId);
        
        ConfigurationToCategoryRowMapper configurationToCategoryRowMapper = getCategoriesForConfig(configId);
        
        // Get the supported device types.
        ConfigurationToDeviceTypesRowMapper configurationToDeviceTypesRowMapper = getDeviceTypesForConfig(configId);
        
        // Got the categories, wrap it all up into a configuration
        SqlStatementBuilder configSql = new SqlStatementBuilder();
        configSql.append("SELECT DeviceConfigurationId, Name");
        configSql.append("FROM DeviceConfiguration");
        configSql.append("WHERE DeviceConfigurationId").eq(configId);
        
        DeviceConfiguration configuration = 
            jdbcTemplate.queryForObject(
                configSql, 
                new ConfigurationRowMapper(
                    configurationToCategoryRowMapper.configurationToCategoryMap,
                    configurationToDeviceTypesRowMapper.configurationToDeviceTypesMap,
                    categoryRowMapper.categoriesById));
        
        return configuration;
    }
    
    private ItemByCategoryRowMapper getAllDeviceConfigurationItems() {
        // No specific id, get them all.
        return getItemsForConfiguration(null);
    }
    
    private CategoryRowMapper getAllCategories(ItemByCategoryRowMapper itemByCategoryRowMapper) {
        // No specific id, get them all.
        return getCategories(itemByCategoryRowMapper, null);
    }
    
    private ConfigurationToCategoryRowMapper getAllConfigurationCategoryMappings(CategoryRowMapper categoryRowMapper) {
        // No specific id, get them all.
        return getCategoriesForConfig(null);
    }
    
    private ConfigurationToDeviceTypesRowMapper getAllConfigurationDeviceTypeMappings() {
        // No specific id, get them all.
        return getDeviceTypesForConfig(null);
    }
    
    private ItemByCategoryRowMapper getItemsForConfiguration(Integer configId) {
        SqlStatementBuilder itemSql = new SqlStatementBuilder();
        itemSql.append("SELECT DCI.DeviceConfigCategoryId, DCI.ItemName, DCI.ItemValue");
        itemSql.append("FROM DeviceConfigCategoryItem DCI");
        itemSql.append("   JOIN DeviceConfigCategory DCC");
        itemSql.append("      ON DCI.DeviceConfigCategoryId = DCC.DeviceConfigCategoryId");
        itemSql.append("   JOIN DeviceConfigCategoryMap DCCM");
        itemSql.append("      ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId");
        itemSql.append("   JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationId");
        
        if (configId != null) {
            itemSql.append("WHERE DC.DeviceConfigurationId").eq(configId);
        }
        
        ItemByCategoryRowMapper itemByCategoryRowMapper = new ItemByCategoryRowMapper();
        jdbcTemplate.query(itemSql, itemByCategoryRowMapper);
        
        return itemByCategoryRowMapper;
    }
    
    private CategoryRowMapper getCategories(ItemByCategoryRowMapper itemByCategoryRowMapper, Integer configId) {
        SqlStatementBuilder categorySql = new SqlStatementBuilder();
        categorySql.append("SELECT DCC.DeviceConfigCategoryId, DCC.CategoryType, DCC.Name");
        categorySql.append("FROM DeviceConfigCategory DCC");
        categorySql.append("   JOIN DeviceConfigCategoryMap DCCM");
        categorySql.append("      ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId");
        categorySql.append("   JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationId");
        
        if (configId != null) {
            categorySql.append("WHERE DC.DeviceConfigurationId").eq(configId);
        }
        
        CategoryRowMapper categoryRowMapper = new CategoryRowMapper(itemByCategoryRowMapper.itemsByCategoryId);
        jdbcTemplate.query(categorySql, categoryRowMapper);
        
        return categoryRowMapper;
    }
    
    private ConfigurationToDeviceTypesRowMapper getDeviceTypesForConfig(Integer configId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceConfigurationId, PaoType");
        sql.append("FROM DeviceConfigDeviceTypes");
        
        if (configId != null) {
            sql.append("WHERE DeviceConfigurationId").eq(configId);
        }
        
        ConfigurationToDeviceTypesRowMapper rowMapper = new ConfigurationToDeviceTypesRowMapper();
        jdbcTemplate.query(sql, rowMapper);
        
        return rowMapper;
        
    }
    
    private ConfigurationToCategoryRowMapper getCategoriesForConfig(Integer configId) {
        SqlStatementBuilder mapSql = new SqlStatementBuilder();
        mapSql.append("SELECT DeviceConfigurationId, DeviceConfigCategoryId");
        mapSql.append("FROM DeviceConfigCategoryMap");
        
        if (configId != null) {
            mapSql.append("WHERE DeviceConfigurationId").eq(configId);
        }
        
        ConfigurationToCategoryRowMapper configurationToCategoryRowMapper = new ConfigurationToCategoryRowMapper();
        jdbcTemplate.query(mapSql, configurationToCategoryRowMapper);
        
        return configurationToCategoryRowMapper;
    }
    
    @Override
    public boolean categoriesExistForType(String categoryType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfigCategory");
        sql.append("WHERE CategoryType").eq(categoryType);
        
        int numCategories = jdbcTemplate.queryForInt(sql);
        
        return numCategories > 0;
    }
    
    @Override
    public int getNumberOfDevicesForConfiguration(int configId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfigurationDeviceMap");
        sql.append("WHERE DeviceConfigurationId").eq(configId);
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getNumberOfConfigurationsForCategory(int categoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfigCategoryMap");
        sql.append("WHERE DeviceConfigCategoryId").eq(categoryId);
        
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    @Transactional
    public int saveConfiguration(DeviceConfiguration configuration) {
        Integer configurationId = configuration.getConfigurationId();
        
        DbChangeType dbChangeType = DbChangeType.ADD;
        if (configurationId == null) {
            // Create the configuration
            configurationId = nextValueHelper.getNextValue("DeviceConfiguration");
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("DeviceConfiguration");
            params.addValue("DeviceConfigurationId", configurationId);
            params.addValue("Name", configuration.getName());
            
            jdbcTemplate.update(sql);
        } else {
            // Clear out any previous supported pao types.
            SqlStatementBuilder deviceTypeSql = new SqlStatementBuilder();
            deviceTypeSql.append("DELETE FROM DeviceConfigDeviceTypes");
            deviceTypeSql.append("WHERE DeviceConfigurationId").eq(configurationId);
            
            jdbcTemplate.update(deviceTypeSql);
            
            // Clear out any previous category assignments.
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM DeviceConfigCategoryMap");
            sql.append("WHERE DeviceConfigurationId").eq(configurationId);
            
            jdbcTemplate.update(sql);
            
            // Update the name.
            SqlStatementBuilder nameSql = new SqlStatementBuilder();
            SqlParameterSink params = nameSql.update("DeviceConfiguration");
            params.addValue("Name", configuration.getName());
            nameSql.append("WHERE DeviceConfigurationId").eq(configurationId);
            
            jdbcTemplate.update(nameSql);
            
            dbChangeType = DbChangeType.UPDATE;
        }
        
        // Write the category assignments.
        writeCategoriesAndDeviceTypes(configuration, configurationId);
        
        dbChangeManager.processDbChange(configurationId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CONFIG_OBJECT_TYPE, 
                                        dbChangeType);
        
        return configurationId;
    }
    
    private void writeCategoriesAndDeviceTypes(DeviceConfiguration configuration, int configurationId) {
        List<Integer> categoryIds = new ArrayList<>();
        for (DeviceConfigCategory category : configuration.getCategories()) {
            categoryIds.add(category.getCategoryId());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO DeviceConfigCategoryMap");
        sql.append("(DeviceConfigurationId, DeviceConfigCategoryId)");
        sql.append("VALUES (?, ?)");
        
        List<Object[]> batchArgs = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            batchArgs.add(new Object[] {configurationId, categoryId});
        }
        
        jdbcTemplate.batchUpdate(sql.getSql(), batchArgs);
        
        SqlStatementBuilder deviceTypeSql = new SqlStatementBuilder();
        deviceTypeSql.append("INSERT INTO DeviceConfigDeviceTypes");
        deviceTypeSql.append("(DeviceConfigDeviceTypeId, DeviceConfigurationId, PaoType)");
        deviceTypeSql.append("VALUES (?, ?, ?)");
        
        List<Object[]> deviceTypeArgs = new ArrayList<>();
        for (PaoType paoType : configuration.getSupportedDeviceTypes()) {
            int mappingId = nextValueHelper.getNextValue("DeviceConfigDeviceTypes");
            deviceTypeArgs.add(new Object[] {mappingId, configurationId, paoType.getDatabaseRepresentation()});
        }
        
        jdbcTemplate.batchUpdate(deviceTypeSql.getSql(), deviceTypeArgs);
    }
    
    @Override
    public void deleteConfiguration(int deviceConfigurationId) throws InvalidConfigurationRemovalException {
        if (DEFAULT_DNP_CONFIG_ID == deviceConfigurationId) {
            throw new InvalidConfigurationRemovalException("Cannot remove the default DNP configuration.");
        }
        
        // Delete the category associations first.
        SqlStatementBuilder categorySql = new SqlStatementBuilder();
        categorySql.append("DELETE FROM DeviceConfigCategoryMap");
        categorySql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
    
        jdbcTemplate.update(categorySql);
        
        // Delete the config
        SqlStatementBuilder configSql = new SqlStatementBuilder();
        configSql.append("DELETE FROM DeviceConfiguration");
        configSql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
        
        jdbcTemplate.update(configSql);
        
        dbChangeManager.processDbChange(deviceConfigurationId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CONFIG_OBJECT_TYPE, 
                                        DbChangeType.DELETE);
    }
    
    @Override
    @Transactional
    public void deleteCategory(int categoryId) {
        // Delete the items first.
        SqlStatementBuilder itemSql = new SqlStatementBuilder();
        itemSql.append("DELETE FROM DeviceConfigCategoryItem");
        itemSql.append("WHERE DeviceConfigCategoryId").eq(categoryId);
        
        jdbcTemplate.update(itemSql);
        
        // Delete the category
        SqlStatementBuilder categorySql = new SqlStatementBuilder();
        categorySql.append("DELETE FROM DeviceConfigCategory");
        categorySql.append("WHERE DeviceConfigCategoryId").eq(categoryId);
        
        jdbcTemplate.update(categorySql);

        dbChangeManager.processDbChange(categoryId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CATEGORY_OBJECT_TYPE, 
                                        DbChangeType.DELETE);
    }
    
    @Override
    @Transactional
    public int saveCategory(DeviceConfigCategory category) {
        Integer categoryId = category.getCategoryId();
        
        DbChangeType dbChangeType = DbChangeType.ADD;
        if (categoryId == null) {
            // We need to create the category before inserting the items.
            categoryId = nextValueHelper.getNextValue("DeviceConfigCategory");
            
            createCategory(categoryId, category);
            
            // Add the items.
            for (DeviceConfigCategoryItem item : category.getDeviceConfigurationItems()) {
                insertCategoryItem(item, categoryId);
            }
        } else {
            // Update the name.
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.update("DeviceConfigCategory");
            params.addValue("Name", category.getCategoryName());
            sql.append("WHERE DeviceConfigCategoryId").eq(category.getCategoryId());
            
            jdbcTemplate.update(sql);
            
            // Update the item values.
            for (DeviceConfigCategoryItem item : category.getDeviceConfigurationItems()) {
                updateCategoryItem(item);
            }
            
            dbChangeType = DbChangeType.UPDATE;
        }
        
        dbChangeManager.processDbChange(categoryId, 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        CATEGORY_OBJECT_TYPE, 
                                        dbChangeType);
        
        return categoryId;
    }
    
    private void insertCategoryItem(DeviceConfigCategoryItem item, int categoryId) {
        int deviceConfigurationItemId = nextValueHelper.getNextValue("DeviceConfigCategoryItem");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("DeviceConfigCategoryItem");
        params.addValue("DeviceConfigurationItemId", deviceConfigurationItemId);
        params.addValue("DeviceConfigCategoryId", categoryId);
        params.addValue("ItemName", item.getFieldName());
        params.addValue("ItemValue", item.getValue());
        
        jdbcTemplate.update(sql);
    }
    
    private void updateCategoryItem(DeviceConfigCategoryItem item) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.update("DeviceConfigCategoryItem");
        params.addValue("ItemValue", item.getValue());
        
        sql.append("WHERE ItemName").eq(item.getFieldName());
        sql.append("AND DeviceConfigCategoryId").eq(item.getCategoryId());
        
        jdbcTemplate.update(sql);
    }
    
    private void createCategory(int categoryId, DeviceConfigCategory category) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params = sql.insertInto("DeviceConfigCategory");
        params.addValue("DeviceConfigCategoryId", categoryId);
        params.addValue("CategoryType", category.getCategoryType());
        params.addValue("Name", category.getCategoryName());
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public boolean isDeviceConfigurationAvailable(PaoType paoType) {
        return getAllConfigurationsByType(paoType).size() > 0;
    }
    
    @Override
    public List<LightDeviceConfiguration> getAllConfigurationsByType(PaoType paoType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DC.DeviceConfigurationId, DC.Name");
        sql.append("FROM DeviceConfiguration DC");
        sql.append("   JOIN DeviceConfigDeviceTypes DCDT");
        sql.append("      ON DC.DeviceConfigurationId = DCDT.DeviceConfigurationId");
        sql.append("WHERE DCDT.PaoType").eq(paoType);
        
        List<LightDeviceConfiguration> configurations = jdbcTemplate.query(sql, new LightConfigurationRowMapper());
        
        return configurations;
    }
    
    @Override
    public LightDeviceConfiguration findConfigurationForDevice(YukonDevice device) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DC.DeviceConfigurationId, DC.Name");
        sql.append("FROM DeviceConfiguration DC");
        sql.append("   JOIN DeviceConfigurationDeviceMap DCDM ON");
        sql.append("   DC.DeviceConfigurationId = DCDM.DeviceConfigurationId");
        sql.append("WHERE DCDM.DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
        try {
            return jdbcTemplate.queryForObject(sql, new LightConfigurationRowMapper());
        } catch(IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public boolean isTypeSupportedByConfiguration(LightDeviceConfiguration configuration, PaoType paoType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfigDeviceTypes");
        sql.append("WHERE DeviceConfigurationId").eq(configuration.getConfigurationId());
        sql.append("   AND PaoType").eq(paoType);
        
        return jdbcTemplate.queryForInt(sql) == 1;
    }
    
    @Override
    public DeviceConfiguration getDefaultDNPConfiguration() {
        return getDeviceConfiguration(DEFAULT_DNP_CONFIG_ID);
    }
    
    @Override
    public DNPConfiguration getDnpConfiguration(DeviceConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        
        List<DeviceConfigCategory> categories = configuration.getCategories();
        
        DeviceConfigCategory dnpCategory = null;
        for (DeviceConfigCategory category : categories) {
            if (CategoryType.DNP.value().compareTo(category.getCategoryType()) == 0) {
                dnpCategory = category;
                break;
            }
        }
        
        if (dnpCategory == null) {
            return null;
        }
        
        DNPConfiguration dnpConfiguration = 
            new DNPConfiguration(configuration.getConfigurationId(), configuration.getName());
        
        for (DeviceConfigCategoryItem item : dnpCategory.getDeviceConfigurationItems()) {
            switch (item.getFieldName()) {
            case "internalRetries":
                dnpConfiguration.setInternalRetries(Integer.valueOf(item.getValue()));
                break;
            case "enableDnpTimesyncs":
                dnpConfiguration.setEnableDnpTimesyncs(Boolean.valueOf(item.getValue()));
                break;
            case "enableUnsolicitedMessages":
                dnpConfiguration.setEnableUnsolicitedMessages(Boolean.valueOf(item.getValue()));
                break;
            case "localTime":
                dnpConfiguration.setLocalTime(Boolean.valueOf(item.getValue()));
                break;
            case "omitTimeRequest":
                dnpConfiguration.setOmitTimeRequest(Boolean.valueOf(item.getValue()));
                break;
            }
        }
        
        return dnpConfiguration;
    }
    
    @Override
    @Transactional
    public void assignConfigToDevice(LightDeviceConfiguration configuration, YukonDevice device) 
            throws InvalidDeviceTypeException {
        if (configuration == null || device == null) {
            throw new InvalidDeviceTypeException("Unable to assign device configuration with a null " +
                                                 "device or a null configuration.");
        }

        // Only add the devices whose type is supported by the configuration
        if (!isTypeSupportedByConfiguration(configuration, device.getPaoIdentifier().getPaoType())) {
            throw new InvalidDeviceTypeException("Device type: " 
                    + device.getPaoIdentifier().getPaoType().name() 
                    + " is invalid for config: " + configuration.getName());
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink params;
        boolean isConfigUpdate = findConfigurationForDevice(device) != null;
        if (isConfigUpdate) {
            params = sql.update("DeviceConfigurationDeviceMap");
            params.addValue("DeviceConfigurationId", configuration.getConfigurationId());
            sql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
        } else {
            params = sql.insertInto("DeviceConfigurationDeviceMap");
            params.addValue("DeviceID", device.getPaoIdentifier().getPaoId());
            params.addValue("DeviceConfigurationId", configuration.getConfigurationId());
        }
        
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(), 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        DEVICE_OBJECT_TYPE, 
                                        isConfigUpdate ? DbChangeType.UPDATE : DbChangeType.ADD);
    }

    @Override
    @Transactional
    public void unassignConfig(YukonDevice device) throws InvalidDeviceTypeException {
        if (paoDefinitionDao.isDnpConfigurationType(device.getPaoIdentifier().getPaoType())) {
            throw new InvalidDeviceTypeException("Device type: " 
                    + device.getPaoIdentifier().getPaoType().name() 
                    + " cannot be unassigned from a configuration.");
        }
        
        SqlStatementBuilder removeSql = new SqlStatementBuilder();
        removeSql.append("DELETE FROM DeviceConfigurationDeviceMap");
        removeSql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
        
        jdbcTemplate.update(removeSql);
        
        dbChangeManager.processDbChange(device.getPaoIdentifier().getPaoId(), 
                                        DBChangeMsg.CHANGE_CONFIG_DB, 
                                        DBChangeMsg.CAT_DEVICE_CONFIG, 
                                        DEVICE_OBJECT_TYPE, 
                                        DbChangeType.DELETE);
    }

    @Override
    public String getValueForItemName(int configId, String itemName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ItemValue");
        sql.append("FROM DeviceConfigCategoryItem");
        sql.append("WHERE DeviceConfigurationID").eq(configId).append("AND ItemName").eq(itemName);
        
        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public boolean checkForNameConflict(String name, Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfiguration");
        sql.append("WHERE Name").eq(name);
        if (id != null) {
            sql.append("AND DeviceConfigurationId").neq(id);
        }
        int otherIds = jdbcTemplate.queryForInt(sql);
        return otherIds != 0;
    }
    
    @Override
    public boolean isConfigurationDeletable(int configId) {
        // A configuration is deletable if it isn't the default DNP configuration and has no assigned devices.
        return configId != DEFAULT_DNP_CONFIG_ID && getNumberOfDevicesForConfiguration(configId) == 0;
    }
}
