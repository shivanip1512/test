#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   pilserver
*
* Date:   10/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PIL/pilserver.cpp-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:43 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iomanip>
#include <iostream>
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
#include "logger.h"
#include "executor.h"
#include "dlldefs.h"
#include "connection.h"

#include "pilglob.h"
#include "ctibase.h"
#include "dllbase.h"
#include "dll_msg.h"
#include "utility.h"
#include "logger.h"


void DumpOutMessage(void *Mess)
{
    OUTMESS  *Om = (OUTMESS*)(Mess);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "  Device ID:          " << Om->DeviceID << endl;
        dout << "  Target ID:          " << Om->TargetID << endl;
        dout << "  Route  ID:          " << Om->RouteID << endl;
        dout << "  Port:               " << Om->Port << endl;
        dout << "  Remote:             " << Om->Remote << endl;
        dout << "  Sequence:           " << Om->Sequence << endl;
        dout << "  Priority:           " << Om->Priority << endl;
        dout << "  TimeOut:            " << Om->TimeOut << endl;
        dout << "  Retry:              " << Om->Retry << endl;
        dout << "  OutLength:          " << Om->OutLength << endl;
        dout << "  InLength:           " << Om->InLength << endl;
        dout << "  Source:             " << Om->Source << endl;
        dout << "  Destination:        " << Om->Destination << endl;
        dout << "  Command:            " << Om->Command << endl;
        dout << "  Function:           " << Om->Function << endl;
        dout << "  EventCode:          " << Om->EventCode << endl;
    }
}


/* global variable for scanner function */
HEV PilSem = (HEV) NULL;


CtiConnection           VanGoghConnection;
CtiPILExecutorFactory   ExecFactory;

/* Define the return nexus handle */
/* external dll global */
DLLIMPORT extern CTINEXUS PorterNexus;
DLLIMPORT extern VOID PortPipeCleanup (ULONG Reason);


int CtiPILServer::execute()
{
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

            // rwSleep(500);

            ConnThread_ = rwMakeThreadFunction(*this, &CtiPILServer::connectionThread);
            ConnThread_.start();

            // rwSleep(500);

            ResultThread_ = rwMakeThreadFunction(*this, &CtiPILServer::resultThread);
            ResultThread_.start();

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

    CtiConnection::InQ_t          *APQueue;
    CtiExecutor                   *pExec;
    CtiMessage                    *MsgPtr;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PILMainThread  : Started as TID " << rwThreadId() << endl;
    }

    VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
    VanGoghConnection.WriteConnQue(new CtiRegistrationMsg(PIL_REGISTRATION_NAME, rwThreadId(), TRUE));

    try
    {
        CtiLockGuard<CtiMutex> guard(server_mux);

        NetPort  = RWInetPort(PORTERINTERFACENEXUS);
        NetAddr  = RWInetAddr(NetPort);           // This one for this server!

        Listener = new RWSocketListener(NetAddr);

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
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 24, 0);

    /* Create the event semaphore */
    if(CTICreateEventSem (PILSEM, &PilSem, 0, 0))
    {
        if(CTIOpenEventSem (PILSEM, &PilSem, MUTEX_ALL_ACCESS))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Error Creating Scan Semaphore" << endl;
            exit(-1);
        }
    }

    /* create the lock semaphore and own it for now */
    CTICreateMutexSem (NULL, &LockSem, 0, TRUE);

    /*
     *  MAIN: The main PIL loop lives here for all time!
     */

    for( ; !bQuit ; )
    {
        /* Release the Lock Semaphore */
        CTIReleaseMutexSem (LockSem);

        // Blocks for 1000 ms or until a queue entry exists
        MsgPtr = MainQueue_.getQueue(500);

        if(MsgPtr != NULL)
        {
            /* Wait/Block on the return thread if neccessary */
            CTIRequestMutexSem (LockSem, SEM_INDEFINITE_WAIT);

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

            /* Use the same time base for the full scan check */
            TimeNow = TimeNow.now();   // update the time...

            if((pExec = ExecFactory.getExecutor(MsgPtr)) != NULL)
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

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " PIL Server shut down complete " << endl;
            }
        }
    }

    VanGoghConnection.WriteConnQue(new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15));
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
                XChg = new CtiExchange(sock);

                if(XChg != NULL)
                {
                    CtiPILConnectionManager *ConMan = new CtiPILConnectionManager(XChg, &MainQueue_);

                    if(ConMan != NULL)
                    {
                        ConMan->setClientName("DEFAULT");
                        ConMan->setBlockingWrites(TRUE);    // Writes must be blocking into the main queue

                        /*
                         *  Need to inform MainThread of the "New Guy" so that he may control its destiny from
                         *  now on.
                         */

                        CmdMsg = new CtiCommandMsg(CtiCommandMsg::NewClient, 15);


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
                            dout << RWTime() << " ERROR Starting new connection! " << rwThreadId() << endl;
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

    return;
}

void CtiPILServer::resultThread()
{
    INT i;
    INT status = NORMAL;
    ULONG BytesWritten;
    INT x = 0;

    PBYTE    pMsg = NULL;

    CtiConnection  *Conn = NULL;
    CtiMessage     *pVg  = NULL;
    OUTMESS        *OutMessage;


    /* Define the various records */
    CtiDeviceBase   *DeviceRecord;
    CtiDeviceBase   *RemoteRecord;
    // CtiPoint        *PointRecord;

    /* Define the various time variable */
    RWTime      TimeNow;

    ULONG       BytesRead;
    INMESS      InMessage;

    RWTPtrSlist< OUTMESS    > outList;
    RWTPtrSlist< CtiMessage > retList;
    RWTPtrSlist< CtiMessage > vgList;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << TimeNow.now() << " ResThread      : Started as TID " << rwThreadId() << endl;
        // dout << TimeNow.now() << " ResThread : " << rwThreadId() << " Dispatch to : " << VanGoghMachine << ":" << VANGOGHNEXUS << endl;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 25, 0);

    /* Block here until main program is in loop */
    CTIRequestMutexSem (LockSem, SEM_INDEFINITE_WAIT);

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        /* Release the Lock Semaphore */
        CTIReleaseMutexSem (LockSem);

        /* Wait for the next result to come back from the RTU */

        while(!PorterNexus.CTINexusValid() && !bServerClosing)
        {
            if(!(++x % 60))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PIL connection to Port Control is inactive" << endl;
                }

                PortPipeInit(NOWAIT);       // This is a last ditch attempt.
            }

            CTISleep (500L);

            try
            {
                rwServiceCancellation();
            }
            catch(const RWCancellation& cMsg)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " ResThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
                bServerClosing = TRUE;
                //throw;
            }
        }

        if(bServerClosing)
        {
            continue;
        }

        /* get a result off the port pipe */
        if(PorterNexus.CTINexusRead ( &InMessage, sizeof(InMessage), &BytesRead, CTINEXUS_INFINITE_TIMEOUT) || BytesRead < sizeof(InMessage))
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow << " ResThread : " << rwThreadId() << " just got KICKED!" << endl;
            }

            if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
            {
                PortPipeCleanup(0);
            }

            Sleep(500); // No runnaway loops allowed.
            continue;
        }

        try
        {
            rwServiceCancellation();
        }
        catch(const RWCancellation& cMsg)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << TimeNow << " ResThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
            bServerClosing = TRUE;

            continue;
            // throw;
        }

        /* Wait on the request thread if neccessary */
        CTIRequestMutexSem (LockSem, SEM_INDEFINITE_WAIT);

        LONG id = InMessage.TargetID;

        if(id == 0)
        {
            id = InMessage.DeviceID;
        }

        // Find the device..
        DeviceRecord = DeviceManager->RemoteGetEqual(id);

        if(DeviceRecord != NULL)
        {
            /* get the time for use in the decodes */
            TimeNow = RWTime();

            // Do some device dependant work on this Inbound message!
            DeviceRecord->ProcessResult( &InMessage, TimeNow, vgList, retList, outList);

            if(outList.entries())
            {
                for( i = outList.entries() ; i > 0; i-- )
                {
                    OutMessage = outList.get();

                    /* if pipe shut down return the error */
                    if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
                    {
                        if(PortPipeInit(NOWAIT))
                        {
                            status = PIPEWASBROKEN;
                        }
                    }

                    if(PorterNexus.NexusState != CTINEXUS_STATE_NULL) /* And send them to porter */
                    {
                        if(OutMessage->TargetID != 0 && OutMessage->DeviceID != 0 && OutMessage->Port > 0)
                        {
                            if(PorterNexus.CTINexusWrite (OutMessage, sizeof (OUTMESS), &BytesWritten, 0L) || BytesWritten == 0)
                            {
                                if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
                                {
                                    PorterNexus.CTINexusClose();
                                }
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** Error **** Improperly formed OUTMESS discarded " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            DumpOutMessage(OutMessage);
                        }
                    }

                    // Message is re-built on the other side, so clean it up!
                    delete OutMessage;
                }
            }

            while( (i = retList.entries()) > 0 )
            {
                CtiMessage *pRet = retList.get();

                if((Conn = ((CtiConnection*)InMessage.Return.Connection)) != NULL)
                {
                    Conn->WriteConnQue(pRet);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "InMessage received from unknown device.  Device ID: " << InMessage.DeviceID << endl;
            dout << " Port listed as                                   : " << InMessage.Port     << endl;
            dout << " Remote listed as                                 : " << InMessage.Remote   << endl;
        }

    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << TimeNow << " ResThread : " << rwThreadId() <<
        " terminating " << endl;
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
    ULONG BytesWritten;

    OUTMESS *OutMessage = NULL;
    CtiReturnMsg   *pcRet = NULL;
    CtiMessage     *pMsg  = NULL;
    CtiMessage     *pVg  = NULL;
    CtiDevice      *Dev;


    // Note that any and all arguments into this method may be altered on exit!
    analyzeWhiteRabbits(*pReq, execList, retList);

    for(i = 0; i < execList.entries(); i++)
    {
        CtiRequestMsg *&pExecReq = execList[i];
        CtiDevice *Dev = DeviceManager->RemoteGetEqual(pExecReq->DeviceId());


        if(Dev != NULL)
        {
            pExecReq->setMacroOffset( Dev->selectInitialMacroRouteOffset() );

            /*
             *  We will execute based upon the data in the request....
             */

            if(!pExecReq->getSOE())     // We should attach one if one is not already set...
            {
                pExecReq->setSOE( SystemLogIdGen() );  // Get us a new number to deal with
            }

            tempOutList.clear();
            status = Dev->ExecuteRequest(pExecReq, vgList, retList, tempOutList);    // Defined ONLY in dev_base.cpp

            for(int j = tempOutList.entries(); j > 0; j--)
            {
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

                if(outList.entries() > 0)
                {
                    dout << NowTime << "   Sending " << outList.entries() << " requests to porter on error condition" << endl;
                }
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
                CtiReturnMsg *pcRet = new CtiReturnMsg(pExecReq->DeviceId(),
                                                       pExecReq->CommandString(),
                                                       "Device unknown, unselected, or DB corrupt",
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

    for( i = outList.entries() ; i > 0; i-- )
    {
        OutMessage = outList.get();

        /* if pipe shut down return the error */
        if(PorterNexus.NexusState == CTINEXUS_STATE_NULL)
        {
            if(PortPipeInit(NOWAIT))
            {
                status = PIPEWASBROKEN;
            }
        }

        if( DebugLevel & 0x00200000 )
        {
            Dev = DeviceManager->RemoteGetEqual(OutMessage->DeviceID);

            {
                RWTime NowTime;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << NowTime << " **** Executing **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                if(Dev != NULL)
                    dout << NowTime << "   Device:  " << Dev->getName() << endl;
                dout << NowTime << "   Command: " << pReq->CommandString() << endl;
            }
            DumpOutMessage(OutMessage);
        }

        /* And send them to porter */
        if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
        {
            if(PorterNexus.CTINexusWrite(OutMessage, sizeof (OUTMESS), &BytesWritten, 0L) || BytesWritten == 0)
            {
                if(PorterNexus.NexusState != CTINEXUS_STATE_NULL)
                {
                    PorterNexus.CTINexusClose();
                }
            }
        }

        // Message is re-built on the other side, so clean it up!
        delete OutMessage;
    }

    while( (i = retList.entries()) > 0 )
    {
        pcRet = (CtiReturnMsg*)retList.removeFirst();

        if(i > 1)
        {
            pcRet->setExpectMore(TRUE);    // Let the client know more messages are coming
        }

        CtiPILConnectionManager *CM = (CtiPILConnectionManager *)pReq->getConnectionHandle();

        if(CM)
        {
            if( DebugLevel & 0x00200000 )
            {
                pcRet->dump();
            }
            CM->WriteConnQue(pcRet);
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ERROR : Request Message did not indicate return path." << endl;
            }

            delete pcRet;
        }
    }

    while( (i = vgList.entries()) > 0 )
    {
        pVg = vgList.get();
        VanGoghConnection.WriteConnQue((CtiMessage*)pVg);
    }

    execList.clearAndDestroy();   // Get rid of the new messages.

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

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PIL vgConnThrd : " << rwThreadId() << " terminating " << endl;
    }

}

INT CtiPILServer::analyzeWhiteRabbits(CtiRequestMsg& Req, RWTPtrSlist< CtiRequestMsg > & execList, RWTPtrSlist< CtiMessage > retList)
{
    INT status = NORMAL;
    INT i;

    CtiCommandParser parse(Req.CommandString());

    CtiRequestMsg *pReq = (CtiRequestMsg*)Req.replicateMessage();
    pReq->setConnectionHandle( Req.getConnectionHandle() );


    CtiDevice *Dev = DeviceManager->RemoteGetEqual(pReq->DeviceId());

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
            Dev = DeviceManager->RemoteGetEqual(pReq->DeviceId());
        }
        else
        {
            RWCString dname = parse.getsValue("device");
            Dev = DeviceManager->RemoteGetEqualbyName( dname );
            if(Dev != NULL)
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
            CtiRouteBase *tmpRoute;

            tmpRoute = RouteManager->RouteGetEqualByName( routeName );

            if(tmpRoute != NULL)
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

    if(!pReq->DeviceId() && parse.isKeyValid("group"))
    {
        // We are to do some magiks here.  Looking for names in groups
        RWCString gname = parse.getsValue("group");

        {
            RWRecursiveLock<RWMutexLock>::LockGuard dev_guard(DeviceManager->getMux());
            CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr_dev(DeviceManager->getMap());

            for(; ++itr_dev ;)
            {
                CtiDeviceBase &device = *(itr_dev.value());

                if(device.isMeter())
                {
                    RWCString mgname = device.getMeterGroupName();

                    mgname.toLower();

                    if( !mgname.isNull() && !gname.compareTo(mgname, RWCString::ignoreCase) )
                    {
                        // We have a name match
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Adding device " << device.getName() << " for group execution" << endl;
                        }

                        // Create a message for this one!
                        pReq->setDeviceId(device.getID());

                        CtiRequestMsg *pNew = (CtiRequestMsg*)pReq->replicateMessage();
                        pNew->setConnectionHandle( pReq->getConnectionHandle() );

                        execList.insert( pNew );
                    }
                }
            }
        }
    }
    else if(!pReq->DeviceId() && parse.isKeyValid("altgroup"))
    {
        // We are to do some magiks here.  Looking for names in groups
        RWCString gname = parse.getsValue("altgroup");

        {
            RWRecursiveLock<RWMutexLock>::LockGuard dev_guard(DeviceManager->getMux());
            CtiRTDB<CtiDeviceBase>::CtiRTDBIterator itr_dev(DeviceManager->getMap());

            for(; ++itr_dev ;)
            {
                CtiDeviceBase &device = *(itr_dev.value());

                if(device.isMeter())
                {
                    RWCString mgname = device.getAlternateMeterGroupName();

                    mgname.toLower();

                    if( !mgname.isNull() && !gname.compareTo(mgname, RWCString::ignoreCase) )
                    {
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
            char newparse[128];

            Dev = DeviceManager->RemoteGetEqual(SYS_DID_SYSTEM);     // This is the guy who does configs.
            CtiDevice *GrpDev = DeviceManager->RemoteGetEqualbyName( lmgroup );
            if(GrpDev != NULL)
            {
                sprintf(newparse, "PutConfig serial %d %s", parse.getiValue("serial"), GrpDev->getPutConfigAssignment());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Template putconfig **** " << endl << "   " << newparse << endl;
                }

                pReq->setCommandString(newparse);      // Make the request match our new choices
                parse = CtiCommandParser(newparse);    // Should create a new actionItem list
            }
        }
        else if(INT_MIN != parse.getiValue("fromutility"))
        {
            /*
             *  This indicates the user wants to put the devices defined by group addressing defined in the "fromxxx"
             *  keys into the selected versacom group.
             */
            char newparse[128];

            CtiDeviceGroupVersacom *GrpDev = (CtiDeviceGroupVersacom *)Dev;
            Dev = DeviceManager->RemoteGetEqual(SYS_DID_SYSTEM);     // This is the guy who does ALL configs.
            if(GrpDev != NULL)
            {
                sprintf(newparse, "%s %s", pReq->CommandString(), GrpDev->getPutConfigAssignment());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Group reassign to group **** " << GrpDev->getName() << endl << "   " << newparse << endl;
                }

                pReq->setCommandString(newparse);       // Make the request match our new choices
                pReq->setRouteId(GrpDev->getRouteID()); // Just on this route.
                parse = CtiCommandParser(newparse);     // Should create a new actionItem list
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
