package com.cannontech.dr.rfn.service;

import java.util.List;

import org.springframework.jms.core.JmsTemplate;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.messaging.message.dispatch.PointDataMessage;

public interface RfnLcrDataMappingService {
    
    /**
     * This method extracts point data from an incoming RFN LCR message
     * and returns all available PointData as a List.
     *
     * @param request The incoming request message including encoded payload.
     * @param message The decoded payload represented as an XPath template.
     * @return A list of all PointData that can be extracted from the decoded message.
     */
    public List<PointDataMessage> mapPointData(RfnLcrReadingArchiveRequest request, SimpleXPathTemplate data);

    /**
     * Stores the LM addressing data reported by the device if it is not equivalent to the
     * current address recorded or no address is currently recorded.
     * @param jmsTemplate 
     * @see {@link ExpressComReportedAddress#isEquivalent}
     */
    public void storeAddressingData(JmsTemplate jmsTemplate, SimpleXPathTemplate data, RfnDevice device);

}