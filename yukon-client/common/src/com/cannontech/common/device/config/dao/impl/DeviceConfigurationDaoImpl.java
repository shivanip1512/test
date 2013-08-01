package com.cannontech.common.device.config.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.DisplayableConfigurationCategory;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.Category;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.model.jaxb.DeviceConfigurationCategories;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.jaxb.DeviceCategories;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.TreeMultimap;

public class DeviceConfigurationDaoImpl implements DeviceConfigurationDao {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationDaoImpl.class);
    
    public static final int DEFAULT_DNP_CONFIG_ID = -1;
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
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
            String description = rs.getString("Description");
            List<DeviceConfigCategoryItem> categoryItems = itemsByCategoryId.get(categoryId);
            
            DeviceConfigCategory category = 
                new DeviceConfigCategory(categoryId, categoryType, categoryName, description, categoryItems);
            
            categoriesById.put(categoryId, category);
        }
        
        public CategoryRowMapper(ListMultimap<Integer, DeviceConfigCategoryItem> itemsByCategoryId) {
            this.itemsByCategoryId = itemsByCategoryId;
        }
    }
    
    private static class DisplayableCategoryRowMapper implements YukonRowCallbackHandler {
        private final ListMultimap<Integer, String> configurationsByCategoryId = ArrayListMultimap.create();
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            int categoryId = rs.getInt("DeviceConfigCategoryId");
            String configName = rs.getString("Name");
            
            configurationsByCategoryId.put(categoryId, configName);
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
            String description = rs.getString("Description");
            
            List<Integer> categoryIds = configToCategoryMap.get(configurationId);
            
            List<DeviceConfigCategory> categories = new ArrayList<>();
            for (Integer categoryId : categoryIds) {
                categories.add(categoriesById.get(categoryId));
            }
            
            Set<PaoType> supportedTypes = configToDeviceTypesMap.get(configurationId);
            
            return new DeviceConfiguration(configurationId, name, description, categories, supportedTypes);
        }
    }
    
    private static class LightConfigurationRowMapper implements YukonRowMapper<LightDeviceConfiguration> {
        @Override
        public LightDeviceConfiguration mapRow(YukonResultSet rs) throws SQLException {
            int configurationId = rs.getInt("DeviceConfigurationId");
            String name = rs.getString("Name");
            String description = rs.getString("Description");
            
            return new LightDeviceConfiguration(configurationId, name, description);
        }
    }
    
    @Override
    public List<LightDeviceConfiguration> getAllLightDeviceConfigurations() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceConfigurationId, Name, Description");
        sql.append("FROM DeviceConfiguration");
        
        List<LightDeviceConfiguration> configurations = jdbcTemplate.query(sql, new LightConfigurationRowMapper());
        
        return configurations;
    }
    
    @Override
    public List<DisplayableConfigurationCategory> getAllDeviceConfigurationCategories() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DCC.DeviceConfigCategoryId, DC.Name");
        sql.append("FROM DeviceConfigCategory DCC");
        sql.append("    JOIN DeviceConfigCategoryMap DCCM ON DCCM.DeviceConfigCategoryId = DCC.DeviceConfigCategoryId");
        sql.append("    JOIN DeviceConfiguration DC ON DCCM.DeviceConfigurationId = DC.DeviceConfigurationId");

        final DisplayableCategoryRowMapper mapper = new DisplayableCategoryRowMapper();
        jdbcTemplate.query(sql, mapper);
        
        SqlStatementBuilder categorySql = new SqlStatementBuilder();
        categorySql.append("SELECT DCC.Name, DCC.CategoryType, DCC.DeviceConfigCategoryId");
        categorySql.append("FROM DeviceConfigCategory DCC");
        
        List<DisplayableConfigurationCategory> displayables = 
            jdbcTemplate.query(categorySql, new YukonRowMapper<DisplayableConfigurationCategory>() {
                @Override
                public DisplayableConfigurationCategory mapRow(YukonResultSet rs) throws SQLException {
                    int categoryId = rs.getInt("DeviceConfigCategoryId");
                    String name = rs.getString("Name");
                    String type = rs.getString("CategoryType");
                    
                    // mapper has the names of the configurations per categoryId.
                    return new DisplayableConfigurationCategory(
                        categoryId, 
                        name, 
                        type, 
                        mapper.configurationsByCategoryId.get(categoryId));
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
        categorySql.append("SELECT DCC.DeviceConfigCategoryId, DCC.CategoryType, DCC.Name, DCC.Description");
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
        configSql.append("SELECT DeviceConfigurationId, Name, Description");
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
    
    @Override
    public int saveConfigurationBase(Integer deviceConfigurationId, String name, String description) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfiguration");
        sql.append("WHERE LOWER(Name) = LOWER('" + name + "')");
        
        if (deviceConfigurationId != null) {
            sql.append(" AND DeviceConfigurationId").neq(deviceConfigurationId);
        }
        
        if (jdbcTemplate.queryForInt(sql) > 0) {
            throw new DuplicateException();
        }

        if (deviceConfigurationId == null) {
            // Create the base.
            int newId = nextValueHelper.getNextValue("DeviceConfiguration");
            
            sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("DeviceConfiguration");
            params.addValue("DeviceConfigurationId", newId);
            params.addValue("Name", name);
            params.addValue("Description", description);
            
            jdbcTemplate.update(sql);
            
            return newId;
        } else {
            sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.update("DeviceConfiguration");
            params.addValue("Name", name);
            params.addValue("Description", description);
            
            sql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
            
            jdbcTemplate.update(sql);
        }
        
        return deviceConfigurationId;
    }
    
    @Override
    public Set<PaoType> getSupportedTypesForConfiguration(int deviceConfigurationId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PaoType");
        sql.append("FROM DeviceConfigDeviceTypes");
        sql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
        
        List<PaoType> supportedTypes = jdbcTemplate.query(sql, new YukonRowMapper<PaoType>() {
            @Override
            public PaoType mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("PaoType", PaoType.class);
            }
        });
        
        return Sets.newHashSet(supportedTypes);
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
        categorySql.append("SELECT DCC.DeviceConfigCategoryId, DCC.CategoryType, DCC.Name, DCC.Description");
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
    public void changeCategoryAssignment(int deviceConfigurationId, int newCategoryId, CategoryType categoryType) {
        SqlStatementBuilder idSql = new SqlStatementBuilder();
        idSql.append("SELECT DCCM.DeviceConfigCategoryId");
        idSql.append("FROM DeviceConfigCategoryMap DCCM");
        idSql.append("   JOIN DeviceConfigCategory DCC ON DCCM.DeviceConfigCategoryId = DCC.DeviceConfigCategoryId");
        idSql.append("WHERE DCC.CategoryType").eq(categoryType.value());
        idSql.append("   AND DCCM.DeviceConfigurationId").eq(deviceConfigurationId);
        
        List<Integer> previousId = jdbcTemplate.query(idSql, RowMapper.INTEGER_NULLABLE);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (previousId.size() == 1) {
            // There was an assignment previously. This is an update.
            SqlParameterSink params = sql.update("DeviceConfigCategoryMap");
            params.addValue("DeviceConfigCategoryId", newCategoryId);
            sql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
            sql.append("   AND DeviceConfigCategoryId").eq(previousId.get(0));
            
            jdbcTemplate.update(sql);
        } else {
            // No assignment existed. Insert.
            SqlParameterSink params = sql.insertInto("DeviceConfigCategoryMap");
            params.addValue("DeviceConfigurationId", deviceConfigurationId);
            params.addValue("DeviceConfigCategoryId", newCategoryId);
        }
        
        jdbcTemplate.update(sql);
        
    }
    
    @Override
    public void addSupportedDeviceTypes(int deviceConfigurationId, Set<PaoType> paoTypes) {
        for (PaoType paoType : paoTypes) {
            int id = nextValueHelper.getNextValue("DeviceConfigDeviceTypes");
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("DeviceConfigDeviceTypes");
            params.addValue("DeviceConfigDeviceTypeId", id);
            params.addValue("DeviceConfigurationId", deviceConfigurationId);
            params.addValue("PaoType", paoType);
            
            jdbcTemplate.update(sql);
        }
    }

    @Override
    public void removeSupportedDeviceType(int deviceConfigurationId, PaoType paoType) {
        // Get the categories that will be different following the removal of this pao type (if any.)
        Set<CategoryType> difference = getCategoryDifferenceForPaoTypeRemove(paoType, deviceConfigurationId);
        
        Set<String> categoryTypes = new HashSet<>();
        for (CategoryType categoryType : difference) {
            categoryTypes.add(categoryType.value());
        }
        
        if (!difference.isEmpty()) {
            // There are category types that need to be removed as a result of this pao type being removed.
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DCCM.DeviceConfigCategoryId");
            sql.append("FROM DeviceConfigCategoryMap DCCM");
            sql.append("   JOIN DeviceConfigCategory DCC ON DCC.DeviceConfigCategoryId = DCCM.DeviceConfigCategoryId");
            sql.append("WHERE DCCM.DeviceConfigurationId").eq(deviceConfigurationId);
            sql.append("   AND DCC.CategoryType").in(categoryTypes);
            
            List<Integer> categoryIds = jdbcTemplate.query(sql, RowMapper.INTEGER);
            
            SqlStatementBuilder removeSql = new SqlStatementBuilder();
            removeSql.append("DELETE FROM DeviceConfigCategoryMap");
            removeSql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
            removeSql.append("   AND DeviceConfigCategoryId").in(categoryIds);
            
            jdbcTemplate.update(removeSql);
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceConfigDeviceTypes");
        sql.append("WHERE DeviceConfigurationId").eq(deviceConfigurationId);
        sql.append("   AND PaoType").eq_k(paoType);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public Set<CategoryType> getCategoryDifferenceForPaoTypeRemove(PaoType paoType, int configId) {
        Set<PaoType> supportedDeviceTypes = getSupportedTypesForConfiguration(configId);
        
        // Get the list of categories supported BEFORE the removal of the type.
        Set<DeviceCategories.Category> currentCategories = 
            paoDefinitionDao.getCategoriesForPaoTypes(supportedDeviceTypes);

        // Remove the pao type.
        supportedDeviceTypes.remove(paoType);
        
        // Get the list of categories that will exist AFTER the removal of the type.
        Set<DeviceCategories.Category> categoriesPostRemoval = 
            paoDefinitionDao.getCategoriesForPaoTypes(supportedDeviceTypes);
        
        // Return the difference between the sets. 
        return getCategoryTypeDifference(currentCategories, categoriesPostRemoval);
    }
    
    @Override
    public Set<CategoryType> getCategoryDifferenceForPaoTypesAdd(Set<PaoType> paoTypes, int configId) {
        Set<PaoType> supportedDeviceTypes = getSupportedTypesForConfiguration(configId);

        // Get the list of categories supported BEFORE the addition of the type.
        Set<DeviceCategories.Category> currentCategories = 
            paoDefinitionDao.getCategoriesForPaoTypes(supportedDeviceTypes);
        
        // Add the new type.
        supportedDeviceTypes.addAll(paoTypes);
        
        // Get the list of categories that will exist AFTER the addition of the type.
        Set<DeviceCategories.Category> categoriesPostAddition = 
                paoDefinitionDao.getCategoriesForPaoTypes(supportedDeviceTypes);
        
        // Return the difference between the sets. Pass them in opposite order, since the post-add set should be bigger
        return getCategoryTypeDifference(categoriesPostAddition, currentCategories);
    }
    
    private Set<CategoryType> getCategoryTypeDifference(Set<DeviceCategories.Category> baseSet, 
                                                        Set<DeviceCategories.Category> comparisonSet) {
        SetView<DeviceCategories.Category> difference = Sets.difference(baseSet, comparisonSet);
        
        Set<CategoryType> categoryTypes = new HashSet<>();
        for (DeviceCategories.Category category : difference) {
            categoryTypes.add(CategoryType.fromValue(category.getType().value()));
        }
        
        return categoryTypes;
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
    public List<String> getConfigurationNamesForCategory(int categoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DC.Name");
        sql.append("FROM DeviceConfiguration DC");
        sql.append("   JOIN DeviceConfigCategoryMap DCCM ON DC.DeviceConfigurationId = DCCM.DeviceConfigurationId");
        sql.append("WHERE DCCM.DeviceConfigCategoryId").eq(categoryId);

        List<String> configNames = jdbcTemplate.query(sql, RowMapper.STRING);
        
        return configNames;
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
    }
    
    @Override
    @Transactional
    public int saveCategory(DeviceConfigCategory category) {
        Integer categoryId = category.getCategoryId();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM DeviceConfigCategory");
        sql.append("WHERE LOWER(Name) = LOWER('" + category.getCategoryName() + "')");
        
        if (categoryId != null) {
            sql.append("   AND DeviceConfigCategoryId").neq(categoryId);
        }
        
        if (jdbcTemplate.queryForInt(sql) > 0) {
            throw new DuplicateException();
        }
        
        if (categoryId == null) {
            // We need to create the category before inserting the items.
            categoryId = nextValueHelper.getNextValue("DeviceConfigCategory");
            
            createCategory(categoryId, category);
            
            // Add the items.
            for (DeviceConfigCategoryItem item : category.getDeviceConfigurationItems()) {
                insertCategoryItem(item, categoryId);
            }
        } else {
            // Update the name and description.
            sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.update("DeviceConfigCategory");
            params.addValue("Name", category.getCategoryName());
            params.addValue("Description", category.getDescription());
            sql.append("WHERE DeviceConfigCategoryId").eq(category.getCategoryId());
            
            jdbcTemplate.update(sql);
            
            // Update the item values.
            for (DeviceConfigCategoryItem item : category.getDeviceConfigurationItems()) {
                updateCategoryItem(item);
            }
        }
        
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
        params.addValue("Description", category.getDescription());
        
        jdbcTemplate.update(sql);
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
        sql.append("SELECT DC.DeviceConfigurationId, DC.Name, DC.Description");
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
    public DNPConfiguration getDnpConfiguration(DeviceConfiguration config) {
        if (config == null) {
            return null;
        }
        
        List<DeviceConfigCategory> categories = config.getCategories();
        
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
            new DNPConfiguration(config.getConfigurationId(), config.getName(), config.getDescription());
        
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
        if (findConfigurationForDevice(device) != null) {
            params = sql.update("DeviceConfigurationDeviceMap");
            params.addValue("DeviceConfigurationId", configuration.getConfigurationId());
            sql.append("WHERE DeviceId").eq(device.getPaoIdentifier().getPaoId());
        } else {
            params = sql.insertInto("DeviceConfigurationDeviceMap");
            params.addValue("DeviceID", device.getPaoIdentifier().getPaoId());
            params.addValue("DeviceConfigurationId", configuration.getConfigurationId());
        }
        
        jdbcTemplate.update(sql);
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
    public boolean isConfigurationDeletable(int configId) {
        // A configuration is deletable if it isn't the default DNP configuration and has no assigned devices.
        return configId != DEFAULT_DNP_CONFIG_ID && getNumberOfDevicesForConfiguration(configId) == 0;
    }
}
