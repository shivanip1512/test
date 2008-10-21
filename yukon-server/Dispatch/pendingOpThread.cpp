/*-----------------------------------------------------------------------------*
*
* File:   pendingOpThread
*
* Date:   11/2/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.39 $
* DATE         :  $Date: 2008/10/21 21:51:12 $
*
* HISTORY      :
* $Log: pendingOpThread.cpp,v $
* Revision 1.39  2008/10/21 21:51:12  jotteson
* YUK-6504 Server processes should not load all points
* Changed Dispatch to load points by pao differently than porter handles it.
* Updated tracking of paoid's loaded.
* Changed functions to require the point to exist instead of just using a point id.
*
* Revision 1.38  2008/10/13 16:25:18  jotteson
* YUK-6504 Server processes should not load all points
* Re-factor of CtiPointManager::getEqual to getPoint.
* Changed getEqualWithoutLoad to getCachedPoint.
* Removed un-necessary sleep in Dispatch's point loading threads.
*
* Revision 1.37  2008/10/02 18:27:29  mfisher
* YUK-6504 Server-side point management is naive
* Implemented first draft of LRU (least recently used) point expirations
*
* Revision 1.36  2008/04/24 19:41:50  jotteson
* YUK-4897 Load management implementation of Expresscom priorities
* Moved the handling of control status points to Dispatch.
* Enabled dispatch to know what the current control priority of any group is.
*
* Revision 1.35  2008/04/21 15:22:32  jotteson
* YUK-4897 Load management implementation of Expresscom priorities
* Added expresscom priority tracking.
* Modified tables to add priorities.
*
* Revision 1.34  2007/08/27 18:27:52  jotteson
* YUK-4279
* Changed "repeat control" functionality to copy the "control state" to the new control object.
*
* Revision 1.33  2007/03/21 21:51:19  mfisher
* Changed to say "pendingOpThread" instead of "verification thread" on shutdown
*
* Revision 1.32  2006/09/08 20:18:05  jotteson
* Fixed a bug that would cause delayed limits to not be ack-able.
*
* Revision 1.31  2006/06/16 20:04:56  jotteson
* Now modifies tags when removing a control. Can be told not to write control history if desired.
*
* Revision 1.30  2006/05/16 19:55:40  cplender
* Problems were found in the processing of the control stop point at ER.
*
* Revision 1.29  2006/05/16 15:25:52  cplender
* Problems were found in the processing of the control stop point at ER.
*
* Revision 1.28  2006/05/02 20:25:30  cplender
* Added cparm DISPATCH_COMPUTE_RESTORATION to turn on the
* ControlStopCountdown point.  Set to true to enable.
*
* Revision 1.27  2006/03/23 15:29:15  jotteson
* Mass update of point* to smart pointers. Point manager now uses smart pointers.
*
* Revision 1.26  2006/01/19 20:18:36  cplender
* Added CPARM function isTrue() cause I'm sick of doing the same thing everywhere.
*
* Revision 1.25  2006/01/05 21:05:14  cplender
* Changed a CtiQueue (sorted) to CtiFIFOQueue for speed.
*
* Revision 1.24  2006/01/05 19:30:10  cplender
* Que_t changed to Que_t typedef name.
*
* Revision 1.23  2005/12/21 22:22:07  cplender
* Altered code to make the lmctrlhistid match between dynamic an lmcontrolhistory tables.
*
* Revision 1.22  2005/12/21 18:11:56  cplender
* Altered code to make the lmctrlhistid match between dynamic an lmcontrolhistory tables.
*
* Revision 1.21  2005/12/20 17:16:57  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.20  2005/09/01 14:42:52  cplender
* Made some changes to the limit timing code.  Earlier code would not properly track limit 2.
*
* Revision 1.19  2005/07/25 16:40:53  cplender
* Working on lmcontrolhistory for Minnkota.
* Revision 1.18.2.3  2005/08/12 19:53:41  jliu
* Date Time Replaced
*
* Revision 1.18.2.2  2005/07/14 22:26:54  jliu
* RWCStringRemoved
*
* Revision 1.18.2.1  2005/07/12 21:08:35  jliu
* rpStringWithoutCmpParser
*
* Revision 1.18  2005/06/14 20:19:11  cplender
* Initial Control Condition should indicate that a control has completed.
*
* Revision 1.17  2005/06/13 19:07:48  cplender
* LM Control History would get off a bit on RESTORE operations.
*
* Revision 1.16  2005/05/24 00:39:51  cplender
* Added additional try catch on the DB writing threads which use transactions.
* Improved the use of the thread monitor.
*
* Revision 1.15  2005/02/18 14:34:28  cplender
* Make pendingOpThread announce itself every 5 like the others do.
*
* Revision 1.14  2005/02/17 19:02:57  mfisher
* Removed space before CVS comment header, moved #include "yukon.h" after CVS header
*
* Revision 1.13  2005/02/10 23:23:50  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.12  2005/01/27 17:48:46  cplender
* Make certain the _opId map is copied for pendables.
* Reduce the frequency of ctlhist AI points.
*
* Revision 1.11  2004/12/14 22:24:37  cplender
* Removed unused _actionCount
*
* Revision 1.10  2004/12/06 22:15:50  cplender
* Missing state in control InProgress.
*
* Revision 1.9  2004/12/02 22:58:43  cplender
* Try to reduce the number of pointdata messages sent to loadmanagement due to controlling groups.
*
* Revision 1.8  2004/12/01 20:15:04  cplender
* LMControlHistory.
*
* Revision 1.7  2004/11/23 14:19:30  cplender
* Working on lmcontrolhistory and slow performance
*
* Revision 1.6  2004/11/19 17:10:28  cplender
* Control History Getting real close.
*
* Revision 1.5  2004/11/18 23:56:08  cplender
* Control History Getting closer.
*
* Revision 1.4  2004/11/17 23:46:33  cplender
* Updates for GRE performance issues.
*
* Revision 1.3  2004/11/09 06:12:51  cplender
* Working to calm dispatch down
*
* Revision 1.2  2004/11/08 14:40:37  cplender
* 305 Protocol should send controls on RTCs now.
*
* Revision 1.1  2004/11/05 17:22:48  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>

#include "counter.h"
#include "cparms.h"
#include "dllvg.h"
#include "mgr_ptclients.h"
#include "msg_cmd.h"
#include "pendingopthread.h"

using namespace std;

#ifndef PERF_TO_MS
    #define PERF_TO_MS(b,a,p) ((UINT)((b).QuadPart - (a).QuadPart) / ((UINT)(p).QuadPart / 1000L))
#endif

// #define CTI_REPORTPENDINGREMOVAL TRUE

extern CtiPointClientManager  PointMgr;  // The RTDB for memory points....

extern int CntlHistInterval;
extern int CntlHistPointPostInterval;
extern int CntlStopInterval;

static LARGE_INTEGER perfFrequency;

CtiPendingOpThread::CtiPendingOpSet_t CtiPendingOpThread::_pendingControls;
CtiPendingOpThread::CtiPendingOpSet_t CtiPendingOpThread::_pendingPointData;
CtiPendingOpThread::CtiPendingOpSet_t CtiPendingOpThread::_pendingPointLimit;


CtiPendingOpThread::CtiPendingOpThread() :
_multi(0),
_pMainQueue(0)
{
    _dbThread  = rwMakeThreadFunction(*this, &CtiPendingOpThread::dbWriterThread);
    _dbThread.start();

    QueryPerformanceFrequency(&perfFrequency);
}

CtiPendingOpThread::CtiPendingOpThread(const CtiPendingOpThread& aRef)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - copy constructor called for CtiPendingOpThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPendingOpThread::~CtiPendingOpThread()
{
    _dbThread.join();
    if(_multi) delete _multi;
}

void CtiPendingOpThread::setMainQueue(CtiConnection::Que_t *pMQ)
{
    _pMainQueue = pMQ;
    return;
}

void CtiPendingOpThread::setSignalManager(CtiSignalManager *pSM)
{
    _pSignalManager = pSM;
    return;
}

void CtiPendingOpThread::run( void )
{
    UINT sanity = 0;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PendingOperationThread TID: " << CurrentTID () << endl;
    }

    set(PROCESSQ);

    try
    {
        CtiTime now;
        CtiTime lastMulti;
        _multi = new CtiMultiMsg;
        _multi->setMessagePriority(5);
        _multi->setSource("Dispatch pendingOpThread");

        while( !isSet(SHUTDOWN) )
        {
            now = now.now();

            if( isSet(PROCESSQ) )
            {
                set(PROCESSQ, false);
            }

            try
            {
                processPendableQueue();
                doPendingLimits(false);
                doPendingPointData(false);
                {
                    CtiLockGuard<CtiMutex> guard(_controlMux);
                    doPendingControls(false);
                }

                // Every 60 seconds or when the box is large/full enough.
                if(lastMulti < now || _multi->getCount() > gConfigParms.getValueAsULong("DISPATCH_MAX_CTLHIST_POINT_BATCH", 100) )
                {
                    lastMulti = nextScheduledTimeAlignedOnRate(now, gConfigParms.getValueAsULong("DISPATCH_MAX_CTLHIST_RATE", 60)) + 5L;

                    if(_multi && _multi->getCount() > 0)
                    {
                        if(_pMainQueue)
                            _pMainQueue->putQueue( _multi );
                        else
                            delete _multi;

                        _multi = new CtiMultiMsg;
                        _multi->setMessagePriority(5);
                        _multi->setSource("Dispatch pendingOpThread");
                    }
                }

                if(!(++sanity % 300))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " PendingOperationThread run() Thread Active " << endl;
                    }
                }

                sleep(1000);               // interrupt(XXX) can wake us.
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " PendingOperationThread received shutdown - processing all remaining items" << endl;
        }

        processPendableQueue();
        doPendingLimits(true);
        doPendingPointData(true);
        {
            CtiLockGuard<CtiMutex> guard(_controlMux);
            doPendingControls(true);
        }

        if(_multi && _multi->getCount() > 0)
        {
            if(_pMainQueue)
                _pMainQueue->putQueue( _multi );
            else
                delete _multi;

            _multi= 0;
        }
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Exception in CtiPendingOpThread, thread exiting" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " PendingOperationThread TID: " << CurrentTID() << " shutting down" << endl;
    }

}

void CtiPendingOpThread::push(CtiPendable *entry)
{
    if( entry )
    {
        _input.putQueue(entry);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - CtiPendingOpThread::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


/*
 *  This method processes the inbound requests made by dispatch since the last sweep.
 */
void CtiPendingOpThread::processPendableQueue()
{
    LARGE_INTEGER startTime, completeTime;
    CtiPendable   *pendable;

    int action;
    QueryPerformanceCounter(&startTime);
    while( pendable = _input.getQueue(0) )
    {
        action = pendable->getAction();

        switch( action )
        {
        case CtiPendable::CtiPendableAction_Add:
            {
                processPendableAdd(pendable);
                break;
            }
        case CtiPendable::CtiPendableAction_RemoveLimit:
            {
                removeLimit(pendable);
                break;
            }
        case CtiPendable::CtiPendableAction_RemovePointData:
            {
                removePointData(pendable);
                break;
            }
        case (CtiPendable::CtiPendableAction_ControlStatusComplete):
            {
                checkForControlBegin(pendable);
                break;
            }
        case CtiPendable::CtiPendableAction_ControlStatusChanged:
            {
                checkControlStatusChange(pendable);
                break;
            }
        case CtiPendable::CtiPendableAction_Unspecified:
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - unknown type \"" << pendable->getAction() << "\" in PendingOpThread;  deleting pendable **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }

        delete pendable;
    }

    set(QPROCESSED);
    QueryPerformanceCounter(&completeTime);

    #if 1
    if(gConfigParms.isTrue("DISPATCH_TIME_PENDING_OPS") && PERF_TO_MS(completeTime, startTime, perfFrequency) > 500)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " processPendableQueue duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
    }
    #endif
}

void CtiPendingOpThread::doPendingControls(bool bShutdown)
{
    LARGE_INTEGER startTime, completeTime;
    CtiTime now;
    static CtiTime cHistTime;
    static CtiTime cStopTime;
    static CtiTime cPostTime;

    int counter01 = 0;
    int counter02 = 0;
    int counter03 = 0;
    int counter04 = 0;
    int counter05 = 0;

    try
    {
        if(!_pMainQueue)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " MainQueue is non-valid." << endl;
            }
            return;
        }

        if( !_pendingControls.empty() )
        {
            QueryPerformanceCounter(&startTime);
            int lpcnt = 0;

            bool postCtrlHist = now >= cHistTime;
            bool postCtrlPost = now >= cPostTime;
            bool postCtrlStop = now >= cStopTime;

            if(postCtrlHist)
            {
                cHistTime = now.seconds() - (now.seconds() % CntlHistInterval) + CntlHistInterval;
            }
            if(postCtrlPost)
            {
                cPostTime = now.seconds() - (now.seconds() % CntlHistPointPostInterval) + CntlHistPointPostInterval;
            }
            if(postCtrlStop)
            {
                cStopTime = now.seconds() - (now.seconds() % CntlStopInterval) + CntlStopInterval;
            }

            // There are pending operations for points out there in the world!
            CtiPendingOpSet_t::iterator it = _pendingControls.begin();

            while( it != _pendingControls.end() )
            {
                now = now.now();

                CtiPendingPointOperations &ppo = *it;

                if(ppo.getType() == CtiPendingPointOperations::pendingControl)
                {
                    ULONG prevLogSec = ppo.getControl().getPreviousLogTime().seconds();

                    if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
                    {
                        /*
                         *  Order is important here.  Please do not rearrange the else if conditionals.
                         */
                        if(bShutdown)
                        {
                            // Record for the future!
                            updateControlHistory( ppo, CtiPendingPointOperations::dispatchShutdown, now, CtiTime(), __LINE__);
                        }
                        else if( ppo.getControl().getControlDuration() >= 0 && now.seconds() >= ppo.getControl().getControlCompleteTime())
                        {
                            /*  CONTROL IS COMPLETE!  Time it in. */
                            updateControlHistory( ppo, CtiPendingPointOperations::datachange, ppo.getControl().getControlCompleteTime(), CtiTime(), __LINE__ );    // This will write a 'T' row!
#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
                            it = erasePendingControl(it);
                            continue;   // iterator has been repositioned!
                        }
                        else if(ppo.getControl().getControlDuration() < 0)
                        {
                            /*  This is a restore command.  */
                            updateControlHistory( ppo, CtiPendingPointOperations::datachange, ppo.getControl().getStartTime(), CtiTime(), __LINE__ );    // This will write a 'M' or slight possibility of a 'T' row!
#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
                            it = erasePendingControl(it);
                            continue;   // iterator has been repositioned!
                        }
                        else
                        {
                            if(postCtrlHist)
                            {
                                // We have accumulated enough time against this control to warrant a new log entry!
                                updateControlHistory( ppo, CtiPendingPointOperations::intervalcrossing, now, CtiTime(), __LINE__);
                                counter01++;
                            }
                            else if(postCtrlPost)
                            {
                                // This rate posts history points and stop point.
                                updateControlHistory( ppo, CtiPendingPointOperations::intervalpointpostcrossing, now, CtiTime(), __LINE__);
                                counter02++;
                            }
                            else if(postCtrlStop)
                            {
                                // This rate posts stop point.  Does not affect ppo.
                                updateControlHistory( ppo, CtiPendingPointOperations::stopintervalcrossing, now, CtiTime(), __LINE__);
                                counter03++;
                            }
                        }
                    }
                    else if(ppo.getControlState() == CtiPendingPointOperations::controlPending ||
                            ppo.getControlState() == CtiPendingPointOperations::controlSentToPorter)
                    {
                        if(now.seconds() > ppo.getTime().seconds() + (ULONG)ppo.getControlTimeout())
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
                                _pMainQueue->putQueue( pCmd );
                                _pMainQueue->putQueue( pSig );

                                pCmd = 0;
                                pSig = 0;
                            }

#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
                            it = erasePendingControl(it);
                            continue;   // iterator has been repositioned!
                        }
                    }
                    else if( ppo.getControlState() == CtiPendingPointOperations::controlCompleteCommanded   ||
                             ppo.getControlState() == CtiPendingPointOperations::controlCompleteTimedIn     ||
                             ppo.getControlState() == CtiPendingPointOperations::controlCompleteManual      )
                    {
#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
                        it = erasePendingControl(it);
                        continue;   // iterator has been repositioned!
                    }
                    else if(ppo.getControlState() == CtiPendingPointOperations::controlSeasonalReset)
                    {
                        updateControlHistory( ppo, CtiPendingPointOperations::seasonReset, now, CtiTime(), __LINE__);

#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
                        it = erasePendingControl(it);
                        continue;   // iterator has been repositioned!
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " Unexpected pending operation control state " << ppo.getControlState() << endl;
                        }
                    }
                }
                else
                {
#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
                    it = erasePendingControl(it);
                    continue;   // iterator has been repositioned!
                }

                it++;
            }
            QueryPerformanceCounter(&completeTime);

            #if 1
            if(gConfigParms.isTrue("DISPATCH_TIME_PENDING_OPS") && PERF_TO_MS(completeTime, startTime, perfFrequency) > 2)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " doPendingControls count         = " << _pendingControls.size() << endl;
                dout << CtiTime() << " doPendingControls duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;

                /*
                dout << " Counter 01 " << counter01 << endl;
                dout << " Counter 02 " << counter02 << endl;
                dout << " Counter 03 " << counter03 << endl;
                dout << " Counter 04 " << counter04 << endl;
                dout << " Counter 05 " << counter05 << endl;
                */
            }
            #endif

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


void CtiPendingOpThread::doPendingPointData(bool bShutdown)
{
    LARGE_INTEGER startTime, completeTime;

    try
    {
        if(!_pMainQueue)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " MainQueue is non-valid." << endl;
            }
            return;
        }

        if( !_pendingPointData.empty() )
        {
            QueryPerformanceCounter(&startTime);
            int lpcnt = 0;
            CtiTime now;

            // There are pending operations for points out there in the world!
            CtiPendingOpSet_t::iterator it = _pendingPointData.begin();

            while( it != _pendingPointData.end() )
            {
                now = now.now();

                CtiPendingPointOperations &ppo = *it;

                if(ppo.getType() == CtiPendingPointOperations::pendingPointData)
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

                            _pMainQueue->putQueue( pData );    // Plop it out there for processing.
                            pData = 0;
                        }

                        it = _pendingPointData.erase(it);

                        continue;   // iterator has been repositioned!
                    }
                }
                else
                {
                    it = _pendingPointData.erase(it);
                    continue;   // iterator has been repositioned!
                }

                it++;
            }
            QueryPerformanceCounter(&completeTime);

            #if 1
            if(gConfigParms.isTrue("DISPATCH_TIME_PENDING_OPS") && PERF_TO_MS(completeTime, startTime, perfFrequency) > 2)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " doPendingPointDataOp duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
            }
            #endif
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

void CtiPendingOpThread::doPendingLimits(bool bShutdown)
{
    LARGE_INTEGER startTime, completeTime;

    try
    {
        if(!_pMainQueue)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " MainQueue is non-valid." << endl;
            }
            return;
        }

        if( !_pendingPointLimit.empty() )
        {
            QueryPerformanceCounter(&startTime);
            int lpcnt = 0;
            CtiTime now;

            // There are pending operations for points out there in the world!
            CtiPendingOpSet_t::iterator it = _pendingPointLimit.begin();

            while( it != _pendingPointLimit.end() )
            {
                now = now.now();

                CtiPendingPointOperations &ppo = *it;

                if(CtiPendingPointOperations::pendingLimit <= ppo.getType() && ppo.getType() <= CtiPendingPointOperations::pendingLimit + 9)    // we are a limit ppo.
                {
                    if(now.seconds() > ppo.getTime().seconds() + ppo.getLimitDuration())
                    {
                        CtiSignalMsg *pSig = (CtiSignalMsg*)ppo.getSignal()->replicateMessage();
                        pSig->setMessageTime( ppo.getTime() + ppo.getLimitDuration() );
                        pSig->setMessagePriority( MAXPRIORITY - 1 );

                        _pSignalManager->addSignal(*pSig);
                        _pMainQueue->putQueue( pSig );
                        pSig = 0;

                        it = _pendingPointLimit.erase(it);
                        continue;   // iterator has been repositioned!
                    }
                }
                else
                {
                    it = _pendingPointLimit.erase(it);
                    continue;   // iterator has been repositioned!
                }

                it++;
            }
            QueryPerformanceCounter(&completeTime);

            if(gConfigParms.isTrue("DISPATCH_TIME_PENDING_OPS") && PERF_TO_MS(completeTime, startTime, perfFrequency) > 2)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " doPendingLimitsOp duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

/*
 *  Should only be called by doPendingOperation() method!  Any other caller MAY be blocked by the DB etc!!!
 *
 *  Note that updatetime is is the time used for any duration calculations and may not represent current clock time.
 */
void CtiPendingOpThread::updateControlHistory( CtiPendingPointOperations &ppc, int cause, const CtiTime &updateTime, CtiTime &now, int line)
{
    try
    {
#ifdef CTI_REPORTPENDINGREMOVAL
        if(line)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") line " << line << " cause " << cause << endl;
            }
        }
#endif
        switch(cause)
        {
        case (CtiPendingPointOperations::newcontrol):
            {
                /*
                 *  newcontrol record should fully define a control in a way that dispatch can recover from a shutdown
                 *  accross their occurrence.
                 */
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
                   ppc.getControl().getControlDuration() != RESTORE_DURATION)
                {
                    CtiTime completiontime = ppc.getControl().getStopTime();
                    ppc.getControl().incrementTimes( updateTime, 0 );                                      // This effectively primes the entry for the next write.  Critical.
                    ppc.getControl().setStopTime( completiontime );
                    ppc.getControl().setControlCompleteTime( completiontime );                          // This is when we think this control should complete.

                    ppc.getControl().setActiveRestore( LMAR_NEWCONTROL );                               // Record this as a start interval.

                    insertControlHistoryRow(ppc, __LINE__);                                             // Drop the row in there!
                    postControlHistoryPoints(ppc, true);                                  // May ignore this if it has been posted recently.
                    postControlStopPoint(ppc, true);                                                    // Let everyone know when control should end.  Force the update.

                    ppc.getControl().setStopTime( updateTime );
                    ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );    // Reset to the original completion state.
                }

                break;
            }
        case (CtiPendingPointOperations::intervalcrossing):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = updateTime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        ppc.getControl().incrementTimes( updateTime, addnlseconds );

                        // Create some lies
                        ppc.getControl().setActiveRestore( LMAR_LOGTIMER );             // Record this as a start or continue interval.
                        insertControlHistoryRow(ppc, __LINE__);
                        postControlHistoryPoints(ppc);                                  // May ignore this if it has been posted recently.
                        postControlStopPoint(ppc, true);                               // Let everyone know when control should end.  Force the update.

                        // OK, set them out for the next run ok. Undo the lies.
                        ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );        // Reset to the original completion state.
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::intervalpointpostcrossing):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = updateTime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        CtiPendingPointOperations temporaryPPC(ppc);
                        temporaryPPC.getControl().incrementTimes( updateTime, addnlseconds );
                        postControlHistoryPoints(temporaryPPC, true);
                        postControlStopPoint(temporaryPPC, true);
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::stopintervalcrossing):
            {
                postControlStopPoint(ppc);
                break;
            }
        case (CtiPendingPointOperations::repeatcontrol):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
                   ppc.getControl().getControlDuration() != RESTORE_DURATION)     // This indicates a restore.
                {
                    LONG addnlseconds = updateTime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        ppc.getControl().setStopTime(updateTime);
                        ppc.getControl().incrementTimes( updateTime, addnlseconds );

                        insertControlHistoryRow(ppc, __LINE__);
                        postControlHistoryPoints(ppc, true);
                        postControlStopPoint(ppc, true);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::control):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = updateTime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        ppc.getControl().incrementTimes( updateTime, addnlseconds );
                        ppc.setControlState( CtiPendingPointOperations::controlCompleteCommanded );

                        insertControlHistoryRow(ppc, __LINE__);
                        postControlHistoryPoints(ppc, true);
                        postControlStopPoint(ppc, true);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        case (CtiPendingPointOperations::datachange):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = updateTime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        ppc.getControl().incrementTimes( updateTime, addnlseconds );

                        if(updateTime < ppc.getControl().getControlCompleteTime())         // The control did not run for its full duration.  It must have been manually resotred
                        {
                            ppc.getControl().setActiveRestore( LMAR_MANUAL_RESTORE );
                            ppc.setControlState( CtiPendingPointOperations::controlCompleteManual );
                        }
                        else
                        {
                            ppc.setControlState( CtiPendingPointOperations::controlCompleteTimedIn );
                        }


                        insertControlHistoryRow(ppc, __LINE__);
                        postControlHistoryPoints(ppc, true);
                        postControlStopPoint(ppc, true);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
                break;
            }
        case (CtiPendingPointOperations::seasonReset):
            {
                /*
                 *  seasonreset.  No matter the state of the control, the seasonal hours should be reset...
                 */

                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = updateTime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        ppc.getControl().incrementTimes( updateTime, addnlseconds );

                        // Create some lies
                        ppc.getControl().setActiveRestore( LMAR_CONTROLACCT_ADJUST );                         // Record this as a continuation.
                        insertControlHistoryRow(ppc, __LINE__);
                        postControlHistoryPoints(ppc);

                        // OK, set them out for the next run ok. Undo the lies.
                        ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );        // Reset to the original completion state.

                        postControlStopPoint(ppc);
                    }
                }
                else
                {
                    CtiTime writetime = ppc.getControl().getStopTime();
                    ppc.getControl().incrementTimes( updateTime, 0, true );                                // This effectively primes the entry for the write.  Seasonal hours are reset.  Critical.
                    ppc.getControl().setStopTime( writetime );
                    ppc.getControl().setControlCompleteTime( writetime );                               // This is when we think this control should complete.

                    ppc.getControl().setActiveRestore( LMAR_CONTROLACCT_ADJUST );                       // Record this as a control adjustment.

                    insertControlHistoryRow(ppc, __LINE__);                                                  // Drop the row in there!
                    postControlHistoryPoints(ppc);
                    postControlStopPoint(ppc);                                                      // Let everyone know when control should end.

                    ppc.getControl().setStopTime( updateTime );
                    ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );    // Reset to the original completion state.
                }

                break;
            }
        case (CtiPendingPointOperations::dispatchShutdown):
            {
                if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
                {
                    LONG addnlseconds = ppc.getControl().getControlCompleteTime().seconds() - ppc.getControl().getPreviousLogTime().seconds();

                    if(addnlseconds >= 0)
                    {
                        ppc.getControl().incrementTimes( updateTime, addnlseconds );
                        ppc.getControl().setStopTime( ppc.getControl().getControlCompleteTime() );
                        ppc.getControl().setActiveRestore( LMAR_DISPATCH_SHUTDOWN );
                        ppc.setControlState( CtiPendingPointOperations::controlCompleteManual );

                        insertControlHistoryRow(ppc, __LINE__);
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") AR : " << ppc.getControl().getActiveRestore(  ) << " " << ppc.getControl().getControlCompleteTime() << " < " << ppc.getControl().getPreviousLogTime() << endl;
                    }
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint: Unexpected state **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
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
}


void CtiPendingOpThread::postControlStopPoint(CtiPendingPointOperations &ppc, bool doit)
{
    CtiTime now;
    int poff;

    CtiPointNumericSPtr pPoint;

    if( gConfigParms.isTrue("DISPATCH_COMPUTE_RESTORATION") && (doit || ppc.getControl().getPreviousStopReportTime() <= now) )
    {
        if(ppc.getControl().getControlDuration() > 0)
        {
            // We want to post to the analog which records seconds until control STOPS.
            ULONG remainingseconds = 0;

            if( ppc.getControl().getControlCompleteTime() > now && ppc.getControl().getActiveRestore().compare(LMAR_MANUAL_RESTORE) )
            {
                remainingseconds = ppc.getControl().getControlCompleteTime().seconds() - now.seconds();
            }

            if(pPoint = boost::static_pointer_cast<CtiPointNumeric>(getPointOffset(ppc, ppc.getControl().getPAOID(), CONTROLSTOPCOUNTDOWNOFFSET)))
            {
                double ai = pPoint->computeValueForUOM((double)remainingseconds);
                if(_multi) _multi->insert(CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), ai, NormalQuality, pPoint->getType(), pPoint->getName() + " control remaining"));

                ppc.getControl().setPreviousStopReportTime( nextScheduledTimeAlignedOnRate( now, CntlStopInterval ) );
            }
        }
    }

    #if 0
    if(_multi && _multi->getCount() >= gConfigParms.getValueAsULong("DISPATCH_MAX_CTLHIST_POINT_BATCH", 100))
    {
        if(_pMainQueue)
            _pMainQueue->putQueue( _multi );
        else
            delete _multi;

        _multi = new CtiMultiMsg;
        _multi->setMessagePriority(5);
        _multi->setSource("Dispatch pendingOpThread");
    }
    #endif

    return;
}

void CtiPendingOpThread::postControlHistoryPoints( CtiPendingPointOperations &ppc, bool doit )
{
    CtiTime now;
    int poff;

    if( (doit || ppc.getLastHistoryPost() <= now) && !ppc.getExcludeFromHistory() )
    {
        CtiPointNumericSPtr pPoint;
        double ctltime;

        for(poff = DAILYCONTROLHISTOFFSET; poff <= ANNUALCONTROLHISTOFFSET; poff++ )
        {
            pPoint = boost::static_pointer_cast<CtiPointNumeric>(getPointOffset(ppc, ppc.getControl().getPAOID(), poff));    //(CtiPointNumeric *)PointMgr.getOffsetTypeEqual( ppc.getControl().getPAOID(), poff, AnalogPointType );

            if(pPoint)
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

                if(_multi)
                {
                    ppc.setLastHistoryPost( nextScheduledTimeAlignedOnRate( now, CntlHistPointPostInterval )  );
                    _multi->insert(CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), ctltime, NormalQuality, pPoint->getType(), pPoint->getName() + " control history"));
                }
            }
        }
    }

    #if 0
    if(_multi && _multi->getCount() >= gConfigParms.getValueAsULong("DISPATCH_MAX_CTLHIST_POINT_BATCH", 100))
    {
        if(_pMainQueue)
            _pMainQueue->putQueue( _multi );
        else
            delete _multi;

        _multi = new CtiMultiMsg;
        _multi->setMessagePriority(5);
        _multi->setSource("Dispatch pendingOpThread");
    }
    #endif

    return;
}

// This method is not threadsafe.  Do not call after startup!
bool CtiPendingOpThread::isPointInPendingControl(LONG pointid)
{
    bool stat = false;

    UINT sleepcnt = 0;

    set(QPROCESSED, false);
    set(PROCESSQ);

    if(!_pendingControls.empty())
    {
        CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pointid, CtiPendingPointOperations::pendingControl));
        stat = it != _pendingControls.end();
    }

    return(stat);
}

int CtiPendingOpThread::getCurrentControlPriority(LONG pointid)
{
    int retVal = INT_MAX;
    CtiLockGuard<CtiMutex> guard(_controlMux);

    if(!_pendingControls.empty())
    {
        CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pointid, CtiPendingPointOperations::pendingControl));
        if(it != _pendingControls.end())
        {
            retVal = it->getControl().getControlPriority();
        }
    }

    return(retVal);
}

void CtiPendingOpThread::insertControlHistoryRow( CtiPendingPointOperations &ppc, int line)
{
    createOrUpdateICControl(ppc.getControl().getPAOID(), ppc.getControl());     // This keeps it current in mem.

    if(ppc.getControl().getStopTime() >= ppc.getControl().getStartTime() && !ppc.getExcludeFromHistory())
    {
        CtiTableLMControlHistory *pTbl = CTIDBG_new CtiTableLMControlHistory(ppc.getControl());
        LONG chid = LMControlHistoryIdGen();
        pTbl->setLMControlHistoryID( chid );
        _dynLMControlHistoryQueue.putQueue( pTbl );

        if(ppc.getControl().getActiveRestore() != LMAR_LOGTIMER &&
           ppc.getControl().getActiveRestore() != LMAR_DISPATCH_SHUTDOWN)
        {
            pTbl = CTIDBG_new CtiTableLMControlHistory(ppc.getControl());
            pTbl->setLMControlHistoryID( chid );
            _lmControlHistoryQueue.putQueue( pTbl );
        }

        if(ppc.getControl().isNewControl())
        {
            ppc.getControl().setNotNewControl();
            ppc.getControl().setPreviousLogTime( ppc.getControl().getStartTime() );
        }
        else if(ppc.getControl().getActiveRestore() != string(LMAR_NEWCONTROL))
        {
            ppc.getControl().setPreviousLogTime( ppc.getControl().getStopTime() );
        }
    }

    return;
}

/*
 *  This method keeps the "Initial Condition" map current.  This map is used if there is no control in flight and the
 *  control time values need to be primed.  Otherwise, the control times come from the in flight pendable.
 */
bool CtiPendingOpThread::createOrUpdateICControl(long paoid, CtiTableLMControlHistory &lmch )
{
    bool bres = false;
    lmch.setDirty();        // Mark as dirty so we know who needs to be written out.
    CtiICLMControlHistMap_t::iterator fitr = _initialConditionControlHistMap.find( paoid );

    if(fitr != _initialConditionControlHistMap.end())
    {
        // It was found
        CtiICLMControlHistMap_t::value_type &vt = *fitr;
        CtiTableLMControlHistory &origLMCH = vt.second;

        origLMCH = lmch;
    }
    else
    {
        // Try to put it in there!
        pair< CtiICLMControlHistMap_t::iterator, bool > resultpair = _initialConditionControlHistMap.insert( make_pair(paoid, lmch) );

        if(resultpair.second == false)           // Insert was unsuccessful.
        {
            CtiICLMControlHistMap_t::iterator itr = resultpair.first;
            if(itr != _initialConditionControlHistMap.end())
            {
                CtiICLMControlHistMap_t::value_type &vt = *fitr;
                CtiTableLMControlHistory &origLMCH = vt.second;

                origLMCH = lmch;
           }
        }

        bres = resultpair.second;
    }

    return bres;
}

#define DUMP_RATE 30
#define MAX_ARCHIVER_ENTRIES 100
#define MAX_DYNLMQ_ENTRIES 500
#define PANIC_CONSTANT 1000

void CtiPendingOpThread::writeLMControlHistoryToDB(bool justdoit)
{
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
                string controlHistory("controlHistory");
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                conn.beginTransaction(string2RWCString(controlHistory));

                try
                {
                    while( conn.isValid() && ( justdoit || (panicCounter < PANIC_CONSTANT) ) && (pTblEntry = _lmControlHistoryQueue.getQueue(0)) != NULL)
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

                conn.commitTransaction(string2RWCString(controlHistory));
            }
            if( panicCounter > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " LMControlHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " LM Control History queue has " << _lmControlHistoryQueue.entries() << " entries" << endl;
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


void CtiPendingOpThread::writeDynamicLMControlHistoryToDB(bool justdoit)
{
    static UINT  dumpCounter = 0;
    UINT         panicCounter = 0;      // Make sure we don't write for too long...

    try
    {
        CtiTableLMControlHistory *pTblEntry;
        size_t lmentries = _dynLMControlHistoryQueue.entries();

        /*
         *  Go look if we need to write out archive points.
         *  We only do this once every 30 seconds or if there are >= 10 entries to do.
         */
        if(!(++dumpCounter % (DUMP_RATE))
           || lmentries > MAX_DYNLMQ_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            if(lmentries > 0)
            {
                string controlHistory("dynamicControlHistory");
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                conn.beginTransaction(string2RWCString(controlHistory));

                try
                {
                    while( conn.isValid() && ( justdoit || (panicCounter < PANIC_CONSTANT) ) && (pTblEntry = _dynLMControlHistoryQueue.getQueue(0)) != NULL)
                    {
                        panicCounter++;
                        pTblEntry->UpdateDynamic(conn);
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

                conn.commitTransaction(string2RWCString(controlHistory));
            }
            if( panicCounter > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Dynamic LMControlHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " Dynamic LM Control History queue has " << _dynLMControlHistoryQueue.entries() << " entries" << endl;
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

CtiPendingOpThread::CtiICLMControlHistMap_t CtiPendingOpThread::_initialConditionControlHistMap;

void CtiPendingOpThread::resetICControlMap()
{
    _initialConditionControlHistMap.clear();
    return;
}

bool CtiPendingOpThread::loadICControlMap()
{
    bool cleanShutdown = false;             // Set to true if there is at least one "S" in the table indicating that dispatch was shutdown.
    bool allControlsCompleted = true;       // Set to false if each and every control in the dyn table is either "T" or "M" complete.

    try
    {
        resetICControlMap();

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBDatabase   db       = conn.database();
        RWDBSelector   selector = conn.database().selector();
        RWDBTable      keyTable;
        RWDBReader     rdr;

        CtiTableLMControlHistory::getDynamicSQL( db, keyTable, selector );
        rdr = selector.reader( conn );

        if(rdr.status().errorCode() != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
        }

        CtiTime now;
        while( rdr() )
        {
            long paoid = 0;
            rdr["paobjectid"] >> paoid;

            CtiTableLMControlHistory dynC;
            dynC.DecodeDatabaseReader(rdr);

            if(!stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_DISPATCH_SHUTDOWN) ||
               !stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_NEWCONTROL))
            {
                if( now >= dynC.getStopTime() )  // This control completed during dispatch's shutdown.
                {
                    dynC.setNotNewControl();
                    dynC.setActiveRestore(LMAR_TIMED_RESTORE);
                    dynC.Insert();                                  // Insert into the lmcontrolhistory
                    dynC.UpdateDynamic();                          // Update the dynamiclmcontrolhistory;
                }
                else
                {
                    LONG inc = now.seconds() - dynC.getStopTime().seconds();        // This should be negative and back us up!
                    dynC.setNotNewControl();
                    dynC.setControlCompleteTime(dynC.getStopTime());
                    dynC.incrementTimes(now, inc);
                    dynC.setDefaultActiveRestore(LMAR_TIMED_RESTORE);
                    dynC.setActiveRestore(LMAR_LOGTIMER);
                    dynC.UpdateDynamic();                          // Update the dynamiclmcontrolhistory;
                    dynC.setPreviousLogTime(now);

                    CtiPointSPtr pPt = PointMgr.getControlOffsetEqual(dynC.getPAOID(),  1);
                    if(pPt)
                    {
                        // This control is active and should be added to the pendingControlList!
                        CtiPendingPointOperations ppc(pPt->getPointID());
                        ppc.setType(CtiPendingPointOperations::pendingControl);

                        ppc.setControlState( CtiPendingPointOperations::controlInProgress );
                        ppc.setTime( dynC.getStartTime() );
                        ppc.setControl(dynC);

                        _pendingControls.insert( ppc ); // Writes to this set in this way can only occur prior to the thread starting up.  All others via processPendableQueue
                    }
                }

                cleanShutdown = true;           // Shutdown was clean we do not need to recover from lmctrlhist.
            }
            else if( !stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_LOGTIMER) )
            {
                if( now >= dynC.getStopTime() )  // This control completed during dispatch's shutdown.
                {
                    dynC.setNotNewControl();
                    dynC.setActiveRestore(LMAR_TIMED_RESTORE);
                    dynC.Insert();                                  // Insert into the lmcontrolhistory
                    dynC.UpdateDynamic();                           // Update the dynamiclmcontrolhistory;
                }
                else
                {
                    LONG inc = now.seconds() - dynC.getStopTime().seconds();
                    dynC.setNotNewControl();
                    dynC.setControlCompleteTime(dynC.getStopTime());    // This is still in the future.  Not very likely.
                    dynC.incrementTimes(now, inc);
                    dynC.setDefaultActiveRestore(LMAR_TIMED_RESTORE);
                    dynC.setActiveRestore(LMAR_LOGTIMER);
                    dynC.UpdateDynamic();                          // Update the dynamiclmcontrolhistory;
                    dynC.setPreviousLogTime(now);

                    CtiPointSPtr pPt = PointMgr.getControlOffsetEqual(dynC.getPAOID(),  1);
                    if(pPt)
                    {
                        // This control is active and should be added to the pendingControlList!
                        CtiPendingPointOperations ppc(pPt->getPointID());
                        ppc.setType(CtiPendingPointOperations::pendingControl);
                        ppc.setControlState( CtiPendingPointOperations::controlInProgress );
                        ppc.setTime( dynC.getStartTime() );
                        ppc.setControl(dynC);

                        insertPendingControl( ppc ); // Writes to this set in this way can only occur prior to the thread starting up.  All others via processPendableQueue
                    }
                }
            }
            else if(!cleanShutdown && allControlsCompleted)
            {
                if(!(!stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_MANUAL_RESTORE) ||
                     !stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_TIMED_RESTORE)))
                {
                    allControlsCompleted = false;       // All controls in the list were not completed.
                }
            }

            if( !stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_MANUAL_RESTORE) ||
                 !stringCompareIgnoreCase(dynC.getLoadedActiveRestore(),LMAR_TIMED_RESTORE) )
            {
                dynC.setActiveRestore(dynC.getLoadedActiveRestore());       // This is a completed control!
            }

            createOrUpdateICControl(paoid, dynC);
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if(cleanShutdown || allControlsCompleted)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Dynamic LMControlHistory was complete, no deep analysis required." << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dynamic LMControlHistory was incomplete, _deep_ analysis required." << endl;
    }

    return(cleanShutdown || allControlsCompleted);
}

bool CtiPendingOpThread::getICControlHistory( CtiTableLMControlHistory &lmch )
{
    CtiTime now;
    bool found = false;
    static CtiTime lastLoadCheck(PASTDATE);

    if(_initialConditionControlHistMap.empty() && (lastLoadCheck + 300 < now) )        // Not more than once per 5 minutes...
    {
        lastLoadCheck = lastLoadCheck.now();
        loadICControlMap();
    }

    if(!_initialConditionControlHistMap.empty())
    {
        CtiICLMControlHistMap_t::iterator itr = _initialConditionControlHistMap.find(lmch.getPAOID());

        if( itr != _initialConditionControlHistMap.end() )
        {
            found = true;

            lmch.setCurrentDailyTime((*itr).second.getCurrentDailyTime());
            lmch.setCurrentMonthlyTime((*itr).second.getCurrentMonthlyTime());
            lmch.setCurrentSeasonalTime((*itr).second.getCurrentSeasonalTime());
            lmch.setCurrentAnnualTime((*itr).second.getCurrentAnnualTime());
            lmch.setActiveRestore((*itr).second.getActiveRestore());

            lmch.setPreviousLogTime((*itr).second.getPreviousLogTime());
            lmch.setPreviousStopReportTime((*itr).second.getPreviousStopReportTime());
        }
    }

    if(!found)
    {
        lmch.setCurrentDailyTime(0);
        lmch.setCurrentMonthlyTime(0);
        lmch.setCurrentSeasonalTime(0);
        lmch.setCurrentAnnualTime(0);
    }

    return found;
}


void CtiPendingOpThread::checkForControlBegin( CtiPendable *&pendable )
{
    CtiLockGuard<CtiMutex> guard(_controlMux);
    CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pendable->_pointID, CtiPendingPointOperations::pendingControl));

    if( it != _pendingControls.end() )
    {
        // OK, we just got a change in value on a Status type point, and it is awaiting control!
        CtiPendingPointOperations &ppo = *it;

        if( ppo.isInControlCompleteState(pendable->_value) )                // We are in the control state (value)?
        {
            if(pendable->_tags & TAG_CONTROL_PENDING)                       // Are we still awaiting the start of control?
            {
                if(ppo.getControl().getStartTime() == CtiTime(YUKONEOT) )
                {
                    ppo.getControl().setStartTime( pendable->_time );       // Arrival of this point data indicates a control start, no longer pending!
                }

                ppo.setSignal(0);                                           // No longer need to send any error signal.

                if(ppo.getControlState() != CtiPendingPointOperations::controlInProgress)
                {
                    ppo.setControlState(CtiPendingPointOperations::controlInProgress);  // control has begun!
                    updateControlHistory( ppo, CtiPendingPointOperations::newcontrol, pendable->_time, CtiTime(), __LINE__ );
                }
            }
            else
            {
                updateControlHistory( ppo, CtiPendingPointOperations::repeatcontrol, pendable->_time, CtiTime(), __LINE__ );
            }
        }
    }
}

/*
 *  We get here if a controllable point changed value and WAS not in a control pending state.
 *  This means a manual update, or the delayed data message is pushing our group out of control.
 */
void CtiPendingOpThread::checkControlStatusChange( CtiPendable *&pendable )
{
    CtiLockGuard<CtiMutex> guard(_controlMux);
    CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pendable->_pointID, CtiPendingPointOperations::pendingControl));

    if( it != _pendingControls.end() )
    {
        // OK, we just got a change in value on a Status type point, and it is awaiting control!
        CtiPendingPointOperations &ppo = *it;

        if( pendable->_value != ppo.getControlCompleteValue() )  // One final check to make sure we are NOT in the controlled state (value)?
        {
            // No longer in the controlcomplete state... because of a pointdata (Manual Change or TimedIn???)
            updateControlHistory( ppo, CtiPendingPointOperations::datachange, pendable->_time, CtiTime(), __LINE__ );
#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
            erasePendingControl(it);
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

void CtiPendingOpThread::removeControl(CtiPendable *&pendable)
{
    CtiLockGuard<CtiMutex> guard(_controlMux);
    CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pendable->_pointID, CtiPendingPointOperations::pendingControl));

    if( it != _pendingControls.end() )
    {
#ifdef CTI_REPORTPENDINGREMOVAL
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** REMOVAL Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
#endif
        it = erasePendingControl(it);
    }
}

void CtiPendingOpThread::removeLimit(CtiPendable *&pendable)
{
    if( !_pendingPointLimit.empty() )
    {
        // There are pending operations for points out there in the world!
        CtiPendingOpSet_t::iterator it = _pendingPointLimit.begin();

        while( it != _pendingPointLimit.end() )
        {
            CtiPendingPointOperations &ppo = *it;

            if(ppo.getPointID() == pendable->_pointID &&
               (CtiPendingPointOperations::pendingLimit <= ppo.getType() && ppo.getType() <= CtiPendingPointOperations::pendingLimit + 9))    // we are a limit ppo.
            {
                if(pendable->_limit == ppo.getLimitBeingTimed())
                {
#ifdef CTI_REPORTPENDINGREMOVAL
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** removeLimit Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
#endif
                    it = _pendingPointLimit.erase(it);
                    continue;   // iterator has been repositioned!
                }
            }
            it++;
        }
    }
}

void CtiPendingOpThread::removePointData(CtiPendable *&pendable)
{
    if( !_pendingPointData.empty() )
    {
        CtiPendingOpSet_t::iterator it = _pendingPointData.find(CtiPendingPointOperations(pendable->_pointID, CtiPendingPointOperations::pendingPointData));

        if( it != _pendingPointData.end() )
        {
#ifdef CTI_REPORTPENDINGREMOVAL
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** removePointData Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
#endif
            it = _pendingPointData.erase(it);
        }
    }
}

void CtiPendingOpThread::processPendableAdd(CtiPendable *&pendable)
{
    CtiTime tempTime;

    if(pendable->_ppo)
    {
        switch(pendable->_ppo->getType())
        {
        case (CtiPendingPointOperations::pendingControl):
            {
                CtiLockGuard<CtiMutex> guard(_controlMux);
                pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                resultpair = insertPendingControl( *pendable->_ppo );            // Add a copy (ppo) to the pending operations.

                CtiPendingPointOperations &ppo = *resultpair.first;

                if(resultpair.second != true)
                {
                    /*
                     *  We only need to "do something" if the earlier control had gone into the controlInProgress state.
                     *  Otherwise it is waiting or completed and left over.
                     */
                    pendable->_ppo->setOffsetMap(ppo.getOffsetMap());   // Try to reduce the number of pointmap searches.

                    // If this is a control and the new control is at least as high of priority as the previous
                    // Remember that 0 is high priority and higher than 0 is lower
                    // priority. If old priority value >= new, the new can
                    // override the old. By default most things will have priority 0.
                    if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress &&
                       ppo.getControl().getControlPriority() >= pendable->_ppo->getControl().getControlPriority())
                    {
                        tempTime = pendable->_time >= ppo.getControl().getPreviousLogTime() ? pendable->_time : ppo.getControl().getPreviousLogTime();

                        if( pendable->_ppo->getControlState() == CtiPendingPointOperations::controlSeasonalReset )
                        {
                            string origType = ppo.getControl().getControlType();
                            ppo.getControl().setControlType(pendable->_ppo->getControl().getControlType());
                            ppo.getControl().setCurrentSeasonalTime(0);
                            updateControlHistory( ppo, CtiPendingPointOperations::seasonReset, tempTime, CtiTime(), __LINE__ );

                            ppo.getControl().setControlType(origType);
                        }
                        else if( pendable->_ppo->getControl().getControlDuration() <= 0 )
                        {
                            // The incoming control information is a RESTORE type operation.
                            ppo.getControl().setActiveRestore(LMAR_MANUAL_RESTORE);
                            updateControlHistory( ppo, CtiPendingPointOperations::control, tempTime, CtiTime(), __LINE__ );

                            pendable->_ppo->getControl().setControlDuration(RESTORE_DURATION);
                            pendable->_ppo->getControl().setCurrentDailyTime(ppo.getControl().getCurrentDailyTime());
                            pendable->_ppo->getControl().setCurrentMonthlyTime(ppo.getControl().getCurrentMonthlyTime());
                            pendable->_ppo->getControl().setCurrentSeasonalTime(ppo.getControl().getCurrentSeasonalTime());
                            pendable->_ppo->getControl().setCurrentAnnualTime(ppo.getControl().getCurrentAnnualTime());
                            pendable->_ppo->getControl().setSoeTag(ppo.getControl().getSoeTag());
                            pendable->_ppo->getControl().setStartTime(tempTime);

                            ppo = *pendable->_ppo;    // Copy it to update the control state.
                        }
                        else if( pendable->_ppo->getControl().getControlType() == ppo.getControl().getControlType() )
                        {
                            // The control command we just received is the same command as that which started the prior control.
                            // This is a repeat/continuation of the old command!  We just record that and continue.
                            CtiTime logTime = pendable->_ppo->getControl().getStartTime() >= ppo.getControl().getPreviousLogTime() ? pendable->_ppo->getControl().getStartTime() : ppo.getControl().getPreviousLogTime();
                            ppo.getControl().setActiveRestore(LMAR_CONT_CONTROL);
                            ppo.getControl().setStopTime(logTime);

                            updateControlHistory( ppo, CtiPendingPointOperations::repeatcontrol, logTime, CtiTime(), __LINE__ );  // Recoreds the 'C' row (LMAR_CONT_CONTROL)

                            pendable->_ppo->getControl().setNotNewControl();

                            pendable->_ppo->getControl().setStartTime(ppo.getControl().getStartTime());
                            pendable->_ppo->getControl().setPreviousLogTime(logTime);                                           // FIX ?? CGP // ppo.getControl().getPreviousLogTime());
                            pendable->_ppo->getControl().setControlCompleteTime(pendable->_ppo->getControl().getStopTime());

                            pendable->_ppo->getControl().setCurrentDailyTime(ppo.getControl().getCurrentDailyTime());
                            pendable->_ppo->getControl().setCurrentMonthlyTime(ppo.getControl().getCurrentMonthlyTime());
                            pendable->_ppo->getControl().setCurrentSeasonalTime(ppo.getControl().getCurrentSeasonalTime());
                            pendable->_ppo->getControl().setCurrentAnnualTime(ppo.getControl().getCurrentAnnualTime());

                            pendable->_ppo->setControlState(ppo.getControlState());

                            ppo = *pendable->_ppo;    // Copy it to update the control state.
                        }
                        else
                        {
                            // A new and different control command has arrived.  We need to record the old command as having
                            // been overridden.
                            ppo.getControl().setActiveRestore(LMAR_OVERRIDE_CONTROL);
                            updateControlHistory( ppo, CtiPendingPointOperations::control, tempTime, CtiTime(), __LINE__ );

                            pendable->_ppo->getControl().setCurrentDailyTime(ppo.getControl().getCurrentDailyTime());
                            pendable->_ppo->getControl().setCurrentMonthlyTime(ppo.getControl().getCurrentMonthlyTime());
                            pendable->_ppo->getControl().setCurrentSeasonalTime(ppo.getControl().getCurrentSeasonalTime());
                            pendable->_ppo->getControl().setCurrentAnnualTime(ppo.getControl().getCurrentAnnualTime());
                            pendable->_ppo->getControl().setPreviousLogTime(ppo.getControl().getPreviousLogTime());

                            ppo = *pendable->_ppo;    // Copy it to update the control state.
                        }
                    }
                    else if( ppo.getControl().getControlPriority() >= pendable->_ppo->getControl().getControlPriority() )
                    {
                        pendable->_ppo->getControl().setCurrentDailyTime(ppo.getControl().getCurrentDailyTime());
                        pendable->_ppo->getControl().setCurrentMonthlyTime(ppo.getControl().getCurrentMonthlyTime());
                        pendable->_ppo->getControl().setCurrentSeasonalTime(ppo.getControl().getCurrentSeasonalTime());
                        pendable->_ppo->getControl().setCurrentAnnualTime(ppo.getControl().getCurrentAnnualTime());
                        pendable->_ppo->getControl().setPreviousLogTime(ppo.getControl().getPreviousLogTime());

                        ppo = *pendable->_ppo;    // Copy it to update the control state.
                    }
                }
                else
                {
                    // It went in clean.
                    // Make certain new controls know about any predecessor controls' control times.
                    getICControlHistory( ppo.getControl() );
                }

                break;
            }
        case (CtiPendingPointOperations::pendingPointData):
            {
                pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                resultpair = _pendingPointData.insert( *pendable->_ppo );            // Add a copy (ppo) to the pending operations.

                CtiPendingPointOperations &ppo = *resultpair.first;

                if(resultpair.second != true)
                {
                    ppo = *pendable->_ppo;    // Copy it.
                }

                break;
            }
        case (CtiPendingPointOperations::pendingLimit):
        case (CtiPendingPointOperations::pendingLimit + 1):
        case (CtiPendingPointOperations::pendingLimit + 2):
        case (CtiPendingPointOperations::pendingLimit + 3):
        case (CtiPendingPointOperations::pendingLimit + 4):
        case (CtiPendingPointOperations::pendingLimit + 5):
        case (CtiPendingPointOperations::pendingLimit + 6):
        case (CtiPendingPointOperations::pendingLimit + 7):
        case (CtiPendingPointOperations::pendingLimit + 8):
        case (CtiPendingPointOperations::pendingLimit + 9):
            {
                pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                resultpair = _pendingPointLimit.insert( *pendable->_ppo );            // Add a copy (ppo) to the pending operations.

                CtiPendingPointOperations &ppo = *resultpair.first;

                if(resultpair.second != true)
                {
                    CtiTime origTime = ppo.getTime();
                    ppo = *pendable->_ppo;              // Copy it.
                    ppo.setTime(origTime);              // Don't move the time.  The violation timer requires the initial time!
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }
    }
}


void CtiPendingOpThread::dbWriterThread()
{
    UINT sanity = 0;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Dispatch PendingOp DB Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
    }

    SetThreadPriority(GetCurrentThread(), THREAD_PRIORITY_ABOVE_NORMAL);

    try
    {
        for(;!isSet(CtiThread::SHUTDOWN);)
        {
            try
            {
                if(!(++sanity % 300))
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " PendingOp DB Writer Thread Active " << endl;
                    }
                }

                rwSleep(1000);

                writeDynamicLMControlHistoryToDB();
                writeLMControlHistoryToDB();
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }

        // Make sure no one snuck in under the wire..
        writeDynamicLMControlHistoryToDB(true);
        writeLMControlHistoryToDB(true);

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
        dout << CtiTime() << " Dispatch PendingOp DB Writer Thread shutdown" << endl;
    }

    return;
}

//FIX_ME!!!
CtiPointNumericSPtr CtiPendingOpThread::getPointOffset(CtiPendingPointOperations &ppc, long pao, int poff)
{
    CtiPointNumericSPtr pPoint;

    long fpid = ppc.getOffsetsPointID(poff);

    if(fpid)
    {
        pPoint = boost::static_pointer_cast<CtiPointNumeric>(PointMgr.getPoint(fpid));
        if(pPoint->getPointOffset() != poff) pPoint.reset();
    }

    if(!pPoint)
    {
        pPoint = boost::static_pointer_cast<CtiPointNumeric>(PointMgr.getOffsetTypeEqual(pao, poff, AnalogPointType ));
        if(pPoint) ppc.addOffset(poff, pPoint->getPointID());
    }

    return pPoint;
}

CtiPendingOpThread::CtiPendingOpSet_t::iterator CtiPendingOpThread::erasePendingControl(CtiPendingOpThread::CtiPendingOpSet_t::iterator iter)
{
    CtiPointSPtr point = PointMgr.getPoint(iter->getPointID());
    if( point )
    {
        CtiDynamicPointDispatch *pDyn = PointMgr.getDynamic(point);
        if( pDyn != NULL )
        {
            pDyn->getDispatch().resetTags( TAG_CONTROL_PENDING );
        }
    }
    return _pendingControls.erase(iter);
}

pair< CtiPendingOpThread::CtiPendingOpSet_t::iterator, bool > CtiPendingOpThread::insertPendingControl(CtiPendingPointOperations &ppo)
{
    return _pendingControls.insert(ppo);
}
