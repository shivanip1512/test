#pragma once

#include <set>

#include "connection.h"
#include "pendable.h"
#include "pt_numeric.h"
#include "queue.h"
#include "tbl_lm_controlhist.h"
#include "thread.h"
#include "signalmanager.h"
#include "guard.h"
#include "mutex.h"

class CtiPendingOpThread : public CtiThread
{
public:

    typedef std::set< CtiPendingPointOperations >  CtiPendingOpSet_t;
    typedef std::map< long, CtiTableLMControlHistory >  CtiICLMControlHistMap_t;       // Contains the intial conditions for controls history and control state.

private:

    CtiMultiMsg *_multi;
    RWThreadFunction  _dbThread;

    CtiConnection::Que_t *_pMainQueue;    // Main queue
    CtiSignalManager     *_pSignalManager;

    //  the input queue
    CtiFIFOQueue< CtiPendable > _input;
    static CtiPendingOpSet_t _pendingControls;
    static CtiPendingOpSet_t _pendingPointData;
    static CtiPendingOpSet_t _pendingPointLimit;

    CtiMutex _controlMux;

    typedef CtiFIFOQueue< CtiTableLMControlHistory > ControlHistoryQueue;
    ControlHistoryQueue _lmControlHistoryQueue;
    ControlHistoryQueue _dynLMControlHistoryQueue;

    CtiICLMControlHistMap_t _controlHistoryPrimeValues;
    static CtiICLMControlHistMap_t _initialConditionControlHistMap;

    void dbWriterThread();
    static CtiPendingOpSet_t::iterator erasePendingControl(CtiPendingOpSet_t::iterator iter);
    static std::pair< CtiPendingOpSet_t::iterator, bool > insertPendingControl(CtiPendingPointOperations &ppo);

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

    void setMainQueue(CtiConnection::Que_t *pMQ);
    void setSignalManager(CtiSignalManager *pSM);
    void processPendableQueue();

    void doPendingControls(bool bShutdown = false);
    void doPendingLimits(bool bShutdown = false);
    void doPendingPointData(bool bShutdown = false);
    void updateControlHistory(  CtiPendingPointOperations &ppc, int cause, const CtiTime &thetime = CtiTime(), CtiTime &now = CtiTime(), int line = 0);
    // void dumpPendingOps( bool force = false  );
    void postControlStopPoint( CtiPendingPointOperations &ppc, bool doit = false);
    void postControlHistoryPoints( CtiPendingPointOperations &ppc, bool doit = false );
    bool isPointInPendingControl(LONG pointid);
    int  getCurrentControlPriority(LONG pointid);
    CtiTime getPendingControlCompleteTime(LONG pointid);

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

    static CtiPointNumericSPtr getPointOffset(CtiPendingPointOperations &ppc, long pao, int poff);

};
