package com.cannontech.yukon.system.metrics.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ConcurrentHashMap;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.yukon.system.metrics.helper.YukonMetricHelper;
import com.cannontech.yukon.system.metrics.message.YukonMetric;

public class RfnLcrReadingArchiveRequestsQueueSizeProducerTest {

    @Test
    public void testWatch_QueueSizeSameAsThresholdValue() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(100L);
        yukonMetricCache.put("yukonMetricKey", metric);

        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "shouldNotifyForThresholdValue", false);
        ReflectionTestUtils.setField(producer, "firstNotifiedTime", new DateTime());
        assertFalse(producer.watch(), "Must be false");
    }

    @Test
    public void testWatch_QueueSizeLessThanThresholdValue() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(99L);
        yukonMetricCache.put("yukonMetricKey", metric);

        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "shouldNotifyForThresholdValue", false);
        ReflectionTestUtils.setField(producer, "firstNotifiedTime", new DateTime());
        assertFalse(producer.watch(), "Must be false");
    }

    @Test
    public void testWatch_QueueSizeGreaterThanThresholdValue_1() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(101L);
        yukonMetricCache.put("yukonMetricKey", metric);

        YukonMetricHelper helper = EasyMock.createMock(YukonMetricHelper.class);
        EasyMock.expect(helper.checkForEscapeValve(EasyMock.anyInt(), EasyMock.anyObject(), EasyMock.anyInt()))
                .andReturn(true);
        EasyMock.replay(helper);

        ReflectionTestUtils.setField(producer, "helper", helper);
        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "shouldNotifyForThresholdValue", false);
        ReflectionTestUtils.setField(producer, "firstNotifiedTime", new DateTime());
        assertTrue(producer.watch(), "Must be True");
    }

    @Test
    public void testWatch_QueueSizeGreaterThanThresholdValue_2() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(101L);
        yukonMetricCache.put("yukonMetricKey", metric);

        YukonMetricHelper helper = EasyMock.createMock(YukonMetricHelper.class);
        EasyMock.expect(helper.checkForEscapeValve(EasyMock.anyInt(), EasyMock.anyObject(), EasyMock.anyInt()))
                .andReturn(false);
        EasyMock.replay(helper);

        ReflectionTestUtils.setField(producer, "helper", helper);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);
        ReflectionTestUtils.setField(producer, "shouldNotifyForThresholdValue", false);
        ReflectionTestUtils.setField(producer, "firstNotifiedTime", new DateTime());
        assertFalse(producer.watch(), "Must be false");
    }

    @Test
    public void testCheckPreviousValue_1() {

        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);

        YukonMetricHelper helper = EasyMock.createMock(YukonMetricHelper.class);
        EasyMock.expect(helper.getQueueSize(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(90L);
        EasyMock.replay(helper);

        ReflectionTestUtils.setField(producer, "helper", helper);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);

        assertNull(producer.checkPreviousValue(), "Must be null");
    }

    @Test
    public void testCheckPreviousValue_2() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(90L);
        yukonMetricCache.put("yukonMetricKey", metric);

        YukonMetricHelper helper = EasyMock.createMock(YukonMetricHelper.class);
        // Previous value was 90L, let's change the current value above threshold. i.e > 100L
        EasyMock.expect(helper.getQueueSize(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(101L);
        EasyMock.replay(helper);

        ReflectionTestUtils.setField(producer, "helper", helper);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);

        // this should get published as its transition from 90 to 101 i.e just before threshold value.
        YukonMetric prevMetric = producer.checkPreviousValue();
        assertNotNull(prevMetric, "Must not be null");
        assertEquals(90L, Long.valueOf(String.valueOf(prevMetric.getValue())));
    }

    @Test
    public void testCheckPreviousValue_3() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(101L);
        yukonMetricCache.put("yukonMetricKey", metric);

        YukonMetricHelper helper = EasyMock.createMock(YukonMetricHelper.class);
        // Previous value was 101L, let's change the current value above threshold. i.e > 100L
        EasyMock.expect(helper.getQueueSize(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(110L);
        EasyMock.replay(helper);

        ReflectionTestUtils.setField(producer, "helper", helper);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);

        // Should be null as both previous and current values are above threshold value
        assertNull(producer.checkPreviousValue(), "Must be null");
    }

    @Test
    public void testCheckPreviousValue_4() {
        RfnLcrReadingArchiveRequestsQueueSizeProducer producer = new RfnLcrReadingArchiveRequestsQueueSizeProducer();
        ConcurrentHashMap<String, YukonMetric> yukonMetricCache = new ConcurrentHashMap<String, YukonMetric>(1);
        YukonMetric metric = new YukonMetric();
        metric.setValue(110L);
        yukonMetricCache.put("yukonMetricKey", metric);

        YukonMetricHelper helper = EasyMock.createMock(YukonMetricHelper.class);
        // Previous value was 110L, let's change the current value below threshold. i.e < 100L
        EasyMock.expect(helper.getQueueSize(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(95L);
        EasyMock.replay(helper);

        ReflectionTestUtils.setField(producer, "helper", helper);
        ReflectionTestUtils.setField(producer, "thresholdValue", 100L);
        ReflectionTestUtils.setField(producer, "yukonMetricCache", yukonMetricCache);

        // this should get published as its transition from 110 to 95 i.e just after threshold value.
        YukonMetric prevMetric = producer.checkPreviousValue();
        assertNotNull(prevMetric, "Must not be null");
        assertEquals(110L, Long.valueOf(String.valueOf(prevMetric.getValue())));
    }
}
