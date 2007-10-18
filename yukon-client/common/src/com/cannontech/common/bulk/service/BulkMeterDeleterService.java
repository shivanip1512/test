package com.cannontech.common.bulk.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface BulkMeterDeleterService {

    /**
     * Returns a list of all the PAObjects that will be effected by a remove command
     * 
     */
    public List<LiteYukonPAObject> showPAObjectsAddress(int address);
    public List<LiteYukonPAObject> showPAObjectsAddress(int minRange, int maxRange);
    public List<LiteYukonPAObject> showPAObjectsMeterNumber(String meterNumber);
    public List<LiteYukonPAObject> showPAObjectsPaoName(String paoName);
    
    public void remove(List<LiteYukonPAObject> liteYukonPAObjects);
    public String getErrors();

}