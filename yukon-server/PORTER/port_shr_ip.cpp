/*-----------------------------------------------------------------------------*
*
* File:   port_shr_ip
*
* Date:   8/16/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PORTER/port_shr_ip.cpp-arc  $
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2006/04/13 19:40:02 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <winsock.h>

#include "types.h"
#include "cparms.h"
#include "cticalls.h"
#include "dsm2.h"
#include "logger.h"
#include "port_shr_ip.h"
#include "prot_welco.h"
#include "porter.h"
#include "tcpsup.h"
#include "utility.h"
#include "numstr.h"

#include <rw/toolpro/inetaddr.h>

#define SCADA_CONNECT_TIMEOUT  5000
#define DEBUG_INFROMSCADA     0x00000010
#define DEBUG_OUTTOSCADA      0x00000020
#define DEBUG_SCADACONNECTION 0x00000040

#define FILELINE     " " << __FILE__ << " (" << __LINE__ << ")"

CtiPortShareIP::CtiPortShareIP(CtiPortSPtr myPort, INT listenPort) :
    CtiPortShare(myPort,listenPort),
    _reconnect(false),
    _broadcast(false),
    _ipPort(-1)
{
    set(CtiPortShareIP::INWAITFOROUT, false);
    set(CtiPortShareIP::SOCKCONNECTED, false);
    set(CtiPortShareIP::SOCKFAILED, true);
    set(CtiPortShareIP::RUNCOMPLETE, false);
}


CtiPortShareIP::~CtiPortShareIP()
{
    interrupt(SHUTDOWN);    // Make sure we don't go back into waitOnSCADAClient if that is what we are doing.
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
        //If you see this, and experience a deadlock, this is probably to blame.
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " *** CHECKPOINT *** PortShare refused to shutdown after 5 seconds " << FILELINE << endl;
    }
    if(_scadaNexus.CTINexusValid())
    {
        _scadaNexus.CTINexusClose();
    }
}


/*-----------------------------------------------------------------------------*
 * This method waits for the outThread connection to complete the trasaction and
 * post the INWAITFOROUT flag.  Otherwise (no post) we clear the inbound socket and
 * continue on our own.
 *
 * If we do get the post, we read a CTIDBG_new message from the socket and write it out
 * to the appropriate portQueue.
 *-----------------------------------------------------------------------------*/
void CtiPortShareIP::inThread()
{
    int recLen, loopsSinceRead = 0, readStatus;
    unsigned long bytesRead;
    BYTE Buffer[300];
    unsigned long bytesWritten;
    CTINEXUS scadaListenNexus;          // Presents the IP Port for scada to connect to.
    CTINEXUS tmpNexus;

    OUTMESS *OutMessage = NULL;

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
                        if( _scadaNexus.CTINexusValid() )
                        {
                            set(CtiPortShareIP::INWAITFOROUT, false);  // outThread might be broken - we will allow a new read from scada.
                            decRequestCount();                         // The last request is assumed timed out!
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getPort()->getName() << ": inThread timed out waiting for synchronization flag. " << FILELINE << endl;
                            }

                            //  Send an error message back to the SCADA system...
                            Buffer[0] = 0x7d;
                            Buffer[1] = CtiPortShare::PSHR_ERROR_NOREPLY;

                            //  if we got no reply, it should be safe to write to the _scadaNexus - outThread won't wake up unless we get a
                            //    reply, and he's the only other thread that can write
                            if(_scadaNexus.CTINexusWrite(Buffer, 2, &bytesWritten, 10) || (bytesWritten != 2))
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " SCADA Nexus \"no reply\" error send Failed " << FILELINE << endl;
                                }
                                _scadaNexus.CTINexusClose();
                            }
                            else if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUGLEVEL_LUDICROUS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Sent \"no reply\" error to SCADA system " << FILELINE << endl;
                            }

                            _scadaNexus.CTINexusFlushInput();
                        }
                    }
                    //  recheck the nexus - it may have died in the (up to) 90 seconds we were asleep
                    continue;
                }

                //  make sure the listener is up.  Any new connections arrive through this nexus.
                if( scadaListenNexus.sockt == INVALID_SOCKET )
                {
                    if(scadaListenNexus.CTINexusCreate(getIPPort()))
                    {
                        scadaListenNexus.CTINexusClose();
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " inThread couldn't create scadaListenNexus - sleeping 5 seconds **** " << FILELINE << endl;
                        }
                        //  sanity check - 5 second delay keeps us from an insanely tight loop if we break repeatedly
                        sleep(5000);
                    }
                }

                //  check for new connections if we haven't read anything in the last minute (20 * 90000 ms)
                //    or if we're not connected
                if( loopsSinceRead > 20 || !_scadaNexus.CTINexusValid())
                {
                    scadaListenNexus.CTINexusConnect(&tmpNexus, NULL, 1, CTINEXUS_FLAG_READANY);

                    if(tmpNexus.CTINexusValid())
                    {
                        if( gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUG_SCADACONNECTION )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " inThread got a new SCADA connection - copying tmpNexus to _scadaNexus " << FILELINE << endl;
                        }
                        _scadaNexus.CTINexusClose();  //  make sure the old/invalid socket is closed
                        _scadaNexus = tmpNexus;           //  byte-for-byte copy - no pointers, ...
                        tmpNexus.sockt = INVALID_SOCKET;  //  ...  and observe how i make tmpNexus's sockt handle point elsewhere, so is all good
                        setRequestCount(0);             // Reconnect zeros the busy value.
                        _sequenceFailReported = false;  // Allow a new report on this nexus.
                        loopsSinceRead = 0;
                    }
                }

                //  if we don't have a valid SCADA connection
                if(!_scadaNexus.CTINexusValid())
                {
                    set(CtiPortShareIP::INWAITFOROUT, false);     // Do not cause this thread to wait before resetting scadaNexus.

                    if( gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUG_SCADACONNECTION )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " waiting for new connection - _scadaNexus is invalid " << FILELINE << endl;
                    }
                    sleep(5000);
                }
                else
                {
                    bytesRead = 0; // <-- else we tend to report we've read a rather amazing 4e9 bytes from the TCP stack...
                    // listen to our SCADA system for a bit.
                    if(((readStatus = _scadaNexus.CTINexusRead(Buffer, 4, &bytesRead, 15) == NORMAL)) && (bytesRead > 0))
                    {
                        loopsSinceRead = 0;

                        if(Buffer[0] == 0x7e)
                        {
                            //  it's an IDLC message, read the rest
                            if(((readStatus = _scadaNexus.CTINexusRead(&(Buffer[4]), (Buffer[3] + 2), &bytesRead, 15) == NORMAL)) && (bytesRead == (Buffer[3] + 2 + 4)))
                            {
                                // Get an OutMessage.
                                if( (OutMessage = CTIDBG_new OUTMESS) == NULL )
                                {
                                    // This is bad.
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint **** " << FILELINE << endl;
                                        dout << CtiTime() << " Could not allocate CTIDBG_new OUTMESS for inThread" << endl;
                                    }
                                    return;
                                }

                                if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUG_INFROMSCADA)  // Debug the inbound from SCADA
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << " Read " << bytesRead << " SCADA bytes: ";
                                    for( int byteitr = 0; byteitr < bytesRead; byteitr++ )
                                    {
                                        dout << string( CtiNumStr((int)Buffer[byteitr]).hex().zpad(2) ) << " ";
                                    }
                                    dout << endl;
                                }

                                set(CtiPortShareIP::INWAITFOROUT, true);     // reset the SYNC bool to indicate we've done a read this makes the inThread wait on us.

                                OutMessage->Port = getPort()->getPortID();
                                OutMessage->Remote = Buffer[1] >> 1;
                                OutMessage->TimeOut = 1;
                                OutMessage->Retry = 0;
                                OutMessage->Source = 0;
                                OutMessage->Destination = 0;
                                OutMessage->Sequence = 0;
                                OutMessage->Priority = gConfigParms.getValueAsInt("PORTER_PORTSHARE_PRIORITY", MAXPRIORITY - 1);
                                OutMessage->ReturnNexus = getReturnNexus();
                                OutMessage->SaveNexus = NULL;

                                OutMessage->MessageFlags |= MessageFlag_PortSharing;
                                OutMessage->ExpirationTime = CtiTime().seconds() + gConfigParms.getValueAsInt("PORTER_PORTSHARE_EXPIRATION_TIME", 600);

                                //  Figure out what the out length is
                                OutMessage->OutLength = Buffer[3] - 3;
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
                                    if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUGLEVEL_LUDICROUS)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Intercepted RTU broadcast message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                    set(CtiPortShareIP::INWAITFOROUT, false);     // set the SYNC bool so inThread does not wait.
                                    OutMessage->EventCode = NOWAIT | NORESULT | ENCODED;
                                }

                                //  Check if this is a time sync
                                if((Buffer[5] & 0x7f) == IDLC_TIMESYNC)
                                {
                                    if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUGLEVEL_LUDICROUS)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Intercepted time sync message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                    OutMessage->EventCode |= TSYNC;
                                }

                                //  Now copy the message into the buffer...
                                memcpy (OutMessage->Buffer.OutMessage, Buffer, bytesRead);

                                //  ... and put it on the correct port's queue
                                if(getPort()->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint **** " << FILELINE << endl;
                                        dout << CtiTime() << " Error Writing to Queue for Port " << getPort()->getPortID() << endl;
                                    }

                                    delete OutMessage;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "Failed to read header-specified rest of IDLC message" << endl;
                                    dout << "Buffer[3] = " << (int)Buffer[3] << endl;
                                    dout << "Actually read " << bytesRead << " bytes" << endl;
                                }
                                //  we're broken, try to read some more
                                set(CtiPortShareIP::INWAITFOROUT, false);     // set the SYNC bool to allow inthread to run.
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "Non-IDLC message intercepted" << endl;
                            }
                        }
                    }
                    else
                    {
                        sleep(1000);        // Make certain we don't loop too crazy here
                        loopsSinceRead++;
                        if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUGLEVEL_LUDICROUS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - no input from _scadaNexus.CTINexusRead **** " << FILELINE << endl;
                        }
                        if( readStatus )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** closing connection to _scadaNexus " << FILELINE << endl;
                            }
                            _scadaNexus.CTINexusClose();
                        }
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Waiting to connect to returnNexus " << FILELINE << endl;
                }

                if( !sleep(5000) )   // sleep until outThread interrupts us with a INWAITFOROUT flag.
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Timed out waiting to connect to returnNexus" << endl;
                    }

                    _scadaNexus.CTINexusFlushInput();
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    scadaListenNexus.CTINexusClose();
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
    ULONG BytesRead, BytesWritten;
    bool bDoReply = true;

    INT sendLen, status;

    try
    {
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
                    if(_internalNexus.CTINexusRead(&InMessage, sizeof(InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead == 0)
                    {
                        //  This cannot be
                        set(CtiPortShareIP::INWAITFOROUT, false);
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << FILELINE << " **** Checkpoint **** " << FILELINE << endl;
                        }

                        return;
                    }

                    //  We got one so see if we need to embed the error code
                    if(InMessage.EventCode & ~ENCODED)
                    {
                        // first byte of non-DLC section of message
                        // for ACS set byte 11 to 7d (error) and set byte 12 to event code
                        InMessage.IDLCStat[11] = 0x7d;
                        InMessage.IDLCStat[12] = ProcessEventCode(InMessage.EventCode & ~ENCODED);
                        InMessage.InLength = 2;
                    }
                    else
                    {
                        InMessage.InLength += (2 + PREIDLEN);
                    }

                    //  Go ahead and send it on over
                    if(_scadaNexus.CTINexusValid())
                    {
                        if( getRequestCount() == 0 && !_sequenceFailReported)        // This means something went wrong between submittal and response.
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getPort()->getName() << " port sharing sequence problem: Unexpected field response. " << getRequestCount() << " pending operations." << endl;
                        }

                        decRequestCount();
                        if( getRequestCount() > 0 )
                        {
                            if(!_sequenceFailReported)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getPort()->getName() << " port sharing sequence problem: Multiple responses pending. " << getRequestCount() << " pending operations." << endl;
                            }
                            _sequenceFailReported = true;      // We are OUT of sequence!  Do not report additional failures.
                        }
                        else
                        {
                            _sequenceFailReported = false;      // We are IN sequence!  Next failure may be reported again.
                        }

                        if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUG_OUTTOSCADA)  // Debug the outbound back to SCADA
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << " Write " << InMessage.InLength << " to SCADA bytes: ";
                            for( int byteitr = 0; byteitr < InMessage.InLength; byteitr++ )
                            {
                                dout << string( CtiNumStr((int)((BYTE*)(InMessage.IDLCStat + 11))[byteitr]).hex().zpad(2) ) << " ";
                            }
                            dout << endl;
                        }

                        if(_scadaNexus.CTINexusWrite((char*)(InMessage.IDLCStat + 11), InMessage.InLength, &BytesWritten, 10) || (BytesWritten != InMessage.InLength))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << FILELINE << endl;
                                dout << CtiTime() << " SCADA Nexus send Failed" << endl;
                            }
                            _scadaNexus.CTINexusClose();
                        }
                        else if(gConfigParms.getValueAsULong("DEBUG_PORT_SHARE",0,16) & DEBUGLEVEL_LUDICROUS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << FILELINE << " Successful write back to SCADA" << endl;
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << CtiTime() << " Attempted send on invalid SCADA Nexus" << endl;
                    }
                }
                else if( !isSet(CtiThread::SHUTDOWN) )//Ignore this if we are shutting down
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " !!!! - outThread unable to create listenNexus" << FILELINE << endl;
                    }
                    sleep(1000);
                }
            }
            catch(RWCancellation& rwx)
            {
                interrupt(SHUTDOWN);    // Close it out.
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Port Share IP OutThread cancellation - " << rwx.why() << endl;
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        _scadaNexus.CTINexusClose();
    }
    catch(RWCancellation& rwx)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Port Share IP OutThread cancellation - " << rwx.why() << endl;
        }
    }
}

void CtiPortShareIP::run()
{
    try
    {
        _outThread = rwMakeThreadFunction(*this, &CtiPortShareIP::outThread );
        _outThread.start();

        _inThread  = rwMakeThreadFunction(*this, &CtiPortShareIP::inThread );
        _inThread.start();

        while( !isSet(SHUTDOWN) )
        {
            sleep(250);//This wakes up on interrupt, so it shouldnt matter!
        }
        interrupt(CtiPortShareIP::RUNCOMPLETE);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

int CtiPortShareIP::outThreadValidateNexus()
{
    if(!getReturnNexus()->CTINexusValid())
    {
        createNexus("Port Share Listener Nexus");             //  Blocks awaiting connection from inthread.
    }

    return getReturnNexus()->CTINexusValid();
}

int CtiPortShareIP::inThreadConnectNexus()
{
    if(!getReturnNexus()->CTINexusValid())
    {
        connectNexus();
    }

    return getReturnNexus()->CTINexusValid();
}

/*-----------------------------------------------------------------------------*
 * Shuts down the out thread and sets the flags for the in thread to shut down.
 *-----------------------------------------------------------------------------*/
void CtiPortShareIP::shutDown()
{
    try
    {

        if(RW_THR_COMPLETED != _outThread.join( 250 ))
        {
            if(getDebugLevel() & DEBUGLEVEL_LUDICROUS) //I hate to use this, but everywhere else in this code does...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port Share OutThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
            }
            _outThread.terminate();
        }

        if(RW_THR_COMPLETED != _inThread.join( 250 ))
        {
            if(getDebugLevel() & DEBUGLEVEL_LUDICROUS) //I hate to use this, but everywhere else in this code does...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Port Share InThread did not shutdown gracefully.  Will attempt a forceful shutdown." << endl;
            }
            _inThread.terminate();
        }
    }
    catch(RWxmsg &msg)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << msg.why() << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

}
