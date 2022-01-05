package com.cannontech.dr.rfn.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.service.ParsingService.Schema;

public interface RfnLcrParsingStrategy {
    public List<Schema> getSchema();

    /**
     * This method extracts point data, Verification messages and address data from an incoming RFN LCR
     * message based on message format (TLV or EXI)
     * 
     */
    public void parseRfLcrReading(RfnLcrArchiveRequest request, RfnDevice rfnDevice, AtomicInteger archivedReadings,
            AtomicInteger pointDatasProduced) throws ParseException;
}
