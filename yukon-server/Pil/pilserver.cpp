/*-----------------------------------------------------------------------------*
*
* File:   pilserver
*
* Date:   10/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PIL/pilserver.cpp-arc  $
* REVISION     :  $Revision: 1.122.2.2 $
* DATE         :  $Date: 2008/11/21 16:14:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#pragma warning( disable : 4786 )


#include <iomanip>
#include <iostream>
#include <vector>
#include <boost/regex.hpp>
#include <boost/bind.hpp>

#include <rw/toolpro/winsock.h>
#include <rw/thr/thrfunc.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw/rwerr.h>
#include <rw/thr/mutex.h>
#include <rw/re.h>
#undef mask_                // Stupid RogueWave re.h

#include "os2_2w32.h"
#include "cticalls.h"

#include "dev_grp_versacom.h"
#include "dev_grp_point.h"
#include "dev_mct.h"
#include "dsm2.h"
#include "ctinexus.h"
#include "CtiLocalConnect.h"
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

#include "ctibase.h"
#include "dllbase.h"
#include "logger.h"
#include "repeaterrole.h"
#include "rte_ccu.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"

#include "ctistring.h"

using namespace std;

void ReportMessagePriority( CtiMessage *MsgPtr, CtiDeviceManager *&DeviceManager );
extern IM_EX_CTIBASE void DumpOutMessage(void *Mess);
CtiConnection           VanGoghConnection;
CtiPILExecutorFactory   ExecFactory;

/* Define the return nexus handle */
DLLEXPORT CtiLocalConnect<OUTMESS, INMESS> PilToPorter; //Pil handles this one
DLLEXPORT CtiFIFOQueue< CtiMessage > PorterSystemMessageQueue;

bool inmess_user_message_id_equal(const INMESS &in, int user_message_id);

static vector< CtiPointDataMsg > pdMsgCol;
static bool findShedDeviceGroupControl(const long key, CtiDeviceSPtr otherdevice, void *vptrControlParent);
static bool findRestoreDeviceGroupControl(const long key, CtiDeviceSPtr otherdevice, void *vptrControlParent);

int CtiPILServer::execute()
{
    _broken = false;

    try
    {
        //  all references to this need to be moved to Scanner - we now use PilToPorter and PorterToPil
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

            _schedulerThread = rwMakeThreadFunction(*this, &CtiPILServer::schedulerThread);
            _schedulerThread.start();
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
    BOOL          bQuit = FALSE;
    int           status;

    CtiTime       TimeNow;

    CtiExecutor  *pExec;
    CtiMessage   *MsgPtr;
    int groupBypass = 0;

    CtiTime  starttime, finishtime;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PILMainThread  : Started as TID " << rwThreadId() << endl;
    }

    VanGoghConnection.doConnect(VANGOGHNEXUS, VanGoghMachine);
    VanGoghConnection.setName("Dispatch");
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(PIL_REGISTRATION_NAME, rwThreadId(), TRUE));

    /* Give us a tiny attitude */
    CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

    /*
     *  MAIN: The main PIL loop lives here for all time!
     */

    for( ; !bQuit ; )
    {
        try
        {
            if( (MainQueue_.isEmpty() || (++groupBypass > 1))
                && !_groupQueue.empty() )
            {
                groupBypass = 0;

                MsgPtr = *(_groupQueue.begin());
                _groupQueue.erase(_groupQueue.begin());

                MsgPtr->setMessageTime(CtiTime::now());
            }
            else
            {
                // Blocks for 500 ms or until a queue entry exists
                MsgPtr = MainQueue_.getQueue(500);
            }

            try
            {
                if(MsgPtr != NULL)
                {
                    starttime = starttime.now();

                    if(DebugLevel & DEBUGLEVEL_PIL_MAINTHREAD)
                    {
                        ReportMessagePriority(MsgPtr, DeviceManager);
                    }

                    /* Use the same time base for the full scan check */
                    TimeNow = TimeNow.now();   // update the time...

                    if(MsgPtr->isA() == MSG_PCREQUEST && MsgPtr->getMessageTime().seconds() < (TimeNow.seconds() - 900))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " PIL processing an inbound request/command message which is over 15 minutes old.  Message will be discarded." << endl;
                            dout << " >>---------- Message Content ----------<< " << endl;
                            MsgPtr->dump();
                            dout << " <<---------- Message Content ---------->> " << endl;
                        }

                        delete MsgPtr;    // No one attached it to them, so we need to kill it!
                        MsgPtr = 0;
                    }
                    else if(MsgPtr->isA() == MSG_MULTI)
                    {
                        //  split it out so we don't block processing all of the submessages
                        const CtiMultiMsg_vec &subMessages = (static_cast<CtiMultiMsg *>(MsgPtr))->getData();

                        CtiMultiMsg_vec::const_iterator msg_itr = subMessages.begin(),
                                                        msg_end = subMessages.end();

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " PIL breaking out a CtiMultiMsg with " << subMessages.size() << " submessages" << endl;
                        }

                        for( ; msg_itr != msg_end; ++msg_itr )
                        {
                            if( *msg_itr )
                            {
                                CtiMessage *subMessage = static_cast<CtiMessage *>(*msg_itr)->replicateMessage();
                                subMessage->setConnectionHandle(MsgPtr->getConnectionHandle());

                                MainQueue_.putQueue(subMessage);
                            }
                        }

                        delete MsgPtr;
                    }
                    else if((pExec = ExecFactory.getExecutor(MsgPtr)) != NULL)
                    {
                        try
                        {
                            status = pExec->ServerExecute(this);
                        }
                        catch(...)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        delete pExec;  //  NOTE - this deletes the MsgPtr!
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
                            dout << CtiTime() << " PIL mainthread took " << (finishtime.seconds() - starttime.seconds()) << " seconds to process the last message." << endl;
                        }
                    }
                }
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                                         //
                try
                {
                    // This forces the listener thread to exit on shutdown.
                    _listenerSocket.close();
                }
                catch(...)
                {
                    // Dont really care, we are shutting down.
                }
                if( ConnThread_.join(10000) == RW_THR_TIMEOUT) // Wait for the Conn thread to die.
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " PIL Server shutting down the ConnThread_: FAILED " << endl;
                    }
                    ConnThread_.terminate();
                }

                ResultThread_.requestCancellation(750);

                if(ResultThread_.join(10000) == RW_THR_TIMEOUT)                     // Wait for the closure
                {
                    if(ResultThread_.requestCancellation(150) == RW_THR_TIMEOUT)   // Mark it for destruction...
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " PIL Server shutting down the ResultThread_: TIMEOUT " << endl;
                        }
                        if(ResultThread_.join(500) == RW_THR_TIMEOUT)                     // Wait for the closure
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " PIL Server shutting down the ResultThread_: FAILED " << endl;

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
                            dout << CtiTime() << " PIL Server shutting down the _nexusWriteThread: TIMEOUT " << endl;
                        }
                        if(_nexusWriteThread.join(500) == RW_THR_TIMEOUT)                     // Wait for the closure
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " PIL Server shutting down the _nexusWriteThread: FAILED " << endl;

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
                            dout << CtiTime() << " PIL Server shutting down the _nexusThread: TIMEOUT " << endl;
                        }
                        if(_nexusThread.join(500) == RW_THR_TIMEOUT)                     // Wait for the closure
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " PIL Server shutting down the _nexusThread: FAILED " << endl;

                            _nexusThread.terminate();
                        }
                    }
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " PIL Server shut down complete " << endl;
                }
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ****  EXCEPTION: PIL mainThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  - Will attmept to recover" << endl;
            }

            Sleep(5000);
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
        dout << CtiTime() << " ConnThread     : Started as TID " << rwThreadId() << endl;
    }

    try
    {
        NetPort  = RWInetPort(PORTERINTERFACENEXUS);
        NetAddr  = RWInetAddr(NetPort);           // This one for this server!

        _listenerSocket.listen(NetAddr);

        if(!_listenerSocket.valid())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Could not open socket " << NetAddr << " for listning" << endl;
            }

            exit(-1);
        }
    }
    catch(RWxmsg &msg)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception " << __FILE__ << " (" << __LINE__ << ") " << msg.why() << endl;
        throw;
    }

    for(;!bQuit && !bServerClosing;)
    {
        try
        { // It seems necessary to make this copy. RW does this and now so do we.
            RWSocket tempSocket = _listenerSocket;
            RWSocket newSocket = tempSocket.accept();
            RWSocketPortal sock;

            // This is very important. We tell the socket portal that we own the socket!
            sock = RWSocketPortal(newSocket, RWSocketPortalBase::Application);

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

                        CtiServer::ptr_type sptrConMan(ConMan);
                        clientConnect( sptrConMan );             // Put it in the list...

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
                            dout << CtiTime() << " ERROR Starting CTIDBG_new connection! " << rwThreadId() << endl;
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
                dout << CtiTime() << " Socket error RWNETENOTSOCK" << endl;
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
                dout << endl << CtiTime() << " ConnThread: Failed... " ;
                dout << msg.why() << endl;

                bQuit = TRUE;
            }
            throw;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ConnThread: " << rwThreadId() << " is properly shutdown... " << endl;
    }

    _broken = true;
    return;
}


void CtiPILServer::resultThread()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ResThread      : Started as TID " << rwThreadId() << endl;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYC_NOCHANGE, THREAD_PRIORITY_HIGHEST);

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        try
        {
            const unsigned int inQueueBlockSize = 255;
            const unsigned int inQueueMaxWait   = 500;  //  500 ms

            unsigned long start = GetTickCount(), elapsed;

            deque<INMESS*> pendingInQueue;

            while( pendingInQueue.size() < inQueueBlockSize && (elapsed = (GetTickCount() - start)) < inQueueMaxWait )
            {
                INMESS *im = _inQueue.getQueue(inQueueMaxWait - elapsed);

                if( im )
                {
                    pendingInQueue.push_back(im);
                }
            }

            if( !pendingInQueue.empty() )
            {
                set<long> paoids;

                for_each(pendingInQueue.begin(),
                         pendingInQueue.end(),
                         collect_inmess_target_device(paoids));

                PointManager->refreshListByPAOIDs(paoids);
            }

            try
            {
                rwServiceCancellation();
            }
            catch(const RWCancellation& cMsg)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " ResThread : " << rwThreadId() << " " <<  cMsg.why() << endl;

                bServerClosing = true;
            }

            while( !bServerClosing && !pendingInQueue.empty() )
            {
                INMESS *InMessage = pendingInQueue.front();
                pendingInQueue.pop_front();

                if( InMessage )
                {
                    LONG id = InMessage->TargetID;

                    // Checking the sequence since we will actually want the system device 0 for the Phase Detect cases
                    if(id == 0 && !(InMessage->Sequence == Cti::Protocols::EmetconProtocol::PutConfig_PhaseDetectClear ||
                                    InMessage->Sequence == Cti::Protocols::EmetconProtocol::PutConfig_PhaseDetect))
                    {
                        id = InMessage->DeviceID;
                    }

                    // Find the device..
                    CtiDeviceSPtr DeviceRecord = DeviceManager->getDeviceByID(id);

                    list<OUTMESS*   > outList;
                    list<CtiMessage*> retList;
                    list<CtiMessage*> vgList;

                    if(DeviceRecord)
                    {
                        if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Pilserver resultThread received an InMessage for " << DeviceRecord->getName();
                            dout << " at priority " << InMessage->Priority << endl;
                        }

                        /* get the time for use in the decodes */
                        CtiTime TimeNow;

                        try
                        {
                            // Do some device dependant work on this Inbound message!
                            DeviceRecord->ProcessResult( InMessage, TimeNow, vgList, retList, outList);
                        }
                        catch(...)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << CtiTime() << " Process Result FAILED " << DeviceRecord->getName() << endl;
                            }
                        }
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
                        if(outList.size())
                        {
                            while( !outList.empty() )
                            {
                                OUTMESS *OutMessage = outList.front();outList.pop_front();
                                OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
                                _porterOMQueue.putQueue(OutMessage);
                            }
                        }

                        if( retList.size() > 0 )
                        {
                            if((DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD) && vgList.size())
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Info **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << "   Device " << (DeviceRecord ? DeviceRecord->getName() : "UNKNOWN") << " has generated a dispatch return message.  Data may be duplicated." << endl;
                            }

                            string cmdstr(InMessage->Return.CommandStr);
                            CtiCommandParser parse( cmdstr );
                            if(parse.getFlags() & CMD_FLAG_UPDATE)
                            {
                                std::list< CtiMessage* >::iterator retlist_itr = retList.begin(),
                                                                   retlist_end = retList.end();

                                for( ; retlist_itr != retlist_end; ++retlist_itr )
                                {
                                    CtiMessage *&pMsg = *retlist_itr;
                                    if(InMessage->Priority > 0)
                                    {
                                        pMsg->setMessagePriority(InMessage->Priority);
                                    }

                                    if(pMsg->isA() == MSG_PCRETURN || pMsg->isA() == MSG_POINTDATA)
                                    {
                                        vgList.push_back(pMsg->replicateMessage());       // Mash it in ther if we said to do so.
                                    }
                                }
                            }
                        }


                        while( !retList.empty() )
                        {
                            CtiMessage *pRet = retList.front();retList.pop_front();
                            if(InMessage->Priority > 0)
                            {
                                pRet->setMessagePriority(InMessage->Priority);
                            }
                            CtiConnection  *Conn = NULL;

                            if( pRet->isA() == MSG_PCREQUEST )
                            {
                                _schedulerQueue.putQueue(pRet);
                            }
                            else if((Conn = ((CtiConnection*)InMessage->Return.Connection)) != NULL)
                            {
                                if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
                                {
                                    pRet->dump();
                                }

                                Conn->WriteConnQue(pRet);
                            }
                            else
                            {
                                if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Notice: Request message did not indicate return path. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << CtiTime() << " Response to client will be discarded." << endl;
                                }
                                delete pRet;
                            }
                        }

                        while( !vgList.empty() )
                        {
                            VanGoghConnection.WriteConnQue(vgList.front());
                            vgList.pop_front();
                        }
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    if(InMessage)
                    {
                        delete InMessage;
                        InMessage = 0;
                    }
                }
            }
        }
        catch(...)
        {
            Sleep(5000);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ****  EXCEPTION: PIL resultThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  - Will attmept to recover" << endl;
        }

    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ResThread : " << rwThreadId() << " terminating " << endl;
    }

    _broken = true;
}

void CtiPILServer::nexusThread()
{
    INT i = 0;
    INT status = NORMAL;
    int err;
    /* Time variable for decode */
    CtiTime      TimeNow;

    ULONG       BytesRead;
    INMESS      *InMessage = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusThread    : Started as TID " << rwThreadId() << endl;
    }

    /* Give us a tiny attitude */
    CTISetPriority(PRTYC_NOCHANGE, THREAD_PRIORITY_HIGHEST);

    SetThreadName(-1, "PILNexus ");

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        /* Wait for the next result to come back from the RTU */
        while(!PilToPorter.CTINexusValid() && !bServerClosing)
        {
            if(!(++i % 60))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " PIL connection to Port Control is inactive" << endl;
                }

                PilToPorter.CtiLocalConnectOpen();
            }

            CTISleep (500L);

            try
            {
                rwServiceCancellation();
            }
            catch(const RWCancellation& cMsg)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " NexusThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
                bServerClosing = TRUE;
                //throw;
            }
        }

        try
        {
            rwServiceCancellation();
        }
        catch(const RWCancellation& cMsg)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " NexusThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
            bServerClosing = TRUE;

            continue;
            // throw;
        }

        if(bServerClosing)
        {
            continue;
        }

        InMessage = CTIDBG_new INMESS;
        ::memset(InMessage, 0, sizeof(*InMessage));

        /* get a result off the port pipe */
        err = PilToPorter.CTINexusRead ( InMessage, sizeof(*InMessage), &BytesRead, 5);
        if(err || BytesRead < sizeof(*InMessage))
        {
            if(err != ERR_CTINEXUS_READTIMEOUT)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " NexusThread : " << rwThreadId() << " just failed to read a full InMessage." << endl;
            }

            Sleep(500); // No runnaway loops allowed.

            delete InMessage;
            InMessage = 0;
            continue;
        }

        // Enqueue the INMESS into the appropriate list
        if(InMessage)
        {
            _inQueue.putQueue(InMessage);

            InMessage = 0;
        }
    } /* End of for */

    if(InMessage)
    {
        delete InMessage;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusThread : " << rwThreadId() << " terminating " << endl;
    }

    _broken = true;
}

void CtiPILServer::nexusWriteThread()
{
    INT i = 0;
    INT status = NORMAL;
    /* Time variable for decode */
    CtiTime      TimeNow;
    ULONG       BytesWritten;

    CtiOutMessage *OutMessage;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusWriteThread    : Started as TID " << rwThreadId() << endl;
    }

    SetThreadName(-1, "PILNxsWrt");

    /* Give us a tiny attitude */
    CTISetPriority(PRTYC_NOCHANGE, THREAD_PRIORITY_HIGHEST);

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        /* Check if we need to reopen the port pipe */
        if(PilToPorter.CtiGetNexusState() == CTINEXUS_STATE_NULL)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << TimeNow.now() << " PIL lost connection to Port Control " << endl;
            }

            if(!(PilToPorter.CtiLocalConnectOpen()))
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
        if(PilToPorter.CtiGetNexusState() == CTINEXUS_STATE_NULL)
        {
            if(PilToPorter.CtiLocalConnectOpen())
            {
                status = PIPEWASBROKEN;
            }
        }

        if(OutMessage)
        {
            if(PilToPorter.CtiGetNexusState() != CTINEXUS_STATE_NULL) /* And send them to porter */
            {
                if(PilToPorter.CTINexusWrite (OutMessage, sizeof (OUTMESS), &BytesWritten, 30L) || BytesWritten == 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    DumpOutMessage(OutMessage);

                    if(PilToPorter.CtiGetNexusState() != CTINEXUS_STATE_NULL)
                    {
                        PilToPorter.CTINexusClose();
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
            dout << CtiTime() << " NexusThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
            bServerClosing = TRUE;

            continue;
            // throw;
        }
    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusWriteThread : " << rwThreadId() << " terminating " << endl;
    }

}


int CtiPILServer::executeRequest(CtiRequestMsg *pReq)
{
    int i = 0;
    int status = NoError;

    list< CtiMessage* >  vgList,  temp_vgList;
    list< CtiMessage* >  retList, temp_retList;
    list< OUTMESS* >     outList, temp_outList;

    list< CtiRequestMsg* >  execList;

    OUTMESS *OutMessage = NULL;
    CtiReturnMsg   *pcRet = NULL;
    CtiMessage     *pMsg  = NULL;
    CtiMessage     *pVg  = NULL;

    if( pReq->UserMessageId() != _currentUserMessageId ||
        !_currentParse.isEqual(pReq->CommandString()))
    {
        _currentParse = CtiCommandParser(pReq->CommandString());
        _currentUserMessageId = pReq->UserMessageId();
    }

    static const string str_system_message = "system_message";
    if( !pReq->DeviceId() && _currentParse.isKeyValid(str_system_message) )
    {
        //  first, scrub our queue of this request
        if( _currentParse.isKeyValid("request_cancel") )
        {
            group_queue_t::iterator itr     = _groupQueue.begin(),
                                    itr_end = _groupQueue.end();
            int group_message_id = pReq->GroupMessageId();

            while( itr != itr_end )
            {
                if( reinterpret_cast<const CtiRequestMsg *>(*itr)->GroupMessageId() == group_message_id )
                {
                    delete *itr;
                    itr = _groupQueue.erase(itr);
                }
                else
                {
                    itr++;
                }
            }

            _inQueue.erase_if(boost::bind(inmess_user_message_id_equal, _1, group_message_id));
        }

        //  first, count the yet to be processed items
        //  This does not count items still in the MainQueue_, only group processed items.
        if( _currentParse.isKeyValid("request_count") )
        {
            int unProcessedCount = 0;
            group_queue_t::iterator itr     = _groupQueue.begin(),
                                    itr_end = _groupQueue.end();
            int group_message_id = pReq->GroupMessageId();

            for each(CtiRequestMsg * msg in _groupQueue)
            {
                if( msg->GroupMessageId() == group_message_id )
                {
                    unProcessedCount++;
                }
            }

            pReq->setOptionsField(unProcessedCount);
        }

        //This message is a system request for porter, send it to the porter system thread, not a device.
        CtiMessage* tempReqMsg = pReq->replicateMessage();
        tempReqMsg->setConnectionHandle(pReq->getConnectionHandle());
        PorterSystemMessageQueue.putQueue(tempReqMsg);
        tempReqMsg = NULL;
        pReq = NULL;
        return status;
    }

    try
    {
        // Note that any and all arguments into this method may be altered on exit!
        analyzeWhiteRabbits(*pReq, _currentParse, execList, retList);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    try
    {
        CtiDeviceSPtr Dev;
        std::list< CtiRequestMsg* >::iterator itr = execList.begin();
        while( itr != execList.end() )
        {
            Dev.reset();

            CtiRequestMsg *&pExecReq = *itr;
            Dev = DeviceManager->getDeviceByID(pExecReq->DeviceId());

            if(Dev)
            {
                if( !_currentParse.isEqual(pExecReq->CommandString()) )
                {
                    // They did not match!  We MUST re-parse!
                    _currentParse = CtiCommandParser(pExecReq->CommandString());
                }

                pExecReq->setMacroOffset( Dev->selectInitialMacroRouteOffset(pReq->RouteId() != 0 ? pReq->RouteId() : Dev->getRouteID()) );

                /*
                 *  We will execute based upon the data in the request....
                 */

                if(!pExecReq->getSOE())     // We should attach one if one is not already set...
                {
                    pExecReq->setSOE( SystemLogIdGen() );  // Get us a new number to deal with
                }
                delete_container(temp_outList);
                temp_outList.clear();              // Just make sure!

                if(Dev->isGroup())                          // We must indicate any group which is protocol/heirarchy controlled!
                {
                    indicateControlOnSubGroups(Dev, pExecReq, _currentParse, temp_vgList, temp_retList);
                }

                try
                {
                    status = Dev->beginExecuteRequest(pExecReq, _currentParse, temp_vgList, temp_retList, temp_outList);
                    reportClientRequests(Dev, _currentParse, pReq, pExecReq, temp_vgList, temp_retList);
                }
                catch(...)
                {
                    {
                        CtiTime NowTime;
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << NowTime << " ExecuteRequest FAILED for \"" << Dev->getName() << "\"" << endl;
                        dout << NowTime << "   Command: " << pExecReq->CommandString() << endl;
                    }
                }

                while( !temp_outList.empty() )
                {
                    // _porterOMQueue.putQueue(temp_outList.get());
                    outList.push_back(temp_outList.front());
                    temp_outList.pop_front();
                }

                while( !temp_retList.empty() )
                {
                    //  smuggle any request messages back out to the scheduler queue
                    if( temp_retList.front() && temp_retList.front()->isA() == MSG_PCREQUEST )
                    {
                        _schedulerQueue.putQueue(temp_retList.front());
                    }
                    else
                    {
                        retList.push_back(temp_retList.front());
                    }

                    temp_retList.pop_front();
                }

                while( !temp_vgList.empty() )
                {
                    vgList.push_back(temp_vgList.front());
                    temp_vgList.pop_front();
                }

                if(status != NORMAL &&
                   status != DEVICEINHIBITED)
                {
                    CtiTime NowTime;
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
                    dout << CtiTime() << " Command " << pExecReq->CommandString() << endl;
                    dout << CtiTime() << " Device: " << pExecReq->DeviceId() << endl;
                }

                CtiServer::ptr_type ptr = findConnectionManager((long)pExecReq->getConnectionHandle());

                if(ptr)
                {
                    CtiPILConnectionManager *CM = (CtiPILConnectionManager *)ptr.get();
                    CtiReturnMsg *pcRet = CTIDBG_new CtiReturnMsg(pExecReq->DeviceId(),
                                                                  pExecReq->CommandString(),
                                                                  "Device unknown, unselected, or DB corrupt. ID = " + CtiNumStr(pExecReq->DeviceId()),
                                                                  IDNF,
                                                                  pExecReq->RouteId(),
                                                                  pExecReq->MacroOffset(),
                                                                  pExecReq->AttemptNum(),
                                                                  pExecReq->GroupMessageId(),
                                                                  pExecReq->UserMessageId(),
                                                                  pExecReq->getSOE());

                    if(pcRet != NULL)
                    {
                        CM->WriteConnQue(pcRet);
                    }
                }
            }
            ++itr;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Submitting " << retList.size() << " CtiReturnMsg objects to client" << endl;
        }
    }

    while( (i = retList.size()) > 0 )
    {
        pcRet = (CtiReturnMsg*)retList.front();retList.pop_front();

        /*if( i > 1 ) //This is causing problems when we return "no method" but dont want expectmore = 1
        {
            pcRet->setExpectMore(TRUE);    // Let the client know more messages are coming
        }*/

        //  Note that this sends all responses to the initial request message's connection -
        //    even if any other return messages were generated by another ExecuteRequest invoked
        //    from the original and have their ConnectionHandle set to 0!
        CtiServer::ptr_type ptr = findConnectionManager((long)pReq->getConnectionHandle());

        if(ptr)
        {
            CtiPILConnectionManager *CM = (CtiPILConnectionManager *)ptr.get();
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                pcRet->dump();
            }

            CM->WriteConnQue(pcRet);
        }
        else
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Notice: Request Message did not indicate return path." << endl;
                dout << CtiTime() << " Response will be discarded." << endl;
                dout << CtiTime() << " Command String: " << pcRet->CommandString() << endl;
            }

            delete pcRet;
        }
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Submitting " << outList.size() << " CtiOutMessage objects to porter" << endl;
        }
    }

    for( i = outList.size() ; i > 0; i-- )
    {
        OutMessage = outList.front();outList.pop_front();
        _porterOMQueue.putQueue(OutMessage);
        OutMessage = 0;
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Submitting " << vgList.size() << " CtiMessage objects to dispatch" << endl;
        }
    }

    while( (i = vgList.size()) > 0 )
    {
        pVg = vgList.front();vgList.pop_front();
        VanGoghConnection.WriteConnQue((CtiMessage*)pVg);
    }

    delete_container(vgList);
    vgList.clear();

    delete_container(retList);
    retList.clear();

    delete_container(execList);
    execList.clear();

    return status;
}

int CtiPILServer::executeMulti(CtiMultiMsg *pMulti)
{
    int status = NoError;

    CtiMessage *pMyMsg = NULL;

    if(pMulti != NULL)
    {
        CtiMultiMsg_vec::iterator itr = pMulti->getData().begin();
        //RWOrderedIterator itr( pMulti->getData() );

        for(;itr != pMulti->getData().end(); itr++)
        {
            if((pMyMsg = (CtiMessage*) *itr) != NULL)
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
    }

    return status;
}

void CtiPILServer::clientShutdown(CtiServer::ptr_type CM)
{
//#ifdef DEBUG_SHUTDOWN
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Now shutting down (ClientAppShutdown) in PIL Server" << endl;
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
        dout << CtiTime() << " PIL vgConnThrd : Started as TID " << rwThreadId() << endl;
    }

    SetThreadName(-1, "VGConnThd");

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
                            // bServerClosing = TRUE;
                            {
                                Cmd->dump();
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Shutdown requests by command messages are ignored." << endl;
                            }
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
        dout << CtiTime() << " PIL vgConnThrd : " << rwThreadId() << " terminating " << endl;
    }

}

struct message_time_less : public binary_function< CtiMessage *, CtiMessage *, bool>
{
    bool operator()(CtiMessage *lhs, CtiMessage *rhs)
    {
        return (lhs && rhs)?(lhs->getMessageTime() < rhs->getMessageTime()):(lhs < rhs);
    }
};


void CtiPILServer::schedulerThread()
{
    fifo_multiset<CtiMessage *, message_time_less> message_queue;

    CtiMessage *pMsg;

    unsigned last_iteration;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PIL schedulerThread : Started as TID " << rwThreadId() << endl;
    }

    SetThreadName(-1, "schdlrThd");

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        pMsg = _schedulerQueue.getQueue(1000);

        if(pMsg != NULL)
        {
            message_queue.insert(pMsg);
        }

        while( !message_queue.empty() && (*(message_queue.begin()))->getMessageTime() <= CtiTime::now() )
        {
            //  write it to PIL
            putQueue(*(message_queue.begin()));

            message_queue.erase(message_queue.begin());
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PIL schedulerThread : " << rwThreadId() << " terminating " << endl;
    }
}

int CtiPILServer::getDeviceGroupMembers( string groupname, vector<long> &paoids )
{
    int deviceid;

    if(DebugLevel & 0x00020000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Loading group \"" << groupname << "\"" << endl;
    }

    vector< string > group_taxonomy;
    string::size_type slashpos = 0;

    while( (slashpos = groupname.find_last_of('/')) != string::npos )
    {
        //  substr will copy all available if length == string::npos
        group_taxonomy.push_back(groupname.substr(slashpos + 1));

        //  erase everything after the slash
        groupname.erase(slashpos);
    }

    std::stringstream ss;

    const string basicSQL = "SELECT DGM.yukonpaoid "
                            "FROM DeviceGroupMember DGM";

    unsigned group_size = group_taxonomy.size();

    ss << basicSQL;

    for(int i = group_size; i >= 0; i--)
    {
        ss << ", DeviceGroup GP" << i;
    }

    ss << " WHERE GP0.parentdevicegroupid IS NULL";

    if(group_size)
    {
        ss << " AND GP" << group_size << ".devicegroupid = DGM.devicegroupid";
    }
    else
    {
        ss << " AND GP0.devicegroupid = DGM.devicegroupid";
    }

    while( !group_taxonomy.empty() )
    {
        ss << " AND GP" << group_size - 1 << ".devicegroupid = GP" << group_size << ".parentdevicegroupid AND ";
        ss << "lower (GP" << group_size << ".groupname) = ";

        const string str = group_taxonomy.front().c_str();

        if(str.size() == 0)
        {
                ss << "NULL";
        }
        else
        {
                ss << "'" << str << "'";
        }

        group_taxonomy.erase(group_taxonomy.begin());

        group_size--;
    }

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, ss.str());
    rdr.execute();

    if( DebugLevel & 0x00020000 || !rdr.isValid() )
    {
        string loggedSQLstring = rdr.asString();
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << loggedSQLstring << endl;
        }
    }

    while( rdr() )
    {
        rdr[0] >> deviceid;

        paoids.push_back(deviceid);
    }

    return rdr.isValid() ? 0 : 1;
}


INT CtiPILServer::analyzeWhiteRabbits(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList)
{
    INT status = NORMAL;
    INT i;
    bool isGroupCommand = false;

    CtiRequestMsg *pReq = (CtiRequestMsg*)Req.replicateMessage();
    pReq->setConnectionHandle( Req.getConnectionHandle() );

    static const string str_serial = "serial";
    if(parse.isKeyValid(str_serial))
    {
        pReq->setDeviceId( SYS_DID_SYSTEM );    // Make sure we are targeting the serial/system device;
    }

    // Can you say WHITE RABBIT?  This could override the above!
    // This code will not execute in most cases
    static const string str_device = "device";
    if(parse.isKeyValid(str_device))
    {
        if( -1 != (i = parse.getiValue(str_device)) )
        {
            // OK, someone tried to send us an override on the device ID
            pReq->setDeviceId( i ) ;
        }
        else
        {
            string dname = parse.getsValue(str_device);
            CtiDeviceSPtr Dev = DeviceManager->RemoteGetEqualbyName( dname );
            if(Dev)
            {
                pReq->setDeviceId( Dev->getID() ) ;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Execute Error **** " << endl;
                    dout << CtiTime() << " Device Name: " << dname << endl;
                    dout << CtiTime() << " Command: " << pReq->CommandString() << endl;
                    dout << CtiTime() << " No device found in the database with this device name." << endl;
                }

                CtiReturnMsg *pcRet = CTIDBG_new CtiReturnMsg(pReq->DeviceId(),
                                                              pReq->CommandString(),
                                                              "No device with name '" + dname + "' exists in the database.",
                                                              IDNF,
                                                              pReq->RouteId(),
                                                              pReq->MacroOffset(),
                                                              pReq->AttemptNum(),
                                                              pReq->GroupMessageId(),
                                                              pReq->UserMessageId(),
                                                              pReq->getSOE());

                retList.push_back(pcRet);
                delete pReq;

                return status;
            }
        }
    }

    static const string str_route  = "route";
    if(parse.isKeyValid(str_route))
    {
        if( (i = parse.getiValue(str_route)) != -1 )
        {
            // OK, someone tried to send us an override on the route ID
            pReq->setRouteId( i );
        }
        else
        {
            //  apparently we've been sent a route name
            string routeName = parse.getsValue(str_route);
            CtiRouteSPtr tmpRoute;

            tmpRoute = RouteManager->getEqualByName( routeName );

            if(tmpRoute)
            {
                pReq->setRouteId( tmpRoute->getRouteID() );
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if(parse.getCommand() != GetValueRequest)
    {
        CtiDeviceSPtr Dev = DeviceManager->getDeviceByID(pReq->DeviceId());

        if( Dev )
        {
            if( (isRepeater(Dev->getType())) && parse.isKeyValid("install") )
            {
                analyzeAutoRole(*pReq,parse,execList,retList);
            }
            else if( Dev->getType() == TYPE_LMGROUP_POINT )
            {
                analyzePointGroup(*pReq,parse,execList,retList);
            }
        }
    }

    //  if we have no device id, check to see if we're a group command
    if( !pReq->DeviceId() )
    {
        bool group     = false,
             altgroup  = false,
             billgroup = false;

        //  note that only one of these will be set at once - the first one
        //    will cause the if block to be true, and the others will be false
        if( (group     = parse.isKeyValid("group"))    ||
            (altgroup  = parse.isKeyValid("altgroup")) ||
            (billgroup = parse.isKeyValid("billgroup")) )
        {
            string groupname;
            int groupsubmitcnt = 0;
            vector<long> members;

            if( group )     groupname = parse.getsValue("group");
            if( altgroup )  groupname = parse.getsValue("altgroup");
            if( billgroup ) groupname = parse.getsValue("billgroup");

            //  if it's not a new-style group, convert it
            if( groupname.find_first_of('/') == string::npos )
            {
                if( group )     groupname = "/Meters/Collection/" + groupname;
                if( altgroup )  groupname = "/Meters/Alternate/"  + groupname;
                if( billgroup ) groupname = "/Meters/Billing/"    + groupname;
            }

            //  this catches any old-style group names with embedded slashes
            if( groupname.find_first_of('/') > 0 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint - groupname \"" << groupname << "\" is malformed - cannot determine group hierarchy **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            else
            {
                std::transform(groupname.begin(), groupname.end(), groupname.begin(), ::tolower);

                getDeviceGroupMembers(groupname, members);
                isGroupCommand = true; // We will take the remaining pReq and place it on the group queue as well.
            }

            vector<long>::iterator itr, members_end = members.end();

            //  the parser eliminates only the parse keywords that have already been acted on (select device, route)
            pReq->setCommandString(parse.getCommandStr());

            for( itr = members.begin(); itr != members_end; )
            {
                //   Note that we're going to let PIL fail us on a failed device lookup to save us the device lookup here
                pReq->setDeviceId(*itr);
                itr++;

                //On the last loop we dont put the message into the group queue.
                if(itr != members_end)
                {
                    // Create a message for this one!
                    CtiRequestMsg *pNew = (CtiRequestMsg*)pReq->replicateMessage();
                    pNew->setConnectionHandle( pReq->getConnectionHandle() );

                    //  put it back on the queue to be processed in order
                    _groupQueue.insert(pNew);
                }

                groupsubmitcnt++;
            }

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << groupname << " found " << groupsubmitcnt << " target devices." << endl;
            }
        }
    }

    if(parse.getCommand() == PutConfigRequest)
    {
        UINT modifier = 0;

        if( findStringIgnoreCase(parse.getCommandStr()," force") ) modifier |= CtiDeviceBase::PutConfigAssignForce;
        if( findStringIgnoreCase(gConfigParms.getValueAsString("EXPRESSCOM_FORCE_FULL_CONFIG","false"),"true") ) modifier |= CtiDeviceBase::PutConfigAssignForce;


        if(parse.isKeyValid("template") && INT_MIN != parse.getiValue("serial"))
        {
            /* OK, a serial number was specified in a putconfg, with a template......
             *  We will take that to mean that the desired outcome is to assign this group's
             *  addressing to the serial number specified...
             */

            string lmgroup = parse.getsValue("template");
            string service = parse.getsValue("templateinservice");
            char newparse[256];

            CtiDeviceSPtr GrpDev = DeviceManager->RemoteGetEqualbyName( lmgroup );
            if(GrpDev)
            {
                _snprintf(newparse, 255, "putconfig serial %d %s %s", parse.getiValue("serial"), GrpDev->getPutConfigAssignment(modifier).c_str(), service.c_str());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Template putconfig **** " << endl << "   " << newparse << endl;
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
            char newparse[256];

            CtiDeviceGroupVersacom *GrpDev = (CtiDeviceGroupVersacom *)DeviceManager->getDeviceByID(pReq->DeviceId()).get();
            // Dev = DeviceManager->getEqual(SYS_DID_SYSTEM);     // This is the guy who does ALL configs.
            if(GrpDev != NULL)
            {
                _snprintf(newparse, 255, "%s %s", pReq->CommandString().c_str(), GrpDev->getPutConfigAssignment(modifier).c_str());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Group reassign to group **** " << GrpDev->getName() << endl << "   " << newparse << endl;
                }

                pReq->setCommandString(newparse);       // Make the request match our new choices
                pReq->setRouteId(GrpDev->getRouteID()); // Just on this route.
                parse = CtiCommandParser(newparse);     // Should create a new actionItem list
            }
        }
    }
    else if(parse.getCommand() == PutStatusRequest)
    {
        if(parse.isKeyValid("freeze") && pReq->DeviceId())
        {
            boost::regex coll_grp("collection_group +((\"|')[^\"']+(\"|'))");
            CtiString tmp, groupname;

            int next_freeze = parse.getiValue("freeze");

            if( !(tmp = CtiString(parse.getCommandStr().c_str()).match(coll_grp)).empty() )
            {
                //  pull out the group name
                groupname = tmp.match(boost::regex("(\"|')[^\"']+(\"|')"));
                //  trim off the quotes
                groupname = groupname.substr(1, groupname.size() - 2);

                vector<long> members;

                //  if it's not a new-style group, convert it
                if( groupname.find_first_of('/') == string::npos )
                {
                    groupname = CtiString("/Meters/Collection/") + groupname;
                }

                //  this catches any old-style group names with embedded slashes
                if( groupname.find_first_of('/') > 0 )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - groupname \"" << groupname << "\" is malformed - cannot determine group hierarchy **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                else
                {
                    std::transform(groupname.begin(), groupname.end(), groupname.begin(), ::tolower);

                    getDeviceGroupMembers(groupname, members);
                }

                vector<long>::iterator itr, members_end = members.end();

                for( itr = members.begin(); itr != members_end; itr++ )
                {
                    CtiDeviceManager::ptr_type device = DeviceManager->getDeviceByID(*itr);

                    if( device )
                    {
                        //  if a freeze wasn't specified, grab the first MCT and initialize the freeze counter
                        //    with what he expects to hear next
                        if( !next_freeze )
                        {
                            shared_ptr<Cti::Devices::MctDevice> mct = boost::static_pointer_cast<Cti::Devices::MctDevice>(device);

                            next_freeze = mct->getNextFreeze();
                        }

                        device->setExpectedFreeze(next_freeze);
                    }
                }

                //  this is where we'd attempt to correct devices that have an incorrect freeze counter
            }

            if(parse.isKeyValid("voltage"))
            {

            }
        }
    }

    // The last request from the group is not placed on the group list until now.
    // This allows some processing to happen after the group expansion,
    // but the message must go into the queue or MACS has serious problems.
    if(isGroupCommand && pReq != NULL)
    {
        _groupQueue.insert(pReq);
        pReq = NULL;
    }

    if(execList.size() == 0 && pReq != NULL)
    {
        execList.push_back( pReq );
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
        CtiDeviceSPtr DeviceRecord = DeviceManager->getDeviceByID(((CtiRequestMsg*)MsgPtr)->DeviceId());
        if(DeviceRecord)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Pilserver mainThread received a CtiRequestMsg for " << DeviceRecord->getName();
            dout << " at priority " << MsgPtr->getMessagePriority() << endl;

            CtiRequestMsg *pCmd = (CtiRequestMsg*)MsgPtr;

            if(!pCmd->CommandString().empty())
            {
                dout << CtiTime() << "   Command string: \"" << pCmd->CommandString() << "\"" << endl;
            }
        }
    }
    else if(MsgPtr->isA() == MSG_MULTI)
    {
        CtiMultiMsg_vec::iterator itr = ((CtiMultiMsg*)MsgPtr)->getData().begin();
        //RWOrderedIterator itr( ((CtiMultiMsg*)MsgPtr)->getData() );
        CtiMessage *pMyMsg;

        while( ((CtiMultiMsg*)MsgPtr)->getData().end() != itr )//TS
        {
            pMyMsg = (CtiMessage*)*itr;
            ReportMessagePriority(pMyMsg, DeviceManager);// And recurse.
            itr++;
        }
    }

    return;
}

INT CtiPILServer::analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList)
{
    INT status = NORMAL;
    int i;
    CtiDeviceManager::coll_type::reader_lock_guard_t guard(DeviceManager->getLock());  //  I don't think we need this, but I'm leaving it until we prove that out
    // CtiRouteManager::LockGuard rte_guard(RouteManager->getMux());

    CtiDeviceSPtr pRepeaterToRole = DeviceManager->getDeviceByID(Req.DeviceId());    // This is our repeater we are curious about!

    if(pRepeaterToRole)
    {
        if( isRepeater(pRepeaterToRole->getType()) )
        {
            //CtiRequestMsg *pReq = (CtiRequestMsg*)Req.replicateMessage();
            //pReq->setConnectionHandle( Req.getConnectionHandle() );

            // Alright.. An appropriate device has been selected for this command.
            vector< CtiDeviceRepeaterRole > roleVector;

            try
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Looking for " << pRepeaterToRole->getName() << " in all routes" << endl;
                }

                RouteManager->buildRoleVector( pRepeaterToRole->getID(), Req, retList, roleVector );
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            if(roleVector.size() > 0)
            {
                string newReqString = string("putconfig emetcon mrole 1");       // We always write 1 through whatever.
                string roleStr;

                for(i = 0; i < roleVector.size(); i++)
                {
                    // dev_repeater.cpp now looks for a : and uses this to later send out a second command.
                    // This gives us a way to "sleep" between transmissions so the RPT-901 can keep up with us.
                    // The repeater code essentially strips off the first command then sends the 2nd, so to tell
                    // it how to do this we add a : to seperate the commands. We must put the role number in front
                    // so dev_repeater.cpp has a proper command to work with. This is all decoded in
                    // CtiDeviceRepeater900::decodePutConfigRole().
                    //
                    // putconfig emetcon mrole 1 3 4 7 0 4 5 6 1 0 5 7 0 6 2 7 0 2 5 7 0 1 1 7 0 : 7 0 2 7 0 5 5 7 0
                    //                         ^ Role number                                       ^ Role number
                    if( i != 0 && i % 6 == 0 )
                    {
                        roleStr += " : ";
                        roleStr += CtiNumStr(i+1);
                    }
                    roleStr += " " + CtiNumStr((int)roleVector[i].getFixBits());
                    roleStr += " " + CtiNumStr((int)roleVector[i].getOutBits());
                    roleStr += " " + CtiNumStr((int)roleVector[i].getInBits());
                    roleStr += " " + CtiNumStr((int)roleVector[i].getStages());
                }

                newReqString += roleStr;

                if( parse.isKeyValid("noqueue") && newReqString.find("noqueue")!=string::npos )
                {
                    newReqString += " noqueue";
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << "  " << newReqString << endl;
                }

                Req.setCommandString( newReqString );
            }
        }
    }

    return status;
}


INT CtiPILServer::analyzePointGroup(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList)
{
    CtiDeviceManager::coll_type::reader_lock_guard_t guard(DeviceManager->getLock());  //  I don't think we need this, but I'm leaving it until we prove that out

    CtiDeviceSPtr ptGroup = DeviceManager->getDeviceByID(Req.DeviceId());    // This is our repeater we are curious about!

    if(ptGroup)
    {
        CtiRequestMsg *pReq = new CtiRequestMsg;
        ((CtiDeviceGroupPoint*)ptGroup.get())->generateRequest(pReq, parse);
        pReq->setUser( Req.getUser() );

        execList.push_back( pReq );                                        // Fine then.
        execList.push_back( (CtiRequestMsg*)Req.replicateMessage() );
    }

    return NORMAL;
}

void CtiPILServer::putQueue(CtiMessage *Msg)
{
    MainQueue_.putQueue( Msg );
}


void CtiPILServer::indicateControlOnSubGroups(CtiDeviceSPtr &Dev, CtiRequestMsg *&pReq, CtiCommandParser &parse, list< CtiMessage* > &vgList, list< CtiMessage* > &retList)
{
    try
    {
        if(findStringIgnoreCase(gConfigParms.getValueAsString("PIL_IDENTIFY_SUBGROUP_CONTROLS"), "true") &&
           parse.getCommand() == ControlRequest)
        {
            if(Dev->getType() == TYPE_MACRO)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ACH indicateControlOnSubGroups for MACRO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
            else
            {
                vector< CtiDeviceManager::ptr_type > match_coll;

                if(parse.getFlags() & (CMD_FLAG_CTL_RESTORE|CMD_FLAG_CTL_TERMINATE|CMD_FLAG_CTL_CLOSE))
                {
                    DeviceManager->select(findRestoreDeviceGroupControl, (void*)(Dev.get()), match_coll);
                }
                else
                {
                    DeviceManager->select(findShedDeviceGroupControl, (void*)(Dev.get()), match_coll);
                }

                CtiDeviceSPtr sptr;
                while(!match_coll.empty())
                {
                    sptr = match_coll.back();
                    match_coll.pop_back();

                    CtiMessage *pMsg = sptr->rsvpToDispatch(true);
                    if(pMsg)
                    {
                        vgList.push_back(pMsg);
                    }

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Protocol hierarchy match on group: " << sptr->getName() << endl;
                    }
                }
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return;
}

static bool findShedDeviceGroupControl(const long key, CtiDeviceSPtr otherdevice, void *vptrControlParent)
{
    bool bstat = false;
    CtiDeviceBase *parentGroup = (CtiDeviceBase *)vptrControlParent;

    if(parentGroup->getID() != otherdevice->getID() && parentGroup->getType() == otherdevice->getType())
    {
        // they are both groups and of the same type.  Now we need to try to determine if the other dev is a heirarchy match.
        if( parentGroup->isShedProtocolParent(otherdevice.get()) )
        {
            bstat = true;
        }
    }
    return bstat;
}

static bool findRestoreDeviceGroupControl(const long key, CtiDeviceSPtr otherdevice, void *vptrControlParent)
{
    bool bstat = false;
    CtiDeviceBase *parentGroup = (CtiDeviceBase *)vptrControlParent;

    if(parentGroup->getID() != otherdevice->getID() && parentGroup->getType() == otherdevice->getType())
    {
        // they are both groups and of the same type.  Now we need to try to determine if the other dev is a heirarchy match.
        if( parentGroup->isRestoreProtocolParent(otherdevice.get()) )
        {
            bstat = true;
        }
    }

    return bstat;
}


/*
 *  This method takes a look at the parsed request and processes the command into the event log.  This will happen if and only if
 *  1. The device is known.
 *  2. The parse is of type control, putvalue, putconfig, or putstatus.
 *  3. Username will be acquired from pExecReq first, and then pReqOrig if not specified.
 */
int CtiPILServer::reportClientRequests(CtiDeviceSPtr &Dev, const CtiCommandParser &parse, const CtiRequestMsg * pReqOrig, const CtiRequestMsg *pExecReq, list< CtiMessage* > &vgList, list< CtiMessage* > &retList)
{
    int status = NORMAL;

    long pid = SYS_PID_PORTER;
    static unsigned soe = 0;
    string text, addl;

    static const string PIL_OMIT_COMMAND_LOGGING("PIL_OMIT_COMMAND_LOGGING");
    static const string PIL_LOG_UNKNOWN_USERS   ("PIL_LOG_UNKNOWN_USERS");

    if( !gConfigParms.isTrue(PIL_OMIT_COMMAND_LOGGING) )   // Set this to true if you want to skip the excessive logs from pil.
    {
        bool name_none  = !pReqOrig->getUser().empty() && (!stringCompareIgnoreCase(pReqOrig->getUser(), "none") || !stringCompareIgnoreCase(pReqOrig->getUser(), "(none)"));
        bool user_valid = !pReqOrig->getUser().empty() && (!name_none || gConfigParms.isTrue(PIL_LOG_UNKNOWN_USERS) );

        if(Dev && user_valid &&
            (parse.getCommand() == ControlRequest ||
             parse.getCommand() == PutConfigRequest ||
             parse.getCommand() == PutStatusRequest ||
             parse.getCommand() == PutValueRequest) )
        {

            addl = Dev->getName() + " / (" + CtiNumStr(Dev->getID()) + ")";
            text = string("Command Request: ") + parse.getCommandStr();

            // The user has requested an "outbound" field action.  We have everything we need to generate a log.
            CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(pid, ++soe, text, addl, PILLogType, SignalEvent, pReqOrig->getUser() );
            vgList.push_back(pSig);

            std::list< CtiMessage* >::const_iterator itr;

            for(itr = retList.begin(); itr != retList.end(); itr++)
            {
                const CtiReturnMsg *&pcRet = (const CtiReturnMsg*&)*itr;

                addl = Dev->getName() + " / (" + CtiNumStr(Dev->getID()) + "): " + pcRet->CommandString();
                if(pcRet->Status() == NORMAL)
                    text = string("Success: ");
                else
                    text = string("Failed (Err ") + CtiNumStr(pcRet->Status()) + "): ";

                text += pcRet->ResultString();
                CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(pid, soe, text, addl, PILLogType, SignalEvent, pReqOrig->getUser() );
                vgList.push_back(pSig);
            }
        }
    }

    return status;
}


bool inmess_user_message_id_equal(const INMESS &in, int user_message_id)
{
    return in.Return.UserID == user_message_id;
}

