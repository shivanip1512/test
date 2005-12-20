/*-----------------------------------------------------------------------------*
*
* File:   mgr_holiday
*
* Date:   3/18/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_holiday.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/12/20 17:20:30 $
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

using std::map;
using std::multimap;
using std::less;

class IM_EX_HOLIDAYDB CtiHolidayManager
{
public:
    bool isHoliday(const CtiDate& date = CtiDate(), long holiday_sched_id=0);
    bool isHoliday(long holiday_sched_id, const CtiDate& date = CtiDate());
    void refresh();

    static CtiHolidayManager& getInstance();

private:

    struct holiday
    {
        unsigned month;
        unsigned day;
        int year; // could be -1
    };

    typedef std::multimap<long,holiday,less<long> > hSchedMap;

    hSchedMap _hsched_map;

    static CtiMutex _mux;
    static CtiHolidayManager* _instance;

    CtiHolidayManager();
    ~CtiHolidayManager() { };

    static const string holidaysql;
};
#endif

