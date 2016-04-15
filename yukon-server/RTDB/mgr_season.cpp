#include "precompiled.h"

#include "ctidbgmem.h"  // CTIDBG_new
#include "mgr_season.h"
#include "database_reader.h"
#include "ctidate.h"
#include "guard.h"
#include "logger.h"

using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;


CtiSeasonManager::CtiSeasonManager()
{
    refresh();
}

/*
  Returns true if the given date is inside any of the seasons in the given season schedule
*/
bool CtiSeasonManager::isInAnySeason(const CtiDate& date, long season_sched_id)
{
    unsigned month_of_year = date.month();
    unsigned day_of_month = date.dayOfMonth();

    SeasonScheduleMap::iterator lower = _season_map.lower_bound(season_sched_id);
    SeasonScheduleMap::iterator upper = _season_map.upper_bound(season_sched_id);

    if(lower != upper)
    {
        for(SeasonScheduleMap::iterator iter = lower;
            iter != upper;
            iter++)
        {
            date_of_season cur_dos = iter->second;
            if( (month_of_year > cur_dos.start_month ||
                 (month_of_year == cur_dos.start_month && day_of_month >= cur_dos.start_day)) &&
                (month_of_year < cur_dos.end_month ||
                 (month_of_year == cur_dos.end_month && day_of_month <= cur_dos.end_day)) )
            {
                return true;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Couldn't locate season schedule id: "<< season_sched_id);
    }
    return false;
}

bool CtiSeasonManager::isInAnySeason(long season_sched_id, const CtiDate& date)
{
    return isInAnySeason(date, season_sched_id);
}

bool CtiSeasonManager::isInNamedSeason( const long scheduleID, const std::string & name, const CtiDate & date )
{
    auto range = _season_map.equal_range( scheduleID );

    if ( range.first != range.second ) 
    {
        const unsigned month_of_year = date.month(),
                       day_of_month  = date.dayOfMonth();

        for ( ; range.first != range.second; ++range.first )
        {
            const date_of_season & cur_dos = range.first->second;

            if ( cur_dos.name == name &&
                 ( month_of_year > cur_dos.start_month ||
                    ( month_of_year == cur_dos.start_month && day_of_month >= cur_dos.start_day ) ) &&
                 ( month_of_year < cur_dos.end_month ||
                   ( month_of_year == cur_dos.end_month && day_of_month <= cur_dos.end_day ) ) )
            {
                return true;
            }
        }
    }
    else
    {
        CTILOG_ERROR( dout, "Couldn't locate Season Schedule ID: " << scheduleID );
    }

    return false;
}

void CtiSeasonManager::refresh()
{
    static const std::string _season_sql =
        "SELECT "
            "SeasonScheduleID, "
            "SeasonName, "
            "SeasonStartMonth, "
            "SeasonStartDay, "
            "SeasonEndMonth, "
            "SeasonEndDay "
        "FROM "
            "DateOfSeason";

    long id;
    struct date_of_season dos_temp;

    CtiLockGuard<CtiMutex> g(_mux);

    try
    {
        _season_map.clear();
        {
            DatabaseConnection conn;
            DatabaseReader rdr(conn, _season_sql);
            rdr.execute();

            while( rdr() )
            {
                rdr["SeasonScheduleID"] >> id;
                rdr["SeasonName"]       >> dos_temp.name; 
                rdr["SeasonStartMonth"] >> dos_temp.start_month;
                rdr["SeasonStartDay"]   >> dos_temp.start_day;
                rdr["SeasonEndMonth"]   >> dos_temp.end_month;
                rdr["SeasonEndDay"]     >> dos_temp.end_day;

                _season_map.emplace( id, dos_temp );
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

//STATIC
CtiMutex CtiSeasonManager::_mux;
CtiSeasonManager* CtiSeasonManager::_instance = nullptr;

CtiSeasonManager& CtiSeasonManager::getInstance()
{
    CtiLockGuard<CtiMutex> g(_mux);

    if ( ! _instance )
    {
        _instance = CTIDBG_new CtiSeasonManager;
    }

    return *_instance;
}

