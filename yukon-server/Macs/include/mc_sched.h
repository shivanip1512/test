/*-----------------------------------------------------------------------------*
*
* File:   mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/mc_sched.h-arc  $
* REVISION     :  $Revision: 1.8.12.1 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#ifndef __MCSCHED_H__
#define __MCSCHED_H__

#include <time.h>

#include <rw/db/reader.h>

#include "mc.h"
#include "dbmemobject.h"
#include "tbl_pao.h"
#include "tbl_mcsched.h"
#include "tbl_mcsimpsched.h"
#include "guard.h"
#include "logger.h"
#include "message.h"

class CtiMCSchedule : public CtiMemDBObject, public CtiMessage
{
public:

    RWDECLARE_COLLECTABLE( CtiMCSchedule )

    // Schedule Types
    static const char* Simple;
    static const char* Scripted;

    // Possible States
    static const char* Waiting;
    static const char* Pending;
    static const char* Running;
    static const char* Disabled;

    // Start policies
    static const char* DateTimeStartPolicy;
    static const char* DayOfMonthStartPolicy;
    static const char* WeekDayTimeStartPolicy;
    static const char* ManualStartPolicy;

    // Stop policies
    static const char* AbsoluteTimeStopPolicy;
    static const char* DurationStopPolicy;
    static const char* UntilCompleteStopPolicy;
    static const char* ManualStopPolicy;

    // Last Run Status
    static const char* None;
    static const char* Error;
    static const char* Finished;

    // Defaults
    static const char* DefaultType;
    static const char* DefaultState;
    static const char* DefaultCategory;
    static const char* DefaultStartPolicy;
    static const char* DefaultStopPolicy;
    static const char* DefaultLastRunStatus;

    CtiMCSchedule();
    CtiMCSchedule(const CtiMCSchedule& sched);
    virtual ~CtiMCSchedule();

    virtual bool operator==(const CtiMCSchedule& ref) const;
    virtual CtiMCSchedule& operator=(const CtiMCSchedule& sched);

    virtual CtiMessage* replicateMessage() const;

    // true if simple, false is scripted
    bool isSimpleSchedule() const;

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    bool DecodeDatabaseReader(RWDBReader &rdr);

    // Update and insert into the database
    bool Update();
    bool Insert();
    bool Delete();

    long getScheduleID() const;
    const string getScheduleName() const;
    const string& getCategoryName() const;
    const string getScheduleType() const;
    long getHolidayScheduleID() const;
    const string& getCommandFile() const;
    const string& getCurrentState() const;
    const string& getStartPolicy() const;
    const string& getStopPolicy() const;
    const CtiTime& getLastRunTime() const;
    const string& getLastRunStatus() const;
    int getStartDay() const;
    int getStartMonth() const;
    int getStartYear() const;
    const string& getStartTime() const;
    const string& getStopTime() const;
    const string& getValidWeekDays() const;
    int getDuration() const;
    const CtiTime& getManualStartTime() const;
    const CtiTime& getManualStopTime() const;
    const CtiTime& getCurrentStartTime() const;
    const CtiTime& getCurrentStopTime() const;
    int getTemplateType() const;

    //Only simple schedules will return meaningfull info for these
    long getTargetPaoId() const;
    const string& getStartCommand() const;
    const string& getStopCommand() const;
    long getRepeatInterval() const;

    CtiMCSchedule& setScheduleID(long sched_id);
    CtiMCSchedule& setScheduleName(const string& schedule_name);
    CtiMCSchedule& setCategoryName(const string& category_name);
    CtiMCSchedule& setScheduleType(const string& sched_type);
    CtiMCSchedule& setHolidayScheduleID(long holiday_sched_id);
    CtiMCSchedule& setCommandFile(const string& cmd_file);
    CtiMCSchedule& setCurrentState(const string& state);
    CtiMCSchedule& setStartPolicy(const string& start_policy);
    CtiMCSchedule& setStopPolicy(const string& stop_policy);
    CtiMCSchedule& setLastRunTime(const CtiTime& last_run_time);
    CtiMCSchedule& setLastRunStatus(const string& last_run_status);
    CtiMCSchedule& setStartDay(int start_day);
    CtiMCSchedule& setStartMonth(int start_month);
    CtiMCSchedule& setStartYear(int start_year);
    CtiMCSchedule& setStartTime(const string& start_time);
    CtiMCSchedule& setStopTime(const string& stop_time);
    CtiMCSchedule& setValidWeekDays(const string& valid_week_days);
    CtiMCSchedule& setDuration(int duration);
    CtiMCSchedule& setManualStartTime(const CtiTime& manual_start_time);
    CtiMCSchedule& setManualStopTime(const CtiTime& manual_stop_time);
    CtiMCSchedule& setCurrentStartTime(const CtiTime& start_time);
    CtiMCSchedule& setCurrentStopTime(const CtiTime& stop_time);
    CtiMCSchedule& setTemplateType(int template_type);

    //Only simple schedules will return meaningfull info for these
    CtiMCSchedule& setTargetSelect(const int target_select);
    CtiMCSchedule& setStartCommand(const string& start_command);
    CtiMCSchedule& setStopCommand(const string& stop_command);
    CtiMCSchedule& setRepeatInterval(long repeat_interval);

    // CGP 022802 DBMEMOBJ lost his monitor.
    CtiMutex& getMux()  { return _classMutex;}

    // For RW Streaming
    virtual void saveGuts(RWvostream &aStream) const;
    virtual void restoreGuts(RWvistream& aStream);


//debug
    bool checkSchedule() const;
    bool checkField(const string& fld, const string& msg) const;

protected:

    CtiTime _current_start_time;
    CtiTime _current_stop_time;

    CtiTblPAO _pao_table;
    CtiTableMCSchedule _schedule_table;
    CtiTableMCSimpleSchedule _simple_schedule_table;

private:

    bool isValidTime(const CtiTime& t) const;
    mutable CtiMutex _classMutex;
};

std::ostream& operator<<( std::ostream& ostrm, CtiMCSchedule& sched );


#endif
