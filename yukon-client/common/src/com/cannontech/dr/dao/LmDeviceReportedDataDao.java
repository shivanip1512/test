package com.cannontech.dr.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.core.dao.NotFoundException;

public interface LmDeviceReportedDataDao {

    /**
     * Saves the {@link LmReportedAddress} to the LmReportedAddress table
     * if the address is actually different than the current one.  We will get
     * these every time a device reports it's dr report (default is once per day).
     * 
     * @return Returns true if the {@link LmReportedAddress} was saved meaning the address was
     *  different than the current address.
     */
    public boolean save(LmReportedAddress address);
    
    /**
     * Retreives current address for device.
     * 
     * @return Returns the most recent {@link LmReportedAddress} from LmReportedAddress table for this device
     * @throws Throws {@link NotFoundException} if no address has been recorded for that device.
     */
    public LmReportedAddress getCurrentAddress(int deviceId) throws NotFoundException;
    
    /**
     * Retrieves all recorded {@link LmReportedAddress} for this device.
     * 
     * @return Returns all recorded {@link LmReportedAddress} for this device, or an empty list if none exist.
     */
    public List<LmReportedAddress> getAllRecordedAddresses(int deviceId);

    /**
     * Retrieves the most current address for all devices
     */
    public Set<LmReportedAddress> getAllCurrentAddresses();
    
}