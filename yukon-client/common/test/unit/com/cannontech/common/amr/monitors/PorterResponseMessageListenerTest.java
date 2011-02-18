package com.cannontech.common.amr.monitors;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.message.PorterResponseMessage;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorTransaction;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMessageListener;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class PorterResponseMessageListenerTest {
    
    // Notes:
    // -Errors at or above 300 are "filler" error message and don't represent any specific error

    // STATES
    private final String GOOD = "0";
    private final String GOOD_QUESTIONABLE = "1";
//    private final String BAD = "2";
    private final String QUESTIONABLE = "3";
    
    private PorterResponseMonitor standard; //should eventually add another monitor with NOT standard (more specific / odd) rules
    private PorterResponseMessageListener listener;
    private PorterResponseMessage message1;
    private PorterResponseMessage message2;
    private PorterResponseMessage message3;
    private final List<Integer> sentPointData = Lists.newArrayList();
    
    @Before
    public void setup() {
        
        List<PorterResponseMonitorRule> standardRules = Lists.newArrayList();
        standardRules.add(getRule(true, PorterResponseMonitorMatchStyle.any, GOOD));
        standardRules.add(getRule(false, PorterResponseMonitorMatchStyle.any, GOOD_QUESTIONABLE, 1, 17, 74));
        standardRules.add(getRule(false, PorterResponseMonitorMatchStyle.any, QUESTIONABLE));
        standard = new PorterResponseMonitor();
        standard.setRules(standardRules);
        standard.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);

        message1 = getMessage(1,1,1);
        message2 = getMessage(2,2,2);
        message3 = getMessage(3,3,3);
        
        listener = new PorterResponseMessageListener() {
            @Override
            protected void sendPointData(PorterResponseMonitor monitor,
                                         PorterResponseMonitorRule rule,
                                         PorterResponseMonitorTransaction transaction) {
                sentPointData.add(rule.getStateInt());
            }
        };
        listener.setMonitors(ImmutableMap.of(1, standard));
    }
    
    @Test
    public void test_single_msg_success() {
        sentPointData.clear();

        handleMockMessage(message1, 0, true);
        
        Assert.assertEquals(1, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(0));
    }
    
    @Test
    public void test_single_msg_failure() {
        sentPointData.clear();

        handleMockMessage(message1, 74, true);
        
        Assert.assertEquals(1, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(0));
    }
    
    @Test
    public void test_default_rule1() {
        sentPointData.clear();

        handleMockMessage(message1, 1, false);
        handleMockMessage(message1, 17, false);
        handleMockMessage(message1, 74, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 0, true);
        Assert.assertEquals(1, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(0));
    }
    @Test
    public void test_default_rule2() {
        sentPointData.clear();

        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 17, false);
        handleMockMessage(message1, 74, false);
        handleMockMessage(message1, 300, true);
        Assert.assertEquals(1, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(0));
    }
    @Test
    public void test_default_rule3() {
        sentPointData.clear();

        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, true);
        Assert.assertEquals(1, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(QUESTIONABLE), sentPointData.get(0));
    }
    
    @Test
    public void test_multiple1() {
        sentPointData.clear();

        handleMockMessage(message1, 1, false);
        handleMockMessage(message2, 300, false);
        handleMockMessage(message3, 300, false);
        handleMockMessage(message1, 17, false);
        handleMockMessage(message2, 17, false);
        handleMockMessage(message3, 300, false);
        handleMockMessage(message3, 300, false);
        handleMockMessage(message1, 74, false);
        handleMockMessage(message3, 300, true); // sent QUESTIONABLE
        handleMockMessage(message2, 74, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message2, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message2, 300, false);
        handleMockMessage(message2, 300, false);
        handleMockMessage(message2, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message2, 300, false);
        handleMockMessage(message2, 300, true); // sent GOOD_QUESTIONABLE
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 0, true); // sent GOOD
        
        Assert.assertEquals(3, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(QUESTIONABLE), sentPointData.get(0)); //message 3
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(1)); //message 2
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(2)); //message 1
    }

    private void handleMockMessage(PorterResponseMessage message, int errorCode, boolean isFinalMsg) {
        message.setErrorCode(errorCode);
        message.setFinalMsg(isFinalMsg);
        listener.handleMessage(message);
    }

    private PorterResponseMonitorRule getRule(boolean success, PorterResponseMonitorMatchStyle style, String state, Integer... errorCodes) {
        List<PorterResponseMonitorErrorCode> errorCodesList = Lists.newArrayList();
        for (Integer errorCode: errorCodes) {
            PorterResponseMonitorErrorCode error = new PorterResponseMonitorErrorCode(errorCode);
            errorCodesList.add(error);
        }
        PorterResponseMonitorRule rule = new PorterResponseMonitorRule();
        rule.setSuccess(success);
        rule.setMatchStyle(style);
        rule.setState(state);
        rule.setErrorCodes(errorCodesList);
        return rule;
    }
    
    private PorterResponseMessage getMessage(int userMessageId, int connectionId, int paoId) {
        PorterResponseMessage message = new PorterResponseMessage();
        message.setUserMessageId(userMessageId);
        message.setConnectionId(connectionId);
        message.setPaoId(paoId);
        return message;
    }
}
