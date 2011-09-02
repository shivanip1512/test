#pragma once
#include <time.h>

#include "row_reader.h"

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

    bool DecodeDatabaseReader(Cti::RowReader &rdr);

    // Update and insert into the database
    bool Update();
    bool Insert();
    bool Delete();

    long getScheduleID() const;
    const std::string getScheduleName() const;
    const std::string& getCategoryName() const;
    const std::string getScheduleType() const;
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
    const CtiTime& getCurrentStartTime() const;
    const CtiTime& getCurrentStopTime() const;
    int getTemplateType() const;

    //Only simple schedules will return meaningfull info for these
    long getTargetPaoId() const;
    const std::string& getStartCommand() const;
    const std::string& getStopCommand() const;
    long getRepeatInterval() const;

    CtiMCSchedule& setScheduleID(long sched_id);
    CtiMCSchedule& setScheduleName(const std::string& schedule_name);
    CtiMCSchedule& setCategoryName(const std::string& category_name);
    CtiMCSchedule& setScheduleType(const std::string& sched_type);
    CtiMCSchedule& setHolidayScheduleID(long holiday_sched_id);
    CtiMCSchedule& setCommandFile(const std::string& cmd_file);
    CtiMCSchedule& setCurrentState(const std::string& state);
    CtiMCSchedule& setStartPolicy(const std::string& start_policy);
    CtiMCSchedule& setStopPolicy(const std::string& stop_policy);
    CtiMCSchedule& setLastRunTime(const CtiTime& last_run_time);
    CtiMCSchedule& setLastRunStatus(const std::string& last_run_status);
    CtiMCSchedule& setStartDay(int start_day);
    CtiMCSchedule& setStartMonth(int start_month);
    CtiMCSchedule& setStartYear(int start_year);
    CtiMCSchedule& setStartTime(const std::string& start_time);
    CtiMCSchedule& setStopTime(const std::string& stop_time);
    CtiMCSchedule& setValidWeekDays(const std::string& valid_week_days);
    CtiMCSchedule& setDuration(int duration);
    CtiMCSchedule& setManualStartTime(const CtiTime& manual_start_time);
    CtiMCSchedule& setManualStopTime(const CtiTime& manual_stop_time);
    CtiMCSchedule& setCurrentStartTime(const CtiTime& start_time);
    CtiMCSchedule& setCurrentStopTime(const CtiTime& stop_time);
    CtiMCSchedule& setTemplateType(int template_type);

    //Only simple schedules will return meaningfull info for these
    CtiMCSchedule& setTargetPaoID(const int target_id);
    CtiMCSchedule& setStartCommand(const std::string& start_command);
    CtiMCSchedule& setStopCommand(const std::string& stop_command);
    CtiMCSchedule& setRepeatInterval(long repeat_interval);

    // CGP 022802 DBMEMOBJ lost his monitor.
    CtiMutex& getMux()  { return _classMutex;}

    // For RW Streaming
    virtual void saveGuts(RWvostream &aStream) const;
    virtual void restoreGuts(RWvistream& aStream);


//debug
    bool checkSchedule() const;
    bool checkField(const std::string& fld, const std::string& msg) const;

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
