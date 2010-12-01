package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
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
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.google.common.collect.Lists;

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
    private final static YukonRowMapper<InventoryConfigTaskItem> itemRowMapper = new YukonRowMapper<InventoryConfigTaskItem>() {
        @Override
        public InventoryConfigTaskItem mapRow(YukonResultSet rs)
        throws SQLException {
            InventoryConfigTaskItem retVal = new InventoryConfigTaskItem();
            retVal.setInventoryConfigTaskId(rs.getInt("inventoryConfigTaskId"));
            retVal.setInventoryId(rs.getInt("inventoryId"));
            retVal.setEnergyCompanyId(rs.getInt("energyCompanyId"));
            retVal.setStatus(Status.valueOf(rs.getString("status")));
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
    public List<InventoryConfigTask> getUnfinished() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName,");
        sql.append(  "numberOfItems, numberOfItemsProcessed");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE numberOfItems > numberOfItemsProcessed");
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
            sql.values(taskId, inventory.getInventoryIdentifier().getInventoryId(), Status.UNPROCESSED);
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

    @Override
    @Transactional(readOnly=true)
    public List<InventoryConfigTaskItem> getItems(int maxItems) {
        List<InventoryConfigTask> unfinishedTasks = getUnfinished();
        if (unfinishedTasks.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<InventoryConfigTaskItem>> items = Lists.newArrayList();
        for (InventoryConfigTask task : unfinishedTasks) {
            items.add(getItems(task, maxItems));
        }

        List<InventoryConfigTaskItem> retVal = Lists.newArrayList();
        while (!items.isEmpty() && retVal.size() < maxItems) {
            Iterator<List<InventoryConfigTaskItem>> iter = items.iterator();
            while (iter.hasNext() && retVal.size() < maxItems) {
                List<InventoryConfigTaskItem> thisList = iter.next();
                if (!thisList.isEmpty()) {
                    retVal.add(thisList.remove(0));
                }
                if (thisList.isEmpty()) {
                    iter.remove();
                }
            }
        }
        return retVal;
    }

    private List<InventoryConfigTaskItem> getItems(InventoryConfigTask task, int maxItems) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM (");
        sql.append(  "SELECT inventoryConfigTaskId, ti.inventoryId, status, energyCompanyId,");
        sql.append(    "ROW_NUMBER() OVER (ORDER BY ti.inventoryId) AS rowNumber");
        sql.append(  "FROM inventoryConfigTaskItem ti");
        sql.append(    "JOIN ecToInventoryMapping ecm ON ecm.inventoryId = ti.inventoryId");
        sql.append(  "WHERE inventoryConfigTaskId").eq(task.getInventoryConfigTaskId());
        sql.append(    "AND status").eq(Status.UNPROCESSED).append(") f");
        sql.append("WHERE rowNumber").lte(maxItems);
        return yukonJdbcTemplate.query(sql, itemRowMapper);
    }

    @Override
    public int getFailedCount(int taskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM InventoryConfigTaskItem");
        sql.append("WHERE InventoryConfigTaskId").eq(taskId);
        sql.append(  "AND Status").eq(Status.FAIL);
        return yukonJdbcTemplate.queryForInt(sql);
    }

    @Override
    public int getSuccessCount(int taskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM InventoryConfigTaskItem");
        sql.append("WHERE InventoryConfigTaskId").eq(taskId);
        sql.append(  "AND Status").eq(Status.SUCCESS);
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public List<InventoryIdentifier> getSuccessFailList(int taskId, Status status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT icti.InventoryId InventoryId, yle.YukonDefinitionId YukonDefinitionId");
        sql.append("FROM InventoryConfigTaskItem icti");
        sql.append(  "JOIN LmHardwareBase lmhb ON lmhb.InventoryId = icti.InventoryId");
        sql.append(  "JOIN YukonListEntry yle ON yle.EntryID = lmhb.LMHardwareTypeID");
        sql.append("WHERE icti.InventoryConfigTaskId").eq(taskId);
        sql.append(  "AND Status").eq(status);
        
        return yukonJdbcTemplate.query(sql, new InventoryDaoImpl.InventoryIdentifierMapper());
    }

    @Override
    @Transactional
    public void markComplete(InventoryConfigTaskItem taskItem, Status status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE inventoryConfigTaskItem");
        sql.append("SET status = ").appendArgument(status);
        sql.append("WHERE inventoryConfigTaskId").eq(taskItem.getInventoryConfigTaskId());
        sql.append(  "AND inventoryId").eq(taskItem.getInventoryId());
        yukonJdbcTemplate.update(sql);

        sql = new SqlStatementBuilder();
        sql.append("UPDATE inventoryConfigTask");
        sql.append("SET numberOfItemsProcessed = numberOfItemsProcessed + 1");
        sql.append("WHERE inventoryConfigTaskId").eq(taskItem.getInventoryConfigTaskId());
        yukonJdbcTemplate.update(sql);
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