package com.cannontech.common.bulk.collection.device.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.device.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.dao.DeviceCollectionPersistenceDao;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionPersistenceType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceListBasedCollectionPersistable;
import com.cannontech.common.bulk.collection.device.persistable.FieldBasedCollectionPersistable;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Maps;

public class DeviceCollectionPersistenceDaoImpl implements DeviceCollectionPersistenceDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    @Transactional
    public int savePersistable(DeviceCollectionPersistable persistable) {
        //Insert base device collection
        int collectionId = insertCollectionBase(persistable);
        
        if(persistable.getPersistenceType() == DeviceCollectionPersistenceType.DEVICE_LIST) {
            //Insert deviceIds
            DeviceListBasedCollectionPersistable listPersistable = (DeviceListBasedCollectionPersistable) persistable;
            for(int deviceId : listPersistable.getDeviceIds()) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink sink = sql.insertInto("DeviceCollectionById");
                sink.addValue("CollectionId", collectionId);
                sink.addValue("DeviceId", deviceId);
                yukonJdbcTemplate.update(sql);
            }
        } else if(persistable.getPersistenceType() == DeviceCollectionPersistenceType.FIELD) {
            //Insert key-value pairs
            FieldBasedCollectionPersistable fieldPersistable = (FieldBasedCollectionPersistable) persistable;
            for(Map.Entry<String, String> entry : fieldPersistable.getValueMap().entrySet()) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink sink = sql.insertInto("DeviceCollectionByField");
                sink.addValue("CollectionId", collectionId);
                sink.addValue("FieldName", entry.getKey());
                sink.addValue("FieldValue", entry.getValue());
                yukonJdbcTemplate.update(sql);
            }
        } else {
            throw new IllegalArgumentException("Unsupported device collection persistable type: "
                                               + persistable.getPersistenceType());
        }
        return collectionId;
    }
    
    @Override
    public DeviceCollectionPersistable loadPersistable(int collectionId) {
        //load type of persistable from db
        DeviceCollectionPersistenceType persistenceType = queryPersistenceType(collectionId);
        DeviceCollectionType collectionType = queryCollectionType(collectionId);
        //query for persistable stuff
        if(persistenceType == DeviceCollectionPersistenceType.DEVICE_LIST) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DeviceId");
            sql.append("FROM DeviceCollectionById");
            sql.append("WHERE CollectionId").eq(collectionId);
            List<Integer> deviceIds = yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
            return new DeviceListBasedCollectionPersistable(collectionType, deviceIds);
        } else if(persistenceType == DeviceCollectionPersistenceType.FIELD) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT FieldName, FieldValue");
            sql.append("FROM DeviceCollectionByField");
            sql.append("WHERE CollectionId").eq(collectionId);
            final Map<String, String> valueMap = Maps.newHashMap();
            yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                    valueMap.put(rs.getString("FieldName"), rs.getString("FieldValue"));
                }
            });
            return new FieldBasedCollectionPersistable(collectionType, valueMap);
        } else {
            throw new IllegalStateException("Loaded an unsupported type of persistent device collection: " + persistenceType);
        }
    }
    
    @Override
    public boolean deletePersistable(int collectionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceCollection");
        sql.append("WHERE CollectionId").eq(collectionId);
        return yukonJdbcTemplate.update(sql) == 1;
    }
    
    /**
     * Inserts a new row into the DeviceCollection table for this persistable.
     * @return The collectionId used to reference this entry in DeviceCollection.
     */
    private int insertCollectionBase(DeviceCollectionPersistable persistable) {
        int collectionId = nextValueHelper.getNextValue("DeviceCollection");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("DeviceCollection");
        sink.addValue("CollectionId", collectionId);
        sink.addValue("CollectionType", persistable.getCollectionType());
        sink.addValue("PersistenceType", persistable.getPersistenceType());
        yukonJdbcTemplate.update(sql);
        return collectionId;
    }
    
    /**
     * Retrieves the persistence type of the specified device collection.
     */
    private DeviceCollectionPersistenceType queryPersistenceType(int collectionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PersistenceType");
        sql.append("FROM DeviceCollection");
        sql.append("WHERE CollectionId").eq(collectionId);
        DeviceCollectionPersistenceType type = 
                yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<DeviceCollectionPersistenceType>() {
            @Override
            public DeviceCollectionPersistenceType mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("PersistenceType", DeviceCollectionPersistenceType.class);
            }
        });
        return type;
    }
    
    /**
     * Retrieves the collection type of the specified device collection.
     */
    private DeviceCollectionType queryCollectionType(int collectionId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CollectionType");
        sql.append("FROM DeviceCollection");
        sql.append("WHERE CollectionId").eq(collectionId);
        DeviceCollectionType type = yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<DeviceCollectionType>() {
            @Override
            public DeviceCollectionType mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("CollectionType", DeviceCollectionType.class);
            }
        });
        return type;
    }
}