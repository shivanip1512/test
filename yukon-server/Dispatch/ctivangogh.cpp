
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

/*-----------------------------------------------------------------------------*
*
* File:   ctivangogh
*
* Date:   6/26/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/ctivangogh.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2002/06/10 22:29:23 $
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


#define DEBUGPOINTCHANGES

#define LMCTLHIST_WINDOW         30             // How often partial LM control intervals are written out to DB.
#define MAX_ARCHIVER_ENTRIES     10             // If this many entries appear, we'll do a dump
#define DUMP_RATE                30             // Otherwise, do a dump evey this many seconds
#define CONFRONT_RATE            300            // Ask every client to post once per 5 minutes or be terminated
#define UPDATERTDB_RATE          300            // Save all dirty point records once per n seconds
#define SANITY_RATE              300

DLLIMPORT extern CtiLogger   dout;              // From proclog.dll

DLLEXPORT BOOL  bGCtrlC = FALSE;

/* Global Variables */
CtiPointClientManager      PointMgr;  // The RTDB for memory points....
CtiVanGoghExecutorFactory  ExecFactory;

static const RWTime MAXTime(YUKONEOT);
static int CntlHistInterval = 3600;
static int CntlStopInterval = 60;

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
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }


                            if(ConnThread_.getCompletionState() != RW_THR_PENDING)
                            {
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
             *  This next bit blocks on a connect and creates a new
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
                    dout << RWTime() << " Dispatch Connection Handler Thread Active " << endl;
                }
            }
            else
            {
                bQuit = TRUE;
                continue; // the for loop
            }

            {
                CtiLockGuard<CtiMutex> guard(server_mux);

                XChg                                = new CtiExchange(sock);
                CtiVanGoghConnectionManager *ConMan = new CtiVanGoghConnectionManager(XChg, &MainQueue_);

                ConMan->setBlockingWrites(TRUE);    // Writes must be blocking into the main queue

#if 1       // 041802 CGP.

                clientConnect( ConMan );
                ConMan->ThreadInitiate();     // Kick off the connection's communication threads.

                if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " New connection established" << endl;
                }

#else
                /*
                 *  Need to inform VGMain of the "New Guy" so that he may control its destiny from
                 *  now on.
                 */

                CmdMsg = new CtiCommandMsg(CtiCommandMsg::NewClient, 15);

                if(CmdMsg != NULL)
                {
                    CmdMsg->setConnectionHandle((void*) ConMan);    // Mark it so MainThread knows who to respond to
                    MainQueue_.putQueue(CmdMsg);
                    ConMan->ThreadInitiate();                       // Kick off the connection's communication threads.

                    if(gDispatchDebugLevel & DISPATCH_DEBUG_CONNECTIONS)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " New connection established" << endl;
                    }
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " ERROR Starting new connection! " << rwThreadId() << endl;
                }
#endif
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
        dout << RWTime() << " VGCThread: " << rwThreadId() << " is terminating... " << endl;
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

int  CtiVanGogh::commandMsgHandler(const CtiCommandMsg *Cmd)
{
    int status = NORMAL;
    int i;
    int pid;

    CtiPoint       *pPt;
    CtiTableSignal *pSig;

    switch( Cmd->getOperation() )
    {
    case (CtiCommandMsg::ClearAlarm):
        {
            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMACK)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** CLEAR RECEIVED **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                Cmd->dump();
            }

            if(Cmd->getOpArgList().entries() > 0)
            {
                CtiLockGuard<CtiMutex> pmguard(server_mux);

                for(i = 0; i < Cmd->getOpArgList().entries(); i++)
                {
                    pid   = Cmd->getOpArgList().at(i);
                    pPt   = PointMgr.getMap().findValue( &CtiHashKey(pid) );

                    if(pPt != NULL)      // I know about the point...
                    {
                        CtiSignalMsg *pSigNew = NULL;

                        {
                            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();

                            if(pDyn != NULL)
                            {
                                if(pDyn->getDispatch().getTags() & MASK_ANY_ALARM)
                                {
                                    if(pDyn->getDispatch().isDirty())
                                    {
                                        pDyn->getDispatch().Update();
                                    }

                                    pDyn->getDispatch().setDirty();
                                    pDyn->getDispatch().resetTags( MASK_ANY_ALARM );
                                    pDyn->setLastSignal(-1);

                                    pSig = _signalsPending.getMap().findValue( &CtiHashKey(pid) );

                                    if(pSig != NULL)
                                    {
                                        // Message is no longer reportable to any clients...

                                        pSigNew = new CtiSignalMsg(pSig->getPointID(),
                                                                   pSig->getSOE(),
                                                                   pSig->getText(),
                                                                   pSig->getAdditionalInfo(),
                                                                   pSig->getLogType(),
                                                                   pSig->getPriority(),
                                                                   Cmd->getUser(),
                                                                   pDyn->getDispatch().getTags() | TAG_REPORT_MSG_TO_ALARM_CLIENTS);


                                        if(pSigNew != NULL)
                                        {
                                            RWCString text("ALM CLR: ");
                                            text += pSig->getText();

                                            pSigNew->setText(text);

                                            if( !pPt->getAlarming().getNotifyOnAcknowledge() )
                                            {
                                                pSigNew->setSignalGroup(SignalEvent);    // Don't want this to be another alarm..
                                            }
                                        }

                                        /*
                                         *  Since this error has been cleared, we need to get it out of the list of pending alarms
                                         */
                                        CtiHashKey *pKey = _signalsPending.getMap().remove( &CtiHashKey(pid) );
                                        delete pSig;
                                        pSig = NULL;

                                        if(pKey != NULL)     // 'Twas removed!
                                        {
                                            delete pKey;
                                        }
                                    }
                                }
                            }
                        }

                        if(pSigNew != NULL)
                        {
                            // Make sure that anyone who cared about the first one gets the new state of the tag!
                            postMessageToClients(pSigNew);
                            _signalMsgQueue.putQueue(pSigNew);
                        }
                    }
                }
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

            for(i = 0; i < Cmd->getOpArgList().entries(); i++)
            {
                pid   = Cmd->getOpArgList().at(i);

                pPt   = PointMgr.getMap().findValue( &CtiHashKey(pid) );
                pSig  = _signalsPending.getMap().findValue(&CtiHashKey(pid));

                if(pPt != NULL)      // I know about the point...
                {
                    CtiSignalMsg *pSigNew = NULL;

                    {
                        CtiLockGuard<CtiMutex> pmguard(server_mux);
                        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPt->getDynamic();

                        if(pDyn != NULL)
                        {
                            if(pDyn->getDispatch().getTags() & TAG_UNACKNOWLEDGED_ALARM)
                            {
                                if(pDyn->getDispatch().isDirty()) // Oh, someone has already set this, make sure it gets written.
                                {
                                    pDyn->getDispatch().Update();
                                }

                                pDyn->getDispatch().setDirty();
                                pDyn->getDispatch().resetTags( TAG_UNACKNOWLEDGED_ALARM );

                                pSig = _signalsPending.getMap().findValue(&CtiHashKey(pid));

                                if(pSig != NULL)
                                {
                                    // Message is no longer reportable to any clients...
                                    pSigNew = new CtiSignalMsg(pSig->getPointID(),
                                                               pSig->getSOE(),
                                                               pSig->getText(),
                                                               pSig->getAdditionalInfo(),
                                                               pSig->getLogType(),
                                                               pSig->getPriority(),
                                                               Cmd->getUser(),
                                                               pDyn->getDispatch().getTags() | TAG_REPORT_MSG_TO_ALARM_CLIENTS);

                                    if(pSigNew != NULL)
                                    {
                                        RWCString text("ALM ACK: ");
                                        text += pSig->getText();

                                        pSigNew->setText(text);

                                        if( !pPt->getAlarming().getNotifyOnAcknowledge() )
                                        {
                                            pSigNew->setSignalGroup(SignalEvent);    // Don't want this to be another alarm..
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(pSigNew != NULL)
                    {
                        // Make sure that anyone who cared about the first one gets the new state of the tag!
                        postMessageToClients(pSigNew);
                        _signalMsgQueue.putQueue(pSigNew);
                    }
                }
            }

            break;
        }
    case (CtiCommandMsg::ControlRequest):
        {
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
                        CtiLockGuard<CtiMutex> pmguard(server_mux);


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
                            pPoint = PointMgr.getMap().findValue( &CtiHashKey(pid) );
                        }

                        if(pPoint != NULL)
                        {
                            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

                            if(pDyn != NULL
                               && pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE
                               && !(pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_POINT ||
                                    pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_DEVICE))
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

                                CtiSignalMsg *pFailSig = new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), "Control " + resolveStateName(*pPoint, rawstate) + " Failed", getAlarmStateName( pPoint->getAlarming().getAlarmStates(CtiTablePointAlarming::commandFailure) ), GeneralLogType, pPoint->getAlarming().getAlarmStates(CtiTablePointAlarming::commandFailure), Cmd->getUser());

                                pendingControlRequest.setSignal( pFailSig );

                                {
                                    CtiLockGuard<CtiMutex> guard(server_mux);
                                    pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                                    resultpair = _pendingPointInfo.insert( pendingControlRequest );            // Add to the pending operations.

                                    if(resultpair.second != true)
                                    {
                                        CtiPendingPointOperations &ppo = *resultpair.first;

                                        if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
                                        {
                                            // We've had a collision and must tally the partial interval!
                                            {
                                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                dout << RWTime() << " Partial control interval must be adjusted. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                            }

                                            if( rawstate != UNCONTROLLED )
                                            {
                                                {
                                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                                }
                                                ppo.getControl().setActiveRestore(LMAR_CONT_CONTROL);
                                                pendingControlRequest.getControl().setStartTime(ppo.getControl().getStartTime());
                                                updateControlHistory( pPoint->getPointID(), CtiPendingPointOperations::newcontrol );
                                                pendingControlRequest.getControl().setPreviousLogTime(ppo.getControl().getPreviousLogTime());
                                                pendingControlRequest.getControl().setNotNewControl();
                                            }
                                            else
                                            {
                                                ppo.getControl().setActiveRestore(LMAR_OVERRIDE_CONTROL);         // It is a new
                                                updateControlHistory( pPoint->getPointID(), CtiPendingPointOperations::newcontrol );
                                            }

                                        }

                                        ppo = pendingControlRequest;    // Copy it to update the control state.
                                    }

                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " " << resolveDeviceName(*pPoint) << " / " << pPoint->getName() << " has gone CONTROL SUBMITTED. Control expires at " << RWTime( Cmd->getMessageTime() + pPoint->getControlExpirationTime()) << endl;
                                    }
                                }

                                CtiSignalMsg *pCRP = new CtiSignalMsg(pPoint->getID(), Cmd->getSOE(), "Control " + resolveStateName(*pPoint, rawstate) + " Sent", RWCString(), GeneralLogType, pPoint->getAlarming().getAlarmStates(CtiTablePointAlarming::commandFailure), Cmd->getUser());

                                MainQueue_.putQueue( pCRP );
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
                    LONG idtype     = Cmd->getOpArgList().at(i);
                    LONG id         = Cmd->getOpArgList().at(i+1);
                    bool disable    = !((Cmd->getOpArgList().at(i+2) != 0));
                    int  validmask  = 0;         // This mask represents all the bits which are to be adjusted.
                    int  setmask    = 0;         // This mask represents the state of the adjusted-masked bit.. Ok, read it again.

                    try
                    {
                        CtiMultiMsg *pMulti = new CtiMultiMsg;

                        if(pMulti)
                        {
                            pMulti->setSource(DISPATCH_APPLICATION_NAME);
                            pMulti->setUser(Cmd->getUser());

                            if(Cmd->getOperation() == CtiCommandMsg::Ablement)
                            {
                                if(idtype == OP_DEVICEID)
                                {
                                    validmask = TAG_DISABLE_DEVICE_BY_DEVICE;
                                    setmask |= (disable ? TAG_DISABLE_DEVICE_BY_DEVICE : 0);
                                }
                                else if(idtype == OP_POINTID)
                                {
                                    validmask = TAG_DISABLE_POINT_BY_POINT;
                                    setmask |= (disable ? TAG_DISABLE_POINT_BY_POINT : 0);
                                }
                            }
                            else if(Cmd->getOperation() == CtiCommandMsg::ControlAblement)
                            {
                                if(idtype == OP_DEVICEID)
                                {
                                    validmask = TAG_DISABLE_CONTROL_BY_DEVICE;
                                    setmask |= (disable ? TAG_DISABLE_CONTROL_BY_DEVICE : 0);
                                }
                                else if(idtype == OP_POINTID)
                                {
                                    validmask = TAG_DISABLE_CONTROL_BY_POINT;
                                    setmask |= (disable ? TAG_DISABLE_CONTROL_BY_POINT : 0);
                                }
                            }

                            if(idtype == OP_DEVICEID)
                            {
                                if(ablementDevice(id, setmask, validmask, Cmd->getUser()))
                                {
                                    adjustDeviceDisableTags();
                                }
                            }
                            else if(idtype == OP_POINTID)
                            {
                                ablementPoint(id, setmask, validmask, Cmd->getUser(), *pMulti);
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
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    try
    {
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
        if(Msg.getId())
        {
            _snprintf(temp, sizeof(temp), "ID %ld", Msg.getId());
        }
        else
        {
            _snprintf(temp, sizeof(temp), "ENTRY");
        }

        loadAlarmToDestinationTranslation();

        RWCString desc = RWCString(temp) + resolveDBChangeType(Msg.getTypeOfChange()) + resolveDBChanged(Msg.getDatabase());

        CtiSignalMsg *pSig = new CtiSignalMsg(0, 0, desc, "DATABASE CHANGE");

        pSig->setUser(Msg.getUser());

        {
            CtiLockGuard<CtiMutex> guard(server_mux);
            CtiServer::iterator  iter(mConnectionTable);

            for(;(Mgr = (CtiVanGoghConnectionManager *)iter());)
            {
                Mgr->WriteConnQue( Msg.replicateMessage() );

                if(((CtiVanGoghConnectionManager*)Mgr)->getEvent())
                {
                    Mgr->WriteConnQue(pSig->replicateMessage());    // Copy pSig out to any event registered client
                }
            }
        }

        _signalMsgQueue.putQueue( pSig );
        loadRTDB(true, Msg.replicateMessage());
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

    updateRuntimeDispatchTable();
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
        CtiPoint *TempPoint = PointMgr.getMap().findValue(&CtiHashKey(aPD.getId()));

        if(TempPoint != NULL)      // We do know this point..
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)TempPoint->getDynamic();

            bool isNew = isPointDataNewInformation( aPD );

            if(pDyn != NULL)
            {
                if( aPD.getTime() >= pDyn->getDispatch().getTimeStamp().rwtime() || (aPD.getTags() & TAG_POINT_FORCE_UPDATE) )
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

                    _archiverQueue.putQueue( new CtiTableRawPointHistory(TempPoint->getID(), aPD.getQuality(), aPD.getValue(), aPD.getTime()));
                }
                else if(TempPoint->isArchivePending() || (TempPoint->getArchiveType() == ArchiveTypeOnChange && isNew))
                {
                    _archiverQueue.putQueue( new CtiTableRawPointHistory(TempPoint->getID(), aPD.getQuality(), aPD.getValue(), aPD.getTime()));
                    TempPoint->setArchivePending(FALSE);
                }
            }
        }
        else
        {
            CHAR temp[80];

            sprintf(temp, "Point change for unknown PointID: %ld", aPD.getId());
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << temp << endl;
            }

            CtiSignalMsg *pSig = new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Point Data Relay");
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
        CtiHashKey key(aSig.getId());

        {
            // See if I know about this point ID
            CtiLockGuard<CtiMutex> pmguard(server_mux);
            CtiPoint *TempPoint = PointMgr.getMap().findValue(&key);

            if(TempPoint != NULL)
            {
                pSig = (CtiSignalMsg*)aSig.replicateMessage();

                {
                    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)TempPoint->getDynamic();

                    if(pDyn != NULL)
                    {
                        pDyn->getDispatch().setTags( (aSig.getTags() & MASK_ANY_ALARM) );     // Set any alarm tags sent us via the message.
                    }
                }
            }
            else
            {
                CHAR temp[80];

                _snprintf(temp, sizeof(temp), "Signal for unknown PointID: %ld", aSig.getId());

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << temp << endl;
                }

                pSig = new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Signal Relay");
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
            CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(aCEHM.getPAOId());

            if( dliteit != _deviceLiteSet.end() )   // We do know this device..
            {
                // dliteit should be an iterator which represents the lite device now!
                CtiDeviceBaseLite &dLite = *dliteit;
                _commErrorHistoryQueue.putQueue( new CtiTableCommErrorHistory(dLite.getID(),
                                                                              aCEHM.getDateTime(),
                                                                              aCEHM.getSOE(),
                                                                              aCEHM.getErrorType(),
                                                                              aCEHM.getErrorNumber(),
                                                                              aCEHM.getCommand(),
                                                                              aCEHM.getOutMessage(),
                                                                              aCEHM.getInMessage()/*,
                                                                              aCEHM.getCommErrorId()*/));
            }
            else
            {
                CHAR temp[80];

                _snprintf(temp, sizeof(temp), "Comm Error History for unknown PAO ID: %ld", aCEHM.getPAOId());
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "*******************************************************" << endl;
                    dout << RWTime() << " - " << temp << " in: " <<  __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << RWTime() << " - Writing to DB (CommErrorHistory table) anyway!!!" << endl;
                    dout << "*******************************************************" << endl;
                }

                _commErrorHistoryQueue.putQueue( new CtiTableCommErrorHistory(aCEHM.getPAOId(),
                                                                              aCEHM.getDateTime(),
                                                                              aCEHM.getSOE(),
                                                                              aCEHM.getErrorType(),
                                                                              aCEHM.getErrorNumber(),
                                                                              aCEHM.getCommand(),
                                                                              aCEHM.getOutMessage(),
                                                                              aCEHM.getInMessage()));

                CtiSignalMsg *pSig = new CtiSignalMsg(SYS_PID_DISPATCH, 0, temp, "FAIL: Comm Error History Log");
                pSig->setUser(aCEHM.getUser());
                _signalMsgQueue.putQueue(pSig);

                status = IDNF; // Error is ID not found!
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
INT CtiVanGogh::analyzeMessageData( CtiMessage *pMsg )
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
                analyzeMultiMessage(pMulti);
                break;
            }
        case MSG_EMAIL:
            {
                CtiEmailMsg &Email = *((CtiEmailMsg*)pMsg);
                mail(Email);
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

                    if( VGCM.WriteConnQue(pMulti) )
                    {
                        MgrToRemove = Mgr;

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Connection is having issues : " << VGCM.getClientName() << " / " << VGCM.getClientAppId() << endl;
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

    CtiMultiMsg    *pMulti  = new CtiMultiMsg;


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

                CtiPoint *pTempPoint = PointMgr.getMap().findValue(&CtiHashKey(pSig->getId()));

                if(pTempPoint != NULL)
                {
                    CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();
                    pNewSig->setTags( pDyn->getDispatch().getTags() );
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
                CtiPoint *pTempPoint = PointMgr.getMap().findValue(&CtiHashKey(pDat->getId()));

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

                    if( pDat->getQuality() == ManualQuality ||
                        !(pDyn->getDispatch().getTags() & (MASK_ANY_SERVICE_DISABLE | MASK_ANY_CONTROL_DISABLE)) )
                    {
                        if(isPointDataForConnection(Conn, *pDat))
                        {
#ifdef NUMERICNEWINFO
                            if(isPointDataNewInformation(*pDat))
#endif
                            {
                                CtiPointDataMsg *pNew = (CtiPointDataMsg *)pDat->replicateMessage();

                                pNew->setTags( pDyn->getDispatch().getTags() );       // Report any set tags out to the clients.
                                Ord.insert(pNew);
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
    BOOL bStatus = TRUE;

    if( Msg.isExemptable() )      // Find out if we can exempt the data from being sent to clients
    {
        // Verify that the point has actually changed from the last known!
        // OR it must be marked for forcing through the system
        CtiHashKey  key(Msg.getId());

        CtiPoint *pPoint = PointMgr.getMap().findValue(&key);

        if(pPoint != NULL)      // We do know this point..
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

            if(pDyn != NULL && (pDyn->getValue() == Msg.getValue() && pDyn->getQuality() == Msg.getQuality()) )
            {
                bStatus = FALSE;
            }
        }
    }

    return bStatus;
}

BOOL CtiVanGogh::isConnectionAttachedToMsgPoint(const CtiVanGoghConnectionManager   &Conn,
                                                const LONG                          pID)
{
    BOOL bStatus = FALSE;

    CtiHashKey  key(pID);

    CtiPoint *TempPoint = PointMgr.getMap().findValue(&key);

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


int CtiVanGogh::processControlMessage(CtiLMControlHistoryMsg *pMsg)
{
    int status = NORMAL;

    try
    {
        CtiPoint *pPoint = NULL;
        CtiLockGuard<CtiMutex> pmguard(server_mux);
        pPoint = PointMgr.getMap().findValue( &CtiHashKey(pMsg->getPointId()) );

        if(pPoint != NULL)
        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

            if(pDyn != NULL
               && pDyn->getDispatch().getTags() & TAG_ATTRIB_CONTROL_AVAILABLE
               && !(pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_POINT || pDyn->getDispatch().getTags() & TAG_DISABLE_CONTROL_BY_DEVICE))
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
                pendingControlLMMsg.getControl().setSoeTag( CtiTableLMControlHistory::getNextSOE() );

                CtiSignalMsg *pFailSig = new CtiSignalMsg(pPoint->getID(), 0, "Control " + resolveStateName(*pPoint, pMsg->getRawState()) + " Failed", getAlarmStateName( pPoint->getAlarming().getAlarmStates(CtiTablePointAlarming::commandFailure) ), GeneralLogType, pPoint->getAlarming().getAlarmStates(CtiTablePointAlarming::commandFailure), pMsg->getUser());

                pendingControlLMMsg.setSignal( pFailSig );

                {
                    CtiLockGuard<CtiMutex> guard(server_mux);
                    pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                    resultpair = _pendingPointInfo.insert( pendingControlLMMsg );            // Add to the pending operations.

                    if(resultpair.second != true)
                    {
                        CtiPendingPointOperations &ppo = *resultpair.first;

                        if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
                        {
                            // We've had a collision and must tally the partial interval!
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Partial control interval must be adjusted. " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }

                            if( pMsg->getControlDuration() <= 0 )
                            {
                                ppo.getControl().setActiveRestore(LMAR_MANUAL_RESTORE);
                                updateControlHistory( pPoint->getPointID(), CtiPendingPointOperations::newcontrol, pMsg->getMessageTime() );
                            }
                            else if( pendingControlLMMsg.getControl().getControlType() == ppo.getControl().getControlType() )    // Same types?  Then this is a continuation of the old command!
                            {
                                ppo.getControl().setActiveRestore(LMAR_CONT_CONTROL);         // It is a new
                                pendingControlLMMsg.getControl().setStartTime(ppo.getControl().getStartTime());
                                updateControlHistory( pPoint->getPointID(), CtiPendingPointOperations::newcontrol, pMsg->getMessageTime() );
                                pendingControlLMMsg.getControl().setPreviousLogTime(ppo.getControl().getPreviousLogTime());
                                pendingControlLMMsg.getControl().setNotNewControl();
                            }
                            else
                            {
                                ppo.getControl().setActiveRestore(LMAR_OVERRIDE_CONTROL);         // It is a new
                                updateControlHistory( pPoint->getPointID(), CtiPendingPointOperations::newcontrol, pMsg->getMessageTime() );
                            }
                        }

                        ppo = pendingControlLMMsg;    // Copy it to update the control state.
                    }
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

    CtiLockGuard<CtiMutex> guard(server_mux);

    checkDataStateQuality(pMsg, MultiWrapper);
    /*
     *  Order here is important since the analyze routine writes into the RTDB the current values
     *  and the post routine compares messages against the current values to determine exceptions.
     */
    postMessageToClients(pMsg);
    analyzeMessageData(pMsg);

    // Now process any messages which were generated by the processing of the message.
    if( MultiWrapper.isNotNull() )
    {
        postMessageToClients((CtiMessage*)MultiWrapper.getMulti());
        analyzeMessageData((CtiMessage*)MultiWrapper.getMulti());
    }

    return status;
}

INT CtiVanGogh::analyzeMultiMessage(CtiMultiMsg *pMulti)
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
                {
                    analyzeMessageData( pMsg );
                    break;
                }
            case MSG_PCRETURN:
            case MSG_MULTI:
                {
                    CtiMultiMsg *pMultiNew = (CtiMultiMsg*)pMsg;    // Oh no, some more recursion!
                    analyzeMultiMessage( pMultiNew );
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


INT CtiVanGogh::postMOAUploadToConnection(CtiVanGoghConnectionManager &VGCM, int flags)
{
    INT i;
    INT status = NORMAL;

    CtiTableSignal *pSig;
    CtiMultiMsg    *pMulti  = new CtiMultiMsg;


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
                            CtiPointDataMsg *pDat = new CtiPointDataMsg(TempPoint->getID(),
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
                                pDat->setTime( pDyn->getDispatch().getTimeStamp().rwtime() );
                                pMulti->getData().insert(pDat);
                            }
                        }

                        if( (pDyn->getDispatch().getTags() & MASK_ANY_ALARM) && VGCM.getAlarm() )
                        {
                            if( _signalsPending.entries() > 0 )
                            {
                                if((pSig = _signalsPending.getMap().findValue(&CtiHashKey(TempPoint->getID()))) != NULL)
                                {
                                    CtiSignalMsg *pNewSig = new CtiSignalMsg(TempPoint->getID(),
                                                                             pSig->getSOE(),
                                                                             pSig->getText(),
                                                                             pSig->getAdditionalInfo(),
                                                                             pSig->getLogType(),
                                                                             pSig->getPriority(),
                                                                             pSig->getUser(),
                                                                             pDyn->getDispatch().getTags());

                                    if(pNewSig != NULL)
                                    {
                                        pNewSig->setMessageTime(pSig->getTime());
                                        pMulti->getData().insert(pNewSig);
                                    }
                                }

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

            if(VGCM.WriteConnQue(pMulti))
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
    CtiTableSignal *pSig;
    CtiPoint       *pTempPoint;

    CHAR           where[256];

    {
        CtiLockGuard<CtiMutex> pmguard(server_mux);
        CtiPointClientManager::CtiRTDBIterator  itr(PointMgr.getMap());

        for(;itr();)
        {
            pTempPoint = itr.value();

            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pTempPoint->getDynamic();

            if( pDyn != NULL )
            {
                if( (pDyn->getDispatch().getTags() & MASK_ANY_ALARM) )      // This point seems to have an alarm indication on it.
                {
                    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                    RWDBConnection conn = getConnection();

                    RWDBDatabase   db       = conn.database();
                    RWDBSelector   selector = conn.database().selector();
                    RWDBTable      keyTable;
                    RWDBReader     rdr;

                    CtiTableSignal::getSQLMaxID( db, keyTable, selector, pTempPoint->getID() );
                    rdr = selector.reader( conn );

                    if(rdr.status().errorCode() != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << selector.asString() << endl;
                    }

                    LONG maxID;

                    if( rdr() ) // there better be Only one in there!
                    {
                        rdr["logid"] >> maxID;
                    }
                    else
                    {
                        continue;      // This point has no logs!
                    }

                    selector = conn.database().selector();

                    CtiTableSignal::getSQL( db, keyTable, selector );
                    selector.where( selector.where() && keyTable["logid"] == maxID);

                    // dout << selector.asString() << endl;
                    rdr = selector.reader( conn );
                    if(rdr.status().errorCode() != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << selector.asString() << endl;
                    }

                    if(rdr.status().errorCode() != RWDBStatus::ok)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << selector.asString() << endl;
                    }

                    if( rdr() ) // there better be Only one in there!
                    {
                        pSig = NULL;
                        rdr["logid"] >> lTemp;            // get the LogID
                        CtiHashKey key(lTemp);


                        if( _signalsPending.entries() > 0 && ((pSig = _signalsPending.getMap().findValue(&key)) != NULL) )
                        {
                            /*
                             *  The point just returned from the rdr already was in my list.  We need to
                             *  update my list entry to the new settings!
                             */

                            pSig->DecodeDatabaseReader(rdr);     // Fills himself in from the reader
                            if(rdr.status().errorCode() != RWDBStatus::ok)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                dout << selector.asString() << endl;
                            }

                            pSig->setUpdatedFlag();              // Mark it updated
                        }
                        else
                        {
                            pSig = new CtiTableSignal;  // Use the reader to get me an object of the proper type

                            if(pSig)
                            {
                                pSig->DecodeDatabaseReader(rdr);        // Fills himself in from the reader
                                _signalsPending.getMap().insert( new CtiHashKey(pSig->getPointID()), pSig ); // Stuff it in the list
                            }
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** ERROR Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  No pending error for point id " << pTempPoint->getID() << " found" << endl;
                            dout << endl << selector.asString() << endl;
                        }

                        pDyn->getDispatch().resetTags( MASK_ANY_ALARM );
                    }
                }
            }
        }
    }

    return status;
}


void CtiVanGogh::writeSignalsToDB(bool justdoit)
{
    CtiSignalMsg   *Msg;
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
                        Msg = _signalMsgQueue.getQueue(0);

                        if(Msg != NULL)
                        {
                            CtiTableSignal sig(Msg->getId(), Msg->getMessageTime(), Msg->getText(), Msg->getAdditionalInfo(), Msg->getSignalGroup(), Msg->getLogType(), Msg->getSOE(), Msg->getUser());

                            if(!Msg->getText().isNull() || !Msg->getAdditionalInfo().isNull())
                            {
                                // No text, no point then is there now?
                                sig.Insert(conn);

                                /*
                                 *  Last thing we do is add this signal to the pending signal list iff it is an alarm
                                 *  AND it is not a cleared alarm....  This second condition prevents clear reports
                                 *  from being kept on the pending list.
                                 */

                                if( Msg->getSignalGroup() > SignalEvent && !(Msg->getTags() & TAG_REPORT_MSG_TO_ALARM_CLIENTS) )
                                {
                                    CtiHashKey *pKey = new CtiHashKey(sig.getPointID());
                                    CtiTableSignal *pNewSig = sig.replicate();

                                    if(pKey != NULL && pNewSig != NULL)
                                    {
                                        if(!_signalsPending.getMap().insert( pKey , pNewSig ))
                                        {
                                            CtiTableSignal *pOldSig = _signalsPending.getMap().findValue(&CtiHashKey(Msg->getId()));

                                            if(pOldSig != NULL)
                                            {
                                                *pOldSig = *pNewSig;
                                            }

                                            // And don't let the memory escape from us..
                                            delete pKey;
                                            delete pNewSig;
                                        }
                                    }
                                }
                            }

                            postList.insert(Msg);
                        }

                    } while( conn.isValid() && Msg != NULL && (justdoit || (panicCounter++ < 500)));

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

            {
                RWOrderedIterator itr( postList );
                for(;NULL != (Msg = (CtiSignalMsg*)itr());)
                {
                    postSignalAsEmail( *Msg );
                }

                postList.clearAndDestroy();
            }

            if(panicCounter > 0)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " SystemLog transaction complete. Inserted " << panicCounter << " signal messages" << endl;
                }

                if(panicCounter >= 500)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << " PANIC, Lots of signals" << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
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
                if(_archiverQueue.size() < 64000 && (_archiverQueue.entries() > (_archiverQueue.size() / 2)))
                {
                    _archiverQueue.resize( _archiverQueue.size() );
                }

                if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Archival queue has " << _archiverQueue.entries() << " entries" << endl;
                    dout << " Queue is currently sized at " << _archiverQueue.size() << " entries" << endl;
                }
            }
            else
            {
                if(_archiverQueue.entries() == 0 && _archiverQueue.size() > 100 )
                {
                    _archiverQueue.resize( 100 - _archiverQueue.size() );    // Shrink back to 100 entries..

                    if(gDispatchDebugLevel & DISPATCH_DEBUG_VERBOSE)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " Archival queue is empty.  Capacity has been reduced to default size. " << endl;
                        dout << " Queue is currently sized at " << _archiverQueue.size() << " entries" << endl;
                    }
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

                if( panicCounter > 0 )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " LMControlHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
                    }
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                /*
                 *  double the queue's size if it is more than half full.
                 */
                if(_lmControlHistoryQueue.size() < 64000 && (_lmControlHistoryQueue.entries() > (_lmControlHistoryQueue.size() / 2)))
                {
                    _lmControlHistoryQueue.resize( _lmControlHistoryQueue.size() );
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " LM Control History queue has " << _lmControlHistoryQueue.entries() << " entries" << endl;
                    dout << " Queue is currently sized at " << _lmControlHistoryQueue.size() << " entries" << endl;
                }
            }
            else
            {
                if(_lmControlHistoryQueue.entries() == 0 && _lmControlHistoryQueue.size() > 100 )
                {
                    _lmControlHistoryQueue.resize( 100 - _lmControlHistoryQueue.size() );    // Shrink back to 100 entries..

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " LM Control History queue is empty.  Capacity has been reduced to default size. " << endl;
                        dout << " Queue is currently sized at " << _lmControlHistoryQueue.size() << " entries" << endl;
                    }
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

                if( panicCounter > 0 )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " CommErrorHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
                    }
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                /*
                 *  double the queue's size if it is more than half full.
                 */
                if(_commErrorHistoryQueue.size() < 64000 && (_commErrorHistoryQueue.entries() > (_commErrorHistoryQueue.size() / 2)))
                {
                    _commErrorHistoryQueue.resize( _commErrorHistoryQueue.size() );
                }

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Comm Error History queue has " << _commErrorHistoryQueue.entries() << " entries" << endl;
                    dout << " Queue is currently sized at " << _commErrorHistoryQueue.size() << " entries" << endl;
                }
            }
            else
            {
                if(_commErrorHistoryQueue.entries() == 0 && _commErrorHistoryQueue.size() > 100 )
                {
                    _commErrorHistoryQueue.resize( 100 - _commErrorHistoryQueue.size() );    // Shrink back to 100 entries..

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** INFO **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << " Comm Error History queue is empty.  Capacity has been reduced to default size. " << endl;
                        dout << " Queue is currently sized at " << _commErrorHistoryQueue.size() << " entries" << endl;
                    }
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
void CtiVanGogh::updateRuntimeDispatchTable()
{
    static UINT callCounter = 0;

    try
    {
        if(!(callCounter % UPDATERTDB_RATE) )    // Only chase the queue once per CONFRONT_RATE seconds.
        {
            CtiLockGuard<CtiMutex> server_guard(server_mux, 2500);      // Get a lock on it.

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


INT CtiVanGogh::checkDataStateQuality(CtiMessage *pMsg, CtiMultiWrapper &aWrap)
{
    INT status   = NORMAL;

    switch(pMsg->isA())
    {
    case MSG_PCRETURN:
    case MSG_MULTI:
        {
            try
            {
                status = checkMultiDataStateQuality((CtiMultiMsg*)pMsg, aWrap);
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case MSG_POINTDATA:
        {
            try
            {
                status = checkPointDataStateQuality((CtiPointDataMsg*)pMsg, aWrap);
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case MSG_COMMAND:
        {
            CtiCommandMsg *pCmd = (CtiCommandMsg*)pMsg;

            try
            {
                if(pCmd->getOperation() == CtiCommandMsg::UpdateFailed)
                {
                    status = checkCommandDataStateQuality(pCmd, aWrap);
                }
                else
                {
                    // Make sure nobody is doing something odd.  CGP 3/18/2002
                    commandMsgHandler(pCmd);
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    case MSG_SIGNAL:
        {
            try
            {
                status = checkSignalStateQuality((CtiSignalMsg*)pMsg, aWrap);
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    default:
        {
            break;
        }
    }

    return status;
}

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
 *----------------------------------------------------------------------------*/
INT CtiVanGogh::checkPointDataStateQuality(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap)
{
    INT status   = NORMAL;

    if(pData != NULL)
    {
        CtiLockGuard<CtiMutex> pmguard(server_mux);
        CtiPoint *pPoint = PointMgr.getMap().findValue( &CtiHashKey(pData->getId()) );

        if(pPoint != NULL)      // We do know this point..
        {
            // We need to make sure there is no pending pointdata on this pointid. 102501 CGP.
            if( removePointDataFromPending( pData->getId(), *pData))
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
                {
                    CtiLockGuard<CtiMutex> guard(server_mux);
                    pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                    resultpair = _pendingPointInfo.insert( pendingPointData );            // Add to the pending operations.

                    if(resultpair.second != true)
                    {
                        /*
                         *  Based upon the removal above, we never expect a collision here, but if the operator<() method
                         *  gets touched, it could occur.. This is insurance.
                         */
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        *resultpair.first = pendingPointData;
                    }
                }
            }
            else
            {
                try
                {
                    if(pPoint->isNumeric())
                    {
                        status = analyzeForNumericAlarms(pData, aWrap, *pPoint);
                    }
                    else if(pPoint->isStatus())
                    {
                        status = analyzeForStatusAlarms(pData, aWrap, *pPoint);
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

INT CtiVanGogh::checkCommandDataStateQuality(CtiCommandMsg *pCmd, CtiMultiWrapper &aWrap)
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
        CtiPoint *pPoint = PointMgr.getMap().findValue( &CtiHashKey(Op.at((size_t)2)) );

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

    if(point.getPointOffset() != 2000)
    {
        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

        // Make sure we use the correct enumeration based upon point type.
        INT alarm = (point.isStatus() ? CtiTablePointAlarming::nonUpdatedStatus : CtiTablePointAlarming::nonUpdatedNumeric);

        bool nonUpdated = (pDyn->getLastSignal() == alarm);


        if( pDyn != NULL )
        {
            UINT quality = pDyn->getDispatch().getQuality();
            pDyn->setLastSignal(alarm);

            if(quality != NonUpdatedQuality)
            {
                pDyn->getDispatch().setQuality(NonUpdatedQuality);

                if(!nonUpdated)
                {
                    CtiSignalMsg *pSig = new CtiSignalMsg(point.getID(), 0, "Non Updated", getAlarmStateName( point.getAlarming().getAlarmStates(alarm) ), GeneralLogType, point.getAlarming().getAlarmStates(alarm));

                    if(point.getAlarming().getAlarmStates(alarm) > SignalEvent)
                    {
                        pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                        pDyn->getDispatch().setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                    }

                    aWrap.getMulti()->insert( pSig );
                }

                CtiPointDataMsg *pDat = new CtiPointDataMsg(point.getID(), pDyn->getValue(), pDyn->getQuality(), point.getType(), "Non Updated", pDyn->getDispatch().getTags());

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
        UINT signaltrx = sig.getSignalGroup(); // Alarm Category.

        if(signaltrx > SignalEvent)
        {
            // This is an "alarm" class signal.  It may need to be emailed into the world.

            CtiPoint *pPoint = NULL;
            {
                CtiLockGuard<CtiMutex> pmguard(server_mux);
                pPoint = PointMgr.getMap().findValue( &CtiHashKey(sig.getId()) );

                if(pPoint)
                {
                    rid  = pPoint->getAlarming().getRecipientID();
                    ngid = pPoint->getAlarming().getNotificationGroupID();

                    // Check if point identifies a single recipient to be notified on any signal
                    if(rid > 0)
                    {
                        CtiTableGroupRecipient    *pRecipient = NULL;
                        CtiTableNotificationGroup nulGrp;

                        nulGrp.setEmailFromAddress(gEmailFrom);
                        nulGrp.setEmailMessage(" ");
                        nulGrp.setGroupName("Yukon Alarming Subsystem");
                        nulGrp.setEmailSubject("YUKON ALARM");

                        if( (pRecipient = getRecipient(rid)) != NULL )
                        {
                            // Now we have it ALL!  Send the email
                            sendMail(sig, nulGrp, *pRecipient);
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

INT CtiVanGogh::analyzeForStatusAlarms(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point)
{
    int alarm;
    INT status = NORMAL;

    if(point.isStatus())    // OK, we are indeed a status point.
    {
        const CtiTablePointAlarming &ptAlarm = point.getAlarming();

        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

        if(pDyn != NULL)     // We must know about the point!
        {
            CtiSignalMsg *pSig;

            for( alarm = 0; alarm < CtiTablePointAlarming::invalidstatusstate; alarm++ )
            {
                pSig = NULL;       // There is no alarm for this alarmstate.

                // This prohibits re-alarming on the same problem!
                if( pDyn->getLastSignal() != alarm && ptAlarm.alarmOn( alarm ) )
                {
                    switch(alarm)
                    {
                    case (CtiTablePointAlarming::nonUpdatedStatus):
                        {
                            break;
                        }
                    case (CtiTablePointAlarming::abnormal):
                        {
                            break;
                        }
                    case (CtiTablePointAlarming::uncommandedStateChange):
                        {
                            analyzeStatusUCOS(alarm, pData, aWrap, point, pDyn, pSig );
                            break;
                        }
                    case (CtiTablePointAlarming::commandFailure): // ANALOG CASE -> case (CtiTablePointAlarming::rateOfChange):
                        {
                            analyzeStatusCommandFail(alarm, pData, aWrap, point, pDyn, pSig );
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
                            analyzeStatusState(alarm, pData, aWrap, point, pDyn, pSig );
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
            }

            // We ALWAYS create an occurrence event to stuff a state event log into the systemlog table.
            if( pDyn->getDispatch().getValue() != pData->getValue() )
            {
                RWCString txt = resolveStateName(point, (int)pData->getValue());
                RWCString addn;

                if(pData->getQuality() == ManualQuality)
                {
                    addn = "Manual Update";
                }

                pSig = new CtiSignalMsg(point.getID(), pData->getSOE(), txt, addn);
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

INT CtiVanGogh::analyzeForNumericAlarms(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point)
{
    int alarm;
    INT status = NORMAL;

    if(point.isNumeric())
    {
        CtiPointNumeric             *pNumeric = (CtiPointNumeric*)&point;
        const CtiTablePointAlarming &ptAlarm  = point.getAlarming();

        CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)point.getDynamic();

        if(pDyn != NULL)     // We must know about the point!
        {
            CtiSignalMsg    *pSig;
            UINT            tags            = pDyn->getDispatch().getTags();

            // We check if the point has been sent in as a Manual Update.  If so, we ALWAYS log this occurence.
            if(pData->getQuality() == ManualQuality)
            {
                char tstr[80];
                _snprintf(tstr, sizeof(tstr), "Value set to %.3f", pData->getValue());
                RWCString addn = "Manual Update";

                pSig = new CtiSignalMsg(point.getID(), pData->getSOE(), tstr, addn);
                if(pSig != NULL)
                {
                    pSig->setUser(pData->getUser());
                    aWrap.getMulti()->insert( pSig );
                }
            }

            /*
             *  Check if this pData puts us back into nominal range iff we have _ANY_ pending limit violation going on.
             */
            analyzeLimitViolationReset( pData, aWrap, *pNumeric, pDyn, pSig );

            for( alarm = 0; alarm < CtiTablePointAlarming::invalidnumericstate; alarm++ )
            {
                pSig = NULL;


                switch(alarm)
                {
                case (CtiTablePointAlarming::highReasonability):
                case (CtiTablePointAlarming::lowReasonability):
                    {
                        analyzeNumericReasonability( alarm, pData, aWrap, *pNumeric, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::rateOfChange):
                    {
                        analyzeNumericRateOfChange( alarm, pData, aWrap, *pNumeric, pDyn, pSig );
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
                        analyzeNumericLimits( alarm, pData, aWrap, *pNumeric, pDyn, pSig );
                        break;
                    }
                case (CtiTablePointAlarming::nonUpdatedNumeric):  // alarm generated by checkCommandDataStateQuality.
                default:
                    {
                        break;
                    }
                }

                // If pSig is non-NULL, this "alarm" condition occurred and we need to decide if the point goes into alarm over it.
                if(pSig != NULL)
                {
                    // We now need to check if this "alarm" is a real alarm ( > SignalEvent level )
                    if(point.getAlarming().getAlarmStates(alarm) > SignalEvent)
                    {
                        pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                    }

                    if(pData->getQuality() == ManualQuality)    // If is "pushed" into the alarm condition, let's label it that way.
                    {
                        pSig->setAdditionalInfo("Manual Update: " + pSig->getAdditionalInfo());
                    }

                    aWrap.getMulti()->insert( pSig );
                }
            }
        }
    }

    return status;
}

#include <io.h>

INT CtiVanGogh::sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp, const CtiTableGroupRecipient &recip, RWCString subject)
{
    INT status = NORMAL;

    CtiPoint *point = NULL;

    RWCString pointname;
    RWCString devicetext("Unknown");
    RWCString paodescription;
    bool excluded = true;

    CtiLockGuard<CtiMutex> pmguard(server_mux);
    if((point =  PointMgr.getMap().findValue( &CtiHashKey(sig.getId()) )) != NULL)
    {
        {
            pointname = point->getName();
            devicetext = resolveDeviceName( *point );
            excluded = point->getAlarming().isExcluded(sig.getSignalGroup());
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
            sm.lpszRecipient     = recip.getEmailAddress();
            sm.lpszRecipientName = recip.getRecipientName();
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

INT CtiVanGogh::sendMail(const CtiEmailMsg &aMail, const CtiTableGroupRecipient &recip)
{
    INT status = NORMAL;

    SENDMAIL sm;

    RWCString mailstr = "\r" + aMail.getText() + resolveEmailMsgDescription( aMail );

    sm.lpszHost          = gSMTPServer;                   // Global loaded by ctibase.dll.
    sm.lpszSender        = aMail.getSender();
    sm.lpszSenderName    = NULL;
    sm.lpszRecipient     = recip.getEmailAddress();
    sm.lpszRecipientName = recip.getRecipientName();
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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << NowTime.now() << " " << CM->getClientName() << " just re-registered." << endl;
                    removeMgr = TRUE;
                    break;
                }
                else if(Mgr->getClientUnique())
                {
                    if( Mgr->getClientQuestionable() )       // has this guy been previously pinged and not responded?
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime.now() << " " <<
                        CM->getClientName() << " / " <<
                        CM->getClientAppId() << " / " <<
                        CM->getPeer() << " just won a client arbitration." << endl;

                        removeMgr = TRUE;    // Make the old one go away...

                        break;
                    }
                    else
                    {
                        Mgr->setClientQuestionable(TRUE);      // Mark the old guy as needing confirmation (also causes eventual cleanup if he doesn't respond.)
                        Mgr->setClientRegistered(TRUE);        // Mark the old guy as having been previously registered

                        Mgr->WriteConnQue(new CtiCommandMsg(CtiCommandMsg::AreYouThere, 15));   // Ask the old guy to respond to us..
                        CM->setClientRegistered(FALSE);                                         // New guy is not quite kosher yet...

                        questionedEntry = TRUE;
                        validEntry = TRUE;

                        // Old one wanted to be the only one
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << NowTime.now() << " New client \"" << CM->getClientName() << "\" conflicts with an existing client." << endl;
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

        CM->WriteConnQue(new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));  // Ask the new guy to blow off..

        clientShutdown(CM);
    }

    if(removeMgr && Mgr != NULL)
    {
        CtiLockGuard<CtiMutex> guard(server_mux);

        if(mConnectionTable.remove(Mgr))
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << NowTime.now() << " " <<
            Mgr->getClientName() << " / " <<
            Mgr->getClientAppId() << " / " <<
            Mgr->getPeer() << " just _lost_ the client arbitration." << endl;

            // This connection manager is abandoned now...
            clientShutdown(Mgr);
        }
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

            Mgr->WriteConnQue(new CtiCommandMsg(CtiCommandMsg::Shutdown, 15));  // Ask the new guy to blow off..

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
                                    dout << now << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  *****************  CONTROL HISTORY **************** " << endl;
                                    dout << "  MODIFY THE CONTROL HISTORY RUNNING TABLE NOW PLEASE " << endl;
                                    dout << "  Control seems to have failed "  << endl;
                                    dout << "  *************************************************** " << endl;
                                }

                                MainQueue_.putQueue( pSig );
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
                        else if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress &&
                                ppo.getControl().getControlDuration() > 0 &&
                                now.seconds() >= ppo.getControl().getStartTime().seconds() + ppo.getControl().getControlDuration())
                        {
                            /*
                             *  Order is important here.  Please do not rearrange the else if conditionals.
                             *  CONTROL IS COMPLETE!
                             *  delayed data will pick this up...  Don't tally any more time though..
                             */
                        }
                        else if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress &&
                                ppo.getControl().getControlDuration() < 0)
                        {
                            // This is a restore command.  We cannot allow it to accumulate time!
                        }
                        else if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress &&
                                now.seconds() >= prevLogSec - (prevLogSec % CntlHistInterval) + CntlHistInterval)
                        {
                            // We have accumulated enough time against this control to warrant a new log entry!
                            updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::intervalcrossing, now);
                        }
                        else if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress &&
                                ppo.getControl().getControlDuration() > 0 &&
                                now.seconds() >= ppo.getControl().getPreviousStopReportTime().seconds() - (ppo.getControl().getPreviousStopReportTime().seconds() % CntlStopInterval) + CntlStopInterval)
                        {
                            updateControlHistory( ppo.getPointID(), CtiPendingPointOperations::stopintervalcrossing, now);
                        }
                    }
                    else if(ppo.getType() == CtiPendingPointOperations::pendingPointData)
                    {
                        try
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
                                }
                                else
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << now << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                }

                                it = _pendingPointInfo.erase(it);
                                dumpPendingOps();

                                continue;   // iterator has been repositioned!
                            }
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << now << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        try
                        {
                            it = _pendingPointInfo.erase(it);
                            dumpPendingOps();
                            continue;   // iterator has been repositioned!
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << now << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << Now << " Starting loadRTDB. " << endl;
            }

            // This loads up the points that VanGogh will manage.
            if( pChg == NULL || (pChg->getDatabase() == ChangePointDb) )
            {
                CtiLockGuard<CtiMutex> pmguard(server_mux, 10000);
                if(pmguard.isAcquired())
                {
                    PointMgr.RefreshList(isPoint);
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

            Now = Now.now();

            // Make damn sure any signals are in the DB
            writeSignalsToDB(true);

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << deltaT << " seconds to write signal list to DB" << endl;
            }
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
                loadStateNames();
            }
            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << deltaT << " seconds to load state names" << endl;
            }
            Now = Now.now();

            if( pChg == NULL || (resolvePAOCategory(pChg->getCategory()) == PAO_CATEGORY_DEVICE) )
            {
                CtiLockGuard<CtiMutex> guard(server_mux, 5000);

                if(guard.isAcquired())
                {
                    loadDeviceLites();
                    deltaT = Now.now().seconds() - Now.seconds();
                    if( deltaT > 5 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << deltaT << " seconds to load lite device data" << endl;
                    }
                    Now = Now.now();

                    adjustDeviceDisableTags();

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

                Now = Now.now();
            }
            if( pChg == NULL || (pChg->getDatabase() == ChangeCustomerContactDb) )
            {
                loadCICustomers();
            }

            deltaT = Now.now().seconds() - Now.seconds();
            if( deltaT > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << deltaT << " seconds to load CI Customers" << endl;
            }
            Now = Now.now();

            if(pChg != NULL)
            {
                CtiLockGuard<CtiMutex> guard(server_mux, 1000);

                while(!guard.tryAcquire(5000))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " INFO: Unable to acquire exclusion for notification group load. Will try again." << endl;
                    }
                }

                // We will own that mutex following at this point.
                if(pChg->getDatabase() == ChangeNotificationGroupDb)
                {
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
                    // Recipients have changed
                    CtiRecipientSet_t::iterator rit;

                    for(rit = _recipientSet.begin(); rit != _recipientSet.end(); rit++ )
                    {
                        CtiTableGroupRecipient &theRecip = *rit;
                        theRecip.setDirty(true);
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

            Refresh = Now.seconds() - (Now.seconds() % 60) + gDispatchReloadRate;
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
 *  returns true if the point has a valid state group with this raw value
 */
RWCString CtiVanGogh::resolveStateName(const CtiPointBase &aPoint , LONG rawValue)
{
    RWCString rStr;
    LONG grpid = aPoint.getStateGroupID();

    if(grpid > 0)
    {
        CtiTableStateGroup mygroup( grpid );

        CtiLockGuard<CtiMutex> guard(server_mux);

        CtiStateGroupSet_t::iterator sgit = _stateGroupSet.find( mygroup );

        if( sgit == _stateGroupSet.end() )
        {
            // We need to load it up, and/or then insert it!
            mygroup.Restore();

            pair< CtiStateGroupSet_t::iterator, bool > resultpair;

            // Try to insert. Return indicates success.
            resultpair = _stateGroupSet.insert( mygroup );

            if(resultpair.second == true)
            {
                sgit = resultpair.first;      // Iterator which points to the set entry.
            }
        }

        if( sgit != _stateGroupSet.end() )
        {
            // git should be an iterator which represents the group now!
            CtiTableStateGroup &theGroup = *sgit;
            rStr = theGroup.getRawState(rawValue);
        }
    }

    return rStr;
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

void CtiVanGogh::loadStateNames()
{
    CtiLockGuard<CtiMutex> guard(server_mux, 5000);

    if(guard.isAcquired())
    {
        CtiStateGroupSet_t::iterator sgit;

        bool reloadFailed = false;

        for(sgit = _stateGroupSet.begin(); sgit != _stateGroupSet.end(); sgit++ )
        {
            CtiTableStateGroup &theGroup = *sgit;
            if(theGroup.Restore().errorCode() != RWDBStatus::ok)
            {
                reloadFailed = true;
                break;
            }
        }

        if(reloadFailed)
        {
            _stateGroupSet.clear();          // All stategroups will be reloaded on their next usage..  This shouldn't happen very often
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " State Group Set reset. " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " INFO: State group names were not reloaded this pass.  Exclusion could not be obtained." << endl;
    }
}

/*
 *  This method loads all pao objects which "could" have control.. This may need change in the future.
 *
 *  Expects protections to be performed by someone else.
 */
void CtiVanGogh::loadDeviceLites()
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
    CtiLockGuard<CtiMutex> guard(server_mux);

    if(!_deviceLiteSet.empty())
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

void CtiVanGogh::loadCICustomers()
{
    CtiLockGuard<CtiMutex> guard(server_mux, 2500);

    if(guard.isAcquired())
    {
        CtiDeviceCICustSet_t::iterator it;

        bool reloadFailed = false;

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
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " INFO: CI Customer Set was not reloaded. Exclusion oculd not be acquired." << endl;
    }
}

CtiTableGroupRecipient* CtiVanGogh::getRecipient( LONG locid )
{
    CtiTableGroupRecipient *pRecipient = NULL;

    CtiLockGuard<CtiMutex> guard(server_mux);
    CtiTableGroupRecipient recip( locid );
    CtiRecipientSet_t::iterator rit = _recipientSet.find( recip );

    if( rit == _recipientSet.end() )
    {
        // We need to load it up, and then insert it!
        recip.Restore();

        pair< CtiRecipientSet_t::iterator, bool > resultpair;
        resultpair = _recipientSet.insert( recip );

        if(resultpair.second == true)
        {
            rit = resultpair.first;
        }
    }

    if( rit != _recipientSet.end() )
    {
        // rit should be an iterator which represents the recipient now!
        pRecipient = &(*rit);
    }

    return pRecipient;
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

void CtiVanGogh::sendSignalToGroup(LONG ngid, CtiSignalMsg sig)
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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  Restoring a changed (dirty) Notification Group" << endl;
            }
            theGroup.Restore();     // Clean the thing then!
        }

        vector<int> recipients = theGroup.getRecipientVector();
        vector<int>::iterator r_iter;

        for(r_iter = recipients.begin(); r_iter != recipients.end(); r_iter++ )
        {
            CtiTableGroupRecipient *pRecipient;
            int locationid = *r_iter;

            {
                CtiLockGuard<CtiMutex> guard(server_mux);
                if( (pRecipient = getRecipient(locationid)) != NULL )
                {
                    // Now we have it ALL!  Send the email
                    sendMail(sig, theGroup, *pRecipient);
                }
            }
        }
    }
}

void CtiVanGogh::sendEmailToGroup(LONG ngid, CtiEmailMsg email)
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

        vector<int> recipients = theGroup.getRecipientVector();
        vector<int>::iterator r_iter;

        for(r_iter = recipients.begin(); r_iter != recipients.end(); r_iter++ )
        {
            CtiTableGroupRecipient *pRecipient;
            int locationid = *r_iter;

            {
                CtiLockGuard<CtiMutex> guard(server_mux);
                if( (pRecipient = getRecipient(locationid)) != NULL )
                {
                    // Now we have it ALL!  Send the email
                    sendMail(email, *pRecipient);
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

bool CtiVanGogh::ablementPoint(LONG pid, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList)
{
    bool status = false;

    CtiLockGuard<CtiMutex> pmguard(server_mux);
    CtiPoint *pPoint = PointMgr.getMap().findValue( &CtiHashKey(pid) );

    if(pPoint != NULL)
    {
        CtiSignalMsg *pSig = NULL;

        {
            CtiDynamicPointDispatch *pDyn = (CtiDynamicPointDispatch*)pPoint->getDynamic();

            if(pDyn != NULL)
            {
                UINT currtags  = (pDyn->getDispatch().getTags() & (tagmask & MASK_ANY_DISABLE));
                UINT newpttags = (setmask & MASK_ANY_DISABLE);

                UINT currpttags = (currtags & (tagmask & MASK_ANY_POINT_DISABLE));
                UINT pttags   = (setmask & MASK_ANY_POINT_DISABLE);

                UINT currdvtags = (currtags & (tagmask & MASK_ANY_DEVICE_DISABLE));
                UINT dvtags   = (setmask & MASK_ANY_DEVICE_DISABLE);

                if( currtags != newpttags )
                {
                    if(currpttags != pttags)
                    {
                        if(updatePointStaticTables(pPoint->getPointID(), pttags, tagmask, user, sigList))
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }

                    if(currdvtags != dvtags)
                    {
                        status = true;
                    }

                    pDyn->getDispatch().resetTags(tagmask);
                    pDyn->getDispatch().setTags(newpttags);
                }
            }
        }
    }

    return status;
}

bool CtiVanGogh::ablementDevice(LONG did, UINT setmask, UINT tagmask, RWCString user)
{
    bool delta = false;     // Anything changed???

    CtiLockGuard<CtiMutex> pmguard(server_mux);

    CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(did);

    if( dliteit != _deviceLiteSet.end() )   // We do know this device..
    {
        CtiDeviceBaseLite &dLite = *dliteit;

        if( tagmask & TAG_DISABLE_DEVICE_BY_DEVICE )
        {
            dLite.setDisableFlag((TAG_DISABLE_DEVICE_BY_DEVICE & setmask ? "Y" : "N"));
            delta = true;
        }
        else if( tagmask & TAG_DISABLE_CONTROL_BY_DEVICE )
        {
            dLite.setControlInhibitFlag((TAG_DISABLE_CONTROL_BY_DEVICE & setmask ? "Y" : "N"));
            delta = true;
        }
        else if( tagmask & TAG_DISABLE_ALARM_BY_DEVICE )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            delta = true;
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
                    vector<int> recip = pCustomer->getRecipientVector();
                    vector<int>::iterator it;

                    try
                    {
                        CtiTableGroupRecipient *pRecipient = NULL;

                        for(it = recip.begin(); it != recip.end(); ++it)
                        {
                            vector<int>::reference locID = *it;

                            if( (pRecipient = getRecipient(locID)) != NULL )
                            {
                                // Now we have it ALL!  Send the email
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Emailing " << locID << " / " << pRecipient->getRecipientName() << endl;
                                }
                                sendMail(aMail, *pRecipient);
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

CtiVanGogh::CtiVanGogh() :
_signalMsgQueue(100),
CtiServer(1000)
{
    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        for(int i = 0; i < MAX_ALARM_TRX; i++)
        {
            _alarmToDestInfo[i].grpid = 0; // Zero is invalid!
        }
    }
}

CtiVanGogh::CtiVanGogh(int QueueSize) :
_signalMsgQueue(100),
CtiServer(QueueSize)
{
    {
        CtiLockGuard<CtiMutex> guard(server_mux);
        for(int i = 0; i < MAX_ALARM_TRX; i++)
        {
            _alarmToDestInfo[i].grpid = 0; // Zero is invalid!
        }
    }
}

CtiVanGogh::~CtiVanGogh();

void  CtiVanGogh::shutdown()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Van Gogh Server Shutting Down " << endl;
    }
    Inherited::shutdown();
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

        Listener = new RWSocketListener(NetAddr);

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
        cmdstr += resolveStateName(*pPoint, rawstate);
    }

    cmdstr += RWCString(" select pointid " + CtiNumStr(pPoint->getPointID()));

    if(pReq = new CtiRequestMsg( deviceid, cmdstr ))
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
                PilCM->WriteConnQue( pReq->replicateMessage() );
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
        CtiCommandMsg *pAltRate = new CtiCommandMsg( CtiCommandMsg::AlternateScanRate );
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

void CtiVanGogh::writeMessageToScanner(const CtiCommandMsg *Cmd)
{
    // this guy goes to scanner only
    CtiVanGoghConnectionManager *scannerCM = getScannerConnection();

    if(scannerCM != NULL)
    {
        // pass the message through
        scannerCM->WriteConnQue(Cmd->replicateMessage());
    }
}

void CtiVanGogh::updateControlHistory( long pendid, int cause, const RWTime &thetime, RWTime &now )
{
    CtiLockGuard<CtiMutex> guard(server_mux);
    CtiPendingOpSet_t::iterator it = _pendingPointInfo.find(CtiPendingPointOperations(pendid, CtiPendingPointOperations::pendingControl));

    if( it != _pendingPointInfo.end() )
    {
        CtiPendingPointOperations &ppc = *it;

        switch(cause)
        {
        case (CtiPendingPointOperations::intervalcrossing):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds > 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        ppc.getControl().incrementTimes( thetime, addnlseconds );

                        // Create some lies
                        ppc.getControl().setActiveRestore( LMAR_LOGTIMER );             // Record this as a start or continue interval.
                        insertAndPostControlHistoryPoints(ppc, now);

                        // OK, set them out for the next run ok. Undo the lies.
                        ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );        // Reset to the original completion state.

                        insertAndPostControlHistoryPoints(ppc, now, false, false, true);    // Post the AI now that the lies are covered up..
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
                insertAndPostControlHistoryPoints(ppc, now, false, false, true);        // Post countdown AI
                break;
            }
        case (CtiPendingPointOperations::newcontrol):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
                   ppc.getControl().getControlDuration() != RESTORE_DURATION)     // This indicates a restore.
                {
                    LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds > 0)
                    {
                        verifyControlTimesValid(ppc);   // Make sure ppc has been primed.

                        ppc.getControl().incrementTimes( now, addnlseconds );
                        ppc.setControlState( CtiPendingPointOperations::controlCompleteCommanded );
                        insertAndPostControlHistoryPoints(ppc, now);
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
                        insertAndPostControlHistoryPoints(ppc, now, true, true, true);
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
                        insertAndPostControlHistoryPoints(ppc, now, true, true, true);
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

void CtiVanGogh::insertAndPostControlHistoryPoints( CtiPendingPointOperations &ppc,
                                                    const RWTime &now,
                                                    bool insertctlhistrow,       // A row will be added to table lmcontrolhistory
                                                    bool postctlhistaipts,       // Analog points for cumulative control times will be generated
                                                    bool postctlstopaipnt)       // Analog point indicating the control remaining will be posted.
{
    int poff;

    CtiPointBase *pPt = PointMgr.getMap().findValue( &CtiHashKey(ppc.getPointID()) );

    if(pPt != 0)
    {
        CtiPointNumeric *pPoint = 0;
        double ctltime;

        if(postctlhistaipts)
        {
            for(poff = DAILYCONTROLHISTOFFSET; poff <= ANNUALCONTROLHISTOFFSET; poff++ )
            {
                pPoint = (CtiPointNumeric *)PointMgr.getOffsetTypeEqual( pPt->getDeviceID(), poff, AnalogPointType );

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

                    MainQueue_.putQueue( new CtiPointDataMsg(pPoint->getPointID(), ctltime, NormalQuality, pPoint->getType(), pPoint->getName() + " control history"));
                }
            }
        }

        if(postctlstopaipnt)
        {
            if(ppc.getControl().getControlDuration() > 0 &&
               ((now.seconds() >= ppc.getControl().getPreviousStopReportTime().seconds() - (ppc.getControl().getPreviousStopReportTime().seconds() % CntlStopInterval) + CntlStopInterval) ||
                (now > ppc.getControl().getStartTime() + ppc.getControl().getControlDuration())) &&
               (0 != (pPoint = (CtiPointNumeric *)PointMgr.getOffsetTypeEqual( pPt->getDeviceID(), CONTROLSTOPCOUNTDOWNOFFSET, AnalogPointType ))))
            {
                // We want to post to the analog which records seconds until control STOPS.

                RWTime stoptime( ppc.getControl().getStartTime() + ppc.getControl().getControlDuration() );
                ULONG remainingseconds = 0;

                if(now < stoptime)
                {
                    remainingseconds = stoptime.seconds() - now.seconds();
                }

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Control stops at " << stoptime << endl;
                    dout << "  There are " << remainingseconds << " seconds = " << remainingseconds/60 << " minutes remaining" << endl;
                }
#endif

                double ai = pPoint->computeValueForUOM((double)remainingseconds);
                MainQueue_.putQueue( new CtiPointDataMsg(pPoint->getPointID(), ai, NormalQuality, pPoint->getType(), pPoint->getName() + " control remaining"));

                ppc.getControl().setPreviousStopReportTime(now);
            }
        }
    }

    if(insertctlhistrow) ppc.getControl().Insert();
    return;
}



INT CtiVanGogh::updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList)
{
    INT status = NORMAL;
    RWCString objtype = resolveDeviceObjectType(did);

    // Is this a device-based enable or disable?

    {
        // In this case, we poke at the PAO table
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
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

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable deviceTable = getDatabase().table("device");
        RWDBUpdater updater = deviceTable.updater();

        updater.where( deviceTable["deviceid"] == did );
        updater << deviceTable["controlinhibit"].assign( RWCString((TAG_DISABLE_CONTROL_BY_DEVICE & setmask?'Y':'N')) );
        updater.execute( conn );

        status = (updater.status().isValid() ? NORMAL: UnknownError);
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(did, ChangePAODb, "Device", objtype, ChangeTypeUpdate);
    dbChange->setUser(user);
    dbChange->setSource(DISPATCH_APPLICATION_NAME);
    sigList.insert(dbChange);


    return status;
}

INT CtiVanGogh::updatePointStaticTables(LONG pid, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList)
{
    INT status = NORMAL;

    if(TAG_DISABLE_POINT_BY_POINT & tagmask)
    {
        // In this case, we poke at the PAO table
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
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

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable tbl = getDatabase().table("pointstatus");
        RWDBUpdater updater = tbl.updater();

        updater.where( tbl["pointid"] == pid );
        updater << tbl["controlinhibit"].assign( RWCString((TAG_DISABLE_CONTROL_BY_POINT & setmask?'Y':'N')) );
        updater.execute( conn );

        status = (updater.status().isValid() ? NORMAL: UnknownError);
    }

    CtiDBChangeMsg* dbChange = new CtiDBChangeMsg(pid, ChangePointDb, "Point", "Point", ChangeTypeUpdate);
    dbChange->setUser(user);
    dbChange->setSource(DISPATCH_APPLICATION_NAME);
    sigList.insert(dbChange);

    return status;
}

void CtiVanGogh::adjustDeviceDisableTags()
{
    if(!_deviceLiteSet.empty())
    {
        set< long > devicesupdated;

        UINT tagmask = TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_CONTROL_BY_DEVICE;

        CtiDeviceLiteSet_t::iterator dnit;

        for(dnit = _deviceLiteSet.begin(); dnit != _deviceLiteSet.end(); dnit++ )
        {
            CtiDeviceBaseLite &dLite = *dnit;

            UINT setmask = 0;

            setmask |= (dLite.getDisableFlag() == "Y" ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
            setmask |= (dLite.getControlInhibitFlag() == "Y" ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

            ablementDevice(dLite.getID(), setmask, tagmask, DISPATCH_APPLICATION_NAME);
        }

        {
            CtiMultiMsg *pMulti = new CtiMultiMsg;

            if(pMulti)
            {
                pMulti->setSource(DISPATCH_APPLICATION_NAME);

                /*
                 *  K.I.S.S.  Loop through each point looking for a mismatch on tags...
                 *  Yes, this sucks less than the alternative.
                 */

                CtiPointClientManager::CtiRTDBIterator  itr(PointMgr.getMap());
                for(;itr();)
                {
                    CtiPoint *pPoint = itr.value();

                    CtiDeviceLiteSet_t::iterator dliteit = deviceLiteFind(pPoint->getDeviceID());

                    if( dliteit != _deviceLiteSet.end() )   // We do know this device..
                    {
                        CtiDeviceBaseLite &dLite = *dliteit;

                        UINT setmask = 0;
                        setmask |= (dLite.getDisableFlag() == "Y" ? TAG_DISABLE_DEVICE_BY_DEVICE : 0 );
                        setmask |= (dLite.getControlInhibitFlag() == "Y" ? TAG_DISABLE_CONTROL_BY_DEVICE : 0 );

                        if(ablementPoint(pPoint->getPointID(), setmask, tagmask, DISPATCH_APPLICATION_NAME, *pMulti))
                        {
                            devicesupdated.insert( pPoint->getDeviceID() );  // Relying on the fact that only one may be in there!
                        }
                    }
                }

                if(!devicesupdated.empty())
                {
                    set< long>::iterator didset;
                    bool reloadFailed = false;

                    for(didset = devicesupdated.begin(); didset != devicesupdated.end(); didset++ )
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




void CtiVanGogh::analyzeNumericReasonability(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    if( pDyn->getLastSignal() != alarm ) // This prohibits re-alarming on the same problem!
    {
        RWCString text;

        if(alarm == CtiTablePointAlarming::highReasonability)
        {
            if(pointNumeric.getPointUnits().getHighReasonabilityLimit() != pointNumeric.getPointUnits().getLowReasonabilityLimit())       // They must be different.
            {
                if(pointNumeric.getPointUnits().getHighReasonabilityLimit() < MAX_HIGH_REASONABILITY)  // Is the reasonability reasonable?
                {
                    double val = pData->getValue();

                    if(val > pointNumeric.getPointUnits().getHighReasonabilityLimit())
                    {
                        pData->setValue( pointNumeric.getPointUnits().getHighReasonabilityLimit() );          // Value of the CtiPointDataMsg must be be modified.
                        pData->setQuality( UnreasonableQuality );
                        pDyn->setLastSignal(alarm);

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

                        pSig = new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), text, getAlarmStateName( pointNumeric.getAlarming().getAlarmStates(alarm) ), GeneralLogType, pointNumeric.getAlarming().getAlarmStates(alarm), pData->getUser());

                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
                        if(pointNumeric.getAlarming().getAlarmStates(alarm) > SignalEvent)
                        {
                            pData->setTags( TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM );
                            // Need this here in case the signal gets put on the pending list
                            pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                        }
                    }
                }
            }
        }
        else
        {
            if(pointNumeric.getPointUnits().getHighReasonabilityLimit() != pointNumeric.getPointUnits().getLowReasonabilityLimit())       // They must be different.
            {
                if(pointNumeric.getPointUnits().getLowReasonabilityLimit() > MIN_LOW_REASONABILITY)  // Is the reasonability reasonable?
                {
                    double val = pData->getValue();

                    if(val < pointNumeric.getPointUnits().getLowReasonabilityLimit())
                    {
                        pData->setValue( pointNumeric.getPointUnits().getLowReasonabilityLimit() );          // Value of the CtiPointDataMsg must be be modified.
                        pData->setQuality( UnreasonableQuality );
                        pDyn->setLastSignal(alarm);

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

                        pSig = new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), text, getAlarmStateName( pointNumeric.getAlarming().getAlarmStates(alarm) ), GeneralLogType, pointNumeric.getAlarming().getAlarmStates(alarm), pData->getUser());

                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
                        if(pointNumeric.getAlarming().getAlarmStates(alarm) > SignalEvent)
                        {
                            pData->setTags( TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM );
                            // Need this here in case the signal gets put on the pending list
                            pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                        }
                    }
                }
            }
        }
    }
}

void CtiVanGogh::analyzeNumericRateOfChange(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    if( pDyn->getLastSignal() != alarm ) // This prohibits re-alarming on the same problem!
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
                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** RATE OF CHANGE Violation **** Point: " << pointNumeric.getID() << " " << tstr << endl;
                }

                pDyn->setLastSignal(alarm);
                // OK, we have an actual alarm condition to gripe about!
                pSig = new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), tstr, getAlarmStateName( pointNumeric.getAlarming().getAlarmStates(alarm) ), GeneralLogType, pointNumeric.getAlarming().getAlarmStates(alarm), pData->getUser());
                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                if(pointNumeric.getAlarming().getAlarmStates(alarm) > SignalEvent)
                {
                    // Mark the current data message as being/causing an alarm!
                    pData->setTags( TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM );
                }
            }
        }
    }
}

/*
 *  Every point data has the opportunity to drive the value back into the nominal condition.
 */

void CtiVanGogh::analyzeLimitViolationReset( CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
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
                        pDyn->setLastSignal(-1);    // Not this one anymore!
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

void CtiVanGogh::analyzeNumericLimits(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    RWCString text;

    double  val = pData->getValue();
    INT     statelimit = (alarm - CtiTablePointAlarming::limit0);
    INT     exceeds = LIMIT_IN_RANGE;
    INT     lastpointsignal = pDyn->getLastSignal();

    try
    {
        if( lastpointsignal != alarm && pDyn->getQuality() != pData->getQuality() )          // This prohibits re-alarming on the same problem!
        {
            if(CtiTablePointAlarming::limit0 <= lastpointsignal  && lastpointsignal < alarm)
            {
                /*
                 *  The last alarm I complained about was a limit alarm
                 *  AND the limit alarm I complained about was of a higher "priority"  (lower number)
                 *  than the alarm currently under examination..
                 */

                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Already alarmed on limit " << lastpointsignal - CtiTablePointAlarming::limit0 << endl;
                    dout << "   will not check limit " << statelimit << endl;
                }
            }
            else if(pointNumeric.limitStateCheck(statelimit, val, exceeds))
            {
                pDyn->setLastSignal(alarm);

                INT duration = pointNumeric.getLimit(statelimit).getLimitDuration();

                if(exceeds == LIMIT_EXCEEDS_LO )
                {
                    char tstr[120];
                    _snprintf(tstr, sizeof(tstr), "Limit %d Exceeded Low. %.3f < %.3f", statelimit+1, val, pointNumeric.getLowLimit(statelimit));
                    text = RWCString(tstr);
                }
                else if(LIMIT_EXCEEDS_HI)
                {
                    char tstr[120];
                    _snprintf(tstr, sizeof(tstr), "Limit %d Exceeded High. %.3f > %.3f", statelimit+1, val, pointNumeric.getHighLimit(statelimit));
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

                pSig = new CtiSignalMsg(pointNumeric.getID(), pData->getSOE(), text, getAlarmStateName( pointNumeric.getAlarming().getAlarmStates(alarm) ), GeneralLogType, pointNumeric.getAlarming().getAlarmStates(alarm), pData->getUser());

                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                if(pointNumeric.getAlarming().getAlarmStates(alarm) > SignalEvent)
                {
                    if(duration <= 0)
                    {
                        // If there is no limit duration, we modify the current data message, so clients know
                        // immediately that this point is in alarm.
                        pData->setTags( TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM );
                    }
                    // Need this here in case the signal gets put on the pending list
                    pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                }

                if(duration > 0)  // Am I required to hold in this state for a bit before the announcement of this condition?
                {
                    CtiPendingPointOperations pendingPointLimit(pointNumeric.getID());
                    pendingPointLimit.setType(CtiPendingPointOperations::pendingLimit);
                    pendingPointLimit.setLimitBeingTimed( statelimit );
                    pendingPointLimit.setTime( RWTime() );
                    pendingPointLimit.setLimitDuration( duration );
                    pendingPointLimit.setSignal( pSig );
                    pSig = NULL;   // Don't let it get put in the Wrapper because it is now in the pending list!

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
                            dout << RWTime() << " **** LIMIT Violation ****  Point: " << pointNumeric.getName() << " delayed violation. Limit " << statelimit+1 << " pending alarm." << endl;
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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiVanGogh::analyzeStatusUCOS(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    UINT tags = pDyn->getDispatch().getTags();

    if((tags & TAG_ATTRIB_CONTROL_AVAILABLE))
    {
        if(!(tags & TAG_CONTROL_PENDING))
        {
            // Well, we were NOT expecting a change, so make sure the values match
            if(pDyn->getDispatch().getValue() != pData->getValue())
            {
                // Values don't match and we weren't expecting a change!  Holy COW!
                if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** UNCOMMANDEDSTATECHANGE **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                pDyn->setLastSignal(alarm);
                // OK, we have an actual alarm condition to gripe about!
                pSig = new CtiSignalMsg(point.getID(), pData->getSOE(), resolveStateName(point, (int)pData->getValue()), getAlarmStateName( point.getAlarming().getAlarmStates(alarm) ), GeneralLogType, point.getAlarming().getAlarmStates(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.

                pSig->setAdditionalInfo("UCOS");
                // This is an alarm if the alarm state indicates anything other than SignalEvent.
                if(point.getAlarming().getAlarmStates(alarm) > SignalEvent)
                {
                    pData->setTags( TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM );
                    pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
                }
            }
        }
    }
}

void CtiVanGogh::analyzeStatusCommandFail(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
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
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        ppo.getControl().setStartTime( pData->getTime() );              // Arrival of this point data indicates a control start, no longer pending!
                    }

                    ppo.setSignal(0);                                                   // No longer need to send any error signal.

                    pDyn->getDispatch().resetTags( TAG_CONTROL_PENDING );               // We got to the desired state, no longer pending.. we are now controlling!

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << resolveDeviceName(point) << " / " << point.getName() << " has gone CONTROL COMPLETE." << endl;
                    }

                    ppo.setControlState(CtiPendingPointOperations::controlInProgress);  // control has begun!
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


void CtiVanGogh::analyzeStatusState(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointBase &point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig )
{
    RWCString action;
    double val = pData->getValue();
    INT statelimit = (alarm - CtiTablePointAlarming::state0);

    if( pDyn->getLastSignal() != alarm &&
        (pDyn->getValue() != pData->getValue() ||
         pDyn->getQuality() != pData->getQuality()))          // This prohibits re-alarming on the same state!
    {
        INT exceeds = LIMIT_IN_RANGE;

        if(point.limitStateCheck(statelimit, val, exceeds))
        {
            pDyn->setLastSignal(alarm);

            if(gDispatchDebugLevel & DISPATCH_DEBUG_ALARMS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** STATE Violation **** \n" <<
                "   Point: " << point.getID() << " " << resolveStateName(point, (int)pData->getValue()) << endl;
            }

            char tstr[80];
            _snprintf(tstr, sizeof(tstr), "%s", resolveStateName(point, (int)pData->getValue()));

            pDyn->setLastSignal(alarm);
            // OK, we have an actual alarm condition to gripe about!
            pSig = new CtiSignalMsg(point.getID(), pData->getSOE(), tstr, getAlarmStateName( point.getAlarming().getAlarmStates(alarm) ), GeneralLogType, point.getAlarming().getAlarmStates(alarm), pData->getUser());                        // This is an alarm if the alarm state indicates anything other than SignalEvent.
            // This is an alarm if the alarm state indicates anything other than SignalEvent.
            if(point.getAlarming().getAlarmStates(alarm) > SignalEvent)
            {
                pData->setTags( TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM );
                pSig->setTags(TAG_UNACKNOWLEDGED_ALARM | TAG_ACKNOWLEDGED_ALARM);
            }
        }
    }
}

