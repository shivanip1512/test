#include "precompiled.h"

#include <iostream>
#include <utility>

#include "collectable.h"
#include "counter.h"
#include "cparms.h"
#include "guard.h"
#include <list>

#include "netports.h"
#include "queent.h"
#include "queue.h"
#include "con_mgr.h"

#include "msg_cmd.h"
#include "msg_reg.h"
#include "msg_pcrequest.h"
#include "msg_ptreg.h"
#include "msg_signal.h"
#include "msg_commerrorhistory.h"
#include "msg_notif_alarm.h"
#include "msg_server_req.h"
#include "msg_server_resp.h"
#include "ctivangogh.h"

#include "pt_base.h"
#include "pt_accum.h"
#include "pt_analog.h"
#include "pt_status.h"
#include "pttrigger.h"

#include "dev_base.h"

#include "tbl_dyn_ptalarming.h"
#include "tbl_signal.h"
#include "tbl_lm_controlhist.h"
#include "tbl_commerrhist.h"
#include "tbl_ptdispatch.h"
#include "tbl_pt_alarm.h"
#include "thread_monitor.h"
#include "ThreadStatusKeeper.h"

#include "mgr_ptclients.h"
#include "dlldefs.h"

#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_transaction.h"
#include "database_writer.h"

#include "connection.h"
#include "dbaccess.h"
#include "utility.h"
#include "logger.h"
#include "dllvg.h"

#include "dllyukon.h"
#include "ctidate.h"
#include "numstr.h"
#include "debug_timer.h"
#include "millisecond_timer.h"

#include <boost/tuple/tuple_comparison.hpp>

using namespace std;

#define MAX_ARCHIVER_ENTRIES     10             // If this many entries appear, we'll do a dump
#define MAX_DYNLMQ_ENTRIES       100            // If this many entries appear, we'll do a dump
#define DUMP_RATE                30             // Otherwise, do a dump every this many seconds
#define CONFRONT_RATE            300            // Ask every client to post once per 5 minutes or be terminated
#define UPDATERTDB_RATE          1800           // Save all dirty point records once per n seconds
#define SANITY_RATE              300
#define POINT_EXPIRE_CHECK_RATE  60
#define DYNAMIC_LOAD_SIZE        256

#define MAX_ALARM_TRX 256

DLLIMPORT extern CtiLogger   dout;

DLLEXPORT BOOL  bGCtrlC = FALSE;

using Cti::ThreadStatusKeeper;

/* Global Variables */
CtiPointClientManager      PointMgr;   // The RTDB for memory points....
//This trigger mananger was implemented but never used. It is commented out everywhere!
//CtiPointTriggerManager     TriggerMgr; // The RTDB for point triggers....
CtiVanGoghExecutorFactory  ExecFactory;

static map< long, CtiPointDataMsg* > fullBoatMap;

static const CtiTime MAXTime(YUKONEOT);
int CntlHistInterval = 3600;
int CntlHistPointPostInterval = 300;
int CntlStopInterval = 300;

static CtiCounter msgCounts;
static CtiCounter msgPrioritys;
static CtiCounter msgTimes;

static bool processExecutionTime(UINT ms);

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
                    if( const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPoint) )
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

                            updateGroupPseduoControlPoint( pPoint, stopTime);
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

void ApplyBlankDeletedConnection(CtiMessage*&Msg, void *Conn);

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
    try
    {
        MainThread_ = rwMakeThreadFunction(*this, &CtiVanGogh::VGMainThread);
        MainThread_.start();
    }
    catch(const RWxmsg& x)
    {
        cout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
    }

    return 0;
}

void CtiVanGogh::VGMainThread()
{
    int  nRet;
    ULONG MessageCount = 0;
    ULONG MessageLog = 0;

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

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initial Database Load" << endl;
        }
        loadRTDB(true);
        loadPendingSignals();       // Reload any signals written out at last shutdown.
        loadStalePointMaps();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Reloading pending control information" << endl;
        }
        if( !_pendingOpThread.loadICControlMap() )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Done reloading dynamic control information.  Deep control history load beginning." << endl;
            }
            // loadPendingControls();      // Reload any controls written out at last shutdown.
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done Reloading pending control information" << endl;
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Verifying control point states" << endl;
        }
        //PointMgr.apply( ApplyGroupControlStatusVerification, this );
        MainQueue_.putQueue((CtiMessage *)CTIDBG_new CtiCommandMsg(CtiCommandMsg::ControlStatusVerification, 15));
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Done verifying control point states" << endl;
        }

        ThreadMonitor.start();

        _pendingOpThread.setMainQueue( &MainQueue_ );
        _pendingOpThread.setSignalManager( &_signalManager );
        _pendingOpThread.start();

        _rphThread = rwMakeThreadFunction(*this, &CtiVanGogh::VGRPHWriterThread);
        _rphThread.start();

        _archiveThread = rwMakeThreadFunction(*this, &CtiVanGogh::VGArchiverThread);
        _archiveThread.start();

        _timedOpThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGTimedOperationThread);
        _timedOpThread.start();

        _dbThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGDBWriterThread);
        _dbThread.start();

        _dbSigThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGDBSignalWriterThread);
        _dbSigThread.start();

        _dbSigEmailThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGDBSignalEmailThread);
        _dbSigEmailThread.start();

        _appMonitorThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGAppMonitorThread);
        _appMonitorThread.start();

        _cacheHandlerThread1 = rwMakeThreadFunction(*this, &CtiVanGogh::VGCacheHandlerThread,1);
        _cacheHandlerThread1.start();

        _cacheHandlerThread2 = rwMakeThreadFunction(*this, &CtiVanGogh::VGCacheHandlerThread,2);
        _cacheHandlerThread2.start();

        _cacheHandlerThread3 = rwMakeThreadFunction(*this, &CtiVanGogh::VGCacheHandlerThread,3);
        _cacheHandlerThread3.start();

         // Prime the connection to the notification server
         getNotificationConnection();

        // all that is good and ready has been started, open up for business from clients
        ConnThread_ = rwMakeThreadFunction(*this, &CtiVanGogh::VGConnectionHandlerThread);
        ConnThread_.start();

        // SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);
        CTISetPriority(PRTYC_TIMECRITICAL, THREAD_PRIORITY_HIGHEST);

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

                    CtiServer::ptr_type sptrCM = mConnectionTable.find((long)pSvrReq->getConnectionHandle());
                    if(sptrCM)
                    {
                        sptrCM->setRequestId(pSvrReq->getID());  // Stow this, you'll need it for a response.
                    }
                }

                msgCounts.inc( MsgPtr->isA() );
                msgPrioritys.inc( MsgPtr->getMessagePriority() );

                switch( MsgPtr->isA() )
                {
                case MSG_PCRETURN:
                case MSG_MULTI:
                    {
                        int increment = ((CtiMultiMsg*)MsgPtr)->getCount();
                        MessageCount += increment;
                        MessageLog += increment;

                        if(increment > 1000)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** BIGMULTI Checkpoint **** submessages to process " << increment << " from " << MsgPtr->getSource() << " " << MsgPtr->getUser() << endl;
                        }
                        break;
                    }
                default:
                    {
                        MessageCount++;
                        MessageLog++;
                        break;
                    }
                }

                if( MessageLog >= 1000  )
                {
                    MessageLog = 0;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Dispatch has processed " << MessageCount << " inbound messages." << endl;
                }

                if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSFRMCLIENT)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "\n>>>> INCOMING " << endl;
                    }
                    MsgPtr->dump();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << ">>>> INCOMING COMPLETE\n" << endl;
                    }
                }


                try
                {
                    nRet = NORMAL;

                    if( MsgPtr->isValid() )
                    {
                        if((pExec = ExecFactory.getExecutor(MsgPtr)) != NULL)
                        {
                            nRet = pExec->ServerExecute(this);     // The "this" in question is the CtiVanGogh object...

                            DWORD completeTime = timer.elapsed();

                            if(processExecutionTime(completeTime - dequeueTime))
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    if(pExec->getMessage())
                                        dout << CtiTime() << " Message Type " << pExec->getMessage()->typeString() << " get ms = " << dequeueTime << "  ms = " << (completeTime - dequeueTime) << endl;
                                }

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " The last message took more than 5 sec to process in dispatch " << endl;
                                    //if(pExec->getMessage())
                                    //    pExec->getMessage()->dump();
                                }
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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  Message reported itself as invalid " << endl;
                        }

                        delete MsgPtr;
                    }

                    if( nRet )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  ServerExecute returned an error = " << nRet << endl;
                        }
                    }
                }
                catch( ... )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
            else
            {
                if( !(ConnThread_.isValid() &&
                      ConnThread_.getExecutionState() & RW_THR_ACTIVE &&
                      ConnThread_.getCompletionState() == RW_THR_PENDING) )
                {
                    if(ConnThread_.getCompletionState() != RW_THR_PENDING)
                    {
                        CtiServerExclusion pmguard(_server_exclusion, 10000);

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Restarting ConnThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        // all that is good and ready has been started, open up for business from clients
                        ConnThread_ = rwMakeThreadFunction(*this, &CtiVanGogh::VGConnectionHandlerThread);
                        ConnThread_.start();
                    }
                }
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Main loop duration: " << loopDuration << " ms.  MainQueue_ has " << MainQueue_.entries() << endl;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << endl << "Shutting down on CTRL-C" << endl << endl;
        }

        stopDispatch();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " *********** Exiting Dispatch MAIN ***********  " << endl;
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "**** MAIN JUST DIED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        stopDispatch();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " *********** Exiting Dispatch MAIN ***********  " << endl;
        }
    }
}

void CtiVanGogh::VGConnectionHandlerThread()
{
    int               i=0;
    BOOL              bQuit = FALSE;

    UINT sanity = 0;

    CtiExchange       *XChg;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch Connection Handler Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

    NetPort = RWInetPort(gConfigParms.getValueAsInt("DISPATCH_PORT", VANGOGHNEXUS));
    NetAddr = RWInetAddr(NetPort);

    _listenerSocket.listen(NetAddr);

    // This is here for looks, in reality it is rarely called.
    if( !_listenerSocket.valid() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Could not open socket " << NetAddr << " for listening" << endl;

        exit(-1);
    }

    /* Up this threads priority a notch over the other procs */
    CTISetPriority(PRTYC_NOCHANGE, THREAD_PRIORITY_BELOW_NORMAL);

    for(;!bQuit && !bGCtrlC;)
    {
        try
        {
            // It seems necessary to make this copy. RW does this and now so do we.
            RWSocket tempSocket = _listenerSocket;
            RWSocket newSocket = tempSocket.accept();
            RWSocketPortal sock;

            if( !newSocket.valid() )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Could not accept new connection " << endl;
            }
            else
            {
                // This is very important. We tell the socket portal that we own the socket!
                sock = RWSocketPortal(newSocket, RWSocketPortalBase::Application);

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Connection Handler Thread. New connect. " << endl;
                }

                {
                    CtiServerExclusion guard(_server_exclusion);

                    XChg                                = CTIDBG_new CtiExchange(sock);
                    CtiVanGoghConnectionManager *ConMan = CTIDBG_new CtiVanGoghConnectionManager(XChg, &MainQueue_);
                    CtiServer::ptr_type sptrConMan(ConMan);

                    clientConnect( sptrConMan );
                    sptrConMan->ThreadInitiate();     // Kick off the connection's communication threads.

                    if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " New connection established" << endl;
                    }
                }

            }

            reportOnThreads();
        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() == RWNETENOTSOCK)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Socket error RWNETENOTSOCK" << endl;
                bQuit = TRUE;     // get out of the for loop
            }
            else
            {
                bQuit = TRUE;
                // dout << CtiTime() << " VGConnectionHandlerThread: The KNOWN socket has been closed" << endl;
            }
        }
        catch(RWxmsg& msg )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << "VGConnectionHandler Failed: " ;
                dout << msg.why() << endl;
                bQuit = TRUE;
            }
            throw;
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch Connection Handler Thread shutting down " << endl;
    }

    return;
}

int CtiVanGogh::registration(CtiServer::ptr_type pCM, const CtiPointRegistrationMsg &aReg)
{
    int nRet = NoError;
    CtiTime     NowTime;

    CtiVanGoghConnectionManager *CM = (CtiVanGoghConnectionManager*)pCM.get();

    if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
    {
        displayConnections();
    }

    try
    {
        if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime << " Registration: " << CM->getClientName() << endl;
        }

        /*
         * We need to set up our CtiVanGoghConnectionManager according to the request!
         */

        if(aReg.getFlags() & REG_ALL_PTS_MASK)     // I am registering for all/any of some type of point.
        {
            if(aReg.getFlags() & REG_ALL_STATUS)
            {
                CM->setStatus(TRUE);
                if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " " << CM->getClientName() << " has registered for ALL status points " << endl;
                }
            }

            if(aReg.getFlags() & REG_ALL_ANALOG)
            {
                CM->setAnalog(TRUE);
                if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " " << CM->getClientName() << " has registered for ALL analog points " << endl;
                }
            }

            if(aReg.getFlags() & REG_ALL_ACCUMULATOR)
            {
                CM->setAccumulator(TRUE);
                if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " " << CM->getClientName() << " has registered for ALL accum points " << endl;
                }
            }

            if(aReg.getFlags() & REG_ALL_CALCULATED)
            {
                CM->setCalculated(TRUE);
                if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime << " " << CM->getClientName() << " has registered for ALL calc points " << endl;
                }
            }
        }

        if(aReg.getFlags() & REG_EVENTS)
        {
            CM->setEvent(TRUE);
            if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << NowTime << " " << CM->getClientName() << " has registered for EVENTS " << endl;
            }
        }

        if(aReg.getFlags() & REG_ALARMS)
        {
            CM->setAlarm(TRUE);
            if(gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << NowTime << " " << CM->getClientName() << " has registered for ALARMS " << endl;
            }
        }

        validateConnections();        // Make sure nobody has disappeared on us since the last registration
        PointMgr.InsertConnectionManager(pCM, aReg, gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION);


        if(!(aReg.getFlags() & (REG_NO_UPLOAD | REG_ADD_POINTS | REG_REMOVE_POINTS)))
        {
            postMOAUploadToConnection(pCM, aReg.getFlags());
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return nRet;
}

// Assumes lock on _server_exclusion has been obtained.
int  CtiVanGogh::commandMsgHandler(CtiCommandMsg *Cmd)
{
    int status = NORMAL;
    int i;

    switch( Cmd->getOperation() )
    {
    case (CtiCommandMsg::ClearAlarm):
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Alarms can no longer be CtiCommandMsg::ClearAlarm " << endl;
            }
            break;
        }
    case (CtiCommandMsg::AcknowledgeAlarm):
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMACK)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** ACKNOWLEDGE RECEIVED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                Cmd->dump();
            }

            for(i = 1; i + 1 < Cmd->getOpArgList().size(); i += 2)
            {
                int pid = Cmd->getOpArgList()[i];
                int alarmcondition = Cmd->getOpArgList()[i+1];

                CtiPointSPtr  pPt = PointMgr.getPoint(pid);

                if(pPt )      // I know about the point...
                {
                    acknowledgeCommandMsg(pPt, Cmd, alarmcondition);
                }
            }

            break;
        }
    case (CtiCommandMsg::PorterConsoleInput):
        {
            writeMessageToClient(Cmd, string(PORTER_REGISTRATION_NAME));
            break;
        }
    case (CtiCommandMsg::ControlRequest):
        {
            /*
             *  This block receives a CommandMsg from a requesting client and processes it for submission to
             *  pil/porter.
             */
            if(Cmd->getOpArgList().size() >= 4)
            {
                LONG token     = Cmd->getOpArgList()[0];
                LONG did       = Cmd->getOpArgList()[1];
                LONG pid       = Cmd->getOpArgList()[2];
                LONG rawstate  = Cmd->getOpArgList()[3];
                INT  ctrl_offset = 0;

                // Find PIL in the connection list.
                try
                {
                    {
                        CtiPointSPtr pPoint;
                        if(Cmd->getOpArgList().size() >= 5)
                        {
                            // This is a BOOL which indicates whether a ctrl offset is spec'd by pid.
                            // If this is so, "did" MUST also be specified!
                            ctrl_offset = Cmd->getOpArgList()[4];

                            if(did == 0 || ctrl_offset == 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  CtiCommandMsg::ControlRequest sent with DeviceID = " << did << " and Ctrl Offset = " << ctrl_offset << endl;
                                }
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

                        if(pPoint)
                        {
                            CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPoint);

                            int controlTimeout = DefaultControlExpirationTime;

                            if( pPoint->isStatus() )
                            {
                                CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(pPoint);

                                if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
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

                                bool is_a_control = writeControlMessageToPIL(did, rawstate, boost::static_pointer_cast<CtiPointStatus>(pPoint), Cmd);

                                CtiPointDataMsg *pPseudoValPD = 0;
                                PtVerifyTriggerSPtr verificationPtr;

                                CtiPendingPointOperations *pendingControlRequest = CTIDBG_new CtiPendingPointOperations(pPoint->getID());
                                pendingControlRequest->setType(CtiPendingPointOperations::pendingControl);
                                pendingControlRequest->setControlState( CtiPendingPointOperations::controlSentToPorter );
                                pendingControlRequest->setTime( Cmd->getMessageTime() );
                                pendingControlRequest->setControlCompleteValue( (DOUBLE) rawstate );
                                pendingControlRequest->setControlTimeout( controlTimeout );
                                pendingControlRequest->setExcludeFromHistory(!isDeviceGroupType(did));

                                /*if( verificationPtr = TriggerMgr.getPointTriggerFromPoint(pPoint->getID()) )
                                {
                                    pendingControlRequest->setControlTimeout( verificationPtr->dbTriggerData.getCommandTimeOut() );
                                    pendingControlRequest->setControlCompleteDeadband(verificationPtr->dbTriggerData.getVerificationDeadband());
                                }*/

                                pendingControlRequest->getControl().setPAOID( did );
                                pendingControlRequest->getControl().setStartTime(CtiTime(YUKONEOT));
                                pendingControlRequest->getControl().setControlDuration(0);

                                string devicename= resolveDeviceName(pPoint);

                                if( pPoint->isPseudoPoint() )
                                {
                                    // We are going to fake a rawstate behavior here since no one else is likely to do it for us...
                                    pPseudoValPD = CTIDBG_new CtiPointDataMsg(pPoint->getID(), (DOUBLE) rawstate, NormalQuality, pPoint->getType());
                                    pPseudoValPD->setSource(DISPATCH_APPLICATION_NAME);
                                }
                                else if( is_a_control )
                                {
                                    const CtiTablePointAlarming alarming = PointMgr.getAlarming(pPoint);

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

                                addToPendingSet(pendingControlRequest, Cmd->getMessageTime());

                                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " " << devicename << " / " << pPoint->getName() << " has gone CONTROL SUBMITTED. Control expires at " << CtiTime(Cmd->getMessageTime() + controlTimeout) << endl;
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
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << resolveDeviceName(pPoint) << " / " << pPoint->getName() << " CONTROL SENT to port control. Control expires at " << CtiTime(Cmd->getMessageTime() + controlTimeout) << endl;
                            }
                        }
                    }
                }
                catch(const RWxmsg& x)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
                    break;
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Error, Control Request did not have a valid command vector **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case (CtiCommandMsg::AnalogOutput):
        {
            if(Cmd->getOpArgList().size() >= 2)
            {
                const long point_id = Cmd->getOpArgList()[0];
                const long value    = Cmd->getOpArgList()[1];

                if(const CtiPointSPtr point = PointMgr.getPoint(point_id))
                {
                    if(const long device_id = point->getDeviceID())
                    {
                        writeAnalogOutputMessageToPIL(device_id, point_id, value, Cmd);
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Error, Control Request did not have a valid command vector **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            break;
        }
    case (CtiCommandMsg::InitiateScan):
        if(Cmd->getOpArgList().size() >= 1)
        {
            const long paoID = Cmd->getOpArgList()[0];

            CtiRequestMsg pReq(paoID, "scan integrity");
            pReq.setUser( Cmd->getUser() );
            pReq.setMessagePriority( MAXPRIORITY - 1 );

            writeMessageToClient(&pReq, string(PIL_REGISTRATION_NAME));
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Scan Integrity Request sent to DeviceID: " << paoID << endl;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Error, Control Request did not have a valid command vector **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        break;
    case (CtiCommandMsg::Ablement):
    case (CtiCommandMsg::ControlAblement):
        {
            messageDump((CtiMessage*)Cmd);

            if(Cmd->getOpArgList().size() >= 4)
            {
                LONG token     = Cmd->getOpArgList()[0];

                for(i = 1; i < Cmd->getOpArgList().size(); i = i + 3 )
                {
                    LONG idtype   = Cmd->getOpArgList()[i];
                    LONG id       = Cmd->getOpArgList()[i+1];
                    bool disable  = !((Cmd->getOpArgList()[i+2] != 0));
                    int  tagmask  = 0;           // This mask represents all the bits which are to be adjusted.
                    int  setmask  = 0;         // This mask represents the state of the adjusted-masked bit.. Ok, read it again.

                    try
                    {
                        CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

                        if(pMulti)
                        {
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
                                CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(id);
                                if(dliteit != _deviceLiteSet.end() && ablementDevice(dliteit, setmask, tagmask))
                                {
                                    adjustDeviceDisableTags(id, false, Cmd->getUser());    // We always have an id here.
                                }
                            }
                            else if(idtype == CtiCommandMsg::OP_POINTID)
                            {
                                bool devicedifferent;

                                CtiPointSPtr pPt = PointMgr.getPoint(id);
                                ablementPoint(pPt, devicedifferent, setmask, tagmask, Cmd->getUser(), *pMulti);

                                if(devicedifferent)     // The device became interesting because of this change.
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                }
                            }

                            if(pMulti->getData().size())
                            {
                                MainQueue_.putQueue(pMulti);
                                pMulti = 0;
                            }
                            else
                            {
                                delete pMulti;
                            }
                        }
                    }
                    catch(const RWxmsg& x)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
                        break;
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Error, Control Request did not have a valid command vector **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case (CtiCommandMsg::UpdateFailed):
        {
            processMessage( (CtiMessage*)Cmd );
            break;
        }
    case (CtiCommandMsg::AlternateScanRate):
        {
            writeMessageToScanner( Cmd );
            break;
        }
    case (CtiCommandMsg::PointTagAdjust):
        {
            if(Cmd->getOpArgList().size() >= 4)
            {
                // Vector contains token, pointid, tag(s) to set.
                LONG token      = Cmd->getOpArgList()[0];
                LONG pointid    = Cmd->getOpArgList()[1];
                LONG tagstoset  = Cmd->getOpArgList()[2];
                LONG tagstoreset= Cmd->getOpArgList()[3];

                {
                    CtiPointSPtr pPt = PointMgr.getPoint( pointid );
                    CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPt);
                    if(pDyn)
                    {
                        pDyn->getDispatch().setTags( tagstoset );
                        pDyn->getDispatch().resetTags( tagstoreset );
                    }
                }
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Invalid PointTagAdjust vector size. " << endl;
                }
            }

            break;
        }
    case (CtiCommandMsg::ResetControlHours):
        {
            // Vector contains token? ? ? ? ?
            // LONG token = Cmd->getOpArgList().at(0);

            try
            {
                CtiDeviceLiteSet_t::iterator iter = _deviceLiteSet.begin();
                CtiDeviceLiteSet_t::iterator end  = _deviceLiteSet.end();
                long deviceID;

                for( ; iter != end; iter++ )
                {
                    if(isDeviceGroupType(&(*iter)))
                    {
                        deviceID = iter->getID();
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << iter->getName() << " resetting seasonal hours" << endl;
                        }

                        CtiPointSPtr pControlPoint = PointMgr.getControlOffsetEqual( deviceID, 1);     // This is the control status control point which keeps control in play.

                        if(pControlPoint)
                        {
                            CtiPendingPointOperations *pendingSeasonReset = CTIDBG_new CtiPendingPointOperations(pControlPoint->getID());
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
                            addToPendingSet(pendingSeasonReset, Cmd->getMessageTime());
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

            break;
        }
    case (CtiCommandMsg::Shutdown):
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            Cmd->dump();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Shutdown requests by command messages are ignored by dispatch." << endl;
            }
            // bGCtrlC = TRUE;
            break;
        }
    case (CtiCommandMsg::PointDataRequest):
        {
            // Vector contains ONLY PointIDs that need to be sent to the client.
            // LONG token = Cmd->getOpArgList().at(0);

            try
            {
                CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

                CtiServerExclusion pmguard(_server_exclusion);
                int payload_status = CtiServerResponseMsg::OK;
                string payload_string;

                for(i = 0; i < Cmd->getOpArgList().size(); i++ )
                {
                    long pid = Cmd->getOpArgList()[i];
                    CtiPointSPtr pPt = PointMgr.getPoint( pid );
                    if(pPt)
                    {
                        const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPt);
                        if(pDyn)
                        {

                            CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(pPt->getID(),
                                                                               pDyn->getValue(),
                                                                               pDyn->getQuality(),
                                                                               pPt->getType(),
                                                                               string(),
                                                                               pDyn->getDispatch().getTags());

                            if(pDat != NULL)
                            {
                                pDat->setSource(DISPATCH_APPLICATION_NAME);
                                pDat->setTime( pDyn->getTimeStamp() );  // Make the time match the point's last received time
                                pMulti->getData().push_back(pDat);
                            }
                        }

                        CtiMultiMsg *pSigMulti = _signalManager.getPointSignals(pPt->getID());
                        if(pSigMulti)
                        {
                            pMulti->getData().push_back(pSigMulti);
                        }
                    }
                    else
                    {
                        payload_status = CtiServerResponseMsg::ERR;
                        payload_string = "Point id (" + CtiNumStr(pid) + ") not found";
                    }
                }

                CtiServer::ptr_type sptrCM = mConnectionTable.find((long)Cmd->getConnectionHandle());

                if(sptrCM && pMulti)
                    sptrCM->WriteConnQue(pMulti, 0, true, payload_status, payload_string);
                else delete
                    pMulti;

            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            break;
        }
    case (CtiCommandMsg::AlarmCategoryRequest):
        {
            // Vector contains ONLY AlarmCategoryIDs that need to be sent to the client.
            try
            {
                CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

                CtiServerExclusion pmguard(_server_exclusion);
                int payload_status = CtiServerResponseMsg::OK;

                for(i = 0; i < Cmd->getOpArgList().size(); i++ )
                {
                    unsigned alarm_category = Cmd->getOpArgList()[i];

                    CtiMultiMsg *pSigMulti = _signalManager.getCategorySignals(alarm_category);
                    if(pSigMulti)
                    {
                        pMulti->getData().push_back(pSigMulti);
                    }
                }

                CtiServer::ptr_type sptrCM = mConnectionTable.find((long)Cmd->getConnectionHandle());

                if(sptrCM && pMulti)
                {
                    sptrCM->WriteConnQue(pMulti, 0, true, payload_status);
                }
                else
                    delete pMulti;

            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            break;
        }
    case (CtiCommandMsg::ControlStatusVerification):
        {
            std::vector<unsigned long> pseudoPoints;

            GetPseudoPointIDs(pseudoPoints);

            for(std::vector<unsigned long>::iterator iter = pseudoPoints.begin(); iter != pseudoPoints.end(); iter++)
            {
                groupControlStatusVerification(*iter);
            }
        }
    default:
        {
            status = Inherited::commandMsgHandler(Cmd);
            break;
        }
    }

    return status;
}

void CtiVanGogh::clientShutdown(CtiServer::ptr_type CM)
{
    CtiServerExclusion guard(_server_exclusion);

    try
    {
        // Make sure no queue entries point at this connection!
        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ") Marking mainQueue entries to _not_ respond to this connection as a client." << endl;
        }
        MainQueue_.apply(ApplyBlankDeletedConnection, (void*)CM.get());
        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ") Removing point registrations for this connection." << endl;
        }
        PointMgr.RemoveConnectionManager(CM);
        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ") Calling server_b clientShutdown()." << endl;
        }
        Inherited::clientShutdown(CM);
        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ") Dispatch clientShutdown() complete." << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

/*
 *  Posts the Database change to each connected client.
 */
int CtiVanGogh::postDBChange(const CtiDBChangeMsg &Msg)
{
    int                     nRet = NORMAL;
    ptr_type                Mgr;
    CHAR                    temp[80];


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

                    if( Mgr->WriteConnQue( Msg.replicateMessage(), 2500 ) )        // Send a copy of DBCHANGE on to each clients.
                    {
                        MgrToRemove = Mgr;

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Connection is not viable : " << Mgr->getClientName() << " / " << Mgr->getClientAppId() << endl;
                    }

                    if(((CtiVanGoghConnectionManager*)Mgr.get())->getEvent()) // If the client cares about events...
                    {
                        Mgr->WriteConnQue(pSig->replicateMessage(), 2500);    // Copy pSig out to any event registered client
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " DBCHANGE has expired.  It was sent at " << Msg.getMessageTime() << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return nRet;
}


void CtiVanGogh::VGArchiverThread()
{
    UINT     sleepTime;
    CtiTime   NextTime;
    CtiTime   TimeNow;



    UINT sanity = 0;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch RTDB Archiver Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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

            PointMgr.scanForArchival(TimeNow, _archiverQueue);

            /*
             *  Step 2. identify the next nearest time we need to do an archival write.
             */

            sleepTime = PointMgr.findNextNearestArchivalTime().seconds()  - TimeNow.seconds();

            if(sleepTime > 60)
            {
                sleepTime = 60 - (TimeNow.second());    // Make them align with the minute

                if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " RTDB Archiver Thread: Archiving/Checking in " << sleepTime << " Seconds" << endl;
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
                rwSleep(1000);
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Dispatch RTDB Archiver Thread shutting down" << endl;
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Exception.  Thread death. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}


void CtiVanGogh::VGTimedOperationThread()
{
    UINT sanity = 0;
    CtiMultiMsg *pMulti = 0;

    ThreadStatusKeeper threadStatus("Timed Operation Thread");

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch Timed Operation Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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

            rwSleep(1000);

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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Timed operations took " << stop.seconds() - start.seconds() << " seconds to run." << endl;
            }
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    updateRuntimeDispatchTable(true);

    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch Timed Operation Thread shutting down" << endl;
    }

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

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << threadName << " starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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

            MsgPtr = CacheQueue_.getQueue(10000);

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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << threadName << " took " << stop.seconds() - start.seconds() << " seconds to run." << endl;
            }
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch " << threadName << " shutting down" << endl;
    }
    return;
}

void CtiVanGogh::archivePointDataMessage(const CtiPointDataMsg &aPD)
{
    try
    {
        // See if I know about this point ID
        if(const CtiPointSPtr TempPoint = PointMgr.getCachedPoint(aPD.getId()))
        {
            CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(TempPoint);

            if(pDyn && !(pDyn->getDispatch().getTags() & MASK_ANY_SERVICE_DISABLE))
            {
                const bool isNew = isPointDataNewInformation(aPD, *pDyn);
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
                    pDyn->setPoint(aPD.getTime(), aPD.getMillis(), aPD.getValue(), aPD.getQuality(), (aPD.getTags() & ~SIGNAL_MANAGER_MASK) | _signalManager.getTagMask(aPD.getId()));

                    if( isDuplicate && previouslyArchived )
                    {
                        pDyn->setWasArchived(true);
                    }
                }

                if( aPD.getTags() & (TAG_POINT_MUST_ARCHIVE | TAG_POINT_LOAD_PROFILE_DATA) )
                {
                    if( isDuplicate && previouslyArchived && gConfigParms.isTrue("DISPATCH_LOG_DUPLICATE_ARCHIVE_SUPPRESSION", true) )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Suppressing duplicate forced archive for pointid " << TempPoint->getPointID() << endl;
                    }
                    else
                    {
                        // This is a forced reading, which must be written, it should not
                        // however cause any change in normal pending scanned readings

                        _archiverQueue.putQueue( CTIDBG_new CtiTableRawPointHistory(TempPoint->getID(), aPD.getQuality(), aPD.getValue(), aPD.getTime(), aPD.getMillis()));

                        pDyn->setWasArchived(true);
                    }
                }
                else if(pDyn->isArchivePending() ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnUpdate) ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnChange && isNew) ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnTimerOrUpdated))
                {
                    _archiverQueue.putQueue( CTIDBG_new CtiTableRawPointHistory(TempPoint->getID(), aPD.getQuality(), aPD.getValue(), aPD.getTime(), aPD.getMillis()));

                    pDyn->setArchivePending(false);

                    pDyn->setWasArchived(true);
                }
            }
        }
        else
        {
            CHAR temp[80];

            _snprintf(temp, 79, "Point change for unknown PointID: %ld", aPD.getId());
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << temp << endl;
            }

            CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Point Data Relay");
            pSig->setUser(aPD.getUser());
            queueSignalToSystemLog(pSig);
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiVanGogh::processStalePoint(CtiPointSPtr pPoint, CtiDynamicPointDispatchSPtr &pDyn, int updateType, const CtiPointDataMsg &aPD, CtiMultiWrapper& wrap)
{
    if( pPoint && pDyn && aPD.getSource() != DISPATCH_APPLICATION_NAME )
    {
        if( updateType == CtiTablePointProperty::UPDATE_ALWAYS ||
           (updateType == CtiTablePointProperty::UPDATE_ON_CHANGE && aPD.getValue() != pDyn->getValue()) )
        {
            _pointUpdatedTime.erase(pPoint->getPointID());
            _pointUpdatedTime.insert(make_pair(pPoint->getPointID(), CtiTime::now()));
            int alarm = pPoint->isNumeric() ? CtiTablePointAlarming::staleNumeric : CtiTablePointAlarming::staleStatus;
            if( _signalManager.isAlarmActive(pPoint->getPointID(), alarm) || pDyn->isConditionActive(alarm) )
            {
                activatePointAlarm(alarm, wrap, pPoint, pDyn, false);
                //I possibly changed the point tags, update!
                pDyn->getDispatch().resetTags(SIGNAL_MANAGER_MASK);
                pDyn->getDispatch().setTags(_signalManager.getTagMask(pPoint->getPointID()));
                if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Point is no longer stale " << pPoint->getPointID() << endl;
                }
            }
        }
    }
}

INT CtiVanGogh::archiveSignalMessage(const CtiSignalMsg& aSig)
{
    int status = NORMAL;

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
                CHAR temp[80];

                _snprintf(temp, sizeof(temp), "Signal for unknown PointID: %ld", aSig.getId());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << temp << endl;
                }

                pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Signal Relay");
                pSig->setUser(aSig.getUser());
                status = IDNF; // Error is ID not found!
            }
        }

        if(pSig != NULL)
        {
            queueSignalToSystemLog(pSig);
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}


INT CtiVanGogh::archiveCommErrorHistoryMessage(const CtiCommErrorHistoryMsg& aCEHM)
{
    INT status = NORMAL;

    try
    {
        if(aCEHM.getPAOId() > 0)
        {
            // See if I know about this PAO (Device) ID
            _commErrorHistoryQueue.putQueue( CTIDBG_new CtiTableCommErrorHistory(aCEHM.getPAOId(),
                                                                                 aCEHM.getDateTime(),
                                                                                 aCEHM.getSOE(),
                                                                                 aCEHM.getErrorType(),
                                                                                 aCEHM.getErrorNumber(),
                                                                                 aCEHM.getCommand(),
                                                                                 aCEHM.getOutMessage(),
                                                                                 aCEHM.getInMessage()/*,
                                                                                 aCEHM.getCommErrorId()*/));
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}


INT CtiVanGogh::processMultiMessage(CtiMultiMsg *pMulti)
{
    INT status = NORMAL;

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
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}


/*--------------------------------------------------------------------*
 * This guy looks at and absorbs any information that Dispatch needs
 * from the message, following this, the message is posted to clients
 *--------------------------------------------------------------------*/
INT CtiVanGogh::processMessageData( CtiMessage *pMsg )
{
    INT status = NORMAL;

    try
    {
        switch( pMsg->isA() )
        {
        case MSG_POINTREGISTRATION:
            {
                messageDump(pMsg);
                const CtiPointRegistrationMsg &aReg = *((CtiPointRegistrationMsg*)(pMsg));
                CtiServer::ptr_type sptrCM = mConnectionTable.find((long)pMsg->getConnectionHandle());
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
                CtiServer::ptr_type pCM = mConnectionTable.find((long)pMsg->getConnectionHandle());

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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        case MSG_COMMERRORHISTORY:
            {
                processCommErrorMessage((CtiCommErrorHistoryMsg*)pMsg);
                break;
            }
        }
    }
    catch( ... )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}

/******************************************************************************
 * This method handles all messages which need to go to _globally_ connected
 * clients...
 *
 ******************************************************************************/
INT CtiVanGogh::postMessageToClients(CtiMessage *pMsg)
{
    INT i;
    INT status = NORMAL;

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
                        CtiTime Now;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "\n<<<< Message to Client Connection " << Mgr->getClientName() << " on " << Mgr->getPeer() << " START" << endl;
                        }
                        pMulti->dump();
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "<<<< Message to Client Connection " << Mgr->getClientName() << " on " << Mgr->getPeer() << " COMPLETE\n" << endl;
                        }
                    }

                    if( Mgr->WriteConnQue(pMulti, 5000) )
                    {
                        MgrToRemove = Mgr;

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Connection is not viable : " << Mgr->getClientName() << " / " << Mgr->getClientAppId() << endl;
                    }
                }
                else if(pMulti != NULL) // This means none of the messages were for this connection.
                {
                    delete pMulti;
                    pMulti = NULL;
                }
            }
            catch(const RWxmsg& x)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
                break;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if(MgrToRemove)    // Someone failed.. Blitz them
        {
            clientShutdown(MgrToRemove);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}



CtiMultiMsg* CtiVanGogh::generateMultiMessageForConnection(const CtiServer::ptr_type &Conn, CtiMessage *pMsg)
{
    INT            status   = NORMAL;

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
    INT status   = NORMAL;

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
    case MSG_COMMERRORHISTORY:
        {
            break;
        }
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** ERROR **** " << __FILE__ << " (" << __LINE__ << ") MSG_TYPE = " << pMsg->isA() << endl;
            break;
        }
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromMultiForConnection(const CtiServer::ptr_type &Conn,
                                                    CtiMessage                        *pMsg,
                                                    CtiMultiMsg_vec                         &Ord)
{
    INT            i;
    INT            status   = NORMAL;
    CtiMultiMsg    *pMulti  = (CtiMultiMsg*)pMsg;      // Uses the inheritance nature of MSG_PCRETURN
    CtiMessage     *pMyMsg;

    if(pMulti != NULL)
    {

        CtiMultiMsg_vec::iterator itr = pMulti->getData().begin();

        for(;pMulti->getData().end() != itr; itr++)
        {
            pMyMsg = (CtiMessage*)*itr;
            status = assembleMultiForConnection(Conn, pMyMsg, Ord);
            if(status != NORMAL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Error processing this message " << endl;
                }
                pMyMsg->dump();
            }
        }
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromSignalForConnection(const CtiServer::ptr_type &Conn,
                                                     CtiMessage                        *pMsg,
                                                     CtiMultiMsg_vec                         &Ord)
{
    INT            status   = NORMAL;
    CtiSignalMsg   *pSig    = (CtiSignalMsg*)pMsg;

    try
    {
        if(pSig != NULL)
        {
            if(isSignalForConnection(Conn, *pSig))
            {
                // FIX FIX FIX ... Do I need this point code here???
                CtiPointSPtr pPoint = PointMgr.getPoint(pSig->getId());

                if(pPoint)
                {
                    const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPoint);
                    if(pDyn)
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromTagForConnection(const CtiServer::ptr_type &Conn,
                                                  CtiMessage                        *pMsg,
                                                  CtiMultiMsg_vec                         &Ord)
{
    INT            status   = NORMAL;
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromPointDataForConnection(const CtiServer::ptr_type &Conn,
                                                        CtiMessage                        *pMsg,
                                                        CtiMultiMsg_vec                         &Ord)
{
    INT               status   = NORMAL;
    CtiPointDataMsg   *pDat    = (CtiPointDataMsg*)pMsg;

    try
    {
        if(pDat != NULL)
        {
            if(!(pDat->getTags() & TAG_POINT_DELAYED_UPDATE))   // We will propagate this one later!
            {
                CtiPointSPtr pTempPoint = PointMgr.getPoint(pDat->getId());

                if(pTempPoint)
                {
                    const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pTempPoint);


                    // 20041228 CGP.  This is the correct usage for exemptible... No one wants this code right now.
                    // if( isPointDataNewInformation(*pDat, pDyn) || !pDat->isExemptable() )    // This is new data and it indicates that it can NOT be skipped if that is NOT the case.
                    if(pDyn)
                    {
                        if( pDat->getQuality() == ManualQuality || !(pDyn->getDispatch().getTags() & (MASK_ANY_SERVICE_DISABLE)) ) // (MASK_ANY_SERVICE_DISABLE | MASK_ANY_CONTROL_DISABLE)) )
                        {
                            if(isPointDataForConnection(Conn, *pDat))
                            {
                                {
                                    CtiPointDataMsg *pNewData = (CtiPointDataMsg *)pDat->replicateMessage();

                                    pNewData->resetTags(~TAG_POINT_OLD_TIMESTAMP);            // Dispatch has specifically asked for old timestamp, dont remove it
                                    pNewData->setTags( pDyn->getDispatch().getTags() );       // Report any set tags out to the clients.

                                    Ord.push_back(pNewData);
                                }
                            }
                        }
                        else if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                        {
                            // Point data on a disabled point.
                            string gripe = string(" NO DATA REPORT to: ") + Conn->getClientName() + string(" ");
                            INT mask = (pDyn->getDispatch().getTags() & MASK_ANY_DISABLE);

                            if(mask & (TAG_DISABLE_DEVICE_BY_DEVICE))
                            {
                                gripe += pTempPoint->getName() + string(" is disabled by its device");
                            }
                            else if(mask & (TAG_DISABLE_POINT_BY_POINT))
                            {
                                gripe += pTempPoint->getName() + string(" is disabled");
                            }
                            else if(mask & (TAG_DISABLE_CONTROL_BY_POINT | TAG_DISABLE_CONTROL_BY_DEVICE))
                            {
                                gripe += pTempPoint->getName() + string(" is control disabled");
                            }

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " " << gripe << endl;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** ERROR **** NULL message " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
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

    CtiPointSPtr pPoint = PointMgr.getPoint(Msg.getPointID());

    if( pPoint && ((const CtiVanGoghConnectionManager *)Conn.get())->isRegForChangeType(pPoint->getType()) )
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

    if( ((const CtiVanGoghConnectionManager *)Conn.get())->isRegForChangeType(Msg.getType()))
    {
            bStatus = TRUE;
    }
    else
    {
        bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getId());
    }

    return bStatus;
}

bool CtiVanGogh::isPointDataNewInformation(const CtiPointDataMsg &Msg, const CtiDynamicPointDispatch &Dyn)
{
    //  This is for points on devices like RTUs that send or are scanned for periodic updates.
    //    The value might not change, but the point data is new if the timestamp is new.
    if( Msg.getTags() & TAG_POINT_DATA_TIMESTAMP_VALID )
    {
        //  If the time is newer
        if( (Msg.getTime() > Dyn.getTimeStamp()) ||
            (Msg.getTime() == Dyn.getTimeStamp() && Msg.getMillis() != Dyn.getTimeStampMillis()) )
        {
            return true;
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

int CtiVanGogh::processCommErrorMessage(CtiCommErrorHistoryMsg *pMsg)
{
    int status = NORMAL;

    try
    {
        switch( pMsg->getErrorNumber() )
        {
        case DEVICEINHIBITED:
        case PORTINHIBITED:
            {
                // Do nothing.  No need to fill the DB with garbage.
                break;
            }
        default:
            {
                status = archiveCommErrorHistoryMessage(*pMsg);
                break;
            }
        }
    }
    catch(const RWxmsg& x)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return status;
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
    int status = NORMAL;
    bool isPseudo = false;
    PtVerifyTriggerSPtr verificationPtr;

    try
    {
        CtiPointSPtr pPoint;
        CtiServerExclusion pmguard(_server_exclusion);
        pPoint = PointMgr.getPoint(pMsg->getPointId());

        if(pPoint)
        {
            CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPoint);

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

                CtiPendingPointOperations *pendingControlLMMsg = CTIDBG_new CtiPendingPointOperations(pPoint->getID());
                pendingControlLMMsg->setType(CtiPendingPointOperations::pendingControl);
                pendingControlLMMsg->setControlState( CtiPendingPointOperations::controlPending );
                pendingControlLMMsg->setTime( pMsg->getStartDateTime() );
                pendingControlLMMsg->setControlCompleteValue( (DOUBLE) pMsg->getRawState() );
                pendingControlLMMsg->setControlTimeout( controlTimeout );
                pendingControlLMMsg->setExcludeFromHistory(!isDeviceGroupType(pPoint->getDeviceID()));

                /*if( verificationPtr = TriggerMgr.getPointTriggerFromPoint(pPoint->getID()) )
                {
                    pendingControlLMMsg->setControlTimeout(verificationPtr->dbTriggerData.getCommandTimeOut());
                    pendingControlLMMsg->setControlCompleteDeadband(verificationPtr->dbTriggerData.getVerificationDeadband());

                    if( verificationPtr->dbTriggerData.getVerificationID() == 0 )
                    {
                        //So we dont verify, we are a pseudo. Handle the pseudo point here!
                        isPseudo = true;
                    }
                }*/

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

                const CtiTablePointAlarming alarming = PointMgr.getAlarming(pPoint);

                CtiSignalMsg *pFailSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), 0, "Control " + ResolveStateName(pPoint->getStateGroupID(), pMsg->getRawState()) + " Failed", getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure), pMsg->getUser());

                pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK));
                if(pFailSig->getSignalCategory() > SignalEvent)
                {
                    pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | (alarming.isAutoAcked(CtiTablePointAlarming::commandFailure) ? 0 : TAG_UNACKNOWLEDGED_ALARM));
                    pFailSig->setLogType(AlarmCategoryLogType);
                }
                pFailSig->setCondition(CtiTablePointAlarming::commandFailure);

                pendingControlLMMsg->setSignal( pFailSig );

                if(isDeviceGroupType(pMsg->getPAOId()) && _pendingOpThread.getCurrentControlPriority(pMsg->getPointId()) >= pMsg->getControlPriority())
                {
                    CtiPointSPtr pControlStatus = PointMgr.getControlOffsetEqual(pMsg->getPAOId() , 1);
                    if(pControlStatus->isPseudoPoint())
                    {
                        // There is no physical point to observe and respect.  We lie to the control point.
                        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pControlStatus->getPointID(), pMsg->getRawState(), NormalQuality, StatusPointType, (pMsg->getRawState() == CONTROLLED ? string(resolveDeviceNameByPaoId(pMsg->getPAOId()) + " controlling") : string(resolveDeviceNameByPaoId(pMsg->getPAOId()) + " restoring")));
                        pData->setMessagePriority( pData->getMessagePriority() + 1 );
                        MainQueue_.putQueue(pData);
                    }

                    if(pMsg->getRawState() == CONTROLLED && pMsg->getControlDuration() > 0)
                    {
                        // Present the restore as a delayed update to dispatch.  Note that the order of opened and closed have reversed
                        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pControlStatus->getPointID(), (DOUBLE)UNCONTROLLED, NormalQuality, StatusPointType, string(resolveDeviceNameByPaoId(pMsg->getPAOId()) + " restoring (delayed)"), TAG_POINT_DELAYED_UPDATE);
                        pData->setTime( CtiTime() + pMsg->getControlDuration() );
                        pData->setMessagePriority( pData->getMessagePriority() - 1 );
                        //vgList.push_back(pData);
                        MainQueue_.putQueue(pData);
                    }
                }

                if( isPseudo )
                {
                    CtiPendingPointOperations *controlCompleteControlMsg = CTIDBG_new CtiPendingPointOperations(*pendingControlLMMsg);
                    addToPendingSet( pendingControlLMMsg, pMsg->getMessageTime() );

                    controlCompleteControlMsg->setControlState(CtiPendingPointOperations::controlCompleteCommanded);
                    addToPendingSet( controlCompleteControlMsg, pMsg->getMessageTime() );
                }
                else
                {
                    addToPendingSet( pendingControlLMMsg, pMsg->getMessageTime() );
                }

                pDyn->getDispatch().setTags( TAG_CONTROL_PENDING );
                bumpDeviceToAlternateRate( pPoint );
            }
        }
    }
    catch(const RWxmsg& x)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
        }
    }

    return status;
}

int CtiVanGogh::processMessage(CtiMessage *pMsg)
{
    int status = NORMAL;
    CtiMultiWrapper MultiWrapper;

    CtiServerExclusion guard(_server_exclusion);

    checkDataStateQuality(pMsg, MultiWrapper);
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

    return status;
}

INT CtiVanGogh::postMOAUploadToConnection(CtiServer::ptr_type &CM, int flags)
{
    INT i;
    INT status = NORMAL;

    CtiTableSignal *pSig;
    CtiMultiMsg    *pMulti  = CTIDBG_new CtiMultiMsg;

    CtiTime now;

    CtiVanGoghConnectionManager *VGCM = (CtiVanGoghConnectionManager*)(CM.get());


    bool isFullBoat = ((const CtiVanGoghConnectionManager *)CM.get())->isRegForAll();                   // Is this connection asking for everything?

    if(isFullBoat)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << now << " **** CHECKPOINT **** requesting all points is NOT allowed" << endl;
            dout << now << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << now << " Client Connection " << CM->getClientName() << " on " << CM->getPeer() << endl;

        }
    }
    else if( pMulti != NULL )
    {
        pMulti->setMessagePriority(15);
        CtiPointManager::WeakPointMap pointMap = PointMgr.getRegistrationMap(CM->hash(*CM.get()));
        CtiPointSPtr TempPoint;

        for( CtiPointManager::WeakPointMap::iterator iter = pointMap.begin(); iter != pointMap.end(); iter++ )
        {
            TempPoint = iter->second.lock();

            if( TempPoint )
            {
                const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(TempPoint);

                if(pDyn)
                {
                    CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(TempPoint->getID(),
                                                                           pDyn->getValue(),
                                                                           pDyn->getQuality(),
                                                                           TempPoint->getType(),
                                                                           string(),
                                                                           pDyn->getDispatch().getTags());

                    if(pDat != NULL)
                    {
                        if(flags & REG_TAG_MARKMOA)
                        {
                            pDat->setTags(TAG_POINT_MOA_REPORT);
                        }

                        // Make the time match the entered time
                        pDat->setTime( pDyn->getTimeStamp() );
                        pDat->setSource(DISPATCH_APPLICATION_NAME);
                        pMulti->getData().push_back(pDat);
                    }
                }
            }

            /*
             *  Block the MOA into 1000 element multis.
             */
            if( pMulti->getCount() >= gConfigParms.getValueAsULong("DISPATCH_MAX_MULTI_MOA", 1000) )
            {
                if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)
                {
                    {
                        CtiTime Now;
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << Now << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << Now << " Client Connection " << CM->getClientName() << " on " << CM->getPeer() << endl;
                    }
                    pMulti->dump();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                if(CM->WriteConnQue(pMulti, 5000))
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << " Connection is having issues : " << CM->getClientName() << " / " << CM->getClientAppId() << endl;
                }

                pMulti  = CTIDBG_new CtiMultiMsg;
            }
        }

        //This now gets all alarms in the known universe.
        // full of all alarms active/unacknowledged on all points
        if( ((const CtiVanGoghConnectionManager *)CM.get())->getAlarm() )
        {
            CtiMultiMsg *pSigMulti = _signalManager.getAllAlarmSignals();

            if(pSigMulti)
            {
                pMulti->getData().push_back(pSigMulti);
            }
        }

        // We add all the assigned tags into the multi as well.
        {
            CtiMultiMsg *pTagMulti = _tagManager.getAllPointTags();
            if(pTagMulti)
            {
                pMulti->getData().push_back(pTagMulti);
            }
        }

        if(pMulti->getCount() > 0)
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)    // Temp debug
            {
                {
                    CtiTime Now;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << Now << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << Now << " Client Connection " << CM->getClientName() << " on " << CM->getPeer() << endl;
                }
                pMulti->dump();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            if(CM->WriteConnQue(pMulti, 5000))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << " Connection is having issues : " << CM->getClientName() << " / " << CM->getClientAppId() << endl;
            }
        }
        else
        {
            delete pMulti;
        }
    }

    return status;
}


INT CtiVanGogh::loadPendingSignals()
{
    INT            status = NORMAL;
    LONG           lTemp;

    CtiServerExclusion pmguard(_server_exclusion);
    {
        static const string sql = CtiTableDynamicPointAlarming().getSQLCoreStatement();

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        if(!rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << loggedSQLstring << endl;
            }
        }

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
    }

    return status;
}

void CtiVanGogh::writeSignalsToDB(bool justdoit)
{
    CtiSignalMsg   *sigMsg;
    UINT           panicCounter = 0;
    static UINT  dumpCounter = 0;


    std::vector<CtiSignalMsg*>      postList;

    try
    {
        if(!(++dumpCounter % DUMP_RATE)
           || _signalMsgQueue.entries() > MAX_ARCHIVER_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            {
                Cti::Database::DatabaseConnection   conn;

                if ( ! conn.isValid() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                    return;
                }

                {
                    Cti::Database::DatabaseTransaction trans(conn);

                    try
                    {
                        do
                        {
                            sigMsg = _signalMsgQueue.getQueue(0);

                            if(sigMsg != NULL)
                            {
                                CtiTableSignal sig(sigMsg->getId(), sigMsg->getMessageTime(), sigMsg->getSignalMillis(), sigMsg->getText(), sigMsg->getAdditionalInfo(), sigMsg->getSignalCategory(), sigMsg->getLogType(), sigMsg->getSOE(), sigMsg->getUser(), sigMsg->getLogID());

                                if(!sigMsg->getText().empty() || !sigMsg->getAdditionalInfo().empty())
                                {
                                    // No text, no point then is there now?
                                    sig.Insert(conn);
                                }

                                if(!(sigMsg->getTags() & TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL))
                                {
                                    postList.push_back(sigMsg);
                                }
                                else
                                {
                                    delete sigMsg;
                                }
                            }

                        } while( sigMsg != NULL && (justdoit || (panicCounter++ < 500)));
                    }
                    catch(...)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }
            }

            if(panicCounter > 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " SystemLog transaction complete. Inserted " << panicCounter << " signal messages.  " << _signalMsgQueue.entries() << " left on queue." << endl;
                }
            }
        }

        if((justdoit == true || !(dumpCounter % SANITY_RATE)) && !_signalManager.empty() && _signalManager.dirty())//use sanity rate to slow us down
        {
            _signalManager.writeDynamicSignalsToDB();
        }

        {
            while(!postList.empty())
            {
                sigMsg = (CtiSignalMsg*)postList.back();
                postList.pop_back();
                bool done = _signalMsgPostQueue.putQueue(sigMsg, 5000);           // Place them on the email queue and let him clean them up!
                if(!done)
                {
                    delete sigMsg;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "   Failed to queue signal message for emailing. " << endl;
                    }
                }
            }
        }
    }
    catch(RWxmsg &msg)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** RW EXCEPTION **** " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiVanGogh::writeArchiveDataToDB(bool justdoit)
{
#define PANIC_CONSTANT 1000

    static int maxrowstowrite = PANIC_CONSTANT;
    static UINT  dumpCounter = 0;
    UINT panicCounter = 0;      // Make sure we don't write for too long...

    try
    {
        size_t archiveent = _archiverQueue.entries();

        /*
         *  Go look if we need to write out archive points.
         *  We only do this once every 30 seconds or if there are >= 10 entries to do.
         */
        if(!(++dumpCounter % DUMP_RATE)
           || archiveent > MAX_ARCHIVER_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            if(archiveent > 0)
            {
                panicCounter = writeRawPointHistory(justdoit, maxrowstowrite);
            }

            size_t rowsLeft = _archiverQueue.entries();
            if( panicCounter > 0 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " RawPointHistory transaction complete. Inserted " << panicCounter
                     << " rows" << endl;

                if(rowsLeft > MAX_ARCHIVER_ENTRIES)
                {
                    dout << CtiTime() << " RawPointHistory rows remaining: " << rowsLeft << endl;
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                /*
                 *  double the queue's size if it is more than half full.
                 */
                if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Archival queue has " << _archiverQueue.entries() << " entries" << endl;
                    dout << " Queue is currently sized at " << _archiverQueue.size() << " entries" << endl;
                }
            }
        }
    }
    catch(const RWxmsg& x)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiVanGogh::writeCommErrorHistoryToDB(bool justdoit)
{
#define PANIC_CONSTANT 1000

    static UINT  dumpCounter = 0;
    static INT   daynumber = -1;
    UINT         panicCounter = 0;      // Make sure we don't write for too long...

    try
    {
        CtiTableCommErrorHistory *pTblEntry;
        size_t comment = _commErrorHistoryQueue.entries();

        /*
         *  Go look if we need to write out archive points.
         *  We only do this once every 30 seconds or if there are >= 10 entries to do.
         */
        if(!(++dumpCounter % DUMP_RATE)
           || comment > MAX_ARCHIVER_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            if(comment > 0)
            {
                bool success = true;
                Cti::Database::DatabaseConnection   conn;

                if ( ! conn.isValid() )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                    return;
                }

                {

                    Cti::Database::DatabaseTransaction trans(conn);

                    while( success && ( justdoit || (panicCounter < PANIC_CONSTANT) ) && (pTblEntry = _commErrorHistoryQueue.getQueue(0)) != NULL)
                    {
                        if(pTblEntry)
                        {
                            if(isDeviceIdValid(pTblEntry->getPAOID()))
                            {
                                panicCounter++;
                                success = pTblEntry->Insert(conn);
                            }
                            delete pTblEntry;
                            pTblEntry = 0;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                }
            }

            if( panicCounter > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CommErrorHistory transaction complete. Inserted " << panicCounter << " rows.  " << _commErrorHistoryQueue.entries() << " entries left on queue." << endl;
                }
            }

            CtiDate todaysdate;

            if(todaysdate.dayOfMonth() != daynumber)
            {
                if(daynumber > 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Comm Error History log is being cleaned up. " << endl;
                    }
                    pruneCommErrorHistory();
                }
                daynumber = todaysdate.dayOfMonth();    // Ok.  We know whay DAY today is...
            }

        }
    }
    catch(const RWxmsg& x)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Of " << ClientCount << " connections " <<
                        KilledCount << " were deleted" << endl;
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
    catch(RWxmsg& msg )
    {
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            CtiTime start;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "Writing dispatch dynamic table. " << endl;
            }
            PointMgr.storeDirtyRecords();
            CtiTime stop;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "Writing dispatch dynamic table, took: " << stop.seconds() - start.seconds() << " seconds. " << endl;
            }
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHIST_INTERVAL found : " << str << endl;
        }
        else
        {
            CntlHistInterval = 3600;
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHIST_INTERVAL default : " << CntlHistInterval << endl;
        }

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTLHISTPOINTPOST_INTERVAL")).empty() )
        {
            CntlHistPointPostInterval = atoi(str.c_str());
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHISTPOINTPOST_INTERVAL found : " << str << endl;
        }
        else
        {
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHISTPOINTPOST_INTERVAL default : " << CntlHistPointPostInterval << endl;
        }

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTL_STOP_REPORT_INTERVAL")).empty() )
        {
            CntlStopInterval = atoi(str.c_str());
            if(CntlStopInterval > 3600)
            {
                CntlStopInterval = 3600;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " DISPATCH_CNTL_STOP_REPORT_INTERVAL cannot be greater than 3600" << endl;
                }
            }
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTL_STOP_REPORT_INTERVAL found : " << str << endl;
        }
        else
        {
            CntlStopInterval = 60;
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTL_STOP_REPORT_INTERVAL default : " << CntlStopInterval << endl;
        }

        if( gConfigParms.isTrue("DISPATCH_SHUTDOWN_ON_THREAD_TIMEOUT") )
        {
            ShutdownOnThreadTimeout = true;
        }
        else
        {
            ShutdownOnThreadTimeout = false;
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_SHUTDOWN_ON_THREAD_TIMEOUT default : " << ShutdownOnThreadTimeout << endl;
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
INT CtiVanGogh::checkDataStateQuality(CtiMessage *pMsg, CtiMultiWrapper &aWrap)
{
    INT status   = NORMAL;

    switch(pMsg->isA())
    {
    case MSG_PCRETURN:
    case MSG_MULTI:
        {
            status = checkMultiDataStateQuality((CtiMultiMsg*)pMsg, aWrap);
            break;
        }
    case MSG_POINTDATA:
        {
            status = checkPointDataStateQuality((CtiPointDataMsg*)pMsg, aWrap);
            break;
        }
    case MSG_COMMAND:
        {
            CtiCommandMsg *pCmd = (CtiCommandMsg*)pMsg;

            if(pCmd->getOperation() == CtiCommandMsg::UpdateFailed)
            {
                status = commandMsgUpdateFailedHandler(pCmd, aWrap);
            }
            else
            {
                // Make sure nobody is doing something odd.  CGP 3/18/2002
                commandMsgHandler(pCmd);
            }
            break;
        }
    case MSG_SIGNAL:
        {
            status = checkSignalStateQuality((CtiSignalMsg*)pMsg, aWrap);
            break;
        }
    case MSG_TAG:
        {
            // Allocate instance number to any non-allocated message!
            // Process instances for removal requests.
            _tagManager.verifyTagMsg(*((CtiTagMsg*)pMsg));

            break;
        }
    case MSG_LMCONTROLHISTORY:
        {
            // Pick this up here so that the dyn Tags may be modified....
            processControlMessage((CtiLMControlHistoryMsg*)pMsg);
            break;
        }
    default:
        {
            break;
        }
    }

    return status;
}
/*
 *  Could recurse.  Does not worry about self referential loops... Don't do it.
 */
INT CtiVanGogh::checkMultiDataStateQuality(CtiMultiMsg  *pMulti, CtiMultiWrapper &aWrap)
{
    INT            i;
    INT            status   = NORMAL;
    CtiMessage     *pMyMsg;

    if(pMulti != NULL)
    {
        CtiMultiMsg_vec::iterator itr = pMulti->getData().begin();

        for(; pMulti->getData().end() != itr; )
        {
            pMyMsg = (CtiMessage*)*itr;
            status = checkDataStateQuality( pMyMsg, aWrap );
            itr++;
        }
    }

    return status;
}

INT CtiVanGogh::checkSignalStateQuality(CtiSignalMsg  *pSig, CtiMultiWrapper &aWrap)
{
    INT status = NORMAL;

    if(pSig->getText().empty() && pSig->getAdditionalInfo().empty())
    {
        CtiServer::ptr_type pCM = mConnectionTable.find((long)pSig->getConnectionHandle());

        string cliname("Unknown");

        if(pCM)
        {
            cliname = pCM->getClientName();
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << cliname << "\r" << "just submitted a blank signal message for point id " << pSig->getId() << endl;
        }

    }

    return status;
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
INT CtiVanGogh::checkPointDataStateQuality(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap)
{
    INT status   = NORMAL;

    if(pData != NULL)
    {
        CtiPointSPtr pPoint = PointMgr.getCachedPoint(pData->getId());
        CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPoint);

        if(pPoint && pDyn)      // We do know this point..
        {
            if(pData->getType() == InvalidPointType)
            {
                pData->setType(pPoint->getType());
            }
            else if(pPoint->getType() != pData->getType())
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** WARNING **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  Point Type mismatch.  Received point data message indicated a type" << endl;
                dout << "  for point " << pData->getId() << " which does not match the memory image held by dispatch." << endl;

                dout << " Memory Image point type = " << pPoint->getType() << endl;
                dout << " Message point type = " << pData->getType() << endl;
                dout << "   Point \"" << pPoint->getName() << "\" is attached to " << resolveDeviceName( pPoint ) << endl;

                if(pData->getConnectionHandle() != NULL)
                {
                    CtiServer::ptr_type pCM = mConnectionTable.find((long)pData->getConnectionHandle());

                    if(pCM)
                    {
                        dout << " Submitting client   " << pCM->getClientName() << endl;
                        dout << "  Client Information " << pCM->getPeer() << endl;
                    }
                }

                pData->setType(pPoint->getType());
            }


            // We need to make sure there is no pending pointdata on this pointid.
            // Arrival of a pointdata message eliminates a pending data msg.  If this is a delayed point, it will overwrite anyway!
            if( pDyn->inDelayedData() && !(pData->getTags() & TAG_POINT_DELAYED_UPDATE) )
            {
                pDyn->setInDelayedData(false);
                removePointDataFromPending(pData->getId());
            }

            if( pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE &&     // This is a controllable point.
                !(pDyn->getDispatch().getTags() & TAG_CONTROL_PENDING) &&           // This point is not expecting a control point change.
                !(pData->getTags() & TAG_POINT_DELAYED_UPDATE) &&                   // This data message is not delayed point data (future).
                pData->getValue() != pDyn->getValue() )                             // The point value has changed
            {
                // The value changed.  Any control in progress was just terminated manually.
                CtiPendable *pendable = CTIDBG_new CtiPendable(pData->getId(), CtiPendable::CtiPendableAction_ControlStatusChanged);
                pendable->_tags = pDyn->getDispatch().getTags();
                pendable->_value = pData->getValue();
                _pendingOpThread.push( pendable );
            }

            /*
             *  A point data can be submitted with the intent that it occur in the future.  If that bit indicator is set,
             *  the point is added to the pending list as a "pendingPointData".  Any subsequent change via pointdata
             *  pulls this pending operation from the list (per above)!
             */
            if(pData->getTags() & TAG_POINT_DELAYED_UPDATE)
            {
                if(gDispatchDebugLevel & DISPATCH_DEBUG_DELAYED_UPDATE)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Delayed update \"" << pData->getTime() << "\" is indicated on point data for " << resolveDeviceName(pPoint) << " / " << pPoint->getName() << endl;
                }

                pDyn->setInDelayedData(true);

                CtiPendingPointOperations *pendingPointData = CTIDBG_new CtiPendingPointOperations(pData->getId());
                pendingPointData->setType(CtiPendingPointOperations::pendingPointData);
                pendingPointData->setTime( pData->getTime() );
                pendingPointData->setPointData( (CtiPointDataMsg*)pData->replicateMessage() );

                addToPendingSet(pendingPointData);
            }
            else
            {
                // This is a point data which is to be processed right NOW.  It may generate signals and it
                // may clear active alarms.
                try
                {
                    if(!pPoint->isAlarmDisabled())
                    {
                        if(pPoint->isNumeric())
                        {
                            status = checkForNumericAlarms(pData, aWrap, pPoint);
                            /*if( TriggerMgr.isAVerificationPoint(pPoint->getPointID()) )
                            {
                                PtVerifyTriggerSPtr verificationPtr;
                                if( verificationPtr = TriggerMgr.getPointTriggerFromVerificationID(pPoint->getID()) )
                                {
                                    // This call will probably hit the database!
                                    CtiPointSPtr pCtrlPt = PointMgr.getPoint(verificationPtr->dbTriggerData.getPointID());

                                    if(pCtrlPt)      // We do know this point..
                                    {
                                        const CtiDynamicPointDispatch *pCtrlDyn = PointMgr.getDynamic(pCtrlPt);
                                        CtiPendable *pendable = CTIDBG_new CtiPendable(verificationPtr->dbTriggerData.getPointID(), CtiPendable::CtiPendableAction_ControlStatusComplete, NULL, pData->getTime() );
                                        pendable->_value = pData->getValue();
                                        pendable->_tags = pCtrlDyn->getDispatch().getTags();
                                        _pendingOpThread.push( pendable );

                                        if(pCtrlDyn->getDispatch().getTags() & TAG_CONTROL_PENDING)                                          // Are we still awaiting the start of control?
                                        {
                                            if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
                                            {
                                                if(pDyn->getValue() == pData->getValue())                       // Not changing, must be a control refresh
                                                {
                                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                    dout << CtiTime() << " " << resolveDeviceName(pPoint) << " / " << pPoint->getName() << " CONTROL CONTINUATION/COMPLETE." << endl;
                                                }
                                                else                                                            // Changing this time through.  Control begins now!
                                                {
                                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                    dout << CtiTime() << " " << resolveDeviceName(pPoint) << " / " << pPoint->getName() << " has gone CONTROL COMPLETE." << endl;
                                                }
                                            }
                                        }
                                    }
                                }
                            }*/
                        }
                        else if(pPoint->isStatus())
                        {
                            status = checkForStatusAlarms(pData, aWrap, pPoint);
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

                    if(pData->getTime() < pDyn->getTimeStamp())
                    {
                        pData->setTags(TAG_POINT_OLD_TIMESTAMP);
                    }
                    else
                    {
                        pData->resetTags(TAG_POINT_OLD_TIMESTAMP); // No one else may set this but us!
                    }

                    //  checked here to avoid calling the point manager inside processStalePoint()
                    if( PointMgr.hasProperty(pPoint->getPointID(), CtiTablePointProperty::STALE_UPDATE_TYPE) )
                    {
                        int updateType = PointMgr.getProperty(pPoint->getPointID(), CtiTablePointProperty::STALE_UPDATE_TYPE);
                        processStalePoint(pPoint, pDyn, updateType, *pData, aWrap);
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Point exception on " << pPoint->getName() << endl;
                }
            }
        }
        else
        {
            status = IDNF;
        }
    }

    return status;
}

INT CtiVanGogh::commandMsgUpdateFailedHandler(CtiCommandMsg *pCmd, CtiMultiWrapper &aWrap)
{
    INT status = NORMAL;

    CtiCommandMsg::CtiOpArgList_t &Op = pCmd->getOpArgList();

    if( Op[1] == CtiCommandMsg::OP_DEVICEID )    // All points on a device must be marked as nonUpdated
    {
        vector<CtiPointSPtr> points;
        vector<CtiPointSPtr>::iterator pointIter;
        LONG did = Op[(size_t)2];
        PointMgr.getEqualByPAO(did, points);

        for( pointIter = points.begin(); pointIter != points.end(); pointIter++ )
        {
            CtiPointSPtr pPoint = *pointIter;

            if(pPoint && pPoint->getDeviceID() == did)      // We know this point.. AND it is our device!
            {
                status = markPointNonUpdated(pPoint, aWrap);
            }
        }
    }
    else if( Op[1] == CtiCommandMsg::OP_POINTID )
    {
        CtiServerExclusion pmguard(_server_exclusion);
        CtiPointSPtr pPoint = PointMgr.getPoint(Op[(size_t)2]);

        if(pPoint)      // We know this point..
        {
            status = markPointNonUpdated(pPoint, aWrap);
        }
    }

    return status;
}


INT CtiVanGogh::markPointNonUpdated(CtiPointSPtr point, CtiMultiWrapper &aWrap)
{
    INT status = NORMAL;

    if( !(point->getType() == StatusPointType && point->getPointOffset() == 2000) ) // If not a comm status point
    {
        CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);

        // Make sure we use the correct enumeration based upon point type.
        INT alarm = (point->isStatus() ? CtiTablePointAlarming::nonUpdatedStatus : CtiTablePointAlarming::nonUpdatedNumeric);

        if( pDyn )
        {
            UINT quality = pDyn->getDispatch().getQuality();

            if(quality != NonUpdatedQuality)
            {
                pDyn->getDispatch().setQuality(NonUpdatedQuality);

                if(!_signalManager.isAlarmed(point->getID(), alarm)  && !pDyn->isConditionActive(alarm))
                {
                    const CtiTablePointAlarming alarming = PointMgr.getAlarming(point);

                    CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(point->getID(), 0, "Non Updated", getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm));
                    pSig->setPointValue(pDyn->getDispatch().getValue());

                    tagSignalAsAlarm(point, pSig, alarm);
                    updateDynTagsForSignalMsg(point,pSig,alarm,true);
                    aWrap.getMulti()->insert( pSig );
                }

                CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(point->getID(), pDyn->getValue(), pDyn->getQuality(), point->getType(), "Non Updated");

                pDat->setSource(DISPATCH_APPLICATION_NAME);
                pDat->setTags(pDyn->getDispatch().getTags() & ~(TAG_POINT_LOAD_PROFILE_DATA | TAG_POINT_MUST_ARCHIVE));
                pDat->setTime(pDyn->getTimeStamp());

                aWrap.getMulti()->insert( pDat );
            }
        }

    }

    return status;
}


CtiServer::ptr_type CtiVanGogh::getPILConnection()
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

    CtiServer::ptr_type CM;

    CtiServerExclusion guard(_server_exclusion);

    if(MainQueue_.entries() == 0)
    {
        while( (CM = mConnectionTable.remove(NonViableConnection, NULL)) )
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

            CtiPointSPtr pPoint;
            {
                CtiServerExclusion pmguard(_server_exclusion);
                pPoint = PointMgr.getPoint(sig.getId());

                if(pPoint)
                {
                    ngid = PointMgr.getAlarming(pPoint).getNotificationGroupID();
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

            if(!rdr.isValid())
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << loggedSQLstring << endl;
                }
            }

            while( rdr() ) // there better be Only one in there!
            {
                rdr["alarmcategoryid"] >> alm;
                rdr["categoryname"] >> name;
                rdr["notificationgroupid"] >> ngid;

                _alarmToDestInfo[alm].grpid = ngid;
                _alarmToDestInfo[alm].name = name;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " INFO: Alarm to Destination reload unable to acquire exclusion" << endl;
        }
    }
    catch( RWxmsg &e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << e.why() << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

INT CtiVanGogh::checkForStatusAlarms(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point)
{
    int alarm;
    INT status = NORMAL;

    if(point->isStatus())    // OK, we are indeed a status point.
    {
        CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);

        if(pDyn)     // We must know about the point!
        {
            CtiSignalMsg *pSig = 0;

            for( alarm = 0; alarm < CtiTablePointAlarming::invalidstatusstate; alarm++ )
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
                                activatePointAlarm(alarm,aWrap,point,pDyn,false);
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
                            checkStatusState(alarm, pData, aWrap, point, pDyn, pSig );
                            break;
                        }
                    case (CtiTablePointAlarming::changeOfState):
                        {
                            checkChangeOfState(alarm, pData, aWrap, point, pDyn, pSig );
                            break;
                        }
                    case (CtiTablePointAlarming::staleStatus): //This is haneled externally, look for processStalePoint(...)
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
                    activatePointAlarm(alarm,aWrap,point,pDyn,false);
                }
            }

            // We ALWAYS create an occurrence event to stuff a state event log into the systemlog table.
            if( pDyn->getDispatch().getValue() != pData->getValue() )
            {
                string txt = ResolveStateName(point->getStateGroupID(), (int)pData->getValue());
                string addn;

                if(pData->getQuality() == ManualQuality)
                {
                    addn = "Manual Update";
                }

                if(pSig)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** MEMORY Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                pSig = CTIDBG_new CtiSignalMsg(point->getID(), pData->getSOE(), txt, addn);
                pSig->setPointValue(pData->getValue());
                if(pSig != NULL)
                {
                    pSig->setUser(pData->getUser());
                    pSig->setTags(pDyn->getDispatch().getTags()  & ~SIGNAL_MANAGER_MASK);
                    aWrap.getMulti()->insert( pSig );
                }
            }
        }
    }

    return status;
}

INT CtiVanGogh::checkForNumericAlarms(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point)
{
    int alarm;
    INT status = NORMAL;

    if(point->isNumeric())
    {
        CtiPointNumericSPtr             pNumeric = boost::static_pointer_cast<CtiPointNumeric>(point);

        CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);

        if(pDyn)     // We must know about the point!
        {
            CtiSignalMsg    *pSig = NULL;
            UINT            tags = pDyn->getDispatch().getTags();

            // We check if the point has been sent in as a Manual Update.  If so, we ALWAYS log this occurence.
            if(pData->getQuality() == ManualQuality)
            {
                // If the value has changed, OR the last quality WAS NOT Manual, we then write into the system log!
                if( pDyn->getDispatch().getQuality() != ManualQuality || pDyn->getDispatch().getValue() != pData->getValue() )
                {
                    char tstr[80];
                    _snprintf(tstr, sizeof(tstr), "Value set to %.3f", pData->getValue());
                    string addn = "Manual Update";

                    pSig = CTIDBG_new CtiSignalMsg(point->getID(), pData->getSOE(), tstr, addn);
                    pSig->setPointValue(pData->getValue());
                    if(pSig != NULL)
                    {
                        pSig->setTags(pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK);
                        pSig->setUser(pData->getUser());
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
            }


            /*
             *  Check if this pData must be value modified due to reasonability limits.  This must be done first bec. it can alter
             *  the point value passed through to the remaining alarm conditions.
             */
            checkNumericReasonability( pData, aWrap, pNumeric, pDyn, pSig );

            for( alarm = 0; alarm < CtiTablePointAlarming::invalidnumericstate; alarm++ )
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
                        checkNumericLimits( alarm, pData, aWrap, pNumeric, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::nonUpdatedNumeric):    // alarm generated by commandMsgUpdateFailedHandler().
                    {
                        if(pDyn->getQuality() != NonUpdatedQuality)
                        {
                            activatePointAlarm(alarm,aWrap,point,pDyn,false);
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

    return status;
}

#include <io.h>

INT CtiVanGogh::sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp)
{
    INT status = NORMAL;
    vector<int> group_ids;

    group_ids.push_back(grp.getGroupID());

    CtiNotifAlarmMsg* alarm_msg = new CtiNotifAlarmMsg( group_ids,
                                                        sig.getSignalCategory(),
                                                        sig.getId(),
                                                        sig.getCondition(),
                                                        sig.getPointValue(),
                                                        sig.getMessageTime(),
                                                        !(sig.getTags() & TAG_UNACKNOWLEDGED_ALARM),
                                                        sig.getTags() & TAG_ACTIVE_ALARM);


    if(gDispatchDebugLevel & DISPATCH_DEBUG_NOTIFICATION)
    {
        dout << CtiTime() << " Sending alarm notification" << endl;
        alarm_msg->dump();
    }

    if(!getNotificationConnection()->valid())
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " << "Connection to notification server is not valid, alarm notification has been queued " << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    getNotificationConnection()->WriteConnQue(alarm_msg);

    return status;
}

string CtiVanGogh::getAlarmStateName( INT alarm )
{
    CtiServerExclusion guard(_server_exclusion);
    if( _alarmToDestInfo[alarm].grpid < SignalEvent )  // Zero is invalid!
    {
        // OK, prime the array!
        loadAlarmToDestinationTranslation();
    }
    string str = _alarmToDestInfo[alarm].name;
    return str;
}


/*----------------------------------------------------------------------------*
 * This method sweeps the table blasting anyone marked as questionable.
 *----------------------------------------------------------------------------*/
int CtiVanGogh::clientPurgeQuestionables(PULONG pDeadClients)
{
    extern bool isQuestionable(CtiServer::ptr_type &ptr, void* narg);

    int status = NORMAL;
    CtiServer::ptr_type Mgr;

    CtiServerExclusion server_guard(_server_exclusion);      // Get a lock on it.

    if(MainQueue_.entries() == 0)
    {

        if(pDeadClients != NULL) *pDeadClients = 0;

        while( (Mgr = mConnectionTable.remove(isQuestionable, NULL)) )
        {
            if(pDeadClients != NULL) (*pDeadClients)++;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Client did not respond to AreYouThere message" << endl;
                dout << CtiTime() << "  Client " << Mgr->getClientName() << " on " << Mgr->getPeer() << endl;
            }

            clientShutdown(Mgr);
        }
    }

    return status;
}


int  CtiVanGogh::clientRegistration(CtiServer::ptr_type CM)
{
    int         nRet = NoError;
    CtiTime      NowTime;
    RWBoolean   validEntry(TRUE);
    RWBoolean   questionedEntry(FALSE);
    RWBoolean   removeMgr(FALSE);
    CtiServer::ptr_type Mgr;

    CtiServerExclusion guard(_server_exclusion);

    if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
    {
        displayConnections();
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << NowTime.now() << " Validating " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << endl;
    }

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
                if(CM->getClientAppId() != (RWThreadId)0 && (Mgr->getClientAppId() == CM->getClientAppId()))
                {
                    // This guy is already registered. Might have lost his connection??
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime.now() << " " << CM->getClientName() << " just re-registered." << endl;
                    }
                    removeMgr = TRUE;
                    break;
                }
                else if(Mgr->getClientUnique())
                {
                    if( Mgr->getClientQuestionable() )       // has this guy been previously pinged and not responded?
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << NowTime.now() << " " <<
                            CM->getClientName() << " / " <<
                            CM->getClientAppId() << " / " <<
                            CM->getPeer() << " just won a client arbitration." << endl;
                        }

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

                        Mgr->WriteConnQue(pCmd, 500);   // Ask the old guy to respond to us..
                        CM->setClientRegistered(FALSE); // New guy is not quite kosher yet...

                        questionedEntry = TRUE;
                        validEntry = TRUE;

                        // Old one wanted to be the only one
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << NowTime.now() << " New client \"" << CM->getClientName() << "\" conflicts with an existing client." << endl;
                        }
                    }

                    break; // the for
                }
                else if(CM->getClientUnique())
                {
                    // New one wanted to be the only one
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime.now() << " New client is not unique as requested." << endl;
                    }
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime.now() << " Connection rejected, entry will be deleted." << endl;
        }

        CM->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15), 500);  // Ask the new guy to blow off..

        clientShutdown(CM);
    }

    if(removeMgr && Mgr)
    {

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime.now() << " " <<
            Mgr->getClientName() << " / " <<
            Mgr->getClientAppId() << " / " <<
            Mgr->getPeer() << " just _lost_ the client arbitration." << endl;
        }

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
int  CtiVanGogh::clientArbitrationWinner(CtiServer::ptr_type CM)
{
    int status = NORMAL;
    CtiServer::ptr_type Mgr;

    CtiServerExclusion guard(_server_exclusion);

    // Now that it is locked, get an iterator
    CtiServer::spiterator  itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        if((Mgr != CM)                                     &&       // Don't match on yourself
           (Mgr->getClientName() == CM->getClientName())   &&       // Names match
           (Mgr->getClientRegistered() == RWBoolean(FALSE)))        // Other Mgr is not registered completely yet
        {
            CtiTime Now;
            // The connection Mgr has been refuted by the prior manager. Shut Mgr down...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Connection " << Mgr->getClientName() << " on " <<
                Mgr->getPeer() << " has been blocked by a prior client of the same name" << endl;
                dout << Now << " Dispatch will shut it down now." << endl;
            }

            Mgr->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15), 500);  // Ask the new guy to blow off..

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
                msgtype = string("MSG_POINTREGISTRATION");
                break;
            }
        case MSG_REGISTER:
            {
                msgtype = string("MSG_REGISTER");
                break;
            }
        case MSG_POINTDATA:
            {
                msgtype = string("MSG_POINTDATA");
                break;
            }
        case MSG_SIGNAL:
            {
                msgtype = string("MSG_SIGNAL");
                break;
            }
        case MSG_DBCHANGE:      // Everyone gets these, no matter what!
            {
                msgtype = string("MSG_DBCHANGE");
                break;
            }
        case MSG_COMMAND:
            {
                msgtype = string("MSG_COMMAND");
                break;
            }
        case MSG_PCRETURN:
        case MSG_MULTI:
            {
                msgtype = string("MSG_PCRETURN/MSG_MULTI");
                break;
            }
        default:
            {
                msgtype = string("UNKNONWN");
                break;
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** DISPATCH HAS PROCESSED A " << msgtype << " MSG **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        pMsg->dump();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** (END " << msgtype << " MSG) **** " << endl;
        }
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
            ResetBreakAlloc();  // Make certain the debug allocator does not break our spirit.

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Starting loadRTDB. " << (pChg != 0 ? string(pChg->getCategory() + " DBChange present.") : "No DBChange present.") << endl;
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
                        //TriggerMgr is really not used so I am currently not loading it here!
                    }
                    else if(pChg != NULL && (pChg->getTypeOfChange() == ChangeTypeUpdate || pChg->getTypeOfChange() == ChangeTypeAdd))
                    {
                        PointMgr.updatePoints(pChg->getId(), 0, resolvePointType(pChg->getObjectType()) );

                        //TriggerMgr.refreshList(pChg->getId(), PointMgr);
                    }
                    else if(pChg != NULL && pChg->getTypeOfChange() == ChangeTypeDelete)
                    {
                        PointMgr  .erase(pChg->getId());
                        //TriggerMgr.erase(pChg->getId());
                    }
                    else if(pChg == NULL)
                    {
                        if(gConfigParms.isTrue("DISPATCH_LOAD_ALL_POINTS"))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << Now << " DISPATCH_LOAD_ALL_POINTS is set" << endl;
                            }
                            PointMgr.refreshList();
                            PointMgr.loadAllStaticData();
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << Now << " DISPATCH_LOAD_ALL_POINTS is not set" << endl;
                            }
                            PointMgr.loadAllStaticData();
                        }
                        //TriggerMgr.refreshList(0, PointMgr);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << Now << " LoadRTDB point loading entered an unknown state " << pChg->getDatabase() << " " << pChg->getTypeOfChange() << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " INFO: Could not acquire server exclusion to perform point reload." << endl;
                }
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << deltaT << " seconds to collect point data from database... " << endl;
            }

#if 0
            Now = Now.now();

            // Make sure any signals are in the DB
            writeSignalsToDB(true);

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << deltaT << " seconds to write signal list to DB" << endl;
            }
#endif
            Now = Now.now();

            if( pChg == NULL || (pChg->getDatabase() == ChangeAlarmCategoryDb) )
            {
                // Update our Bookkeeping data!!!
                loadAlarmToDestinationTranslation();
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << deltaT << " seconds to load alarm to destination translations" << endl;
            }
            Now = Now.now();

            if( pChg == NULL || (pChg->getDatabase() == ChangeStateGroupDb) )
            {
                ReloadStateNames();
            }
            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << deltaT << " seconds to load state names" << endl;
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

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Device delete for PAO id " << pChg->getId() << endl;
                        }

                        _deviceLiteSet.erase(pChg->getId());
                    }
                    else if(pChg && pChg->getTypeOfChange() == ChangeTypeUpdate)
                    {
                        CtiDeviceLiteSet_t::iterator iter = _deviceLiteSet.find(id);
                        if(iter != _deviceLiteSet.end())
                        {
                            deviceDisabled =   iter->isDisabled();
                            controlInhibited = iter->isControlInhibited();
                        }
                    }



                    Now = Now.now();
                    loadDeviceLites(id);
                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << deltaT << " seconds to load lite device data" << endl;
                    }

                    Now = Now.now();
                    string username = pChg ? pChg->getUser() : string("Dispatch Application");

                    // If it is an update and the disable flag has changed, call adjust.
                    // On add, deviceDisabled and controlInhibited == false.
                    // If the device has disable or inhibited set on add, we call adjust.
                    if(pChg && (pChg->getTypeOfChange() == ChangeTypeUpdate || pChg->getTypeOfChange() == ChangeTypeAdd))
                    {
                        CtiDeviceLiteSet_t::iterator iter = _deviceLiteSet.find(id);
                        if(iter != _deviceLiteSet.end())
                        {
                            if(deviceDisabled != iter->isDisabled() || controlInhibited != iter->isControlInhibited())
                            {
                                adjustDeviceDisableTags(id, true, username);
                            }
                        }
                    }

                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << deltaT << " seconds to adjust disabled status of devices" << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " INFO: Device lite info was not reloaded.  Exclusion could not be obtained." << endl;
                }
            }

            Now = Now.now();
            if( pChg == NULL || (pChg->getDatabase() == ChangeCustomerContactDb) )
            {
                id = ((pChg == NULL) ? 0 : pChg->getId());
                loadCICustomers(id);
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << deltaT << " seconds to load CI Customers" << endl;
            }
            Now = Now.now();

            if(pChg != NULL && (pChg->getDatabase() == ChangeNotificationGroupDb || pChg->getDatabase() == ChangeNotificationRecipientDb))
            {
                CtiServerExclusion guard(_server_exclusion, 1000);

                while(!guard.isAcquired() && !guard.tryAcquire(5000))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " INFO: Unable to acquire exclusion for notification group load. Will try again." << endl;
                    }
                }

                if(guard.isAcquired())
                {
                    // We will own that mutex following at this point->
                    if(pChg->getDatabase() == ChangeNotificationGroupDb)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Notification Groups will be reloaded on next usage." << endl;
                        }
                        // Group or destinations have changed!
                        CtiNotificationGroupSet_t::iterator git;

                        for(git = _notificationGroupSet.begin(); git != _notificationGroupSet.end(); git++ )
                        {
                            CtiTableNotificationGroup &theGroup = *git;
                            theGroup.setDirty(true);
                        }
                    }
                    else if(pChg->getDatabase() == ChangeNotificationRecipientDb )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Notification Recipients will be reloaded on next usage." << endl;
                        }
                        // Recipients have changed
                        CtiContactNotificationSet_t::iterator cnit;

                        for(cnit = _contactNotificationSet.begin(); cnit != _contactNotificationSet.end(); cnit++)
                        {
                            CtiTableContactNotification &CNotif = *cnit;
                            CNotif.setDirty(true);
                        }
                    }
                }
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << deltaT << " seconds to adjust notification groups" << endl;
            }
            Now = Now.now();

            refreshCParmGlobals(force);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Done loading RTDB... " << endl;
            }

            Refresh = nextScheduledTimeAlignedOnRate( Now, gDispatchReloadRate );
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(pChg != NULL)
    {
        delete pChg;
    }
}

/*
 *  returns description of the device which IS this pao.
 */
string CtiVanGogh::resolveDeviceDescription(LONG PAO)
{
    string rStr;

    if(PAO > 0)
    {
        CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(PAO);

        if( dliteit != _deviceLiteSet.end() )
        {
            // dliteit should be an iterator which represents the lite device now!
            CtiDeviceBaseLite &dLite = *dliteit;
            rStr = dLite.getDescription();
        }
    }

    return rStr;
}

/*
 *  returns name of the device which owns this point.
 */
string CtiVanGogh::resolveDeviceName(const CtiPointSPtr &aPoint)
{
    string rStr;
    LONG devid = aPoint->getDeviceID();

    if(devid > 0)
    {
        CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(devid);

        if( dliteit != _deviceLiteSet.end() )
        {
            // dliteit should be an iterator which represents the lite device now!
            CtiDeviceBaseLite &dLite = *dliteit;
            rStr = dLite.getName();
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
        CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(PAOId);

        if( dliteit != _deviceLiteSet.end() )
        {
            // dliteit should be an iterator which represents the lite device now!
            CtiDeviceBaseLite &dLite = *dliteit;
            rStr = dLite.getName();
        }
    }

    return rStr;
}

string CtiVanGogh::resolveDeviceObjectType(const LONG devid)
{
    string rStr;

    if(devid > 0)
    {
        CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(devid);

        if( dliteit != _deviceLiteSet.end() )
        {
            // dliteit should be an iterator which represents the lite device now!
            CtiDeviceBaseLite &dLite = *dliteit;
            rStr = dLite.getObjectType();
        }
    }

    return rStr;
}

bool CtiVanGogh::isDeviceIdValid(const LONG devid)
{
    bool bret = false;

    if(devid > 0)
    {
        try
        {
            CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(devid);
            bret = (dliteit != _deviceLiteSet.end() );
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return bret;
}

bool CtiVanGogh::isDeviceGroupType(const LONG devid)
{
    bool bret = false;

    if(devid > 0)
    {
        try
        {
            CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(devid);
            if( dliteit != _deviceLiteSet.end() )
            {
                // dliteit should be an iterator which represents the lite device now!
                CtiDeviceBaseLite &dLite = *dliteit;
                bret = ciStringEqual(dLite.getClass(), "group");
            }
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return bret;
}

bool CtiVanGogh::isDeviceGroupType(const CtiDeviceBaseLite *device)
{
    bool bret = false;

    if(device != NULL)
    {
        try
        {
            bret = ciStringEqual(device->getClass(), "group");
        }
        catch(...)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    return bret;
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
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Loading DeviceLites " << endl;
    }
    {
        const string sql = CtiDeviceBaseLite().getSQLCoreStatement(id);

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        if(id)
        {
            rdr << id;
        }

        rdr.execute();

        while( rdr() )
        {
            CtiDeviceBaseLite dLite;
            dLite.DecodeDatabaseReader(rdr);

            pair< CtiDeviceLiteSet_t::iterator, bool > resultpair;

            resultpair = _deviceLiteSet.insert( dLite );
            if(resultpair.second == false)                          // Couldn't insert
            {
                if( resultpair.first != _deviceLiteSet.end() )      // Found a match
                {
                    *(resultpair.first) = dLite;                    // Copy it over the match in case it has changed.
                }
            }
        }
    }

    if(DebugLevel & 0x00010000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << CtiTime() << " Done Loading DeviceLites " << endl;
    }
}

/*
 *  This method reloads all currently loaded devicelites in memory.
 */
void CtiVanGogh::loadDeviceNames()
{
    CtiServerExclusion guard(_server_exclusion, 10000);

    if(guard.isAcquired() && !_deviceLiteSet.empty())
    {
        CtiDeviceLiteSet_t::iterator dnit;
        bool reloadFailed = false;

        for(dnit = _deviceLiteSet.begin(); dnit != _deviceLiteSet.end(); dnit++ )
        {
            CtiDeviceBaseLite &dLite = *dnit;
            if(!dLite.Restore())
            {
                reloadFailed = true;
                break;
            }
        }

        if(reloadFailed)
        {
            _deviceLiteSet.clear();          // All stategroups will be reloaded on their next usage..  This shouldn't happen very often
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Device Lite Set reset. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}

void CtiVanGogh::loadCICustomers(LONG id)
{
    CtiServerExclusion guard(_server_exclusion, 2500);

    if(guard.isAcquired())
    {
        CtiDeviceCICustSet_t::iterator it;
        bool reloadFailed = false;

        if(id)
        {
            it = _ciCustSet.find(id);
            if( it != _ciCustSet.end() )
            {
                CtiTableCICustomerBase &theCust = *it;
                theCust.Restore();
            }
        }
        else
        {
            for(it = _ciCustSet.begin(); it != _ciCustSet.end(); it++ )
            {
                CtiTableCICustomerBase &theCust = *it;
                if(!theCust.Restore())
                {
                    reloadFailed = true;
                    break;
                }
            }

            if(reloadFailed)
            {
                _ciCustSet.clear();          // All cicustomers will be reloaded on their next usage..  This shouldn't happen very often
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " CI Customer Set has been reset. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " INFO: CI Customer Set was not reloaded. Exclusion oculd not be acquired." << endl;
    }
}

CtiTableContactNotification* CtiVanGogh::getContactNotification(LONG cNotifID)
{
    CtiTableContactNotification* pCNotif = NULL;
    CtiServerExclusion guard(_server_exclusion);
    CtiTableContactNotification cNotif(cNotifID);

    CtiContactNotificationSet_t::iterator cnit = _contactNotificationSet.find(cNotif);

    if(cnit == _contactNotificationSet.end())
    {
        // We need to load it up, and then insert it!
        cNotif.Restore();

        pair< CtiContactNotificationSet_t::iterator, bool > resultpair;
        resultpair = _contactNotificationSet.insert(cNotif);

        if(resultpair.second == true)
        {
            cnit = resultpair.first;
        }
    }

    if(cnit != _contactNotificationSet.end())
    {
        pCNotif = &(*cnit);

        if(pCNotif->isDirty())
        {
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Reloading ContactNotification " << pCNotif->getContactNotificationID() << endl;
            }
            pCNotif->Restore();
        }
    }

    return pCNotif;
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
    CtiTableNotificationGroup mygroup( ngid );
    CtiServerExclusion guard(_server_exclusion);

    CtiNotificationGroupSet_t::iterator git = _notificationGroupSet.find( mygroup );

    if( git == _notificationGroupSet.end() )
    {
        // We need to load it up, and then insert it!
        mygroup.Restore();

        pair< CtiNotificationGroupSet_t::iterator, bool > resultpair;
        resultpair = _notificationGroupSet.insert( mygroup );

        if(resultpair.second == true)
        {
            git = resultpair.first;
        }
    }

    if( git != _notificationGroupSet.end() )
    {
        // git should be an iterator which represents the group now!
        CtiTableNotificationGroup &theGroup = *git;

        if(theGroup.isDirty())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "  Reloading Notification Group " << theGroup.getGroupName() << endl;
            }
            theGroup.Restore();     // Clean the thing then!
        }
        sendMail(sig, theGroup);

    }
}

void CtiVanGogh::displayConnections(void)
{
    CtiServer::ptr_type Mgr;
    CtiServerExclusion guard(_server_exclusion);
    CtiServer::spiterator  itr;

    for(itr = mConnectionTable.getMap().begin(); itr != mConnectionTable.getMap().end(); itr++)
    {
        Mgr = itr->second;

        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " ";
        dout << Mgr->getClientName() << " / " <<  Mgr->getClientAppId() << " / " << Mgr->getPeer();
        dout << " " << (Mgr->isViable() ? "is Viable" : "is NOT Viable" ) << endl;
    }
}

/********************
 *  This method uses the dynamic dispatch POINT settings to determine if the inbound setmask and tagmask indicate a change
 *  from the current settings.  If a change is indicated, the static tags of the point will be updated.
 */
bool CtiVanGogh::ablementPoint(CtiPointSPtr &pPoint, bool &devicedifferent, UINT setmask, UINT tagmask, string user, CtiMultiMsg &Multi)
{
    bool different = false;

    devicedifferent = false;        // Make it false by default.

    if(pPoint)
    {
        CtiSignalMsg *pSig = NULL;

        {
            CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPoint);

            if(pDyn)
            {
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

                        if(updatePointStaticTables(pPoint->getPointID(), pttags, tagmask, user, Multi))
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Updated " << pPoint->getName() << "'s point enablement status" << endl;
                        }
                    }

                    if(currdvtags != dvtags)    // Is the difference in the device tags?
                    {
                        addnl += string("Point tags: ") + (dvtags_cnt > currdvtags_cnt ? " device disable" : " device enable");
                        devicedifferent = true;
                    }

                    pDyn->getDispatch().resetTags(tagmask);
                    pDyn->getDispatch().setTags(newpttags);

                    {
                        CtiSignalMsg *pTagSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), 0, "Tag Update", addnl);
                        pTagSig->setPointValue(pDyn->getDispatch().getValue());
                        pTagSig->setMessagePriority(15);
                        pTagSig->setUser(user);
                        pTagSig->setTags( pDyn->getDispatch().getTags());
                        postMessageToClients(pTagSig);
                        delete pTagSig;
                    }
                }
            }
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
bool CtiVanGogh::ablementDevice(CtiDeviceLiteSet_t::iterator &dliteit, UINT setmask, UINT tagmask)
{
    bool delta = false;     // Anything changed???

    CtiDeviceBaseLite &dLite = *dliteit;

    if( tagmask & TAG_DISABLE_DEVICE_BY_DEVICE )
    {
        bool initialsetting = dLite.isDisabled();
        dLite.setDisableFlag((TAG_DISABLE_DEVICE_BY_DEVICE & setmask ? "Y" : "N"));
        if(initialsetting != dLite.isDisabled()) delta = true;
    }

    if( tagmask & TAG_DISABLE_CONTROL_BY_DEVICE )
    {
        bool initialsetting = dLite.isControlInhibited();
        dLite.setControlInhibitFlag((TAG_DISABLE_CONTROL_BY_DEVICE & setmask ? "Y" : "N"));
        if(initialsetting != dLite.isControlInhibited()) delta = true;
    }

    if( tagmask & TAG_DISABLE_ALARM_BY_DEVICE )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return delta;
}

CtiTableCICustomerBase* CtiVanGogh::getCustomer( LONG custid )
{
    CtiTableCICustomerBase *pCustomer = NULL;

    CtiServerExclusion guard(_server_exclusion);
    CtiTableCICustomerBase customer( custid );
    CtiDeviceCICustSet_t::iterator custit = _ciCustSet.find( customer );

    if( custit == _ciCustSet.end() )
    {
        // We need to load it up, and then insert it!
        customer.Restore();

        pair< CtiDeviceCICustSet_t::iterator, bool > resultpair;
        resultpair = _ciCustSet.insert( customer );

        if(resultpair.second == true)
        {
            custit = resultpair.first;
        }
    }

    if( custit != _ciCustSet.end() )
    {
        // rit should be an iterator which represents the recipient now!
        pCustomer = &(*custit);
    }

    return pCustomer;
}


CtiVanGogh::CtiVanGogh() : _notificationConnection(NULL)
{
    {
        CtiServerExclusion guard(_server_exclusion);
        for(int i = 0; i < MAX_ALARM_TRX; i++)
        {
            _alarmToDestInfo[i].grpid = 0; // Zero is invalid!
        }
    }
    ShutdownOnThreadTimeout = true;
    _tagManager.start();
}

void  CtiVanGogh::shutdown()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch Server Shutting Down " << endl;
    }

    _tagManager.interrupt(CtiThread::SHUTDOWN);
    _tagManager.join();

    try
    {
        Inherited::shutdown();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

}

void CtiVanGogh::VGRPHWriterThread()
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("RawPointHistory Writer Thread");

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch RawPointHistory Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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

            rwSleep(1000);

            writeArchiveDataToDB();
        }

        // Make sure no one snuck in under the wire..
        writeArchiveDataToDB(true);
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch RawPointHistory Writer Thread shutting down" << endl;
    }

    return;
}

void CtiVanGogh::VGDBWriterThread()
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("DB Writer Thread");

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch DB Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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

                rwSleep(1000);

                writeCommErrorHistoryToDB();
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        _timedOpThread.join();      // _timedOpThread is a producer for us...
        // Make sure no one snuck in under the wire..
        writeCommErrorHistoryToDB(true);
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch DB Writer Thread shutting down" << endl;
    }

    return;
}

/******************************************************************************
*   Monitor the applications that are registered in thread_monitor.h and MUST
*   have those system points defined. Function will wait for 15 minutes then
*   make sure it has heard from every device every 15 minutes or so.
******************************************************************************/
void CtiVanGogh::VGAppMonitorThread()
{
    CtiTime NextThreadMonitorReportTime;
    CtiThreadMonitor::State previous = CtiThreadMonitor::Normal;
    CtiPointDataMsg vgStatusPoint;
    long pointID = ThreadMonitor.getPointIDFromOffset(CtiThreadMonitor::Dispatch);

    //on startup wait for 10 minutes!
    for(int i=0;i<120 && !bGCtrlC;i++)
    {
        rwSleep(5000);//5 second sleep

        if(NextThreadMonitorReportTime.now() > NextThreadMonitorReportTime)
        {
            CtiMessage* pData = (CtiMessage *)CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString().c_str());
            pData->setSource(DISPATCH_APPLICATION_NAME);
            MainQueue_.putQueue(pData);

            NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate(CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2);
        }
    }

    if(!bGCtrlC)
    {
        //First find all of my point ID's so I dont ever have to look them up again.
        CtiThreadMonitor::PointIDList pointIDList = ThreadMonitor.getPointIDList();
        CtiThreadMonitor::PointIDList::iterator pointListWalker;
        CtiPointSPtr pPt;
        CtiDynamicPointDispatchSPtr pDynPt;
        CtiTime compareTime;

        while(!bGCtrlC)
        {
            compareTime = CtiTime::now();
            compareTime -= 600;//take away 10 minutes

            for(pointListWalker = pointIDList.begin();pointListWalker!=pointIDList.end();pointListWalker++)
            {
                if(*pointListWalker !=0)
                {
                    // This call probably hits the database.
                    if( pPt = PointMgr.getPoint(*pointListWalker) )
                    {
                        if(pDynPt = PointMgr.getDynamic(pPt))
                        {
                            if((pDynPt->getTimeStamp()).seconds()<(compareTime))
                            {
                                //its been more than 15 minutes, set the alarms!!!
                                CtiMessage* pData = (CtiMessage *)CTIDBG_new CtiPointDataMsg(*pointListWalker, CtiThreadMonitor::Dead, NormalQuality, StatusPointType, "Thread has not responded for 15 minutes.");
                                pData->setSource(DISPATCH_APPLICATION_NAME);
                                MainQueue_.putQueue(pData);
                            }
                        }
                    }
                }
            }

            //no need to process very often, wait say.... randomly ill pick 3 minutes or so
            for(int i=0;i<=36 && !bGCtrlC;i++)
            {
                rwSleep(5000);//5 second sleep

                //Check thread watcher status
                if(pointID!=0)
                {
                    CtiThreadMonitor::State next;

                    if( NextThreadMonitorReportTime.now() > NextThreadMonitorReportTime ||
                       (next = ThreadMonitor.getState()) != previous)
                    {
                        NextThreadMonitorReportTime = nextScheduledTimeAlignedOnRate(CtiTime::now(), CtiThreadMonitor::StandardMonitorTime / 2);
                        previous = next;

                        CtiMessage *pData = (CtiMessage *)CTIDBG_new CtiPointDataMsg(pointID, ThreadMonitor.getState(), NormalQuality, StatusPointType, ThreadMonitor.getString());
                        pData->setSource(DISPATCH_APPLICATION_NAME);
                        MainQueue_.putQueue(pData);
                    }
                }
            }
        }
    }
}


/*----------------------------------------------------------------------------*
 * This FUNCTION is used to apply against the MainQueue_ with the expectation
 * that some number of them have a pointer to the to-be-deleted connection.
 * That connection in the mainqueue must be nullified.
 *----------------------------------------------------------------------------*/
void ApplyBlankDeletedConnection(CtiMessage*&Msg, void *Conn)
{
    if( Msg->getConnectionHandle() == Conn )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Msg on MainQueue found which refers to a connection which no longer exists. Removing reference, msg will be processed." << endl;
        }
        Msg->setConnectionHandle(NULL);
    }
}

CtiVanGogh::CtiDeviceLiteSet_t::iterator CtiVanGogh::deviceLiteFind(const LONG paoId)
{
    CtiDeviceBaseLite &dLite = CtiDeviceBaseLite(paoId);
    CtiDeviceLiteSet_t::iterator dliteit = _deviceLiteSet.end();

    CtiServerExclusion guard(_server_exclusion, 30000);

    if(guard.isAcquired())
    {
        dliteit = _deviceLiteSet.find( dLite );

        if( dliteit == _deviceLiteSet.end() )
        {
            // We need to load it up, and/or then insert it!
            if(dLite.Restore())
            {
                pair< CtiDeviceLiteSet_t::iterator, bool > resultpair;

                // Try to insert. Return indicates success.
                resultpair = _deviceLiteSet.insert( dLite );

                if(resultpair.second == true)
                {
                    dliteit = resultpair.first;      // Iterator which points to the set entry.
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " TID " << rwThreadId() << " Unable to aqcuire the _server_exclusion for deviceLiteFind() " << __FILE__ << " (" << __LINE__ << ") Owned by TID " << _server_exclusion.lastAcquiredByTID() << endl;
    }

    return dliteit;
}


void CtiVanGogh::reportOnThreads()
{

    try
    {
        CtiServerExclusion sguard(_server_exclusion, 100);

        if(sguard.isAcquired())
        {
            RWThreadFunction &aThr = _rphThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " RawPointHistory Writer Thread is not running " << endl;
                }
            }

            aThr = _archiveThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Archiver Thread is not running " << endl;
                }
            }

            aThr = _timedOpThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Timed Operation Thread is not running " << endl;
                }
            }

            aThr = _dbThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " DBThread is not running " << endl;
                }
            }

            aThr = _dbSigThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " DB Signal Thread is not running " << endl;
                }
            }


            aThr = _dbSigEmailThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " DB Signal Email Thread is not running " << endl;
                }
            }
        }
        else
        {
            // OK, we just had a potential deadlock prevention here...
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

bool CtiVanGogh::writeControlMessageToPIL(LONG deviceid, LONG rawstate, CtiPointStatusSPtr pPoint, const CtiCommandMsg *Cmd )
{
    bool control = false;
    string  cmdstr;
    CtiRequestMsg *pReq = 0;

    const boost::optional<CtiTablePointStatusControl> controlParameters = pPoint->getControlParameters();

    if( controlParameters &&
        controlParameters->getStateZeroControl().length() > 0 &&
        controlParameters->getStateOneControl().length() > 0 )
    {
        cmdstr += ((rawstate == UNCONTROLLED) ? controlParameters->getStateZeroControl(): controlParameters->getStateOneControl() );
    }
    else
    {
        cmdstr += string("Control ");
        cmdstr += ResolveStateName(pPoint->getStateGroupID(), rawstate);
    }

    if( stringContainsIgnoreCase( cmdstr, "control ") ) control = true;  // Do this before we append the point name.  It will likely include "control"

    cmdstr += string(" select pointid " + CtiNumStr(pPoint->getPointID()));

    if(pReq = CTIDBG_new CtiRequestMsg( deviceid, cmdstr ))
    {
        pReq->setUser( Cmd->getUser() );
        pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!
        writeMessageToClient(pReq, string(PIL_REGISTRATION_NAME));
    }

    delete pReq;
    pReq = 0;

    return control;
}

void CtiVanGogh::writeAnalogOutputMessageToPIL(long deviceid, long pointid, long value, const CtiCommandMsg *Cmd)
{
    string cmdstr;

    cmdstr += "putvalue analog value ";
    cmdstr += CtiNumStr(value);

    cmdstr += " select pointid ";
    cmdstr += CtiNumStr(pointid);

    if( CtiRequestMsg *pReq = new CtiRequestMsg(deviceid, cmdstr) )
    {
        pReq->setUser( Cmd->getUser() );
        pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!

        writeMessageToClient(pReq, string(PIL_REGISTRATION_NAME));

        delete pReq;
    }
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
                if( CM->WriteConnQue( pReq->replicateMessage(), 5000 ) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Message to PIL was unable to be queued" << endl;
                }

                if(bDone)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " Multiple PIL entries in dispatch list." << endl;
                        dout << "  Will submit request on ALL entries" << endl;
                    }
                }
                bDone = true;
            }
        }
    }

    if(!bDone)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Unable to submit control command.  Port Control Interface not currently" << endl;
            dout << "  registered with dispatch.  Control request discarded" << endl;
            dout << "  ---- Control Request ---- " << endl;
            pReq->dump();
            dout << "  ---- End Control Request ---- " << endl;
        }
    }
}

void CtiVanGogh::bumpDeviceToAlternateRate(CtiPointSPtr pPoint)
{
    if(!pPoint->isPseudoPoint())
    {
        CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );
        if(pAltRate)
        {
            int controlTimeout = DefaultControlExpirationTime;

            if( pPoint->isStatus() )
            {
                CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(pPoint);

                if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
                {
                    controlTimeout = controlParameters->getCommandTimeout();
                }
            }

            pAltRate->insert(-1); // token
            pAltRate->insert( pPoint->getDeviceID() );
            pAltRate->insert( -1 );                     // Seconds since midnight, or NOW if negative.
            pAltRate->insert( controlTimeout );

            writeMessageToScanner( pAltRate );
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Requesting scans at the alternate scan rate for " << resolveDeviceName( pPoint ) << endl;
            }
        }
    }
}

void CtiVanGogh::bumpDeviceFromAlternateRate(CtiPointSPtr pPoint)
{
    if(!pPoint->isPseudoPoint())
    {
        CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );
        if(pAltRate)
        {
            pAltRate->insert(-1); // token
            pAltRate->insert( pPoint->getDeviceID() );
            pAltRate->insert( -1 );                     // Seconds since midnight, or NOW if negative.
            pAltRate->insert( 0 );                      // Stop it already!

            writeMessageToScanner( pAltRate );
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Requesting scans at the normal scan rate for " << resolveDeviceName( pPoint ) << endl;
            }
        }
    }
}

void CtiVanGogh::writeMessageToScanner(const CtiCommandMsg *Cmd)
{
    // this guy goes to scanner only
    CtiServer::ptr_type scannerCM = getScannerConnection();

    if(scannerCM)
    {
        // pass the message through
        if(scannerCM->WriteConnQue(Cmd->replicateMessage(), 5000))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

INT CtiVanGogh::updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, string user, CtiMultiMsg &sigList)
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

            paobjectSuccess = executeUpdater(updater);
        }

        {
            static const std::string sql_device_update = "update device set controlinhibit = ? where deviceid = ?";

            Cti::Database::DatabaseWriter   updater(conn, sql_device_update);

            updater
                << ( TAG_DISABLE_CONTROL_BY_DEVICE & setmask ? std::string("Y") : std::string("N") )
                << did;

            deviceSuccess = executeUpdater(updater);
        }
    }

    if ( paobjectSuccess || deviceSuccess )
    {
        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(did, ChangePAODb, "Device", objtype, ChangeTypeUpdate);
        dbChange->setUser(user);
        dbChange->setSource(DISPATCH_APPLICATION_NAME);
        sigList.insert(dbChange);
    }

    return ( paobjectSuccess && deviceSuccess ) ? NORMAL : UnknownError;
}

INT CtiVanGogh::updatePointStaticTables(LONG pid, UINT setmask, UINT tagmask, string user, CtiMultiMsg &Multi)
{
    bool pointSuccess = false;
    bool pointStatusSuccess = false;

    CtiServerExclusion smguard(_server_exclusion, 10000);
    if(smguard.isAcquired())
    {
        if (TAG_DISABLE_POINT_BY_POINT & tagmask)
        {
            static const std::string sql_point_update = "update point set serviceflag = ? where pointid = ?";

            Cti::Database::DatabaseConnection   conn;
            Cti::Database::DatabaseWriter       updater(conn, sql_point_update);

            updater
                << ( TAG_DISABLE_POINT_BY_POINT & setmask ? std::string("Y") : std::string("N") )
                << pid;

            pointSuccess = executeUpdater(updater);
        }

        if (TAG_DISABLE_CONTROL_BY_POINT & tagmask)
        {
            static const std::string sql_pointstatus_update = "update pointcontrol set controlinhibit = ? where pointid = ?";

            Cti::Database::DatabaseConnection   conn;
            Cti::Database::DatabaseWriter       updater(conn, sql_pointstatus_update);

            updater
                << ( TAG_DISABLE_CONTROL_BY_POINT & setmask ? std::string("Y") : std::string("N") )
                << pid;

            pointStatusSuccess = executeUpdater(updater);
        }
    }

    if ( pointSuccess || pointStatusSuccess )
    {
        CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(pid, ChangePointDb, "Point", "Point", ChangeTypeUpdate);
        dbChange->setUser(user);
        dbChange->setSource(DISPATCH_APPLICATION_NAME);
        dbChange->setMessagePriority(15);
        Multi.insert(dbChange);
    }

    return ( pointSuccess && pointStatusSuccess ) ? NORMAL : UnknownError;
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
    if(!_deviceLiteSet.empty())
    {
        bool devicedifferent = false;

        UINT tagmask = TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_CONTROL_BY_DEVICE;

        {
            CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

            if(pMulti)
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

                    CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(id);
                    CtiDeviceBaseLite &dLite = *dliteit;

                    for(int i = 0; i < points.size() && dliteit != _deviceLiteSet.end() ; i++)
                    {
                        CtiPointSPtr pPoint = points[i];

                        UINT setmask = 0;
                        setmask |= (dLite.getDisableFlag() == "Y" ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                        setmask |= (dLite.getControlInhibitFlag() == "Y" ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                        ablementPoint(pPoint, devicedifferent, setmask, tagmask, user, *pMulti);
                    }
                }

                if(devicedifferent && !dbchange)
                {
                    CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(id);

                    if( dliteit != _deviceLiteSet.end() )   // We do know this device..
                    {
                        CtiDeviceBaseLite &dLite = *dliteit;

                        UINT setmask = 0;

                        setmask |= (dLite.getDisableFlag() == "Y" ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                        setmask |= (dLite.getControlInhibitFlag() == "Y" ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                        if(updateDeviceStaticTables(dLite.getID(), setmask, tagmask, user, *pMulti))
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Updated " << dLite.getName() << "'s device enablement status" << endl;
                            }
                        }
                    }
                }

                if(pMulti->getData().size())
                {
                    MainQueue_.putQueue(pMulti);
                }
                else
                {
                    delete pMulti;
                }
            }
        }
    }
}

UINT CtiVanGogh::writeRawPointHistory(bool justdoit, int maxrowstowrite)
{
    UINT panicCounter = 0;      // Make sure we don't write for too long...

    try
    {
        CtiTableRawPointHistory *pTblEntry;

        Cti::Database::DatabaseConnection   conn;

        if ( ! conn.isValid() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            return 0;
        }

       {
            Cti::Database::DatabaseTransaction trans(conn);

            try
            {
                while( ( justdoit || (panicCounter < maxrowstowrite) ) && (pTblEntry = _archiverQueue.getQueue(0)) != NULL)
                {
                    panicCounter++;
                    pTblEntry->Insert(conn);
                    delete pTblEntry;
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
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return panicCounter;
}

int CtiVanGogh::checkNumericReasonability(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumericSPtr pointNumeric, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    int alarm = NORMAL;
    string text;
    CtiPointClientManager::ReasonabilityLimitStruct limits = PointMgr.getReasonabilityLimits(pointNumeric);

    try
    {

        if(limits.highLimit != limits.lowLimit &&       // They must be different.
           limits.highLimit >  limits.lowLimit )
        {
            // Evaluate High Limit
            if(limits.highLimit < MAX_HIGH_REASONABILITY)  // Is the reasonability reasonable?
            {
                alarm = CtiTablePointAlarming::highReasonability;
                double val = pData->getValue();

                if(val > limits.highLimit && pDyn)
                {
                    pData->setValue( pDyn->getValue() );          // Value of the CtiPointDataMsg must be be modified.
                    pData->setQuality( UnreasonableQuality );

                    if(!_signalManager.isAlarmed(pointNumeric->getID(), CtiTablePointAlarming::highReasonability) && !pDyn->isConditionActive(alarm))
                    {
                        {
                            char tstr[120];
                            _snprintf(tstr, sizeof(tstr), "Reasonability Limit Exceeded High. %.3f > %.3f", val, limits.highLimit);
                            text = string(tstr);
                        }

                        if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** HIGH REASONABILITY Violation ****  Point: " << pointNumeric->getName() << " " << text << endl;
                        }

                        const CtiTablePointAlarming alarming = PointMgr.getAlarming(pointNumeric);

                        pSig = CTIDBG_new CtiSignalMsg(pointNumeric->getID(), pData->getSOE(), text, getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::highReasonability) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::highReasonability), pData->getUser());
                    }
                    else if(!_signalManager.isAlarmActive(pointNumeric->getID(), alarm))
                    {
                        activatePointAlarm(alarm, aWrap, pointNumeric, pDyn, true);
                    }

                    // This is an alarm if the alarm state indicates anything other than SignalEvent.
                    if(pSig)
                    {
                        tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);
                        updateDynTagsForSignalMsg(pointNumeric,pSig,alarm,true);
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
                else
                {
                    activatePointAlarm(alarm,aWrap,pointNumeric,pDyn,false);
                }
            }

            if(limits.lowLimit > MIN_LOW_REASONABILITY && pDyn)  // Is the reasonability reasonable?
            {
                alarm = CtiTablePointAlarming::lowReasonability;
                double val = pData->getValue();

                if(val < limits.lowLimit)
                {
                    pData->setValue( pDyn->getValue() );          // Value of the CtiPointDataMsg must be be modified.
                    pData->setQuality( UnreasonableQuality );

                    if(!_signalManager.isAlarmed(pointNumeric->getID(), alarm) && !pDyn->isConditionActive(alarm))
                    {
                        {
                            char tstr[120];
                            _snprintf(tstr, sizeof(tstr), "Reasonability Limit Exceeded Low. %.3f < %.3f", val, limits.lowLimit);
                            text = string(tstr);
                        }

                        if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** LOW REASONABILITY Violation ****  Point: " << pointNumeric->getName() << " " << text << endl;
                        }

                        const CtiTablePointAlarming &alarming = PointMgr.getAlarming(pointNumeric);

                        pSig = CTIDBG_new CtiSignalMsg(pointNumeric->getID(), pData->getSOE(), text, getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::lowReasonability) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::lowReasonability), pData->getUser());
                        pSig->setPointValue(pData->getValue());
                    }
                    else if(!_signalManager.isAlarmActive(pointNumeric->getID(), alarm))
                    {
                        activatePointAlarm(alarm, aWrap, pointNumeric, pDyn, true);
                    }

                    // This is an alarm if the alarm state indicates anything other than SignalEvent.
                    if(pSig)
                    {
                        tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);
                        updateDynTagsForSignalMsg(pointNumeric,pSig,alarm,true);
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
                else
                {
                    activatePointAlarm(alarm,aWrap,pointNumeric,pDyn,false);
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return alarm;
}

void CtiVanGogh::checkNumericLimits(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumericSPtr pointNumeric, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    string text;

    double  val = pData->getValue();
    INT     numericAlarmOffset = (alarm - CtiTablePointAlarming::limit0); // This gives a base 0 (limit0 alarm = 0) for the alarms for later use with pending operations
    INT     limitnumber = getNumericLimitFromHighLow(numericAlarmOffset, alarm) +1; //Currently returns 0 or 1 for limit 0 or 1
    INT     exceeds = LIMIT_IN_RANGE;

    try
    {
        const CtiTablePointLimit limit = PointMgr.getPointLimit(pointNumeric, limitnumber);

        if(limit.getLowLimit() != -DBL_MAX && limit.getHighLimit() != DBL_MAX && limitStateCheck(alarm, limit, val, exceeds))
        {
            if(!_signalManager.isAlarmed(pointNumeric->getID(), alarm) && pDyn && !pDyn->isConditionActive(alarm))
            {
                INT duration = limit.getLimitDuration();

                if(exceeds == LIMIT_EXCEEDS_LO )
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
                    text = string(tstr);
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
                    text = string(tstr);
                }
                else if(exceeds == LIMIT_SETUP_ERROR)
                {
                    char tstr[120];
                    _snprintf(tstr, sizeof(tstr), "Limit %d Invalid Setup. Is %.3f < %.3f < %.3f?", limitnumber, limit.getLowLimit(), val, limit.getHighLimit());
                    text = string(tstr);
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** Invalid limit setup" << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }


                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** LIMIT Violation ****  Point: " << pointNumeric->getName() << " " << text << endl;
                }

                const CtiTablePointAlarming &alarming = PointMgr.getAlarming(pointNumeric);

                pSig = CTIDBG_new CtiSignalMsg(pointNumeric->getID(), pData->getSOE(), text, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData->getUser());
                pSig->setPointValue(pData->getValue());
                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);

                if(duration > 0)  // Am I required to hold in this state for a bit before the announcement of this condition?
                {
                    CtiPendingPointOperations *pendingPointLimit = CTIDBG_new CtiPendingPointOperations(pointNumeric->getID());
                    pendingPointLimit->setType(CtiPendingPointOperations::pendingLimit + numericAlarmOffset);
                    pendingPointLimit->setLimitBeingTimed( numericAlarmOffset );
                    pendingPointLimit->setTime( CtiTime() );
                    pendingPointLimit->setLimitDuration( duration );
                    pendingPointLimit->setSignal( pSig );
                    pSig = 0;   // Don't let it get put in the Wrapper because it is now in the pending list!

                    // If there is a limit duration, we modify the data message, so clients don't immediately know that this point is in a pending alarm.
                    pData->resetTags( TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM );

                    addToPendingSet(pendingPointLimit);
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** LIMIT Violation ****  Point: " << pointNumeric->getName() << " delayed (" << duration << ") violation. Limit " << limitnumber << " pending alarm." << endl;
                    }
                }
                else
                {
                    updateDynTagsForSignalMsg(pointNumeric,pSig,alarm,true);
                }
            }
            else if(!_signalManager.isAlarmActive(pointNumeric->getID(), alarm))
            {
                activatePointAlarm(alarm, aWrap, pointNumeric, pDyn, true);
            }
        }
        else
        {
            if( limit.getLowLimit() != -DBL_MAX && limit.getHighLimit() != DBL_MAX )    // No limits set, no limits can be exceeded!
            {
                // Remove any possible pending limit violator.
                CtiPendable *pPend = CTIDBG_new CtiPendable(pData->getId(), CtiPendable::CtiPendableAction_RemoveLimit);
                pPend->_limit = numericAlarmOffset;
                _pendingOpThread.push( pPend );

                activatePointAlarm(alarm,aWrap,pointNumeric,pDyn,false);
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

INT CtiVanGogh::getNumericLimitFromHighLow(INT numericAlarmOffset, INT alarm)
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

void CtiVanGogh::checkStatusUCOS(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    UINT tags = 0;
    if(pDyn)
    {
        tags = pDyn->getDispatch().getTags();
    }

    // Pseudo points cannot have feedback, therefore cannot have UCOS.
    // Control must be available to care about UCOS
    // Control must not be expected to have UCOS (unexpected is the same as NOT PENDING)
    if(pDyn && !point->isPseudoPoint() && (tags & TAG_ATTRIB_CONTROL_AVAILABLE) && !(tags & TAG_CONTROL_PENDING))
    {
        // Well, we were NOT expecting a change, so make sure the values match
        if( pDyn->getDispatch().getValue() != pData->getValue())
        {
            // Values don't match and we weren't expecting a change!
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** UNCOMMANDEDSTATECHANGE **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

            // OK, we have an actual alarm condition to gripe about!
            pSig = CTIDBG_new CtiSignalMsg(point->getID(), pData->getSOE(), string( "UCOS: " + ResolveStateName(point->getStateGroupID(), (int)pData->getValue())), getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
            pSig->setPointValue(pData->getValue());

            // This is an alarm if the alarm state indicates anything other than SignalEvent.
            tagSignalAsAlarm(point, pSig, alarm, pData);
            updateDynTagsForSignalMsg(point,pSig,alarm,true);

            pSig->resetTags( TAG_ACTIVE_ALARM );
            pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
            pDyn->getDispatch().setTags( _signalManager.getTagMask(point->getID()) );
        }
    }
}

void CtiVanGogh::checkStatusState(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    string action;
    double val = pData->getValue();
    INT alarmValue = (alarm - CtiTablePointAlarming::state0); // The value we want to alarm on

    // Value or quality must have been changed to look at this stuff again!
    // if( (pDyn->getValue() != pData->getValue() || pDyn->getQuality() != pData->getQuality()))
    if(pDyn)
    {
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
            if(!_signalManager.isAlarmed(point->getID(), alarm) && !pDyn->isConditionActive(alarm))
            {
                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** STATE Violation **** \n" <<
                    "   Point: " << point->getID() << " " << ResolveStateName(point->getStateGroupID(), (int)pData->getValue()) << endl;
                }

                string tstr(ResolveStateName(point->getStateGroupID(), (int)pData->getValue()));

                const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

                // OK, we have an actual alarm condition to gripe about!
                pSig = CTIDBG_new CtiSignalMsg(point->getID(), pData->getSOE(), tstr, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
                pSig->setPointValue(pData->getValue());
                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                tagSignalAsAlarm(point, pSig, alarm, pData);
                updateDynTagsForSignalMsg(point,pSig,alarm,true);
            }
            else if(!_signalManager.isAlarmActive(point->getID(), alarm))
            {
                activatePointAlarm(alarm,aWrap,point,pDyn,true);
            }
        }
        else
        {
            activatePointAlarm(alarm,aWrap,point,pDyn,false);
        }
    }
}

/*
 *  This category was removed as an alarmable category type.  The code is here for future reinclusion if that is ever desired.
 */
void CtiVanGogh::checkChangeOfState(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    #if 0
    string action;
    double val = pData->getValue();
    INT statelimit = (alarm - CtiTablePointAlarming::state0);

    // Value or quality must have been changed!
    if(pDyn && (pDyn->getValue() != pData->getValue() || pDyn->getQuality() != pData->getQuality()))
    {
        if(!_signalManager.isAlarmed(point->getID(), alarm) && !pDyn->isConditionActive(alarm))
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** COS Violation **** \n" <<
                "   Point: " << point->getID() << " " << ResolveStateName(point->getStateGroupID(), (int)pData->getValue()) << endl;
            }

            string tstr(ResolveStateName(point->getStateGroupID(), (int)pData->getValue()));

            const CtiTablePointAlarming &alarming = PointMgr.getAlarming(pPoint);

            // OK, we have an actual alarm condition to gripe about!
            pSig = CTIDBG_new CtiSignalMsg(point->getID(), pData->getSOE(), tstr, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
            pSig->setPointValue(pData->getValue());

            // This is an alarm if the alarm state indicates anything other than SignalEvent.
            tagSignalAsAlarm(point, pSig, alarm, pData);
            updateDynTagsForSignalMsg(point,pSig,alarm,true);
        }
        else if(!_signalManager.isAlarmActive(point->getID(), alarm))
        {
            activatePointAlarm(alarm,aWrap,point,pDyn,true);
        }
    }
    else
    {
        activatePointAlarm(alarm,aWrap,point,pDyn,false);
    }
    #endif
}

void CtiVanGogh::tagSignalAsAlarm( CtiPointSPtr point, CtiSignalMsg *&pSig, int alarm, CtiPointDataMsg *pData )
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
                pSig->setAdditionalInfo("Manual Update: " + pSig->getAdditionalInfo());
        }
    }
}

void CtiVanGogh::updateDynTagsForSignalMsg( CtiPointSPtr point, CtiSignalMsg *&pSig, int alarm_condition, bool condition_active )
{
    // If pSig is non-NULL, this "alarm" condition occurred and we need to decide if the point goes into alarm over it.
    if(pSig != NULL)
    {
        _signalManager.addSignal(*pSig);
    }

    CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
    if(pDyn)
    {
        pDyn->setConditionActive(alarm_condition, condition_active);                      // Mark this point as in this condition if the signal says the condition IS active.
        pDyn->getDispatch().resetTags(SIGNAL_MANAGER_MASK);
        pDyn->getDispatch().setTags(_signalManager.getTagMask(point->getID()));
    }
}

void CtiVanGogh::pruneCommErrorHistory()
{
    CtiDate earliestDate = CtiDate() - gCommErrorDays;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Comm Error History log is being pruned back to " << earliestDate << endl;
    }

    CtiTableCommErrorHistory::Prune(earliestDate);
}

void CtiVanGogh::activatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatchSPtr &pDyn, bool activate )
{
    CtiSignalMsg *pSigActive = _signalManager.setAlarmActive(point->getID(), alarm, activate);
    if(pDyn) pDyn->setConditionActive(alarm, activate);

    if(pSigActive != NULL)
    {
        pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
        pDyn->getDispatch().setTags( _signalManager.getTagMask(point->getID()) );

        unsigned sigtags = (pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | TAG_REPORT_MSG_TO_ALARM_CLIENTS;

        if(activate)
        {
            sigtags = TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL;
        }
        else if( !activate && ((pDyn->getDispatch().getTags() & SIGNAL_MANAGER_MASK) || !PointMgr.getAlarming(point).getNotifyOnClear()) )
        {
            sigtags |= TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL;
        }

        pSigActive->setTags( sigtags );
        pSigActive->setText( TrimAlarmTagText((string&)pSigActive->getText()) + AlarmTagsToString(pSigActive->getTags()) );
        pSigActive->setMessageTime( CtiTime() );

        aWrap.getMulti()->insert( pSigActive );
        pSigActive = 0;
    }
}

void CtiVanGogh::deactivatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *&pDyn )
{
    CtiSignalMsg *pSigActive = _signalManager.setAlarmActive(point->getID(), alarm, false);

    if(pSigActive != NULL)
    {
        pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
        pDyn->getDispatch().setTags( _signalManager.getTagMask(point->getID()) );

        unsigned sigtags = (pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | TAG_REPORT_MSG_TO_ALARM_CLIENTS;

        if( (pDyn->getDispatch().getTags() & SIGNAL_MANAGER_MASK) || !PointMgr.getAlarming(point).getNotifyOnClear() )
        {
            sigtags |= TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL;
        }

        pSigActive->setTags( sigtags );
        pSigActive->setText( TrimAlarmTagText((string&)pSigActive->getText()) + AlarmTagsToString(pSigActive->getTags()) );
        pSigActive->setMessageTime( CtiTime() );


        aWrap.getMulti()->insert( pSigActive );
        pSigActive = 0;
    }
}

void CtiVanGogh::reactivatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *&pDyn )
{
    CtiSignalMsg *pSigActive = _signalManager.setAlarmActive(point->getID(), alarm, true);

    if(pSigActive != NULL)
    {
        pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
        pDyn->getDispatch().setTags( _signalManager.getTagMask(point->getID()) );

        pSigActive->setTags( (pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | TAG_REPORT_MSG_TO_ALARM_CLIENTS | TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL );
        pSigActive->setText( TrimAlarmTagText((string&)pSigActive->getText()) + AlarmTagsToString(pSigActive->getTags()) );
        pSigActive->setMessageTime( CtiTime() );

        aWrap.getMulti()->insert( pSigActive );
        pSigActive = 0;
    }
}

void CtiVanGogh::acknowledgeCommandMsg( CtiPointSPtr &pPt, const CtiCommandMsg *Cmd, int alarmcondition )
{
    CtiSignalMsg *pSigNew = 0;

    if(alarmcondition == -1)
    {
        for(alarmcondition = 0; alarmcondition < 32; alarmcondition++)
        {
            acknowledgeAlarmCondition( pPt, Cmd, alarmcondition );
        }
    }
    else
    {
        acknowledgeAlarmCondition( pPt, Cmd, alarmcondition );
    }

}

void CtiVanGogh::acknowledgeAlarmCondition( CtiPointSPtr &pPt, const CtiCommandMsg *Cmd, int alarmcondition )
{
    CtiSignalMsg *pSigNew = 0;

    {
        CtiServerExclusion pmguard(_server_exclusion);
        pSigNew = _signalManager.setAlarmAcknowledged(pPt->getPointID(), alarmcondition, true);    // Clear the tag, return the signal!

        if(pSigNew)
        {
            CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(pPt);

            if(pDyn)
            {
                pSigNew->setUser( Cmd->getUser() );

                unsigned sigtags = (pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | TAG_REPORT_MSG_TO_ALARM_CLIENTS | TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL;

                bool almclear = (_signalManager.getTagMask(pPt->getPointID()) & MASK_ANY_ALARM) == 0; // true if no bits are set.

                const CtiTablePointAlarming &alarming = PointMgr.getAlarming(pPt);

                if( ( almclear && alarming.getNotifyOnClear() ) || alarming.getNotifyOnAcknowledge())
                {
                    sigtags &= ~TAG_REPORT_MSG_BLOCK_EXTRA_EMAIL;   // Remove this to send the emails.
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
                UINT amask = _signalManager.getTagMask(pPt->getPointID());

                if(premask != amask)
                {
                    // Adjust the point tags to reflect the potentially new state of the alarm tags.
                    pDyn->getDispatch().resetTags( SIGNAL_MANAGER_MASK );
                    pDyn->getDispatch().setTags( amask );

                    CtiPointDataMsg *pTagDat = CTIDBG_new CtiPointDataMsg(pPt->getID(), pDyn->getValue(), pDyn->getQuality(), pPt->getType(), "Tags Updated", pDyn->getDispatch().getTags());

                    pTagDat->setSource(DISPATCH_APPLICATION_NAME);
                    pTagDat->setTime(pDyn->getTimeStamp());
                    pTagDat->setMessagePriority(15);

                    MainQueue_.putQueue( pTagDat );
                }

                if(isDebugLudicrous())
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                    dout << " Signal has tags: " << pSigNew->getTags() << " " << explainTags(pSigNew->getTags())  << endl;
                    dout << " SigMgr has tags: " << amask << " " << explainTags(amask) << endl;
                    dout << " Point  has tags: " << pDyn->getDispatch().getTags()  << " " << explainTags(pDyn->getDispatch().getTags()) << endl;
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

int CtiVanGogh::processTagMessage(CtiTagMsg &tagMsg)
{
    int status = NORMAL;

    int resultAction = _tagManager.processTagMsg(tagMsg);


    bool disable = false;
    LONG id       = tagMsg.getPointID();
    int  tagmask  = 0;           // This mask represents all the bits which are to be adjusted.
    int  setmask  = 0;         // This mask represents the state of the adjusted-masked bit.. Ok, read it again.

    switch(resultAction)
    {
    case (CtiTagManager::ActionPointControlInhibit):
        {
            disable = true;
            break;
        }
    case (CtiTagManager::ActionPointInhibitRemove):
        {
            disable = false;
            break;
        }
    }

    if(resultAction != CtiTagManager::ActionNone)
    {
        try
        {
            CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

            if(pMulti)
            {
                pMulti->setSource(DISPATCH_APPLICATION_NAME);
                pMulti->setUser(tagMsg.getUser());

                tagmask = TAG_DISABLE_CONTROL_BY_POINT;
                setmask |= (disable ? TAG_DISABLE_CONTROL_BY_POINT : 0);    // Set it, or clear it?

                {
                    bool devicedifferent;

                    CtiPointSPtr pPt = PointMgr.getPoint(id);
                    ablementPoint(pPt, devicedifferent, setmask, tagmask, tagMsg.getUser(), *pMulti);

                    if(devicedifferent)     // The device became interesting because of this change.
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                }

                if(pMulti->getData().size())
                {
                    MainQueue_.putQueue(pMulti);
                    pMulti = 0;
                }
                else
                {
                    delete pMulti;
                }
            }
        }
        catch(const RWxmsg& x)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
        }
    }

    return status;
}

void CtiVanGogh::VGDBSignalWriterThread()
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("DB Signal Writer Thread");

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch DB Signal Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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

                rwSleep(1000);
                writeSignalsToDB();
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        // Make sure no one snuck in under the wire..
        writeSignalsToDB(true);
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch DB Signal Writer Thread shutting down" << endl;
    }

    return;
}

void CtiVanGogh::VGDBSignalEmailThread()
{
    UINT sanity = 0;

    ThreadStatusKeeper threadStatus("DB Signal Email Thread");

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch DB Signal Email Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    catch(RWxmsg& msg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch DB Signal Email Thread shutting down" << endl;
    }

    return;
}

/*
 *  This method is called on startup to attempt to clean up the lmcontrol history for missed controls and
 *  controls which may have completed while dispatch was not running.
 */
int CtiVanGogh::loadPendingControls()
{
    INT            status = NORMAL;
    LONG           lTemp;

    typedef map< pair<LONG, LONG>, CtiTableLMControlHistory > LMCHMap_t;    // Key is make_pair(PAOID, SOE)

    CtiServerExclusion pmguard(_server_exclusion);

    // This block will clean up any non closed control blocks.
    {
        CtiTime todaynow;
        CtiTime thepast = todaynow.addDays( -5 );

        static const string sql = CtiTableLMControlHistory().getSQLCoreStatementIncomplete();

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr << thepast;

        rdr.execute();

        if(!rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << loggedSQLstring << endl;
            }
        }

        LMCHMap_t dynCtrlMap;

        while( rdr() )
        {
            CtiTableLMControlHistory dynC;
            dynC.DecodeOutstandingControls(rdr);

            if(dynC.getLoadedActiveRestore() == LMAR_NEWCONTROL)
            {
                pair< LMCHMap_t::iterator, bool > insertpair  = dynCtrlMap.insert( LMCHMap_t::value_type(make_pair(dynC.getPAOID(), dynC.getSoeTag()), dynC) );

                if(insertpair.second == false)      // This is a collision!
                {
                    LMCHMap_t::iterator &itr = insertpair.first;
                    CtiTableLMControlHistory &dynControl = (*itr).second;

                    CtiPointSPtr pPt = PointMgr.getControlOffsetEqual(dynControl.getPAOID(),  1);

                    if(pPt)
                    {
                        CtiTime incTime;
                        CtiPendingPointOperations *ppc = CTIDBG_new CtiPendingPointOperations(pPt->getPointID());
                        ppc->setType(CtiPendingPointOperations::pendingControl);
                        ppc->setControlState( CtiPendingPointOperations::controlInProgress );
                        ppc->setTime( dynControl.getStartTime() );
                        ppc->setControl(dynControl);

                        ppc->getControl().setPreviousLogTime(dynControl.getStartTime());
                        ppc->getControl().setPreviousStopReportTime(dynControl.getStartTime());

                        LONG addnlseconds = 0;

                        if( dynControl.getStopTime() > incTime )   // Control is not yet complete.
                        {
                            ppc->getControl().setActiveRestore(LMAR_LOGTIMER);
                            ppc->getControl().setDefaultActiveRestore(LMAR_TIMED_RESTORE);
                            addnlseconds = incTime.seconds() - dynControl.getStartTime().seconds();
                        }
                        else    // Control is complete.
                        {
                            ppc->setControlState( CtiPendingPointOperations::controlCompleteTimedIn );
                            ppc->getControl().setDefaultActiveRestore(LMAR_TIMED_RESTORE);
                            ppc->getControl().setActiveRestore(LMAR_TIMED_RESTORE);
                            incTime = dynControl.getStopTime();
                            addnlseconds = dynControl.getStopTime().seconds() - dynControl.getStartTime().seconds();
                        }

                        if(addnlseconds >= 0)
                        {
                            ppc->getControl().incrementTimes( incTime, addnlseconds );

                            ppc->getControl().setActiveRestore(ppc->getControl().getDefaultActiveRestore());
                        }

                        updateGroupPseduoControlPoint(pPt, dynControl.getStopTime());
                        addToPendingSet( ppc );
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    (*itr).second = dynC; // store the new 'N'ew control!
                }
            }
            else
            {
                // We can remove the matching 'N' from the list!
                dynCtrlMap.erase( make_pair(dynC.getPAOID(), dynC.getSoeTag()));

            }
        }
    }

    {
        static const string sql = CtiTableLMControlHistory().getSQLCoreStatementOutstanding();

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        if(!rdr.isValid())
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << loggedSQLstring << endl;
            }
        }


        while( rdr() )
        {
            CtiTableLMControlHistory dynControl;
            dynControl.DecodeOutstandingControls(rdr);

            CtiPointSPtr pPt = PointMgr.getControlOffsetEqual(dynControl.getPAOID(),  1);

            if(pPt)
            {
                CtiPendingPointOperations *ppc = CTIDBG_new CtiPendingPointOperations(pPt->getPointID());
                ppc->setType(CtiPendingPointOperations::pendingControl);
                ppc->setControlState( CtiPendingPointOperations::controlInProgress );
                ppc->setTime( dynControl.getStartTime() );
                ppc->setControl(dynControl);

                // We prime the pending control object here, where we know all there is to know.
                // ppc.dump();

                updateGroupPseduoControlPoint(pPt, dynControl.getStopTime());
                addToPendingSet( ppc );
            }
            else
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        CtiTableLMControlHistory::deleteOutstandingControls( );
    }

    return status;
}

void CtiVanGogh::updateGroupPseduoControlPoint(CtiPointSPtr &pPt, const CtiTime &delaytime)
{
    if( pPt->isPseudoPoint() )  // The delayed update is needed in this case!
    {
        if(gDispatchDebugLevel & DISPATCH_DEBUG_DELAYED_UPDATE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << resolveDeviceName(pPt) << " / " << pPt->getName() << " has been scheduled/reloaded for delayed update at " << delaytime << endl;
        }

        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg( pPt->getPointID(), (DOUBLE)UNCONTROLLED, NormalQuality, StatusPointType, string(resolveDeviceNameByPaoId(pPt->getDeviceID()) + " restoring (delayed)"), TAG_POINT_DELAYED_UPDATE | TAG_POINT_FORCE_UPDATE);
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
            /* Print some instructions */
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);

                dout << endl;
                dout << "Dispatch " << endl << endl;
                dout << endl;

                break;
            }
        case 0x6d:              // alt-m trace filter.
            {
                _CrtDumpMemoryLeaks();
                break;
            }
        case 0x71:              // alt-q
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Vangogh mainqueue has " << MainQueue_.entries() << " entries" << endl;
                }
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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << Mgr->outQueueCount() << " outqueue entries on connection to " << Mgr->getName() << " / " << Mgr->who() << endl;
                        }
                    }
                }
                break;
            }
        case 0x72:              // alt-r
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << CtiTime() << " Vangogh mainqueue has " << MainQueue_.entries() << " entries" << endl;
                    dout << "  CtiLMControlHistoryMsg   icnt = " << CtiLMControlHistoryMsg::getInstanceCount() << endl;
                    dout << "  CtiPointdataMsg          icnt = " << CtiPointDataMsg::getInstanceCount() << endl;
                    dout << "  CtiSignalMsg             icnt = " << CtiSignalMsg::getInstanceCount() << endl;
                }

                break;
            }
        case 0x64:              // alt-d
            {

                /*
                    #define MSG_TRACE                         ((MSG_BASE) + 20)
                    #define MSG_COMMAND                       ((MSG_BASE) + 30)
                    #define MSG_REGISTER                      ((MSG_BASE) + 40)
                    #define MSG_SERVER_REQUEST                ((MSG_BASE) + 50)
                    #define MSG_SERVER_RESPONSE               ((MSG_BASE) + 51)
                    #define MSG_POINTREGISTRATION             ((MSG_BASE) + 70)
                    #define MSG_DBCHANGE                      ((MSG_BASE) + 80)
                    #define MSG_PCREQUEST                     ((MSG_BASE) + 85)
                    #define MSG_PCRETURN                      ((MSG_BASE) + 90)

                    #define MSG_MULTI                         ((MSG_BASE) + 91)
                    #define MSG_TAG                           ((MSG_BASE) + 94)
                    #define MSG_POINTDATA                     ((MSG_BASE) + 95)
                    #define MSG_SIGNAL                        ((MSG_BASE) + 96)
                    #define MSG_EMAIL                         ((MSG_BASE) + 97)
                    #define MSG_LMCONTROLHISTORY              ((MSG_BASE) + 98)
                    #define MSG_COMMERRORHISTORY              ((MSG_BASE) + 99)
                */
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << CtiTime() << " Vangogh mainqueue has " << MainQueue_.entries() << " entries" << endl;
                    dout << " MSG_TRACE                      " << msgCounts.get(MSG_TRACE) << endl;
                    dout << " MSG_COMMAND                    " << msgCounts.get(MSG_COMMAND) << endl;
                    dout << " MSG_REGISTER                   " << msgCounts.get(MSG_REGISTER) << endl;
                    dout << " MSG_SERVER_REQUEST             " << msgCounts.get(MSG_SERVER_REQUEST) << endl;
                    dout << " MSG_SERVER_RESPONSE            " << msgCounts.get(MSG_SERVER_RESPONSE) << endl;
                    dout << " MSG_POINTREGISTRATION          " << msgCounts.get(MSG_POINTREGISTRATION) << endl;
                    dout << " MSG_DBCHANGE                   " << msgCounts.get(MSG_DBCHANGE) << endl;
                    dout << " MSG_PCREQUEST                  " << msgCounts.get(MSG_PCREQUEST) << endl;
                    dout << " MSG_PCRETURN                   " << msgCounts.get(MSG_PCRETURN) << endl;

                    dout << " MSG_MULTI                      " << msgCounts.get(MSG_MULTI) << endl;
                    dout << " MSG_TAG                        " << msgCounts.get(MSG_TAG) << endl;
                    dout << " MSG_POINTDATA                  " << msgCounts.get(MSG_POINTDATA) << endl;
                    dout << " MSG_SIGNAL                     " << msgCounts.get(MSG_SIGNAL) << endl;
                    dout << " MSG_LMCONTROLHISTORY           " << msgCounts.get(MSG_LMCONTROLHISTORY) << endl;
                    dout << " MSG_COMMERRORHISTORY           " << msgCounts.get(MSG_COMMERRORHISTORY) << endl;

                    for(int i = 1; i <= 15; i++)
                    {
                        dout << " Priority               " << i << " has " << msgPrioritys.get(i) << endl;
                    }
                    for(int i = 1; i <= 15; i++)
                    {
                        if(i <= 10)
                            dout << " Message times          " << i*50 << " ms = " << msgTimes.get(i) << endl;
                        else
                            dout << " Message times          " << 1000 + ((i-11)*1000) << " ms = " << msgTimes.get(i) << endl;
                    }
                }
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

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        pMsg->dump();
                    }

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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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


bool CtiVanGogh::isPointInPendingControl(LONG pointid)
{
    return _pendingOpThread.isPointInPendingControl(pointid);
}


void CtiVanGogh::checkStatusCommandFail(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig )
{
    if(pDyn)
    {
        UINT tags = pDyn->getDispatch().getTags();

        // We can only care about failure if we are a status/control point
        // and someone has sent out a command.  Otherwise this is irrelevant.
        if(tags & TAG_ATTRIB_CONTROL_AVAILABLE /*|| TriggerMgr.isAVerificationPoint(point->getPointID())*/)
        {
            CtiPendable *pendable = CTIDBG_new CtiPendable(pData->getId(), CtiPendable::CtiPendableAction_ControlStatusComplete, NULL, pData->getTime() );

            /*if( TriggerMgr.isAVerificationPoint(point->getPointID()) )
            {
                PtVerifyTriggerSPtr verificationPtr;
                if( verificationPtr = TriggerMgr.getPointTriggerFromVerificationID(point->getID()) )
                {
                    pendable->_pointID = verificationPtr->dbTriggerData.getPointID();

                    //This may hit the database.
                    CtiPointSPtr pCtrlPt = PointMgr.getPoint(verificationPtr->dbTriggerData.getPointID());

                    if( pCtrlPt )      // We do know this point..
                    {
                        const CtiDynamicPointDispatch *pCtrlDyn = PointMgr.getDynamic(pCtrlPt);
                        tags = pCtrlDyn->getDispatch().getTags();
                    }
                }
            }*/
            pendable->_value = pData->getValue();
            pendable->_tags = tags;
            _pendingOpThread.push( pendable );

            if(tags & TAG_CONTROL_PENDING)                                          // Are we still awaiting the start of control?
            {
                pDyn->getDispatch().resetTags( TAG_CONTROL_PENDING );               // We got to the desired state, no longer pending.. we are now controlling!

                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
                {
                    if(pDyn->getValue() == pData->getValue())                       // Not changing, must be a control refresh
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << resolveDeviceName(point) << " / " << point->getName() << " CONTROL CONTINUATION/COMPLETE." << endl;
                    }
                    else                                                            // Changing this time through.  Control begins now!
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << resolveDeviceName(point) << " / " << point->getName() << " has gone CONTROL COMPLETE." << endl;
                    }
                }
            }
        }
    }
}

bool CtiVanGogh::addToPendingSet(CtiPendingPointOperations *&pendingOp, CtiTime &updatetime)
{
    _pendingOpThread.push(CTIDBG_new CtiPendable(pendingOp->getPointID(), CtiPendable::CtiPendableAction_Add, pendingOp, updatetime));
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

    #if 0
    /*
     *  Last thing we do is add this signal to the pending signal list iff it is an alarm
     *  AND it is not an cleared alarm....  This second condition prevents ack/clear reports
     *  from being kept on the pending list.
     */

    if( pSig->getSignalCategory() > SignalEvent && !(pSig->getTags() & TAG_REPORT_MSG_TO_ALARM_CLIENTS) )
    {
        _signalManager.addSignal(*pSig);
    }
    #endif

    _signalMsgQueue.putQueue(pSig); // Queue it for a write to the DB!
    pSig = 0;

    return;
}


void CtiVanGogh::stopDispatch()
{
    RWWaitStatus rwwait;

    bGCtrlC = TRUE;     // set this flag so vangogh main thread signals the rest to shutdown.  YUK-8884

    try
    {
        // This forces the listener thread to exit on shutdown.
        _listenerSocket.close();
    }
    catch(...)
    {
        // Dont really care, we are shutting down.
    }
    shutdown();                   // Shutdown the server object.

    // Interrupt the CtiThread based threads.
    _pendingOpThread.interrupt(CtiThread::SHUTDOWN);
    ThreadMonitor.interrupt(CtiThread::SHUTDOWN);

    if(RW_THR_TIMEOUT == ConnThread_.join(30000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating connection thread " << __FILE__ << " at:" << __LINE__ << endl;
        ConnThread_.terminate();
    }
    _pendingOpThread.join();
    if(RW_THR_TIMEOUT == _archiveThread.join(30000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating archive thread " << __FILE__ << " at:" << __LINE__ << endl;
        _archiveThread.terminate();
    }
    if(RW_THR_TIMEOUT == _dbThread.join(30000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating database thread " << __FILE__ << " at:" << __LINE__ << endl;
        _dbThread.terminate();
    }
    if(RW_THR_TIMEOUT == _dbSigThread.join(30000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating dbsig thread " << __FILE__ << " at:" << __LINE__ << endl;
        _dbSigThread.terminate();
    }
    if(RW_THR_TIMEOUT == _dbSigEmailThread.join(30000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating email thread " << __FILE__ << " at:" << __LINE__ << endl;
        _dbSigEmailThread.terminate();
    }
    if(RW_THR_TIMEOUT == _appMonitorThread.join(30000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating app thread " << __FILE__ << " at:" << __LINE__ << endl;
        _appMonitorThread.terminate();
    }
    if(RW_THR_TIMEOUT == _timedOpThread.join(gConfigParms.getValueAsInt("SHUTDOWN_TERMINATE_TIME", 300)*1000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating timed op thread " << __FILE__ << " at:" << __LINE__ << endl;
        _timedOpThread.terminate();
    }
    if(RW_THR_TIMEOUT == _rphThread.join(gConfigParms.getValueAsInt("SHUTDOWN_TERMINATE_TIME", 300)*1000))
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Terminating RPH thread " << __FILE__ << " at:" << __LINE__ << endl;
        _rphThread.terminate();
    }
    ThreadMonitor.join();

    PointMgr.DeleteList();
}

CtiConnection* CtiVanGogh::getNotificationConnection()
{
    try
    {
        if( _notificationConnection == NULL || (_notificationConnection != NULL && _notificationConnection->verifyConnection()) )
        {
            if( _notificationConnection != NULL && _notificationConnection->verifyConnection() )
            {
                delete _notificationConnection;
                _notificationConnection = NULL;
            }

            if( _notificationConnection == NULL )
            {
                _notificationConnection  = new CtiConnection( NotificationPort, NotificationMachine );
                _notificationConnection->setName("Dispatch to Notification");
            }
        }

        return _notificationConnection;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Control history points reset in progress" << endl;
        }
        try
        {
            CtiServerExclusion pmguard(_server_exclusion);
            CtiDeviceLiteSet_t::iterator iter = _deviceLiteSet.begin();
            CtiDeviceLiteSet_t::iterator end  = _deviceLiteSet.end();
            long deviceID;
            CtiPointSPtr point;

            for( ; iter != end; iter++ )
            {
                if(isDeviceGroupType(&(*iter)))
                {
                    deviceID = iter->getID();

                    point = PointMgr.getOffsetTypeEqual(deviceID, ANNUALCONTROLHISTOFFSET, AnalogPointType);
                    if(point)
                    {
                        const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
                        if(pDyn)
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

                    point = PointMgr.getOffsetTypeEqual(deviceID, MONTHLYCONTROLHISTOFFSET, AnalogPointType);
                    if(point)
                    {
                        const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
                        if(pDyn)
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

                    point = PointMgr.getOffsetTypeEqual(deviceID, DAILYCONTROLHISTOFFSET, AnalogPointType);
                    if(point)
                    {
                        const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
                        if(pDyn)
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        prevdate = today;
    }

    return pMulti;
}

void CtiVanGogh::sendbGCtrlC( const std::string & who )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << who << " has asked for shutdown."<< endl;
    }
    bGCtrlC = TRUE;
}

/*void CtiVanGogh::sendPointTriggers( const CtiPointDataMsg &aPD , CtiPointSPtr point )
{
    bool isValid = true;
    if( point )
    {
        CtiLockGuard<CtiMutex> guard(TriggerMgr.getMux());
        CtiPointTriggerManager::coll_type *pointMap = TriggerMgr.getPointIteratorFromTrigger(point->getPointID());
        const CtiDynamicPointDispatch *pDyn = PointMgr.getDynamic(point);

        double pointValue = aPD.getValue();

        if( !(pDyn->getDispatch().getTags() & (TAG_DISABLE_CONTROL_BY_POINT | TAG_DISABLE_CONTROL_BY_DEVICE)) )
        {
            if( pointMap )
            {
                CtiPointTriggerManager::spiterator iter;
                for( iter = pointMap->begin(); iter != pointMap->end(); iter++ )
                {
                    if( iter->second->dbTriggerData.getTriggerDeadband() <= fabs(pointValue - iter->second->lastTriggerValue) )
                    {
                        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
                        {
                            string devicename = resolveDeviceName(point);
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Trigger recieved on trigger point: " << point->getPointID() << " for " << iter->second->dbTriggerData.getPointID() << endl;
                        }
                        CtiRequestMsg *pReq = 0;
                        string cmdstr;

                        //Should we worry about a point that references itself (could be infinite loop if set to deadband == 0)
                        //This call probably hits the database
                        CtiPointSPtr controlPoint = PointMgr.getPoint(iter->second->dbTriggerData.getPointID());
                        if( controlPoint )
                        {
                            switch( controlPoint->getType() )
                            {
                                case AnalogOutputPointType:
                                {
                                    cmdstr = "putvalue analog " + CtiNumStr(controlPoint->getPointOffset()) + " " + CtiNumStr(pointValue);
                                    isValid = true;
                                    break;
                                }
                                case StatusPointType:
                                {
                                    cmdstr = "putstatus offset " + CtiNumStr(controlPoint->getPointOffset()) + " value " + CtiNumStr(pointValue);
                                    isValid = true;
                                    break;
                                }
                                default:
                                {
                                    isValid = false;
                                    string devicename = resolveDeviceName(point);
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Attempted trigger on  " << devicename << " / " << point->getName() << " failed, incorrect point type" << endl;
                                }
                            }

                            if( isValid )
                            {
                                if(pReq = CTIDBG_new CtiRequestMsg( controlPoint->getDeviceID(), cmdstr ))
                                {
                                    pReq->setUser( aPD.getUser() );
                                    pReq->setSource(DISPATCH_APPLICATION_NAME);
                                    pReq->setMessagePriority( MAXPRIORITY - 1 );    // Make it sing!
                                    writeMessageToClient(pReq, string(PIL_REGISTRATION_NAME));
                                }
                                sendPendingControlRequest(aPD, controlPoint, iter->second);
                                iter->second->lastTriggerValue = pointValue;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
            {
                string devicename = resolveDeviceName(point);
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Control disabled on " << devicename << " / " << point->getName() << endl;
            }
        }
    }
}*/

void CtiVanGogh::sendPendingControlRequest(const CtiPointDataMsg &aPD, CtiPointSPtr point, PtVerifyTriggerSPtr verificationPtr)
{
    CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
    CtiPointDataMsg *pPseudoValPD = 0;
    if(pDyn)
    {

        CtiPendingPointOperations *pendingControlRequest = CTIDBG_new CtiPendingPointOperations(point->getID());
        pendingControlRequest->setType(CtiPendingPointOperations::pendingControl);
        pendingControlRequest->setControlState( CtiPendingPointOperations::controlSentToPorter );
        pendingControlRequest->setTime(CtiTime::now());
        pendingControlRequest->setControlCompleteValue(aPD.getValue());
        pendingControlRequest->setControlTimeout(verificationPtr->dbTriggerData.getCommandTimeOut());
        pendingControlRequest->setControlCompleteDeadband(verificationPtr->dbTriggerData.getVerificationDeadband());
        pendingControlRequest->setExcludeFromHistory(!isDeviceGroupType(point->getDeviceID()));

        pendingControlRequest->getControl().setPAOID( point->getDeviceID() );
        pendingControlRequest->getControl().setStartTime(CtiTime(YUKONEOT));
        pendingControlRequest->getControl().setControlDuration(0);

        string devicename = resolveDeviceName(point);

        if( verificationPtr->dbTriggerData.getVerificationID() != 0 )
        {
            const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);

            CtiSignalMsg *pFailSig = CTIDBG_new CtiSignalMsg(point->getID(), aPD.getSOE(), devicename + " / " + point->getName() + ": Triggered Control Failed", getAlarmStateName( alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, alarming.getAlarmCategory(CtiTablePointAlarming::commandFailure), aPD.getUser());

            pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK));
            if(pFailSig->getSignalCategory() > SignalEvent)
            {
                pFailSig->setTags((pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK) | (alarming.isAutoAcked(CtiTablePointAlarming::commandFailure) ? 0 : TAG_UNACKNOWLEDGED_ALARM));
                pFailSig->setLogType(AlarmCategoryLogType);
            }
            pFailSig->setCondition(CtiTablePointAlarming::commandFailure);

            pendingControlRequest->setSignal( pFailSig );
        }

        addToPendingSet(pendingControlRequest, CtiTime::now());

        if(gDispatchDebugLevel & DISPATCH_DEBUG_CONTROLS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << devicename << " / " << point->getName() << " has gone CONTROL SUBMITTED. Control expires at " << CtiTime( aPD.getMessageTime() + verificationPtr->dbTriggerData.getCommandTimeOut()) << endl;
        }

        CtiSignalMsg *pCRP = CTIDBG_new CtiSignalMsg(point->getID(), aPD.getSOE(), "Triggered Control Sent", string(), GeneralLogType, SignalEvent, aPD.getUser());
        pDyn->getDispatch().setTags( TAG_CONTROL_PENDING );
        pCRP->setTags(pDyn->getDispatch().getTags() & ~SIGNAL_MANAGER_MASK);
        MainQueue_.putQueue( pCRP );
        pCRP = 0;
        if(pPseudoValPD) MainQueue_.putQueue( pPseudoValPD );
        pPseudoValPD = 0;
    }
}

void CtiVanGogh::loadStalePointMaps(int pointID)
{
    if( pointID == 0 )
    {
        if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Loading stale point maps - all points " << endl;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Loading stale point maps for point id " << pointID << endl;
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
                                    CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
                                    if(pDyn)
                                    {
                                        int alarm = point->isNumeric() ? CtiTablePointAlarming::staleNumeric : CtiTablePointAlarming::staleStatus;
                                        _expirationSet.erase(checkTimeIter);
                                        StalePointTimeData newTimeData;
                                        newTimeData.time = updatedIter->second + (alarmTime*60);
                                        newTimeData.pointID = point->getPointID();
                                        _expirationSet.insert(newTimeData); //Be sure this cant recurse..

                                        if( _signalManager.isAlarmActive(point->getPointID(), alarm) || pDyn->isConditionActive(alarm) )
                                        {
                                            if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << CtiTime() << " Point is no longer stale " << point->getPointID() << endl;
                                            }
                                            activatePointAlarm(alarm, aWrap, point, pDyn, false);
                                        }
                                    }
                                }
                                else
                                {
                                    //Alarm!!
                                    const CtiDynamicPointDispatchSPtr pDyn = PointMgr.getDynamic(point);
                                    int alarm = point->isNumeric() ? CtiTablePointAlarming::staleNumeric : CtiTablePointAlarming::staleStatus;
                                    _expirationSet.erase(checkTimeIter);
                                    StalePointTimeData newTimeData;
                                    newTimeData.time = CtiTime::now() + (alarmTime*60);
                                    newTimeData.pointID = point->getPointID();
                                    _expirationSet.insert(newTimeData); //Be sure this cant recurse..

                                    if(pDyn && !_signalManager.isAlarmed(point->getPointID(), alarm) && !pDyn->isConditionActive(alarm))
                                    {
                                        string text = "Point is stale, it has not been updated since: ";
                                        text += updatedIter->second.asString();
                                        const CtiTablePointAlarming &alarming = PointMgr.getAlarming(point);
                                        CtiSignalMsg *pSig = pSig = CTIDBG_new CtiSignalMsg(point->getPointID(), 0, text, getAlarmStateName( alarming.getAlarmCategory(alarm) ), GeneralLogType, alarming.getAlarmCategory(alarm));
                                        pSig->setPointValue(pDyn->getDispatch().getValue());
                                        tagSignalAsAlarm(point, pSig, alarm, 0);
                                        updateDynTagsForSignalMsg(point,pSig,alarm,true);
                                        aWrap.getMulti()->insert( pSig );

                                        if( gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS )
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Point is stale " << point->getPointID() << endl;
                                            dout << CtiTime() << " Time limit is " << alarmTime << " minutes " << endl;
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
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << "Checking For Stale Points, took: " << stop.seconds() - start.seconds() << " seconds. Did Check = " << (didCheck ? "TRUE" : "FALSE") << endl;
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
            for(int i = 0; i< pRegMsg->getCount(); i++)
            {
                if(!PointMgr.isPointLoaded((*pRegMsg)[i]))
                {
                    retVal = true;
                    break;
                }
            }
        }
        else if(MsgPtr->isA() == MSG_COMMAND)
        {
            CtiCommandMsg *pCmdMsg = (CtiCommandMsg*)MsgPtr;
            if(pCmdMsg->getOperation() == CtiCommandMsg::PointDataRequest)
            {
                for(int i = 0; i < pCmdMsg->getOpArgList().size(); i++ )
                {
                    if(!PointMgr.isPointLoaded(pCmdMsg->getOpArgList()[i]))
                    {
                        retVal = true;
                        break;
                    }
                }
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
                CtiCommandMsg::CtiOpArgList_t &Op = pCmdMsg->getOpArgList();

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
                std::vector<unsigned long> pseudoPoints;
                GetPseudoPointIDs(pseudoPoints);

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

        /*if(gDispatchDebugLevel & DISPATCH_DEBUG_PERFORMANCE)
        {
            GetLocalTime(&endTime);
            int ms = (endTime.wMinute - startTime.wMinute) * 60000 +
                     (endTime.wSecond - startTime.wSecond) * 1000  +
                     (endTime.wMilliseconds - startTime.wMilliseconds);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** PERFORMANCE CHECK **** " << endl;
            dout << "CheckMessageForPreLoad took " << ms << "ms" << endl;
        }*/

    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** CHECKPOINT **** INVALID POINTER " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
            for(int i = 0; i< pRegMsg->getCount(); i++)
            {
                if(!PointMgr.isPointLoaded((*pRegMsg)[i]))
                {
                    ptIdList.insert((*pRegMsg)[i]);
                }
            }
        }
        else if(MsgPtr->isA() == MSG_COMMAND)
        {
            CtiCommandMsg *pCmdMsg = (CtiCommandMsg*)MsgPtr;
            if(pCmdMsg->getOperation() == CtiCommandMsg::PointDataRequest)
            {
                for(int i = 0; i < pCmdMsg->getOpArgList().size(); i++ )
                {
                    if(!PointMgr.isPointLoaded(pCmdMsg->getOpArgList()[i]))
                    {
                        ptIdList.insert(pCmdMsg->getOpArgList()[i]);
                    }
                }
            }
            else if(pCmdMsg->getOperation() == CtiCommandMsg::AcknowledgeAlarm)
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
                CtiCommandMsg::CtiOpArgList_t &Op = pCmdMsg->getOpArgList();

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
                std::vector<unsigned long> pseudoPoints;
                GetPseudoPointIDs(pseudoPoints);

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

        /*if(gDispatchDebugLevel & DISPATCH_DEBUG_PERFORMANCE)
        {
            GetLocalTime(&endTime);
            int ms = (endTime.wMinute - startTime.wMinute) * 60000 +
                     (endTime.wSecond - startTime.wSecond) * 1000  +
                     (endTime.wMilliseconds - startTime.wMilliseconds);

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** PERFORMANCE CHECK **** " << endl;
            dout << "FindPreLoadPointId took " << ms << "ms" << endl;
        }*/
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** CHECKPOINT **** INVALID POINTER " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}
