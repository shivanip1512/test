#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   statistics
*
* Date:   5/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/29 15:14:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/rwdate.h>

#include "dsm2.h"
#include "dsm2err.h"
#include "logger.h"
#include "statistics.h"


void CtiStatistics::incrementRequest(const RWTime &stattime)
{
    int HourNo = newHour(stattime, Requests);

    _counter[ HourNo ].inc( Requests );
    _counter[ Daily ].inc( Requests );
    _counter[ Monthly ].inc( Requests );
}

void CtiStatistics::incrementAttempts(const RWTime &stattime)        // This is a retry scenario
{
    int HourNo = newHour(stattime, Attempts);

    _counter[HourNo].inc( Attempts );
    _counter[ Daily ].inc( Attempts );
    _counter[ Monthly ].inc( Attempts );
}

void CtiStatistics::incrementCompletion(const RWTime &stattime, int CompletionStatus)
{
    incrementAttempts(stattime);

    if(CompletionStatus == NORMAL)
    {
        incrementSuccess(stattime);
    }
    else
    {
        incrementFail(stattime, resolveFailType(CompletionStatus));
    }

    verifyThresholds();
}


void CtiStatistics::incrementFail(const RWTime &stattime, CtiStatisticsCounters_t failtype)
{
    int HourNo = newHour(stattime, failtype);

    _counter[HourNo].inc( failtype );
    _counter[ Daily ].inc( failtype );
    _counter[ Monthly ].inc( failtype );
}

void CtiStatistics::incrementSuccess(const RWTime &stattime)
{
    int HourNo = newHour(stattime, Completions);

    _counter[HourNo].inc( Completions );
    _counter[ Daily ].inc( Completions );
    _counter[ Monthly ].inc( Completions );
}

CtiStatistics::CtiStatisticsCounters_t CtiStatistics::resolveFailType( int CompletionStatus ) const
{
    CtiStatisticsCounters_t failtype = SystemErrors;
    int errortype = GetErrorType(CompletionStatus);

    switch(errortype)
    {
    case ERRTYPESYSTEM:
        {
            failtype = SystemErrors;
            break;
        }
    case ERRTYPECOMM:
        {
            failtype = CommErrors;
            break;
        }
    case ERRTYPEPROTOCOL:
        {
            failtype = ProtocolErrors;
            break;
        }
    }

    return failtype;
}

int CtiStatistics::newHour(const RWTime &newtime, CtiStatisticsCounters_t countertoclean)
{
    static unsigned dumpit = 1;
    bool newsomething = false;
    unsigned HourNo = newtime.hour();

    RWDate lastdate( _previoustime );
    RWDate newdate( newtime );

    if( HourNo != _previoustime.hour() )
    {
        // Reset the new counter to zero in case we've run for an entire 24 hours... hm.
        _counter[HourNo].reset( countertoclean );
        newsomething = true;
    }

    if(lastdate.day() != newdate.day())
    {
        // Copy current to previous. Write out a report.
        _counter[ Yesterday ] = _counter[ Daily ];

        // Completely reset the daily counter.
        _counter[ Daily ].resetAll();
        newsomething = true;
    }

    if(lastdate.month() != newdate.month())
    {
        // Copy current to previous.
        _counter[ LastMonth ] = _counter[ Monthly ];

        // Completely reset the monthly counter.
        _counter[ Monthly ].resetAll( );
        newsomething = true;
    }

    _previoustime = newtime;

    if(newsomething || !(++dumpit % 50))
    {
        dumpStats();
    }

    return HourNo;
}

long CtiStatistics::getID() const
{
    return _pid;
}

CtiStatistics& CtiStatistics::setID(long id)
{
    _pid = id;
    return *this;
}

bool CtiStatistics::operator<( const CtiStatistics &rhs ) const
{
    return(getID() < rhs.getID() );
}
bool CtiStatistics::operator==( const CtiStatistics &rhs ) const
{
    return(getID() == rhs.getID() );
}
bool CtiStatistics::operator()(const CtiStatistics &aRef) const
{
    return operator<(aRef);
}

int CtiStatistics::getTotalErrors(int Counter) const
{
    return _counter[ Counter ].get( CommErrors ) + _counter[ Counter ].get( ProtocolErrors ) + _counter[ Counter ].get( SystemErrors);
}

double CtiStatistics::getCompletionRatio(int Counter) const
{
    double ratio = 1.0;

    if(_counter[ Counter ].get( Requests ) > 10)        // No computation until 10 Attempts have been made..
    {
        ratio = (double)_counter[ Counter ].get( Completions ) / (double)_counter[ Counter ].get( Requests );
    }
    return ratio;
}

void CtiStatistics::dumpStats() const
{
    if(getDebugLevel() & DEBUGLEVEL_STATISTICS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Statistics Report for PaoID " << _pid << endl;
        dout << "   Daily Requests              " << _counter[ Daily ].get( Requests ) << endl;
        dout << "   Daily Attempts              " << _counter[ Daily ].get( Attempts ) << endl;
        dout << "   Daily Completions           " << _counter[ Daily ].get( Completions ) << endl;

        int starthour = (RWTime().hour() + 1) % 24;

        for( ; starthour != RWTime().hour() ; starthour = (starthour + 1) % 24)
        {
            dout << "   " << getCounterName(starthour) << " Completions Ratio   " << getCompletionRatio(starthour) << endl;
        }

        // Yak the final and current hour out!
        dout << "   " << getCounterName(starthour) << " Completions Ratio   " << getCompletionRatio(starthour) << endl;
        dout << "   " << getCounterName(Daily) << " Completions Ratio   " << getCompletionRatio(Daily) << endl;
        dout << "   " << getCounterName(Yesterday) << " Completions Ratio   " << getCompletionRatio(Yesterday) << endl;
        dout << "   " << getCounterName(Monthly) << " Completions Ratio   " << getCompletionRatio(Monthly) << endl;
        dout << "   " << getCounterName(LastMonth) << " Completions Ratio   " << getCompletionRatio(LastMonth) << endl;
    }
}

void CtiStatistics::verifyThresholds()
{
    int i;
    double ratio;

    for(i = 0; i < FinalCounterSlot; i++)
    {
        if( (ratio = getCompletionRatio(i)) < _threshold[i] )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Threshold " << getCounterName(i) << " has been crossed" << endl;
            }

            _thresholdAlarm[i] = true;
        }
        else
        {
            _thresholdAlarm[i] = false;
        }
    }

}

RWCString CtiStatistics::_counterName[FinalCounterSlot] = {
    "Hour0    ",
    "Hour1    ",
    "Hour2    ",
    "Hour3    ",
    "Hour4    ",
    "Hour5    ",
    "Hour6    ",
    "Hour7    ",
    "Hour8    ",
    "Hour9    ",
    "Hour10   ",
    "Hour11   ",
    "Hour12   ",
    "Hour13   ",
    "Hour14   ",
    "Hour15   ",
    "Hour16   ",
    "Hour17   ",
    "Hour18   ",
    "Hour19   ",
    "Hour20   ",
    "Hour21   ",
    "Hour22   ",
    "Hour23   ",
    "Daily    ",
    "Yesterday",
    "Monthly  ",
    "LastMonth"
    };

double CtiStatistics::getThreshold(int Counter) const
{
    return _threshold[Counter];
}

CtiStatistics& CtiStatistics::setThreshold(int Counter, double thresh)
{
    _threshold[Counter] = thresh;
    return *this;
}

