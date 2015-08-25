package com.cannontech.web.capcontrol.service;

import java.sql.SQLException;

import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.web.editor.CapControlCBC;

public interface CBCService {
    CapControlCBC getCapControlCBC(int id);

    int save(CapControlCBC capControlCBC) throws SerialNumberExistsException, SQLException, PortDoesntExistException,
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException;
}
