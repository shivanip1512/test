package com.cannontech.web.capcontrol.service;

import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.web.editor.CapControlCBC;

public interface CbcService {

    /**
     * Retrieves a fully populated {@link CapControlCBC} from the database
     */
    CapControlCBC getCbc(int id);

    /**
     * Persist the cbc. If the cbc id exists, update it,
     * if cbc.id is null, inserts the cbc as a new device.
     * @param capControlCBC
     * @return id of saved cbc
     */
    int save(CapControlCBC capControlCBC);

    /**
     * Deletes a cbc of the given id
     * @return true when successful, false otherwise
     */
    boolean delete(int id);

    /**
     * Attempts to copy the cbc
     * @param originalId id of the device to copy
     * @param newName name of the copy
     * @param copyPoints when true, will copy the points to the new device.
     * @return id of the new name
     * @throws IllegalArgumentException If the new name is already taken.
     */
    int copy(int originalId, String newName, boolean copyPoints) throws IllegalArgumentException;

    /**
     * Looks up the DNP Configuration used by the device.
     * @param cbc
     * @return The full {@link DNPConfiguration} for the device. If none is assigned, the default is returned.
     */
    DNPConfiguration getDnpConfigForDevice(CapControlCBC cbc);
}
