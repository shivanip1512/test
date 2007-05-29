package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.service.Meter;

public interface MultispeakDao
{
    public MultispeakVendor getMultispeakVendor(String vendorName, String appName);
    
    public MultispeakVendor getMultispeakVendor(int vendorID);

    public List<MultispeakInterface> getMultispeakInterfaces(int vendorID);
    
    public List<MultispeakVendor> getMultispeakVendors();

    public int deleteMultispeakInterface(int vendorID);
    
    public int addMultispeakInterfaces(MultispeakInterface mspInterface);
    
    public void addMultispeakVendor(MultispeakVendor mspVendor);

    public void deleteMultispeakVendor(int vendorID);
    
    public int getNextVendorId();
    /**
     * Transactional method that updates mspVendor,  
     * deletes all MultispeakInterfaces for mspVendor.vendorID,
     * then inserts mspVendor.vendorMspInterfaces into MultispeakInterafaces.
     * @param mspVendor
     */
    public void updateMultispeakVendor(final MultispeakVendor mspVendor);
    
    public List<Meter> getAMRSupportedMeters(String lastReceived, String key);
    
    public List<Meter> getCDSupportedMeters(String lastReceived, String key);
    
    public boolean isCDSupportedMeter(String objectID, String key);
}
