package com.cannontech.maintenance.task.dao;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;

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
    
    /**
     * Get Groups which have RFN device enrolled.
     */
    List<Integer> getGroupsWithRfnDeviceEnrolled();
    
    /**
     * Get Enrolled LCR for a group.
     */
    List<Integer> getEnrolledRfnLcrForGroup(int groupId);
    
    /**
     * Get LCR and associated group, for those LCR's which are enrolled in multiple groups.
     */
    Multimap<Integer, Integer> getLcrEnrolledInMultipleGroup(Set<Integer> lcrs);
}
