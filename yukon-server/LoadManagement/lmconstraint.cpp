#include "lmconstraint.h"

#include <algorithm>

#include "lmprogramcontrolwindow.h"
#include "lmid.h"
#include "mgr_season.h"
#include "mgr_holiday.h"
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
    ret_val = (checkControlWindows(lm_program, proposed_gear, proposed_start_from_1901, proposed_stop_from_1901, &results) && ret_val);
    ret_val = (checkMasterActive(lm_program, &results) && ret_val);
    
    if( _LM_DEBUG & LM_DEBUG_CONSTRAINTS )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        if(!ret_val && lm_program.getControlType() != CtiLMProgramBase::TimedType)
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
    if(lm_program.getSeasonScheduleId() <= 0)
    {
        return true;
    }

    CtiSeasonManager& seasonMgr = CtiSeasonManager::getInstance();

    RWTime startTime(proposed_start_from_1901);
    RWTime stopTime(proposed_stop_from_1901);
 
    RWDate startDate(startTime);
    RWDate stopDate(stopTime);

    do
    {
        if(!CtiSeasonManager::getInstance().isInSeason(startDate, lm_program.getSeasonScheduleId()))
        {
            if(results != 0)
            {
                string result = "The program is not allowed to run outside of its' prescribed season schedule. ";
                result += RWDate(startTime).asString();
                result += " is not in a season in schedule id: ";
                result += CtiNumStr(lm_program.getSeasonScheduleId());
                results->push_back(result);
            }
            return false;
        }
    } while(startDate++ <= stopDate);

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

    if(weekdays[(size_t)7] == 'Y' || weekdays[(size_t)7] == 'y')
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  " Found 'Y' for the holiday slot in the available weekdays constraint for program: " << lm_program.getPAOName() << "  F (force), E (exclude), N (no effect) - update the database and/or your database editor" << __FILE__ << "(" << __LINE__ << ")" << endl;
        dout << RWTime() << " Cowardly deciding that holidays have no effect on this program." << endl;
    }

    bool force_holiday = true;
    if(weekdays[(size_t)7] == 'F' || weekdays[(size_t)7] == 'f')
    {
        do
        {
            if(!CtiHolidayManager::getInstance().isHoliday(startDate, lm_program.getHolidayScheduleId())) 
            {   // The entire time the program is to run isn't a holiday,
                // make sure to do a normal week day check
                force_holiday = false;
                break;
            }
        } while(++startDate <= stopDate);
    }

    if( !((weekdays[(size_t)7] == 'F' || weekdays[(size_t)7] == 'f') && force_holiday))
    {
        string result;
        do
        {
            bool is_holiday = CtiHolidayManager::getInstance().isHoliday(startDate, lm_program.getHolidayScheduleId());
            if(is_holiday && (weekdays[(size_t)7] == 'E' || weekdays[(size_t)7] == 'e'))
            {
                if(results != 0)
                {
                    result = "The program is not allowed to run on ";
                    result += startDate.asString();
                    result += ", which is a holiday";
                    results->push_back(result);
                }
                violated = true;
                break;
            }

            int week_day = startDate.weekDay(); 
            if(week_day == 7) //yukon is 0-6, sunday-sat, rw is 1-7, monday-sunday
            {
                week_day = 0;
            }
            if(weekdays[(size_t)week_day] != 'Y' && weekdays[(size_t)week_day] != 'y')
            {
                if(results != 0)
                {
                    result = "The program is not allowed to run on " + startDate.weekDayName();
                    results->push_back(result);
                }
                violated = true;
                break;
            }
        } while(++startDate <= stopDate);
    }
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
            if(results != 0)
            {
                string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum daily control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
                results->push_back(result);
                result = "load group, '" + lm_group->getPAOName() + "' maximum daily control hours: " + CtiNumStr(lm_program.getMaxHoursDaily());
                results->push_back(result);
                result = "load group, '" + lm_group->getPAOName() + "' current daily control hours: " + CtiNumStr((double)lm_group->getCurrentHoursDaily()/60.0/60.0);
                results->push_back(result);
            }
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
            if(results != 0)
            {
                string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum monthly control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
                results->push_back(result);
                result = "load group, '" + lm_group->getPAOName() + "' maximum monthly control hours: " + CtiNumStr(lm_program.getMaxHoursMonthly());
                results->push_back(result);
                result = "load group, '" + lm_group->getPAOName() + "' current monthly control hours: " + CtiNumStr((double)lm_group->getCurrentHoursMonthly()/60.0/60.0);
                results->push_back(result);
            }
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
            if(results != 0)
            {
                string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum seasonal control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
                results->push_back(result);
                result = "load group, '" + lm_group->getPAOName() + "' maximum seasonal control hours: " + CtiNumStr(lm_program.getMaxHoursSeasonal());
                results->push_back(result);
                result = "load group, '" + lm_group->getPAOName() + "' current seasonal control hours: " + CtiNumStr((double)lm_group->getCurrentHoursSeasonal()/60.0/60.0);
                results->push_back(result);
            }
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
            if(results != 0)
            {
                results->push_back(result);
            }
            result = "load group, '" + lm_group->getPAOName() + "' maximum annual control hours: " + CtiNumStr(lm_program.getMaxHoursAnnually());
            results->push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' current annual control hours: " + CtiNumStr((double)lm_group->getCurrentHoursAnnually()/60.0/60.0);
            if(results != 0)
            {
                results->push_back(result);
            }
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
        if(results != 0)
        {
            results->push_back(result);
        }
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
            if(results != 0)
            {
                results->push_back(result);
            }
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
        if(results != 0)
        {
            results->push_back(result);
        }
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
        if(results != 0)
        {
            results->push_back(result);
        }
        return false;
    }
    return true;
}

bool CtiLMConstraintChecker::checkControlWindows(const CtiLMProgramDirect& lm_program, ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901, vector<string>* results)
{
    CtiLMProgramBase& lm_base = (CtiLMProgramBase&) lm_program;
    if(lm_base.getLMProgramControlWindows().entries() == 0 ||
        lm_program.getControlType() == CtiLMProgramBase::TimedType)
    {  // No control windows so don't consider control windows
        return true;
    }

    RWTime today(RWDate());

    CtiLMProgramControlWindow* start_ctrl_window = lm_base.getControlWindow(proposed_start_from_1901 - today.seconds());
    CtiLMProgramControlWindow* stop_ctrl_window = lm_base.getControlWindow(proposed_stop_from_1901 - today.seconds());
      
    if(start_ctrl_window != 0 && stop_ctrl_window != 0)
    {
        if(start_ctrl_window->getWindowNumber() == stop_ctrl_window->getWindowNumber())
        { //good
            return true;
        }
        else
        {
            if(results != 0)
            {
                results->push_back("The program cannot run outside of its prescribed control windows.  The proposed start and stop times span different control windows");
            }
            return false;
        }
    }
    
    if(start_ctrl_window == 0 && stop_ctrl_window == 0)
    { //start and stop outside any control windows
        if(results != 0)
        {
            results->push_back("The program cannot run outside of its prescribed control windows");
        }
        return false;
    }

    if(start_ctrl_window != 0 && stop_ctrl_window == 0)
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed stop time of ";
        result += RWTime(proposed_stop_from_1901).asString();
        result += " is outside the control window that runs from ";
        result += RWTime(start_ctrl_window->getAvailableStartTime() + today.seconds()).asString();
        result += " to ";
        result += RWTime(start_ctrl_window->getAvailableStopTime() + today.seconds()).asString();
        if(results != 0)
        {
            results->push_back(result);
        }
        return false;
    }

    if(start_ctrl_window == 0 && stop_ctrl_window != 0)
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed start time of ";
        result += RWTime(proposed_start_from_1901).asString();
        result += " is outside the control window that runs from ";
        result += RWTime(stop_ctrl_window->getAvailableStartTime() + today.seconds()).asString();
        result += " to ";
        result += RWTime(stop_ctrl_window->getAvailableStopTime() + today.seconds()).asString();
        if(results != 0)
        {
            results->push_back(result);
        }
        return false;
    }

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  " Shouldn't get here " << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return false;
}

bool CtiLMConstraintChecker::checkMasterActive(const CtiLMProgramDirect& lm_program, vector<string>* results)
{
    bool master_active = false;
    set<CtiLMProgramDirect*>& master_set = ((CtiLMProgramDirect&)lm_program).getMasterPrograms();
    
    for(set<CtiLMProgramDirect*>::iterator master_iter = master_set.begin();
        master_iter != master_set.end();
        master_iter++)
    {
        if((*master_iter)->getProgramState() != CtiLMProgramBase::InactiveState)
        {
            string result = "The program cannot since its master program, ";
            result += (*master_iter)->getPAOName();
            result += " is active";
            if(results != 0)
            {
                results->push_back(result);
            }
            master_active = true;
        }
    }
    return !master_active;
}
