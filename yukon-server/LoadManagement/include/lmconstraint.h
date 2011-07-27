#ifndef __CTILMCONSTRAINT_H__
#define __CTILMCONSTRAINT_H__

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <vector>
#include <string>

#include "lmprogramdirect.h"
#include "lmcontrolarea.h"
#include "lmutility.h"
#include "lmmessage.h"

#include "ConstraintViolation.h"

class CtiLMProgramConstraintChecker
{
public:

    CtiLMProgramConstraintChecker(CtiLMProgramDirect& lm_program, ULONG seconds_from_1901);

    const std::vector<std::string>& getResults();

    const std::vector<ConstraintViolation>& getViolations();
    void clearViolations();

    void dumpViolations();
    
    bool checkConstraints(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkManualProgramConstraints(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkAutomaticProgramConstraints(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);    
    bool checkGroupConstraints(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkManualGearChangeConstraints(ULONG proposed_gear, ULONG proposed_stop_seconds);
    
    bool checkSeason(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkWeekDays(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkControlWindows(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkControlAreaControlWindows(CtiLMControlArea &controlArea, ULONG proposed_start_from_epoch, ULONG proposed_stop_from_epoch, const CtiDate &theDate);
    bool checkMasterActive();
    bool checkNotifyActiveOffset(ULONG proposed_start_from_1901);
    
    // Group related constraings
    bool checkMaxHoursDaily(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkMaxHoursMonthly(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkMaxHoursSeasonal(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkMaxHoursAnnually(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkMinActivateTime(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);
    bool checkMinRestartTime(ULONG proposed_start_from_1901);
    bool checkMaxDailyOps();
    bool checkMaxActivateTime(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901);

//    bool checkWeekDays(const CtiLMProgram

    CtiLMProgramConstraintChecker* getInstance();
private:

    ULONG estimateGroupControlTime(CtiLMGroupBase& lm_group, CtiLMProgramDirectGear& lm_gear, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, std::vector<std::string>* results = 0);

    CtiLMProgramDirect& _lm_program;
    ULONG _seconds_from_1901;
    std::vector<std::string> _results;
    std::vector<ConstraintViolation> _constraintViolations;
};

class CtiLMGroupConstraintChecker
{
public:

    CtiLMGroupConstraintChecker(CtiLMProgramBase& lm_program, CtiLMGroupPtr& lm_group, ULONG control_start_from_1901); 

    const std::vector<std::string>& getViolations();
    void clearViolations();

    void dumpViolations();

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
    ULONG _seconds_from_1901;
    
    std::vector<std::string> _results;
    std::vector<ConstraintViolation> _constraintViolations;
};
#endif
