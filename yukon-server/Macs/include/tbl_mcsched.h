#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/tbl_mcsched.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/12/16 23:56:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*---------------------------------------------------------------------------

        Filename:  tbl_mcsched.h

        Programmer:  Aaron Lauinger

        Description:    Header file for CtiMCSchedule.

        Initial Date:  4/7/99
                       1/11/01

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999, 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef __TBL_MCSCHED_H__
#define _TBL_MCSCHED_H__

#include <string>
using namespace std;

#include <rw/db/db.h>

#include "mutex.h"
#include "guard.h"
#include "logger.h"

class CtiTableMCSchedule
{
public:

    CtiTableMCSchedule();
    CtiTableMCSchedule(long sched_id,                       
                       const string& category_name,                       
                       long h_sched_id,
                       const string& cmd_file,
                       const string& state,
                       const string& start_policy,
                       const string& stop_policy,
                       const RWTime& last_run_time,
                       const string& last_run_status,
                       int start_day,
                       int start_month,
                       int start_year,
                       const string& start_time,
                       const string& stop_time,
                       const string& valid_week_days,
                       int duration,
                       const RWTime& manual_start_time,
                       const RWTime& manual_stop_time,
                       int template_type );

    virtual ~CtiTableMCSchedule();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    bool DecodeDatabaseReader(RWDBReader &rdr);

    //database operations
    bool Update();
    bool Insert();
    bool Delete();

    long getScheduleID() const;
    const string& getCategoryName() const;
    long getHolidayScheduleID() const;
    const string& getCommandFile() const;
    const string& getCurrentState() const;
    const string& getStartPolicy() const;
    const string& getStopPolicy() const;
    const RWTime& getLastRunTime() const;
    const string& getLastRunStatus() const;
    int getStartDay() const;
    int getStartMonth() const;
    int getStartYear() const;
    const string& getStartTime() const;
    const string& getStopTime() const;
    const string& getValidWeekDays() const;
    int getDuration() const;
    const RWTime& getManualStartTime() const;
    const RWTime& getManualStopTime() const;
    int getTemplateType() const;

    CtiTableMCSchedule& setScheduleID(long sched_id);
    CtiTableMCSchedule& setScheduleName(const string& schedule_name);
    CtiTableMCSchedule& setCategoryName(const string& category_name);
    CtiTableMCSchedule& setScheduleType(const string& sched_type);
    CtiTableMCSchedule& setHolidayScheduleID(long holiday_sched_id);
    CtiTableMCSchedule& setCommandFile(const string& cmd_file);
    CtiTableMCSchedule& setCurrentState(const string& state);
    CtiTableMCSchedule& setStartPolicy(const string& start_policy);
    CtiTableMCSchedule& setStopPolicy(const string& stop_policy);
    CtiTableMCSchedule& setLastRunTime(const RWTime& last_run_time);
    CtiTableMCSchedule& setLastRunStatus(const string& last_run_status);
    CtiTableMCSchedule& setStartDay(int start_day);
    CtiTableMCSchedule& setStartMonth(int start_month);
    CtiTableMCSchedule& setStartYear(int start_year);
    CtiTableMCSchedule& setStartTime(const string& start_time);
    CtiTableMCSchedule& setStopTime(const string& stop_time);
    CtiTableMCSchedule& setValidWeekDays(const string& valid_week_days);
    CtiTableMCSchedule& setDuration(int duration);
    CtiTableMCSchedule& setManualStartTime(const RWTime& manual_start_time);
    CtiTableMCSchedule& setManualStopTime(const RWTime& manual_stop_time);
    CtiTableMCSchedule& setTemplateType(int template_type);

private:

    //CtiMutex _mux;

    static const char* _table_name;

    long            _schedule_id;
    string          _schedule_name;
    string          _category_name;
    string          _schedule_type;
    long            _holiday_schedule_id;
    string          _command_file;
    string          _current_state;
    string          _start_policy;
    string          _stop_policy;
    RWTime          _last_run_time;
    string          _last_run_status;
    int             _start_day;
    int             _start_month;
    int             _start_year;
    string          _start_time;  // in HH:MM:SS format
    string          _stop_time;
    string          _valid_week_days; // XXXXXXXZ where X = Y,N, Z = E,F,N
                             // Y = yes on that day, N = no on that day
                             // E = holiday exclusion,
    int             _duration;
    RWTime          _manual_start_time;
    RWTime          _manual_stop_time;
    int             _template_type;

};
#endif
