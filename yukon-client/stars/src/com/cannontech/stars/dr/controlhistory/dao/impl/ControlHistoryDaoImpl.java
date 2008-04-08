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
import com.cannontech.stars.dr.controlhistory.model.ControlHistory;
import com.cannontech.stars.dr.controlhistory.model.ControlHistoryStatus;
import com.cannontech.stars.dr.hardware.dao.InventoryBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.InventoryBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.xml.serialize.StarsLMControlHistory;
import com.cannontech.stars.xml.serialize.types.StarsCtrlHistPeriod;
import com.cannontech.user.YukonUserContext;

public class ControlHistoryDaoImpl implements ControlHistoryDao {
    private InventoryBaseDao inventoryBaseDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ProgramDao programDao;
    
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
        
        return getControlHistory(requestList, yukonUserContext);
    }
    
    private Map<Integer, List<ControlHistory>> getControlHistory(List<ControlHistoryRequest> requestList,
             YukonUserContext yukonUserContext) {

        final Map<Integer, List<ControlHistory>> resultMap = new HashMap<Integer, List<ControlHistory>>();
        
        final List<Integer> inventoryIdList = new ArrayList<Integer>(requestList.size());
        for (final ControlHistoryRequest request : requestList) {
            inventoryIdList.add(request.inventoryId);
        }
        
        final Map<Integer, InventoryBase> inventoryMap = inventoryBaseDao.getByIds(inventoryIdList);
        
        for (final ControlHistoryRequest request : requestList) {
            Integer key = request.programId;
            
            List<ControlHistory> value = resultMap.get(key);
            if (value == null) {
                value = new ArrayList<ControlHistory>();
                resultMap.put(key, value);
            }
            
          final ControlHistory controlHistory = new ControlHistory();
          ControlHistoryStatus controlStatus = getCurrentControlStatus(request, yukonUserContext);
          controlHistory.setCurrentStatus(controlStatus);
          
          InventoryBase inventory = inventoryMap.get(request.inventoryId);
          controlHistory.setInventory(inventory);
          
          String displayName = getDisplayName(inventory);
          controlHistory.setDisplayName(displayName);
          
          value.add(controlHistory);
        }
        
        return resultMap;
    }
    
    private ControlHistoryStatus getCurrentControlStatus(ControlHistoryRequest request, YukonUserContext yukonUserContext) {

        final List<LMHardwareControlGroup> enrolledList = new ArrayList<LMHardwareControlGroup>();
        final List<LMHardwareControlGroup> optOutList = new ArrayList<LMHardwareControlGroup>();
        final Map<Integer, StarsLMControlHistory> controlHistoryMap = new HashMap<Integer, StarsLMControlHistory>();
        
        List<Integer> groupIdList = lmHardwareControlGroupDao.getDistinctGroupIdsByAccountId(request.customerAccountId);
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
            controlHistoryMap.put(groupId, controlHistory);
        }
        
        final Date now = new Date();
        
        // filter out entries that do not belong to this program or inventory
        List<Integer> supportedGroupIds = programDao.getGroupIdsByProgramId(request.programId);
        filterList(enrolledList, supportedGroupIds, request.inventoryId);
        filterList(optOutList, supportedGroupIds, request.inventoryId);
        
        // Step 1 - NOT ENROLLED
        boolean isNotEnrolled = isNotEnrolled(enrolledList, now);
        if (isNotEnrolled) return ControlHistoryStatus.NOT_ENROLLED;
        
        // Step 2 - OPTED OUT
        boolean isOptedOut = isOptedOut(optOutList, now);
        if (isOptedOut) return ControlHistoryStatus.OPTED_OUT;
        
        // Step 3 - CURRENTLY CONTROLLING
        boolean isControlling = isControlling(enrolledList, controlHistoryMap);
        if (isControlling) return ControlHistoryStatus.CONTROLLED_CURRENT;
        
        // Step 4 - CONTROLLED TODAY
        boolean isControlledToday = isControlledToday(enrolledList, controlHistoryMap);
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
    
    private String getDisplayName(InventoryBase inventory) {
        String displayName;
        String deviceLabel = inventory.getDeviceLabel();
        if (!deviceLabel.matches("^\\s*$")) {
            displayName = deviceLabel;
        } else {
            LMHardwareBase hardware = lmHardwareBaseDao.getById(inventory.getInventoryId());
            displayName = hardware.getManufacturerSerialNumber();
        }
        return displayName;
    }
    
    private boolean isControlledToday(List<LMHardwareControlGroup> controlGroupList, Map<Integer, StarsLMControlHistory> controlHistoryMap) {
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            StarsLMControlHistory controlHistory = controlHistoryMap.get(controlGroup.getLmGroupId());
            if (controlHistory == null) continue;
            boolean result = controlHistory.getControlSummary().getDailyTime() != 0;
            if (result) return true;
        }    
        return false;
    }    
    
    private boolean isControlling(List<LMHardwareControlGroup> controlGroupList, Map<Integer, StarsLMControlHistory> controlHistoryMap) {
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            StarsLMControlHistory controlHistory = controlHistoryMap.get(controlGroup.getLmGroupId());
            if (controlHistory == null) continue;
            boolean result = controlHistory.getBeingControlled();
            if (result) return true;    
        }
        return false;
    }    

    private boolean isNotEnrolled(List<LMHardwareControlGroup> controlGroupList, Date now) {
        if (controlGroupList.isEmpty()) return true;
        
        for (final LMHardwareControlGroup controlGroup : controlGroupList) {
            Date start = controlGroup.getGroupEnrollStart();
            Date stop = controlGroup.getGroupEnrollStop();
            
            boolean hasBothNullDates = hasBothNullDates(start, stop);
            if (hasBothNullDates) return true;
            
            boolean hasBothDatesInFuture = hasBothDatesInFuture(start, stop, now);
            if (hasBothDatesInFuture) return true;

            boolean hasBothDatesInPast = hasBothDatesInPast(start, stop, now);
            if (hasBothDatesInPast) return true;
        }    
        return false;
    }
    
    private boolean isOptedOut(List<LMHardwareControlGroup> outList, Date now) {
        for (final LMHardwareControlGroup controlGroup : outList) {
            boolean hasStartWithNullStop = hasStartWithNullStop(controlGroup.getOptOutStart(),
                                                            controlGroup.getOptOutStop(),
                                                            now);
            if (hasStartWithNullStop) return true;
        }
        return false;
    }
    
    private boolean hasStartWithNullStop(Date start, Date stop, Date now) {
        boolean result = (start != null && start.before(now) && stop == null);
        return result;
    }
    
    private boolean hasBothDatesInPast(Date start, Date stop, Date now) {
        boolean startDateInPast = (start != null && start.before(now));
        boolean stopDateInPast = (stop != null && stop.before(now));
        boolean result = startDateInPast && stopDateInPast;
        return result;
    }
    
    private boolean hasBothDatesInFuture(Date start, Date stop, Date now) {
        boolean startDateInFuture = (start != null && start.after(now));
        boolean stopDateInFuture = (stop != null && stop.after(now));
        boolean result = startDateInFuture && stopDateInFuture;
        return result;
    }
    
    private boolean hasBothNullDates(Date start, Date stop) {
        boolean result = (start == null && stop == null);
        return result;
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
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
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
    
}
