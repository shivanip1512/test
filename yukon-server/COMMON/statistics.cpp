/*-----------------------------------------------------------------------------*
*
* File:   statistics
*
* Date:   5/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.30 $
* DATE         :  $Date: 2008/08/14 18:26:11 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <utility>

using namespace std;

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

#include "rwutil.h"
#include "utility.h"
#include "ctidate.h"
#include "cparms.h"

CtiDate CtiStatistics::_lastPrune = _lastPrune.now();

CtiStatistics::CtiStatistics(long id) :
    _restoreworked(0),
    _dirty(false),
    _pid(id),
    _doHistInsert(false)
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
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }


    CtiTime now;

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


void CtiStatistics::incrementRequest(const CtiTime &stattime)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Requests);
    _dirty = true;
    incrementCounter( Requests, HourNo, 1);
    incrementCounter( Requests, Daily, 1);
    incrementCounter( Requests, Monthly, 1);
    incrementCounter( Requests, Lifetime, 1);
}

void CtiStatistics::decrementRequest(const CtiTime &stattime)        // This is a retry scenario
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Attempts);
    _dirty = true;
    incrementCounter( Requests, HourNo, -1);
    incrementCounter( Requests, Daily, -1);
    incrementCounter( Requests, Monthly, -1);
    incrementCounter( Requests, Lifetime, -1);
}

void CtiStatistics::incrementAttempts(const CtiTime &stattime, int CompletionStatus)        // This is a retry scenario
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Attempts);
    _dirty = true;

    incrementCounter( Attempts, HourNo, 1);
    incrementCounter( Attempts, Daily, 1);
    incrementCounter( Attempts, Monthly, 1);
    incrementCounter( Attempts, Lifetime, 1);

    if(CompletionStatus != NORMAL)
    {
        incrementFail(stattime, resolveFailType(CompletionStatus));
    }
}

void CtiStatistics::incrementCompletion(const CtiTime &stattime, int CompletionStatus)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);

    if( getCounter(Completions, Daily) >= getCounter(Requests, Daily)
        && CompletionStatus == NORMAL
        && !(gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_COMPENSATED_RESULTS) )
    {
        //This is a failure mode preventative, and should not be used lightly...
        //If the flag is set, we do not count successes (or the attempts associated)
        //when that success would take our SUCCESS count above the request count (attempts is free to roam)
        if( gConfigParms.getValueAsULong("STATISTICS_DEBUGLEVEL", 0, 16) & STATISTICS_REPORT_ON_RESULTS )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Statistics results have been excluded for ID " << _pid << endl;
        }
    }
    else
    {
        incrementAttempts(stattime, CompletionStatus);      // This may also increment the fail counter if CompletionStatus != NORMAL

        if(CompletionStatus == NORMAL)
        {
            incrementSuccess(stattime);
        }

        verifyThresholds();
    }
}

void CtiStatistics::incrementFail(const CtiTime &stattime, CtiStatisticsCounters_t failtype)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, failtype);

    _dirty = true;
    incrementCounter( failtype, HourNo, 1);
    incrementCounter( failtype, Daily, 1);
    incrementCounter( failtype, Monthly, 1);
    incrementCounter( failtype, Lifetime, 1);
}

void CtiStatistics::incrementSuccess(const CtiTime &stattime)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    int HourNo = newHour(stattime, Completions);

    _dirty = true;
    incrementCounter( Completions, HourNo, 1);
    incrementCounter( Completions, Daily, 1);
    incrementCounter( Completions, Monthly, 1);
    incrementCounter( Completions, Lifetime, 1);

    if(getDebugLevel() & DEBUGLEVEL_STATISTICS && getCounter( Requests, Daily ) < getCounter( Completions, Daily ))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") id " << getID() << " has a statistics anomoly" << endl;
        }
    }
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

int CtiStatistics::newHour(const CtiTime &newtime, CtiStatisticsCounters_t countertoclean)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    static unsigned dumpit = 1;
    unsigned HourNo = newtime.hour();

    CtiDate lastdate( _previoustime );
    CtiDate newdate( newtime );

    if( HourNo != _previoustime.hour()
        || _startStopTimePairs[HourNo].second < newtime )
    {
        // Reset the new counter to zero in case we've run for an entire 24 hours... hm.
        resetCounter( HourNo );
        computeHourInterval( HourNo, _startStopTimePairs[ HourNo ] );
        _dirty = true;

        _dirtyCounter[ HourNo ] = true;
    }

    CtiDate currentDayCounter(_startStopTimePairs[Daily].first);
    if(currentDayCounter.day() != newdate.day())
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
        _doHistInsert = true;
    }

    CtiDate currentMonthCounter(_startStopTimePairs[Monthly].first);
    if(currentMonthCounter.month() != newdate.month())
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        _doHistInsert = aRef._doHistInsert;

        for(i = 0; i < FinalCounterBin; i++)
        {
            for(statType = 0; statType < FinalStatType; statType++)
            {
                setInitialCounterVal(statType, i, aRef.getCounter(statType, i));
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
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << CtiTime() << " Statistics Report for PaoID " << _pid << endl;
        dout << "   Daily Requests              " << getCounter(Requests, Daily) << endl;
        dout << "   Daily Attempts              " << getCounter(Attempts, Daily) << endl;
        dout << "   Daily Completions           " << getCounter(Completions, Daily) << endl;

        int starthour = (CtiTime().hour() + 1) % 24;

        for( ; starthour != CtiTime().hour() ; starthour = (starthour + 1) % 24)
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
                dout << CtiTime() << " Threshold " << getCounterName(i) << " has been crossed" << endl;
            }

            _thresholdAlarm[i] = true;
        }
        else
        {
            _thresholdAlarm[i] = false;
        }
    }

}

string CtiStatistics::_counterName[FinalCounterBin] = {
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
    "LastMonth",
    "Lifetime"
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

string CtiStatistics::getTableName()
{
    return string("DynamicPaoStatistics");
}

string CtiStatistics::getTableNameDynamicHistory()
{
    return string("DynamicPAOStatisticsHistory");
}

RWDBStatus::ErrorCode CtiStatistics::Restore()
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    string   typeStr;
    int         counter;
    int         val;
    CtiTime    startdt;
    CtiTime    stopdt;
    RWDBStatus      dbstat;

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName().c_str() );
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
                setInitialCounterVal( Requests, counter, val);
                rdr["completions"]      >> val;
                setInitialCounterVal( Completions, counter, val);
                rdr["attempts"]         >> val;
                setInitialCounterVal( Attempts, counter, val);
                rdr["commerrors"]       >> val;
                setInitialCounterVal( CommErrors, counter, val);
                rdr["protocolerrors"]   >> val;
                setInitialCounterVal( ProtocolErrors, counter, val);
                rdr["systemerrors"]     >> val;
                setInitialCounterVal( SystemErrors, counter, val);

                rdr["startdatetime"]    >> startdt;
                rdr["stopdatetime"]     >> stopdt;

                _startStopTimePairs[counter] = make_pair( startdt, stopdt );
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
    RWDBTable table = getDatabase().table( string2RWCString(getTableName()) );
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
            CtiTime(_startStopTimePairs[i].first) <<
            CtiTime(_startStopTimePairs[i].second);

        stat = ExecuteInserter(conn,ins,__FILE__,__LINE__);

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
    RWDBStatus::ErrorCode ec = RWDBStatus::ok;
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBTable table = getDatabase().table( string2RWCString(getTableName()) );
    RWDBUpdater updater = table.updater();

    RWDBColumn col_paobjectid     = table["paobjectid"],
               col_statistictype  = table["statistictype"],
               col_requests       = table["requests"],
               col_completions    = table["completions"],
               col_attempts       = table["attempts"],
               col_commerrors     = table["commerrors"],
               col_protocolerrors = table["protocolerrors"],
               col_systemerrors   = table["systemerrors"],
               col_startdatetime  = table["startdatetime"],
               col_stopdatetime   = table["stopdatetime"];

    _startStopTimePairs[Lifetime].second.resetToNow();

    for(int i = 0; i < FinalCounterBin && ec == RWDBStatus::ok; i++)
    {
        if(_dirtyCounter[i])
        {
            _dirtyCounter[i] = false;

            updater <<
                col_requests   .assign(getCounter(Requests,    i)) <<
                col_completions.assign(getCounter(Completions, i)) <<
                col_attempts   .assign(getCounter(Attempts,    i)) <<
                col_commerrors    .assign(getCounter(CommErrors,     i)) <<
                col_protocolerrors.assign(getCounter(ProtocolErrors, i)) <<
                col_systemerrors  .assign(getCounter(SystemErrors,   i)) <<
                col_startdatetime .assign(toRWDBDT(_startStopTimePairs[i].first)) <<
                col_stopdatetime  .assign(toRWDBDT(_startStopTimePairs[i].second));

            updater.where(col_paobjectid == getID() && col_statistictype == string2RWCString(getCounterName(i)));

            ec = ExecuteUpdater(conn,updater,__FILE__,__LINE__);
        }
    }

    if( ec == RWDBStatus::ok )
    {
        _dirty = false;
    }

    return ec;
}

RWDBStatus::ErrorCode  CtiStatistics::Update()
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    return Update(conn);
}

RWDBStatus::ErrorCode  CtiStatistics::InsertDaily(RWDBConnection &conn)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBStatus stat;

    if( _doHistInsert )
    {
        _doHistInsert = false;
        RWDBTable insertTable = getDatabase().table( string2RWCString(getTableNameDynamicHistory()) );
        RWDBInserter inserter = insertTable.inserter();
        inserter << getID() << _startStopTimePairs[Yesterday].first.date().daysFrom1970() <<
            getCounter( Requests, Yesterday ) <<
            getCounter( Completions, Yesterday ) <<
            getCounter( Attempts, Yesterday ) <<
            getCounter( CommErrors, Yesterday ) <<
            getCounter( ProtocolErrors, Yesterday ) <<
            getCounter( SystemErrors, Yesterday );

        stat = ExecuteInserter(conn, inserter, __FILE__, __LINE__);

        if( stat.errorCode() != RWDBStatus::ok )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Statistics Insert Error Code = " << stat.errorCode() << endl;
            dout << inserter.asString() << endl;
        }
    }

    return stat.errorCode();
}

RWDBStatus::ErrorCode  CtiStatistics::PruneDaily(RWDBConnection &conn)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBStatus stat;

    if( _lastPrune < _lastPrune.now() )
    {
        CtiDate today;
        RWDBTable deleteTable = getDatabase().table( string2RWCString(getTableNameDynamicHistory()) );
        RWDBDeleter deleter = deleteTable.deleter();
        int numDays = gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS", 120, 10) + 1; //No magic here, I just like +1

        _lastPrune = _lastPrune.now();
        deleter.where( deleteTable["dateoffset"] < (today.daysFrom1970() - numDays) );

        if( numDays > 0 )
        {
            deleter.execute();
        }
    }

    return stat.errorCode();
}

int CtiStatistics::resolveStatisticsType(const string& _rwsTemp) const
{
    int stattype;
    string rwsTemp = _rwsTemp;
    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);

    rwsTemp = trim(rwsTemp);

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
    else if(rwsTemp == "lifetime")
    {
        stattype = Lifetime;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported statistics collection type \"" << rwsTemp << "\" " << endl;
        stattype = -1;
    }

    return stattype;
}

void CtiStatistics::computeHourInterval(int hournumber, pair<CtiTime, CtiTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiTime startdt(CtiDate());
    startdt = startdt + (hournumber * 3600);
    CtiTime stopdt(startdt + 3600);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeDailyInterval(pair<CtiTime, CtiTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiDate todayDate;
    CtiDate tomorrow = todayDate;

    ++tomorrow;

    CtiTime startdt(todayDate);
    CtiTime stopdt(tomorrow);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeYesterdayInterval(pair<CtiTime, CtiTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiDate todayDate;
    CtiDate yesterday(todayDate);

    ++yesterday;

    CtiTime startdt(yesterday);
    CtiTime stopdt(todayDate);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeMonthInterval(pair<CtiTime, CtiTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiDate todayDate;
    unsigned month = todayDate.month();
    unsigned year  = todayDate.year();
    CtiDate firstOfMonth(1, month, year);

    if(++month > 12)
    {
        month = 1;
        year++;
    }

    CtiDate nextMonth(1, month, year);

    CtiTime startdt(firstOfMonth);
    CtiTime stopdt(nextMonth);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeLastMonthInterval(pair<CtiTime, CtiTime> &myinterval)
{
    // CtiLockGuard<CtiMutex> guard(_statMux);
    CtiDate todayDate;
    unsigned month = todayDate.month();
    unsigned year  = todayDate.year();
    CtiDate firstOfMonth(1, month, year);

    if(--month == 0)
    {
        month = 12;
        year--;
    }

    CtiDate lastMonth(1, month, year);

    CtiTime startdt(lastMonth);
    CtiTime stopdt(firstOfMonth);

    myinterval = make_pair(startdt, stopdt);
}

bool CtiStatistics::isDirty() const
{
    return _dirty;
}

CtiStatistics& CtiStatistics::resetDirty()
{
    _dirty = false;
    _doHistInsert = false;

    for(int i = 0; i < FinalCounterBin; i++)
    {
        _dirtyCounter[ i ] = false;
    }
    return *this;
}

