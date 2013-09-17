package com.cannontech.common.tdc.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.Cog;
import com.cannontech.common.tdc.model.ColumnTypeEnum;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayTypeEnum;
import com.cannontech.common.tdc.model.IDisplay;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.command.service.CommandService;
import com.cannontech.message.dispatch.command.service.impl.CommandServiceImpl;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class TdcServiceImpl implements TdcService, IDisplay{
   
    private Logger log = YukonLogManager.getLogger(CommandServiceImpl.class);
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private CommandService commandService;
    @Autowired private AlarmCatDao alarmCatDao;
    @Autowired private DisplayDao displayDao;
    
    private final YukonRowMapper<DisplayData> createCustomRowMapper =
        createCustomDisplayRowMapper();
    private final YukonRowMapper<DisplayData> createSoeRowMapper =
        createRowMapper(SOE_LOG_DISPLAY_NUMBER);
    private final YukonRowMapper<DisplayData> createTagRowMapper =
        createRowMapper(TAG_LOG_DISPLAY_NUMBER);
    private final YukonRowMapper<DisplayData> createEventViewerRowMapper =
        createRowMapper(EVENT_VIEWER_DISPLAY_NUMBER);
    
    Comparator<DisplayData> sortByDate = new Comparator<DisplayData>() {
        @Override
        public int compare(DisplayData d1, DisplayData d2) {
            return -d1.getDate().compareTo(d2.getDate());
        }
    };
    
    @Override
    public List<DisplayData> getDisplayData(Display display, DateTimeZone timeZone) {
        List<DisplayData> retVal = null;
        switch (display.getDisplayId()) {
        case SOE_LOG_DISPLAY_NUMBER:
            retVal = getSoeLogDisplayData(timeZone);
            break;
        case TAG_LOG_DISPLAY_NUMBER:
            retVal = getTagLogDisplayData(timeZone);
            break;
        case EVENT_VIEWER_DISPLAY_NUMBER:
            retVal = getEventViewerDisplayData(timeZone);
            break;
        case GLOBAL_ALARM_DISPLAY:
            retVal = getAlarms(true);
            break;
        default:
            if(display.getType() == DisplayTypeEnum.CUSTOM_DISPLAYS){
                retVal = getCustomDisplayData(display);
            }else if(display.getType() == DisplayTypeEnum.ALARMS_AND_EVENTS){
                retVal = getCustomDisplayDataByAlarmCategory(display);
            }
        }
        return retVal;
    }
        
    @Override
    public void sendPointData(int pointId, double value, LiteYukonUser user){

        PointData pd = dynamicDataSource.getPointData(pointId);
        PointData data  = new PointData();
        data.setId(pointId);
        data.setTags(pd.getTags());
        data.setTimeStamp(new java.util.Date());
        data.setTime(new java.util.Date());
        data.setType(pd.getType());
        data.setValue(value);
        data.setPointQuality(PointQuality.Manual);
        data.setStr("Manual change occurred from " + CtiUtilities.getUserName()
                  + " using TDC (Yukon)");
        data.setUserName(user.getUsername());
        dynamicDataSource.putValue(data);
    }
    
    @Override
    public void acknowledgeAlarm(int pointId, int condition, LiteYukonUser user){
       commandService.sendAcknowledgeAlarm(pointId, condition, user);
    }
    
    @Override
    public int acknowledgeAlarmsForPoint(final int pointId, LiteYukonUser user){
        List<DisplayData> activeAlarms =  getUnacknowledgedAlarms(pointId);
        for(DisplayData alarm : activeAlarms){
            acknowledgeAlarm(alarm.getPointId(), alarm.getCondition(), user);
        }
        return activeAlarms.size();
    }
        
    @Override
    public int acknowledgeAllAlarms(LiteYukonUser user){
        List<DisplayData> activeAlarms = getAlarms(false);
        for(DisplayData alarm :activeAlarms){
            acknowledgeAlarm(alarm.getPointId(), alarm.getCondition(), user);
        }
        return activeAlarms.size();
    }
    
    @Override
    public int acknowledgeAlarmsForDisplay(Display display, LiteYukonUser user){
        int count = getUnackAlarmCountForDisplay(display.getDisplayId());
        List<DisplayData> displayData = getDisplayData(display, null);
        for(DisplayData data :displayData){
            acknowledgeAlarmsForPoint(data.getPointId(), user);
        }
        return count;
    }

    @Override
    public String getPointState(int pointId) {
        int tags = dynamicDataSource.getTags(pointId);
        return TagUtils.getTagString(tags);
    }

    @Override
    public List<DisplayData> getAlarms(boolean showActive) {
        List<DisplayData> data = Lists.newArrayList();
        List<LiteAlarmCategory> alarmList = alarmCatDao.getAlarmCategories();
        for (LiteAlarmCategory alarmCat : alarmList) {
            Set<Signal> signals = dynamicDataSource.getSignalsByCategory(alarmCat.getAlarmStateID());
            data.addAll(getDisplayData(signals, showActive));
        }
        Collections.sort(data, sortByDate);
        return data;
    }
        
    @Override
    public List<DisplayData> getUnacknowledgedAlarms(int pointId) {
        Set<Signal> signals = dynamicDataSource.getSignals(pointId);
        return getDisplayData(signals, false);
    }

    @Override
    public int getUnackAlarmCountForPoint(int pointId) {
        int count = 0;
        Set<Signal> signals = dynamicDataSource.getSignals(pointId);
        for (Signal signal : signals) {
            if (TagUtils.isAlarmUnacked(signal.getTags()) && signal.getTags() != Signal.SIGNAL_COND) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public int getUnackAlarmCountForPoint(int pointId, int condition) {
        int count = 0;
        Set<Signal> signals = dynamicDataSource.getSignals(pointId);
        for (Signal signal : signals) {
            if (signal.getCondition() == condition && TagUtils.isAlarmUnacked(signal.getTags())
                && signal.getTags() != Signal.SIGNAL_COND) {
                count++;
            }
        }
        return count;
    }


    @Override
    public int getUnackAlarmCountForDisplay(int displayId) {
        Display display = displayDao.getDisplayById(displayId);
        int count = 0;
        List<DisplayData> displayData = getDisplayData(display, null);
        for(DisplayData data :displayData){
            // there might be more then one alarm for a point
            count += getUnackAlarmCountForPoint(data.getPointId());
        }
        return count;
    }

    @Override
    public int getUnackAlarmCount() {
        int count = 0;
        List<LiteAlarmCategory> alarmList = alarmCatDao.getAlarmCategories();
        for (LiteAlarmCategory alarmCat : alarmList) {
            Set<Signal> signals =
                dynamicDataSource.getSignalsByCategory(alarmCat.getAlarmStateID());
            for (Signal signal : signals) {
                if (TagUtils.isAlarmUnacked(signal.getTags())
                    && signal.getTags() != Signal.SIGNAL_COND) {
                    count++;
                }
            }
        }
        return count;
    }

    private List<DisplayData> getDisplayData(Set<Signal> signals, boolean showActive) {
        List<DisplayData> data = new ArrayList<>();
        for (Signal signal : signals) {
            if (signal.getCondition() != Signal.SIGNAL_COND) {
                int tags = signal.getTags();
                boolean displayAlarm;
                if (showActive) {
                    displayAlarm = TagUtils.isAlarmActive(tags);
                } else {
                    displayAlarm = TagUtils.isAlarmUnacked(tags);
                }
                if (displayAlarm) {
                    LitePoint litePoint = null;
                    try {
                        litePoint = pointDao.getLitePoint(signal.getPointID());
                    } catch (NotFoundException nfe) {
                        log.error("The point (pointId:" + signal.getPointID()
                                  + ") for this Alarm might have been deleted!", nfe);
                    }
                    if (litePoint != null) {
                        SimpleDevice device = deviceDao.getYukonDevice(litePoint.getPaobjectID());
                        LiteYukonPAObject liteYukonPAO =
                            paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
                        DisplayData alarm = new DisplayData();
                        alarm.setCog(new Cog());
                        alarm.setDevice(device);
                        alarm.setPointId(signal.getPointID());
                        alarm.setDeviceName(liteYukonPAO.getPaoName());
                        alarm.setPointName(litePoint.getPointName());
                        alarm.setDate(new Instant(signal.getTimeStamp()));
                        alarm.setPointName(litePoint.getPointName());
                        alarm.setTextMessage(signal.getDescription());
                        alarm.setCondition(signal.getCondition());
                        alarm.setAdditionalInfo(signal.getAction());
                        data.add(alarm);
                    }
                }
            }
        }
        Collections.sort(data, sortByDate);
        return data;
    }

    private List<DisplayData> getEventViewerDisplayData(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select s.datetime, y.PAOName, y.PAObjectID,  p.pointname, s.description, s.action, s.username, s.pointid, s.soe_tag");
        sql.append("from SystemLog s, YukonPAObject y, point p");
        sql.append("where s.pointid=p.pointid and y.PAObjectID=p.PAObjectID");
        sql.append("and s.datetime").gte(from);
        sql.append("and s.datetime").lt(to);
        sql.append("order by s.datetime desc, s.soe_tag");
        List<DisplayData> displayData = yukonJdbcTemplate.query(sql, createEventViewerRowMapper);
        return displayData;
    }
    
    @Override
    public boolean isManualControlEnabled(int pointId){
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        if(pointValue.getPointType() == PointType.Analog || pointValue.getPointType() == PointType.Status){
            int tags = dynamicDataSource.getTags(pointId);
            return TagUtils.isControllablePoint(tags) && TagUtils.isControlEnabled(tags);
        }
        return false;
    }
    
    private List<DisplayData> getCustomDisplayData(Display display) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select d.pointid, d.deviceid, d.pointtype, d.pointname, d.devicename, d.pointstate, d.devicecurrentstate");
        sql.append("from display2waydata_view d join display2waydata y on d.pointid = y.pointid");
        sql.append("where y.displaynum").eq(display.getDisplayId());
        sql.append("and d.pointid > 0 order by y.ordering");
        List<DisplayData> displayData = yukonJdbcTemplate.query(sql,  createCustomRowMapper);
        for (final DisplayData data : displayData) {
            List<DisplayData> unackAlarms =  getUnacknowledgedAlarms(data.getPointId());
            int unackCount = getUnacknowledgedAlarms(data.getPointId()).size();
            if(unackCount == 1){
                // condition needed to acknowledge the alarm
                data.setCondition(unackAlarms.get(0).getCondition());
                data.setPointName(unackAlarms.get(0).getPointName());
            }
            Cog cog = new Cog();
            data.setCog(cog);
            // status points are not supported by the flot tag
            cog.setTrend(data.getPointType() != PointType.Status);
            boolean isValidTypeForManualEntry =
                data.getPointType() == PointType.Analog
                        || data.getPointType() == PointType.PulseAccumulator
                        || data.getPointType() == PointType.DemandAccumulator
                        || data.getPointType() == PointType.CalcAnalog
                        || data.getPointType() == PointType.Status
                        || data.getPointType() == PointType.CalcStatus;
            PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(data.getPointId());
            cog.setManualEntry(display.hasColumn(ColumnTypeEnum.POINT_VALUE)
                               && isValidTypeForManualEntry
                               && pointValue.getPointQuality() != PointQuality.Constant);
            cog.setAltScan(DeviceTypesFuncs.hasDeviceScanRate(data.getDevice().getType()));
            cog.setEnableDisable(true);
            cog.setTags(true);
        }
        return displayData;
    }
       
    private List<DisplayData> getCustomDisplayDataByAlarmCategory(Display display) {
        int catId = alarmCatDao.getAlarmCategoryId(display.getName());
        Set<Signal> signals = dynamicDataSource.getSignalsByCategory(catId);
        return getDisplayData(signals, true);
    }
    
    private List<DisplayData> getSoeLogDisplayData(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select s.datetime, y.PAOName, y.PAObjectID, p.pointname, p.pointid, s.description, s.action, s.pointid, s.millis");
        sql.append("from SystemLog s, YukonPAObject y, point p");
        sql.append("where p.LogicalGroup").eq(PointLogicalGroups.LGRP_STRS[PointLogicalGroups.LGRP_SOE]);
        sql.append("and s.pointid=p.pointid and y.PAObjectID=p.PAObjectID");
        sql.append("and s.datetime").gte(from);
        sql.append("and s.datetime").lt(to);
        sql.append("order by s.datetime, s.millis");
        List<DisplayData> displayData = yukonJdbcTemplate.query(sql, createSoeRowMapper);
        return displayData;
    }

    public List<DisplayData> getTagLogDisplayData(DateTimeZone timeZone) {
        DateTime from = new DateTime(timeZone).toDateMidnight().toDateTime();
        DateTime to = from.plusDays(1);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select s.tagtime, y.PAOName, y.PAObjectID, p.pointname, p.pointid, s.description, s.action, s.username, t.tagname");
        sql.append("from TagLog s, YukonPAObject y, point p, tags t");
        sql.append("where s.pointid=p.pointid and y.PAObjectID=p.PAObjectID");
        sql.append("and t.tagid = s.tagid");
        sql.append("and s.tagtime").gte(from);
        sql.append("and s.tagtime").lt(to);
        sql.append("order by s.tagtime, s.tagid");
        List<DisplayData> displayData = yukonJdbcTemplate.query(sql, createTagRowMapper);
        return displayData;
    }
    
    private YukonRowMapper<DisplayData> createCustomDisplayRowMapper() {
        final YukonRowMapper<DisplayData> mapper = new YukonRowMapper<DisplayData>() {
            @Override
            public DisplayData mapRow(YukonResultSet rs) throws SQLException {
                DisplayData row = new DisplayData();
                row.setDeviceCurrentState(rs.getStringSafe("devicecurrentstate"));
                SimpleDevice device = deviceDao.getYukonDevice(rs.getInt("deviceid"));
                row.setDevice(device);
                row.setPointId(rs.getInt("pointid"));
                row.setPointEnabled(!rs.getBoolean("pointstate"));
                row.setPointType(rs.getEnum("pointtype", PointType.class));
                row.setPointName(rs.getString("pointname"));
                row.setDeviceName(rs.getString("devicename"));
                return row;
            }
        };
        return mapper;
    }

    private YukonRowMapper<DisplayData> createRowMapper(final Integer displayId) {
        final YukonRowMapper<DisplayData> mapper = new YukonRowMapper<DisplayData>() {
            @Override
            public DisplayData mapRow(YukonResultSet rs) throws SQLException {
                DisplayData row = new DisplayData();
                SimpleDevice device = deviceDao.getYukonDevice(rs.getInt("PAObjectID"));
                row.setDevice(device);
                row.setDeviceName(rs.getString("PAOName"));
                row.setPointName(rs.getString("pointname"));
                row.setDescription(rs.getString("description"));
                row.setAdditionalInfo(rs.getString("action"));
                row.setPointId(rs.getInt("pointid"));
                if (displayId == EVENT_VIEWER_DISPLAY_NUMBER) {
                    row.setUserName(rs.getString("username"));
                    row.setDate(rs.getInstant("datetime"));
                    row.setTextMessage(rs.getString("description"));
                }
                else if (displayId == TAG_LOG_DISPLAY_NUMBER) {
                    row.setUserName(rs.getString("username"));
                    row.setTagName(rs.getString("tagname"));
                    row.setDate(rs.getInstant("tagtime"));
                }
                else if (displayId == SOE_LOG_DISPLAY_NUMBER) {
                    row.setDate(rs.getInstant("datetime"));
                }
                return row;
            }
        };
        return mapper;
    }
}
