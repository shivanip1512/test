
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mgr_holiday
*
* Date:   3/18/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_holiday.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __MGR_HOLIDAY_H__
#define __MGR_HOLIDAY_H__

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

