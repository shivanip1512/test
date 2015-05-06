package com.cannontech.stars.dr.workOrder.dao;

import java.util.List;

import com.cannontech.stars.dr.workOrder.model.WorkOrderBase;


public interface WorkOrderBaseDao {

    /**
     * Adds a WorkOrderBase entry to the database
     */
    public void add(WorkOrderBase workOrderBase);

    /**
     * Removes a WorkOrderBase entry from the database
     */
    public void delete(int workOrderId);
    
    /**
     * Updates a WorkOrderBase entry in the database
     */
    public void update(WorkOrderBase workOrderBase);
    
    /**
     * Retrieves a workOrderBase entry from the database
     */
    public WorkOrderBase getById(int workOrderId);
    
    /**
     * Retrieves all the workOrderBase entries for a given account.
     */
    public List<WorkOrderBase> getByAccountId(int accountId);

    /**
     * Find the largest order number. If no numeric call numbers exist 0 is returned.
     */
    long getLargestNumericOrderNumber(int ecId);

    /**
     * Returns true if order number already exists for ecId.
     */
    boolean orderNumberExists(String orderNumber, int ecId);
}