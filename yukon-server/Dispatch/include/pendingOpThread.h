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
* REVISION     :  $Revision: 1.14.6.1 $
* DATE         :  $Date: 2008/11/20 20:37:42 $
* HISTORY      :
* $Log: pendingOpThread.h,v $
* Revision 1.14.6.1  2008/11/20 20:37:42  jmarks
* [YUKRV-525] Comment: YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008
* * Builds through "Dispatch" subdirectory
* * A few new lines commented out to link successfully
*
* Revision 1.14  2008/04/24 19:41:51  jotteson
* YUK-4897 Load management implementation of Expresscom priorities
* Moved the handling of control status points to Dispatch.
* Enabled dispatch to know what the current control priority of any group is.
*
* Revision 1.13  2006/09/26 14:11:52  mfisher
* moved headers to the top so Slick's diff ignores them
*
* Revision 1.12  2006/09/08 20:18:05  jotteson
* Fixed a bug that would cause delayed limits to not be ack-able.
*
* Revision 1.11  2006/06/16 20:04:56  jotteson
* Now modifies tags when removing a control. Can be told not to write control history if desired.
*
* Revision 1.10  2006/03/23 15:29:19  jotteson
* Mass update of point* to smart pointers. Point manager now uses smart pointers.
*
* Revision 1.9  2006/01/05 21:05:14  cplender
* Changed a CtiQueue (sorted) to CtiFIFOQueue for speed.
*
* Revision 1.8  2006/01/05 19:30:10  cplender
* Que_t changed to Que_t typedef name.
*
* Revision 1.7  2005/12/20 17:16:58  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.6  2005/07/25 16:40:53  cplender
* Working on lmcontrolhistory for Minnkota.
* Revision 1.5.4.2  2005/08/12 19:53:43  jliu
* Date Time Replaced
*
* Revision 1.5.4.1  2005/07/14 22:26:55  jliu
* RWCStringRemoved
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
using std::map;
using std::set;
using std::pair;

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
    static pair< CtiPendingOpSet_t::iterator, bool > insertPendingControl(CtiPendingPointOperations &ppo);

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
#endif // #ifndef __PENDINGOPTHREAD_H__
