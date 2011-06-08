/*-----------------------------------------------------------------------------*
*
* File:   mgr_holiday
*
* Date:   3/18/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_holiday.h-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/03/11 16:43:17 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_HOLIDAY_H__
#define __MGR_HOLIDAY_H__
#pragma warning( disable : 4786)


#include <map>

#include "ctidate.h"
#include "ctitime.h"


#include "dlldefs.h"
#include "mutex.h"
#include "guard.h"
#include "logger.h"

class IM_EX_HOLIDAYDB CtiHolidayManager
{
public:
    bool isHoliday(const CtiDate& date = CtiDate(), long holiday_sched_id=0);
    bool isHoliday(long holiday_sched_id, const CtiDate& date = CtiDate());
    bool isHolidayForAnySchedule(const CtiDate& date);
    void refresh();

    static CtiHolidayManager& getInstance();

private:

    struct holiday
    {
        unsigned month;
        unsigned day;
        int year; // could be -1
    };

    typedef std::multimap<long,holiday,std::less<long> > hSchedMap;

    hSchedMap _hsched_map;

    static CtiMutex _mux;
    static CtiHolidayManager* _instance;

    CtiHolidayManager();
    ~CtiHolidayManager() { };

    static const std::string holidaysql;
};
#endif

