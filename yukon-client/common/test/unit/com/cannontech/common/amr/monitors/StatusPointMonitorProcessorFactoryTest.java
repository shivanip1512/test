package com.cannontech.common.amr.monitors;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.amr.monitors.impl.StatusPointMonitorProcessorFactory;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorMessageProcessor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorStateType;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.message.dispatch.message.PointData;

public class StatusPointMonitorProcessorFactoryTest {
    
    //See technical release doc for a state machine chart that will make following these tests much easier
    
    @Test
    public void test_messageProcessor_sendMessage1() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage2() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage2_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage3() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState("0");
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage3_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState("0");
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage4() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage4_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage5() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage5_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage6() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("1");
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage6_false1() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("1");
        
        PointValueHolder pointPrev = createPoint(2);
        PointValueHolder pointNext = createPoint(2);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage6_false2() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("1");
        
        PointValueHolder pointPrev = createPoint(1);
        PointValueHolder pointNext = createPoint(2);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage7() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage7_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        PointValueHolder pointPrev = createPoint(3);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage8() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage8_false1() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(0);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage8_false2() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        PointValueHolder pointPrev = createPoint(1);
        PointValueHolder pointNext = createPoint(2);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage9() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(2);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage9_false1() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointPrev = createPoint(0);
        PointValueHolder pointNext = createPoint(1);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage9_false2() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointPrev = createPoint(1);
        PointValueHolder pointNext = createPoint(2);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.shouldSendMessage(processor, pointNext, pointPrev);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage_nullCheck1() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState("2");
        
        PointValueHolder pointNext = createPoint(2);
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.isDifference(pointNext, null);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_sendMessage_nullCheck2() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.isExactMatch(processor.getPrevStateInt(), null);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    
    
    @Test
    public void test_messageProcessor_needPrevValue1_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue2_false() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState("3");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(false, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue1() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DONT_CARE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue2() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue3() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue4() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState(StatusPointMonitorStateType.DIFFERENCE.name());
        processor.setNextState("0");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue5() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("1");
        processor.setNextState(StatusPointMonitorStateType.DONT_CARE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue6() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("3");
        processor.setNextState(StatusPointMonitorStateType.DIFFERENCE.name());
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    @Test
    public void test_messageProcessor_needPrevValue7() {
        
        StatusPointMonitorMessageProcessor processor = new StatusPointMonitorMessageProcessor();
        processor.setPrevState("0");
        processor.setNextState("1");
        
        boolean shouldSendMessage = StatusPointMonitorProcessorFactory.needPreviousValue(processor);
        Assert.assertEquals(true, shouldSendMessage);
    }
    
    private PointData createPoint(double value) {
        PointData pointDataMsg = new PointData();
        pointDataMsg.setValue(value);
        return pointDataMsg;
    }
}
