package com.cannontech.web.capcontrol.service;

import java.sql.SQLException;

import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.web.editor.CapControlCBC;

public interface CbcService {

    /**
     * Retrieves a fully populated {@link CapControlCBC} from the database
     */
    CapControlCBC getCbc(int id);

    /**
     * Attempts to save capControlCbc
     * @param capControlCBC
     * @return id of saved cbc
     *
     * Throws the following exceptions if cbc has invalid setup:
     * @throws SerialNumberExistsException
     * @throws SQLException
     * @throws PortDoesntExistException
     * @throws MultipleDevicesOnPortException
     * @throws SameMasterSlaveCombinationException
     */
    int save(CapControlCBC capControlCBC) throws SerialNumberExistsException, SQLException, PortDoesntExistException,
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException;

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
