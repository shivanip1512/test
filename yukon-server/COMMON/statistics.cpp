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

#include "row_reader.h"

#include "dbaccess.h"
#include "dsm2.h"
#include "dsm2err.h"
#include "logger.h"
#include "statistics.h"

#include "utility.h"
#include "ctidate.h"
#include "cparms.h"
#include "database_writer.h"

using namespace Cti::Database;

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
}

void CtiStatistics::updateTime(const CtiTime &now_time)
{
    rotateCounters(now_time);
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

        // If midnight, we dont need to set this. If its a new value, that incoming value will set this to true.
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
        _dirtyCounter  [ Yesterday ] = hasNonZeroValue(Yesterday);      // Write out yesterday only if non zero

        // Completely reset the daily counter.
        resetCounter( Daily );
        computeDailyInterval(_intervalBounds[Daily]);

        _dirtyCounter[ Daily ] = true;

        _dirty = true;
        _doHistInsert = true;
    }

    CtiDate currentMonthCounter(_intervalBounds[Monthly].first);
    if(currentMonthCounter.month() != newdate.month())
    {
        // Copy current to previous.
        copyCounter( LastMonth, Monthly );
        _intervalBounds[ LastMonth ] = _intervalBounds[ Monthly ];
        _dirtyCounter  [ LastMonth ] = hasNonZeroValue(LastMonth);    // Write out lastmonth only if non zero

        // Completely reset the monthly counter.
        resetCounter( Monthly );
        computeMonthInterval(_intervalBounds[Monthly]);

        _dirtyCounter[ Monthly ] = true;

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

string getHourName(const CtiTime &t)
{
    return "Hour" + CtiNumStr(t.hour()).zpad(2);
}

string CtiStatistics::getCounterName( int Counter, const CtiTime &t )
{
    switch( Counter )
    {
        case CurrentHour:  return getHourName(t);
        case PreviousHour: return getHourName(t);
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

string CtiStatistics::getSQLCoreStatement(std::vector<long>::iterator id_begin, std::vector<long>::iterator id_end)
{
    string sql =  "SELECT DPS.paobjectid, DPS.statistictype, DPS.requests, DPS.completions, DPS.attempts, "
                  "DPS.commerrors, DPS.protocolerrors, DPS.systemerrors, DPS.startdatetime "
                  "FROM DynamicPaoStatistics DPS WHERE DPS.paobjectid IN ";

    const string orderBy = " ORDER BY DPS.paobjectid ASC";

    ostringstream in_list;

    in_list << "(";

    copy(id_begin, id_end, csv_output_iterator<long, ostringstream>(in_list));

    in_list << ")";

    sql += in_list.str();
    sql += orderBy;

    return sql;
}

void CtiStatistics::Factory(Cti::RowReader &rdr, vector<CtiStatistics *> &restored)
{
    string      typeStr;
    int         val;
    CtiTime     startdt;
    long        current_id = -1;
    bool        first_row = true;
    CtiStatistics *pStat = 0;

    string hourname = getHourName(CtiTime::now());

    while( rdr() )
    {
        long new_id;

        rdr["paobjectid"] >> new_id;

        if( first_row || new_id != current_id )
        {
            if( pStat )
            {
                restored.push_back(pStat);
            }

            current_id = new_id;
            pStat = new CtiStatistics(current_id);
            first_row = false;
        }

        if( pStat )
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

                pStat->_hourRowExists.set(atoi(typeStr.substr(4).c_str()));
            }
            else if( typeStr == getCounterName(Daily, startdt) )
            {
                counter = Daily;
            }
            else if( typeStr == getCounterName(Yesterday, startdt) )
            {
                counter = Yesterday;
            }
            else if( typeStr == getCounterName(Monthly, startdt) )
            {
                counter = Monthly;
            }
            else if( typeStr == getCounterName(LastMonth, startdt) )
            {
                counter = LastMonth;
            }
            else if( typeStr == getCounterName(Lifetime, startdt) )
            {
                counter = Lifetime;
            }

            if( counter >= 0 )
            {
                // This ensures we only load the values if they are current. Old values are not loaded.
                if( startdt == pStat->_intervalBounds[counter].first || counter == Lifetime )
                {
                    rdr["requests"]         >> val;  pStat->setInitialCounterVal( Requests,       counter, val);
                    rdr["completions"]      >> val;  pStat->setInitialCounterVal( Completions,    counter, val);
                    rdr["attempts"]         >> val;  pStat->setInitialCounterVal( Attempts,       counter, val);
                    rdr["commerrors"]       >> val;  pStat->setInitialCounterVal( CommErrors,     counter, val);
                    rdr["protocolerrors"]   >> val;  pStat->setInitialCounterVal( ProtocolErrors, counter, val);
                    rdr["systemerrors"]     >> val;  pStat->setInitialCounterVal( SystemErrors,   counter, val);
                }

                pStat->_rowExists[counter] = true;
            }
        }
    }

    if( pStat )
    {
        restored.push_back(pStat);
    }
}


bool  CtiStatistics::Record(Cti::Database::DatabaseConnection &conn)
{
    bool retVal = false;

    for(int i = 0; i < FinalCounterBin; i++)
    {
        if( _dirtyCounter.test(i) )
        {
            if( _rowExists.test(i) )
            {
                retVal = Update(conn, i);
                if( retVal )
                {
                    continue;
                }
            }

            retVal = Insert(conn, i);
        }
    }

    if( retVal )
    {
        _dirty = false;
    }

    return retVal;
}


bool  CtiStatistics::Insert(Cti::Database::DatabaseConnection &conn, int counter)
{
    bool retVal = false;
    static const string sql = "insert into " + getTableName() +
        " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    if( counter < FinalCounterBin )
    {
        Cti::Database::DatabaseWriter ins(conn, sql);

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

        

        retVal = ins.execute();
        if( !retVal )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "Statistics Insert Error " << endl << sql << endl;
            }
        }
    }

    return retVal;
}

bool  CtiStatistics::Update(Cti::Database::DatabaseConnection &conn, int counter)
{
    bool retVal = false;
    static const string sql =  "update " + getTableName() + 
                        " set "
                            "requests = ?, "
                            "completions = ?, "
                            "attempts = ?, "
                            "commerrors = ?, "
                            "protocolerrors = ?, "
                            "systemerrors = ?, "
                            "startdatetime = ?, "
                            "stopdatetime = ? "
                        " where "
                            "paobjectid = ? AND "
                            "statistictype = ?";


    _intervalBounds[Lifetime].second.resetToNow();

    if( counter < FinalCounterBin )
    {
        Cti::Database::DatabaseWriter updater(conn, sql);

        // set
        updater <<
            getCounter(Requests,       counter) <<
            getCounter(Completions,    counter) <<
            getCounter(Attempts,       counter) <<
            getCounter(CommErrors,     counter) <<
            getCounter(ProtocolErrors, counter) <<
            getCounter(SystemErrors,   counter) <<
            _intervalBounds[counter].first      <<
            _intervalBounds[counter].second;

        // where
        updater << getID() << getCounterName(counter, _intervalBounds[counter].first);

        retVal = updater.execute();
        retVal &= ( updater.rowsAffected() > 0 );

        if( retVal )
        {
            _dirtyCounter[counter] = false;
        }
    }

    return retVal;
}

bool  CtiStatistics::InsertDaily(Cti::Database::DatabaseConnection &conn)
{
    if( !_doHistInsert )
    {
        return true;
    }

    bool retVal = false;
    _doHistInsert = false;

    {
        static const string sql = "insert into " + getTableNameDynamicHistory() +
            " values (?, ?, ?, ?, ?, ?, ?, ?)";

        Cti::Database::DatabaseWriter dbinserter(conn, sql);

        dbinserter <<
            getID() <<
            _intervalBounds[Yesterday].first.date().daysFrom1970() <<
            getCounter( Requests, Yesterday ) <<
            getCounter( Completions, Yesterday ) <<
            getCounter( Attempts, Yesterday ) <<
            getCounter( CommErrors, Yesterday ) <<
            getCounter( ProtocolErrors, Yesterday ) <<
            getCounter( SystemErrors, Yesterday );


        retVal = dbinserter.execute();
        if( !retVal )
        {
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << "Statistics Insert Error" << endl;
            }
        }
    }

    return retVal;
}

bool  CtiStatistics::PruneDaily(Cti::Database::DatabaseConnection &conn)
{
    int numDays = gConfigParms.getValueAsULong("STATISTICS_NUM_DAYS", 120, 10) + 1; //No magic here, I just like +1

    if( numDays <= 0 )
    {
        return true;
    }

    {
        static const string sql = "delete from " + getTableNameDynamicHistory() + " where "
                                  "dateoffset < ?";

        Cti::Database::DatabaseWriter deleter(conn, sql);
        CtiDate today;

        deleter << today.daysFrom1970() - numDays;

        return deleter.execute();
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
