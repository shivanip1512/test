/*-----------------------------------------------------------------------------*
*
* File:   statistics
*
* Date:   5/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.30.2.3 $
* DATE         :  $Date: 2008/11/14 20:08:51 $
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

CtiStatistics::CtiStatistics(long id) :
    _dirty(false),
    _paoid(id),
    _doHistInsert(false)
{
    if(_paoid < 0)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    fill(&_counter[0][0], &_counter[0][0] + FinalStatType * FinalCounterBin, 0);

    computeHourInterval(CtiTime().hour(), _intervalBounds[ CurrentHour ]);

    computeDailyInterval(_intervalBounds[ Daily ]);
    computeMonthInterval(_intervalBounds[ Monthly ]);
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
    //  this should all be handled by Porter's shutdown code - individual objects shouldn't be accessing the DB
    /*
    if(_dirty)
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        Record(conn);

        if(_doHistInsert)
        {
            InsertDaily(conn);
        }
    }
    */
}


void CtiStatistics::incrementRequest(const CtiTime &stattime)
{
    rotateCounters(stattime);
    _dirty = true;
    incrementCounter( Requests, CurrentHour);
    incrementCounter( Requests, Daily);
    incrementCounter( Requests, Monthly);
    incrementCounter( Requests, Lifetime);
}

void CtiStatistics::incrementAttempts(const CtiTime &stattime, int CompletionStatus)        // This is a retry scenario
{
    rotateCounters(stattime);
    _dirty = true;

    incrementCounter( Attempts, CurrentHour);
    incrementCounter( Attempts, Daily);
    incrementCounter( Attempts, Monthly);
    incrementCounter( Attempts, Lifetime);

    if(CompletionStatus != NORMAL)
    {
        CtiStatisticsCounters_t failtype = resolveFailType(CompletionStatus);

        incrementCounter( failtype, CurrentHour);
        incrementCounter( failtype, Daily);
        incrementCounter( failtype, Monthly);
        incrementCounter( failtype, Lifetime);
    }
}

void CtiStatistics::incrementCompletion(const CtiTime &stattime, int CompletionStatus)
{
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
            dout << CtiTime() << " Statistics results have been excluded for ID " << _paoid << endl;
        }
    }
    else
    {
        incrementAttempts(stattime, CompletionStatus);      // This may also increment the fail counter if CompletionStatus != NORMAL

        if(CompletionStatus == NORMAL)
        {
            rotateCounters(stattime);

            _dirty = true;
            incrementCounter( Completions, CurrentHour);
            incrementCounter( Completions, Daily);
            incrementCounter( Completions, Monthly);
            incrementCounter( Completions, Lifetime);

            if(getDebugLevel() & DEBUGLEVEL_STATISTICS && getCounter( Requests, Daily ) < getCounter( Completions, Daily ))
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") id " << getID() << " has a statistics anomoly" << endl;
                }
            }
        }
    }
}

bool CtiStatistics::isCommFail( int CompletionStatus )
{
    return resolveFailType(CompletionStatus) == CommErrors;
}

CtiStatistics::CtiStatisticsCounters_t CtiStatistics::resolveFailType( int CompletionStatus )
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

void CtiStatistics::rotateCounters(const CtiTime &newtime)
{
    CtiDate newdate( newtime );

    if( _intervalBounds[CurrentHour].first.hour() != newtime.hour()
        || _intervalBounds[CurrentHour].second < newtime )
    {
        copyCounter(PreviousHour, CurrentHour);
        _intervalBounds[ PreviousHour ] = _intervalBounds[ CurrentHour ];

        _dirtyCounter[ PreviousHour ] = _dirtyCounter[ CurrentHour ];
        _rowExists   [ PreviousHour ] = _rowExists   [ CurrentHour ];

        // Reset the new counter to zero in case we've run for an entire 24 hours... hm.
        resetCounter( CurrentHour );
        computeHourInterval( newtime.hour(), _intervalBounds[ CurrentHour ] );

        _dirtyCounter[ CurrentHour ] = false;
        _rowExists   [ CurrentHour ] = _hourRowExists[ newtime.hour() ];

        _dirty = true;
    }

    CtiDate currentDayCounter(_intervalBounds[Daily].first);
    if(currentDayCounter.day() != newdate.day())
    {
        // Copy current to previous. Write out a report.
        copyCounter(Yesterday, Daily);
        _intervalBounds[ Yesterday ] = _intervalBounds[ Daily ];        // Give it yesterday's timestamp.

        _dirtyCounter[ Yesterday ] = _dirtyCounter[ Daily ];

        // Completely reset the daily counter.
        resetCounter( Daily );
        computeDailyInterval(_intervalBounds[Daily]);

        _dirtyCounter[ Daily ] = false;

        _dirty = true;
        _doHistInsert = true;
    }

    CtiDate currentMonthCounter(_intervalBounds[Monthly].first);
    if(currentMonthCounter.month() != newdate.month())
    {
        // Copy current to previous.
        copyCounter( LastMonth, Monthly );
        _intervalBounds[ LastMonth ] = _intervalBounds[ Monthly ];

        _dirtyCounter[ LastMonth ] = _dirtyCounter[ Monthly ];
        _rowExists   [ LastMonth ] = _rowExists   [ Monthly ];

        // Completely reset the monthly counter.
        resetCounter( Monthly );
        computeMonthInterval(_intervalBounds[Monthly]);

        _dirtyCounter[ Monthly ] = false;

        _dirty = true;
    }
}

long CtiStatistics::getID() const
{
    if(_paoid < 0)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return _paoid;
}

CtiStatistics& CtiStatistics::operator=(const CtiStatistics& aRef)
{
    if(this != &aRef)
    {
        int i, statType;

        _dirty = true;

        _paoid         = aRef._paoid;
        _doHistInsert  = aRef._doHistInsert;
        _dirtyCounter  = aRef._dirtyCounter;
        _rowExists     = aRef._rowExists;
        _hourRowExists = aRef._hourRowExists;

        for(i = 0; i < FinalCounterBin; i++)
        {
            for(statType = 0; statType < FinalStatType; statType++)
            {
                setInitialCounterVal(statType, i, aRef.getCounter(statType, i));
            }
            _intervalBounds[i] = aRef._intervalBounds[i];
        }

        if(_paoid < 0)
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

string getHourName(const CtiTime &tm)
{
    return "Hour" + CtiNumStr(tm.hour()).zpad(2);
}

string CtiStatistics::getCounterName( int Counter, const CtiTime &tm )
{
    switch( Counter )
    {
        case CurrentHour:  return getHourName(tm);
        case PreviousHour: return getHourName(tm);
        case Daily:        return "Daily";
        case Yesterday:    return "Yesterday";
        case Monthly:      return "Monthly";
        case LastMonth:    return "LastMonth";
        case Lifetime:     return "Lifetime";
        default:           return "";
    }
}

string CtiStatistics::getTableName()
{
    return string("DynamicPaoStatistics");
}

string CtiStatistics::getTableNameDynamicHistory()
{
    return string("DynamicPAOStatisticsHistory");
}

void CtiStatistics::getSQL(RWDBDatabase &db, RWDBTable &table, RWDBSelector &selector, vector<long>::iterator id_begin, vector<long>::iterator id_end)
{
    table = db.table(getTableName().c_str());

    selector
        << table["paobjectid"]
        << table["statistictype"]
        << table["requests"]
        << table["completions"]
        << table["attempts"]
        << table["commerrors"]
        << table["protocolerrors"]
        << table["systemerrors"]
        << table["startdatetime"];

    CtiTime now;

    ostringstream in_list;

    in_list << "(";

    copy(id_begin, id_end, csv_output_iterator<long, ostringstream>(in_list));

    in_list << ")";

    selector.where(table["paobjectid"].in(RWDBExpr(in_list.str().c_str(), false)));
    selector.orderBy(table["paobjectid"]);
}

void CtiStatistics::Factory(RWDBReader &rdr, vector<CtiStatistics *> &restored)
{
    string      typeStr;
    int         counter;
    int         val;
    CtiTime     startdt;
    RWDBStatus  dbstat(RWDBStatus::ok);
    long        current_id = -1;
    bool        first_row = true;
    CtiStatistics *stat = 0;

    string hourname = getHourName(CtiTime::now());

    while( rdr() && (dbstat.errorCode() == RWDBStatus::ok) )
    {
        long new_id;

        rdr["paobjectid"] >> new_id;

        if( first_row || new_id != current_id )
        {
            if( stat )
            {
                restored.push_back(stat);
            }

            current_id = new_id;
            stat = new CtiStatistics(current_id);
            first_row = false;
        }

        if( stat )
        {
            rdr["statistictype"] >> typeStr;
            rdr["startdatetime"] >> startdt;

            int counter = -1;

            if( !typeStr.empty() && typeStr[0] == 'H' )
            {
                if( typeStr == hourname )
                {
                    counter = CurrentHour;
                }

                stat->_hourRowExists.set(atoi(typeStr.substr(4).c_str()));
            }
            else if( typeStr == "Daily" )
            {
                counter = Daily;
            }
            else if( typeStr == "Monthly" )
            {
                counter = Monthly;
            }
            else if( typeStr == "Lifetime" )
            {
                counter = Lifetime;
            }

            if( counter >= 0 )
            {
                // This ensures we only load the values if they are current. Old values are not loaded.
                if( startdt == stat->_intervalBounds[counter].first || counter == Lifetime )
                {
                    rdr["requests"]         >> val;  stat->setInitialCounterVal( Requests,       counter, val);
                    rdr["completions"]      >> val;  stat->setInitialCounterVal( Completions,    counter, val);
                    rdr["attempts"]         >> val;  stat->setInitialCounterVal( Attempts,       counter, val);
                    rdr["commerrors"]       >> val;  stat->setInitialCounterVal( CommErrors,     counter, val);
                    rdr["protocolerrors"]   >> val;  stat->setInitialCounterVal( ProtocolErrors, counter, val);
                    rdr["systemerrors"]     >> val;  stat->setInitialCounterVal( SystemErrors,   counter, val);
                }

                stat->_rowExists[counter] = true;
            }
        }

        dbstat = rdr.status();
    }

    if( stat )
    {
        restored.push_back(stat);
    }
}


RWDBStatus::ErrorCode  CtiStatistics::Record(RWDBConnection &conn)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBStatus::ErrorCode ec = RWDBStatus::ok;

    for(int i = 0; i < FinalCounterBin && ec == RWDBStatus::ok; i++)
    {
        if( _dirtyCounter.test(i) )
        {
            if( _rowExists.test(i) )
            {
                if( Update(conn, i) == RWDBStatus::ok )
                {
                    continue;
                }
            }

            ec = Insert(conn, i);
        }
    }

    if( ec == RWDBStatus::ok )
    {
        _dirty = false;
    }

    return ec;
}


RWDBStatus::ErrorCode  CtiStatistics::Insert(RWDBConnection &conn, int counter)
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBStatus stat;
    RWDBTable table = getDatabase().table(getTableName().c_str());
    RWDBInserter ins = table.inserter();

    if( counter < FinalCounterBin )
    {
        ins <<
            getID() <<
            getCounterName( counter, _intervalBounds[counter].first ) <<
            getCounter( Requests, counter ) <<
            getCounter( Completions, counter ) <<
            getCounter( Attempts, counter ) <<
            getCounter( CommErrors, counter ) <<
            getCounter( ProtocolErrors, counter ) <<
            getCounter( SystemErrors, counter ) <<
            _intervalBounds[counter].first <<
            _intervalBounds[counter].second;

        stat = ExecuteInserter(conn,ins,__FILE__,__LINE__);

        if( stat.errorCode() != RWDBStatus::ok )
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Statistics Insert Error Code = " << stat.errorCode() << endl;
            dout << ins.asString() << endl;
        }
    }

    return stat.errorCode();
}

RWDBStatus::ErrorCode  CtiStatistics::Update(RWDBConnection &conn, int counter)
{
    RWDBStatus::ErrorCode ec = RWDBStatus::ok;
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

    RWDBTable table = getDatabase().table(getTableName().c_str());
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

    _intervalBounds[Lifetime].second.resetToNow();

    if( counter < FinalCounterBin )
    {
        updater <<
            col_requests   .assign(getCounter(Requests,    counter)) <<
            col_completions.assign(getCounter(Completions, counter)) <<
            col_attempts   .assign(getCounter(Attempts,    counter)) <<
            col_commerrors    .assign(getCounter(CommErrors,     counter)) <<
            col_protocolerrors.assign(getCounter(ProtocolErrors, counter)) <<
            col_systemerrors  .assign(getCounter(SystemErrors,   counter)) <<
            col_startdatetime .assign(toRWDBDT(_intervalBounds[counter].first)) <<
            col_stopdatetime  .assign(toRWDBDT(_intervalBounds[counter].second));

        updater.where(col_paobjectid == getID() && col_statistictype == getCounterName(counter, _intervalBounds[counter].first).c_str());

        ec = ExecuteUpdater(conn,updater,__FILE__,__LINE__);

        if( ec == RWDBStatus::ok )
        {
            _dirtyCounter[counter] = false;
        }
    }

    return ec;
}

RWDBStatus::ErrorCode  CtiStatistics::InsertDaily(RWDBConnection &conn)
{
    if( !_doHistInsert )
    {
        return RWDBStatus::ok;
    }

    _doHistInsert = false;

    RWDBStatus stat;

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

        RWDBTable insertTable = getDatabase().table(getTableNameDynamicHistory().c_str());
        RWDBInserter inserter = insertTable.inserter();
        inserter <<
            getID() <<
            _intervalBounds[Yesterday].first.date().daysFrom1970() <<
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
    int numDays = gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS", 120, 10) + 1; //No magic here, I just like +1

    if( numDays <= 0 )
    {
        return RWDBStatus::ok;
    }

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);

        CtiDate today;
        RWDBTable deleteTable = getDatabase().table(getTableNameDynamicHistory().c_str());
        RWDBDeleter deleter = deleteTable.deleter();

        deleter.where( deleteTable["dateoffset"] < (today.daysFrom1970() - numDays) );

        return deleter.execute().status().errorCode();
    }
}

void CtiStatistics::computeHourInterval(int hournumber, pair<CtiTime, CtiTime> &myinterval)
{
    CtiTime startdt = CtiTime(CtiDate());
    startdt = startdt + (hournumber * 3600);
    CtiTime stopdt(startdt + 3600);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeDailyInterval(pair<CtiTime, CtiTime> &myinterval)
{
    CtiDate todayDate;
    CtiDate tomorrow = todayDate;

    ++tomorrow;

    CtiTime startdt(todayDate);
    CtiTime stopdt(tomorrow);

    myinterval = make_pair(startdt, stopdt);
}

void CtiStatistics::computeMonthInterval(pair<CtiTime, CtiTime> &myinterval)
{
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

bool CtiStatistics::isDirty() const
{
    return _dirty;
}

// Clears all dirty flags and sets all dirty intervals as being written
CtiStatistics& CtiStatistics::resetDirty()
{
    _dirty = false;
    _doHistInsert = false;

    for(int counter = 0; counter < FinalCounterBin; counter++)
    {
        if( _dirtyCounter.test(counter) )
        {
            _dirtyCounter[counter] = false;
            _rowExists[counter] = true;
            if( counter == CurrentHour || counter == PreviousHour)
            {
                _hourRowExists[ _intervalBounds[counter].first.hour() ] = true;
            }
        }
    }

    return *this;
}
