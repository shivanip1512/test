/*-----------------------------------------------------------------------------*
*
* File:   pilserver
*
* Date:   10/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PIL/pilserver.cpp-arc  $
* REVISION     :  $Revision: 1.50 $
* DATE         :  $Date: 2004/05/05 15:31:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include <windows.h>
#include <iomanip>
#include <iostream>
#include <vector>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\cstring.h>
#include <rw\rwtime.h>
#include <rw/toolpro/winsock.h>
#include <rw\thr\thrfunc.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "dev_grp_versacom.h"
#include "dsm2.h"
#include "ctinexus.h"
#include "porter.h"

#include "cparms.h"
#include "netports.h"
#include "queent.h"
#include "pil_conmgr.h"
#include "pil_exefct.h"
#include "pilserver.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_reg.h"
#include "mgr_device.h"
#include "mutex.h"
#include "numstr.h"
#include "logger.h"
#include "executor.h"
#include "dlldefs.h"
#include "connection.h"

#include "pilglob.h"
#include "ctibase.h"
#include "dllbase.h"
#include "logger.h"
#include "repeaterrole.h"
#include "rte_ccu.h"
#include "utility.h"


void ReportMessagePriority( CtiMessage *MsgPtr, CtiDeviceManager *&DeviceManager );
extern IM_EX_CTIBASE void DumpOutMessage(void *Mess);

CtiConnection           VanGoghConnection;
CtiPILExecutorFactory   ExecFactory;

/* Define the return nexus handle */
/* external dll global */
DLLIMPORT extern CTINEXUS PorterNexus;
DLLIMPORT extern VOID PortPipeCleanup (ULONG Reason);


int CtiPILServer::execute()
{
    _broken = false;

    try
    {
        /*----------------------------------------------------------*
         * This is a key operation here.. This is the "old-style"
         * connection to porter's standard portpipe connectionthread.
         *
         * The machinery is in ctibase, which means it is available
         * even to the internals of porter.. an added benefit right
         * now, since we really are doing a protocol conversion here.
         *----------------------------------------------------------*/
        PortPipeInit (NOWAIT);


        if(!bServerClosing)
        {
            MainThread_ = rwMakeThreadFunction(*this, &CtiPILServer::mainThread);
            MainThread_.start();

            ConnThread_ = rwMakeThreadFunction(*this, &CtiPILServer::connectionThread);
            ConnThread_.start();

            ResultThread_ = rwMakeThreadFunction(*this, &CtiPILServer::resultThread);
            ResultThread_.start();

            _nexusThread = rwMakeThreadFunction(*this, &CtiPILServer::nexusThread);
            _nexusThread.start();

            _nexusWriteThread = rwMakeThreadFunction(*this, &CtiPILServer::nexusWriteThread);
            _nexusWriteThread.start();

            _vgConnThread = rwMakeThreadFunction(*this, &CtiPILServer::vgConnThread);
            _vgConnThread.start();
        }
    }
    catch(const RWxmsg& x)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
    }

    return 0;
}

void CtiPILServer::mainThread()
{

    BOOL                       bQuit = FALSE;
    int                        Tag = 100;
    int                        nCount = 0;
    int                        status;

    RWTime                     TimeNow;

    // CtiConnection::InQ_t          *APQueue;
    CtiExecutor                   *pExec;
    CtiMessage                    *MsgPtr;

    RWTime  starttime, finishtime;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PILMainThread  : Started as TID " << rwThreadId() << endl;
    }

    VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
    VanGoghConnection.setName("Dispatch");
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(PIL_REGISTRATION_NAME, rwThreadId(), TRUE));

    try
    {
        CtiLockGuard<CtiMutex> guard(server_mux);

        NetPort  = RWInetPort(PORTERINTERFACENEXUS);
        NetAddr  = RWInetAddr(NetPort);           // This one for this server!

        Listener = CTIDBG_new RWSocketListener(NetAddr);

        if(!Listener)
        {
            dout << "Could not open socket " << NetAddr << " for listning" << endl;

            exit(-1);
        }

        _listenerAvailable = TRUE;                 // Release the connection handler

    }
    catch(RWxmsg &msg)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception " << __FILE__ << " (" << __LINE__ << ") " << msg.why() << endl;
        throw;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 30, 0);

    /*
     *  MAIN: The main PIL loop lives here for all time!
     */

    for( ; !bQuit ; )
    {
        try
        {
            // Blocks for 1000 ms or until a queue entry exists
            MsgPtr = MainQueue_.getQueue(500);

            if(MsgPtr != NULL)
            {
                starttime = starttime.now();

                if(DebugLevel & DEBUGLEVEL_PIL_MAINTHREAD)
                {
                    ReportMessagePriority(MsgPtr, DeviceManager);
                }

                /* Use the same time base for the full scan check */
                TimeNow = TimeNow.now();   // update the time...

                if(MsgPtr->getMessageTime().seconds() < (TimeNow.seconds() - 900))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << TimeNow << " PIL processing an inbound message which is over 15 minutes old.  Message will be discarded." << endl;
                        dout << " >>---------- Message Content ----------<< " << endl;
                        MsgPtr->dump();
                        dout << " <<---------- Message Content ---------->> " << endl;
                    }

                    delete MsgPtr;    // No one attached it to them, so we need to kill it!
                    MsgPtr = 0;
                }
                else if((pExec = ExecFactory.getExecutor(MsgPtr)) != NULL)
                {
                    status = pExec->ServerExecute(this);

                    delete pExec;
                }
                else
                {
                    delete MsgPtr;    // No one attached it to them, so we need to kill it!
                }

                if(status)
                {
                    bQuit = TRUE;
                    Inherited::shutdown();
                }

                finishtime = finishtime.now();

                if(finishtime.seconds() - starttime.seconds() > 5)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " PIL mainthread took " << (finishtime.seconds() - starttime.seconds()) << " seconds to process the last message." << endl;
                    }
                }
            }

            try
            {
                rwServiceCancellation();
            }
            catch(RWCancellation& c)
            {

                bServerClosing = TRUE;
                bQuit = TRUE;

                // Force the inherited Listener socket to close!
                Inherited::shutdown();                   // Should cause the ConnThread_ to be closed!
                ConnThread_.join();                      // Wait for the Conn thread to die.

                ResultThread_.requestCancellation(750);

                if(ResultThread_.join(10000) == RW_THR_TIMEOUT)                     // Wait for the closure
                {
                    if(ResultThread_.requestCancellation(150) == RW_THR_TIMEOUT)   // Mark it for destruction...
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL Server shutting down the ResultThread_: TIMEOUT " << endl;
                        }
                        if(ResultThread_.join(500) == RW_THR_TIMEOUT)                     // Wait for the closure
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL Server shutting down the ResultThread_: FAILED " << endl;

                            ResultThread_.terminate();
                        }
                    }
                }

                _nexusWriteThread.requestCancellation(750);

                if(_nexusWriteThread.join(10000) == RW_THR_TIMEOUT)                     // Wait for the closure
                {
                    if(_nexusWriteThread.requestCancellation(150) == RW_THR_TIMEOUT)   // Mark it for destruction...
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL Server shutting down the _nexusWriteThread: TIMEOUT " << endl;
                        }
                        if(_nexusWriteThread.join(500) == RW_THR_TIMEOUT)                     // Wait for the closure
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL Server shutting down the _nexusWriteThread: FAILED " << endl;

                            _nexusWriteThread.terminate();
                        }
                    }
                }

                _nexusThread.requestCancellation(750);

                if(_nexusThread.join(10000) == RW_THR_TIMEOUT)                     // Wait for the closure
                {
                    if(_nexusThread.requestCancellation(150) == RW_THR_TIMEOUT)   // Mark it for destruction...
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL Server shutting down the _nexusThread: TIMEOUT " << endl;
                        }
                        if(_nexusThread.join(500) == RW_THR_TIMEOUT)                     // Wait for the closure
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL Server shutting down the _nexusThread: FAILED " << endl;

                            _nexusThread.terminate();
                        }
                    }
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PIL Server shut down complete " << endl;
                }
            }
        }
        catch(...)
        {
            Sleep(5000);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ****  EXCEPTION: PIL mainThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  - Will attmept to recover" << endl;
        }
    }

    _broken = true;

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    VanGoghConnection.ShutdownConnection();
}

void CtiPILServer::connectionThread()
{
    int               i=0;
    BOOL              bQuit = FALSE;

    CtiCommandMsg     *CmdMsg = NULL;

    CtiExchange       *XChg;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ConnThread     : Started as TID " << rwThreadId() << endl;
    }

    /*
     *  Wait for Main to get lister up and ready to go.
     */
    while(!_listenerAvailable)
    {
        rwSleep(250);
    }

    for(;!bQuit;)
    {
        try
        {
            RWSocketPortal sock;

            if( Listener )
            {
                sock = (*Listener)();
            }
            else
            {
                bQuit = TRUE;
                continue; // the for loop
            }

            if( sock.socket().valid() )
            {
                XChg = CTIDBG_new CtiExchange(sock);

                if(XChg != NULL)
                {
                    CtiPILConnectionManager *ConMan = CTIDBG_new CtiPILConnectionManager(XChg, &MainQueue_);

                    if(ConMan != NULL)
                    {
                        ConMan->setClientName("DEFAULT");

                        /*
                         *  Need to inform MainThread of the "New Guy" so that he may control its destiny from
                         *  now on.
                         */

                        CmdMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::NewClient, 15);


                        if(CmdMsg != NULL)
                        {
                            CmdMsg->setConnectionHandle((void*) ConMan);
                            MainQueue_.putQueue(CmdMsg);
                            ConMan->ThreadInitiate();
                        }
                        else
                        {
                            delete ConMan;    // Also deletes the XChg...
                            ConMan = NULL;

                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ERROR Starting CTIDBG_new connection! " << rwThreadId() << endl;
                        }
                    }
                }
            }
            else
            {
                Sleep(1000);   // No runaways here please
            }
        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() == RWNETENOTSOCK)
            {
                bQuit = TRUE;     // get out of the for loop
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Socket error RWNETENOTSOCK" << endl;
            }
            else
            {
                bQuit = TRUE;
            }
        }
        catch(RWxmsg& msg )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << RWTime() << " ConnThread: Failed... " ;
                dout << msg.why() << endl;

                bQuit = TRUE;
            }
            throw;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ConnThread: " << rwThreadId() << " is terminating... " << endl;
    }

    _broken = true;
    return;
}

void CtiPILServer::resultThread()
{
    INT i;
    INT status = NORMAL;
    INT x = 0;

    PBYTE    pMsg = NULL;

    CtiConnection  *Conn = NULL;
    CtiMessage     *pVg  = NULL;
    OUTMESS        *OutMessage;


    /* Define the various records */
    CtiDeviceSPtr DeviceRecord;

    /* Time variable for decode */
    RWTime      TimeNow;

    ULONG       BytesRead;
    INMESS      *InMessage = 0;

    RWTPtrSlist< OUTMESS    > outList;
    RWTPtrSlist< CtiMessage > retList;
    RWTPtrSlist< CtiMessage > vgList;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ResThread      : Started as TID " << rwThreadId() << endl;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        try
        {
            // Let's go look at the inbound sList, if we can!
            while( _inList.isEmpty() && !bServerClosing)
            {
                Sleep( 500 );

                try
                {
                    rwServiceCancellation();
                }
                catch(const RWCancellation& cMsg)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ResThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
                    bServerClosing = TRUE;
                    break;  // the while!
                }
            }

            if( !bServerClosing && !_inList.isEmpty() )
            {
                CtiLockGuard< CtiMutex > ilguard( _inMux, 15000 );

                if(ilguard.isAcquired())
                {
                    InMessage = _inList.get();
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Unable to lock PIL's INMESS list. You should not see this much." << endl;
                }
            }

            if(bServerClosing || InMessage == 0)
            {
                continue;
            }

            {
                LONG id = InMessage->TargetID;

                if(id == 0)
                {
                    id = InMessage->DeviceID;
                }

                // Find the device..
                DeviceRecord = DeviceManager->getEqual(id);

                if(DeviceRecord && !(InMessage->MessageFlags & MSGFLG_ROUTE_TO_PORTER_GATEWAY_THREAD))
                {
                    if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Pilserver resultThread received an InMessage for " << DeviceRecord->getName();
                        dout << " at priority " << InMessage->Priority << endl;
                    }

                    /* get the time for use in the decodes */
                    TimeNow = RWTime();

                    try
                    {
                        // Do some device dependant work on this Inbound message!
                        DeviceRecord->ProcessResult( InMessage, TimeNow, vgList, retList, outList);
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << RWTime() << " Process Result FAILED " << DeviceRecord->getName() << endl;
                        }
                    }
                }
                else if( InMessage->MessageFlags & MSGFLG_ROUTE_TO_PORTER_GATEWAY_THREAD )
                {
                    // We need response strings from someone.  How can we get a list of results back?

                    RWCString bufstr((char*)(InMessage->Buffer.GWRSt.MsgData));
                    retList.insert( CTIDBG_new CtiReturnMsg(0,
                                                            RWCString(InMessage->Return.CommandStr),
                                                            bufstr,
                                                            InMessage->EventCode,
                                                            InMessage->Return.RouteID,
                                                            InMessage->Return.MacroOffset,
                                                            InMessage->Return.Attempt,
                                                            InMessage->Return.TrxID,
                                                            InMessage->Return.UserID,
                                                            InMessage->Return.SOE,
                                                            RWOrdered()));

                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "InMessage received from unknown device.  Device ID: " << InMessage->DeviceID << endl;
                    dout << " Port listed as                                   : " << InMessage->Port     << endl;
                    dout << " Remote listed as                                 : " << InMessage->Remote   << endl;
                }

                try
                {
                    if(outList.entries())
                    {
                        for( i = outList.entries() ; i > 0; i-- )
                        {
                            OutMessage = outList.get();
                            OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC;
                            _porterOMQueue.putQueue(OutMessage);
                            OutMessage = 0;
                        }
                    }

                    if( retList.entries() > 0 )
                    {
                        if((DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD) && vgList.entries())
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Info **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "   Device " << (DeviceRecord ? DeviceRecord->getName() : "UNKNOWN") << " has generated a dispatch return message.  Data may be duplicated." << endl;
                            }
                        }

                        RWCString cmdstr(InMessage->Return.CommandStr);
                        CtiCommandParser parse( cmdstr );
                        if(parse.getFlags() & CMD_FLAG_UPDATE)
                        {
                            for(i = 0; i < retList.entries(); i++)
                            {
                                CtiMessage *&pMsg = retList.at(i);

                                if(pMsg->isA() == MSG_PCRETURN || pMsg->isA() == MSG_POINTDATA)
                                {
                                    vgList.append(pMsg->replicateMessage());       // Mash it in ther if we said to do so.
                                }
                            }
                        }
                    }


                    while( (i = retList.entries()) > 0 )
                    {
                        CtiMessage *pRet = retList.get();

                        if((Conn = ((CtiConnection*)InMessage->Return.Connection)) != NULL)
                        {
                            if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
                            {
                                pRet->dump();
                            }

                            Conn->WriteConnQue(pRet);
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Notice: Request message did not indicate return path. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << RWTime() << " Response to client will be discarded." << endl;
                            }
                            delete pRet;
                        }
                    }

                    while( (i = vgList.entries()) > 0 )
                    {
                        pVg = vgList.get();
                        VanGoghConnection.WriteConnQue(pVg);
                    }
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if(InMessage)
                {
                    delete InMessage;
                    InMessage = 0;
                }
            }
        }
        catch(...)
        {
            Sleep(5000);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ****  EXCEPTION: PIL resultThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  - Will attmept to recover" << endl;
        }

    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ResThread : " << rwThreadId() << " terminating " << endl;
    }

    _broken = true;
}

void CtiPILServer::nexusThread()
{
    INT i = 0;
    INT status = NORMAL;
    /* Time variable for decode */
    RWTime      TimeNow;

    ULONG       BytesRead;
    INMESS      *InMessage = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " NexusThread    : Started as TID " << rwThreadId() << endl;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        /* Wait for the next result to come back from the RTU */
        while(!PorterNexus.CTINexusValid() && !bServerClosing)
        {
            if(!(++i % 60))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PIL connection to Port Control is inactive" << endl;
                }

                PortPipeInit(NOWAIT); // defibrillate
            }

            CTISleep (500L);

            try
            {
                rwServiceCancellation();
            }
            catch(const RWCancellation& cMsg)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " NexusThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
                bServerClosing = TRUE;
                //throw;
            }
        }

        if(bServerClosing)
        {
            continue;
        }

        InMessage = CTIDBG_new INMESS;
        memset(InMessage, sizeof(*InMessage), 0);

        /* get a result off the port pipe */
        if(PorterNexus.CTINexusRead ( InMessage, sizeof(*InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead < sizeof(*InMessage))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " NexusThread : " << rwThreadId() << " just failed to read a full InMessage." << endl;
            }

            if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
            {
                PortPipeCleanup(0);
            }

            Sleep(500); // No runnaway loops allowed.

            delete InMessage;
            InMessage = 0;
            continue;
        }

        try
        {
            rwServiceCancellation();
        }
        catch(const RWCancellation& cMsg)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " NexusThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
            bServerClosing = TRUE;

            continue;
            // throw;
        }

        // Enqueue the INMESS into the appropriate list

        if(InMessage)
        {
            CtiLockGuard< CtiMutex > inguard( _inMux );
            _inList.append( InMessage );
            InMessage = 0;
        }
    } /* End of for */

    if(InMessage)
    {
        delete InMessage;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " NexusThread : " << rwThreadId() << " terminating " << endl;
    }

    _broken = true;
}

void CtiPILServer::nexusWriteThread()
{
    INT i = 0;
    INT status = NORMAL;
    /* Time variable for decode */
    RWTime      TimeNow;
    ULONG       BytesWritten;

    CtiOutMessage *OutMessage;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " NexusWriteThread    : Started as TID " << rwThreadId() << endl;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        /* Check if we need to reopen the port pipe */
        if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow.now() << " PIL lost connection to Port Control " << endl;
            }

            if(!(PortPipeInit(NOWAIT)))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << TimeNow.now() << " PIL connected to Port Control" << endl;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow.now() << " PIL IS NOT connected to Port Control" << endl;
                dout << TimeNow << " This is mostly bad... " << endl;
            }
        }

        OutMessage = _porterOMQueue.getQueue(1000);

        /* if pipe shut down return the error */
        if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
        {
            if(PortPipeInit(NOWAIT))
            {
                status = PIPEWASBROKEN;
            }
        }

        if(OutMessage)
        {
            if(PorterNexus.NexusState != CTINEXUS_STATE_NULL) /* And send them to porter */
            {
                if(PorterNexus.CTINexusWrite (OutMessage, sizeof (OUTMESS), &BytesWritten, 30L) || BytesWritten == 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    DumpOutMessage(OutMessage);

                    if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
                    {
                        PorterNexus.CTINexusClose();
                    }
                }
            }

            // Message is re-built on the other side, so clean it up!
            delete OutMessage;
        }

        if(bServerClosing)
        {
            continue;
        }

        try
        {
            rwServiceCancellation();
        }
        catch(const RWCancellation& cMsg)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " NexusThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
            bServerClosing = TRUE;

            continue;
            // throw;
        }
    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " NexusWriteThread : " << rwThreadId() << " terminating " << endl;
    }

}


int CtiPILServer::executeRequest(CtiRequestMsg *pReq)
{
    int i = 0;
    int status = NoError;

    RWTPtrSlist< CtiMessage >  vgList;
    RWTPtrSlist< CtiMessage >  retList;
    RWTPtrSlist< OUTMESS >     outList;
    RWTPtrSlist< OUTMESS >     tempOutList;

    RWTPtrSlist< CtiRequestMsg >  execList;

    BASEDLL_IMPORT extern CTINEXUS PorterNexus;

    OUTMESS *OutMessage = NULL;
    CtiReturnMsg   *pcRet = NULL;
    CtiMessage     *pMsg  = NULL;
    CtiMessage     *pVg  = NULL;
    CtiDevice      *Dev;

    CtiCommandParser parse(pReq->CommandString());

    // Note that any and all arguments into this method may be altered on exit!
    analyzeWhiteRabbits(*pReq, parse, execList, retList);

    for(i = 0; i < execList.entries(); i++)
    {
        CtiRequestMsg *&pExecReq = execList[i];
        CtiDeviceSPtr Dev = DeviceManager->getEqual(pExecReq->DeviceId());


        if(Dev)
        {
            if( parse.getCommandStr().compareTo(pExecReq->CommandString(), RWCString::ignoreCase) )
            {
                // They did not match!  We MUST re-parse!
                parse = CtiCommandParser(pExecReq->CommandString());
            }

            pExecReq->setMacroOffset( Dev->selectInitialMacroRouteOffset(pReq->RouteId() != 0 ? pReq->RouteId() : Dev->getRouteID()) );

            /*
             *  We will execute based upon the data in the request....
             */

            if(!pExecReq->getSOE())     // We should attach one if one is not already set...
            {
                pExecReq->setSOE( SystemLogIdGen() );  // Get us a CTIDBG_new number to deal with
            }

            tempOutList.clearAndDestroy();              // Just make sure!

            try
            {
                status = Dev->ExecuteRequest(pExecReq, parse, vgList, retList, tempOutList);    // Defined ONLY in dev_base.cpp
            }
            catch(...)
            {
                {
                    RWTime NowTime;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << NowTime << " ExecuteRequest FAILED for \"" << Dev->getName() << "\"" << endl;
                    dout << NowTime << "   Command: " << pExecReq->CommandString() << endl;
                }
            }

            for(int j = tempOutList.entries(); j > 0; j--)
            {
                // _porterOMQueue.putQueue(tempOutList.get());
                outList.insert( tempOutList.get() );
            }

            if(status != NORMAL)
            {
                RWTime NowTime;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << NowTime << " **** Execute Error **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << NowTime << "   Device:  " << Dev->getName() << endl;
                dout << NowTime << "   Command: " << pExecReq->CommandString() << endl;
                dout << NowTime << "   Status = " << status << ": " << FormatError(status) << endl;
            }

            status = NORMAL;
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Device unknown, unselected, or DB corrupt " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << " Command " << pExecReq->CommandString() << endl;
                dout << RWTime() << " Device: " << pExecReq->DeviceId() << endl;
            }

            CtiPILConnectionManager *CM = (CtiPILConnectionManager *)pExecReq->getConnectionHandle();

            if(CM)
            {
                CtiReturnMsg *pcRet = CTIDBG_new CtiReturnMsg(pExecReq->DeviceId(),
                                                              pExecReq->CommandString(),
                                                              "Device unknown, unselected, or DB corrupt. ID = " + CtiNumStr(pExecReq->DeviceId()),
                                                              IDNF,
                                                              pExecReq->RouteId(),
                                                              pExecReq->MacroOffset(),
                                                              pExecReq->AttemptNum(),
                                                              pExecReq->TransmissionId(),
                                                              pExecReq->UserMessageId(),
                                                              pExecReq->getSOE());

                if(pcRet != NULL)
                {
                    CM->WriteConnQue(pcRet);
                }
            }
        }
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Submitting " << retList.entries() << " CtiReturnMsg objects to client" << endl;
        }
    }

    while( (i = retList.entries()) > 0 )
    {
        pcRet = (CtiReturnMsg*)retList.removeFirst();
        pcRet->setExpectMore(TRUE);    // Let the client know more messages are coming

        CtiPILConnectionManager *CM = (CtiPILConnectionManager *)pReq->getConnectionHandle();

        if(CM)
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                pcRet->dump();
            }

            CM->WriteConnQue(pcRet);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Notice: Request Message did not indicate return path." << endl;
                dout << RWTime() << " Response will be discarded." << endl;
                dout << RWTime() << " Command String: " << pcRet->CommandString() << endl;
            }

            delete pcRet;
        }
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Submitting " << outList.entries() << " CtiOutMessage objects to porter" << endl;
        }
    }

    for( i = outList.entries() ; i > 0; i-- )
    {
        OutMessage = outList.get();
        _porterOMQueue.putQueue(OutMessage);
        OutMessage = 0;
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Submitting " << vgList.entries() << " CtiMessage objects to dispatch" << endl;
        }
    }

    while( (i = vgList.entries()) > 0 )
    {
        pVg = vgList.get();
        VanGoghConnection.WriteConnQue((CtiMessage*)pVg);
    }

    vgList.clearAndDestroy();
    retList.clearAndDestroy();
    execList.clearAndDestroy();

    return status;
}

int CtiPILServer::executeMulti(CtiMultiMsg *pMulti)
{
    int status = NoError;

    CtiMessage *pMyMsg = NULL;

    if(pMulti != NULL)
    {
        RWOrderedIterator itr( pMulti->getData() );

        for(;NULL != (pMyMsg = (CtiMessage*)itr());)
        {
            switch( pMyMsg->isA() )
            {
            case MSG_PCREQUEST:
                {
                    CtiRequestMsg *pReq = (CtiRequestMsg *)pMyMsg;
                    status = executeRequest(pReq);
                    break;
                }
            default:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    break;
                }
            }
        }
    }

    return status;
}

void CtiPILServer::clientShutdown(CtiConnectionManager *&CM)
{
//#ifdef DEBUG_SHUTDOWN
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Now shutting down (ClientAppShutdown) in PIL Server" << endl;
    }
//#endif

    Inherited::clientShutdown(CM);
}

void CtiPILServer::shutdown()
{
    bServerClosing = TRUE;

    MainThread_.requestCancellation(5000);
}


void CtiPILServer::vgConnThread()
{
    CtiMessage *pMsg;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PIL vgConnThrd : Started as TID " << rwThreadId() << endl;
    }

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        pMsg = VanGoghConnection.ReadConnQue( 1500 );

        if(pMsg != NULL)
        {
            switch(pMsg->isA())
            {
            case MSG_COMMAND:
                {
                    CtiCommandMsg* Cmd = (CtiCommandMsg*)pMsg;

                    switch(Cmd->getOperation())
                    {
                    case (CtiCommandMsg::Shutdown):
                        {
                            bServerClosing = TRUE;
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " PIL received a shutdown message from somewhere" << endl;
                            break;
                        }
                    case (CtiCommandMsg::AreYouThere):
                        {
                            VanGoghConnection.WriteConnQue(pMsg->replicateMessage()); // Copy one back
                            break;
                        }
                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "Unhandled command message " << Cmd->getOperation() << " sent to Main.." << endl;
                        }
                    }
                    break;
                }
            case MSG_PCREQUEST:
                {
                    // Let it be handled as if it came from a real connection?
                    MainQueue_.putQueue( pMsg );

                    pMsg = NULL;   // Save the message from the Lions!
                    break;
                }
            }


            if(pMsg != NULL)
            {
                delete pMsg;
            }
        }


    } /* End of for */

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
    VanGoghConnection.ShutdownConnection();

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PIL vgConnThrd : " << rwThreadId() << " terminating " << endl;
    }

}

INT CtiPILServer::analyzeWhiteRabbits(CtiRequestMsg& Req, CtiCommandParser &parse, RWTPtrSlist< CtiRequestMsg > & execList, RWTPtrSlist< CtiMessage > & retList)
{
    INT status = NORMAL;
    INT i;

    CtiRequestMsg *pReq = (CtiRequestMsg*)Req.replicateMessage();
    pReq->setConnectionHandle( Req.getConnectionHandle() );

    CtiDeviceSPtr Dev = DeviceManager->getEqual(pReq->DeviceId());

    if(parse.isKeyValid("serial"))
    {
        pReq->setDeviceId( SYS_DID_SYSTEM );    // Make sure we are targeting the serial/system device;
    }

    // Can you say WHITE RABBIT?  This could override the above!
    // This code will not execute in most cases
    if(parse.isKeyValid("device"))
    {
        if( -1 != (i = parse.getiValue("device")) )
        {
            // OK, someone tried to send us an override on the device ID
            pReq->setDeviceId( i ) ;
            Dev = DeviceManager->getEqual(pReq->DeviceId());
        }
        else
        {
            RWCString dname = parse.getsValue("device");
            Dev = DeviceManager->RemoteGetEqualbyName( dname );
            if(Dev)
            {
                pReq->setDeviceId( Dev->getID() ) ;
            }
        }
    }
    if(parse.isKeyValid("route"))
    {
        if( (i = parse.getiValue("route")) != -1 )
        {
            // OK, someone tried to send us an override on the route ID
            pReq->setRouteId( i );
        }
        else
        {
            //  apparently we've been sent a route name
            RWCString routeName = parse.getsValue("route");
            CtiRouteSPtr tmpRoute;

            tmpRoute = RouteManager->getEqualByName( routeName );

            if(tmpRoute)
            {
                pReq->setRouteId( tmpRoute->getRouteID() );
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if( Dev && parse.isKeyValid("install") && (Dev->getType() == TYPE_REPEATER800 || Dev->getType() == TYPE_REPEATER900) )
    {
        analyzeAutoRole(*pReq,parse,execList,retList);
    }

    if(!pReq->DeviceId() && parse.isKeyValid("group"))
    {
        // We are to do some magiks here.  Looking for names in groups
        RWCString gname = parse.getsValue("group");

        {
            CtiDeviceManager::LockGuard dev_guard(DeviceManager->getMux());
            CtiDeviceManager::spiterator itr_dev;

            int groupsubmitcnt = 0;

            for(itr_dev = DeviceManager->begin(); itr_dev != DeviceManager->end(); itr_dev++)
            {
                CtiDeviceBase &device = *(itr_dev->second.get());

                if(device.isMeter() || isION(device.getType()))
                {
                    RWCString mgname = device.getMeterGroupName();

                    mgname.toLower();

                    if( !mgname.isNull() && !gname.compareTo(mgname, RWCString::ignoreCase) )
                    {
                        groupsubmitcnt++;

                        // We have a name match
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Adding device " << device.getID() << " / " << device.getName() << " for group execution" << endl;
                        }

                        // Create a message for this one!
                        pReq->setDeviceId(device.getID());

                        CtiRequestMsg *pNew = (CtiRequestMsg*)pReq->replicateMessage();
                        pNew->setConnectionHandle( pReq->getConnectionHandle() );

                        execList.insert( pNew );
                    }
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Collection Group " << gname << " found " << groupsubmitcnt << " target devices." << endl;
            }
        }
    }
    else if(!pReq->DeviceId() && parse.isKeyValid("altgroup"))
    {
        // We are to do some magiks here.  Looking for names in groups
        RWCString gname = parse.getsValue("altgroup");

        {
            CtiDeviceManager::LockGuard dev_guard(DeviceManager->getMux());
            CtiDeviceManager::spiterator itr_dev;

            int groupsubmitcnt = 0;

            for(itr_dev = DeviceManager->begin(); itr_dev != DeviceManager->end(); itr_dev++)
            {
                CtiDeviceBase &device = *(itr_dev->second.get());

                if(device.isMeter() || isION(device.getType()))
                {
                    RWCString mgname = device.getAlternateMeterGroupName();

                    mgname.toLower();

                    if( !mgname.isNull() && !gname.compareTo(mgname, RWCString::ignoreCase) )
                    {
                        groupsubmitcnt++;

                        // We have a name match
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Adding device " << device.getName() << " for ALT group execution" << endl;
                        }

                        // Create a message for this one!
                        pReq->setDeviceId(device.getID());

                        CtiRequestMsg *pNew = (CtiRequestMsg*)pReq->replicateMessage();
                        pNew->setConnectionHandle( pReq->getConnectionHandle() );

                        execList.insert( pNew );
                    }
                }
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Alternate Collection Group " << gname << " found " << groupsubmitcnt << " target devices." << endl;
            }
        }
    }

    if(parse.getCommand() == PutConfigRequest)
    {
        if(parse.isKeyValid("template") && INT_MIN != parse.getiValue("serial"))
        {
            /* OK, a serial number was specified in a putconfg, with a template......
             *  We will take that to mean that the desired outcome is to assign this group's
             *  addressing to the serial number specified...
             */

            RWCString lmgroup = parse.getsValue("template");
            RWCString service = parse.getsValue("templateinservice");
            char newparse[256];

            Dev = DeviceManager->getEqual(SYS_DID_SYSTEM);     // This is the guy who does configs.
            CtiDeviceSPtr GrpDev = DeviceManager->RemoteGetEqualbyName( lmgroup );
            if(GrpDev)
            {
                _snprintf(newparse, 255, "putconfig serial %d %s %s", parse.getiValue("serial"), GrpDev->getPutConfigAssignment(), service.data());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Template putconfig **** " << endl << "   " << newparse << endl;
                }

                pReq->setCommandString(newparse);      // Make the request match our CTIDBG_new choices
                parse = CtiCommandParser(newparse);    // Should create a CTIDBG_new actionItem list
            }
        }
        else if(INT_MIN != parse.getiValue("fromutility"))
        {
            /*
             *  This indicates the user wants to put the devices defined by group addressing defined in the "fromxxx"
             *  keys into the selected versacom group.
             */
            char newparse[256];

            CtiDeviceGroupVersacom *GrpDev = (CtiDeviceGroupVersacom *)Dev.get();
            // Dev = DeviceManager->getEqual(SYS_DID_SYSTEM);     // This is the guy who does ALL configs.
            if(GrpDev != NULL)
            {
                _snprintf(newparse, 255, "%s %s", pReq->CommandString(), GrpDev->getPutConfigAssignment());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Group reassign to group **** " << GrpDev->getName() << endl << "   " << newparse << endl;
                }

                pReq->setCommandString(newparse);       // Make the request match our CTIDBG_new choices
                pReq->setRouteId(GrpDev->getRouteID()); // Just on this route.
                parse = CtiCommandParser(newparse);     // Should create a CTIDBG_new actionItem list
            }
        }
    }

    if(execList.entries() == 0)
    {
        execList.insert( pReq );
        pReq = NULL;
    }

    if(pReq != NULL)
    {
        delete pReq;
    }

    return status;
}

void ReportMessagePriority( CtiMessage *MsgPtr, CtiDeviceManager *&DeviceManager )
{
    if(MsgPtr->isA() == MSG_PCREQUEST)
    {
        CtiDeviceManager::LockGuard dev_guard(DeviceManager->getMux());
        CtiDeviceSPtr DeviceRecord = DeviceManager->getEqual(((CtiRequestMsg*)MsgPtr)->DeviceId());
        if(DeviceRecord)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Pilserver mainThread received a CtiRequestMsg for " << DeviceRecord->getName();
            dout << " at priority " << MsgPtr->getMessagePriority() << endl;

            CtiRequestMsg *pCmd = (CtiRequestMsg*)MsgPtr;

            if(!pCmd->CommandString().isNull())
            {
                dout << RWTime() << "   Command string: \"" << pCmd->CommandString() << "\"" << endl;
            }
        }
    }
    else if(MsgPtr->isA() == MSG_MULTI)
    {
        RWOrderedIterator itr( ((CtiMultiMsg*)MsgPtr)->getData() );
        CtiMessage *pMyMsg;

        while(NULL != (pMyMsg = (CtiMessage*)itr()))
        {
            ReportMessagePriority(pMyMsg, DeviceManager);                  // And recurse.
        }
    }

    return;
}

INT CtiPILServer::analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, RWTPtrSlist< CtiRequestMsg > & execList, RWTPtrSlist< CtiMessage > & retList)
{
    INT status = NORMAL;
    int i;
    CtiDeviceManager::LockGuard dev_guard(DeviceManager->getMux());
    // CtiRouteManager::LockGuard rte_guard(RouteManager->getMux());

    CtiDeviceSPtr pRepeaterToRole = DeviceManager->getEqual(Req.DeviceId());    // This is our repeater we are curious about!

    if(pRepeaterToRole)
    {
        if(pRepeaterToRole->getType() == TYPE_REPEATER800 || pRepeaterToRole->getType() == TYPE_REPEATER900)
        {
            //CtiRequestMsg *pReq = (CtiRequestMsg*)Req.replicateMessage();
            //pReq->setConnectionHandle( Req.getConnectionHandle() );

            // Alright.. An appropriate device has been selected for this command.
            vector< CtiDeviceRepeaterRole > roleVector;

            try
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Looking for " << pRepeaterToRole->getName() << " in all routes" << endl;
                }

                RouteManager->buildRoleVector( pRepeaterToRole->getID(), Req, retList, roleVector );
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            if(roleVector.size() > 0)
            {
                RWCString newReqString = RWCString("putconfig emetcon mrole 1");       // We always write 1 through whatever.
                RWCString roleStr;

                for(i = 0; i < roleVector.size(); i++)
                {
#if 0
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << " RouteId " << roleVector[i].getRouteID() << endl;
                        dout << " OutBits " << (int)roleVector[i].getOutBits() << endl;
                        dout << " FixBits " << (int)roleVector[i].getFixBits() << endl;
                        dout << " InBits  " << (int)roleVector[i].getInBits() << endl;
                        dout << " Stages  " << (int)roleVector[i].getStages() << endl;
                        dout << endl;
                    }
#endif
                    roleStr += " " + CtiNumStr((int)roleVector[i].getFixBits());
                    roleStr += " " + CtiNumStr((int)roleVector[i].getOutBits());
                    roleStr += " " + CtiNumStr((int)roleVector[i].getInBits());
                    roleStr += " " + CtiNumStr((int)roleVector[i].getStages());
                }

                newReqString += roleStr;

                if( parse.isKeyValid("noqueue") && newReqString.subString("noqueue").isNull() )
                {
                    newReqString += " noqueue";
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "  " << newReqString << endl;
                }

                Req.setCommandString( newReqString );
            }
        }
    }

    return status;
}


void CtiPILServer::putQueue(CtiMessage *Msg)
{
    MainQueue_.putQueue( Msg );
}


