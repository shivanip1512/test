#include "precompiled.h"

#include "ctidbgmem.h"  // CTIDBG_new
#include "mgr_season.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"
#include "ctidate.h"

using std::multimap;
using std::make_pair;
using std::string;
using std::endl;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

const string CtiSeasonManager::_season_sql = "select seasonscheduleid, seasonstartmonth, seasonstartday, seasonendmonth, seasonendday from dateofseason";

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

void CtiSeasonManager::refresh()
{
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
                rdr["seasonscheduleid"]      >> id;
                rdr["seasonstartmonth"] >> dos_temp.start_month;
                rdr["seasonstartday"] >> dos_temp.start_day;
                rdr["seasonendmonth"] >> dos_temp.end_month;
                rdr["seasonendday"] >> dos_temp.end_day;

                _season_map.insert( make_pair(id, dos_temp) );
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return;
}

//STATIC
CtiMutex CtiSeasonManager::_mux;
CtiSeasonManager* CtiSeasonManager::_instance = NULL;

CtiSeasonManager& CtiSeasonManager::getInstance()
{
    CtiLockGuard<CtiMutex> g(_mux);

    if( _instance == NULL )
        _instance = CTIDBG_new CtiSeasonManager;

    return *_instance;
}
