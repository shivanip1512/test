package com.cannontech.common.bulk.collection.device.dao.impl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.device.dao.DeviceCollectionDao;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionField;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionBase;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionById;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceCollectionDaoImpl implements DeviceCollectionDao {
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    @Transactional
    public int saveCollection(DeviceCollectionBase collection) {
        //Insert base device collection
        int collectionId = insertCollectionBase(collection);
        
        if (collection.getCollectionDbType() == DeviceCollectionDbType.DEVICE_LIST) {
            //Insert deviceIds
            DeviceCollectionById listBase = (DeviceCollectionById) collection;
            for(int deviceId : listBase.getDeviceIds()) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink sink = sql.insertInto("DeviceCollectionById");
                sink.addValue("CollectionId", collectionId);
                sink.addValue("DeviceId", deviceId);
                yukonJdbcTemplate.update(sql);
            }
        } else if (collection.getCollectionDbType() == DeviceCollectionDbType.FIELD) {
            //Insert key-value pairs
            DeviceCollectionByField fieldBase = (DeviceCollectionByField) collection;
            for (DeviceCollectionField field : fieldBase.getFields()) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                SqlParameterSink sink = sql.insertInto("DeviceCollectionByField");
                sink.addValue("CollectionId", collectionId);
                sink.addValue("FieldName", field.getName());
                sink.addValue("FieldValue", field.getValue());
                yukonJdbcTemplate.update(sql);
            }
        } else {
            throw new IllegalArgumentException("Unsupported device collection persistable type: "
                                               + collection.getCollectionDbType());
        }
        
        return collectionId;
    }
    
    @Override
    public DeviceCollectionBase loadCollection(int collectionId) {
        //load db type of collection
        DeviceCollectionDbType dbType = queryCollectionDbType(collectionId);
        DeviceCollectionType collectionType = queryCollectionType(collectionId);
        //query for collection data
        if (dbType == DeviceCollectionDbType.DEVICE_LIST) {
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DeviceId");
            sql.append("FROM DeviceCollectionById");
            sql.append("WHERE CollectionId").eq(collectionId);
            List<Integer> deviceIds = yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
            
            return new DeviceCollectionById(collectionType, deviceIds);
            
        } else if (dbType == DeviceCollectionDbType.FIELD) {
            
            final Set<DeviceCollectionField> fields = new HashSet<>();
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT FieldName, FieldValue");
            sql.append("FROM DeviceCollectionByField");
            sql.append("WHERE CollectionId").eq(collectionId);
            
            yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                    String name = rs.getString("FieldName");
                    String value = rs.getString("FieldValue");
                    fields.add(DeviceCollectionField.of(name, value));
                }
            });
            
            return new DeviceCollectionByField(collectionType, fields);
            
        } else {
            throw new IllegalStateException("Loaded an unsupported type of persistent device collection: " + dbType);
        }
    }
    
    @Override
    public boolean deleteCollection(int collectionId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceCollection");
        sql.append("WHERE CollectionId").eq(collectionId);
        
        return yukonJdbcTemplate.update(sql) == 1;
    }
    
    /**
     * Inserts a new row into the DeviceCollection table for this collection.
     * @return The collectionId used to reference this entry in DeviceCollection.
     */
    private int insertCollectionBase(DeviceCollectionBase collection) {
        
        int collectionId = nextValueHelper.getNextValue("DeviceCollection");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("DeviceCollection");
        sink.addValue("CollectionId", collectionId);
        sink.addValue("CollectionType", collection.getCollectionType());
        sink.addValue("PersistenceType", collection.getCollectionDbType());
        yukonJdbcTemplate.update(sql);
        
        return collectionId;
    }
    
    /**
     * Retrieves the persistence type of the specified device collection.
     */
    private DeviceCollectionDbType queryCollectionDbType(int collectionId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PersistenceType");
        sql.append("FROM DeviceCollection");
        sql.append("WHERE CollectionId").eq(collectionId);
        
        DeviceCollectionDbType type = 
                yukonJdbcTemplate.queryForObject(sql, new YukonRowMapper<DeviceCollectionDbType>() {
            @Override
            public DeviceCollectionDbType mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("PersistenceType", DeviceCollectionDbType.class);
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