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
    RWTPtrHashMap<CtiHashKey, CtiCalc, my_hash<CtiHashKey> , equal_to<CtiHashKey> > _periodicPoints, _allUpdatePoints;
    RWTValDeque<long> _auAffectedPoints;
    RWTPtrDeque<CtiMultiMsg> _outbox;
    CtiPointStore pointStore;                        
    RWMutexLock _pointDataMutex;

    void periodicLoop( void );
    void allUpdateLoop( void );

public:
    CtiCalculateThread( void )
    {
    };

    RWMutexLock outboxMux;
    
    void calcLoop( void );
    void appendPoint( long pointID, RWCString &updateType, int updateInterval );
    void appendPointComponent( long pointID, RWCString &componentType, long componentPointID, 
                               RWCString &operationType, double constantValue, RWCString &functionName );
    void pointChange( long changedID, double newValue, RWTime &newTime, unsigned newQuality );

    BOOL isACalcPointID(const long aPointID);
    BOOL isAPeriodicCalcPointID(const long aPointID);
    BOOL isAUpdateAllCalcPointID(const long aPointID);

    
    RWTPtrDeque<CtiMultiMsg>::size_type outboxEntries( void )   {   return _outbox.entries( ); };
    CtiMultiMsg *getOutboxEntry( void )                         {   return _outbox.popFront( ); };
    RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >
    *getPointDependencyIterator( void )                         {   return new RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >( pointStore );   };

};

#endif // #ifndef __CALCTHREAD_H__

