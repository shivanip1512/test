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
* REVISION     :  $Revision: 1.28.6.1 $
* DATE         :  $Date: 2008/11/13 17:23:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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

#include <rw/toolpro/inetaddr.h>

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
    interrupt(CtiThread::SHUTDOWN);    // Make sure we don't go back into waitOnSCADAClient if that is what we are doing.
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
        dout << CtiTime() << " " << getIDString() << " - *** CHECKPOINT *** PortShare refused to shutdown after 5 seconds " << FILELINE << endl;
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
 * If we do get the post, we read a new message from the socket and write it out
 * to the appropriate portQueue.
 *-----------------------------------------------------------------------------*/
void CtiPortShareIP::inThread()
{
    int recLen, loopsSinceRead = 0, readStatus;
    unsigned long bytesRead;
    BYTE Buffer[300];
    unsigned long bytesWritten;
    unsigned long remainderbytes;
    CTINEXUS scadaListenNexus;          // Presents the IP Port for scada to connect to.
    CTINEXUS tmpNexus;

    OUTMESS *OutMessage = NULL;

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
                        if( _scadaNexus.CTINexusValid() )
                        {
                            set(CtiPortShareIP::INWAITFOROUT, false);  // outThread might be broken - we will allow a new read from scada.
                            decRequestCount();                         // The last request is assumed timed out!

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getIDString() << " - inThread timed out waiting for synchronization flag. " << FILELINE << endl;
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
                                    dout << CtiTime() << " " << getIDString() << " - SCADA Nexus \"no reply\" error send Failed " << FILELINE << endl;
                                }
                                _scadaNexus.CTINexusClose();
                            }
                            else if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getIDString() << " - Sent \"no reply\" error to SCADA system " << FILELINE << endl;
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
                            dout << CtiTime() << " " << getIDString() << " - inThread couldn't create scadaListenNexus - sleeping 5 seconds " << FILELINE << endl;
                        }
                        //  sanity check - 5 second delay keeps us from an insanely tight loop if we break repeatedly
                        sleep(5000);
                    }
                }

                //  check for new connections if we haven't read anything in the last N loops.
                //    or if we're not connected
                if( !_scadaNexus.CTINexusValid() || loopsSinceRead > gConfigParms.getValueAsULong("PORTER_PORTSHARE_LOOPCOUNT", 4))
                {
                    if( getDebugLevel(DEBUG_SCADA_CONNECTION) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getIDString() << " - inThread is looking for a new SCADA connection. Loops = " << loopsSinceRead << ". " << FILELINE << endl;
                    }
                    scadaListenNexus.CTINexusConnect(&tmpNexus, NULL, 1000, CTINEXUS_FLAG_READANY);

                    if(tmpNexus.CTINexusValid())
                    {
                        if( getDebugLevel(DEBUG_SCADA_CONNECTION) )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getIDString() << " - inThread got a new SCADA connection - copying tmpNexus to _scadaNexus " << FILELINE << endl;
                        }
                        _scadaNexus.CTINexusClose();  //  make sure the old/invalid socket is closed
                        _scadaNexus = tmpNexus;           //  byte-for-byte copy - no pointers, ...
                        _snprintf(_scadaNexus.Name, sizeof(_scadaNexus.Name) - 1, "%s", getIDString().data());

                        tmpNexus.sockt = INVALID_SOCKET;  //  ...  and observe how i make tmpNexus's sockt handle point elsewhere, so is all good
                        setRequestCount(0);             // Reconnect zeros the busy value.
                        _sequenceFailReported = false;  // Allow a new report on this nexus.
                        loopsSinceRead = 0;
                    }

                    if(!_scadaNexus.CTINexusValid())
                    {
                        set(CtiPortShareIP::INWAITFOROUT, false);     // Do not cause this thread to wait before resetting scadaNexus.
                        sleep(2000);
                    }
                }

                //  if we have a valid SCADA connection
                if(_scadaNexus.CTINexusValid())
                {
                    bytesRead = 0; // <-- else we tend to report we've read a rather amazing 4e9 bytes from the TCP stack...
                    // listen to our SCADA system for a bit.
                    if(((readStatus = _scadaNexus.CTINexusRead(Buffer, 4, &bytesRead, 15)) == NORMAL) && (bytesRead > 0))
                    {
                        loopsSinceRead = 0;

                        if(Buffer[0] == 0x7e)
                        {
                            //  it's an IDLC message, read the rest

                            //  is this an unsequenced control message?
                            if(Buffer[2] & 0x01 && Buffer[2] != HDLC_UD )
                            {
                                //  we only need to read one more byte, so we don't need any of the fancy ioctl stuff below
                                readStatus = _scadaNexus.CTINexusRead(&(Buffer[4]), 1, &bytesRead, 15);
                            }
                            else if(((readStatus = _scadaNexus.CTINexusRead(&(Buffer[4]), (Buffer[3] + 2), &bytesRead, 15)) == NORMAL) && (bytesRead == (Buffer[3] + 2 + 4)))
                            {
                                // If there are any bytes still in the socket we must find the most recent.  The continue will
                                // cause a new read.
                                if( (SOCKET_ERROR != ioctlsocket(_scadaNexus.sockt, FIONREAD, &remainderbytes)) && remainderbytes != 0)
                                    continue;
                            }

                            if( readStatus == NORMAL )
                            {
                                // Get an OutMessage.
                                if( (OutMessage = CTIDBG_new OUTMESS) == NULL )
                                {
                                    // This is bad.
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " " << getIDString() << " - **** Checkpoint **** could not allocate CTIDBG_new OUTMESS for inThread " << FILELINE << endl;
                                    }
                                    return;
                                }

                                if( getDebugLevel(DEBUG_INPUT_FROM_SCADA) )  // Debug the inbound from SCADA
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " " << getIDString() << " - read " << bytesRead << " SCADA bytes: ";

                                    for( int byteitr = 0; byteitr < bytesRead; byteitr++ )
                                    {
                                        dout << string( CtiNumStr((int)Buffer[byteitr]).hex().zpad(2) ) << " ";
                                    }

                                    dout << endl;
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
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " " << getIDString() << " - Intercepted RTU broadcast message " << FILELINE << endl;
                                    }

                                    set(CtiPortShareIP::INWAITFOROUT, false);     // set the SYNC bool so inThread does not wait.
                                    OutMessage->EventCode = NOWAIT | NORESULT | ENCODED;
                                }

                                //  Check if this is a time sync
                                if((Buffer[5] & 0x7f) == IDLC_TIMESYNC)
                                {
                                    if( getDebugLevel(DEBUG_SPECIAL_MESSAGES) )
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " " << getIDString() << " - Intercepted time sync message " << FILELINE << endl;
                                    }

                                    OutMessage->EventCode |= TSYNC;
                                }

                                //  Now copy the message into the buffer...
                                memcpy (OutMessage->Buffer.OutMessage, Buffer, bytesRead);

                                //  ... and put it on the correct port's queue
                                if(getPort()->writeQueue(OutMessage))
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " " << getIDString() << " - error writing to queue **** " << FILELINE << endl;
                                    }

                                    set(CtiPortShareIP::INWAITFOROUT, false);  //  we couldn't write, so we're not waiting for a response
                                    sleep(2000);

                                    delete OutMessage;
                                }
                            }
                            else
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " " << getIDString() << " - failed to read header-specified rest of IDLC message " << FILELINE << endl;
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
                                dout << CtiTime() << " " << getIDString() << " - non-IDLC message intercepted " << FILELINE << endl;
                            }
                        }
                    }
                    else
                    {
                        sleep(1000);        // Make certain we don't loop too crazy here
                        loopsSinceRead++;

                        if( readStatus != -ERR_CTINEXUS_READTIMEOUT )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getIDString() << " - closing connection to _scadaNexus " << readStatus << " error on socket " << FILELINE << endl;
                            }
                            _scadaNexus.CTINexusClose();
                        }
                        else if( getDebugLevel(DEBUG_NO_INPUT) )
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getIDString() << " - no input from _scadaNexus.CTINexusRead " << FILELINE << endl;
                        }

                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getIDString() << " - waiting to connect to returnNexus " << FILELINE << endl;
                }

                if( !sleep(5000) )   // sleep until outThread interrupts us with a INWAITFOROUT flag.
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getIDString() << " - timed out waiting to connect to returnNexus" << FILELINE << endl;
                    }

                    _scadaNexus.CTINexusFlushInput();
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << getIDString() << " - **** EXCEPTION Checkpoint **** " << FILELINE << endl;
            }
        }
    }
    scadaListenNexus.CTINexusClose();
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
    ULONG BytesRead, BytesWritten;
    bool bDoReply = true;

    INT sendLen, status;

    string thread_name = "PSout" + CtiNumStr(_port->getPortID()).zpad(4);
    SetThreadName(-1, thread_name.c_str());

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
                            dout << CtiTime() << getIDString() << " - **** Checkpoint **** " << FILELINE << endl;
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
                        // No need to add this on - we already have the proper length for the message
                        // InMessage.InLength += (2 + PREIDLEN);
                    }

                    //  Go ahead and send it on over
                    if(_scadaNexus.CTINexusValid())
                    {
                        if( getRequestCount() == 0 && !_sequenceFailReported)        // This means something went wrong between submittal and response.
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getIDString() << " - port sharing sequence problem: Unexpected field response. " << getRequestCount() << " pending operations. " << FILELINE << endl;
                        }

                        decRequestCount();
                        if( getRequestCount() > 0 )
                        {
                            if(!_sequenceFailReported)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getIDString() << " - port sharing sequence problem: Multiple responses pending. " << getRequestCount() << " pending operations. " << FILELINE << endl;
                            }
                            _sequenceFailReported = true;      // We are OUT of sequence!  Do not report additional failures.
                        }
                        else
                        {
                            _sequenceFailReported = false;      // We are IN sequence!  Next failure may be reported again.
                        }

                        if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )  // Debug the outbound back to SCADA
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << getIDString() << " - Write " << InMessage.InLength << " to SCADA bytes: ";
                            for( int byteitr = 0; byteitr < InMessage.InLength; byteitr++ )
                            {
                                dout << string( CtiNumStr((int)((BYTE*)(InMessage.IDLCStat + 11))[byteitr]).hex().zpad(2) ) << " ";
                            }
                            dout << endl;
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

                            if(_scadaNexus.CTINexusWrite((char*)InMessage.IDLCStat, InMessage.InLength, &BytesWritten, 10) || (BytesWritten != InMessage.InLength))
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " " << getIDString() << " - SCADA Nexus send Failed" << FILELINE << endl;
                                }
                                _scadaNexus.CTINexusClose();
                            }
                            else if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getIDString() << " - Successful write back to SCADA " << FILELINE << endl;
                            }
                        }
                        else  // if InMessage.IDLCStat[11] == 0x7e OR InMessage.IDLCStat[13] == HDLC_UD
                        {
                            if(_scadaNexus.CTINexusWrite((char*)(InMessage.IDLCStat + 11), InMessage.InLength, &BytesWritten, 10) || (BytesWritten != InMessage.InLength))
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " " << getIDString() << " - SCADA Nexus send Failed" << FILELINE << endl;
                                }
                                _scadaNexus.CTINexusClose();
                            }
                            else if( getDebugLevel(DEBUG_OUTPUT_TO_SCADA) )
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << getIDString() << " - Successful write back to SCADA " << FILELINE << endl;
                            }
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getIDString() << " - Attempted send on invalid SCADA Nexus " << FILELINE << endl;
                    }
                }
                else if( !isSet(CtiThread::SHUTDOWN) )//Ignore this if we are shutting down
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getIDString() << " - !!!! - outThread unable to create listenNexus" << FILELINE << endl;
                    }
                    sleep(1000);
                }
            }
            catch(RWCancellation& rwx)
            {
                interrupt(CtiThread::SHUTDOWN);    // Close it out.
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getIDString() << " - Port Share IP OutThread cancellation - " << rwx.why() << endl;
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getIDString() << " - **** EXCEPTION Checkpoint **** " << FILELINE << endl;
                }
            }
        }

        _scadaNexus.CTINexusClose();
    }
    catch(RWCancellation& rwx)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getIDString() << " - Port Share IP OutThread cancellation - " << rwx.why() << endl;
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
            dout << CtiTime() << " " << getIDString() << " - **** EXCEPTION Checkpoint **** " << FILELINE << endl;
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
            if( getDebugLevel(DEBUG_THREAD) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << getIDString() << " - Port Share OutThread did not shutdown gracefully.  Will attempt a forceful shutdown. " << FILELINE << endl;
            }
            _outThread.terminate();
        }

        if(RW_THR_COMPLETED != _inThread.join( 250 ))
        {
            if( getDebugLevel(DEBUG_THREAD) )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << getIDString() << " - Port Share InThread did not shutdown gracefully.  Will attempt a forceful shutdown. " << FILELINE << endl;
            }
            _inThread.terminate();
        }
    }
    catch(RWxmsg &msg)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getIDString() << " - **** EXCEPTION Checkpoint **** " << FILELINE << endl;
            dout << msg.why() << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getIDString() << " - **** EXCEPTION Checkpoint **** " << FILELINE << endl;
        }
    }

}
