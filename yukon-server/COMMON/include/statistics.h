/*-----------------------------------------------------------------------------*
*
* File:   statistics
*
* Class:  CtiStatistics
* Date:   5/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/09/08 19:55:16 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __STATISTICS_H__
#define __STATISTICS_H__
#pragma warning( disable : 4786)


#include <rw/rwtime.h>
#include <rw/db/status.h>

#include "counter.h"
#include "yukon.h"

class IM_EX_CTIBASE CtiStatistics
{
public:

    enum
    {
        Hour0,
        Hour1,
        Hour2,
        Hour3,
        Hour4,
        Hour5,
        Hour6,
        Hour7,
        Hour8,
        Hour9,
        Hour10,
        Hour11,
        Hour12,
        Hour13,
        Hour14,
        Hour15,
        Hour16,
        Hour17,
        Hour18,
        Hour19,
        Hour20,
        Hour21,
        Hour22,
        Hour23,
        Daily,
        Yesterday,
        Monthly,
        LastMonth,

        FinalCounterSlot               // Leave this guy last!
    };

    enum CtiStatisticsCounters_t
    {
        Requests,
        Completions,
        Attempts,
        CommErrors,
        ProtocolErrors,
        SystemErrors
    };


    CtiStatistics(long id = 0);
    CtiStatistics(const CtiStatistics& aRef);
    virtual ~CtiStatistics();

    bool operator<( const CtiStatistics &rhs ) const;
    bool operator==( const CtiStatistics &rhs ) const;
    bool operator()(const CtiStatistics &aRef) const;

    CtiStatistics& operator=(const CtiStatistics& aRef);

    bool isDirty() const;
    CtiStatistics& resetDirty();
    void incrementRequest(const RWTime &stattime);
    void decrementRequest(const RWTime &stattime);
    void incrementAttempts(const RWTime &stattime, int CompletionStatus);        // This is a retry scenario
    void incrementCompletion(const RWTime &stattime, int CompletionStatus);
    long getID() const;
    CtiStatistics& setID(long id);

    void dumpStats() const;
    int getTotalErrors(int Counter) const;
    double getCompletionRatio(int Counter) const;

    double getThreshold(int Counter) const;
    CtiStatistics& setThreshold(int Counter, double thresh);

    int get(int counter, int index ) const;
    static RWCString getTableName();

    RWDBStatus::ErrorCode Update(RWDBConnection &conn);
    RWDBStatus::ErrorCode Update();
    RWDBStatus::ErrorCode Insert(RWDBConnection &conn);
    RWDBStatus::ErrorCode Insert();
    RWDBStatus::ErrorCode Restore();
    int resolveStatisticsType(RWCString rwsTemp) const;
    void computeHourInterval(int hournumber, pair<RWTime, RWTime> &myinterval);
    void computeDailyInterval(pair<RWTime, RWTime> &myinterval);
    void computeYesterdayInterval(pair<RWTime, RWTime> &myinterval);
    void computeMonthInterval(pair<RWTime, RWTime> &myinterval);
    void computeLastMonthInterval(pair<RWTime, RWTime> &myinterval);
    CtiStatisticsCounters_t resolveFailType( int CompletionStatus ) const;


protected:

    long _pid;                   // paoid.
    static RWCString _counterName[FinalCounterSlot];

    CtiCounter  _counter[FinalCounterSlot];
    int         _threshold[FinalCounterSlot];
    bool        _thresholdAlarm[FinalCounterSlot];
    pair<RWTime, RWTime> _startStopTimePairs[FinalCounterSlot];

    void        verifyThresholds();

    static RWCString getCounterName( int Counter )
    {
        return _counterName[Counter];
    }

private:

    mutable CtiMutex _statMux;

    int  _restoreworked;
    bool _updateOnNextCompletion;     // If set, the next completion causes an update
    bool _dirty;

    RWTime _previoustime;

    int newHour(const RWTime &newtime, CtiStatisticsCounters_t countertoclean);
    void incrementFail(const RWTime &stattime, CtiStatisticsCounters_t failtype);
    void incrementSuccess(const RWTime &stattime);


};
#endif // #ifndef __STATISTICS_H__
