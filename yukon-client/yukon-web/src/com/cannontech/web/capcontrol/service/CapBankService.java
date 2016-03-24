package com.cannontech.web.capcontrol.service;

import java.util.List;

import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;

public interface CapBankService {

    /**
     * Retrieves a fully populated {@link CapBank} from the database
     */
    CapBank getCapBank(int id);

    /**
     * Persist the capbank. If the capbank id exists, update it,
     * if capbank.id is null, inserts the capbank as a new device.
     * @param capBank CapBank
     * @return id of saved capbank
     */
    int save(CapBank capBank);

    /**
     * Deletes a capbank of the given id
     * @return true when successful, false otherwise
     */
    boolean delete(int id);

    /**
     * Gets the unassigned points for the capbank
     * @param capBank CapBank
     * @return list of unassigned points
     */
    List<CCMonitorBankList> getUnassignedPoints(CapBank capBank);

    /**
     * Sets the assigned points for the capbank - this will add the point name and order them in display order
     * @param capBank CapBank
     */
    void setAssignedPoints(CapBank capbank);

    /**
     * Assigns the given points to the capbank.  This will add a new point if it does not exist or update it (the display order) if it exists  
     * This will also delete any existing points that are not supplied.
     * @param capbankId the id for the capbank to assign to
     * @param pointIds the ids for the points to assign in display order
     */
    void savePoints(int capbankId, Integer[] pointIds);

}
