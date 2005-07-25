
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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/07/25 16:40:53 $
* HISTORY      :
* $Log: pendingOpThread.h,v $
* Revision 1.6  2005/07/25 16:40:53  cplender
* Working on lmcontrolhistory for Minnkota.
*
* Revision 1.5  2004/12/01 20:15:04  cplender
* LMControlHistory.
*
* Revision 1.4  2004/11/18 23:56:08  cplender
* Control History Getting closer.
*
* Revision 1.3  2004/11/09 06:12:51  cplender
* Working to calm dispatch down
*
* Revision 1.2  2004/11/08 14:40:38  cplender
* 305 Protocol should send controls on RTCs now.
*
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
#include "pt_numeric.h"
#include "queue.h"
#include "tbl_lm_controlhist.h"
#include "thread.h"

class CtiPendingOpThread : public CtiThread
{
public:

    typedef set< CtiPendingPointOperations >  CtiPendingOpSet_t;
    typedef map< long, CtiTableLMControlHistory >  CtiICLMControlHistMap_t;       // Contains the intial conditions for controls history and control state.

private:

    CtiMultiMsg *_multi;
    RWThreadFunction  _dbThread;

    CtiConnection::InQ_t *_pMainQueue;    // Main queue

    //  the input queue
    CtiQueue< CtiPendable, less< CtiPendable > > _input;
    static CtiPendingOpSet_t _pendingControls;
    static CtiPendingOpSet_t _pendingPointData;
    static CtiPendingOpSet_t _pendingPointLimit;


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
        PROCESSQ = CtiThread::LAST,
        QPROCESSED,
        POSTHISTORY,
        POSTSTOP,
        PRIMED                                      // set by self after initial data has been loaded.

    };

    void push(CtiPendable *e);                    //  the objects are consumed when submitted to the thread
    void run();

    void setMainQueue(CtiConnection::InQ_t *pMQ);
    void processPendableQueue();

    void doPendingControls(bool bShutdown = false);
    void doPendingLimits(bool bShutdown = false);
    void doPendingPointData(bool bShutdown = false);
    void updateControlHistory(  CtiPendingPointOperations &ppc, int cause, const RWTime &thetime = RWTime(), RWTime &now = RWTime(), int line = 0);
    // void dumpPendingOps( bool force = false  );
    void postControlStopPoint( CtiPendingPointOperations &ppc, bool doit = false);
    void postControlHistoryPoints( CtiPendingPointOperations &ppc, bool doit = false );
    bool isPointInPendingControl(LONG pointid);

    void insertControlHistoryRow( CtiPendingPointOperations &ppc, int line);
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
    void removeControl(CtiPendable *&pendable);

    static CtiPointNumeric* getPointOffset(CtiPendingPointOperations &ppc, long pao, int poff);

};
#endif // #ifndef __PENDINGOPTHREAD_H__
