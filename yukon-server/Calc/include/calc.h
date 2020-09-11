#pragma once

#include "ctiqueues.h"

#include "calccomponent.h"

class CtiCalc
{
    std::vector<std::unique_ptr<CtiCalcComponent>>  _components;
    CtiStack<double>     _stack;
    PointUpdateType      _updateType;
    ULONG                _nextInterval;
    int                  _updateInterval;
    long                 _pointId, _baselineId, _baselinePercentId, _regressionPtId;
    BOOL                 _valid;
    bool                 _calculateQuality;
    CtiTime              _pointCalcWindowEndTime;
    bool                 _isBaseline;
    bool                 _reuseRegression;

    // text from the database
    static const std::string UpdateType_Periodic;
    static const std::string UpdateType_AllChange;
    static const std::string UpdateType_OneChange;
    static const std::string UpdateType_Historical;
    static const std::string UpdateType_BackfilledHistorical;
    static const std::string UpdateType_PeriodicPlusUpdate;
    static const std::string UpdateType_Constant;

    CtiTime calcTimeFromComponentTime( const CtiTime &minTime, const CtiTime &maxTime );
    bool calcTimeFromComponentTime( CtiTime &componentTime, int componentQuality, CtiTime &minTime, CtiTime &maxTime );
    int calcQualityFromComponentQuality( int qualityFlag, const CtiTime &minTime, const CtiTime &maxTime );

public:

    CtiCalc() = delete;
    CtiCalc(const CtiCalc&) = delete;
    CtiCalc& operator=(const CtiCalc&) = delete;
    bool operator==(const CtiCalc&) = delete;

    CtiCalc( long pointId, const std::string &updateType, int updateInterval, const std::string &qualityFlag );

    ULONG     getNextInterval() const;
    CtiCalc&  setNextInterval (int interval);
    int      getUpdateInterval( ) const;
    long     getRegressionComponentId() const;
    int      getComponentCount();
    std::set<long> getComponentIDList();
    long findDemandAvgComponentPointId();

    long getPointId( void ) const
    {  
        return _pointId;
    }

    long getBaselineId( void ) const
    {  
        return _baselineId;
    }

    long getBaselinePercentId( void ) const
    {  
        return _baselinePercentId;
    }

    bool isBaselineCalc() {
        return _isBaseline;
    }

    void appendComponent( std::unique_ptr<CtiCalcComponent> componentToAdd );
    void cleanup( void );
    void clearComponentDependencies( void );
    PointUpdateType getUpdateType( void );
    double calculate( int &calc_quality, CtiTime &calc_time, bool &calcValid );
    double figureDemandAvg(long secondsInAvg);
    BOOL ready( void );
    bool push( double );
    double pop( void );
};

