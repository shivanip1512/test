#include "lmconstraint.h"

#include <algorithm>

#include "lmid.h"
#include "mgr_season.h"
#include "numstr.h"

extern ULONG _LM_DEBUG;

CtiLMConstraintChecker::CtiLMConstraintChecker()
{    
}

/*
 * Checks all the constraints.
 */
bool CtiLMConstraintChecker::checkConstraints(const CtiLMProgramDirect& lm_program,
					      ULONG proposed_gear,
					      ULONG proposed_start_from_1901,
					      ULONG proposed_stop_from_1901,
					      vector<string>& results)
{
    bool ret_val = true;
    
    ret_val = (checkSeason(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkWeekDays(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMaxHoursDaily(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMaxHoursMonthly(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMaxHoursSeasonal(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMaxHoursAnnually(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMinActivateTime(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMinRestartTime(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMaxDailyOps(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMaxActivateTime(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);

    if( _LM_DEBUG & LM_DEBUG_CONSTRAINTS )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
	if(!ret_val)
	{
	    dout << RWTime() << " Failed at least one constraint: " << endl;
	    for(vector<string>::iterator iter = results.begin();
		iter != results.end();
		iter++)
	    {
		dout << RWTime() << *iter << endl;
	    }
	}
    }
    
    return ret_val;
}

/*
 * Checks if the given program is enabled.
 */
/*bool checkAblement(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results = 0)
{
    return (!lm_program.getDisableFlag());
}*/

/*
 * Checks if the given program is allowed to run during the given period.
 * proposed_start and proposed_stop are seconds from 1901, ala roguewave
 */
bool CtiLMConstraintChecker::checkSeason(const CtiLMProgramDirect& lm_program,
					 ULONG proposed_gear,
					 ULONG proposed_start_from_1901,
					 ULONG proposed_stop_from_1901,
					 vector<string>* results)
{    
    CtiSeasonManager& seasonMgr = CtiSeasonManager::getInstance();
    const RWCString& availableSeasons = lm_program.getAvailableSeasons();

    RWTime startTime(proposed_start_from_1901);
    RWTime stopTime(proposed_stop_from_1901);

    RWDate startDate(startTime);
    RWDate stopDate(stopTime);

    while(startDate++ <= stopDate)
    {
	long season = CtiSeasonManager::getInstance().getCurrentSeason(startDate, lm_program.getSeasonScheduleId());
	if(availableSeasons[(size_t)season] != 'Y' && availableSeasons[(size_t)season] != 'y')
	{
	    string result = "The program is not allowed to run in season " + CtiNumStr(season);
	    results->push_back(result);
	    return false;
	}
    }

    return true;
}

/*
 * Checks if the program is allowed to control based on the week day constraint
 */
bool CtiLMConstraintChecker::checkWeekDays(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    bool violated = false;
    const RWCString& weekdays = lm_program.getAvailableWeekDays();
    
    RWTime startTime(proposed_start_from_1901);
    RWTime stopTime(proposed_stop_from_1901);

    RWDate startDate(startTime);
    RWDate stopDate(stopTime);

    do
    {
	int week_day = startDate.weekDay(); 
	if(week_day == 7) //yukon is 0-6, sunday-sat, rw is 1-7, monday-sunday
	{
	    week_day = 0;
	}
	if(weekdays[(size_t)week_day] != 'Y' && weekdays[(size_t)week_day] != 'y')
	{
	    string result = "The program is not allowed to run on " + startDate.weekDayName();
	    results->push_back(result);
	    violated = true;
	    break;
	}
    } while(++startDate <= stopDate);

    return !violated;
}

bool CtiLMConstraintChecker::checkMaxHoursDaily(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    if(lm_program.getMaxHoursDaily() == 0)
    {
	return true;
    }
    
    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60;//convert to minutes
    
    RWOrdered lm_groups = ((CtiLMProgramDirect&)lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(int i = 0; i < lm_groups.entries(); i++)
    {
	CtiLMGroupBase* lm_group = (CtiLMGroupBase*) lm_groups[i];
	int diff_minutes = estimated_control_time + lm_group->getCurrentHoursDaily()/60 - lm_program.getMaxHoursDaily()*60;
	if( diff_minutes > 0)
	{
	    string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum daily control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' maximum daily control hours: " + CtiNumStr(lm_program.getMaxHoursDaily());
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' current daily control hours: " + CtiNumStr((double)lm_group->getCurrentHoursDaily()/60.0/60.0);
	    results->push_back(result);
	    violated = true;
	}
    }
    return !violated;
}
bool CtiLMConstraintChecker::checkMaxHoursMonthly(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    
    if(lm_program.getMaxHoursMonthly() == 0)
    {
	return true;
    }
    
    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60; //convert to minutes

    RWOrdered lm_groups = ((CtiLMProgramDirect&)lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(int i = 0; i < lm_groups.entries(); i++)
    {
	CtiLMGroupBase* lm_group = (CtiLMGroupBase*) lm_groups[i];
	int diff_minutes = estimated_control_time + lm_group->getCurrentHoursMonthly()/60 - lm_program.getMaxHoursMonthly()*60;
	if( diff_minutes > 0)
	{
	    string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum monthly control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' maximum monthly control hours: " + CtiNumStr(lm_program.getMaxHoursMonthly());
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' current monthly control hours: " + CtiNumStr((double)lm_group->getCurrentHoursMonthly()/60.0/60.0);
	    results->push_back(result);
	    violated = true;
	}
    }
    return !violated;
}

bool CtiLMConstraintChecker::checkMaxHoursSeasonal(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{    if(lm_program.getMaxHoursSeasonal() == 0)
    {
	return true;
    }
    
    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60;//convert to minutes

    RWOrdered lm_groups = ((CtiLMProgramDirect&)lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(int i = 0; i < lm_groups.entries(); i++)
    {
	CtiLMGroupBase* lm_group = (CtiLMGroupBase*) lm_groups[i];
	int diff_minutes = estimated_control_time + lm_group->getCurrentHoursSeasonal()/60 - lm_program.getMaxHoursSeasonal()*60;
	if( diff_minutes > 0)
	{
	    string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum seasonal control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' maximum seasonal control hours: " + CtiNumStr(lm_program.getMaxHoursSeasonal());
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' current seasonal control hours: " + CtiNumStr((double)lm_group->getCurrentHoursSeasonal()/60.0/60.0);
	    results->push_back(result);
	    violated = true;
	}
    }
    return !violated;
}

bool CtiLMConstraintChecker::checkMaxHoursAnnually(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{    if(lm_program.getMaxHoursAnnually() == 0)
    {
	return true;
    }
    
    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60;//convert to minutes

    RWOrdered lm_groups = ((CtiLMProgramDirect&)lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(int i = 0; i < lm_groups.entries(); i++)
    {
	CtiLMGroupBase* lm_group = (CtiLMGroupBase*) lm_groups[i];
	int diff_minutes = estimated_control_time + lm_group->getCurrentHoursAnnually()/60 - lm_program.getMaxHoursAnnually()*60;
	if( diff_minutes > 0)
	{
	    string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum annual control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' maximum annual control hours: " + CtiNumStr(lm_program.getMaxHoursAnnually());
	    results->push_back(result);
	    result = "load group, '" + lm_group->getPAOName() + "' current annual control hours: " + CtiNumStr((double)lm_group->getCurrentHoursAnnually()/60.0/60.0);
	    results->push_back(result);
	    violated = true;
	}
    }
    return !violated;
}

/*
 * Check that the program will run at least the minactivatetime
 */
bool CtiLMConstraintChecker::checkMinActivateTime(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    if(lm_program.getMinActivateTime() == 0)
    {
	return true;
    }
    
    ULONG run_time = proposed_stop_from_1901 - proposed_start_from_1901;
    if(!(run_time >= lm_program.getMinActivateTime()))
    {
	string result = "The program cannot run for less than its minimum activate time, which is " + CtiNumStr(lm_program.getMinActivateTime()/60.0/60.0) + " hours.";
	results->push_back(result);
	return false;
    }
    return true;
}

/*
 * Check that the program won't start again until min restart time has elapsed
 */
bool CtiLMConstraintChecker::checkMinRestartTime(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    if(lm_program.getMinRestartTime() == 0)
    {
	return true;
    }
    
    RWOrdered lm_groups = ((CtiLMProgramDirect&)lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(int i = 0; i < lm_groups.entries(); i++)
    {
	CtiLMGroupBase* lm_group = (CtiLMGroupBase*) lm_groups[i];
	if(lm_group->getControlCompleteTime().seconds() + lm_program.getMinRestartTime() > proposed_start_from_1901)
	{
	    string result = "The program cannot control again until its minimum restart time, which is " + CtiNumStr((double)lm_program.getMinRestartTime()/60.0/60.0) + " hours, has elapsed since control last completed.";
	    results->push_back(result);
	    return false;
	}
    }
    return true;
}

/*
 * Check that the program hasn't exceeded its max daily operations
 */
bool CtiLMConstraintChecker::checkMaxDailyOps(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results )
{
    if(lm_program.getMaxDailyOps() == 0)
    {
	return true;
    }

    if(((CtiLMProgramDirect&)lm_program).getDailyOps() >= lm_program.getMaxDailyOps())
    {
	string result = "The program has reached its maximum daily operations, which is " + CtiNumStr(lm_program.getMaxDailyOps());
	results->push_back(result);
	return false;
    }
    
    return true;
}

/*
 * Check that the program will run at least its max activate time
 */
bool CtiLMConstraintChecker::checkMaxActivateTime(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    if(lm_program.getMaxActivateTime() == 0)
    {
	return true;
    }
    
    ULONG run_time = proposed_stop_from_1901 - proposed_start_from_1901;
    if(!(run_time <= lm_program.getMaxActivateTime()))
    {
	string result = "The program cannot run for more than its maximum activate time, which is " + CtiNumStr((double)lm_program.getMaxActivateTime()/60.0/60.0) + " hours.";
	results->push_back(result);
	return false;
    }
    return true;
}
