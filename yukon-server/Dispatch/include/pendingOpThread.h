
/*-----------------------------------------------------------------------------*
*
* File:   pendingOpThread
*
* Class:  CtiPendingOpThread
* Date:   11/2/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/11/05 17:22:48 $
* HISTORY      :
* $Log: pendingOpThread.h,v $
* Revision 1.1  2004/11/05 17:22:48  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __PENDINGOPTHREAD_H__
#define __PENDINGOPTHREAD_H__

#include <set>
#include <map>
using namespace std;

#include "connection.h"
#include "pendable.h"
#include "queue.h"
#include "tbl_lm_controlhist.h"
#include "thread.h"

class CtiPendingOpThread : public CtiThread
{
public:

    typedef set< CtiPendingPointOperations >  CtiPendingOpSet_t;
    typedef map< long, CtiTableLMControlHistory >  CtiICLMControlHistMap_t;       // Contains the intial conditions for controls history and control state.

private:

    RWThreadFunction  _dbThread;

    CtiConnection::InQ_t *_pMainQueue;    // Main queue

    //  the input queue
    CtiQueue< CtiPendable, less< CtiPendable > > _input;
    CtiPendingOpSet_t _pendingControls;
    CtiPendingOpSet_t _pendingPointData;
    CtiPendingOpSet_t _pendingPointLimit;


    CtiQueue< CtiTableLMControlHistory, less<CtiTableLMControlHistory> > _lmControlHistoryQueue;
    CtiQueue< CtiTableLMControlHistory, less<CtiTableLMControlHistory> > _dynLMControlHistoryQueue;

    CtiICLMControlHistMap_t _controlHistoryPrimeValues;
    static CtiICLMControlHistMap_t _initialConditionControlHistMap;

    void dbWriterThread();


    CtiPendingOpThread(const CtiPendingOpThread& aRef);

protected:

public:

    CtiPendingOpThread();
    virtual ~CtiPendingOpThread();

    enum
    {
        RELOAD = CtiThread::LAST,
        PRIMED                                      // set by self after initial data has been loaded.

    };

    void push(CtiPendable *e);                    //  the objects are consumed when submitted to the thread
    void run();

    void setMainQueue(CtiConnection::InQ_t *pMQ);
    void processPendableQueue();

    void doPendingControls(bool bShutdown = false);
    void doPendingLimits(bool bShutdown = false);
    void doPendingPointData(bool bShutdown = false);
    bool removePointDataFromPending( LONG pID );
    void updateControlHistory(  CtiPendingPointOperations &ppc, int cause, const RWTime &thetime = RWTime(), RWTime &now = RWTime() );
    // void dumpPendingOps( bool force = false  );
    void postControlStopPoint( CtiPendingPointOperations &ppc, const RWTime &now);
    void postControlHistoryPoints( CtiPendingPointOperations &ppc );
    bool isPointInPendingControl(LONG pointid);

    void insertControlHistoryRow( CtiPendingPointOperations &ppc);
    void writeLMControlHistoryToDB(bool justdoit = false);
    void writeDynamicLMControlHistoryToDB(bool justdoit = false);

    static void resetICControlMap();
    static bool loadICControlMap();
    static bool getICControlHistory( CtiTableLMControlHistory &lmch );
    static bool createOrUpdateICControl(long paoid, CtiTableLMControlHistory &lmch );

    void checkForControlBegin( CtiPendable *&pendable );
    void checkControlStatusChange( CtiPendable *&pendable );
    void processPendableAdd(CtiPendable *&pendable);
    void removeLimit(CtiPendable *&pendable);
    void removePointData(CtiPendable *&pendable);

};
#endif // #ifndef __PENDINGOPTHREAD_H__
