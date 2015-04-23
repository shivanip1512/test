#include "precompiled.h"

#include "types.h"
#include "cparms.h"
#include "cticalls.h"
#include "dsm2.h"
#include "logger.h"
#include "port_shr_ip.h"
#include "prot_welco.h"
#include "porter.h"
#include "utility.h"
#include "numstr.h"
#include "cti_asmc.h"
#include "streamSocketListener.h"
#include "std_helper.h"

#define SCADA_CONNECT_TIMEOUT  5000

#define DEBUG_INPUT_FROM_SCADA 0x00000010
#define DEBUG_OUTPUT_TO_SCADA  0x00000020
#define DEBUG_SCADA_CONNECTION 0x00000040
#define DEBUG_SPECIAL_MESSAGES 0x00000080

#define DEBUG_THREAD           0x00000100
#define DEBUG_NO_INPUT         0x00000200

using std::string;
using std::endl;
using std::set;
using Cti::Timing::Chrono;
using Cti::StreamSocketConnection;
using Cti::StreamConnectionException;
using Cti::arrayToRange;
using Cti::Logging::Range::Hex::operator<<;

CtiPortShareIP::CtiPortShareIP(CtiPortSPtr myPort, INT listenPort) :
    CtiPortShare(myPort,listenPort),
    _reconnect(false),
    _broadcast(false),
    _ipPort(-1)
{
    set(CtiPortShareIP::INWAITFOROUT, false);
    set(CtiPortShareIP::RUNCOMPLETE, false);
}


CtiPortShareIP::~CtiPortShareIP()
{
    interrupt(CtiThread::SHUTDOWN);    // Make sure we don't go back into waitOnSCADAClient if that is what we are doing.
    SetEvent(_shutdownEvent);
    shutDown();

    for( int i = 0; i<25; i++ )
    {
        //Currently a 5 second wait!
        if( !isSet(RUNCOMPLETE) )
        {
            sleep(200);
        }
        else
        {
            i = 25;
        }
    }

    if( !isSet(RUNCOMPLETE) )
    {
        CTILOG_ERROR(dout, getIDString() <<" - PortShare refused to shutdown after 5 seconds");
    }
}


/*-----------------------------------------------------------------------------*
 * This method waits for the outThread connection to complete the trasaction and
 * post the INWAITFOROUT flag.  Otherwise (no post) we clear the inbound socket and
 * continue on our own.
 *
 * If we do get the post, we read a new message from the socket and write it out
 * to the appropriate portQueue.
 *-----------------------------------------------------------------------------*/
void CtiPortShareIP::inThread()
{
    int recLen, loopsSinceRead = 0;
    unsigned long bytesRead, bytesReadChunk, bytesPeek;
    unsigned char peekBuf; // just 1 byte
    BYTE Buffer[300];
    unsigned long remainderbytes;
    Cti::StreamSocketListener scadaListenNexus; // Presents the IP Port for scada to connect to.

    string thread_name = "PSin " + CtiNumStr(_port->getPortID()).zpad(4);
    SetThreadName(-1, thread_name.c_str());

    while( !isSet(SHUTDOWN) )     // Have we been kicked from on high.
    {
        try
        {
            //  make sure we're connected to the outThread nexus
            if(inThreadConnectNexus())
            {
                if( isSet(CtiPortShareIP::INWAITFOROUT) )
                {
                    if( !sleep(90000) )   // sleep until outThread interrupts us with a INWAITFOROUT flag.
                    {
                        // We arrive here only if the sleep completed uninterrupted.  We normally do not expect that.
                        if( _scadaNexus.isValid() )
                        {
                            set(CtiPortShareIP::INWAITFOROUT, false);  // outThread might be broken - we will allow a new read from scada.
                            decRequestCount();                         // The last request is assumed timed out!

                            CTILOG_WARN(dout, getIDString() <<" - Timed out waiting for synchronization flag");

                            //  Send an error message back to the SCADA system...
                            Buffer[0] = 0x7d;
                            Buffer[1] = CtiPortShare::PSHR_ERROR_NOREPLY;

                            //  if we got no reply, it should be safe to write to the _scadaNexus - outThread won't wake up unless we get a
                            //    reply, and he's the only other thread that can write
                            if( _scadaNexus.write(Buffer, 2, Chrono::seconds(10)) != 2 )
                            {
                                CTILOG_ERROR(dout, getIDString() <<" - SCADA Nexus \"no reply\" error send Failed");
                                _scadaNexus.close();
                            }
                            else if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )
                            {
                                CTILOG_DEBUG(dout, getIDString() <<" - Sent \"no reply\" error to SCADA system");
                            }

                            _scadaNexus.flushInput();
                        }
                    }
                    //  recheck the nexus - it may have died in the (up to) 90 seconds we were asleep
                    continue;
                }

                //  make sure the listener is up.  Any new connections arrive through this nexus.
                if( ! scadaListenNexus.isValid() )
                {
                    if( ! scadaListenNexus.create(getIPPort()) )
                    {
                        CTILOG_ERROR(dout, getIDString() <<" - Couldn't create scadaListenNexus - sleeping 5 seconds");

                        //  sanity check - 5 second delay keeps us from an insanely tight loop if we break repeatedly
                        sleep(5000);
                    }
                }

                //  check for new connections if we haven't read anything in the last N loops.
                //    or if we're not connected
                if( !_scadaNexus.isValid() || loopsSinceRead > gConfigParms.getValueAsULong("PORTER_PORTSHARE_LOOPCOUNT", 4) )
                {
                    if( getDebugLevel(DEBUG_SCADA_CONNECTION) )
                    {
                        CTILOG_DEBUG(dout, getIDString() <<" - looking for a new SCADA connection. Loops = "<< loopsSinceRead);
                    }

                    std::auto_ptr<StreamSocketConnection> newNexus = scadaListenNexus.accept(StreamSocketConnection::ReadExactly, Chrono::seconds(1), &_shutdownEvent);

                    if( newNexus.get() )
                    {
                        if( getDebugLevel(DEBUG_SCADA_CONNECTION) )
                        {
                            CTILOG_DEBUG(dout, getIDString() <<" - got a new SCADA connection, copying tmpNexus to _scadaNexus ");
                        }

                        _scadaNexus.close();      //  make sure the old/invalid socket is closed

                        newNexus->Name = getIDString();
                        _scadaNexus.swap(*newNexus);

                        setRequestCount(0);             // Reconnect zeros the busy value.
                        _sequenceFailReported = false;  // Allow a new report on this nexus.
                        loopsSinceRead = 0;
                    }

                    if( !_scadaNexus.isValid() )
                    {
                        set(CtiPortShareIP::INWAITFOROUT, false);     // Do not cause this thread to wait before resetting scadaNexus.
                        sleep(2000);
                    }
                }

                //  if we have a valid SCADA connection
                if( _scadaNexus.isValid() )
                {
                    bytesRead = 0;      // <-- else we tend to report we've read a rather amazing 4e9 bytes from the TCP stack...
                    bool dataReady = false;

                    // listen to our SCADA system for a bit.
                    if( bytesReadChunk = _scadaNexus.read(Buffer, 4, Chrono::seconds(15), &_shutdownEvent) )
                    {
                        bytesRead += bytesReadChunk;
                        loopsSinceRead = 0;

                        if(Buffer[0] == 0x7e)
                        {
                            //  it's an IDLC message, read the rest
                            try
                            {
                                //  is this an unsequenced control message?
                                if(Buffer[2] & 0x01 && Buffer[2] != HDLC_UD )
                                {
                                    //  we only need to read one more byte, so we don't need any of the fancy ioctl stuff below
                                    if( bytesReadChunk = _scadaNexus.read(&(Buffer[4]), 1, Chrono::seconds(15), &_shutdownEvent) )
                                    {
                                        bytesRead += bytesReadChunk;
                                        dataReady  = true;
                                    }
                                }
                                else
                                {
                                    if( bytesReadChunk = _scadaNexus.read(&(Buffer[4]), (Buffer[3] + 2), Chrono::seconds(15), &_shutdownEvent) )
                                    {
                                        if( _scadaNexus.peek(&peekBuf, 1) )
                                        {
                                            // If there are any bytes still in the socket we must find the most recent. The continue will
                                            // cause a new read.
                                            continue;
                                        }
                                        bytesRead += bytesReadChunk;
                                        dataReady  = true;
                                    }
                                }
                            }
                            catch( const StreamConnectionException &ex )
                            {
                                CTILOG_EXCEPTION_ERROR(dout, ex, getIDString() <<" - scadaNexus read failed");
                            }

                            if( dataReady )
                            {
                                // Get an OutMessage.
                                OUTMESS *OutMessage = new OUTMESS;

                                if( getDebugLevel(DEBUG_INPUT_FROM_SCADA) )  // Debug the inbound from SCADA
                                {
                                    CTILOG_DEBUG(dout, getIDString() <<" - read "<< bytesRead <<" SCADA bytes:"<<
                                            endl << arrayToRange(Buffer, bytesRead));
                                }

                                set(CtiPortShareIP::INWAITFOROUT, true);     // reset the SYNC bool to indicate we've done a read this makes the inThread wait on us.

                                OutMessage->Port = getPort()->getPortID();
                                OutMessage->Remote = Buffer[1] >> 1;
                                OutMessage->TimeOut = determineTimeout(Buffer, bytesRead);  //  this will create the appropriate timeout if we're talking DTRAN
                                OutMessage->Retry = 0;
                                OutMessage->Source = 0;
                                OutMessage->Destination = 0;
                                OutMessage->Sequence = 0;
                                OutMessage->Priority = gConfigParms.getValueAsInt("PORTER_PORTSHARE_PRIORITY", MAXPRIORITY - 1);
                                OutMessage->ReturnNexus = getReturnNexus();
                                OutMessage->SaveNexus = NULL;

                                OutMessage->MessageFlags |= MessageFlag_PortSharing;
                                OutMessage->ExpirationTime = CtiTime().seconds() + gConfigParms.getValueAsInt("PORTER_PORTSHARE_EXPIRATION_TIME", 600);

                                if( Buffer[2] == HDLC_UD )
                                {
                                    //  this is an RTU request

                                    //  Figure out what the out length is
                                    OutMessage->OutLength = Buffer[3] - 3;
                                }
                                else
                                {
                                    //  this is a CCU request

                                    if( Buffer[2] & 0x01 )
                                    {
                                        //  this is an IDLC CCU control message
                                        OutMessage->OutLength = 5;
                                    }
                                    else
                                    {
                                        OutMessage->OutLength = bytesRead;
                                    }
                                }

                                OutMessage->InLength = 0;

                                //  Now figure out the event code
                                if(OutMessage->Remote != RTUGLOBAL)
                                {
                                    OutMessage->EventCode = RESULT | ENCODED;
                                    incRequestCount();
                                }
                                else
                                {
                                    // broadcast message - tell inThread to read again, we expect no reply from this type of message
                                    if( getDebugLevel(DEBUG_SPECIAL_MESSAGES) )
                                    {
                                        CTILOG_DEBUG(dout, getIDString() <<" - Intercepted RTU broadcast message");
                                    }

                                    set(CtiPortShareIP::INWAITFOROUT, false);     // set the SYNC bool so inThread does not wait.
                                    OutMessage->EventCode = NOWAIT | NORESULT | ENCODED;
                                }

                                //  Check if this is a time sync
                                if((Buffer[5] & 0x7f) == IDLC_TIMESYNC)
                                {
                                    if( getDebugLevel(DEBUG_SPECIAL_MESSAGES) )
                                    {
                                        CTILOG_DEBUG(dout, getIDString() <<" - Intercepted time sync message");
                                    }

                                    OutMessage->EventCode |= TSYNC;
                                }

                                //  Now copy the message into the buffer...
                                memcpy (OutMessage->Buffer.OutMessage, Buffer, bytesRead);

                                //  ... and put it on the correct port's queue
                                if(getPort()->writeQueue(OutMessage))
                                {
                                    CTILOG_ERROR(dout, getIDString() <<" - error writing to queue");

                                    set(CtiPortShareIP::INWAITFOROUT, false);  //  we couldn't write, so we're not waiting for a response
                                    sleep(2000);

                                    delete OutMessage;
                                }
                            }
                            else
                            {
                                CTILOG_ERROR(dout, getIDString() <<" - failed to read header-specified rest of IDLC message."<<
                                        endl <<"Buffer[3] = "<< (int)Buffer[3] <<", Actually read "<< bytesRead <<" bytes");

                                //  we're broken, try to read some more
                                set(CtiPortShareIP::INWAITFOROUT, false);     // set the SYNC bool to allow inthread to run.
                            }
                        }
                        else
                        {
                            CTILOG_WARN(dout, getIDString() <<" - non-IDLC message intercepted");
                        }
                    }
                    else
                    {
                        sleep(1000);        // Make certain we don't loop too crazy here
                        loopsSinceRead++;

                        if( !_scadaNexus.isValid() )
                        {
                            CTILOG_INFO(dout, getIDString() <<" - connection to _scadaNexus has closed");
                        }
                        else if( getDebugLevel(DEBUG_NO_INPUT) )
                        {
                            CTILOG_DEBUG(dout, getIDString() <<" - no input from _scadaNexus.read()");
                        }
                    }
                }
            }
            else
            {
                CTILOG_INFO(dout, getIDString() <<" - waiting to connect to returnNexus");

                if( !sleep(5000) )   // sleep until outThread interrupts us with a INWAITFOROUT flag.
                {
                    CTILOG_WARN(dout, getIDString() <<" - timed out waiting to connect to returnNexus");

                    if( _scadaNexus.isValid() )
                    {
                        _scadaNexus.flushInput();
                    }
                }
            }
        }
        catch (boost::thread_interrupted) 
        {
            CTILOG_DEBUG(dout, thread_name + " thread Interrupted, exiting");
            break;
        }
        catch( const StreamConnectionException &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, getIDString());
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, getIDString());
        }
    } // end of while
}


int CtiPortShareIP::determineTimeout(unsigned char *message, unsigned int len)
{
    int timeout;

    //  is it a sequenced IDLC message?
    if( (message[0] == 0x7e)        //  HDLC/IDLC
        &&  (message[1]  & 0x01)    //  is it for the CCU?
        && !(message[2]  & 0x01) )  //  sequenced
    {
        timeout = TIMEOUT;

        //  Is it DTRAN?
        if( message[5] == CMND_DTRAN )
        {
            int stages    =  message[PREIDLEN + 1] & 0x0f,
                wordcount = (message[PREIDLEN + PREAMLEN + 4] & 0x30) >> 4;

            timeout += stages * (wordcount + 1);
        }
    }
    else
    {
        timeout = 1;
    }

    return timeout;
}


CtiPortShareIP& CtiPortShareIP::setIPPort(int prt)
{
    _ipPort = prt;
    return *this;
}

int CtiPortShareIP::getIPPort() const
{
    return _ipPort;
}

/*-----------------------------------------------------------------------------*
   This method waits for porter to return an InMessage for the pending
   transaction.  When this happens we post the INWAITFOROUT flag and return.
   One must note that we cannot take more than 90 seconds in this method.
 *-----------------------------------------------------------------------------*/
void CtiPortShareIP::outThread()
{
    //  Buffer to get stuff off the pipe
    INMESS InMessage;

    //  Misc Variables
    bool bDoReply = true;

    INT sendLen, status;

    string thread_name = "PSout" + CtiNumStr(_port->getPortID()).zpad(4);
    SetThreadName(-1, thread_name.c_str());

    while(!isSet(CtiThread::SHUTDOWN))
    {
        try
        {
            //  make sure inThread can read whenever the nexus is set up
            set(CtiPortShareIP::INWAITFOROUT, false);
            interrupt();                                // set INWAITFOROUT false and interrupt the sleep!

            if(outThreadValidateNexus())
            {
                //  wait forever to hear back from the port
                if( _internalNexus.read(&InMessage, sizeof(InMessage), Chrono::infinite, &_shutdownEvent) != sizeof(InMessage) )
                {
                    //  This cannot be
                    set(CtiPortShareIP::INWAITFOROUT, false);

                    CTILOG_ERROR(dout, getIDString() <<" - Internal connection failed to read InMessage");

                    return;
                }

                //  We got one so see if we need to embed the error code
                if(InMessage.ErrorCode)
                {
                    // first byte of non-DLC section of message
                    // for ACS set byte 11 to 7d (error) and set byte 12 to event code
                    InMessage.IDLCStat[11] = 0x7d;
                    InMessage.IDLCStat[12] = ProcessEventCode(InMessage.ErrorCode);
                    InMessage.InLength = 2;
                }
                else
                {
                    // No need to add this on - we already have the proper length for the message
                    // InMessage.InLength += (2 + PREIDLEN);
                }

                //  Go ahead and send it on over
                if( _scadaNexus.isValid() )
                {
                    if( getRequestCount() == 0 && !_sequenceFailReported)        // This means something went wrong between submittal and response.
                    {
                        CTILOG_ERROR(dout, getIDString() <<" - port sharing sequence problem: Unexpected field response. "<< getRequestCount() <<" pending operations.");
                    }

                    decRequestCount();
                    if( getRequestCount() > 0 )
                    {
                        if(!_sequenceFailReported)
                        {
                            CTILOG_ERROR(dout, getIDString() <<" - port sharing sequence problem: Multiple responses pending. "<< getRequestCount() <<" pending operations.");
                        }

                        _sequenceFailReported = true;      // We are OUT of sequence!  Do not report additional failures.
                    }
                    else
                    {
                        _sequenceFailReported = false;      // We are IN sequence!  Next failure may be reported again.
                    }

                    if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )  // Debug the outbound back to SCADA
                    {
                        CTILOG_DEBUG(dout, getIDString() <<" - Write "<< InMessage.InLength <<" to SCADA bytes: "<<
                                endl << arrayToRange(&InMessage.IDLCStat[11], InMessage.InLength));
                    }

                    //  is this a CCU-711?  This is shaky, since there's no guarantee that this should be set to 0...
                    //    the good news is that we generally don't scan RTUs and CCU-711s on the same shared port
                    if( InMessage.IDLCStat[0] == 0x7e )
                    {
                        unsigned char address = InMessage.IDLCStat[1] >> 1;

                        if( InMessage.InLength > 5 && hasSharedCCUError(address) )
                        {
                            InMessage.IDLCStat[6] |= getSharedCCUError(address);

                            //  refer to PostIDLC() for the proper way to do this - I didn't want to have to export/import the function
                            unsigned short crc = NCrcCalc_C(InMessage.IDLCStat + 1, InMessage.InLength - 3);

                            InMessage.IDLCStat[InMessage.InLength - 2] = (crc >> 8) & 0xff;
                            InMessage.IDLCStat[InMessage.InLength - 1] =  crc       & 0xff;

                            clearSharedCCUError(address);
                        }

                        if( _scadaNexus.write((char*)InMessage.IDLCStat, InMessage.InLength, Chrono::seconds(10)) != InMessage.InLength )
                        {
                            CTILOG_ERROR(dout, getIDString() <<" - SCADA Nexus send Failed");
                            _scadaNexus.close();
                        }
                        else if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )
                        {
                            CTILOG_DEBUG(dout, getIDString() <<" - Successful write back to SCADA");
                        }
                    }
                    else  // if InMessage.IDLCStat[11] == 0x7e OR InMessage.IDLCStat[13] == HDLC_UD
                    {
                        if( _scadaNexus.write((char*)(InMessage.IDLCStat + 11), InMessage.InLength, Chrono::seconds(10)) != InMessage.InLength )
                        {
                            CTILOG_ERROR(dout, getIDString() <<" - SCADA Nexus send Failed");
                            _scadaNexus.close();
                        }
                        else if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )
                        {
                            CTILOG_DEBUG(dout, getIDString() <<" - Successful write back to SCADA");
                        }
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, getIDString() <<" - Attempted send on invalid SCADA Nexus");
                }
            }
            else if( !isSet(CtiThread::SHUTDOWN) )//Ignore this if we are shutting down
            {
                CTILOG_ERROR(dout, getIDString() <<" - unable to create listenNexus");
                sleep(1000);
            }
        }
        catch (boost::thread_interrupted) 
        {
            CTILOG_DEBUG(dout, thread_name + " thread Interrupted, exiting");
            break;
        }
        catch( const StreamConnectionException &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, getIDString());
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, getIDString());
        }
    }

    _scadaNexus.close();
}

void CtiPortShareIP::run()
{
    try
    {
        _outThread = boost::thread(&CtiPortShareIP::outThread, this);
        _inThread  = boost::thread(&CtiPortShareIP::inThread,  this);

        while( !isSet(SHUTDOWN) )
        {
            sleep(250);//This wakes up on interrupt, so it shouldnt matter!
        }
        interrupt(CtiPortShareIP::RUNCOMPLETE);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, getIDString());
    }
}

int CtiPortShareIP::outThreadValidateNexus()
{
    if( ! _internalNexus.isValid() )
    {
        createNexus("Port Share Listener Nexus");             //  Blocks awaiting connection from inthread.
    }

    return _internalNexus.isValid();
}

int CtiPortShareIP::inThreadConnectNexus()
{
    if( ! _returnNexus.isValid() )
    {
        connectNexus();
    }

    return _returnNexus.isValid();
}


/*-----------------------------------------------------------------------------*
 * Shuts down the out thread and sets the flags for the in thread to shut down.
 *-----------------------------------------------------------------------------*/
void CtiPortShareIP::shutDown()
{
    try
    {
        _outThread.interrupt();
        _inThread.interrupt();

        if( ! _outThread.timed_join(boost::posix_time::seconds(30)) )
        {
            CTILOG_WARN(dout, getIDString() <<" - Port Share OutThread did not shutdown gracefully. Will attempt a forceful shutdown.");
            TerminateThread(_outThread.native_handle(), EXIT_SUCCESS);
        }

        if( ! _inThread.timed_join(boost::posix_time::seconds(30)) )
        {
            CTILOG_WARN(dout, getIDString() <<" - Port Share InThread did not shutdown gracefully. Will attempt a forceful shutdown.");
            TerminateThread(_inThread.native_handle(), EXIT_SUCCESS);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, getIDString());
    }

}
