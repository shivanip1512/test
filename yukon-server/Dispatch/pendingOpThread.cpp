
/*-----------------------------------------------------------------------------*
*
* File:   pendingOpThread
*
* Date:   11/2/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/11/05 17:22:48 $
*
* HISTORY      :
* $Log: pendingOpThread.cpp,v $
* Revision 1.1  2004/11/05 17:22:48  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include <windows.h>

#include "counter.h"
#include "dllvg.h"
#include "mgr_ptclients.h"
#include "msg_cmd.h"
#include "pendingopthread.h"
#include "pt_numeric.h"

#ifndef PERF_TO_MS
    #define PERF_TO_MS(b,a,p) ((UINT)((b).QuadPart - (a).QuadPart) / ((UINT)(p).QuadPart / 1000L))
#endif

extern CtiPointClientManager  PointMgr;  // The RTDB for memory points....

extern int CntlHistInterval;
extern int CntlHistPointPostInterval;
extern int CntlStopInterval;

static LARGE_INTEGER perfFrequency;

CtiPendingOpThread::CtiPendingOpThread() :
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
        dout << RWTime() << " **** Checkpoint - copy constructor called for CtiPendingOpThread **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiPendingOpThread::~CtiPendingOpThread()
{
    _dbThread.join();
}

void CtiPendingOpThread::setMainQueue(CtiConnection::InQ_t *pMQ)
{
    _pMainQueue = pMQ;
    return;
}

void CtiPendingOpThread::run( void )
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PendingOperationThread TID: " << CurrentTID () << endl;
    }

    set(RELOAD);

    try
    {
        while( !isSet(SHUTDOWN) )
        {
            if( isSet(RELOAD) )
            {
                set(RELOAD, false);
            }

            processPendableQueue();
            doPendingLimits(false);
            doPendingPointData(false);
            doPendingControls(false);

            sleep(5000);               // interrupt(XXX) can wake us.
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Verification thread received shutdown - writing all pending codes to DB" << endl;
        }

        doPendingLimits(true);
        doPendingPointData(true);
        doPendingControls(true);
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Exception in CtiPendingOpThread, thread exiting" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " PendingOperationThread TID: " << CurrentTID() << " shutting down" << endl;
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
        dout << RWTime() << " **** Checkpoint - CtiPendingOpThread::push cannot enqueue null pointer **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


/*
 *  This method processes the inbound requests made by dispatch since the last sweep.
 */
void CtiPendingOpThread::processPendableQueue()
{
    LARGE_INTEGER startTime, completeTime;
    CtiPendable   *pendable;
    CtiCounter    _actionCount;


    #if 0
    if(_input.size())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")  Input Queue Size " << _input.size() << endl;
        }
    }
    #endif

    int action;
    QueryPerformanceCounter(&startTime);
    while( pendable = _input.getQueue(0) )
    {
        action = pendable->getAction();
        _actionCount.inc(action);

        switch( action )
        {
        case CtiPendable::CtiPendableAction_Add:
            {
                processPendableAdd(pendable);

                break;
            }
        case CtiPendable::CtiPendableAction_Remove:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
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
                    dout << RWTime() << " **** Checkpoint - unknown type \"" << pendable->getAction() << "\" in PendingOpThread;  deleting pendable **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                break;
            }
        }

        delete pendable;
    }

    QueryPerformanceCounter(&completeTime);

    #if 0
    // if(PERF_TO_MS(completeTime, startTime, perfFrequency) > 50)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " processPendableQueue duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
    }

    for(action = 0; action <= CtiPendable::CtiPendableAction_ControlStatusChanged; action++)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Action " << action << " had " << _actionCount.get(action) << endl;
        }
    }
    #endif
}


void CtiPendingOpThread::doPendingControls(bool bShutdown)
{
    LARGE_INTEGER startTime, completeTime;

    try
    {
        if(!_pMainQueue)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " MainQueue is non-valid." << endl;
            }
            return;
        }

        if( !_pendingControls.empty() )
        {
            QueryPerformanceCounter(&startTime);
            int lpcnt = 0;
            RWTime now;

            #if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " doPendingControls count = " << _pendingControls.size() << endl;
            }
            #endif
            // There are pending operations for points out there in the world!
            CtiPendingOpSet_t::iterator it = _pendingControls.begin();

            while( it != _pendingControls.end() )
            {
                now = now.now();

                CtiPendingPointOperations &ppo = *it;

                if(ppo.getType() == CtiPendingPointOperations::pendingControl)
                {
                    ULONG prevLogSec = ppo.getControl().getPreviousLogTime().seconds();

                    if(ppo.getControlState() == CtiPendingPointOperations::controlSeasonalReset)
                    {
                        updateControlHistory( ppo, CtiPendingPointOperations::seasonReset, now);

                        it = _pendingControls.erase(it);
                        continue;   // iterator has been repositioned!
                    }
                    else if(ppo.getControlState() == CtiPendingPointOperations::controlPending)
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

                            try
                            {
                                it = _pendingControls.erase(it);
                                continue;   // iterator has been repositioned!
                            }
                            catch(...)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }
                    }
                    else if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
                    {
                        /*
                         *  Order is important here.  Please do not rearrange the else if conditionals.
                         */
                        if(bShutdown)
                        {
                            // Record for the future!
                            updateControlHistory( ppo, CtiPendingPointOperations::dispatchShutdown, now);
                        }
                        else if( ppo.getControl().getControlDuration() >= 0 &&
                                 now.seconds() >= ppo.getControl().getStartTime().seconds() + ppo.getControl().getControlDuration())
                        {
                            /*  Do NOTHING.  CONTROL IS COMPLETE! Don't tally any more time though.. */
                            if(gDispatchDebugLevel & 0x00000001)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << " Start + control duration are < now not gonna write any more..." << endl;
                                }
                            }
                        }
                        else if(ppo.getControl().getControlDuration() < 0)
                        {
                            /*  Do NOTHING.  This is a restore command.  Don't tally any more time though.. */
                            if(gDispatchDebugLevel & 0x00000001)
                            {
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                    dout << "  Control Duration is less than zero" << endl;
                                }
                            }
                        }
                        else
                        {
                            if(now.seconds() >= prevLogSec - (prevLogSec % CntlHistInterval) + CntlHistInterval)
                            {
                                // We have accumulated enough time against this control to warrant a new log entry!
                                updateControlHistory( ppo, CtiPendingPointOperations::intervalcrossing, now);
                            }
                            else if(CntlHistPointPostInterval && !(now.seconds() % CntlHistPointPostInterval))
                            {
                                // This rate posts history points and stop point.
                                updateControlHistory( ppo, CtiPendingPointOperations::intervalpointpostcrossing, now);
                            }
                            else if(ppo.getControl().getControlDuration() > 0 &&
                                    now.seconds() >= ppo.getControl().getPreviousStopReportTime().seconds() - (ppo.getControl().getPreviousStopReportTime().seconds() % CntlStopInterval) + CntlStopInterval)
                            {
                                // This rate posts stop point.
                                updateControlHistory( ppo, CtiPendingPointOperations::stopintervalcrossing, now);
                            }
                        }
                    }
                    else if( ppo.getControlState() == CtiPendingPointOperations::controlCompleteCommanded   ||
                             ppo.getControlState() == CtiPendingPointOperations::controlCompleteTimedIn     ||
                             ppo.getControlState() == CtiPendingPointOperations::controlCompleteManual      )
                    {
                        try
                        {
                            it = _pendingControls.erase(it);
                            continue;   // iterator has been repositioned!
                        }
                        catch(...)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " Unexpected pending operation control state " << ppo.getControlState() << endl;
                        }
                    }
                }
                else
                {
                    it = _pendingControls.erase(it);
                    continue;   // iterator has been repositioned!
                }

                it++;
            }
            QueryPerformanceCounter(&completeTime);

            #if 0
            // if(PERF_TO_MS(completeTime, startTime, perfFrequency) > 50)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " doPendingControlOp duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
            }
            #endif
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << RWTime() << " MainQueue is non-valid." << endl;
            }
            return;
        }

        if( !_pendingPointData.empty() )
        {
            QueryPerformanceCounter(&startTime);
            int lpcnt = 0;
            RWTime now;

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

                            #if 0
                            //updateControlHistory( pData->getId(), CtiPendingPointOperations::delayeddatamessage, pData->getTime() );
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            // ACH ACH ACH
                            #endif

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

            #if 0
            // if(PERF_TO_MS(completeTime, startTime, perfFrequency) > 50)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " doPendingPointDataOp duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
            }
            #endif
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                dout << RWTime() << " MainQueue is non-valid." << endl;
            }
            return;
        }

        if( !_pendingPointLimit.empty() )
        {
            QueryPerformanceCounter(&startTime);
            int lpcnt = 0;
            RWTime now;

            // There are pending operations for points out there in the world!
            CtiPendingOpSet_t::iterator it = _pendingPointLimit.begin();

            while( it != _pendingPointLimit.end() )
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

            // if(PERF_TO_MS(completeTime, startTime, perfFrequency) > 50)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " doPendingLimitsOp duration (ms) = " << PERF_TO_MS(completeTime, startTime, perfFrequency) << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

bool CtiPendingOpThread::removePointDataFromPending( LONG pID )
{
    bool bRemoved = false;
    bool bRemoveIt = false;

    if( !_pendingPointData.empty() )
    {
        // There are pending operations for points out there in the world!
        CtiPendingOpSet_t::iterator it = _pendingPointData.begin();

        while( it != _pendingPointData.end() )
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
                    it = _pendingPointData.erase(it);
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


/*
 *  Should only be called by doPendingOperation() method!  Any other caller MAY be blocked by the DB etc!!!
 */
void CtiPendingOpThread::updateControlHistory( CtiPendingPointOperations &ppc, int cause, const RWTime &thetime, RWTime &now )
{
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
                RWTime completiontime = ppc.getControl().getStopTime();
                ppc.getControl().incrementTimes( thetime, 0 );                                      // This effectively primes the entry for the next write.  Critical.
                ppc.getControl().setStopTime( completiontime );
                ppc.getControl().setControlCompleteTime( completiontime );                          // This is when we think this control should complete.

                ppc.getControl().setActiveRestore( LMAR_NEWCONTROL );                               // Record this as a start interval.

                insertControlHistoryRow(ppc);                                                       // Drop the row in there!
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
                    ppc.getControl().incrementTimes( thetime, addnlseconds );

                    // Create some lies
                    ppc.getControl().setActiveRestore( LMAR_LOGTIMER );             // Record this as a start or continue interval.
                    insertControlHistoryRow(ppc);
                    postControlHistoryPoints(ppc);

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
                    CtiPendingPointOperations temporaryPPC(ppc);
                    temporaryPPC.getControl().incrementTimes( thetime, addnlseconds );
                    postControlHistoryPoints(temporaryPPC);
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
                    ppc.getControl().setStopTime(thetime);
                    ppc.getControl().incrementTimes( thetime, addnlseconds );

                    insertControlHistoryRow(ppc);
                    postControlHistoryPoints(ppc);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Thetime seconds() = " << thetime << " addnlt time = " << addnlseconds << endl;
                    ppc.dump();
                }
            }

            break;
        }
    case (CtiPendingPointOperations::control):
        {
            if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress &&
               ppc.getControl().getControlDuration() != RESTORE_DURATION)
            {
                LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                if(addnlseconds >= 0)
                {
                    ppc.getControl().incrementTimes( thetime, addnlseconds );
                    ppc.setControlState( CtiPendingPointOperations::controlCompleteCommanded );

                    insertControlHistoryRow(ppc);
                    postControlHistoryPoints(ppc);
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
    case (CtiPendingPointOperations::delayeddatamessage):
        {
            if(ppc.getControlState() == CtiPendingPointOperations::controlInProgress)
            {
                LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                if(addnlseconds >= 0)
                {
                    ppc.getControl().incrementTimes( thetime, addnlseconds );
                    ppc.setControlState( CtiPendingPointOperations::controlCompleteTimedIn );

                    insertControlHistoryRow(ppc);
                    postControlHistoryPoints(ppc);
                    postControlStopPoint(ppc,now);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Thetime seconds() = " << thetime << " addnlt time = " << addnlseconds << endl;
                    ppc.dump();
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint: MANUAL dataChange! **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    ppc.getControl().incrementTimes( thetime, addnlseconds );
                    ppc.getControl().setActiveRestore( LMAR_MANUAL_RESTORE );
                    ppc.setControlState( CtiPendingPointOperations::controlCompleteManual );

                    insertControlHistoryRow(ppc);
                    postControlHistoryPoints(ppc);
                    postControlStopPoint(ppc,now);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Thetime seconds() = " << thetime << " addnlt time = " << addnlseconds << endl;
                    ppc.dump();
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
                LONG addnlseconds = thetime.seconds() - ppc.getControl().getPreviousLogTime().seconds();

                if(addnlseconds >= 0)
                {
                    ppc.getControl().incrementTimes( thetime, addnlseconds );

                    // Create some lies
                    ppc.getControl().setActiveRestore( LMAR_CONTROLACCT_ADJUST );                         // Record this as a continuation.
                    insertControlHistoryRow(ppc);
                    postControlHistoryPoints(ppc);

                    // OK, set them out for the next run ok. Undo the lies.
                    ppc.getControl().setActiveRestore( ppc.getControl().getDefaultActiveRestore() );        // Reset to the original completion state.

                    postControlStopPoint(ppc,now);
                }
            }
            else
            {
                RWTime writetime = ppc.getControl().getStopTime();
                ppc.getControl().incrementTimes( thetime, 0, true );                                // This effectively primes the entry for the write.  Seasonal hours are reset.  Critical.
                ppc.getControl().setStopTime( writetime );
                ppc.getControl().setControlCompleteTime( writetime );                               // This is when we think this control should complete.

                ppc.getControl().setActiveRestore( LMAR_CONTROLACCT_ADJUST );                       // Record this as a control adjustment.

                insertControlHistoryRow(ppc);                                                  // Drop the row in there!
                postControlHistoryPoints(ppc);
                postControlStopPoint(ppc,now);                                                      // Let everyone know when control should end.

                ppc.getControl().setStopTime( thetime );
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
                    ppc.getControl().incrementTimes( thetime, addnlseconds );
                    ppc.getControl().setStopTime( ppc.getControl().getControlCompleteTime() );
                    ppc.getControl().setActiveRestore( LMAR_DISPATCH_SHUTDOWN );
                    ppc.setControlState( CtiPendingPointOperations::controlCompleteManual );

                    // ppc.dump();

                    insertControlHistoryRow(ppc);
                    //postControlHistoryPoints(ppc);
                    //postControlStopPoint(ppc,now);
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") AR : " << ppc.getControl().getActiveRestore(  ) << " " << ppc.getControl().getControlCompleteTime() << " < " << ppc.getControl().getPreviousLogTime() << endl;
                    ppc.dump();
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


void CtiPendingOpThread::postControlStopPoint( CtiPendingPointOperations &ppc, const RWTime &now)
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    double ai = pPoint->computeValueForUOM((double)remainingseconds);
                    _pMainQueue->putQueue( CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), ai, NormalQuality, pPoint->getType(), pPoint->getName() + " control remaining"));
                }

                ppc.getControl().setPreviousStopReportTime(now);
            }
        }
    }
    return;
}

void CtiPendingOpThread::postControlHistoryPoints( CtiPendingPointOperations &ppc )
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

                _pMainQueue->putQueue( CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), ctltime, NormalQuality, pPoint->getType(), pPoint->getName() + " control history"));
            }
        }
    }

    return;
}

// This method is not threadsafe.  Do not call after startup!
bool CtiPendingOpThread::isPointInPendingControl(LONG pointid)
{
    bool stat = false;

    if(!_pendingControls.empty())
    {
        CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pointid, CtiPendingPointOperations::pendingControl));
        stat = it != _pendingControls.end();
    }

    return(stat);
}

void CtiPendingOpThread::insertControlHistoryRow( CtiPendingPointOperations &ppc)
{
    createOrUpdateICControl(ppc.getControl().getPAOID(), ppc.getControl());     // This keeps it current in mem.
    _dynLMControlHistoryQueue.putQueue(CTIDBG_new CtiTableLMControlHistory(ppc.getControl()) );
    _lmControlHistoryQueue.putQueue(CTIDBG_new CtiTableLMControlHistory(ppc.getControl()) );
    return;
}

bool CtiPendingOpThread::createOrUpdateICControl(long paoid, CtiTableLMControlHistory &lmch )
{
    lmch.setDirty();        // Mark as dirty so we know who needs to be written out.
    pair< CtiICLMControlHistMap_t::iterator, bool > resultpair = _initialConditionControlHistMap.insert( make_pair(paoid, lmch) );

    if(resultpair.second == false)           // Insert was unsuccessful.
    {
        CtiICLMControlHistMap_t::iterator itr = resultpair.first;
        if(itr != _initialConditionControlHistMap.end())
        {
            CtiICLMControlHistMap_t::value_type vt = *itr;
            CtiTableLMControlHistory &origLMCH = vt.second;

            origLMCH = lmch;
        }
    }

    return resultpair.second;
}

#define DUMP_RATE 60
#define MAX_ARCHIVER_ENTRIES 100
#define MAX_DYNLMQ_ENTRIES 1000
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
        if(!(++dumpCounter % (DUMP_RATE * 2))
           || lmentries > MAX_DYNLMQ_ENTRIES
           || justdoit == true )                                 // Only chase the queue once per DUMP_RATE seconds.
        {
            if(lmentries > 0)
            {
                RWCString controlHistory("dynamicControlHistory");
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection();

                conn.beginTransaction(controlHistory);

                while( conn.isValid() && ( justdoit || (panicCounter < PANIC_CONSTANT) ) && (pTblEntry = _dynLMControlHistoryQueue.getQueue(0)) != NULL)
                {
                    panicCounter++;
                    pTblEntry->UpdateDynamic(conn);
                    delete pTblEntry;
                }

                conn.commitTransaction(controlHistory);
            }
            if( panicCounter > 0 )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dynamic LMControlHistory transaction complete. Inserted " << panicCounter << " rows" << endl;
                }
            }

            if(panicCounter >= PANIC_CONSTANT)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Dynamic LM Control History queue has " << _dynLMControlHistoryQueue.entries() << " entries" << endl;
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
    bool cleanShutdown = false;
    bool allCompleted = true;

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
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
        }

        while( rdr() )
        {
            long paoid = 0;
            rdr["paobjectid"] >> paoid;

            CtiTableLMControlHistory dynC;
            dynC.DecodeDatabaseReader(rdr);

            if(!dynC.getLoadedActiveRestore().compareTo(LMAR_DISPATCH_SHUTDOWN, RWCString::ignoreCase))
            {
                cleanShutdown = true;           // Shutdown was clean we do not need to recover from lmctrlhist.
            }
            else if(!cleanShutdown && allCompleted)
            {
                if(!(!dynC.getLoadedActiveRestore().compareTo(LMAR_MANUAL_RESTORE, RWCString::ignoreCase) ||
                     !dynC.getLoadedActiveRestore().compareTo(LMAR_TIMED_RESTORE, RWCString::ignoreCase)))
                {
                    allCompleted = false;       // All controls in the list were not completed.
                }
            }

            createOrUpdateICControl(paoid, dynC);
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    if(cleanShutdown || allCompleted)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Dynamic LMControlHistory was complete, no deep analysis required." << endl;
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Dynamic LMControlHistory was incomplete, _deep_ analysis required." << endl;
    }

    return(cleanShutdown || allCompleted);
}

bool CtiPendingOpThread::getICControlHistory( CtiTableLMControlHistory &lmch )
{
    bool found = false;
    static RWTime lastLoadCheck;

    if(_initialConditionControlHistMap.empty() && (lastLoadCheck + 300 < RWTime()) )        // Not more than once per 5 minutes...
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

            lmch.setStopTime((*itr).second.getStopTime());
            lmch.setCurrentDailyTime((*itr).second.getCurrentDailyTime());
            lmch.setCurrentMonthlyTime((*itr).second.getCurrentMonthlyTime());
            lmch.setCurrentSeasonalTime((*itr).second.getCurrentSeasonalTime());
            lmch.setCurrentAnnualTime((*itr).second.getCurrentAnnualTime());
            lmch.setActiveRestore((*itr).second.getActiveRestore());
        }
    }

    return found;
}


void CtiPendingOpThread::checkForControlBegin( CtiPendable *&pendable )
{
    CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pendable->_pointID, CtiPendingPointOperations::pendingControl));

    if( it != _pendingControls.end() )
    {
        // OK, we just got a change in value on a Status type point, and it is awaiting control!
        CtiPendingPointOperations &ppo = *it;

        if( pendable->_value == ppo.getControlCompleteValue() )             // We are in the control state (value)?
        {
            if(pendable->_tags & TAG_CONTROL_PENDING)                       // Are we still awaiting the start of control?
            {
                if(ppo.getControl().getStartTime() == RWTime(YUKONEOT) )
                {
                    ppo.getControl().setStartTime( pendable->_time );       // Arrival of this point data indicates a control start, no longer pending!
                }

                ppo.setSignal(0);                                           // No longer need to send any error signal.

                if(ppo.getControlState() != CtiPendingPointOperations::controlInProgress)
                {
                    ppo.setControlState(CtiPendingPointOperations::controlInProgress);  // control has begun!
                    updateControlHistory( ppo, CtiPendingPointOperations::newcontrol, pendable->_time );
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            // No longer in the controlcomplete state... because of a pointdata (Manual Change???)
            // updateControlHistory( pendable->_pointID, CtiPendingPointOperations::datachange, pendable->_time );
        }
    }
}

/*
 *  We get here if a controllable point changed value and WAS not in a control pending state.  This means a manual update, or some other status return indicated a non-controlled value.
 */
void CtiPendingOpThread::checkControlStatusChange( CtiPendable *&pendable )
{
    CtiPendingOpSet_t::iterator it = _pendingControls.find(CtiPendingPointOperations(pendable->_pointID, CtiPendingPointOperations::pendingControl));

    if( it != _pendingControls.end() )
    {
        // OK, we just got a change in value on a Status type point, and it is awaiting control!
        CtiPendingPointOperations &ppo = *it;

        if( pendable->_value != ppo.getControlCompleteValue() )  // One final check to make sure we are NOT in the controlled state (value)?
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ACH Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            // No longer in the controlcomplete state... because of a pointdata (Manual Change???)
            updateControlHistory( ppo, CtiPendingPointOperations::datachange, pendable->_time );
            _pendingControls.erase(it);
        }
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
               ppo.getType() == CtiPendingPointOperations::pendingLimit)    // we are a limit ppo.
            {
                if(pendable->_limit == ppo.getLimitBeingTimed())
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** removeLimit Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
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
            #if 0
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** removePointData Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            #endif
            it = _pendingPointData.erase(it);
        }
    }
}

void CtiPendingOpThread::processPendableAdd(CtiPendable *&pendable)
{
    if(pendable->_ppo)
    {
        switch(pendable->_ppo->getType())
        {
        case (CtiPendingPointOperations::pendingControl):
            {
                pair< CtiPendingOpSet_t::iterator, bool > resultpair;
                resultpair = _pendingControls.insert( *pendable->_ppo );            // Add a copy (ppo) to the pending operations.

                CtiPendingPointOperations &ppo = *resultpair.first;

                if(resultpair.second != true)
                {

                    /*
                     *  We only need to "do something" if the earlier control had gone into the controlInProgress state.
                     *  Otherwise it is waiting or completed and left over.
                     */
                    if(ppo.getControlState() == CtiPendingPointOperations::controlInProgress)
                    {
                        if( pendable->_ppo->getControlState() == CtiPendingPointOperations::controlSeasonalReset )
                        {
                            RWCString origType = ppo.getControl().getControlType();
                            ppo.getControl().setControlType(pendable->_ppo->getControl().getControlType());
                            ppo.getControl().setCurrentSeasonalTime(0);
                            updateControlHistory( ppo, CtiPendingPointOperations::seasonReset, pendable->_time );

                            ppo.getControl().setControlType(origType);
                        }
                        else if( pendable->_ppo->getControl().getControlDuration() <= 0 )
                        {
                            // The incoming control information is a RESTORE type operation.
                            ppo.getControl().setActiveRestore(LMAR_MANUAL_RESTORE);
                            updateControlHistory( ppo, CtiPendingPointOperations::control, pendable->_time );
                            ppo = *pendable->_ppo;    // Copy it to update the control state.
                        }
                        else if( pendable->_ppo->getControl().getControlType() == ppo.getControl().getControlType() )
                        {
                            // The control command we just received is the same command as that which started the prior control.
                            // This is a repeat/continuation of the old command!  We just record that and continue.
                            ppo.getControl().setActiveRestore(LMAR_CONT_CONTROL);

                            RWTime logTime = pendable->_ppo->getControl().getStartTime() >= ppo.getControl().getPreviousLogTime() ? pendable->_ppo->getControl().getStartTime() : ppo.getControl().getPreviousLogTime();

                            ppo.getControl().setStopTime(logTime);
                            updateControlHistory( ppo, CtiPendingPointOperations::repeatcontrol, logTime );

                            pendable->_ppo->getControl().setStartTime(ppo.getControl().getStartTime());
                            pendable->_ppo->getControl().setPreviousLogTime(ppo.getControl().getStopTime());
                            pendable->_ppo->getControl().setNotNewControl();
                            ppo = *pendable->_ppo;    // Copy it to update the control state.
                        }
                        else
                        {
                            // A new and different control command has arrived.  We need to record the old command as having
                            // been overridden.
                            ppo.getControl().setActiveRestore(LMAR_OVERRIDE_CONTROL);
                            updateControlHistory( ppo, CtiPendingPointOperations::control, pendable->_time );

                            ppo = *pendable->_ppo;    // Copy it to update the control state.
                        }
                    }
                    else
                    {
                        ppo = *pendable->_ppo;    // Copy it to update the control state.
                    }
                }
                else
                {
                    // It went in clean.
                    // Make certain controls know about any predecessor controls.
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
                    RWTime origTime = ppo.getTime();
                    ppo = *pendable->_ppo;    // Copy it.
                    ppo.setTime(origTime);              // Don't move the time.  The
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
        dout << RWTime() << " Dispatch PendingOp DB Writer Thread starting as TID " << rwThreadId() << " (0x" << hex << rwThreadId() << dec << ")" << endl;
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
                        dout << RWTime() << " PendingOp DB Writer Thread Active " << endl;
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
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << RWTime() << " Dispatch PendingOp DB Writer Thread shutting down" << endl;
    }

    return;
}

