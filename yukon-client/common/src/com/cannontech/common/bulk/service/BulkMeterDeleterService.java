package com.cannontech.common.bulk.service;

import com.cannontech.common.bulk.BulkDataContainer;

public interface BulkMeterDeleterService {

    /**
     * Returns a list of all the PAObjects that will be effected by a remove command
     * 
     */
    public BulkDataContainer getPAObjectsByAddress(int address, BulkDataContainer bulkDataContainer);
    public BulkDataContainer getPAObjectsByAddress(int minRange, int maxRange, BulkDataContainer bulkDataContainer);
    public BulkDataContainer getPAObjectsByMeterNumber(String meterNumber, BulkDataContainer bulkDataContainer);
    public BulkDataContainer getPAObjectsByPaoName(String paoName, BulkDataContainer bulkDataContainer);
    
    public void remove(BulkDataContainer bulkDataContainer);
    
}