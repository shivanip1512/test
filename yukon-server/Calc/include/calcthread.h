#pragma once

#include "msg_multi.h"

#include "calc.h"
#include "pointstore.h"
#include "ctiqueues.h"

#include "CalcWorkerThread.h"

#include <functional>

#include <map>
#include <vector>

class CtiCalculateThread
{
public:

    typedef enum
    {
        Shutdown = 0,
        DBReload,
        Pause

    } CtiCalcThreadInterruptReason;

    typedef std::map<long, CtiCalc* > CtiCalcPointMap;
    typedef std::map<long, CtiCalc* >::iterator CtiCalcPointMapIterator;

private:
    CtiCalcPointMap _periodicPoints, _onUpdatePoints, _constantPoints, _historicalPoints;
    CtiValDeque<long> _auAffectedPoints;
    CtiPtrDeque<CtiMultiMsg> _outbox;
    CtiCriticalSection _pointDataMutex;

    struct BaselineData
    {
        int maxSearchDays;
        int usedDays;
        int percent;
        long holidays;
        std::string excludedWeekDays;
    };
    typedef std::pair<long, double> PointValuePair;
    typedef std::vector<double> HourlyValues;
    typedef std::map<long, CtiTime> PointTimeMap;
    typedef std::set<CtiDate> DatesSet;
    typedef std::map<long, double> HistoricalPointValueMap;
    typedef std::map<long, long> PointBaselineMap;
    typedef std::map<long, BaselineData> BaselineMap;
    typedef std::map<CtiTime, HistoricalPointValueMap > DynamicTableData;
    typedef std::map<CtiTime, HistoricalPointValueMap >::iterator DynamicTableDataIter;
    typedef std::map<CtiTime, PointValuePair> DynamicTableSinglePointData;
    typedef std::map<CtiTime, PointValuePair >::iterator DynamicTableSinglePointDataIter;

    void periodicThread( void );
    void onUpdateThread( void );
    void historicalThread( void );
    void baselineThread( void );
    static void sendUserQuit( const std::string & who );

    mutable CtiCriticalSection _mutex;

    Cti::CalcLogic::CalcWorkerThread    _periodicThreadFunc;
    Cti::CalcLogic::CalcWorkerThread    _onUpdateThreadFunc;
    Cti::CalcLogic::CalcWorkerThread    _historicalThreadFunc;
    Cti::CalcLogic::CalcWorkerThread    _baselineThreadFunc;

    void getCalcHistoricalLastUpdatedTime(PointTimeMap &dbTimeMap);
    void getHistoricalTableData(CtiCalc *calcPoint, CtiTime &lastTime, DynamicTableData &data);
    void getHistoricalTableSinglePointData(long calcPoint, CtiTime &lastTime, DynamicTableSinglePointData &data);
    void setHistoricalPointStore(HistoricalPointValueMap &valueMap);
    void updateCalcHistoricalLastUpdatedTime(PointTimeMap &unlistedPoints, PointTimeMap &updatedPoints);
    void getCalcBaselineMap(PointBaselineMap &baselineMap);
    void getBaselineMap(BaselineMap &baselineMap);
    void getCurtailedDates(DatesSet &curtailedDates, long pointID, CtiTime &startTime);
    bool processDay(long pointID, CtiTime curTime, DynamicTableSinglePointData &data, DynamicTableSinglePointData &percentData, int percent, HourlyValues &results);

public:

    CtiCalculateThread();
    ~CtiCalculateThread();

    CtiCriticalSection outboxMux;

    void calcThread( void );
    bool appendPoint( long pointID, std::string &updateType, int updateInterval, std::string &qualityFlag );
    void appendPointComponent( long pointID, std::string &componentType, long componentPointID,
                               std::string &operationType, double constantValue, std::string &functionName );
    void appendCalcPoint( long pointID );
    void pointChange( long changedID, double newValue, const CtiTime newTime, unsigned newQuality, unsigned newTags );
    void pointSignal( long changedID, unsigned newTags );

    BOOL isACalcPointID(const long aPointID);

    long numberOfLoadedCalcPoints() { return (_periodicPoints.size() + _onUpdatePoints.size() + _constantPoints.size() + _historicalPoints.size()); };


    int outboxEntries( void )   {   return _outbox.entries( ); };
    CtiMultiMsg *getOutboxEntry( void )                         {   return _outbox.popFront( ); };

    std::vector<long> getPointDependencies() const;

    void startThreads();
    void joinThreads();

    void interruptThreads();
    void pauseThreads();
    void resumeThreads();

    void sendConstants();

    CtiCalcPointMap getPeriodicPointMap() const;
    CtiCalcPointMap getOnUpdatePointMap() const;
    CtiCalcPointMap getConstantPointMap() const;
    CtiCalcPointMap getHistoricalPointMap() const;

    void setPeriodicPointMap(const CtiCalcPointMap &);
    void setOnUpdatePointMap(const CtiCalcPointMap &);
    void setConstantPointMap(const CtiCalcPointMap &);
    void setHistoricalPointMap(const CtiCalcPointMap &);

    void clearPointMaps();
    void clearAndDestroyPointMaps();

    void removePointStoreObject( const long aPointID );
};

