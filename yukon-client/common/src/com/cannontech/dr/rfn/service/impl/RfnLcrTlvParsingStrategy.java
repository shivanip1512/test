package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.jms.ConnectionFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.service.RfnDataValidator;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.ParseException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.ByteUtil;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.rfn.dao.PqrEventDao;
import com.cannontech.dr.rfn.message.archive.RfnLcrArchiveRequest;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingArchiveRequest;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.service.ParsingService;
import com.cannontech.dr.rfn.service.ParsingService.Schema;
import com.cannontech.dr.rfn.service.PqrEventParsingService;
import com.cannontech.dr.rfn.service.RfnLcrDataMappingService;
import com.cannontech.dr.rfn.service.RfnLcrParsingStrategy;
import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.dr.rfn.tlv.FieldType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class RfnLcrTlvParsingStrategy implements RfnLcrParsingStrategy {
    @Autowired private InventoryDao inventoryDao;
    @Autowired private ParsingService<ListMultimap<FieldType, byte[]>> parsingService;
    @Autowired private RfnLcrDataMappingService<ListMultimap<FieldType, byte[]>> rfnLcrDataMappingService;
    @Autowired private RfnPerformanceVerificationService rfnPerformanceVerificationService;
    @Autowired protected AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private PqrEventDao pqrEventDao;
    @Autowired private PqrEventParsingService pqrEventLogParsingService;
    protected JmsTemplate jmsTemplate;

    private static final Logger log = YukonLogManager.getLogger(RfnLcrTlvParsingStrategy.class);

    @Override
    public void parseRfLcrReading(RfnLcrArchiveRequest request, RfnDevice rfnDevice,
            AtomicInteger archivedReadings) throws ParseException {
        RfnLcrReadingArchiveRequest reading = ((RfnLcrReadingArchiveRequest) request);
        ListMultimap<FieldType, byte[]> decodedPayload = null;

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

        Instant payloadTime = new Instant(ByteUtil.getLong(decodedPayload.get(FieldType.UTC).get(0)) * 1000);
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

            // In LCR 6700, received addressing fields in data packet only if there is change in field value. 
            // if any addressing field present in data packet then process it otherwise not
            Set<FieldType> commonAddressingFields = decodedPayload.keySet().stream()
                                                                           .filter(FieldType.getAddressingFieldTypes()::contains)
                                                                           .collect(Collectors.toSet());

            if (CollectionUtils.isNotEmpty(commonAddressingFields)) {
                rfnLcrDataMappingService.storeAddressingData(jmsTemplate, decodedPayload, rfnDevice);
            }
            
            if(schema.supportsPowerQualityResponse()) {
                int inventoryId = inventoryDao.getYukonInventoryForDeviceId(rfnDevice.getPaoIdentifier().getPaoId())
                                              .getInventoryId();
                List<byte[]> pqrLogBlob = decodedPayload.get(FieldType.POWER_QUALITY_RESPONSE_LOG_BLOB);
                if (pqrLogBlob.size() > 0) {
                    List<PqrEvent> pqrEvents = pqrEventLogParsingService.parseLogBlob(inventoryId, pqrLogBlob.get(0));
                    pqrEventDao.saveEvents(pqrEvents);
                }
            }
        } else {
            log.warn("Discarding invalid or old pointdata for device " + rfnDevice + " with timestamp " + payloadTime);
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
        schemas.add(Schema.SCHEMA_0_0_4);
        schemas.add(Schema.SCHEMA_0_0_5);
        return schemas;
    }

}
