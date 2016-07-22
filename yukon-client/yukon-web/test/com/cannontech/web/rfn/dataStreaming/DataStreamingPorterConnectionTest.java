package com.cannontech.web.rfn.dataStreaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.bulk.callbackResult.DataStreamingConfigCallback;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.impl.PorterCommandCallback;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dev.dataStreaming.DataStreamingDevSettings;
import com.cannontech.web.dev.dataStreaming.FakeDataStreamingCommandRequestDeviceExecutor;
import com.google.common.collect.Lists;

public class DataStreamingPorterConnectionTest {
    
    @Test
    public void test_buildConfigurationCommandRequests() {
        DataStreamingPorterConnection porterConn = new DataStreamingPorterConnection();
        
        //Create a couple devices
        SimpleDevice device1 = new SimpleDevice(123, PaoType.RFN420CD);
        SimpleDevice device2 = new SimpleDevice(124, PaoType.RFN420FL);
        Collection<SimpleDevice> devices = Lists.newArrayList(device1, device2);
        
        List<CommandRequestDevice> commands = porterConn.buildConfigurationCommandRequests(devices);
        
        //Check that the commands look good:
        //1. Are there the correct number of commands?
        Assert.assertEquals("Incorrect number of commands", 2, commands.size());
        
        //2. Is the first command correct?
        CommandRequestDevice crd1 = commands.get(0);
        Assert.assertEquals("Incorrect device id in command 1", 123, crd1.getDevice().getDeviceId());
        Assert.assertEquals("Incorrect device type in command 1", PaoType.RFN420CD, crd1.getDevice().getDeviceType());
        Assert.assertEquals("Incorrect command in command 1", "putconfig behavior rfndatastreaming", crd1.getCommandCallback().getGeneratedCommand());
        
        //3. Is the second command correct?
        CommandRequestDevice crd2 = commands.get(1);
        Assert.assertEquals("Incorrect device id in command 2", 124, crd2.getDevice().getDeviceId());
        Assert.assertEquals("Incorrect device type in command 2", PaoType.RFN420FL, crd2.getDevice().getDeviceType());
        Assert.assertEquals("Incorrect command in command 1", "putconfig behavior rfndatastreaming", crd2.getCommandCallback().getGeneratedCommand());
    }
    
    @Test
    public void test_buildConfigurationCommandRequests_withEmptyDeviceList() {
        DataStreamingPorterConnection porterConn = new DataStreamingPorterConnection();
        
        // Use an empty device list
        List<CommandRequestDevice> commands = porterConn.buildConfigurationCommandRequests(new ArrayList<>());
        
        //Check that an empty list of commands is returned
        Assert.assertEquals("Incorrect number of commands", 0, commands.size());
    }
    
    @Test
    public void test_sendConfiguration_withFakeExecutor() {
        DataStreamingPorterConnection porterConn = new DataStreamingPorterConnection();
        
        // Initialize with the fake dev mode executor
        MockExecutor mockExecutor = new MockExecutor();
        ReflectionTestUtils.setField(porterConn, "fakeCommandExecutor", mockExecutor);
        
        // Set dev settings to use the fake executor
        DataStreamingDevSettings settings = new DataStreamingDevSettings();
        settings.setSimulatePorterConfigResponse(true);
        ReflectionTestUtils.setField(porterConn, "devSettings", settings);
        
        porterConn.sendConfiguration(new ArrayList<>(), null, null);
        Assert.assertTrue("Response simulation was set to true, but fake executor was not called.", mockExecutor.wasCalled);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void test_sendConfiguration_withRealExecutor() {
        DataStreamingPorterConnection porterConn = new DataStreamingPorterConnection();
        
        // Set the fake dev mode executor
        MockExecutor mockFakeExecutor = new MockExecutor();
        ReflectionTestUtils.setField(porterConn, "fakeCommandExecutor", mockFakeExecutor);
        
        // Set the "real" executor
        MockExecutor mockRealExecutor = new MockExecutor();
        ReflectionTestUtils.setField(porterConn, "commandExecutor", mockRealExecutor);
        
        // Set dev settings to use the real executor
        DataStreamingDevSettings settings = new DataStreamingDevSettings();
        settings.setSimulatePorterConfigResponse(false);
        ReflectionTestUtils.setField(porterConn, "devSettings", settings);
        
        // Build commands
        List<CommandRequestDevice> commands = new ArrayList<>();
        CommandCallback commandCallback = new PorterCommandCallback("putconfig behavior rfndatastreaming");
        
        SimpleDevice device1 = new SimpleDevice(123, PaoType.RFN420CD);
        CommandRequestDevice command1 = new CommandRequestDevice();
        command1.setDevice(device1);
        command1.setCommandCallback(commandCallback);
        commands.add(command1);
        
        SimpleDevice device2 = new SimpleDevice(124, PaoType.RFN420FL);
        CommandRequestDevice command2 = new CommandRequestDevice();
        command2.setDevice(device2);
        command2.setCommandCallback(commandCallback);
        commands.add(command2);
        
        // Set mock callback
        MockDataStreamingConfigCallback innerCallback = new MockDataStreamingConfigCallback();
        
        porterConn.sendConfiguration(commands, innerCallback, YukonUserContext.system.getYukonUser());
        
        //Check that fake executor was not called
        Assert.assertFalse("Response simulation was set to false, but fake executor was called.", mockFakeExecutor.wasCalled);
        
        //Check that "real" executor was called
        Assert.assertTrue("Response simulation was set to false, but real executor was not called.", mockRealExecutor.wasCalled);
        
        //Check that executor arguments are correct
        Assert.assertEquals("Incorrect user passed to executor.", mockRealExecutor.user, YukonUserContext.system.getYukonUser());
        Assert.assertEquals("Incorrect device request type passed to executor.", mockRealExecutor.type, DeviceRequestType.DATA_STREAMING_CONFIG);
        Assert.assertEquals("Incorrect commands passed to executor.", mockRealExecutor.commands, commands);
        
        //Check that callback works
        Assert.assertFalse("Callback completed before complete() called.", innerCallback.isComplete);
        Assert.assertFalse("Callback canceled before cancel() called.", innerCallback.isCancelled); 
        CommandCompletionCallback executorCallback = mockRealExecutor.callback;
        
        //1. Check cancel
        executorCallback.cancel();
        //Assert.assertTrue("Callback wasn't canceled when cancel() called.", innerCallback.isCancelled);
        
        //2. Check complete
        executorCallback.complete();
        Assert.assertTrue("Callback wasn't completed when complete() called.", innerCallback.isComplete);
        
        //Tests #3 & 4 are commented out because I haven't figured out how to mock up the dependencies yet.
        //3. check receivedIntermediateError 
//        SpecificDeviceErrorDescription error1 = new SpecificDeviceErrorDescription(null, null);
//        executorCallback.receivedIntermediateError(command1, error1);
//        Assert.assertEquals("Inner callback wasn't called with correct device.", innerCallback.lastDeviceHeard, command1.getDevice());
//        Assert.assertEquals("Inner callback wasn't called with correct error.", innerCallback.lastErrorHeard, error1);
        
        //4. Check receivedLastError
//        SpecificDeviceErrorDescription error2 = new SpecificDeviceErrorDescription(null, null);
//        executorCallback.receivedLastError(command1, error2);
//        Assert.assertEquals("Inner callback wasn't called with correct device.", innerCallback.lastDeviceHeard, command1.getDevice());
//        Assert.assertEquals("Inner callback wasn't called with correct error.", innerCallback.lastErrorHeard, error2);
        
        //5. Check receivedIntermediateResultString
       /* String resultString = "{\"streamingEnabled\" : true,\"configuredMetrics\" : [{\"attribute\" : \"DEMAND\",\"interval\" : 5,\"enabled\" : true}],\"sequence\" : 3735928559}";
        executorCallback.receivedIntermediateResultString(command2, resultString);
        Assert.assertEquals("Inner callback wasn't called with correct device.", innerCallback.lastDeviceHeard, command2.getDevice());
        Assert.assertTrue("Inner callback received incorrect reported config - streaming not enabled.", innerCallback.lastConfigHeard.isStreamingEnabled());
        
        List<ReportedDataStreamingAttribute> reportedAttributes = innerCallback.lastConfigHeard.getConfiguredMetrics();
        Assert.assertEquals("Inner callback received incorrect reported config - wrong number of attributes.", reportedAttributes.size(), 1);
        String attribute = reportedAttributes.get(0).getAttribute();
        Assert.assertEquals("Inner callback received incorrect reported config - wrong attribute.", attribute, "DEMAND");
        int interval = reportedAttributes.get(0).getInterval();
        Assert.assertEquals("Inner callback received incorrect reported config - wrong attribute interval.", interval, 5);
        boolean enabled = reportedAttributes.get(0).isEnabled();
        Assert.assertTrue("Inner callback received incorrect reported config - attribute not enabled.", enabled);
        
        //6. Check receivedLastResultString
        String resultString2 = "{\"streamingEnabled\" : false,\"configuredMetrics\" : [{\"attribute\" : \"VOLTAGE\",\"interval\" : 30,\"enabled\" : false}],\"sequence\" : 3735928559}";
        executorCallback.receivedLastResultString(command2, resultString2);
        Assert.assertEquals("Inner callback wasn't called with correct device.", innerCallback.lastDeviceHeard, command2.getDevice());
        Assert.assertFalse("Inner callback received incorrect reported config - streaming incorrectly enabled.", innerCallback.lastConfigHeard.isStreamingEnabled());
        
        List<ReportedDataStreamingAttribute> reportedAttributes2 = innerCallback.lastConfigHeard.getConfiguredMetrics();
        Assert.assertEquals("Inner callback received incorrect reported config - wrong number of attributes.", reportedAttributes2.size(), 1);
        String attribute2 = reportedAttributes2.get(0).getAttribute();
        Assert.assertEquals("Inner callback received incorrect reported config - wrong attribute.", attribute2, "VOLTAGE");
        int interval2 = reportedAttributes2.get(0).getInterval();
        Assert.assertEquals("Inner callback received incorrect reported config - wrong attribute interval.", interval2, 30);
        boolean enabled2 = reportedAttributes2.get(0).isEnabled();
        Assert.assertFalse("Inner callback received incorrect reported config - attribute not enabled.", enabled2);*/
    }
    
    /**
     * Simple mock that can test whether or not its primary method was called.
     */
    private class MockExecutor extends FakeDataStreamingCommandRequestDeviceExecutor {
        public boolean wasCalled = false;
        public List<CommandRequestDevice> commands;
        public CommandCompletionCallback<? super CommandRequestDevice> callback;
        public DeviceRequestType type;
        public LiteYukonUser user;
        
        public MockExecutor() {
            super(null, null);
        }
        
        @Override
        public CommandRequestExecutionIdentifier execute(List<CommandRequestDevice> commands,
                                                         CommandCompletionCallback<? super CommandRequestDevice> callback,
                                                         DeviceRequestType type, LiteYukonUser user) {
            
            this.commands = commands;
            this.callback = callback;
            this.type = type;
            this.user = user;
            wasCalled = true;
            return null;
        }
    }
    
    private class MockDataStreamingConfigCallback implements DataStreamingConfigCallback {
        public boolean isComplete = false;
        public boolean isCancelled = false;
        public SimpleDevice lastDeviceHeard;
        public ReportedDataStreamingConfig lastConfigHeard;
        public SpecificDeviceErrorDescription lastErrorHeard;
        
        @Override
        public void receivedConfigReport(SimpleDevice device, ReportedDataStreamingConfig config) {
            lastDeviceHeard = device;
            lastConfigHeard = config;
        }

        @Override
        public void receivedConfigError(SimpleDevice device, SpecificDeviceErrorDescription error) {
            lastDeviceHeard = device;
            lastErrorHeard = error;
        }

        @Override
        public void complete() {
            isComplete = true;
        }

        @Override
        public void cancel(LiteYukonUser user) {
            isCancelled = true;
        }

        @Override
        public boolean isComplete() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void processingExceptionOccured(String reason) {
            // TODO Auto-generated method stub
            
        }
    }
}
