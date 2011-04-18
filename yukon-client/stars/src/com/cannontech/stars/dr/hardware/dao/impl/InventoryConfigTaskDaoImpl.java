package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.inventory.InventoryIdentifierMapper;
import com.cannontech.common.inventory.YukonInventory;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.InventoryConfigTaskDao;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTask;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem;
import com.cannontech.stars.dr.hardware.model.InventoryConfigTaskItem.Status;
import com.google.common.collect.Lists;

public class InventoryConfigTaskDaoImpl implements InventoryConfigTaskDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private YukonUserDao yukonUserDao;
    private InventoryIdentifierMapper inventoryIdentifierMapper;

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
            parameterHolder.addValue("sendInService", YNBoolean.valueOf(task.isSendInService()));
            parameterHolder.addValue("numberOfItems", task.getNumberOfItems());
            parameterHolder.addValue("numberOfItemsProcessed", task.getNumberOfItemsProcessed());
            parameterHolder.addValue("energyCompanyId", task.getEnergyCompanyId());
            parameterHolder.addValue("userId", task.getUser().getUserID());
        }
    };
    private final YukonRowMapper<InventoryConfigTask> rowMapper = new YukonRowMapper<InventoryConfigTask>() {
        @Override
        public InventoryConfigTask mapRow(YukonResultSet rs)
                throws SQLException {
            InventoryConfigTask retVal = new InventoryConfigTask();
            retVal.setInventoryConfigTaskId(rs.getInt("InventoryConfigTaskId"));
            retVal.setTaskName(rs.getString("taskName"));
            retVal.setSendInService(rs.getEnum("sendInService", YNBoolean.class).getBoolean());
            retVal.setNumberOfItems(rs.getInt("numberOfItems"));
            retVal.setNumberOfItemsProcessed(rs.getInt("numberOfItemsProcessed"));
            retVal.setEnergyCompanyId(rs.getInt("energyCompanyId"));
            retVal.setUser(yukonUserDao.getLiteYukonUser(rs.getInt("userId")));
            return retVal;
        }
    };
    private final YukonRowMapper<InventoryConfigTaskItem> itemRowMapper = new YukonRowMapper<InventoryConfigTaskItem>() {
        @Override
        public InventoryConfigTaskItem mapRow(YukonResultSet rs)
        throws SQLException {
            InventoryConfigTaskItem retVal = new InventoryConfigTaskItem();
            retVal.setInventoryConfigTask(rowMapper.mapRow(rs));
            retVal.setInventoryId(rs.getInt("inventoryId"));
            retVal.setStatus(rs.getEnum("status", Status.class));
            return retVal;
        }
    };

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public InventoryConfigTask getById(int inventoryConfigTaskId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName, sendInService, numberOfItems,");
        sql.append(  "numberOfItemsProcessed, energyCompanyId, userId");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE inventoryConfigTaskId").eq(inventoryConfigTaskId);
        return yukonJdbcTemplate.queryForObject(sql, rowMapper);
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public InventoryConfigTask findTask(String taskName, int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName, sendInService, numberOfItems,");
        sql.append(  "numberOfItemsProcessed, energyCompanyId, userId");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE TaskName").eq(taskName);
        sql.append(  "AND energyCompanyId").eq(energyCompanyId);
        try {
            return yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<InventoryConfigTask> getAll(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName, sendInService, numberOfItems,");
        sql.append(  "numberOfItemsProcessed, energyCompanyId, userId");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE energyCompanyId").eq(energyCompanyId);
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<InventoryConfigTask> getUnfinished(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT inventoryConfigTaskId, taskName, sendInService, numberOfItems,");
        sql.append(  "numberOfItemsProcessed, energyCompanyId, userId");
        sql.append("FROM inventoryConfigTask");
        sql.append("WHERE numberOfItems > numberOfItemsProcessed");
        sql.append("AND energyCompanyId").eq(energyCompanyId);
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Transactional
    public InventoryConfigTask create(String taskName, boolean sendInService,
            InventoryCollection inventoryCollection, int energyCompanyId, LiteYukonUser user) {
        InventoryConfigTask task = new InventoryConfigTask();
        task.setTaskName(taskName);
        task.setSendInService(sendInService);
        task.setNumberOfItems((int) inventoryCollection.getCount());
        task.setNumberOfItemsProcessed(0);
        task.setEnergyCompanyId(energyCompanyId);
        task.setUser(user);
        dbTemplate.save(task);
        int taskId = task.getInventoryConfigTaskId();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO inventoryConfigTaskItem");
        sql.append("(InventoryConfigTaskId, inventoryId, status)");
        sql.append("VALUES (" + taskId + ", ?, '" + Status.UNPROCESSED + "')");

        List<Object[]> batchArgs = Lists.newArrayList();
        for (YukonInventory inventory : inventoryCollection) {
            batchArgs.add(new Object[] {inventory.getInventoryIdentifier().getInventoryId()});
        }
        yukonJdbcTemplate.batchUpdate(sql.getSql(), batchArgs);

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
    @Transactional(propagation=Propagation.SUPPORTS)
    public Iterable<InventoryConfigTaskItem> getItems(int maxItems, int energyCompanyId) {
        List<InventoryConfigTask> unfinishedTasks = getUnfinished(energyCompanyId);
        if (unfinishedTasks.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<InventoryConfigTaskItem>> items = Lists.newArrayList();
        for (InventoryConfigTask task : unfinishedTasks) {
            items.add(getItems(task, maxItems));
        }

        return IterableUtils.clipped(IterableUtils.collate(items), maxItems);
    }

    private List<InventoryConfigTaskItem> getItems(InventoryConfigTask task, int maxItems) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM (");
        sql.append(  "SELECT ti.inventoryConfigTaskId, inventoryId, status, taskName,");
        sql.append(    "sendInService, numberOfItems, numberOfItemsProcessed, energyCompanyId,");
        sql.append(    "userId, ROW_NUMBER() OVER (ORDER BY inventoryId) AS rowNumber");
        sql.append(  "FROM inventoryConfigTaskItem ti");
        sql.append(    "JOIN inventoryConfigTask t ON ti.inventoryConfigTaskId = t.inventoryConfigTaskId");
        sql.append(  "WHERE ti.inventoryConfigTaskId").eq(task.getInventoryConfigTaskId());
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
        sql.append("SELECT i.InventoryID, h.LMHardwareTypeID, m.MeterTypeID");
        sql.append(  "JOIN InventoryConfigTaskItem icti on icti.InventoryId = i.InventoryId");
        sql.append(  "LEFT JOIN LmHardwareBase h ON h.InventoryId = i.InventoryId");
        sql.append(  "LEFT JOIN MeterHardwareBase m ON m.InventoryId = i.InventoryId");
        sql.append("WHERE icti.InventoryConfigTaskId").eq(taskId);
        sql.append(  "AND icti.Status").eq(status);
        
        return yukonJdbcTemplate.query(sql, inventoryIdentifierMapper);
    }

    @Override
    @Transactional
    public void markComplete(InventoryConfigTaskItem taskItem, Status status) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE inventoryConfigTaskItem");
        sql.append("SET status = ").appendArgument(status);
        sql.append("WHERE inventoryConfigTaskId").eq(taskItem.getInventoryConfigTask().getInventoryConfigTaskId());
        sql.append(  "AND inventoryId").eq(taskItem.getInventoryId());
        yukonJdbcTemplate.update(sql);

        sql = new SqlStatementBuilder();
        sql.append("UPDATE inventoryConfigTask");
        sql.append("SET numberOfItemsProcessed = numberOfItemsProcessed + 1");
        sql.append("WHERE inventoryConfigTaskId").eq(taskItem.getInventoryConfigTask().getInventoryConfigTaskId());
        yukonJdbcTemplate.update(sql);
    }

    @PostConstruct
    public void init() {
        dbTemplate = new SimpleTableAccessTemplate<InventoryConfigTask>(yukonJdbcTemplate,
                                                                        nextValueHelper);
        dbTemplate.setTableName("inventoryConfigTask");
        dbTemplate.setFieldMapper(fieldMapper);
        dbTemplate.setPrimaryKeyField("inventoryConfigTaskId");
        dbTemplate.setPrimaryKeyValidOver(0);
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setInventoryIdentifierMapper(InventoryIdentifierMapper inventoryIdentifierMapper) {
        this.inventoryIdentifierMapper = inventoryIdentifierMapper;
    }
    
}