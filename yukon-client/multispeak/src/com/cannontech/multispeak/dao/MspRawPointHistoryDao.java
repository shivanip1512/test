package com.cannontech.multispeak.dao;

import java.util.Date;

import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.MeterRead;

public interface MspRawPointHistoryDao
{    
    public enum ReadBy {
        METER_NUMBER
    }
    
    /**
     * Returns a MeterRead[] with kW and kWh readings for MeterNo, > startDate and <= endDate 
     * @param meterNo
     * @param startDate
     * @param endDate
     * @return
     */
    public MeterRead[] retrieveMeterReads(ReadBy readBy, String readByValue, Date startDate, Date endDate, String lastReceived, int maxRecords);

    public FormattedBlock retrieveBlock(FormattedBlockService<Block> block, Date startDate, Date endDate, String lastReceived);

    public FormattedBlock retrieveBlockByMeterNo(FormattedBlockService<Block> block, Date startDate, Date endDate, String meterNumber, int maxRecords);
}

