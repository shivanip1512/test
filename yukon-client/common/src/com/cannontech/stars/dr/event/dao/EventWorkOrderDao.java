package com.cannontech.stars.dr.event.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.stars.database.data.event.EventWorkOrder;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.dr.event.model.EventBase;

public interface EventWorkOrderDao {

    Map<Integer,List<EventWorkOrder>> getByWorkOrders(List<LiteWorkOrderBase> workOrderList);
    
    /**
     * This method gets all the events for a given work order.
     */
    List<EventBase> getByWorkOrderId(int workOrderId);

    /**
     * Removes all the event work order entries for a given work order id.
     */
    public void deleteEventWorkOrders(List<Integer> workOrderIds);

    public List<Integer> getEventIdsForWorkOrder(Integer workOrderId);

    /**
     * Adds an event work order entry to the database.
     */
    public void add(com.cannontech.stars.dr.event.model.EventWorkOrder eventWorkOrder);
    
}
