package com.cannontech.common.pao.dao.impl;

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
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.cannontech.common.pao.dao.PaoPersistenceDao;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoPersistenceDaoImpl implements PaoPersistenceDao {
    private static final Logger log = YukonLogManager.getLogger(PaoPersistenceDaoImpl.class);
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;

    /**
     * This map stores an insertion-order sorted DbTableMapping list for each supported PaoType.
     * These lists are used to tell the DAO how to access (in the correct order) the data
     * in (or required for) a PAO.
     */
    private final Map<PaoType, List<CompletePaoMetaData>> paoTypeToTableMapping = Maps.newHashMap();
    
    /**
     * This map stores a set of supported PaoTypes that are represented by a class.
     */
    private final Map<Class<?>, Set<PaoType>> classToPaoTypeMapping = Maps.newHashMap();
    
    @PostConstruct
    public void init() throws IOException, ClassNotFoundException {
        // Scan the package for classes to populate the dbTableMappings map.
        Map<Class<?>, CompletePaoMetaData> metaDataMappings = scanForYukonPaos();

        // Build the maps required for later use in the DAO methods.
        buildPaoTypeMaps(metaDataMappings);
        
        /*
         *  Remove all fields which are annotated as YukonPaoPart because we're dealing with them 
         *  as table objects now. We can't do this previously because we need them around to help 
         *  define all of the class structures.
         */
        for (CompletePaoMetaData mapping : metaDataMappings.values()) {
            Iterator<PaoFieldMetaData> iter = mapping.getFields().iterator();
            while (iter.hasNext()) {
                PaoFieldMetaData next = iter.next();
                if (next.isPaoPart()) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * This method scans all classes in the com.cannontech.common.pao.model package and attempts
     * to extract {@link CompletePaoMetaData} information from the them.
     * @param metaDataMappings The map of class type to insertion-ordered list of DbTableMappings 
     * which will be populated after this method completes.
     */
    private Map<Class<?>, CompletePaoMetaData> scanForYukonPaos() throws IOException {
        Map<Class<?>, CompletePaoMetaData> metaDataMappings = Maps.newHashMap();
        
        // scan com.cannontech.common.pao.model package for YukonPao annotation
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory =
            new CachingMetadataReaderFactory(resourcePatternResolver);
        
        String resourcePath = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders("com.cannontech.common.pao.model"));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resourcePath + "/" + "**/*.class";
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                try {
                    Class<?> completePaoType = Class.forName(metadataReader.getClassMetadata().getClassName());
                    makeDbTableMapping(metaDataMappings, completePaoType);
                } catch (Throwable e) {
                    log.error("caught exception in init", e);
                    throw new RuntimeException(e);
                }
            }
        }
        
        return metaDataMappings;
    }

    /**
     * This method uses the information in the dbTableMappings map to generate the two private 
     * class maps which will be used for the functionality of the DAO.
     * @param dbTableMappings A fully populated map of all classes in the 
     * com.cannontech.common.pao.pojo package to their {@link CompletePaoMetaData} representation
     */
    private void buildPaoTypeMaps(Map<Class<?>, CompletePaoMetaData> dbTableMappings) {
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
                List<CompletePaoMetaData> classTableMappings = Lists.newArrayList();
                
                /*
                 * Iterate over klass's hierarchy, adding all inheritance and composition
                 * classes to the classTableMappings list.
                 */
                Class<?> currentClass = klass;
                while (currentClass != null) {
                    CompletePaoMetaData mapping = dbTableMappings.get(currentClass);
                    if (mapping != null) {
                        /*
                         * Look to see if currentClass contains any YukonPaoPart members. If it
                         * does, create a new DbTableMapping for that Part, including the getter 
                         * for that Part, and add it to the list of mappings klass requires. This 
                         * must be done before we add mapping to the list, since this list is 
                         * created deletion order as we iterate UPWARDS over the hierarchy.
                         */
                        for (PaoFieldMetaData field : mapping.getFields()) {
                            PropertyDescriptor propertyDescriptor = field.getPropertyDescriptor();
                            Class<?> returnType = propertyDescriptor.getReadMethod().getReturnType();
                            
                            /*
                             * We already have a record of all classes in the package, so if the
                             * dbTableMappings map contains the return type of this annotated
                             * member, we know it's a YukonPaoPart and needs to be stored.
                             */
                            CompletePaoMetaData partMapping = dbTableMappings.get(returnType);
                            if (partMapping != null) {
                                classTableMappings.add(new CompletePaoMetaData(partMapping,
                                                                               propertyDescriptor));
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
                    Validate.isTrue(classTableMappings.get(0).getKlass() == CompleteYukonPao.class, 
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
     * class' contents and constructs a {@link CompletePaoMetaData} for the class, then stores it in 
     * the dbTableMappings map.
     * @param dbTableMappings the Class-to-DbTableMapping map into which completePaoType's 
     * DbTableMapping information will be stored.
     * @param completeClass the class whose DbTableMapping information will be created.
     */
    private void makeDbTableMapping(Map<Class<?>, CompletePaoMetaData> dbTableMappings, Class<?> completeClass) {
        YukonPao paoAnnotation = completeClass.getAnnotation(YukonPao.class);
        YukonPaoPart partAnnotation = completeClass.getAnnotation(YukonPaoPart.class);

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
        if (!defaultConstructorExistsForClass(completeClass)) {
            throw new RuntimeException(completeClass.getName() + " doesn't have a default constructor.");
        }

        CompletePaoMetaData paoMetaData = new CompletePaoMetaData();
        paoMetaData.setKlass(completeClass);
        
        // This will be used to detect the table name automatically if one wasn't provided.
        String tableName = completeClass.getSimpleName().replaceAll("Complete", "");
        
        if (paoAnnotation != null) {
            if (paoAnnotation.tableBacked()) {
                if(!YukonPao.AUTO_DETECT.equals(paoAnnotation.tableName())) {
                    tableName = paoAnnotation.tableName();
                }
                paoMetaData.setDbIdColumnName(paoAnnotation.idColumnName());
            }
        } else {
            if (!YukonPao.AUTO_DETECT.equals(partAnnotation.tableName())) {
                tableName = partAnnotation.tableName();
            }
            paoMetaData.setDbIdColumnName(partAnnotation.idColumnName());
        }
        
        paoMetaData.setDbTableName(tableName);
        
        List<PaoFieldMetaData> fields = Lists.newArrayList();
        List<String> propertiesInserted = Lists.newArrayList();
        Method[] methods = completeClass.getDeclaredMethods();
        for (Method method : methods) {
            YukonPaoField fieldAnnotation = method.getAnnotation(YukonPaoField.class);
            if (fieldAnnotation != null) {
                PropertyDescriptor propertyDescriptor = BeanUtils.findPropertyForMethod(method);
                if (propertiesInserted.contains(propertyDescriptor.getName())) {
                    throw new RuntimeException("More than one method for " + completeClass.getSimpleName() + 
                                               "'s " + propertyDescriptor.getName() + " property were annotated.");
                }
                PaoFieldMetaData paoFieldMetaData = new PaoFieldMetaData();
                paoFieldMetaData.setPropertyDescriptor(propertyDescriptor);
                if (YukonPao.AUTO_DETECT.equals(fieldAnnotation.columnName())) {
                    paoFieldMetaData.setDbColumnName(propertyDescriptor.getName());
                } else {
                    paoFieldMetaData.setDbColumnName(fieldAnnotation.columnName());
                }
                fields.add(paoFieldMetaData);
                propertiesInserted.add(propertyDescriptor.getName());
            }
        }
        paoMetaData.setFields(fields);

        dbTableMappings.put(completeClass, paoMetaData);
    }

    /**
     * This method checks to see if a class has a default constructor.
     * @param klass
     * @return true if klass contains a default constructor, false otherwise.
     */
    private boolean defaultConstructorExistsForClass(Class<?> klass) {
        Constructor<?>[] declaredConstructors = klass.getDeclaredConstructors();
        for (Constructor<?> constructor : declaredConstructors) {
            if (constructor.getGenericParameterTypes().length == 0) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public <T extends CompleteYukonPao> T retreivePao(PaoIdentifier paoIdentifier, final Class<T> klass) {
        try {
            final T newInstance = klass.newInstance();
            
            newInstance.setPaoIdentifier(paoIdentifier);
            
            Set<PaoType> supportedTypes = classToPaoTypeMapping.get(klass);
            if (!supportedTypes.contains(newInstance.getPaoType())) {
                // They're asking for a class that doesn't match this paoType, this is a problem!
                throw new RuntimeException("The class " + klass.getSimpleName() + " doesn't support" +
                                           " the PaoType " + newInstance.getPaoType());
            }

            // Now, initialize other fields.
            final List<CompletePaoMetaData> dbTableMappings = paoTypeToTableMapping.get(newInstance.getPaoType());
            for (final CompletePaoMetaData paoMetaData : dbTableMappings) {
                if (paoMetaData != null) {
                    final PropertyDescriptor propertyDescriptor = paoMetaData.getPropertyDescriptor();
                    final Method partSetter = propertyDescriptor == null ? null : propertyDescriptor.getWriteMethod();
                    
                    // build a query for the table
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("SELECT");
                    Iterable<String> fieldNames =
                        Iterables.transform(paoMetaData.getFields(), PaoFieldMetaData.colNameOfField);
                    sql.append(StringUtils.join(fieldNames.iterator(), ", "));
                    sql.append("FROM").append(paoMetaData.getDbTableName());
                    sql.append("WHERE").append(paoMetaData.getDbIdColumnName()).eq(newInstance.getPaObjectId());
                    
                    final AtomicBoolean rowExists = new AtomicBoolean(false);
                    
                    jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                        @Override
                        public void processRow(YukonResultSet rs) throws SQLException {
                            Object objFromDb = newInstance;
                            Method partGetter = propertyDescriptor == null ? null : propertyDescriptor.getReadMethod();
                            if (partGetter != null) {
                                // Get the part from newInstance.
                                Object partInstance;
                                try {
                                    partInstance = partGetter.invoke(newInstance);

                                    if (partInstance == null) {
                                        Class<?> partClass = propertyDescriptor.getPropertyType();
                                        
                                        // newInstance's getter returned null, we need to create an instance
                                        // to store the data if a row exists in the db for this table.
                                        if (partSetter == null) {
                                            // How can we give newInstance the data without a setter?
                                            throw new RuntimeException(klass.getSimpleName() + " doesn't have a setter method " +
                                                                       "for its " + partClass.getSimpleName() + " member!");
                                        }
                                        partInstance = partClass.newInstance();
                                        partSetter.invoke(newInstance, partInstance);
                                    }

                                    objFromDb = partInstance;
                                } catch (IllegalArgumentException e) {
                                    log.warn("caught exception in processRow", e);
                                } catch (IllegalAccessException e) {
                                    log.warn("caught exception in processRow", e);
                                } catch (InvocationTargetException e) {
                                    log.warn("caught exception in processRow", e);
                                } catch (InstantiationException e) {
                                    log.warn("caught exception in processRow", e);
                                }
                            }
                            
                            for (PaoFieldMetaData paoFieldMetaData : paoMetaData.getFields()) {
                                paoFieldMetaData.updateField(objFromDb, rs);
                            }
                            rowExists.set(true);
                        }
                    });
                    
                    if (!rowExists.get() && (partSetter != null)) {
                        partSetter.invoke(newInstance, (Object)null);
                    }
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
    public void createPao(CompleteYukonPao pao, PaoType paoType) {
        // Get the next paoId to insert this into the DB with.
        int paoId = paoDao.getNextPaoId();
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoType);
        pao.setPaoIdentifier(paoIdentifier);
        
        // Debug log is more useful if we have our PaoIdentifier non-null.
        if (log.isDebugEnabled()) {
            log.debug("Creating " + pao);
        }

        insertOrUpdatePao(pao, false);
    }
    
    @Override
    @Transactional
    public void updatePao(CompleteYukonPao pao) {
        if (log.isDebugEnabled()) {
            log.debug("Updating " + pao);
        }
        
        insertOrUpdatePao(pao, true);
    }
    
    @Override
    @Transactional
    public void deletePao(PaoIdentifier paoIdentifier) {
        if (log.isDebugEnabled()) {
            log.debug("Deleting " + paoIdentifier);
        }
        
        List<CompletePaoMetaData> mappings = paoTypeToTableMapping.get(paoIdentifier.getPaoType());
        // The list we get from the map is in insertion order, we need to reverse it for deletion.
        List<CompletePaoMetaData> dbTableMappings = Lists.reverse(mappings);
        
        for (CompletePaoMetaData dbTableMapping : dbTableMappings) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM").append(dbTableMapping.getDbTableName());
            sql.append("WHERE").append(dbTableMapping.getDbIdColumnName()).eq(paoIdentifier.getPaoId());
            
             jdbcTemplate.update(sql);
        }
    }

    /**
     * This method handles the SQL for either the inserting or updating of a PAO.
     * @param pao The PAO being inserted or updated.
     * @param isUpdate determines whether we're inserting or updating the PAO 
     * (true if we're updating, false if we're inserting)
     */
    private void insertOrUpdatePao(CompleteYukonPao pao, boolean isUpdate) {
        try {
            Set<PaoType> supportedTypes = classToPaoTypeMapping.get(pao.getClass());
            if (supportedTypes == null || !supportedTypes.contains(pao.getPaoIdentifier().getPaoType())) {
                // They're asking for a class that doesn't match this paoType, this is a problem!
                throw new RuntimeException("The class " + pao.getClass().getSimpleName() + " doesn't support" +
                                           " the PaoType " + pao.getPaoIdentifier().getPaoType());
            }

            List<CompletePaoMetaData> metaDataList = paoTypeToTableMapping.get(pao.getPaoType());

            for (CompletePaoMetaData paoMetaData : metaDataList) {
                // Build the SQL for the table
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink params = isUpdate ? sql.update(paoMetaData.getDbTableName()) :
                                                     sql.insertInto(paoMetaData.getDbTableName());

                if (!isUpdate) {
                    params.addValue(paoMetaData.getDbIdColumnName(), pao.getPaObjectId());
                }
                
                Object objToWriteToDb = pao;
                
                if (isUpdate) {
                    processNullableField(pao, paoMetaData);
                }
                
                PropertyDescriptor propertyDescriptor = paoMetaData.getPropertyDescriptor();
                if (propertyDescriptor != null) {
                    Method getter = propertyDescriptor.getReadMethod();
                    if (getter == null) {
                        throw new RuntimeException(paoMetaData.getDbTableName() + " doesn't have " +
                                                   "a getter method!");
                    }
                    
                    Object obj = getter.invoke(pao);
                    if (obj != null) {
                        objToWriteToDb = obj;
                    } else {
                        if (propertyDescriptor.getWriteMethod() == null) {
                            throw new RuntimeException(paoMetaData.getDbTableName() + " does " +
                            		"not have a setter method and its value is null!");
                        }
                        // We have no object to perform on since it was nullable and null.
                        continue;
                    }
                }
                
                for (PaoFieldMetaData dbFieldMapping : paoMetaData.getFields()) {
                    Method getter = dbFieldMapping.getPropertyDescriptor().getReadMethod();
                    Object obj = getter.invoke(objToWriteToDb);
                    
                    if (obj instanceof Boolean || obj.getClass() == Boolean.TYPE) {
                        obj = YNBoolean.valueOf((Boolean)obj);
                    } else if (obj instanceof String) {
                        obj = SqlUtils.convertStringToDbValue((String) obj);
                    }
                    params.addValue(dbFieldMapping.getDbColumnName(), obj);
                }

                if (isUpdate) {
                    sql.append("WHERE").append(paoMetaData.getDbIdColumnName()).eq(pao.getPaObjectId());
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
    
    /**
     * This method is used to check for nullable fields in the CompleteYukonPao object in order
     * to handle the following two specific cases pre-update on the object:
     * 
     *  1. The object contains a populated nullable field for which a corresponding entry does
     *     not exist already in the database. In this case, we want to insert an entry manually
     *     into the database, since calling update on the object will not result in data being
     *     entered into the database automatically.
     *  2. The object contains a nullable field which is null but has an entry in the database
     *     from a previous update or insert. In this case, we want to delete the entry manually
     *     from the database.
     */
    private void processNullableField(CompleteYukonPao pao, CompletePaoMetaData paoMetaData) 
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor propertyDescriptor = paoMetaData.getPropertyDescriptor();
        
        if (propertyDescriptor == null) {
            return;
        }
    
        Method getter = propertyDescriptor.getReadMethod();
        Method setter = propertyDescriptor.getWriteMethod();
        
        Object obj = (getter == null) ? null : getter.invoke(pao);
        
        if (obj == null) {
            // This is PROBABLY a nullable object; it better have a setter if it is!
            if (setter == null) {
                throw new RuntimeException(paoMetaData.getDbTableName() + " does not have a " +
                                           "setter method and its value is null!");
            }
            
            /* 
             * We need to "check" if a delete is necessary. Since blindly executing
             * a delete doesn't hurt anything, we can just execute the query without
             * any worries.
             */
            SqlStatementBuilder deleteSql = new SqlStatementBuilder();
            deleteSql.append("DELETE FROM").append(paoMetaData.getDbTableName());
            deleteSql.append("WHERE").append(paoMetaData.getDbIdColumnName()).eq(pao.getPaObjectId());
            
            jdbcTemplate.update(deleteSql);
        } else {
            if (setter != null) {
                /*
                 * This object has a setter, so it's nullable. We need to check to see if
                 * an insert is necessary.
                 */                    
                final AtomicBoolean rowExists = new AtomicBoolean(false);
                 
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT").append(paoMetaData.getDbIdColumnName());
                sql.append("FROM").append(paoMetaData.getDbTableName());
                sql.append("WHERE").append(paoMetaData.getDbIdColumnName()).eq(pao.getPaObjectId());
                 
                jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                    @Override
                    public void processRow(YukonResultSet rs) throws SQLException {
                        rowExists.set(true);
                    } 
                });
                 
                if (!rowExists.get()) {
                    // No entry, time to insert!
                    SqlStatementBuilder insertSql = new SqlStatementBuilder();
                    SqlParameterSink params = insertSql.insertInto(paoMetaData.getDbTableName());
                    params.addValue(paoMetaData.getDbIdColumnName(), pao.getPaObjectId());
                     
                    for (PaoFieldMetaData dbFieldMapping : paoMetaData.getFields()) {
                        Method fieldGetter = dbFieldMapping.getPropertyDescriptor().getReadMethod();
                        Object field = fieldGetter.invoke(obj);
                         
                        if (field instanceof Boolean || field.getClass() == Boolean.TYPE) {
                            field = YNBoolean.valueOf((Boolean)field);
                        } else if (field instanceof String) {
                            field = SqlUtils.convertStringToDbValue((String) field);
                        }
                        params.addValue(dbFieldMapping.getDbColumnName(), field);
                    }
                     
                    jdbcTemplate.update(insertSql);
                }
            }
        }
    }
}
