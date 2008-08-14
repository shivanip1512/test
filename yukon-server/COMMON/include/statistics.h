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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/08/14 18:26:11 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __STATISTICS_H__
#define __STATISTICS_H__
#pragma warning( disable : 4786)


#include <rw/db/status.h>

#include "counter.h"
#include "ctidate.h"
#include "yukon.h"

using std::pair;
// Uncomment this to execute the code with the old CtiCounters instead of arrays.
// #define CTISTATUSECOUNTERS FALSE


#define STATISTICS_REPORT_ON_MSGFLAGS  0x00000001
#define STATISTICS_COMPENSATED_RESULTS 0x00000010
#define STATISTICS_REPORT_ON_RESULTS   0x00000020

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
        Lifetime,

        FinalCounterBin               // Leave this guy last!
    };

    enum CtiStatisticsCounters_t
    {
        Requests,
        Completions,
        Attempts,
        CommErrors,
        ProtocolErrors,
        SystemErrors,

        FinalStatType
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
    void incrementRequest(const CtiTime &stattime);
    void decrementRequest(const CtiTime &stattime);
    void incrementAttempts(const CtiTime &stattime, int CompletionStatus);        // This is a retry scenario
    void incrementCompletion(const CtiTime &stattime, int CompletionStatus);
    long getID() const;
    CtiStatistics& setID(long id);

    void dumpStats() const;
    int getTotalErrors(int Counter) const;
    double getCompletionRatio(int Counter) const;

    double getThreshold(int Counter) const;
    CtiStatistics& setThreshold(int Counter, double thresh);

    int get(int counter, int index ) const;
    static string getTableName();
    static string getTableNameDynamicHistory();

    RWDBStatus::ErrorCode Update(RWDBConnection &conn);
    RWDBStatus::ErrorCode Update();
    RWDBStatus::ErrorCode Insert(RWDBConnection &conn);
    RWDBStatus::ErrorCode Insert();
    RWDBStatus::ErrorCode Restore();
    RWDBStatus::ErrorCode InsertDaily(RWDBConnection &conn);
    static RWDBStatus::ErrorCode PruneDaily(RWDBConnection &conn);
    int resolveStatisticsType(const string& rwsTemp) const;
    void computeHourInterval(int hournumber, pair<CtiTime, CtiTime> &myinterval);
    void computeDailyInterval(pair<CtiTime, CtiTime> &myinterval);
    void computeYesterdayInterval(pair<CtiTime, CtiTime> &myinterval);
    void computeMonthInterval(pair<CtiTime, CtiTime> &myinterval);
    void computeLastMonthInterval(pair<CtiTime, CtiTime> &myinterval);
    CtiStatisticsCounters_t resolveFailType( int CompletionStatus ) const;


protected:

    long _pid;                   // paoid.
    static string _counterName[FinalCounterBin];

    bool        _dirtyCounter[FinalCounterBin];
    #ifdef CTISTATUSECOUNTERS
    CtiCounter  _counter[FinalCounterBin];
    #else
    int        _counter[FinalStatType][FinalCounterBin];
    #endif
    int         _threshold[FinalCounterBin];
    bool        _thresholdAlarm[FinalCounterBin];
    pair<CtiTime, CtiTime> _startStopTimePairs[FinalCounterBin];

    void        verifyThresholds();

    static string getCounterName( int Counter )
    {
        return _counterName[Counter];
    }

private:

    mutable CtiMutex _statMux;

    int  _restoreworked;
    bool _updateOnNextCompletion;     // If set, the next completion causes an update
    bool _dirty;

    CtiTime _previoustime;
    bool _doHistInsert;
    static CtiDate _lastPrune;

    int newHour(const CtiTime &newtime, CtiStatisticsCounters_t countertoclean);
    void incrementFail(const CtiTime &stattime, CtiStatisticsCounters_t failtype);
    void incrementSuccess(const CtiTime &stattime);

    inline void incrementCounter( int statTypeIndex, int statBinIndex, int bump = 1 )      // Bump MAY be negative for a decrement!
    {
        #ifdef CTISTATUSECOUNTERS
        _counter[ statBinIndex ].inc( statTypeIndex, bump );
        #else
        if(statTypeIndex < FinalStatType && statBinIndex < FinalCounterBin)
        {
            _counter[statTypeIndex][statBinIndex] += bump;
        }
        #endif
        _dirtyCounter[ statBinIndex ] = true;
    }

    inline void resetCounter( int statBinIndex )
    {
        #ifdef CTISTATUSECOUNTERS
        _counter[statBinIndex].resetAll( );
        #else
        if(statBinIndex < FinalCounterBin)
        {
            for(int i = 0; i < FinalStatType; i++)
            {
                _counter[i][statBinIndex] = 0;
            }
        }
        #endif
        _dirtyCounter[ statBinIndex ] = true;
    }

    //Set Counter does not mark the row as dirty. This is only used on copy and on load.
    inline void setInitialCounterVal( int statTypeIndex, int statBinIndex, int value )
    {
        #ifdef CTISTATUSECOUNTERS
        _counter[statBinIndex].set( value );
        #else
        if(statTypeIndex < FinalStatType && statBinIndex < FinalCounterBin)
        {
            _counter[statTypeIndex][statBinIndex] = value;
        }
        #endif
    }

    inline int getCounter( int statTypeIndex, int statBinIndex ) const
    {
        int retval = 0;

        #ifdef CTISTATUSECOUNTERS
        retval = _counter[statBinIndex].get( statTypeIndex );
        #else
        if(statTypeIndex < FinalStatType && statBinIndex < FinalCounterBin)
        {
            retval = _counter[statTypeIndex][statBinIndex];
        }
        #endif

        return retval;
    }

    inline void copyCounter( int destBinIndex, int srcBinIndex )
    {
        #ifdef CTISTATUSECOUNTERS
        _counter[ destBinIndex ] = _counter[ srcBinIndex ];
        #else
        if(destBinIndex < FinalCounterBin && srcBinIndex < FinalCounterBin)
        {
            for(int i = 0; i < FinalStatType; i++)
            {
                _counter[i][destBinIndex] = _counter[i][srcBinIndex];
            }
        }
        #endif
        _dirtyCounter[ destBinIndex ] = true;
    }

};
#endif // #ifndef __STATISTICS_H__
