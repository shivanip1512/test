
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mgr_holiday
*
* Date:   3/18/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_holiday.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/05/02 17:02:23 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "mgr_holiday.h"
#include "dbaccess.h"

const RWCString sql("select holidayschedule.holidayscheduleid,dateofholiday.holidaymonth,dateofholiday.holidayday,dateofholiday.holidayyear \
from holidayschedule,dateofholiday \
where dateofholiday.holidayscheduleid=holidayschedule.holidayscheduleid");

CtiHolidayManager::CtiHolidayManager()
{
    refresh();
}

bool CtiHolidayManager::isHoliday(const RWDate& date, long holiday_sched_id)
{
    bool is_holiday = false;

    for( hSchedMap::iterator iter = _hsched_map.begin() ;
       iter != _hsched_map.end();
       iter++ )
    {
        if( iter->first == holiday_sched_id &&
            iter->second.month+1 == date.month()  &&
            iter->second.day == date.dayOfMonth() &&
            (iter->second.year == -1 || iter->second.year == date.year()) )
        {
            is_holiday = true;
        }
    }
    return is_holiday;
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
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();
            RWDBReader rdr = ExecuteQuery(conn, sql);

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
CtiMutex CtiHolidayManager::_mux;
CtiHolidayManager* CtiHolidayManager::_instance = NULL;

CtiHolidayManager& CtiHolidayManager::getInstance()
{
    CtiLockGuard<CtiMutex> g(_mux);

    if( _instance == NULL )
        _instance = new CtiHolidayManager;

    return *_instance;
}
