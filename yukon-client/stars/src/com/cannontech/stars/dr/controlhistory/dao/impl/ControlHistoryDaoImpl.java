package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
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
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.util.ControlGroupUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ControlHistoryDaoImpl implements ControlHistoryDao {
    private ControlHistoryEventDao controlHistoryEventDao;
    private ControlHistorySummaryService controlHistorySummaryService;
    private InventoryBaseDao inventoryBaseDao;
    private ProgramDao programDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    
    @Override
    public ListMultimap<Integer, ControlHistory> getControlHistory(int accountId, YukonUserContext userContext, ControlPeriod controlPeriod, boolean past) {
 
        ListMultimap<Integer, ControlHistory> programControlHistoryMultimap = ArrayListMultimap.create();
        List<DistinctEnrollment> enrollments = lmHardwareControlGroupDao.getDistinctEnrollments(accountId, past);
        
        for (DistinctEnrollment enrollment : enrollments) {
            
            // Any groupId that does *not* belong in supportedGroupIdList will not used.
            final List<Integer> supportedGroups = programDao.getGroupIdsByProgramId(enrollment.getProgramId());
    
            final List<Holder> holderList = Lists.newArrayList();
            final Set<Integer> inventoryIds = Sets.newHashSet();
            
            generateHolderListAndBuildInventoryIds(supportedGroups, enrollments, holderList, inventoryIds);
    
            final Map<Integer, InventoryBase> inventoryMap = inventoryBaseDao.getByIds(new ArrayList<Integer>(inventoryIds));
            for (final Holder holder : holderList) {
                InventoryBase inventory = inventoryMap.get(holder.inventoryId);
                
                ControlHistory controlHistory = new ControlHistory();

                StarsLMControlHistory starsLMControlHistory = controlHistoryEventDao.getEventsByGroup(accountId, holder.groupId, holder.inventoryId, controlPeriod, userContext, past);
                List<ControlHistoryEvent> controlHistoryEventList = controlHistoryEventDao.toEventList(enrollment.getProgramId(), starsLMControlHistory, userContext);
                controlHistory.setCurrentHistory(controlHistoryEventList);
                
                controlHistory.setInventory(inventory);

                ControlHistoryRequest request = new ControlHistoryRequest(enrollment.getProgramId(), holder.inventoryId, accountId, controlPeriod);
                ControlGroupHolder controlGroupHolder = createControlGroupHolder(request, Collections.singletonList(holder.groupId), userContext, past);

                controlHistory.setDisplayName(inventoryBaseDao.getDisplayName(inventory));

                ControlHistorySummary programControlHistorySummary = new ControlHistorySummary();
                
                /* Daily Summary */
                Duration dailyTime = starsLMControlHistory.getControlSummary().getDailyTime();
                programControlHistorySummary.setDailySummary(dailyTime);
                
                /* Monthly Summary */
                Duration monthlyTime = starsLMControlHistory.getControlSummary().getMonthlyTime();
                programControlHistorySummary.setMonthlySummary(monthlyTime);
                
                /* Yearly Summary */
                Duration yearlyTime = starsLMControlHistory.getControlSummary().getAnnualTime();
                programControlHistorySummary.setYearlySummary(yearlyTime);
                
                controlHistory.setProgramControlHistorySummary(programControlHistorySummary);
                
                ControlHistorySummary controlHistorySummary = controlHistorySummaryService.getControlSummary(accountId, holder.inventoryId, holder.groupId, userContext, past);
                controlHistory.setControlHistorySummary(controlHistorySummary);

                ControlHistoryEvent lastControlHistoryEvent = controlHistoryEventDao.getLastControlHistoryEntry(accountId, enrollment.getProgramId(), holder.inventoryId, userContext, past);
                controlHistory.setLastControlHistoryEvent(lastControlHistoryEvent);

                ControlHistoryStatus controlHistoryStatus = getCurrentControlStatus(controlGroupHolder, lastControlHistoryEvent);
                controlHistory.setCurrentStatus(controlHistoryStatus);
                
                programControlHistoryMultimap.put(enrollment.getProgramId(), controlHistory);
            }
        }

        return programControlHistoryMultimap;
        
    }

    private ControlGroupHolder createControlGroupHolder(ControlHistoryRequest request, 
                                                        List<Integer> groupIdList,
                                                        YukonUserContext userContext,
                                                        boolean past) {

        final List<LMHardwareControlGroup> enrolledList = new ArrayList<LMHardwareControlGroup>();
        final List<LMHardwareControlGroup> optOutList = new ArrayList<LMHardwareControlGroup>();
        final Map<Integer, StarsLMControlHistory> currentControlHistory = new HashMap<Integer, StarsLMControlHistory>();
        
        for (final Integer groupId : groupIdList) {
            List<LMHardwareControlGroup> enrolledEntryList =
                lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(request.inventoryId,
                                                                                        groupId,
                                                                                        request.customerAccountId,
                                                                                        LMHardwareControlGroup.ENROLLMENT_ENTRY);
            enrolledList.addAll(enrolledEntryList);
            
            List<LMHardwareControlGroup> optOutEntryList = 
                lmHardwareControlGroupDao.getByInventoryIdAndGroupIdAndAccountIdAndType(request.inventoryId,
                                                                                        groupId,
                                                                                        request.customerAccountId,
                                                                                        LMHardwareControlGroup.OPT_OUT_ENTRY);
            optOutList.addAll(optOutEntryList);
            

            StarsLMControlHistory starsLMControlHistory = 
                controlHistoryEventDao.getEventsByGroup(request.customerAccountId, 
                                                        groupId, 
                                                        request.inventoryId, 
                                                        request.controlPeriod, 
                                                        userContext,
                                                        past);
            
            controlHistoryEventDao.removeInvalidEnrollmentControlHistory(starsLMControlHistory, request.inventoryId, groupId, past);
            currentControlHistory.put(groupId, starsLMControlHistory);
        }
        
        // filter out entries that do not belong to this program or inventory
        List<Integer> supportedGroupIds = programDao.getGroupIdsByProgramId(request.programId);
        filterList(enrolledList, supportedGroupIds, request.inventoryId);
        filterList(optOutList, supportedGroupIds, request.inventoryId);
        filterMap(supportedGroupIds, currentControlHistory);
        
        return new ControlGroupHolder(enrolledList, optOutList, currentControlHistory);
    }
    
    private ControlHistoryStatus getCurrentControlStatus(ControlGroupHolder holder, 
                                                         ControlHistoryEvent lastControlHistoryEvent) {
        final Instant now = new Instant();
        
        // Step 1 - NOT ENROLLED
        boolean isNotEnrolled = !ControlGroupUtil.isEnrolled(holder.enrolledList, now);
        if (isNotEnrolled) return ControlHistoryStatus.NOT_ENROLLED;
        
        // Step 2 - OPTED OUT
        boolean isOptedOut = ControlGroupUtil.isOptedOut(holder.optOutList, now);
        if (isOptedOut) return ControlHistoryStatus.OPTED_OUT;
        
        // Step 3 - CURRENTLY CONTROLLING
        boolean isControlling = ControlGroupUtil.isControlling(holder.enrolledList, holder.currentControlHistoryMap);
        if (isControlling) return ControlHistoryStatus.CONTROLLED_CURRENT;
        
        // Step 4 - CONTROLLED TODAY
        boolean isControlledToday = ControlGroupUtil.isControlledToday(holder.enrolledList, holder.currentControlHistoryMap);
        if (isControlledToday) return ControlHistoryStatus.CONTROLLED_TODAY;


        // Step 5 - CONTROLLED IN THE PAST
        if (lastControlHistoryEvent != null){
            return ControlHistoryStatus.CONTROLLED_PREVIOUSLY;
        }

        // Step 6 - HAVE NOT BEEN CONTROLLED
        return ControlHistoryStatus.CONTROLLED_NONE;
    }

    private void filterList(List<LMHardwareControlGroup> list, List<Integer> supportedGroupIds, int inventoryId) {
        
        final List<LMHardwareControlGroup> removeList = new ArrayList<LMHardwareControlGroup>();
        for (final LMHardwareControlGroup controlGroup : list) {
            int controlInventoryId = controlGroup.getInventoryId();
            int controlGroupId = controlGroup.getLmGroupId();
            
            boolean failedInventoryCheck = (controlInventoryId != inventoryId);
            boolean failedGroupIdCheck = !supportedGroupIds.contains(controlGroupId); 
            
            if (failedInventoryCheck || failedGroupIdCheck) removeList.add(controlGroup);
        }
        list.removeAll(removeList);
    }
    
    private void filterMap(List<Integer> keyList, Map<Integer, StarsLMControlHistory> map) {
        final List<Integer> removeList = new ArrayList<Integer>(0);
        
        for (final Integer key : map.keySet()) {
            boolean removeKey = !keyList.contains(key);
            if (removeKey) removeList.add(key);
        }

        for (final Integer key : removeList) {
            map.remove(key);
        }
    }
    
    private void generateHolderListAndBuildInventoryIds( final List<Integer> validGroups, 
                                                         final List<DistinctEnrollment> enrollments, 
                                                         final List<Holder> holderList, 
                                                         final Set<Integer> inventoryIds) {
        for (DistinctEnrollment enrollment : enrollments) {
            Integer groupId = enrollment.getGroupId();
            if (!validGroups.contains(groupId)) continue;
   
            Holder holder = new Holder();
            holder.groupId = groupId;
            holder.inventoryId = enrollment.getInventoryId();
            holderList.add(holder);
            
            inventoryIds.add(enrollment.getInventoryId());
        }
    }
    
    private final class ControlGroupHolder {
        final List<LMHardwareControlGroup> enrolledList;
        final List<LMHardwareControlGroup> optOutList;
        final Map<Integer, StarsLMControlHistory> currentControlHistoryMap;
        
        public ControlGroupHolder(List<LMHardwareControlGroup> enrolledList, 
                                  List<LMHardwareControlGroup> optOutList, 
                                  Map<Integer, StarsLMControlHistory> currentControlHistoryMap) {
            this.enrolledList = enrolledList;
            this.optOutList = optOutList;
            this.currentControlHistoryMap = currentControlHistoryMap;
        }
    }
    
    private final class ControlHistoryRequest {
        final int programId;
        final int inventoryId;
        final int customerAccountId;
        final ControlPeriod controlPeriod;
        
        public ControlHistoryRequest(int programId, int inventoryId, int customerAccountId, ControlPeriod controlPeriod) {
            this.programId = programId;
            this.inventoryId = inventoryId;
            this.customerAccountId = customerAccountId;
            this.controlPeriod = controlPeriod;
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
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
    
}