

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   ctivangogh
*
* Date:   6/26/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/ctivangogh.cpp-arc  $
* REVISION     :  $Revision: 1.52 $
* DATE         :  $Date: 2003/08/25 16:21:30 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iomanip>
#include <iostream>
#include <exception>
#include <utility>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\cstring.h>
#include <rw\thr\thrfunc.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw/toolpro/inetaddr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>


#include "collectable.h"
#include "monitor.h"
#include "cparms.h"
#include "guard.h"

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

#include "ctivangogh.h"

#include "pt_base.h"
#include "pt_accum.h"
#include "pt_analog.h"
#include "pt_status.h"

#include "dev_base.h"

#include "tbl_dyn_ptalarming.h"
#include "tbl_signal.h"
#include "tbl_lm_controlhist.h"
#include "tbl_commerrhist.h"
#include "tbl_ptdispatch.h"
#include "tbl_pt_alarm.h"

#include "mgr_ptclients.h"
#include "dlldefs.h"
#include "slctpnt.h"

#include "logger.h"
#include "numstr.h"

// #include "slctdev.h"
#include "connection.h"
#include "yukon.h"
#include "device.h"
#include "dbaccess.h"
#include "utility.h"
#include "logger.h"
#include "dllvg.h"

#include "dllyukon.h"

#define DEBUGPOINTCHANGES

#define LMCTLHIST_WINDOW         30             // How often partial LM control intervals are written out to DB.
#define MAX_ARCHIVER_ENTRIES     10             // If this many entries appear, we'll do a dump
#define DUMP_RATE                30             // Otherwise, do a dump evey this many seconds
#define CONFRONT_RATE            300            // Ask every client to post once per 5 minutes or be terminated
#define UPDATERTDB_RATE          3600           // Save all dirty point records once per n seconds
#define SANITY_RATE              300

DLLIMPORT extern CtiLogger   dout;              // From proclog.dll

DLLEXPORT BOOL  bGCtrlC = FALSE;

/* Global Variables */
CtiPointClientManager      PointMgr;  // The RTDB for memory points....
CtiVanGoghExecutorFactory  ExecFactory;

static const RWTime MAXTime(YUKONEOT);
static int CntlHistInterval = 3600;
static int CntlHistPointPostInterval = 60;
static int CntlStopInterval = 60;

static RWCString AlarmTagsToString(UINT tags)
{
    RWCString str;

    if(tags & TAG_ACTIVE_ALARM)
    {
        str = "ABNORMAL / ";
    }
    else
    {
        str = "NORMAL / ";
    }

    if(tags & TAG_UNACKNOWLEDGED_ALARM)
    {
        str += "UNACK: ";
    }
    else
    {
        str += "ACK: ";
    }

    return str;
}

void ApplyBlankDeletedConnection(CtiMessage*&Msg, void *Conn);

/*
 *  This function can be used to pull out connectinos which have become bachy over time.
 */
bool NonViableConnection(const CtiConnectionManager *CM, void* d)
{
    return !CM->isViable();
}

CtiVanGogh::~CtiVanGogh()
{
    PointMgr.storeDirtyRecords();
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
    BOOL bQuit = FALSE;
    int  Tag = 100;
    int  nCount = 0;
    int  nRet;
    UINT iteration = 0;
    UINT maxiterations = Options_.ReturnIntOpt('i') * 60;
    UINT sanity = 0;

    /*
     *  Iterators, place pointers etc.
     */
    CtiConnection::InQ_t          *APQueue;
    CtiExecutor                   *pExec;
    CtiMessage                    *MsgPtr;

    if(maxiterations)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch Main Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
        dout << " MAX ITERATIONS = " << maxiterations << endl;
    }

    try
    {
        establishListener();

        /*
         *  MAIN: The main Dispatch loop lives here for all time!
         */

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Initial Database Load" << endl;
        }
        loadRTDB(true);
        loadPendingSignals();       // Reload any signals written out at last shutdown.

        _rphThread = rwMakeThreadFunction(*this, &CtiVanGogh::VGRPHWriterThread);
        _rphThread.start();

        _archiveThread = rwMakeThreadFunction(*this, &CtiVanGogh::VGArchiverThread);
        _archiveThread.start();

        _timedOpThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGTimedOperationThread);
        _timedOpThread.start();

        _dbThread  = rwMakeThreadFunction(*this, &CtiVanGogh::VGDBWriterThread);
        _dbThread.start();

        // all that is good and ready has been started, open up for business from clients
        ConnThread_ = rwMakeThreadFunction(*this, &CtiVanGogh::VGConnectionHandlerThread);
        ConnThread_.start();

        SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

        for(;!bQuit;)
        {
            {
                do
                {
                    MsgPtr = MainQueue_.getQueue( 1000 );

                    if(MsgPtr != NULL)
                    {
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
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  Message reported itserf as invalid " << endl;
                                }

                                delete MsgPtr;
                            }

                            if( nRet )
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                                CtiLockGuard<CtiMutex> pmguard(server_mux, 10000);

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Restarting ConnThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                establishListener();

                                // all that is good and ready has been started, open up for business from clients
                                ConnThread_ = rwMakeThreadFunction(*this, &CtiVanGogh::VGConnectionHandlerThread);
                                ConnThread_.start();
                            }
                        }


                        if(!(++sanity % SANITY_RATE))
                        {
                            reportOnThreads();

                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " VG Main Thread Active " << endl;
                            }
                        }
                    }
                } while(MsgPtr != NULL);
            }

            iteration++;

            if(maxiterations && !(iteration%15) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Iteration " << iteration << endl;
                }
            }

            if(bGCtrlC || (maxiterations && ( iteration > maxiterations )))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << endl << "Shutting down on CTRL-C" << endl << endl;
                }
                bQuit = TRUE;
                bGCtrlC = TRUE;
            }
        }

        shutdown();                   // Shutdown the server object.

        PointMgr.DeleteList();

        ConnThread_.join();            // Wait for the Conn thread to die.

        _rphThread.join();
        _archiveThread.join();
        _timedOpThread.join();
        _dbThread.join();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " *********** Exiting Dispatch MAIN ***********  " << endl;
        }
    }
    catch(RWxmsg& msg )
    {
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "**** MAIN JUST DIED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        shutdown();
        bQuit = TRUE;

        PointMgr.DeleteList();

        ConnThread_.join();            // Wait for the Conn thread to die.

        _rphThread.join();
        _archiveThread.join();
        _timedOpThread.join();
        _dbThread.join();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " *********** Exiting Dispatch MAIN ***********  " << endl;
        }
    }
}

void CtiVanGogh::VGConnectionHandlerThread()
{
    int               i=0;
    BOOL              bQuit = FALSE;

    UINT sanity = 0;

    CtiCommandMsg     *CmdMsg = NULL;

    CtiExchange       *XChg;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch Connection Handler Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }


    /* Up this threads priority a notch over the other procs */
    CTISetPriority (PRTYS_THREAD, PRTYC_NOCHANGE, -10, 0);

    /*
     *  Wait for Main to get listener up and ready to go.
     */
    while(!_listenerAvailable && !bGCtrlC)
    {
        rwSleep(250);
    }

    for(;!bQuit && !bGCtrlC;)
    {
        try
        {
            /*
             *  This next bit blocks on a connect and creates a CTIDBG_new
             */

            /*
             *  First off, let me apologize for the awful line which follows.
             *  The problem is that RWSocketListener is a reference counted entity and any
             *  varaible used would hold a reference, so I put it in the initializer for
             *  my xchange to keep that extra reference from happening.
             */

            RWSocketPortal sock;

            if(Listener)
            {
                sock = (*Listener)();

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch Connection Handler Thread. New connect. " << endl;
                }
            }
            else
            {
                bQuit = TRUE;
                continue; // the for loop
            }

            {
                CtiLockGuard<CtiMutex> guard(server_mux);

                XChg                                = CTIDBG_new CtiExchange(sock);
                CtiVanGoghConnectionManager *ConMan = CTIDBG_new CtiVanGoghConnectionManager(XChg, &MainQueue_);

                clientConnect( ConMan );
                ConMan->ThreadInitiate();     // Kick off the connection's communication threads.

                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " New connection established" << endl;
                }
            }

            reportOnThreads();
        }
        catch(RWSockErr& msg )
        {
            if(msg.errorNumber() == RWNETENOTSOCK)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Socket error RWNETENOTSOCK" << endl;
                bQuit = TRUE;     // get out of the for loop
            }
            else
            {
                bQuit = TRUE;
                // dout << RWTime() << " VGConnectionHandlerThread: The KNOWN socket has been closed" << endl;
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
        dout << RWTime() << " Dispatch Connection Handler Thread shutting down " << endl;
    }

    return;
}

int CtiVanGogh::registration(CtiVanGoghConnectionManager *CM, const CtiPointRegistrationMsg &aReg)
{
    int nRet = NoError;

    RWTime     NowTime;

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
        PointMgr.InsertConnectionManager(CM, aReg, gDispatchDebugLevel & DISPATCH_DEBUG_REGISTRATION);


        if(!(aReg.getFlags() & REG_NO_UPLOAD))
        {
            postMOAUploadToConnection(*CM, aReg.getFlags());
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return nRet;
}

// Assumes lock on server_mux has been obtained.
int  CtiVanGogh::commandMsgHandler(CtiCommandMsg *Cmd)
{
    int status = NORMAL;
    int i;
    int pid;

    CtiPoint       *pPt;

    switch( Cmd->getOperation() )
    {
    case (CtiCommandMsg::ClearAlarm):
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

            for(i = 1; i + 1 < Cmd->getOpArgList().entries(); i += 2)
            {
                pid = Cmd->getOpArgList().at(i);
                int alarmcondition = Cmd->getOpArgList().at(i+1);

                pPt = PointMgr.getEqual(pid);

                if(pPt != NULL)      // I know about the point...
                {
                    CtiSignalMsg *pSigNew = 0;

                    {
                        CtiLockGuard<CtiMutex> pmguard(server_mux);
                        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();

                        pSigNew = _signalManager.setAlarmAcknowledged(pid, alarmcondition, true);    // Clear the tag, return the signal!

                        if(pDyn != NULL)
                        {
                            if(pSigNew != NULL)
                            {
                                pSigNew->setUser( Cmd->getUser() );
                                pSigNew->setTags( (pDyn->getDispatch().getTags() & ~MASK_ANY_ALARM) | TAG_REPORT_MSG_TO_ALARM_CLIENTS);
                                pSigNew->setText(AlarmTagsToString(pSigNew->getTags()) + pSigNew->getText());

                                if( !pPt->getAlarming().getNotifyOnAcknowledge() )
                                {
                                    pSigNew->setMessageTime( Cmd->getMessageTime() );
                                }

                                pSigNew->setMessagePriority( 15 );   // Max this out we want it to hurry.
                            }

                            // Mark it if there are other alarms on the point.
                            UINT premask    = pDyn->getDispatch().getTags( ) & MASK_ANY_ALARM;
                            UINT amask      = _signalManager.getAlarmMask(pid);

                            if(premask != amask)
                            {
                                // Adjust the point tags to reflect the potentially new state of the alarm tags.
                                pDyn->getDispatch().resetTags( MASK_ANY_ALARM );
                                pDyn->getDispatch().setTags( amask );

                                // Hey fool you need to send out tags now!
                                CtiSignalMsg *pTagSig = CTIDBG_new CtiSignalMsg(pPt->getID(), Cmd->getSOE(), "Tag Update");
                                pTagSig->setMessagePriority(15);
                                pTagSig->setUser(Cmd->getUser());
                                pTagSig->setTags( pDyn->getDispatch().getTags() | TAG_REPORT_MSG_TO_ALARM_CLIENTS);
                                postMessageToClients(pTagSig);
                                delete pTagSig;
                            }

                            if(DebugLevel & DEBUGLEVEL_LUDICROUS)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;

                                dout << " Signal has tags: " << pSigNew->getTags() << " " << explainTags(pSigNew->getTags())  << endl;
                                dout << " SigMgr has tags: " << amask << " " << explainTags(amask) << endl;
                                dout << " Point  has tags: " << pDyn->getDispatch().getTags()  << " " << explainTags(pDyn->getDispatch().getTags()) << endl;
                            }
                        }
                    }

                    if(pSigNew != NULL)
                    {
                        // Make sure that anyone who cared about the first one gets the CTIDBG_new state of the tag!
                        postMessageToClients(pSigNew);
                        _signalMsgQueue.putQueue(pSigNew);
                        pSigNew = 0;
                    }
                }
            }

            break;
        }
    case (CtiCommandMsg::ControlRequest):
        {
            /*
             *  This block receives a CommandMsg from a requesting client and processes it for submission to
             *  pil/porter.
             */
            if(Cmd->getOpArgList().entries() >= 4)
            {
                LONG token     = Cmd->getOpArgList().at(0);
                LONG did       = Cmd->getOpArgList().at(1);
                LONG pid       = Cmd->getOpArgList().at(2);
                LONG rawstate  = Cmd->getOpArgList().at(3);
                INT  ctrl_offset = 0;

                // Find PIL in the connection list.
                try
                {
                    {
                        CtiPoint *pPoint = NULL;
                        if(Cmd->getOpArgList().entries() >= 5)
                        {
                            // This is a BOOL which indicates whether a ctrl offset is spec'd by pid.
                            // If this is so, "did" MUST also be specified!
                            ctrl_offset = Cmd->getOpArgList().at(4);

                            if(did == 0 || ctrl_offset == 0)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                            pPoint = PointMgr.getEqual(pid);
                        }

                        if(pPoint != NULL)
                        {
                            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

                            if(pDyn != NULL
                               && pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE
                               && !(pDyn->getDispatch().getTags() & (TAG_DISABLE_CONTROL_BY_POINT | TAG_DISABLE_CONTROL_BY_DEVICE)))
                            {
                                if(did == 0)      // We need to find the point's device
                                {
                                    did = pPoint->getDeviceID();
                                }

                                CtiPendingPointOperations pendingControlRequest(pPoint->getID());
                                pendingControlRequest.setType(CtiPendingPointOperations::pendingControl);
                                pendingControlRequest.setControlState( CtiPendingPointOperations::controlSentToPorter );
                                pendingControlRequest.setTime( Cmd->getMessageTime() );
                                pendingControlRequest.setControlCompleteValue( (DOUBLE) rawstate );
                                pendingControlRequest.setControlTimeout( pPoint->getControlExpirationTime() );

                                pendingControlRequest.getControl().setPAOID( did );
                                pendingControlRequest.getControl().setStartTime(RWTime(YUKONEOT));

                                RWCString devicename= resolveDeviceName(*pPoint);
                                CtiSignalMsg *pFailSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), devicename + " / " + pPoint->getName() + ": Commanded Control " + ResolveStateName(pPoint->getStateGroupID(), rawstate) + " Failed", getAlarmStateName( pPoint->getAlarming().getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, pPoint->getAlarming().getAlarmCategory(CtiTablePointAlarming::commandFailure), Cmd->getUser());

                                pendingControlRequest.setSignal( pFailSig );

                                addToPendingSet(pendingControlRequest, Cmd->getMessageTime());

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " " << devicename << " / " << pPoint->getName() << " has gone CONTROL SUBMITTED. Control expires at " << RWTime( Cmd->getMessageTime() + pPoint->getControlExpirationTime()) << endl;
                                }

                                CtiSignalMsg *pCRP = CTIDBG_new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), "Control " + ResolveStateName(pPoint->getStateGroupID(), rawstate) + " Sent", RWCString(), GeneralLogType, SignalEvent, Cmd->getUser());
                                MainQueue_.putQueue( pCRP );
                                pCRP = 0;

                                pDyn->getDispatch().setTags( TAG_CONTROL_PENDING );
                                writeControlMessageToPIL(did, rawstate, (CtiPointStatus*)pPoint, Cmd);
                            }
                        }
                    }
                }
                catch(const RWxmsg& x)
                {
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
    case (CtiCommandMsg::Ablement):
    case (CtiCommandMsg::ControlAblement):
        {
            messageDump((CtiMessage*)Cmd);

            if(Cmd->getOpArgList().entries() >= 4)
            {
                LONG token     = Cmd->getOpArgList().at(0);

                for(i = 1; i < Cmd->getOpArgList().entries(); i = i + 3 )
                {
                    LONG idtype   = Cmd->getOpArgList().at(i);
                    LONG id       = Cmd->getOpArgList().at(i+1);
                    bool disable  = !((Cmd->getOpArgList().at(i+2) != 0));
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
                                if(idtype == OP_DEVICEID)
                                {
                                    tagmask = TAG_DISABLE_DEVICE_BY_DEVICE;
                                    setmask |= (disable ? TAG_DISABLE_DEVICE_BY_DEVICE : 0);    // Set it, or clear it?
                                }
                                else if(idtype == OP_POINTID)
                                {
                                    tagmask = TAG_DISABLE_POINT_BY_POINT;
                                    setmask |= (disable ? TAG_DISABLE_POINT_BY_POINT : 0);      // Set it, or clear it?
                                }
                            }
                            else if(Cmd->getOperation() == CtiCommandMsg::ControlAblement)
                            {
                                if(idtype == OP_DEVICEID)
                                {
                                    tagmask = TAG_DISABLE_CONTROL_BY_DEVICE;
                                    setmask |= (disable ? TAG_DISABLE_CONTROL_BY_DEVICE : 0);   // Set it, or clear it?
                                }
                                else if(idtype == OP_POINTID)
                                {
                                    tagmask = TAG_DISABLE_CONTROL_BY_POINT;
                                    setmask |= (disable ? TAG_DISABLE_CONTROL_BY_POINT : 0);    // Set it, or clear it?
                                }
                            }

                            if(idtype == OP_DEVICEID)
                            {
                                CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(id);
                                if(dliteit != _deviceLiteSet.end() && ablementDevice(dliteit, setmask, tagmask))
                                {
                                    adjustDeviceDisableTags(id);    // We always have an id here.
                                }
                            }
                            else if(idtype == OP_POINTID)
                            {
                                bool devicedifferent;

                                pPt = PointMgr.getEqual(id);
                                ablementPoint(pPt, devicedifferent, setmask, tagmask, Cmd->getUser(), *pMulti);

                                if(devicedifferent)     // The device became interesting because of this change.
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    }
                                }
                            }

                            if(pMulti->getData().entries())
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
            // Vector contains token, pointid, tag(s) to set.
            LONG token      = Cmd->getOpArgList().at(0);
            LONG pointid    = Cmd->getOpArgList().at(1);
            LONG tagstoset  = Cmd->getOpArgList().at(2);
            LONG tagstoreset= Cmd->getOpArgList().at(3);

            {
                CtiPoint *pPt = PointMgr.getEqual( pointid );
                CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();
                pDyn->getDispatch().setTags( tagstoset );
                pDyn->getDispatch().resetTags( tagstoreset );
            }

            break;
        }
    default:
        {
            status = Inherited::commandMsgHandler(Cmd);
            break;
        }
    }

    return status;
}

void CtiVanGogh::clientShutdown(CtiConnectionManager *&CM)
{
    CtiLockGuard<CtiMutex> guard(server_mux);

    try
    {
        // Make sure no queue entries point at this connection!
        MainQueue_.apply(ApplyBlankDeletedConnection, (void*)CM);
        PointMgr.RemoveConnectionManager(CM);
        Inherited::clientShutdown(CM);
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
    CtiConnectionManager    *Mgr = NULL;
    CHAR                    temp[80];


    try
    {
        if(Msg.getMessageTime().seconds() > RWTime().seconds() ||
           RWTime().seconds() - Msg.getMessageTime().seconds() < 900)   // Nothing older than 15 minutes!
        {
            if(Msg.getId())
            {
                _snprintf(temp, sizeof(temp) - 1, "ID %ld", Msg.getId());
            }
            else
            {
                _snprintf(temp, sizeof(temp) - 1, "ENTRY");
            }

            RWCString desc = RWCString(temp) + resolveDBChangeType(Msg.getTypeOfChange()) + resolveDBChanged(Msg.getDatabase());

            CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(0, 0, desc, "DATABASE CHANGE");

            pSig->setUser(Msg.getUser());

            // Send the message out to every connected client.
            {
                CtiLockGuard<CtiMutex> guard(server_mux);
                CtiServer::iterator  iter(mConnectionTable);

                for(;(Mgr = (CtiVanGoghConnectionManager *)iter());)
                {
                    Mgr->WriteConnQue( Msg.replicateMessage(), 2500 );        // Send a copy of DBCHANGE on to each clients.

                    if(((CtiVanGoghConnectionManager*)Mgr)->getEvent()) // If the client cares about events...
                    {
                        Mgr->WriteConnQue(pSig->replicateMessage(), 2500);    // Copy pSig out to any event registered client
                    }
                }
            }

            _signalMsgQueue.putQueue( pSig );
            pSig = 0;
            loadRTDB(true, Msg.replicateMessage());
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " DBCHANGE has expired.  It was sent at " << Msg.getMessageTime() << endl;
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
    RWTime   NextTime;
    RWTime   TimeNow;

    UINT sanity = 0;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch RTDB Archiver Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
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
                    dout << RWTime() << " RTDB Archiver Thread: Archiving/Checking in " << sleepTime << " Seconds" << endl;
                }
            }

            /*
             *  Step 3. Wait that amount of time from now
             */

            for(int i = sleepTime; !bGCtrlC && sleepTime > 0; sleepTime--)
            {
                if(!(++sanity % SANITY_RATE))
                {
                    reportOnThreads();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " RTDB Archiver Thread Active " << endl;
                    }
                }

                // This should be an WaitForSync API call.
                rwSleep(1000);
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Dispatch RTDB Archiver Thread shutting down" << endl;
        }
    }
    catch(RWxmsg& msg )
    {
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Exception.  Thread death. **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}


void CtiVanGogh::VGTimedOperationThread()
{
    UINT sanity = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch Timed Operation Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!bGCtrlC;)
        {
            if(!(++sanity % SANITY_RATE))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch Timed Operation Thread Active " << endl;
                }
                reportOnThreads();
            }

            rwSleep(1000);

            purifyClientConnectionList();
            updateRuntimeDispatchTable();
            doPendingOperations();

            loadRTDB(false);                 // Refresh (if time says so) the memory objects
        }
    }
    catch(RWxmsg& msg )
    {
        dout << "Error: " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    updateRuntimeDispatchTable(true);
    doPendingOperations();


    // And let'em know were A.D.
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch Timed Operation Thread shutting down" << endl;
    }

    return;
}

INT CtiVanGogh::archivePointDataMessage(const CtiPointDataMsg &aPD)
{
    INT status = NORMAL;

    try
    {
        // See if I know about this point ID
        CtiPoint *TempPoint = PointMgr.getEqual(aPD.getId());

        if(TempPoint != NULL)      // We do know this point..
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)TempPoint->getDynamic();

            bool isNew = isPointDataNewInformation( aPD );

            if(pDyn != NULL)
            {
                if( aPD.getTime() >= pDyn->getTimeStamp() || (aPD.getTags() & TAG_POINT_FORCE_UPDATE) )
                {
                    if( pDyn->getDispatch().getTags() & (MASK_ANY_SERVICE_DISABLE | MASK_ANY_CONTROL_DISABLE) )
                    {
                        // This one cannot go unless manual tag is set.
                        if(aPD.getQuality() == ManualQuality)
                        {
                            pDyn->setPoint(aPD.getTime(), aPD.getValue(), aPD.getQuality(), aPD.getTags());
                        }
                    }
                    else
                    {
                        // Set the point in memory to the current value.  Archive if an archive is pending.
                        // Do not update with an older time!
                        // Unless we are in the forced condition
                        pDyn->setPoint(aPD.getTime(), aPD.getValue(), aPD.getQuality(), aPD.getTags());
                    }
                }

                if( aPD.getTags() & (TAG_POINT_MUST_ARCHIVE | TAG_POINT_LOAD_PROFILE_DATA) )
                {
                    // This is a forced reading, which must be written, it should not
                    // however cause any change in normal pending scanned readings

                    _archiverQueue.putQueue( CTIDBG_new CtiTableRawPointHistory(TempPoint->getID(), aPD.getQuality(), aPD.getValue(), aPD.getTime()));
                }
                else if(pDyn->isArchivePending() ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnUpdate) ||
                        (TempPoint->getArchiveType() == ArchiveTypeOnChange && isNew))
                {
                    _archiverQueue.putQueue( CTIDBG_new CtiTableRawPointHistory(TempPoint->getID(), aPD.getQuality(), aPD.getValue(), aPD.getTime()));
                    TempPoint->setArchivePending(FALSE);
                }
            }
        }
        else
        {
            CHAR temp[80];

            _snprintf(temp, 79, "Point change for unknown PointID: %ld", aPD.getId());
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << temp << endl;
            }

            CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Point Data Relay");
            pSig->setUser(aPD.getUser());
            _signalMsgQueue.putQueue(pSig);

            status = IDNF; // Error is ID not found!
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

INT CtiVanGogh::archiveSignalMessage(const CtiSignalMsg& aSig)
{
    int status = NORMAL;

    try
    {
        CtiSignalMsg *pSig = NULL;

        {
            // See if I know about this point ID
            CtiLockGuard<CtiMutex> pmguard(server_mux);
            CtiPoint *TempPoint = PointMgr.getEqual(aSig.getId());

            if(TempPoint != NULL)
            {
                pSig = (CtiSignalMsg*)aSig.replicateMessage();
            }
            else
            {
                CHAR temp[80];

                _snprintf(temp, sizeof(temp), "Signal for unknown PointID: %ld", aSig.getId());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << temp << endl;
                }

                pSig = CTIDBG_new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Signal Relay");
                pSig->setUser(aSig.getUser());
                status = IDNF; // Error is ID not found!
            }
        }

        if(pSig != NULL)
        {
            _signalMsgQueue.putQueue(pSig);
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
        for(int i = 0; i < pMulti->getData().entries(); i++)
        {
            CtiMessage *pMsg = (CtiMessage*)pMulti->getData()(i);

            switch(pMsg->isA())
            {
            case MSG_POINTREGISTRATION:
            case MSG_REGISTER:
            case MSG_POINTDATA:
            case MSG_SIGNAL:
            case MSG_DBCHANGE:            // How about this potential recursion....
            case MSG_EMAIL:
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
                registration((CtiVanGoghConnectionManager*)(pMsg->getConnectionHandle()), aReg);
                break;
            }
        case MSG_REGISTER:
            {
                messageDump(pMsg);
                const CtiRegistrationMsg &aReg = *((CtiRegistrationMsg*)(pMsg));

                CtiVanGoghConnectionManager *CM = (CtiVanGoghConnectionManager*)(pMsg->getConnectionHandle());

                CM->setClientName(aReg.getAppName());
                CM->setClientAppId(aReg.getAppId());
                CM->setClientUnique(aReg.getAppIsUnique());
                CM->setClientExpirationDelay(aReg.getAppExpirationDelay());

                clientRegistration(CM);
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
        case MSG_EMAIL:
            {
                CtiEmailMsg &Email = *((CtiEmailMsg*)pMsg);
                mail(Email);
                break;
            }
        case MSG_LMCONTROLHISTORY:
            {
                processControlMessage((CtiLMControlHistoryMsg*)pMsg);
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

    CtiLockGuard<CtiMutex> guard(server_mux);
    CtiServer::iterator  iter(mConnectionTable);

    CtiConnectionManager *Mgr = NULL;
    CtiConnectionManager *MgrToRemove = NULL;

    try
    {
        for(;(Mgr = (CtiConnectionManager *)iter()) != NULL;)
        {
            CtiVanGoghConnectionManager &VGCM = *((CtiVanGoghConnectionManager*)Mgr);

            try
            {
                pMulti = generateMultiMessageForConnection(VGCM, pMsg);

                if(pMulti->getData().entries() > 0)
                {
                    if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)
                    {
                        RWTime Now;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "\n<<<< Message to Client Connection " << VGCM.getClientName() << " on " << VGCM.getPeer() << " START" << endl;
                        }
                        pMulti->dump();
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "<<<< Message to Client Connection " << VGCM.getClientName() << " on " << VGCM.getPeer() << " COMPLETE\n" << endl;
                        }
                    }

                    if( VGCM.WriteConnQue(pMulti, 5000) )
                    {
                        MgrToRemove = Mgr;

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Connection is not viable : " << VGCM.getClientName() << " / " << VGCM.getClientAppId() << endl;
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
                dout << "Exception: " << __FILE__ << " (" << __LINE__ << ") " << x.why() << endl;
                break;
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        if(MgrToRemove != NULL)    // Someone failed.. Blitz them
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



CtiMultiMsg* CtiVanGogh::generateMultiMessageForConnection(const CtiVanGoghConnectionManager &Conn, CtiMessage *pMsg)
{
    INT            status   = NORMAL;

    CtiMultiMsg    *pMulti  = CTIDBG_new CtiMultiMsg;


    if(pMulti != NULL)
    {
        RWOrdered &aOrdered = pMulti->getData();        // This is the big box we put all other messages in for this connection
        status = assembleMultiForConnection(Conn, pMsg, aOrdered);
    }

    return pMulti;
}

INT CtiVanGogh::assembleMultiForConnection(const CtiVanGoghConnectionManager &Conn, CtiMessage *pMsg, RWOrdered &aOrdered)
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
    case MSG_DBCHANGE:
    case MSG_POINTREGISTRATION:
    case MSG_REGISTER:
    case MSG_COMMAND:                // This may be a non-updated command
    case MSG_EMAIL:
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

INT CtiVanGogh::assembleMultiFromMultiForConnection(const CtiVanGoghConnectionManager &Conn,
                                                    CtiMessage                        *pMsg,
                                                    RWOrdered                         &Ord)
{
    INT            i;
    INT            status   = NORMAL;
    CtiMultiMsg    *pMulti  = (CtiMultiMsg*)pMsg;      // Uses the inheritance nature of MSG_PCRETURN
    CtiMessage     *pMyMsg;

    if(pMulti != NULL)
    {
        RWOrderedIterator itr( pMulti->getData() );

        for(;NULL != (pMyMsg = (CtiMessage*)itr());)
        {
            status = assembleMultiForConnection(Conn, pMyMsg, Ord);
            if(status != NORMAL)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Error processing this message " << endl;
                }
                pMyMsg->dump();
            }
        }
    }

    return status;
}

INT CtiVanGogh::assembleMultiFromSignalForConnection(const CtiVanGoghConnectionManager &Conn,
                                                     CtiMessage                        *pMsg,
                                                     RWOrdered                         &Ord)
{
    INT            status   = NORMAL;
    CtiSignalMsg   *pSig    = (CtiSignalMsg*)pMsg;

    try
    {
        if(pSig != NULL)
        {
            if(isSignalForConnection(Conn, *pSig))
            {
                CtiSignalMsg *pNewSig = (CtiSignalMsg *)pSig->replicateMessage();

                // FIX FIX FIX ... Do I need this point code here???
                CtiPoint *pPoint = PointMgr.getEqual(pSig->getId());

                if(pPoint != NULL)
                {
                    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();
                    // Only set non-alarm tags.  This signal must indicate if it is an alarm on entry (via checkSignalStateQuality)
                    pNewSig->setTags( (pDyn->getDispatch().getTags() & ~MASK_ANY_ALARM) );
                }

                Ord.insert(pNewSig);
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

INT CtiVanGogh::assembleMultiFromPointDataForConnection(const CtiVanGoghConnectionManager &Conn,
                                                        CtiMessage                        *pMsg,
                                                        RWOrdered                         &Ord)
{
    INT               status   = NORMAL;
    CtiPointDataMsg   *pDat    = (CtiPointDataMsg*)pMsg;

    try
    {
        if(pDat != NULL)
        {
            if(!(pDat->getTags() & TAG_POINT_DELAYED_UPDATE))   // We will propagate this one later!
            {
                CtiPoint *pTempPoint = PointMgr.getEqual(pDat->getId());

                if(pTempPoint != NULL)
                {
                    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

                    if(pDat->getType() == InvalidPointType)
                    {
                        pDat->setType(pTempPoint->getType());
                    }
                    else if(pTempPoint->getType() != pDat->getType())
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** WARNING **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Point Type mismatch.  Received point data message indicated a type" << endl;
                        dout << "  for point " << pDat->getId() << " which does not match the memory image held by dispatch." << endl;

                        dout << " Memory Image point type = " << pTempPoint->getType() << endl;
                        dout << " Message point type = " << pDat->getType() << endl;

                        if(pDat->getConnectionHandle() != NULL)
                        {
                            CtiConnectionManager *CM = ((CtiConnectionManager*)pDat->getConnectionHandle());

                            dout << " Submitting client   " << CM->getClientName() << endl;
                            dout << "  Client Information " << CM->getPeer() << endl;
                        }
                    }

                    if( pDat->getQuality() == ManualQuality || !(pDyn->getDispatch().getTags() & (MASK_ANY_SERVICE_DISABLE)) ) // (MASK_ANY_SERVICE_DISABLE | MASK_ANY_CONTROL_DISABLE)) )
                    {
                        if(isPointDataForConnection(Conn, *pDat))
                        {
                            {
                                CtiPointDataMsg *pNewData = (CtiPointDataMsg *)pDat->replicateMessage();

                                pNewData->setTags( pDyn->getDispatch().getTags() );       // Report any set tags out to the clients.
                                Ord.insert(pNewData);
                            }
                        }
                    }
                    else if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                    {
                        // Point data on a disabled point.
                        RWCString gripe = RWCString(" NO DATA REPORT to: ") + Conn.getClientName() + RWCString(" ");
                        INT mask = (pDyn->getDispatch().getTags() & MASK_ANY_DISABLE);

                        if(mask & (TAG_DISABLE_DEVICE_BY_DEVICE))
                        {
                            gripe += pTempPoint->getName() + RWCString(" is disabled by its device");
                        }
                        else if(mask & (TAG_DISABLE_POINT_BY_POINT))
                        {
                            gripe += pTempPoint->getName() + RWCString(" is disabled");
                        }
                        else if(mask & (TAG_DISABLE_CONTROL_BY_POINT | TAG_DISABLE_CONTROL_BY_DEVICE))
                        {
                            gripe += pTempPoint->getName() + RWCString(" is control disabled");
                        }

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << gripe << endl;
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

BOOL CtiVanGogh::isSignalForConnection(const CtiVanGoghConnectionManager   &Conn,
                                       const CtiSignalMsg                  &Msg)
{
    BOOL bStatus = FALSE;

    /*
     *  Check if this connection cared about signals at all
     */
    if( ((Msg.getTags() & TAG_REPORT_MSG_TO_ALARM_CLIENTS) && Conn.getAlarm()) ||
        ( Msg.isAlarm() && Conn.getAlarm() ) ||
        ( Msg.isEvent() && Conn.getEvent() ) )
    {
        bStatus = TRUE;
    }
    else
    {
        bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getId());
    }

    return bStatus;
}

BOOL CtiVanGogh::isPointDataForConnection(const CtiVanGoghConnectionManager &Conn, const CtiPointDataMsg &Msg)
{
    BOOL bStatus = FALSE;

    if( !(Msg.getTags() & TAG_POINT_LOAD_PROFILE_DATA) )  // Load profile does not go through the system.
    {
        if( Conn.isRegForChangeType(Msg.getType()))
        {
            bStatus = TRUE;
        }
        else
        {
            bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getId());
        }
    }
    else if( Conn.getLoadProfile() )    // Connection cares about load profile data.
    {
        bStatus = isConnectionAttachedToMsgPoint(Conn, Msg.getId());   // Is this a point he cares about..
    }

    return bStatus;
}

BOOL CtiVanGogh::isPointDataNewInformation(const CtiPointDataMsg &Msg)
{
    bool bValueChange = true;
    bool bQualityChange = true;
    BOOL bStatus = TRUE;

    if( Msg.isExemptable() )      // Find out if we can exempt the data from being sent to clients
    {
        // Verify that the point has actually changed from the last known!
        // OR it must be marked for forcing through the system
        CtiPoint *pPoint = PointMgr.getEqual(Msg.getId());

        if(pPoint != NULL)      // We do know this point..
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();


            if(pDyn != NULL)
            {
                if(pDyn->getValue() == Msg.getValue())
                {
                    bValueChange = false;   // There was no value change.
                }

                if( pDyn->getQuality() == NonUpdatedQuality || pDyn->getQuality() == Msg.getQuality() )
                {
                    /*
                     *  If the qualities have not changed, or the old quality was non-updated, we do not wish to realarm the point.
                     */
                    bQualityChange = false;
                }
            }
        }
    }

    bStatus = ( (bValueChange || bQualityChange) ? TRUE : FALSE );

    return bStatus;
}

BOOL CtiVanGogh::isConnectionAttachedToMsgPoint(const CtiVanGoghConnectionManager   &Conn,
                                                const LONG                          pID)
{
    BOOL bStatus = FALSE;

    CtiPoint *TempPoint = PointMgr.getEqual(pID);

    if(TempPoint != NULL)      // We do know this point..
    {
        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)TempPoint->getDynamic();

        if(pDyn != NULL)
        {
            CtiPointConnection *pPC = (CtiPointConnection*)(pDyn->getAttachment());
            if(pPC != NULL)
            {
                if( pPC->getManagerList().contains(&Conn) )
                {
                    bStatus = TRUE;
                }
            }
        }
    }

    return bStatus;
}

int CtiVanGogh::processCommErrorMessage(CtiCommErrorHistoryMsg *pMsg)
{
    int status = NORMAL;

    try
    {
        status = archiveCommErrorHistoryMessage(*pMsg);
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
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    try
    {
        CtiPoint *pPoint = NULL;
        CtiLockGuard<CtiMutex> pmguard(server_mux);
        pPoint = PointMgr.getEqual(pMsg->getPointId());

        if(pPoint != NULL)
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

            if(pDyn != NULL
               && pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE
               && !(pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_POINT ||
                    pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_DEVICE))
            {
                if(pMsg->getPAOId() == 0)      // We need to find the point's device
                {
                    pMsg->setPAOId( pPoint->getDeviceID() );
                }

                CtiPendingPointOperations pendingControlLMMsg(pPoint->getID());
                pendingControlLMMsg.setType(CtiPendingPointOperations::pendingControl);
                pendingControlLMMsg.setControlState( CtiPendingPointOperations::controlPending );
                pendingControlLMMsg.setTime( pMsg->getStartDateTime() );
                pendingControlLMMsg.setControlCompleteValue( (DOUBLE) pMsg->getRawState() );
                pendingControlLMMsg.setControlTimeout( pPoint->getControlExpirationTime() );

                // We prime the pending control object here, where we know all there is to know.
                pendingControlLMMsg.getControl().setPAOID(pMsg->getPAOId());
                pendingControlLMMsg.getControl().setActiveRestore(pMsg->getActiveRestore());
                pendingControlLMMsg.getControl().setDefaultActiveRestore(pMsg->getActiveRestore());
                pendingControlLMMsg.getControl().setControlDuration(pMsg->getControlDuration());
                pendingControlLMMsg.getControl().setControlType(pMsg->getControlType());
                pendingControlLMMsg.getControl().setReductionValue(pMsg->getReductionValue());
                pendingControlLMMsg.getControl().setReductionRatio(pMsg->getReductionRatio());
                pendingControlLMMsg.getControl().setStartTime(pMsg->getStartDateTime());
                pendingControlLMMsg.getControl().setStopTime(pMsg->getStartDateTime().seconds() + pMsg->getControlDuration());
                pendingControlLMMsg.getControl().setControlCompleteTime(pMsg->getStartDateTime().seconds() + pMsg->getControlDuration());
                pendingControlLMMsg.getControl().setSoeTag( CtiTableLMControlHistory::getNextSOE() );

                CtiSignalMsg *pFailSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), 0, "Control " + ResolveStateName(pPoint->getStateGroupID(), pMsg->getRawState()) + " Failed", getAlarmStateName( pPoint->getAlarming().getAlarmCategory(CtiTablePointAlarming::commandFailure) ), GeneralLogType, pPoint->getAlarming().getAlarmCategory(CtiTablePointAlarming::commandFailure), pMsg->getUser());

                pendingControlLMMsg.setSignal( pFailSig );

                addToPendingSet( pendingControlLMMsg, pMsg->getMessageTime() );

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

    CtiLockGuard<CtiMutex> guard(server_mux);

    checkDataStateQuality(pMsg, MultiWrapper);
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

INT CtiVanGogh::postMOAUploadToConnection(CtiVanGoghConnectionManager &VGCM, int flags)
{
    INT i;
    INT status = NORMAL;

    CtiTableSignal *pSig;
    CtiMultiMsg    *pMulti  = CTIDBG_new CtiMultiMsg;


    if(pMulti != NULL)
    {
        pMulti->setMessagePriority(15);

        CtiLockGuard<CtiMutex> pmguard(server_mux);
        CtiPointClientManager::CtiRTDBIterator  itr(PointMgr.getMap());

        for(;itr();)
        {
            CtiPoint *TempPoint = itr.value();
            {
                CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)TempPoint->getDynamic();

                if(pDyn != NULL)
                {
                    CtiPointConnection *pPC = (CtiPointConnection*)(pDyn->getAttachment());
                    if(pPC != NULL)
                    {
                        if( pPC->getManagerList().contains(&VGCM) || (VGCM.isRegForChangeType(TempPoint->getType())))
                        {
                            CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(TempPoint->getID(),
                                                                               pDyn->getValue(),
                                                                               pDyn->getQuality(),
                                                                               TempPoint->getType(),
                                                                               RWCString(),
                                                                               pDyn->getDispatch().getTags());

                            if(pDat != NULL)
                            {
                                if(flags & REG_TAG_MARKMOA)
                                {
                                    pDat->setTags(TAG_POINT_MOA_REPORT);
                                }

                                // Make the time match the entered time
                                pDat->setTime( pDyn->getTimeStamp() );
                                pMulti->getData().insert(pDat);
                            }
                        }

                        // This could be replaced by an object/method which returns a multi message
                        // full of all alarms active/unacknowledged on this point.
                        if( (pDyn->getDispatch().getTags() & MASK_ANY_ALARM) && VGCM.getAlarm() )
                        {
                            CtiMultiMsg *pSigMulti = _signalManager.getPointSignals(TempPoint->getID());

                            if(pSigMulti)
                            {
                                pMulti->getData().insert(pSigMulti);
                            }
                        }
                    }
                }
            }
        }

        if(pMulti->getCount() > 0)
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_MSGSTOCLIENT)    // Temp debug
            {
                {
                    RWTime Now;
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << Now << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << Now << " Client Connection " << VGCM.getClientName() << " on " << VGCM.getPeer() << endl;
                }
                pMulti->dump();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** MOA UPLOAD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            if(VGCM.WriteConnQue(pMulti, 5000))
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Connection is having issues : " << VGCM.getClientName() << " / " << VGCM.getClientAppId() << endl;
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

    CtiLockGuard<CtiMutex> pmguard(server_mux);
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBDatabase   db       = conn.database();
        RWDBSelector   selector = conn.database().selector();
        RWDBTable      keyTable;
        RWDBReader     rdr;

        CtiTableDynamicPointAlarming::getSQL( db, keyTable, selector );

        rdr = selector.reader( conn );

        if(rdr.status().errorCode() != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
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
            sig.setTags( dynAlarm.getTags() & MASK_ANY_ALARM );     // We only care about the alarm masks!
            sig.setCondition( dynAlarm.getAlarmCondition() );
            sig.setLogID(dynAlarm.getLogID());
            sig.setSOE(dynAlarm.getSOE());
            sig.setUser(dynAlarm.getUser());

            // sig.setLogType(dynAlarm.getLogType());   FIX FIX FIX CGP ... think about these two lines.
            sig.setLogType( AlarmCategoryLogType );

            _signalManager.addSignal( sig );
        }
    }

    return status;
}


void CtiVanGogh::writeSignalsToDB(bool justdoit)
{
    CtiSignalMsg   *sigMsg;
    UINT           panicCounter = 0;

    RWOrdered      postList;

    try
    {
        if(_signalMsgQueue.entries() > 0)
        {
            {
                RWCString signals("signals");
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                if(conn.isValid())
                {
                    conn.beginTransaction(signals);

                    do
                    {
                        sigMsg = _signalMsgQueue.getQueue(0);

                        if(sigMsg != NULL)
                        {
                            CtiTableSignal sig(sigMsg->getId(), sigMsg->getMessageTime(), sigMsg->getText(), sigMsg->getAdditionalInfo(), sigMsg->getSignalCategory(), sigMsg->getLogType(), sigMsg->getSOE(), sigMsg->getUser());

                            if(!sigMsg->getText().isNull() || !sigMsg->getAdditionalInfo().isNull())
                            {
                                // No text, no point then is there now?
                                sig.Insert(conn);

                                sigMsg->setLogID(sig.getLogID());
                                /*
                                 *  Last thing we do is add this signal to the pending signal list iff it is an alarm
                                 *  AND it is not an cleared alarm....  This second condition prevents clear reports
                                 *  from being kept on the pending list.
                                 */

                                if( sigMsg->getSignalCategory() > SignalEvent && !(sigMsg->getTags() & TAG_REPORT_MSG_TO_ALARM_CLIENTS) )
                                {
                                    _signalManager.addSignal(*sigMsg);
                                }
                            }

                            postList.insert(sigMsg);
                        }

                    } while( conn.isValid() && sigMsg != NULL && (justdoit || (panicCounter++ < 500)));

                    conn.commitTransaction(signals);
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Unable to acquire a valid conneciton to the database" << endl;
                    }
                }
            }

            if(panicCounter > 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " SystemLog transaction complete. Inserted " << panicCounter << " signal messages.  " << _signalMsgQueue.entries() << " left on queue." << endl;
                }
            }
        }

        {
            RWOrderedIterator itr( postList );
            for(;NULL != (sigMsg = (CtiSignalMsg*)itr());)
            {
                postSignalAsEmail( *sigMsg );
            }

            postList.clearAndDestroy();
        }

        if(!_signalManager.empty() && _signalManager.dirty())
        {
            _signalManager.writeDynamicSignalsToDB();
        }
    }
    catch(RWxmsg &msg)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** RW EXCEPTION **** " << msg.why() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

            if( panicCounter > 0 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " RawPointHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                /*
                 *  double the queue's size if it is more than half full.
                 */
                if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

void CtiVanGogh::writeLMControlHistoryToDB(bool justdoit)
{
#define PANIC_CONSTANT 1000

    static UINT  dumpCounter = 0;
    UINT         panicCounter = 0;      // Make sure we don't write for too long...

    try
    {
        CtiTableLMControlHistory *pTblEntry;
        size_t lmentries = _lmControlHistoryQueue.entries();

        /*
         *  Go look if we need to write out archive points.
         *  We only do this once every 30 seconds or if there are >= 10 entries to do.
         */
        if(!(++dumpCounter % DUMP_RATE)
           || lmentries > MAX_ARCHIVER_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            if(lmentries > 0)
            {
                RWCString controlHistory("controlHistory");
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                conn.beginTransaction(controlHistory);

                while( conn.isValid() && ( justdoit || (panicCounter < PANIC_CONSTANT) ) && (pTblEntry = _lmControlHistoryQueue.getQueue(0)) != NULL)
                {
                    panicCounter++;
                    pTblEntry->Insert(conn);
                    delete pTblEntry;
                }

                conn.commitTransaction(controlHistory);
            }
            if( panicCounter > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " LMControlHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " LM Control History queue has " << _lmControlHistoryQueue.entries() << " entries" << endl;
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
                RWCString commError("commError");
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();
                {

                    conn.beginTransaction(commError);

                    while( conn.isValid() && ( justdoit || (panicCounter < PANIC_CONSTANT) ) && (pTblEntry = _commErrorHistoryQueue.getQueue(0)) != NULL)
                    {
                        panicCounter++;
                        pTblEntry->Insert(conn);
                        delete pTblEntry;
                    }

                    conn.commitTransaction(commError);
                }
            }

            if( panicCounter > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " CommErrorHistory transaction complete. Inserted " << panicCounter << " rows.  " << _commErrorHistoryQueue.entries() << " entries left on queue." << endl;
                }
            }

            RWDate todaysdate;

            if(todaysdate.dayOfMonth() != daynumber)
            {
                if(daynumber > 0)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Comm Error History log is being cleaned up. " << endl;
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
            CtiLockGuard<CtiMutex> server_guard(server_mux, 2500);      // Get a lock on it.

            if(server_guard.isAcquired())
            {
                if(ClientCount > 0)
                {
                    clientPurgeQuestionables(&KilledCount);      // Purge anyone marked as questionable

                    if(KilledCount > 0)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Of " << ClientCount << " connections " <<
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

    try
    {
        if(force || !(callCounter % UPDATERTDB_RATE) )    // Only chase the queue once per CONFRONT_RATE seconds.
        {
            unsigned long delay = force ? 20000 : 2500;

            CtiLockGuard<CtiMutex> server_guard(server_mux, delay);      // Get a lock on it.

            if(server_guard.isAcquired())
            {
                PointMgr.storeDirtyRecords();
                ++callCounter;
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

void CtiVanGogh::refreshCParmGlobals(bool force)
{
    try
    {
        InitYukonBaseGlobals();

        RWCString str;

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTLHIST_INTERVAL")).isNull() )
        {
            CntlHistInterval = atoi(str.data());
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHIST_INTERVAL found : " << str << endl;
        }
        else
        {
            CntlHistInterval = 3600;
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHIST_INTERVAL default : " << CntlHistInterval << endl;
        }

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTLHISTPOINTPOST_INTERVAL")).isNull() )
        {
            CntlHistPointPostInterval = atoi(str.data());
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHISTPOINTPOST_INTERVAL found : " << str << endl;
        }
        else
        {
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTLHISTPOINTPOST_INTERVAL default : " << CntlHistPointPostInterval << endl;
        }

        if( !(str = gConfigParms.getValueAsString("DISPATCH_CNTL_STOP_REPORT_INTERVAL")).isNull() )
        {
            CntlStopInterval = atoi(str.data());
            if(CntlStopInterval > 3600)
            {
                CntlStopInterval = 3600;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " DISPATCH_CNTL_STOP_REPORT_INTERVAL cannot be greater than 3600" << endl;
                }
            }
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTL_STOP_REPORT_INTERVAL found : " << str << endl;
        }
        else
        {
            CntlStopInterval = 60;
            if(DebugLevel & 0x0001) cout << "Configuration Parameter DISPATCH_CNTL_STOP_REPORT_INTERVAL default : " << CntlStopInterval << endl;
        }
    }
    catch(RWxmsg& msg )
    {
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
        RWOrderedIterator itr( pMulti->getData() );

        for(;NULL != (pMyMsg = (CtiMessage*)itr());)
        {
            status = checkDataStateQuality( pMyMsg, aWrap );
        }
    }

    return status;
}

INT CtiVanGogh::checkSignalStateQuality(CtiSignalMsg  *pSig, CtiMultiWrapper &aWrap)
{
    INT status = NORMAL;

    if(pSig->getText().isNull() && pSig->getAdditionalInfo().isNull())
    {
        CtiVanGoghConnectionManager *pVGCM = (CtiVanGoghConnectionManager*)(pSig->getConnectionHandle());

        RWCString cliname("Unknown");

        if(pVGCM != NULL && mConnectionTable.contains(pVGCM))
        {
            cliname = pVGCM->getClientName();
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << cliname << "\r" << "just submitted a blank signal message for point id " << pSig->getId() << endl;
        }

    }

    // This is an alarm if the alarm state indicates anything other than SignalEvent.
    if(pSig->getSignalCategory() > SignalEvent)
    {
        pSig->setTags(TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);
        pSig->setLogType(AlarmCategoryLogType);
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
 * CtiLockGuard<CtiMutex> guard(server_mux); must have been grabbed already.
 *----------------------------------------------------------------------------*/
INT CtiVanGogh::checkPointDataStateQuality(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap)
{
    INT status   = NORMAL;

    if(pData != NULL)
    {
        CtiPoint *pPoint = PointMgr.getEqual(pData->getId());

        if(pPoint != NULL)      // We do know this point..
        {
            // We need to make sure there is no pending pointdata on this pointid.
            // Arrival of a pointdata message eliminates a pending data msg.
            if( removePointDataFromPending( pData->getId(), *pData) )
            {
                updateControlHistory( pData->getId(), CtiPendingPointOperations::datachange, pData->getTime() );
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Pending pointdata for " << pPoint->getName() << " has been blocked by point update. " << endl;
                }
            }

            /*
             *  A point data can be submitted with the intent that it occur in the future.  If that bit indicator is set,
             *  the point is added to the pending list as a "pendingPointData".  Any subsequent change via pointdata
             *  pulls this pending operation from the list (per above)!
             */
            if(pData->getTags() & TAG_POINT_DELAYED_UPDATE)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Delayed update is indicated on point data for " << resolveDeviceName(*pPoint) << " / " << pPoint->getName() << endl;
                }

                CtiPendingPointOperations pendingPointData(pData->getId());
                pendingPointData.setType(CtiPendingPointOperations::pendingPointData);
                pendingPointData.setTime( pData->getTime() );
                pendingPointData.setPointData( (CtiPointDataMsg*)pData->replicateMessage() );

                pair< CtiPendingOpSet_t::iterator, bool > resultpair = _pendingPointInfo.insert( pendingPointData );            // Add to the pending operations.

                if(resultpair.second != true)
                {
                    /*  Based upon the removal above, we never expect a collision here, but if the operator<() method
                     *  gets touched, it could occur.. This is insurance. */
                    *resultpair.first = pendingPointData;
                }
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
                            status = checkForNumericAlarms(pData, aWrap, *pPoint);
                        }
                        else if(pPoint->isStatus())
                        {
                            status = checkForStatusAlarms(pData, aWrap, *pPoint);
                        }
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

    if( Op.at((size_t)1) == RWInteger(OP_DEVICEID) )    // All points on a device must be marked as nonUpdated
    {
        LONG did = Op.at((size_t)2);

        CtiLockGuard<CtiMutex> pmguard(server_mux);
        CtiPointClientManager::CtiRTDBIterator  itr(PointMgr.getMap());

        for(;itr();)
        {
            CtiPoint *pPoint = itr.value();

            if(pPoint != NULL && pPoint->getDeviceID() == did)      // We know this point.. AND it is our device!
            {
                status = markPointNonUpdated(*pPoint, aWrap);
            }
        }
    }
    else if( Op.at((size_t)1) == RWInteger(OP_POINTID) )
    {
        CtiLockGuard<CtiMutex> pmguard(server_mux);
        CtiPoint *pPoint = PointMgr.getEqual(Op.at((size_t)2));

        if(pPoint != NULL)      // We know this point..
        {
            status = markPointNonUpdated(*pPoint, aWrap);
        }
    }

    return status;
}


INT CtiVanGogh::markPointNonUpdated(CtiPointBase &point, CtiMultiWrapper &aWrap)
{
    INT status = NORMAL;

    if( !(point.getType() == StatusPointType && point.getPointOffset() == 2000) ) // If not a comm status point
    {
        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

        // Make sure we use the correct enumeration based upon point type.
        INT alarm = (point.isStatus() ? CtiTablePointAlarming::nonUpdatedStatus : CtiTablePointAlarming::nonUpdatedNumeric);

        if( pDyn != NULL )
        {
            UINT quality = pDyn->getDispatch().getQuality();

            if(quality != NonUpdatedQuality)
            {
                pDyn->getDispatch().setQuality(NonUpdatedQuality);

                if(!_signalManager.isAlarmed(point.getID(), alarm))
                {
                    CtiSignalMsg *pSig = CTIDBG_new CtiSignalMsg(point.getID(), 0, "Non Updated", getAlarmStateName( point.getAlarming().getAlarmCategory(alarm) ), GeneralLogType, point.getAlarming().getAlarmCategory(alarm));

                    tagSignalAsAlarm(point, pSig, alarm);
                    aWrap.getMulti()->insert( pSig );
                }

                CtiPointDataMsg *pDat = CTIDBG_new CtiPointDataMsg(point.getID(), pDyn->getValue(), pDyn->getQuality(), point.getType(), "Non Updated", pDyn->getDispatch().getTags());
                pDat->setTime(pDyn->getTimeStamp());

                aWrap.getMulti()->insert( pDat );
            }
        }

    }

    return status;
}


CtiVanGoghConnectionManager* CtiVanGogh::getPILConnection()
{
    CtiVanGoghConnectionManager *Mgr = NULL;

    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        CtiServer::iterator  iter(mConnectionTable);

        for(;(Mgr = (CtiVanGoghConnectionManager *)iter());)
        {
            if(Mgr->getClientName() == PIL_REGISTRATION_NAME)
            {
                break;      // The for has completed, we found the PIL.
            }
            else
            {
                Mgr = NULL;
            }
        }
    }

    return Mgr;
}

CtiVanGoghConnectionManager* CtiVanGogh::getScannerConnection()
{
    CtiVanGoghConnectionManager *Mgr = NULL;

    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        CtiServer::iterator  iter(mConnectionTable);

        for(;(Mgr = (CtiVanGoghConnectionManager *)iter());)
        {
            if(!Mgr->getClientName().compareTo(SCANNER_REGISTRATION_NAME,RWCString::ignoreCase))
            {
                break;      // The for has completed, we found the SCANNER.
            }
            else
            {
                Mgr = NULL;
            }
        }
    }

    return Mgr;
}


void CtiVanGogh::validateConnections()
{

    CtiConnectionManager *CM = rwnil;

    CtiLockGuard<CtiMutex> guard(server_mux);

    if(MainQueue_.entries() == 0)
    {
        while( (CM = mConnectionTable.remove(NonViableConnection, NULL)) != NULL )
        {
            {
                RWTime Now;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " ** INFO ** Vagrant connection detected. Removing it." << endl;
                dout << Now << "   Connection: " << CM->getClientName() << " id " << CM->getClientAppId() << " on " << CM->getPeer() << " will be removed" << endl;
            }

            clientShutdown(CM);
        }
    }

    return;
}

void CtiVanGogh::postSignalAsEmail( const CtiSignalMsg &sig )
{
    {
        LONG rid = -1;
        UINT ngid = SignalEvent;
        UINT signaltrx = sig.getSignalCategory(); // Alarm Category.

        if(signaltrx > SignalEvent)
        {
            // This is an "alarm" class signal.  It may need to be emailed into the world.

            CtiPoint *pPoint = NULL;
            {
                CtiLockGuard<CtiMutex> pmguard(server_mux);
                pPoint = PointMgr.getEqual(sig.getId());

                if(pPoint)
                {
                    rid  = pPoint->getAlarming().getRecipientID();
                    ngid = pPoint->getAlarming().getNotificationGroupID();

                    // Check if point identifies a single recipient to be notified on any signal
                    if(rid > 0)
                    {
                        CtiTableContactNotification *pContactNotif = NULL;
                        CtiTableNotificationGroup nulGrp;

                        nulGrp.setEmailFromAddress(gEmailFrom);
                        nulGrp.setEmailMessage(" ");
                        nulGrp.setGroupName("Yukon Alarming Subsystem");
                        nulGrp.setEmailSubject("YUKON ALARM");

                        if( (pContactNotif = getContactNotification(rid)) != NULL )
                        {
                            // Now we have it ALL!  Send the email
                            sendMail(sig, nulGrp, *pContactNotif);
                        }
                    }
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
    RWCString name;

    try
    {   // Make sure all objects that that store results are out of scope when the release is called
        CtiLockGuard<CtiMutex> guard(server_mux, 5000);

        if(guard.isAcquired())
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBDatabase   db       = conn.database();
            RWDBSelector   selector = conn.database().selector();
            RWDBTable      table    = db.table( "AlarmCategory" );

            selector <<
            table["alarmcategoryid"] <<
            table["categoryname"] <<
            table["notificationgroupid"];

            selector.from(table);

            RWDBReader  rdr = selector.reader( conn );
            if(rdr.status().errorCode() != RWDBStatus::ok)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << selector.asString() << endl;
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
            dout << RWTime() << " INFO: Alarm to Destination reload unable to acquire exclusion" << endl;
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

INT CtiVanGogh::checkForStatusAlarms(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point)
{
    int alarm;
    INT status = NORMAL;

    if(point.isStatus())    // OK, we are indeed a status point.
    {
        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

        if(pDyn != NULL)     // We must know about the point!
        {
            CtiSignalMsg *pSig;

            for( alarm = 0; alarm < CtiTablePointAlarming::invalidstatusstate; alarm++ )
            {
                pSig = NULL;       // There is no alarm for this alarmstate.

                // This prohibits re-alarming on the same problem!
                if( point.getAlarming().alarmOn( alarm ) ) // we are set to alarm on this condition (category > SignalEvent).
                {
                    switch(alarm)
                    {
                    case (CtiTablePointAlarming::nonUpdatedStatus):
                        {
                            if(pDyn->getQuality() != NonUpdatedQuality)
                            {
                                deactivatePointAlarm(alarm,aWrap,point,pDyn);
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
                    deactivatePointAlarm(alarm,aWrap,point,pDyn);
                }
            }

            // We ALWAYS create an occurrence event to stuff a state event log into the systemlog table.
            if( pDyn->getDispatch().getValue() != pData->getValue() )
            {
                RWCString txt = ResolveStateName(point.getStateGroupID(), (int)pData->getValue());
                RWCString addn;

                if(pData->getQuality() == ManualQuality)
                {
                    addn = "Manual Update";
                }

                pSig = CTIDBG_new CtiSignalMsg(point.getID(), pData->getSOE(), txt, addn);
                if(pSig != NULL)
                {
                    pSig->setUser(pData->getUser());
                    aWrap.getMulti()->insert( pSig );
                }
            }
        }
    }

    return status;
}

INT CtiVanGogh::checkForNumericAlarms(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point)
{
    int alarm;
    INT status = NORMAL;

    if(point.isNumeric())
    {
        CtiPointNumeric             *pNumeric = (CtiPointNumeric*)&point;

        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

        if(pDyn != NULL)     // We must know about the point!
        {
            CtiSignalMsg    *pSig = NULL;
            UINT            tags = pDyn->getDispatch().getTags();

            // We check if the point has been sent in as a Manual Update.  If so, we ALWAYS log this occurence.
            if(pData->getQuality() == ManualQuality)
            {
                char tstr[80];
                _snprintf(tstr, sizeof(tstr), "Value set to %.3f", pData->getValue());
                RWCString addn = "Manual Update";

                pSig = CTIDBG_new CtiSignalMsg(point.getID(), pData->getSOE(), tstr, addn);
                if(pSig != NULL)
                {
                    pSig->setUser(pData->getUser());
                    aWrap.getMulti()->insert( pSig );
                    pSig = NULL;
                }
            }


            /*
             *  Check if this pData must be value modified due to reasonability limits.  This must be done first bec. it can alter
             *  the point value passed through to the remaining alarm conditions.
             */
            checkNumericReasonability( pData, aWrap, *pNumeric, pDyn, pSig );

            /*
             *  Check if this pData puts us back into nominal range iff we have _ANY_ pending limit violation going on.
             */
            checkForPendingLimitViolation( pData, *pNumeric );

            for( alarm = 0; alarm < CtiTablePointAlarming::invalidnumericstate; alarm++ )
            {
                pSig = NULL;

                switch(alarm)
                {
                case (CtiTablePointAlarming::rateOfChange):
                    {
                        checkNumericRateOfChange( alarm, pData, aWrap, *pNumeric, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::limit0):
                case (CtiTablePointAlarming::limit1):
                case (CtiTablePointAlarming::limit2):
                case (CtiTablePointAlarming::limit3):
                case (CtiTablePointAlarming::limit4):
                case (CtiTablePointAlarming::limit5):
                case (CtiTablePointAlarming::limit6):
                case (CtiTablePointAlarming::limit7):
                case (CtiTablePointAlarming::limit8):
                case (CtiTablePointAlarming::limit9):
                    {
                        checkNumericLimits( alarm, pData, aWrap, *pNumeric, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::nonUpdatedNumeric):    // alarm generated by commandMsgUpdateFailedHandler().
                    {
                        if(pDyn->getQuality() != NonUpdatedQuality)
                        {
                            deactivatePointAlarm(alarm,aWrap,point,pDyn);
                        }
                    }
                case (CtiTablePointAlarming::highReasonability):    // These conditions must be evaluated prior to the for loop.  The may modify pData.
                case (CtiTablePointAlarming::lowReasonability):     // These conditions must be evaluated prior to the for loop.  The may modify pData.
                default:
                    {
                        break;
                    }
                }

                if(pSig)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    delete pSig;
                    pSig = 0;
                }
            }
        }
    }

    return status;
}

#include <io.h>

INT CtiVanGogh::sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp, const CtiTableContactNotification &cNotif, RWCString subject)
{
    INT status = NORMAL;

    CtiPoint *point = NULL;

    RWCString pointname;
    RWCString devicetext("Unknown");
    RWCString paodescription;
    bool excluded = true;

    CtiLockGuard<CtiMutex> pmguard(server_mux);
    if((point =  PointMgr.getEqual(sig.getId())) != NULL)
    {
        {
            pointname = point->getName();
            devicetext = resolveDeviceName( *point );
            excluded = point->getAlarming().isExcluded(sig.getSignalCategory());
            paodescription = resolveDeviceDescription( point->getDeviceID() );
        }

        // Make sure the ExcludeNotify is 'N'O.
        if(!excluded)
        {
            RWCString error = "\r\n" + sig.getMessageTime().asString() + "\r\n\r\n" + devicetext + " / " + pointname + " reported as " + sig.getText();

            if(!sig.getAdditionalInfo().isNull())
            {
                error += "\r\n\r\n" + sig.getAdditionalInfo();
            }

            if(!grp.getEmailMessage().isNull())
            {
                error += "\r\n\r\n" + grp.getEmailMessage();
            }

            if(subject.isNull())
            {
                subject = grp.getEmailSubject() + ".  " + devicetext + " / " + pointname;
            }

            if(!paodescription.isNull())
            {
                error += "\r\n\r\n" + paodescription;
            }


            SENDMAIL sm;

            sm.lpszHost          = gSMTPServer;                   // Global loaded by ctibase.dll.
            sm.lpszSender        = grp.getEmailFromAddress();
            sm.lpszSenderName    = grp.getGroupName();
            sm.lpszRecipient     = cNotif.getNotification();

            // Need to add to the CtiTableContactNotification the contacts name if we want to set this field
            //            sm.lpszRecipientName = cNotif.getRecipientName();

            sm.lpszReplyTo       = NULL;
            sm.lpszReplyToName   = NULL;
            sm.lpszMessageID     = NULL;
            sm.lpszSubject       = subject;
            sm.lpszMessage       = error.data();
            sm.bLog              = TRUE;

            SendMail( &sm, &status);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** EMAIL ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Couldn't send this message\n" << error << endl;
            }
        }
    }

    return status;
}

INT CtiVanGogh::sendMail(const CtiEmailMsg &aMail, const CtiTableContactNotification &cNotif)
{
    INT status = NORMAL;

    SENDMAIL sm;

    RWCString mailstr = "\r" + aMail.getText() + resolveEmailMsgDescription( aMail );

    sm.lpszHost          = gSMTPServer;                   // Global loaded by ctibase.dll.
    sm.lpszSender        = ( aMail.getSender().isNull() ? gEmailFrom : aMail.getSender() )  ;
    sm.lpszSenderName    = NULL;
    sm.lpszRecipient     = cNotif.getNotification();
    //    sm.lpszRecipientName = recip.getRecipientName();
    sm.lpszReplyTo       = NULL;
    sm.lpszReplyToName   = NULL;
    sm.lpszMessageID     = NULL;
    sm.lpszSubject       = aMail.getSubject();
    sm.lpszMessage       = mailstr.data();
    sm.bLog              = TRUE;

    SendMail(&sm, &status);

    if(status != NORMAL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** EMAIL ERROR **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << " Couldn't send this message\n" << aMail.getText() << endl;
    }

    return status;
}


RWCString CtiVanGogh::getAlarmStateName( INT alarm )
{
    CtiLockGuard<CtiMutex> guard(server_mux);
    if( _alarmToDestInfo[alarm].grpid < SignalEvent )  // Zero is invalid!
    {
        // OK, prime the array!
        loadAlarmToDestinationTranslation();
    }
    RWCString str = _alarmToDestInfo[alarm].name;
    return str;
}


/*----------------------------------------------------------------------------*
 * This method sweeps the table blasting anyone marked as questionable.
 *----------------------------------------------------------------------------*/
int CtiVanGogh::clientPurgeQuestionables(PULONG pDeadClients)
{
    int status = NORMAL;
    CtiConnectionManager *Mgr;

    CtiLockGuard<CtiMutex> server_guard(server_mux);      // Get a lock on it.

    if(MainQueue_.entries() == 0)
    {

        if(pDeadClients != NULL) *pDeadClients = 0;

        while( (Mgr = (CtiConnectionManager *)mConnectionTable.remove(isQuestionable, NULL)) != NULL)
        {
            if(pDeadClients != NULL) (*pDeadClients)++;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Client did not respond to AreYouThere message" << endl;
                dout << RWTime() << "  Client " << Mgr->getClientName() << " on " << Mgr->getPeer() << endl;
            }

            clientShutdown(Mgr);
        }
    }

    return status;
}


int  CtiVanGogh::clientRegistration(CtiConnectionManager *CM)
{
    int         nRet = NoError;
    RWTime      NowTime;
    RWBoolean   validEntry(TRUE);
    RWBoolean   questionedEntry(FALSE);
    RWBoolean   removeMgr(FALSE);
    CtiConnectionManager *Mgr;

    CtiLockGuard<CtiMutex> guard(server_mux);

    if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
    {
        displayConnections();
    }

    RWTPtrHashMultiSetIterator< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >  iter(mConnectionTable);

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << NowTime.now() << " Validating " << CM->getClientName() << " / " << CM->getClientAppId() << " / " << CM->getPeer() << endl;
    }

    /*
     *  OK, now we need to check if the registration jives with our other client connections!
     *  if not, we will not allow this guy to have any operations though us.
     */

    for(;(Mgr = (CtiConnectionManager *)iter());)
    {
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

                        Mgr->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15), 500);   // Ask the old guy to respond to us..
                        CM->setClientRegistered(FALSE);                                         // New guy is not quite kosher yet...

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

        CM->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15), 500);  // Ask the CTIDBG_new guy to blow off..

        clientShutdown(CM);
    }

    if(removeMgr && Mgr != NULL)
    {
        CtiLockGuard<CtiMutex> guard(server_mux);

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
int  CtiVanGogh::clientArbitrationWinner(CtiConnectionManager *CM)
{
    int status = NORMAL;
    CtiConnectionManager *Mgr;

    CtiLockGuard<CtiMutex> guard(server_mux);

    // Now that it is locked, get an iterator
    RWTPtrHashMultiSetIterator< CtiConnectionManager, vg_hash, equal_to<CtiConnectionManager> >  iter(mConnectionTable);

    for(;(Mgr = (CtiConnectionManager *)iter());)
    {
        if((Mgr != CM)                                     &&       // Don't match on yourself
           (Mgr->getClientName() == CM->getClientName())   &&       // Names match
           (Mgr->getClientRegistered() == RWBoolean(FALSE)))        // Other Mgr is not registered completely yet
        {
            RWTime Now;
            // The connection Mgr has been refuted by the prior manager. Shut Mgr down...
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Connection " << Mgr->getClientName() << " on " <<
                Mgr->getPeer() << " has been blocked by a prior client of the same name" << endl;
                dout << Now << " Dispatch will shut it down now." << endl;
            }

            Mgr->WriteConnQue(CTIDBG_new CtiCommandMsg(CtiCommandMsg::Shutdown, 15), 500);  // Ask the CTIDBG_new guy to blow off..

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
        RWCString msgtype;
        switch( pMsg->isA() )
        {
        case MSG_POINTREGISTRATION:
            {
                msgtype = RWCString("MSG_POINTREGISTRATION");
                break;
            }
        case MSG_REGISTER:
            {
                msgtype = RWCString("MSG_REGISTER");
                break;
            }
        case MSG_POINTDATA:
            {
                msgtype = RWCString("MSG_POINTDATA");
                break;
            }
        case MSG_SIGNAL:
            {
                msgtype = RWCString("MSG_SIGNAL");
                break;
            }
        case MSG_DBCHANGE:      // Everyone gets these, no matter what!
            {
                msgtype = RWCString("MSG_DBCHANGE");
                break;
            }
        case MSG_COMMAND:
            {
                msgtype = RWCString("MSG_COMMAND");
                break;
            }
        case MSG_PCRETURN:
        case MSG_MULTI:
            {
                msgtype = RWCString("MSG_PCRETURN/MSG_MULTI");
                break;
            }
        default:
            {
                msgtype = RWCString("UNKNONWN");
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

void CtiVanGogh::doPendingOperations()
{
    try
    {
        if( !_pendingPointInfo.empty() )
        {
            CtiLockGuard<CtiMutex> sguard(server_mux, 1000);

            if(sguard.isAcquired())
            {
                RWTime now;

                // There are pending operations for points out there in the world!
                CtiPendingOpSet_t::iterator it = _pendingPointInfo.begin();

                while( it != _pendingPointInfo.end() )
                {
                    now = now.now();

                    CtiPendingPointOperations &ppo = *it;

                    if(ppo.getType() == CtiPendingPointOperations::pendingLimit)    // we are a limit ppo.
                    {
                        if(now.seconds() > ppo.getTime().seconds() + ppo.getLimitDuration())
                        {
                            CtiSignalMsg *pSig = (CtiSignalMsg*)ppo.getSignal()->replicateMessage();
                            pSig->setMessageTime( ppo.getTime() + ppo.getLimitDuration() );
                            pSig->setMessagePriority( MAXPRIORITY - 1 );

                            MainQueue_.putQueue( pSig );
                            pSig = 0;

                            try
                            {
                                it = _pendingPointInfo.erase(it);
                                dumpPendingOps();
                                continue;   // iterator has been repositioned!
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                    else if(ppo.getType() == CtiPendingPointOperations::pendingControl)
                    {
                        ULONG prevLogSec = ppo.getControl().getPreviousLogTime().seconds();

                        if(ppo.getControlState() == CtiPendingPointOperations::controlPending &&
                           now.seconds() > ppo.getTime().seconds() + (ULONG)ppo.getControlTimeout())
                        {
                            // Post a message about the time out situation.
                            CtiSignalMsg *pOrig = ppo.getSignal();

                            if(pOrig)
                            {
                                CtiSignalMsg *pSig = (CtiSignalMsg*)(pOrig->replicateMessage());
                                pSig->setMessageTime( now );
                                pSig->setMessagePriority( MAXPRIORITY - 1 );

                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "  *****************  CONTROL FAILED **************** " << endl;
                                    pSig->dump();
                                    dout << "  *************************************************** " << endl;
                                }

                                CtiCommandMsg *pCmd = CTIDBG_new CtiCommandMsg(CtiCommandMsg::PointTagAdjust, 15);
                                pCmd->insert(-1);                   // token
                                pCmd->insert(pSig->getId());        // PointID
                                pCmd->insert(0x00000000);           // Tags to set
                                pCmd->insert(TAG_CONTROL_PENDING);  // Tags to reset.
                                MainQueue_.putQueue( pCmd );
                                MainQueue_.putQueue( pSig );
                                pCmd = 0;
                                pSig = 0;
                            }

                            try
                            {
                                it = _pendingPointInfo.erase(it);
                                dumpPendingOps();
                                continue;   // iterator has been repositioned!
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                        else if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
                        {
                            /*
                             *  Order is important here.  Please do not rearrange the else if conditionals.
                             */
                            if( ppo.getControl().getControlDuration() >= 0 &&
                                now.seconds() == ppo.getControl().getStartTime().seconds() + ppo.getControl().getControlDuration())
                            {
                                /*
                                 *  Do NOTHING.  CONTROL IS COMPLETE!
                                 *  delayed data will pick this up...
                                 *  Don't tally any more time though..
                                 */
                                if(gDispatchDebugLevel & 0x00000001)
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        dout << " Start + control duration are < now not gonna write any more..." << endl;
                                    }
                                }
                            }
                            else
                                if(ppo.getControl().getControlDuration() < 0)
                            {
                                /*
                                 *  Do NOTHING.  This is a restore command.
                                 *  delayed data will pick this up...
                                 *  Don't tally any more time though..
                                 */
                                if(gDispatchDebugLevel & 0x00000001)
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        dout << "  Control Duration is less than zero" << endl;
                                    }
                                }
                            }
                            else if(now.seconds() >= prevLogSec - (prevLogSec % CntlHistInterval) + CntlHistInterval)
                            {
                                // We have accumulated enough time against this control to warrant a new log entry!
                                updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::intervalcrossing, now);
                            }
                            else if(CntlHistPointPostInterval && !(now.seconds() % CntlHistPointPostInterval))
                            {
                                // This rate posts history points and stop point.
                                updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::intervalpointpostcrossing, now);
                            }
                            else if(ppo.getControl().getControlDuration() > 0 &&
                                    now.seconds() >= ppo.getControl().getPreviousStopReportTime().seconds() - (ppo.getControl().getPreviousStopReportTime().seconds() % CntlStopInterval) + CntlStopInterval)
                            {
                                // This rate posts stop point.
                                updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::stopintervalcrossing, now);
                            }
                        }
                    }
                    else if(ppo.getType() == CtiPendingPointOperations::pendingPointData)
                    {
                        if(now >= ppo.getTime())
                        {
                            CtiPointDataMsg *pOrig = ppo.getPointData();
                            CtiPointDataMsg *pData = 0;

                            if(pOrig)
                            {
                                pData = (CtiPointDataMsg*)(pOrig->replicateMessage());
                                pData->setMessageTime( pOrig->getTime() );
                                pData->setMessagePriority( MAXPRIORITY - 1 );
                                pData->resetTags( TAG_POINT_DELAYED_UPDATE );

                                updateControlHistory( pData->getId(), CtiPendingPointOperations::delayeddatamessage, pData->getTime() );

                                MainQueue_.putQueue( pData );    // Plop it out there for processing.
                                pData = 0;
                            }

                            it = _pendingPointInfo.erase(it);
                            dumpPendingOps();

                            continue;   // iterator has been repositioned!
                        }
                    }
                    else
                    {
                        it = _pendingPointInfo.erase(it);
                        dumpPendingOps();
                        continue;   // iterator has been repositioned!
                    }

                    it++;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void CtiVanGogh::loadRTDB(bool force, CtiMessage *pMsg)
{
    LONG id = 0;
    RWTime Now;
    static RWTime Refresh(rwEpoch);
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
                dout << Now << " Starting loadRTDB. " << (pChg != 0 ? RWCString(pChg->getCategory() + " DBChange present.") : "No DBChange present.") << endl;
            }

            // This loads up the points that VanGogh will manage.
            if( pChg == NULL || (pChg->getDatabase() == ChangePointDb) )
            {
                CtiLockGuard<CtiMutex> pmguard(server_mux, 10000);
                if(pmguard.isAcquired())
                {
                    if(pChg != NULL && (pChg->getTypeOfChange() == ChangeTypeUpdate || pChg->getTypeOfChange() == ChangeTypeAdd))
                    {
                        PointMgr.refreshList(isPoint, NULL, pChg->getId());
                    }
                    else if(pChg != NULL && pChg->getTypeOfChange() == ChangeTypeDelete)
                    {
                        PointMgr.orphan(pChg->getId());
                    }
                    else
                    {
                        PointMgr.refreshList(isPoint);
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " INFO: Could not acquire server exclusion to perform point reload." << endl;
                }
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << deltaT << " seconds to collect point data from database... " << endl;
            }

#if 0
            Now = Now.now();

            // Make sure any signals are in the DB
            writeSignalsToDB(true);

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << deltaT << " seconds to write signal list to DB" << endl;
            }
#endif
            Now = Now.now();

            // Update our Bookkeeping data!!!
            loadAlarmToDestinationTranslation();

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << deltaT << " seconds to load alarm to destination translations" << endl;
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
                dout << RWTime() << " " << deltaT << " seconds to load state names" << endl;
            }

            if( pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) )
            {
                CtiLockGuard<CtiMutex> guard(server_mux, 5000);

                if(guard.isAcquired())
                {
                    id = ((pChg == NULL) ? 0 : pChg->getId());

                    Now = Now.now();
                    loadDeviceLites(id);
                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << deltaT << " seconds to load lite device data" << endl;
                    }

                    Now = Now.now();
                    adjustDeviceDisableTags(id);

                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << deltaT << " seconds to adjust disabled status of devices" << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " INFO: Device lite info was not reloaded.  Exclusion could not be obtained." << endl;
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
                dout << RWTime() << " " << deltaT << " seconds to load CI Customers" << endl;
            }
            Now = Now.now();

            if(pChg != NULL && (pChg->getDatabase() == ChangeNotificationGroupDb || pChg->getDatabase() == ChangeNotificationRecipientDb))
            {
                CtiLockGuard<CtiMutex> guard(server_mux, 1000);

                while(!guard.isAcquired() && !guard.tryAcquire(5000))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " INFO: Unable to acquire exclusion for notification group load. Will try again." << endl;
                    }
                }

                if(guard.isAcquired())
                {
                    // We will own that mutex following at this point.
                    if(pChg->getDatabase() == ChangeNotificationGroupDb)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Notification Groups will be reloaded on next usage." << endl;
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
                            dout << RWTime() << " Notification Recipients will be reloaded on next usage." << endl;
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
                dout << RWTime() << " " << deltaT << " seconds to adjust notification groups" << endl;
            }
            Now = Now.now();

            refreshCParmGlobals(force);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Done loading RTDB... " << endl;
            }

            Refresh = nextScheduledTimeAlignedOnRate( Now, gDispatchReloadRate );
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if(pChg != NULL)
    {
        delete pChg;
    }
}

/*
 *  returns description of the device which IS this pao.
 */
RWCString CtiVanGogh::resolveDeviceDescription(LONG PAO)
{
    RWCString rStr;

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
RWCString CtiVanGogh::resolveDeviceName(const CtiPointBase &aPoint)
{
    RWCString rStr;
    LONG devid = aPoint.getDeviceID();

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

RWCString CtiVanGogh::resolveDeviceObjectType(const LONG devid)
{
    RWCString rStr;

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

/*
 *  This method loads all pao objects which "could" have control.. This may need change in the future.
 *
 *  Expects protections to be performed by someone else.
 */
void CtiVanGogh::loadDeviceLites(LONG id)
{
    if(DebugLevel & 0x00010000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " Loading DeviceLites " << endl;
    }


    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBDatabase   db       = conn.database();
        RWDBSelector   selector = conn.database().selector();
        RWDBTable      keyTable;
        RWDBReader     rdr;

        /* Go after the system defined points! */
        CtiDeviceBaseLite().getSQL( db, keyTable, selector );

        if(id != 0)
        {
            selector.where( keyTable["paobjectid"] == id && selector.where() );
        }

        rdr = selector.reader(conn);

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
        CtiDeviceLiteSet_t::iterator dnit;
        bool reloadFailed = false;

        for(dnit = _deviceLiteSet.begin(); dnit != _deviceLiteSet.end(); dnit++ )
        {
            CtiDeviceBaseLite &dLite = *dnit;

            if(dLite.getDisableFlag() != "N")
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << dLite.getName() << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if(DebugLevel & 0x00010000)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << RWTime() << " Done Loading DeviceLites " << endl;
    }
}

/*
 *  This method reloads all currently loaded devicelites in memory.
 */
void CtiVanGogh::loadDeviceNames()
{
    CtiLockGuard<CtiMutex> guard(server_mux, 10000);

    if(guard.isAcquired() && !_deviceLiteSet.empty())
    {
        CtiDeviceLiteSet_t::iterator dnit;
        bool reloadFailed = false;

        for(dnit = _deviceLiteSet.begin(); dnit != _deviceLiteSet.end(); dnit++ )
        {
            CtiDeviceBaseLite &dLite = *dnit;
            if(dLite.Restore().errorCode() != RWDBStatus::ok)
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
                dout << RWTime() << " Device Lite Set reset. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
}

void CtiVanGogh::loadCICustomers(LONG id)
{
    CtiLockGuard<CtiMutex> guard(server_mux, 2500);

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
                if(theCust.Restore().errorCode() != RWDBStatus::ok)
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
                    dout << RWTime() << " CI Customer Set has been reset. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " INFO: CI Customer Set was not reloaded. Exclusion oculd not be acquired." << endl;
    }
}

CtiTableContactNotification* CtiVanGogh::getContactNotification(LONG cNotifID)
{
    CtiTableContactNotification* pCNotif = NULL;
    CtiLockGuard<CtiMutex> guard(server_mux);
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
                CtiLockGuard<CtiLogger> guard(dout);
                dout << RWTime() << " Reloading ContactNotification " << pCNotif->getContactNotificationID() << endl;
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
        CtiLockGuard<CtiMutex> guard(server_mux);
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
    CtiLockGuard<CtiMutex> guard(server_mux);

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
                dout << RWTime() << "  Reloading Notification Group " << theGroup.getGroupName() << endl;
            }
            theGroup.Restore();     // Clean the thing then!
        }

        vector<int> recipients = theGroup.getRecipientVector();
        vector<int>::iterator r_iter;

        for(r_iter = recipients.begin(); r_iter != recipients.end(); r_iter++ )
        {
            CtiTableContactNotification *pCNotif;
            int cnotifid = *r_iter;

            {
                CtiLockGuard<CtiMutex> guard(server_mux);
                if( (pCNotif = getContactNotification(cnotifid)) != NULL)
                {
                    //Now we have it ALL!!! send the email
                    sendMail(sig, theGroup, *pCNotif);
                }
            }
        }
    }
}

void CtiVanGogh::sendEmailToGroup(LONG ngid, const CtiEmailMsg& email)
{
    CtiTableNotificationGroup mygroup( ngid );
    CtiLockGuard<CtiMutex> guard(server_mux);

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
                dout << RWTime() << " Reloading Notification Group " << theGroup.getGroupName() << endl;
            }
            theGroup.Restore();     // Clean the thing then!
        }

        vector<int> recipients = theGroup.getRecipientVector();
        vector<int>::iterator r_iter;

        for(r_iter = recipients.begin(); r_iter != recipients.end(); r_iter++ )
        {
            CtiTableContactNotification* pCNotif;
            int cnotifid = *r_iter;

            {
                CtiLockGuard<CtiMutex> guard(server_mux);
                if( (pCNotif = getContactNotification(cnotifid)) != NULL)
                {
                    sendMail(email, *pCNotif);
                }
            }
        }
    }
}

void CtiVanGogh::displayConnections(void)
{
    CtiVanGoghConnectionManager *Mgr = NULL;
    CtiLockGuard<CtiMutex> guard(server_mux);
    CtiServer::iterator  iter(mConnectionTable);

    for(;(Mgr = (CtiVanGoghConnectionManager *)iter());)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ";
        dout << Mgr->getClientName() << " / " <<  Mgr->getClientAppId() << " / " << Mgr->getPeer();
        dout << " " << (Mgr->isViable() ? "is Viable" : "is NOT Viable" ) << endl;
    }
}

/********************
 *  This method uses the dynamic dispatch POINT settings to determine if the inbound setmask and tagmask indicate a change
 *  from the current settings.  If a change is indicated, the static tags of the point will be updated.
 */
bool CtiVanGogh::ablementPoint(CtiPointBase *&pPoint, bool &devicedifferent, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &Multi)
{
    bool different = false;

    devicedifferent = false;        // Make it false by default.

    if(pPoint != NULL)
    {
        CtiSignalMsg *pSig = NULL;

        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

            if(pDyn != NULL)
            {
                UINT currtags  = (pDyn->getDispatch().getTags() & (tagmask & MASK_ANY_DISABLE));        // All (dev & pnt) ablement tags.
                UINT newpttags = (setmask & (tagmask & MASK_ANY_DISABLE));

                UINT currpttags = (currtags & (tagmask & MASK_ANY_POINT_DISABLE));                      // Point only ablement tags.
                UINT pttags   = (setmask & (tagmask & MASK_ANY_POINT_DISABLE));

                UINT currdvtags = (currtags & (tagmask & MASK_ANY_DEVICE_DISABLE));                     // Device only ablement tags.
                UINT dvtags   = (setmask & (tagmask & MASK_ANY_DEVICE_DISABLE));

                if( currtags != newpttags )      // Is anything different?
                {
                    different = true;

                    if(currpttags != pttags)    // Is the difference in the point tags?
                    {
                        if(updatePointStaticTables(pPoint->getPointID(), pttags, tagmask, user, Multi))
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Updated " << pPoint->getName() << "'s point enablement status" << endl;
                        }
                    }

                    if(currdvtags != dvtags)    // Is the difference in the device tags?
                    {
                        devicedifferent = true;
                    }

                    pDyn->getDispatch().resetTags(tagmask);
                    pDyn->getDispatch().setTags(newpttags);

                    {
                        CtiSignalMsg *pTagSig = CTIDBG_new CtiSignalMsg(pPoint->getID(), 0, "Tag Update");
                        pTagSig->setMessagePriority(15);
                        pTagSig->setTags( pDyn->getDispatch().getTags() | TAG_REPORT_MSG_TO_ALARM_CLIENTS);
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return delta;
}

CtiTableCICustomerBase* CtiVanGogh::getCustomer( LONG custid )
{
    CtiTableCICustomerBase *pCustomer = NULL;

    CtiLockGuard<CtiMutex> guard(server_mux);
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

int CtiVanGogh::mail(const CtiEmailMsg &aMail)
{
    int status = NORMAL;

    try
    {
        switch( aMail.getType() )
        {
        case (CtiEmailMsg::CICustomerEmailType):
            {
                CtiLockGuard<CtiMutex> guard(server_mux);

                CtiTableCICustomerBase *pCustomer = getCustomer( aMail.getID() );

                if( pCustomer != NULL )
                {
                    vector<int> recip = pCustomer->getContactNotificationVector();
                    vector<int>::iterator it;

                    try
                    {
                        CtiTableContactNotification *pCNotif = NULL;

                        for(it = recip.begin(); it != recip.end(); ++it)
                        {
                            vector<int>::reference cnotifid = *it;

                            if( (pCNotif = getContactNotification(cnotifid)) != NULL)
                            {
                                // Now we have it ALL!!!! send the email
                                {
                                    CtiLockGuard<CtiLogger> guard(dout);
                                    dout << RWTime() << " Emailing " << cnotifid << endl;
                                }
                                sendMail(aMail, *pCNotif);
                            }
                        }
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                }

                break;
            }
        case (CtiEmailMsg::NGroupIDEmailType):
            {
                sendEmailToGroup( aMail.getID(), aMail );
                break;
            }
        default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

CtiVanGogh::CtiVanGogh()
{
    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        for(int i = 0; i < MAX_ALARM_TRX; i++)
        {
            _alarmToDestInfo[i].grpid = 0; // Zero is invalid!
        }
    }
}

void  CtiVanGogh::shutdown()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch Server Shutting Down " << endl;
    }

    try
    {
        Inherited::shutdown();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

}

void CtiVanGogh::VGRPHWriterThread()
{
    UINT sanity = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch RawPointHistory Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }


    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!bGCtrlC;)
        {
            if(!(++sanity % SANITY_RATE))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch RawPointHistory Writer Thread Active " << endl;
                }
                reportOnThreads();
            }

            rwSleep(1000);

            writeArchiveDataToDB();
        }

        // Make sure no one snuck in under the wire..
        writeArchiveDataToDB(true);
    }
    catch(RWxmsg& msg )
    {
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
        dout << RWTime() << " Dispatch RawPointHistory Writer Thread shutting down" << endl;
    }

    return;
}

void CtiVanGogh::VGDBWriterThread()
{
    UINT sanity = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dispatch DB Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!bGCtrlC;)
        {
            if(!(++sanity % SANITY_RATE))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dispatch DB Writer Thread Active " << endl;
                }
                reportOnThreads();
            }

            rwSleep(1000);

            writeSignalsToDB();
            writeLMControlHistoryToDB();
            writeCommErrorHistoryToDB();
        }

        // Make sure no one snuck in under the wire..
        writeSignalsToDB(true);
        writeLMControlHistoryToDB(true);
        writeCommErrorHistoryToDB(true);
    }
    catch(RWxmsg& msg )
    {
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
        dout << RWTime() << " Dispatch DB Writer Thread shutting down" << endl;
    }

    return;
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
            dout << RWTime() << " An unprocessed message in the queue indicates a non-viable connection." << endl;
            dout << "   " << endl;
        }
        Msg->setConnectionHandle(NULL);
    }
}

CtiVanGogh::CtiDeviceLiteSet_t::iterator CtiVanGogh::deviceLiteFind(const LONG paoId)
{
    CtiDeviceBaseLite &dLite = CtiDeviceBaseLite(paoId);
    CtiLockGuard<CtiMutex> guard(server_mux);

    CtiDeviceLiteSet_t::iterator dliteit = _deviceLiteSet.find( dLite );

    if( dliteit == _deviceLiteSet.end() )
    {
        // We need to load it up, and/or then insert it!
        dLite.Restore();

        pair< CtiDeviceLiteSet_t::iterator, bool > resultpair;

        // Try to insert. Return indicates success.
        resultpair = _deviceLiteSet.insert( dLite );

        if(resultpair.second == true)
        {
            dliteit = resultpair.first;      // Iterator which points to the set entry.
        }
    }

    return dliteit;
}

bool CtiVanGogh::removePointDataFromPending( LONG pID, const CtiPointDataMsg &Data )
{
    bool bRemoved = false;
    bool bRemoveIt = false;

    CtiLockGuard<CtiMutex> guard(server_mux);
    if( !_pendingPointInfo.empty() )
    {
        // There are pending operations for points out there in the world!
        CtiPendingOpSet_t::iterator it = _pendingPointInfo.begin();

        while( it != _pendingPointInfo.end() )
        {
            CtiPendingPointOperations &ppo = *it;

            if(ppo.getPointID() == pID && ppo.getType() == CtiPendingPointOperations::pendingPointData )
            {
                bRemoveIt = true;
            }

            if(bRemoveIt)
            {
                bRemoveIt = false;

                try
                {
                    it = _pendingPointInfo.erase(it);
                    dumpPendingOps();
                    bRemoved = true;
                    continue;
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

            it++;   // And look at the next item in the list.
        }
    }

    return bRemoved;
}

void CtiVanGogh::establishListener()
{
    try
    {
        CtiLockGuard<CtiMutex> guard(server_mux);

        if(Listener != 0)
        {
            _listenerAvailable = FALSE;
            delete Listener;

            Listener = 0;
        }

        NetPort  = RWInetPort(VANGOGHNEXUS);
        NetAddr  = RWInetAddr(NetPort);           // This one for this server!

        Listener = CTIDBG_new RWSocketListener(NetAddr);

        if(!Listener)
        {
            dout << "Could not open socket " << NetAddr << " for listening" << endl;

            exit(-1);
        }

        _listenerAvailable = TRUE;                 // Release the connection handler

    }
    catch(RWxmsg &msg)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Exception " << __FILE__ << " (" << __LINE__ << ") " << msg.why() << endl;
    }
}

void CtiVanGogh::reportOnThreads()
{

    try
    {
        CtiLockGuard<CtiMutex> sguard(server_mux, 1000);

        if(sguard.isAcquired())
        {
            RWThreadFunction &aThr = _rphThread;

            if( !(aThr.isValid() &&
                  aThr.getExecutionState() & RW_THR_ACTIVE  &&
                  aThr.getCompletionState() == RW_THR_PENDING ) )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " DBThread is not running " << endl;
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiVanGogh::writeControlMessageToPIL(LONG deviceid, LONG rawstate, CtiPointStatus *pPoint, const CtiCommandMsg *&Cmd  )
{
    RWCString  cmdstr;
    CtiRequestMsg *pReq = 0;

    if( pPoint->getPointStatus().getStateZeroControl().length() > 0 && pPoint->getPointStatus().getStateOneControl().length() > 0 )
    {
        cmdstr += ((rawstate == UNCONTROLLED) ? pPoint->getPointStatus().getStateZeroControl(): pPoint->getPointStatus().getStateOneControl() );
    }
    else
    {
        cmdstr += RWCString("Control ");
        cmdstr += ResolveStateName(pPoint->getStateGroupID(), rawstate);
    }

    cmdstr += RWCString(" select pointid " + CtiNumStr(pPoint->getPointID()));

    if(pReq = CTIDBG_new CtiRequestMsg( deviceid, cmdstr ))
    {
        pReq->setUser( Cmd->getUser() );
        writeMessageToPIL((CtiMessage*&)pReq);
    }
}

void CtiVanGogh::writeMessageToPIL(CtiMessage *&pReq)
{
    CtiVanGoghConnectionManager *PilCM;
    bool bDone = false;
    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        CtiServer::iterator  iter(mConnectionTable);

        for(;(PilCM = (CtiVanGoghConnectionManager *)iter());)
        {
            if(PilCM->getClientName() == PIL_REGISTRATION_NAME)
            {
                if( PilCM->WriteConnQue( pReq->replicateMessage(), 5000 ) )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Message to PIL was unable to be queued" << endl;
                }

                if(bDone)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Multiple PIL entries in dispatch list." << endl;
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
            dout << RWTime() << " Unable to submit control command.  Port Control Interface not currently" << endl;
            dout << "  registered with dispatch.  Control request discarded" << endl;
            dout << "  ---- Control Request ---- " << endl;
            pReq->dump();
            dout << "  ---- End Control Request ---- " << endl;
        }
    }

    delete pReq;
    pReq = 0;
}

void CtiVanGogh::bumpDeviceToAlternateRate(CtiPointBase *pPoint)
{
    if(!pPoint->isPseudoPoint())
    {
        CtiCommandMsg *pAltRate = CTIDBG_new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );
        if(pAltRate)
        {
            pAltRate->insert(-1); // token
            pAltRate->insert( pPoint->getDeviceID() );
            pAltRate->insert( -1 );                     // Seconds since midnight, or NOW if negative.
            pAltRate->insert( pPoint->getControlExpirationTime() );

            writeMessageToScanner( pAltRate );
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Requesting scans at the alternate scan rate for " << resolveDeviceName( *pPoint ) << endl;
            }
        }
    }
}

void CtiVanGogh::bumpDeviceFromAlternateRate(CtiPointBase *pPoint)
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
                dout << RWTime() << " Requesting scans at the normal scan rate for " << resolveDeviceName( *pPoint ) << endl;
            }
        }
    }
}

void CtiVanGogh::writeMessageToScanner(const CtiCommandMsg *Cmd)
{
    // this guy goes to scanner only
    CtiVanGoghConnectionManager *scannerCM = getScannerConnection();

    if(scannerCM != NULL)
    {
        // pass the message through
        if(scannerCM->WriteConnQue(Cmd->replicateMessage(), 5000))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

/*
 *  Should only be called by doPendingOperation() method!  Any other caller MAY be blocked by the DB etc!!!
 */
void CtiVanGogh::updateControlHistory( long pendid, int cause, const RWTime &thetime, RWTime &now )
{
    CtiLockGuard<CtiMutex> guard(server_mux);
    CtiPendingOpSet_t::iterator it = _pendingPointInfo.find(CtiPendingPointOperations(pendid, CtiPendingPointOperations::pendingControl));

    if( it != _pendingPointInfo.end() )
    {
        CtiPendingPointOperations &ppc = *it;

        switch(cause)
        {
        case (CtiPendingPointOperations::newcontrol):
            {
                /*
                 *  newcontrol record should fully define a control in a way that dispatch can recover from a shutdown
                 *  accross their occurence.
                 */
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
                   ppc.getControl().getControlDuration() != RESTORE_DURATION)                           // This indicates a restore.
                {
                    verifyControlTimesValid(ppc);                                                       // Make sure ppc has been primed.

                    RWTime writetime = ppc.getControl().getStopTime();
                    ppc.getControl().incrementTimes( thetime, 0 );                                      // This effectively primes the entry for the next write.  Critical.
                    ppc.getControl().setStopTime( writetime );
                    ppc.getControl().setControlCompleteTime( writetime );                               // This is when we think this control should complete.

                    ppc.getControl().setActiveRestore( LMAR_NEWCONTROL );                               // Record this as a start interval.

                    insertControlHistoryRow(ppc, now);                                                  // Drop the row in there!
                    postControlStopPoint(ppc,now);                                                      // Let everyone know when control should end.

                    ppc.getControl().setStopTime( thetime );
                    ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );    // Reset to the original completion state.
                }

                break;
            }
        case (CtiPendingPointOperations::intervalcrossing):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        ppc.getControl().incrementTimes( thetime, addnlseconds );

                        // Create some lies
                        ppc.getControl().setActiveRestore( LMAR_LOGTIMER );             // Record this as a start or continue interval.
                        insertControlHistoryRow(ppc, now);
                        postControlHistoryPoints(ppc,now);

                        // OK, set them out for the next run ok. Undo the lies.
                        ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );        // Reset to the original completion state.

                        // insertAndPostControlHistoryPoints(ppc, now, false, false, true);    // Post the AI now that the lies are covered up..
                        postControlStopPoint(ppc,now);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::intervalpointpostcrossing):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        CtiPendingPointOperations temporaryPPC(ppc);
                        temporaryPPC.getControl().incrementTimes( thetime, addnlseconds );
                        postControlHistoryPoints(temporaryPPC, now);
                        postControlStopPoint(temporaryPPC, now);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::stopintervalcrossing):
            {
                // insertAndPostControlHistoryPoints(ppc, now, false, false, true);        // Post countdown AI
                postControlStopPoint(ppc,now);
                break;
            }
        case (CtiPendingPointOperations::repeatcontrol):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
                   ppc.getControl().getControlDuration() != RESTORE_DURATION)     // This indicates a restore.
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.
                        ppc.getControl().incrementTimes( now, addnlseconds );

                        insertControlHistoryRow(ppc, now);
                        postControlHistoryPoints(ppc,now);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::control):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
                   ppc.getControl().getControlDuration() != RESTORE_DURATION)     // This indicates a restore.
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        ppc.getControl().incrementTimes( now, addnlseconds );
                        ppc.setControlState( CtiPendingPointOperations::controlCompleteCommanded );

                        insertControlHistoryRow(ppc, now);
                        postControlHistoryPoints(ppc,now);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::delayeddatamessage):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        ppc.getControl().incrementTimes( now, addnlseconds );
                        ppc.setControlState( CtiPendingPointOperations::controlCompleteTimedIn );

                        insertControlHistoryRow(ppc, now);
                        postControlHistoryPoints(ppc,now);
                        postControlStopPoint(ppc,now);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                break;
            }
        case (CtiPendingPointOperations::datachange):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        ppc.getControl().incrementTimes( now, addnlseconds );
                        ppc.getControl().setActiveRestore( LMAR_MANUAL_RESTORE );
                        ppc.setControlState( CtiPendingPointOperations::controlCompleteManual );

                        insertControlHistoryRow(ppc, now);
                        postControlHistoryPoints(ppc,now);
                        postControlStopPoint(ppc,now);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }
    }
}


void CtiVanGogh::dumpPendingOps(  )
{
    if(gDispatchDebugLevel & DISPATCH_DEBUG_PENDINGOPS)
    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        if( !_pendingPointInfo.empty() )
        {
            // There are pending operations for points out there in the world!
            CtiPendingOpSet_t::iterator it = _pendingPointInfo.begin();

            while( it != _pendingPointInfo.end() )
            {
                CtiPendingPointOperations &ppo = *it;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << endl << " PENDING OPERATION " << endl;

                    if(ppo.getType() == CtiPendingPointOperations::pendingLimit)
                    {
                        dout << "  PENDING LIMIT" << endl;
                        dout << "    Point           " << ppo.getPointID() << endl;
                        dout << "    Type            " << ppo.getType() << endl;
                        dout << "    Time            " << ppo.getTime() << endl;
                        dout << "    Limit           " << ppo.getLimitBeingTimed() << endl;
                        dout << "    Duration        " << ppo.getLimitDuration() << endl;
                    }
                    else if(ppo.getType() == CtiPendingPointOperations::pendingControl)
                    {
                        dout << "  PENDING CONTROL " << endl;
                        dout << "    Point           " << ppo.getPointID() << endl;
                        dout << "    Type            " << ppo.getType() << endl;
                        dout << "    Time            " << ppo.getTime() << endl;
                        dout << "    Ctl State       " << ppo.getControlState() << endl;
                        dout << "    Ctl Timeout     " << ppo.getControlTimeout() << endl;
                        dout << "    CC Value        " << ppo.getControlCompleteValue() << endl;
                    }
                    else if(ppo.getType() == CtiPendingPointOperations::pendingPointData)
                    {
                        dout << "  PENDING DATA " << endl;
                        dout << "    Point           " << ppo.getPointID() << endl;
                        dout << "    Type            " << ppo.getType() << endl;
                        dout << "    Time            " << ppo.getTime() << endl;
                    }
                }

                it++;   // And look at the next item in the list.
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " There are no pending operations available." << endl;
        }
    }

    return;
}

INT CtiVanGogh::updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList)
{
    INT status = NORMAL;
    RWCString objtype = resolveDeviceObjectType(did);

    CtiLockGuard<CtiMutex> smguard(server_mux, 10000);
    if(smguard.isAcquired())
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        {
            // In this case, we poke at the PAO table
            RWDBConnection conn = getConnection();

            RWDBTable yukonPAObjectTable = getDatabase().table("yukonpaobject");
            RWDBUpdater updater = yukonPAObjectTable.updater();

            updater.where( yukonPAObjectTable["paobjectid"] == did );
            updater << yukonPAObjectTable["disableflag"].assign( RWCString((TAG_DISABLE_DEVICE_BY_DEVICE & setmask?'Y':'N')) );
            updater.execute( conn );

            status = (updater.status().isValid() ? NORMAL: UnknownError);
        }


        {
            // In this case, we poke at the base device table.
            RWDBConnection conn = getConnection();

            RWDBTable deviceTable = getDatabase().table("device");
            RWDBUpdater updater = deviceTable.updater();

            updater.where( deviceTable["deviceid"] == did );
            updater << deviceTable["controlinhibit"].assign( RWCString((TAG_DISABLE_CONTROL_BY_DEVICE & setmask?'Y':'N')) );
            updater.execute( conn );

            status = (updater.status().isValid() ? NORMAL: UnknownError);
        }
    }

    CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(did, ChangePAODb, "Device", objtype, ChangeTypeUpdate);
    dbChange->setUser(user);
    dbChange->setSource(DISPATCH_APPLICATION_NAME);
    sigList.insert(dbChange);

    return status;
}

INT CtiVanGogh::updatePointStaticTables(LONG pid, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &Multi)
{
    INT status = NORMAL;

    CtiLockGuard<CtiMutex> smguard(server_mux, 10000);
    if(smguard.isAcquired())
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

        if(TAG_DISABLE_POINT_BY_POINT & tagmask)
        {
            // In this case, we poke at the PAO table
            RWDBConnection conn = getConnection();

            RWDBTable tbl = getDatabase().table("point");
            RWDBUpdater updater = tbl.updater();

            updater.where( tbl["pointid"] == pid );
            updater << tbl["serviceflag"].assign( RWCString((TAG_DISABLE_POINT_BY_POINT & setmask?'Y':'N')) );
            updater.execute( conn );

            status = (updater.status().isValid() ? NORMAL: UnknownError);
        }

        if(TAG_DISABLE_CONTROL_BY_POINT & tagmask)
        {
            // In this case, we poke at the base device table.
            RWDBConnection conn = getConnection();

            RWDBTable tbl = getDatabase().table("pointstatus");
            RWDBUpdater updater = tbl.updater();

            updater.where( tbl["pointid"] == pid );
            updater << tbl["controlinhibit"].assign( RWCString((TAG_DISABLE_CONTROL_BY_POINT & setmask?'Y':'N')) );
            updater.execute( conn );

            status = (updater.status().isValid() ? NORMAL: UnknownError);
        }
    }

    CtiDBChangeMsg* dbChange = CTIDBG_new CtiDBChangeMsg(pid, ChangePointDb, "Point", "Point", ChangeTypeUpdate);
    dbChange->setUser(user);
    dbChange->setSource(DISPATCH_APPLICATION_NAME);
    dbChange->setMessagePriority(15);
    Multi.insert(dbChange);

    return status;
}

/*
 *  This method attempts to set all device "ablement" information.
 *  A key item to remember is that this information _really_ only exists on the _points_ which dispatch tracks.
 *
 */
void CtiVanGogh::adjustDeviceDisableTags(LONG id)
{
    if(!_deviceLiteSet.empty())
    {
        set< long > devicesupdated;

        UINT tagmask = TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_CONTROL_BY_DEVICE;

        {
            CtiMultiMsg *pMulti = CTIDBG_new CtiMultiMsg;

            if(pMulti)
            {
                pMulti->setSource(DISPATCH_APPLICATION_NAME);

                /*
                 *  K.I.S.S.  Loop through each point looking for a mismatch on tags...
                 *  Yes, this sucks less than the alternative.
                 *
                 *  This block looks at each point and re-establishes the device's ablement on it.  MARKING the point
                 *  as disabled for X because of device.
                 */

                CtiPointClientManager::CtiRTDBIterator  itr(PointMgr.getMap());
                for(;itr();)
                {
                    CtiPoint *pPoint = itr.value();

                    if(id != 0 && pPoint->getDeviceID() != id) continue;    // Let's skip devices which DID NOT CHANGE!

                    if(pPoint->getDeviceID() > 0)
                    {
                        CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(pPoint->getDeviceID());

                        if( dliteit != _deviceLiteSet.end() )   // We do know this device..
                        {
                            bool devicedifferent;
                            CtiDeviceBaseLite &dLite = *dliteit;

                            UINT setmask = 0;
                            setmask |= (dLite.getDisableFlag() == "Y" ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                            setmask |= (dLite.getControlInhibitFlag() == "Y" ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                            ablementPoint(pPoint, devicedifferent, setmask, tagmask, DISPATCH_APPLICATION_NAME, *pMulti);

                            if(devicedifferent)
                            {
                                devicesupdated.insert( pPoint->getDeviceID() );  // Relying on the fact that only one may be in there!
                            }
                        }
                    }
                }

                if(!devicesupdated.empty())
                {
                    set< long>::iterator didset;
                    bool reloadFailed = false;

                    for(didset = devicesupdated.begin(); didset != devicesupdated.end(); didset++ )
                    {
                        if(*didset > 0)
                        {
                            CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(*didset);

                            if( dliteit != _deviceLiteSet.end() )   // We do know this device..
                            {
                                CtiDeviceBaseLite &dLite = *dliteit;

                                UINT setmask = 0;

                                setmask |= (dLite.getDisableFlag() == "Y" ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                                setmask |= (dLite.getControlInhibitFlag() == "Y" ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                                if(updateDeviceStaticTables(dLite.getID(), setmask, tagmask, DISPATCH_APPLICATION_NAME, *pMulti))
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }
                                else
                                {
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " Updated " << dLite.getName() << "'s device enablement status" << endl;
                                    }
                                }
                            }
                        }
                    }
                }

                if(pMulti->getData().entries())
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

        RWCString raw("raw");
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        conn.beginTransaction(raw);

        while( conn.isValid() && ( justdoit || (panicCounter < maxrowstowrite) ) && (pTblEntry = _archiverQueue.getQueue(0)) != NULL)
        {
            panicCounter++;
            pTblEntry->Insert(conn);
            delete pTblEntry;
        }

        conn.commitTransaction(raw);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return panicCounter;
}

void CtiVanGogh::verifyControlTimesValid( CtiPendingPointOperations &ppc )
{
    if(!(ppc.getControl().getUpdatedFlag()))  // Have not ever loaded this!
    {
        try
        {
            ppc.getControl().RestoreControlTimes();
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}


RWCString CtiVanGogh::resolveEmailMsgDescription( const CtiEmailMsg &aMail )
{
    RWCString rstr("");

    switch( aMail.getType() )
    {
    case (CtiEmailMsg::DeviceIDEmailType):
        {
            rstr = RWCString("\r\n\r\n") + resolveDeviceDescription( aMail.getID() );
            break;
        }
    case (CtiEmailMsg::CICustomerEmailType):
        {
            CtiLockGuard<CtiMutex> guard(server_mux);
            CtiTableCICustomerBase *pCustomer = getCustomer( aMail.getID() );
            rstr = RWCString("\r\n\r\n") + resolveDeviceDescription( pCustomer->getID() );
            break;
        }
    default:
        {
            break;
        }
    }

    return rstr;
}

int CtiVanGogh::checkNumericReasonability(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    int alarm = NORMAL;
    RWCString text;

    try
    {
        if(pointNumeric.getPointUnits().getHighReasonabilityLimit() != pointNumeric.getPointUnits().getLowReasonabilityLimit() &&       // They must be different.
           pointNumeric.getPointUnits().getHighReasonabilityLimit() >  pointNumeric.getPointUnits().getLowReasonabilityLimit() )
        {
            // Evaluate High Limit
            if(pointNumeric.getPointUnits().getHighReasonabilityLimit() < MAX_HIGH_REASONABILITY)  // Is the reasonability reasonable?
            {
                alarm = CtiTablePointAlarming::highReasonability;
                double val = pData->getValue();

                if(val > pointNumeric.getPointUnits().getHighReasonabilityLimit())
                {
                    pData->setValue( pDyn->getValue() );          // Value of the CtiPointDataMsg must be be modified.
                    pData->setQuality( UnreasonableQuality );

                    if(!_signalManager.isAlarmed(pointNumeric.getID(), CtiTablePointAlarming::highReasonability))
                    {
                        {
                            char tstr[120];
                            _snprintf(tstr, sizeof(tstr), "Reasonability Limit Exceeded High. %.3f > %.3f", val, pointNumeric.getPointUnits().getHighReasonabilityLimit());
                            text = RWCString(tstr);
                        }

                        if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** HIGH REASONABILITY Violation ****  Point: " << pointNumeric.getName() << " " << text << endl;
                        }

                        pSig = CTIDBG_new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), text, getAlarmStateName( pointNumeric.getAlarming().getAlarmCategory(CtiTablePointAlarming::highReasonability) ), GeneralLogType, pointNumeric.getAlarming().getAlarmCategory(CtiTablePointAlarming::highReasonability), pData->getUser());
                    }
                    else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
                    {
                        reactivatePointAlarm(alarm, aWrap, pointNumeric, pDyn);
                    }

                    // This is an alarm if the alarm state indicates anything other than SignalEvent.
                    if(pSig)
                    {
                        tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
                else
                {
                    deactivatePointAlarm(alarm,aWrap,pointNumeric,pDyn);
                }
            }

            if(pointNumeric.getPointUnits().getLowReasonabilityLimit() > MIN_LOW_REASONABILITY)  // Is the reasonability reasonable?
            {
                alarm = CtiTablePointAlarming::lowReasonability;
                double val = pData->getValue();

                if(val < pointNumeric.getPointUnits().getLowReasonabilityLimit())
                {
                    pData->setValue( pDyn->getValue() );          // Value of the CtiPointDataMsg must be be modified.
                    pData->setQuality( UnreasonableQuality );

                    if(!_signalManager.isAlarmed(pointNumeric.getID(), CtiTablePointAlarming::lowReasonability))
                    {
                        {
                            char tstr[120];
                            _snprintf(tstr, sizeof(tstr), "Reasonability Limit Exceeded Low. %.3f < %.3f", val, pointNumeric.getPointUnits().getLowReasonabilityLimit());
                            text = RWCString(tstr);
                        }

                        if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** LOW REASONABILITY Violation ****  Point: " << pointNumeric.getName() << " " << text << endl;
                        }

                        pSig = CTIDBG_new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), text, getAlarmStateName( pointNumeric.getAlarming().getAlarmCategory(CtiTablePointAlarming::lowReasonability) ), GeneralLogType, pointNumeric.getAlarming().getAlarmCategory(CtiTablePointAlarming::lowReasonability), pData->getUser());
                    }
                    else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
                    {
                        reactivatePointAlarm(alarm, aWrap, pointNumeric, pDyn);
                    }

                    // This is an alarm if the alarm state indicates anything other than SignalEvent.
                    if(pSig)
                    {
                        tagSignalAsAlarm(pointNumeric, pSig, CtiTablePointAlarming::lowReasonability, pData);
                        aWrap.getMulti()->insert( pSig );
                        pSig = NULL;
                    }
                }
                else
                {
                    deactivatePointAlarm(alarm,aWrap,pointNumeric,pDyn);
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return alarm;
}

void CtiVanGogh::checkNumericRateOfChange(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    bool balarm = false;

    if(pointNumeric.getRateOfChange() >= 0)
    {
        double curval = pDyn->getDispatch().getValue();
        double delta = curval * (double) pointNumeric.getRateOfChange() / 100.0;

        char tstr[80];

        if( pData->getValue() < curval - delta )
        {
            // We've lost too much too fast.
            _snprintf(tstr, sizeof(tstr), "ROC - Value decreased > %d%% from %.3f", pointNumeric.getRateOfChange(), curval);
            balarm = true;
        }
        else if( pData->getValue() > curval + delta )
        {
            // We've gained too much too fast.
            _snprintf(tstr, sizeof(tstr), "ROC - Value increased > %d%% from %.3f", pointNumeric.getRateOfChange(), curval);
            balarm = true;
        }

        if(balarm)
        {
            if(!_signalManager.isAlarmed(pointNumeric.getID(), alarm))
            {
                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** RATE OF CHANGE Violation **** Point: " << pointNumeric.getID() << " " << tstr << endl;
                }

                // OK, we have an actual alarm condition to gripe about!
                pSig = CTIDBG_new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), tstr, getAlarmStateName( pointNumeric.getAlarming().getAlarmCategory(alarm) ), GeneralLogType, pointNumeric.getAlarming().getAlarmCategory(alarm), pData->getUser());

                if(pSig)
                {
                    tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);
                    aWrap.getMulti()->insert( pSig );
                    pSig = 0;
                }
            }
            else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
            {
                reactivatePointAlarm(alarm, aWrap, pointNumeric, pDyn);
            }
        }
        else
        {
            deactivatePointAlarm(alarm,aWrap,pointNumeric,pDyn);
        }
    }

    return;
}

/*
 *  Every point data has the opportunity to drive the value back into the nominal condition.
 *  This method ensures that a pending limit (one which is waiting for n seconds before alarming)
 *   is canceled if the point data which is in process drives us within limit.
 */
void CtiVanGogh::checkForPendingLimitViolation( CtiPointDataMsg *pData, CtiPointNumeric &pointNumeric )
{
    bool bRemoved = false;

    CtiLockGuard<CtiMutex> guard(server_mux);

    if( !_pendingPointInfo.empty() )
    {
        // There are pending operations for points out there in the world!
        CtiPendingOpSet_t::iterator it = _pendingPointInfo.begin();

        while( it != _pendingPointInfo.end() )
        {
            CtiPendingPointOperations &ppo = *it;

            if(ppo.getType() == CtiPendingPointOperations::pendingLimit && ppo.getPointID() == pointNumeric.getID() )
            {
                INT exceeds = LIMIT_IN_RANGE;

                if(!(pointNumeric.limitStateCheck(ppo.getLimitBeingTimed(), pData->getValue(), exceeds)))
                {
                    try
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** LIMIT Violation ****  Point: " << pointNumeric.getName() << " returned to nominal. Limit " << ppo.getLimitBeingTimed() + 1 << " pending operation deleted." << endl;
                        }

                        it = _pendingPointInfo.erase(it);
                        dumpPendingOps();
                        bRemoved = true;

                        continue;
                    }
                    catch(...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            }
            it++;   // And look at the next item in the list.
        }
    }
}

void CtiVanGogh::checkNumericLimits(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    RWCString text;

    double  val = pData->getValue();
    INT     statelimit = (alarm - CtiTablePointAlarming::limit0);
    INT     exceeds = LIMIT_IN_RANGE;

    try
    {
        if(pointNumeric.limitStateCheck(statelimit, val, exceeds))
        {
            if(!_signalManager.isAlarmed(pointNumeric.getID(), alarm))
            {
                INT duration = pointNumeric.getLimit(statelimit).getLimitDuration();

                if(exceeds == LIMIT_EXCEEDS_LO )
                {
                    char tstr[120];
                    _snprintf(tstr, sizeof(tstr), "Limit %d Exceeded Low. %.3f < %.3f", statelimit+1, val, pointNumeric.getLowLimit(statelimit));
                    text = RWCString(tstr);
                }
                else if(exceeds == LIMIT_EXCEEDS_HI)
                {
                    char tstr[120];
                    _snprintf(tstr, sizeof(tstr), "Limit %d Exceeded High. %.3f > %.3f", statelimit+1, val, pointNumeric.getHighLimit(statelimit));
                    text = RWCString(tstr);
                }
                else if(exceeds == LIMIT_SETUP_ERROR)
                {
                    char tstr[120];
                    _snprintf(tstr, sizeof(tstr), "Limit %d Invalid Setup. Is %.3f < %.3f < %.3f?", statelimit+1, pointNumeric.getLowLimit(statelimit), val, pointNumeric.getHighLimit(statelimit));
                    text = RWCString(tstr);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }


                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** LIMIT Violation ****  Point: " << pointNumeric.getName() << " " << text << endl;
                }

                pSig = CTIDBG_new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), text, getAlarmStateName( pointNumeric.getAlarming().getAlarmCategory(alarm) ), GeneralLogType, pointNumeric.getAlarming().getAlarmCategory(alarm), pData->getUser());

                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);

                if(duration > 0)  // Am I required to hold in this state for a bit before the announcement of this condition?
                {
                    CtiPendingPointOperations pendingPointLimit(pointNumeric.getID());
                    pendingPointLimit.setType(CtiPendingPointOperations::pendingLimit);
                    pendingPointLimit.setLimitBeingTimed( statelimit );
                    pendingPointLimit.setTime( RWTime() );
                    pendingPointLimit.setLimitDuration( duration );
                    pendingPointLimit.setSignal( pSig );
                    pSig = NULL;   // Don't let it get put in the Wrapper because it is now in the pending list!

                    // If there is a limit duration, we modify the data message, so clients don't immediately know that this point is in a pending alarm.
                    pData->resetTags( TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM );

                    {
                        CtiLockGuard<CtiMutex> guard(server_mux);
                        pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                        resultpair = _pendingPointInfo.insert( pendingPointLimit );            // Add to the pending operations.

                        if(resultpair.second != true)
                        {
                            CtiPendingPointOperations &ppl = *resultpair.first;

                            if(ppl.getLimitBeingTimed() > pendingPointLimit.getLimitBeingTimed() )
                            {
                                ppl = pendingPointLimit;
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** LIMIT Violation ****  Point: " << pointNumeric.getName() << " delayed violation " << ppl.getLimitBeingTimed() + 1 << " preempted. Limit " << statelimit+1 << " pending alarm." << endl;
                                }
                            }
                        }
                        else
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** LIMIT Violation ****  Point: " << pointNumeric.getName() << " delayed (" << duration << ") violation. Limit " << statelimit+1 << " pending alarm." << endl;
                        }
                    }
                }
            }
            else if(!_signalManager.isAlarmActive(pointNumeric.getID(), alarm))
            {
                reactivatePointAlarm(alarm, aWrap, pointNumeric, pDyn);
            }
        }
        else
        {
            deactivatePointAlarm(alarm,aWrap,pointNumeric,pDyn);
        }

        if(pSig)
        {
            tagSignalAsAlarm(pointNumeric, pSig, alarm, pData);
            aWrap.getMulti()->insert( pSig );
            pSig = 0;
        }

    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiVanGogh::checkStatusUCOS(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    UINT tags = pDyn->getDispatch().getTags();

    if((tags & TAG_ATTRIB_CONTROL_AVAILABLE))
    {
        if(!(tags & TAG_CONTROL_PENDING))
        {
            // Well, we were NOT expecting a change, so make sure the values match
            if(pDyn->getDispatch().getValue() != pData->getValue())
            {
                // Values don't match and we weren't expecting a change!
                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** UNCOMMANDEDSTATECHANGE **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                // OK, we have an actual alarm condition to gripe about!
                pSig = CTIDBG_new CtiSignalMsg(point.getID(), pData->getSOE(), RWCString( "UCOS: " + ResolveStateName(point.getStateGroupID(), (int)pData->getValue())), getAlarmStateName( point.getAlarming().getAlarmCategory(alarm) ), GeneralLogType, point.getAlarming().getAlarmCategory(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.

                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                tagSignalAsAlarm(point, pSig, alarm, pData);

                pSig->resetTags( TAG_ACTIVE_ALARM );
                pDyn->getDispatch().resetTags( MASK_ANY_ALARM );
                pDyn->getDispatch().setTags( _signalManager.getAlarmMask(point.getID()) );
            }
        }
    }
}

void CtiVanGogh::checkStatusCommandFail(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    UINT tags = pDyn->getDispatch().getTags();

    // We can only care about failure if we are a status/control point
    // and someone has sent out a command.  Otherwise this is irrelevant.
    if(tags & TAG_ATTRIB_CONTROL_AVAILABLE)
    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        CtiPendingOpSet_t::iterator it = _pendingPointInfo.find(CtiPendingPointOperations(point.getPointID(), CtiPendingPointOperations::pendingControl));

        if( it != _pendingPointInfo.end() )
        {
            // OK, we just got a change in value on a Status type point, and it is awaiting control!
            CtiPendingPointOperations &ppo = *it;

            if( pData->getValue() == ppo.getControlCompleteValue() )  // We are in the control state (value)?
            {
                if(tags & TAG_CONTROL_PENDING)                                          // Are we still awaiting the start of control?
                {
                    if(ppo.getControl().getStartTime() == RWTime(YUKONEOT) )
                    {
                        ppo.getControl().setStartTime( pData->getTime() );              // Arrival of this point data indicates a control start, no longer pending!
                    }

                    ppo.setSignal(0);                                                   // No longer need to send any error signal.

                    pDyn->getDispatch().resetTags( TAG_CONTROL_PENDING );               // We got to the desired state, no longer pending.. we are now controlling!

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << resolveDeviceName(point) << " / " << point.getName() << " has gone CONTROL COMPLETE." << endl;
                    }

                    if(ppo.getControlState() != CtiPendingPointOperations::controlInProgress)
                    {
                        ppo.setControlState(CtiPendingPointOperations::controlInProgress);  // control has begun!
                        updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::newcontrol, pData->getTime() );
                    }
                }
            }
            else
            {
                // No longer in the controlcomplete state... because of a pointdata (Manual Change???)
                updateControlHistory( pData->getId(), CtiPendingPointOperations::datachange, pData->getTime() );
            }
        }
    }
}


void CtiVanGogh::checkStatusState(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    RWCString action;
    double val = pData->getValue();
    INT statelimit = (alarm - CtiTablePointAlarming::state0);

    // Value or quality must have been changed to look at this stuff again!
    if( (pDyn->getValue() != pData->getValue() || pDyn->getQuality() != pData->getQuality()))
    {
        INT exceeds = LIMIT_IN_RANGE;

        if(point.limitStateCheck(statelimit, val, exceeds))
        {
            if(!_signalManager.isAlarmed(point.getID(), alarm))
            {
                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** STATE Violation **** \n" <<
                    "   Point: " << point.getID() << " " << ResolveStateName(point.getStateGroupID(), (int)pData->getValue()) << endl;
                }

                char tstr[80];
                _snprintf(tstr, sizeof(tstr)-1, "%s", ResolveStateName(point.getStateGroupID(), (int)pData->getValue()));

                // OK, we have an actual alarm condition to gripe about!
                pSig = CTIDBG_new CtiSignalMsg(point.getID(), pData->getSOE(), tstr, getAlarmStateName( point.getAlarming().getAlarmCategory(alarm) ), GeneralLogType, point.getAlarming().getAlarmCategory(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                tagSignalAsAlarm(point, pSig, alarm, pData);
            }
            else if(!_signalManager.isAlarmActive(point.getID(), alarm))
            {
                reactivatePointAlarm(alarm,aWrap,point,pDyn);
            }
        }
        else
        {
            deactivatePointAlarm(alarm,aWrap,point,pDyn);
        }
    }
}

void CtiVanGogh::tagSignalAsAlarm( CtiPointBase &point, CtiSignalMsg *&pSig, int alarm, CtiPointDataMsg *pData )
{
    // If pSig is non-NULL, this "alarm" condition occurred and we need to decide if the point goes into alarm over it.
    if(pSig != NULL)
    {
        // We now need to check if this "alarm" is a real alarm ( > SignalEvent level )
        if(point.getAlarming().getAlarmCategory(alarm) > SignalEvent)
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

            if(pDyn)
                pDyn->getDispatch().setTags(TAG_ACTIVE_ALARM | TAG_UNACKNOWLEDGED_ALARM);

            pSig->setTags(pDyn->getDispatch().getTags());   // They are equal here!
            pSig->setLogType(AlarmCategoryLogType);
            pSig->setCondition(alarm);
        }

        if(pData)    // If is "pushed" into the alarm condition, let's label it that way.
        {
            pSig->setUser(pData->getUser());

            if(pData->getQuality() == ManualQuality)
                pSig->setAdditionalInfo("Manual Update: " + pSig->getAdditionalInfo());
        }
    }
}

void CtiVanGogh::pruneCommErrorHistory()
{
    RWDate earliestDate = RWDate() - gCommErrorDays;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Comm Error History log is being pruned back to " << earliestDate << endl;
    }

    CtiTableCommErrorHistory::Prune(earliestDate);
}


bool CtiVanGogh::addToPendingSet(CtiPendingPointOperations &pendingControlRequest, RWTime &updatetime)
{
    bool bRet = true;

    CtiLockGuard<CtiMutex> guard(server_mux);
    pair< CtiPendingOpSet_t::iterator, bool > resultpair;
    resultpair = _pendingPointInfo.insert( pendingControlRequest );            // Add to the pending operations.

    if(resultpair.second != true)
    {
        CtiPendingPointOperations &ppo = *resultpair.first;

        /*
         *  We only need to "do something" if the earlier control had gone into the controlInProgress state.
         *  Otherwise it is waiting or completed and left over.
         */
        if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
        {
            if( pendingControlRequest.getControl().getControlDuration() <= 0 )
            {
                // The incoming control information is a RESTORE type operation.
                ppo.getControl().setActiveRestore(LMAR_MANUAL_RESTORE);
                updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::control, updatetime );
            }
            else if( pendingControlRequest.getControl().getControlType() == ppo.getControl().getControlType() )
            {
                // The control command we just received is the same command as that which started the prior control.
                // This is a repeat/continuation of the old command!  We just record that and continue.
                ppo.getControl().setActiveRestore(LMAR_CONT_CONTROL);
                updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::repeatcontrol, updatetime );

                pendingControlRequest.getControl().setStartTime(ppo.getControl().getStartTime());
                pendingControlRequest.getControl().setPreviousLogTime(ppo.getControl().getPreviousLogTime());
                pendingControlRequest.getControl().setNotNewControl();
            }
            else
            {
                // A new and different control command has arrived.  We need to record the old command as having
                // been overridden.
                ppo.getControl().setActiveRestore(LMAR_OVERRIDE_CONTROL);
                updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::control, updatetime );
            }
        }

        ppo = pendingControlRequest;    // Copy it to update the control state.
    }

    return bRet;
}


void CtiVanGogh::insertControlHistoryRow( CtiPendingPointOperations &ppc, const RWTime &now)
{
    ppc.getControl().Insert();
    return;
}

void CtiVanGogh::postControlHistoryPoints( CtiPendingPointOperations &ppc, const RWTime &now)
{
    int poff;

    {
        CtiPointNumeric *pPoint = 0;
        double ctltime;

        for(poff = DAILYCONTROLHISTOFFSET; poff <= ANNUALCONTROLHISTOFFSET; poff++ )
        {
            pPoint = (CtiPointNumeric *)PointMgr.getOffsetTypeEqual( ppc.getControl().getPAOID(), poff, AnalogPointType );

            if(pPoint != 0)
            {
                if(poff == DAILYCONTROLHISTOFFSET)
                {
                    ctltime = pPoint->computeValueForUOM((double)ppc.getControl().getCurrentDailyTime());
                }
                else if(poff == MONTHLYCONTROLHISTOFFSET)
                {
                    ctltime = pPoint->computeValueForUOM((double)ppc.getControl().getCurrentMonthlyTime());
                }
                else if(poff == SEASONALCONTROLHISTOFFSET)
                {
                    ctltime = pPoint->computeValueForUOM((double)ppc.getControl().getCurrentSeasonalTime());
                }
                else if(poff == ANNUALCONTROLHISTOFFSET)
                {
                    ctltime = pPoint->computeValueForUOM((double)ppc.getControl().getCurrentAnnualTime());
                }

                MainQueue_.putQueue( CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), ctltime, NormalQuality, pPoint->getType(), pPoint->getName() + " control history"));
            }
        }
    }

    return;
}

void CtiVanGogh::postControlStopPoint( CtiPendingPointOperations &ppc, const RWTime &now)
{
    int poff;

    CtiPointBase *pPt = PointMgr.getEqual(ppc.getPointID());

    if(pPt != 0)
    {
        CtiPointNumeric *pPoint = 0;

        {
            if(ppc.getControl().getControlDuration() > 0 &&
               ((now.seconds() >= ppc.getControl().getPreviousStopReportTime().seconds() - (ppc.getControl().getPreviousStopReportTime().seconds() % CntlStopInterval) + CntlStopInterval) ||
                (now > ppc.getControl().getStartTime() + ppc.getControl().getControlDuration())) )
            {
                // We want to post to the analog which records seconds until control STOPS.
                ULONG remainingseconds = 0;

                if( ppc.getControl().getControlCompleteTime() > now )
                {
                    remainingseconds = ppc.getControl().getControlCompleteTime().seconds() - now.seconds();
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Control stops at " << stoptime << endl;
                    dout << "  There are " << remainingseconds << " seconds = " << remainingseconds/60 << " minutes remaining" << endl;
                }
#endif

                if((0 != (pPoint = (CtiPointNumeric *)PointMgr.getOffsetTypeEqual( pPt->getDeviceID(), CONTROLSTOPCOUNTDOWNOFFSET, AnalogPointType))))
                {
                    double ai = pPoint->computeValueForUOM((double)remainingseconds);
                    MainQueue_.putQueue( CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), ai, NormalQuality, pPoint->getType(), pPoint->getName() + " control remaining"));
                }

                ppc.getControl().setPreviousStopReportTime(now);
            }
        }
    }
    return;
}

void CtiVanGogh::deactivatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *&pDyn )
{
    CtiSignalMsg *pSigActive = _signalManager.setAlarmActive(point.getID(), alarm, false);

    if(pSigActive != NULL)
    {
        pDyn->getDispatch().resetTags( MASK_ANY_ALARM );
        pDyn->getDispatch().setTags( _signalManager.getAlarmMask(point.getID()) );

        pSigActive->setTags( (pDyn->getDispatch().getTags() & ~MASK_ANY_ALARM) | TAG_REPORT_MSG_TO_ALARM_CLIENTS);
        pSigActive->setText(AlarmTagsToString(pSigActive->getTags()) + pSigActive->getText());
        pSigActive->setMessageTime( RWTime() );

        aWrap.getMulti()->insert( pSigActive );
        pSigActive = 0;
    }
}

void CtiVanGogh::reactivatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *&pDyn )
{
    CtiSignalMsg *pSigActive = _signalManager.setAlarmActive(point.getID(), alarm, true);

    if(pSigActive != NULL)
    {
        pDyn->getDispatch().resetTags( MASK_ANY_ALARM );
        pDyn->getDispatch().setTags( _signalManager.getAlarmMask(point.getID()) );

        pSigActive->setTags( (pDyn->getDispatch().getTags() & ~MASK_ANY_ALARM) | TAG_REPORT_MSG_TO_ALARM_CLIENTS );
        pSigActive->setText(AlarmTagsToString(pSigActive->getTags()) + pSigActive->getText());
        pSigActive->setMessageTime( RWTime() );

        aWrap.getMulti()->insert( pSigActive );
        pSigActive = 0;
    }
}


