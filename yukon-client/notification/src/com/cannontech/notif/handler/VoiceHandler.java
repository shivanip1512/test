package com.cannontech.notif.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryFuncs;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramCurtailCustomerList;
import com.cannontech.database.data.device.lm.LMProgramCurtailment;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.device.lm.LMProgramEnergyExchange;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.lm.LMDirectNotificationGroupList;
import com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifVoiceMsg;
import com.roguewave.tools.v2_0.Queue;
import com.vocomo.vxml.tools.MakeCall;

/**
 * @author rneuharth
 *
 *  Handles all outgoing voice queries and responses.
 * 
 */
class VoiceHandler implements INotifHandler, Runnable
{
    //only 1 thread to do all the outbound calls should ever exist
    private static Thread _runner;

    //1 queue to hold all the request messages for us to dial
    private static Queue _messageQueue = new Queue();
    
    //for each contact, we add 4 minutes for timeout (wild guess here!)
    private static final long PER_CONTACT_TIMEOUT = 240 * 1000;
    
    private static final int CALL_TIMEOUT = 30;  //seconds
    private static final String VOICE_HOST = "10.100.2.100";
    private static final String VOICE_APP = "login";


    VoiceHandler( NotifVoiceMsg msg )
    {
        super();

        if( !isValid(msg) )
            _messageQueue.put( msg );
        
    }

    /**
     * Starts the handling of the given message
     * 
     */
    public synchronized void start()
    {
        if( _runner == null )
        {
            _runner = new Thread(this, "VoiceMsgHand");
            _runner.setDaemon( true );
            _runner.start();
        }

    }
    
    /**
     * Checks for validity of the given message
     * 
     */
    public boolean isValid( Message msg )
    {
        if( !(msg instanceof NotifVoiceMsg) )
            return false;

        LiteYukonPAObject litePAO =
            PAOFuncs.getLiteYukonPAO( 
                    ((NotifVoiceMsg)msg).getNotifProgramID() );
        
        return DeviceTypesFuncs.isLMProgram( litePAO.getLiteType() );
    }
    
    /**
     * Handles Direct program instances. Returns the contact for each phone
     * number found in the assigned NotificationGroup.
     * 
     */
    private LiteContact[] handleDirectProgram( LMProgramDirect prog )
    {
        ArrayList contactsList = new ArrayList(16);

        for( int i = 0; i < prog.getLmProgramDirectNotifyGroupVector().size(); i++ )
        {
            LMDirectNotificationGroupList cust =
                (LMDirectNotificationGroupList)prog.getLmProgramDirectNotifyGroupVector().get(i);
        
            LiteNotificationGroup lNotifGrp =
                NotificationGroupFuncs.getLiteNotificationGroup(
                        cust.getNotificationGroupID().intValue() );

            if( !lNotifGrp.isDisabled() )
            {
                for( int j = 0; j < lNotifGrp.getNotificationDestinations().size(); j++ )
                {
                    LiteContactNotification lContDest =
                        (LiteContactNotification)
                            lNotifGrp.getNotificationDestinations().get(j);
                    
                    //if this is a phone number, add the contact for this phone number to 
                    // out list
                    if( !lContDest.isDisabled() &&
                            YukonListEntryFuncs.isPhoneNumber(lContDest.getNotificationCategoryID()) )
                    {
                        LiteContact lCont = ContactFuncs.getContact(lContDest.getContactID());
                        if( !contactsList.contains(lCont) )
                            contactsList.add( lCont );
                    }
                    
                    
                }
            }
        }

        LiteContact[] retCont = new LiteContact[ contactsList.size() ];
        contactsList.toArray( retCont );
        return retCont;
    }

    
    /**
     * Handles Curtialment program instances. Returns the primary contact
     * for each customer in the program.
     * 
     */
    private LiteContact[] handleCurtailmentProgram( LMProgramCurtailment prog )
    {
        ArrayList contactsList = new ArrayList(16);

        for( int i = 0; i < prog.getLmProgramStorageVector().size(); i++ )
        {
            LMProgramCurtailCustomerList cust =
                (LMProgramCurtailCustomerList)prog.getLmProgramStorageVector().get(i);
        
            contactsList.add( 
                    CustomerFuncs.getPrimaryContact(
                            cust.getLmProgramCurtailCustomerList().getCustomerID().intValue()) );
        }
        
        LiteContact[] retCont = new LiteContact[ contactsList.size() ];
        contactsList.toArray( retCont );
        return retCont;
    }

    /**
     * Handles EnergyExchange program instances. Returns the primary contact
     * for each customer in the program.
     * 
     */
    private LiteContact[] handleEExchangeProgram( LMProgramEnergyExchange prog )
    {
        ArrayList contactsList = new ArrayList(16);

        for( int i = 0; i < prog.getLmProgramStorageVector().size(); i++ )
        {
            LMEnergyExchangeCustomerList cust =
                (LMEnergyExchangeCustomerList)prog.getLmProgramStorageVector().get(i);
        
            contactsList.add( 
                    CustomerFuncs.getPrimaryContact(
                            cust.getCustomerID().intValue()) );
        }
        
        LiteContact[] retCont = new LiteContact[ contactsList.size() ];
        contactsList.toArray( retCont );
        return retCont;
    }
    
    /**
     * Retrieves the chubby representation of the object from
     * the DB
     * 
     */
    private LMProgramBase getDBObject( NotifVoiceMsg msg ) throws Exception
    {
        LiteYukonPAObject litePAO =
            PAOFuncs.getLiteYukonPAO( msg.getNotifProgramID() );
        
        DBPersistent dbPersist = LiteFactory.createDBPersistent(litePAO);
        Transaction t = Transaction.createTransaction(
                Transaction.RETRIEVE,
                dbPersist );
        
        t.execute();
        return (LMProgramBase)dbPersist;
    }
    
    /**
     * Do the dialing out to the given phone number. Returns the result code.
     * 
     * @param phoneNumber
     * @return CallResult
     * @throws IOException
     */
    private int makeCalls( String phoneNumber ) throws IOException
    {
        MakeCall mc = new MakeCall( VOICE_HOST );
        int rInt = mc.makeCall(phoneNumber, VOICE_APP, CALL_TIMEOUT);        
        
        switch( rInt )
        {
            case MakeCall.CONNECTED:
                System.out.println("The call was connected (Phone #: " + phoneNumber + ")");
                break;
            case MakeCall.BUSY:
                System.out.println("The destination was busy (Phone #: " + phoneNumber + ")");
                break;
            case MakeCall.NO_ANSWER:
                System.out.println("The callee rejected the call (Phone #: " + phoneNumber + ")");
                break;
            case MakeCall.NO_RINGBACK:
                System.out.println("There's no ringback from network (Phone #: " + phoneNumber + ")");
                break;
            case MakeCall.NO_CHANNEL:
                System.out.println("There's not enough resource to make a call (Phone #: " + phoneNumber + ")");
                break;
            case MakeCall.TIMEOUT:
                System.out.println("The callee did not answer the call in " +
                        CALL_TIMEOUT + " seconds (Phone #: " + phoneNumber + ")");
                break;
            case MakeCall.ERROR:
                System.out.println("An Unknown ERROR occured (Phone #: " + phoneNumber + ")");
                break;
        }


        return rInt;
    }

    public void run()
    {
        for( ;; )
        {
            try
            {                
                if( _messageQueue.isEmpty() )
                {
                    Thread.sleep( 500 );
                    continue;
                }


                LMProgramBase prog = getDBObject( 
                        (NotifVoiceMsg)_messageQueue.get() );

                LiteContact[] contacts = new LiteContact[0];
                if( prog instanceof LMProgramDirect )
                {
                    contacts =
                        handleDirectProgram( (LMProgramDirect)prog );
                }            
                else if( prog instanceof LMProgramCurtailment )
                {
                    contacts = 
                        handleCurtailmentProgram( (LMProgramCurtailment)prog );
                }
                else if( prog instanceof LMProgramEnergyExchange )
                {
                    contacts = 
                        handleEExchangeProgram( (LMProgramEnergyExchange)prog );
                }
                else
                    throw new IllegalArgumentException("Unhandled program type " +
                            "found. type = " + prog.getClass().getName() );


                callContacts( contacts );

                //let us know if no contacts were found
                if( contacts.length <= 0 )
                    CTILogger.info( " No contacts found for the LMProgram named '" +
                            prog.getPAOName() + "'");
                
            }
            catch( Exception e )
            {
                CTILogger.error( "Unable to complete outbound voice request", e );
            }
        }

    }

    /**
     * Call the array of contacts by trying each phone number until one of the
     * phone numbers completes successfully.
     * 
     * @param contacts
     */
    private void callContacts( final LiteContact[] contacts )
    {
        ArrayList contactList = new ArrayList();
        contactList.addAll( Arrays.asList(contacts) );

        long start = System.currentTimeMillis();
        long stop = PER_CONTACT_TIMEOUT * contactList.size();
        int i = contactList.size()-1;

        while( contactList.size() > 0
                && (System.currentTimeMillis() - start <= stop) )
        {
            LiteContact contact = (LiteContact)contactList.get(i);

            LiteContactNotification[] phoneNumbers =
                ContactFuncs.getAllPhonesNumbers( contact.getContactID() );
            
            String currNumber = null;
            try
            {
                for( int j = 0; j < phoneNumbers.length; j++ )
                {
                    if( phoneNumbers[j].isDisabled() )
                        continue;

                    currNumber = phoneNumbers[j].getNotification();
                    
                    //this thread blocks here
                    int retCode = makeCalls( currNumber );


                    //success on one of the numbers, we can get
                    // out of the loop
                    if( retCode == MakeCall.CONNECTED )
                    {   
                        CTILogger.info(" Outbound call CONNECTED to: " + contact.toString() );
                        CTILogger.info("   Phone #: " + currNumber );
                        contactList.remove( contact );
                        
                        break;
                    }
                    
                }
            }
            catch( IOException ioe )
            {
                CTILogger.error("An ERROR occured attempting to dial the" +
                        "following outbound phone #: " + currNumber +
                        ", Contact = " + contact.toString()
                        , ioe );
                
                contactList.remove( contact );
            }


            if( i == 0 )
                i = contactList.size()-1;
            else
                i--;
        }

    }
    
    public static void main( String[] args )
    {
        try
        {
            NotifVoiceMsg vMsg = new NotifVoiceMsg();
            vMsg.setNotifProgramID( 14 );
            final VoiceHandler v1 = new VoiceHandler( vMsg );
            
            v1.start();
            
            while( v1._runner.isAlive() )
            {}
        }
        catch( Exception e )
        {
            e.printStackTrace( System.out );
        }

    }
    
}
