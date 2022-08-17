package com.cannontech.dr.rfn.service;

import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.Range;
import com.cannontech.dr.dao.ExpressComReportedAddress;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.message.dispatch.message.PointData;

public interface RfnLcrDataMappingService <T> {
    
    /**
     * This method extracts point data from an incoming RFN LCR message
     * and returns all available PointData as a List.
     *
     * @param request The incoming request message including encoded payload.
     * @param message The decoded payload.
     * @return A list of all PointData that can be extracted from the decoded message.
     */
    public List<PointData> mapPointData(RfnLcrReadingArchiveRequest request, T data);

    /**
     * Stores the LM addressing data reported by the device if it is not equivalent to the
     * current address recorded or no address is currently recorded.
     * @param jmsTemplate 
     * @see {@link ExpressComReportedAddress#isEquivalent}
     */
    public void storeAddressingData(JmsTemplate jmsTemplate, T data, RfnDevice device);

    /**
     * This method extracts message id and the time the message was received by LCR.
     */
    public Map<Long, Instant> mapBroadcastVerificationMessages(T decodedPayload);

    /**
     * This method creates a date range of earliest start time and the time of reading.
     * @return This method will return null if a valid range is not determined.
     */
    public Range<Instant> mapBroadcastVerificationUnsuccessRange(T data, RfnDevice device);

}