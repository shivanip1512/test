/*-----------------------------------------------------------------------------*
*
* File:   mgr_season
*
* Date:   3/16/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_season.cpp-arc  $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/23 20:34:36 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



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
bool CtiSeasonManager::isInSeason(const CtiDate& date, long season_sched_id)
{
    unsigned month_of_year = date.month();
    unsigned day_of_month = date.dayOfMonth();

    std::multimap<long, date_of_season>::iterator lower = _season_map.lower_bound(season_sched_id);
    std::multimap<long, date_of_season>::iterator upper = _season_map.upper_bound(season_sched_id);

    if(lower != upper)
    {
        for(std::multimap<long, date_of_season>::iterator iter = lower;
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
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " <<  " Couldn't locate season schedule id: " << season_sched_id << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return false;
}

bool CtiSeasonManager::isInSeason(long season_sched_id, const CtiDate& date)
{
    return isInSeason(date, season_sched_id);
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
    catch(RWExternalErr e )
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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
