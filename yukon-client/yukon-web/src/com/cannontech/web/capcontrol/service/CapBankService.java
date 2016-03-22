package com.cannontech.web.capcontrol.service;

import com.cannontech.database.data.capcontrol.CapBank;

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

}
