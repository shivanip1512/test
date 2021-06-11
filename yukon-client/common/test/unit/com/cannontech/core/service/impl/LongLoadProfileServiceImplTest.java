package com.cannontech.core.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDaoAdapter;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ScheduledExecutorMock;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;

public class LongLoadProfileServiceImplTest {
    private final class PorterConnection implements BasicServerConnection {
        public Set<MessageListener> listeners = new HashSet<MessageListener>();
        private final BlockingQueue<Message> writtenOut = new LinkedBlockingQueue<Message>();
        private boolean failMode = false;

        @Override
        public void addMessageListener(MessageListener listener) {
            listeners.add(listener);
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public void queue(Message o) {
            writtenOut.add(o);
        }

        @Override
        public void removeMessageListener(MessageListener l) {
            listeners.remove(l);
        }

        @Override
        public void write(Message o) {
            if (failMode) {
                throw new ConnectionException("Unable to write message");
            }
            writtenOut.add(o);
        }
        
        public void respond(Message m) {
            for (MessageListener listener : listeners) {
                listener.messageReceived(new MessageEvent(this,m));
            }
        }
    }
    
    private class PorterQueueDataServiceMock implements PorterQueueDataService {
        private long returnValue = 0;
        public void setReturnValue(long returnValue) {
            this.returnValue = returnValue;
        }
        @Override
        public long getMessageCountForRequest(long requestId) {
            return returnValue;
        }
    }

    private LoadProfileService service;
    private LoadProfileServiceImpl serviceDebug;
    private PorterConnection porterConnection;
    private int successRan = 0;
    private int failureRan = 0;
    private int cancelRan = 0;

    private final LoadProfileService.CompletionCallback incrementingRunner = new LoadProfileService.CompletionCallback() {
        @Override
        public String onFailure(int returnStatus, String resultString) {
            failureRan++;
            return resultString;
        }

        @Override
        public void onSuccess(String successInfo) {
            successRan++;
        }
        
        @Override
        public void onCancel(LiteYukonUser cancelUser) {
            cancelRan++;
        }
    };
    private ScheduledExecutorMock scheduledExecutorMock;
    private PorterQueueDataServiceMock queueDataService;

    @BeforeEach
    public void setUp() throws Exception {
        System.setProperty("java.locale.providers", "COMPAT,SPI");
        serviceDebug = new LoadProfileServiceImpl();
        scheduledExecutorMock = new ScheduledExecutorMock(true);
        ReflectionTestUtils.setField(serviceDebug, "executor", scheduledExecutorMock);
        queueDataService = new PorterQueueDataServiceMock();
        ReflectionTestUtils.setField(serviceDebug, "queueDataService", queueDataService);
        porterConnection = new PorterConnection();
        serviceDebug.setPorterConnection(porterConnection);
        serviceDebug.initialize();

        ReflectionTestUtils.setField(serviceDebug, "dateFormattingService", new DateFormattingServiceImpl());
        ReflectionTestUtils.setField(serviceDebug, "systemDateFormattingService", new SystemDateFormattingServiceImpl() {
            @Override
            public TimeZone getSystemTimeZone() throws BadConfigurationException {
                return TimeZone.getDefault();
            }
        });
        ReflectionTestUtils.setField(serviceDebug, "activityLoggerService", new ActivityLoggerServiceImpl(){
            @Override
            public void logEvent(int userID, String action, String description){
                // do nothing
            }
        });
        ReflectionTestUtils.setField(serviceDebug, "paoDefinitionDao", new PaoDefinitionDaoAdapter() {
            @Override
            public boolean isTagSupported(PaoType deviceType, PaoTag tag) {
                return true;
            }
        });

        successRan = 0;
        failureRan = 0;
        cancelRan = 0;
        this.service = serviceDebug;
    }
    
    @AfterEach
    public void tearDown() {
    	Locale.setDefault(Locale.US);
    }

    public void testInitialize() {
        // initialize was already called
        assertEquals(1, porterConnection.listeners.size(), "wrong number of listeners");
    }
    
    
    //Tests to make sure the command string is correct in a given locale
    private void testCommandStringWithStart(Locale locale) throws ParseException {
        Locale.setDefault(locale);
        LiteYukonPAObject myDevice = 
        	new LiteYukonPAObject(5, 
        			"Test Device Id:5", 
        			PaoType.MCT410IL,
        			CtiUtilities.STRING_NONE,
        			"N");	 // five is arbitrary

        int channel = 4;
        
        //Using this DateFormat so that the date and time are being parsed the way porter would, regardless of locale
        DateFormat dateTimeInstance = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date start = dateTimeInstance.parse("5/5/05 4:30 pm");
        Date stop = dateTimeInstance.parse("10/9/06 1:50 am");
        service.initiateLoadProfile(myDevice, channel, start, stop, incrementingRunner, YukonUserContext.system);
        
        // get message that was written
        Message message = porterConnection.writtenOut.remove();
        Request reqMsg = (Request)message; // implicit instanceof check
        
        // check command string
        String expectedCmd = "getvalue lp channel 4 05/05/2005 04:30 10/09/2006 01:50";
        assertEquals(expectedCmd, reqMsg.getCommandString(), "command is different than expected");
    }
    
    //Same test performed in multiple locales to ensure same functionality
    @Test
    public void testCommandStringWithStart_en_US() throws ParseException {
        testCommandStringWithStart(Locale.US);
    }
    
    @Test
    public void testCommandStringWithStart_fr_CA() throws ParseException {
        testCommandStringWithStart(Locale.CANADA_FRENCH);
    }
    
    @Test
    public void testCommandStringWithStart_pt_BR() throws ParseException {
        testCommandStringWithStart(new Locale("pt", "BR"));
    }
    
    private void testInitiateLongLoadProfileBasic(Locale locale) throws ParseException {
        Locale.setDefault(locale);
        // check that outQueue is empty
        assertEquals(0, porterConnection.writtenOut.size(), "out queue should be empty");
        LiteYukonPAObject myDevice = 
            new LiteYukonPAObject(5, 
                    "Test Device Id:5", 
                    PaoType.MCT410IL,
                    CtiUtilities.STRING_NONE,
                    "N");    // five is arbitrary

        int channel = 1;
        
        //Using this DateFormat so that the date and time are being parsed the way porter would, regardless of locale
        DateFormat dateTimeInstance = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date start = dateTimeInstance.parse("10/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        service.initiateLoadProfile(myDevice, channel, start, stop, incrementingRunner, YukonUserContext.system);
        
        // check that outQueue has one message
        assertEquals(1, porterConnection.writtenOut.size(), "out queue should have one message");
        
        // check that runner hasn't run
        assertEquals(0, successRan, "runner should not have run");
        
        // get message that was written
        Message message = porterConnection.writtenOut.remove();
        Request reqMsg = (Request)message; // implicit instanceof check
        
        // check command string
        String expectedCmd = "getvalue lp channel 1 10/13/2006 01:50 12/13/2006 01:50";
        assertEquals(expectedCmd, reqMsg.getCommandString(), "command is different than expected");
        
        // send expect more response
        Return retMsg = createReturn(reqMsg, true);
        porterConnection.respond(retMsg);
        
        // check that runner hasn't run
        assertEquals(0, successRan, "runner should not have run");
        
        // send final response
        retMsg.setExpectMore(0);
        porterConnection.respond(retMsg);
        
        // check that runner has run
        assertEquals(1, successRan, "runner should have run");
    }

    @Test
    public void testInitiateLongLoadProfileBasic_en_US() throws ParseException {
        testInitiateLongLoadProfileBasic(Locale.US);
    }
    
    @Test
    public void testInitiateLongLoadProfileBasic_fr_CA() throws ParseException {
        testInitiateLongLoadProfileBasic(Locale.CANADA_FRENCH);
    }
    
    @Test
    public void testInitiateLongLoadProfileBasic_pt_BR() throws ParseException {
        testInitiateLongLoadProfileBasic(new Locale("pt", "BR"));
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
        LiteYukonPAObject myDevice1 = 
        	new LiteYukonPAObject(5, 
        			"Test Device Id:5", 
        			PaoType.MCT410IL,
        			CtiUtilities.STRING_NONE,
        			"N");	 // five is arbitrary

        LiteYukonPAObject myDevice2 = 
        	new LiteYukonPAObject(8, 
        			"Test Device Id:8", 
        			PaoType.MCT410IL,
        			CtiUtilities.STRING_NONE,
        			"N");	 // eight is arbitrary

        int channel = 1;
        DateFormat dateTimeInstance = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date start = dateTimeInstance.parse("12/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
        
        // check that outQueue has one message
        assertEquals(1, porterConnection.writtenOut.size(), "out queue should have one message");
        
        // check that runner hasn't run
        assertEquals(0, successRan, "runner should not have run");
        
        // attempt to request again
        service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
        
        // check that outQueue still has one message
        assertEquals(1, porterConnection.writtenOut.size(), "out queue should have one message");
        
        // attempt to request for device 2
        service.initiateLoadProfile(myDevice2, channel, start, stop, incrementingRunner, YukonUserContext.system);
        
        assertEquals(2, porterConnection.writtenOut.size(), "out queue should have two messages");
        
        // attempt to request again for device 1
        service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
        
        // check that outQueue still has two messages
        assertEquals(2, porterConnection.writtenOut.size(), "out queue should have two messages");
        
        // check that runner still hasn't run
        assertEquals(0, successRan, "runner should not have run");
        
        assertEquals(3, service.getPendingLoadProfileRequests(myDevice1).size(), "pending list should have three messages");
        assertEquals(1, service.getPendingLoadProfileRequests(myDevice2).size(), "pending list should have one message");

        int responses = 0;
        Set<Long> usedIds = new HashSet<Long>();
        // suck up all messages
        while (!porterConnection.writtenOut.isEmpty()) {
            // get message that was written
            Message message = porterConnection.writtenOut.remove();
            Request reqMsg = (Request)message; // implicit instanceof check
            
            // check that id is unique
            boolean uniq = usedIds.add(reqMsg.getUserMessageID());
            assertTrue(uniq, "Non unique userMessageId " + reqMsg.getUserMessageID() + " in " + usedIds);
            
            // send final response
            Return retMsg = createReturn(reqMsg, false);
            porterConnection.respond(retMsg);
            responses++;
            
            // check that runner has run
            assertEquals(responses, successRan, "runner should have run");
        }
        
        assertEquals(4, responses, "wrong number of responses sent");

        // did we leak anything?
        assertEquals(0, serviceDebug.debugSizeOfCollections(), "still holding data");
    }
    
    @Test
    public void testWriteError() throws ParseException {
        // five is arbitrary
        LiteYukonPAObject myDevice1 =
            new LiteYukonPAObject(5, "Test Device Id:5", PaoType.MCT410IL, CtiUtilities.STRING_NONE, "N");

        // eight is arbitrary
        new LiteYukonPAObject(8, "Test Device Id:8", PaoType.MCT410IL, CtiUtilities.STRING_NONE, "N");

        int channel = 1;
        DateFormat dateTimeInstance = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date start = dateTimeInstance.parse("12/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        
        // turn connection failure on
        porterConnection.failMode = true;
        
        try {
            service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
            fail("should have thrown an exception");
        } catch (RuntimeException e) {
            // expected
        }
        assertEquals(1, service.getPendingLoadProfileRequests(myDevice1).size(), "Profile requests list should have 1 messages");
        
        porterConnection.failMode = false;
        
        // this should not fail and it shouldn't cause the service to hold the message
        // because its deviceId is the same as the previous
        try {
            service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
        } catch (RuntimeException e) {
            fail("should have not thrown an exception");
        }
        
        assertEquals(1, porterConnection.writtenOut.size(), "out queue should have one message");
        
        assertEquals(0, successRan, "runner should not have run");
    }

    @Test
    public void testPorterTimeout() throws ParseException, Exception {
        LiteYukonPAObject myDevice1 = 
        	new LiteYukonPAObject(5, 
        			"Test Device Id:5", 
        			PaoType.MCT410IL,
        			CtiUtilities.STRING_NONE,
        			"N");	 // five is arbitrary
        
        LiteYukonPAObject myDevice2 = 
        	new LiteYukonPAObject(8, 
        			"Test Device Id:6", 
        			PaoType.MCT410IL,
        			CtiUtilities.STRING_NONE,
        			"N");	 // eight is arbitrary

        int channel = 1;
        DateFormat dateTimeInstance = new SimpleDateFormat("MM/dd/yy hh:mm");
        Date start = dateTimeInstance.parse("12/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");


        service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
        service.initiateLoadProfile(myDevice2, channel, start, stop, incrementingRunner, YukonUserContext.system);
        service.initiateLoadProfile(myDevice1, channel, start, stop, incrementingRunner, YukonUserContext.system);
        service.initiateLoadProfile(myDevice2, channel, start, stop, incrementingRunner, YukonUserContext.system);
        assertEquals(2, porterConnection.writtenOut.size(), "wrong number of messages reached out queue");
        assertEquals(2, service.getPendingLoadProfileRequests(myDevice1).size(), "messages weren't queued");
        assertEquals(2, service.getPendingLoadProfileRequests(myDevice2).size(), "messages weren't queued");
        assertEquals(0, successRan, "runner should not have run");
        assertEquals(0, failureRan, "runner should not have run");
        assertEquals(0, cancelRan, "runner should not have run");

        // eat the two requests
        porterConnection.writtenOut.remove(); 
        porterConnection.writtenOut.remove(); 

        // some time has passed and porter hasn't responded, run timers
        queueDataService.setReturnValue(0); // forgotten messagess...
        scheduledExecutorMock.doAllTasks();

        // this should have queued two LLP request
        assertEquals(2, porterConnection.writtenOut.size(), "wrong number of messages reached out queue");
        //assertEquals("messages weren't queued", 1, service.getPendingLoadProfileRequests(myDevice1).size());
        assertEquals(0, successRan, "runner should not have run");
        assertEquals(2, failureRan, "runner should not have run");
        
        // respond to each
        Message message = porterConnection.writtenOut.remove();
        Request reqMsg = (Request)message; // implicit instanceof check
        Return retMsg = createReturn(reqMsg, true);
        porterConnection.respond(retMsg);

        message = porterConnection.writtenOut.remove();
        reqMsg = (Request)message; // implicit instanceof check
        retMsg = createReturn(reqMsg, true);
        porterConnection.respond(retMsg);
        
        scheduledExecutorMock.doAllTasks();
        // even though the queueDataService is still set to return 0, nothing should change
        assertEquals(0, porterConnection.writtenOut.size(), "wrong number of messages reached out queue");
        assertEquals(0, successRan, "runner should not have run");
        assertEquals(2, failureRan, "runner should not have run");
        
        // more time has passed, but this time no expect more messages have been received, however porter is still working...
        queueDataService.setReturnValue(1);
        scheduledExecutorMock.doAllTasks();
       
        // nothing should change
        assertEquals(0, porterConnection.writtenOut.size(), "wrong number of messages reached out queue");
        assertEquals(0, successRan, "runner should not have run");
        assertEquals(2, failureRan, "runner should not have run");

        
        // but all good things must come to an end
        queueDataService.setReturnValue(0);
        scheduledExecutorMock.doAllTasks();
        
        // nothing should be left
        assertEquals(0, porterConnection.writtenOut.size(), "wrong number of messages reached out queue");
        assertEquals(0, successRan, "runner should not have run");
        assertEquals(4, failureRan, "runner should not have run");

        // did we leak anything?
        assertEquals(0, serviceDebug.debugSizeOfCollections(), "still holding data");
    }
}

