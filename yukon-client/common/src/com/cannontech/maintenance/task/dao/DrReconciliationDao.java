package com.cannontech.maintenance.task.dao;

import java.util.List;

public interface DrReconciliationDao {

    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'In Service' in Yukon.
     * 
     * @return list of InventoryIds of LCRs
     */
    List<Integer> getInServiceExpectedLcrs();

    /**
     * Method gives a list of two way RFN LCRs whose expected service status is 'out of service' in Yukon.
     * 
     * @return list of InventoryIds of LCRs
     */
    List<Integer> getOutOfServiceExpectedLcrs();
}
