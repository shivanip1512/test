package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisHelper;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisDaoImpl implements ArchiveDataAnalysisDao {
    private final static Logger log = YukonLogManager.getLogger(ArchiveDataAnalysisDaoImpl.class);

    @Autowired private ArchiveDataAnalysisHelper archiveDataAnalysisHelper;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private @Qualifier("longRunning") Executor executor;
    @Autowired private VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;

    private class ArchiveDataRowMapper implements YukonRowMapper<ArchiveData> {
        private Period intervalPeriod;
        
        public ArchiveDataRowMapper(Period intervalPeriod) {
            this.intervalPeriod = intervalPeriod;
        }
        
        @Override
        public ArchiveData mapRow(YukonResultSet rs) throws SQLException {
            Instant startTime = rs.getInstant("startTime");
            Long changeId = rs.getNullableLong("changeId");
            
            ReadType readType;
            if (changeId == null) {
                readType = ReadType.DATA_MISSING;
            } else {
                readType = ReadType.DATA_PRESENT;
            }
            
            Interval interval = new Interval(startTime, intervalPeriod);
            ArchiveData archiveData = new ArchiveData(interval, readType, changeId);
            
            return archiveData;
        }
    }
    
    private final YukonRowMapper<Analysis> analysisRowMapper = new YukonRowMapper<Analysis>() {
        @Override
        public Analysis mapRow(YukonResultSet rs) throws SQLException {
            int analysisId = rs.getInt("analysisId");
            BuiltInAttribute attribute = rs.getEnum("attribute", BuiltInAttribute.class);
            Instant startDate = rs.getInstant("startDate");
            Instant stopDate = rs.getInstant("stopDate");
            Interval dateTimeRange = new Interval(startDate, stopDate);
            PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
            Period intervalPeriod = periodFormatter.parsePeriod(rs.getString("intervalPeriod"));
            Long lastChangeId = rs.getLong("lastChangeId");
            Instant runDate = rs.getInstant("runDate");
            boolean excludeBadPointQualities = rs.getBoolean("excludeBadPointQualities");
            AdaStatus status = rs.getEnum("AnalysisStatus", AdaStatus.class);
            String statusId = rs.getString("StatusId");
            
            Analysis analysis = new Analysis();
            analysis.setAnalysisId(analysisId);
            analysis.setAttribute(attribute);
            analysis.setDateTimeRange(dateTimeRange);
            analysis.setIntervalPeriod(intervalPeriod);
            analysis.setLastChangeId(lastChangeId);
            analysis.setRunDate(runDate);
            analysis.setExcludeBadPointQualities(excludeBadPointQualities);
            analysis.setStatus(status);
            analysis.setStatusId(statusId);
            
            return analysis;
        }
    };
    
    @Override
    public int createNewAnalysis(BuiltInAttribute attribute, Period intervalPeriod, boolean excludeBadPointQualities, Interval dateTimeRange) {
        int analysisId = insertIntoArchiveDataAnalysis(attribute, intervalPeriod, excludeBadPointQualities, dateTimeRange);
        insertSlots(analysisId, dateTimeRange, intervalPeriod);
        
        return analysisId;
    }
    
    @Override
    public void insertSlotValues(PaoIdentifier paoIdentifier, Analysis analysis, int pointId, boolean excludeBadPointQualities) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ArchiveDataAnalysisSlotValue");
        sql.append("  SELECT").append(paoIdentifier.getPaoId()).append("AS DeviceId,").append("slot.SlotId, rph2.MaxChangeId");
        sql.append("  FROM ArchiveDataAnalysisSlot slot");
        sql.append("    LEFT JOIN (");
        sql.append("      SELECT Timestamp, max(changeId) MaxChangeId");
        sql.append("      FROM RawPointHistory rph");
        sql.append("      WHERE PointId").eq(pointId);
        if (excludeBadPointQualities) {
            sql.append("    AND Quality").eq_k(PointQuality.Normal);
        }
        sql.append("        AND Timestamp").gt(analysis.getDateTimeRange().getStart());
        sql.append("        AND Timestamp").lte(analysis.getDateTimeRange().getEnd());
        sql.append("      GROUP BY Timestamp");
        sql.append("    ) rph2 ON slot.StartTime = rph2.Timestamp");
        sql.append("  WHERE slot.AnalysisId").eq(analysis.getAnalysisId());
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<DeviceArchiveData> getSlotValues(int analysisId, PagingParameters paging,
            SortingParameters sorting) {
        /*
         * First DeviceId, paoname, type, MeterNumber and a flag that represents changed is null or
         * not (changeIdNull) is fetch in alias innertable for the passed analysis id.
         * The deviceid that needs to fetched are retrieved against analysisid.
         * Then the data is grouped and count of changeIdNull is fetched in alias groupedtable.
         * Then Row number is allocated to each row and the rows are selected based on row number.
         */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT");

        if (sorting != null) {
            sql.append("RowNumber,");
        }
        sql.append("PaObjectId, Name, Type, Meter_Number, Missing FROM (");
        sql.append(    "SELECT");
        
        if (sorting != null) {
            sql.append("(ROW_NUMBER() OVER (ORDER BY")
               .append(sorting.getSort())
               .append(sorting.getDirection())
               .append(")) AS RowNumber,");
        }
        

        sql.append(    "PaObjectId, Name, Type, Meter_Number,Missing");
        sql.append(    "FROM (");
        sql.append(        "SELECT PaObjectId, Name, Type, Meter_Number, SUM(ChangeIdNull) AS Missing");
        sql.append(        "FROM (");
        sql.append(            "SELECT SlotValue.DeviceId AS PaObjectId, PaoName as Name, Type, MeterNumber as Meter_Number,");
        sql.append(            "(CASE WHEN ChangeId IS NULL THEN 1 ELSE 0 END) AS ChangeIdNull");
        sql.append(            "FROM ArchiveDataAnalysisSlotValue slotValue");
        sql.append(              "JOIN ArchiveDataAnalysisSlot slot ON slotValue.slotId = slot.slotId");
        sql.append(              "JOIN YukonPaObject yukonpao ON yukonpao.PAObjectID = slotValue.DeviceId");
        sql.append(              "JOIN DeviceMeterGroup dmg ON yukonpao.PAObjectID = dmg.DeviceId");
        sql.append(            "WHERE AnalysisId").eq(analysisId);
        sql.append(              "AND slotValue.DeviceId IN (");
        sql.append(                "SELECT DeviceId");
        sql.append(                "FROM ArchiveDataAnalysisSlotValue");
        sql.append(                "WHERE SlotId = (");
        sql.append(                    "SELECT SlotId FROM");
        sql.append(                        "(SELECT ROW_NUMBER() OVER (ORDER BY SlotId DESC) rowNumber, SlotId");
        sql.append(                        "FROM ArchiveDataAnalysisSlot");
        sql.append(                        "WHERE AnalysisId").eq(analysisId);
        sql.append(                        ") slotTable WHERE rowNumber=1");
        sql.append(                ")");
        sql.append(            ")");
        sql.append(        ") innertable");
        sql.append(        "GROUP BY PaObjectId, Type, Meter_Number, Name");
        sql.append(    ") groupedtable");
        sql.append(") outertable");
        
        if (paging != null) {
            sql.append("WHERE RowNumber BETWEEN");
            sql.append(paging.getOneBasedStartIndex()).append("AND").append(paging.getOneBasedEndIndex());
        }

        final LinkedHashMap<PaoIdentifier, DeviceArchiveData> deviceArchiveData = new LinkedHashMap<PaoIdentifier, DeviceArchiveData>();
        final Analysis analysis = getAnalysisById(analysisId);
        List<DeviceArchiveData> arsList = new ArrayList<>();

        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                DeviceArchiveData data = new DeviceArchiveData();
                PaoIdentifier pao = rs.getPaoIdentifier("paobjectId", "type");

                data.setAttribute(analysis.getAttribute());
                data.setArchiveRange(analysis.getDateTimeRange());
                data.setId(pao);
                data.setMeterNumber(rs.getString("Meter_Number"));
                data.setName(rs.getString("Name"));
                data.setMissingIntervals(rs.getInt("Missing"));
                deviceArchiveData.put(pao, data);
            }
        });

        Set<PaoIdentifier> deviceList = deviceArchiveData.keySet();

        final ArchiveDataRowMapper archiveDataRowMapper = new ArchiveDataRowMapper(analysis.getIntervalPeriod());
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT StartTime, ChangeId, AnalysisId, DeviceId");
                sql.append("FROM ArchiveDataAnalysisSlotValue slotValue");
                sql.append("  JOIN ArchiveDataAnalysisSlot slot ON slotValue.slotId = slot.slotId");
                sql.append("WHERE AnalysisId").eq(analysis.getAnalysisId());
                sql.append("AND DeviceId").in(subList);
                return sql;
            }
        };
        final ListMultimap<Integer, ArchiveData> archiveDatas = ArrayListMultimap.create();
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(jdbcTemplate);
        chunkingTemplate.query(sqlGenerator,
                               Iterables.transform(deviceList, PaoIdentifier.TO_PAO_ID),
                               new YukonRowCallbackHandler() {
                                   @Override
                                   public void processRow(YukonResultSet rs) throws SQLException {
                                       archiveDatas.put(rs.getInt("DeviceId"),
                                                        archiveDataRowMapper.mapRow(rs));
                                   }
                               });

        for (PaoIdentifier deviceId : deviceArchiveData.keySet()) {
            DeviceArchiveData data = deviceArchiveData.get(deviceId);
            data.setArchiveData(archiveDatas.get(deviceId.getPaoId()));
            arsList.add(data);
        }
        return arsList;
    }

    @Override
    public Analysis getAnalysisById(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AnalysisId, Attribute, StartDate, StopDate, IntervalPeriod, LastChangeId, RunDate, ExcludeBadPointQualities, AnalysisStatus, StatusId");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        Analysis analysis = jdbcTemplate.queryForObject(sql, analysisRowMapper);
        return analysis;
    }
    
    @Override
    public List<Analysis> getAllNotDeletedAnalyses(PagingParameters pagingParameter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM (");
        sql.append("SELECT (ROW_NUMBER() OVER (ORDER BY RunDate DESC)) RowNumber, AnalysisId, Attribute, StartDate, StopDate, IntervalPeriod, LastChangeId, RunDate, ExcludeBadPointQualities, AnalysisStatus, StatusId");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisStatus").neq_k(AdaStatus.DELETED);
        sql.append(") ArchiveDataAnalysisInnerTable WHERE RowNumber BETWEEN");
        sql.append(pagingParameter.getOneBasedStartIndex());
        sql.append(" AND ");
        sql.append(pagingParameter.getOneBasedEndIndex());
        sql.append("ORDER BY RunDate DESC");
        
        List<Analysis> analyses = jdbcTemplate.query(sql, analysisRowMapper);
        return analyses;
    }
    
    @Override
    public void deleteAnalysis(final int analysisId) {
        log.info("Deleting analysis with id " + analysisId);
        updateStatus(analysisId, AdaStatus.DELETED, null);
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Get the slotIds that belong to this analysis
                log.debug("Getting slot ids to delete analysis " + analysisId);
                SqlStatementBuilder sql1 = new SqlStatementBuilder();
                sql1.append("SELECT SlotId");
                sql1.append("FROM ArchiveDataAnalysisSlot");
                sql1.append("WHERE AnalysisId").eq(analysisId);
                List<Integer> slotIds = jdbcTemplate.query(sql1, TypeRowMapper.INTEGER);
                
                //perform deletion of slots and slot values in chunks so we don't completely lock the table
                log.debug("Deleting slots and slot values for analysis " + analysisId);
                SqlFragmentGenerator<Integer> sqlFragmentGenerator = new SqlFragmentGenerator<Integer>() {
                    @Override
                    public SqlFragmentSource generate(List<Integer> subList) {
                        log.trace("Generating sql chunk for deleting analysis " + analysisId);
                        SqlStatementBuilder sql2 = new SqlStatementBuilder();
                        sql2.append("DELETE FROM ArchiveDataAnalysisSlot");
                        sql2.append("WHERE SlotId").in(subList);
                        return sql2;
                    }
                };
                ChunkingSqlTemplate chunkingSqlTemplate = new ChunkingSqlTemplate(jdbcTemplate);
                chunkingSqlTemplate.update(sqlFragmentGenerator, slotIds);
                
                //finally, delete the analysis
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("DELETE FROM ArchiveDataAnalysis");
                sql.append("WHERE AnalysisId").eq(analysisId);
                jdbcTemplate.update(sql);
                log.info("Deleted analysis " + analysisId);
            }
        });
    }
    
    @Override
    public int getNumberOfDevicesInAnalysis(int analysisId) {
        // It might be more correct to do 'GROUP BY DeviceCount' here but that slows this down
        // We can select 'Top 1' because every slotId for any given AnalysisId will have the same set of devices
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CASE WHEN DeviceCount IS NULL");
        sql.append("    THEN 0 ELSE DeviceCount END");
        sql.append("    FROM (SELECT (");
        sql.append("SELECT DeviceCount");
        sql.append("FROM(");
        sql.append("SELECT DeviceCount,row_number() OVER (ORDER BY DeviceCount DESC) RowNumber");
        sql.append("FROM ArchiveDataAnalysisSlot ADAS");
        sql.append("LEFT JOIN (");
        sql.append("    SELECT COUNT(DISTINCT DeviceId) AS DeviceCount, SlotId");
        sql.append("    FROM ArchiveDataAnalysisSlotValue");
        sql.append("    GROUP BY SlotId");
        sql.append(") SlotCount");
        sql.append("ON SlotCount.SlotId = ADAS.SlotId");
        sql.append("WHERE AnalysisId").eq(analysisId);
        sql.append(") tmp ");
        sql.append("WHERE RowNumber = 1");
        sql.append(") AS DeviceCount ");
        sql.append(getTable().getSql());
        sql.append(" ) outertable");
        

        return jdbcTemplate.queryForInt(sql);
    }
    
    private SqlFragmentSource getTable() {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        SqlBuilder sqla = builder.buildForAllOracleDatabases();
        sqla.append("FROM Dual");
        
        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("");

        return builder;
    }
    
    @Override
    public List<PaoIdentifier> getRelevantDeviceIds(int analysisId) {
        // Since every slot has the same set of devices, we can use 'TOP 1' to limit this query to one slot
        // which speeds this query up a lot for large archives
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();
        SqlBuilder sqla = builder.buildFor(DatabaseVendor.getMsDatabases());
        sqla.append("SELECT DeviceId AS PAObjectID, Type")
            .append("FROM ArchiveDataAnalysisSlotValue SlotValue")
            .append("JOIN (")
            .append("    SELECT TOP 1 SlotId")
            .append("    FROM ArchiveDataAnalysisSlot")
            .append("    WHERE AnalysisId").eq(analysisId)
            .append(") AS Slot")
            .append("ON Slot.SlotId = SlotValue.SlotId")
            .append("JOIN YukonPAObject ypo ON ypo.paobjectId = slotValue.deviceId");
        
        SqlBuilder sqlb = builder.buildOther();
        sqlb.append("SELECT DeviceId AS PAObjectID, Type")
            .append("FROM ArchiveDataAnalysisSlotValue slotValue")
            .append("JOIN (")
            .append("    SELECT SlotId")
            .append("    FROM ArchiveDataAnalysisSlot")
            .append("    WHERE AnalysisId").eq(analysisId)
            .append("    AND ROWNUM <= 1 ) slot")
            .append("ON slot.SlotId = slotValue.SlotId")
            .append("JOIN YukonPAObject ypo ON ypo.PaobjectId = slotValue.DeviceId");
        

        List<PaoIdentifier> deviceIds = jdbcTemplate.query(builder, TypeRowMapper.PAO_IDENTIFIER);
        return deviceIds;
    }
    
    @Override
    public List<DevicePointValuesHolder> getAnalysisPointValues(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.Type, ypo.PAObjectId, rph.PointId, rph.Timestamp, rph.Value, point.PointType");
        sql.append("FROM ArchiveDataAnalysisSlot slot");
        sql.append("  JOIN ArchiveDataAnalysisSlotValue value ON value.slotId = slot.slotId");
        sql.append("  JOIN YukonPAObject ypo ON ypo.paobjectId = value.deviceId");
        sql.append("  LEFT JOIN RawPointHistory rph ON rph.changeId = value.changeId");
        sql.append("  LEFT JOIN Point ON Point.PointId = rph.PointId");
        sql.append("WHERE slot.analysisId").eq(analysisId);
        sql.append("ORDER BY deviceId, startTime");
        
        final ArrayListMultimap<PaoIdentifier, PointValueHolder> devicePointValuesMap = ArrayListMultimap.create();
        
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                PaoIdentifier paoIdentifier = new PaoIdentifier(rs.getInt("PAObjectId"), rs.getEnum("Type", PaoType.class));
                PointValueHolder pointValue = null;
                
                Integer pointId = rs.getNullableInt("PointId");
                if(pointId != null) {
                    Date timestamp = rs.getDate("Timestamp");
                    PointType type = rs.getEnum("PointType", PointType.class);
                    Double value = rs.getDouble("Value");
                    pointValue = new SimplePointValue(pointId, timestamp, type.getPointTypeId(), value);
                }
                
                devicePointValuesMap.put(paoIdentifier, pointValue);
            }
        });
        
        List<PaoIdentifier> paoIdentifiers = getRelevantDeviceIds(analysisId);
        Map<PaoIdentifier, DisplayablePao> displayableMap = paoLoadingService.getDisplayableDeviceLookup(paoIdentifiers);
        
        List<DevicePointValuesHolder> devicePointValuesList = Lists.newArrayList();
        for(PaoIdentifier paoIdentifier : devicePointValuesMap.keySet()) {
            DevicePointValuesHolder holder = new DevicePointValuesHolder();
            holder.setPaoIdentifier(paoIdentifier);
            holder.setDisplayablePao(displayableMap.get(paoIdentifier));
            holder.setPointValues(devicePointValuesMap.get(paoIdentifier));
            devicePointValuesList.add(holder);
        }
        
        return devicePointValuesList;
    }
    
    @Override
    public void updateStatus(int analysisId, AdaStatus status, String statusId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE ArchiveDataAnalysis");
        sql.append("SET AnalysisStatus").eq(status);
        sql.append(", StatusId").eq(statusId);
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        jdbcTemplate.update(sql);
    }
    
    private int insertIntoArchiveDataAnalysis(BuiltInAttribute attribute, Period intervalPeriod, boolean excludeBadPointQualities, Interval dateTimeRange) {
        long maxChangeId = rawPointHistoryDao.getMaxChangeId();
        int analysisId = nextValueHelper.getNextValue("ArchiveDataAnalysis");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("ArchiveDataAnalysis");
        sink.addValue("AnalysisId", analysisId);
        sink.addValue("Attribute", attribute.getKey());
        PeriodFormatter formatter = ISOPeriodFormat.standard();
        sink.addValue("IntervalPeriod", formatter.print(intervalPeriod));
        sink.addValue("LastChangeId", maxChangeId);
        sink.addValue("RunDate", new Date());
        sink.addValue("ExcludeBadPointQualities", YNBoolean.valueOf(excludeBadPointQualities));
        sink.addValue("StartDate", dateTimeRange.getStart());
        sink.addValue("StopDate", dateTimeRange.getEnd());
        sink.addValue("AnalysisStatus", AdaStatus.RUNNING);
        
        jdbcTemplate.update(sql);
        
        return analysisId;
    }
    
    private void insertSlots(int analysisId, Interval dateTimeRange, Period intervalPeriod) {
        List<Instant> relevantTimes = archiveDataAnalysisHelper.getListOfRelevantDateTimes(dateTimeRange, intervalPeriod);
        
        for(Instant dateTime : relevantTimes) {
            int slotId = nextValueHelper.getNextValue("ArchiveDataAnalysisSlot");
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink sink = sql.insertInto("ArchiveDataAnalysisSlot");
            sink.addValue("SlotId", slotId);
            sink.addValue("AnalysisId", analysisId);
            sink.addValue("StartTime", dateTime);
            
            jdbcTemplate.update(sql);
        }
    }

    @Override
    public int getTotalNonDeletedAdaCount() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisStatus").neq_k(AdaStatus.DELETED);
        return jdbcTemplate.queryForInt(sql);
    }
}
