/*-----------------------------------------------------------------------------*
*
* File:   mgr_season
*
* Date:   3/16/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_season.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/06/30 22:02:23 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include "ctidbgmem.h"  // CTIDBG_new
#include "mgr_season.h"
#include "dbaccess.h"

const string CtiSeasonManager::_season_sql = "select seasonscheduleid, seasonstartmonth, seasonstartday, seasonendmonth, seasonendday from dateofseason";

CtiSeasonManager::CtiSeasonManager()
{
    refresh();
}

/*
  Returns true if the given date is inside any of the seasons in the given season schedule
*/
bool CtiSeasonManager::isInSeason(const RWDate& date, long season_sched_id)
{
    unsigned month_of_year = date.month();
    unsigned day_of_month = date.dayOfMonth();
    
    multimap<long, date_of_season>::iterator iter = _season_map.find(season_sched_id);
    multimap<long, date_of_season>::iterator last_elem = _season_map.upper_bound(season_sched_id);
    
    if(iter != _season_map.end())
    {
	do
	{
	    date_of_season cur_dos = iter->second;
	    if( (month_of_year > cur_dos.start_month ||
		 (month_of_year == cur_dos.start_month && day_of_month >= cur_dos.start_day)) &&
		(month_of_year < cur_dos.end_month ||
		 (month_of_year == cur_dos.end_month && day_of_month <= cur_dos.end_day)) )
	    {
		return true;
	    }
	} while(iter++ != last_elem);
    }

    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  " Couldn't locate season schedule id: " << season_sched_id << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return false;
}

bool CtiSeasonManager::isInSeason(long season_sched_id, const RWDate& date)
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
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            RWDBReader rdr = ExecuteQuery(conn, _season_sql.data());

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
