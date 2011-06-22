package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveData;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ArchiveDataAnalysisDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class ArchiveDataAnalysisDaoImpl implements ArchiveDataAnalysisDao {
    private RawPointHistoryDao rawPointHistoryDao;
    private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
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
            
            PaoIdentifier paoIdentifier = new PaoIdentifier(rs.getInt("deviceId"), rs.getEnum("Type", PaoType.class));
            
            Interval interval = new Interval(startTime, intervalDuration);
            ArchiveData archiveData = new ArchiveData(interval, readType, changeId, paoIdentifier);
            
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
            Duration intervalLength = new Duration(rs.getLong("intervalLengthInMillis"));
            Integer lastChangeId = rs.getInt("lastChangeId");
            Instant runDate = rs.getInstant("runDate");
            boolean excludeBadPointQualities = rs.getEnum("excludeBadPointQualities", YNBoolean.class).getBoolean();
            
            Analysis analysis = new Analysis();
            analysis.setAnalysisId(analysisId);
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
    public int createNewAnalysis(BuiltInAttribute attribute, Duration intervalLength, boolean excludeBadPointQualities, Interval dateTimeRange) {
        int analysisId = insertIntoArchiveDataAnalysis(attribute, intervalLength, excludeBadPointQualities, dateTimeRange);
        insertSlots(analysisId, dateTimeRange, intervalLength);
        
        return analysisId;
    }
    
    @Override
    public void insertSlotValues(PaoIdentifier paoIdentifier, int analysisId, int pointId, boolean excludeBadPointQualities) {
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
        sql.append("      GROUP BY Timestamp");
        sql.append("    ) rph2 ON slot.StartTime = rph2.Timestamp");
        sql.append("  WHERE slot.AnalysisId").eq(analysisId);
        
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
        sql.append("SELECT AnalysisId, Attribute, StartDate, StopDate, IntervalLengthInMillis, LastChangeId, RunDate, ExcludeBadPointQualities");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        Analysis analysis = yukonJdbcTemplate.queryForObject(sql, analysisRowMapper);
        return analysis;
    }
    
    @Override
    public List<Analysis> getAllAnalyses() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT AnalysisId, Attribute, IntervalLengthInMillis, LastChangeId, RunDate, ExcludeBadPointQualities, StartDate, StopDate");
        sql.append("FROM ArchiveDataAnalysis");
        sql.append("ORDER BY RunDate DESC");
        
        List<Analysis> analyses = yukonJdbcTemplate.query(sql, analysisRowMapper);
        return analyses;
    }
    
    @Override
    public void deleteAnalysis(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ArchiveDataAnalysis");
        sql.append("WHERE AnalysisId").eq(analysisId);
        
        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int getNumberOfDevicesInAnalysis(int analysisId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(DISTINCT DeviceId)");
        sql.append("FROM ArchiveDataAnalysisSlotValue");
        sql.append("  LEFT JOIN ArchiveDataAnalysisSlot");
        sql.append("    ON ArchiveDataAnalysisSlotValue.SlotId = ArchiveDataAnalysisSlot.SlotId");
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
    
    private DeviceArchiveData getDeviceSlotValues(Analysis analysis, PaoIdentifier paoIdentifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Type, DeviceId, StartTime, ChangeId, AnalysisId");
        sql.append("FROM ArchiveDataAnalysisSlotValue slotValue");
        sql.append("  JOIN ArchiveDataAnalysisSlot slot ON slotValue.slotId = slot.slotId");
        sql.append("  JOIN YukonPAObject ypo ON ypo.paobjectId = slotValue.deviceId");
        sql.append("WHERE AnalysisId").eq(analysis.getAnalysisId());
        sql.append("AND DeviceId").eq(paoIdentifier.getPaoId());
        
        ArchiveDataRowMapper archiveDataRowMapper = new ArchiveDataRowMapper(analysis.getIntervalLength());
        List<ArchiveData> arsList = yukonJdbcTemplate.query(sql, archiveDataRowMapper);
        
        DeviceArchiveData data = new DeviceArchiveData();
        data.setAttribute(analysis.getAttribute());
        data.setArchiveRange(analysis.getDateTimeRange());
        data.setArchiveData(arsList);
        data.setId(paoIdentifier);
        
        return data;
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
            int slotId = nextValueHelper.getNextValue("ArchiveDataAnalysisSlot");
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink sink = sql.insertInto("ArchiveDataAnalysisSlot");
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
        Instant tempInstant = start.plus(interval); //first point of interest is one interval into the range
        while(tempInstant.isBefore(stop) || tempInstant.isEqual(stop)) {
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
}
