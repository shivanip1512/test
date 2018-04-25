package com.cannontech.dr.rfn.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStatementBuilder.SqlBatchUpdater;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.dr.rfn.dao.PqrEventDao;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.model.PqrEventType;
import com.cannontech.dr.rfn.model.PqrResponseType;

public class PqrEventDaoImpl implements PqrEventDao {
    private static final Logger log = YukonLogManager.getLogger(PqrEventDaoImpl.class);
    private static boolean createTable = false;
    private static VendorSpecificSqlBuilder tableCreatorSql;
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final YukonRowMapper<PqrEvent> eventRowMapper = (rs) -> {
        int inventoryId = rs.getInt("inventoryId");
        Instant timestamp = rs.getInstant("timestamp");
        PqrEventType eventType = rs.getEnum("EventType", PqrEventType.class);
        PqrResponseType responseType = rs.getEnum("ResponseType", PqrResponseType.class);
        double value = rs.getDouble("Value");
        RfnIdentifier rfnIdentifier = rs.getRfnIdentifier();
        return new PqrEvent(inventoryId, rfnIdentifier, timestamp, eventType, responseType, value);
    };
    
    @PostConstruct
    private void init() {
        VendorSpecificSqlBuilder tableCounterSql = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder oracleCountSql = tableCounterSql.buildFor(DatabaseVendor.getOracleDatabases());
        SqlBuilder msCountSql = tableCounterSql.buildOther();
        buildOracleTableCounterSql(oracleCountSql);
        buildMsSqlTableCounterSql(msCountSql);
        
        int tableCount = jdbcTemplate.queryForInt(tableCounterSql);
        
        if (tableCount == 0) {
            createTable = true;
            tableCreatorSql = vendorSpecificSqlBuilderFactory.create();
            SqlBuilder oracleSql = tableCreatorSql.buildFor(DatabaseVendor.getOracleDatabases());
            SqlBuilder msSql = tableCreatorSql.buildOther();
            buildOracleTableCreationSql(oracleSql);
            buildMsSqlTableCreationSql(msSql);
        }
    }
    
    @Override
    @Transactional
    public List<PqrEvent> getEvents(Iterable<Integer> inventoryIds, Range<Instant> dateRange) {
        // If the table doesn't exist, there is no data to return.
        if (createTable) {
            return new ArrayList<>();
        }
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> inventoryIdsSubList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT pel.EventLogId, pel.InventoryId, pel.Timestamp, pel.EventType, pel.ResponseType, pel.Value, ra.Manufacturer, ra.Model, ra.SerialNumber");
                sql.append("FROM PqrEventLog pel");
                sql.append("JOIN InventoryBase ib ON pel.InventoryId = ib.InventoryId");
                sql.append("JOIN RfnAddress ra ON ra.DeviceId = ib.DeviceId");
                sql.append("WHERE pel.InventoryId").in(inventoryIdsSubList);
                sql.append("AND Timestamp").gte(dateRange.getMin());
                sql.append("AND Timestamp").lte(dateRange.getMax());
                sql.append("ORDER BY InventoryId ASC, Timestamp ASC");
                return sql;
            }
        };
        
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        return chunkingTemplate.query(sqlGenerator, inventoryIds, eventRowMapper);
    }
    
    @Override
    @Transactional
    public void saveEvents(Collection<PqrEvent> events) {
        createTable();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlBatchUpdater updater = sql.batchInsertInto("PqrEventLog");
        updater.columns("EventLogId", "InventoryId", "Timestamp", "EventType", "ResponseType", "Value");
        updater.values(buildEventValues(events));
        
        jdbcTemplate.yukonBatchUpdate(sql); 
    }
    
    /**
     * Convert a Collection of PqrEvents into Lists of event values, for use in a batch insertion.
     */
    private List<List<Object>> buildEventValues(Collection<PqrEvent> events) {
        return events.stream()
                     .map(this::buildEventValues)
                     .collect(Collectors.toList());
    }
    
    /**
     * Builds a List of values from a PqrEvent, for use in a batch insertion.
     */
    private List<Object> buildEventValues(PqrEvent event) { 
        List<Object> values = new ArrayList<>();
        values.add(nextValueHelper.getNextValue("PqrEventLog"));
        values.add(event.getInventoryId());
        values.add(event.getTimestamp());
        values.add(event.getEventType());
        values.add(event.getResponseType());
        values.add(event.getValue());
        return values;
    }
    
    /**
     * Create the PqrEventLog table, constraints and indexes, if not already present.
     * This will require the sql user to have appropriate privileges to work.
     */
    private void createTable() {
        if (createTable) {
            try {
                jdbcTemplate.update(tableCreatorSql);
                addIndex();
                addFk();
                createTable = false;
                log.info("Power Quality Response Event Log table created successfully.");
            } catch (Exception e) {
                log.warn("Unable to create Power Quality Response tables", e);
            }
        }
    }
    
    /**
     * Create the PqrEventLog table, constraints and indexes with MSSQL syntax.
     */
    private void buildMsSqlTableCreationSql(SqlBuilder sql) {
        // Create table
        sql.append("CREATE TABLE PqrEventLog (");
        sql.append(  "EventLogId      NUMERIC(18,0)   NOT NULL,");
        sql.append(  "InventoryId     NUMERIC(18,0)   NOT NULL,");
        sql.append(  "Timestamp       DATETIME        NOT NULL,");
        sql.append(  "EventType       VARCHAR(50)     NOT NULL,");
        sql.append(  "ResponseType    VARCHAR(50)     NOT NULL,");
        sql.append(  "Value           FLOAT           NOT NULL,");
        sql.append(  "CONSTRAINT PK_PqrEventLog PRIMARY KEY (EventLogId)");
        sql.append(")");
    }
    
    /**
     * Create the PqrEventLog table, constraints and indexes with Oracle syntax.
     */
    private void buildOracleTableCreationSql(SqlBuilder sql) {
        //Create table
        sql.append("CREATE TABLE PqrEventLog (");
        sql.append(  "EventLogId    NUMBER          NOT NULL,");
        sql.append(  "InventoryId   NUMBER          NOT NULL,");
        sql.append(  "Timestamp     DATETIME        NOT NULL,");
        sql.append(  "EventType     VARCHAR2(50)    NOT NULL,");
        sql.append(  "ResponseType  VARCHAR2(50)    NOT NULL,");
        sql.append(  "Value         FLOAT           NOT NULL,");
        sql.append(  "CONSTRAINT PK_PqrEventLog PRIMARY KEY (EventLogId)");
        sql.append(")");
    }
    
    /**
     * Adds the foreign key to the PqrEventLog table.
     */
    private void addFk() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("ALTER TABLE PqrEventLog");
        sql.append("ADD CONSTRAINT FK_PqrEvent_InventoryBase FOREIGN KEY (InventoryId)");
        sql.append("REFERENCES InventoryBase (InventoryId)");
        jdbcTemplate.update(sql);
    }
    
    /**
     * Adds the index to the PqrEventLog table.
     */
    private void addIndex() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("CREATE INDEX Indx_PqrEvent_Inv_Time ON PqrEventLog (");
        sql.append(  "InventoryId ASC,");
        sql.append(  "Timestamp ASC");
        sql.append(")");
        jdbcTemplate.update(sql);
    }
    
    /**
     * Checks if the PqrEventLog table exists, with MS SQL syntax.
     */
    private void buildOracleTableCounterSql(SqlBuilder sql) {
        sql.append("SELECT COUNT(*) AS TableCount");
        sql.append("FROM USER_TABLES");
        sql.append("WHERE TABLE_NAME").eq("PqrEventLog");
    }
    
    /**
     * Checks if the PqrEventLog table exists, with Oracle syntax.
     */
    private void buildMsSqlTableCounterSql(SqlBuilder sql) {
        sql.append("SELECT COUNT(*) AS TableCount");
        sql.append("FROM INFORMATION_SCHEMA.TABLES");
        sql.append("WHERE TABLE_NAME").eq("PqrEventLog");
    }
}
