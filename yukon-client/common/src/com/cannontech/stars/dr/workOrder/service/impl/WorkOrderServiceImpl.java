package com.cannontech.stars.dr.workOrder.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.event.dao.EventBaseDao;
import com.cannontech.stars.dr.event.dao.EventWorkOrderDao;
import com.cannontech.stars.dr.event.model.EventBase;
import com.cannontech.stars.dr.event.model.EventWorkOrder;
import com.cannontech.stars.dr.workOrder.dao.WorkOrderBaseDao;
import com.cannontech.stars.dr.workOrder.model.WorkOrderBase;
import com.cannontech.stars.dr.workOrder.model.WorkOrderCurrentStateEnum;
import com.cannontech.stars.dr.workOrder.model.WorkOrderDto;
import com.cannontech.stars.dr.workOrder.service.WorkOrderService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class WorkOrderServiceImpl implements WorkOrderService {

    private AccountEventLogService accountEventLogService;
    private EventBaseDao eventBaseDao;
    private EventWorkOrderDao eventWorkOrderDao;
    private StarsDatabaseCache starsDatabaseCache;
    private WorkOrderBaseDao workOrderBaseDao;
    private YukonListDao yukonListDao;
    
    @Override
    @Transactional
    public void createWorkOrder(WorkOrderDto workOrderDto, int energyCompanyId, 
                                String accountNumber, YukonUserContext userContext) {

        // Generate a new work order number if needed
        if (StringUtils.isEmpty(workOrderDto.getWorkOrderBase().getOrderNumber())) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
            workOrderDto.getWorkOrderBase().setOrderNumber(energyCompany.getNextOrderNumber());
        }
        workOrderDto.getWorkOrderBase().setEnergyCompanyId(energyCompanyId);
        workOrderDto.getWorkOrderBase().setDateReported(workOrderDto.getEventDate().toInstant());
        workOrderBaseDao.add(workOrderDto.getWorkOrderBase());
        
        // Create a new event
        createNewWorkOrderEvent(workOrderDto, userContext);
       
        // Add event log entry
        accountEventLogService.workOrderCreated(userContext.getYukonUser(),
                                                accountNumber,
                                                workOrderDto.getWorkOrderBase().getOrderNumber());
    }

    @Override
    @Transactional
    public void updateWorkOrder(WorkOrderDto workOrderDto, String accountNumber,
                                YukonUserContext userContext) {

        // Retrieve the old state of the work order and check to see if the current state has changed.
        WorkOrderDto oldWorkOrderDto = getWorkOrder(workOrderDto.getWorkOrderBase().getOrderId());
        workOrderDto.getWorkOrderBase().setDateReported(oldWorkOrderDto.getWorkOrderBase().getDateReported());
        workOrderDto.getWorkOrderBase().setDateScheduled(oldWorkOrderDto.getWorkOrderBase().getDateScheduled());
        workOrderDto.getWorkOrderBase().setDateCompleted(oldWorkOrderDto.getWorkOrderBase().getDateCompleted());
        workOrderBaseDao.update(workOrderDto.getWorkOrderBase());
        
        // Create a new event
        if (oldWorkOrderDto.getWorkOrderBase().getCurrentStateId() != workOrderDto.getWorkOrderBase().getCurrentStateId()) {

            createNewWorkOrderEvent(workOrderDto, userContext);
            
            // Set scheduled date
            YukonListEntry yukonListEntry = 
                yukonListDao.getYukonListEntry(workOrderDto.getWorkOrderBase().getCurrentStateId());
            if (yukonListEntry.getYukonDefID() == WorkOrderCurrentStateEnum.SCHEDULED.getDefinitionId()) {
                workOrderDto.getWorkOrderBase().setDateScheduled(workOrderDto.getEventDate());
            }
            
            // Set completed date 
            if (yukonListEntry.getYukonDefID() == WorkOrderCurrentStateEnum.COMPLETED.getDefinitionId()){
                workOrderDto.getWorkOrderBase().setDateCompleted(workOrderDto.getEventDate());
            }
        }
        
        // Add event log entry
        accountEventLogService.workOrderUpdated(userContext.getYukonUser(),
                                                accountNumber,
                                                workOrderDto.getWorkOrderBase().getOrderNumber());
    }
    
    @Override
    @Transactional
    public void deleteWorkOrder(int workOrderId, String accountNumber, YukonUserContext userContext) {
        WorkOrderBase workOrderBase = workOrderBaseDao.getById(workOrderId);

        // Delete work order
        workOrderBaseDao.delete(workOrderId);

        // Add event log entry
        accountEventLogService.workOrderDeleted(userContext.getYukonUser(),
                                                accountNumber,
                                                workOrderBase.getOrderNumber());
    }

    @Override
    public List<WorkOrderDto> getWorkOrderList(int accountId) {
        List<WorkOrderDto> workOrders = Lists.newArrayList();
        
        List<WorkOrderBase> workOrderBases = workOrderBaseDao.getByAccountId(accountId);
        for (WorkOrderBase workOrderBase : workOrderBases) {
            List<EventBase> eventBases =
                getWorkOrderEventHistory(workOrderBase.getOrderId());

            WorkOrderDto workOrderDto = new WorkOrderDto(workOrderBase, eventBases);
            workOrders.add(workOrderDto);
        }
        
        return workOrders;
    }

    @Override
    public WorkOrderDto getWorkOrder(int workOrderId) {
        WorkOrderBase workOrderBase = workOrderBaseDao.getById(workOrderId);

        List<EventBase> eventBases =
            getWorkOrderEventHistory(workOrderBase.getOrderId());

        WorkOrderDto workOrderDto = new WorkOrderDto(workOrderBase, eventBases);
        return workOrderDto;
    }

    @Override
    public List<EventBase> getWorkOrderEventHistory(int workOrderId) {
        List<EventBase> eventBases =
            eventWorkOrderDao.getByWorkOrderId(workOrderId);

        return eventBases;
    }

    /**
     * This method creates a new work order event and also create the event to work order
     * mapping entry
     */
    private void createNewWorkOrderEvent(WorkOrderDto workOrderDto,
                                         YukonUserContext userContext) {
        // Add event base 
        EventBase eventBase = new EventBase();
        eventBase.setUserId(userContext.getYukonUser().getUserID());
        eventBase.setSystemCategoryId(YukonListEntryTypes.EVENT_SYS_CAT_WORKORDER);
        eventBase.setActionId(workOrderDto.getWorkOrderBase().getCurrentStateId());
        eventBase.setEventTimestamp(new Instant(workOrderDto.getEventDate()));
        eventBaseDao.add(eventBase);

        // Add event base to work order mapping entry
        EventWorkOrder eventWorkOrder = 
            new EventWorkOrder(eventBase.getEventId(), workOrderDto.getWorkOrderBase().getOrderId());
        eventWorkOrderDao.add(eventWorkOrder);
    }
    
    // DI
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
    @Autowired
    public void setEventBaseDao(EventBaseDao eventBaseDao) {
        this.eventBaseDao = eventBaseDao;
    }
    
    @Autowired
    public void setEventWorkOrderDao(EventWorkOrderDao eventWorkOrderDao) {
        this.eventWorkOrderDao = eventWorkOrderDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setWorkOrderBaseDao(WorkOrderBaseDao workOrderBaseDao) {
        this.workOrderBaseDao = workOrderBaseDao;
    }
    
    @Autowired
    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }

}