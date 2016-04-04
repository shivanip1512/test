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

#include "debug_timer.h"
#include "millisecond_timer.h"

#include "connection_client.h"
#include "win_helper.h"
#include "desolvers.h"

#include <boost/regex.hpp>
#include <boost/bind.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/range/algorithm/count_if.hpp>
#include <boost/ptr_container/ptr_deque.hpp>

#include <iomanip>
#include <iostream>
#include <vector>

using namespace std;

extern IM_EX_CTIBASE std::string outMessageToString(const OUTMESS* Om);

CtiClientConnection   VanGoghConnection( Cti::Messaging::ActiveMQ::Queue::dispatch );
CtiPILExecutorFactory ExecFactory;

/* Define the return nexus handle */
DLLEXPORT Cti::StreamLocalConnection<OUTMESS, INMESS> PilToPorter; //Pil handles this one
DLLEXPORT CtiFIFOQueue< CtiMessage >                  PorterSystemMessageQueue;

using Cti::Timing::Chrono;

namespace Cti {
namespace Pil {

DLLEXPORT CtiFIFOQueue<CtiMessage> ClientReturnQueue;

void ReportMessagePriority( CtiMessage *MsgPtr, CtiDeviceManager *&DeviceManager );

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
    _listenerConnection( Cti::Messaging::ActiveMQ::Queue::porter ),
    _rfnRequestId(0),
    _resultThread        (WorkerThread::Function([this]{ resultThread();         }).name("_resultThread")),
    _nexusThread         (WorkerThread::Function([this]{ nexusThread();          }).name("_nexusThread")),
    _nexusWriteThread    (WorkerThread::Function([this]{ nexusWriteThread();     }).name("_nexusWriteThread")),
    _vgConnThread        (WorkerThread::Function([this]{ vgConnThread();         }).name("_vgConnThread")),
    _schedulerThread     (WorkerThread::Function([this]{ schedulerThread();      }).name("_schedulerThread")),
    _periodicActionThread(WorkerThread::Function([this]{ periodicActionThread(); }).name("_periodicActionThread"))
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

    if(!bServerClosing)
    {
        _mainThread = boost::thread(&PilServer::mainThread, this);
        _connThread = boost::thread(&PilServer::connectionThread, this);

        _resultThread    .start();
        _nexusThread     .start();
        _nexusWriteThread.start();
        _vgConnThread    .start();
        _schedulerThread .start();

        Messaging::Rfn::gE2eMessenger->start();
        _rfnManager.start();

        _periodicActionThread.start();
    }

    return 0;
}

void PilServer::mainThread()
{
    BOOL          bQuit = FALSE;
    YukonError_t  status;

    CtiMessage   *MsgPtr;
    int groupBypass = 0;

    CTILOG_INFO(dout, "PIL mainThread - Started");

    VanGoghConnection.setName("Pil to Dispatch");
    VanGoghConnection.start();
    VanGoghConnection.WriteConnQue( CTIDBG_new CtiRegistrationMsg( PIL_REGISTRATION_NAME, GetCurrentThreadId(), true ), CALLSITE );

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
                        CTILOG_INFO(dout, "PIL processing an inbound request message which is over 15 minutes old - Message will be discarded" <<
                                *MsgPtr);

                        if( auto requestingClient = findConnectionManager(MsgPtr->getConnectionHandle()) )
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

                            requestingClient->WriteConnQue( expiredRequestError.release(), CALLSITE );
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

                        CTILOG_INFO(dout, "PIL breaking out a CtiMultiMsg with " << subMessages.size() << " submessages");

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
                            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            try
            {
                boost::this_thread::interruption_point();
            }
            catch( boost::thread_interrupted & )
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

                if( ! _connThread.timed_join(boost::posix_time::seconds(10)) ) // Wait for the Conn thread to die.
                {
                    CTILOG_ERROR(dout, "PIL Server shutting down the ConnThread_: FAILED (will terminate)");
                    TerminateThread(_connThread.native_handle(), EXIT_SUCCESS);
                }

                _resultThread.interrupt();

                if( ! _resultThread.tryJoinFor(Chrono::seconds(10)) )      // Wait for the closure
                {
                    _resultThread.interrupt();  //  Try again

                    CTILOG_WARN(dout, "PIL Server shutting down the ResultThread_: TIMEOUT");

                    _resultThread.tryJoinOrTerminateFor(Chrono::seconds(1));
                }

                _nexusWriteThread.interrupt();

                if( ! _nexusWriteThread.tryJoinFor(Chrono::seconds(10)) )
                {
                    _nexusWriteThread.interrupt();  //  Try again

                    CTILOG_WARN(dout, "PIL Server shutting down the _nexusWriteThread: TIMEOUT");

                    _nexusWriteThread.tryJoinOrTerminateFor(Chrono::seconds(1));
                }

                _nexusThread.interrupt();

                if( ! _nexusThread.tryJoinFor(Chrono::seconds(10)) )       // Wait for the closure
                {
                    _nexusThread.interrupt();  //  Try again

                    CTILOG_WARN(dout, "PIL Server shutting down the _nexusThread: TIMEOUT");

                    _nexusThread.tryJoinOrTerminateFor(Chrono::seconds(1));
                }

                CTILOG_INFO(dout, "PIL Server shutdown complete");
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "PIL mainThread - FAILED (will attempt to recover)");
            Sleep(5000);
        }
    }

    _broken = true;

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
    VanGoghConnection.close();

    CTILOG_INFO(dout, "PIL mainThread - Terminating");
}

void PilServer::connectionThread()
{
    CTILOG_INFO(dout, "PIL connThread - Started");

    // main loop
    try
    {
        for(;!bServerClosing;)
        {
            if( !_listenerConnection.verifyConnection() )
            {
                CTILOG_INFO( dout, "[Re]starting listener, resetting connections" );
                mConnectionTable.clear();

                _listenerConnection.start();
            }

            if( _listenerConnection.acceptClient() )
            {
                // create new pil connection manager
                CtiServer::ptr_type sptrConMan(new CtiConnectionManager(_listenerConnection, &MainQueue_));

                // add the new client connection
                clientConnect(sptrConMan);

                // start the connection
                sptrConMan->start();
            }

            validateConnections();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "PIL connThread - FAILED");
    }

    _broken = true;

    CTILOG_INFO(dout, "PIL connThread - Terminating");
}

/**
 * remove all non-viable connections
 */
void PilServer::validateConnections()
{
    CtiServerExclusion guard(_server_exclusion);

    while( CtiServer::ptr_type CM = mConnectionTable.remove(NonViableConnection, NULL) )
    {
        CTILOG_INFO(dout, "Vagrant connection detected - Removing it"<<
                std::endl <<"Connection: "<< CM->getClientName() <<" id "<< CM->getClientAppId() <<" on "<< CM->getPeer() <<" will be removed");

        clientShutdown(CM);
    }
}

void PilServer::copyReturnMessageToResponseMonitorQueue(const CtiReturnMsg &returnMsg, const ConnectionHandle connectionHandle)
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
    CTILOG_INFO(dout, "PIL resultThread - Started");

    bool clientReturnsWaiting = false;

    /* perform the wait loop forever */
    for( ; !bServerClosing ; )
    {
        try
        {
            const unsigned int inQueueBlockSize = 255;
            const unsigned int inQueueMaxWait   = clientReturnsWaiting ? 50 : 500;  //  milliseconds

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

            WorkerThread::interruptionPoint();

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

            const size_t clientReturnBlockSize = 20;
            for( size_t i = 0; i < clientReturnBlockSize && ClientReturnQueue.size(); ++i )
            {
                if( std::unique_ptr<CtiMessage> Msg{ ClientReturnQueue.getQueue(0) } )
                {
                    if( auto conn = findConnectionManager(Msg->getConnectionHandle()) )
                    {
                        conn->WriteConnQue(Msg.release(), CALLSITE);
                    }
                }
            }
            clientReturnsWaiting = ClientReturnQueue.size();
        }
        catch( const WorkerThread::Interrupted & )
        {
            CTILOG_WARN(dout, "PIL resultThread - Thread cancellation");
            bServerClosing = true;
        }
        catch( ... )
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "PIL resultThread - FAILED (will attempt to recover)");
            Sleep(5000);
        }

    } /* End of for */

    _broken = true;

    CTILOG_INFO(dout, "PIL resultThread - Terminating");
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
        CTILOG_INFO(dout, "InMessageResultProcessor called on RFN device: "<< dev.getName() <<" / "<< dev.getID());

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
        FormattedList logItems;

        logItems.add("Device ID")        << InMessage.DeviceID;
        logItems.add("Port listed as")   << InMessage.Port;
        logItems.add("Remote listed as") << InMessage.Remote;

        CTILOG_WARN(dout, "InMessage received from unknown device" <<
                logItems);

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
            CTILOG_DEBUG(dout, "Pilserver resultThread received an InMessage for "<< DeviceRecord->getName() <<" at priority "<< InMessage.Priority);
        }

        InMessageResultProcessor imrp(InMessage, vgList, retList);

        try
        {
            // Do some device dependant work on this Inbound message!
            DeviceRecord->invokeDeviceHandler(imrp);
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Process Result FAILED "<< DeviceRecord->getName());
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
            CTILOG_DEBUG(dout, "Device " << (DeviceRecord ? DeviceRecord->getName() : "UNKNOWN") << " has generated a dispatch return message. Data may be duplicated");
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
        CTILOG_ERROR(dout, "RfnDeviceResultProcessor called on non-RFN device: "<< dev.getName() <<" / "<< dev.getID());

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
                CTILOG_ERROR(dout, "Point not found for device "<< dev.getName() <<" / "<< dev.getID() <<": "<< desolvePointType(pd.type) <<" "<< pd.offset);

                pointDataDescription << "[Unknown]";
            }

            pointDataDescription << " - " << desolvePointType(pd.type) << " " << pd.offset << ": " << pd.value << " @ " << pd.time;
        }

        retMsg->setResultString(retMsg->ResultString() + pointDataDescription.str());

        dev.decrementGroupMessageCount(result.request.userMessageId, result.request.connectionHandle);

        if( dev.getGroupMessageCount(result.request.userMessageId, result.request.connectionHandle) )
        {
            retMsg->setExpectMore(true);
        }

        vgList.push_back(retMsg->replicateMessage());
        retList.push_back(retMsg.release());

        if( ! result.status )
        {
            dev.extractCommandResult(*result.request.command);
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
        FormattedList logItems;

        logItems.add("Device ID")     << result.request.deviceId;
        logItems.add("Manufacturer")  << result.request.rfnIdentifier.manufacturer;
        logItems.add("Model")         << result.request.rfnIdentifier.model;
        logItems.add("Serial")        << result.request.rfnIdentifier.serialNumber;

        CTILOG_ERROR(dout, "RFN result received from unknown device" <<
                logItems);

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
            CTILOG_DEBUG(dout, "Pilserver resultThread received an RfnDeviceResult for "<< DeviceRecord->getName() <<" at priority "<< result.request.priority);
        }

        try
        {
            RfnDeviceResultProcessor rp(result, vgList, retList);

            DeviceRecord->invokeDeviceHandler(rp);
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Process Result FAILED "<< DeviceRecord->getName());
        }
    }

    sendResults(vgList, retList, result.request.priority, result.request.connectionHandle);
}


void PilServer::sendResults(CtiDeviceBase::CtiMessageList &vgList, CtiDeviceBase::CtiMessageList &retList, const int priority, const ConnectionHandle connectionHandle)
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
            else if( auto Conn = findConnectionManager(connectionHandle) )
            {
                if(DebugLevel & DEBUGLEVEL_PIL_RESULTTHREAD)
                {
                    CTILOG_DEBUG(dout, *pRet);
                }

                if( pRet->isA() == MSG_PCRETURN )
                {
                    //  Note that this excludes ReturnMsgs that are sent on execute, such as "... commands sent on route"

                    copyReturnMessageToResponseMonitorQueue(*(static_cast<CtiReturnMsg *>(pRet)), connectionHandle);
                }

                //  This sends all ReturnMsgs to the connectionHandle, overriding the ReturnMsg->ConnectionHandle (if set).
                Conn->WriteConnQue(pRet, CALLSITE);
            }
            else
            {
                if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
                {
                    CTILOG_DEBUG(dout, "Notice: Request message did not indicate return path. Response to client will be discarded.");
                }
                delete pRet;
            }
        }
        retList.clear();

        for each( CtiMessage *vgMsg in vgList )
        {
            VanGoghConnection.WriteConnQue(vgMsg, CALLSITE);
        }
        vgList.clear();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void PilServer::nexusThread()
{
    CTILOG_INFO(dout, "PIL nexusThread - Started");

    SetThreadName(-1, "PILNexus");

    try
    {
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
                CTILOG_EXCEPTION_ERROR(dout, ex, "PIL nexusThread - just failed to read a full InMessage");
                Sleep(500); // prevent run-away loop
            }

            WorkerThread::interruptionPoint();
        } /* End of for */
    }
    catch( const WorkerThread::Interrupted & )
    {
        CTILOG_WARN(dout, "PIL nexusThread - Thread cancellation");
        bServerClosing = TRUE;
    }

    _broken = true;

    CTILOG_INFO(dout, "PIL nexusThread - Terminating");
}

void PilServer::nexusWriteThread()
{
    /* Time variable for decode */
    CTILOG_INFO(dout, "PIL nexusWriteThread - Started");

    SetThreadName(-1, "PILNxsWrt");

    try
    {
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
                    CTILOG_EXCEPTION_ERROR(dout, ex, "PIL nexusWriteThread - just failed to write OutMessage: "<<
                            outMessageToString(OutMessage.get()));

                    Sleep(500); // prevent run-away loop
                }
            }

            WorkerThread::interruptionPoint();
        } /* End of for */
    }
    catch( const WorkerThread::Interrupted & )
    {
        CTILOG_WARN(dout, "PIL nexusWriteThread - Thread cancellation");
        bServerClosing = TRUE;
    }

    CTILOG_INFO(dout, "PIL nexusWriteThread - Terminating");
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

        if(dev.isInhibited())
        {
            retList.push_back(new CtiReturnMsg(
                dev.getID(),
                pReq->CommandString(),
                dev.getName() + string(": ") + 
                  GetErrorString(ClientErrors::DeviceInhibited),
                ClientErrors::DeviceInhibited,
                0,
                MacroOffset::none,
                0,
                pReq->GroupMessageId(),
                pReq->UserMessageId()));

            return ClientErrors::DeviceInhibited;
        }

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

        const int group_message_id = pReq->GroupMessageId();

        //  first, scrub our queue of this request
        if( _currentParse.isKeyValid("request_cancel") )
        {
            auto itr = _groupQueue.begin(),
                 end = _groupQueue.end();

            while( itr != end )
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

            _inQueue.erase_if(
                [=](const INMESS &In) {
                    return In.Return.GrpMsgID == group_message_id;
                });

            _rfnManager.cancelByGroupMessageId(group_message_id);
        }

        //  first, count the yet to be processed items
        //  This does not count items still in the MainQueue_, only group processed items.
        if( _currentParse.isKeyValid("request_count") )
        {
            auto requestMsgGroupIdMatches =
                [=](const CtiRequestMsg *msg)
                {
                    return msg->GroupMessageId() == group_message_id;
                };

            long group_id_count = 0;

            group_id_count += boost::count_if(_groupQueue, requestMsgGroupIdMatches);

            group_id_count += _rfnManager.countByGroupMessageId(group_message_id);

            tempReqMsg->setOptionsField(group_id_count);
        }

        //  now ask Porter to do the same work on the port queues
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "ExecuteRequest FAILED for \""<< Dev.getName() <<"\" - Command: " << pExecReq->CommandString());
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
                    FormattedList logItems;
                    logItems.add("Device")  << Dev.getName();
                    logItems.add("Command") << pExecReq->CommandString();
                    logItems.add("Status")  << status <<" -> "<< GetErrorString(status);

                    CTILOG_ERROR(dout, "Execute has failed"<<
                            logItems);
                }

                status = ClientErrors::None;
            }
            else
            {
                FormattedList logItems;
                logItems.add("Command")   << pExecReq->CommandString();
                logItems.add("Device ID") << pExecReq->DeviceId();

                CTILOG_ERROR(dout, "Device unknown, unselected, or DB corrupt"<<
                        logItems);

                if( CtiServer::ptr_type ptr = findConnectionManager(pExecReq->getConnectionHandle()) )
                {
                    CtiConnectionManager *CM = (CtiConnectionManager *)ptr.get();
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
                        CM->WriteConnQue(pcRet, CALLSITE);
                    }
                }
            }
        }
        execList.clear();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        CTILOG_DEBUG(dout, "Submitting "<< retList.size() <<" CtiReturnMsg objects to client");
    }

    for each( CtiReturnMsg *pcRet in retList )
    {
        //  Note that this sends all responses to the initial request message's connection -
        //    even if any other return messages were generated by another ExecuteRequest invoked
        //    from the original and have their ConnectionHandle set to 0!
        CtiServer::ptr_type ptr = findConnectionManager(pReq->getConnectionHandle());

        if(ptr)
        {
            CtiConnectionManager *CM = (CtiConnectionManager *)ptr.get();
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                CTILOG_DEBUG(dout, *pcRet);
            }

            CM->WriteConnQue(pcRet, CALLSITE);
        }
        else
        {
            if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
            {
                CTILOG_DEBUG(dout, "Notice: Request Message did not indicate return path - Response will be discarded."<<
                        endl <<"Command: "<< pcRet->CommandString());
            }

            delete pcRet;
        }
    }
    retList.clear();

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        CTILOG_DEBUG(dout, "Submitting " << outList.size() << " CtiOutMessage objects to porter");
    }

    for each( OUTMESS *OutMessage in outList )
    {
        _porterOMQueue.putQueue(OutMessage);
    }
    outList.clear();

    if(DebugLevel & DEBUGLEVEL_PIL_INTERFACE)
    {
        CTILOG_DEBUG(dout, "Submitting " << vgList.size() << " CtiMessage objects to dispatch");
    }

    for each( CtiMessage *pVg in vgList )
    {
        VanGoghConnection.WriteConnQue(pVg, CALLSITE);
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
                        CTILOG_ERROR(dout, "Received unexpected message type in Multi ("<< pMyMsg->isA() <<")");
                        break;
                    }
                }
            }
        }
    }

    return status;
}

void PilServer::clientShutdown(CtiServer::ptr_type &CM)
{
    CTILOG_INFO(dout, CM->who() << "Now shutting down");

    Inherited::clientShutdown(CM);
}

void PilServer::shutdown()
{
    bServerClosing = TRUE;
    SetEvent(serverClosingEvent);
    _mainThread.interrupt();
}


void PilServer::vgConnThread()
{
    CtiMessage *pMsg;

    CTILOG_INFO(dout, "PIL vgConnThread - Started");

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
                            CTILOG_WARN(dout, "Shutdown requests by command messages are ignored");
                            break;
                        }
                    case (CtiCommandMsg::AreYouThere):
                        {
                            VanGoghConnection.WriteConnQue(pMsg->replicateMessage(), CALLSITE); // Copy one back
                            break;
                        }
                    default:
                        {
                            CTILOG_WARN(dout, "Unhandled command message "<< Cmd->getOperation() <<" sent to Main..");
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

    VanGoghConnection.WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::ClientAppShutdown, 15), CALLSITE);
    VanGoghConnection.close();

    CTILOG_INFO(dout, "PIL vgConnThread - Terminating");
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

    CTILOG_INFO(dout, "PIL schedulerThread - Started");

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
            //  submit it to the main queue
            putQueue(*(message_queue.begin()));

            message_queue.erase(message_queue.begin());
        }
    }

    CTILOG_INFO(dout, "PIL schedulerThread - Terminating");
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

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
        }
        else if( DebugLevel & 0x00020000 )
        {
            CTILOG_DEBUG(dout, "DB read for SQL query: "<< rdr.asString());
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
            FormattedList logItems;
            logItems.add("Device Name") << deviceName;
            logItems.add("Command")     << pReq->CommandString();

            CTILOG_ERROR(dout, "No device found in the database with this device name:"<<
                    logItems);

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
            CTILOG_ERROR(dout, "Invalid route \"" << routeName << "\"");
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
         std::vector<GroupKeyAndPrefix> ordered_group_parse_keys {
             {"group",     "/Meters/Collection/"},
             {"altgroup",  "/Meters/Alternate/"},
             {"billgroup", "/Meters/Billing/"}};

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

                CTILOG_INFO(dout, group_name <<" found "<< deviceGroupMemberIds.size() <<" target devices.");

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

            CtiDeviceSPtr GrpDev = DeviceManager->RemoteGetEqualbyName( lmgroup );
            if(GrpDev)
            {
                const std::string commandStr = StreamBuffer() <<
                        "putconfig serial "<< parse.getiValue("serial") <<" "<< GrpDev->getPutConfigAssignment(modifier) <<" "<< service;

                CTILOG_INFO(dout, "Template putconfig:"<<
                        endl <<"command: "<< commandStr)

                pReq->setCommandString(commandStr);      // Make the request match our new choices
                parse = CtiCommandParser(commandStr);    // Should create a new actionItem list
            }
        }
        else if(INT_MIN != parse.getiValue("fromutility"))
        {
            /*
             *  This indicates the user wants to put the devices defined by group addressing defined in the "fromxxx"
             *  keys into the selected versacom group.
             */
            CtiDeviceGroupVersacom *GrpDev = (CtiDeviceGroupVersacom *)DeviceManager->getDeviceByID(pReq->DeviceId()).get();

            if(GrpDev != NULL)
            {
                const std::string commandStr = StreamBuffer() <<
                        pReq->CommandString() <<" "<< GrpDev->getPutConfigAssignment(modifier);

                CTILOG_INFO(dout, "Group reassign to group - "<< GrpDev->getName() <<":"<<
                        endl <<"command: "<< commandStr);

                pReq->setCommandString(commandStr);       // Make the request match our new choices
                pReq->setRouteId(GrpDev->getRouteID()); // Just on this route.
                parse = CtiCommandParser(commandStr);     // Should create a new actionItem list
            }
        }
    }
    else if(parse.getCommand() == PutStatusRequest)
    {
        if(parse.isKeyValid("freeze") && pReq->DeviceId())
        {
            boost::regex coll_grp("collection_group +((\"|')[^\"']+(\"|'))");
            std::string tmp, groupname;

            int next_freeze = parse.getiValue("freeze");

            if( !(tmp = matchRegex(parse.getCommandStr(), coll_grp)).empty() )
            {
                //  pull out the group name
                groupname = matchRegex(tmp, "(\"|')[^\"']+(\"|')");
                //  trim off the quotes
                groupname = groupname.substr(1, groupname.size() - 2);

                vector<long> members;

                //  if it's not a new-style group, convert it
                if( groupname.find_first_of('/') == string::npos )
                {
                    groupname = "/Meters/Collection/" + groupname;
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
                            Devices::MctDevice &mct = static_cast<Cti::Devices::MctDevice &>(*device);

                            next_freeze = mct.getNextFreeze();
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
            CtiRequestMsg *pCmd = (CtiRequestMsg*)MsgPtr;

            StreamBuffer logmsg;
            logmsg <<"Pilserver mainThread received a CtiRequestMsg for "<< DeviceRecord->getName() <<" at priority "<< MsgPtr->getMessagePriority();

            if( ! pCmd->CommandString().empty() )
            {
                logmsg << endl <<"Command: \""<< pCmd->CommandString() <<"\"";
            }

            CTILOG_INFO(dout, logmsg);
        }
    }
    else if(MsgPtr->isA() == MSG_MULTI)
    {
        CtiMultiMsg_vec::iterator itr = ((CtiMultiMsg*)MsgPtr)->getData().begin();
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
                CTILOG_INFO(dout, "Looking for "<< pRepeaterToRole->getName() <<" in all routes");

                RouteManager->buildRoleVector( pRepeaterToRole->getID(), Req, retList, roleVector );
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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

                CTILOG_INFO(dout, "Setting request command: "<<  newReqString);

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
                CTILOG_INFO(dout, "ACH indicateControlOnSubGroups for MACRO");
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

                    CTILOG_INFO(dout, "Protocol hierarchy match on group: " << sptr->getName());
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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


void PilServer::periodicActionThread()
{
    CTILOG_INFO(dout, "PIL periodicActionThread - Started");

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
            CTILOG_WARN(dout, "PIL periodicActionThread took " << elapsed << " milliseconds");
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
                if( ! device->isInhibited() &&
                    device->timeToPerformPeriodicAction( CtiTime::now() ) )
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
                if( ! device->isInhibited() &&
                    ! device->hasDynamicInfo(CtiTableDynamicPaoInfo::Key_RF_DA_DnpSlaveAddress) )
                {
                    putQueue( new CtiRequestMsg( device->getID(), "getconfig dnp address") );
                }
            }
        }

        //  <Add other periodic events here>
    }

    CTILOG_INFO(dout, "PIL periodicActionThread - Terminating");
}

}
}

