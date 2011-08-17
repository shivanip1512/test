package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.bulk.service.ArchiveDataAnalysisHelper;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisDaoImpl implements ArchiveDataAnalysisDao {
    private ArchiveDataAnalysisHelper archiveDataAnalysisHelper;
    private RawPointHistoryDao rawPointHistoryDao;
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoLoadingService paoLoadingService;
    private Executor longRunningExecutor;
    
    private class ArchiveDataRowMapper implements YukonRowMapper<ArchiveData> {
        private Period intervalPeriod;
        
        public ArchiveDataRowMapper(Period intervalPeriod) {
            this.intervalPeriod = intervalPeriod;
        }
        
        @Override
        public ArchiveData mapRow(YukonResultSet rs) throws SQLException {
            Instant startTime = rs.getInstant("startTime");
            Integer changeId = rs.getNullableInt("changeId");
            
            ReadType readType;
            if(changeId==null) {
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
            Integer lastChangeId = rs.getInt("lastChangeId");
            Instant runDate = rs.getInstant("runDate");
            boolean excludeBadPointQualities = rs.getEnum("excludeBadPointQualities", YNBoolean.class).getBoolean();
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
        if(excludeBadPointQualities) {
            sql.append("    AND Quality").eq(PointQuality.Normal);
        }
        sql.append("        AND Timestamp").gt(analysis.getDateTimeRange().getStart());
        sql.append("        AND Timestamp").lte(analysis.getDateTimeRange().getEnd());
        sql.append("      GROUP BY Timestamp");
        sql.append("    ) rph2 ON slot.StartTime = rph2.Timestamp");
        sql.append("  WHERE slot.AnalysisId").eq(analysis.getAnalysisId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public List<DeviceArchiveData> getSlotValues(int analysisId) {
        List<PaoIdentifier> deviceList = getRelevantDeviceIds(analysisId);
        return getSlotValues(analysisId, deviceList);
    }
    
    @Override
    public List<DeviceArchiveData> getSlotValues(int analysisId, List<PaoIdentifier> deviceIds) {
        Analysis analysis = getAnalysisById(analysisId);
        
        List<DeviceArchiveData> dataList = Lists.newArrayList();
        for(PaoIdentifier paoIdentifier : deviceIds) {
            //get device + slot values for relevant slots - each set becomes a DeviceArchiveData
            DeviceArchiveData data = getDeviceSlotValues(analysis, paoIdentifier);
            dataList.add(data);
        }
        
        return dataList;
    }
    
    @Override
    public Analysis getAnalysisById(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AnalysisId, Attribute, StartDate, StopDate, IntervalPeriod, LastChangeId, RunDate, ExcludeBadPointQualities, AnalysisStatus, StatusId");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        Analysis analysis = yukonJdbcTemplate.queryForObject(sql, analysisRowMapper);
        return analysis;
    }
    
    @Override
    public List<Analysis> getAllAnalyses() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AnalysisId, Attribute, StartDate, StopDate, IntervalPeriod, LastChangeId, RunDate, ExcludeBadPointQualities, AnalysisStatus, StatusId");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("ORDER BY RunDate DESC");
        
        List<Analysis> analyses = yukonJdbcTemplate.query(sql, analysisRowMapper);
        return analyses;
    }
    
    @Override
    public void deleteAnalysis(final int analysisId) {
        longRunningExecutor.execute(new Runnable() {
            public void run() {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("DELETE FROM ArchiveDataAnalysis");
                sql.append("WHERE AnalysisId").eq(analysisId);
                
                yukonJdbcTemplate.update(sql);
            }
        });
    }
    
    @Override
    public int getNumberOfDevicesInAnalysis(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(DISTINCT DeviceId)");
        sql.append("FROM ArchiveDataAnalysisSlotValue slotValue");
        sql.append("  JOIN ArchiveDataAnalysisSlot slot ON slotValue.SlotId = slot.SlotId");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        int numberOfDevices = yukonJdbcTemplate.queryForInt(sql);
        return numberOfDevices;
    }
    
    @Override
    public List<PaoIdentifier> getRelevantDeviceIds(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT PAObjectID, Type");
        sql.append("FROM ArchiveDataAnalysisSlotValue slotValue");
        sql.append("  JOIN ArchiveDataAnalysisSlot slot ON slotValue.slotId = slot.slotId");
        sql.append("  JOIN YukonPAObject ypo ON ypo.paobjectId = slotValue.deviceId");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        List<PaoIdentifier> deviceIds = yukonJdbcTemplate.query(sql, new YukonPaoRowMapper());
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
        
        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
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
    
    public void updateStatus(int analysisId, AdaStatus status, String statusId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE ArchiveDataAnalysis");
        sql.append("SET AnalysisStatus").eq(status);
        sql.append(", StatusId").eq(statusId);
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    private DeviceArchiveData getDeviceSlotValues(Analysis analysis, PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StartTime, ChangeId, AnalysisId");
        sql.append("FROM ArchiveDataAnalysisSlotValue slotValue");
        sql.append("  JOIN ArchiveDataAnalysisSlot slot ON slotValue.slotId = slot.slotId");
        sql.append("WHERE AnalysisId").eq(analysis.getAnalysisId());
        sql.append("AND DeviceId").eq(paoIdentifier.getPaoId());
        
        ArchiveDataRowMapper archiveDataRowMapper = new ArchiveDataRowMapper(analysis.getIntervalPeriod());
        List<ArchiveData> arsList = yukonJdbcTemplate.query(sql, archiveDataRowMapper);
        
        DeviceArchiveData data = new DeviceArchiveData();
        data.setAttribute(analysis.getAttribute());
        data.setArchiveRange(analysis.getDateTimeRange());
        data.setArchiveData(arsList);
        data.setId(paoIdentifier);
        
        return data;
    }
    
    private int insertIntoArchiveDataAnalysis(BuiltInAttribute attribute, Period intervalPeriod, boolean excludeBadPointQualities, Interval dateTimeRange) {
        int maxChangeId = rawPointHistoryDao.getMaxChangeId();
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
        
        yukonJdbcTemplate.update(sql);
        
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
            
            yukonJdbcTemplate.update(sql);
        }
    }
    
    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setArchiveDataAnalysisHelper(ArchiveDataAnalysisHelper archiveDataAnalysisHelper) {
        this.archiveDataAnalysisHelper = archiveDataAnalysisHelper;
    }
    
    @Resource(name="longRunningExecutor")
    public void setLongRunningExecutor(Executor longRunningExecutor) {
        this.longRunningExecutor = longRunningExecutor;
    }
}
