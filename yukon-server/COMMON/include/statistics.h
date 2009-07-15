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

#include <bitset>
#include <string>

#include "dlldefs.h"
#include "ctitime.h"

#define STATISTICS_REPORT_ON_MSGFLAGS  0x00000001
#define STATISTICS_COMPENSATED_RESULTS 0x00000010
#define STATISTICS_REPORT_ON_RESULTS   0x00000020

class IM_EX_CTIBASE CtiStatistics
{
public:

    CtiStatistics(long id = 0);
    CtiStatistics(const CtiStatistics& aRef);
    virtual ~CtiStatistics();

    long getID() const;

    bool operator<( const CtiStatistics &rhs ) const;

    CtiStatistics& operator=(const CtiStatistics& aRef);

    bool isDirty() const;
    CtiStatistics& resetDirty();
    void updateTime(const CtiTime &now_time);
    void incrementRequest(const CtiTime &stattime);
    void incrementAttempts(const CtiTime &stattime, int CompletionStatus);        // This is a retry scenario
    void incrementCompletion(const CtiTime &stattime, int CompletionStatus);

    static void getSQL(RWDBDatabase &db, RWDBTable &table, RWDBSelector &selector, std::vector<long>::iterator id_begin, std::vector<long>::iterator id_end);
    static void Factory(RWDBReader &rdr, vector<CtiStatistics *> &restored);

    RWDBStatus::ErrorCode Record(RWDBConnection &conn);
    RWDBStatus::ErrorCode Restore();
    RWDBStatus::ErrorCode InsertDaily(RWDBConnection &conn);
    static RWDBStatus::ErrorCode PruneDaily(RWDBConnection &conn);

    static bool isCommFail( int CompletionStatus );

protected:

private:

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

    enum
    {
        CurrentHour,
        PreviousHour,
        Daily,
        Yesterday,
        Monthly,
        LastMonth,
        Lifetime,

        FinalCounterBin               // Leave this guy last!
    };

    long _paoid;

    std::bitset<FinalCounterBin> _dirtyCounter, _rowExists;
    std::bitset<24> _hourRowExists;
    int  _counter[FinalStatType][FinalCounterBin];
    std::pair<CtiTime, CtiTime> _intervalBounds[FinalCounterBin];
    bool _dirty;
    bool _doHistInsert;

    static std::string getTableName();
    static std::string getTableNameDynamicHistory();

    static std::string getCounterName( int Counter, const CtiTime &tm );

    static CtiStatisticsCounters_t resolveFailType( int CompletionStatus );

    static void computeHourInterval(int hournumber, std::pair<CtiTime, CtiTime> &myinterval);
    static void computeDailyInterval(std::pair<CtiTime, CtiTime> &myinterval);
    static void computeMonthInterval(std::pair<CtiTime, CtiTime> &myinterval);

    void rotateCounters(const CtiTime &newtime);

    RWDBStatus::ErrorCode Update(RWDBConnection &conn, int counter);
    RWDBStatus::ErrorCode Insert(RWDBConnection &conn, int counter);

    inline void incrementCounter( int statTypeIndex, int statBinIndex)
    {
        if(statTypeIndex < FinalStatType && statBinIndex < FinalCounterBin)
        {
            ++_counter[statTypeIndex][statBinIndex];
        }
        _dirtyCounter.set(statBinIndex);
    }

    inline void resetCounter( int statBinIndex )
    {
        if(statBinIndex < FinalCounterBin)
        {
            for(int i = 0; i < FinalStatType; i++)
            {
                _counter[i][statBinIndex] = 0;
            }
        }
        _dirtyCounter.set(statBinIndex);
    }

    //Set Counter does not mark the row as dirty. This is only used on copy and on load.
    inline void setInitialCounterVal( int statTypeIndex, int statBinIndex, int value )
    {
        if(statTypeIndex < FinalStatType && statBinIndex < FinalCounterBin)
        {
            _counter[statTypeIndex][statBinIndex] = value;
        }
    }

    inline int getCounter( int statTypeIndex, int statBinIndex ) const
    {
        int retval = 0;

        if(statTypeIndex < FinalStatType && statBinIndex < FinalCounterBin)
        {
            retval = _counter[statTypeIndex][statBinIndex];
        }

        return retval;
    }

    inline void copyCounter( int destBinIndex, int srcBinIndex )
    {
        if(destBinIndex < FinalCounterBin && srcBinIndex < FinalCounterBin)
        {
            for(int i = 0; i < FinalStatType; i++)
            {
                _counter[i][destBinIndex] = _counter[i][srcBinIndex];
            }
        }
        _dirtyCounter.set(destBinIndex);
    }

    inline bool hasNonZeroValue( unsigned int binIndex )
    {
        if(binIndex < FinalCounterBin)
        {
            for(int i = 0; i < FinalStatType; i++)
            {
                if(_counter[i][binIndex] != 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

};
#endif // #ifndef __STATISTICS_H__
