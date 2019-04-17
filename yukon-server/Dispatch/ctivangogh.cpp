#include "precompiled.h"

#include "ctivangogh.h"

#include "mgr_ptclients.h"
#include "dllyukon.h"

#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcrequest.h"
#include "msg_ptreg.h"
#include "msg_signal.h"
#include "msg_notif_alarm.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"

#include "tbl_dyn_ptalarming.h"

#include "database_reader.h"
#include "database_transaction.h"
#include "database_writer.h"
#include "database_util.h"
#include "database_exceptions.h"

#include "ThreadStatusKeeper.h"

#include "debug_timer.h"
#include "millisecond_timer.h"

#include "GlobalSettings.h"
#include "logManager.h"
#include "std_helper.h"
#include "win_helper.h"
#include "amq_constants.h"
#include "desolvers.h"

#include "coroutine_util.h"

#include "counter.h"
#include "ctidate.h"
#include "MessageCounter.h"
#include "ServiceMetricReporter.h"

#include <boost/tuple/tuple_comparison.hpp>
#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/algorithm/string.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/adaptor/indirected.hpp>
#include <boost/range/algorithm/for_each.hpp>
#include <boost/range/algorithm/remove_copy_if.hpp>
#include <boost/range/algorithm/stable_sort.hpp>

using namespace std;

#define DISPATCH_APPLICATION_NAME               "Dispatch Server"

#define DISPATCH_DEBUG_VERBOSE                  0x00000001
#define DISPATCH_DEBUG_CONNECTIONS              0x00000010
#define DISPATCH_DEBUG_PENDINGOPS               0x00000020
#define DISPATCH_DEBUG_REGISTRATION             0x00000040
#define DISPATCH_DEBUG_CONTROLS                 0x00000080
#define DISPATCH_DEBUG_DELAYED_UPDATE           0x00000100
#define DISPATCH_DEBUG_PERFORMANCE              0x00100000
#define DISPATCH_DEBUG_ALARMACK                 0x01000000
#define DISPATCH_DEBUG_MESSAGES                 0x02000000
#define DISPATCH_DEBUG_MSGSTOCLIENT             0x04000000
#define DISPATCH_DEBUG_MSGSFRMCLIENT            0x08000000
#define DISPATCH_DEBUG_ALARMS                   0x10000000
#define DISPATCH_DEBUG_NOTIFICATION             0x20000000

#define MAX_ARCHIVER_ENTRIES     10             // If this many entries appear, we'll do a dump
#define DUMP_RATE                30             // Otherwise, do a dump every this many seconds
#define CONFRONT_RATE            300            // Ask every client to post once per 5 minutes or be terminated
#define UPDATERTDB_RATE          1800           // Save all dirty point records once per n seconds
#define SANITY_RATE              300
#define POINT_EXPIRE_CHECK_RATE  60
#define DYNAMIC_LOAD_SIZE        256

#define INCOMING_TAG_MASK (SIGNAL_MANAGER_MASK | TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL)

#define MAX_ALARM_TRX 256

BOOL  bGCtrlC = FALSE;

using Cti::ThreadStatusKeeper;
using Cti::DeviceBaseLite;

/* Global Variables */
CtiVanGoghExecutorFactory  ExecFactory;

int CntlHistInterval = 3600;
int CntlHistPointPostInterval = 300;
int CntlStopInterval = 300;

static CtiCounter msgCounts;
static CtiCounter msgPrioritys;
static CtiCounter msgTimes;

static bool processExecutionTime(UINT ms);

unsigned gDispatchDebugLevel = 0x00000000;
unsigned gDispatchReloadRate = 86400;

/*
 *  This function detects all group control status points which are indicating control, but do not have a pending control object.
 *  It should synchronize the control state of the group with what is known about the point from the control history table.
 *
 *  It should (as of time of writing) not be used post-startup.
 */
void CtiVanGogh::groupControlStatusVerification(unsigned long pointID)
{
    if( CtiPointSPtr pPoint = PointMgr.getPoint(pointID))
    {
        if( pPoint->isStatus() && pPoint->isPseudoPoint() )
        {
            CtiPointStatusSPtr pPointStatus = boost::static_pointer_cast<CtiPointStatus>(pPoint);

            if( const boost::optional<CtiTablePointStatusControl> controlParameters = pPointStatus->getControlParameters() )
            {
                if( controlParameters->getControlOffset() == 1 &&
                    isDeviceGroupType(pPoint->getDeviceID()) )
                {
                    // This is almost certainly a pseudo control indicator that needs to be checked.
                    if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pPoint) )
                    {
                        if( (INT)(pDyn->getValue()) == CONTROLLED && pDyn->getQuality() != ManualQuality )
                        {
                            // We either need to set up a future point if control is still running, or send a point for
                            // now if control stopped while we were shut down.
                            CtiTime stopTime =  _pendingOpThread.getPendingControlCompleteTime(pPoint->getPointID());
                            if(!stopTime.isValid())
                            {
                                stopTime = stopTime.now();
                            }

                            updateGroupPseduoControlPoint( *pPoint, stopTime);
                        }
                    }
                }
            }
        }
    }
}


string AlarmTagsToString(UINT tags)
{
    string rstr(" Alarm ");

    if( (tags & TAG_ACTIVE_ALARM) && (tags & TAG_UNACKNOWLEDGED_ALARM) )
    {
        // Active and unacknowledged.. this is the toggling state
        rstr += gConfigParms.getValueAsString("DISPATCH_UNACK_ALARM_TEXT", "Unacknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_ACTIVE_ALARM_TEXT", "Condition Active") + ")";
    }
    else if( !(tags & TAG_ACTIVE_ALARM) && tags & TAG_UNACKNOWLEDGED_ALARM )
    {
        // Alarm condition is not active, but the alarm has not been acknowledged
        rstr += gConfigParms.getValueAsString("DISPATCH_UNACK_ALARM_TEXT", "Unacknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_INACTIVE_ALARM_TEXT", "Condition Inactive") + ")";
    }
    else if( (tags & TAG_ACTIVE_ALARM) && !(tags & TAG_UNACKNOWLEDGED_ALARM) )
    {
        rstr += gConfigParms.getValueAsString("DISPATCH_ACK_ALARM_TEXT", "Acknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_ACTIVE_ALARM_TEXT", "Condition Active") + ")";
    }
    else if( !(tags & TAG_ACTIVE_ALARM) && !(tags & TAG_UNACKNOWLEDGED_ALARM) )
    {
        // There is no alarm condition anymore, this report ends the sequence
        rstr += "Cleared";
    }

    return rstr;
}
string& TrimAlarmTagText(string& text)
{
    string rstr(" Alarm ");
    string temp;
    int pos;
    /* TS Original
    temp = rstr + gConfigParms.getValueAsString("DISPATCH_UNACK_ALARM_TEXT", "Unacknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_ACTIVE_ALARM_TEXT", "Condition Active") + ")";
    text.replace(temp,"");
    */

    temp = rstr + gConfigParms.getValueAsString("DISPATCH_UNACK_ALARM_TEXT", "Unacknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_ACTIVE_ALARM_TEXT", "Condition Active") + ")";
    pos = text.find(temp);
    if (pos != -1)
        text.replace(pos,temp.length(),"");

    temp = rstr + gConfigParms.getValueAsString("DISPATCH_UNACK_ALARM_TEXT", "Unacknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_INACTIVE_ALARM_TEXT", "Condition Inactive") + ")";
    pos = text.find(temp);
    if (pos != -1)
        text.replace(pos,temp.length(),"");

    temp = rstr + gConfigParms.getValueAsString("DISPATCH_ACK_ALARM_TEXT", "Acknowledged") + " (" + gConfigParms.getValueAsString("DISPATCH_ACTIVE_ALARM_TEXT", "Condition Active") + ")";
    pos = text.find(temp);
    if (pos != -1)
        text.replace(pos,temp.length(),"");

    temp = rstr + "Cleared";
    pos = text.find(temp);
    if (pos != -1)
        text.replace(pos,temp.length(),"");

    return text;
}

void ApplyBlankDeletedConnection(CtiMessage *Msg, void *ConnId);

/*
 *  This function can be used to pull out connectinos which have become bachy over time.
 */
bool NonViableConnection(CtiServer::ptr_type &CM, void* d)
{
    return !CM->isViable();
}

CtiVanGogh::~CtiVanGogh()
{
    //PointMgr.storeDirtyRecords();

    _signalMsgQueue.clearAndDestroy();
}

int CtiVanGogh::execute()
{
    _mainThread = boost::thread(&CtiVanGogh::VGMainThread, this);

    return 0;
}

void CtiVanGogh::VGMainThread()
{
    int  nRet;
    Cti::MessageCounter mc("Dispatch Incoming Message");
    ThreadStatusKeeper threadStatus("VG Main Thread");

    INPUT_RECORD      inRecord;
    HANDLE            hStdIn = GetStdHandle(STD_INPUT_HANDLE);
    DWORD             Count;
    CHAR              Char;
    /*
     *  Iterators, place pointers etc.
     */
    CtiExecutor                   *pExec;
    CtiMessage                    *MsgPtr;

    try
    {
        /*
         *  MAIN: The main Dispatch loop lives here for all time!
         */

        // Make sure the database is available before we try to load anything from it.
        {
            bool writeLogMessage = true;

            while ( ! ( bGCtrlC || canConnectToDatabase() ) )
            {
                if ( writeLogMessage )
                {
                    CTILOG_ERROR(dout, "Database connection attempt failed");

                    writeLogMessage = false;
                }
                Sleep( 5000 );
            }
            if ( bGCtrlC )
            {
                stopDispatch();
                return;
            }
        }

        _tagManager.start();

        CTILOG_INFO(dout, "Initial Database Load");
        loadRTDB(true);
        loadPendingSignals();       // Reload any signals written out at last shutdown.
        loadStalePointMaps();

        CTILOG_INFO(dout, "Reloading pending control information");
        _pendingOpThread.loadICControlMap();
        CTILOG_INFO(dout, "Done Reloading pending control information");

        CTILOG_INFO(dout, "Requesting control point state verification");
        MainQueue_.putQueue((CtiMessage *)CTIDBG_new CtiCommandMsg(CtiCommandMsg::ControlStatusVerification, 15));

        ThreadMonitor.start(CtiThreadMonitor::Dispatch);

        _pendingOpThread.setMainQueue( &MainQueue_ );
        _pendingOpThread.setSignalManager( &_signalManager );
        _pendingOpThread.start();

        _rphArchiver.start();

        _archiveThread    = boost::thread(&CtiVanGogh::VGArchiverThread,       this);
        _timedOpThread    = boost::thread(&CtiVanGogh::VGTimedOperationThread, this);
        _dbSigThread      = boost::thread(&CtiVanGogh::VGDBSignalWriterThread, this);
        _dbSigEmailThread = boost::thread(&CtiVanGogh::VGDBSignalEmailThread,  this);
        _appMonitorThread = boost::thread(&CtiVanGogh::VGAppMonitorThread,     this);

        _cacheHandlerThread1 = boost::thread(&CtiVanGogh::VGCacheHandlerThread, this, 1);
        _cacheHandlerThread2 = boost::thread(&CtiVanGogh::VGCacheHandlerThread, this, 2);
        _cacheHandlerThread3 = boost::thread(&CtiVanGogh::VGCacheHandlerThread, this, 3);

         // Prime the connection to the notification server
         getNotificationConnection();

        // all that is good and ready has been started, open up for business from clients
        _connThread = boost::thread(&CtiVanGogh::VGConnectionHandlerThread, this);

        Cti::Timing::MillisecondTimer timer;

        for(;!bGCtrlC;)
        {
            timer.reset();

            if((MsgPtr = DeferredQueue_.getQueue(0)) == NULL)
            {
                MsgPtr = MainQueue_.getQueue( 1000 );

                if(MsgPtr != NULL)
                {
                    if(checkMessageForPreLoad(MsgPtr))
                    {
                        CacheQueue_.putQueue(MsgPtr);
                        MsgPtr = NULL;
                    }
                }
            }


            DWORD dequeueTime = timer.elapsed();
            if(MsgPtr != NULL)
            {
                if(MsgPtr->isA() == MSG_SERVER_REQUEST)
                {
                    CtiServerRequestMsg *pSvrReq = (CtiServerRequestMsg*)MsgPtr;
                    MsgPtr = (CtiMessage*)pSvrReq->getPayload();
                    MsgPtr->setConnectionHandle(pSvrReq->getConnectionHandle());

                    CtiServer::ptr_type sptrCM = findConnectionManager(pSvrReq->getConnectionHandle());
                    if(sptrCM)
                    {
                        sptrCM->setRequestId(pSvrReq->getID());  // Stow this, you'll need it for a response.
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Received message for unknown handle " << pSvrReq->getConnectionHandle());
                    }
                }

                msgCounts.inc( MsgPtr->isA() );
                msgPrioritys.inc( MsgPtr->getMessagePriority() );

                switch( MsgPtr->isA() )
                {
                case MSG_PCRETURN:
                case MSG_MULTI:
                    {
                        int msgCount = ((CtiMultiMsg*)MsgPtr)->getCount();

                        mc.increment(msgCount);

                        if(msgCount > 1000)
                        {
                            CTILOG_INFO(dout, "LARGE MULTI: submessages to process "<< msgCount <<" from " << MsgPtr->getSource() <<" "<< MsgPtr->getUser());
                        }
                        break;
                    }
                default:
                    {
                        mc.increment();
                        break;
                    }
                }

                if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSFRMCLIENT)
                {
                    CTILOG_DEBUG(dout, ">>>> INCOMING"<<
                            *MsgPtr);
                }

                try
                {
                    nRet = ClientErrors::None;

                    if( MsgPtr->isValid() )
                    {
                        if((pExec = ExecFactory.getExecutor(MsgPtr)) != NULL)
                        {
                            nRet = pExec->ServerExecute(this);     // The "this" in question is the CtiVanGogh object...

                            DWORD completeTime = timer.elapsed();

                            if(processExecutionTime(completeTime - dequeueTime))
                            {
                                Cti::StreamBuffer sb;
                                sb << "The last message took more than 5 sec to process in dispatch";

                                if(pExec->getMessage())
                                {
                                    sb << endl <<"Message Type "<< pExec->getMessage()->typeString() <<" get ms = "<< dequeueTime <<"  ms = "<< (completeTime - dequeueTime);
                                }

                                CTILOG_WARN(dout, sb);
                            }

                            delete pExec;
                        }
                        else
                        {
                            delete MsgPtr;
                        }
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Message reported itself as invalid");
                        delete MsgPtr;
                    }

                    if( nRet )
                    {
                        CTILOG_ERROR(dout, "ServerExecute returned an error = " << nRet);
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }
            else if( _connThread.timed_join(boost::posix_time::milliseconds(0)) )
            {
                //  thread isn't running - restart it
                CtiServerExclusion pmguard(_server_exclusion, 10000);

                CTILOG_INFO(dout, "Restarting ConnThread");

                // all that is good and ready has been started, open up for business from clients
                _connThread = boost::thread(&CtiVanGogh::VGConnectionHandlerThread, this);
            }

            threadStatus.monitorCheck(CtiThreadRegData::None);

            Count = 0;
            if(PeekConsoleInput(hStdIn, &inRecord, 1L, &Count) && (Count > 0))     // There is something there if we succeed.
            {
                if(inRecord.EventType != KEY_EVENT)
                {
                    ReadConsoleInput(hStdIn, &inRecord, 1L, &Count);     // Read out the offending input.
                }
                else    // These are to only kind we care about!
                {
                    ReadConsoleInput(hStdIn, &inRecord, 1L, &Count);

                    if( inRecord.Event.KeyEvent.bKeyDown != FALSE )
                    {
                        if((inRecord.Event.KeyEvent.dwControlKeyState & (LEFT_ALT_PRESSED | RIGHT_ALT_PRESSED)))
                        {
                            Char = ::tolower(inRecord.Event.KeyEvent.uChar.AsciiChar);
                        }
                        else
                        {
                            Char = 0;
                        }

                        if( processInputFunction(Char) )
                        {
                            if(Char != 0)
                            {
                                fprintf(stdout, " No ALT 0x%04X, 0x%04X, 0x%02X 0x%08X\n",inRecord.Event.KeyEvent.wVirtualScanCode,inRecord.Event.KeyEvent.wVirtualKeyCode, Char, inRecord.Event.KeyEvent.dwControlKeyState);
                            }
                        }
                    }
                }
            }

            DWORD loopDuration = timer.elapsed();
            if( loopDuration > 5000)
            {
                CTILOG_INFO(dout, "Main loop duration: " << loopDuration << " ms.  MainQueue_ has " << MainQueue_.entries());
            }
        }

        CTILOG_INFO(dout, "Shutting down on CTRL-C");
        stopDispatch();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Dispatch MAIN just died");
        stopDispatch();
    }

    CTILOG_INFO(dout, "Exiting Dispatch MAIN");
}

void CtiVanGogh::VGConnectionHandlerThread()
{
    CTILOG_INFO(dout, "Dispatch Connection Handler Thread starting");

    try
    {
        // main loop
        for(;!bGCtrlC;)
        {
            if( !_listenerConnection.verifyConnection() )
            {
                shutdownAllClients();

                _listenerConnection.start();
            }

            if( _listenerConnection.acceptClient() )
            {
                // Create new vangogh connection manager
                CtiServer::ptr_type sptrConMan( CTIDBG_new CtiVanGoghConnectionManager( _listenerConnection, &MainQueue_ ));

                // add the new client connection
                clientConnect( sptrConMan );

                // Kick off the connection's communication threads.
                sptrConMan->start();

                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
                {
                    CTILOG_DEBUG(dout, "New connection established");
                }
            }

            validateConnections();

            reportOnThreads();
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "Dispatch Connection Handler Thread shutting down");
}


void CtiVanGogh::registration(CtiServer::ptr_type &pCM, const CtiPointRegistrationMsg &aReg)
{
    CTILOG_ENTRY(dout, "pCM->getConnectionId()=" << pCM->getConnectionId() << ", aReg.getFlags()=" << aReg.getFlags());

    auto& VGCM = static_cast<CtiVanGoghConnectionManager&>(*pCM);

    if( gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS )
    {
        CTILOG_DEBUG(dout, displayConnections());
    }

    try
    {
        if( gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION )
        {
            CTILOG_DEBUG(dout, "Registration: " << VGCM.getClientName());
        }

        /*
         * We need to set up our CtiVanGoghConnectionManager according to the request!
         */

        if( aReg.isRequestingAllPoints() )
        {
            VGCM.setAllPoints(TRUE);
            if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
            {
                CTILOG_DEBUG(dout, VGCM.getClientName() <<" has registered for ALL points");
            }
        }

        if( aReg.isRequestingEvents() )
        {
            VGCM.setEvent(TRUE);
            if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
            {
                CTILOG_DEBUG(dout, VGCM.getClientName() <<" has registered for EVENTS");
            }
        }

        if( aReg.isRequestingAlarms() )
        {
            VGCM.setAlarm(TRUE);
            if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
            {
                CTILOG_DEBUG(dout, VGCM.getClientName() <<" has registered for ALARMS ");
            }
        }

        validateConnections();        // Make sure nobody has disappeared on us since the last registration

        CTILOG_DEBUG(dout, "Pre Point Mgr Insert pCM->getConnectionId()=" << pCM->getConnectionId() << ", use_count=" << pCM.use_count());
        auto ptSet = PointMgr.InsertConnectionManager(pCM, aReg,
                         ((gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION) ?
                         CtiPointClientManager::DebugPrint::True :
                         CtiPointClientManager::DebugPrint::False));
        CTILOG_DEBUG(dout, "Post Point Mgr Insert pCM->getConnectionId()=" << pCM->getConnectionId() << ", use_count=" << pCM.use_count());

        if( ! (aReg.isDecliningUpload() || aReg.isAddingPoints() || aReg.isRemovingPoints()) )
        {
            postMOAUploadToConnection(VGCM, ptSet, aReg.isRequestingMoaTag());
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

// Assumes lock on _server_exclusion has been obtained.
void CtiVanGogh::commandMsgHandler(CtiCommandMsg *Cmd)
{
    switch( Cmd->getOperation() )
    {
    case (CtiCommandMsg::ClearAlarm):
        {
            CTILOG_WARN(dout, "Alarms can no longer be CtiCommandMsg::ClearAlarm");
            return;
        }
    case (CtiCommandMsg::AcknowledgeAlarm):
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMACK)
            {
                CTILOG_DEBUG(dout, "ACKNOWLEDGE RECEIVED"<<
                        *Cmd);
            }

            for(int i = 1; i + 1 < Cmd->getOpArgList().size(); i += 2)
            {
                int pid = Cmd->getOpArgList()[i];
                int alarmcondition = Cmd->getOpArgList()[i+1];

                // I know about the point...
                if( const CtiPointSPtr pPt = PointMgr.getPoint(pid) )
                {
                    acknowledgeCommandMsg(*pPt, Cmd, alarmcondition);
                }
            }

            return;
        }
    case (CtiCommandMsg::PorterConsoleInput):
        {
            writeMessageToClient(Cmd, string(PORTER_REGISTRATION_NAME));
            return;
        }
    case (CtiCommandMsg::ControlRequest):
        {
            /*
             *  This block receives a CommandMsg from a requesting client and processes it for submission to
             *  pil/porter.
             */
            if(Cmd->getOpArgList().size() < 4)
            {
                CTILOG_ERROR(dout, "Control Request did not have a valid command vector");
                return;
            }
            LONG token     = Cmd->getOpArgList()[0];
            LONG did       = Cmd->getOpArgList()[1];
            LONG pid       = Cmd->getOpArgList()[2];
            LONG rawstate  = Cmd->getOpArgList()[3];
            INT  ctrl_offset = 0;

            CtiPointSPtr pPoint;
            if(Cmd->getOpArgList().size() >= 5)
            {
                // This is a BOOL which indicates whether a ctrl offset is spec'd by pid.
                // If this is so, "did" MUST also be specified!
                ctrl_offset = Cmd->getOpArgList()[4];

                if(did == 0 || ctrl_offset == 0)
                {
                    CTILOG_WARN(dout, "CtiCommandMsg::ControlRequest sent with DeviceID = "<< did <<" and Ctrl Offset = "<< ctrl_offset);
                }
                else
                {
                    pPoint = PointMgr.getControlOffsetEqual( did, ctrl_offset );
                }
            }
            else
            {
                pPoint = PointMgr.getPoint(pid);
            }

            if( ! pPoint )
            {
                return;
            }
            CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pPoint);

            int controlTimeout = DefaultControlExpirationTime;

            if( pPoint->isStatus() )
            {
                const CtiPointStatus &ptStatus = static_cast<const CtiPointStatus &>(*pPoint);

                if( const boost::optional<CtiTablePointStatusControl> controlParameters = ptStatus.getControlParameters() )
                {
                    controlTimeout = controlParameters->getCommandTimeout();
                }
            }

            if(pDyn
               && pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE
               && !(pDyn->getDispatch().getTags() & (TAG_DISABLE_CONTROL_BY_POINT | TAG_DISABLE_CONTROL_BY_DEVICE)))
            {
                if(did == 0)      // We need to find the point's device
                {
                    did = pPoint->getDeviceID();
                }

                bool is_a_control = writeControlMessageToPIL(did, rawstate, static_cast<const CtiPointStatus &>(*pPoint), Cmd);

                CtiPointDataMsg *pPseudoValPD = 0;

                auto pendingControlRequest = std::make_unique<CtiPendingPointOperations>(pPoint->getID());
                pendingControlRequest->setType(CtiPendingPointOperations::pendingControl);
                pendingControlRequest->setControlState( CtiPendingPointOperations::controlSentToPorter );
                pendingControlRequest->setTime( Cmd->getMessageTime() );
                pendingControlRequest->setControlCompleteValue( (DOUBLE) rawstate );
                pendingControlRequest->setControlTimeout( controlTimeout );
                pendingControlRequest->setExcludeFromHistory(!isDeviceGroupType(did));

                pendingControlRequest->getControl().setPAOID( did );
                pendingControlRequest->getControl().setStartTime(CtiTime(YUKONEOT));
                pendingControlRequest->getControl().setControlDuration(0);

                string devicename= resolveDeviceName(*pPoint);

                if( pPoint->isPseudoPoint() )
                {
                    // We are going to fake a rawstate behavior here since no one else is likely to do it for us...
                    pPseudoValPD = CTIDBG_new CtiPointDataMsg(pPoint->getID(), (DOUBLE) rawstate, NormalQuality, pPoint->getType());
                    pPseudoValPD->setSource(DISPATCH_APPLICATION_NAME);
                }
                else if( is_a_control )
                {
                    const CtiTablePointAlarming alarming = PointMgr.getAlarming(*pPoint);

                    CtiSignalMsg *pFailSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), devicename + " / " + pPoint->getName() + ": Commanded Control " + ResolveStateName(pPoint->getStateGroupID(), rawstate) + " Failed", getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure), Cmd->getUser());

                    pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK));
                    if(pFailSig->getSignalCategory() > SignalEvent)
                    {
                        pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | (alarming.isAutoAcked(CtiTablePointAlarming::commandFailure) ? 0 : TAG_UNACKNOWLEDGED_ALARM));
                        pFailSig->setLogType(AlarmCategoryLogType);
                    }
                    pFailSig->setCondition(CtiTablePointAlarming::commandFailure);

                    pendingControlRequest->setSignal( pFailSig );
                }

                addToPendingSet(std::move(pendingControlRequest), Cmd->getMessageTime());

                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
                {
                    CTILOG_DEBUG(dout, devicename <<" / "<< pPoint->getName() <<" has gone CONTROL SUBMITTED. Control expires at "<< CtiTime(Cmd->getMessageTime() + controlTimeout));
                }

                CtiSignalMsg *pCRP = CTIDBG_new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), "Control " + ResolveStateName(pPoint->getStateGroupID(), rawstate) + " Sent", string(), GeneralLogType, SignalEvent, Cmd->getUser());
                pDyn->getDispatch().setTags( TAG_CONTROL_PENDING );
                pCRP->setTags(pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK);
                MainQueue_.putQueue( pCRP );
                pCRP = 0;
                if(pPseudoValPD) MainQueue_.putQueue( pPseudoValPD );
                pPseudoValPD = 0;
            }
            else
            {
                CTILOG_INFO(dout, resolveDeviceName(*pPoint) <<" / "<< pPoint->getName() <<" CONTROL SENT to port control. Control expires at "<< CtiTime(Cmd->getMessageTime() + controlTimeout));
            }
            return;
        }
    case (CtiCommandMsg::AnalogOutput):
        {
            if(Cmd->getOpArgList().size() < 2)
            {
                CTILOG_ERROR(dout, "Control Request did not have a valid command vector");
                return;
            }
            const long point_id = Cmd->getOpArgList()[0];
            const long value    = Cmd->getOpArgList()[1];

            if(const CtiPointSPtr point = PointMgr.getPoint(point_id))
            {
                if(const long device_id = point->getDeviceID())
                {
                    writeAnalogOutputMessageToPIL(device_id, point_id, value, Cmd);
                }
            }
            return;
        }
    case (CtiCommandMsg::InitiateScan):
        {
            if(Cmd->getOpArgList().size() < 1)
            {
                CTILOG_ERROR(dout, "Control Request did not have a valid command vector");
                return;
            }
            const long paoID = Cmd->getOpArgList()[0];

            CtiRequestMsg pReq(paoID, "scan integrity");
            pReq.setUser( Cmd->getUser() );
            pReq.setMessagePriority( MAXPRIORITY - 1 );

            writeMessageToClient(&pReq, string(PIL_REGISTRATION_NAME));

            CTILOG_INFO(dout, "Scan Integrity Request sent to DeviceID: " << paoID);
            return;
        }
    case (CtiCommandMsg::Ablement):
    case (CtiCommandMsg::ControlAblement):
        {
            messageDump((CtiMessage*)Cmd);

            if(Cmd->getOpArgList().size() < 4)
            {
                CTILOG_ERROR(dout, "Control Request did not have a valid command vector");
                return;
            }
            for(int i = 1; i < Cmd->getOpArgList().size(); i = i + 3 )
            {
                LONG idtype   = Cmd->getOpArgList()[i];
                LONG id       = Cmd->getOpArgList()[i+1];
                bool disable  = !((Cmd->getOpArgList()[i+2] != 0));
                int  tagmask  = 0;           // This mask represents all the bits which are to be adjusted.
                int  setmask  = 0;         // This mask represents the state of the adjusted-masked bit.. Ok, read it again.

                std::unique_ptr<CtiMultiMsg> pMulti(new CtiMultiMsg);

                pMulti->setSource(DISPATCH_APPLICATION_NAME);
                pMulti->setUser(Cmd->getUser());

                if(Cmd->getOperation() == CtiCommandMsg::Ablement)
                {
                    if(idtype == CtiCommandMsg::OP_DEVICEID)
                    {
                        tagmask = TAG_DISABLE_DEVICE_BY_DEVICE;
                        setmask |= (disable ? TAG_DISABLE_DEVICE_BY_DEVICE : 0);    // Set it, or clear it?
                    }
                    else if(idtype == CtiCommandMsg::OP_POINTID)
                    {
                        tagmask = TAG_DISABLE_POINT_BY_POINT;
                        setmask |= (disable ? TAG_DISABLE_POINT_BY_POINT : 0);      // Set it, or clear it?
                    }
                }
                else if(Cmd->getOperation() == CtiCommandMsg::ControlAblement)
                {
                    if(idtype == CtiCommandMsg::OP_DEVICEID)
                    {
                        tagmask = TAG_DISABLE_CONTROL_BY_DEVICE;
                        setmask |= (disable ? TAG_DISABLE_CONTROL_BY_DEVICE : 0);   // Set it, or clear it?
                    }
                    else if(idtype == CtiCommandMsg::OP_POINTID)
                    {
                        tagmask = TAG_DISABLE_CONTROL_BY_POINT;
                        setmask |= (disable ? TAG_DISABLE_CONTROL_BY_POINT : 0);    // Set it, or clear it?
                    }
                }

                if(idtype == CtiCommandMsg::OP_DEVICEID)
                {
                    if( auto optDevLite = findDeviceLite(id) )
                    {
                        if( ablementDevice(*optDevLite, setmask, tagmask))
                        {
                            adjustDeviceDisableTags(id, false, Cmd->getUser());    // We always have an id here.
                        }
                    }
                }
                else if(idtype == CtiCommandMsg::OP_POINTID)
                {
                    if( CtiPointSPtr pPt = PointMgr.getPoint(id) )
                    {
                        bool devicedifferent;

                        ablementPoint(*pPt, devicedifferent, setmask, tagmask, Cmd->getUser(), *pMulti);

                        if(devicedifferent)     // The device became interesting because of this change.
                        {
                            CTILOG_INFO(dout, "Device enabled/disabled change due to Command to pointid "<< id);
                        }
                    }
                }

                if(pMulti->getData().size())
                {
                    MainQueue_.putQueue(pMulti.release());
                }
            }
            return;
        }
    case (CtiCommandMsg::UpdateFailed):
        {
            processMessage( (CtiMessage*)Cmd );
            return;
        }
    case (CtiCommandMsg::AlternateScanRate):
        {
            writeMessageToScanner( Cmd );
            return;
        }
    case (CtiCommandMsg::PointTagAdjust):
        {
            if(Cmd->getOpArgList().size() < 4)
            {
                CTILOG_ERROR(dout, "Invalid PointTagAdjust vector size");
                return;
            }

            // Vector contains token, pointid, tag(s) to set.
            LONG token      = Cmd->getOpArgList()[0];
            LONG pointid    = Cmd->getOpArgList()[1];
            LONG tagstoset  = Cmd->getOpArgList()[2];
            LONG tagstoreset= Cmd->getOpArgList()[3];

            if( const CtiPointSPtr pPt = PointMgr.getPoint(pointid) )
            {
                if( CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pPt) )
                {
                    pDyn->getDispatch().setTags( tagstoset );
                    pDyn->getDispatch().resetTags( tagstoreset );
                }
            }

            return;
        }
    case (CtiCommandMsg::ResetControlHours):
        {
            // Vector contains token? ? ? ? ?
            // LONG token = Cmd->getOpArgList().at(0);

            try
            {
                for( auto kv : _deviceLites )
                {
                    const DeviceBaseLite &dLite = kv.second;

                    if( dLite.isGroup() )
                    {
                        const long deviceID = dLite.getID();

                        CTILOG_INFO(dout, dLite.getName() <<" resetting seasonal hours");

                        CtiPointSPtr pControlPoint = PointMgr.getControlOffsetEqual( deviceID, 1);     // This is the control status control point which keeps control in play.

                        if(pControlPoint)
                        {
                            auto pendingSeasonReset = std::make_unique<CtiPendingPointOperations>(pControlPoint->getID());
                            pendingSeasonReset->setType(CtiPendingPointOperations::pendingControl);                  // Must be a pendingControl Type to help us if we are currently controlling this group!
                            pendingSeasonReset->setControlState(CtiPendingPointOperations::controlSeasonalReset);    // control state clues the guts on what we are trying to do for this command.
                            pendingSeasonReset->setExcludeFromHistory(false);
                            pendingSeasonReset->setTime( Cmd->getMessageTime() );

                            pendingSeasonReset->getControl().setPAOID(deviceID);
                            pendingSeasonReset->getControl().setActiveRestore(LMAR_PERIOD_TRANSITION);
                            pendingSeasonReset->getControl().setDefaultActiveRestore(LMAR_PERIOD_TRANSITION);
                            pendingSeasonReset->getControl().setControlDuration(0);
                            pendingSeasonReset->getControl().setControlType("Season Reset");
                            pendingSeasonReset->getControl().setReductionValue(0);
                            pendingSeasonReset->getControl().setReductionRatio(0);
                            pendingSeasonReset->getControl().setStartTime(Cmd->getMessageTime());
                            pendingSeasonReset->getControl().setStopTime(Cmd->getMessageTime());
                            pendingSeasonReset->getControl().setControlCompleteTime(Cmd->getMessageTime());
                            pendingSeasonReset->getControl().setSoeTag( CtiTableLMControlHistory::getNextSOE() );

                            //verifyControlTimesValid(pendingSeasonReset);
                            addToPendingSet(std::move(pendingSeasonReset), Cmd->getMessageTime());
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
    case (CtiCommandMsg::Shutdown):
        {
            CTILOG_WARN(dout, "Received shutdown command - Shutdown requests by command messages are ignored by dispatch"
                    << *Cmd);

            // bGCtrlC = TRUE;
            return;
        }
    case (CtiCommandMsg::PointDataDebug):
        {
            Cti::FormattedList l;

            l.add("Java debug") << Cmd->getOpString();

            auto opArgs = Cmd->getOpArgList();

            if( opArgs.empty() )
            {
                l.add("Point ID list empty");
            }
            else
            {
                auto pointId = opArgs[0];

                l.add("Point ID") << pointId;

                if( auto pt = PointMgr.getCachedPoint(pointId) )
                {
                    l.add("Point name") << pt->getName();
                    l.add("Point offset") << pt->getPointOffset();
                    l.add("Point type") << desolvePointType(pt->getType());
                    l.add("Device ID") << pt->getDeviceID();

                    if( auto dyn = PointMgr.getDynamic(*pt) )
                    {
                        l.add("Dynamic timestamp") << dyn->getTimeStamp() << "." << CtiNumStr(dyn->getTimeStampMillis()).zpad(3);
                        l.add("Dynamic value") << dyn->getValue();
                        l.add("Dynamic quality") << dyn->getQuality();
                        l.add("Dynamic tags") << std::hex << dyn->getDispatch().getTags();
                        l.add("Dynamic updated") << !!dyn->getDispatch().getUpdatedFlag();
                    }
                    else
                    {
                        l.add("Point has no dynamic record");
                    }
                }
                else
                {
                    l.add("Point not in cache");
                }
            }

            CTILOG_INFO(dout, "PointDataDebug request from " << Cmd->getConnectionHandle() << l);

            return;
        }
    case (CtiCommandMsg::PointDataRequest):
        {
            try
            {
                CtiServerExclusion pmguard(_server_exclusion);
                int payload_status = CtiServerResponseMsg::OK;
                string payload_string;

                auto results = PointMgr.getCurrentPointData(Cmd->getOpArgList());

                auto pMulti = std::make_unique<CtiMultiMsg>();

                for( auto& msg : results.pointData )
                {
                    msg->setSource(DISPATCH_APPLICATION_NAME);

                    const auto pointId = msg->getId();

                    pMulti->getData().push_back(msg.release());

                    if( auto pSigMulti = _signalManager.getPointSignals(pointId) )
                    {
                        pMulti->getData().push_back(pSigMulti.release());
                    }
                }

                if( ! results.missingIds.empty() )
                {
                    payload_status = CtiServerResponseMsg::ERR;
                    payload_string =
                        results.missingIds.size() == 1
                            ? "Point id not found: "
                            : "Point ids not found: ";
                    payload_string += Cti::join(results.missingIds, ",");

                    CTILOG_WARN(dout, "PointDataRequest could not be fulfilled:" << std::endl << payload_string);
                }

                if( auto sptrCM = findConnectionManager(Cmd->getConnectionHandle()) )
                {
                    if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)
                    {
                        CTILOG_DEBUG(dout, "<<<< OUTGOING to " << sptrCM->getClientName() << " (request ID:" << sptrCM->getRequestId() << ", handle:" << Cmd->getConnectionHandle() << ")"<<
                                *pMulti);
                    }

                    sptrCM->WriteConnQue( pMulti.release(), CALLSITE, 0, payload_status, payload_string );
                }
                else
                {
                    CTILOG_ERROR(dout, "Could not find connection for connection handle "
                        << Cmd->getConnectionHandle()
                        << " for message " << Cmd->toString());
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            return;
        }
    case (CtiCommandMsg::AlarmCategoryRequest):
        {
            // Vector contains ONLY AlarmCategoryIDs that need to be sent to the client.
            try
            {
                CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

                CtiServerExclusion pmguard(_server_exclusion);
                int payload_status = CtiServerResponseMsg::OK;

                for(int i = 0; i < Cmd->getOpArgList().size(); i++ )
                {
                    unsigned alarm_category = Cmd->getOpArgList()[i];

                    CtiMultiMsg *pSigMulti = _signalManager.getCategorySignals(alarm_category);
                    if(pSigMulti)
                    {
                        pMulti->getData().push_back(pSigMulti);
                    }
                }

                CtiServer::ptr_type sptrCM = findConnectionManager(Cmd->getConnectionHandle());

                if(sptrCM && pMulti)
                {
                    sptrCM->WriteConnQue( pMulti, CALLSITE, 0, payload_status );
                }
                else
                {
                    delete pMulti;
                }

            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            return;
        }
    case (CtiCommandMsg::ControlStatusVerification):
        {
            std::vector<unsigned long> pseudoPoints = GetPseudoPointIDs();

            for(std::vector<unsigned long>::iterator iter = pseudoPoints.begin(); iter != pseudoPoints.end(); iter++)
            {
                groupControlStatusVerification(*iter);
            }
        }
    default:
        {
            return Inherited::commandMsgHandler(Cmd);
        }
    }
}

void CtiVanGogh::clientShutdown(CtiServer::ptr_type &CM)
{
    CTILOG_ENTRY(dout, CM->getConnectionId());

    CtiServerExclusion guard(_server_exclusion);

    try
    {
        // Make sure no queue entries point at this connection!
        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CTILOG_DEBUG(dout, "Marking mainQueue entries to _not_ respond to this connection as a client");
        }

        MainQueue_.apply(ApplyBlankDeletedConnection, reinterpret_cast<void*>(CM->getConnectionId()));

        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CTILOG_DEBUG(dout, "Removing point registrations for this connection");
        }

        CTILOG_INFO(dout, "Pre Point Mgr Remove pCM->getConnectionId()=" << CM->getConnectionId() << ", use_count=" << CM.use_count());
        PointMgr.removePointsFromConnectionManager( CM,
            ((gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION) ?
            CtiPointClientManager::DebugPrint::True :
            CtiPointClientManager::DebugPrint::False));
        CTILOG_INFO(dout, "Post Point Mgr Remove pCM->getConnectionId()=" << CM->getConnectionId() << ", use_count=" << CM.use_count());

        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CTILOG_DEBUG(dout, "Calling server_b clientShutdown()");
        }

        Inherited::clientShutdown(CM);

        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CTILOG_DEBUG(dout, "Dispatch clientShutdown() complete");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*
 *  Posts the Database change to each connected client.
 */
void CtiVanGogh::postDBChange(const CtiDBChangeMsg &Msg)
{
    ptr_type Mgr;
    CHAR     temp[80];

    try
    {
        if(Msg.getMessageTime().seconds() > CtiTime().seconds() ||
           CtiTime().seconds() - Msg.getMessageTime().seconds() < 900)   // Nothing older than 15 minutes!
        {
            if(Msg.getId())
            {
                _snprintf(temp, sizeof(temp) - 1, "ID %ld", Msg.getId());
            }
            else
            {
                _snprintf(temp, sizeof(temp) - 1, "ENTRY");
            }

            string desc = string(temp) + resolveDBChangeType(Msg.getTypeOfChange()) + resolveDBChanged(Msg.getDatabase());

            if(Msg.getDatabase() == ChangePAODb)
            {
                desc += " " + resolveDeviceNameByPaoId(Msg.getId());
            }
            else if(Msg.getDatabase() == ChangePointDb)
            {
                desc += " POINT " + CtiNumStr(Msg.getId());
            }

            CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(0, 0, desc, "DATABASE CHANGE");

            pSig->setUser(Msg.getUser());

            // Send the message out to every connected client.
            {
                CtiServerExclusion guard(_server_exclusion);
                CtiServer::spiterator itr;
                CtiServer::ptr_type MgrToRemove;

                for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
                {
                    Mgr = itr->second;

                    if(Mgr->WriteConnQue( Msg.replicateMessage(), CALLSITE, 2500 ))        // Send a copy of DBCHANGE on to each clients.
                    {
                        MgrToRemove = Mgr;

                        CTILOG_WARN(dout, "Connection is not viable : "<< Mgr->getClientName() <<" / "<< Mgr->getClientAppId());
                    }

                    if(((CtiVanGoghConnectionManager*)Mgr.get())->getEvent()) // If the client cares about events...
                    {
                        Mgr->WriteConnQue( pSig->replicateMessage(), CALLSITE, 2500 );    // Copy pSig out to any event registered client
                    }
                }

                if( MgrToRemove ) //This connection is invalid, get rid of it!
                {
                    clientShutdown(MgrToRemove);
                }
            }

            queueSignalToSystemLog(pSig);
            loadRTDB(true, Msg.replicateMessage());
            if(Msg.getDatabase() == ChangePointDb)
            {
                loadStalePointMaps(Msg.getId());
            }
        }
        else
        {
            CTILOG_WARN(dout, "DBCHANGE has expired.  It was sent at " << Msg.getMessageTime());
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


void CtiVanGogh::VGArchiverThread()
{
    UINT     sleepTime;
    CtiTime  TimeNow;

    CTILOG_INFO(dout, "Dispatch RTDB Archiver Thread starting");

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        ThreadStatusKeeper threadStatus("RTDB Archiver Thread");

        for(;!bGCtrlC;)
        {
            TimeNow = TimeNow.now();

            /*
             *  Step 1. Write/mark any points which need archiving and update their nextArchive times.
             */

            _rphArchiver.submitRows(PointMgr.scanForArchival(TimeNow));

            /*
             *  Step 2. identify the next nearest time we need to do an archival write.
             */

            sleepTime = PointMgr.findNextNearestArchivalTime().seconds()  - TimeNow.seconds();

            if(sleepTime > 60)
            {
                sleepTime = 60 - (TimeNow.second());    // Make them align with the minute

                if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                {
                    CTILOG_DEBUG(dout, "RTDB Archiver Thread: Archiving/Checking in " << sleepTime << " Seconds");
                }
            }

            /*
             *  Step 3. Wait that amount of time from now
             */

            for(int i = sleepTime; !bGCtrlC && sleepTime > 0; sleepTime--)
            {
                if( ShutdownOnThreadTimeout )
                {
                    threadStatus.monitorCheck(&CtiVanGogh::sendbGCtrlC);
                }
                else
                {
                    threadStatus.monitorCheck(CtiThreadRegData::None);
                }

                // This should be an WaitForSync API call.
                Sleep(1000);
            }
        }

        CTILOG_INFO(dout, "Dispatch RTDB Archiver Thread shutting down");
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Thread death");
    }

    return;
}


void CtiVanGogh::VGTimedOperationThread()
{
    UINT sanity = 0;
    CtiMultiMsg *pMulti = 0;

    ThreadStatusKeeper threadStatus("Timed Operation Thread");

    CTILOG_INFO(dout, "Dispatch Timed Operation Thread starting");

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!bGCtrlC;)
        {
            if( ShutdownOnThreadTimeout )
            {
                threadStatus.monitorCheck(&CtiVanGogh::sendbGCtrlC);
            }
            else
            {
                threadStatus.monitorCheck(CtiThreadRegData::None);
            }

            Sleep(1000);

            CtiTime start;
            purifyClientConnectionList();
            updateRuntimeDispatchTable();
            if( NULL != (pMulti = resetControlHours()) )
            {
                MainQueue_.putQueue(pMulti);
                pMulti = 0;
            }

            loadRTDB(false);                 // Refresh (if time says so) the memory objects
            CtiTime stop;

            if( stop.seconds() - start.seconds() > 5 )
            {
                CTILOG_WARN(dout, "Timed operations took "<< (stop.seconds() - start.seconds()) <<" seconds to run.");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    updateRuntimeDispatchTable(true);

    // And let'em know were A.D.
    CTILOG_INFO(dout, "Dispatch Timed Operation Thread shutting down");

    return;
}

//The thread with ID of 1 must always exist! Only this thread checks for point clearing.
void CtiVanGogh::VGCacheHandlerThread(int threadNumber)
{
    UINT sanity = 0;
    CtiMultiMsg *pMulti = 0;
    CtiTime lastPointExpireTime; //No need to do this on start up.
    CtiMessage *MsgPtr, *MsgBasePtr;
    CtiTime start, stop;
    list<CtiMessage *>       msgList;
    set<long>                ptIdList;
    CtiPointDataMsg         *pDataMsg;
    CtiCommandMsg           *pCmdMsg;
    CtiPointRegistrationMsg *pRegMsg;
    string timerOutput;
    string threadName = "Cache Handler Thread " + CtiNumStr(threadNumber);

    ThreadStatusKeeper threadStatus(threadName);

    CTILOG_INFO(dout, threadName <<" starting");

    try
    {
        for(;!bGCtrlC;)
        {
            if( ShutdownOnThreadTimeout )
            {
                threadStatus.monitorCheck(&CtiVanGogh::sendbGCtrlC);
            }
            else
            {
                threadStatus.monitorCheck(CtiThreadRegData::None);
            }

            if(threadNumber == 1 && lastPointExpireTime.seconds() < (lastPointExpireTime.now().seconds() - POINT_EXPIRE_CHECK_RATE))
            {
                PointMgr.processExpired();
                lastPointExpireTime = lastPointExpireTime.now();
            }

            MsgPtr = CacheQueue_.getQueue(2500);

            start = start.now();
            while(MsgPtr != NULL)
            {
                //This loads the point id list.
                findPreLoadPointId(MsgPtr, ptIdList);

                //We have gotten the data we need from the message, put the message on the outgoing list
                msgList.push_back(MsgPtr);
                MsgPtr = NULL;

                if(ptIdList.size() < DYNAMIC_LOAD_SIZE) //Note that it is very possible to go over this number.
                {
                    MsgPtr = CacheQueue_.getQueue(0);
                }
            }

            if(ptIdList.size() > 0)
            {
                timerOutput = threadName + " loading " + CtiNumStr(ptIdList.size()) + " points";
                Cti::Timing::DebugTimer debugTime(timerOutput, gDispatchDebugLevel & DISPATCH_DEBUG_PERFORMANCE, 2);

                PointMgr.refreshListByPointIDs(ptIdList);
                ptIdList.clear();
            }

            if(msgList.size() > 0)
            {
                for(std::list<CtiMessage *>::iterator iter = msgList.begin(); iter != msgList.end(); iter++)
                {
                    DeferredQueue_.putQueue(*iter);
                }
                msgList.clear();
            }

            stop = stop.now();

            if( stop.seconds() - start.seconds() > 1 )
            {
                CTILOG_WARN(dout, threadName <<" took "<< (stop.seconds() - start.seconds()) <<" seconds to run.");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    // And let'em know were A.D.
    CTILOG_INFO(dout, "Dispatch "<< threadName <<" shutting down");

    return;
}

void CtiVanGogh::archivePointDataMessage(const CtiPointDataMsg &aPD)
{
    try
    {
        // See if I know about this point ID
        if(const CtiPointSPtr TempPoint = PointMgr.getCachedPoint(aPD.getId()))
        {
            if ( auto pDyn = PointMgr.getDynamic(*TempPoint) )
            {
                const bool hasChanged = hasPointDataChanged(aPD, *pDyn);
                const bool isDuplicate = isDuplicatePointData(aPD, *pDyn);
                const bool previouslyArchived = pDyn->wasArchived();

                if( aPD.getTime() >= pDyn->getTimeStamp()
                    || (aPD.getTags() & TAG_POINT_FORCE_UPDATE)
                    || (pDyn->getQuality() == UnintializedQuality &&
                        aPD.getQuality() != UnintializedQuality) )
                {
                    // Set the point in memory to the current value.
                    // Do not update with an older time unless we are in the forced condition or if
                    // the point has never been updated (uninit quality)
                    pDyn->setPoint(aPD.getTime(), aPD.getMillis(), aPD.getValue(), aPD.getQuality(), (aPD.getTags() & ~INCOMING_TAG_MASK) | _signalManager.getTagMask(aPD.getId()));

                    if( isDuplicate && previouslyArchived )
                    {
                        pDyn->setWasArchived(true);
                    }
                }

                if( aPD.getTags() & (TAG_POINT_MUST_ARCHIVE | TAG_POINT_LOAD_PROFILE_DATA) )
                {
                    if( isDuplicate && previouslyArchived )
                    {
                        if( gConfigParms.isTrue("DISPATCH_LOG_DUPLICATE_ARCHIVE_SUPPRESSION", true) )
                        {
                            CTILOG_DEBUG(dout, "Suppressing duplicate forced archive, id " << TempPoint->getPointID() << ", " << aPD.getValue() << "@" << aPD.getTime() << " " << aPD.getTrackingId());
                        }
                    }
                    else
                    {
                        // This is a forced reading, which must be written, it should not
                        // however cause any change in normal pending scanned readings

                        _rphArchiver.submitPointData(aPD);

                        pDyn->setWasArchived(true);
                    }
                }
                else if(pDyn->isArchivePending() ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnUpdate) ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnChange && hasChanged) ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnTimerOrUpdated))
                {
                    _rphArchiver.submitPointData(aPD);

                    pDyn->setArchivePending(false);

                    pDyn->setWasArchived(true);
                }
            }
        }
        else
        {
            const std::string message = Cti::StreamBuffer() << "Point change for unknown PointID: " << aPD.getId();

            CTILOG_WARN(dout, message);

            CtiSignalMsg *pSig = new CtiSignalMsg(SYS_PID_DISPATCH, 0, message, "FAIL: Point Data Relay");

            pSig->setUser(aPD.getUser());
            queueSignalToSystemLog(pSig);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiVanGogh::processStalePoint(const CtiPointBase &point, CtiDynamicPointDispatch &dpd, int updateType, const CtiPointDataMsg &aPD, CtiMultiWrapper& wrap)
{
    if( aPD.getSource() != DISPATCH_APPLICATION_NAME )
    {
        if( updateType == CtiTablePointProperty::UPDATE_ALWAYS ||
           (updateType == CtiTablePointProperty::UPDATE_ON_CHANGE && aPD.getValue() != dpd.getValue()) )
        {
            _pointUpdatedTime.erase(point.getPointID());
            _pointUpdatedTime.insert(make_pair(point.getPointID(), CtiTime::now()));
            int alarm = point.isNumeric() ? CtiTablePointAlarming::staleNumeric : CtiTablePointAlarming::staleStatus;
            if (_signalManager.isAlarmedOrConditionActive(point.getPointID(), alarm)) 
            {
                activatePointAlarm(alarm, wrap, point, dpd, false);
                //I possibly changed the point tags, update!
                dpd.getDispatch().resetTags(SIGNAL_MANAGER_MASK);
                dpd.getDispatch().setTags(_signalManager.getTagMask(point.getPointID()));
                if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
                {
                    CTILOG_DEBUG(dout, "Point is no longer stale "<< point.getPointID());
                }
            }
        }
    }
}

INT CtiVanGogh::archiveSignalMessage(const CtiSignalMsg& aSig)
{
    int status = ClientErrors::None;

    try
    {
        CtiSignalMsg *pSig = NULL;

        {
            // See if I know about this point ID
            CtiServerExclusion pmguard(_server_exclusion);
            CtiPointSPtr TempPoint = PointMgr.getPoint(aSig.getId());

            if(TempPoint)
            {
                pSig = (CtiSignalMsg*)aSig.replicateMessage();
            }
            else
            {
                const std::string message = Cti::StreamBuffer() << "Signal for unknown PointID: "<< aSig.getId();

                CTILOG_WARN(dout, message);

                pSig = new CtiSignalMsg(SYS_PID_DISPATCH, 0, message, "FAIL: Signal Relay");
                pSig->setUser(aSig.getUser());
                status = ClientErrors::IdNotFound; // Error is ID not found!
            }
        }

        if(pSig != NULL)
        {
            queueSignalToSystemLog(pSig);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}


void CtiVanGogh::processMultiMessage(CtiMultiMsg *pMulti)
{
    try
    {
        for(int i = 0; i < pMulti->getData().size(); i++)
        {
            CtiMessage *pMsg = (CtiMessage*)pMulti->getData()[i];

            switch(pMsg->isA())
            {
            case MSG_POINTREGISTRATION:
            case MSG_REGISTER:
            case MSG_POINTDATA:
            case MSG_SIGNAL:
            case MSG_DBCHANGE:            // How about this potential recursion....
            case MSG_TAG:
            default:
                {
                    processMessageData( pMsg );
                    break;
                }
            case MSG_PCRETURN:
            case MSG_MULTI:
                {
                    CtiMultiMsg *pMultiNew = (CtiMultiMsg*)pMsg;    // Oh no, some more recursion!
                    processMultiMessage( pMultiNew );
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}


/*--------------------------------------------------------------------*
 * This guy looks at and absorbs any information that Dispatch needs
 * from the message, following this, the message is posted to clients
 *--------------------------------------------------------------------*/
void CtiVanGogh::processMessageData( CtiMessage *pMsg )
{
    try
    {
        switch( pMsg->isA() )
        {
        case MSG_POINTREGISTRATION:
            {
                messageDump(pMsg);
                const CtiPointRegistrationMsg &aReg = *((CtiPointRegistrationMsg*)(pMsg));
                CtiServer::ptr_type sptrCM = findConnectionManager(pMsg->getConnectionHandle());
                if( sptrCM )
                {
                    registration(sptrCM, aReg);
                }
                break;
            }
        case MSG_REGISTER:
            {
                messageDump(pMsg);
                const CtiRegistrationMsg &aReg = *((CtiRegistrationMsg*)(pMsg));
                CtiServer::ptr_type pCM = findConnectionManager(pMsg->getConnectionHandle());

                if(pCM)
                {
                    CtiVanGoghConnectionManager *CM = (CtiVanGoghConnectionManager*)(pCM.get());

                    CM->setClientName(aReg.getAppName());
                    CM->setClientAppId(aReg.getAppId());
                    CM->setClientUnique(aReg.getAppIsUnique());
                    CM->setClientExpirationDelay(aReg.getAppExpirationDelay());

                    clientRegistration(pCM);
                }
                else
                {
                    CTILOG_ERROR(dout, "Could not register connection, connection handle "<< pMsg->getConnectionHandle() <<" not found")
                }

                break;
            }
        case MSG_POINTDATA:
            {
                messageDump(pMsg);
                const CtiPointDataMsg &aPD = *((CtiPointDataMsg*)(pMsg));
                if(!(aPD.getTags() & TAG_POINT_DELAYED_UPDATE))             // These guys come up again like Thai food.
                {
                    archivePointDataMessage(aPD);
                }
                break;
            }
        case MSG_SIGNAL:
            {
                messageDump(pMsg);
                const CtiSignalMsg &aSig = *((CtiSignalMsg*)(pMsg));
                archiveSignalMessage(aSig);
                break;
            }
        case MSG_DBCHANGE:      // Everyone gets these, no matter what!
            {
                messageDump(pMsg);
                const CtiDBChangeMsg &aChg = *((CtiDBChangeMsg*)(pMsg));
                postDBChange(aChg);
                // In the event that a GlobalSetting has been updated, reload GlobalSettings.
                if (resolveDBCategory(aChg.getCategory()) == CtiDBChangeCategory::GlobalSetting)
                {
                    GlobalSettings::reload();

                    doutManager.reloadMaxFileSize();
                }
                break;
            }
        case MSG_PCRETURN:
        case MSG_MULTI:
            {
                CtiMultiMsg *pMulti = (CtiMultiMsg*)pMsg;
                processMultiMessage(pMulti);
                break;
            }
        case MSG_TAG:
            {
                CtiTagMsg &tagMsg = *((CtiTagMsg*)pMsg);
                processTagMessage(tagMsg);
                break;
            }
        case MSG_LMCONTROLHISTORY:
            {
                // Cannot call this here because it alters dynamic point info.  That must be done before any point data messages arrive.
                // See the checkDataStateQuality call.
                // processControlMessage((CtiLMControlHistoryMsg*)pMsg);
                break;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/******************************************************************************
 * This method handles all messages which need to go to _globally_ connected
 * clients...
 *
 ******************************************************************************/
void CtiVanGogh::postMessageToClients(CtiMessage *pMsg)
{
    INT i;

    CtiMultiMsg  *pMulti;

    CtiServerExclusion guard(_server_exclusion);
    CtiServer::spiterator  itr;

    CtiServer::ptr_type Mgr;
    CtiServer::ptr_type MgrToRemove;

    try
    {
        for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
        {
            Mgr = itr->second;
            CtiVanGoghConnectionManager &VGCM = *((CtiVanGoghConnectionManager*)Mgr.get());

            try
            {
                pMulti = generateMultiMessageForConnection(Mgr, pMsg);

                if(pMulti->getData().size() > 0)
                {
                    if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)
                    {
                        CTILOG_DEBUG(dout, "<<<< Message to Client Connection "<< Mgr->getClientName() <<" on "<< Mgr->getPeer() <<
                                *pMulti);
                    }

                    if(Mgr->WriteConnQue( pMulti, CALLSITE, 5000 ))
                    {
                        MgrToRemove = Mgr;

                        CTILOG_WARN(dout, "Connection is not viable : "<< Mgr->getClientName() <<" / "<< Mgr->getClientAppId());
                    }
                }
                else if(pMulti != NULL) // This means none of the messages were for this connection.
                {
                    delete pMulti;
                    pMulti = NULL;
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }

        if(MgrToRemove)    // Someone failed.. Blitz them
        {
            clientShutdown(MgrToRemove);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}



CtiMultiMsg* CtiVanGogh::generateMultiMessageForConnection(const CtiServer::ptr_type &Conn, CtiMessage *pMsg)
{
    INT            status   = ClientErrors::None;

    CtiMultiMsg    *pMulti  = CTIDBG_new CtiMultiMsg;


    if(pMulti != NULL)
    {
        CtiMultiMsg_vec &aOrdered = pMulti->getData();        // This is the big box we put all other messages in for this connection
        status = assembleMultiForConnection(Conn, pMsg, aOrdered);
    }

    return pMulti;
}

INT CtiVanGogh::assembleMultiForConnection(const CtiServer::ptr_type &Conn, CtiMessage *pMsg, CtiMultiMsg_vec &aOrdered)
{
    INT status   = ClientErrors::None;

    switch(pMsg->isA())
    {
    case MSG_PCRETURN:
    case MSG_MULTI:
        {
            status = assembleMultiFromMultiForConnection(Conn, pMsg, aOrdered);
            break;
        }
    case MSG_SIGNAL:
        {
            status = assembleMultiFromSignalForConnection(Conn, pMsg, aOrdered);
            break;
        }
    case MSG_POINTDATA:
        {
            status = assembleMultiFromPointDataForConnection(Conn, pMsg, aOrdered);
            break;
        }
    case MSG_TAG:
        {
            status = assembleMultiFromTagForConnection(Conn, pMsg, aOrdered);
            break;
        }
    case MSG_DBCHANGE:
    case MSG_POINTREGISTRATION:
    case MSG_REGISTER:
    case MSG_COMMAND:                // This may be a non-updated command
        {
            break;
        }
    case MSG_LMCONTROLHISTORY:
        {
            break;
        }
    default:
        {
            CTILOG_ERROR(dout, "Unexpected MSG_TYPE = "<< pMsg->isA());
        }
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromMultiForConnection(const CtiServer::ptr_type &Conn,
                                                    CtiMessage                        *pMsg,
                                                    CtiMultiMsg_vec                         &Ord)
{
    INT            i;
    INT            status   = ClientErrors::None;
    CtiMultiMsg    *pMulti  = (CtiMultiMsg*)pMsg;      // Uses the inheritance nature of MSG_PCRETURN
    CtiMessage     *pMyMsg;

    if(pMulti != NULL)
    {

        CtiMultiMsg_vec::iterator itr = pMulti->getData().begin();

        for(;pMulti->getData().end() != itr; itr++)
        {
            pMyMsg = (CtiMessage*)*itr;
            status = assembleMultiForConnection(Conn, pMyMsg, Ord);
            if( status )
            {
                CTILOG_ERROR(dout, "unable to process this message"<<
                        *pMyMsg);
            }
        }
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromSignalForConnection(const CtiServer::ptr_type &Conn,
                                                     CtiMessage                        *pMsg,
                                                     CtiMultiMsg_vec                         &Ord)
{
    INT            status   = ClientErrors::None;
    CtiSignalMsg   *pSig    = (CtiSignalMsg*)pMsg;

    try
    {
        if(pSig != NULL)
        {
            if(isSignalForConnection(Conn, *pSig))
            {
                if( CtiPointSPtr pPoint = PointMgr.getPoint(pSig->getId()) )
                {
                    if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pPoint) )
                    {
                        // Only set non-alarm tags.  This signal must indicate if it is an alarm on entry (via checkSignalStateQuality)
                        pSig->setTags( (pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) );
                    }
                }

                //The tags are now set in pSig so that the effect goes beyond just this message.
                CtiSignalMsg *pNewSig = (CtiSignalMsg *)pSig->replicateMessage();
                Ord.push_back(pNewSig);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromTagForConnection(const CtiServer::ptr_type &Conn,
                                                  CtiMessage                        *pMsg,
                                                  CtiMultiMsg_vec                         &Ord)
{
    INT            status   = ClientErrors::None;
    CtiTagMsg      *pTag    = (CtiTagMsg*)pMsg;

    try
    {
        if(pTag != NULL)
        {
            if(isTagForConnection(Conn, *pTag))
            {
                // At this point we may want to stuff an entire multi of all pointid related tags out onto the clients.
                // This ensures that any processing can occur naturally.???  Beware of removals... Not sure how to handle them?
                // May have to blitz the const-ness of the processing to account for that??
                CtiTagMsg *pNewTag = (CtiTagMsg *)pTag->replicateMessage();
                Ord.push_back(pNewTag);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromPointDataForConnection(const CtiServer::ptr_type &Conn,
                                                        CtiMessage *pMsg,
                                                        CtiMultiMsg_vec &Ord)
{
    if( ! pMsg )
    {
        CTILOG_ERROR(dout, "NULL message");
        return ClientErrors::Abnormal;
    }

    CtiPointDataMsg *pDat = (CtiPointDataMsg*)pMsg;

    try
    {
        if(!(pDat->getTags() & TAG_POINT_DELAYED_UPDATE))   // We will propagate this one later!
        {
            if(CtiPointSPtr pTempPoint = PointMgr.getPoint(pDat->getId()))
            {
                if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pTempPoint) )
                {
                    if( pDat->getQuality() == ManualQuality || !(pDyn->getDispatch().getTags() & (MASK_ANY_SERVICE_DISABLE)) ) // (MASK_ANY_SERVICE_DISABLE | MASK_ANY_CONTROL_DISABLE)) )
                    {
                        if(isPointDataForConnection(Conn, *pDat))
                        {
                            {
                                CtiPointDataMsg *pNewData = (CtiPointDataMsg *)pDat->replicateMessage();

                                pNewData->resetTags(~MASK_INCOMING_VALUE_TAGS);  //  Reset everything but the incoming value tags
                                pNewData->setTags(pDyn->getDispatch().getTags() & ~MASK_INCOMING_VALUE_TAGS);  //  Set any of the "sticky" tags

                                Ord.push_back(pNewData);
                            }
                        }
                    }
                    else if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                    {
                        // Point data on a disabled point.
                        Cti::StreamBuffer gripe;
                        gripe << " NO DATA REPORT to: "<< Conn->getClientName() <<" ";

                        INT mask = (pDyn->getDispatch().getTags() & MASK_ANY_DISABLE);

                        if(mask & (TAG_DISABLE_DEVICE_BY_DEVICE))
                        {
                            gripe << pTempPoint->getName() <<" is disabled by its device";
                        }
                        else if(mask & (TAG_DISABLE_POINT_BY_POINT))
                        {
                            gripe << pTempPoint->getName() <<" is disabled";
                        }
                        else if(mask & (TAG_DISABLE_CONTROL_BY_POINT | TAG_DISABLE_CONTROL_BY_DEVICE))
                        {
                            gripe << pTempPoint->getName() <<" is control disabled";
                        }

                        CTILOG_DEBUG(dout, gripe);
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::None;
}

BOOL CtiVanGogh::isSignalForConnection(const CtiServer::ptr_type &Conn, const CtiSignalMsg &Msg)
{
    BOOL bStatus = FALSE;

    /*
     *  Check if this connection cared about signals at all
     */
    const CtiVanGoghConnectionManager *vgConn = (const CtiVanGoghConnectionManager *)Conn.get();
    if( ((Msg.getTags() & TAG_REPORT_MSG_TO_ALARM_CLIENTS) && vgConn->getAlarm()) ||
        ( Msg.isAlarm() && vgConn->getAlarm() ) ||
        ( Msg.isEvent() && vgConn->getEvent() ) )
    {
        bStatus = TRUE;
    }
    else
    {
        bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getId());
    }

    return bStatus;
}

BOOL CtiVanGogh::isTagForConnection(const CtiServer::ptr_type &Conn, const CtiTagMsg &Msg)
{
    BOOL bStatus = FALSE;

    if( ((const CtiVanGoghConnectionManager *)Conn.get())->isRegForAll() )
    {
        bStatus = TRUE;
    }
    else
    {
        bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getPointID());
    }

    return bStatus;
}

BOOL CtiVanGogh::isPointDataForConnection(const CtiServer::ptr_type &Conn, const CtiPointDataMsg &Msg)
{
    BOOL bStatus = FALSE;

    if( ((const CtiVanGoghConnectionManager *)Conn.get())->isRegForAll() )
    {
            bStatus = TRUE;
    }
    else
    {
        bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getId());
    }

    return bStatus;
}

bool CtiVanGogh::hasPointDataChanged(const CtiPointDataMsg &Msg, const CtiDynamicPointDispatch &Dyn)
{
    //  This is for points on devices like RTUs that send or are scanned for periodic updates.
    if( Msg.getTags() & TAG_POINT_DATA_TIMESTAMP_VALID )
    {
        //  If the time is newer
        if( (Msg.getTime() > Dyn.getTimeStamp()) ||
            (Msg.getTime() == Dyn.getTimeStamp() && Msg.getMillis() != Dyn.getTimeStampMillis()) )
        {
            //  and the value has changed
            return Dyn.getValue() != Msg.getValue();
        }

        //  Or if we've never received a point before
        if( Dyn.getQuality() == UnintializedQuality &&
            Msg.getQuality() != UnintializedQuality )
        {
            return true;
        }
    }
    else
    {
        if( Dyn.getValue() != Msg.getValue() )
        {
            return true;
        }

        if( Dyn.getQuality() != NonUpdatedQuality &&
            Dyn.getQuality() != Msg.getQuality() )
        {
            return true;
        }
    }

    return false;
}

bool CtiVanGogh::isDuplicatePointData( const CtiPointDataMsg &pd, const CtiDynamicPointDispatch &dp )
{
    return boost::make_tuple(pd.getId(), pd.getTime(), pd.getMillis(), pd.getQuality(), pd.getValue())
        == boost::make_tuple(dp.getDispatch().getPointID(), dp.getTimeStamp(), dp.getTimeStampMillis(), dp.getQuality(), dp.getValue());
}


BOOL CtiVanGogh::isConnectionAttachedToMsgPoint(const CtiServer::ptr_type &Conn, const LONG pID)
{
    return PointMgr.pointHasConnection(pID, Conn);
}

/*
 *  A CtiLMControlHistoryMsg is the message type sent by each device or device group when a
 *  control operation begins.
 *
 *  There are two options for the pending list
 *  1. It is empty, this is the first time dispatch has heard of this control
 *  2. It has a pending op in the control state of "controlSentToPorter" if
 *  it was started by a CommandMsg.  Note that the controlSentToPorter msg can be useful for
 *  catching timeout scenarios for controls.
 */
int CtiVanGogh::processControlMessage(CtiLMControlHistoryMsg *pMsg)
{
    int status = ClientErrors::None;

    CtiServerExclusion pmguard(_server_exclusion);

    if( CtiPointSPtr pPoint = PointMgr.getPoint(pMsg->getPointId()) )
    {
        CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pPoint);

        if(pDyn
           && pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE
           && !(pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_POINT ||
                pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_DEVICE))
        {
            if(pMsg->getPAOId() == 0)      // We need to find the point's device
            {
                pMsg->setPAOId( pPoint->getDeviceID() );
            }

            int controlTimeout = DefaultControlExpirationTime;

            if( pPoint->isStatus() )
            {
                CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(pPoint);

                if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
                {
                    controlTimeout = controlParameters->getCommandTimeout();
                }
            }

            auto pendingControlLMMsg = std::make_unique<CtiPendingPointOperations>(pPoint->getID());
            pendingControlLMMsg->setType(CtiPendingPointOperations::pendingControl);
            pendingControlLMMsg->setControlState( CtiPendingPointOperations::controlPending );
            pendingControlLMMsg->setTime( pMsg->getStartDateTime() );
            pendingControlLMMsg->setControlCompleteValue( (DOUBLE) pMsg->getRawState() );
            pendingControlLMMsg->setControlTimeout( controlTimeout );
            pendingControlLMMsg->setExcludeFromHistory(!isDeviceGroupType(pPoint->getDeviceID()));

            // We prime the pending control object here, where we know all there is to know.
            pendingControlLMMsg->getControl().setPAOID(pMsg->getPAOId());
            pendingControlLMMsg->getControl().setActiveRestore(pMsg->getActiveRestore());
            pendingControlLMMsg->getControl().setDefaultActiveRestore(pMsg->getActiveRestore());
            pendingControlLMMsg->getControl().setControlDuration(pMsg->getControlDuration());
            pendingControlLMMsg->getControl().setControlType(pMsg->getControlType());
            pendingControlLMMsg->getControl().setReductionValue(pMsg->getReductionValue());
            pendingControlLMMsg->getControl().setReductionRatio(pMsg->getReductionRatio());
            pendingControlLMMsg->getControl().setStartTime(pMsg->getStartDateTime());
            pendingControlLMMsg->getControl().setControlPriority(pMsg->getControlPriority());
            pendingControlLMMsg->setAssociationKey(pMsg->getAssociationKey());

            CtiTime cntrlStopTime(pMsg->getStartDateTime().seconds() + pMsg->getControlDuration());
            if(pMsg->getControlDuration() < 0) cntrlStopTime = pMsg->getStartDateTime();

            pendingControlLMMsg->getControl().setStopTime(cntrlStopTime);
            pendingControlLMMsg->getControl().setControlCompleteTime(cntrlStopTime);
            pendingControlLMMsg->getControl().setSoeTag( CtiTableLMControlHistory::getNextSOE() );

            const CtiTablePointAlarming alarming = PointMgr.getAlarming(*pPoint);

            auto pFailSig = std::make_unique<CtiSignalMsg>(pPoint->getID(), 0, "Control " + ResolveStateName(pPoint->getStateGroupID(), pMsg->getRawState()) + " Failed", getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure), pMsg->getUser());

            pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK));
            if(pFailSig->getSignalCategory() > SignalEvent)
            {
                pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | (alarming.isAutoAcked(CtiTablePointAlarming::commandFailure) ? 0 : TAG_UNACKNOWLEDGED_ALARM));
                pFailSig->setLogType(AlarmCategoryLogType);
            }
            pFailSig->setCondition(CtiTablePointAlarming::commandFailure);

            pendingControlLMMsg->setSignal( pFailSig.release() );

            if(isDeviceGroupType(pMsg->getPAOId()) && _pendingOpThread.getCurrentControlPriority(pMsg->getPointId()) >= pMsg->getControlPriority())
            {
                if( auto pControlStatus = PointMgr.getControlOffsetEqual(pMsg->getPAOId(), 1) )
                {
                    if( pControlStatus->isPseudoPoint() )
                    {
                        // There is no physical point to observe and respect.  We lie to the control point.
                        auto pData = std::make_unique<CtiPointDataMsg>(pControlStatus->getPointID(), pMsg->getRawState(), NormalQuality, StatusPointType, (pMsg->getRawState() == CONTROLLED ? string(resolveDeviceNameByPaoId(pMsg->getPAOId()) + " controlling") : string(resolveDeviceNameByPaoId(pMsg->getPAOId()) + " restoring")));
                        pData->setMessagePriority( pData->getMessagePriority() + 1 );
                        MainQueue_.putQueue(pData.release());
                    }

                    if( pMsg->getRawState() == CONTROLLED && pMsg->getControlDuration() > 0 )
                    {
                        // Present the restore as a delayed update to dispatch.  Note that the order of opened and closed have reversed
                        auto pData = std::make_unique<CtiPointDataMsg>(pControlStatus->getPointID(), (DOUBLE)UNCONTROLLED, NormalQuality, StatusPointType, string(resolveDeviceNameByPaoId(pMsg->getPAOId()) + " restoring (delayed)"), TAG_POINT_DELAYED_UPDATE);
                        pData->setTime(CtiTime() + pMsg->getControlDuration());
                        pData->setMessagePriority(pData->getMessagePriority() - 1);
                        MainQueue_.putQueue(pData.release());
                    }

                }
                else
                {
                    CTILOG_WARN(dout, "No control status point for device group id " << pMsg->getPAOId());
                }
            }

            addToPendingSet(std::move(pendingControlLMMsg), pMsg->getMessageTime());

            pDyn->getDispatch().setTags( TAG_CONTROL_PENDING );
            bumpDeviceToAlternateRate( *pPoint );
        }
    }

    return status;
}

YukonError_t CtiVanGogh::processMessage(CtiMessage *pMsg)
{
    CtiMultiWrapper MultiWrapper;

    CtiServerExclusion guard(_server_exclusion);

    if( const auto status = checkDataStateQuality(pMsg, MultiWrapper) )
    {
        //  bail out if we can't find the ID or if the pointdata is invalid
        return status;
    }

    checkForStalePoints(MultiWrapper);
    /*
     *  Order here is important since the processMessageData routine writes into the RTDB the current values
     *  and the post routine compares messages against the current values to determine exceptions (data changes).
     */
    postMessageToClients(pMsg);
    processMessageData(pMsg);

    // Now process any messages which were generated by the processing of the message.
    if( MultiWrapper.isNotNull() )
    {
        postMessageToClients((CtiMessage*)MultiWrapper.getMulti());
        processMessageData((CtiMessage*)MultiWrapper.getMulti());
    }

    return ClientErrors::None;
}

void CtiVanGogh::postMOAUploadToConnection(CtiVanGoghConnectionManager& VGCM, std::set<long> &ptIds, const bool tag_as_moa)
{
    // Is this connection asking for everything?
    if( VGCM.isRegForAll() )
    {
        CTILOG_INFO(dout, "Client Connection "<< VGCM.getClientName() <<" on "<< VGCM.getPeer()<< " registered for everything");

        return;
    }

    auto pMulti = std::make_unique<CtiMultiMsg>();

    pMulti->setMessagePriority(15);

    for( const auto ptId : ptIds )
    {
        if( auto TempPoint = PointMgr.getPoint(ptId) )
        {
            if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*TempPoint) )
            {
                auto pDat = 
                        std::make_unique<CtiPointDataMsg>(
                                TempPoint->getID(),
                                pDyn->getValue(),
                                pDyn->getQuality(),
                                TempPoint->getType(),
                                string(),
                                pDyn->getDispatch().getTags());

                if( tag_as_moa )
                {
                    pDat->setTags(TAG_POINT_MOA_REPORT);
                }

                // Make the time match the entered time
                pDat->setTime( pDyn->getTimeStamp() );
                pDat->setSource(DISPATCH_APPLICATION_NAME);
                pMulti->getData().push_back(pDat.release());
            }
        }

        /*
        *  Block the MOA into 1000 element multis.
        */
        if( pMulti->getCount() >= gConfigParms.getValueAsULong("DISPATCH_MAX_MULTI_MOA", 1000) )
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)
            {
                CTILOG_DEBUG(dout, "MOA UPLOAD - Client Connection "<< VGCM.getClientName() <<" on "<< VGCM.getPeer()<<
                        *pMulti);
            }

            if( VGCM.WriteConnQue( std::move(pMulti), CALLSITE, 5000 ) )
            {
                CTILOG_ERROR(dout, "Connection is having issues : "<< VGCM.getClientName() <<" / "<< VGCM.getClientAppId());
            }

            pMulti = std::make_unique<CtiMultiMsg>();
        }
    }

    //This now gets all alarms in the known universe.
    // full of all alarms active/unacknowledged on all points
    if( VGCM.getAlarm() )
    {
        if( CtiMultiMsg *pSigMulti = _signalManager.getAllAlarmSignals() )
        {
            pMulti->getData().push_back(pSigMulti);
        }
    }

    // We add all the assigned tags into the multi as well.
    if( CtiMultiMsg *pTagMulti = _tagManager.getAllPointTags() )
    {
        pMulti->getData().push_back(pTagMulti);
    }

    if( pMulti->getCount() > 0 )
    {
        if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)    // Temp debug
        {
            CTILOG_DEBUG(dout, "MOA UPLOAD - Client Connection "<< VGCM.getClientName() <<" on "<< VGCM.getPeer() <<
                    *pMulti);
        }

        if( VGCM.WriteConnQue( std::move(pMulti), CALLSITE, 5000 ) )
        {
            CTILOG_ERROR(dout, "Connection is having issues: " << VGCM.getClientName() << " / " << VGCM.getClientAppId());
        }
    }
}


void CtiVanGogh::loadPendingSignals()
{
    CtiServerExclusion pmguard(_server_exclusion);
    {
        static const string sql = CtiTableDynamicPointAlarming().getSQLCoreStatement();

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        while( rdr() )
        {
            CtiTableDynamicPointAlarming dynAlarm;

            dynAlarm.DecodeDatabaseReader(rdr);
            CtiSignalMsg sig;

            sig.setId( dynAlarm.getPointID() );
            sig.setMessageTime( dynAlarm.getAlarmTime() );
            sig.setSignalCategory( dynAlarm.getCategoryID() );
            sig.setText( dynAlarm.getAction() );
            sig.setAdditionalInfo( dynAlarm.getDescription() );
            sig.setTags( dynAlarm.getTags() & SIGNAL_MANAGER_MASK );     // We only care about the alarm masks!
            sig.setCondition( dynAlarm.getAlarmCondition() );
            sig.setLogID(dynAlarm.getLogID());
            sig.setSOE(dynAlarm.getSOE());
            sig.setUser(dynAlarm.getUser());

            sig.setLogType(dynAlarm.getLogType());   // FIX FIX FIX CGP ... think about these two lines.
            // sig.setLogType( AlarmCategoryLogType );

            _signalManager.addSignal( sig, false );
        }

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
        }
    }
}

void CtiVanGogh::writeSignalsToDB(bool justdoit)
{
    UINT         panicCounter = 0;
    static UINT  dumpCounter  = 0;

    try
    {
        boost::ptr_deque<CtiSignalMsg> postList;

        if(!(++dumpCounter % DUMP_RATE)
           || _signalMsgQueue.entries() > MAX_ARCHIVER_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            {
                Cti::Database::DatabaseConnection   conn;

                if ( ! conn.isValid() )
                {
                    CTILOG_ERROR(dout, "Invalid Connection to Database");
                    return;
                }

                try
                {
                    while( justdoit || (panicCounter++ < 500) )
                    {
                        std::unique_ptr<CtiSignalMsg> sigMsg(_signalMsgQueue.getQueue(0));

                        if( ! sigMsg.get() )
                        {
                            break;
                        }

                        if( ! sigMsg->getText().empty() || ! sigMsg->getAdditionalInfo().empty() )
                        {
                            CtiTableSignal sig(sigMsg->getId(), sigMsg->getMessageTime(), sigMsg->getSignalMillis(), sigMsg->getText(), sigMsg->getAdditionalInfo(), sigMsg->getSignalCategory(), sigMsg->getLogType(), sigMsg->getSOE(), sigMsg->getUser(), sigMsg->getLogID());

                            // No text, no point then is there now?
                            sig.Insert(conn);
                        }

                        if( ! (sigMsg->getTags() & TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL) )
                        {
                            postList.push_back(std::move(sigMsg));
                        }
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }

            if(panicCounter > 0)
            {
                CTILOG_INFO(dout, "SystemLog transaction complete. Inserted "<< panicCounter <<" signal messages. " << _signalMsgQueue.entries() << " left on queue");
            }
        }

        if((justdoit == true || !(dumpCounter % SANITY_RATE)) && !_signalManager.empty() && _signalManager.dirty())//use sanity rate to slow us down
        {
            _signalManager.writeDynamicSignalsToDB();
        }

        while( ! postList.empty() )
        {
            boost::ptr_deque<CtiSignalMsg>::auto_type sigMsg = postList.pop_back();

            // Place them on the email queue and let him clean them up!
            if( _signalMsgPostQueue.putQueue(sigMsg.get(), 5000) )
            {
                sigMsg.release();
            }
            else
            {
                CTILOG_ERROR(dout, "Failed to queue signal message for emailing");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

/*
 *  This method makes sure that AreYouThere messages are being responded to by clients.
 *
 *  Potential Locks:
 *
 */
void CtiVanGogh::purifyClientConnectionList()
{
    static UINT  confrontationCount = 0;
    static ULONG KilledCount;
    static ULONG ClientCount;

    try
    {
        /*
         *  Once per CONFRONT_RATE = 300 seconds, we ask if anyone has not responded to the last
         *  Confront.  If they haven't, we Purge them.. Then we confront again for this 300
         *  second interval.
         */
        if(!(++confrontationCount % CONFRONT_RATE) )    // Only chase the queue once per CONFRONT_RATE seconds.
        {
            CtiServerExclusion server_guard(_server_exclusion, 2500);      // Get a lock on it.

            if(server_guard.isAcquired())
            {
                if(ClientCount > 0)
                {
                    clientPurgeQuestionables(&KilledCount);      // Purge anyone marked as questionable

                    if(KilledCount > 0)
                    {
                        CTILOG_INFO(dout, "Of "<< ClientCount <<" connections "<< KilledCount <<" were deleted");
                    }
                }

                clientConfrontEveryone(&ClientCount);        // Ask everyone to respond to an AreYouThere..
            }
            else
            {
                confrontationCount--; // Make us try again next time through.
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return;
}


/*
 *  This method ensures that the runtime database is periodically flushed to the db.
 *
 * Potentially Locks:
 *          PointMgr's monitor.
 *          db connection mutex.
 */
void CtiVanGogh::updateRuntimeDispatchTable(bool force)
{
    static UINT callCounter = 0;
    callCounter++;
    try
    {
        if(force || !(callCounter % UPDATERTDB_RATE) )    // Only chase the queue once per CONFRONT_RATE seconds.
        {
            CTILOG_INFO(dout, "Writing dispatch dynamic table");

            CtiTime start;
            PointMgr.storeDirtyRecords();
            CtiTime stop;

            CTILOG_INFO(dout, "Done writing dispatch dynamic table, took: " << (stop.seconds() - start.seconds()) << " seconds");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return;
}

void CtiVanGogh::refreshCParmGlobals(bool force)
{
    try
    {
        InitYukonBaseGlobals();

        string str;

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTLHIST_INTERVAL")).empty() )
        {
            CntlHistInterval = atoi(str.c_str());
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_CNTLHIST_INTERVAL found : "<< str);
            }
        }
        else
        {
            CntlHistInterval = 3600;
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_CNTLHIST_INTERVAL default : "<< CntlHistInterval);
            }
        }

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTLHISTPOINTPOST_INTERVAL")).empty() )
        {
            CntlHistPointPostInterval = atoi(str.c_str());
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_CNTLHISTPOINTPOST_INTERVAL found : "<< str);
            }
        }
        else
        {
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_CNTLHISTPOINTPOST_INTERVAL default : "<< CntlHistPointPostInterval);
            }
        }

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTL_STOP_REPORT_INTERVAL")).empty() )
        {
            CntlStopInterval = atoi(str.c_str());
            if(CntlStopInterval > 3600)
            {
                CntlStopInterval = 3600;
                CTILOG_WARN(dout, "DISPATCH_CNTL_STOP_REPORT_INTERVAL cannot be greater than 3600");
            }

            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_CNTL_STOP_REPORT_INTERVAL found : "<< str);
            }
        }
        else
        {
            CntlStopInterval = 60;
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_CNTL_STOP_REPORT_INTERVAL default : "<< CntlStopInterval);
            }
        }

        if( gConfigParms.isTrue("DISPATCH_SHUTDOWN_ON_THREAD_TIMEOUT") )
        {
            ShutdownOnThreadTimeout = true;
        }
        else
        {
            ShutdownOnThreadTimeout = false;
            if(DebugLevel & 0x0001)
            {
                CTILOG_DEBUG(dout, "Configuration Parameter DISPATCH_SHUTDOWN_ON_THREAD_TIMEOUT default : "<< ShutdownOnThreadTimeout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return;
}

/*
 *  This method attempts to examine the inbound message and handle its content.
 *  Any processing at this stage can and may produce additional messages which  require
 *  further processing.  At this stage, the original inbound message may be altered by
 *  dispatch to fit the current system state.
 *
 *  Data messages payload is processed at this stage.
 *  Command messages which could produce signal messages must be processed at this stage as well.
 */
YukonError_t CtiVanGogh::checkDataStateQuality(CtiMessage *pMsg, CtiMultiWrapper &aWrap)
{
    switch(pMsg->isA())
    {
    case MSG_PCRETURN:
    case MSG_MULTI:
        {
            return checkMultiDataStateQuality((CtiMultiMsg*)pMsg, aWrap);
        }
    case MSG_POINTDATA:
        {
            return checkPointDataStateQuality(static_cast<CtiPointDataMsg &>(*pMsg), aWrap);
        }
    case MSG_COMMAND:
        {
            CtiCommandMsg *pCmd = (CtiCommandMsg*)pMsg;

            if(pCmd->getOperation() == CtiCommandMsg::UpdateFailed)
            {
                return commandMsgUpdateFailedHandler(pCmd, aWrap);
            }

            // Make sure nobody is doing something odd.  CGP 3/18/2002
            commandMsgHandler(pCmd);

            return ClientErrors::None;
        }
    case MSG_SIGNAL:
        {
            return checkSignalStateQuality((CtiSignalMsg*)pMsg, aWrap);
        }
    case MSG_TAG:
        {
            // Allocate instance number to any non-allocated message!
            // Process instances for removal requests.
            _tagManager.verifyTagMsg(*((CtiTagMsg*)pMsg));

            return ClientErrors::None;
        }
    case MSG_LMCONTROLHISTORY:
        {
            // Pick this up here so that the dyn Tags may be modified....
            processControlMessage((CtiLMControlHistoryMsg*)pMsg);

            return ClientErrors::None;
        }
    }

    return ClientErrors::None;
}

bool sortPointDataByTimestamp( const CtiMessage * a, const CtiMessage * b )
{
    if( a && b && a->isA() == MSG_POINTDATA && b->isA() == MSG_POINTDATA )
    {
        auto aPd = static_cast<const CtiPointDataMsg *>(a);
        auto bPd = static_cast<const CtiPointDataMsg *>(b);

        return
            std::make_pair( aPd->getTime(), aPd->getMillis() ) <
            std::make_pair( bPd->getTime(), bPd->getMillis() );
    }
    return false; // Non-PointData messages are treated as equivalent to all other messages and will not be sorted.
}

/*
 *  Could recurse.  Does not worry about self referential loops... Don't do it.
 */
YukonError_t CtiVanGogh::checkMultiDataStateQuality(CtiMultiMsg  *pMulti, CtiMultiWrapper &aWrap)
{
    if( pMulti )
    {
        auto &bag = pMulti->getData();

        boost::range::stable_sort( bag, sortPointDataByTimestamp );

        for( auto itr = bag.begin(); itr != bag.end(); )
        {
            if( const auto status = checkDataStateQuality(*itr, aWrap) )
            {
                CTILOG_WARN(dout, "Pointdata filtered " << static_cast<unsigned>(status) << " " << CtiError::GetErrorString(status) << **itr);

                itr = bag.erase(itr);
            }
            else
            {
                ++itr;
            }
        }
    }

    return ClientErrors::None;
}

YukonError_t CtiVanGogh::checkSignalStateQuality(CtiSignalMsg  *pSig, CtiMultiWrapper &aWrap)
{
    if(pSig->getText().empty() && pSig->getAdditionalInfo().empty())
    {
        CtiServer::ptr_type pCM = findConnectionManager(pSig->getConnectionHandle());

        const string clientname = pCM
                ? pCM->getClientName()
                : string("Unknown");

        CTILOG_INFO(dout, clientname <<" just submitted a blank signal message for point id "<< pSig->getId());
    }

    return ClientErrors::None;
}

/*----------------------------------------------------------------------------*
 * This method examines the pointdata to ensure that no alarms need to be
 * generated.  If they do, it creates the alarms and allows them to propagate
 * out to the system.
 *
 * Under certain conditions, the CtiPointData itself may be modified during
 * the analysis.
 *
 * Additional messages may be generated and added to the CtiMultiWrapper object.
 *
 * CtiServerExclusion guard(_server_exclusion); must have been grabbed already.
 *----------------------------------------------------------------------------*/
YukonError_t CtiVanGogh::checkPointDataStateQuality(CtiPointDataMsg &pData, CtiMultiWrapper &aWrap)
{
    static CtiTime Sanity = CtiTime::now() + 86400 * 365;

    if( pData.getTime() > Sanity )
    {
        Sanity = CtiTime::now() + 86400 * 365;

        if( pData.getTime() > Sanity )
        {
            return ClientErrors::InvalidFutureData;
        }
    }

    CtiPointSPtr pPoint = PointMgr.getCachedPoint(pData.getId());

    if ( ! pPoint )
    {
        return ClientErrors::IdNotFound;
    }

    CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*pPoint);

    if ( ! pDyn )
    {
        return ClientErrors::IdNotFound;
    }

    if(pData.getType() == InvalidPointType)
    {
        pData.setType(pPoint->getType());
    }
    else if(pPoint->getType() != pData.getType())
    {
        Cti::FormattedList itemlist;
        itemlist <<"Point Type mismatch. Received point data message indicated a type for point "<< pData.getId() <<" which does not match the memory image held by dispatch";
        itemlist.add("Memory Image point type") << pPoint->getType();
        itemlist.add("Message point type")      << pData.getType();

        itemlist <<"Point \""<< pPoint->getName() <<"\" is attached to "<< resolveDeviceName( *pPoint );
        if(pData.getConnectionHandle())
        {
            CtiServer::ptr_type pCM = findConnectionManager(pData.getConnectionHandle());

            if(pCM)
            {
                itemlist.add("Submitting client")  << pCM->getClientName();
                itemlist.add("Client Information") << pCM->getPeer();
            }
        }

        CTILOG_WARN(dout, itemlist);

        pData.setType(pPoint->getType());
    }


    // We need to make sure there is no pending pointdata on this pointid.
    // Arrival of a pointdata message eliminates a pending data msg.  If this is a delayed point, it will overwrite anyway!
    if( pDyn->inDelayedData() && !(pData.getTags() & TAG_POINT_DELAYED_UPDATE) )
    {
        pDyn->setInDelayedData(false);
        removePointDataFromPending(pData.getId());
    }

    if( pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE &&     // This is a controllable point.
        !(pDyn->getDispatch().getTags() & TAG_CONTROL_PENDING) &&           // This point is not expecting a control point change.
        !(pData.getTags() & TAG_POINT_DELAYED_UPDATE) &&                   // This data message is not delayed point data (future).
        pData.getValue() != pDyn->getValue() )                             // The point value has changed
    {
        // The value changed.  Any control in progress was just terminated manually.
        CtiPendable *pendable = CTIDBG_new CtiPendable(pData.getId(), CtiPendable::CtiPendableAction_ControlStatusChanged);
        pendable->_tags = pDyn->getDispatch().getTags();
        pendable->_value = pData.getValue();
        _pendingOpThread.push( pendable );
    }

    /*
     *  A point data can be submitted with the intent that it occur in the future.  If that bit indicator is set,
     *  the point is added to the pending list as a "pendingPointData".  Any subsequent change via pointdata
     *  pulls this pending operation from the list (per above)!
     */
    if(pData.getTags() & TAG_POINT_DELAYED_UPDATE)
    {
        if(gDispatchDebugLevel & DISPATCH_DEBUG_DELAYED_UPDATE)
        {
            CTILOG_DEBUG(dout, "Delayed update \""<< pData.getTime() <<"\" is indicated on point data for "<< resolveDeviceName(*pPoint) <<" / "<< pPoint->getName());
        }

        pDyn->setInDelayedData(true);

        auto pendingPointData = std::make_unique<CtiPendingPointOperations>(pData.getId());
        pendingPointData->setType(CtiPendingPointOperations::pendingPointData);
        pendingPointData->setTime( pData.getTime() );
        pendingPointData->setPointData( (CtiPointDataMsg*)pData.replicateMessage() );

        addToPendingSet(std::move(pendingPointData));

        return ClientErrors::None;
    }

    // This is a point data which is to be processed right NOW.  It may generate signals and it
    // may clear active alarms.
    try
    {
        if(pData.getTime() < pDyn->getTimeStamp() && pDyn->getQuality() != UnintializedQuality)
        {
            pData.setTags(TAG_POINT_OLD_TIMESTAMP);
        }
        else
        {
            pData.resetTags(TAG_POINT_OLD_TIMESTAMP); // No one else may set this but us!

            if( !pPoint->isAlarmDisabled() )
            {
                if(pPoint->isNumeric())
                {
                    checkForNumericAlarms(pData, aWrap, *pPoint);
                }
                else if(pPoint->isStatus())
                {
                    checkForStatusAlarms(pData, aWrap, *pPoint);
                }
            }
            else
            {
                if(_signalManager.getTagMask(pPoint->getPointID()) & TAG_ACTIVE_ALARM)
                {
                    CtiSignalMsg *sigMsg = _signalManager.clearAlarms(pPoint->getPointID());
                    pDyn->getDispatch().resetTags(MASK_ANY_ALARM);
                    if( sigMsg != NULL )
                    {
                        aWrap.getMulti()->insert(sigMsg);
                    }
                }
            }
        }

        //  checked here to avoid calling the point manager inside processStalePoint()
        if( PointMgr.hasProperty(pPoint->getPointID(), CtiTablePointProperty::STALE_UPDATE_TYPE) )
        {
            int updateType = PointMgr.getProperty(pPoint->getPointID(), CtiTablePointProperty::STALE_UPDATE_TYPE);
            processStalePoint(*pPoint, *pDyn, updateType, pData, aWrap);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Point exception on "<< pPoint->getName());
    }

    return ClientErrors::None;
}

YukonError_t CtiVanGogh::commandMsgUpdateFailedHandler(CtiCommandMsg *pCmd, CtiMultiWrapper &aWrap)
{
    auto status = ClientErrors::None;

    CtiCommandMsg::OpArgList &Op = pCmd->getOpArgList();

    if( Op[1] == CtiCommandMsg::OP_DEVICEID )    // All points on a device must be marked as nonUpdated
    {
        vector<CtiPointSPtr> points;
        LONG did = Op[2];
        PointMgr.getEqualByPAO(did, points);

        for( auto& pPoint : points )
        {
            //  Don't mark calc points nonupdated - they can inherit the quality through their component points if necessary.
            if( pPoint
                && pPoint->getType() != CalculatedPointType
                && pPoint->getType() != CalculatedStatusPointType)
            {
                status = markPointNonUpdated(*pPoint, aWrap);
            }
        }
    }
    else if( Op[1] == CtiCommandMsg::OP_POINTID )
    {
        CtiServerExclusion pmguard(_server_exclusion);

        LONG did = Op[2];

        // We know this point..
        if( CtiPointSPtr pPoint = PointMgr.getPoint(did) )
        {
            status = markPointNonUpdated(*pPoint, aWrap);
        }
    }

    return status;
}


YukonError_t CtiVanGogh::markPointNonUpdated(const CtiPointBase &point, CtiMultiWrapper &aWrap)
{
    auto status = ClientErrors::None;

    if( !(point.getType() == StatusPointType && point.getPointOffset() == 2000) ) // If not a comm status point
    {
        CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);

        // Make sure we use the correct enumeration based upon point type.
        INT alarm = (point.isStatus() ? CtiTablePointAlarming::nonUpdatedStatus : CtiTablePointAlarming::nonUpdatedNumeric);

        if( pDyn )
        {
            UINT quality = pDyn->getDispatch().getQuality();

            if(quality != NonUpdatedQuality)
            {
                pDyn->getDispatch().setQuality(NonUpdatedQuality);

                if(!_signalManager.isAlarmedOrConditionActive(point.getPointID(), alarm))
                {
                    const CtiTablePointAlarming alarming = PointMgr.getAlarming(point);

                    CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(point.getID(), 0, "Non Updated", getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm));
                    pSig->setPointValue(pDyn->getDispatch().getValue());

                    tagSignalAsAlarm(point, pSig, alarm, nullptr);
                    updateDynTagsForSignalMsg(point, pSig);
                    aWrap.getMulti()->insert( pSig );
                }

                CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(point.getID(), pDyn->getValue(), pDyn->getQuality(), point.getType(), "Non Updated");

                pDat->setSource(DISPATCH_APPLICATION_NAME);
                pDat->setTags(pDyn->getDispatch().getTags() & ~(TAG_POINT_LOAD_PROFILE_DATA | TAG_POINT_MUST_ARCHIVE));
                pDat->setTime(pDyn->getTimeStamp());

                aWrap.getMulti()->insert( pDat );
            }
        }

    }

    return status;
}


CtiServer::ptr_type CtiVanGogh::getPorterConnection()
{
    CtiServer::ptr_type Mgr;

    {
        CtiServerExclusion guard(_server_exclusion);
        CtiServer::spiterator  itr;

        for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
        {
            Mgr = itr->second;

            if(ciStringEqual(Mgr->getClientName(), PIL_REGISTRATION_NAME))
            {
                break;      // The for has completed, we found the PIL.
            }
            else
            {
                Mgr.reset();
            }
        }
    }

    return Mgr;
}

CtiServer::ptr_type CtiVanGogh::getScannerConnection()
{
    CtiServer::ptr_type Mgr;

    {
        CtiServerExclusion guard(_server_exclusion);
        CtiServer::spiterator  itr;

        for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
        {
            Mgr = itr->second;

            if(ciStringEqual(Mgr->getClientName(), SCANNER_REGISTRATION_NAME))
            {
                break;      // The for has completed, we found the SCANNER.
            }
            else
            {
                Mgr.reset();
            }
        }
    }

    return Mgr;
}


void CtiVanGogh::validateConnections()
{
    CtiServerExclusion guard(_server_exclusion);

    while( CtiServer::ptr_type CM = mConnectionTable.remove(NonViableConnection, NULL) )
    {
        CTILOG_INFO(dout, "Vagrant connection detected. Removing it."<<
            endl <<"Connection: "<< CM->getClientName() <<" id "<< CM->getClientAppId() <<" on "<< CM->getPeer() <<" will be removed");

        clientShutdown(CM);
    }

    return;
}

void CtiVanGogh::postSignalAsEmail( CtiSignalMsg &sig )
{
    {
        UINT ngid = SignalEvent;
        UINT signaltrx = sig.getSignalCategory(); // Alarm Category.

        if(signaltrx > SignalEvent)
        {
            // This is an "alarm" class signal.  It may need to be emailed into the world.

            {
                CtiServerExclusion pmguard(_server_exclusion);

                if( CtiPointSPtr pPoint = PointMgr.getPoint(sig.getId()) )
                {
                    ngid = PointMgr.getAlarming(*pPoint).getNotificationGroupID();
                }
            }

            // Check if point identifies a group to be notified on any signal.
            if( ngid > SignalEvent )
            {
                sendSignalToGroup(ngid, sig);
            }

            // Check if this particular alarm has its own notification group.
            ngid = alarmToNotificationGroup(signaltrx);
            if(ngid != 0)
            {
                sendSignalToGroup(ngid, sig);
            }
        }
    }
}


void CtiVanGogh::loadAlarmToDestinationTranslation()
{
    UINT alm, ngid;
    string name;

    try
    {   // Make sure all objects that that store results are out of scope when the release is called
        CtiServerExclusion guard(_server_exclusion, 5000);

        if(guard.isAcquired())
        {
            static const string sql = "SELECT AC.alarmcategoryid, AC.categoryname, AC.notificationgroupid "
                                      "FROM AlarmCategory AC";

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection);

            rdr.setCommandText(sql);
            rdr.execute();

            while( rdr() ) // there better be Only one in there!
            {
                rdr["alarmcategoryid"] >> alm;
                rdr["categoryname"] >> name;
                rdr["notificationgroupid"] >> ngid;

                _alarmToDestInfo[alm].grpid = ngid;
                _alarmToDestInfo[alm].name = name;
            }

            if( ! rdr.isValid() )
            {
                CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
            }
        }
        else
        {
            CTILOG_WARN(dout, "Alarm to Destination reload unable to acquire exclusion");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    // Make sure no one is not an Event at the least!
    for(alm = 1; alm < MAX_ALARM_TRX; alm++)
    {
        if(_alarmToDestInfo[alm].grpid < SignalEvent)
        {
            _alarmToDestInfo[alm].grpid = SignalEvent;
        }
    }

    return;
}

void CtiVanGogh::checkForStatusAlarms(const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point)
{
    if( ! point.isStatus() )
    {
        return;
    }

    // We must know about the point!
    if( CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point) )
    {
        CtiSignalMsg *pSig = 0;

        for( int alarm = 0; alarm < CtiTablePointAlarming::invalidstatusstate; alarm++ )
        {
            pSig = NULL;       // There is no alarm for this alarmstate.

            // This prohibits re-alarming on the same problem!
            if( PointMgr.getAlarming(point).alarmOn( alarm ) ) // we are set to alarm on this condition (category > SignalEvent).
            {
                switch(alarm)
                {
                case (CtiTablePointAlarming::nonUpdatedStatus):
                    {
                        if(pDyn->getQuality() != NonUpdatedQuality)
                        {
                            activatePointAlarm(alarm,aWrap,point,*pDyn,false);
                        }
                        break;
                    }
                case (CtiTablePointAlarming::abnormal):
                    {
                        break;
                    }
                case (CtiTablePointAlarming::uncommandedStateChange):
                    {
                        checkStatusUCOS(alarm, pData, aWrap, point, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::commandFailure): // ANALOG CASE -> case (CtiTablePointAlarming::rateOfChange):
                    {
                        checkStatusCommandFail(alarm, pData, aWrap, point, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::state0):
                case (CtiTablePointAlarming::state1):
                case (CtiTablePointAlarming::state2):
                case (CtiTablePointAlarming::state3):
                case (CtiTablePointAlarming::state4):
                case (CtiTablePointAlarming::state5):
                case (CtiTablePointAlarming::state6):
                case (CtiTablePointAlarming::state7):
                case (CtiTablePointAlarming::state8):
                case (CtiTablePointAlarming::state9):
                    {
                        checkStatusState(alarm, pData, aWrap, point, *pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::staleStatus): //This is haneled externally, look for processStalePoint(...)
                case (CtiTablePointAlarming::changeOfState):
                default:
                    {
                        break;
                    }
                }

                if(pSig != NULL)
                {
                    aWrap.getMulti()->insert( pSig );
                }
            }
            else
            {
                activatePointAlarm(alarm,aWrap,point,*pDyn,false);
            }
        }

        // We ALWAYS create an occurrence event to stuff a state event log into the systemlog table.
        if( pDyn->getDispatch().getValue() != pData.getValue() )
        {
            string txt = ResolveStateName(point.getStateGroupID(), (int)pData.getValue());
            string addn;

            if(pData.getQuality() == ManualQuality)
            {
                addn = "Manual Update";
            }

            if(pSig)
            {
                CTILOG_ERROR(dout, "pSig is not Null"); // FIXME: where is it set to Null?
            }

            pSig = new CtiSignalMsg(point.getID(), pData.getSOE(), txt, addn);
            pSig->setPointValue(pData.getValue());
            if(pSig != NULL)
            {
                pSig->setUser(pData.getUser());
                pSig->setTags(pDyn->getDispatch().getTags()  & ~SIGNAL_MANAGER_MASK);
                aWrap.getMulti()->insert( pSig );
            }
        }
    }
}

void CtiVanGogh::checkForNumericAlarms(CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point)
{
    if( ! point.isNumeric() )
    {
        return;
    }

    const CtiPointNumeric &pNumeric = static_cast<const CtiPointNumeric &>(point);

    if( CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point) )
    {
        CtiSignalMsg    *pSig = NULL;
        UINT            tags = pDyn->getDispatch().getTags();

        // We check if the point has been sent in as a Manual Update.  If so, we ALWAYS log this occurence.
        if(pData.getQuality() == ManualQuality)
        {
            // If the value has changed, OR the last quality WAS NOT Manual, we then write into the system log!
            if( pDyn->getDispatch().getQuality() != ManualQuality || pDyn->getDispatch().getValue() != pData.getValue() )
            {
                char tstr[80];
                _snprintf(tstr, sizeof(tstr), "Value set to %.3f", pData.getValue());
                string addn = "Manual Update";

                pSig = CTIDBG_new CtiSignalMsg(point.getID(), pData.getSOE(), tstr, addn);
                pSig->setPointValue(pData.getValue());
                if(pSig != NULL)
                {
                    pSig->setTags(pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK);
                    pSig->setUser(pData.getUser());
                    aWrap.getMulti()->insert( pSig );
                    pSig = NULL;
                }
            }
        }

        /*
         *  Check if this pData must be value modified due to reasonability limits.  This must be done first bec. it can alter
         *  the point value passed through to the remaining alarm conditions.
         */
        checkNumericReasonability( pData, aWrap, pNumeric, *pDyn, pSig );

        for( int alarm = 0; alarm < CtiTablePointAlarming::invalidnumericstate; alarm++ )
        {
            pSig = NULL;

            switch(alarm)
            {
            case (CtiTablePointAlarming::limit0):
            case (CtiTablePointAlarming::limit1):
            case (CtiTablePointAlarming::limitLow0):
            case (CtiTablePointAlarming::limitLow1):
            case (CtiTablePointAlarming::limitHigh0):
            case (CtiTablePointAlarming::limitHigh1):
                {
                    checkNumericLimits( alarm, pData, aWrap, pNumeric, *pDyn, pSig );
                    break;
                }
            case (CtiTablePointAlarming::nonUpdatedNumeric):    // alarm generated by commandMsgUpdateFailedHandler().
                {
                    if(pDyn->getQuality() != NonUpdatedQuality)
                    {
                        activatePointAlarm(alarm,aWrap,point,*pDyn,false);
                    }
                }
            case (CtiTablePointAlarming::highReasonability):    // These conditions must be evaluated prior to the for loop.  The may modify pData.
            case (CtiTablePointAlarming::lowReasonability):     // These conditions must be evaluated prior to the for loop.  The may modify pData.
            case (CtiTablePointAlarming::staleNumeric): //This is haneled externally, look for processStalePoint(...)
            case (CtiTablePointAlarming::rateOfChange):
            default:
                {
                    break;
                }
            }

            if(pSig)
            {
                delete pSig;
                pSig = 0;
            }
        }
    }
}

#include <io.h>

INT CtiVanGogh::sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp)
{
    INT status = ClientErrors::None;
    vector<int> group_ids;

    group_ids.push_back(grp.getGroupID());

    auto alarm_msg = std::make_unique<CtiNotifAlarmMsg>(group_ids,
                                                        sig.getSignalCategory(),
                                                        sig.getId(),
                                                        sig.getCondition(),
                                                        sig.getPointValue(),
                                                        sig.getMessageTime(),
                                                        !(sig.getTags() & TAG_UNACKNOWLEDGED_ALARM),
                                                        static_cast<bool>(sig.getTags() & TAG_ACTIVE_ALARM));


    if(gDispatchDebugLevel & DISPATCH_DEBUG_NOTIFICATION)
    {
        CTILOG_DEBUG(dout, "Sending alarm notification"<<
                *alarm_msg);
    }

    if(!getNotificationConnection()->valid())
    {
        CTILOG_WARN(dout, "Connection to notification server is not valid - Alarm notification has been queued");
    }

    getNotificationConnection()->WriteConnQue(std::move(alarm_msg), CALLSITE);

    return status;
}

string CtiVanGogh::getAlarmStateName( INT alarm )
{
    string str;
    CTILOG_ENTRY_RC(dout, "()", str);

    CtiServerExclusion guard(_server_exclusion);
    if( _alarmToDestInfo[alarm].grpid < SignalEvent )  // Zero is invalid!
    {
        // OK, prime the array!
        loadAlarmToDestinationTranslation();
    }
    str = _alarmToDestInfo[alarm].name;
    return str;
}


/*----------------------------------------------------------------------------*
 * This method sweeps the table blasting anyone marked as questionable.
 *----------------------------------------------------------------------------*/
int CtiVanGogh::clientPurgeQuestionables(PULONG pDeadClients)
{
    extern bool isQuestionable(CtiServer::ptr_type &ptr, void* narg);

    int status = ClientErrors::None;
    CTILOG_ENTRY_RC(dout, "()", status);

    CtiServer::ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

    if(MainQueue_.entries() == 0)
    {

        if(pDeadClients != NULL) *pDeadClients = 0;

        while( (Mgr = mConnectionTable.remove(isQuestionable, NULL)) )
        {
            if(pDeadClients != NULL) (*pDeadClients)++;

            CTILOG_WARN(dout, "Client did not respond to AreYouThere message. Client "
                << Mgr->getClientName() <<" on "<< Mgr->getPeer() << " connection id " << Mgr->getConnectionId());

            clientShutdown(Mgr);
        }
    }

    return status;
}


YukonError_t CtiVanGogh::clientRegistration(CtiServer::ptr_type &CM)
{
    YukonError_t nRet = ClientErrors::None;
    CTILOG_ENTRY_RC(dout, "()", nRet);

    CtiTime NowTime;
    bool    validEntry(TRUE);
    bool    questionedEntry(FALSE);
    bool    removeMgr(FALSE);
    CtiServer::ptr_type Mgr;

    CtiServerExclusion guard(_server_exclusion);

    if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
    {
        CTILOG_DEBUG(dout, displayConnections());
    }

    CTILOG_INFO(dout, "Validating "<< CM->getClientName() <<" / "<< CM->getClientAppId() <<" / "<< CM->getPeer());

    /*
     *  OK, now we need to check if the registration jives with our other client connections!
     *  if not, we will not allow this guy to have any operations though us.
     */

    CtiServer::spiterator  itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if((Mgr != CM))      // Not me loser.
        {
            if(Mgr->getClientName() == CM->getClientName())
            {
                // Names match, what do I do.
                if(CM->getClientAppId() && Mgr->getClientAppId() == CM->getClientAppId())
                {
                    // This guy is already registered. Might have lost his connection??
                    CTILOG_WARN(dout, CM->getClientName() << " just re-registered");

                    removeMgr = TRUE;
                    break;
                }
                else if(Mgr->getClientUnique())
                {
                    if( Mgr->getClientQuestionable() )       // has this guy been previously pinged and not responded?
                    {
                        CTILOG_WARN(dout, CM->getClientName() <<" / "<< CM->getClientAppId() <<" / "<< CM->getPeer() <<" just won a client arbitration");

                        removeMgr = TRUE;    // Make the old one go away...
                        break;
                    }
                    else
                    {
                        Mgr->setClientQuestionable(TRUE);      // Mark the old guy as needing confirmation (also causes eventual cleanup if he doesn't respond.)
                        Mgr->setClientRegistered(TRUE);        // Mark the old guy as having been previously registered

                        CtiCommandMsg *pCmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15);

                        pCmd->setSource(getMyServerName());
                        pCmd->setOpString(CompileInfo.version);

                        CTILOG_DEBUG(dout, "Sending AreYouThere to " << Mgr->getClientName());
                        Mgr->WriteConnQue( pCmd, CALLSITE, 500 );   // Ask the old guy to respond to us..
                        CM->setClientRegistered(FALSE); // New guy is not quite kosher yet...

                        questionedEntry = TRUE;
                        validEntry = TRUE;

                        // Old one wanted to be the only one
                        CTILOG_WARN(dout, "New client \""<< CM->getClientName() <<"\" conflicts with an existing client");
                    }

                    break; // the for
                }
                else if(CM->getClientUnique())
                {
                    // New one wanted to be the only one
                    CTILOG_ERROR(dout, "New client is not unique as requested");

                    validEntry = FALSE;
                    break; // the for
                }
            }
        }
    }

    if(validEntry)
    {
        if(!questionedEntry)
        {
            CM->setClientRegistered(TRUE);
        }
    }
    else
    {
        // For some reason, the connection has been refused. Shut it down...
        CTILOG_WARN(dout, "Connection rejected - Entry will be deleted");

        CM->WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::Shutdown, 15 ), CALLSITE, 500 );  // Ask the new guy to blow off..

        clientShutdown(CM);
    }

    if(removeMgr && Mgr)
    {
        CTILOG_WARN(dout, Mgr->getClientName() <<" / "<< Mgr->getClientAppId() <<" / "<< Mgr->getPeer() <<" just _lost_ the client arbitration");

        // This connection manager is abandoned now...
        clientShutdown(Mgr);
    }

    return nRet;
}

/*----------------------------------------------------------------------------*
 * The client CM (who must be an older connection) has just responded that he's
 * the man and the other guy which made him become questionable should be
 * blown away,.. find him.
 *----------------------------------------------------------------------------*/
int  CtiVanGogh::clientArbitrationWinner(CtiServer::ptr_type &CM)
{
    int status = ClientErrors::None;
    CTILOG_ENTRY_RC(dout, "()", status);

    CtiServer::ptr_type Mgr;

    CtiServerExclusion guard(_server_exclusion);

    // Now that it is locked, get an iterator
    CtiServer::spiterator  itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if((Mgr != CM)                                     &&       // Don't match on yourself
           (Mgr->getClientName() == CM->getClientName())   &&       // Names match
           (Mgr->getClientRegistered() == false))                   // Other Mgr is not registered completely yet
        {
            CTILOG_WARN(dout, "Connection "<< Mgr->getClientName() <<" on "<< Mgr->getPeer() <<" has been blocked by a prior client of the same name - Dispatch will shut it down now");

            Mgr->WriteConnQue( CTIDBG_new CtiCommandMsg( CtiCommandMsg::Shutdown, 15 ), CALLSITE, 500 );  // Ask the new guy to blow off..

            clientShutdown(Mgr);
            break;
        }
    }

    return status;
}

void CtiVanGogh::messageDump(CtiMessage *pMsg)
{
    if(gDispatchDebugLevel & DISPATCH_DEBUG_MESSAGES)
    {
        string msgtype;
        switch( pMsg->isA() )
        {
        case MSG_POINTREGISTRATION:
            {
                msgtype = "MSG_POINTREGISTRATION";
                break;
            }
        case MSG_REGISTER:
            {
                msgtype = "MSG_REGISTER";
                break;
            }
        case MSG_POINTDATA:
            {
                msgtype = "MSG_POINTDATA";
                break;
            }
        case MSG_SIGNAL:
            {
                msgtype = "MSG_SIGNAL";
                break;
            }
        case MSG_DBCHANGE:      // Everyone gets these, no matter what!
            {
                msgtype = "MSG_DBCHANGE";
                break;
            }
        case MSG_COMMAND:
            {
                msgtype = "MSG_COMMAND";
                break;
            }
        case MSG_PCRETURN:
        case MSG_MULTI:
            {
                msgtype = "MSG_PCRETURN/MSG_MULTI";
                break;
            }
        default:
            {
                msgtype = "UNKNOWN";
            }
        }

        CTILOG_INFO(dout, "Dispatch has preprocessed a "<< msgtype <<" message"<<
                *pMsg);
    }

    return;
}

void CtiVanGogh::loadRTDB(bool force, CtiMessage *pMsg)
{
    LONG id = 0;
    CtiTime Now;
    static CtiTime Refresh = CtiTime();
    CtiDBChangeMsg *pChg = (CtiDBChangeMsg *)pMsg;
    ULONG   deltaT;

    if(pChg != NULL && pChg->getSource() == DISPATCH_APPLICATION_NAME)
    {
        // Don't reload if we sourced it.
        // If we let this happen, we can end in an endless reload loop.
        return;
    }

    try
    {
        if(Now > Refresh || force)   // Should be 5 minutes or greater
        {
            if( pChg )
            {
                CTILOG_INFO(dout, "Starting loadRTDB, DBChange " << 
                    pChg->getCategory() << " " << pChg->getId() << " " <<
                        (pChg->getTypeOfChange() == ChangeTypeAdd    ? "ADD" :
                         pChg->getTypeOfChange() == ChangeTypeDelete ? "DELETE" :
                         pChg->getTypeOfChange() == ChangeTypeUpdate ? "UPDATE" : "UNKNOWN"));
            }
            else
            {
                CTILOG_INFO(dout, "Starting loadRTDB, no DBChange present.");
            }

            // This loads up the points that VanGogh will manage.
            if( pChg == NULL || ((pChg->getDatabase() == ChangePointDb) ||
               (pChg->getDatabase() == ChangePAODb && pChg->getTypeOfChange() == ChangeTypeAdd)) )
            {
                CtiServerExclusion pmguard(_server_exclusion, 10000);
                if(pmguard.isAcquired())
                {
                    if(pChg != NULL && pChg->getDatabase() == ChangePAODb && pChg->getTypeOfChange() == ChangeTypeAdd )
                    {
                        PointMgr.updatePoints(0, pChg->getId());
                    }
                    else if(pChg != NULL && (pChg->getTypeOfChange() == ChangeTypeUpdate || pChg->getTypeOfChange() == ChangeTypeAdd))
                    {
                        boost::optional<long> preloadTags;

                        if( const auto pt = PointMgr.getCachedPoint(pChg->getId()) )
                        {
                            if( const auto dyn = PointMgr.getDynamic(*pt) )
                            {
                                preloadTags = dyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK;
                            }
                        }

                        PointMgr.updatePoints(pChg->getId(), 0, resolvePointType(pChg->getObjectType()) );

                        if( preloadTags )
                        {
                            if( const auto pt = PointMgr.getCachedPoint(pChg->getId()) )
                            {
                                if( const auto dyn = PointMgr.getDynamic(*pt) )
                                {
                                    if( *preloadTags != (dyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) )
                                    {
                                        sendTagUpdate(dyn->getDispatch(), "Point tags change on DB reload", pChg->getUser());
                                    }
                                }
                            }
                        }
                    }
                    else if(pChg != NULL && pChg->getTypeOfChange() == ChangeTypeDelete)
                    {
                        PointMgr.erase(pChg->getId());
                    }
                    else if(pChg == NULL)
                    {
                        if(gConfigParms.isTrue("DISPATCH_LOAD_ALL_POINTS"))
                        {
                            CTILOG_INFO(dout, "DISPATCH_LOAD_ALL_POINTS is set");

                            PointMgr.refreshList();
                            PointMgr.loadAllStaticData();
                        }
                        else
                        {
                            CTILOG_INFO(dout, "DISPATCH_LOAD_ALL_POINTS is not set");

                            PointMgr.loadAllStaticData();
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "LoadRTDB point loading entered an unknown state "<< pChg->getDatabase() <<" "<< pChg->getTypeOfChange());
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "Could not acquire server exclusion to perform point reload");
                }
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CTILOG_INFO(dout, deltaT <<" seconds to collect point data from database...");
            }

            Now = Now.now();

            if( pChg == NULL || (pChg->getDatabase() == ChangeAlarmCategoryDb) )
            {
                // Update our Bookkeeping data!!!
                loadAlarmToDestinationTranslation();
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CTILOG_INFO(dout, deltaT <<" seconds to load alarm to destination translations");
            }
            Now = Now.now();

            if( pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb) )
            {
                ReloadStateNames();
            }
            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CTILOG_INFO(dout, deltaT <<" seconds to load state names");
            }

            if( pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) )
            {
                CtiServerExclusion guard(_server_exclusion, 5000);

                if(guard.isAcquired())
                {
                    bool deviceDisabled = false, controlInhibited = false;
                    id = ((pChg == NULL) ? 0 : pChg->getId());

                    if(pChg && pChg->getTypeOfChange() == ChangeTypeDelete)
                    {
                        // The device has been deleted.  Knock down all the device lites for a reload!!
                        CTILOG_INFO(dout, "Device delete for PAO id " << pChg->getId());

                        _deviceLites.erase(pChg->getId());

                        // erase all associated points for the pao id
                        PointMgr.erasePao(pChg->getId());
                    }
                    else if(pChg && pChg->getTypeOfChange() == ChangeTypeUpdate)
                    {
                        if( auto optDevLite = Cti::mapFindRef(_deviceLites, id) )
                        {
                            deviceDisabled   = optDevLite->isDisabled();
                            controlInhibited = optDevLite->isControlInhibited();
                        }
                    }



                    Now = Now.now();
                    loadDeviceLites(id);
                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CTILOG_INFO(dout, deltaT <<" seconds to load lite device data");
                    }

                    Now = Now.now();
                    string username = pChg ? pChg->getUser() : string("Dispatch Application");

                    // If it is an update and the disable flag has changed, call adjust.
                    // On add, deviceDisabled and controlInhibited == false.
                    // If the device has disable or inhibited set on add, we call adjust.
                    if(pChg && (pChg->getTypeOfChange() == ChangeTypeUpdate || pChg->getTypeOfChange() == ChangeTypeAdd))
                    {
                        if( auto optDevLite = Cti::mapFindRef(_deviceLites, id ) )
                        {
                            if( dout->isLevelEnable(Cti::Logging::Logger::Debug) )
                            {
                                Cti::FormattedList l;
                                l.add("Device name") << optDevLite->getName();
                                l.add("Device ID")   << optDevLite->getID();
    
                                l.add("Is disabled")          << optDevLite->isDisabled();
                                l.add("Is control inhibited") << optDevLite->isControlInhibited();

                                l.add("Previously disabled")          << deviceDisabled;
                                l.add("Previously control inhibited") << controlInhibited;

                                CTILOG_DEBUG(dout, l)
                            }

                            if( deviceDisabled   != optDevLite->isDisabled() ||
                                controlInhibited != optDevLite->isControlInhibited() )
                            {
                                CTILOG_DEBUG(dout, "Adjusting device disable tags for device ID " << id);

                                adjustDeviceDisableTags(id, true, username);
                            }
                            else
                            {
                                CTILOG_DEBUG(dout, "No change to device disable tags for device ID " << id);
                            }
                        }
                    }

                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CTILOG_INFO(dout, deltaT <<" seconds to adjust disabled status of devices");
                    }
                }
                else
                {
                    CTILOG_WARN(dout, "Device lite info was not reloaded. Exclusion could not be obtained");
                }
            }

            Now = Now.now();

            if(pChg != NULL && (pChg->getDatabase() == ChangeNotificationGroupDb || pChg->getDatabase() == ChangeNotificationRecipientDb))
            {
                CtiServerExclusion guard(_server_exclusion, 1000);

                while(!guard.isAcquired() && !guard.tryAcquire(5000))
                {
                    CTILOG_WARN(dout, "Unable to acquire exclusion for notification group load. Will try again..");
                }

                if(guard.isAcquired())
                {
                    // We will own that mutex following at this point->
                    if(pChg->getDatabase() == ChangeNotificationGroupDb)
                    {
                        CTILOG_INFO(dout, "Notification Groups will be reloaded on next usage");

                        // Group or destinations have changed!
                        for( auto &kv : _notificationGroups )
                        {
                            CtiTableNotificationGroup &theGroup = kv.second;
                            theGroup.setDirty(true);
                        }
                    }
                }
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CTILOG_INFO(dout, deltaT << " seconds to adjust notification groups");
            }

            Now = Now.now();
            refreshCParmGlobals(force);

            CTILOG_INFO(dout, "Done loading RTDB...");

            Refresh = nextScheduledTimeAlignedOnRate( Now, gDispatchReloadRate );
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    if(pChg != NULL)
    {
        delete pChg;
    }
}

/*
 *  returns name of the device which owns this point.
 */
string CtiVanGogh::resolveDeviceName(const CtiPointBase &aPoint)
{
    string rStr;
    const long devid = aPoint.getDeviceID();

    if(devid > 0)
    {
        if( auto optDevLite = findDeviceLite(devid) )
        {
            rStr = optDevLite->getName();
        }
    }

    return rStr;
}

/*
 *  returns name of the device.
 */
string CtiVanGogh::resolveDeviceNameByPaoId(const LONG PAOId)
{
    string rStr;
    if(PAOId > 0)
    {
        if( auto optDevLite = findDeviceLite(PAOId) )
        {
            rStr = optDevLite->getName();
        }
    }

    return rStr;
}

string CtiVanGogh::resolveDeviceObjectType(const LONG devid)
{
    string rStr;

    if(devid > 0)
    {
        if( auto optDevLite = findDeviceLite(devid) )
        {
            // dliteit should be an iterator which represents the lite device now!
            rStr = optDevLite->getObjectType();
        }
    }

    return rStr;
}

bool CtiVanGogh::isDeviceGroupType(const LONG devid)
{
    if(devid > 0)
    {
        try
        {
            if( auto optDevLite = findDeviceLite(devid) )
            {
                return optDevLite->isGroup();
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    return false;
}


/*
 *  This method loads all pao objects which "could" have control.. This may need change in the future.
 *
 *  Expects protections to be performed by someone else.
 */
void CtiVanGogh::loadDeviceLites(LONG id)
{
    if(DebugLevel & 0x00010000)
    {
        CTILOG_DEBUG(dout, "Loading DeviceLites");
    }

    const string sql = DeviceBaseLite().getSQLCoreStatement(id);

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    if(id)
    {
        rdr << id;
    }

    rdr.execute();

    while( rdr() )
    {
        DeviceBaseLite dLite;
        dLite.DecodeDatabaseReader(rdr);

        _deviceLites[dLite.getID()] = dLite;
    }

    if(DebugLevel & 0x00010000)
    {
        CTILOG_DEBUG(dout, "Done Loading DeviceLites");
    }
}

// Next figure out the translation from alarm group to notification group.
LONG CtiVanGogh::alarmToNotificationGroup(INT signaltrx)
{
    LONG ngid;

    {
        CtiServerExclusion guard(_server_exclusion);
        if( _alarmToDestInfo[signaltrx].grpid < SignalEvent )  // Zero is invalid!
        {
            // OK, prime the array!
            loadAlarmToDestinationTranslation();
        }

        ngid = _alarmToDestInfo[signaltrx].grpid;
    }
    return ngid;
}

void CtiVanGogh::sendSignalToGroup(LONG ngid, const CtiSignalMsg& sig)
{
    CtiServerExclusion guard(_server_exclusion);

    auto git = _notificationGroups.find( ngid );

    if( git == _notificationGroups.end() )
    {
        CtiTableNotificationGroup mygroup( ngid );

        // We need to load it up, and then insert it!
        mygroup.Restore();

        auto resultpair = _notificationGroups.emplace( ngid, mygroup );

        if(resultpair.second == true)
        {
            git = resultpair.first;
        }
    }

    if( git != _notificationGroups.end() )
    {
        // git should be an iterator which represents the group now!
        CtiTableNotificationGroup &theGroup = git->second;

        if(theGroup.isDirty())
        {
            CTILOG_INFO(dout, "Reloading Notification Group "<< theGroup.getGroupName());
            theGroup.Restore();     // Clean the thing then!
        }

        sendMail(sig, theGroup);
    }
}

std::string CtiVanGogh::displayConnections()
{
    Cti::StreamBuffer sb;

    CtiServer::ptr_type Mgr;
    CtiServerExclusion guard(_server_exclusion);
    CtiServer::spiterator  itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;
        sb << endl << Mgr->getClientName() <<" / "<<  Mgr->getClientAppId() <<" / "<< Mgr->getPeer() <<" "<< (Mgr->isViable() ? "is Viable" : "is NOT Viable");
    }

    return sb;
}

void CtiVanGogh::sendTagUpdate(const CtiTablePointDispatch &dispatch, const std::string &addnl, const std::string &user)
{
    CtiSignalMsg tagSig{ dispatch.getPointID(), 0, "Tag Update", addnl };
    
    tagSig.setPointValue(dispatch.getValue());
    tagSig.setMessagePriority(15);
    tagSig.setUser(user);
    tagSig.setTags(dispatch.getTags() & ~SIGNAL_MANAGER_MASK);

    postMessageToClients(&tagSig);
}

/********************
 *  This method uses the dynamic dispatch POINT settings to determine if the inbound setmask and tagmask indicate a change
 *  from the current settings.  If a change is indicated, the static tags of the point will be updated.
 */
bool CtiVanGogh::ablementPoint(const CtiPointBase &point, bool &devicedifferent, UINT setmask, UINT tagmask, string user, CtiMultiMsg &Multi)
{
    bool different = false;

    devicedifferent = false;        // Make it false by default.

    CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
    
    Cti::FormattedList l;
    l.add("Point ID") << point.getID();
    l.add("Device ID") << point.getDeviceID();
    l.add("Point name") << point.getName();
    l.add("pDyn found") << !!pDyn;
    l.add("pDyn tags") << (pDyn ? pDyn->getDispatch().getTags() : 0);
    l.add("setmask") << setmask;
    l.add("tagmask") << tagmask;

    if( ! pDyn )
    {
        CTILOG_ERROR(dout, "pDyn not found" << l);
    }
    else
    {
        CTILOG_DEBUG(dout, l);

        UINT currtags  = (pDyn->getDispatch().getTags() & (tagmask & MASK_ANY_DISABLE));        // All (dev & pnt) ablement tags.
        UINT newpttags = (setmask & (tagmask & MASK_ANY_DISABLE));

        UINT currpttags = (currtags & (tagmask & MASK_ANY_POINT_DISABLE));                      // Point only ablement tags.
        UINT pttags   = (setmask & (tagmask & MASK_ANY_POINT_DISABLE));

        UINT currdvtags = (currtags & (tagmask & MASK_ANY_DEVICE_DISABLE));                     // Device only ablement tags.
        UINT dvtags   = (setmask & (tagmask & MASK_ANY_DEVICE_DISABLE));

        string addnl;
        UINT filter = 0x00000001;
        UINT currtags_cnt = 0;
        UINT newpttags_cnt = 0;
        UINT currpttags_cnt = 0;
        UINT pttags_cnt = 0;
        UINT currdvtags_cnt = 0;
        UINT dvtags_cnt = 0;

        for(int pos = 0; pos < 32; pos++)
        {
            filter = filter << 1;

            if(currtags & filter)   currtags_cnt++;         // This is the number of bite - currently.
            if(newpttags & filter)  newpttags_cnt++;        // This is the number of bits - after complete.
            if(currpttags & filter) currpttags_cnt++;       // This is the number of POINT tags - currently.
            if(pttags & filter)     pttags_cnt++;           // This is the number of POINT tags - after complete.
            if(currdvtags & filter) currdvtags_cnt++;       // This is the number of DEVICE tags - currently.
            if(dvtags & filter)     dvtags_cnt++;           // This is the number of DEVICE tags - after complete.
        }

        if( currtags != newpttags )      // Is anything different?
        {
            different = true;

            if(currpttags != pttags)    // Is the difference in the point tags?
            {
                addnl += string("Point tags: ") + (pttags_cnt > currpttags_cnt ? " point disable" : " point enable");

                if( ! updatePointStaticTables(point.getPointID(), pttags, tagmask, user, Multi) )
                {
                    CTILOG_ERROR(dout, "Could not update point static table");
                }
                else
                {
                    CTILOG_INFO(dout, "Updated point enablement status on point ID " << point.getID());
                }
            }
            else
            {
                CTILOG_DEBUG(dout, "No point enablement change on point ID " << point.getID());
            }

            if(currdvtags != dvtags)    // Is the difference in the device tags?
            {
                addnl += string("Point tags: ") + (dvtags_cnt > currdvtags_cnt ? " device disable" : " device enable");
                devicedifferent = true;
            }
            else
            {
                CTILOG_DEBUG(dout, "No device enablement change on point ID " << point.getID());
            }

            pDyn->getDispatch().resetTags(tagmask);
            pDyn->getDispatch().setTags(newpttags);

            sendTagUpdate(pDyn->getDispatch(), addnl, user);
        }
    }

    return different;
}

/*
 *  This method sets the value of the "ablement" information in the lite map.  I will cause an update of the static data
 *  if a change occurs.
 *
 *  tagmask decides which tags are looked at on the device.
 *  setmask decides what those bits are to be set to.
 *   they will only be marked as "delta" if the setting was not already in effect.
 */
bool CtiVanGogh::ablementDevice(DeviceBaseLite &dLite, UINT setmask, UINT tagmask)
{
    bool delta = false;     // Anything changed???

    if( dout->isLevelEnable(Cti::Logging::Logger::Debug) )
    {
        Cti::FormattedList l;
        l.add("Device ID") << dLite.getID();
        l.add("Device name") << dLite.getID();
        l.add("Device disabled") << dLite.isDisabled();
        l.add("Device control inhibited") << dLite.isControlInhibited();
        l.add("setmask") << setmask;
        l.add("tagmask") << tagmask;
        CTILOG_DEBUG(dout, l);
    }
        
    if( tagmask & TAG_DISABLE_DEVICE_BY_DEVICE )
    {
        bool initialsetting = dLite.isDisabled();
        dLite.setDisableFlag(TAG_DISABLE_DEVICE_BY_DEVICE & setmask);
        delta |= (initialsetting != dLite.isDisabled());
    }

    if( tagmask & TAG_DISABLE_CONTROL_BY_DEVICE )
    {
        bool initialsetting = dLite.isControlInhibited();
        dLite.setControlInhibitFlag(TAG_DISABLE_CONTROL_BY_DEVICE & setmask);
        delta |= (initialsetting != dLite.isControlInhibited());
    }

    if( tagmask & TAG_DISABLE_ALARM_BY_DEVICE )
    {
        CTILOG_WARN(dout, "Unexpected tagmask TAG_DISABLE_ALARM_BY_DEVICE");
    }

    return delta;
}

CtiVanGogh::CtiVanGogh(CtiPointClientManager& externalMgr, Cti::Test::use_in_unit_tests_only&)
    :   CtiVanGogh( &externalMgr )
{}

CtiVanGogh::CtiVanGogh() 
    :   CtiVanGogh( nullptr )
{}

CtiVanGogh::CtiVanGogh(CtiPointClientManager* externalMgr)
    :   _localPointClientMgr{
                externalMgr 
                    ? nullptr 
                    : std::make_unique<CtiPointClientManager>() },
        PointMgr{
                externalMgr 
                    ? *externalMgr 
                    : *_localPointClientMgr },
        _pendingOpThread{PointMgr},
        _notificationConnection(NULL),
        _listenerConnection( Cti::Messaging::ActiveMQ::Queue::dispatch ),
        ShutdownOnThreadTimeout { true },
        _rphArchiver { ShutdownOnThreadTimeout, &CtiVanGogh::sendbGCtrlC }
{
    {
        CtiServerExclusion guard(_server_exclusion);
        for(int i = 0; i < MAX_ALARM_TRX; i++)
        {
            _alarmToDestInfo[i].grpid = 0; // Zero is invalid!
        }
    }
    ShutdownOnThreadTimeout = true;
}

void  CtiVanGogh::shutdown()
{
    CTILOG_INFO(dout, "Dispatch Server Shutting Down");

    _tagManager.interrupt(CtiThread::SHUTDOWN);
    _tagManager.join();

    try
    {
        shutdownAllClients();
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}


void CtiVanGogh::shutdownAllClients()
{
    CtiServer::ptr_type Mgr;
    CtiServer::spiterator itr;

    CtiServerExclusion guard(_server_exclusion);

    while((itr = mConnectionTable.getMap().begin()) != mConnectionTable.getMap().end() )
    {
        Mgr = itr->second;
        clientShutdown(Mgr); // expected to remove first element from mConnectionTable
    }
}


/******************************************************************************
*   Monitor the applications that are registered in thread_monitor.h and MUST
*   have those system points defined. Function will wait for 15 minutes then
*   make sure it has heard from every device every 15 minutes or so.
******************************************************************************/
void CtiVanGogh::VGAppMonitorThread()
{
    CtiTime NextThreadMonitorReportTime;
    CtiTime nextCPULoadReportTime;

    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;
    CtiPointDataMsg vgStatusPoint;
    long pointID = ThreadMonitor.getProcessPointID();
    Cti::ServiceMetrics::MetricReporter metricReporter {
        Cti::ServiceMetrics::CpuPointOffsets::Dispatch,
        Cti::ServiceMetrics::MemoryPointOffsets::Dispatch,
        "Dispatch",
        DISPATCH_APPLICATION_NAME };

    CTILOG_INFO(dout, "Dispatch Application Monitor Thread starting");

    //on startup wait for 10 minutes!
    for(int i=0;i<120 && !bGCtrlC;i++)
    {
        Sleep(5000);//5 second sleep

        if(NextThreadMonitorReportTime.now() > NextThreadMonitorReportTime)
        {
            CtiMessage* pData = (CtiMessage *)CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str());
            pData->setSource(DISPATCH_APPLICATION_NAME);
            MainQueue_.putQueue(pData);

            NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate(CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2);
        }

        metricReporter.reportCheck(CompileInfo, MainQueue_);
    }

    if(!bGCtrlC)
    {
        const auto pointIDList = ThreadMonitor.getAllProcessPointIDs();
        CtiTime compareTime;

        while(!bGCtrlC)
        {
            compareTime = CtiTime::now();
            compareTime -= 600;//take away 10 minutes

            for(auto pointId : pointIDList)
            {
                // This call probably hits the database.
                if( auto pPt = PointMgr.getPoint(pointId) )
                {
                    if( auto pDynPt = PointMgr.getDynamic(*pPt) )
                    {
                        if((pDynPt->getTimeStamp()).seconds()<(compareTime))
                        {
                            //its been more than 15 minutes, set the alarms!!!
                            auto pData = std::make_unique<CtiPointDataMsg>(pointId, CtiThreadMonitor::Dead, NormalQuality, StatusPointType, "Thread has not responded for 15 minutes.");
                            pData->setSource(DISPATCH_APPLICATION_NAME);
                            MainQueue_.putQueue(pData.release());
                        }
                    }
                }
            }

            //no need to process very often, wait say.... randomly ill pick 3 minutes or so
            for(int i=0;i<=36 && !bGCtrlC;i++)
            {
                Sleep(5000);//5 second sleep

                metricReporter.reportCheck(CompileInfo, MainQueue_);

                //Check thread watcher status
                if(pointID!=0)
                {
                    CtiThreadMonitor::State next = ThreadMonitor.getState();

                    if( NextThreadMonitorReportTime.now() > NextThreadMonitorReportTime || next != previous)
                    {
                        NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate(CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2);
                        previous = next;

                        auto pData = std::make_unique<CtiPointDataMsg>(pointID, next, NormalQuality, StatusPointType, ThreadMonitor.getString());
                        pData->setSource(DISPATCH_APPLICATION_NAME);
                        MainQueue_.putQueue(pData.release());
                    }
                }
            }
        }
    }

    CTILOG_INFO(dout, "Dispatch Application Monitor Thread shutting down");
}


/*----------------------------------------------------------------------------*
 * This FUNCTION is used to apply against the MainQueue_ with the expectation
 * that some number of them have a pointer to the to-be-deleted connection.
 * That connection in the mainqueue must be nullified.
 *----------------------------------------------------------------------------*/
void ApplyBlankDeletedConnection(CtiMessage *Msg, void *ConnId)
{
    if( Msg->getConnectionHandle().getConnectionId() == reinterpret_cast<long>(ConnId) )
    {
        CTILOG_WARN(dout, "Msg on MainQueue found which refers to a connection which no longer exists. Removing reference, msg will be processed "<< *Msg);

        Msg->setConnectionHandle(Cti::ConnectionHandle::none);
    }
}

boost::optional<DeviceBaseLite &> CtiVanGogh::findDeviceLite(const LONG paoId)
{
    CtiServerExclusion guard(_server_exclusion, 30000);

    if(guard.isAcquired())
    {
        if( auto optDev = Cti::mapFindRef(_deviceLites, paoId) )
        {
            return *optDev;
        }

        DeviceBaseLite &dLite = DeviceBaseLite(paoId);

        // We need to load it up, and/or then insert it!
        if(dLite.Restore())
        {
            // Try to insert. Return indicates success.
            auto resultpair = _deviceLites.emplace( paoId, dLite );

            if(resultpair.second == true)
            {
                return resultpair.first->second;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Unable to aqcuire the _server_exclusion, owned by TID " << _server_exclusion.lastAcquiredByTID());
    }

    return boost::none;
}


void CtiVanGogh::reportOnThreads()
{
    try
    {
        CtiServerExclusion sguard(_server_exclusion, 100);

        if( ! sguard.isAcquired())
        {
            // OK, we just had a potential deadlock prevention here...
            return;
        }

        boost::posix_time::milliseconds shake(0);

        if( ! _rphArchiver.isRunning() )
        {
            CTILOG_ERROR(dout, "RawPointHistory Writer Thread is not running");
        }

        if( _archiveThread.timed_join(shake) )
        {
            CTILOG_ERROR(dout, "Archiver Thread is not running");
        }

        if( _timedOpThread.timed_join(shake) )
        {
            CTILOG_ERROR(dout, "Timed Operation Thread is not running");
        }

        if( _dbSigThread.timed_join(shake) )
        {
            CTILOG_ERROR(dout, "DB Signal Thread is not running");
        }

        if( _dbSigEmailThread.timed_join(shake) )
        {
            CTILOG_ERROR(dout, "DB Signal Email Thread is not running");
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

bool CtiVanGogh::writeControlMessageToPIL(LONG deviceid, LONG rawstate, const CtiPointStatus &statusPt, const CtiCommandMsg *Cmd )
{
    string  cmdstr;

    const boost::optional<CtiTablePointStatusControl> controlParameters = statusPt.getControlParameters();

    if( controlParameters &&
        controlParameters->getStateZeroControl().length() > 0 &&
        controlParameters->getStateOneControl().length() > 0 )
    {
        cmdstr += ((rawstate == UNCONTROLLED) ? controlParameters->getStateZeroControl(): controlParameters->getStateOneControl() );
    }
    else
    {
        cmdstr += string("Control ");
        cmdstr += ResolveStateName(statusPt.getStateGroupID(), rawstate);
    }

    // Do this before we append the point name, since the point name may include "control"
    bool control = stringContainsIgnoreCase(cmdstr, "control ");

    cmdstr += string(" select pointid " + CtiNumStr(statusPt.getPointID()));

    std::unique_ptr<CtiRequestMsg> pReq(
        new CtiRequestMsg(deviceid, cmdstr));

    pReq->setUser( Cmd->getUser() );
    pReq->setMessagePriority( MAXPRIORITY - 1 );

    writeMessageToClient(pReq.release(), string(PIL_REGISTRATION_NAME));

    return control;
}

void CtiVanGogh::writeAnalogOutputMessageToPIL(long deviceid, long pointid, long value, const CtiCommandMsg *Cmd)
{
    string cmdstr;

    cmdstr += "putvalue analog value ";
    cmdstr += CtiNumStr(value);

    cmdstr += " select pointid ";
    cmdstr += CtiNumStr(pointid);

    std::unique_ptr<CtiRequestMsg> pReq(
        new CtiRequestMsg(deviceid, cmdstr));

    pReq->setUser( Cmd->getUser() );
    pReq->setMessagePriority( MAXPRIORITY - 1 );

    writeMessageToClient(pReq.release(), string(PIL_REGISTRATION_NAME));
}

void CtiVanGogh::writeMessageToClient(const CtiMessage *pReq, string clientName)
{
    CtiServer::ptr_type CM;
    bool bDone = false;
    {
        CtiServerExclusion guard(_server_exclusion);
        CtiServer::spiterator  itr;

        for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
        {
            CM = itr->second;

            if(CM->getClientName() == clientName)
            {
                if(CM->WriteConnQue( pReq->replicateMessage(), CALLSITE, 5000 ))
                {
                    CTILOG_ERROR(dout, "Message to Porter was unable to be queued"<<
                            pReq);
                }

                if(bDone)
                {
                    CTILOG_INFO(dout, "Multiple PIL entries in dispatch list. Will submit request on ALL entries");
                }

                bDone = true;
            }
        }
    }

    if(!bDone)
    {
        CTILOG_ERROR(dout, "Unable to submit control command. Port Control Interface not currently registered with dispatch - Control request discarded"<<
                pReq
                );
    }
}

void CtiVanGogh::bumpDeviceToAlternateRate(const CtiPointBase &point)
{
    if( ! point.isPseudoPoint() )
    {
        std::unique_ptr<CtiCommandMsg> pAltRate(
           new CtiCommandMsg(CtiCommandMsg::AlternateScanRate));

        int controlTimeout = DefaultControlExpirationTime;

        if( point.isStatus() )
        {
            const CtiPointStatus &status = static_cast<const CtiPointStatus &>(point);

            if( const boost::optional<CtiTablePointStatusControl> controlParameters = status.getControlParameters() )
            {
                controlTimeout = controlParameters->getCommandTimeout();
            }
        }

        pAltRate->insert(-1); // token
        pAltRate->insert( point.getDeviceID() );
        pAltRate->insert( -1 );                     // Seconds since midnight, or NOW if negative.
        pAltRate->insert( controlTimeout );

        writeMessageToScanner(pAltRate.release());

        CTILOG_INFO(dout, "Requesting scans at the alternate scan rate for "<< resolveDeviceName(point));
    }
}

void CtiVanGogh::bumpDeviceFromAlternateRate(const CtiPointBase &point)
{
    if( ! point.isPseudoPoint() )
    {
        std::unique_ptr<CtiCommandMsg> pAltRate(
           new CtiCommandMsg( CtiCommandMsg::AlternateScanRate));

        pAltRate->insert(-1); // token
        pAltRate->insert( point.getDeviceID() );
        pAltRate->insert( -1 );                     // Seconds since midnight, or NOW if negative.
        pAltRate->insert( 0 );                      // Stop it already!

        writeMessageToScanner(pAltRate.release());

        CTILOG_INFO(dout, "Requesting scans at the normal scan rate for "<< resolveDeviceName(point));
    }
}

void CtiVanGogh::writeMessageToScanner(const CtiCommandMsg *Cmd)
{
    // this guy goes to scanner only
    CtiServer::ptr_type scannerCM = getScannerConnection();

    if(scannerCM)
    {
        // pass the message through
        if(scannerCM->WriteConnQue( Cmd->replicateMessage(), CALLSITE, 5000 ))
        {
            CTILOG_ERROR(dout, "unable to write message to scanner"<<
                    *Cmd);
        }
    }
}

bool CtiVanGogh::updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, string user, CtiMultiMsg &sigList)
{
    bool paobjectSuccess = false;
    bool deviceSuccess = false;

    string objtype = resolveDeviceObjectType(did);

    CtiServerExclusion smguard(_server_exclusion, 10000);
    if(smguard.isAcquired())
    {
        Cti::Database::DatabaseConnection   conn;

        {
            static const std::string sql_pao_update = "update yukonpaobject set disableflag = ? where paobjectid = ?";

            Cti::Database::DatabaseWriter   updater(conn, sql_pao_update);

            updater
                << ( TAG_DISABLE_DEVICE_BY_DEVICE & setmask ? std::string("Y") : std::string("N") )
                << did;

            paobjectSuccess = Cti::Database::executeUpdater( updater, CALLSITE );
        }

        {
            static const std::string sql_device_update = "update device set controlinhibit = ? where deviceid = ?";

            Cti::Database::DatabaseWriter   updater(conn, sql_device_update);

            updater
                << ( TAG_DISABLE_CONTROL_BY_DEVICE & setmask ? std::string("Y") : std::string("N") )
                << did;

            deviceSuccess = Cti::Database::executeUpdater( updater, CALLSITE );
        }
    }

    if ( paobjectSuccess || deviceSuccess )
    {
        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(did, ChangePAODb, "Device", objtype, ChangeTypeUpdate);
        dbChange->setUser(user);
        dbChange->setSource(DISPATCH_APPLICATION_NAME);
        sigList.insert(dbChange);
    }

    return paobjectSuccess && deviceSuccess;
}

bool CtiVanGogh::updatePointStaticTables(LONG pid, UINT setmask, UINT tagmask, string user, CtiMultiMsg &Multi)
{
    using namespace Cti::Database;

    bool pointSuccess = false;
    bool pointStatusSuccess = false;

    CtiServerExclusion smguard(_server_exclusion, 10000);
    if(smguard.isAcquired())
    {
        if (TAG_DISABLE_POINT_BY_POINT & tagmask)
        {
            static const std::string sql_point_update = "update point set serviceflag = ? where pointid = ?";

            DatabaseConnection   conn;
            DatabaseWriter       updater(conn, sql_point_update);

            updater
                << ( TAG_DISABLE_POINT_BY_POINT & setmask ? "Y" : "N" )
                << pid;

            pointSuccess = executeUpdater( updater, CALLSITE );
        }

        if (TAG_DISABLE_CONTROL_BY_POINT & tagmask)
        {
            static const std::string sql_pointstatus_update = "update pointcontrol set controlinhibit = ? where pointid = ?";

            DatabaseConnection   conn;
            DatabaseWriter       updater(conn, sql_pointstatus_update);

            updater
                << ( TAG_DISABLE_CONTROL_BY_POINT & setmask ? "Y" : "N" )
                << pid;

            //  no error if the point doesn't have a control table entry
            pointStatusSuccess = executeUpdater( updater, CALLSITE, LogDebug(isDebugLudicrous()), LogNoRowsAffected::Disable );
        }
    }

    if ( pointSuccess || pointStatusSuccess )
    {
        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(pid, ChangePointDb, "Point", "Point", ChangeTypeUpdate);
        dbChange->setUser(user);
        dbChange->setSource(DISPATCH_APPLICATION_NAME);
        dbChange->setMessagePriority(15);
        Multi.insert(dbChange);

        return true;
    }

    return false;
}

/**
 * This method attempts to set all device "ablement" information.
 * A key item to remember is that this information _really_ only exists on the _points_ which dispatch tracks.
 *
 * @param id LONG
 * @param dbchange bool
 * @param user string
 */
void CtiVanGogh::adjustDeviceDisableTags(LONG id, bool dbchange, string user)
{
    if(!_deviceLites.empty())
    {
        bool devicedifferent = false;

        UINT tagmask = TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_CONTROL_BY_DEVICE;

        {
            auto pMulti = std::make_unique<CtiMultiMsg>();

            {
                pMulti->setUser(user);
                pMulti->setSource(DISPATCH_APPLICATION_NAME);

                /**
                 * It is assumed the tags are correct for devices on startup!
                 * The only checking needs to happen on individual devices when
                 * they change!
                 */

                if(id != 0)
                {
                    vector<CtiPointManager::ptr_type> points;
                    PointMgr.getEqualByPAO(id, points);

                    CTILOG_DEBUG(dout, "Setting point ablements for Device ID " << id << "'s " << points.size() << " point(s)");

                    if( auto optDevLite = findDeviceLite(id) )
                    {
                        const DeviceBaseLite &dLite = *optDevLite;

                        for( const auto& pPoint : points )
                        {
                            if( pPoint )
                            {
                                UINT setmask = 0;
                                setmask |= (dLite.isDisabled() ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                                setmask |= (dLite.isControlInhibited() ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                                ablementPoint(*pPoint, devicedifferent, setmask, tagmask, user, *pMulti);
                            }
                            else
                            {
                                CTILOG_ERROR(dout, "pPoint null for device ID " << id);
                            }
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "device not found for device ID " << id);
                    }
                }

                if(devicedifferent && !dbchange)
                {
                    if( auto optDevLite = findDeviceLite(id) )   // We do know this device..
                    {
                        DeviceBaseLite &dLite = *optDevLite;

                        UINT setmask = 0;

                        setmask |= (dLite.isDisabled() ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                        setmask |= (dLite.isControlInhibited() ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                        if( ! updateDeviceStaticTables(dLite.getID(), setmask, tagmask, user, *pMulti) )
                        {
                            CTILOG_ERROR(dout, "Could not update device static table");
                        }
                        else
                        {
                            CTILOG_INFO(dout, "Updated "<< dLite.getName() <<"'s device enablement status");
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "device not found for device ID " << id);
                    }
                }

                if(pMulti->getData().size())
                {
                    MainQueue_.putQueue(pMulti.release());
                }
            }
        }
    }
}

void CtiVanGogh::checkNumericReasonability(CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch &dpd, CtiSignalMsg *&pSig )
{
    int alarm = ClientErrors::None;
    string text;
    CtiPointClientManager::ReasonabilityLimitStruct limits = PointMgr.getReasonabilityLimits(pointNumeric);

    // Relatively arbitrary, but should be ok.
    const double MaxHighReasonability =  1e30;
    const double MinLowReasonability  = -1e30;

    try
    {

        if(limits.highLimit != limits.lowLimit &&       // They must be different.
           limits.highLimit >  limits.lowLimit )
        {
            // Evaluate High Limit
            if(limits.highLimit < MaxHighReasonability)  // Is the reasonability reasonable?
            {
                alarm = CtiTablePointAlarming::highReasonability;
                double val = pData.getValue();

                if(val > limits.highLimit)
                {
                    pData.setValue( dpd.getValue() );          // Value of the CtiPointDataMsg must be be modified.
                    pData.setQuality( UnreasonableQuality );

                    if(!_signalManager.isAlarmedOrConditionActive(pointNumeric.getPointID(), alarm))
                    {
                        {
                            char tstr[120];
                            _snprintf(tstr, sizeof(tstr), "Reasonability Limit Exceeded High. %.3f > %.3f", val, limits.highLimit);
                            text = tstr;
                        }

                        if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                        {
                            CTILOG_DEBUG(dout, "HIGH REASONABILITY Violation, Point: "<< pointNumeric.getName() <<" "<< text);
                        }

                        const CtiTablePointAlarming alarming = PointMgr.getAlarming(pointNumeric);

                        pSig = new CtiSignalMsg(pointNumeric.getID(), pData.getSOE(), text, getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::highReasonability) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::highReasonability), pData.getUser());
                        pSig->setPointValue(pData.getValue());
                    }
                    else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
                    {
                        activatePointAlarm(alarm, aWrap, pointNumeric, dpd, true);
                    }

                    // This is an alarm if the alarm state indicates anything other than SignalEvent.
                    if(pSig)
                    {
                        tagSignalAsAlarm(pointNumeric, pSig, alarm, &pData);
                        updateDynTagsForSignalMsg(pointNumeric,pSig);
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
                else
                {
                    activatePointAlarm(alarm,aWrap,pointNumeric,dpd,false);
                }
            }

            if(limits.lowLimit > MinLowReasonability)  // Is the reasonability reasonable?
            {
                alarm = CtiTablePointAlarming::lowReasonability;
                double val = pData.getValue();

                if(val < limits.lowLimit)
                {
                    pData.setValue( dpd.getValue() );          // Value of the CtiPointDataMsg must be be modified.
                    pData.setQuality( UnreasonableQuality );

                    if(!_signalManager.isAlarmedOrConditionActive(pointNumeric.getPointID(), alarm))
                    {
                        {
                            char tstr[120];
                            _snprintf(tstr, sizeof(tstr), "Reasonability Limit Exceeded Low. %.3f < %.3f", val, limits.lowLimit);
                            text = tstr;
                        }

                        if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                        {
                            CTILOG_DEBUG(dout, "LOW REASONABILITY Violation, Point: "<< pointNumeric.getName() <<" "<< text);
                        }

                        const CtiTablePointAlarming &alarming = PointMgr.getAlarming(pointNumeric);

                        pSig = new CtiSignalMsg(pointNumeric.getID(), pData.getSOE(), text, getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::lowReasonability) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::lowReasonability), pData.getUser());
                        pSig->setPointValue(pData.getValue());
                    }
                    else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
                    {
                        activatePointAlarm(alarm, aWrap, pointNumeric, dpd, true);
                    }

                    // This is an alarm if the alarm state indicates anything other than SignalEvent.
                    if(pSig)
                    {
                        tagSignalAsAlarm(pointNumeric, pSig, alarm, &pData);
                        updateDynTagsForSignalMsg(pointNumeric,pSig);
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
                else
                {
                    activatePointAlarm(alarm,aWrap,pointNumeric, dpd,false);
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

void CtiVanGogh::checkNumericLimits(int alarm, CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch &dpd, CtiSignalMsg *&pSig )
{
    string text;

    double  val = pData.getValue();
    INT     numericAlarmOffset = (alarm - CtiTablePointAlarming::limit0); // This gives a base 0 (limit0 alarm = 0) for the alarms for later use with pending operations
    INT     limitnumber = getNumericLimitFromHighLow(numericAlarmOffset, alarm) +1; //Currently returns 0 or 1 for limit 0 or 1
    INT     exceeds = LIMIT_IN_RANGE;

    try
    {
        const CtiTablePointLimit limit = PointMgr.getPointLimit(pointNumeric, limitnumber);

        if(limit.getLowLimit() != -DBL_MAX && limit.getHighLimit() != DBL_MAX && limitStateCheck(alarm, limit, val, exceeds))
        {
            if(!_signalManager.isAlarmedOrConditionActive(pointNumeric.getPointID(), alarm))
            {
                INT duration = limit.getLimitDuration();

                if(exceeds == LIMIT_EXCEEDS_LO)
                {
                    char tstr[120];
                    if( alarm >= CtiTablePointAlarming::limitLow0 && alarm <= CtiTablePointAlarming::limitHigh1 )
                    {
                        _snprintf(tstr, sizeof(tstr), "Low Limit %d Exceeded. %.3f < %.3f", alarm - CtiTablePointAlarming::limitLow0 + 1, val, limit.getLowLimit());
                    }
                    else
                    {
                        _snprintf(tstr, sizeof(tstr), "Limit %d Exceeded Low. %.3f < %.3f", limitnumber, val, limit.getLowLimit());
                    }
                    text = tstr;
                }
                else if(exceeds == LIMIT_EXCEEDS_HI)
                {
                    char tstr[120];
                    if( alarm >= CtiTablePointAlarming::limitLow0 && alarm <= CtiTablePointAlarming::limitHigh1 )
                    {
                        _snprintf(tstr, sizeof(tstr), "High Limit %d Exceeded. %.3f > %.3f", alarm - CtiTablePointAlarming::limitHigh0 + 1, val, limit.getHighLimit());
                    }
                    else
                    {
                        _snprintf(tstr, sizeof(tstr), "Limit %d Exceeded High. %.3f > %.3f", limitnumber, val, limit.getHighLimit());
                    }
                    text = tstr;
                }
                else if(exceeds == LIMIT_SETUP_ERROR)
                {
                    {
                        char tstr[120];
                        _snprintf(tstr, sizeof(tstr), "Limit %d Invalid Setup. Is %.3f < %.3f < %.3f?", limitnumber, limit.getLowLimit(), val, limit.getHighLimit());
                        text = tstr;
                    }

                    CTILOG_ERROR(dout, "Invalid limit setup");
                }
                else
                {
                    CTILOG_ERROR(dout, "Unknown cause for limit violation");
                }

                const CtiTablePointAlarming &alarming = PointMgr.getAlarming(pointNumeric);

                pSig = new CtiSignalMsg(pointNumeric.getID(), pData.getSOE(), text, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData.getUser());
                pSig->setPointValue(pData.getValue());
                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                tagSignalAsAlarm(pointNumeric, pSig, alarm, &pData);

                if(duration > 0)  // Am I required to hold in this state for a bit before the announcement of this condition?
                {
                    auto pendingPointLimit = std::make_unique<CtiPendingPointOperations>(pointNumeric.getID());
                    pendingPointLimit->setType(CtiPendingPointOperations::pendingLimit + numericAlarmOffset);
                    pendingPointLimit->setLimitBeingTimed( numericAlarmOffset );
                    pendingPointLimit->setTime( CtiTime() );
                    pendingPointLimit->setLimitDuration( duration );
                    pendingPointLimit->setSignal( pSig );
                    pSig = 0;   // Don't let it get put in the Wrapper because it is now in the pending list!

                    // If there is a limit duration, we modify the data message, so clients don't immediately know that this point is in a pending alarm.
                    pData.resetTags( TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM );

                    addToPendingSet(std::move(pendingPointLimit));

                    CTILOG_INFO(dout, "LIMIT Violation, " << text << ": " << pData.getString());
                }
                else
                {
                    updateDynTagsForSignalMsg(pointNumeric,pSig);
                }
            }
            else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
            {
                activatePointAlarm(alarm, aWrap, pointNumeric, dpd, true);
            }
        }
        else
        {
            if( limit.getLowLimit() != -DBL_MAX && limit.getHighLimit() != DBL_MAX )    // No limits set, no limits can be exceeded!
            {
                // Remove any possible pending limit violator.
                CtiPendable *pPend = new CtiPendable(pData.getId(), CtiPendable::CtiPendableAction_RemoveLimit);
                pPend->_limit = numericAlarmOffset;
                _pendingOpThread.push( pPend );

                activatePointAlarm(alarm,aWrap,pointNumeric,dpd,false);
            }
        }

        if(pSig)
        {
            // tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);
            // updateDynTagsForSignalMsg(pointNumeric,pSig,alarm,true);
            aWrap.getMulti()->insert( pSig );
            pSig = 0;
        }

    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

INT CtiVanGogh::getNumericLimitFromHighLow(int numericAlarmOffset, int alarm)
{
    INT retVal = numericAlarmOffset;
    if( alarm >= CtiTablePointAlarming::limitLow0 && alarm <= CtiTablePointAlarming::limitHigh1 )
    {
        switch(alarm)
        {
            case CtiTablePointAlarming::limitLow0:
            case CtiTablePointAlarming::limitHigh0:
            {
                retVal = CtiTablePointAlarming::limit0 - CtiTablePointAlarming::limit0;
                break;
            }
            case CtiTablePointAlarming::limitLow1:
            case CtiTablePointAlarming::limitHigh1:
            {
                retVal = CtiTablePointAlarming::limit1 - CtiTablePointAlarming::limit0;
                break;
            }
        }
    }

    return retVal;
}

void CtiVanGogh::checkStatusUCOS(int alarm, const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    UINT tags = 0;
    if(pDyn)
    {
        tags = pDyn->getDispatch().getTags();
    }

    // Pseudo points cannot have feedback, therefore cannot have UCOS.
    // Control must be available to care about UCOS
    // Control must not be expected to have UCOS (unexpected is the same as NOT PENDING)
    if(pDyn && !point.isPseudoPoint() && (tags & TAG_ATTRIB_CONTROL_AVAILABLE) && !(tags & TAG_CONTROL_PENDING))
    {
        // Well, we were NOT expecting a change, so make sure the values match
        if( pDyn->getDispatch().getValue() != pData.getValue())
        {
            // Values don't match and we weren't expecting a change!
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
            {
                CTILOG_DEBUG(dout, "Uncommended State Change");
            }

            const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

            // OK, we have an actual alarm condition to gripe about!
            pSig = new CtiSignalMsg(point.getID(), pData.getSOE(), string( "UCOS: " + ResolveStateName(point.getStateGroupID(), (int)pData.getValue())), getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData.getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
            pSig->setPointValue(pData.getValue());

            // This is an alarm if the alarm state indicates anything other than SignalEvent.
            tagSignalAsAlarm(point, pSig, alarm, &pData);
            updateDynTagsForSignalMsg(point,pSig);

            pSig->resetTags( TAG_ACTIVE_ALARM );
            pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
            pDyn->getDispatch().setTags( _signalManager.getTagMask(point.getID()) );
        }
    }
}

void CtiVanGogh::checkStatusState(int alarm, const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatch &dpd, CtiSignalMsg *&pSig )
{
    string action;
    double val = pData.getValue();
    INT alarmValue = (alarm - CtiTablePointAlarming::state0); // The value we want to alarm on

    INT exceeds = LIMIT_IN_RANGE;

    bool signal = false;

    if(CtiTablePointAlarming::state0 <= alarm && alarm <= CtiTablePointAlarming::state9)
    {
        if( PointMgr.getAlarming(point).getAlarmCategory(alarm) > SignalEvent)
        {
            // check if our value == the value we want to alarm on
            signal = ((int)val == alarmValue);
        }
    }

    if( signal )
    {
        if(!_signalManager.isAlarmedOrConditionActive(point.getPointID(), alarm))
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
            {
                CTILOG_DEBUG(dout, "STATE Violation"<<
                        endl <<"Point: "<< point.getID() <<" "<< ResolveStateName(point.getStateGroupID(), (int)pData.getValue()));
            }

            string tstr(ResolveStateName(point.getStateGroupID(), (int)pData.getValue()));

            const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

            // OK, we have an actual alarm condition to gripe about!
            pSig = new CtiSignalMsg(point.getID(), pData.getSOE(), tstr, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData.getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
            pSig->setPointValue(pData.getValue());
            // This is an alarm if the alarm state indicates anything other than SignalEvent.
            tagSignalAsAlarm(point, pSig, alarm, &pData);
            updateDynTagsForSignalMsg(point,pSig);
        }
        else if(!_signalManager.isAlarmActive(point.getID(), alarm))
        {
            activatePointAlarm(alarm,aWrap,point,dpd,true);
        }
    }
    else
    {
        activatePointAlarm(alarm,aWrap,point,dpd,false);
    }
}

void CtiVanGogh::tagSignalAsAlarm( const CtiPointBase &point, CtiSignalMsg *&pSig, int alarm, const CtiPointDataMsg *pData )
{
    // If pSig is non-NULL, this "alarm" condition occurred and we need to decide if the point goes into alarm over it.
    if(pSig != NULL)
    {
        UINT tags = 0;
        const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
        const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

        // We now need to check if this "alarm" is a real alarm ( > SignalEvent level )
        if(alarming.getAlarmCategory(alarm) > SignalEvent)
        {
            pSig->setLogType(AlarmCategoryLogType);
            tags |= TAG_ACTIVE_ALARM | (alarming.isAutoAcked(alarm) ? 0 : TAG_UNACKNOWLEDGED_ALARM);
        }
        else
        {
            pSig->setSignalCategory(SignalEvent);
        }

        tags |= TAG_ACTIVE_CONDITION;
        if(pDyn)
        {
            pSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | tags);   // They are equal here!
        }
        pSig->setCondition(alarm);

        if(pData)    // If is "pushed" into the alarm condition, let's label it that way.
        {
            pSig->setUser(pData->getUser());

            if(pData->getQuality() == ManualQuality)
            {
                pSig->setAdditionalInfo("Manual Update: " + pSig->getAdditionalInfo());
            }
        }
    }
}

void CtiVanGogh::updateDynTagsForSignalMsg( const CtiPointBase &point, CtiSignalMsg *&pSig )
{
    // If pSig is non-NULL, this "alarm" condition occurred and we need to decide if the point goes into alarm over it.
    if(pSig != NULL)
    {
        _signalManager.addSignal(*pSig);
    }

    if( CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point) )
    {
        pDyn->getDispatch().resetTags(SIGNAL_MANAGER_MASK);
        pDyn->getDispatch().setTags(_signalManager.getTagMask(point.getID()));
    }
}

void CtiVanGogh::activatePointAlarm(int alarm, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatch &dpd, bool activate )
{
    //  Is the signal active?
    if( CtiSignalMsg *pSigActive = _signalManager.setAlarmActive(point.getID(), alarm, activate) )
    {
        dpd.getDispatch().resetTags( SIGNAL_MANAGER_MASK );
        dpd.getDispatch().setTags( _signalManager.getTagMask(point.getID()) );

        unsigned sigtags = (dpd.getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | TAG_REPORT_MSG_TO_ALARM_CLIENTS;

        //  If we're activating the alarm, we've already sent an alarm signal via
        //    updateDynTagsForSignalMsg(), so we should block this one from sending anything
        if(activate)
        {
            sigtags = TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL;
        }
        //  If we're deactivating, and:
        //    * any of TAG_UNACKNOWLEDGED_ALARM, TAG_ACTIVE_ALARM, or TAG_ACTIVE_CONDITION are set
        //    OR
        //    * we do not notify on clear
        //  then block the email.
        else if( (dpd.getDispatch().getTags() & SIGNAL_MANAGER_MASK) || !PointMgr.getAlarming(point).getNotifyOnClear() )
        {
            sigtags |= TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL;
        }

        pSigActive->setTags( sigtags );
        pSigActive->setText( TrimAlarmTagText((string&)pSigActive->getText()) + AlarmTagsToString(pSigActive->getTags()) );
        pSigActive->setMessageTime( CtiTime() );

        aWrap.getMulti()->insert( pSigActive );
        pSigActive = 0;
    }
}

void CtiVanGogh::acknowledgeCommandMsg( const CtiPointBase &point, const CtiCommandMsg *Cmd, int alarmcondition )
{
    CtiSignalMsg *pSigNew = 0;

    if(alarmcondition == -1)
    {
        for(alarmcondition = 0; alarmcondition < 32; alarmcondition++)
        {
            acknowledgeAlarmCondition( point, Cmd, alarmcondition );
        }
    }
    else
    {
        acknowledgeAlarmCondition( point, Cmd, alarmcondition );
    }

}

void CtiVanGogh::acknowledgeAlarmCondition( const CtiPointBase &point, const CtiCommandMsg *Cmd, int alarmcondition )
{
    CtiSignalMsg *pSigNew = 0;

    {
        CtiServerExclusion pmguard(_server_exclusion);
        pSigNew = _signalManager.setAlarmAcknowledged(point.getPointID(), alarmcondition, true);    // Clear the tag, return the signal!

        if(pSigNew)
        {
            if( CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point) )
            {
                pSigNew->setUser( Cmd->getUser() );

                unsigned sigtags = (pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | TAG_REPORT_MSG_TO_ALARM_CLIENTS | TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL;

                bool almclear = (_signalManager.getTagMask(point.getPointID()) & MASK_ANY_ALARM) == 0; // true if no bits are set.

                const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

                if( ( almclear && alarming.getNotifyOnClear() ) || alarming.getNotifyOnAcknowledge())
                {
                    sigtags &= ~TAG_DO_NOT_SEND_SIGNAL_AS_EMAIL;   // Remove this to send the emails.
                }

                pSigNew->setTags( sigtags );
                pSigNew->setText( TrimAlarmTagText((string&)pSigNew->getText()) + AlarmTagsToString(pSigNew->getTags()) );

                if( !alarming.getNotifyOnAcknowledge() )
                {
                    pSigNew->setMessageTime( Cmd->getMessageTime() );
                }

                pSigNew->setMessagePriority( 15 );   // Max this out we want it to hurry.

                // Mark it if there are other alarms on the point.
                UINT premask = pDyn->getDispatch().getTags( ) & SIGNAL_MANAGER_MASK;
                UINT amask = _signalManager.getTagMask(point.getPointID());

                if(premask != amask)
                {
                    // Adjust the point tags to reflect the potentially new state of the alarm tags.
                    pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
                    pDyn->getDispatch().setTags( amask );

                    CtiPointDataMsg *pTagDat = CTIDBG_new CtiPointDataMsg(point.getID(), pDyn->getValue(), pDyn->getQuality(), point.getType(), "Tags Updated", pDyn->getDispatch().getTags());

                    pTagDat->setSource(DISPATCH_APPLICATION_NAME);
                    pTagDat->setTime(pDyn->getTimeStamp());
                    pTagDat->setMessagePriority(15);

                    MainQueue_.putQueue( pTagDat );
                }

                if(isDebugLudicrous())
                {
                    Cti::FormattedList itemList;

                    itemList.add("Signal has tags") << pSigNew->getTags() <<" "<< explainTags(pSigNew->getTags());
                    itemList.add("SigMgr has tags") << amask <<" "<< explainTags(amask);
                    itemList.add("Point  has tags") << pDyn->getDispatch().getTags() <<" "<< explainTags(pDyn->getDispatch().getTags());

                    CTILOG_DEBUG(dout, itemList);
                }
            }
            else
            {
                if(pSigNew)
                    delete pSigNew;

                pSigNew = 0;
            }
        }
    }

    if(pSigNew != NULL)
    {
        // Make sure that anyone who cared about the first one gets the new state of the tag!
        postMessageToClients(pSigNew);
        queueSignalToSystemLog(pSigNew);
    }
}

void CtiVanGogh::processTagMessage(CtiTagMsg &tagMsg)
{
    const auto resultAction = _tagManager.processTagMsg(tagMsg);

    if( resultAction == CtiTagManager::Actions::None )
    {
        return;
    }

    const long id       = tagMsg.getPointID();

    if( CtiPointSPtr pPt = PointMgr.getPoint(id) )
    {
        auto pMulti = std::make_unique<CtiMultiMsg>();

        pMulti->setSource(DISPATCH_APPLICATION_NAME);
        pMulti->setUser(tagMsg.getUser());

        const int  tagmask  = TAG_DISABLE_CONTROL_BY_POINT;
        const int  setmask  =
                (resultAction == CtiTagManager::Actions::PointControlInhibit) //  set or clear the DISABLE_CONTROL_BY_POINT tag?
                    ? TAG_DISABLE_CONTROL_BY_POINT
                    : 0;

        bool devicedifferent;

        ablementPoint(*pPt, devicedifferent, setmask, tagmask, tagMsg.getUser(), *pMulti);

        if( devicedifferent )     // The device became interesting because of this change.
        {
            CTILOG_INFO(dout, "Device enabled/disabled change due to Command to pointid "<< id);
        }

        if(pMulti->getData().size())
        {
            MainQueue_.putQueue(pMulti.release());
        }
    }
}

void CtiVanGogh::VGDBSignalWriterThread()
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("DB Signal Writer Thread");

    CTILOG_INFO(dout, "Dispatch DB Signal Writer Thread starting");

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!bGCtrlC;)
        {
            try
            {
                if( ShutdownOnThreadTimeout )
                {
                    threadStatus.monitorCheck(&CtiVanGogh::sendbGCtrlC);
                }
                else
                {
                    threadStatus.monitorCheck(CtiThreadRegData::None);
                }

                Sleep(1000);
                writeSignalsToDB();
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }

        // Make sure no one snuck in under the wire..
        writeSignalsToDB(true);
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    // And let'em know were A.D.
    CTILOG_INFO(dout, "Dispatch DB Signal Writer Thread shutting down");

    return;
}

void CtiVanGogh::VGDBSignalEmailThread()
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("DB Signal Email Thread");

    CTILOG_INFO(dout, "Dispatch DB Signal Email Thread starting");

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!bGCtrlC;)
        {
            try
            {
                if( ShutdownOnThreadTimeout )
                {
                    threadStatus.monitorCheck(&CtiVanGogh::sendbGCtrlC);
                }
                else
                {
                    threadStatus.monitorCheck(CtiThreadRegData::None);
                }

                CtiSignalMsg *sigMsg = 0;

                while(0 != (sigMsg = _signalMsgPostQueue.getQueue(1000)))
                {
                    postSignalAsEmail( *sigMsg );
                    delete sigMsg;
                    sigMsg = 0;
                }
            }
            catch(...)
            {
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    // And let'em know were A.D.
    CTILOG_INFO(dout, "Dispatch DB Signal Email Thread shutting down");

    return;
}

void CtiVanGogh::updateGroupPseduoControlPoint(const CtiPointBase &point, const CtiTime &delaytime)
{
    if( point.isPseudoPoint() )  // The delayed update is needed in this case!
    {
        if(gDispatchDebugLevel & DISPATCH_DEBUG_DELAYED_UPDATE)
        {
            CTILOG_DEBUG(dout, resolveDeviceName(point) <<" / "<< point.getName() <<" has been scheduled/reloaded for delayed update at "<< delaytime);
        }

        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( point.getPointID(), (DOUBLE)UNCONTROLLED, NormalQuality, StatusPointType, string(resolveDeviceNameByPaoId(point.getDeviceID()) + " restoring (delayed)"), TAG_POINT_DELAYED_UPDATE | TAG_POINT_FORCE_UPDATE);
        pData->setSource(DISPATCH_APPLICATION_NAME);
        pData->setTime( delaytime );
        pData->setMessagePriority( pData->getMessagePriority() - 1 );

        MainQueue_.putQueue( pData );
    }
}


bool CtiVanGogh::processInputFunction(CHAR Char)
{
    bool process_fail = false;
    PSZ Environment;

    try
    {
        switch(Char)
        {
        case 0x68:              // alt - h
        case 0x3f:              // alt - ?
            {
                // TODO: Print some instructions
                break;
            }
        case 0x6d:              // alt-m trace filter.
            {
                _CrtDumpMemoryLeaks();
                break;
            }
        case 0x71:              // alt-q
            {
                CTILOG_INFO(dout, "Vangogh mainqueue has "<< MainQueue_.entries() <<" entries");
                break;
            }
        case 0x74:              // alt-t
            {
                CtiServer::ptr_type Mgr;

                {
                    CtiServer::spiterator  itr;

                    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
                    {
                        Mgr = itr->second;

                        CTILOG_INFO(dout,  Mgr->outQueueCount() <<" outqueue entries on connection to "<< Mgr->getName() <<" / "<< Mgr->who());
                    }
                }
                break;
            }
        case 0x72:              // alt-r
            {
                Cti::FormattedList itemList;
                itemList <<"Vangogh mainqueue has "<< MainQueue_.entries() <<" entries";
                itemList <<"Instance count";
                itemList.add("CtiLMControlHistoryMsg") << CtiLMControlHistoryMsg::getInstanceCount();
                itemList.add("CtiPointdataMsg")        << CtiPointDataMsg::getInstanceCount();
                itemList.add("CtiSignalMsg")           << CtiSignalMsg::getInstanceCount();

                CTILOG_INFO(dout, itemList);

                break;
            }
        case 0x64:              // alt-d
            {
                Cti::FormattedList itemList;
                itemList <<"Vangogh mainqueue has "<< MainQueue_.entries() <<" entries";
                itemList.add("MSG_TRACE")               << msgCounts.get(MSG_TRACE);
                itemList.add("MSG_COMMAND")             << msgCounts.get(MSG_COMMAND);
                itemList.add("MSG_REGISTER")            << msgCounts.get(MSG_REGISTER);
                itemList.add("MSG_SERVER_REQUEST")      << msgCounts.get(MSG_SERVER_REQUEST);
                itemList.add("MSG_SERVER_RESPONSE")     << msgCounts.get(MSG_SERVER_RESPONSE);
                itemList.add("MSG_POINTREGISTRATION")   << msgCounts.get(MSG_POINTREGISTRATION);
                itemList.add("MSG_DBCHANGE")            << msgCounts.get(MSG_DBCHANGE);
                itemList.add("MSG_PCREQUEST")           << msgCounts.get(MSG_PCREQUEST);
                itemList.add("MSG_PCRETURN")            << msgCounts.get(MSG_PCRETURN);
                itemList.add("MSG_MULTI")               << msgCounts.get(MSG_MULTI);
                itemList.add("MSG_TAG")                 << msgCounts.get(MSG_TAG);
                itemList.add("MSG_POINTDATA")           << msgCounts.get(MSG_POINTDATA);
                itemList.add("MSG_SIGNAL")              << msgCounts.get(MSG_SIGNAL);
                itemList.add("MSG_LMCONTROLHISTORY")    << msgCounts.get(MSG_LMCONTROLHISTORY);

                for(int i = 1; i <= 15; i++)
                {
                    itemList <<"Priority "<< i << " has " << msgPrioritys.get(i) << endl;
                }

                for(int i = 1; i <= 15; i++)
                {
                    if(i <= 10)
                        itemList <<"Message times "<< i*50 <<" ms = "<< msgTimes.get(i);
                    else
                        itemList <<"Message times "<< 1000 + ((i-11)*1000) <<" ms = "<< msgTimes.get(i);
                }

                CTILOG_INFO(dout, itemList);

                break;
            }
        case 0x65:              // alt-e
            {
                msgCounts.resetAll();
                msgPrioritys.resetAll();
                break;
            }
        case 0x66:              // alt-f trace filter.
            {
                CtiMessage *pMsg;
                while(0 != (pMsg = MainQueue_.getQueue(500)))
                {
                    CTILOG_INFO(dout, *pMsg);
                    delete pMsg;
                }

                break;
            }
        case 0x6c:              // alt-l
            {
            }
        case 0x63:              // alt-c
            {
            }
        case 0x6b:              // alt-k
            {
            }
        case 0x73:              // alt-s
            {
            }
        case 0x70:              // alt-p
            {
            }
        default:
            process_fail = true;
            break;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return process_fail;
}

bool processExecutionTime(UINT ms)
{
    bool ret = false;

    static int cnt = 0;

    if(ms < 50 )
    {
        msgTimes.inc(1);
    }
    else if(50 <= ms && ms < 100)
    {
        msgTimes.inc(2);
    }
    else if(100 <= ms && ms < 150)
    {
        msgTimes.inc(3);
    }
    else if(150 <= ms && ms < 200)
    {
        msgTimes.inc(4);
    }
    else if(200 <= ms && ms < 250)
    {
        msgTimes.inc(5);
    }
    else if(250 <= ms && ms < 300)
    {
        msgTimes.inc(6);
    }
    else if(300 <= ms && ms < 350)
    {
        msgTimes.inc(7);
    }
    else if(350 <= ms && ms < 400)
    {
        msgTimes.inc(8);
    }
    else if(400 <= ms && ms < 450)
    {
        msgTimes.inc(9);
    }
    else if(500 <= ms && ms < 1000)
    {
        msgTimes.inc(10);
    }
    else if(1000 <= ms && ms < 2000)
    {
        msgTimes.inc(11);
    }
    else if(2000 <= ms && ms < 3000)
    {
        msgTimes.inc(12);
    }
    else if(3000 <= ms && ms < 4000)
    {
        msgTimes.inc(13);
    }
    else if(4000 <= ms && ms < 5000)
    {
        msgTimes.inc(14);
    }
    else if(5000 <= ms)
    {
        msgTimes.inc(15);

        if(5000 <= ms)
        {
            ret = true;
        }
    }

    return ret;
}


string CtiVanGogh::getMyServerName() const
{
    return string("Dispatch Server");
}


void CtiVanGogh::checkStatusCommandFail(int alarm, const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    if(pDyn)
    {
        UINT tags = pDyn->getDispatch().getTags();

        // We can only care about failure if we are a status/control point
        // and someone has sent out a command.  Otherwise this is irrelevant.
        if(tags & TAG_ATTRIB_CONTROL_AVAILABLE)
        {
            CtiPendable *pendable = new CtiPendable(pData.getId(), CtiPendable::CtiPendableAction_ControlStatusComplete, NULL, pData.getTime() );

            pendable->_value = pData.getValue();
            pendable->_tags = tags;
            _pendingOpThread.push( pendable );

            if(tags & TAG_CONTROL_PENDING)                                          // Are we still awaiting the start of control?
            {
                pDyn->getDispatch().resetTags( TAG_CONTROL_PENDING );               // We got to the desired state, no longer pending.. we are now controlling!

                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
                {
                    if(pDyn->getValue() == pData.getValue())                       // Not changing, must be a control refresh
                    {
                        CTILOG_INFO(dout, resolveDeviceName(point) <<" / "<< point.getName() <<" CONTROL CONTINUATION/COMPLETE");
                    }
                    else                                                            // Changing this time through.  Control begins now!
                    {
                        CTILOG_INFO(dout, resolveDeviceName(point) <<" / "<< point.getName() <<" has gone CONTROL COMPLETE");
                    }
                }
            }
        }
    }
}

bool CtiVanGogh::addToPendingSet(std::unique_ptr<CtiPendingPointOperations> &&pendingOp, CtiTime &updatetime)
{
    _pendingOpThread.push(CTIDBG_new CtiPendable(pendingOp->getPointID(), CtiPendable::CtiPendableAction_Add, std::move(pendingOp), updatetime));
    return true;
}

bool CtiVanGogh::removePointDataFromPending( LONG pID )
{
    _pendingOpThread.push( CTIDBG_new CtiPendable(pID, CtiPendable::CtiPendableAction_RemovePointData) );
    return true;
}

void CtiVanGogh::queueSignalToSystemLog( CtiSignalMsg *&pSig )
{
    // Set the logID so that the dynamicData can know it now!
    pSig->setLogID(SystemLogIdGen());

    _signalMsgQueue.putQueue(pSig); // Queue it for a write to the DB!
    pSig = 0;

    return;
}


void CtiVanGogh::stopDispatch()
{
    bGCtrlC = TRUE;     // set this flag so vangogh main thread signals the rest to shutdown.  YUK-8884

    try
    {
        _listenerConnection.close();
    }
    catch(...)
    {
        // Dont really care, we are shutting down.
    }
    shutdown();                   // Shutdown the server object.

    // Interrupt the CtiThread based threads.
    _pendingOpThread.interrupt(CtiThread::SHUTDOWN);
    _rphArchiver.interrupt();
    _cacheHandlerThread1.interrupt();
    _cacheHandlerThread2.interrupt();
    _cacheHandlerThread3.interrupt();
    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);

    if( ! _connThread.timed_join(boost::posix_time::seconds(30)) )
    {
        CTILOG_WARN(dout, "Terminating connection thread");
        TerminateThread(_connThread.native_handle(), EXIT_SUCCESS);
    }
    _pendingOpThread.join();
    if( ! _archiveThread.timed_join(boost::posix_time::seconds(30)) )
    {
        CTILOG_WARN(dout, "Terminating archive thread");
        TerminateThread(_archiveThread.native_handle(), EXIT_SUCCESS);
    }
    if( ! _dbSigThread.timed_join(boost::posix_time::seconds(30)) )
    {
        CTILOG_WARN(dout, "Terminating dbsig thread");
        TerminateThread(_dbSigThread.native_handle(), EXIT_SUCCESS);
    }
    if( ! _dbSigEmailThread.timed_join(boost::posix_time::seconds(30)) )
    {
        CTILOG_WARN(dout, "Terminating email thread");
        TerminateThread(_dbSigEmailThread.native_handle(), EXIT_SUCCESS);
    }
    if( ! _appMonitorThread.timed_join(boost::posix_time::seconds(30)) )
    {
        CTILOG_WARN(dout, "Terminating app thread");
        TerminateThread(_appMonitorThread.native_handle(), EXIT_SUCCESS);
    }
    if( ! _timedOpThread.timed_join(boost::posix_time::seconds(gConfigParms.getValueAsInt("SHUTDOWN_TERMINATE_TIME", 300))) )
    {
        CTILOG_WARN(dout, "Terminating timed op thread");
        TerminateThread(_timedOpThread.native_handle(), EXIT_SUCCESS);
    }
    if( ! _rphArchiver.tryJoinFor(std::chrono::seconds(gConfigParms.getValueAsInt("SHUTDOWN_TERMINATE_TIME", 300))) )
    {
        CTILOG_WARN(dout, "Terminating RPH thread");
        _rphArchiver.terminate();
    }
    if( ! _cacheHandlerThread1.timed_join(boost::posix_time::seconds(30)) )
    {
        CTILOG_WARN(dout, "Cache handler thread 1 failed to join in a timely manner");
    }
    if (!_cacheHandlerThread2.timed_join(boost::posix_time::seconds(30)))
    {
        CTILOG_WARN(dout, "Cache handler thread 2 failed to join in a timely manner");
    }
    if (!_cacheHandlerThread3.timed_join(boost::posix_time::seconds(30)))
    {
        CTILOG_WARN(dout, "Cache handler thread 3 failed to join in a timely manner");
    }
    ThreadMonitor.join();

    PointMgr.DeleteList();
}

CtiConnection* CtiVanGogh::getNotificationConnection()
{
    try
    {
        if( ! _notificationConnection || ! _notificationConnection->isConnectionUsable() )
        {
            if( _notificationConnection && ! _notificationConnection->isConnectionUsable() )
            {
                delete _notificationConnection;
                _notificationConnection = NULL;
            }

            if( _notificationConnection == NULL )
            {
                _notificationConnection  = new CtiClientConnection( Cti::Messaging::ActiveMQ::Queue::notification );
                _notificationConnection->setName("Dispatch to Notification");
                _notificationConnection->start();
            }
        }

        return _notificationConnection;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        return NULL;
    }
}


/*
 *  This method returns point data messages iff the control hour points need to be set to zero.
 *  The return will be null if there are no point data elements to update.  This should be ok regardless of the
 *
 */
CtiMultiMsg* CtiVanGogh::resetControlHours()
{
    CtiMultiMsg *pMulti = 0;

    CtiDate today;
    static CtiDate prevdate = CtiDate();

    if(today != prevdate) //TS does this have to be julian?   .julian()
    {
        CTILOG_INFO(dout, "Control history points reset in progress");

        try
        {
            CtiServerExclusion pmguard(_server_exclusion);

            for( auto kv : _deviceLites )
            {
                DeviceBaseLite &dLite = kv.second;

                if( dLite.isGroup() )
                {
                    const long deviceID = dLite.getID();

                    if( CtiPointSPtr point = PointMgr.getOffsetTypeEqual(deviceID, ANNUALCONTROLHISTOFFSET, AnalogPointType) )
                    {
                        if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*point) )
                        {
                            CtiDate ptdate = CtiDate(pDyn->getTimeStamp());

                            if( ANNUALCONTROLHISTOFFSET == point->getPointOffset() && today.year() != ptdate.year() )
                            {
                                CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(point->getID(), 0, pDyn->getQuality(), AnalogPointType, "Yearly History Reset", pDyn->getDispatch().getTags());
                                pDat->setTime(CtiTime(today));
                                pDat->setSource(DISPATCH_APPLICATION_NAME);
                                if(!pMulti) pMulti = CTIDBG_new CtiMultiMsg;
                                pMulti->insert(pDat);
                            }
                        }
                    }

                    if( CtiPointSPtr point = PointMgr.getOffsetTypeEqual(deviceID, MONTHLYCONTROLHISTOFFSET, AnalogPointType) )
                    {
                        if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*point) )
                        {
                            CtiDate ptdate = CtiDate(pDyn->getTimeStamp());

                            if( MONTHLYCONTROLHISTOFFSET == point->getPointOffset() && today.month() != ptdate.month() )
                            {
                                CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(point->getID(), 0, pDyn->getQuality(), AnalogPointType, "Monthly History Reset", pDyn->getDispatch().getTags());
                                pDat->setTime(CtiTime(today));
                                pDat->setSource(DISPATCH_APPLICATION_NAME);
                                if(!pMulti) pMulti = CTIDBG_new CtiMultiMsg;
                                pMulti->insert(pDat);
                            }
                        }
                    }

                    if( CtiPointSPtr point = PointMgr.getOffsetTypeEqual(deviceID, DAILYCONTROLHISTOFFSET, AnalogPointType) )
                    {
                        if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*point) )
                        {
                            CtiDate ptdate = CtiDate(pDyn->getTimeStamp());

                            if( DAILYCONTROLHISTOFFSET == point->getPointOffset() && today.day() != ptdate.day() )
                            {
                                CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(point->getID(), 0, pDyn->getQuality(), AnalogPointType, "Daily History Reset", pDyn->getDispatch().getTags());
                                pDat->setTime(CtiTime(today));
                                pDat->setSource(DISPATCH_APPLICATION_NAME);
                                if(!pMulti) pMulti = CTIDBG_new CtiMultiMsg;
                                pMulti->insert(pDat);
                            }
                        }
                    }
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }

        prevdate = today;
    }

    return pMulti;
}

void CtiVanGogh::sendbGCtrlC( const std::string & who )
{
    CTILOG_INFO(dout, who <<" has asked for shutdown");
    bGCtrlC = TRUE;
}


void CtiVanGogh::loadStalePointMaps(int pointID)
{
    if( pointID == 0 )
    {
        if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
        {
            CTILOG_DEBUG(dout, "Loading stale point maps - all points");
        }

        vector<long> points;

        PointMgr.getPointsWithProperty(CtiTablePointProperty::STALE_ALARM_TIME, points);
        vector<long>::iterator iter = points.begin();
        vector<long>::iterator end = points.end();

        CtiPointSPtr tempPoint;
        for( ; iter != end; iter++ )
        {
             pointID = *iter;
             //  this should turn into a find() function for PointClientManager
             if( PointMgr.hasProperty(pointID, CtiTablePointProperty::STALE_ALARM_TIME) )
             {
                 //so we have the alarm time, lets get it and be happy!
                 int alarmTime = PointMgr.getProperty(pointID, CtiTablePointProperty::STALE_ALARM_TIME);
                 if( alarmTime > 0 && _pointUpdatedTime.find(pointID) == _pointUpdatedTime.end() )
                 {
                     //If the point is set up properly and the point is not already in our maps.
                     unsigned int alarmSeconds = alarmTime * 60;
                     StalePointTimeData tempData;
                     CtiTime tempTime;
                     _pointUpdatedTime.insert(make_pair(pointID, tempTime));

                     tempTime += alarmSeconds;
                     tempData.time = tempTime;
                     tempData.pointID = pointID;
                     _expirationSet.insert(tempData);
                 }
             }
        }
    }
    else
    {
        if( PointMgr.hasProperty(pointID, CtiTablePointProperty::STALE_ALARM_TIME) )
        {
            if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
            {
                CTILOG_DEBUG(dout, "Loading stale point maps for point id "<< pointID);
            }

            //Only if the point is not in the map already!
            if( _pointUpdatedTime.find(pointID) == _pointUpdatedTime.end() )
            {
                int alarmTime = PointMgr.getProperty(pointID, CtiTablePointProperty::STALE_ALARM_TIME);
                if( alarmTime > 0 )
                {
                    unsigned int alarmSeconds = alarmTime * 60;
                    StalePointTimeData tempData;
                    CtiTime tempTime;
                    _pointUpdatedTime.insert(make_pair(pointID, tempTime));

                    tempTime += alarmSeconds;
                    tempData.time = tempTime;
                    tempData.pointID = pointID;
                    _expirationSet.insert(tempData);
                }
            }
        }
        //We only erase when calling check for stale points
    }
}

//This will modify the next stale point check time on its own.
void CtiVanGogh::checkForStalePoints(CtiMultiWrapper &aWrap)
{
    static CtiTime nextStalePointCheckTime;
    bool didCheck = false;
    if( CtiTime::now() >= nextStalePointCheckTime )
    {
        if( _expirationSet.empty() )
        {
            nextStalePointCheckTime = (CtiTime::now() + 60);//Try again in 1 minutes
        }
        else
        {
            CtiServerExclusion server_guard(_server_exclusion, 10000);      // Get a lock on it.
            CtiTime start;
            if(server_guard.isAcquired())
            {
                start = CtiTime::now();
                didCheck = true;

                bool keepGoing = true;
                std::multiset<StalePointTimeData>::iterator checkTimeIter;
                std::map<long, CtiTime>::iterator updatedIter;
                do
                {
                    checkTimeIter = _expirationSet.begin();
                    if( checkTimeIter != _expirationSet.end() )
                    {
                        //Check if the current top item in the set is past now (possibly expired)
                        if( checkTimeIter->time < CtiTime::now() )
                        {
                            //If we have one to check, get the point expiration data, and the points last updated time
                            CtiPointSPtr point = PointMgr.getPoint(checkTimeIter->pointID);
                            updatedIter = _pointUpdatedTime.find(checkTimeIter->pointID);

                            if( point && PointMgr.hasProperty(point->getPointID(), CtiTablePointProperty::STALE_ALARM_TIME) && updatedIter != _pointUpdatedTime.end() )
                            {
                                unsigned int alarmTime = PointMgr.getProperty(point->getPointID(), CtiTablePointProperty::STALE_ALARM_TIME);
                                if( (updatedIter->second + (alarmTime*60)) >= CtiTime::now() )
                                {
                                    if( CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*point) )
                                    {
                                        int alarm = point->isNumeric() ? CtiTablePointAlarming::staleNumeric : CtiTablePointAlarming::staleStatus;
                                        _expirationSet.erase(checkTimeIter);
                                        StalePointTimeData newTimeData;
                                        newTimeData.time = updatedIter->second + (alarmTime*60);
                                        newTimeData.pointID = point->getPointID();
                                        _expirationSet.insert(newTimeData); //Be sure this cant recurse..

                                        if( _signalManager.isAlarmedOrConditionActive(point->getPointID(), alarm) )
                                        {
                                            if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
                                            {
                                                CTILOG_DEBUG(dout, "Point is no longer stale "<< point->getPointID());
                                            }

                                            activatePointAlarm(alarm, aWrap, *point, *pDyn, false);
                                        }
                                    }
                                }
                                else
                                {
                                    //Alarm!!
                                    const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(*point);
                                    int alarm = point->isNumeric() ? CtiTablePointAlarming::staleNumeric : CtiTablePointAlarming::staleStatus;
                                    _expirationSet.erase(checkTimeIter);
                                    StalePointTimeData newTimeData;
                                    newTimeData.time = CtiTime::now() + (alarmTime*60);
                                    newTimeData.pointID = point->getPointID();
                                    _expirationSet.insert(newTimeData); //Be sure this cant recurse..

                                    if(pDyn && !_signalManager.isAlarmedOrConditionActive(point->getPointID(), alarm))
                                    {
                                        string text = "Point is stale, it has not been updated since: ";
                                        text += updatedIter->second.asString();
                                        const CtiTablePointAlarming &alarming = PointMgr.getAlarming(*point);
                                        CtiSignalMsg *pSig = pSig = CTIDBG_new CtiSignalMsg(point->getPointID(), 0, text, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm));
                                        pSig->setPointValue(pDyn->getDispatch().getValue());
                                        tagSignalAsAlarm(*point, pSig, alarm, nullptr);
                                        updateDynTagsForSignalMsg(*point,pSig);
                                        aWrap.getMulti()->insert( pSig );

                                        if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
                                        {
                                            CTILOG_DEBUG(dout, "Point is stale "<< point->getPointID() <<" - Time limit is "<< alarmTime <<" minutes");
                                        }
                                    }
                                }
                            }
                            else
                            {
                                //Here is where they are erased.
                                _expirationSet.erase(checkTimeIter);
                                if( updatedIter != _pointUpdatedTime.end() )
                                {
                                    _pointUpdatedTime.erase(updatedIter);
                                }
                            }
                        }
                        else
                        {
                            nextStalePointCheckTime = checkTimeIter->time;
                            keepGoing = false;
                        }
                    }
                    else
                    {
                        keepGoing = false;
                    }
                } while(!_expirationSet.empty() && keepGoing);
            }
            CtiTime stop;

            if( stop.seconds() - start.seconds() >= 1 )
            {
                CTILOG_INFO(dout, "Checking For Stale Points, took: "<< (stop.seconds() - start.seconds()) <<" seconds. Did Check = "<< (didCheck ? "TRUE" : "FALSE"));
            }
        }
    }
}

// Takes a numeric alarm and the id of the limit and checks for exceeding the limits
// This currently assumes that you know the limitID, if not it could be changed to call
// GetNumericStateLimitFromHighLow
bool CtiVanGogh::limitStateCheck( const int alarm, const CtiTablePointLimit &limit, double val, int &direction)
{
   direction = LIMIT_IN_RANGE;

   if(limit.getLowLimit() >= limit.getHighLimit())
   {
       direction = LIMIT_SETUP_ERROR;
   }
   else if( alarm >= CtiTablePointAlarming::limitLow0 && alarm <= CtiTablePointAlarming::limitHigh1 )
   {
       switch(alarm)
       {
           case CtiTablePointAlarming::limitLow0:
           case CtiTablePointAlarming::limitLow1:
           {
               if( val < limit.getLowLimit() )
               {
                   direction = LIMIT_EXCEEDS_LO;
               }
               break;
           }
           case CtiTablePointAlarming::limitHigh0:
           case CtiTablePointAlarming::limitHigh1:
           {
               if( val > limit.getHighLimit() )
               {
                   direction = LIMIT_EXCEEDS_HI;
               }
               break;
           }
       }
   }
   else
   {
       if( val < limit.getLowLimit() )
       {
          // Lo limit has been breached!
          direction = LIMIT_EXCEEDS_LO;
       }
       else if( limit.getHighLimit() < val )
       {
          // Hi limit has been breached!
           direction = LIMIT_EXCEEDS_HI;
       }
   }


   return (direction != LIMIT_IN_RANGE);
}

//Returns true if this message needs pre-loading
bool CtiVanGogh::checkMessageForPreLoad(CtiMessage *MsgPtr)
{
    SYSTEMTIME startTime, endTime;
    GetLocalTime(&startTime);

    bool retVal = false;
    if(MsgPtr != NULL)
    {
        if(MsgPtr->isA() == MSG_SERVER_REQUEST)
        {
            CtiServerRequestMsg *pSvrReq = (CtiServerRequestMsg*)MsgPtr;
            MsgPtr = (CtiMessage*)pSvrReq->getPayload();
        }

        if(MsgPtr->isA() == MSG_POINTDATA)
        {
            CtiPointDataMsg *pDataMsg = (CtiPointDataMsg*)MsgPtr;
            if(!PointMgr.isPointLoaded(pDataMsg->getId()))
            {
                retVal = true;
            }
        }
        else if(MsgPtr->isA() == MSG_SIGNAL)
        {
            CtiSignalMsg *pSigMsg = (CtiSignalMsg*)MsgPtr;
            if(!PointMgr.isPointLoaded(pSigMsg->getId()))
            {
                retVal = true;
            }
        }
        else if(MsgPtr->isA() == MSG_POINTREGISTRATION)
        {
            CtiPointRegistrationMsg *pRegMsg = (CtiPointRegistrationMsg*)MsgPtr;
            if( pRegMsg->isDecliningUpload() || pRegMsg->isAddingPoints() || pRegMsg->isRemovingPoints() )
            {
                //  None of these require points to be loaded.
                return false;
            }
            for( auto ptId : pRegMsg->getPointList() )
            {
                if( ! PointMgr.isPointLoaded(ptId) )
                {
                    return true;
                }
            }
        }
        else if(MsgPtr->isA() == MSG_COMMAND)
        {
            CtiCommandMsg *pCmdMsg = (CtiCommandMsg*)MsgPtr;
            if(pCmdMsg->getOperation() == CtiCommandMsg::PointDataRequest)
            {
                return false;
            }
            else if(pCmdMsg->getOperation() == CtiCommandMsg::AcknowledgeAlarm)
            {
                for(int i = 1; i + 1 < pCmdMsg->getOpArgList().size(); i += 2)
                {
                    if(!PointMgr.isPointLoaded(pCmdMsg->getOpArgList()[i]))
                    {
                        retVal = true;
                        break;
                    }
                }
            }
            else if(pCmdMsg->getOperation() == CtiCommandMsg::PointTagAdjust)
            {
                if(pCmdMsg->getOpArgList().size() >= 4 && !PointMgr.isPointLoaded(pCmdMsg->getOpArgList()[1]))
                {
                    retVal = true;
                }
            }
            else if(pCmdMsg->getOperation() == CtiCommandMsg::UpdateFailed)
            {
                CtiCommandMsg::OpArgList &Op = pCmdMsg->getOpArgList();

                if( Op[1] == CtiCommandMsg::OP_DEVICEID )    // All points on a device must be marked as nonUpdated
                {
                    vector<int> points = getPointIdsOnPao(Op[2]);

                    for(vector<int>::iterator iter = points.begin(); iter != points.end(); iter++)
                    {
                        if(!PointMgr.isPointLoaded(*iter))
                        {
                            retVal = true;
                            break;
                        }
                    }
                }
                else if( Op[1] == CtiCommandMsg::OP_POINTID )
                {
                    if(!PointMgr.isPointLoaded(Op[2]))
                    {
                        retVal = true;
                    }
                }
            }
            else if( pCmdMsg->getOperation() == CtiCommandMsg::ControlStatusVerification )
            {
                std::vector<unsigned long> pseudoPoints = GetPseudoPointIDs();

                for(std::vector<unsigned long>::iterator iter = pseudoPoints.begin(); iter != pseudoPoints.end(); iter++)
                {
                    if(!PointMgr.isPointLoaded(*iter))
                    {
                        retVal = true;
                        break;
                    }
                }
            }
        }
        else if(MsgPtr->isA() == MSG_MULTI || MsgPtr->isA() == MSG_PCRETURN)
        {
            CtiMultiMsg *pMulti = (CtiMultiMsg*)MsgPtr;
            for(int i = 0; i < pMulti->getData().size(); i++)
            {
                retVal = checkMessageForPreLoad((CtiMessage*)pMulti->getData()[i]);
                if(retVal == true)
                {
                    break;
                }
            }
        }
        else if(MsgPtr->isA() == MSG_TAG)
        {
            CtiTagMsg *pTagMsg = (CtiTagMsg*)MsgPtr;
            if(!PointMgr.isPointLoaded(pTagMsg->getPointID()))
            {
                retVal = true;
            }
        }
        else if(MsgPtr->isA() == MSG_LMCONTROLHISTORY)
        {
            CtiLMControlHistoryMsg *pLMMsg = (CtiLMControlHistoryMsg*)MsgPtr;
            if(!PointMgr.isPointLoaded(pLMMsg->getPointId()))
            {
                retVal = true;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "MsgPtr is Null");
    }
    return retVal;
}

void CtiVanGogh::findPreLoadPointId(CtiMessage *MsgPtr, std::set<long> &ptIdList)
{
    SYSTEMTIME startTime, endTime;
    GetLocalTime(&startTime);

    if(MsgPtr != NULL)
    {
        if(MsgPtr->isA() == MSG_SERVER_REQUEST)
        {
            CtiServerRequestMsg *pSvrReq = (CtiServerRequestMsg*)MsgPtr;
            MsgPtr = (CtiMessage*)pSvrReq->getPayload();
        }

        if(MsgPtr->isA() == MSG_POINTDATA)
        {
            CtiPointDataMsg *pDataMsg = (CtiPointDataMsg*)MsgPtr;
            ptIdList.insert(pDataMsg->getId());
        }
        else if(MsgPtr->isA() == MSG_SIGNAL)
        {
            CtiSignalMsg *pSigMsg = (CtiSignalMsg*)MsgPtr;
            ptIdList.insert(pSigMsg->getId());
        }
        else if(MsgPtr->isA() == MSG_POINTREGISTRATION)
        {
            CtiPointRegistrationMsg *pRegMsg = (CtiPointRegistrationMsg*)MsgPtr;
            if( pRegMsg->isDecliningUpload() || pRegMsg->isAddingPoints() || pRegMsg->isRemovingPoints() )
            {
                //  None of these require points to be loaded.
                return;
            }
            boost::remove_copy_if(
                    pRegMsg->getPointList(),
                    std::inserter(ptIdList, ptIdList.begin()),
                    [this](const long ptId) { return PointMgr.isPointLoaded(ptId); });
        }
        else if(MsgPtr->isA() == MSG_COMMAND)
        {
            CtiCommandMsg *pCmdMsg = (CtiCommandMsg*)MsgPtr;
            //  Note that CtiCommandMsg::PointDataRequest does not require preload, and is intentionally excluded.
            if(pCmdMsg->getOperation() == CtiCommandMsg::AcknowledgeAlarm)
            {
                for(int i = 1; i + 1 < pCmdMsg->getOpArgList().size(); i += 2)
                {
                    if(!PointMgr.isPointLoaded(pCmdMsg->getOpArgList()[i]))
                    {
                        ptIdList.insert(pCmdMsg->getOpArgList()[i]);
                    }
                }
            }
            else if(pCmdMsg->getOperation() == CtiCommandMsg::PointTagAdjust)
            {
                if(pCmdMsg->getOpArgList().size() >= 4)
                {
                    ptIdList.insert(pCmdMsg->getOpArgList()[1]);
                }
            }
            else if(pCmdMsg->getOperation() == CtiCommandMsg::UpdateFailed)
            {
                CtiCommandMsg::OpArgList &Op = pCmdMsg->getOpArgList();

                if( Op[1] == CtiCommandMsg::OP_DEVICEID )    // All points on a device must be marked as nonUpdated
                {
                    vector<int> points = getPointIdsOnPao(Op[2]);

                    for(vector<int>::iterator iter = points.begin(); iter != points.end(); iter++)
                    {
                        if(!PointMgr.isPointLoaded(*iter))
                        {
                            ptIdList.insert(*iter);
                        }
                    }
                }
                else if( Op[1] == CtiCommandMsg::OP_POINTID )
                {
                    ptIdList.insert(Op[2]);
                }
            }
            else if( pCmdMsg->getOperation() == CtiCommandMsg::ControlStatusVerification )
            {
                std::vector<unsigned long> pseudoPoints = GetPseudoPointIDs();

                for(std::vector<unsigned long>::iterator iter = pseudoPoints.begin(); iter != pseudoPoints.end(); iter++)
                {
                    if(!PointMgr.isPointLoaded(*iter))
                    {
                        ptIdList.insert(*iter);
                    }
                }
            }
        }
        else if(MsgPtr->isA() == MSG_MULTI || MsgPtr->isA() == MSG_PCRETURN)
        {
            CtiMultiMsg *pMulti = (CtiMultiMsg*)MsgPtr;
            for(int i = 0; i < pMulti->getData().size(); i++)
            {
                findPreLoadPointId((CtiMessage*)pMulti->getData()[i], ptIdList);
            }
        }
        else if(MsgPtr->isA() == MSG_TAG)
        {
            CtiTagMsg *pTagMsg = (CtiTagMsg*)MsgPtr;
            ptIdList.insert(pTagMsg->getPointID());
        }
        else if(MsgPtr->isA() == MSG_LMCONTROLHISTORY)
        {
            CtiLMControlHistoryMsg *pLMMsg = (CtiLMControlHistoryMsg*)MsgPtr;
            ptIdList.insert(pLMMsg->getPointId());
        }
    }
    else
    {
        CTILOG_ERROR(dout, "MsgPtr is Null");
    }
}
