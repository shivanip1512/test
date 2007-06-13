#include "yukon.h"
#include "lmconstraint.h"

#include <algorithm>
#include <stdlib.h>

#include "dbaccess.h"
#include "lmprogramcontrolwindow.h"
#include "lmid.h"
#include "mgr_season.h"
#include "mgr_holiday.h"
#include "numstr.h"

#include "ctidate.h"
#include "ctitime.h"


extern ULONG _LM_DEBUG;

using std::vector;

CtiLMProgramConstraintChecker::CtiLMProgramConstraintChecker(CtiLMProgramDirect& lm_program, ULONG seconds_from_1901)
    : _lm_program(lm_program), _seconds_from_1901(seconds_from_1901)
{
}

/*
 * Checks all the constraints.
 */
bool CtiLMProgramConstraintChecker::checkConstraints(ULONG proposed_gear,
                                                     ULONG proposed_start_from_1901,
                                                     ULONG proposed_stop_from_1901)
{
    bool ret_val = true;
    ret_val = (checkManualProgramConstraints(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkGroupConstraints(proposed_gear, proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    return ret_val;
}

bool CtiLMProgramConstraintChecker::checkManualProgramConstraints(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    bool ret_val = true;
    ret_val = (checkSeason(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkWeekDays(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMasterActive() && ret_val);
    ret_val = (checkControlWindows(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkNotifyActiveOffset(proposed_start_from_1901) && ret_val);

    return ret_val;
}

bool CtiLMProgramConstraintChecker::checkAutomaticProgramConstraints(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    bool ret_val = true;
    ret_val = (checkSeason(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkWeekDays(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMasterActive() && ret_val);
    ret_val = (checkControlWindows(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);

    return ret_val;
}

bool CtiLMProgramConstraintChecker::checkGroupConstraints(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    bool ret_val = true;
    ret_val = (checkMaxHoursDaily(proposed_gear, proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMaxHoursMonthly(proposed_gear, proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMaxHoursSeasonal(proposed_gear, proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMaxHoursAnnually(proposed_gear, proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMinActivateTime(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    ret_val = (checkMinRestartTime(proposed_start_from_1901) && ret_val);
    ret_val = (checkMaxDailyOps() && ret_val);
    ret_val = (checkMaxActivateTime(proposed_start_from_1901, proposed_stop_from_1901) && ret_val);
    return ret_val;
}

/*
 * Checks if the given program is allowed to run during the given period.
 * proposed_start and proposed_stop are seconds from 1901, ala roguewave
 */
bool CtiLMProgramConstraintChecker::checkSeason(ULONG proposed_start_from_1901,
                                                ULONG proposed_stop_from_1901)
{
    if(_lm_program.getSeasonScheduleId() <= 0)
    {
        return true;
    }

    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit
    if(proposed_stop_from_1901 == gEndOfCtiTimeSeconds)
    {
        proposed_stop_from_1901 = proposed_start_from_1901;
    }

    CtiSeasonManager& seasonMgr = CtiSeasonManager::getInstance();

    CtiTime startTime(proposed_start_from_1901);
    CtiTime stopTime(proposed_stop_from_1901);
 
    CtiDate startDate(startTime);
    CtiDate stopDate(stopTime);

    do
    {
        if( !CtiSeasonManager::getInstance().isInSeason(startDate, _lm_program.getSeasonScheduleId()) )
        {
	    string result = "The program is not allowed to run outside of its' prescribed season schedule. ";
	    result += CtiTime(startTime).asString();
	    result += " is not in a season in schedule id: ";
	    result += CtiNumStr(_lm_program.getSeasonScheduleId());
	    _results.push_back(result);

            return false;
        }
    } while(++startDate <= stopDate);

    return true;
}

/*
 * Checks if the program is allowed to control based on the week day constraint
 */
bool CtiLMProgramConstraintChecker::checkWeekDays(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    bool violated = false;

    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit
    if(proposed_stop_from_1901 == gEndOfCtiTimeSeconds)
    {
        proposed_stop_from_1901 = proposed_start_from_1901;
    }
    
    const string& weekdays = _lm_program.getAvailableWeekDays();
    
    CtiTime startTime(proposed_start_from_1901);
    CtiTime stopTime(proposed_stop_from_1901);

    CtiDate startDate(startTime);
    CtiDate stopDate(stopTime);

    if(weekdays[(size_t)7] == 'Y' || weekdays[(size_t)7] == 'y')
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " <<  " Found 'Y' for the holiday slot in the available weekdays constraint for program: " << _lm_program.getPAOName() << "  F (force), E (exclude), N (no effect) - update the database and/or your database editor" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    bool force_holiday = true;
    if(weekdays[(size_t)7] == 'F' || weekdays[(size_t)7] == 'f')
    {
        do
        {
            if(!CtiHolidayManager::getInstance().isHoliday(startDate, _lm_program.getHolidayScheduleId()))
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
            bool is_holiday = CtiHolidayManager::getInstance().isHoliday(startDate, _lm_program.getHolidayScheduleId());
            if(is_holiday && (weekdays[(size_t)7] == 'E' || weekdays[(size_t)7] == 'e'))
            {
                result = "The program is not allowed to run on ";
                result += startDate.asString();
                result += ", which is a holiday";
                _results.push_back(result);

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
                result = "The program is not allowed to run on " + startDate.weekDayName();
                _results.push_back(result);

                violated = true;
                break;
            }
        } while(++startDate <= stopDate);
    }
    return !violated;
}

bool CtiLMProgramConstraintChecker::checkMaxHoursDaily(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    if(_lm_program.getMaxHoursDaily() == 0)
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60.0;//convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = (estimated_control_time + (double) lm_group->getCurrentHoursDaily()/60.0) - ((double) _lm_program.getMaxHoursDaily()/60.0);
        if( diff_minutes > 0)
        {
            string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum daily control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' maximum daily control hours: " + CtiNumStr(_lm_program.getMaxHoursDaily()/60.0/60.0);
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' current daily control hours: " + CtiNumStr((double)lm_group->getCurrentHoursDaily()/60.0/60.0);
            _results.push_back(result);

            violated = true;
        }
    }
    return !violated;
}
bool CtiLMProgramConstraintChecker::checkMaxHoursMonthly(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    if(_lm_program.getMaxHoursMonthly() == 0)
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60.0; //convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = estimated_control_time + lm_group->getCurrentHoursMonthly()/60.0 - _lm_program.getMaxHoursMonthly()/60.0;
        if( diff_minutes > 0)
        {
            string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum monthly control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' maximum monthly control hours: " + CtiNumStr(_lm_program.getMaxHoursMonthly()/60.0/60.0);
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' current monthly control hours: " + CtiNumStr((double)lm_group->getCurrentHoursMonthly()/60.0/60.0);
            _results.push_back(result);

            violated = true;
        }
    }
    return !violated;
}

bool CtiLMProgramConstraintChecker::checkMaxHoursSeasonal(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{    if(_lm_program.getMaxHoursSeasonal() == 0)
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60.0;//convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = estimated_control_time + lm_group->getCurrentHoursSeasonal()/60.0 - _lm_program.getMaxHoursSeasonal()/60.0;
        if( diff_minutes > 0)
        {
            string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum seasonal control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' maximum seasonal control hours: " + CtiNumStr(_lm_program.getMaxHoursSeasonal()/60.0/60.0);
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' current seasonal control hours: " + CtiNumStr((double)lm_group->getCurrentHoursSeasonal()/60.0/60.0);
            _results.push_back(result);

            violated = true;
        }
    }
    return !violated;
}

bool CtiLMProgramConstraintChecker::checkMaxHoursAnnually(ULONG proposed_gear, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{    if(_lm_program.getMaxHoursAnnually() == 0)
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start_from_1901,proposed_stop_from_1901)/60.0;//convert to minutes

        CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = estimated_control_time + lm_group->getCurrentHoursAnnually()/60.0 - _lm_program.getMaxHoursAnnually()/60.0;
        if( diff_minutes > 0)
        {
            string result = "load group, '" + lm_group->getPAOName() + "' would exceed its maximum annual control hours by an estimated " + CtiNumStr((double)diff_minutes/60.0) + " hours";

            _results.push_back(result);

            result = "load group, '" + lm_group->getPAOName() + "' maximum annual control hours: " + CtiNumStr(_lm_program.getMaxHoursAnnually()/60.0/60.0);
            _results.push_back(result);
            result = "load group, '" + lm_group->getPAOName() + "' current annual control hours: " + CtiNumStr((double)lm_group->getCurrentHoursAnnually()/60.0/60.0);

            _results.push_back(result);

            violated = true;
        }
    }
    return !violated;
}

/*
 * Check that the program will run at least the minactivatetime
 */
bool CtiLMProgramConstraintChecker::checkMinActivateTime(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    if(_lm_program.getMinActivateTime() == 0)
    {
        return true;
    }

    ULONG run_time = proposed_stop_from_1901 - proposed_start_from_1901;
    if(!(run_time >= _lm_program.getMinActivateTime()))
    {
        string result = "Load groups might be controlled less than their minimum activate time, which is " + CtiNumStr(_lm_program.getMinActivateTime()/60.0/60.0) + " hours.";

        _results.push_back(result);

        return false;
    }
    return true;
}

/*
 * Check that the program won't start again until min restart time has elapsed
 */
bool CtiLMProgramConstraintChecker::checkMinRestartTime(ULONG proposed_start_from_1901)
{
    if(_lm_program.getMinRestartTime() == 0)
    {
        return true;
    }

    bool found_violation = false;
    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        if(lm_group->getControlCompleteTime().seconds() + _lm_program.getMinRestartTime() > proposed_start_from_1901)
        {
            string result = "Load group: " + lm_group->getPAOName() + " might violate its minimum restart time, which is " + CtiNumStr((double)_lm_program.getMinRestartTime()/60.0/60.0) + " hours.";
            _results.push_back(result);
            found_violation = true;
        }
    }
    return !found_violation;
}

/*
 * Check that the program hasn't exceeded its max daily operations
 */
bool CtiLMProgramConstraintChecker::checkMaxDailyOps()
{
    if(_lm_program.getMaxDailyOps() == 0)
    {
        return true;
    }

    bool found_violation = false;
    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for(CtiLMGroupIter i = groups.begin(); i != groups.end(); i++)
    {
        CtiLMGroupPtr lm_group  = *i;
        if(lm_group->getDailyOps() >= _lm_program.getMaxDailyOps())
        {
            string result = "Load group: " +
                lm_group->getPAOName() +
                " has reached its maximum daily operations which is " + CtiNumStr(_lm_program.getMaxDailyOps());
            _results.push_back(result);
            found_violation = true;
        }
    }
    return !found_violation;
}

/*
 * Check that the program will run at least its max activate time
 */
bool CtiLMProgramConstraintChecker::checkMaxActivateTime(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    if(_lm_program.getMaxActivateTime() == 0)
    {
        return true;
    }

    ULONG run_time = proposed_stop_from_1901 - proposed_start_from_1901;
    if(!(run_time <= _lm_program.getMaxActivateTime()))
    {
        string result = "Load groups might control longer than their maximum activate time, which is " + CtiNumStr((double)_lm_program.getMaxActivateTime()/60.0/60.0) + " hours.";

        _results.push_back(result);

        return false;
    }
    return true;
}

bool CtiLMProgramConstraintChecker::checkControlWindows(ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    CtiLMProgramBase& lm_base = (CtiLMProgramBase&) _lm_program;
    if(lm_base.getLMProgramControlWindows().size() == 0 )// ||
//        lm_program.getControlType() == CtiLMProgramBase::TimedType)
    {  // No control windows so don't consider control windows
        return true;
    }

    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit
    if(proposed_stop_from_1901 == gEndOfCtiTimeSeconds)
    {
        proposed_stop_from_1901 = proposed_start_from_1901;
    }

    // We want the seconds at the beginning of the day in which the program is to start
    CtiTime startTime(proposed_start_from_1901);
    CtiDate startDate(startTime);
    startTime = CtiTime(startDate);

    CtiLMProgramControlWindow* start_ctrl_window = lm_base.getControlWindow(proposed_start_from_1901 - startTime.seconds());
    CtiLMProgramControlWindow* stop_ctrl_window = lm_base.getControlWindow(proposed_stop_from_1901 - startTime.seconds());

    if(start_ctrl_window != 0 && stop_ctrl_window != 0)
    {
        if(start_ctrl_window->getWindowNumber() == stop_ctrl_window->getWindowNumber())
        { //good
            return true;
        }
        else
        {
            _results.push_back("The program cannot run outside of its prescribed control windows.  The proposed start and stop times span different control windows");
            return false;
        }
    }

    if(start_ctrl_window == 0 && stop_ctrl_window == 0)
    { //start and stop outside any control windows
        _results.push_back("The program cannot run outside of its prescribed control windows");
        return false;
    }

    if(start_ctrl_window != 0 && stop_ctrl_window == 0)
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed stop time of ";
        result += CtiTime(proposed_stop_from_1901).asString();
        result += " is outside the control window that runs from ";
        result += CtiTime(start_ctrl_window->getAvailableStartTime() + startTime.seconds()).asString();
        result += " to ";
        result += CtiTime(start_ctrl_window->getAvailableStopTime() + startTime.seconds()).asString();
		_results.push_back(result);

        return false;
    }

    if(start_ctrl_window == 0 && stop_ctrl_window != 0)
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed start time of ";
        result += CtiTime(proposed_start_from_1901).asString();
        result += " is outside the control window that runs from ";
        result += CtiTime(stop_ctrl_window->getAvailableStartTime() + startTime.seconds()).asString();
        result += " to ";
        result += CtiTime(stop_ctrl_window->getAvailableStopTime() + startTime.seconds()).asString();
		_results.push_back(result);

        return false;
    }

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " **Checkpoint** " <<  " Shouldn't get here " << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return false;
}

bool CtiLMProgramConstraintChecker::checkControlAreaControlWindows(CtiLMControlArea &controlArea, ULONG proposed_start_from_1901, ULONG proposed_stop_from_1901)
{
    bool retVal = true;
    // We want the seconds at the beginning of the day in which the program is to start
    CtiTime startTime(proposed_start_from_1901);
    CtiDate startDate(startTime);
    startTime = CtiTime(startDate);

    LONG startSecondsFromDayBegin = proposed_start_from_1901 - startTime.seconds();
    LONG stopSecondsFromDayBegin = proposed_stop_from_1901 - startTime.seconds();

    if(controlArea.getCurrentDailyStartTime() == 0 && controlArea.getCurrentDailyStopTime() == 0)
    {
        retVal = true;
    }
    else if( proposed_start_from_1901 > proposed_stop_from_1901 )
    {
        string result = "The proposed start time of ";
        result += CtiTime(proposed_start_from_1901).asString();
        result += " is after the stop time of ";
        result += CtiTime(proposed_stop_from_1901).asString();
		_results.push_back(result);

        retVal = false;
    }
    else if( startSecondsFromDayBegin < controlArea.getCurrentDailyStartTime() )
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed start time of ";
        result += CtiTime(proposed_start_from_1901).asString();
        result += " is outside the CONTROL AREA control window that runs from ";
        result += CtiTime(controlArea.getCurrentDailyStartTime() + startTime.seconds()).asString();
        result += " to ";
        result += CtiTime(controlArea.getCurrentDailyStopTime() + startTime.seconds()).asString();
		_results.push_back(result);

        retVal = false;
    }
    else if( controlArea.getCurrentDailyStopTime() < controlArea.getCurrentDailyStartTime() && stopSecondsFromDayBegin > (controlArea.getCurrentDailyStopTime() + 24*60*60) )
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed stop time of ";
        result += CtiTime(proposed_start_from_1901).asString();
        result += " is outside the CONTROL AREA control window that runs from ";
        result += CtiTime(controlArea.getCurrentDailyStartTime() + startTime.seconds()).asString();
        result += " to ";
        result += CtiTime(controlArea.getCurrentDailyStopTime() + 24*60*60 + startTime.seconds()).asString();
		_results.push_back(result);

        retVal = false;
    }
    else if( controlArea.getCurrentDailyStopTime() > controlArea.getCurrentDailyStartTime() && stopSecondsFromDayBegin > controlArea.getCurrentDailyStopTime() )
    {
        string result = "The program cannot run outside of its prescribed control windows.  The proposed stop time of ";
        result += CtiTime(proposed_start_from_1901).asString();
        result += " is outside the CONTROL AREA control window that runs from ";
        result += CtiTime(controlArea.getCurrentDailyStartTime() + startTime.seconds()).asString();
        result += " to ";
        result += CtiTime(controlArea.getCurrentDailyStopTime() + startTime.seconds()).asString();
		_results.push_back(result);

        retVal = false;
    }

    return retVal;
}

/*
 * Check that the program is starting before the notify active offset.
 * If the notify active offset is 60 minutes and the program is asked to start in 50 mintes we are violating this constraint
 */
bool CtiLMProgramConstraintChecker::checkNotifyActiveOffset(ULONG proposed_start_from_1901)
{
    if(_lm_program.getNotifyActiveOffset() == CtiLMProgramDirect::invalidNotifyOffset)
    {   // there is no notify active offset
        return true;
    }

    if(proposed_start_from_1901 < _seconds_from_1901)
    {
        proposed_start_from_1901 = _seconds_from_1901;
    }

    if((proposed_start_from_1901 - _seconds_from_1901) < _lm_program.getNotifyActiveOffset())
    {
        string result = "The program cannot start at the proposed start time of ";
        result += CtiTime(proposed_start_from_1901).asString();
        result += " because that is only ";
        result += CtiNumStr((proposed_start_from_1901 - _seconds_from_1901)/60.0);
        result += " minutes from now and the program's notification offset (notify active offset) is set to ";
        result += CtiNumStr(_lm_program.getNotifyActiveOffset() / 60.0);
        result += " minutes.";
        _results.push_back(result);

        return false;
    }
    else
    {
        return true;
    }
}

bool CtiLMProgramConstraintChecker::checkMasterActive()
{
    bool master_active = false;
    std::set<CtiLMProgramDirectSPtr>& master_set = ((CtiLMProgramDirect&)_lm_program).getMasterPrograms();
    
    for(std::set<CtiLMProgramDirectSPtr>::iterator master_iter = master_set.begin();
        master_iter != master_set.end();
        master_iter++)
    {
        if((*master_iter)->getProgramState() != CtiLMProgramBase::InactiveState)
        {
            string result = "The program cannot start since its master program, ";
            result += (*master_iter)->getPAOName();
            result += " is active";
            _results.push_back(result);

            master_active = true;
        }
    }
    return !master_active;
}

const vector<string>& CtiLMProgramConstraintChecker::getViolations()
{
    return _results;
}

void CtiLMProgramConstraintChecker::clearViolations()
{
    _results.clear();
}

void CtiLMProgramConstraintChecker::dumpViolations()
{
    if(_results.size() == 0)
    {
        return;
    }

    CtiLockGuard<CtiLogger> dout_guard(dout);
    dout << CtiTime() << " Program: " << _lm_program.getPAOName() << " constraint violations: " << endl;
    for(std::vector<string>::iterator iter = _results.begin(); iter != _results.end(); iter++)
    {
        dout << CtiTime() << "  " << *iter << endl;
    }
}

CtiLMGroupConstraintChecker::CtiLMGroupConstraintChecker(CtiLMProgramBase& lm_program, CtiLMGroupPtr& lm_group, ULONG seconds_from_1901)
    : _lm_program(lm_program), _lm_group(lm_group), _seconds_from_1901(seconds_from_1901)
{ }


/*------------------------------------------------------------------------------
 * Checks the constraints of a group just prior to a control being sent.
 */
bool CtiLMGroupConstraintChecker::checkControl(LONG& control_duration, bool adjust_duration)
{
    bool ret_val = true;

    // Maximum activate
    ret_val = checkMaxActivateTime(control_duration, adjust_duration) && ret_val;
    // Minimum restart
    ret_val = checkMinRestartTime() && ret_val;
    // Daily Ops
    ret_val = checkMaxDailyOps() && ret_val;
    // Daily Control Hours
    ret_val = checkMaxHoursDaily(control_duration, adjust_duration) && ret_val;
    // Monthly Control hours
    ret_val = checkMaxHoursMonthly(control_duration, adjust_duration) && ret_val;
    // Seasonal Control hours
    ret_val = checkMaxHoursSeasonal(control_duration, adjust_duration) && ret_val;
    // Annual Control hours
    ret_val = checkMaxHoursAnnually(control_duration, adjust_duration) && ret_val;

    ret_val = checkProgramControlWindow(control_duration, adjust_duration) && ret_val;

    return ret_val;
}

bool CtiLMGroupConstraintChecker::checkCycle(LONG& counts, ULONG period, ULONG percent, bool adjust_counts)
{
    if( percent > 100 )
    {
        percent = 100;
    }
    LONG control_duration = (period * (((double) percent/100.0)) * counts);
    LONG full_duration = period * counts;

    if(!checkControl(control_duration, adjust_counts) || !checkProgramControlWindow(full_duration, adjust_counts) )
    {
        return false;
    }
    else
    {
        // checkControl might have reduced the duration - convert it back to counts
        // Also checkProgramControlWindow can change the duration.
        if(period != 0 && percent != 0)
        {
            counts = (LONG) ::ceil((control_duration*(100.0/((double)percent))) / (double) period);
            long tempCount = ::ceil(full_duration / (double) period);

            if(tempCount < counts)
            {
                counts = tempCount;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " **Checkpoint** " << __FILE__ << "(" << __LINE__ << ")" << " tried to divide by zero! Not modifying counts" << endl;
        }
        return true;
    }
}

bool CtiLMGroupConstraintChecker::checkRestore()
{
    // Just make sure our minimum activate time has passed
    return checkMinActivateTime();
}

bool CtiLMGroupConstraintChecker::checkTerminate()
{
    // Just make sure our minimum activate time has passed
    return checkMinActivateTime();
}

bool CtiLMGroupConstraintChecker::checkEnabled()
{
    if( _lm_group->getDisableFlag() == FALSE )
    {
        return true;
    }
    else
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " is disabled.";
        _results.push_back(result);
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxActivateTime(LONG& control_duration, bool adjust_duration)
{
    if(!checkDurationConstraint(_lm_group->getCurrentControlDuration(),
                                _lm_program.getMaxActivateTime(),
                                control_duration,
                                adjust_duration))
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " maximum activate violation.";
        _results.push_back(result);
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkMinActivateTime()
{
    if( _lm_program.getMinActivateTime() == 0 ||
        _lm_group->getCurrentControlDuration() >= _lm_program.getMinActivateTime())
    {
        return true;
    }
    else
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " minimum activate violation.";
        _results.push_back(result);
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMinRestartTime()
{
    if( _lm_program.getMinRestartTime() == 0 ||
        _seconds_from_1901 - _lm_group->getControlCompleteTime().seconds() >= _lm_program.getMinRestartTime())
    {
        return true;
    }
    else
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " minimum restart violation.";
        _results.push_back(result);
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxDailyOps()
{
    if( _lm_program.getMaxDailyOps() == 0 ||
        _lm_group->getDailyOps() < _lm_program.getMaxDailyOps() ||
        (_lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState && _lm_group->getDailyOps() <= _lm_program.getMaxDailyOps()))
    {
        return true;
    }
    else
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " max daily ops violation.";
        _results.push_back(result);
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursDaily(LONG& control_duration, bool adjust_duration)
{
    if(checkDurationConstraint(_lm_group->getCurrentHoursDaily(),
                               _lm_program.getMaxHoursDaily(),
                               control_duration,
                               adjust_duration))
    {
        return true;
    }
    else
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " max hours daily violation.";
        _results.push_back(result);
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursMonthly(LONG& control_duration, bool adjust_duration)
{
    if(!checkDurationConstraint(_lm_group->getCurrentHoursMonthly(),
                                _lm_program.getMaxHoursMonthly(),
                                control_duration,
                                adjust_duration))
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " max hours monthly violation.";
        _results.push_back(result);
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursSeasonal(LONG& control_duration, bool adjust_duration)
{
    if(!checkDurationConstraint(_lm_group->getCurrentHoursSeasonal(),
                                _lm_program.getMaxHoursSeasonal(),
                                control_duration,
                                adjust_duration))
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " max hours seasonal  violation.";
        _results.push_back(result);
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursAnnually(LONG& control_duration, bool adjust_duration)
{
    if(!checkDurationConstraint(_lm_group->getCurrentHoursAnnually(),
                                _lm_program.getMaxHoursAnnually(),
                                control_duration,
                                adjust_duration))
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " max hours annually violation.";
        _results.push_back(result);
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkProgramControlWindow(LONG& control_duration, bool adjust_duration)
{
    // If there are no control windows then i guess you can't violate any of them
    if(_lm_program.getLMProgramControlWindows().size() == 0 )
    {
        return true;
    }
	
    CtiTime now_t(_seconds_from_1901);
    CtiTime now_dt(now_t); // move this calc to a util function at least
    
    LONG seconds_from_beginning_of_today = (now_dt.hour() * 3600) + (now_dt.minute() * 60) + now_dt.second();

    CtiLMProgramControlWindow* control_window = _lm_program.getControlWindow(seconds_from_beginning_of_today);
    if(control_window == 0)
    {
        string result = "Load Group: " + _lm_group->getPAOName() + " not in program control window.";
        _results.push_back(result);
        return false;
    }

    int left_in_window = control_window->getAvailableStopTime() - seconds_from_beginning_of_today;

    if(left_in_window > 0)
    {
        if(adjust_duration)
        {
            control_duration = std::min((ULONG) control_duration, (ULONG) left_in_window);
            return true;
        }
        else
        {
            if(control_duration >= left_in_window)
            {
                string result = "Load Group: " + _lm_group->getPAOName() + " not enough control time left in program control window.";
                _results.push_back(result);
                return false;
            }
            else
            {
                return true;
            }
        }
    }
    else
    {
        return false;
    }
}

void CtiLMGroupConstraintChecker::dumpViolations()
{
    if(_results.size() == 0)
    {
        return;
    }

    CtiLockGuard<CtiLogger> dout_guard(dout);
    dout << CtiTime() << " Load Group: " << _lm_group->getPAOName() << " constraint violations: " << endl;
    for(std::vector<string>::iterator iter = _results.begin(); iter != _results.end(); iter++)
    {
        dout << CtiTime() << "  " << *iter << endl;
    }
}

bool CtiLMGroupConstraintChecker::checkDurationConstraint(LONG current_duration, LONG max_duration, LONG& control_duration, bool adjust_duration)
{
    // 0 - no check
    if(max_duration == 0)
    {
        return true;
    }
    ULONG total_duration = current_duration + control_duration;

    if(total_duration > max_duration)
    {
        if(adjust_duration &&
           max_duration > current_duration) //Cannot adjust the control time negative!
        {
            control_duration = max_duration - current_duration;
            return true;
        }
        else
        {
            return false;
        }
    }
    return true;
}
