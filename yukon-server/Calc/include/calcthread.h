#pragma once

#include "msg_multi.h"

#include "calc.h"
#include "pointstore.h"

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

    using CtiCalcPointMap = std::map<long, std::unique_ptr<CtiCalc>>;

private:
    CtiCalcPointMap _periodicPoints, _onUpdatePoints, _constantPoints, _historicalPoints, _backfillingPoints;
    std::queue<long> _auAffectedPoints;
    std::queue<std::unique_ptr<CtiMultiMsg>> _outbox;
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
    using PointDataMsgs = std::vector<std::unique_ptr<CtiPointDataMsg>>;
    struct HistoricalResults {
        CtiTime newTime;
        PointDataMsgs messages;
    };

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

    auto getCalcHistoricalLastUpdatedTime() -> PointTimeMap;
    auto getHistoricalTableData(const CtiCalc& calcPoint, const CtiTime lastTime) -> DynamicTableData;
    auto getHistoricalTableSinglePointData(const long calcPoint, const CtiTime lastTime) -> DynamicTableSinglePointData;
    void setHistoricalPointStore(const HistoricalPointValueMap& valueMap);
    void updateCalcHistoricalLastUpdatedTime(PointTimeMap &unlistedPoints, PointTimeMap &updatedPoints);
    void getCalcBaselineMap(PointBaselineMap &baselineMap);
    void getBaselineMap(BaselineMap &baselineMap);
    void getCurtailedDates(DatesSet &curtailedDates, long pointID, CtiTime &startTime);
    bool processDay(long pointID, CtiTime curTime, const DynamicTableSinglePointData& data, const DynamicTableSinglePointData& percentData, int percent, HourlyValues &results);

    std::unique_ptr<CtiMultiMsg> processHistoricalPoints(const CtiDate earliestCalcDate, const CtiDate earliestBackfill, const std::function<bool(Cti::CallSite)> wasReloaded);
    PointDataMsgs calcHistoricalPoints(const PointTimeMap& dbTimeMap, const CtiDate earliestCalcDate, const CtiDate earliestBackfill, const std::function<bool(Cti::CallSite)> wasReloaded);
    std::optional<HistoricalResults> calcHistoricalPoint(CtiCalc& calcPoint, const CtiTime lastTime, const CtiDate earliestCalcDate, const std::function<bool(Cti::CallSite)> wasReloaded);
    std::optional<HistoricalResults> calcBackfillingPoint(CtiCalc& calcPoint, const CtiTime lastTime, const CtiDate earliestCalcDate, const std::function<bool(Cti::CallSite)> wasReloaded);
    std::unique_ptr<CtiPointDataMsg> calcFromValues(CtiCalc& calcPoint, const CtiTime dynamicTime, const HistoricalPointValueMap& dynamicValues);

public:

    CtiCalculateThread();
    ~CtiCalculateThread() = default;
    CtiCalculateThread(const CtiCalculateThread&) = delete;
    CtiCalculateThread& operator=(const CtiCalculateThread&) = delete;

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


    bool hasOutboxEntries( void )                        {  return ! _outbox.empty();  }
    std::unique_ptr<CtiMultiMsg> getOutboxEntry( void )  {  auto msg = std::move(_outbox.front());  _outbox.pop();  return std::move(msg);  }

    std::vector<long> getPointDependencies() const;

    void startThreads();
    void joinThreads();

    void interruptThreads();
    void pauseThreads();
    void resumeThreads();

    void sendConstants();

    void stealPointMaps(CtiCalculateThread& victim);

    void clearPointMaps();

    void removePointStoreObject( const long aPointID );
};

