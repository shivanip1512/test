package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.dao.impl.ControlHistoryEventDaoImpl.Holder;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.controlhistory.model.ControlHistorySummary;
import com.cannontech.stars.dr.controlhistory.model.ControlPeriod;
import com.cannontech.stars.dr.controlhistory.service.ControlHistorySummaryService;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.dao.impl.LMHardwareControlGroupDaoImpl.DistinctEnrollment;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.util.ControlGroupUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class ControlHistoryDaoImpl implements ControlHistoryDao {
    private ControlHistoryEventDao controlHistoryEventDao;
    private ControlHistorySummaryService controlHistorySummaryService;
    private InventoryBaseDao inventoryBaseDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    
    @Override
    public ListMultimap<Integer, ControlHistory> getControlHistory(int accountId, YukonUserContext userContext,
                                                                   ControlPeriod controlPeriod, boolean past) {
 
        ListMultimap<Integer, ControlHistory> programControlHistoryMultimap = ArrayListMultimap.create();
        List<DistinctEnrollment> enrollments = lmHardwareControlGroupDao.getDistinctEnrollments(accountId, past);
        
        Set<Holder> holderList = Sets.newHashSet(); 
        for (DistinctEnrollment enrollment : enrollments) {
            
            // Any groupId that does *not* belong in supportedGroupIdList will not used.
            final List<Integer> supportedGroups = Collections.singletonList(enrollment.getGroupId());
            generateHolderListAndBuildInventoryIds(holderList, supportedGroups, enrollments, enrollment.getInventoryId());
        } 
        
        for (final Holder holder : holderList) {
            InventoryBase inventory = inventoryBaseDao.findById(holder.inventoryId);

            ControlHistory controlHistory = new ControlHistory();

            StarsLMControlHistory starsLMControlHistory = 
                controlHistoryEventDao.getEventsByGroup(accountId, holder.groupId, holder.inventoryId, controlPeriod, userContext, past);
            List<ControlHistoryEvent> controlHistoryEventList = 
                controlHistoryEventDao.toEventList(holder.programId, starsLMControlHistory, userContext);
            controlHistory.setCurrentHistory(controlHistoryEventList);
            
            if (inventory != null) {
                controlHistory.setInventory(inventory);
                controlHistory.setDisplayName(inventoryBaseDao.getDisplayName(inventory));
            }

            ControlHistorySummary programControlHistorySummary = new ControlHistorySummary(starsLMControlHistory);
            controlHistory.setProgramControlHistorySummary(programControlHistorySummary);
            
            ControlHistorySummary controlHistorySummary = controlHistorySummaryService.getControlSummary(accountId, holder.inventoryId, holder.groupId, userContext, past);
            controlHistory.setControlHistorySummary(controlHistorySummary);

            ControlHistoryEvent lastControlHistoryEvent = controlHistoryEventDao.getLastControlHistoryEntry(accountId, holder.programId, holder.inventoryId, userContext, past);
            controlHistory.setLastControlHistoryEvent(lastControlHistoryEvent);

            ControlHistoryStatus controlHistoryStatus = getCurrentControlStatus(holder, lastControlHistoryEvent, userContext, past);
            controlHistory.setCurrentStatus(controlHistoryStatus);
            
            programControlHistoryMultimap.put(holder.programId, controlHistory);
        }

        return programControlHistoryMultimap;
        
    }

    private ControlHistoryStatus getCurrentControlStatus(Holder holder,
                                                         ControlHistoryEvent lastControlHistoryEvent,
                                                         YukonUserContext userContext, 
                                                         boolean past) {
        final LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        final Instant now = new Instant();
        
        LMHardwareControlGroup enrollment = 
            lmHardwareControlGroupDao.findCurrentEnrollmentByInventoryIdAndProgramIdAndAccountId(holder.inventoryId, holder.programId, holder.accountId);
        
        List<LMHardwareControlGroup> optOuts = 
            lmHardwareControlGroupDao.getByInventoryIdAndAccountIdAndType(holder.inventoryId, holder.accountId, LMHardwareControlGroup.OPT_OUT_ENTRY);
        
        // Step 1 - NOT ENROLLED
        if (enrollment == null || !enrollment.isActiveEnrollment() || past) {
            return ControlHistoryStatus.NOT_ENROLLED;
        }
        
        // Step 2 - Virtual Program
        if (enrollment.getLmGroupId() == 0) {
            return ControlHistoryStatus.VIRTUALLY_ENROLLED;
        }
        
        // Step 3 - OPTED OUT
        if (ControlGroupUtil.isOptedOut(optOuts, now)) {
            return ControlHistoryStatus.OPTED_OUT;
        }

        // Step 4 - HAVE NOT BEEN CONTROLLED
        if (lastControlHistoryEvent == null) {
            return ControlHistoryStatus.CONTROLLED_NONE;
        }
        
        // Step 5 - CURRENTLY CONTROLLING
        if (lastControlHistoryEvent.isControlling()) {
            return ControlHistoryStatus.CONTROLLED_CURRENT;
        }
        
        // Step 6 - CONTROLLED TODAY
        if (today.toDateMidnight(userContext.getJodaTimeZone()).isBefore(lastControlHistoryEvent.getEndDate())) {
            return ControlHistoryStatus.CONTROLLED_TODAY;
        }

        // Step 7 - CONTROLLED IN THE PAST
        return ControlHistoryStatus.CONTROLLED_PREVIOUSLY;

    }

    private void generateHolderListAndBuildInventoryIds(Set<Holder> holderList, 
                                                        final List<Integer> validGroups, 
                                                        final List<DistinctEnrollment> enrollments, 
                                                        final int inventoryId) {
        
        for (DistinctEnrollment enrollment : enrollments) {
            if (!validGroups.contains(enrollment.getGroupId()) ||
                enrollment.getInventoryId() != inventoryId) {
                 continue;
            }
            
            Holder holder = new Holder();
            holder.accountId = enrollment.getAccountId();
            holder.inventoryId = enrollment.getInventoryId();
            holder.groupId = enrollment.getGroupId();
            holder.programId = enrollment.getProgramId();
            holderList.add(holder);
        }
    }
    
    @Autowired
    public void setControlHistoryEventDao(ControlHistoryEventDao controlHistoryEventDao) {
        this.controlHistoryEventDao = controlHistoryEventDao;
    }
    
    @Autowired
    public void setControlHistorySummaryService(ControlHistorySummaryService controlHistorySummaryService) {
        this.controlHistorySummaryService = controlHistorySummaryService;
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }
    
    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
    
}