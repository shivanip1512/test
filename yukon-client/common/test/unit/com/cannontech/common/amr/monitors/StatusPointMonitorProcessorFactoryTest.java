package com.cannontech.common.amr.monitors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.cannontech.amr.monitors.impl.StatusPointMonitorProcessorFactory;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorProcessor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorStateType;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.message.dispatch.message.PointData;

public class StatusPointMonitorProcessorFactoryTest {
    
    //See technical release doc for a state machine chart that will make following these tests much easier
    
    @Test
    public void test_processor_sendMessage1() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(0.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage2() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage2_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(0.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage3() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState("0");
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(0.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage3_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState("0");
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage4() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage4_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(0.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage5() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage5_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(0.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage6() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("1");
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage6_false1() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("1");
        
        PointValueHolder pointPrev = createPoint(2.0);
        PointValueHolder pointNext = createPoint(2.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage6_false2() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("1");
        
        PointValueHolder pointPrev = createPoint(1.0);
        PointValueHolder pointNext = createPoint(2.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage7() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage7_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(3.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage8() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage8_false1() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(0.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage8_false2() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(1.0);
        PointValueHolder pointNext = createPoint(2.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage9() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(2.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage9_false1() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointPrev = createPoint(0.0);
        PointValueHolder pointNext = createPoint(1.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage9_false2() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointPrev = createPoint(1.0);
        PointValueHolder pointNext = createPoint(2.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage_nullCheck1() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointNext = createPoint(2.0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.isDifference(pointNext, null);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_sendMessage_nullCheck2() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.isExactMatch(processor.transientGetPrevStateInt(), null);
        assertEquals(false, shouldSendMessage);
    }
    
    
    
    @Test
    public void test_processor_needPrevValue1_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue2_false() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState("3");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue1() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue2() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue3() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue4() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("0");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue5() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("1");
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue6() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("3");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_processor_needPrevValue7() {
        
        StatusPointMonitorProcessor processor = new StatusPointMonitorProcessor();
        processor.setPrevState("0");
        processor.setNextState("1");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        assertEquals(true, shouldSendMessage);
    }
    
    private PointData createPoint(double value) {
        PointData pointDataMsg = new PointData();
        pointDataMsg.setValue(value);
        return pointDataMsg;
    }
}
