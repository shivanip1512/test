/*-----------------------------------------------------------------------------*
*
* File:   mgr_holiday
*
* Date:   3/18/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_holiday.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:15 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_HOLIDAY_H__
#define __MGR_HOLIDAY_H__
#pragma warning( disable : 4786)


#include <map>

#include <rw/rwdate.h>

#include "dlldefs.h"
#include "mutex.h"
#include "guard.h"
#include "logger.h"

using namespace std;

class IM_EX_HOLIDAYDB CtiHolidayManager
{
public:
    bool isHoliday(const RWDate& date = RWDate(), long holiday_sched_id=0);
    void refresh();

    static CtiHolidayManager& getInstance();

private:

    struct holiday
    {
        unsigned month;
        unsigned day;
        int year; // could be -1
    };

    typedef multimap<long,holiday,less<long> > hSchedMap;

    hSchedMap _hsched_map;

    static CtiMutex _mux;
    static CtiHolidayManager* _instance;

    CtiHolidayManager();
    ~CtiHolidayManager() { };

};
#endif

