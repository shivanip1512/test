#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __POINTSTORE_H__
#define __POINTSTORE_H__

#include <functional>

#include <rw/collect.h>
#include <rw/rwtime.h>
#include <rw/tphdict.h>
#include <rw/tvhset.h>
//#include <rw/tvset.h>

#include "hashkey.h"
#include "rtdb.h"
#include "pointdefs.h"

enum PointUpdateType
{
    undefined,
    periodic,
    allUpdate,
    anyUpdate,
    historical
};

using namespace std;

struct depStore
{
    long dependentID;
//  currently, this isn't important.  the only point type that has dependencies is
//    the allupdate (and now the anyUpdate type: added by J Wolberg 5/15/2002) point type, but this was constructed with expandability in mind.
//    PointUpdateType updateType;
    int operator<( const depStore &other ) const    { return (dependentID <  other.dependentID); };
    int operator==( const depStore &other ) const   { return (dependentID == other.dependentID); };
    int operator()( const depStore &one, const depStore &two ) const    { return (one == two); };
    int operator()( const depStore &one ) const     { return (one.dependentID); };
};

class CtiPointStoreElement : public RWCollectable
{
    RWDECLARE_COLLECTABLE(CtiPointStoreElement)

    //  so they can access the "appendPointComponent" and "setPointValue" functions.
    //    that's so we don't have to worry about the calccomponent code modifying the values.
    friend class CtiCalculateThread;
    friend class CtiPointStore;

private:
    long _pointNum, _numUpdates, _secondsSincePreviousPointTime;
    double _pointValue;
    unsigned _pointQuality;
    unsigned _pointTags;
    RWTime _pointTime;
    RWTValHashSet<depStore, depStore, depStore> _dependents;
//    RWTValSet<depStore, depStore> _dependents;

public:
    CtiPointStoreElement( long pointNum = 0, double pointValue = 0.0, unsigned pointQuality = UnintializedQuality, unsigned pointTags = 0 ) :
    _pointNum(pointNum), _pointValue(pointValue), _pointQuality(pointQuality), _pointTags(pointTags), _numUpdates(0),
    _secondsSincePreviousPointTime(60)// one minute seems like a reasonable default
    {  };

    long    getPointNum( void )         {   return _pointNum;   };
    double  getPointValue( void )       {   return _pointValue; };
    unsigned  getPointQuality( void )   {   return _pointQuality; };
    unsigned  getPointTags( void )      {   return _pointTags; };
    RWTime  getPointTime( void )        {   return _pointTime;  };
    long    getNumUpdates( void )       {   return _numUpdates; };
    long    getSecondsSincePreviousPointTime( void )       {   return _secondsSincePreviousPointTime; }; //mostly used for demand average points
    RWTValHashSetIterator<depStore, depStore, depStore> 
            *getDependents( void )      {   return new RWTValHashSetIterator<depStore, depStore, depStore>( _dependents );    };
//    RWTValSetIterator<depStore, depStore> 
//            *getDependents( void )      {   return new RWTValSetIterator<depStore, depStore>( _dependents );    };

protected:
    void setPointValue( double newValue, RWTime &newTime, unsigned newQuality, unsigned newTags )
    {
        _secondsSincePreviousPointTime = newTime.seconds() - _pointTime.seconds();
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _pointTags = newTags;
        _numUpdates++;
    };

    void firstPointValue( double newValue, RWTime &newTime, unsigned newQuality, unsigned newTags )
    {
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _pointTags = newTags;
    };

    void appendDependent( long dependentID, PointUpdateType updateType )
    {
        struct depStore newDependent;
        newDependent.dependentID = dependentID;
//        newDependent.updateType = updateType;
        _dependents.insert( newDependent );
    };
};


class CtiPointStore : public RWTPtrHashMap<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >
{
public:
    static CtiPointStore *getInstance();
    CtiPointStoreElement *insertPointElement( long pointNum, long dependentId, enum PointUpdateType updateType );

private:

    CtiPointStore( void )  {  };
    ~CtiPointStore( )      {  this->clearAndDestroy( );  };

    //The singleton instance of CtiPointStore
    static CtiPointStore* _instance;
    
    mutable RWRecursiveLock<RWMutexLock> _mutex;
};


#endif   // #ifndef __POINTSTORE_H__

