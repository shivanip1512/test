#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALCTHREAD_H__
#define __CALCTHREAD_H__

#include <functional>
using namespace std;

#include <rw/tphdict.h>
#include <rw/tvdeque.h>
#include <rw/tpdeque.h>
#include <rw/thr/thrfunc.h>

#include "hashkey.h"
#include "msg_multi.h"

#include "calc.h"
#include "pointstore.h"

//ecs 1/5/2005
#include "thread_monitor.h"
#include "thread_register_data.h"
//

class CtiCalculateThread
{
public:

    typedef enum
    {
        Shutdown = 0,
        DBReload

    } CtiCalcThreadInterruptReason;


    typedef RWTPtrHashMap<CtiHashKey, CtiCalc, my_hash<CtiHashKey> , equal_to<CtiHashKey> > CtiCalcPointMap;
    typedef RWTPtrHashMapIterator<CtiHashKey, CtiCalc, my_hash<CtiHashKey> , equal_to<CtiHashKey> > CtiCalcPointMapIterator;

private:
    CtiCalcPointMap _periodicPoints, _onUpdatePoints, _constantPoints;
    RWTValDeque<long> _auAffectedPoints;
    RWTPtrDeque<CtiMultiMsg> _outbox;
    RWMutexLock _pointDataMutex;

    void periodicLoop( void );
    void onUpdateLoop( void );

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    RWThreadFunction _periodicThreadFunc;
    RWThreadFunction _onUpdateThreadFunc;

    CtiCalcThreadInterruptReason _interruptReason;

public:

    CtiCalculateThread( void )
    {
    };

    ~CtiCalculateThread( void );

    RWMutexLock outboxMux;

    void calcLoop( void );
    bool appendPoint( long pointID, RWCString &updateType, int updateInterval );
    void appendPointComponent( long pointID, RWCString &componentType, long componentPointID,
                               RWCString &operationType, double constantValue, RWCString &functionName );
    void appendCalcPoint( long pointID );
    void pointChange( long changedID, double newValue, RWTime &newTime, unsigned newQuality, unsigned newTags );

    BOOL isACalcPointID(const long aPointID);
    BOOL isAPeriodicCalcPointID(const long aPointID);
    BOOL isAnOnUpdateCalcPointID(const long aPointID);
    BOOL isAConstantCalcPointID(const long aPointID);
    long numberOfLoadedCalcPoints() { return (_periodicPoints.entries() + _onUpdatePoints.entries() + _constantPoints.entries()); };

    RWTPtrDeque<CtiMultiMsg>::size_type outboxEntries( void )   {   return _outbox.entries( ); };
    CtiMultiMsg *getOutboxEntry( void )                         {   return _outbox.popFront( ); };
    RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >
    *getPointDependencyIterator( void )                         {   return new RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >( *CtiPointStore::getInstance() );   };

    void startThreads(  );
    void joinThreads(  );

    void interruptThreads( CtiCalcThreadInterruptReason reason );
    void resumeThreads(  );

    void sendConstants( );

    static void onUpdateComplain( void *la );
    static void periodicComplain( void *la );
    static void calcComplain( void *la );

    CtiCalcPointMap getPeriodicPointMap() const;
    CtiCalcPointMap getOnUpdatePointMap() const;
    CtiCalcPointMap getConstantPointMap() const;

    void setPeriodicPointMap(const CtiCalcPointMap &);
    void setOnUpdatePointMap(const CtiCalcPointMap &);
    void setConstantPointMap(const CtiCalcPointMap &);
};

#endif // #ifndef __CALCTHREAD_H__

