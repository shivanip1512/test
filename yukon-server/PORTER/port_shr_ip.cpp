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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/10 23:23:54 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <winsock.h>

#include "types.h"
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

#define FILELINE     " " << __FILE__ << " (" << __LINE__ << ")"

CtiPortShareIP::CtiPortShareIP(CtiPortSPtr myPort, INT listenPort) :
    CtiPortShare(myPort,listenPort),
    _reconnect(false),
    _broadcast(false),
    _ipPort(-1)
{
    set(CtiPortShareIP::INOUTSYNC, false);
    set(CtiPortShareIP::SOCKCONNECTED, false);
    set(CtiPortShareIP::SOCKFAILED, true);
}


CtiPortShareIP::~CtiPortShareIP()
{
    interrupt(SHUTDOWN);    // Make sure we don't go back into waitOnSCADAClient if that is what we are doing.

    if(_scadaNexus.CTINexusValid())
    {
        _scadaNexus.CTINexusClose();
    }
}


/*-----------------------------------------------------------------------------*
 * This method waits for the outThread connection to complete the trasaction and
 * post the INOUTSYNC flag.  Otherwise (no post) we clear the inbound socket and
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
    CTINEXUS tmpNexus;

    OUTMESS *OutMessage = NULL;

    while( !isSet(SHUTDOWN) )     // Have we been kicked from on high.
    {
        //  make sure we're connected to the outThread nexus
        if(inThreadConnectNexus())
        {
            if(!isSet(CtiPortShareIP::INOUTSYNC))
            {
                if( sleep(90000) )   // sleep until outThread interrupts us with a INOUTSYNC flag.
                {
                    if( isSet(CtiThread::SHUTDOWN) )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                            dout << " Port share IP is shutting down" << endl;
                        }
                        continue;  // Get out now.
                    }
                    // Most likely we were awakened by Inherited::isSet(CtiPortShare::INOUTSYNC) (and its accompanying interrupt())
                }
                else  // Ay caramba.  We never heard from the outThread...
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint ****" << FILELINE << endl;
                        dout << RWTime() << "inThread timed out waiting for synchronization flag" << FILELINE << endl;
                        set(CtiPortShareIP::INOUTSYNC, true);     // outThread must be broken - we will try to read again
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
                            dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                            dout << RWTime() << " SCADA Nexus \"no reply\" error send Failed" << endl;
                        }
                        _scadaNexus.CTINexusClose();
                    }
                    else if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                        dout << RWTime() << " Sent \"no reply\" error to SCADA system " << endl;
                    }

                    _scadaNexus.CTINexusFlushInput();
                }
                //  recheck the nexus - it may have died in the (up to) 90 seconds we were asleep
                continue;
            }

            //  make sure the listener is up
            if(_scadaListenNexus.sockt == INVALID_SOCKET)
            {
                if(_scadaListenNexus.CTINexusCreate(getIPPort()))
                {
                    _scadaListenNexus.CTINexusClose();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint ****" << FILELINE << endl;
                        dout << RWTime() << "inThread couldn't create _scadaListenNexus - sleeping 5 seconds **** "<< endl;
                    }
                    //  sanity check - 5 second delay keeps us from an insanely tight loop if we break repeatedly
                    sleep(5000);
                }
            }

            //  check for CTIDBG_new connections if we haven't read anything in the last minute
            //    or if we're not connected
            if( loopsSinceRead > 4 || !_scadaNexus.CTINexusValid())
            {
                _scadaListenNexus.CTINexusConnect(&tmpNexus, NULL, 1, CTINEXUS_FLAG_READANY);

                if(tmpNexus.CTINexusValid())
                {
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                        dout << RWTime() << "inThread got a CTIDBG_new SCADA connection - copying tmpNexus to _scadaNexus" << endl;
                    }
                    _scadaNexus.CTINexusClose();  //  make sure the old/invalid socket is closed
                    _scadaNexus = tmpNexus;           //  byte-for-byte copy - no pointers, ...
                    tmpNexus.sockt = INVALID_SOCKET;  //  ...  and observe how i make tmpNexus's sockt handle point elsewhere, so is all good
                    setBusyCount(0);           // Reconnect zeros the busy value.
                }
            }

            //  if we don't have a valid SCADA connection
            if(!_scadaNexus.CTINexusValid())
            {
                if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** waiting for CTIDBG_new connection - _scadaNexus is invalid" << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                sleep(500);
            }
            else
            {
                if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                }

                bytesRead = 0; // <-- else we tend to report we've read a rather amazing 4e9 bytes from the TCP stack...
                // listen to our SCADA system for a bit.
                if(((readStatus = _scadaNexus.CTINexusRead(Buffer, 4, &bytesRead, 15) == NORMAL)) && (bytesRead > 0))
                {
                    loopsSinceRead = 0;
                    set(CtiPortShareIP::INOUTSYNC, false);     // reset the SYNC bool to indicate we've done a read

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
                                    dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                                    dout << RWTime() << " Could not allocate CTIDBG_new OUTMESS for inThread" << endl;
                                }
                                return;
                            }

                            if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << RWTime() << " Read " << bytesRead << " bytes:" << endl;
                                for( int byteitr = 0; byteitr < bytesRead; byteitr++ )
                                {
                                    dout << RWCString( CtiNumStr((int)Buffer[byteitr]).hex().zpad(2) ) << " ";
                                }
                                dout << endl;
                            }

                            OutMessage->Port = getPort()->getPortID();
                            OutMessage->Remote = Buffer[1] >> 1;
                            OutMessage->TimeOut = 1;
                            OutMessage->Retry = 0;
                            OutMessage->Source = 0;
                            OutMessage->Destination = 0;
                            OutMessage->Sequence = 0;
                            OutMessage->Priority = MAXPRIORITY - 1;
                            OutMessage->ReturnNexus = getReturnNexus();
                            OutMessage->SaveNexus = NULL;

                            //  Figure out what the out length is
                            OutMessage->OutLength = Buffer[3] - 3;
                            OutMessage->InLength = 0;

                            //  Now figure out the event code
                            if(OutMessage->Remote != RTUGLOBAL)
                            {
                                OutMessage->EventCode = RESULT | ENCODED;
                                incBusyCount();
                            }
                            else
                            {
                                // broadcast message - tell inThread to read again, we expect no reply from this type of message
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Intercepted RTU broadcast message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                set(CtiPortShareIP::INOUTSYNC, true);     // reset the SYNC bool which can only be set by outThread.
                                OutMessage->EventCode = NOWAIT | NORESULT | ENCODED;
                            }

                            //  Check if this is a time sync
                            if((Buffer[5] & 0x7f) == IDLC_TIMESYNC)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Intercepted time sync message **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                OutMessage->EventCode |= TSYNC;
                            }


                            //  Now copy the message into the buffer...
                            memcpy (OutMessage->Buffer.OutMessage, Buffer, bytesRead);

                            //  ... and put it on the queue
                            if(getPort()->writeQueue(OutMessage->EventCode, sizeof (*OutMessage), (char *) OutMessage, OutMessage->Priority))
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                                    dout << RWTime() << " Error Writing to Queue for Port " << getPort()->getPortID() << endl;
                                }

                                delete OutMessage;
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "Failed to read header-specified rest of IDLC message" << endl;
                                dout << "Buffer[3] = " << (int)Buffer[3] << endl;
                                dout << "Actually read " << bytesRead << " bytes" << endl;
                            }
                            //  we're broken, try to read some more
                            set(CtiPortShareIP::INOUTSYNC, true);     // reset the SYNC bool which can only be set by outThread.
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "Non-IDLC message intercepted" << endl;
                        }
                    }
                }
                else
                {
                    loopsSinceRead++;
                    if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - no input from _scadaNexus.CTINexusRead **** " << FILELINE << endl;
                    }
                    if( readStatus )
                    {
                        _scadaNexus.CTINexusClose();
                    }
                }
            }
        }
        else
        {
            if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                dout << RWTime() << " Waiting to connect to returnNexus" << endl;
            }

            if( !sleep(5000) )   // sleep until outThread interrupts us with a INOUTSYNC flag.
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Timed out waiting to connect to returnNexus" << endl;
                }

                _scadaNexus.CTINexusFlushInput();
            }
        }
    }
    _scadaListenNexus.CTINexusClose();
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
   transaction.  When this happens we post the INOUTSYNC flag and return.
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
    int seqComplain = 0;


    try
    {
        while(!isSet(CtiThread::SHUTDOWN))
        {
            //  make sure inThread can read whenever the nexus is set up
            set(CtiPortShareIP::INOUTSYNC, true);
            interrupt();

            if(outThreadValidateNexus())
            {
                //  wait forever to hear back from the port
                if(_listenNexus.CTINexusRead(&InMessage, sizeof(InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead == 0)
                {
                    //  This cannot be
                    set(CtiPortShareIP::INOUTSYNC, true);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << FILELINE << " **** Checkpoint **** " << FILELINE << endl;
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
                    if( this->getBusyCount() > 0 )
                    {
                        if( (this->decBusyCount()).getBusyCount() > 0 )
                        {
                            if( seqComplain++ < 4 )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << (getPort())->getName() << " port sharing sequence problem. " << getBusyCount() << " pending operations" << endl;
                            }
                            else
                            {
                                this->decBusyCount();
                                seqComplain = 0;
                            }
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "Would have tried to decrement busyCount below 0" << endl;
                        }
                    }

                    if(_scadaNexus.CTINexusWrite((char*)(InMessage.IDLCStat + 11), InMessage.InLength, &BytesWritten, 10) || (BytesWritten != InMessage.InLength))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                            dout << RWTime() << " SCADA Nexus send Failed" << endl;
                        }
                        _scadaNexus.CTINexusClose();
                    }
                    else if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                        dout << RWTime() << " Successful write... ?" << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " Attempted send on invalid SCADA Nexus" << endl;
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << FILELINE << endl;
                    dout << RWTime() << " outThread unable to create listenNexus" << FILELINE << endl;
                    sleep(1000);
                }
            }
        }

        _scadaNexus.CTINexusClose();
    }
    catch(RWCancellation& rwx)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Port Share IP OutThread cancellation - " << rwx.why() << endl;
        }
    }
}

void CtiPortShareIP::run()
{
    _outThread = rwMakeThreadFunction(*this, &CtiPortShareIP::outThread );
    _outThread.start();

    inThread();                                              //  This is where we spend our time during execution.

    _outThread.requestCancellation(250);                     //  Post this so the thread terminates when the socket is kicked

    _scadaNexus.CTINexusClose();                              //  Releases/kicks the SCADA nexus in the outThread (interrupts the blocking read).

    if( _outThread.isValid() && _outThread.getExecutionState() & RW_THR_ACTIVE )
    {
        if( _outThread.requestCancellation(100) == RW_THR_ABORTED )
        {
            _outThread.terminate();  //  Cancellation started but didn't complete - kick it over
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
