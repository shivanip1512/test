/*-----------------------------------------------------------------------------*
*
* File:   statistics
*
* Date:   5/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2005/08/22 20:27:49 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <utility>
using namespace std;

#include <rw\cstring.h>
#include <rw\rwtime.h>
#include <rw/db/datetime.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw/db/bkins.h>

#include "dbaccess.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "logger.h"
#include "statistics.h"

CtiStatistics::CtiStatistics(long id) :
    _restoreworked(0),
    _dirty(false),
    _pid(id)
{
    int i;
    for(i = 0; i < FinalCounterBin; i++)
    {
        #ifndef CTISTATUSECOUNTERS
        for(int j=0; j < FinalStatType; j++)
        {
            _counter[j][i] = 0;
        }
        #endif
        _threshold[i] = 0.0;
        _thresholdAlarm[i] = false;

        _dirtyCounter[ i ] = false;
    }

    if(_pid < 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    RWTime now;

    int starthour = (now.hour() + 1) % 24;      // This is 24 hours ago.

    for(i = 23 ; starthour != now.hour() ; starthour = (starthour + 1) % 24, i--)
    {
        computeHourInterval(starthour, _startStopTimePairs[ starthour ]);
    }
    computeHourInterval(starthour, _startStopTimePairs[ starthour ]);

    computeDailyInterval(_startStopTimePairs[ Daily ]);
    computeYesterdayInterval(_startStopTimePairs[ Yesterday ]);
    computeMonthInterval(_startStopTimePairs[ Monthly ]);
    computeLastMonthInterval(_startStopTimePairs[ LastMonth ]);
}

CtiStatistics::CtiStatistics(const CtiStatistics& aRef) :
_dirty(false)
{
    if(this != &aRef)
    {
        *this = aRef;
    }
}

CtiStatistics::~CtiStatistics()
{
    if(_dirty)
    {
        Update();
    }
}


void CtiStatistics::incrementRequest(const RWTime &stattime)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Requests);
    _dirty = true;
    incrementCounter( Requests, HourNo, 1);
    incrementCounter( Requests, Daily, 1);
    incrementCounter( Requests, Monthly, 1);
}

void CtiStatistics::decrementRequest(const RWTime &stattime)        // This is a retry scenario
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Attempts);
    _dirty = true;
    incrementCounter( Requests, HourNo, -1);
    incrementCounter( Requests, Daily, -1);
    incrementCounter( Requests, Monthly, -1);
}

void CtiStatistics::incrementAttempts(const RWTime &stattime, int CompletionStatus)        // This is a retry scenario
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Attempts);
    _dirty = true;

    incrementCounter( Attempts, HourNo, 1);
    incrementCounter( Attempts, Daily, 1);
    incrementCounter( Attempts, Monthly, 1);

    if(CompletionStatus != NORMAL)
    {
        incrementFail(stattime, resolveFailType(CompletionStatus));
    }
}

void CtiStatistics::incrementCompletion(const RWTime &stattime, int CompletionStatus)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);

    incrementAttempts(stattime, CompletionStatus);      // This may also increment the fail counter if CompletionStatus != NORMAL

    if(CompletionStatus == NORMAL)
    {
        incrementSuccess(stattime);
    }

    verifyThresholds();
}

void CtiStatistics::incrementFail(const RWTime &stattime, CtiStatisticsCounters_t failtype)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, failtype);

    _dirty = true;
    incrementCounter( failtype, HourNo, 1);
    incrementCounter( failtype, Daily, 1);
    incrementCounter( failtype, Monthly, 1);
}

void CtiStatistics::incrementSuccess(const RWTime &stattime)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Completions);

    _dirty = true;
    incrementCounter( Completions, HourNo, 1);
    incrementCounter( Completions, Daily, 1);
    incrementCounter( Completions, Monthly, 1);
}

CtiStatistics::CtiStatisticsCounters_t CtiStatistics::resolveFailType( int CompletionStatus ) const
{
    CtiStatisticsCounters_t failtype = SystemErrors;
    int errortype = GetErrorType(CompletionStatus);

    switch(errortype)
    {
    default:
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
    // CtiLockGuard<CtiMutex> guard(_statMux);
    static unsigned dumpit = 1;
    unsigned HourNo = newtime.hour();

    RWDate lastdate( _previoustime );
    RWDate newdate( newtime );

    if( HourNo != _previoustime.hour() )
    {
        // Reset the new counter to zero in case we've run for an entire 24 hours... hm.
        resetCounter( HourNo );
        computeHourInterval( HourNo, _startStopTimePairs[ HourNo ] );
        _dirty = true;

        _dirtyCounter[ HourNo ] = true;
    }

    if(lastdate.day() != newdate.day())
    {
        // Copy current to previous. Write out a report.
        copyCounter(Yesterday, Daily);
        _startStopTimePairs[ Yesterday ] = _startStopTimePairs[ Daily ];        // Give it yesterday's timestamp.

        // Completely reset the daily counter.
        resetCounter( Daily );
        computeDailyInterval(_startStopTimePairs[Daily]);

        _dirtyCounter[ Yesterday ] = true;
        _dirtyCounter[ Daily ] = true;
        _dirty = true;
    }

    if(lastdate.month() != newdate.month())
    {
        // Copy current to previous.
        copyCounter( LastMonth, Monthly );
        _startStopTimePairs[ LastMonth ] = _startStopTimePairs[ Monthly ];

        // Completely reset the monthly counter.
        resetCounter( Monthly );
        computeMonthInterval(_startStopTimePairs[Monthly]);
        _dirty = true;
        _dirtyCounter[ LastMonth ] = true;
        _dirtyCounter[ Monthly ] = true;
    }

    _previoustime = newtime;

    return HourNo;
}

long CtiStatistics::getID() const
{
    if(_pid < 0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return _pid;
}

CtiStatistics& CtiStatistics::setID(long id)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    _pid = id;
    return *this;
}

CtiStatistics& CtiStatistics::operator=(const CtiStatistics& aRef)
{
    if(this != &aRef)
    {
        // CtiLockGuard<CtiMutex> arefguard(aRef._statMux);
        // CtiLockGuard<CtiMutex> guard(_statMux);
        int i, statType;

        _dirty = true;

        setID(aRef.getID());
        _previoustime = aRef._previoustime;
        _restoreworked = aRef._restoreworked;

        for(i = 0; i < FinalCounterBin; i++)
        {
            for(statType = 0; statType < FinalStatType; statType++)
            {
                setCounter(statType, i, aRef.getCounter(statType, i));
            }
            _threshold[i] = aRef._threshold[i];
            _thresholdAlarm[i] = aRef._thresholdAlarm[i];
            _startStopTimePairs[i] = aRef._startStopTimePairs[i];
            _dirtyCounter[ i ] = aRef._dirtyCounter[i];
        }

        if(_pid < 0)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }
    return *this;
}

bool CtiStatistics::operator<( const CtiStatistics &rhs ) const
{
    return(getID() < rhs.getID());
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
    // CtiLockGuard<CtiMutex> guard(_statMux);
    return getCounter(CommErrors, Counter) + getCounter( ProtocolErrors, Counter) + getCounter(SystemErrors, Counter);
}

double CtiStatistics::getCompletionRatio(int Counter) const
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    double ratio = 1.0;

    if(getCounter(Requests, Counter) > 10)        // No computation until 10 Attempts have been made..
    {
        ratio = (double)getCounter(Completions, Counter) / (double)getCounter(Requests, Counter);
    }
    return ratio;
}

void CtiStatistics::dumpStats() const
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    if(getDebugLevel() & DEBUGLEVEL_STATISTICS)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Statistics Report for PaoID " << _pid << endl;
        dout << "   Daily Requests              " << getCounter(Requests, Daily) << endl;
        dout << "   Daily Attempts              " << getCounter(Attempts, Daily) << endl;
        dout << "   Daily Completions           " << getCounter(Completions, Daily) << endl;

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
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int i;
    double ratio;

    for(i = 0; i < FinalCounterBin; i++)
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

RWCString CtiStatistics::_counterName[FinalCounterBin] = {
    "Hour00",
    "Hour01",
    "Hour02",
    "Hour03",
    "Hour04",
    "Hour05",
    "Hour06",
    "Hour07",
    "Hour08",
    "Hour09",
    "Hour10",
    "Hour11",
    "Hour12",
    "Hour13",
    "Hour14",
    "Hour15",
    "Hour16",
    "Hour17",
    "Hour18",
    "Hour19",
    "Hour20",
    "Hour21",
    "Hour22",
    "Hour23",
    "Daily",
    "Yesterday",
    "Monthly",
    "LastMonth"
    };

double CtiStatistics::getThreshold(int Counter) const
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    return _threshold[Counter];
}

CtiStatistics& CtiStatistics::setThreshold(int Counter, double thresh)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    _threshold[Counter] = thresh;
    return *this;
}

int CtiStatistics::get(int counter, int index ) const
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    return getCounter(index, counter);
}

RWCString CtiStatistics::getTableName()
{
    return RWCString("DynamicPaoStatistics");
}

RWDBStatus::ErrorCode CtiStatistics::Restore()
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    RWCString   typeStr;
    int         counter;
    int         val;
    RWDBDateTime    startdt;
    RWDBDateTime    stopdt;
    RWDBStatus      dbstat;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector << table["paobjectid"]
    << table["statistictype"]
    << table["requests"]
    << table["completions"]
    << table["attempts"]
    << table["commerrors"]
    << table["protocolerrors"]
    << table["systemerrors"]
    << table["startdatetime"]
    << table["stopdatetime"];

    selector.where( table["paobjectid"] == getID() );

    dbstat = selector.execute().status();

    if(dbstat.errorCode() == RWDBStatus::ok)
    {
        RWDBReader rdr = selector.reader( conn );

        while( rdr() && (dbstat.errorCode() == RWDBStatus::ok) )
        {
            rdr["statistictype"] >> typeStr;

            counter = resolveStatisticsType(typeStr);

            if(counter >= 0)
            {
                rdr["requests"]         >> val;
                setCounter( Requests, counter, val);
                rdr["completions"]      >> val;
                setCounter( Completions, counter, val);
                rdr["attempts"]         >> val;
                setCounter( Attempts, counter, val);
                rdr["commerrors"]       >> val;
                setCounter( CommErrors, counter, val);
                rdr["protocolerrors"]   >> val;
                setCounter( ProtocolErrors, counter, val);
                rdr["systemerrors"]     >> val;
                setCounter( SystemErrors, counter, val);

                rdr["startdatetime"]    >> startdt;
                rdr["stopdatetime"]     >> stopdt;

                _startStopTimePairs[counter] = make_pair( startdt.rwtime(), stopdt.rwtime() );
            }

            dbstat = rdr.status();

            _restoreworked++;
            _dirty = false;
        }
    }

    if( _restoreworked < FinalCounterBin )
    {
        Insert(conn);
    }

    if(dbstat.errorCode() == RWDBStatus::endOfFetch)
    {
        dbstat.changeError(RWDBStatus::ok, false, "No error", "");
    }

    return dbstat.errorCode();
}


RWDBStatus::ErrorCode  CtiStatistics::Insert(RWDBConnection &conn)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBStatus stat;
    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter ins = table.inserter();

    for(int i = 0; i < FinalCounterBin; i++)
    {
        _dirtyCounter[i] = false;

        ins <<
            getID() <<
            getCounterName( i ) <<
            getCounter( Requests, i ) <<
            getCounter( Completions, i ) <<
            getCounter( Attempts, i ) <<
            getCounter( CommErrors, i ) <<
            getCounter( ProtocolErrors, i ) <<
            getCounter( SystemErrors, i ) <<
            RWDBDateTime(_startStopTimePairs[i].first) <<
            RWDBDateTime(_startStopTimePairs[i].second);

        stat = ins.execute(conn).status();

        if( stat.errorCode() != RWDBStatus::ok )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Statistics Insert Error Code = " << stat.errorCode() << endl;
            dout << ins.asString() << endl;
        }
    }

    _dirty = false;

    return stat.errorCode();
}

RWDBStatus::ErrorCode  CtiStatistics::Insert()
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    return Insert(conn);
}

RWDBStatus::ErrorCode  CtiStatistics::Update(RWDBConnection &conn)
{
    RWDBStatus stat;
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    stat = updater.status();

    for(int i = 0; i < FinalCounterBin && stat.errorCode() == RWDBStatus::ok; i++)
    {
        if(_dirtyCounter[i])
        {
            _dirtyCounter[i] = false;

            updater <<
            table["requests"].assign(getCounter( Requests, i )) <<
            table["completions"].assign(getCounter( Completions, i )) <<
            table["attempts"].assign(getCounter( Attempts, i )) <<
            table["commerrors"].assign(getCounter( CommErrors, i )) <<
            table["protocolerrors"].assign(getCounter( ProtocolErrors, i )) <<
            table["systemerrors"].assign(getCounter( SystemErrors, i )) <<
            table["startdatetime"].assign(RWDBDateTime(_startStopTimePairs[i].first)) <<
            table["stopdatetime"].assign(RWDBDateTime(_startStopTimePairs[i].second));

            updater.where( table["paobjectid"] == getID() && table["statistictype"] == getCounterName( i ));

            stat = updater.execute(conn).status();

            if( stat.errorCode() != RWDBStatus::ok )
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "Statistics Updater Error Code = " << stat.errorCode() << endl;
                dout << updater.asString() << endl;
            }
        }
    }

    if( stat.errorCode() == RWDBStatus::ok )
    {
        _dirty = false;
    }

    return stat.errorCode();
}

RWDBStatus::ErrorCode  CtiStatistics::Update()
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    return Update(conn);
}

int CtiStatistics::resolveStatisticsType(RWCString rwsTemp) const
{
    int stattype;

    rwsTemp.toLower();
    rwsTemp = rwsTemp.strip(RWCString::both);

    if(rwsTemp == "monthly")
    {
        stattype = Monthly;
    }
    else if(rwsTemp == "lastmonth")
    {
        stattype = LastMonth;
    }
    else if(rwsTemp == "daily")
    {
        stattype = Daily;
    }
    else if(rwsTemp == "yesterday")
    {
        stattype = Yesterday;
    }
    else if(rwsTemp == "hour00")
    {
        stattype = Hour0;
    }
    else if(rwsTemp == "hour01")
    {
        stattype = Hour1;
    }
    else if(rwsTemp == "hour02")
    {
        stattype = Hour2;
    }
    else if(rwsTemp == "hour03")
    {
        stattype = Hour3;
    }
    else if(rwsTemp == "hour04")
    {
        stattype = Hour4;
    }
    else if(rwsTemp == "hour05")
    {
        stattype = Hour5;
    }
    else if(rwsTemp == "hour06")
    {
        stattype = Hour6;
    }
    else if(rwsTemp == "hour07")
    {
        stattype = Hour7;
    }
    else if(rwsTemp == "hour08")
    {
        stattype = Hour8;
    }
    else if(rwsTemp == "hour09")
    {
        stattype = Hour9;
    }
    else if(rwsTemp == "hour10")
    {
        stattype = Hour10;
    }
    else if(rwsTemp == "hour11")
    {
        stattype = Hour11;
    }
    else if(rwsTemp == "hour12")
    {
        stattype = Hour12;
    }
    else if(rwsTemp == "hour13")
    {
        stattype = Hour13;
    }
    else if(rwsTemp == "hour14")
    {
        stattype = Hour14;
    }
    else if(rwsTemp == "hour15")
    {
        stattype = Hour15;
    }
    else if(rwsTemp == "hour16")
    {
        stattype = Hour16;
    }
    else if(rwsTemp == "hour17")
    {
        stattype = Hour17;
    }
    else if(rwsTemp == "hour18")
    {
        stattype = Hour18;
    }
    else if(rwsTemp == "hour19")
    {
        stattype = Hour19;
    }
    else if(rwsTemp == "hour20")
    {
        stattype = Hour20;
    }
    else if(rwsTemp == "hour21")
    {
        stattype = Hour21;
    }
    else if(rwsTemp == "hour22")
    {
        stattype = Hour22;
    }
    else if(rwsTemp == "hour23")
    {
        stattype = Hour23;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported statistics collection type \"" << rwsTemp << "\" " << endl;
        stattype = -1;
    }

    return stattype;
}

void CtiStatistics::computeHourInterval(int hournumber, pair<RWTime, RWTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    RWTime startdt(RWDate());
    startdt = startdt + (hournumber * 3600);
    RWTime stopdt(startdt + 3600);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeDailyInterval(pair<RWTime, RWTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    RWDate todayDate;
    RWDate tomorrow = todayDate;

    tomorrow++;

    RWTime startdt(todayDate);
    RWTime stopdt(tomorrow);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeYesterdayInterval(pair<RWTime, RWTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    RWDate todayDate;
    RWDate yesterday(todayDate);

    yesterday--;

    RWTime startdt(yesterday);
    RWTime stopdt(todayDate);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeMonthInterval(pair<RWTime, RWTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    RWDate todayDate;
    unsigned month = todayDate.month();
    unsigned year  = todayDate.year();
    RWDate firstOfMonth(1, month, year);

    if(++month > 12)
    {
        month = 1;
        year++;
    }

    RWDate nextMonth(1, month, year);

    RWTime startdt(firstOfMonth);
    RWTime stopdt(nextMonth);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeLastMonthInterval(pair<RWTime, RWTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    RWDate todayDate;
    unsigned month = todayDate.month();
    unsigned year  = todayDate.year();
    RWDate firstOfMonth(1, month, year);

    if(--month == 0)
    {
        month = 12;
        year--;
    }

    RWDate lastMonth(1, month, year);

    RWTime startdt(lastMonth);
    RWTime stopdt(firstOfMonth);

    myinterval = make_pair(startdt, stopdt);
}

bool CtiStatistics::isDirty() const
{
    return _dirty;
}

CtiStatistics& CtiStatistics::resetDirty()
{
    _dirty = false;
    return *this;
}

