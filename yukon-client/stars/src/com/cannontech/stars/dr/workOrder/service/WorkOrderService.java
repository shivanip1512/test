package com.cannontech.stars.dr.workOrder.service;

import java.util.List;

import com.cannontech.stars.dr.event.model.EventBase;
import com.cannontech.stars.dr.workOrder.model.WorkOrderDto;
import com.cannontech.user.YukonUserContext;

public interface WorkOrderService {
    
    /**
     * This method creates a new work order and also handles generating a work order number
     * if one does not exist and also creates the initial event.
     */
    public void createWorkOrder(WorkOrderDto workOrderDto, int energyCompanyId,
                                String accountNumber, YukonUserContext userContext);
    
    /**
     * This method deletes a work order.  It also handles removing an events that are tied
     * to work orders.
     */
    public void deleteWorkOrder(int workOrderId, String accountNumber, YukonUserContext userContext);

    /** 
     * This method gets a list of all the work orders for a given account.
     */
    public List<WorkOrderDto> getWorkOrderList(int accountId);
    
    /**
     * This method retrieves a work order object from the database.
     */
    public WorkOrderDto getWorkOrder(int workOrderId);

    /**
     * This method updates an existing work order.  It also takes care of creating new events 
     * when an event state has been changed on a work order.
     */
    public void updateWorkOrder(WorkOrderDto workOrderDto, String accountNumber,
                                YukonUserContext userContext);

    /**
     * Returns all the event history for a given work order.  This data is ordered by date.
     */
    public List<EventBase> getWorkOrderEventHistory(int workOrderId);
    
}