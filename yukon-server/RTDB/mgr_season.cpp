/*-----------------------------------------------------------------------------*
*
* File:   mgr_season
*
* Date:   3/16/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_season.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/06/28 20:13:21 $
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
	    if( month_of_year >= cur_dos.start_month && day_of_month >= cur_dos.start_day &&
		month_of_year <= cur_dos.end_month && day_of_month <= cur_dos.end_day )
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
#ifdef _BUNG_
long CtiSeasonManager::getCurrentSeason(const RWDate& date, long season_sched_id)
{
    long returnSeason = SEASON_SCHEDULE_SUMMER;

    sSchedMap::iterator iter;
    iter = _ssched_map.find(season_sched_id);
    if( iter != _ssched_map.end() )
    {
        LONG currentDayOfYear       = ((date.month())*31)+date.dayOfMonth();
        LONG springStartDayOfYear   = ((iter->second.springstartmonth+1)*31)+iter->second.springstartday;
        LONG summerStartDayOfYear   = ((iter->second.summerstartmonth+1)*31)+iter->second.summerstartday;
        LONG fallStartDayOfYear     = ((iter->second.fallstartmonth+1)*31)+iter->second.fallstartday;
        LONG winterStartDayOfYear   = ((iter->second.winterstartmonth+1)*31)+iter->second.winterstartday;
        //determining which season stratles the new year
        if( springStartDayOfYear < winterStartDayOfYear )
        {//year increments during the winter i.e. Dec through Jan is Winter, first case will probably be most common
            if( currentDayOfYear >= winterStartDayOfYear ||
                currentDayOfYear < springStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_WINTER;
            }
            else if( currentDayOfYear >= springStartDayOfYear &&
                     currentDayOfYear < summerStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SPRING;
            }
            else if( currentDayOfYear >= summerStartDayOfYear &&
                     currentDayOfYear < fallStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SUMMER;
            }
            else if( currentDayOfYear >= fallStartDayOfYear &&
                     currentDayOfYear < winterStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_FALL;
            }
        }
        else if( winterStartDayOfYear < fallStartDayOfYear )
        {//Dec through Jan are in the fall, this is also fairly common
            if( currentDayOfYear >= fallStartDayOfYear ||
                currentDayOfYear < winterStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_FALL;
            }
            else if( currentDayOfYear >= winterStartDayOfYear &&
                     currentDayOfYear < springStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_WINTER;
            }
            else if( currentDayOfYear >= springStartDayOfYear &&
                     currentDayOfYear < summerStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SPRING;
            }
            else if( currentDayOfYear >= summerStartDayOfYear &&
                     currentDayOfYear < fallStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SUMMER;
            }
        }
        else if( fallStartDayOfYear < summerStartDayOfYear )
        {//must be in the southern hemisphere since the summer is through Dec and Jan
            if( currentDayOfYear >= summerStartDayOfYear ||
                currentDayOfYear < fallStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SUMMER;
            }
            else if( currentDayOfYear >= fallStartDayOfYear &&
                     currentDayOfYear < winterStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_FALL;
            }
            else if( currentDayOfYear >= winterStartDayOfYear &&
                     currentDayOfYear < springStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_WINTER;
            }
            else if( currentDayOfYear >= springStartDayOfYear &&
                     currentDayOfYear < summerStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SPRING;
            }
        }
        else if( summerStartDayOfYear < springStartDayOfYear )
        {//the year changed over in spring, I guess I'll take your word for it
            if( currentDayOfYear >= springStartDayOfYear ||
                currentDayOfYear < summerStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SPRING;
            }
            else if( currentDayOfYear >= summerStartDayOfYear &&
                     currentDayOfYear < fallStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_SUMMER;
            }
            else if( currentDayOfYear >= fallStartDayOfYear &&
                     currentDayOfYear < winterStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_FALL;
            }
            else if( currentDayOfYear >= winterStartDayOfYear &&
                     currentDayOfYear < springStartDayOfYear )
            {
                returnSeason = SEASON_SCHEDULE_WINTER;
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> guard(dout);
        dout << "Cannot find Season Schedule with Id: " << season_sched_id << " in: " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return returnSeason;
}
#endif

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
                rdr["scheduleid"]      >> id;
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
