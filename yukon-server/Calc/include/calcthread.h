#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALCTHREAD_H__
#define __CALCTHREAD_H__

#include <functional>

#include <rw/tphdict.h>
//#include <rw/tvdeque.h>
//#include <rw/tpdeque.h>
#include <rw/thr/thrfunc.h>
#include <map>
#include <vector>

#include "hashkey.h"
#include "msg_multi.h"

#include "calc.h"
#include "pointstore.h"
#include "ctiqueues.h"

//ecs 1/5/2005
#include "thread_monitor.h"
#include "thread_register_data.h"
//

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
    RWMutexLock _pointDataMutex;

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
    static void sendUserQuit(void *who);

    mutable RWRecursiveLock<RWMutexLock> _mutex;

    RWThreadFunction _periodicThreadFunc;
    RWThreadFunction _onUpdateThreadFunc;
    RWThreadFunction _historicalThreadFunc;
    RWThreadFunction _baselineThreadFunc;

    CtiCalcThreadInterruptReason _interruptReason;

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

    CtiCalculateThread( void )
    {
    };

    ~CtiCalculateThread( void );

    RWMutexLock outboxMux;

    void calcThread( void );
    bool appendPoint( long pointID, string &updateType, int updateInterval, string &qualityFlag );
    void appendPointComponent( long pointID, string &componentType, long componentPointID,
                               string &operationType, double constantValue, string &functionName );
    void appendCalcPoint( long pointID );
    void pointChange( long changedID, double newValue, const CtiTime &newTime, unsigned newQuality, unsigned newTags );
    void pointSignal( long changedID, unsigned newTags );

    BOOL isACalcPointID(const long aPointID);
    BOOL isAPeriodicCalcPointID(const long aPointID);
    BOOL isAnOnUpdateCalcPointID(const long aPointID);
    BOOL isAConstantCalcPointID(const long aPointID);

    BOOL isAHistoricalCalcPointID(const long aPointID);
    long numberOfLoadedCalcPoints() { return (_periodicPoints.size() + _onUpdatePoints.size() + _constantPoints.size() + _historicalPoints.size()); };


    int outboxEntries( void )   {   return _outbox.entries( ); };
    CtiMultiMsg *getOutboxEntry( void )                         {   return _outbox.popFront( ); };
    //Bad?
    RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, std::equal_to<CtiHashKey> >
    *getPointDependencyIterator( void )                         {   return CTIDBG_new RWTPtrHashMapIterator<CtiHashKey, CtiPointStoreElement, my_hash<CtiHashKey>, std::equal_to<CtiHashKey> >( *CtiPointStore::getInstance() );   };

    void startThreads(  );
    void joinThreads(  );

    void interruptThreads( CtiCalcThreadInterruptReason reason );
    void resumeThreads(  );

    void sendConstants( );

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

#endif // #ifndef __CALCTHREAD_H__

