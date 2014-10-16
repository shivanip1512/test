/*-----------------------------------------------------------------------------*
*
* File:   mgr_holiday
*
* Date:   3/18/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_holiday.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/23 20:34:36 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"



#include "ctidbgmem.h"  // CTIDBG_new
#include "mgr_holiday.h"
#include "dbaccess.h"
#include "database_reader.h"
#include "database_connection.h"

using std::string;
using std::endl;
using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

const string CtiHolidayManager::holidaysql("select holidayschedule.holidayscheduleid,dateofholiday.holidaymonth,dateofholiday.holidayday,dateofholiday.holidayyear \
from holidayschedule,dateofholiday \
where dateofholiday.holidayscheduleid=holidayschedule.holidayscheduleid");

CtiHolidayManager::CtiHolidayManager()
{
    refresh();
}

bool CtiHolidayManager::isHoliday(const CtiDate& date, long holiday_sched_id)
{
    bool is_holiday = false;

    for( hSchedMap::iterator iter = _hsched_map.begin() ;
       iter != _hsched_map.end();
       iter++ )
    {
        if( iter->first == holiday_sched_id &&
            iter->second.month == date.month()  &&
            iter->second.day == date.dayOfMonth() &&
            (iter->second.year == -1 || iter->second.year == date.year()) )
        {
            is_holiday = true;
            break;
        }
    }
    return is_holiday;
}


bool CtiHolidayManager::isHolidayForAnySchedule(const CtiDate& date)
{
    bool is_holiday = false;

    for( hSchedMap::iterator iter = _hsched_map.begin() ;
       iter != _hsched_map.end();
       iter++ )
    {
        if( iter->second.month == date.month()  &&
            iter->second.day == date.dayOfMonth() &&
            (iter->second.year == -1 || iter->second.year == date.year()) )
        {
            is_holiday = true;
            break;
        }
    }
    return is_holiday;
}

bool CtiHolidayManager::isHoliday(long holiday_sched_id, const CtiDate& date)
{
    return isHoliday(date,holiday_sched_id);
}

void CtiHolidayManager::refresh()
{
    long id;
    struct holiday h_temp;

    CtiLockGuard<CtiMutex> g(_mux);

    try
    {
        _hsched_map.clear();

        {
            DatabaseConnection conn;
            DatabaseReader rdr(conn, holidaysql);
            rdr.execute();

            while( rdr() )
            {
                rdr["holidayscheduleid"]    >> id;
                rdr["holidaymonth"]         >> h_temp.month;
                rdr["holidayday"]           >> h_temp.day;
                rdr["holidayyear"]          >> h_temp.year;

                _hsched_map.insert( hSchedMap::value_type(id, h_temp));
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
CtiMutex CtiHolidayManager::_mux;
CtiHolidayManager* CtiHolidayManager::_instance = NULL;

CtiHolidayManager& CtiHolidayManager::getInstance()
{
    CtiLockGuard<CtiMutex> g(_mux);

    if( _instance == NULL )
        _instance = CTIDBG_new CtiHolidayManager;

    return *_instance;
}
