#pragma once

#include "ctidate.h"
#include "dlldefs.h"
#include "mutex.h"


class IM_EX_SEASONDB CtiSeasonManager
{
public:

    bool isInAnySeason(const CtiDate& date = CtiDate(), long season_sched_id=0);
    bool isInAnySeason(long season_sched_id, const CtiDate& date = CtiDate());

    bool isInNamedSeason( const long scheduleID, const std::string & name, const CtiDate & date );

    void refresh();

    static CtiSeasonManager& getInstance();

private:

    struct date_of_season
    {
        unsigned start_month;
        unsigned start_day;
        unsigned end_month;
        unsigned end_day;
        std::string name;
    };

    using SeasonScheduleMap = std::multimap<long, date_of_season>;

    SeasonScheduleMap _season_map;

    static CtiMutex _mux;
    static CtiSeasonManager* _instance;

    CtiSeasonManager();
    ~CtiSeasonManager() { };
};

