package com.cannontech.core.service.impl;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.tools.email.TestEmailService;
import com.cannontech.tools.email.TestEmailService.TestMessage;
import com.cannontech.yukon.BasicServerConnection;

public class PorterConnectionServiceImplTest {
    private final class PorterConnection implements BasicServerConnection {
        public MessageListener listener;
        public int listeners = 0;
        private List<Message> writtenOut = new ArrayList<Message>();
        private boolean failMode = false;

        public void addMessageListener(MessageListener listener) {
            listeners++;
            this.listener = listener;
        }

        public boolean isValid() {
            return false;
        }

        public void queue(Object o) {
            writtenOut.add((Message)o);
        }

        public void removeMessageListener(MessageListener l) {
            fail("remove should never be called");
        }

        public void write(Object o) {
            if (failMode) {
                throw new ConnectionException("Unable to write message");
            } else {
                writtenOut.add((Message)o);
            }
            
        }
        
        public void respond(Message m) {
            listener.messageReceived(new MessageEvent(this,m));
        }
    }

    LongLoadProfileService service;
    private PorterConnection porterConnection;
    private int runnerRan = 0;
    private Runnable incrementingRunner = new Runnable() {
        public void run() {
            runnerRan++;
        }
    };
    private TestEmailService emailFactory;

    @Before
    public void setUp() throws Exception {
        PorterConnectionServiceImpl service = new PorterConnectionServiceImpl();
        emailFactory = new TestEmailService();
        service.setEmailService(emailFactory);
        service.setExecutor(new Executor() {
            public void execute(Runnable command) {
                // just do it
                command.run();
            }
        });
        porterConnection = new PorterConnection();
        service.setPorterConnection(porterConnection);
        service.initialize();
        runnerRan = 0;
        this.service = service;
    }
    
    public void testInitialize() {
        // initialize was already called
        Assert.assertEquals("wrong number of listeners", 1, porterConnection.listeners);
        Assert.assertNotNull("listener is null", porterConnection.listener);
    }
    
    @Test
    public void testSendEmail() throws ParseException {
        LiteYukonPAObject myDevice = new LiteYukonPAObject(5); // five is arbitrary
        myDevice.setType(DeviceTypes.MCT410IL);
        int channel = 4;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("5/05/2005 4:30 pm");
        Date stop = dateTimeInstance.parse("10/9/06 1:50 am");
        service.initiateLongLoadProfile(myDevice, channel, start, stop, "test@example.com");
        
        // get message that was written
        Message message = porterConnection.writtenOut.get(0);
        Request reqMsg = (Request)message; // implicit instanceof check
        
        // send final response
        Return retMsg = createReturn(reqMsg, false);
        porterConnection.respond(retMsg);
        
        List<TestMessage> sentMessages = emailFactory.getSentMessages();
        Assert.assertEquals("only one message should be sent", 1, sentMessages.size());
        
        // check addressee
        TestMessage testMessage = sentMessages.get(0);
        Assert.assertEquals("message to wrong person", "test@example.com", testMessage.address);
        
        // check content
        Assert.assertFalse("subject was blank", StringUtils.isBlank(testMessage.subject));
        Assert.assertFalse("body was blank", StringUtils.isBlank(testMessage.body));
    }
    
    @Test
    public void testCommandStringWithStart() throws ParseException {
        LiteYukonPAObject myDevice = new LiteYukonPAObject(5); // five is arbitrary
        myDevice.setType(DeviceTypes.MCT410IL);
        int channel = 4;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("5/5/05 4:30 pm");
        Date stop = dateTimeInstance.parse("10/9/06 1:50 am");
        service.initiateLongLoadProfile(myDevice, channel, start, stop, incrementingRunner);
        
        // get message that was written
        Message message = porterConnection.writtenOut.get(0);
        Request reqMsg = (Request)message; // implicit instanceof check
        
        // check command string
        String expectedCmd = "getvalue lp channel 4 05/05/2005 16:30 10/09/2006 01:50";
        Assert.assertEquals("command is different than expected", expectedCmd, reqMsg.getCommandString());
    }

    @Test
    public void testInitiateLongLoadProfileBasic() throws ParseException {
        // check that outQueue is empty
        Assert.assertEquals("out queue should be empty", 0, porterConnection.writtenOut.size());
        LiteYukonPAObject myDevice = new LiteYukonPAObject(5); // five is arbitrary
        myDevice.setType(DeviceTypes.MCT410IL);
        int channel = 1;
        Date start = null;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        service.initiateLongLoadProfile(myDevice, channel, start, stop, incrementingRunner);
        
        // check that outQueue has one message
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        // check that runner hasn't run
        Assert.assertEquals("runner should not have run", 0, runnerRan);
        
        // get message that was written
        Message message = porterConnection.writtenOut.get(0);
        Request reqMsg = (Request)message; // implicit instanceof check
        
        // check command string
        String expectedCmd = "getvalue lp channel 1 12/13/2006 13:50";
        Assert.assertEquals("command is different than expected", expectedCmd, reqMsg.getCommandString());
        
        // send expect more response
        Return retMsg = createReturn(reqMsg, true);
        porterConnection.respond(retMsg);
        
        // check that runner hasn't run
        Assert.assertEquals("runner should not have run", 0, runnerRan);
        
        // send final response
        retMsg.setExpectMore(0);
        porterConnection.respond(retMsg);
        
        // check that runner has run
        Assert.assertEquals("runner should have run", 1, runnerRan);
    }

    private Return createReturn(Request reqMsg, boolean expectMore) {
        Return retMsg = new Return();
        retMsg.setExpectMore(expectMore ? 1 : 0);
        retMsg.setUserMessageID(reqMsg.getUserMessageID());
        retMsg.setDeviceID(reqMsg.getDeviceID());
        return retMsg;
    }

    @Test
    public void testInitiateLongLoadProfileMultiple() throws ParseException {
        LiteYukonPAObject myDevice1 = new LiteYukonPAObject(5); // five is arbitrary
        myDevice1.setType(DeviceTypes.MCT410IL);
        LiteYukonPAObject myDevice2 = new LiteYukonPAObject(8); // eight is arbitrary
        myDevice2.setType(DeviceTypes.MCT410IL);
        int channel = 1;
        Date start = null;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner);
        
        // check that outQueue has one message
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        // check that runner hasn't run
        Assert.assertEquals("runner should not have run", 0, runnerRan);
        
        // attempt to request again
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner);
        
        // check that outQueue still has one message
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        // attempt to request for device 2
        service.initiateLongLoadProfile(myDevice2, channel, start, stop, incrementingRunner);
        
        Assert.assertEquals("out queue should have two messages", 2, porterConnection.writtenOut.size());
        
        // attempt to request again for device 1
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner);
        
        // check that outQueue still has one message
        Assert.assertEquals("out queue should have one message", 2, porterConnection.writtenOut.size());
        
        // check that runner still hasn't run
        Assert.assertEquals("runner should not have run", 0, runnerRan);
        
        Assert.assertEquals("pending list should have four messages", 3, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("pending list should have four messages", 1, service.getPendingLongLoadProfileRequests(myDevice2).size());

        int responses = 0;
        Set<Long> usedIds = new HashSet<Long>();
        // suck up all messages
        while (!porterConnection.writtenOut.isEmpty()) {
            // get message that was written
            Message message = porterConnection.writtenOut.remove(0);
            Request reqMsg = (Request)message; // implicit instanceof check
            
            // check that id is unique
            boolean uniq = usedIds.add(reqMsg.getUserMessageID());
            Assert.assertTrue("Non unique userMessageId " + reqMsg.getUserMessageID() + " in " + usedIds, uniq);
            
            // send final response
            Return retMsg = createReturn(reqMsg, false);
            porterConnection.respond(retMsg);
            responses++;
            
            // check that runner has run
            Assert.assertEquals("runner should have run", responses, runnerRan);
        }
        
        Assert.assertEquals("wrong number of responses sent", 4, responses);
    }
    
    @Test
    public void testWriteError() throws ParseException {
        LiteYukonPAObject myDevice1 = new LiteYukonPAObject(5); // five is arbitrary
        myDevice1.setType(DeviceTypes.MCT410IL);
        LiteYukonPAObject myDevice2 = new LiteYukonPAObject(8); // eight is arbitrary
        myDevice2.setType(DeviceTypes.MCT410IL);
        int channel = 1;
        Date start = null;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        
        // turn connection failure on
        porterConnection.failMode = true;
        
        try {
            service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner);
            fail("should have thrown an exception");
        } catch (RuntimeException e) {
            // expected
        }
        Assert.assertEquals("pending list should have zero messages", 0, service.getPendingLongLoadProfileRequests(myDevice1).size());
        
        porterConnection.failMode = false;
        
        // this should not fail and it shouldn't cause the service to hold the message
        // because its deviceId is the same as the previous
        try {
            service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner);
        } catch (RuntimeException e) {
            fail("should have not thrown an exception");
        }
        
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        Assert.assertEquals("pending list should have one message", 1, service.getPendingLongLoadProfileRequests(myDevice1).size());
        
        Assert.assertEquals("runner should not have run", 0, runnerRan);
    }
}
