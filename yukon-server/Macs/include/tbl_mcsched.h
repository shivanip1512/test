#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/tbl_mcsched.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/08/07 21:04:32 $
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


#include "mutex.h"
#include "guard.h"
#include "logger.h"
#include "row_reader.h"

class CtiTableMCSchedule
{
public:

    CtiTableMCSchedule();
    CtiTableMCSchedule(long sched_id,                       
                       const std::string& category_name,                       
                       long h_sched_id,
                       const std::string& cmd_file,
                       const std::string& state,
                       const std::string& start_policy,
                       const std::string& stop_policy,
                       const CtiTime& last_run_time,
                       const std::string& last_run_status,
                       int start_day,
                       int start_month,
                       int start_year,
                       const std::string& start_time,
                       const std::string& stop_time,
                       const std::string& valid_week_days,
                       int duration,
                       const CtiTime& manual_start_time,
                       const CtiTime& manual_stop_time,
                       int template_type );

    virtual ~CtiTableMCSchedule();

    bool DecodeDatabaseReader(Cti::RowReader &rdr);

    //database operations
    bool Update();
    bool Insert();
    bool Delete();

    long getScheduleID() const;
    const std::string& getCategoryName() const;
    long getHolidayScheduleID() const;
    const std::string& getCommandFile() const;
    const std::string& getCurrentState() const;
    const std::string& getStartPolicy() const;
    const std::string& getStopPolicy() const;
    const CtiTime& getLastRunTime() const;
    const std::string& getLastRunStatus() const;
    int getStartDay() const;
    int getStartMonth() const;
    int getStartYear() const;
    const std::string& getStartTime() const;
    const std::string& getStopTime() const;
    const std::string& getValidWeekDays() const;
    int getDuration() const;
    const CtiTime& getManualStartTime() const;
    const CtiTime& getManualStopTime() const;
    int getTemplateType() const;

    CtiTableMCSchedule& setScheduleID(long sched_id);
    CtiTableMCSchedule& setScheduleName(const std::string& schedule_name);
    CtiTableMCSchedule& setCategoryName(const std::string& category_name);
    CtiTableMCSchedule& setScheduleType(const std::string& sched_type);
    CtiTableMCSchedule& setHolidayScheduleID(long holiday_sched_id);
    CtiTableMCSchedule& setCommandFile(const std::string& cmd_file);
    CtiTableMCSchedule& setCurrentState(const std::string& state);
    CtiTableMCSchedule& setStartPolicy(const std::string& start_policy);
    CtiTableMCSchedule& setStopPolicy(const std::string& stop_policy);
    CtiTableMCSchedule& setLastRunTime(const CtiTime& last_run_time);
    CtiTableMCSchedule& setLastRunStatus(const std::string& last_run_status);
    CtiTableMCSchedule& setStartDay(int start_day);
    CtiTableMCSchedule& setStartMonth(int start_month);
    CtiTableMCSchedule& setStartYear(int start_year);
    CtiTableMCSchedule& setStartTime(const std::string& start_time);
    CtiTableMCSchedule& setStopTime(const std::string& stop_time);
    CtiTableMCSchedule& setValidWeekDays(const std::string& valid_week_days);
    CtiTableMCSchedule& setDuration(int duration);
    CtiTableMCSchedule& setManualStartTime(const CtiTime& manual_start_time);
    CtiTableMCSchedule& setManualStopTime(const CtiTime& manual_stop_time);
    CtiTableMCSchedule& setTemplateType(int template_type);

private:

    //CtiMutex _mux;

    static const char* _table_name;

    long            _schedule_id;
    std::string          _schedule_name;
    std::string          _category_name;
    std::string          _schedule_type;
    long            _holiday_schedule_id;
    std::string          _command_file;
    std::string          _current_state;
    std::string          _start_policy;
    std::string          _stop_policy;
    CtiTime          _last_run_time;
    std::string          _last_run_status;
    int             _start_day;
    int             _start_month;
    int             _start_year;
    std::string          _start_time;  // in HH:MM:SS format
    std::string          _stop_time;
    std::string          _valid_week_days; // XXXXXXXZ where X = Y,N, Z = E,F,N
                             // Y = yes on that day, N = no on that day
                             // E = holiday exclusion,
    int             _duration;
    CtiTime          _manual_start_time;
    CtiTime          _manual_stop_time;
    int             _template_type;

};
#endif
