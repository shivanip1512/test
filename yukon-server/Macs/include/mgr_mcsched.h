/*-----------------------------------------------------------------------------*
*
* File:   mgr_mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mgr_mcsched.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#ifndef __MGR_MCSCHEDULE_H__
#define __MGR_MCSCHEDULE_H__

#include <set>

#include "mc.h"
#include "rtdb.h"
#include "mc_sched.h"

class CtiMCScheduleManager : public CtiRTDB< CtiMCSchedule >
{
public:

    // refreshAll, updateAll operate on the database
    // retrieves all scheduls from the database
    virtual bool refreshAllSchedules();

    // update all schedules in the database
    virtual bool updateAllSchedules();

    // add,update,delete below operate on
    // schedules in memory
    virtual CtiMCSchedule* addSchedule(const CtiMCSchedule& sched);
    virtual bool updateSchedule(const CtiMCSchedule& sched);
    virtual bool deleteSchedule(long sched_id);

    CtiMCSchedule* findSchedule(long id);
    long getID(const string& name);

private:

    // Schedules are moved here before they are deleted
    // from the database
    set< CtiMCSchedule* > _schedules_to_delete;

    bool retrieveSimpleSchedules(
        RWTPtrHashMap
        < CtiHashKey, CtiMCSchedule, my_hash<CtiHashKey> , equal_to<CtiHashKey> >&
        sched_map );

    bool retrieveScriptedSchedules(
        RWTPtrHashMap
        < CtiHashKey, CtiMCSchedule, my_hash<CtiHashKey> , equal_to<CtiHashKey> >&
        sched_map );

    long nextScheduleID();
};

ostream& operator<<( ostream& ostrm, CtiMCScheduleManager& mgr );

#endif
