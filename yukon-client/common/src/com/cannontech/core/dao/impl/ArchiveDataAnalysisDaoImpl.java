package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.bulk.model.Slot;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisDaoImpl implements ArchiveDataAnalysisDao {
    private int NORMAL_POINT_QUALITY = 5;
    private RawPointHistoryDao rawPointHistoryDao;
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private PaoDao paoDao;
    
    private class ArchiveDataRowMapper implements YukonRowMapper<ArchiveData> {
        private Duration intervalDuration;
        
        public ArchiveDataRowMapper(Duration intervalDuration) {
            this.intervalDuration = intervalDuration;
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
            
            int paoId = rs.getInt("deviceId");
            
            Interval interval = new Interval(startTime, intervalDuration);
            ArchiveData data = new ArchiveData(interval, readType, changeId);
            data.setPaoId(paoId);
            return data;
        }
    }
    
    private final YukonRowMapper<Map<Instant, Integer>> datedChangeIdRowMapper = new YukonRowMapper<Map<Instant, Integer>>() {
        @Override
        public Map<Instant, Integer> mapRow(YukonResultSet rs) throws SQLException {
            Map<Instant, Integer> datedChangeIdMap = new HashMap<Instant, Integer>();
            Instant date = rs.getInstant("startTime");
            Integer changeId = rs.getNullableInt("changeId");
            datedChangeIdMap.put(date, changeId);
            return datedChangeIdMap;
        }
    };
    
    private final YukonRowMapper<Slot> slotRowMapper = new YukonRowMapper<Slot>() {
        @Override
        public Slot mapRow(YukonResultSet rs) throws SQLException {
            Instant dateTime = rs.getInstant("startTime");
            int slotId = rs.getInt("slotId");
            Slot slot = new Slot(dateTime, slotId);
            return slot;
        }
    };
    
    private final YukonRowMapper<Analysis> analysisRowMapper = new YukonRowMapper<Analysis>() {
        @Override
        public Analysis mapRow(YukonResultSet rs) throws SQLException {
            BuiltInAttribute attribute = rs.getEnum("attribute", BuiltInAttribute.class);
            Instant startDate = rs.getInstant("startDate");
            Instant stopDate = rs.getInstant("stopDate");
            Interval dateTimeRange = new Interval(startDate, stopDate);
            Duration intervalLength = new Duration(rs.getLong("intervalLengthInMillis"));
            Integer lastChangeId = rs.getInt("lastChangeId");
            Instant runDate = rs.getInstant("runDate");
            Boolean excludeBadPointQualities = rs.getEnum("excludeBadPointQualities", YNBoolean.class).getBoolean();
            
            Analysis analysis = new Analysis();
            analysis.setAttribute(attribute);
            analysis.setDateTimeRange(dateTimeRange);
            analysis.setIntervalLength(intervalLength);
            analysis.setLastChangeId(lastChangeId);
            analysis.setRunDate(runDate);
            analysis.setExcludeBadPointQualities(excludeBadPointQualities);
            
            return analysis;
        }
    };
    
    @Override
    public int createNewAnalysis(BuiltInAttribute attribute, Duration intervalLength,
                                 boolean excludeBadPointQualities, Interval dateTimeRange) {
        
        int analysisId = insertIntoArchiveDataAnalysis(attribute, intervalLength, excludeBadPointQualities, dateTimeRange);
        
        insertSlots(analysisId, dateTimeRange, intervalLength);
        
        return analysisId;
    }
    
    @Override
    public Map<Instant, Integer> getDeviceSlotValues(int analysisId, int pointId, boolean excludeBadPointQualities) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT StartTime, ChangeId");
        sql.append("FROM ArchiveDataAnalysisSlots");
        sql.append("  LEFT JOIN (");
        sql.append("    SELECT Timestamp, ChangeId");
        sql.append("    FROM RawPointHistory");
        sql.append("    WHERE PointId").eq(pointId);
        if(excludeBadPointQualities) {
            sql.append("AND Quality").eq(NORMAL_POINT_QUALITY);
        }
        sql.append("  ) rph");
        sql.append("    ON StartTime = Timestamp");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        List<Map<Instant, Integer>> datedChangeIdsList = yukonJdbcTemplate.query(sql, datedChangeIdRowMapper);
        
        Map<Instant, Integer> datedChangeIdsMap = new HashMap<Instant, Integer>();
        for(Map<Instant, Integer> map : datedChangeIdsList) {
            datedChangeIdsMap.putAll(map);
        }
        return datedChangeIdsMap;
    }
    
    @Override
    public void insertSlotValues(int deviceId, int analysisId, DeviceArchiveData data) {
        Analysis analysis = getAnalysisById(analysisId);
        Duration intervalLength = analysis.getIntervalLength();
        
        List<Slot> slots = getSlots(analysisId);
        
        //For each slot date, search for a value in the data object.
        //If it exists, insert it. Otherwise insert "missing data" value.
        for(Slot slot : slots) {
            ArchiveData read = data.getReadForDate(slot.getDateTime());
            if(read == null) {
                Instant start = slot.getDateTime();
                Interval intervalRange = new Interval(start, intervalLength);
                read = new ArchiveData(intervalRange, ReadType.DATA_MISSING, null);
            }
            insertSlotValue(data.getId().getPaoId(), read, slot);
        }
    }

    @Override
    public List<DeviceArchiveData> getSlotValues(int analysisId, List<Integer> deviceIds) {
        Analysis analysis = getAnalysisById(analysisId);
        
        List<DeviceArchiveData> dataList = Lists.newArrayList();
        for(Integer deviceId : deviceIds) {
            //get device + slot values for relevant slots - each set becomes a DeviceArchiveData
            DeviceArchiveData data = getDeviceSlotValues(analysis, deviceId);
            dataList.add(data);
        }
        
        return dataList;
    }
    
    @Override
    public Analysis getAnalysisById(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Attribute, StartDate, StopDate, IntervalLengthInMillis, LastChangeId, RunDate, ExcludeBadPointQualities");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        Analysis analysis = yukonJdbcTemplate.queryForObject(sql, analysisRowMapper);
        analysis.setAnalysisId(analysisId);
        return analysis;
    }
    
    private List<Slot> getSlots(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SlotId, StartTime");
        sql.append("FROM ArchiveDataAnalysisSlots");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        List<Slot> slotList = yukonJdbcTemplate.query(sql, slotRowMapper);
        return slotList;
    }
    
    @Override
    public List<Integer> getRelevantDeviceIds(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT DeviceId");
        sql.append("FROM ArchiveDataAnalysisSlotValues");
        sql.append("  LEFT JOIN ArchiveDataAnalysisSlots");
        sql.append("    ON ArchiveDataAnalysisSlotValues.slotId = ArchiveDataAnalysisSlots.slotId");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        List<Integer> deviceIds = yukonJdbcTemplate.query(sql, new IntegerRowMapper());
        return deviceIds;
    }
    
    private DeviceArchiveData getDeviceSlotValues(Analysis analysis, int deviceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceId, StartTime, ChangeId, AnalysisId");
        sql.append("FROM ArchiveDataAnalysisSlotValues");
        sql.append("  LEFT JOIN ArchiveDataAnalysisSlots");
        sql.append("    ON ArchiveDataAnalysisSlotValues.slotId = ArchiveDataAnalysisSlots.slotId");
        sql.append("WHERE AnalysisId").eq(analysis.getAnalysisId());
        sql.append("AND DeviceId").eq(deviceId);
        
        ArchiveDataRowMapper archiveDataRowMapper = new ArchiveDataRowMapper(analysis.getIntervalLength());
        List<ArchiveData> arsList = yukonJdbcTemplate.query(sql, archiveDataRowMapper);
        
        DeviceArchiveData data = new DeviceArchiveData();
        PaoIdentifier paoId = paoDao.getPaoIdentifierForPaoId(deviceId);
        data.setId(paoId);
        data.setAttribute(analysis.getAttribute());
        data.setArchiveRange(analysis.getDateTimeRange());
        data.setArchiveData(arsList);
        
        return data;
    }
    
    private void insertSlotValue(int deviceId, ArchiveData read, Slot slot) {
        int slotValueId = nextValueHelper.getNextValue("ArchiveDataAnalysisSlotValues");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("ArchiveDataAnalysisSlotValues");
        sink.addValue("SlotValueId", slotValueId);
        sink.addValue("DeviceId", deviceId);
        sink.addValue("SlotId", slot.getSlotId());
        sink.addValue("ChangeId", read.getChangeId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private int insertIntoArchiveDataAnalysis(BuiltInAttribute attribute, Duration intervalLength, boolean excludeBadPointQualities, Interval dateTimeRange) {
        int maxChangeId = rawPointHistoryDao.getMaxChangeId();
        int analysisId = nextValueHelper.getNextValue("ArchiveDataAnalysis");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("ArchiveDataAnalysis");
        sink.addValue("AnalysisId", analysisId);
        sink.addValue("Attribute", attribute.getKey());
        sink.addValue("IntervalLengthInMillis", intervalLength.getMillis());
        sink.addValue("LastChangeId", maxChangeId);
        sink.addValue("RunDate", new Date());
        sink.addValue("ExcludeBadPointQualities", YNBoolean.valueOf(excludeBadPointQualities));
        sink.addValue("StartDate", dateTimeRange.getStart());
        sink.addValue("StopDate", dateTimeRange.getEnd());
        
        yukonJdbcTemplate.update(sql);
        
        return analysisId;
    }
    
    private void insertSlots(int analysisId, Interval dateTimeRange, Duration intervalLength) {
        List<Instant> relevantTimes = getListOfRelevantDateTimes(dateTimeRange, intervalLength);
        
        for(Instant dateTime : relevantTimes) {
            int slotId = nextValueHelper.getNextValue("ArchiveDataAnalysisSlots");
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink sink = sql.insertInto("ArchiveDataAnalysisSlots");
            sink.addValue("SlotId", slotId);
            sink.addValue("AnalysisId", analysisId);
            sink.addValue("StartTime", dateTime);
            
            yukonJdbcTemplate.update(sql);
        }
    }
    
    private List<Instant> getListOfRelevantDateTimes(Interval dateTimeRange, Duration interval) {
        Instant start = dateTimeRange.getStart().toInstant();
        Instant stop = dateTimeRange.getEnd().toInstant();
        List<Instant> relevantDateTimes = Lists.newArrayList();
        Instant tempInstant = start;
        while(tempInstant.isBefore(stop)) {
            relevantDateTimes.add(tempInstant);
            tempInstant = tempInstant.plus(interval);
        }
        return relevantDateTimes;
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
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
