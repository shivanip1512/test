#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALC_H__
#define __CALC_H__

#include <rw/slistcol.h>
#include <rw/tvdlist.h>
#include <rw/tstack.h>

#include "calccomponent.h"

class CtiCalc : public RWCollectable
{
    RWDECLARE_COLLECTABLE( CtiCalc );

private:
    RWSlistCollectables  _components;
    RWTStack<double, RWTValDlist<double> > _stack;
    PointUpdateType      _updateType;
    ULONG                _nextInterval;
    int                  _updateInterval;
//   int                  _updateInterval, _countdown;
    long                 _pointId;
    BOOL                 _valid;
    RWTime               _pointCalcWindowEndTime;

    // text from the database
    static const CHAR * UpdateType_Periodic;
    static const CHAR * UpdateType_AllChange;
    static const CHAR * UpdateType_OneChange;
    static const CHAR * UpdateType_Historical;
    static const CHAR * UpdateType_PeriodicPlusUpdate;

    RWTime calcTimeFromComponentTime( const RWTime &minTime, const RWTime &maxTime );
    bool calcTimeFromComponentTime( RWTime &componentTime, int componentQuality, RWTime &minTime, RWTime &maxTime );
    int calcQualityFromComponentQuality( int qualityFlag, const RWTime &minTime, const RWTime &maxTime );

public:

    CtiCalc( ) :
    _updateType(undefined), _updateInterval(-1), _pointId(-1), _valid(FALSE), _nextInterval( 1 ), _pointCalcWindowEndTime( RWTime(RWDate(1,1,1990)) )
    {};

    CtiCalc( long pointId, const RWCString &updateType, int updateInterval );

    ~CtiCalc( )  {  cleanup( );};

    ULONG     getNextInterval() const;
    CtiCalc&  setNextInterval (int interval);
    int      getUpdateInterval( ) const;

    const RWTime& getPointCalcWindowEndTime() const;
    CtiCalc&      setPointCalcWindowEndTime(const RWTime& endTime);

    long findDemandAvgComponentPointId();

    long getPointId( void )  {  return _pointId;};

    CtiCalc &operator=( CtiCalc &toCopy );
    BOOL operator==( CtiCalc &equalTest )  {  return _pointId == equalTest.getPointId( );}

    void appendComponent( CtiCalcComponent *componentToAdd );
    void cleanup( void );
    PointUpdateType getUpdateType( void );
    double calculate( int &calc_quality, RWTime &calc_time );
    BOOL ready( void );
    void push( double );
    double pop( void );

    //  see FIX_ME in calc.cpp
    //void saveGuts( RWvostream &aStream ) const;
    //void restoreGuts( RWvistream &aStream );
};

#endif   // #ifndef __CALC_H__
// square root of 3 for power factor calculations
#define SQRT3               1.7320508075688772935274463415059


