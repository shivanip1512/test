#pragma once

#include <map>

#include "ctidate.h"
#include "ctitime.h"

#include "dlldefs.h"
#include "mutex.h"
#include "guard.h"
#include "logger.h"

class IM_EX_SEASONDB CtiSeasonManager
{
public:
    bool isInSeason(const CtiDate& date = CtiDate(), long season_sched_id=0);
    bool isInSeason(long season_sched_id, const CtiDate& date = CtiDate());
    void refresh();

    static CtiSeasonManager& getInstance();

private:

    struct date_of_season
    {
	unsigned start_month;
	unsigned start_day;
	unsigned end_month;
	unsigned end_day;
    };

    std::multimap<long,date_of_season> _season_map;

    static CtiMutex _mux;
    static CtiSeasonManager* _instance;

    CtiSeasonManager();
    ~CtiSeasonManager() { };

    static const std::string _season_sql;
};
