package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;

public class ControlHistoryEventDaoImpl implements ControlHistoryEventDao {
    private ProgramDao programDao;
    private ApplianceDao applianceDao;
    private InventoryBaseDao inventoryBaseDao;

    @Override
    public Map<String, List<ControlHistoryEvent>> getEventsByProgram(final int customerAccountId, final int programId,
                                                                     final ControlPeriod period, final YukonUserContext yukonUserContext) {
        // Any appliance that has a groupId that does *not* belong in supportedGroupIdList will not used.
        final List<Integer> supportedGroupIdList = programDao.getGroupIdsByProgramId(programId);
        final List<Appliance> applianceList = applianceDao.getByAccountId(customerAccountId);

        class Holder {
            int groupId;
            int inventoryId;
        }

        final List<Holder> holderList = new ArrayList<Holder>();
        final Set<Integer> inventoryIds = new HashSet<Integer>();
        
        for (Appliance appliance : applianceList) {
            int applianceProgramId = appliance.getProgramId();
            if (applianceProgramId != programId) continue;

            Integer applianceGroupId = appliance.getGroupdId();
            if (!supportedGroupIdList.contains(applianceGroupId)) continue;

            Integer inventoryId = appliance.getInventoryId();
            inventoryIds.add(inventoryId);

            Holder holder = new Holder();
            holder.groupId = applianceGroupId;
            holder.inventoryId = inventoryId;
            holderList.add(holder);
        }

        final Map<Integer, InventoryBase> inventoryMap = inventoryBaseDao.getByIds(new ArrayList<Integer>(inventoryIds));
        
        final Map<String, List<ControlHistoryEvent>> displayNameToEventsMap = 
            new HashMap<String, List<ControlHistoryEvent>>(holderList.size());
        
        for (final Holder holder : holderList) {
            InventoryBase inventoryBase = inventoryMap.get(holder.inventoryId);
            String displayName = inventoryBaseDao.getDisplayName(inventoryBase);
            
            List<ControlHistoryEvent> eventList = displayNameToEventsMap.get(displayName);
            if (eventList == null) {
                eventList = new ArrayList<ControlHistoryEvent>();
                displayNameToEventsMap.put(displayName, eventList);
            }
            
            List<ControlHistoryEvent> eventsByGroupList = getEventsByGroup(customerAccountId,
                                                                           holder.groupId,
                                                                           period,
                                                                           yukonUserContext);
            eventList.addAll(eventsByGroupList);
        }

        return displayNameToEventsMap;
    }

    private List<ControlHistoryEvent> getEventsByGroup(final int customerAccountId, final int groupId,
                                                       final ControlPeriod period, final YukonUserContext yukonUserContext) {

        StarsCtrlHistPeriod starsControlPeriod = StarsCtrlHistPeriod.valueOf(period.starsName());

        StarsLMControlHistory controlHistory = LMControlHistoryUtil.getStarsLMControlHistory(groupId,
                                                                                             customerAccountId,
                                                                                             starsControlPeriod,
                                                                                             yukonUserContext.getTimeZone(),
                                                                                             yukonUserContext.getYukonUser());
        List<ControlHistoryEvent> eventList = toEventList(controlHistory);
        return eventList;
    }

    @Override
    public List<ControlHistoryEvent> toEventList(StarsLMControlHistory controlHistory) {
        if (controlHistory == null) return Collections.emptyList();

        final List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>();

        for (int j = 0; j < controlHistory.getControlHistoryCount(); j++) {
            com.cannontech.stars.xml.serialize.ControlHistory history = controlHistory.getControlHistory(j);

            Date startDate = history.getStartDateTime();
            int durationInSeconds = history.getControlDuration();
            Date endDate = DateUtils.addSeconds(startDate, durationInSeconds);
            
            final ControlHistoryEvent event = new ControlHistoryEvent();
            event.setDuration(durationInSeconds);
            event.setStartDate(startDate);
            event.setEndDate(endDate);
            eventList.add(event);
        }  

        return eventList;
    }

    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }

}
