package com.cannontech.dr.rfn.service;

import java.util.List;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.message.dispatch.message.PointData;

public interface RfnLcrDataMappingService {
    
    /**
     * This method extracts point data from an incoming RFN LCR message
     * and returns all available PointData as a List.
     *
     * @param archiveRequest The incoming request message including encoded payload.
     * @param decodedMessage The decoded payload represented as an XPath template.
     * @return A list of all PointData that can be extracted from the decoded message.
     */
    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest archiveRequest, 
            SimpleXPathTemplate decodedMessage);

}
