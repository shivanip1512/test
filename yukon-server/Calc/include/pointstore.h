#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __POINTSTORE_H__
#define __POINTSTORE_H__

#include <functional>

#include <rw/collect.h>

#include <rw/tphdict.h>
#include <rw/tvhset.h>

#include "hashkey.h"
#include "rtdb.h"
#include "pointdefs.h"
#include "regression.h"
#include "ctitime.h"
#include "ctidate.h"

#define CALC_DEBUG_INBOUND_POINTS                   0x00000001
#define CALC_DEBUG_OUTBOUND_POINTS                  0x00000002
#define CALC_DEBUG_PRECALC_VALUE                    0x00000004
#define CALC_DEBUG_POSTCALC_VALUE                   0x00000008
#define CALC_DEBUG_CALC_INIT                        0x00000010
#define CALC_DEBUG_COMPONENT_POSTCALC_VALUE         0x00000020
#define CALC_DEBUG_POINTDATA_QUALITY                0x00000040
#define CALC_DEBUG_DEMAND_AVG                       0x00000080
#define CALC_DEBUG_DISPATCH_INIT                    0x00000100
#define CALC_DEBUG_POINTDATA                        0x00000200
#define CALC_DEBUG_THREAD_REPORTING                 0x00000400
#define CALC_DEBUG_INBOUND_MSGS                     0x00000800
#define CALC_DEBUG_BASELINE                         0x00001000

enum PointUpdateType
{
    undefined,
    periodic,
    allUpdate,
    anyUpdate,
    historical,
    periodicPlusUpdate,
    constant
};

using namespace std;

struct depStore
{
    long dependentID;
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
    long _pointNum, _numUpdates, _secondsSincePreviousPointTime, _updatesInCurrentAvg, _uomID;
    double _pointValue, _curHistoricValue;
    unsigned _pointQuality;
    unsigned _pointTags;
    CtiTime _pointTime, _calcPointWindowEndTime;
    RWTValHashSet<depStore, depStore, depStore> _dependents;
    
    // The following two elements are used to determine if the VALUE changes from one scan to the next.
    CtiTime _lastValueChangedTime;
    CtiRegression _regress;

public:
    CtiPointStoreElement( long pointNum = 0, double pointValue = 0.0, unsigned pointQuality = UnintializedQuality, unsigned pointTags = 0 ) :
    _pointNum(pointNum), _pointValue(pointValue), _pointQuality(pointQuality), _pointTags(pointTags), _numUpdates(0), _lastValueChangedTime(pointValue),
    _updatesInCurrentAvg(0), _calcPointWindowEndTime(CtiTime(CtiDate(1,1,1990))), _secondsSincePreviousPointTime(60)// one minute seems like a reasonable default
    {  };

    long    getPointNum( void )            {   return _pointNum;   };
    double  getPointValue( void )          {   return _pointValue; };
    unsigned  getPointQuality( void )      {   return _pointQuality; };
    unsigned  getPointTags( void )         {   return _pointTags; };
    CtiTime  getPointTime( void )           {   return _pointTime;  };
    long    getNumUpdates( void )          {   return _numUpdates; };
    long    getSecondsSincePreviousPointTime( void )       {   return _secondsSincePreviousPointTime; }; //mostly used for demand average points
    RWTValHashSetIterator<depStore, depStore, depStore> *getDependents( void )      {   return CTIDBG_new RWTValHashSetIterator<depStore, depStore, depStore>( _dependents );    };

    CtiTime  getLastValueChangedTime( void )        {   return _lastValueChangedTime;  };
    void resize_regession(int data_elements)
    {
        _regress.resize(data_elements);
        return;
    }
    double regression( double x)
    {
        return _regress.regression(x);
    }

    void setUpdatesInCurrentAvg( long newCount )   {   _updatesInCurrentAvg = newCount;   };
    long getUpdatesInCurrentAvg( void )            {   return _updatesInCurrentAvg; };
    void setHistoricValue(double historicValue)    {   _curHistoricValue = historicValue; };
    double getHistoricValue()                      {   return _curHistoricValue;          };
    void setUOMID(long uomid)                      {   _uomID = uomid;                   };
    long getUOMID(void)                            {   return _uomID;                     };

    const CtiTime& getPointCalcWindowEndTime( void )                  {   return _calcPointWindowEndTime;    };
    void          setPointCalcWindowEndTime( const CtiTime& endTime ) {   _calcPointWindowEndTime = endTime;  };

    //removes the dependent and returns the number of dependents remaining
    unsigned int removeDependent( long dependentID )
    {
        struct depStore newDependent;
        newDependent.dependentID = dependentID;
        int size = _dependents.entries();
        _dependents.remove( newDependent );

        return _dependents.entries();
    }

protected:
    void setPointValue( double newValue, const CtiTime &newTime, unsigned newQuality, unsigned newTags )
    {
        /*
         *  This represents a bonofide value change.  Record the time of the change.
         */
        if(newValue != _pointValue)
        {
            _lastValueChangedTime = newTime;
        }

        _secondsSincePreviousPointTime = newTime.seconds() - _pointTime.seconds();
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _pointTags = newTags;
        _numUpdates++;

        _regress.append(make_pair(_pointTime.seconds(), _pointValue));
    };

    void firstPointValue( double newValue, const CtiTime &newTime, unsigned newQuality, unsigned newTags )
    {
        _pointTime = newTime;
        _pointValue = newValue;
        _pointQuality = newQuality;
        _pointTags = newTags;

        _lastValueChangedTime = _pointTime;

        _regress.append(make_pair(_pointTime.seconds(), _pointValue));
    };

    void setPointTags( unsigned newTags )
    {
        _pointTags = newTags;
    }

    void appendDependent( long dependentID, PointUpdateType updateType )
    {
        if( dependentID != _pointNum ) //You are not allowed to be dependent on yourself!!
        {
            struct depStore newDependent;
            newDependent.dependentID = dependentID;
            _dependents.insert( newDependent );
        }
    };
};


class CtiPointStore : public RWTPtrHashMap<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, equal_to<CtiHashKey> >
{
public:
    static CtiPointStore *getInstance();
    static void          removeInstance();
    CtiPointStoreElement *insertPointElement( long pointNum, long dependentId, enum PointUpdateType updateType );
    void                 removePointElement( long pointNum );

private:

    CtiPointStore( void )  {  };
    ~CtiPointStore( )
    {
        this->clearAndDestroy( );
    };

    //The singleton instance of CtiPointStore
    static CtiPointStore* _instance;

    mutable RWRecursiveLock<RWMutexLock> _mutex;
};


#endif   // #ifndef __POINTSTORE_H__

