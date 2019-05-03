package com.cannontech.common.alert.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.alert.model.SimpleAlert;
import com.cannontech.common.alert.service.AlertClearHandler;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.TimeSource;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class AlertServiceImpl implements AlertService, MessageListener {
    private Logger log = YukonLogManager.getLogger(AlertServiceImpl.class);
    private GlobalSettingDao globalSettingDao;
    private TimeSource timeSource;
    private List<AlertClearHandler> alertClearHandlers = Collections.emptyList();
    private final Map<Integer,IdentifiableAlertImpl> map = 
        Collections.synchronizedMap(new LinkedHashMap<Integer,IdentifiableAlertImpl>());
    private Long maxAge = null;
    
    @PostConstruct
    public void initialize() {
        if (maxAge == null) {
            setupMaxAge();
        }
    }

    public void setupMaxAge() {
        maxAge = Long.MAX_VALUE;
        String hoursAsString = globalSettingDao.getString(GlobalSettingType.ALERT_TIMEOUT_HOURS);
        if (StringUtils.isBlank(hoursAsString)) {
            log.debug("ALERT_TIMEOUT_HOURS was \"" + hoursAsString + "\", interpreted as blank");
        } else {
            try {
                float hours = Float.valueOf(hoursAsString).floatValue();
                log.debug("ALERT_TIMEOUT_HOURS was \"" + hoursAsString + "\", successfully interpreted as " + hours);
                long milliseconds = (long)(hours * (60 * 60 * 1000));
                maxAge = milliseconds;
            } catch (NumberFormatException e) {
                log.debug("ALERT_TIMEOUT_HOURS was \"" + hoursAsString + "\", could not interpret as float");
            }
        }
        log.debug("maxAge = " + maxAge);
    }

    private void pruneOldEntries() {
        long minimumTime = timeSource.getCurrentMillis() - maxAge;
        Iterator<IdentifiableAlertImpl> iter = map.values().iterator();
        int count = 0;
        while (iter.hasNext() && iter.next().getAddedTimestamp() < minimumTime) {
            iter.remove();
            count++;
        }
        if (count > 0) {
            log.debug("pruned " + count + " alerts");
        }
    }
    
    @Override
    public synchronized void add(final Alert alert) {
        IdentifiableAlertImpl value = new IdentifiableAlertImpl(alert);
        value.setAddedTimestamp(timeSource.getCurrentMillis());
        log.debug("added alert: " + alert);
        Integer key = value.getId();
        map.put(key, value);
        pruneOldEntries();
    }

    @Override
    public synchronized Collection<IdentifiableAlert> getAll(final LiteYukonUser user) {
        List<IdentifiableAlertImpl> resultList = getFilterAlerts(user);
        ReverseList<IdentifiableAlert> reversedList = new ReverseList<IdentifiableAlert>(resultList);
        return reversedList;
    }

    private List<IdentifiableAlertImpl> getFilterAlerts(final LiteYukonUser user) {
        List<IdentifiableAlertImpl> resultList = new ArrayList<IdentifiableAlertImpl>();
        
        for (final IdentifiableAlertImpl alert : map.values()) {
            boolean checkPassed = alert.getUserCheck().check(user);
            if (checkPassed) resultList.add(alert);
        }
        return resultList;
    }

    @Override
    public synchronized int getCountForUser(final LiteYukonUser user) {
        int count = getFilterAlerts(user).size();
        return count;
    }
    
    @Override
    public synchronized long getLatestAlertTime(LiteYukonUser user) {
        List<IdentifiableAlertImpl> filterAlerts = getFilterAlerts(user);
        if (filterAlerts.isEmpty()) {
            return 0;
        } else {
            IdentifiableAlertImpl lastAlert = filterAlerts.get(filterAlerts.size() - 1);
            return lastAlert.getAddedTimestamp();
        }
    }

    @Override
    public synchronized void remove(final int[] alertIds, final LiteYukonUser user) {
        for (final Integer key : alertIds) {
            final IdentifiableAlertImpl alert = map.remove(key);
            log.debug("removed alert " + alert + " for " + user);
            
            if (alert == null) continue;
            
            final Alert unWrappedAlert = alert.getAlert();
            for (final AlertClearHandler handler: alertClearHandlers) {
                handler.clear(unWrappedAlert, user);
            }
        }
    }

    /** Implemented to receive alerts from service manager to web*/
    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof SimpleAlert) {
                    SimpleAlert simpleAlert = (SimpleAlert) object;
                    add(simpleAlert);
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }  
        }
    }

    public void setAlertClearHandlers(List<AlertClearHandler> alertClearHandlers) {
        this.alertClearHandlers = alertClearHandlers;
    }
    
    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }
    
    // Leaving this method old-style Autowiring so this setter can be used in a test.
    @Autowired
    public void setGlobalSettingsDao(GlobalSettingDao globalSettingDao) {
        this.globalSettingDao = globalSettingDao;
    }
    
    @Required
    public void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

}
