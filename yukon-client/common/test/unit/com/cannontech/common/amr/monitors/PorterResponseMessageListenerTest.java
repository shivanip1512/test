package com.cannontech.common.amr.monitors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.impl.MonitorCacheServiceImpl;
import com.cannontech.amr.monitors.message.PorterResponseMessage;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorTransaction;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMessageListener;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.impl.DeviceGroupServiceImpl;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.StubServerDatabaseCache;
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
    
    private PorterResponseMonitor standard, standard1; //should eventually add another monitor with NOT standard (more specific / odd) rules
    private PorterResponseMessageListener listener;
    private PorterResponseMessage message1;
    private PorterResponseMessage message2;
    private PorterResponseMessage message3;
    private PorterResponseMessage message4;
    private PorterResponseMessage message5;
    private final List<Integer> sentPointData = Lists.newArrayList();
    
    @Before
    public void setup() {
        
        List<PorterResponseMonitorRule> standardRules = Lists.newArrayList();
        standardRules.add(getRule(true, PorterResponseMonitorMatchStyle.any, GOOD));
        standardRules.add(getRule(false, PorterResponseMonitorMatchStyle.any, GOOD_QUESTIONABLE, 1, 17, 74));
        standardRules.add(getRule(false, PorterResponseMonitorMatchStyle.any, QUESTIONABLE));
        
        DeviceGroupService deviceGroupService = new DeviceGroupServiceImpl() {
            @Override
            public DeviceGroup findGroupName(String groupName) {
                return new StoredDeviceGroup();
            }

            @Override
            public boolean isDeviceInGroup(DeviceGroup group, YukonPao pao) {
                return (pao.getPaoIdentifier().getPaoId() != 5);
            }
            
            @Override 
            public String getFullPath(SystemGroupEnum groupName) {
                return "/System/Meters/All Meters/All MCT Meters";
            }

        };
        
        standard = new PorterResponseMonitor(deviceGroupService.getFullPath(SystemGroupEnum.ALL_MCT_METERS));
        standard.setRules(standardRules);
        standard.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);
        
        standard1 = new PorterResponseMonitor(deviceGroupService.getFullPath(SystemGroupEnum.ALL_MCT_METERS));
        standard1.setRules(standardRules);
        standard1.setEvaluatorStatus(MonitorEvaluatorStatus.ENABLED);

        message1 = getMessage(1,1,1);
        message2 = getMessage(2,2,2);
        message3 = getMessage(3,3,3);
        message4 = getMessage(4,4,4);
        message5 = getMessage(5,5,5);
        
        listener = new PorterResponseMessageListener() {
            @Override
            protected void sendPointData(PorterResponseMonitor monitor,
                                         PorterResponseMonitorRule rule,
                                         PorterResponseMonitorTransaction transaction) {
                sentPointData.add(rule.getStateInt());
            }
        };
        MonitorCacheServiceImpl cache = new MonitorCacheServiceImpl();
        listener.setMonitorCache(cache);
        cache.setMonitors(ImmutableMap.of(1, standard,
                                          2, standard1));
        
        final LiteYukonPAObject pao1 = new LiteYukonPAObject(1, "pao1", 
            PaoCategory.DEVICE, 
            PaoClass.METER, 
            PaoType.MCT410CL, 
            "", "");
        
        final LiteYukonPAObject pao4 = new LiteYukonPAObject(4, "pao4", 
            PaoCategory.DEVICE, 
            PaoClass.RFMESH, 
            PaoType.RFN420CL, 
            "", "");
        
        final LiteYukonPAObject pao5 = new LiteYukonPAObject(5, "pao5", 
            PaoCategory.DEVICE, 
            PaoClass.METER, 
            PaoType.MCT430SL, 
            "", "");
        
        final IDatabaseCache databaseCache = new StubServerDatabaseCache() {
            @Override
            public Map<Integer, LiteYukonPAObject> getAllPaosMap() {
                Map<Integer, LiteYukonPAObject> allPaos = new HashMap<>();
                allPaos.put(1, pao1);
                allPaos.put(2, pao1);
                allPaos.put(3, pao1);
                allPaos.put(4, pao4);
                allPaos.put(5, pao5);
                return allPaos;
            }
        };
        
        ReflectionTestUtils.setField(listener, "databaseCache", databaseCache);
        ReflectionTestUtils.setField(listener, "deviceGroupService", deviceGroupService);
}
    
    @Test
    public void test_single_msg_success() {
        sentPointData.clear();

        handleMockMessage(message1, 0, true);
        
        Assert.assertEquals(2, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(0));
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(1));
    }
    
    @Test
    public void test_single_msg_failure() {
        sentPointData.clear();

        handleMockMessage(message1, 74, true);
        
        Assert.assertEquals(2, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(0));
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(1));
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
        Assert.assertEquals(2, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(0));
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(1));
    }
    @Test
    public void test_default_rule2() {
        sentPointData.clear();

        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 17, false);
        handleMockMessage(message1, 74, false);
        handleMockMessage(message1, 300, true);
        Assert.assertEquals(2, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(0));
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(1));
    }
    @Test
    public void test_default_rule3() {
        sentPointData.clear();

        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, false);
        handleMockMessage(message1, 300, true);
        Assert.assertEquals(2, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(QUESTIONABLE), sentPointData.get(0));
        Assert.assertEquals(Integer.valueOf(QUESTIONABLE), sentPointData.get(1));
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
        
        Assert.assertEquals(6, sentPointData.size());
        Assert.assertEquals(Integer.valueOf(QUESTIONABLE), sentPointData.get(0)); //message 3
        Assert.assertEquals(Integer.valueOf(GOOD_QUESTIONABLE), sentPointData.get(2)); //message 2
        Assert.assertEquals(Integer.valueOf(GOOD), sentPointData.get(4)); //message 1
    }
    
    @Test
    public void test_Device() {
        sentPointData.clear();

        handleMockMessage(message4, 0, true);
        Assert.assertEquals(0, sentPointData.size());
    }
    
    @Test
    public void test_MultipleMonitor() {
        sentPointData.clear();

        handleMockMessage(message4, 0, true);
        Assert.assertEquals(0, sentPointData.size());
    }

    @Test
    public void test_Group() {
        sentPointData.clear();

        handleMockMessage(message5, 0, true);
        Assert.assertEquals(0, sentPointData.size());
    }

    private void handleMockMessage(PorterResponseMessage message, int errorCode, boolean isFinalMsg) {
        message.setErrorCode(errorCode);
        message.setFinalMsg(isFinalMsg);
        listener.handleMessage(message);
    }

    private PorterResponseMonitorRule getRule(boolean success, PorterResponseMonitorMatchStyle style, String state, Integer... errorCodes) {
        List<PorterResponseMonitorErrorCode> errorCodesList = Lists.newArrayList();
        for (Integer errorCode: errorCodes) {
            PorterResponseMonitorErrorCode error = new PorterResponseMonitorErrorCode();
            error.setErrorCode(errorCode);
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
