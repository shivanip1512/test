#pragma once

#include <set>

#include "mc.h"
#include "rtdb.h"
#include "mc_sched.h"

class CtiMCScheduleManager : public CtiRTDB< CtiMCSchedule >
{
public:

    // refreshAll, updateAll operate on the database
    // retrieves all schedules from the database
    bool refreshAllSchedules();

    // update all schedules in the database
    void updateAllSchedules();

    // add,update,delete below operate on
    // schedules in memory
    CtiMCSchedule* addSchedule(const CtiMCSchedule& sched);
    bool updateSchedule(const CtiMCSchedule& sched);
    bool deleteSchedule(long sched_id);

    CtiMCSchedule* findSchedule(long id);
    long getID(const std::string& name);

private:

    // Schedules are moved here before they are deleted
    // from the database
    std::set< CtiMCSchedule* > _schedules_to_delete;

    bool retrieveSimpleSchedules(
        std::map
        < long, CtiMCSchedule* >&
        sched_map );

    bool retrieveScriptedSchedules(
        std::map
        < long, CtiMCSchedule* >&
        sched_map );

    long nextScheduleID();
};

