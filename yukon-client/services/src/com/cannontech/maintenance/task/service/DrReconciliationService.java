package com.cannontech.maintenance.task.service;

import java.util.List;

public interface DrReconciliationService {

    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'out of service' in Yukon.
     * 
     * @return list of InventoryIds of LCRs
     */
    public List<Integer> getOutOfServiceExpectedLcrs();

    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'InService' in Yukon
     * 
     * @return list of InventoryIds of LCRs
     */
    public List<Integer> getInServiceExpectedLcrs();
}
