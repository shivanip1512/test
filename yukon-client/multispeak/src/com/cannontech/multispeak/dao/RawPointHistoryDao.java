package com.cannontech.multispeak.dao;

import java.util.Date;

import com.cannontech.multispeak.service.MeterRead;

public interface RawPointHistoryDao
{    
    public enum ReadBy {
        METER_NUMBER, 
        COLL_GROUP,
        BILL_GROUP
    }
    
    /**
     * Returns a MeterRead[] with kW and kWh readings for MeterNo, > startDate and <= endDate 
     * @param meterNo
     * @param startDate
     * @param endDate
     * @return
     */
    public MeterRead[] retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, Date endDate, String lastReceived);
    
}
