#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALC_H__
#define __CALC_H__

#include <rw/slistcol.h>
#include <rw/tvdlist.h>
//#include <rw/tstack.h>
#include "ctiqueues.h"

#include "calccomponent.h"

class CtiCalc : public RWCollectable
{
    RWDECLARE_COLLECTABLE( CtiCalc );

private:
    RWSlistCollectables  _components;
    CtiStack<double>     _stack;
    PointUpdateType      _updateType;
    ULONG                _nextInterval;
    int                  _updateInterval;
    long                 _pointId, _baselineId, _baselinePercentId;
    BOOL                 _valid;
    bool                 _calculateQuality;
    CtiTime              _pointCalcWindowEndTime;
    bool                 _isBaseline;

    // text from the database
    static const CHAR * UpdateType_Periodic;
    static const CHAR * UpdateType_AllChange;
    static const CHAR * UpdateType_OneChange;
    static const CHAR * UpdateType_Historical;
    static const CHAR * UpdateType_PeriodicPlusUpdate;
    static const CHAR * UpdateType_Constant;

    CtiTime calcTimeFromComponentTime( const CtiTime &minTime, const CtiTime &maxTime );
    bool calcTimeFromComponentTime( CtiTime &componentTime, int componentQuality, CtiTime &minTime, CtiTime &maxTime );
    int calcQualityFromComponentQuality( int qualityFlag, const CtiTime &minTime, const CtiTime &maxTime );

public:

    CtiCalc( ) :
    _updateType(undefined), _updateInterval(-1), _pointId(-1), _valid(FALSE), _nextInterval( 1 ),
    _pointCalcWindowEndTime( CtiTime(CtiDate(1,1,1990)) ), _calculateQuality(true), _isBaseline(false)
    {}

    CtiCalc( long pointId, const string &updateType, int updateInterval, const string &qualityFlag );

    ~CtiCalc( )  
    {  
        cleanup( );
    }

    ULONG     getNextInterval() const;
    CtiCalc&  setNextInterval (int interval);
    int      getUpdateInterval( ) const;
    int      getComponentCount();
    vector<long> getComponentIDList();
    long findDemandAvgComponentPointId();
    
    long getPointId( void )  
    {  
        return _pointId;
    }

    long getBaselineId( void )  
    {  
        return _baselineId;
    }

    long getBaselinePercentId( void )  
    {  
        return _baselinePercentId;
    }

    CtiCalc &operator=( CtiCalc &toCopy );
    BOOL operator==( CtiCalc &equalTest )  
    {  
        return _pointId == equalTest.getPointId( );
    }
    bool isBaselineCalc() {
        return _isBaseline;
    }

    void appendComponent( CtiCalcComponent *componentToAdd );
    void cleanup( void );
    void clearComponentDependencies( void );
    PointUpdateType getUpdateType( void );
    double calculate( int &calc_quality, CtiTime &calc_time, bool &calcValid );
    double figureDemandAvg(long secondsInAvg);
    BOOL ready( void );
    bool push( double );
    double pop( void );
};

#endif   // #ifndef __CALC_H__
// square root of 3 for power factor calculations
#define SQRT3               1.7320508075688772935274463415059


