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

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
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
    public ListMultimap<Integer, ControlHistory> getControlHistory(final CustomerAccount customerAccount,
            final List<Appliance> applianceList, final YukonUserContext yukonUserContext, final ControlPeriod controlPeriod) {
 
        int customerAccountId = customerAccount.getAccountId();
        ListMultimap<Integer, ControlHistory> programControlHistoryMultimap = ArrayListMultimap.create();
        
        for (Appliance appliance : applianceList) {
            
            // Any appliance that has a groupId that does *not* belong in supportedGroupIdList will not used.
            final List<Integer> supportedGroupIdList = programDao.getGroupIdsByProgramId(appliance.getProgramId());
    
            final List<Holder> holderList = Lists.newArrayList();
            final Set<Integer> inventoryIds = Sets.newHashSet();
            
            generateHolderListAndBuildInventoryIds(appliance.getProgramId(),
                                                   supportedGroupIdList,
                                                   applianceList, holderList,
                                                   inventoryIds);
    
            final Map<Integer, InventoryBase> inventoryMap = inventoryBaseDao.getByIds(new ArrayList<Integer>(inventoryIds));
            for (final Holder holder : holderList) {
                InventoryBase inventory = inventoryMap.get(holder.inventoryId);
                
                ControlHistory controlHistory = new ControlHistory();

                StarsLMControlHistory starsLMControlHistory = 
                    controlHistoryEventDao.getEventsByGroup(customerAccountId, holder.groupId, holder.inventoryId, controlPeriod, yukonUserContext);
                List<ControlHistoryEvent> controlHistoryEventList = controlHistoryEventDao.toEventList(starsLMControlHistory);
                controlHistory.setCurrentHistory(controlHistoryEventList);
                
                controlHistory.setInventory(inventory);

                ControlHistoryRequest request = 
                    new ControlHistoryRequest(appliance.getProgramId(),
                                              holder.inventoryId,
                                              customerAccountId,
                                              controlPeriod);
                ControlGroupHolder controlGroupHolder = 
                    createControlGroupHolder(request, Collections.singletonList(holder.groupId), yukonUserContext);

                String displayName = inventoryBaseDao.getDisplayName(inventory);
                controlHistory.setDisplayName(displayName);

                ControlHistorySummary programControlHistorySummary = new ControlHistorySummary();
                Duration dailyTime = starsLMControlHistory.getControlSummary().getDailyTime();
                programControlHistorySummary.setDailySummary(dailyTime);
                Duration monthlyTime = starsLMControlHistory.getControlSummary().getMonthlyTime();
                programControlHistorySummary.setMonthlySummary(monthlyTime);
                Duration yearlyTime = starsLMControlHistory.getControlSummary().getAnnualTime();
                programControlHistorySummary.setYearlySummary(yearlyTime);
                controlHistory.setProgramControlHistorySummary(programControlHistorySummary);
                
                ControlHistorySummary controlHistorySummary = controlHistorySummaryService.getControlSummary(customerAccountId, holder.inventoryId, holder.groupId, yukonUserContext);
                controlHistory.setControlHistorySummary(controlHistorySummary);

                ControlHistoryEvent lastControlHistoryEvent = controlHistoryEventDao.getLastControlHistoryEntry(customerAccountId, appliance.getProgramId(), holder.inventoryId, yukonUserContext);
                controlHistory.setLastControlHistoryEvent(lastControlHistoryEvent);

                ControlHistoryStatus controlHistoryStatus = getCurrentControlStatus(controlGroupHolder, lastControlHistoryEvent);
                controlHistory.setCurrentStatus(controlHistoryStatus);
                
                programControlHistoryMultimap.put(appliance.getProgramId(), controlHistory);
            }
        }

        return programControlHistoryMultimap;
        
    }

    private ControlGroupHolder createControlGroupHolder(ControlHistoryRequest request, 
                                                        List<Integer> groupIdList,
                                                        YukonUserContext yukonUserContext) {

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
                                                        yukonUserContext);
            
            controlHistoryEventDao.removeInvalidEnrollmentControlHistory(starsLMControlHistory, request.inventoryId, groupId);
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
    
    private void generateHolderListAndBuildInventoryIds(int programId,
                                                          final List<Integer> supportedGroupIdList,
                                                          final List<Appliance> applianceList,
                                                          final List<Holder> holderList,
                                                          final Set<Integer> inventoryIds) {
        for (Appliance appliance : applianceList) {
            int applianceProgramId = appliance.getProgramId();
            if (applianceProgramId != appliance.getProgramId()) continue;
   
            Integer applianceGroupId = appliance.getGroupdId();
            if (!supportedGroupIdList.contains(applianceGroupId)) continue;
   
            Holder holder = new Holder();
            holder.groupId = applianceGroupId;
            holder.inventoryId = appliance.getInventoryId();
            holderList.add(holder);

            
            Integer inventoryId = appliance.getInventoryId();
            inventoryIds.add(inventoryId);
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
