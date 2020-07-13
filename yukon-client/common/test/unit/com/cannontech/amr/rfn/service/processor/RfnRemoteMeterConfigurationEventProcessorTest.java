package com.cannontech.amr.rfn.service.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.rfn.message.event.DetailedConfigurationStatusCode;
import com.cannontech.amr.rfn.message.event.MeterConfigurationStatus;
import com.cannontech.amr.rfn.message.event.MeterStatusCode;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.amr.rfn.service.processor.impl.RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.jms.ThriftRequestTemplate;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRemoteMeterConfigurationEventProcessorTest {

    private static final Instant EVENT_TIMESTAMP = new Instant(1594066865000L);  // 2020-Jul-06 20:21:05 GMT
    private static final Instant ARBITRARY_TIMESTAMP = new Instant(1594105200000L);  //  2020-Jul-07 07:00 GMT 

    private RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor processor;
    private ThriftRequestTemplate<MeterProgramStatusArchiveRequest> mockThriftMessenger;
    private Capture<MeterProgramStatusArchiveRequest> requestPayload;
    private final YukonPao yp = () -> PaoIdentifier.of(314159, PaoType.RFN430A3K);
    private final RfnIdentifier rfnId = new RfnIdentifier("Apple", "Banana", "Carrot");
    private final RfnDevice d = new RfnDevice("Test device", yp, rfnId);
    
    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        processor = new RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor();

        mockThriftMessenger = EasyMock.createStrictMock(ThriftRequestTemplate.class);
        EasyMock.expect(mockThriftMessenger.getRequestQueueName())
            .andReturn("Fake queue");
        
        requestPayload = new Capture<>();
        
        mockThriftMessenger.send(EasyMock.capture(requestPayload));
        
        var mockRfnMeterEventService = EasyMock.createNiceMock(RfnMeterEventService.class);
        
        EasyMock.replay(mockThriftMessenger, mockRfnMeterEventService);

        ReflectionTestUtils.setField(processor, "thriftMessenger", mockThriftMessenger);
        ReflectionTestUtils.setField(processor,  "rfnMeterEventService", mockRfnMeterEventService);
    }
    
    @Test
    public void testProgramStatusTimestamp() {
        RfnEvent e = new RfnEvent();
        e.setTimeStamp(EVENT_TIMESTAMP.getMillis());
        var configurationId = "N00000000-0000-0000-0000-000000000000";
        var meterConfigurationStatus = new MeterConfigurationStatus();
        meterConfigurationStatus.setMeterStatusCode(new MeterStatusCode((short) 1));
        meterConfigurationStatus.setDetailedConfigurationStatusCode(new DetailedConfigurationStatusCode((short) 42));
        e.setEventData(Map.of(
                RfnConditionDataType.METER_CONFIGURATION_ID, configurationId,
                RfnConditionDataType.METER_CONFIGURATION_STATUS, meterConfigurationStatus));
        var l = new ArrayList<PointData>();
        
        processor.process(d, e, l, ARBITRARY_TIMESTAMP);
        
        assertTrue("Captured MeterProgramStatusArchiveRequest", 
                requestPayload.hasCaptured());
        assertEquals("MeterProgramStatusArchiveRequest timestamp equals event timestamp",
                requestPayload.getValue().getTimestamp(), EVENT_TIMESTAMP);
        assertEquals(requestPayload.getValue().getError(), DeviceError.CATASTROPHIC_FAILURE);
    }
    
    @Test
    public void testUnknownStatus() {
        RfnEvent e = new RfnEvent();
        e.setTimeStamp(EVENT_TIMESTAMP.getMillis());
        var configurationId = "N00000000-0000-0000-0000-000000000000";
        var meterConfigurationStatus = new MeterConfigurationStatus();
        meterConfigurationStatus.setMeterStatusCode(new MeterStatusCode((short) 1));
        meterConfigurationStatus.setDetailedConfigurationStatusCode(new DetailedConfigurationStatusCode((short) 99));
        e.setEventData(Map.of(
                RfnConditionDataType.METER_CONFIGURATION_ID, configurationId,
                RfnConditionDataType.METER_CONFIGURATION_STATUS, meterConfigurationStatus));
        var l = new ArrayList<PointData>();
        
        processor.process(d, e, l, ARBITRARY_TIMESTAMP);
        
        assertTrue("Captured MeterProgramStatusArchiveRequest", 
                requestPayload.hasCaptured());
        assertEquals("MeterProgramStatusArchiveRequest timestamp equals event timestamp",
                requestPayload.getValue().getTimestamp(), EVENT_TIMESTAMP);
       assertEquals(requestPayload.getValue().getError(), DeviceError.REASON_UNKNOWN);
    }
}