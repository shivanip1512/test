package com.cannontech.common.pao.service.impl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.cannontech.common.pao.pojo.CompleteYukonPaObject;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoPersistenceServiceImpl implements PaoPersistenceService {
    private static final Logger log = YukonLogManager.getLogger(PaoPersistenceServiceImpl.class);
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoCreationHelper paoCreationHelper;

    /**
     * This map stores an insertion-order sorted DbTableMapping list for each supported PaoType.
     * These lists are used to tell the DAO how to access (in the correct order) the data
     * in (or required for) a PAO.
     */
    private Map<PaoType, List<DbTableMapping>> paoTypeToTableMapping = Maps.newHashMap();
    
    /**
     * This map stores a set of supported PaoTypes that are represented by a class.
     */
    private Map<Class<?>, Set<PaoType>> classToPaoTypeMapping = Maps.newHashMap();
    
    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {
        Map<Class<?>, DbTableMapping> dbTableMappings = Maps.newHashMap();

        // Scan the package for classes to populate the dbTableMappings map.
        scanForYukonPaos(dbTableMappings);

        // Build the maps required for later use in the DAO methods.
        buildPaoTypeMaps(dbTableMappings);
        
        /*
         *  Remove all fields which are annotated as YukonPaoPart because we're dealing with them 
         *  as table objects now. We can't do this previously because we need them around to help 
         *  define all of the class structures.
         */
        for (DbTableMapping mapping : dbTableMappings.values()) {
            Iterator<DbFieldMapping> iter = mapping.getFields().iterator();
            while (iter.hasNext()) {
                DbFieldMapping next = iter.next();
                if (next.isPaoPart()) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * This method scans all classes in the com.cannontech.common.pao.pojo directory and attempts
     * to extract {@link DbTableMapping} information from the them.
     * @param dbTableMappings The map of class type to insertion-ordered list of DbTableMappings 
     * which will be populated after this method completes.
     */
    private void scanForYukonPaos(Map<Class<?>, DbTableMapping> dbTableMappings) throws IOException {
        // scan com.cannontech.common.pao.pojo package for YukonPao annotation
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory =
            new CachingMetadataReaderFactory(resourcePatternResolver);
        
        String resourcePath = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders("com.cannontech.common.pao.pojo"));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resourcePath + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                try {
                    Class<?> completePaoType = Class.forName(metadataReader.getClassMetadata().getClassName());
                    makeDbTableMapping(dbTableMappings, completePaoType);
                } catch (Throwable e) {
                    log.error("caught exception in init", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * This method uses the information in the dbTableMappings map to generate the two private 
     * class maps which will be used for the functionality of the DAO.
     * @param dbTableMappings A fully populated map of all classes in the 
     * com.cannontech.common.pao.pojo package to their {@link DbTableMapping} representation
     */
    private void buildPaoTypeMaps(Map<Class<?>, DbTableMapping> dbTableMappings) {
        /*
         * For each class in the map, we want to build a list of the DbTableMapping objects
         * that are required to build an instance of that object.
         */
        for (Class<?> klass : dbTableMappings.keySet()) {
            YukonPao paoAnnotation = klass.getAnnotation(YukonPao.class);
            
            /*
             * This information is only valid if we have a YukonPao annotation. 
             */
            if (paoAnnotation != null) {
                List<DbTableMapping> classTableMappings = Lists.newArrayList();
                
                /*
                 * Iterate over klass's hierarchy, adding all inheritance and composition
                 * classes to the classTableMappings list.
                 */
                Class<?> currentClass = klass;
                while (currentClass != null) {
                    DbTableMapping mapping = dbTableMappings.get(currentClass);
                    if (mapping != null) {
                        /*
                         * Look to see if currentClass contains any YukonPaoPart members. If it
                         * does, create a new DbTableMapping for that Part, including the getter 
                         * for that Part, and add it to the list of mappings klass requires. This 
                         * must be done before we add mapping to the list, since this list is 
                         * created deletion order as we iterate UPWARDS over the hierarchy.
                         */
                        for (DbFieldMapping field : mapping.getFields()) {
                            PropertyDescriptor propertyDescriptor = field.getPropertyDescriptor();
                            Class<?> returnType = propertyDescriptor.getReadMethod().getReturnType();
                            
                            /*
                             * We already have a record of all classes in the package, so if the
                             * dbTableMappings map contains the return type of this annotated
                             * member, we know it's a YukonPaoPart and needs to be stored.
                             */
                            DbTableMapping partMapping = dbTableMappings.get(returnType);
                            if (partMapping != null) {
                                classTableMappings.add(new DbTableMapping(partMapping,
                                                                          propertyDescriptor.getReadMethod()));
                                field.setPaoPart(true);
                            }
                        }
                        
                        /*
                         * Only add currentClass to klass' list if it's a table-backed class. For 
                         * instance, we wouldn't want to add Ccu721 to the list because it doesn't 
                         * actually represent a place in the database where information is entered 
                         * to create a Ccu721 device.
                         */
                        if (currentClass.getAnnotation(YukonPao.class).tableBacked()) {
                            classTableMappings.add(mapping);
                        }
                    }
                    
                    // Grab the next class up in the hierarchy.
                    currentClass = currentClass.getSuperclass();
                }

                /*
                 *  We want to toss these into the PaoType maps in INSERTION order. This 
                 *  check is only valid on YukonPao annotated classes though.
                 */
                classTableMappings = Lists.reverse(classTableMappings);
                if (klass.getAnnotation(YukonPao.class) != null) {
                    Validate.isTrue(classTableMappings.get(0).getKlass() == CompleteYukonPaObject.class, 
                                    "Init error: " + klass + "'s structure is invalid!");
                }

                /*
                 * Map each of the PaoTypes in the annotation to the insertion-ordered list 
                 * of DbTableMapping objects that define it.
                 */
                Set<PaoType> supportedPaoTypes = Sets.newHashSet(paoAnnotation.paoTypes());
                for (PaoType paoType : supportedPaoTypes) {
                    paoTypeToTableMapping.put(paoType, classTableMappings);
                }

                /*
                 * Map klass itself to the PaoTypes it represents so we don't have to 
                 * re-acquire that data every time someone calls one of our methods.
                 */
                classToPaoTypeMapping.put(klass, supportedPaoTypes);
            }
        }
    }

    /**
     * Given a Class, this method extracts the database information and information about the 
     * class' contents and constructs a {@link DbTableMapping} for the class, then stores it in 
     * the dbTableMappings map.
     * @param dbTableMappings the Class-to-DbTableMapping map into which completePaoType's 
     * DbTableMapping information will be stored.
     * @param completePaoType the class whose DbTableMapping information will be created.
     */
    private void makeDbTableMapping(Map<Class<?>, DbTableMapping> dbTableMappings, Class<?> completePaoType) {
        YukonPao paoAnnotation = completePaoType.getAnnotation(YukonPao.class);
        YukonPaoPart partAnnotation = completePaoType.getAnnotation(YukonPaoPart.class);

        /*
         * Do a little error checking up front so that we know this Class is valid to be used
         * in the service.
         */
        if (paoAnnotation == null && partAnnotation == null) {
            return; // This class isn't annotated: There's nothing to do.
        } 
        if (paoAnnotation != null && partAnnotation != null) {
            throw new RuntimeException("Cannot use both YukonPao and YukonPaoPart on the same class");
        }        
        if (!defaultConstructorExistsForClass(completePaoType)) {
            throw new RuntimeException(completePaoType.getName() + " doesn't have a default constructor.");
        }

        DbTableMapping dbTableMapping = new DbTableMapping();
        dbTableMapping.setKlass(completePaoType);
        
        if (paoAnnotation != null) {
            if (paoAnnotation.tableBacked()) {
                dbTableMapping.setDbTableName(paoAnnotation.tableName());
                dbTableMapping.setDbIdColumnName(paoAnnotation.idColumnName());
            }
        } else {
            dbTableMapping.setDbTableName(partAnnotation.tableName());
            dbTableMapping.setDbIdColumnName(partAnnotation.idColumnName());
        }
        
        List<DbFieldMapping> fields = Lists.newArrayList();
        List<String> propertiesInserted = Lists.newArrayList();
        Method[] methods = completePaoType.getDeclaredMethods();
        for (Method method : methods) {
            YukonPaoField fieldAnnotation = method.getAnnotation(YukonPaoField.class);
            if (fieldAnnotation != null) {
                PropertyDescriptor propertyDescriptor = BeanUtils.findPropertyForMethod(method);
                if (propertiesInserted.contains(propertyDescriptor.getName())) {
                    throw new RuntimeException("More than one method for " + completePaoType.getSimpleName() + 
                                               "'s " + propertyDescriptor.getName() + " property were annotated.");
                }
                DbFieldMapping dbFieldMapping = new DbFieldMapping();
                dbFieldMapping.setPropertyDescriptor(propertyDescriptor);
                if (YukonPao.AUTO_DETECT.equals(fieldAnnotation.columnName())) {
                    dbFieldMapping.setDbColumnName(propertyDescriptor.getName());
                } else {
                    dbFieldMapping.setDbColumnName(fieldAnnotation.columnName());
                }
                fields.add(dbFieldMapping);
                propertiesInserted.add(propertyDescriptor.getName());
            }
        }
        dbTableMapping.setFields(fields);

        dbTableMappings.put(completePaoType, dbTableMapping);
    }

    /**
     * This method checks to see if a class has a default constructor.
     * @param klass
     * @return true if klass contains a default constructor, false otherwise.
     */
    private boolean defaultConstructorExistsForClass(Class<?> klass) {
        boolean defaultConstructorExists = false;
        
        Constructor<?>[] declaredConstructors = klass.getDeclaredConstructors();
        for (Constructor<?> constructor : declaredConstructors) {
            if (constructor.getGenericParameterTypes().length == 0) {
                defaultConstructorExists = true;
                break;
            }
        }
        
        return defaultConstructorExists;
    }

    @Override
    public <T extends CompleteYukonPaObject> T retreivePao(PaoIdentifier paoIdentifier, Class<T> klass) {
        try {
            final T newInstance = klass.newInstance();
            newInstance.setPaoIdentifier(paoIdentifier);
            
            Set<PaoType> supportedTypes = classToPaoTypeMapping.get(klass);
            if (!supportedTypes.contains(paoIdentifier.getPaoType())) {
                // They're asking for a class that doesn't match this paoType, this is a problem!
                throw new RuntimeException("The class " + klass.getSimpleName() + " doesn't support" +
                                           " the PaoType " + paoIdentifier.getPaoType());
            }

            // Now, initialize other fields.
            final List<DbTableMapping> dbTableMappings = paoTypeToTableMapping.get(paoIdentifier.getPaoType());
            for (final DbTableMapping dbTableMapping : dbTableMappings) {
                if (dbTableMapping != null) {
                    
                    Method partGetter = dbTableMapping.getPartGetter();
                    final Object objToWriteToDb = (partGetter != null) ? partGetter.invoke(newInstance) : newInstance;
                    
                    // build a query for the table
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("SELECT");
                    Iterable<String> fieldNames =
                        Iterables.transform(dbTableMapping.getFields(), DbFieldMapping.colNameOfField);
                    sql.append(StringUtils.join(fieldNames.iterator(), ", "));
                    sql.append("FROM").append(dbTableMapping.getDbTableName());
                    sql.append("WHERE").append(dbTableMapping.getDbIdColumnName()).eq(paoIdentifier.getPaoId());
                    YukonRowCallbackHandler rch = new YukonRowCallbackHandler() {
                        @Override
                        public void processRow(YukonResultSet rs) throws SQLException {
                            for (DbFieldMapping fieldMapping : dbTableMapping.getFields()) {
                                fieldMapping.updateField(objToWriteToDb, rs);
                            }
                        }
                    };
                    
                    jdbcTemplate.query(sql, rch);
                }
            }
            
            return newInstance;
        } catch (InstantiationException e) {
            log.error("caught exception in retreivePao", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error("caught exception in retreivePao", e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.error("caught exception in retrievePao", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void createPao(CompleteYukonPaObject pao, PaoType paoType) {
        createNewPao(pao, paoType);
        
        // db change message happens in the processDbChange call below.
        paoCreationHelper.addDefaultPointsToPao(pao.getPaoIdentifier());
            
        // db change msg.  Process Device dbChange AFTER pao AND points have been inserted into DB.
        paoCreationHelper.processDbChange(pao.getPaoIdentifier(), DbChangeType.ADD);
    }
    
    @Override
    @Transactional
    public void createPaoWithCustomPoints(CompleteYukonPaObject pao, PaoType paoType,
                                             List<PointBase> points) {
        createNewPao(pao, paoType);
        
        // Write the points we need to copy to the DB.
        paoCreationHelper.applyPoints(pao.getPaoIdentifier(), points);
        
        // Send DB change message
        paoCreationHelper.processDbChange(pao.getPaoIdentifier(), DbChangeType.ADD);
    }
    
    /**
     * This method handles grabbing the new paoId for the PAO being created and then
     * passes all the information to the insertOrUpdatePao method.
     * @param pao The PAO being created
     * @param paoType the PaoType for the new PAO.
     */
    private void createNewPao(CompleteYukonPaObject pao, PaoType paoType) {
     // Get the next paoId to insert this into the db with.
        int paoId = paoDao.getNextPaoId();
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoType);
        pao.setPaoIdentifier(paoIdentifier);
        
        try {
            insertOrUpdatePao(pao, false);
        } catch (Exception e) {
            log.error("caught exception in createNewPao", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    @Transactional
    public void updatePao(CompleteYukonPaObject pao) {
        insertOrUpdatePao(pao, true);
        
        paoCreationHelper.processDbChange(pao.getPaoIdentifier(), DbChangeType.UPDATE);
    }
    
    @Override
    @Transactional
    public void deletePao(PaoIdentifier paoIdentifier) {
        
        try {
            paoCreationHelper.deletePointsForPao(paoIdentifier.getPaoId());
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        final List<DbTableMapping> mappings = paoTypeToTableMapping.get(paoIdentifier.getPaoType());
        // The list we get from the map is in insertion order, we need to reverse it for deletion.
        final List<DbTableMapping> dbTableMappings = Lists.reverse(mappings);
        
        for (DbTableMapping dbTableMapping : dbTableMappings) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM").append(dbTableMapping.getDbTableName());
            sql.append("WHERE").append(dbTableMapping.getDbIdColumnName()).eq(paoIdentifier.getPaoId());
            
             jdbcTemplate.update(sql);
        }
        
        // Send DB change message
        paoCreationHelper.processDbChange(paoIdentifier, DbChangeType.DELETE);
    }

    /**
     * This method handles the SQL for either the inserting or updating of a PAO.
     * @param pao The PAO being inserted or updated.
     * @param isUpdate determines whether we're inserting or updating the PAO 
     * (true if we're updating, false if we're inserting)
     */
    private void insertOrUpdatePao(CompleteYukonPaObject pao, boolean isUpdate) {
        try {
            Set<PaoType> supportedTypes = classToPaoTypeMapping.get(pao.getClass());
            if (supportedTypes == null || !supportedTypes.contains(pao.getPaoIdentifier().getPaoType())) {
                // They're asking for a class that doesn't match this paoType, this is a problem!
                throw new RuntimeException("The class " + pao.getClass().getSimpleName() + " doesn't support" +
                                           " the PaoType " + pao.getPaoIdentifier().getPaoType());
            }

            List<DbTableMapping> dbTableMappings = paoTypeToTableMapping.get(pao.getPaoType());

            for (DbTableMapping dbTableMapping : dbTableMappings) {
                // build the sql for the table
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink params = isUpdate ? sql.update(dbTableMapping.getDbTableName()) :
                                                     sql.insertInto(dbTableMapping.getDbTableName());

                if (!isUpdate) {
                    params.addValue(dbTableMapping.getDbIdColumnName(), pao.getPaObjectId());
                }

                Method partGetter = dbTableMapping.getPartGetter();
                Object objToWriteToDb = pao;
                if (partGetter != null) {
                    objToWriteToDb = partGetter.invoke(pao);
                }
                
                for (DbFieldMapping dbFieldMapping : dbTableMapping.getFields()) {
                    Method getter = dbFieldMapping.getPropertyDescriptor().getReadMethod();
                    Object obj = getter.invoke(objToWriteToDb);
                    if (obj instanceof Boolean || obj.getClass() == Boolean.TYPE) {
                        obj = YNBoolean.valueOf((Boolean)obj);
                    }
                    params.addValue(dbFieldMapping.getDbColumnName(), obj);
                }

                if (isUpdate) {
                    sql.append("WHERE").append(dbTableMapping.getDbIdColumnName()).eq(pao.getPaObjectId());
                }

                jdbcTemplate.update(sql);
            }
        } catch (IllegalArgumentException e) {
            log.error("caught exception in createPao", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.error("caught exception in createPao", e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.error("caught exception in createPao", e);
            throw new RuntimeException(e);
        }
    }
}
