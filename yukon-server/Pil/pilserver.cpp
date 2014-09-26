#include "precompiled.h"

#include "os2_2w32.h"
#include "cticalls.h"

#include "dev_grp_versacom.h"
#include "dev_grp_point.h"
#include "dev_mct.h"
#include "dev_rfn.h"
#include "dsm2.h"
#include "streamSocketConnection.h"
#include "streamLocalConnection.h"
#include "porter.h"

#include "cparms.h"
#include "netports.h"
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

#include "ctibase.h"
#include "dllbase.h"
#include "logger.h"
#include "repeaterrole.h"
#include "rte_ccu.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "amq_connection.h"
#include "PorterResponseMessage.h"

#include "mgr_rfn_request.h"

#include "ctistring.h"
#include "debug_timer.h"
#include "millisecond_timer.h"

#include "connection_client.h"
#include "win_helper.h"

#include <rw/thr/thrfunc.h>
#include <rw/rwerr.h>
#include <rw/thr/mutex.h>

#include <boost/regex.hpp>
#include <boost/bind.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/assign/list_of.hpp>
#include <boost/ptr_container/ptr_deque.hpp>

#include <iomanip>
#include <iostream>
#include <vector>

using namespace std;

extern IM_EX_CTIBASE void DumpOutMessage(void *Mess);

CtiClientConnection   VanGoghConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );
CtiPILExecutorFactory ExecFactory;

/* Define the return nexus handle */
DLLEXPORT Cti::StreamLocalConnection<OUTMESS, INMESS> PilToPorter; //Pil handles this one
DLLEXPORT CtiFIFOQueue< CtiMessage >                  PorterSystemMessageQueue;

using Cti::Timing::Chrono;

namespace Cti {
namespace Pil {

void ReportMessagePriority( CtiMessage *MsgPtr, CtiDeviceManager *&DeviceManager );

bool inmess_user_message_id_equal(const INMESS &in, int user_message_id);

static bool findShedDeviceGroupControl(const long key, CtiDeviceSPtr otherdevice, void *vptrControlParent);
static bool findRestoreDeviceGroupControl(const long key, CtiDeviceSPtr otherdevice, void *vptrControlParent);

namespace { // anonymous namespace

/**
 * checks if a connection is non-viable
 *
 * @param CM
 * @param d
 * @return true if non-viable, false otherwise
 */
bool NonViableConnection(CtiServer::ptr_type &CM, void* d)
{
    return ! CM->isViable();
}

} // anonymous namespace

PilServer::PilServer(CtiDeviceManager *DM, CtiPointManager *PM, CtiRouteManager *RM) :
   DeviceManager(DM),
   PointManager (PM),
   RouteManager (RM),
   bServerClosing(FALSE),
   _currentParse(""),
   _currentUserMessageId(0),
   _listenerConnection( Cti::Messaging::ActiveMQ::Queue::pil ),
   _rfnRequestId(0)
{
    serverClosingEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
    if( serverClosingEvent == (HANDLE)NULL )
    {
        cout << "Couldn't create serverClosingEvent event!" << endl;
        exit(-1);
    }
}

PilServer::~PilServer()
{
   while( !_inQueue.isEmpty() )
   {
      delete _inQueue.getQueue();
   }

   CloseHandle(serverClosingEvent);
}

int PilServer::execute()
{
    _broken = false;

    try
    {
        //  all references to this need to be moved to Scanner - we now use PilToPorter and PorterToPil
        PortPipeInit (NOWAIT);

        if(!bServerClosing)
        {
            MainThread_ = rwMakeThreadFunction(*this, &PilServer::mainThread);
            MainThread_.start();

            ConnThread_ = rwMakeThreadFunction(*this, &PilServer::connectionThread);
            ConnThread_.start();

            ResultThread_ = rwMakeThreadFunction(*this, &PilServer::resultThread);
            ResultThread_.start();

            _nexusThread = rwMakeThreadFunction(*this, &PilServer::nexusThread);
            _nexusThread.start();

            _nexusWriteThread = rwMakeThreadFunction(*this, &PilServer::nexusWriteThread);
            _nexusWriteThread.start();

            _vgConnThread = rwMakeThreadFunction(*this, &PilServer::vgConnThread);
            _vgConnThread.start();

            _schedulerThread = rwMakeThreadFunction(*this, &PilServer::schedulerThread);
            _schedulerThread.start();

            _periodicActionThread = rwMakeThreadFunction(*this, &PilServer::periodicActionThread);
            _periodicActionThread.start();
        }
    }
    catch(const RWxmsg& x)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
    }

    return 0;
}

void PilServer::mainThread()
{
    BOOL          bQuit = FALSE;
    YukonError_t  status;

    CtiMessage   *MsgPtr;
    int groupBypass = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PILMainThread  : Started as TID " << rwThreadId() << endl;
    }

    VanGoghConnection.setName("Pil to Dispatch");
    VanGoghConnection.start();
    VanGoghConnection.WriteConnQue(CTIDBG_new CtiRegistrationMsg(PIL_REGISTRATION_NAME, rwThreadId(), TRUE));

    if( CtiDeviceSPtr systemDevice = DeviceManager->getDeviceByID(0) )
    {
        systemDevice->getDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_E2eRequestId, _rfnRequestId);
    }

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
                    Cti::Timing::DebugTimer messageProcessingTimer("PIL mainThread message processing");

                    if(DebugLevel & DEBUGLEVEL_PIL_MAINTHREAD)
                    {
                        ReportMessagePriority(MsgPtr, DeviceManager);
                    }

                    /* Use the same time base for the full scan check */
                    const CtiTime TimeNow;

                    if(MsgPtr->isA() == MSG_PCREQUEST && MsgPtr->getMessageTime().seconds() < (TimeNow.seconds() - 900))
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << TimeNow << " PIL processing an inbound request message which is over 15 minutes old.  Message will be discarded." << endl;
                            dout << " >>---------- Message Content ----------<< " << endl;
                            MsgPtr->dump();
                            dout << " <<---------- Message Content ---------->> " << endl;
                        }

                        if( CtiConnection *requestingClient = static_cast<CtiConnection *>(MsgPtr->getConnectionHandle()) )
                        {
                            const CtiRequestMsg *req = static_cast<const CtiRequestMsg *>(MsgPtr);

                            auto_ptr<CtiReturnMsg> expiredRequestError(
                                new CtiReturnMsg(
                                        req->DeviceId(),
                                        req->CommandString(),
                                        GetErrorString(ClientErrors::RequestExpired),
                                        ClientErrors::RequestExpired,
                                        req->RouteId(),
                                        req->MacroOffset(),
                                        req->AttemptNum(),
                                        req->GroupMessageId(),
                                        req->UserMessageId(),
                                        req->getSOE()));

                            requestingClient->WriteConnQue(expiredRequestError.release());
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
                    else if( CtiExecutor *pExec = ExecFactory.getExecutor(MsgPtr) )
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

                if( CtiDeviceSPtr systemDevice = DeviceManager->getDeviceByID(0) )
                {
                    systemDevice->setDynamicInfo(CtiTableDynamicPaoInfo::Key_RFN_E2eRequestId, _rfnRequestId);
                }

                // Force the inherited Listener socket to close!
                Inherited::shutdown();                   // Should cause the ConnThread_ to be closed!
                                                         //
                try
                {
                    // This forces the listener thread to exit on shutdown.
                    _listenerConnection.close();
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
    VanGoghConnection.close();
}

void PilServer::connectionThread()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ConnThread     : Started as TID " << rwThreadId() << endl;
    }

    // main loop
    try
    {
        for(;!bServerClosing;)
        {
            if( !_listenerConnection.verifyConnection() )
            {
                mConnectionTable.clear();

                _listenerConnection.start();
            }

            if( _listenerConnection.acceptClient() )
            {
                // Create new pil connection manager
                CtiServer::ptr_type sptrConMan( CTIDBG_new CtiPILConnectionManager( _listenerConnection, &MainQueue_ ));

                sptrConMan->setClientName("DEFAULT");

                // the new client connection
                clientConnect( sptrConMan );

                // Need to inform MainThread of the "New Guy" so that he may control its destiny from now on.
                auto_ptr<CtiCommandMsg >CmdMsg( CTIDBG_new CtiCommandMsg( CtiCommandMsg::NewClient, 15 ));

                if( CmdMsg.get() != NULL )
                {
                    CmdMsg->setConnectionHandle( (void*) sptrConMan.get() );
                    MainQueue_.putQueue( CmdMsg.release() );
                    sptrConMan->start();
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " ERROR Starting CTIDBG_new connection! " << rwThreadId() << endl;
                }
            }

            validateConnections();
        }
    }
    catch( RWxmsg& msg )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl <<  " ConnThread: Failed... " << msg.why() << endl;
        }
        throw;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ConnThread: " << rwThreadId() << " is properly shutdown... " << endl;
    }

    _broken = true;
}

/**
 * remove all non-viable connections
 */
void PilServer::validateConnections()
{
    CtiServerExclusion guard(_server_exclusion);

    while( CtiServer::ptr_type CM = mConnectionTable.remove(NonViableConnection, NULL) )
    {
        {
            CtiTime Now;
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << Now << " ** INFO ** Vagrant connection detected. Removing it." << endl;
            dout << Now << "   Connection: " << CM->getClientName() << " id " << CM->getClientAppId() << " on " << CM->getPeer() << " will be removed" << endl;
        }

        clientShutdown(CM);
    }
}

void PilServer::copyReturnMessageToResponseMonitorQueue(const CtiReturnMsg &returnMsg, void *connectionHandle)
{
    using namespace Cti::Messaging;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    StreamableMessage::auto_type msg(new PorterResponseMessage(returnMsg, connectionHandle));

    ActiveMQConnectionManager::enqueueMessage(OutboundQueue::PorterResponses, msg);
}



struct collectRfnResultDeviceIds
{
    std::set<long> &c;

    collectRfnResultDeviceIds(std::set<long> &c_) : c(c_)  { };

    void operator()(const RfnDeviceResult &result)  {  c.insert(result.request.deviceId);  };
};


void PilServer::resultThread()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ResThread      : Started as TID " << rwThreadId() << endl;
    }

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        try
        {
            const unsigned int inQueueBlockSize = 255;
            const unsigned int inQueueMaxWait   = 500;  //  500 ms

            unsigned long start = GetTickCount(), elapsed;

            typedef boost::ptr_deque<INMESS> InMessQueue_t;
            InMessQueue_t pendingInMessages;

            while( pendingInMessages.size() < inQueueBlockSize && (elapsed = (GetTickCount() - start)) < inQueueMaxWait )
            {
                if( INMESS *im = _inQueue.getQueue(inQueueMaxWait - elapsed) )
                {
                    pendingInMessages.push_back(im);
                }
            }

            typedef boost::ptr_deque<RfnDeviceResult> RfnResultQueue_t;
            RfnResultQueue_t pendingRfnResultQueue =
                    _rfnManager.getResults(inQueueBlockSize);

            set<long> paoids;

            for_each(pendingInMessages.begin(),
                     pendingInMessages.end(),
                     collect_inmess_target_device(paoids));

            for_each(pendingRfnResultQueue.begin(),
                     pendingRfnResultQueue.end(),
                     collectRfnResultDeviceIds(paoids));

            if( ! paoids.empty() )
            {
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

            while( !bServerClosing && !pendingInMessages.empty() )
            {
                InMessQueue_t::auto_type InMessage = pendingInMessages.pop_front();

                handleInMessageResult(*InMessage);
            }

            while( !bServerClosing && !pendingRfnResultQueue.empty() )
            {
                RfnResultQueue_t::auto_type rfnResult = pendingRfnResultQueue.pop_front();

                handleRfnDeviceResult(*rfnResult);
            }
        }
        catch(...)
        {
            Sleep(5000);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " ****  EXCEPTION: PIL resultThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  - Will attempt to recover" << endl;
        }

    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ResThread : " << rwThreadId() << " terminating " << endl;
    }

    _broken = true;
}


struct InMessageResultProcessor : Devices::DeviceHandler
{
    CtiDeviceBase::CtiMessageList &vgList;
    CtiDeviceBase::CtiMessageList &retList;
    CtiDeviceBase::OutMessageList outList;
    const INMESS &im;

    InMessageResultProcessor(const INMESS &im_, CtiDeviceBase::CtiMessageList &vgList_, CtiDeviceBase::CtiMessageList &retList_) :
        im(im_),
        vgList(vgList_),
        retList(retList_)
    {
    }

    YukonError_t execute(CtiDeviceBase &dev)
    {
        return dev.ProcessResult(im, CtiTime::now(), vgList, retList, outList);
    }

    YukonError_t execute(Devices::RfnDevice &dev)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " InMessageResultProcessor called on RFN device:" << endl;
        dout << dev.getName() << " / " << dev.getID() << endl;

        return ClientErrors::NoMethod;
    }
};


void PilServer::handleInMessageResult(const INMESS &InMessage)
{
    LONG id = InMessage.TargetID;

    // Checking the sequence since we will actually want the system device 0 for the Phase Detect cases
    if(id == 0 && !(InMessage.Sequence == Cti::Protocols::EmetconProtocol::PutConfig_PhaseDetectClear ||
                    InMessage.Sequence == Cti::Protocols::EmetconProtocol::PutConfig_PhaseDetect))
    {
        id = InMessage.DeviceID;
    }

    CtiDeviceBase::CtiMessageList vgList;
    CtiDeviceBase::CtiMessageList retList;

    // Find the device..
    CtiDeviceSPtr DeviceRecord = DeviceManager->getDeviceByID(id);

    if( ! DeviceRecord )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "InMessage received from unknown device.  Device ID: " << InMessage.DeviceID << endl;
            dout << " Port listed as                                   : " << InMessage.Port     << endl;
            dout << " Remote listed as                                 : " << InMessage.Remote   << endl;
        }

        std::auto_ptr<CtiReturnMsg> idnf_msg(
            new CtiReturnMsg(
                    InMessage.DeviceID,
                    InMessage.Return.CommandStr,
                    "Device unknown, unselected, or DB corrupt. ID = " + CtiNumStr(InMessage.DeviceID),
                    ClientErrors::IdNotFound,
                    InMessage.Return.RouteID,
                    InMessage.Return.RetryMacroOffset,
                    InMessage.Return.Attempt,
                    InMessage.Return.GrpMsgID,
                    InMessage.Return.UserID,
                    InMessage.Return.SOE));

        retList.push_back(idnf_msg.release());
    }
    else
    {
        if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Pilserver resultThread received an InMessage for " << DeviceRecord->getName();
            dout << " at priority " << InMessage.Priority << endl;
        }

        InMessageResultProcessor imrp(InMessage, vgList, retList);

        try
        {
            // Do some device dependant work on this Inbound message!
            DeviceRecord->invokeDeviceHandler(imrp);
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << " Process Result FAILED " << DeviceRecord->getName() << endl;
            }
        }

        for each( OUTMESS *OutMessage in imrp.outList )
        {
            OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
            _porterOMQueue.putQueue(OutMessage);
        }
        imrp.outList.clear();
    }

    if( retList.size() > 0 )
    {
        if((DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD) && vgList.size())
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Info **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "   Device " << (DeviceRecord ? DeviceRecord->getName() : "UNKNOWN") << " has generated a dispatch return message.  Data may be duplicated." << endl;
        }

        string cmdstr(InMessage.Return.CommandStr);
        CtiCommandParser parse( cmdstr );
        if(parse.getFlags() & CMD_FLAG_UPDATE)
        {
            for each( CtiMessage *pMsg in retList )
            {
                if(InMessage.Priority > 0)
                {
                    pMsg->setMessagePriority(InMessage.Priority);
                }

                if(pMsg->isA() == MSG_PCRETURN || pMsg->isA() == MSG_POINTDATA)
                {
                    vgList.push_back(pMsg->replicateMessage());
                }
            }
        }
    }

    sendResults(vgList, retList, InMessage.Priority, InMessage.Return.Connection);
}


struct RfnDeviceResultProcessor : Devices::DeviceHandler
{
    CtiDeviceBase::CtiMessageList &vgList;
    CtiDeviceBase::CtiMessageList &retList;
    const RfnDeviceResult &result;

    RfnDeviceResultProcessor(const RfnDeviceResult &result_, CtiDeviceBase::CtiMessageList &vgList_, CtiDeviceBase::CtiMessageList &retList_) :
        result(result_),
        vgList(vgList_),
        retList(retList_)
    {
    }

    YukonError_t execute(CtiDeviceBase &dev)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " RfnDeviceResultProcessor called on non-RFN device:" << endl;
        dout << dev.getName() << " / " << dev.getID() << endl;

        return ClientErrors::NoMethod;
    }

    YukonError_t execute(Devices::RfnDevice &dev)
    {
        std::auto_ptr<CtiReturnMsg> retMsg(
                new CtiReturnMsg(
                        result.request.deviceId,
                        result.request.commandString,
                        result.commandResult.description,
                        result.status,
                        0,
                        MacroOffset::none,
                        0,
                        result.request.groupMessageId,
                        result.request.userMessageId));

        std::ostringstream pointDataDescription;

        for each( const Devices::Commands::DeviceCommand::point_data &pd in result.commandResult.points )
        {
            pointDataDescription << "\n";

            if( const CtiPointSPtr p = dev.getDevicePointOffsetTypeEqual(pd.offset, pd.type) )
            {
                std::auto_ptr<CtiPointDataMsg> pdMsg(
                        new CtiPointDataMsg(
                                p->getID(),
                                pd.value,
                                pd.quality,
                                p->getType(),
                                pd.description,
                                pd.tags));

                pdMsg->setTime(pd.time);

                retMsg->PointData().push_back(pdMsg.release());

                pointDataDescription << p->getName();
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Point not found for device " << dev.getName() << " / " << dev.getID()
                            << ": " << desolvePointType(pd.type) + " " + CtiNumStr(pd.offset) << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                pointDataDescription << "[Unknown]";
            }

            pointDataDescription << " - " << desolvePointType(pd.type) << " " << pd.offset << ": " << pd.value << " @ " << pd.time;
        }

        retMsg->setResultString(retMsg->ResultString() + pointDataDescription.str());

        vgList.push_back(retMsg->replicateMessage());
        retList.push_back(retMsg.release());

        if( ! result.status )
        {
            dev.extractCommandResult(*result.request.command);
        }

        dev.decrementGroupMessageCount(result.request.userMessageId, reinterpret_cast<long>(result.request.connectionHandle));

        if( dev.getGroupMessageCount(result.request.userMessageId, reinterpret_cast<long>(result.request.connectionHandle)) )
        {
            retMsg->setExpectMore(true);
        }

        return result.status;
    }
};


void PilServer::handleRfnDeviceResult(const RfnDeviceResult &result)
{
    // Find the device..
    CtiDeviceSPtr DeviceRecord = DeviceManager->getDeviceByID(result.request.deviceId);

    CtiDeviceBase::CtiMessageList vgList;
    CtiDeviceBase::CtiMessageList retList;

    if( ! DeviceRecord )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "RFN result received from unknown device." << endl;
            dout << "    Device ID: "     << result.request.deviceId << endl;
            dout << "    Manufacturer : " << result.request.rfnIdentifier.manufacturer << endl;
            dout << "    Model  : "       << result.request.rfnIdentifier.model << endl;
            dout << "    Serial : "       << result.request.rfnIdentifier.serialNumber << endl;
        }

        std::auto_ptr<CtiReturnMsg> idnf_msg(
                new CtiReturnMsg(
                        result.request.deviceId,
                        result.request.commandString,
                        "Device lookup failed. ID = " + CtiNumStr(result.request.deviceId),
                        ClientErrors::IdNotFound));

        idnf_msg->setGroupMessageId(result.request.groupMessageId);
        idnf_msg->setUserMessageId (result.request.userMessageId);

        retList.push_back(idnf_msg.release());
    }
    else
    {
        if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Pilserver resultThread received an RfnDeviceResult for " << DeviceRecord->getName();
            dout << " at priority " << result.request.priority << endl;
        }

        try
        {
            RfnDeviceResultProcessor rp(result, vgList, retList);

            DeviceRecord->invokeDeviceHandler(rp);
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

    sendResults(vgList, retList, result.request.priority, result.request.connectionHandle);
}


void PilServer::sendResults(CtiDeviceBase::CtiMessageList &vgList, CtiDeviceBase::CtiMessageList &retList, const int priority, void *connectionHandle)
{
    try
    {
        for each( CtiMessage *pRet in retList )
        {
            if( priority > 0)
            {
                pRet->setMessagePriority(priority);
            }

            if( pRet->isA() == MSG_PCREQUEST )
            {
                _schedulerQueue.putQueue(pRet);
            }
            else if( CtiConnection  *Conn = static_cast<CtiConnection *>(connectionHandle) )
            {
                if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
                {
                    pRet->dump();
                }

                if( pRet->isA() == MSG_PCRETURN )
                {
                    //  Note that this excludes ReturnMsgs that are sent on execute, such as "... commands sent on route"

                    copyReturnMessageToResponseMonitorQueue(*(static_cast<CtiReturnMsg *>(pRet)), connectionHandle);
                }

                //  This sends all ReturnMsgs to the connectionHandle, overriding the ReturnMsg->ConnectionHandle (if set).
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
        retList.clear();

        for each( CtiMessage *vgMsg in vgList )
        {
            VanGoghConnection.WriteConnQue(vgMsg);
        }
        vgList.clear();
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void PilServer::nexusThread()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusThread    : Started as TID " << rwThreadId() << endl;
    }

    SetThreadName(-1, "PILNexus ");

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        std::auto_ptr<INMESS> InMessage(new INMESS); // INMESS set to zero in the constructor

        try
        {
            if( PilToPorter.read(InMessage.get(), sizeof(INMESS), Chrono::infinite, &serverClosingEvent) == sizeof(INMESS) )
            {
                // Enqueue the INMESS into the appropriate list
                _inQueue.putQueue(InMessage.release());
            }
        }
        catch( const StreamConnectionException &ex )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** ERROR **** NexusThread : " << rwThreadId() << " just failed to read a full InMessage : " << ex.what() << endl;
            }

            Sleep(500); // prevent run-away loop
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
            // throw;
        }

    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusThread : " << rwThreadId() << " terminating " << endl;
    }

    _broken = true;
}

void PilServer::nexusWriteThread()
{
    /* Time variable for decode */
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusWriteThread    : Started as TID " << rwThreadId() << endl;
    }

    SetThreadName(-1, "PILNxsWrt");

    /* perform the wait loop forever */
    for( ; ! bServerClosing ; )
    {
        boost::scoped_ptr<OUTMESS> OutMessage(_porterOMQueue.getQueue(1000));

        if( OutMessage )
        {
            try
            {
                PilToPorter.write(OutMessage.get(), sizeof(OUTMESS), Chrono::milliseconds(0));
            }
            catch( const StreamConnectionException &ex )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ERROR **** NexusWriteThread : " << rwThreadId() << " just failed to write OutMessage : " << ex.what() << endl;
                }
                DumpOutMessage(OutMessage.get());

                Sleep(500); // prevent run-away loop
            }
        }

        try
        {
            rwServiceCancellation();
        }
        catch(const RWCancellation& cMsg)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " NexusWriteThread : " << rwThreadId() << " " <<  cMsg.why() << endl;
            bServerClosing = TRUE;
            // throw;
        }
    } /* End of for */

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " NexusWriteThread : " << rwThreadId() << " terminating " << endl;
    }

}

struct RequestExecuter : Devices::DeviceHandler
{
    CtiRequestMsg *pReq;
    CtiCommandParser &parse;

    CtiDeviceBase::OutMessageList outList;
    std::vector<RfnDeviceRequest> rfnRequests;
    CtiDeviceBase::CtiMessageList vgList;
    CtiDeviceBase::CtiMessageList retList;

    RequestExecuter(CtiRequestMsg *pReq_, CtiCommandParser &parse_) :
        pReq (pReq_),
        parse(parse_)
    {}

    YukonError_t execute(CtiDeviceBase &dev)
    {
        return dev.beginExecuteRequest(pReq, parse, vgList, retList, outList);
    }

    virtual YukonError_t execute(Devices::RfnDevice &dev)
    {
        Devices::RfnDevice::RfnCommandList commands;

        Devices::RfnDevice::ReturnMsgList returnMsgList;

        const YukonError_t retVal = dev.ExecuteRequest(pReq, parse, returnMsgList, commands);

        RfnDeviceRequest req;

        req.deviceId         = dev.getID();
        req.rfnIdentifier    = dev.getRfnIdentifier();
        req.commandString    = pReq->CommandString();
        req.priority         = pReq->getMessagePriority();
        req.groupMessageId   = pReq->GroupMessageId();
        req.userMessageId    = pReq->UserMessageId();
        req.connectionHandle = pReq->getConnectionHandle();

        while( ! returnMsgList.empty() )
        {
            retList.push_back( returnMsgList.pop_front().release() );
        }

        for each( const Devices::Commands::RfnCommandSPtr &command in commands )
        {
            req.command = command;

            rfnRequests.push_back(req);
        }

        return retVal;
    }
};


YukonError_t PilServer::executeRequest(const CtiRequestMsg *pReq)
{
    if( pReq->UserMessageId() != _currentUserMessageId ||
        !_currentParse.isEqual(pReq->CommandString()))
    {
        _currentParse = CtiCommandParser(pReq->CommandString());
        _currentUserMessageId = pReq->UserMessageId();
    }

    static const string str_system_message = "system_message";
    if( !pReq->DeviceId() && _currentParse.isKeyValid(str_system_message) )
    {
        //This message is a system request for porter, send it to the porter system thread, not a device.
        std::auto_ptr<CtiRequestMsg> tempReqMsg(
           static_cast<CtiRequestMsg *>(pReq->replicateMessage()));

        tempReqMsg->setConnectionHandle(pReq->getConnectionHandle());

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

            _rfnManager.cancelByGroupMessageId(group_message_id);
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

            tempReqMsg->setOptionsField(unProcessedCount);
        }

        PorterSystemMessageQueue.putQueue(tempReqMsg.release());

        return ClientErrors::None;
    }

    YukonError_t status = ClientErrors::None;

    list< CtiMessage* >  vgList;
    list< CtiMessage* >  retList;
    list< OUTMESS* >     outList;

    list< CtiRequestMsg* >  execList;

    try
    {
        boost::ptr_deque<CtiRequestMsg> groupRequests;

        // Note that any and all arguments into this method may be altered on exit!
        analyzeWhiteRabbits(*pReq, _currentParse, execList, groupRequests, retList);

        while( ! groupRequests.empty() )
        {
            _groupQueue.insert(groupRequests.pop_front().release());
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
        for each( CtiRequestMsg *pExecReq in execList )
        {
            if( CtiDeviceSPtr pDev = DeviceManager->getDeviceByID(pExecReq->DeviceId()) )
            {
                CtiDeviceBase &Dev = *pDev;

                if( !_currentParse.isEqual(pExecReq->CommandString()) )
                {
                    // They did not match!  We MUST re-parse!
                    _currentParse = CtiCommandParser(pExecReq->CommandString());
                }

                pExecReq->setMacroOffset( Dev.selectInitialMacroRouteOffset(pReq->RouteId() != 0 ? pReq->RouteId() : Dev.getRouteID()) );

                /*
                 *  We will execute based upon the data in the request....
                 */

                if(!pExecReq->getSOE())     // We should attach one if one is not already set...
                {
                    pExecReq->setSOE( SystemLogIdGen() );  // Get us a new number to deal with
                }

                RequestExecuter executer(pExecReq, _currentParse);

                if(Dev.isGroup())                          // We must indicate any group which is protocol/heirarchy controlled!
                {
                    indicateControlOnSubGroups(Dev, _currentParse, executer.vgList, executer.retList);
                }

                try
                {
                    status = Dev.invokeDeviceHandler(executer);

                    reportClientRequests(Dev, _currentParse, pReq->getUser(), executer.vgList, executer.retList);
                }
                catch(...)
                {
                    {
                        CtiTime NowTime;
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << NowTime << " ExecuteRequest FAILED for \"" << Dev.getName() << "\"" << endl;
                        dout << NowTime << "   Command: " << pExecReq->CommandString() << endl;
                    }
                }

                outList.splice(outList.end(), executer.outList);

                _rfnRequestId = _rfnManager.submitRequests(executer.rfnRequests, _rfnRequestId);

                for each( CtiMessage *msg in executer.retList )
                {
                    //  smuggle any request messages back out to the scheduler queue
                    if( msg && msg->isA() == MSG_PCREQUEST )
                    {
                        _schedulerQueue.putQueue(msg);
                    }
                    else
                    {
                        retList.push_back(msg);
                    }
                }
                executer.retList.clear();

                vgList.splice(vgList.end(), executer.vgList);

                if(status && status != ClientErrors::DeviceInhibited)
                {
                    CtiTime NowTime;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " **** Execute Error **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << NowTime << "   Device:  " << Dev.getName() << endl;
                    dout << NowTime << "   Command: " << pExecReq->CommandString() << endl;
                    dout << NowTime << "   Status = " << status << ": " << GetErrorString(status) << endl;
                }

                status = ClientErrors::None;
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Device unknown, unselected, or DB corrupt " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << CtiTime() << " Command " << pExecReq->CommandString() << endl;
                    dout << CtiTime() << " Device: " << pExecReq->DeviceId() << endl;
                }

                if( CtiServer::ptr_type ptr = findConnectionManager((long)pExecReq->getConnectionHandle()) )
                {
                    CtiPILConnectionManager *CM = (CtiPILConnectionManager *)ptr.get();
                    CtiReturnMsg *pcRet = CTIDBG_new CtiReturnMsg(pExecReq->DeviceId(),
                                                                  pExecReq->CommandString(),
                                                                  "Device unknown, unselected, or DB corrupt. ID = " + CtiNumStr(pExecReq->DeviceId()),
                                                                  ClientErrors::IdNotFound,
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
        }
        execList.clear();
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

    for each( CtiReturnMsg *pcRet in retList )
    {
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
    retList.clear();

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Submitting " << outList.size() << " CtiOutMessage objects to porter" << endl;
        }
    }

    for each( OUTMESS *OutMessage in outList )
    {
        _porterOMQueue.putQueue(OutMessage);
    }
    outList.clear();

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Submitting " << vgList.size() << " CtiMessage objects to dispatch" << endl;
        }
    }

    for each( CtiMessage *pVg in vgList )
    {
        VanGoghConnection.WriteConnQue(pVg);
    }
    vgList.clear();

    return status;
}

YukonError_t PilServer::executeMulti(const CtiMultiMsg *pMulti)
{
    YukonError_t status = ClientErrors::None;

    CtiMessage *pMyMsg = NULL;

    if(pMulti != NULL)
    {
        CtiMultiMsg_vec::const_iterator itr = pMulti->getData().begin();
        //RWOrderedIterator itr( pMulti->getData() );

        for(;itr != pMulti->getData().end(); itr++)
        {
            if((pMyMsg = (CtiMessage*) *itr) != NULL)
            {
                switch( pMyMsg->isA() )
                {
                case MSG_PCREQUEST:
                    {
                        const CtiRequestMsg *pReq = static_cast<const CtiRequestMsg *>(pMyMsg);
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

void PilServer::clientShutdown(CtiServer::ptr_type CM)
{
//#ifdef DEBUG_SHUTDOWN
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Now shutting down (ClientAppShutdown) in PIL Server" << endl;
    }
//#endif

    Inherited::clientShutdown(CM);
}

void PilServer::shutdown()
{
    bServerClosing = TRUE;
    SetEvent(serverClosingEvent);
    MainThread_.requestCancellation(5000);
}


void PilServer::vgConnThread()
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
    VanGoghConnection.close();

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


void PilServer::schedulerThread()
{
    std::multiset<CtiMessage *, message_time_less> message_queue;

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

vector<long> PilServer::getDeviceGroupMembers( string groupname ) const
{
    vector<long> paoids;

    //  ensure the group name starts with '/'
    if( ! groupname.empty() && groupname[0] == '/' )
    {
        vector<string> groupSegments;

        boost::split(groupSegments, groupname, boost::is_any_of("/"));
        groupSegments[0] = ' ';  //  the first group name is actually a single space

        std::ostringstream sql;

        sql << "SELECT DGM.yukonpaoid FROM DeviceGroupMember DGM";

        string childTableAlias  = "DGM";
        string childColumn      = "devicegroupid";

        //  Join to each table in the group heirarchy
        for( int i = groupSegments.size() - 1; i >= 0; i-- )
        {
            string parentTableAlias = "DG" + CtiNumStr(i);

            sql << " JOIN DeviceGroup " << parentTableAlias << " ON " << parentTableAlias << ".devicegroupid = " << childTableAlias << "." << childColumn;

            childTableAlias = parentTableAlias;
            childColumn = "parentdevicegroupid";
        }

        //  Anchor the root group
        sql << " WHERE DG0.parentdevicegroupid IS NULL";

        //  Add the group name checks
        for( int i = 0; i < groupSegments.size(); i++ )
        {
            sql  << " AND lower(DG" << i << ".groupname) = ?";
        }

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql.str());

        //  Fill in the parameters
        for each( const string &groupSegment in groupSegments )
        {
            rdr << groupSegment;
        }

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
            rdr[0] >> *(paoids.insert(paoids.end(), 0));
        }
    }

    return paoids;
}


void PilServer::analyzeWhiteRabbits(const CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, boost::ptr_deque<CtiRequestMsg> &groupRequests, list< CtiMessage* > & retList)
{
    std::auto_ptr<CtiRequestMsg> pReq(static_cast<CtiRequestMsg *>(Req.replicateMessage()));
    pReq->setConnectionHandle( Req.getConnectionHandle() );

    if( parse.isKeyValid("serial") )
    {
        pReq->setDeviceId( SYS_DID_SYSTEM );    // Make sure we are targeting the serial/system device;
    }

    // Can you say WHITE RABBIT?  This could override the above!
    // This code will not execute in most cases
    if( parse.isKeyValid("device") )
    {
        const int         deviceId   = parse.getiValue("device");
        const std::string deviceName = parse.getsValue("device");

        if( deviceId != -1 )
        {
            // OK, someone tried to send us an override on the device ID
            pReq->setDeviceId(deviceId);
        }
        else if( const CtiDeviceSPtr Dev = DeviceManager->RemoteGetEqualbyName(deviceName) )
        {
            pReq->setDeviceId(Dev->getID());
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Execute Error **** " << endl;
                dout << CtiTime() << " Device Name: " << deviceName << endl;
                dout << CtiTime() << " Command: " << pReq->CommandString() << endl;
                dout << CtiTime() << " No device found in the database with this device name." << endl;
            }

            retList.push_back(
                new CtiReturnMsg(
                        pReq->DeviceId(),
                        pReq->CommandString(),
                        "No device with name '" + deviceName + "' exists in the database.",
                        ClientErrors::IdNotFound,
                        pReq->RouteId(),
                        pReq->MacroOffset(),
                        pReq->AttemptNum(),
                        pReq->GroupMessageId(),
                        pReq->UserMessageId(),
                        pReq->getSOE()));

            return;
        }
    }

    if( parse.isKeyValid("route") )
    {
        const int         routeId   = parse.getiValue("route");
        const std::string routeName = parse.getsValue("route");

        if( routeId != -1 )
        {
            // OK, someone tried to send us an override on the route ID
            pReq->setRouteId(routeId);
        }
        else if( const CtiRouteSPtr Rte = RouteManager->getRouteByName(routeName) )
        {
            pReq->setRouteId( Rte->getRouteID() );
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    if( ! pReq->DeviceId() )
    {
        typedef std::pair<std::string, std::string> GroupKeyAndPrefix;

        //  group keys and prefixes, in the order that we look them up
         std::vector<GroupKeyAndPrefix> ordered_group_parse_keys = boost::assign::map_list_of
            ("group",     "/Meters/Collection/")
            ("altgroup",  "/Meters/Alternate/")
            ("billgroup", "/Meters/Billing/");

         std::string group_key, group_prefix;

        //  look for a group key we know about
        for each( const GroupKeyAndPrefix group_info in ordered_group_parse_keys )
        {
            boost::tie(group_key, group_prefix) = group_info;

            if( parse.isKeyValid(group_key) )
            {
                string group_name = parse.getsValue(group_key);

                //  if it's not a new-style group, convert it
                if( group_name.find_first_of('/') == string::npos )
                {
                    group_name = group_prefix + group_name;
                }

                boost::to_lower(group_name);

                //  the parser eliminates only the parse keywords that have already been acted on (select device, route)
                pReq->setCommandString(parse.getCommandStr());

                const std::vector<long> deviceGroupMemberIds = getDeviceGroupMembers(group_name);

                for each( const long deviceid in deviceGroupMemberIds )
                {
                    //   Note that we're going to let PIL fail us on a failed device lookup to save us the device lookup here
                    pReq->setDeviceId(deviceid);

                    // Create a message for this one!
                    std::auto_ptr<CtiRequestMsg> pNew(static_cast<CtiRequestMsg *>(pReq->replicateMessage()));
                    pNew->setConnectionHandle(pReq->getConnectionHandle());

                    //  put it on the group queue to be processed in order
                    groupRequests.push_back(pNew);
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << group_name << " found " << deviceGroupMemberIds.size() << " target devices." << endl;
                }

                if( deviceGroupMemberIds.empty() )
                {
                    retList.push_back(
                        new CtiReturnMsg(
                           pReq->DeviceId(),
                            pReq->CommandString(),
                            "Group '" + group_name + "' found no target devices.",
                            ClientErrors::IdNotFound,
                            pReq->RouteId(),
                            pReq->MacroOffset(),
                            pReq->AttemptNum(),
                            pReq->GroupMessageId(),
                            pReq->UserMessageId(),
                            pReq->getSOE()));
                }

                return;
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

                CtiToLower(groupname);

                for each( const long deviceid in getDeviceGroupMembers(groupname) )
                {
                    if( CtiDeviceSPtr device = DeviceManager->getDeviceByID(deviceid) )
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
            }
        }
    }

    if( execList.empty() && pReq.get())
    {
        execList.push_back( pReq.release() );
    }
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

INT PilServer::analyzeAutoRole(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList)
{
    INT status = ClientErrors::None;
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


INT PilServer::analyzePointGroup(CtiRequestMsg& Req, CtiCommandParser &parse, list< CtiRequestMsg* > & execList, list< CtiMessage* > & retList)
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

    return ClientErrors::None;
}

void PilServer::putQueue(CtiMessage *Msg)
{
    MainQueue_.putQueue( Msg );
}


void PilServer::indicateControlOnSubGroups(CtiDeviceBase &Dev, CtiCommandParser &parse, list< CtiMessage* > &vgList, list< CtiMessage* > &retList)
{
    try
    {
        if(findStringIgnoreCase(gConfigParms.getValueAsString("PIL_IDENTIFY_SUBGROUP_CONTROLS"), "true") &&
           parse.getCommand() == ControlRequest)
        {
            if(Dev.getType() == TYPE_MACRO)
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
                    DeviceManager->select(findRestoreDeviceGroupControl, (void*)(&Dev), match_coll);
                }
                else
                {
                    DeviceManager->select(findShedDeviceGroupControl, (void*)(&Dev), match_coll);
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
int PilServer::reportClientRequests(const CtiDeviceBase &Dev, const CtiCommandParser &parse, const string &requestingUser, list< CtiMessage* > &vgList, list< CtiMessage* > &retList)
{
    int status = ClientErrors::None;

    long pid = SYS_PID_PORTER;
    static unsigned soe = 0;
    string text, addl;

    static const string PIL_OMIT_COMMAND_LOGGING("PIL_OMIT_COMMAND_LOGGING");
    static const string PIL_LOG_UNKNOWN_USERS   ("PIL_LOG_UNKNOWN_USERS");

    if( !gConfigParms.isTrue(PIL_OMIT_COMMAND_LOGGING) )   // Set this to true if you want to skip the excessive logs from pil.
    {
        bool name_none  = !requestingUser.empty() && (ciStringEqual(requestingUser, "none") || ciStringEqual(requestingUser, "(none)"));
        bool user_valid = !requestingUser.empty() && (!name_none || gConfigParms.isTrue(PIL_LOG_UNKNOWN_USERS) );

        if( user_valid &&
            (parse.getCommand() == ControlRequest ||
             parse.getCommand() == PutConfigRequest ||
             parse.getCommand() == PutStatusRequest ||
             parse.getCommand() == PutValueRequest) )
        {

            addl = Dev.getName() + " / (" + CtiNumStr(Dev.getID()) + ")";
            text = string("Command Request: ") + parse.getCommandStr();

            // The user has requested an "outbound" field action.  We have everything we need to generate a log.
            CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(pid, ++soe, text, addl, PILLogType, SignalEvent, requestingUser );
            vgList.push_back(pSig);

            std::list< CtiMessage* >::const_iterator itr;

            for(itr = retList.begin(); itr != retList.end(); itr++)
            {
                const CtiReturnMsg *&pcRet = (const CtiReturnMsg*&)*itr;

                addl = Dev.getName() + " / (" + CtiNumStr(Dev.getID()) + "): " + pcRet->CommandString();
                if(pcRet->Status() == ClientErrors::None)
                    text = string("Success: ");
                else
                    text = string("Failed (Err ") + CtiNumStr(pcRet->Status()) + "): ";

                text += pcRet->ResultString();
                CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(pid, soe, text, addl, PILLogType, SignalEvent, requestingUser );
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




void PilServer::periodicActionThread()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PIL periodicActionThread : Started as TID " << rwThreadId() << std::endl;
    }

    SetThreadName(-1, "prdActThd");

    Cti::Timing::MillisecondTimer t;

    CtiTime nextRfDaCheck;

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        const CtiTime Now;

        const unsigned elapsed = t.elapsed();

        if( elapsed > 1000 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PIL periodicActionThread took " << elapsed << " milliseconds " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }
        else
        {
            Sleep(1000 - elapsed);
        }

        t.reset();

        {   // RDS application id

            std::vector< CtiDeviceManager::ptr_type >   rdsDevices;

            DeviceManager->getDevicesByType( TYPE_RDS, rdsDevices );

            for each ( CtiDeviceSPtr device in rdsDevices )
            {
                if ( device->timeToPerformPeriodicAction( CtiTime::now() ) )
                {
                    putQueue( new CtiRequestMsg( device->getID(), "putvalue application-id") );
                }
            }
        }

        {
            _rfnManager.tick();
        }

        if( nextRfDaCheck < Now )
        {
            nextRfDaCheck = nextScheduledTimeAlignedOnRate(Now, 1800);

            std::vector< CtiDeviceManager::ptr_type > rfDaDevices;

            DeviceManager->getDevicesByType( TYPE_RFN1200, rfDaDevices );

            for each( CtiDeviceSPtr device in rfDaDevices )
            {
                if( ! device->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress) )
                {
                    putQueue( new CtiRequestMsg( device->getID(), "getconfig dnp address") );
                }
            }
        }

        //  <Add other periodic events here>
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PIL periodicActionThread : " << rwThreadId() << " terminating " << std::endl;
    }
}

}
}

