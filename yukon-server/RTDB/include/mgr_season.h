/*-----------------------------------------------------------------------------*
*
* File:   mgr_season
*
* Date:   4/16/2003
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/mgr_season.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/06/28 20:13:21 $
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

class IM_EX_SEASONDB CtiSeasonManager
{
public:
    bool isInSeason(const RWDate& date = RWDate(), long season_sched_id=0);
    bool isInSeason(long season_sched_id, const RWDate& date = RWDate());
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

    multimap<long,date_of_season> _season_map;

    static CtiMutex _mux;
    static CtiSeasonManager* _instance;

    CtiSeasonManager();
    ~CtiSeasonManager() { };

    static const string _season_sql;
};
#endif

