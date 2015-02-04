package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;

public interface MultispeakDao {

    /**
     * get Multispeak Vendor From Cache
     * 
     * @param vendorName
     * @param appName
     * @return
     */
    public MultispeakVendor getMultispeakVendorFromCache(String vendorName, String appName);

    /**
     * get Multispeak Vendor
     * 
     * @param vendorName
     * @param appName
     * @return
     */
    public MultispeakVendor getMultispeakVendor(String vendorName, String appName);

    /**
     * get Multispeak Vendor
     * 
     * @param vendorID
     * @return
     */
    public MultispeakVendor getMultispeakVendor(int vendorID);

    /**
     * get Multispeak Interfaces
     * 
     * @param vendorID
     * @return
     */
    public List<MultispeakInterface> getMultispeakInterfaces(int vendorID);

    /**
     * get Multispeak Vendors
     * 
     * @return
     */
    public List<MultispeakVendor> getMultispeakVendors();

    /**
     * Returns a list of MultiSpeakVendors that support CIS functionality.
     * 
     * @return
     */
    public List<MultispeakVendor> getMultispeakCISVendors();

    /**
     * delete Multispeak Interface
     * 
     * @param vendorID
     * @return
     */
    public int deleteMultispeakInterface(int vendorID);

    /**
     * add Multispeak Interfaces
     * 
     * @param mspInterface
     * @return
     */
    public int addMultispeakInterfaces(MultispeakInterface mspInterface);

    /**
     * add Multispeak Vendor
     * 
     * @param mspVendor
     */
    public void addMultispeakVendor(MultispeakVendor mspVendor);

    /**
     * delete Multispeak Vendor
     * 
     * @param vendorID
     */
    public void deleteMultispeakVendor(int vendorID);

    /**
     * get Next VendorId
     * 
     * @return
     */
    public int getNextVendorId();

    /**
     * Transactional method that updates mspVendor,
     * deletes all MultispeakInterfaces for mspVendor.vendorID,
     * then inserts mspVendor.vendorMspInterfaces into MultispeakInterafaces.
     * 
     * @param mspVendor
     */
    public void updateMultispeakVendor(final MultispeakVendor mspVendor);

}
