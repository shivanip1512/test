package com.cannontech.stars.dr.controlhistory.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryDao;
import com.cannontech.stars.dr.controlhistory.dao.ControlHistoryEventDao;
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryEvent;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.util.ControlGroupUtil;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;

public class ControlHistoryDaoImpl implements ControlHistoryDao {
    private InventoryBaseDao inventoryBaseDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ProgramDao programDao;
    private ControlHistoryEventDao controlHistoryEventDao;
    
    @Override
    public Map<Integer, List<ControlHistory>> getControlHistory(final CustomerAccount customerAccount,
            final List<Appliance> appliances, final YukonUserContext yukonUserContext) {
        
        final int customerAccountId = customerAccount.getAccountId();
        final List<ControlHistoryRequest> requestList = new ArrayList<ControlHistoryRequest>(appliances.size());
        
        for (final Appliance appliance : appliances) {
            requestList.add(new ControlHistoryRequest(appliance.getProgramId(),
                                                      appliance.getInventoryId(),
                                                      customerAccountId));
        }
        
        return getControlHistory(customerAccountId, requestList, yukonUserContext);
    }
    
    private Map<Integer, List<ControlHistory>> getControlHistory(int customerAccountId, List<ControlHistoryRequest> requestList,
             YukonUserContext yukonUserContext) {

        final Map<Integer, List<ControlHistory>> resultMap = new HashMap<Integer, List<ControlHistory>>();
        final List<Integer> inventoryIdList = new ArrayList<Integer>(requestList.size());

        for (final ControlHistoryRequest request : requestList) {
            inventoryIdList.add(request.inventoryId);
        }
        
        final Map<Integer, InventoryBase> inventoryMap = inventoryBaseDao.getByIds(inventoryIdList);
        final List<Integer> groupIdList = 
            lmHardwareControlGroupDao.getDistinctGroupIdsByAccountId(customerAccountId);

        for (final ControlHistoryRequest request : requestList) {
            Integer key = request.programId;

            List<ControlHistory> value = resultMap.get(key);
            if (value == null) {
                value = new ArrayList<ControlHistory>();
                resultMap.put(key, value);
            }

            final ControlHistory controlHistory = new ControlHistory();
            ControlGroupHolder holder = createControlGroupHolder(request, groupIdList, yukonUserContext);

            ControlHistoryStatus controlStatus = getCurrentControlStatus(holder);
            controlHistory.setCurrentStatus(controlStatus);

            List<ControlHistoryEvent> currentHistory = getCurrentHistory(holder.currentControlHistoryMap);
            controlHistory.setCurrentHistory(currentHistory);

            InventoryBase inventory = inventoryMap.get(request.inventoryId);
            controlHistory.setInventory(inventory);

            String displayName = inventoryBaseDao.getDisplayName(inventory);
            controlHistory.setDisplayName(displayName);

            value.add(controlHistory);
        }
        
        return resultMap;
    }
    
    private ControlGroupHolder createControlGroupHolder(ControlHistoryRequest request, 
            List<Integer> groupIdList, YukonUserContext yukonUserContext) {

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
            
            StarsLMControlHistory controlHistory = LMControlHistoryUtil.getStarsLMControlHistory(groupId,
                                                                                                 request.customerAccountId,
                                                                                                 StarsCtrlHistPeriod.PASTDAY,
                                                                                                 yukonUserContext.getTimeZone(),
                                                                                                 yukonUserContext.getYukonUser());
            currentControlHistory.put(groupId, controlHistory);
        }
        
        // filter out entries that do not belong to this program or inventory
        List<Integer> supportedGroupIds = programDao.getGroupIdsByProgramId(request.programId);
        filterList(enrolledList, supportedGroupIds, request.inventoryId);
        filterList(optOutList, supportedGroupIds, request.inventoryId);
        filterMap(supportedGroupIds, currentControlHistory);
        
        return new ControlGroupHolder(enrolledList, optOutList, currentControlHistory);
    }
    
    private List<ControlHistoryEvent> getCurrentHistory(Map<Integer, StarsLMControlHistory> currentControlHistory) {
        final List<ControlHistoryEvent> eventList = new ArrayList<ControlHistoryEvent>();
        
        for (final StarsLMControlHistory controlHistory : currentControlHistory.values()) {
            List<ControlHistoryEvent> toEventList = controlHistoryEventDao.toEventList(controlHistory);
            eventList.addAll(toEventList);
        }

        return eventList;
    }
    
    private ControlHistoryStatus getCurrentControlStatus(ControlGroupHolder holder) {
        final Date now = new Date();
        
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
        
        // Step 5 - HAVE NOT BEEN CONTROLLED TODAY
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
    
    private final class ControlGroupHolder {
        final List<LMHardwareControlGroup> enrolledList;
        final List<LMHardwareControlGroup> optOutList;
        final Map<Integer, StarsLMControlHistory> currentControlHistoryMap;
        
        public ControlGroupHolder(List<LMHardwareControlGroup> enrolledList, 
                List<LMHardwareControlGroup> optOutList, Map<Integer, StarsLMControlHistory> currentControlHistoryMap) {
            this.enrolledList = enrolledList;
            this.optOutList = optOutList;
            this.currentControlHistoryMap = currentControlHistoryMap;
        }
    }
    
    private final class ControlHistoryRequest {
        final int programId;
        final int inventoryId;
        final int customerAccountId;
        
        public ControlHistoryRequest(int programId, int inventoryId, int customerAccountId) {
            this.programId = programId;
            this.inventoryId = inventoryId;
            this.customerAccountId = customerAccountId;
        }
    }
    
    @Autowired
    public void setInventoryBaseDao(InventoryBaseDao inventoryBaseDao) {
        this.inventoryBaseDao = inventoryBaseDao;
    }

    @Autowired
    public void setLmHardwareControlGroupDao(
            LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
    
    @Autowired
    public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Autowired
    public void setControlHistoryEventDao(
            ControlHistoryEventDao controlHistoryEventDao) {
        this.controlHistoryEventDao = controlHistoryEventDao;
    }
    
}
