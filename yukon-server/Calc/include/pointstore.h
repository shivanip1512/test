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
    historical,
};

using namespace std;

struct depStore
{
    long dependentID;
//  currently, this isn't important.  the only point type that has dependencies is
//    the allupdate point type, but this was constructed with expandability in mind.
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
    long _pointNum, _numUpdates;
    double _pointValue;
    unsigned _pointQuality;
    RWTime _pointTime;
    RWTValHashSet<depStore, depStore, depStore> _dependents;
//    RWTValSet<depStore, depStore> _dependents;

public:
    CtiPointStoreElement( long pointNum = 0, double pointValue = 0.0, unsigned pointQuality = UnintializedQuality ) :
    _pointNum(pointNum), _pointValue(pointValue), _pointQuality(pointQuality), _numUpdates(0)
    {  };

    long    getPointNum( void )         {   return _pointNum;   };
    double  getPointValue( void )       {   return _pointValue; };
    unsigned  getPointQuality( void )   {   return _pointQuality; };
    RWTime  getPointTime( void )        {   return _pointTime;  };
    long    getNumUpdates( void )       {   return _numUpdates; };
    RWTValHashSetIterator<depStore, depStore, depStore> 
            *getDependents( void )      {   return new RWTValHashSetIterator<depStore, depStore, depStore>( _dependents );    };
//    RWTValSetIterator<depStore, depStore> 
//            *getDependents( void )      {   return new RWTValSetIterator<depStore, depStore>( _dependents );    };

protected:
    void setPointValue( double newValue, RWTime &newTime, unsigned newQuality )
    {
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _numUpdates++;
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
    CtiPointStore( void )  {  };

    ~CtiPointStore( )      {  this->clearAndDestroy( );  };

    CtiPointStoreElement *insertPointElement( long dependentId, long pointNum, enum PointUpdateType updateType );
};


#endif   // #ifndef __POINTSTORE_H__

