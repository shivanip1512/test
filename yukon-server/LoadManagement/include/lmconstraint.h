#ifndef __CTILMCONSTRAINT_H__
#define __CTILMCONSTRAINT_H__

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <vector>
#include <string>

#include "lmprogramdirect.h"

using std::vector;
using std::string;

class CtiLMConstraintChecker
{
public:
    CtiLMConstraintChecker();

    bool checkConstraints(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>& results);

    bool checkAblement(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkSeason(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkWeekDays(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMaxHoursDaily(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMaxHoursMonthly(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMaxHoursSeasonal(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMaxHoursAnnually(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMinActivateTime(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMinRestartTime(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMaxDailyOps(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
    bool checkMaxActivateTime(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
 
    

//    bool checkWeekDays(const CtiLMProgram

    CtiLMConstraintChecker* getInstance();
private:

    ULONG estimateGroupControlTime(const CtiLMGroupBase& lm_group, const CtiLMProgramDirectGear& lm_gear, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0);
};

#endif
