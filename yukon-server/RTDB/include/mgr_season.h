/*-----------------------------------------------------------------------------*
*
* File:   mgr_season
*
* Date:   4/16/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_season.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/09/22 23:18:58 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_SEASON_H__
#define __MGR_SEASON_H__
#pragma warning( disable : 4786)


#include <map>

#include <rw/rwdate.h>

#include "dlldefs.h"
#include "mutex.h"
#include "guard.h"
#include "logger.h"

using namespace std;

#define SEASON_SCHEDULE_SPRING         0
#define SEASON_SCHEDULE_SUMMER         1
#define SEASON_SCHEDULE_FALL           2
#define SEASON_SCHEDULE_WINTER         3

class IM_EX_SEASONDB CtiSeasonManager
{
public:
    long getCurrentSeason(const RWDate& date = RWDate(), long season_sched_id=0);
    long getCurrentSeason(long season_sched_id, const RWDate& date = RWDate());
    void refresh();

    static CtiSeasonManager& getInstance();

private:

    struct seasonSchedule
    {
        unsigned springstartmonth;
        unsigned springstartday;
        unsigned summerstartmonth;
        unsigned summerstartday;
        unsigned fallstartmonth;
        unsigned fallstartday;
        unsigned winterstartmonth;
        unsigned winterstartday;
    };

    typedef multimap<long,seasonSchedule,less<long> > sSchedMap;

    sSchedMap _ssched_map;

    static CtiMutex _mux;
    static CtiSeasonManager* _instance;

    CtiSeasonManager();
    ~CtiSeasonManager() { };

    static const RWCString seasonsql;
};
#endif

