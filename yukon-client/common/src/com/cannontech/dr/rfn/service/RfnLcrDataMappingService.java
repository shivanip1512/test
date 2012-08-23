package com.cannontech.dr.rfn.service;

import java.util.List;

import org.springframework.jms.core.JmsTemplate;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.dao.LmReportedAddress;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.message.dispatch.message.PointData;

public interface RfnLcrDataMappingService {
    
    /**
     * This method extracts point data from an incoming RFN LCR message
     * and returns all available PointData as a List.
     *
     * @param request The incoming request message including encoded payload.
     * @param message The decoded payload represented as an XPath template.
     * @return A list of all PointData that can be extracted from the decoded message.
     */
    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest request, SimpleXPathTemplate data);

    /**
     * Stores the LM addressing data reported by the device if it is not equivalent to the
     * current address recorded or no address is currently recorded.
     * @param jmsTemplate 
     * @see {@link LmReportedAddress#isEquivalent}
     */
    public void storeAddressingData(JmsTemplate jmsTemplate, SimpleXPathTemplate data, RfnDevice device);

}