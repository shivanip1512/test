#pragma once

#include "lmprogramdirect.h"
#include "lmcontrolarea.h"
#include "lmutility.h"
#include "lmmessage.h"

#include "ConstraintViolation.h"

class CtiLMProgramConstraintChecker
{
public:

    CtiLMProgramConstraintChecker(CtiLMProgramDirect& lm_program, CtiTime current_time);

    const std::vector<std::string>& getResults() const;
    const std::vector<ConstraintViolation>& getViolations();

    boost::optional<std::string> dumpViolations();

    bool checkConstraints(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop);
    bool checkManualProgramConstraints(CtiTime proposed_start, CtiTime proposed_stop);
    bool checkAutomaticProgramConstraints(CtiTime proposed_start, CtiTime proposed_stop);
    bool checkGroupConstraints(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop);
    bool checkManualGearChangeConstraints(ULONG proposed_gear, ULONG proposed_stop_seconds);

    bool checkSeason(CtiTime proposed_start, CtiTime proposed_stop);
    bool checkWeekDays(CtiTime proposed_start, CtiTime proposed_stop);
    bool checkControlWindows(CtiTime proposed_start, CtiTime proposed_stop);
    bool checkControlAreaControlWindows(CtiLMControlArea &controlArea, CtiTime proposed_start, CtiTime proposed_stop, const CtiDate &theDate);
    bool checkMasterActive();
    bool checkNotifyActiveOffset(CtiTime proposed_start);

    // Group related constraings
    bool checkMaxHoursDaily(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop);
    bool checkMaxHoursMonthly(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop);
    bool checkMaxHoursSeasonal(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop);
    bool checkMaxHoursAnnually(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop);
    bool checkMinActivateTime(CtiTime proposed_start, CtiTime proposed_stop);
    bool checkMinRestartTime(CtiTime proposed_start);
    bool checkMaxDailyOps();
    bool checkMaxActivateTime(CtiTime proposed_start, CtiTime proposed_stop);

//    bool checkWeekDays(const CtiLMProgram

    CtiLMProgramConstraintChecker* getInstance();
private:

    ULONG estimateGroupControlTime(CtiLMGroupBase& lm_group, CtiLMProgramDirectGear& lm_gear, ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop, std::vector<std::string>* results = 0);

    CtiLMProgramDirect& _lm_program;
    CtiTime _current_time;
    std::vector<std::string> _results;
    std::vector<ConstraintViolation> _constraintViolations;
};

class CtiLMGroupConstraintChecker
{
public:

    CtiLMGroupConstraintChecker(CtiLMProgramBase& lm_program, CtiLMGroupPtr& lm_group, CtiTime current_time);

    boost::optional<std::string> dumpViolations();

    bool checkControl(LONG& control_duration, bool adjust_duration);
    bool checkCycle(LONG& counts, ULONG period, ULONG percent, bool adjust_counts);
    bool checkRestore();
    bool checkTerminate();

    bool checkEnabled();
    bool checkMaxActivateTime(LONG& control_duration, bool adjust_duration=false);
    bool checkMinActivateTime();
    bool checkMinRestartTime();
    bool checkMaxDailyOps();
    bool checkMaxHoursDaily(LONG& control_duration, bool adjust_duration = false);
    bool checkMaxHoursMonthly(LONG& control_duration, bool adjust_duration = false);
    bool checkMaxHoursSeasonal(LONG& control_duration, bool adjust_duration = false);
    bool checkMaxHoursAnnually(LONG& control_duration, bool adjust_duration = false);
    bool checkProgramControlWindow(LONG& control_duration, bool adjust_duration = false);
    bool checkControlAreaControlWindow(CtiLMControlArea* controlArea, LONG& control_duration, bool adjust_duration = false);

private:
    bool checkDurationConstraint(LONG current_duration, LONG max_duration, LONG& control_duration, bool adjust_duration);
    CtiLMProgramBase& _lm_program;
    CtiLMGroupPtr& _lm_group;
    CtiTime _current_time;

    std::vector<std::string> _results;
    std::vector<ConstraintViolation> _constraintViolations;
};
