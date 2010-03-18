package com.cannontech.stars.dr.event.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.stars.dr.event.model.LMCustomerEventBase;

public interface LMCustomerEventBaseDao {

    public boolean addHardwareEvent(LMCustomerEventBase eventBase, int energyCompanyId, int inventoryId);

    /**
     * Method to delete customer events from LMCustomerEventBase
     * @param eventIds
     */
    void deleteCustomerEvents(List<Integer> eventIds);

    /**
     * Method to update the notes for an event, usually for hardware install events.
     * @param eventId
     * @param notes
     */
    void updateNotesForEvent(int eventId, Date date, String notes);
    
}
