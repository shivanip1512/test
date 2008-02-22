package com.cannontech.common.alert.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.model.IdentifiableAlert;
import com.cannontech.common.alert.service.AlertClearHandler;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.util.ReverseList;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.ConfigurationRole;

public class AlertServiceImpl implements AlertService{
    private Logger log = YukonLogManager.getLogger(AlertServiceImpl.class);
    private RoleDao roleDao;
    private List<AlertClearHandler> alertClearHandlers = Collections.emptyList();
    private final Map<Integer,IdentifiableAlertImpl> map = 
        Collections.synchronizedMap(new LinkedHashMap<Integer,IdentifiableAlertImpl>());
    private Long maxAge = null;
    
    public void initialize() {
        if (maxAge == null) {
            setupMaxAge();
        }
    }

    public void setupMaxAge() {
        maxAge = Long.MAX_VALUE;
        String hoursAsString = roleDao.getGlobalPropertyValue(ConfigurationRole.ALERT_TIMEOUT_HOURS);
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
        long minimumTime = System.currentTimeMillis() - maxAge;
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
        value.setAddedTimestamp(System.currentTimeMillis());
        log.debug("added alert: " + alert);
        Integer key = value.getId();
        map.put(key, value);
        pruneOldEntries();
    }

    @Override
    public synchronized Collection<IdentifiableAlert> getAll(final LiteYukonUser user) {
        List<IdentifiableAlert> resultList = getFilterAlerts(user);
        resultList = new ReverseList<IdentifiableAlert>(resultList);
        return resultList;
    }

    private List<IdentifiableAlert> getFilterAlerts(final LiteYukonUser user) {
        List<IdentifiableAlert> resultList = new ArrayList<IdentifiableAlert>();
        
        for (final IdentifiableAlert alert : map.values()) {
            boolean checkPassed = alert.getUserCheck().check(user);
            if (checkPassed) resultList.add(alert);
        }
        return resultList;
    }

    @Override
    public synchronized int getCountForUser(final LiteYukonUser user) {
        int count = getAll(user).size();
        return count;
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

    public void setAlertClearHandlers(List<AlertClearHandler> alertClearHandlers) {
        this.alertClearHandlers = alertClearHandlers;
    }
    
    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

}
