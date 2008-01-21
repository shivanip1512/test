package com.cannontech.stars.dr.event.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.stars.event.EventWorkOrder;

public interface EventWorkOrderDao {

    Map<Integer,List<EventWorkOrder>> getByWorkOrders(List<LiteWorkOrderBase> workOrderList);
    
    List<EventWorkOrder> getByWorkOrderId(int workOrderId);
    
}
