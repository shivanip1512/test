/*-----------------------------------------------------------------------------*
*
* File:   mgr_season
*
* Date:   3/16/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_season.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/04/22 21:41:52 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include "ctidbgmem.h"  // CTIDBG_new
#include "mgr_season.h"
#include "dbaccess.h"

const RWCString sql("select scheduleid, springmonth, springday, summermonth, summerday, fallmonth, fallday, wintermonth, winterday from seasonschedule");

CtiSeasonManager::CtiSeasonManager()
{
    refresh();
}

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

long CtiSeasonManager::getCurrentSeason(long season_sched_id, const RWDate& date)
{
    return getCurrentSeason(date, season_sched_id);
}

void CtiSeasonManager::refresh()
{
    long id;
    struct seasonSchedule ss_temp;

    CtiLockGuard<CtiMutex> g(_mux);

    try
    {
        _ssched_map.clear();

        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            RWDBReader rdr = ExecuteQuery(conn, sql);

            while( rdr() )
            {
                rdr["scheduleid"]      >> id;
                rdr["springmonth"]     >> ss_temp.springstartmonth;
                rdr["springday"]       >> ss_temp.springstartday;
                rdr["summermonth"]     >> ss_temp.summerstartmonth;
                rdr["summerday"]       >> ss_temp.summerstartday;
                rdr["fallmonth"]       >> ss_temp.fallstartmonth;
                rdr["fallday"]         >> ss_temp.fallstartday;
                rdr["wintermonth"]     >> ss_temp.winterstartmonth;
                rdr["winterday"]       >> ss_temp.winterstartday;

                _ssched_map.insert( sSchedMap::value_type(id, ss_temp));
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
