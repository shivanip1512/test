package com.cannontech.common.pao.dao.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoPersistenceDao;
import com.cannontech.common.pao.dao.PaoPersistenceTypeHelper;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.userpage.dao.UserPageDao;
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

public class PaoPersistenceDaoImpl implements PaoPersistenceDao {
    private static final Logger log = YukonLogManager.getLogger(PaoPersistenceDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoDao paoDao;
    @Autowired private UserPageDao userPageDao;

    /**
     * An insertion-order sorted DbTableMapping list for each supported PaoType. These lists are used to tell
     * the DAO how to access (in the correct order) the data in (or required for) a PAO.
     */
    private final Map<PaoType, List<CompletePaoMetaData>> paoTypeToTableMapping;

    /**
     * A set of supported PaoTypes that are represented by a class.
     */
    private final Map<Class<?>, Set<PaoType>> paoTypeByClass;

    private final Map<PaoType, Class<? extends CompleteYukonPao>> classByPaoType;

    @Autowired
    public PaoPersistenceDaoImpl(PaoPersistenceTypeHelper paoPersistenceTypeHelper) {
        paoTypeToTableMapping = paoPersistenceTypeHelper.getPaoTypeToTableMapping();
        paoTypeByClass = paoPersistenceTypeHelper.getPaoTypeByClass();
        classByPaoType = paoPersistenceTypeHelper.getClassByPaoType();
    }

    @Override
    public <T extends CompleteYukonPao> T retreivePao(YukonPao pao, final Class<T> klass) {
        try {
            final T newInstance = klass.newInstance();

            newInstance.setPaoIdentifier(pao.getPaoIdentifier());

            Set<PaoType> supportedTypes = paoTypeByClass.get(klass);
            if (!supportedTypes.contains(newInstance.getPaoType())) {
                // They're asking for a class that doesn't match this paoType, this is a problem!
                throw new RuntimeException("The class " + klass.getSimpleName() + " doesn't support" + " the PaoType "
                    + newInstance.getPaoType());
            }

            // Now, initialize other fields.
            final List<CompletePaoMetaData> dbTableMappings = paoTypeToTableMapping.get(newInstance.getPaoType());
            for (final CompletePaoMetaData paoMetaData : dbTableMappings) {
                if (paoMetaData != null) {
                    final PropertyDescriptor propertyDescriptor = paoMetaData.getPropertyDescriptor();
                    final Method partSetter = propertyDescriptor == null ? null : propertyDescriptor.getWriteMethod();

                    // build a query for the table
                    SqlStatementBuilder sql = new SqlStatementBuilder();
                    sql.append("select");
                    Iterable<String> fieldNames =
                        Iterables.transform(paoMetaData.getFields(), PaoFieldMetaData.TO_DB_COLUMN_NAME);
                    sql.append(StringUtils.join(fieldNames.iterator(), ", "));
                    sql.append("from").append(paoMetaData.getDbTableName());
                    sql.append("where").append(paoMetaData.getDbIdColumnName()).eq(newInstance.getPaObjectId());

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
                                            throw new RuntimeException(klass.getSimpleName()
                                                + " doesn't have a setter method " + "for its "
                                                + partClass.getSimpleName() + " member!");
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
                        partSetter.invoke(newInstance, (Object) null);
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
    public CompleteYukonPao retreivePao(YukonPao pao) {
        Class<? extends CompleteYukonPao> representingClass = classByPaoType.get(pao.getPaoIdentifier().getPaoType());
        return retreivePao(pao, representingClass);
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
    public void deletePao(YukonPao pao) {
        if (log.isDebugEnabled()) {
            log.debug("Deleting " + pao);
        }

        List<CompletePaoMetaData> mappings = paoTypeToTableMapping.get(pao.getPaoIdentifier().getPaoType());
        // The list we get from the map is in insertion order, we need to reverse it for deletion.
        List<CompletePaoMetaData> dbTableMappings = Lists.reverse(mappings);

        for (CompletePaoMetaData dbTableMapping : dbTableMappings) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("delete from").append(dbTableMapping.getDbTableName());
            sql.append("where").append(dbTableMapping.getDbIdColumnName()).eq(pao.getPaoIdentifier().getPaoId());

            jdbcTemplate.update(sql);
        }

        userPageDao.deletePagesForPao(pao);
    }

    @Override
    public boolean supports(YukonPao pao) {
        return paoTypeToTableMapping.keySet().contains(pao.getPaoIdentifier().getPaoType());
    }
    /**
     * Insert or update the given PAO into the database.
     * 
     * @param pao The PAO being inserted or updated.
     * @param isUpdate determines whether we're inserting or updating the PAO
     *        (true if we're updating, false if we're inserting)
     */
    private void insertOrUpdatePao(CompleteYukonPao pao, boolean isUpdate) {
        try {
            Set<PaoType> supportedTypes = paoTypeByClass.get(pao.getClass());
            if (supportedTypes == null || !supportedTypes.contains(pao.getPaoIdentifier().getPaoType())) {
                // They're asking for a class that doesn't match this paoType, this is a problem!
                throw new RuntimeException("The class " + pao.getClass().getSimpleName() + " doesn't support"
                    + " the PaoType " + pao.getPaoIdentifier().getPaoType());
            }

            List<CompletePaoMetaData> metaDataList = paoTypeToTableMapping.get(pao.getPaoType());

            for (CompletePaoMetaData paoMetaData : metaDataList) {
                // Build the SQL for the table
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink params =
                    isUpdate ? sql.update(paoMetaData.getDbTableName()) : sql.insertInto(paoMetaData.getDbTableName());

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
                        throw new RuntimeException(paoMetaData.getDbTableName() + " doesn't have a getter method!");
                    }

                    Object obj = getter.invoke(pao);
                    if (obj != null) {
                        objToWriteToDb = obj;
                    } else {
                        if (propertyDescriptor.getWriteMethod() == null) {
                            throw new RuntimeException(paoMetaData.getDbTableName()
                                + " does not have a setter method and its value is null!");
                        }
                        // We have no object to perform on since it was nullable and null.
                        continue;
                    }
                }

                for (PaoFieldMetaData dbFieldMapping : paoMetaData.getFields()) {
                    Method getter = dbFieldMapping.getPropertyDescriptor().getReadMethod();
                    Object obj = getter.invoke(objToWriteToDb);

                    if (obj instanceof Boolean || obj.getClass() == Boolean.TYPE) {
                        obj = YNBoolean.valueOf((Boolean) obj);
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
     * Check for nullable fields in the CompleteYukonPao object in order to handle the following two specific
     * cases pre-update on the object:
     * 
     * 1. The object contains a populated nullable field for which a corresponding entry does not exist
     * already in the database. In this case, we want to insert an entry manually into the database, since
     * calling update on the object will not result in data being entered into the database automatically.
     * 
     * 2. The object contains a nullable field which is null but has an entry in the database from a previous
     * update or insert. In this case, we want to delete the entry manually from the database.
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
                throw new RuntimeException(paoMetaData.getDbTableName() + " does not have a "
                    + "setter method and its value is null!");
            }

            // We need to "check" if a delete is necessary. Since blindly executing a delete doesn't hurt anything,
            // we can just execute the query without any worries.
            SqlStatementBuilder deleteSql = new SqlStatementBuilder();
            deleteSql.append("delete from").append(paoMetaData.getDbTableName());
            deleteSql.append("where").append(paoMetaData.getDbIdColumnName()).eq(pao.getPaObjectId());

            jdbcTemplate.update(deleteSql);
        } else {
            if (setter != null) {
                // This object has a setter, so it's nullable. We need to check to see if an insert is necessary.
                final AtomicBoolean rowExists = new AtomicBoolean(false);

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("select").append(paoMetaData.getDbIdColumnName());
                sql.append("from").append(paoMetaData.getDbTableName());
                sql.append("where").append(paoMetaData.getDbIdColumnName()).eq(pao.getPaObjectId());

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
                            field = YNBoolean.valueOf((Boolean) field);
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
