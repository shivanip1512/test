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
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.dao.DisplayDataDao;
import com.cannontech.common.tdc.model.Cog;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayTypeEnum;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class TdcServiceImpl implements TdcService{

    private Logger log = YukonLogManager.getLogger(TdcServiceImpl.class);
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private CommandService commandService;
    @Autowired private AlarmCatDao alarmCatDao;
    @Autowired private DisplayDao displayDao;
    @Autowired private StateDao stateDao;
    @Autowired private DisplayDataDao displayDataDao;
    
    private  Map<Integer, String> stateColorMap;
        
    private Comparator<DisplayData> sortByDate = new Comparator<DisplayData>() {
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
            retVal = displayDataDao.getSoeLogDisplayData(timeZone);
            break;
        case TAG_LOG_DISPLAY_NUMBER:
            retVal = displayDataDao.getTagLogDisplayData(timeZone);
            break;
        case EVENT_VIEWER_DISPLAY_NUMBER:
            retVal = displayDataDao.getEventViewerDisplayData(timeZone);
            break;
        case GLOBAL_ALARM_DISPLAY:
            retVal = getAlarms(true);
            break;
        default:
            if(display.getType() == DisplayTypeEnum.CUSTOM_DISPLAYS){
                retVal = displayDataDao.getCustomDisplayData(display,getAlarms(false));
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
            if (signal.getCondition() != -1 && TagUtils.isAlarmUnacked(signal.getTags())
                && signal.getTags() != Signal.SIGNAL_COND) {
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
    public String getUnackAlarmColorStateBox(int pointId, int condition) {
        String color = "";
        if (getUnackAlarmCountForPoint(pointId) > 0) {
            if (condition == 0) {
                color = getUnackAlarmColorForPoint(pointId);
            } else {
                color = getUnackAlarmColorForPoint(pointId, condition);
            }
        }
        return color;
    }
    
    private String getUnackAlarmColorForPoint(int pointId, int condition) {
        Set<Signal> signals = dynamicDataSource.getSignals(pointId);
        for (Signal signal : signals) {
            if (signal.getCondition() == condition
                && (TagUtils.isAlarmUnacked(signal.getTags()) || TagUtils.isAlarmActive(signal
                    .getTags()))
                && signal.getTags() != Signal.SIGNAL_COND) {
                String color = stateColorMap.get((int) signal.getCategoryID() - 1);
                return getColorClasses(color, signal.getTags());
            }
        }
        return "";
    }

    private String getColorClasses(String color, int tags){
        if (!color.isEmpty()) {
            StringBuilder classes = new StringBuilder();
            if(TagUtils.isAlarmUnacked(tags)){
                classes.append("pulse box");
            }
            classes.append(" stateBox ");
            classes.append(color.toLowerCase());
            return classes.toString();
        }
        return "";
    }
    
    private String getUnackAlarmColorForPoint(int pointId) {
        List<Signal> signals = Lists.newArrayList(dynamicDataSource.getSignals(pointId));
        //latest alarm 
        Collections.sort(signals, new Comparator<Signal>() {
            @Override public int compare(Signal o1, Signal o2) {
                return -o1.getTimeStamp().compareTo(o2.getTimeStamp());
            }
        });
        for (Signal signal : signals) {
            if ((TagUtils.isAlarmUnacked(signal.getTags()) || TagUtils.isAlarmActive(signal
                .getTags()))
                && signal.getTags() != Signal.SIGNAL_COND) {
                String color = stateColorMap.get((int) signal.getCategoryID() - 1);
                return getColorClasses(color, signal.getTags());
            }
        }
        return "";
    }
    
    @Override
    public Map<Integer, Map<Integer, String>> getUnackAlarmColorStateBoxes(List<DisplayData> displayData) {
        Map<Integer, Map<Integer, String>> alarmColors = new  HashMap<Integer, Map<Integer, String>>();
        for(DisplayData data: displayData){
            Map<Integer, String> mapByCondition = alarmColors.get(data.getPointId());
            if(mapByCondition == null){
                mapByCondition = new HashMap<Integer, String>();
            }
            String color = "";
            if(getUnackAlarmCountForPoint(data.getPointId()) > 0){
               color = getUnackAlarmColorStateBox(data.getPointId(), data.getCondition());
            }
            mapByCondition.put(data.getCondition(),  color);
            alarmColors.put(data.getPointId(), mapByCondition);
        }
        return alarmColors;
    }

    @Override
    public int getUnackAlarmCountForDisplay(int displayId) {
        Display display = displayDao.getDisplayById(displayId);
        int count = 0;
        List<DisplayData> displayData = getDisplayData(display, null);
        for(DisplayData data :displayData){
            // there might be more then one alarm for a point
            count += getUnackAlarmCountForPoint(data.getPointId(), data.getCondition());
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
                    displayAlarm = TagUtils.isAlarmActive(tags) || TagUtils.isAlarmUnacked(tags);
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

    @Override
    public boolean isManualControlEnabled(int pointId){
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        if(pointValue.getPointType() == PointType.Analog || pointValue.getPointType() == PointType.Status){
            int tags = dynamicDataSource.getTags(pointId);
            return TagUtils.isControllablePoint(tags) && TagUtils.isControlEnabled(tags);
        }
        return false;
    }
       
    private List<DisplayData> getCustomDisplayDataByAlarmCategory(Display display) {
        int catId = alarmCatDao.getAlarmCategoryId(display.getName());
        Set<Signal> signals = dynamicDataSource.getSignalsByCategory(catId);
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
                colorString = "alert"; // yukon.css 
            } else {
                colorString = Colors.getColorString(fgColor);
            }
            stateColorMap.put(state.getStateRawState(), colorString);
        }
    }
}
