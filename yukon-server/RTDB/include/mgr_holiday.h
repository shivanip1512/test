#pragma once

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
