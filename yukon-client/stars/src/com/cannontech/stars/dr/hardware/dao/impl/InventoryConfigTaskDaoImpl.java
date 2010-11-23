package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;

public class InventoryConfigTaskDaoImpl implements InventoryConfigTaskDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<InventoryConfigTask> dbTemplate;
    private final static FieldMapper<InventoryConfigTask> fieldMapper = new FieldMapper<InventoryConfigTask>() {
        @Override
        public Number getPrimaryKey(InventoryConfigTask task) {
            return task.getInventoryConfigTaskId();
        }

        @Override
        public void setPrimaryKey(InventoryConfigTask task,
                int inventoryConfigTaskId) {
            task.setInventoryConfigTaskId(inventoryConfigTaskId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder,
                InventoryConfigTask task) {
            parameterHolder.addValue("taskName", task.getTaskName());
            parameterHolder.addValue("numberOfItems", task.getNumberOfItems());
            parameterHolder.addValue("numberOfItemsProcessed", task.getNumberOfItemsProcessed());
        }
    };
    private final static YukonRowMapper<InventoryConfigTask> rowMapper = new YukonRowMapper<InventoryConfigTask>() {
        @Override
        public InventoryConfigTask mapRow(YukonResultSet rs)
                throws SQLException {
            InventoryConfigTask retVal = new InventoryConfigTask();
            retVal.setInventoryConfigTaskId(rs.getInt("InventoryConfigTaskId"));
            retVal.setTaskName(rs.getString("taskName"));
            retVal.setNumberOfItems(rs.getInt("numberOfItems"));
            retVal.setNumberOfItemsProcessed(rs.getInt("numberOfItemsProcessed"));
            return retVal;
        }
    };
    
    @Override
    public InventoryConfigTask getById(int inventoryConfigTaskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName,");
        sql.append(  "numberOfItems, numberOfItemsProcessed");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE inventoryConfigTaskId").eq(inventoryConfigTaskId);
        return yukonJdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    public InventoryConfigTask findTask(String taskName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName,");
        sql.append(  "numberOfItems, numberOfItemsProcessed");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE TaskName").eq(taskName);
        try {
            return yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<InventoryConfigTask> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName,");
        sql.append(  "numberOfItems, numberOfItemsProcessed");
        sql.append("FROM inventoryConfigTask");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Transactional
    public InventoryConfigTask create(String taskName, InventoryCollection inventoryCollection) {
        InventoryConfigTask task = new InventoryConfigTask();
        task.setTaskName(taskName);
        // TODO:  should this be a long?
        task.setNumberOfItems((int) inventoryCollection.getInventoryCount());
        task.setNumberOfItemsProcessed(0);
        dbTemplate.save(task);
        int taskId = task.getInventoryConfigTaskId();
        for (YukonInventory inventory : inventoryCollection) {
            // TODO:  batch
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO inventoryConfigTaskItem");
            sql.append("(InventoryConfigTaskId, inventoryId, status)");
            sql.values(taskId,
                       inventory.getInventoryIdentifier().getInventoryId(),
                       InventoryConfigTaskItem.Status.UNPROCESSED);
            yukonJdbcTemplate.update(sql);
        }
        return task;
    }

    @Override
    @Transactional
    public void update(InventoryConfigTask task) {
        dbTemplate.save(task);
    }
    
    @Override
    public int delete(int taskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM InventoryConfigTask");
        sql.append("WHERE InventoryConfigTaskId").eq(taskId);
        
        return yukonJdbcTemplate.update(sql);
    }

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<InventoryConfigTask>(yukonJdbcTemplate,
                                                                        nextValueHelper);
        dbTemplate.withTableName("inventoryConfigTask");
        dbTemplate.withFieldMapper(fieldMapper);
        dbTemplate.withPrimaryKeyField("inventoryConfigTaskId");
        dbTemplate.withPrimaryKeyValidOver(0);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
