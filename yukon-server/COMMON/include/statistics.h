
#pragma warning( disable : 4786)
#ifndef __STATISTICS_H__
#define __STATISTICS_H__

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/29 15:14:10 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/rwtime.h>
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


    CtiStatistics(long id = 0) :
        _pid(id)
    {
        int i;
        for(i = 0; i < FinalCounterSlot; i++)
        {
            _threshold[i] = 0.0;
            _thresholdAlarm[i] = false;
        }
    }

    CtiStatistics(const CtiStatistics& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiStatistics() {}

    bool operator<( const CtiStatistics &rhs ) const;
    bool operator==( const CtiStatistics &rhs ) const;
    bool operator()(const CtiStatistics &aRef) const;

    CtiStatistics& operator=(const CtiStatistics& aRef)
    {
        if(this != &aRef)
        {
            _pid = aRef._pid;
            _previoustime = aRef._previoustime;

            int i;

            for(i = 0; i < FinalCounterSlot; i++)
            {
                _counter[i] = aRef._counter[i];
                _threshold[i] = aRef._threshold[i];
                _thresholdAlarm[i] = aRef._thresholdAlarm[i];
            }
        }
        return *this;
    }

    void incrementRequest(const RWTime &stattime);
    void incrementAttempts(const RWTime &stattime);        // This is a retry scenario
    void incrementCompletion(const RWTime &stattime, int CompletionStatus);
    long getID() const;
    CtiStatistics& setID(long id);

    void dumpStats() const;
    int getTotalErrors(int Counter) const;
    double getCompletionRatio(int Counter) const;

    double getThreshold(int Counter) const;
    CtiStatistics& setThreshold(int Counter, double thresh);

protected:

    long _pid;                   // paoid.
    static RWCString _counterName[FinalCounterSlot];

    CtiCounter _counter[FinalCounterSlot];
    int _threshold[FinalCounterSlot];
    bool _thresholdAlarm[FinalCounterSlot];
    void verifyThresholds();

    static RWCString getCounterName( int Counter )
    {
        return _counterName[Counter];
    }


private:

    RWTime _previoustime;

    CtiStatisticsCounters_t resolveFailType( int CompletionStatus ) const;
    int newHour(const RWTime &newtime, CtiStatisticsCounters_t countertoclean);
    void incrementFail(const RWTime &stattime, CtiStatisticsCounters_t failtype);
    void incrementSuccess(const RWTime &stattime);


};
#endif // #ifndef __STATISTICS_H__
