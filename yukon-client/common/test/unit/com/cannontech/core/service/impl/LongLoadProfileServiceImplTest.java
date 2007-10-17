package com.cannontech.core.service.impl;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.util.ScheduledExecutorMock;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.core.service.PorterQueueDataService;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class LongLoadProfileServiceImplTest {
    private final class PorterConnection implements BasicServerConnection {
        public Set<MessageListener> listeners = new HashSet<MessageListener>();
        private BlockingQueue<Message> writtenOut = new LinkedBlockingQueue<Message>();
        private boolean failMode = false;

        public void addMessageListener(MessageListener listener) {
            listeners.add(listener);
        }

        public boolean isValid() {
            return false;
        }

        public void queue(Object o) {
            writtenOut.add((Message)o);
        }

        public void removeMessageListener(MessageListener l) {
            listeners.remove(l);
        }

        public void write(Object o) {
            if (failMode) {
                throw new ConnectionException("Unable to write message");
            } else {
                writtenOut.add((Message)o);
            }
            
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
        public long getMessageCountForRequest(long requestId) {
            return returnValue;
        }
    }

    private LongLoadProfileService service;
    private LongLoadProfileServiceImpl serviceDebug;
    private PorterConnection porterConnection;
    private int successRan = 0;
    private int failureRan = 0;
    private int cancelRan = 0;
    private LongLoadProfileService.CompletionCallback incrementingRunner = new LongLoadProfileServiceEmailCompletionCallbackImpl(null, null, null) {
        public void onFailure(int returnStatus, String resultString) {
            failureRan++;
        }

        public void onSuccess(String successInfo) {
            successRan++;
        }
        
        public void onCancel(LiteYukonUser cancelUser) {
            cancelRan++;
        }
    };
    private ScheduledExecutorMock scheduledExecutorMock;
    private PorterQueueDataServiceMock queueDataService;

    @Before
    public void setUp() throws Exception {
        serviceDebug = new LongLoadProfileServiceImpl();
        scheduledExecutorMock = new ScheduledExecutorMock(true);
        serviceDebug.setExecutor(scheduledExecutorMock);
        queueDataService = new PorterQueueDataServiceMock();
        serviceDebug.setQueueDataService(queueDataService);
        porterConnection = new PorterConnection();
        serviceDebug.setPorterConnection(porterConnection);
        serviceDebug.initialize();
        
        serviceDebug.setDbPersistentDao(new DBPersistentDao(){

            public void performDBChange(DBPersistent item, int transactionType) {
                // TODO Auto-generated method stub
                
            }

            public DBPersistent retrieveDBPersistent(LiteBase liteObject) {
                

                    return new MCTBase(){
                        
                        @Override
                        public DeviceLoadProfile getDeviceLoadProfile() {
                           
                            DeviceLoadProfile dlp = new DeviceLoadProfile();
                            
                            dlp.setLoadProfileCollection("YYYY");
                            dlp.setLoadProfileDemandRate(900);
                            dlp.setVoltageDmdRate(900);
                            return dlp;
                        }
                    
                    };
            }
        });
        
        serviceDebug.setDateFormattingService(new DateFormattingServiceImpl(){
            
            public GregorianCalendar getGregorianCalendar(LiteYukonUser user){
                return new GregorianCalendar();
            }
            
        });
        
        serviceDebug.setActivityLoggerService(new ActivityLoggerServiceImpl(){
            
            public void logEvent(int userID, String action, String description){
                // do nothing
            }
            
        });
        
        successRan = 0;
        failureRan = 0;
        cancelRan = 0;
        this.service = serviceDebug;
    }
    
    public void testInitialize() {
        // initialize was already called
        Assert.assertEquals("wrong number of listeners", 1, porterConnection.listeners.size());
    }
    
    @Test
    public void testCommandStringWithStart() throws ParseException {
        LiteYukonPAObject myDevice = new LiteYukonPAObject(5); // five is arbitrary
        myDevice.setType(DeviceTypes.MCT410IL);
        int channel = 4;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("5/5/05 4:30 pm");
        Date stop = dateTimeInstance.parse("10/9/06 1:50 am");
        service.initiateLongLoadProfile(myDevice, channel, start, stop, incrementingRunner, new LiteYukonUser());
        
        // get message that was written
        Message message = porterConnection.writtenOut.remove();
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
        myDevice.setCategory(PAOGroups.CAT_DEVICE);
        int channel = 1;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("10/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        service.initiateLongLoadProfile(myDevice, channel, start, stop, incrementingRunner, new LiteYukonUser());
        
        // check that outQueue has one message
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        // check that runner hasn't run
        Assert.assertEquals("runner should not have run", 0, successRan);
        
        // get message that was written
        Message message = porterConnection.writtenOut.remove();
        Request reqMsg = (Request)message; // implicit instanceof check
        
        // check command string
        String expectedCmd = "getvalue lp channel 1 10/13/2006 13:50 12/13/2006 13:50";
        Assert.assertEquals("command is different than expected", expectedCmd, reqMsg.getCommandString());
        
        // send expect more response
        Return retMsg = createReturn(reqMsg, true);
        porterConnection.respond(retMsg);
        
        // check that runner hasn't run
        Assert.assertEquals("runner should not have run", 0, successRan);
        
        // send final response
        retMsg.setExpectMore(0);
        porterConnection.respond(retMsg);
        
        // check that runner has run
        Assert.assertEquals("runner should have run", 1, successRan);
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
        myDevice1.setCategory(PAOGroups.CAT_DEVICE);
        LiteYukonPAObject myDevice2 = new LiteYukonPAObject(8); // eight is arbitrary
        myDevice2.setType(DeviceTypes.MCT410IL);
        myDevice2.setCategory(PAOGroups.CAT_DEVICE);
        int channel = 1;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("12/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
        
        // check that outQueue has one message
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        // check that runner hasn't run
        Assert.assertEquals("runner should not have run", 0, successRan);
        
        // attempt to request again
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
        
        // check that outQueue still has one message
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        // attempt to request for device 2
        service.initiateLongLoadProfile(myDevice2, channel, start, stop, incrementingRunner, new LiteYukonUser());
        
        Assert.assertEquals("out queue should have two messages", 2, porterConnection.writtenOut.size());
        
        // attempt to request again for device 1
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
        
        // check that outQueue still has two messages
        Assert.assertEquals("out queue should have two messages", 2, porterConnection.writtenOut.size());
        
        // check that runner still hasn't run
        Assert.assertEquals("runner should not have run", 0, successRan);
        
        Assert.assertEquals("pending list should have three messages", 3, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("pending list should have one message", 1, service.getPendingLongLoadProfileRequests(myDevice2).size());

        int responses = 0;
        Set<Long> usedIds = new HashSet<Long>();
        // suck up all messages
        while (!porterConnection.writtenOut.isEmpty()) {
            // get message that was written
            Message message = porterConnection.writtenOut.remove();
            Request reqMsg = (Request)message; // implicit instanceof check
            
            // check that id is unique
            boolean uniq = usedIds.add(reqMsg.getUserMessageID());
            Assert.assertTrue("Non unique userMessageId " + reqMsg.getUserMessageID() + " in " + usedIds, uniq);
            
            // send final response
            Return retMsg = createReturn(reqMsg, false);
            porterConnection.respond(retMsg);
            responses++;
            
            // check that runner has run
            Assert.assertEquals("runner should have run", responses, successRan);
        }
        
        Assert.assertEquals("wrong number of responses sent", 4, responses);

        // did we leak anything?
        Assert.assertEquals("still holding data", 0, serviceDebug.debugSizeOfCollections());
    }
    
    @Test
    public void testWriteError() throws ParseException {
        LiteYukonPAObject myDevice1 = new LiteYukonPAObject(5); // five is arbitrary
        myDevice1.setType(DeviceTypes.MCT410IL);
        myDevice1.setCategory(PAOGroups.CAT_DEVICE);
        LiteYukonPAObject myDevice2 = new LiteYukonPAObject(8); // eight is arbitrary
        myDevice2.setType(DeviceTypes.MCT410IL);
        myDevice2.setCategory(PAOGroups.CAT_DEVICE);
        int channel = 1;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("12/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");
        
        // turn connection failure on
        porterConnection.failMode = true;
        
        try {
            service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
            fail("should have thrown an exception");
        } catch (RuntimeException e) {
            // expected
        }
        Assert.assertEquals("pending list should have zero messages", 0, service.getPendingLongLoadProfileRequests(myDevice1).size());
        
        porterConnection.failMode = false;
        
        // this should not fail and it shouldn't cause the service to hold the message
        // because its deviceId is the same as the previous
        try {
            service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
        } catch (RuntimeException e) {
            fail("should have not thrown an exception");
        }
        
        Assert.assertEquals("out queue should have one message", 1, porterConnection.writtenOut.size());
        
        Assert.assertEquals("pending list should have one message", 1, service.getPendingLongLoadProfileRequests(myDevice1).size());
        
        Assert.assertEquals("runner should not have run", 0, successRan);
    }

    @Test
    public void testPorterTimeout() throws ParseException, Exception {
        LiteYukonPAObject myDevice1 = new LiteYukonPAObject(5); // five is arbitrary
        myDevice1.setType(DeviceTypes.MCT410IL);
        myDevice1.setCategory(PAOGroups.CAT_DEVICE);
        LiteYukonPAObject myDevice2 = new LiteYukonPAObject(8); // eight is arbitrary
        myDevice2.setType(DeviceTypes.MCT410IL);
        myDevice2.setCategory(PAOGroups.CAT_DEVICE);
        int channel = 1;
        DateFormat dateTimeInstance = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Date start = dateTimeInstance.parse("12/13/06 1:50 pm");
        Date stop = dateTimeInstance.parse("12/13/06 1:50 pm");


        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
        service.initiateLongLoadProfile(myDevice2, channel, start, stop, incrementingRunner, new LiteYukonUser());
        service.initiateLongLoadProfile(myDevice1, channel, start, stop, incrementingRunner, new LiteYukonUser());
        service.initiateLongLoadProfile(myDevice2, channel, start, stop, incrementingRunner, new LiteYukonUser());
        Assert.assertEquals("wrong number of messages reached out queue", 2, porterConnection.writtenOut.size());
        Assert.assertEquals("messages weren't queued", 2, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("messages weren't queued", 2, service.getPendingLongLoadProfileRequests(myDevice2).size());
        Assert.assertEquals("runner should not have run", 0, successRan);
        Assert.assertEquals("runner should not have run", 0, failureRan);
        Assert.assertEquals("runner should not have run", 0, cancelRan);
        
        // eat the two requests
        Request temp1 = (Request) porterConnection.writtenOut.remove(); 
        Request temp2 = (Request) porterConnection.writtenOut.remove(); 
        
        // some time has passed and porter hasn't responded, run timers
        queueDataService.setReturnValue(0); // forgotten messagess...
        scheduledExecutorMock.doAllTasks();

        // this should have queued two LLP request
        Assert.assertEquals("wrong number of messages reached out queue", 2, porterConnection.writtenOut.size());
        Assert.assertEquals("messages weren't queued", 1, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("messages weren't queued", 1, service.getPendingLongLoadProfileRequests(myDevice2).size());
        Assert.assertEquals("runner should not have run", 0, successRan);
        Assert.assertEquals("runner should not have run", 2, failureRan);
        
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
        Assert.assertEquals("wrong number of messages reached out queue", 0, porterConnection.writtenOut.size());
        Assert.assertEquals("messages weren't queued", 1, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("messages weren't queued", 1, service.getPendingLongLoadProfileRequests(myDevice2).size());
        Assert.assertEquals("runner should not have run", 0, successRan);
        Assert.assertEquals("runner should not have run", 2, failureRan);
        
        // more time has passed, but this time no expect more messages have been received, however porter is still working...
        queueDataService.setReturnValue(1);
        scheduledExecutorMock.doAllTasks();
       
        // nothing should change
        Assert.assertEquals("wrong number of messages reached out queue", 0, porterConnection.writtenOut.size());
        Assert.assertEquals("messages weren't queued", 1, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("messages weren't queued", 1, service.getPendingLongLoadProfileRequests(myDevice2).size());
        Assert.assertEquals("runner should not have run", 0, successRan);
        Assert.assertEquals("runner should not have run", 2, failureRan);

        
        // but all good things must come to an end
        queueDataService.setReturnValue(0);
        scheduledExecutorMock.doAllTasks();
        
        // nothing should be left
        Assert.assertEquals("wrong number of messages reached out queue", 0, porterConnection.writtenOut.size());
        Assert.assertEquals("messages weren't queued", 0, service.getPendingLongLoadProfileRequests(myDevice1).size());
        Assert.assertEquals("messages weren't queued", 0, service.getPendingLongLoadProfileRequests(myDevice2).size());
        Assert.assertEquals("runner should not have run", 0, successRan);
        Assert.assertEquals("runner should not have run", 4, failureRan);

        // did we leak anything?
        Assert.assertEquals("still holding data", 0, serviceDebug.debugSizeOfCollections());
    }
}

