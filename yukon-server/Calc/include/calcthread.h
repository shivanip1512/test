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

class CtiCalculateThread
{
private:
    RWTPtrHashMap<CtiHashKey, CtiCalc, my_hash<CtiHashKey> , equal_to<CtiHashKey> > _periodicPoints, _onUpdatePoints;
    RWTValDeque<long> _auAffectedPoints;
    RWTPtrDeque<CtiMultiMsg> _outbox;
    RWMutexLock _pointDataMutex;

    void periodicLoop( void );
    void onUpdateLoop( void );

    mutable RWRecursiveLock<RWMutexLock> _mutex;

public:
    CtiCalculateThread( void )
    {
    };

    ~CtiCalculateThread( void )
    {
        _auAffectedPoints.clear();
        if( _periodicPoints.entries() > 0 )
        {
            _periodicPoints.clearAndDestroy();
        }
        if( _onUpdatePoints.entries() > 0 )
        {
            _onUpdatePoints.clearAndDestroy();
        }
    };

    RWMutexLock outboxMux;
    
    void calcLoop( void );
    void appendPoint( long pointID, RWCString &updateType, int updateInterval );
    void appendPointComponent( long pointID, RWCString &componentType, long componentPointID, 
                               RWCString &operationType, double constantValue, RWCString &functionName );
    void appendCalcPoint( long pointID );
    void pointChange( long changedID, double newValue, RWTime &newTime, unsigned newQuality, unsigned newTags );

    BOOL isACalcPointID(const long aPointID);
    BOOL isAPeriodicCalcPointID(const long aPointID);
    BOOL isAnOnUpdateCalcPointID(const long aPointID);
    long numberOfLoadedCalcPoints() { return (_periodicPoints.entries() + _onUpdatePoints.entries()); };

    RWTPtrDeque<CtiMultiMsg>::size_type outboxEntries( void )   {   return _outbox.entries( ); };
    CtiMultiMsg *getOutboxEntry( void )                         {   return _outbox.popFront( ); };
    RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >
    *getPointDependencyIterator( void )                         {   return new RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >( *CtiPointStore::getInstance() );   };

};

#endif // #ifndef __CALCTHREAD_H__

