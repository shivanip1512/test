package com.cannontech.multispeak.dao.v4;

import java.util.List;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;

public interface MultispeakDao {
    
    public MultispeakVendor getMultispeakVendorFromCache(String vendorName, String appName);
    
    public MultispeakVendor getMultispeakVendor(String vendorName, String appName);
    
    public MultispeakVendor getMultispeakVendor(int vendorID);

    public List<MultispeakInterface> getMultispeakInterfaces(int vendorID);
    
    public List<MultispeakVendor> getMultispeakVendors(boolean excludeCannon);
    
    /**
     * Returns a list of MultiSpeakVendors that support CIS functionality.
     * @return
     */
    public List<MultispeakVendor> getMultispeakCISVendors();

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
    
    /**
     * Checks if there is already a Vendor having the same combination of Company Name and App Name.
     * 
     * @return true if there is a conflict
     */
    boolean isUniqueName(String companyName, String appName, Integer vendorId);
    
}
