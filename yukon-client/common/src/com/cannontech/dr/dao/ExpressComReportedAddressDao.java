package com.cannontech.dr.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.core.dao.NotFoundException;

public interface ExpressComReportedAddressDao {

    /**
     * Saves the {@link ExpressComReportedAddress} to the LmReportedAddress table
     * if the address is actually different than the current one.  We will get
     * these every time a device reports it's dr report (default is once per day).
     * 
     * @return Returns true if the {@link ExpressComReportedAddress} was saved meaning the address was
     *  different than the current address.
     */
    public boolean save(ExpressComReportedAddress address, ExpressComReportedAddress currentaddress);
    
    /**
     * Retreives current address for device.
     * 
     * @return Returns the most recent {@link ExpressComReportedAddress} from LmReportedAddress table for this device
     * @throws Throws {@link NotFoundException} if no address has been recorded for that device.
     */
    public ExpressComReportedAddress getCurrentAddress(int deviceId) throws NotFoundException;
    
    /**
     * Retrieves all recorded {@link ExpressComReportedAddress} for this device.
     * 
     * @return Returns all recorded {@link ExpressComReportedAddress} for this device, or an empty list if none exist.
     */
    public List<ExpressComReportedAddress> getAllRecordedAddresses(int deviceId);

    /**
     * Retrieves the most current address for all devices
     */
    public Set<ExpressComReportedAddress> getAllCurrentAddresses();
    
    /**
     * Retrieves the most current address for devices.
     */
    List<ExpressComReportedAddress> getCurrentAddresses(List<Integer> deviceId);

    /**
     * Insert the {@link ExpressComReportedAddress} to the LmReportedAddress table
     */
    void insertAddress(ExpressComReportedAddress address);

    /**
     * Returns the most recent current address{@link ExpressComReportedAddress} or null from LmReportedAddress table for
     * given device.
     * 
     */
    ExpressComReportedAddress findCurrentAddress(int deviceId);

}