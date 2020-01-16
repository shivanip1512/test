package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.service.RfnDataValidator;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.service.ParsingService;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.dr.rfn.service.RfnLcrParsingStrategy;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.dr.rfn.service.ParsingService.Schema;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Lists;

public class RfnLcrExiParsingStrategy implements RfnLcrParsingStrategy {

    @Autowired private ParsingService<SimpleXPathTemplate> parsingService;
    @Autowired private RfnLcrDataMappingService<SimpleXPathTemplate> rfnLcrDataMappingService;
    @Autowired private RfnPerformanceVerificationService rfnPerformanceVerificationService;
    @Autowired protected AsyncDynamicDataSource asyncDynamicDataSource;
    protected JmsTemplate jmsTemplate;

    private static final Logger log = YukonLogManager.getLogger(RfnLcrExiParsingStrategy.class);

    @Override
    public void parseRfLcrReading(RfnLcrArchiveRequest request, RfnDevice rfnDevice, AtomicInteger archivedReadings)
            throws ParseException {
        RfnLcrReadingArchiveRequest reading = ((RfnLcrReadingArchiveRequest) request);
        SimpleXPathTemplate decodedPayload = null;

        byte[] payload = reading.getData().getPayload();

        if (log.isDebugEnabled()) {
            log.debug("decoding: " + rfnDevice);
        }
        decodedPayload = parsingService.parseRfLcrReading(rfnDevice.getRfnIdentifier(), payload);
        // Handle broadcast verification messages
        Schema schema = ParsingService.getSchema(payload);
        if (schema.supportsBroadcastVerificationMessages()) {
            // Verification Messages should be processed even if there is no connection to dispatch.
            Map<Long, Instant> verificationMsgs =
                rfnLcrDataMappingService.mapBroadcastVerificationMessages(decodedPayload);
            Range<Instant> range =
                rfnLcrDataMappingService.mapBroadcastVerificationUnsuccessRange(decodedPayload, rfnDevice);
            rfnPerformanceVerificationService.processVerificationMessages(rfnDevice, verificationMsgs, range);
        }

        Instant payloadTime = new Instant(decodedPayload.evaluateAsLong("/DRReport/@utc") * 1000);
        Instant currentInstant = new Instant();

        // Discard all the data that is older than the global timestamp limit
        if (RfnDataValidator.isTimestampRecent(payloadTime, currentInstant)) {
            // Handle point data
            List<PointData> messagesToSend = Lists.newArrayListWithExpectedSize(16);
            messagesToSend = rfnLcrDataMappingService.mapPointData(reading, decodedPayload);
            asyncDynamicDataSource.putValues(messagesToSend);
            archivedReadings.addAndGet(messagesToSend.size());
            if (log.isDebugEnabled()) {
                log.debug(messagesToSend.size() + " PointDatas generated for RfnLcrReadingArchiveRequest");
            }

            // Handle addressing data
            rfnLcrDataMappingService.storeAddressingData(jmsTemplate, decodedPayload, rfnDevice);
        }

    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }

    @Override
    public List<Schema> getSchema() {
        List<Schema> schemas = new ArrayList<>();
        schemas.add(Schema.SCHEMA_0_0_3);
        schemas.add(Schema.SCHEMA_0_0_2);
        return schemas;
    }

}
