package com.cannontech.common.tdc.service.impl;

import static com.cannontech.common.tdc.model.IDisplay.EVENT_VIEWER_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.GLOBAL_ALARM_DISPLAY;
import static com.cannontech.common.tdc.model.IDisplay.SOE_LOG_DISPLAY_NUMBER;
import static com.cannontech.common.tdc.model.IDisplay.TAG_LOG_DISPLAY_NUMBER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.dao.DisplayDataDao;
import com.cannontech.common.tdc.model.Cog;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.message.dispatch.command.service.CommandService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class TdcServiceImpl implements TdcService {
    private final static Logger log = YukonLogManager.getLogger(TdcServiceImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private CommandService commandService;
    @Autowired private AlarmCatDao alarmCatDao;
    @Autowired private DisplayDao displayDao;
    @Autowired private StateDao stateDao;
    @Autowired private DisplayDataDao displayDataDao;

    private Map<Integer, String> stateColorMap;
    private final String defaultAlertStr = "alert"; // yukon.css 
    
    private Comparator<DisplayData> sortByDate = new Comparator<DisplayData>() {
        @Override
        public int compare(DisplayData d1, DisplayData d2) {
            return -d1.getDate().compareTo(d2.getDate());
        }
    };

    @Override
    public List<DisplayData> getDisplayData(Display display, DateTimeZone timeZone, PagingParameters paging) {
        List<DisplayData> retVal = null;
        switch (display.getDisplayId()) {
        case SOE_LOG_DISPLAY_NUMBER:
            retVal = displayDataDao.getSoeLogDisplayData(timeZone, paging);
            break;
        case TAG_LOG_DISPLAY_NUMBER:
            retVal = displayDataDao.getTagLogDisplayData(timeZone, paging);
            break;
        case EVENT_VIEWER_DISPLAY_NUMBER:
            retVal = displayDataDao.getEventViewerDisplayData(timeZone, paging);
            break;
        case GLOBAL_ALARM_DISPLAY:
            retVal = getAlarms(true);
            break;
        default:
            if(display.getType() == DisplayType.CUSTOM_DISPLAYS){
                retVal = displayDataDao.getCustomDisplayData(display);
            }else if(display.getType() == DisplayType.ALARMS_AND_EVENTS){
                retVal = getCustomDisplayDataByAlarmCategory(display);
            }
        }
        return retVal;
    }
    
    @Override
    public int getDisplayDataCount(int displayId, DateTimeZone timeZone) {
        int count;
        switch (displayId) {
        case SOE_LOG_DISPLAY_NUMBER:
            count = displayDataDao.getSoeLogDisplayDataCount(timeZone);
            break;
        case TAG_LOG_DISPLAY_NUMBER:
            count = displayDataDao.getTagLogDisplayDataCount(timeZone);
            break;
        case EVENT_VIEWER_DISPLAY_NUMBER:
            count = displayDataDao.getEventViewerDisplayDataCount(timeZone);
            break;
        default:
            throw new UnsupportedOperationException();
        }
        return count;
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
    public int acknowledgeAlarmsForPoint(int pointId, LiteYukonUser user){
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
        int count  = 0;
        List<DisplayData> displayData = getDisplayData(display, null, null);
        for(DisplayData data :displayData){
            count += acknowledgeAlarmsForPoint(data.getPointId(), user);
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
            Set<Signal> signals = dynamicDataSource.getCachedSignalsByCategory(alarmCat.getAlarmCategoryId());
            data.addAll(getDisplayData(signals, showActive));
        }
        Collections.sort(data, sortByDate);
        return data;
    }
        
    @Override
    public List<DisplayData> getUnacknowledgedAlarms(int pointId) {
        Set<Signal> signals = dynamicDataSource.getCachedSignals(pointId);
        return getDisplayData(signals, false);
    }

    @Override
    public int getUnackAlarmCountForPoint(int pointId) {
        Set<Signal> signals = dynamicDataSource.getCachedSignals(pointId);
        List<Signal> unackAlarms = Lists.newArrayList(Iterables.filter(signals, new Predicate<Signal>() {
            @Override
            public boolean apply(Signal signal) {
                return signal.getCondition() != -1 && TagUtils.isAlarmUnacked(signal.getTags())
                        && signal.getCondition() != Signal.SIGNAL_COND;
            }
        }));        
        return Iterables.size(unackAlarms);
    }
    
    @Override
    public int getUnackAlarmCountForPoint(int pointId, int condition) {
        Set<Signal> signals = dynamicDataSource.getCachedSignals(pointId);
        for(Signal signal: signals){
            if(signal.getCondition() == condition
                    && TagUtils.isAlarmUnacked(signal.getTags())
                    && signal.getCondition() != Signal.SIGNAL_COND){
                return 1;
            }
        }
        return 0;
    }
         
    @Override
    public String getUnackOrActiveAlarmColor(int pointId, final int condition) {
        Set<Signal> signals = dynamicDataSource.getCachedSignals(pointId);
        for(Signal signal: signals){
            if(signal.getCondition() == condition){
                return getColorClasses(signal);
            }
        }
        return "";
    }

    @Override
    public String getUnackOrActiveAlarmColor(int pointId) {
        List<Signal> signals = Lists.newArrayList(dynamicDataSource.getCachedSignals(pointId));
        List<Signal> alarms =
            Lists.newArrayList(Iterables.filter(signals, new Predicate<Signal>() {
                @Override
                public boolean apply(Signal signal) {
                    return TagUtils.isAlarmUnacked(signal.getTags())
                           && signal.getCondition() != Signal.SIGNAL_COND;
                }
            }));

        if (alarms.isEmpty()) {
            alarms =
                Lists.newArrayList(Iterables.filter(signals, new Predicate<Signal>() {
                    @Override
                    public boolean apply(Signal signal) {
                        return TagUtils.isAlarmActive(signal.getTags())
                               && signal.getCondition() != Signal.SIGNAL_COND;
                    }
                }));
        }
        // latest alarm
        Collections.sort(alarms, new Comparator<Signal>() {
            @Override
            public int compare(Signal o1, Signal o2) {
                return o1.getTimeStamp().compareTo(o2.getTimeStamp());
            }
        });
        if (alarms.isEmpty()) {
            return "";
        }
        Signal signal = alarms.get(alarms.size() - 1);
        return getColorClasses(signal);
    }
    
    @Override
    public Map<Integer, Map<Integer, String>> getUnackAlarmColorStateBoxes(Display display, List<DisplayData> displayData) {
        Map<Integer, Map<Integer, String>> alarmColors =
            new HashMap<Integer, Map<Integer, String>>();
        for (DisplayData data : displayData) {
            Map<Integer, String> mapByCondition = alarmColors.get(data.getPointId());
            if (mapByCondition == null) {
                mapByCondition = new HashMap<Integer, String>();
            }
            String color = "";
            if (display == null) {
                color = getUnackOrActiveAlarmColor(data.getPointId(), data.getCondition());
            }
            else if (display.getType() == DisplayType.CUSTOM_DISPLAYS) {
                color = getUnackOrActiveAlarmColor(data.getPointId());
            }
            else if (display.isAcknowledgable()) {
                color = getUnackOrActiveAlarmColor(data.getPointId(), data.getCondition());
            }

            mapByCondition.put(data.getCondition(), color);
            alarmColors.put(data.getPointId(), mapByCondition);
        }
        return alarmColors;
    }

    private String getColorClasses(Signal signal) {
        if ((TagUtils.isAlarmUnacked(signal.getTags()) || TagUtils.isAlarmActive(signal
            .getTags()))
            && signal.getCondition() != Signal.SIGNAL_COND) {
            String color = stateColorMap.get((int) signal.getCategoryID() - 1);
            if ("".equals(color)) {
                return "";
            }
            StringBuilder classes = new StringBuilder();
            classes.append("state-box");
            classes.append(" ");
            classes.append(color == null ? defaultAlertStr : color.toLowerCase());
            if (TagUtils.isAlarmUnacked(signal.getTags())) {
                classes.append(" blink-shadow");
            }
            return classes.toString();
        }
        return "";
    }

    @Override
    public int getUnackAlarmCountForDisplay(int displayId) {
        Display display = displayDao.getDisplayById(displayId);
        int count = 0;
        List<DisplayData> displayData = getDisplayData(display, null, null);
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
            Set<Signal> signals = dynamicDataSource.getCachedSignalsByCategory(alarmCat.getAlarmCategoryId());
            for (Signal signal : signals) {
                if (TagUtils.isAlarmUnacked(signal.getTags()) && signal.getCondition() != Signal.SIGNAL_COND) {
                    count++;
                }
            }
        }
        return count;
    }

    private List<DisplayData> getDisplayData(Set<Signal> signals, boolean showActive) {
        List<DisplayData> data = new ArrayList<>();
        if (signals.isEmpty()) {
            return data;
        }
        List<Integer> pointIds =
            Lists.transform(Lists.newArrayList(signals), new Function<Signal, Integer>() {
                @Override
                public Integer apply(Signal signal) {
                    return signal.getPointID();
                }
            });

        List<LitePoint> points = pointDao.getLitePoints(pointIds);
        Map<Integer, LitePoint> pointMap =
            Maps.uniqueIndex(points, new Function<LitePoint, Integer>() {
                @Override
                public Integer apply(LitePoint p) {
                    return p.getPointID();
                }
            });

        List<Integer> deviceIds =
            Lists.transform(points, new Function<LitePoint, Integer>() {
                @Override
                public Integer apply(LitePoint point) {
                    return point.getPaobjectID();
                }
            });
        
        deviceIds = ImmutableSet.copyOf(deviceIds).asList();
        List<SimpleDevice> devices = deviceDao.getYukonDeviceObjectByIds(deviceIds);
        Map<Integer, SimpleDevice> deviceMap =
            Maps.uniqueIndex(devices, new Function<SimpleDevice, Integer>() {
                @Override
                public Integer apply(SimpleDevice device) {
                    return device.getDeviceId();
                }
            });

        Map<PaoIdentifier, LiteYukonPAObject> paoIdentifiers =
            paoDao.getLiteYukonPaosById(Lists.transform(devices, SimpleDevice.TO_PAO_IDENTIFIER));

        for (Signal signal : signals) {
            if (signal.getCondition() != Signal.SIGNAL_COND) {
                int tags = signal.getTags();
                boolean displayAlarm;
                if (showActive) {
                    displayAlarm = TagUtils.isAlarmActive(tags) || TagUtils.isAlarmUnacked(tags);
                } else {
                    displayAlarm = TagUtils.isAlarmUnacked(tags);
                }
                if (displayAlarm) {
                    LitePoint litePoint = null;
                    try {
                        litePoint = pointMap.get(signal.getPointID());
                    } catch (NotFoundException nfe) {
                        log.error("The point (pointId:" + signal.getPointID()
                                  + ") for this Alarm might have been deleted!", nfe);
                    }
                    if (litePoint != null) {
                        SimpleDevice device = deviceMap.get(litePoint.getPaobjectID());
                        LiteYukonPAObject liteYukonPAO =
                            paoIdentifiers.get(device.getPaoIdentifier());
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

    @Override
    public boolean isManualControlEnabled(int pointId){
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        if(pointValue.getPointType() == PointType.Analog || pointValue.getPointType() == PointType.Status){
            int tags = dynamicDataSource.getTags(pointId);
            return TagUtils.isControllablePoint(tags) && TagUtils.isControlEnabled(tags);
        }
        return false;
    }
    
    @Override
    public boolean isManualEntryEnabled(int pointId, int pointTypeId, boolean hasPointValueColumn){
        if(!hasPointValueColumn){
            return false;
        }
        int tags = dynamicDataSource.getTags(pointId);
        PointType type = PointType.getForId(pointTypeId);
        boolean inService = !TagUtils.isDeviceOutOfService(tags) && !TagUtils.isPointOutOfService(tags);
        boolean isValidTypeForManualEntry = inService &&
                (type == PointType.Analog
                    || type == PointType.PulseAccumulator
                    || type == PointType.DemandAccumulator
                    || type == PointType.CalcAnalog
                    || type == PointType.Status
                    || type == PointType.CalcStatus);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        return isValidTypeForManualEntry && pointValue.getPointQuality() != PointQuality.Constant;
    }
       
    private List<DisplayData> getCustomDisplayDataByAlarmCategory(Display display) {
        int catId = alarmCatDao.getAlarmCategoryIdFromCache(display.getName());
        Set<Signal> signals = dynamicDataSource.getCachedSignalsByCategory(catId);
        return getDisplayData(signals, true);
    }
       
    @PostConstruct
    public void init() {
        LiteStateGroup states = stateDao.getLiteStateGroup(StateGroupUtils.STATEGROUP_ALARM);
        stateColorMap = Maps.newHashMap();
        for (LiteState state : states.getStatesList()) {
            String colorString;
            int fgColor = state.getFgColor();
            if (fgColor == Colors.RED_ID) {
                colorString = defaultAlertStr;
            } else {
                colorString = Colors.getColorString(fgColor);
            }
            stateColorMap.put(state.getStateRawState(), colorString);
        }
    }
}
