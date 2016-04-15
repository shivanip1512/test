#include "precompiled.h"
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

#include <boost/algorithm/string/join.hpp>

extern ULONG _LM_DEBUG;

using std::vector;
using std::string;
using std::endl;

CtiLMProgramConstraintChecker::CtiLMProgramConstraintChecker(CtiLMProgramDirect& lm_program, CtiTime current_time)
: _lm_program(lm_program), _current_time(current_time)
{
}

/*
 * Checks all the constraints.
 */
bool CtiLMProgramConstraintChecker::checkConstraints(ULONG proposed_gear,
                                                     CtiTime proposed_start,
                                                     CtiTime proposed_stop)
{
    bool ret_val = true;
    ret_val = (checkManualProgramConstraints(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkGroupConstraints(proposed_gear, proposed_start, proposed_stop) && ret_val);
    return ret_val;
}

bool CtiLMProgramConstraintChecker::checkManualProgramConstraints(CtiTime proposed_start, CtiTime proposed_stop)
{
    bool ret_val = true;
    ret_val = (checkSeason(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkWeekDays(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMasterActive() && ret_val);
    ret_val = (checkControlWindows(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkNotifyActiveOffset(proposed_start) && ret_val);

    return ret_val;
}

bool CtiLMProgramConstraintChecker::checkAutomaticProgramConstraints(CtiTime proposed_start, CtiTime proposed_stop)
{
    bool ret_val = true;
    ret_val = (checkSeason(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkWeekDays(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMasterActive() && ret_val);
    ret_val = (checkControlWindows(proposed_start, proposed_stop) && ret_val);

    return ret_val;
}

bool CtiLMProgramConstraintChecker::checkGroupConstraints(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop)
{
    bool ret_val = true;
    ret_val = (checkMaxHoursDaily(proposed_gear, proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMaxHoursMonthly(proposed_gear, proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMaxHoursSeasonal(proposed_gear, proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMaxHoursAnnually(proposed_gear, proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMinActivateTime(proposed_start, proposed_stop) && ret_val);
    ret_val = (checkMinRestartTime(proposed_start) && ret_val);
    ret_val = (checkMaxDailyOps() && ret_val);
    ret_val = (checkMaxActivateTime(proposed_start, proposed_stop) && ret_val);
    return ret_val;
}

/*
 * Checks if the given program is allowed to run during the given period.
 * proposed_start and proposed_stop
 */
bool CtiLMProgramConstraintChecker::checkSeason(CtiTime proposed_start,
                                                CtiTime proposed_stop)
{
    if( _lm_program.getSeasonScheduleId() <= 0 )
    {
        return true;
    }

    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit
    if( proposed_stop == gEndOfCtiTime )
    {
        proposed_stop = proposed_start;
    }

    CtiSeasonManager& seasonMgr = CtiSeasonManager::getInstance();

    CtiTime startTime(proposed_start);
    CtiTime stopTime(proposed_stop);

    CtiDate startDate(startTime);
    CtiDate stopDate(stopTime);

    do
    {
        if( !CtiSeasonManager::getInstance().isInAnySeason(startDate, _lm_program.getSeasonScheduleId()) )
        {
            string result = "The program is not allowed to run outside of its prescribed season schedule. ";
            result += CtiTime(startTime).asString();
            result += " is not in a season in schedule id: ";
            result += CtiNumStr(_lm_program.getSeasonScheduleId());
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TI_OutsideSeasonSchedule,
                                                                CtiTime(startTime),
                                                                _lm_program.getSeasonScheduleId()));

            return false;
        }
    } while( ++startDate <= stopDate );

    return true;
}

/*
 * Checks if the program is allowed to control based on the week day constraint
 */
bool CtiLMProgramConstraintChecker::checkWeekDays(CtiTime proposed_start, CtiTime proposed_stop)
{
    bool violated = false;

    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit
    if( proposed_stop == gEndOfCtiTime )
    {
        proposed_stop = proposed_start;
    }

    const string& weekdays = _lm_program.getAvailableWeekDays();

    CtiTime startTime(proposed_start);
    CtiTime stopTime(proposed_stop);

    CtiDate startDate(startTime);
    CtiDate stopDate(stopTime);

    if( weekdays[(size_t)7] == 'Y' || weekdays[(size_t)7] == 'y' )
    {
        CTILOG_ERROR(dout, "Found 'Y' for the holiday slot in the available weekdays constraint for program: " << _lm_program.getPAOName() << "  F (force), E (exclude), N (no effect) - update the database and/or your database editor");
    }

    bool force_holiday = true;
    if( weekdays[(size_t)7] == 'F' || weekdays[(size_t)7] == 'f' )
    {
        do
        {
            if( !CtiHolidayManager::getInstance().isHoliday(startDate, _lm_program.getHolidayScheduleId()) )   // The entire time the program is to run isn't a holiday,
            {
                // make sure to do a normal week day check
                force_holiday = false;
                break;
            }
        } while( ++startDate <= stopDate );
    }

    if( !((weekdays[(size_t)7] == 'F' || weekdays[(size_t)7] == 'f') && force_holiday) )
    {
        string result;
        do
        {
            bool is_holiday = CtiHolidayManager::getInstance().isHoliday(startDate, _lm_program.getHolidayScheduleId());
            if( is_holiday && (weekdays[(size_t)7] == 'E' || weekdays[(size_t)7] == 'e') )
            {
                result = "The program is not allowed to run on ";
                result += startDate.asString();
                result += ", which is a holiday";
                _results.push_back(result);

                _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_T_ProhibitedHolidayRun,
                                                                    CtiTime(startDate)));

                violated = true;
                break;
            }

            int week_day = startDate.weekDay();
            if( week_day == 7 ) //yukon is 0-6, sunday-sat, rw is 1-7, monday-sunday
            {
                week_day = 0;
            }
            if( weekdays[(size_t)week_day] != 'Y' && weekdays[(size_t)week_day] != 'y' )
            {
                result = "The program is not allowed to run on " + startDate.weekDayName();
                _results.push_back(result);

                _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_ProhibitedWeekdayRun,
                                                                    startDate.weekDayName()));

                violated = true;
                break;
            }
        } while( ++startDate <= stopDate );
    }
    return !violated;
}

bool CtiLMProgramConstraintChecker::checkMaxHoursDaily(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop)
{
    if( _lm_program.getMaxHoursDaily() == 0 )
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start,proposed_stop)/60.0;//convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = (estimated_control_time + (double) lm_group->getCurrentHoursDaily()/60.0) - ((double) _lm_program.getMaxHoursDaily()/60.0);
        if( diff_minutes > 0 )
        {
            string paoName  = lm_group->getPAOName();
            double numHours = (double)diff_minutes/60.0;
            double maxControlHours     = _lm_program.getMaxHoursDaily()/60.0/60.0;
            double currentControlHours = (double)lm_group->getCurrentHoursDaily()/60.0/60.0;

            string result = "load group, '" + paoName + "' would exceed its maximum daily control hours by an estimated " + CtiNumStr(numHours) + " hours";
            _results.push_back(result);
            result = "load group, '" + paoName + "' maximum daily control hours: " + CtiNumStr(maxControlHours);
            _results.push_back(result);
            result = "load group, '" + paoName + "' current daily control hours: " + CtiNumStr(currentControlHours);
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHours,
                                                                paoName,
                                                                numHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHoursMsg2,
                                                                paoName,
                                                                maxControlHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededDailyControlHoursMsg3,
                                                                paoName,
                                                                currentControlHours));

            violated = true;
        }
    }
    return !violated;
}
bool CtiLMProgramConstraintChecker::checkMaxHoursMonthly(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop)
{
    if( _lm_program.getMaxHoursMonthly() == 0 )
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start,proposed_stop)/60.0; //convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = estimated_control_time + lm_group->getCurrentHoursMonthly()/60.0 - _lm_program.getMaxHoursMonthly()/60.0;
        if( diff_minutes > 0 )
        {
            string paoName  = lm_group->getPAOName();
            double numHours = (double)diff_minutes/60.0;
            double maxControlHours     = _lm_program.getMaxHoursMonthly()/60.0/60.0;
            double currentControlHours = (double)lm_group->getCurrentHoursMonthly()/60.0/60.0;

            string result = "load group, '" + paoName + "' would exceed its maximum monthly control hours by an estimated " + CtiNumStr(numHours) + " hours";
            _results.push_back(result);
            result = "load group, '" + paoName + "' maximum monthly control hours: " + CtiNumStr(maxControlHours);
            _results.push_back(result);
            result = "load group, '" + paoName + "' current monthly control hours: " + CtiNumStr(currentControlHours);
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHours,
                                                                paoName,
                                                                numHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHoursMsg2,
                                                                paoName,
                                                                maxControlHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededMonthlyControlHoursMsg3,
                                                                paoName,
                                                                currentControlHours));

            violated = true;
        }
    }
    return !violated;
}

bool CtiLMProgramConstraintChecker::checkMaxHoursSeasonal(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop)
{    if( _lm_program.getMaxHoursSeasonal() == 0 )
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start,proposed_stop)/60.0;//convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = estimated_control_time + lm_group->getCurrentHoursSeasonal()/60.0 - _lm_program.getMaxHoursSeasonal()/60.0;
        if( diff_minutes > 0 )
        {
            string paoName  = lm_group->getPAOName();
            double numHours = (double)diff_minutes/60.0;
            double maxControlHours     = _lm_program.getMaxHoursSeasonal()/60.0/60.0;
            double currentControlHours = (double)lm_group->getCurrentHoursSeasonal()/60.0/60.0;

            string result = "load group, '" + paoName + "' would exceed its maximum seasonal control hours by an estimated " + CtiNumStr(numHours) + " hours";
            _results.push_back(result);
            result = "load group, '" + paoName + "' maximum seasonal control hours: " + CtiNumStr(maxControlHours);
            _results.push_back(result);
            result = "load group, '" + paoName + "' current seasonal control hours: " + CtiNumStr(currentControlHours);
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHours,
                                                                paoName,
                                                                numHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHoursMsg2,
                                                                paoName,
                                                                maxControlHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededSeasonalControlHoursMsg3,
                                                                paoName,
                                                                currentControlHours));

            violated = true;
        }
    }
    return !violated;
}

bool CtiLMProgramConstraintChecker::checkMaxHoursAnnually(ULONG proposed_gear, CtiTime proposed_start, CtiTime proposed_stop)
{    if( _lm_program.getMaxHoursAnnually() == 0 )
    {
        return true;
    }

    bool violated = false;
    unsigned int estimated_control_time = ((CtiLMProgramDirect&)_lm_program).estimateOffTime(proposed_gear, proposed_start,proposed_stop)/60.0;//convert to minutes

    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        int diff_minutes = estimated_control_time + lm_group->getCurrentHoursAnnually()/60.0 - _lm_program.getMaxHoursAnnually()/60.0;
        if( diff_minutes > 0 )
        {
            string paoName  = lm_group->getPAOName();
            double numHours = (double)diff_minutes/60.0;
            double maxControlHours     = _lm_program.getMaxHoursAnnually()/60.0/60.0;
            double currentControlHours = (double)lm_group->getCurrentHoursAnnually()/60.0/60.0;

            string result = "load group, '" + paoName + "' would exceed its maximum annual control hours by an estimated " + CtiNumStr(numHours) + " hours";
            _results.push_back(result);
            result = "load group, '" + paoName + "' maximum annual control hours: " + CtiNumStr(maxControlHours);
            _results.push_back(result);
            result = "load group, '" + paoName + "' current annual control hours: " + CtiNumStr(currentControlHours);
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHours,
                                                                paoName,
                                                                numHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHoursMsg2,
                                                                paoName,
                                                                maxControlHours));
            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_ExceededAnnualControlHoursMsg3,
                                                                paoName,
                                                                currentControlHours));

            violated = true;
        }
    }
    return !violated;
}

/*
 * Check that the program will run at least the minactivatetime
 */
bool CtiLMProgramConstraintChecker::checkMinActivateTime(CtiTime proposed_start, CtiTime proposed_stop)
{
    if( _lm_program.getMinActivateTime() == 0 )
    {
        return true;
    }

    ULONG run_time = proposed_stop.seconds() - proposed_start.seconds();
    if( !(run_time >= _lm_program.getMinActivateTime()) )
    {
        double numHours = (double)_lm_program.getMinActivateTime()/60.0/60.0;

        string result = "Load groups might be controlled less than their minimum activate time, which is " + CtiNumStr(numHours) + " hours.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_D_ControlledLessThanMinimum,
                                                            numHours));

        return false;
    }
    return true;
}

/*
 * Check that the program won't start again until min restart time has elapsed
 */
bool CtiLMProgramConstraintChecker::checkMinRestartTime(CtiTime proposed_start)
{
    if( _lm_program.getMinRestartTime() == 0 )
    {
        return true;
    }

    bool found_violation = false;
    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        if( lm_group->getControlCompleteTime() + _lm_program.getMinRestartTime() > proposed_start )
        {
            string paoName  = lm_group->getPAOName();
            double numHours = (double)_lm_program.getMinRestartTime()/60.0/60.0;

            string result = "Load group: " + paoName + " might violate its minimum restart time, which is " + CtiNumStr(numHours) + " hours.";
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SD_MinRestartTimeViolation,
                                                                paoName,
                                                                numHours));

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
    if( _lm_program.getMaxDailyOps() == 0 )
    {
        return true;
    }

    bool found_violation = false;
    CtiLMGroupVec groups  = ((CtiLMProgramDirect&)_lm_program).getLMProgramDirectGroups(); //cast away const, oooh
    for( CtiLMGroupIter i = groups.begin(); i != groups.end(); i++ )
    {
        CtiLMGroupPtr lm_group  = *i;
        if( lm_group->getDailyOps() >= _lm_program.getMaxDailyOps() )
        {
            string paoName = lm_group->getPAOName();
            double maxOps  = _lm_program.getMaxDailyOps();

            string result = "Load group: " + paoName + " has reached its maximum daily operations which is " + CtiNumStr(maxOps);
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_SI_MaximumDailyOperationsReached,
                                                                paoName,
                                                                maxOps));

            found_violation = true;
        }
    }
    return !found_violation;
}

/*
 * Check that the program will run at least its max activate time
 */
bool CtiLMProgramConstraintChecker::checkMaxActivateTime(CtiTime proposed_start, CtiTime proposed_stop)
{
    if( _lm_program.getMaxActivateTime() == 0 )
    {
        return true;
    }

    ULONG run_time = proposed_stop.seconds() - proposed_start.seconds();
    if( !(run_time <= _lm_program.getMaxActivateTime()) )
    {
        double numHours = (double)_lm_program.getMaxActivateTime()/60.0/60.0;

        string result = "Load groups might control longer than their maximum activate time, which is " + CtiNumStr(numHours) + " hours.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_D_ControlledMoreThanMaximum,
                                                            numHours));

        return false;
    }
    return true;
}

bool CtiLMProgramConstraintChecker::checkControlWindows(CtiTime proposed_start, CtiTime proposed_stop)
{
    CtiLMProgramBase& lm_base = (CtiLMProgramBase&) _lm_program;
    if( lm_base.getLMProgramControlWindows().size() == 0 )// ||
    //        lm_program.getControlType() == CtiLMProgramBase::TimedType)  // No control windows so don't consider control windows
    {
        return true;
    }

    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit
    if( proposed_stop == gEndOfCtiTime )
    {
        proposed_stop = proposed_start;
    }

    CtiTime proposedStartTime(proposed_start),
            proposedStopTime(proposed_stop);

    CtiDate proposedDate(proposedStartTime);

    CtiLMProgramControlWindow *start_ctrl_window = 0,
                              *stop_ctrl_window  = 0;

    for each ( CtiLMProgramControlWindow * window in lm_base.getLMProgramControlWindows() )
    {
        CtiTime windowStart = window->getAvailableStartTime(proposedDate),
                windowStop  = window->getAvailableStopTime(proposedDate);

        if ( start_ctrl_window == 0 )
        {
            if ( ( windowStart <= proposedStartTime ) && ( proposedStartTime <= windowStop ) )
            {
                start_ctrl_window = window;
            }
        }

        if ( stop_ctrl_window == 0 )
        {
            if ( ( windowStart <= proposedStopTime ) && ( proposedStopTime <= windowStop ) )
            {
                stop_ctrl_window = window;
            }
        }
    }

    if( start_ctrl_window != 0 && stop_ctrl_window != 0 )
    {
        if( start_ctrl_window->getWindowNumber() == stop_ctrl_window->getWindowNumber() ) //good
        {
            return true;
        }
        else
        {
            string result = "The program cannot run outside of its prescribed control windows.  The proposed start and stop times span different control windows";
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_NP_ProposedTimesSpanMultipleWindows));

            return false;
        }
    }

    if( start_ctrl_window == 0 && stop_ctrl_window == 0 ) //start and stop outside any control windows
    {
        string result = "The program cannot run outside of its prescribed control windows";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_NP_CannotRunOutsideControlWindows));

        return false;
    }

    if( start_ctrl_window != 0 && stop_ctrl_window == 0 )
    {
        CtiTime startTime = start_ctrl_window->getAvailableStartTime();
        CtiTime stopTime  = start_ctrl_window->getAvailableStopTime();

        string result = "The program cannot run outside of its prescribed control windows.  The proposed stop time of ";
        result += proposedStopTime.asString();
        result += " is outside the control window that runs from ";
        result += startTime.asString();
        result += " to ";
        result += stopTime.asString();
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_StopTimeOutsideControlWindow,
                                                            proposedStopTime,
                                                            startTime,
                                                            stopTime));

        return false;
    }

    if( start_ctrl_window == 0 && stop_ctrl_window != 0 )
    {
        CtiTime startTime = stop_ctrl_window->getAvailableStartTime();
        CtiTime stopTime  = stop_ctrl_window->getAvailableStopTime();

        string result = "The program cannot run outside of its prescribed control windows.  The proposed start time of ";
        result += proposedStartTime.asString();
        result += " is outside the control window that runs from ";
        result += startTime.asString();
        result += " to ";
        result += stopTime.asString();
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_StartTimeOutsideControlWindow,
                                                            proposedStartTime,
                                                            startTime,
                                                            stopTime));

        return false;
    }

    CTILOG_ERROR(dout, "Shouldn't get here ");
    return false;
}

bool CtiLMProgramConstraintChecker::checkControlAreaControlWindows(CtiLMControlArea &controlArea, CtiTime proposed_start, CtiTime proposed_stop, const CtiDate &theDate)
{
    // The stop time could be in the infinate future, in that case
    // I guess we want to ignore the problem ... (auto control reduceprogramload does this)
    // This _could_ be messing up "run forever" manual control .... checkit

    if( proposed_stop == gEndOfCtiTime )
    {
        proposed_stop = proposed_start;
    }

    CtiTime controlAreaStartTime = controlArea.getCurrentDailyStartTime(theDate);
    CtiTime controlAreaStopTime = controlArea.getCurrentDailyStopTime(theDate);

    if( controlAreaStartTime.is_special() || controlAreaStopTime.is_special() ||
        controlAreaStartTime == controlAreaStopTime )
    {
        return true;
    }

    const CtiTime proposedStartTime(proposed_start);
    const CtiTime proposedStopTime(proposed_stop);

    if( proposedStartTime > proposedStopTime )
    {
        string result = "The proposed start time of ";
        result += proposedStartTime.asString();
        result += " is after the stop time of ";
        result += proposedStopTime.asString();
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TT_ProposedStartAfterStop,
                                                            proposedStartTime,
                                                            proposedStopTime));

        return false;
    }



    // If our control area window and target window start on different dates, calculate the difference between them
    //  and slide our control window so it lines up on the same day as our target window.

    if( controlAreaStartTime.date() != proposedStartTime.date() )
    {
        int dayOffset = proposedStartTime.date().daysFrom1970() - controlAreaStartTime.date().daysFrom1970();

        controlAreaStartTime.addDays(dayOffset);
        controlAreaStopTime.addDays(dayOffset);
    }

    if( controlAreaStartTime.date() == controlAreaStopTime.date() )     // control area window doesn't overlap midnight
    {
        if( proposedStartTime < controlAreaStartTime || proposedStartTime > controlAreaStopTime )
        {
            string result = ControlWindowErrorMessage(controlAreaStartTime, controlAreaStopTime, proposedStartTime, "start", "CONTROL AREA");
            _results.push_back( result );

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStartTimeSameDate,
                                                                proposedStartTime,
                                                                controlAreaStartTime,
                                                                controlAreaStopTime));

            return false;
        }

        if( proposedStopTime < controlAreaStartTime || proposedStopTime > controlAreaStopTime )
        {
            string result = ControlWindowErrorMessage(controlAreaStartTime, controlAreaStopTime, proposedStopTime, "stop", "CONTROL AREA");
            _results.push_back( result );

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeSameDate,
                                                                proposedStopTime,
                                                                controlAreaStartTime,
                                                                controlAreaStopTime));

            return false;
        }
    }
    else    // control area window overlaps midnight
    {
        CtiTime todayControlAreaStopTime = controlAreaStopTime;
        todayControlAreaStopTime.addDays(-1);
        CtiTime yesterdayControlAreaStartTime = controlAreaStartTime;
        yesterdayControlAreaStartTime.addDays(-1);

        if( todayControlAreaStopTime < proposedStartTime && proposedStartTime < controlAreaStartTime )
        {
            string result = ControlWindowErrorMessage(controlAreaStartTime, controlAreaStopTime, proposedStartTime, "start", "CONTROL AREA");
            _results.push_back( result );

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStartTimeOverMidnight,
                                                                proposedStartTime,
                                                                controlAreaStartTime,
                                                                controlAreaStopTime));

            return false;
        }

        if( proposedStartTime <= todayControlAreaStopTime && proposedStopTime > todayControlAreaStopTime )
        {
            string result = ControlWindowErrorMessage(yesterdayControlAreaStartTime, todayControlAreaStopTime, proposedStopTime, "stop", "CONTROL AREA");
            _results.push_back( result );

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeOverMidnight,
                                                                proposedStopTime,
                                                                yesterdayControlAreaStartTime,
                                                                todayControlAreaStopTime));

            return false;
        }

        if( proposedStartTime >= controlAreaStartTime && (proposedStopTime < controlAreaStartTime || proposedStopTime > controlAreaStopTime) )
        {
            string result = ControlWindowErrorMessage(controlAreaStartTime, controlAreaStopTime, proposedStopTime, "stop", "CONTROL AREA");
            _results.push_back( result );

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TTT_InvalidProposedCAStopTimeOverMidnight,
                                                                proposedStopTime,
                                                                controlAreaStartTime,
                                                                controlAreaStopTime));

            return false;
        }
    }

    return true;
}

/*
 * Check that the program is starting before the notify active offset.
 * If the notify active offset is 60 minutes and the program is asked to start in 50 mintes we are violating this constraint
 */
bool CtiLMProgramConstraintChecker::checkNotifyActiveOffset(CtiTime proposed_start)
{
    if( _lm_program.getNotifyActiveOffset() == CtiLMProgramDirect::invalidNotifyOffset )   // there is no notify active offset
    {
        return true;
    }

    if( proposed_start < _current_time )
    {
        proposed_start = _current_time;
    }

    if( (proposed_start.seconds() - _current_time.seconds()) < _lm_program.getNotifyActiveOffset() )
    {
        double minutesFromNow = (proposed_start.seconds() - _current_time.seconds())/60.0;
        double offsetMinutes  = _lm_program.getNotifyActiveOffset() / 60.0;
        CtiTime proposedTime  = CtiTime(proposed_start);

        string result = "The program cannot start at the proposed start time of ";
        result += proposedTime.asString();
        result += " because that is only ";
        result += CtiNumStr(minutesFromNow);
        result += " minutes from now and the program's notification offset (notify active offset) is set to ";
        result += CtiNumStr(offsetMinutes);
        result += " minutes.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TDD_ProposedStartTooSoon,
                                                            proposedTime,
                                                            minutesFromNow,
                                                            offsetMinutes));

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

    for( std::set<CtiLMProgramDirectSPtr>::iterator master_iter = master_set.begin();
       master_iter != master_set.end();
       master_iter++ )
    {
        if( (*master_iter)->getProgramState() != CtiLMProgramBase::InactiveState )
        {
            string paoName = (*master_iter)->getPAOName();

            string result = "The program cannot start since its master program, ";
            result += paoName;
            result += ", is active";
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_MasterProgramActive,
                                                                paoName));

            master_active = true;
        }
    }
    return !master_active;
}

const vector<ConstraintViolation>& CtiLMProgramConstraintChecker::getViolations()
{
    return _constraintViolations;
}

boost::optional<std::string> CtiLMProgramConstraintChecker::dumpViolations()
{
    if( _results.empty() )
    {
        return boost::none;
    }

    return "Program: " + _lm_program.getPAOName() + " constraint violations: " + boost::join(_results, " / ");
}

CtiLMGroupConstraintChecker::CtiLMGroupConstraintChecker(CtiLMProgramBase& lm_program, CtiLMGroupPtr& lm_group, CtiTime current_time)
: _lm_program(lm_program), _lm_group(lm_group), _current_time(current_time)
{}


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

    if( !checkControl(control_duration, adjust_counts) || !checkProgramControlWindow(full_duration, adjust_counts) )
    {
        return false;
    }
    else
    {
        // checkControl might have reduced the duration - convert it back to counts
        // Also checkProgramControlWindow can change the duration.
        if( period != 0 && percent != 0 )
        {
            counts = (LONG) ::ceil((control_duration*(100.0/((double)percent))) / (double) period);
            long tempCount = ::ceil(full_duration / (double) period);

            if( tempCount < counts )
            {
                counts = tempCount;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "tried to divide by zero! Not modifying counts");
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
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " is disabled.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupDisabled, paoName));
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxActivateTime(LONG& control_duration, bool adjust_duration)
{
    if( !checkDurationConstraint(_lm_group->getCurrentControlDuration(),
                                 _lm_program.getMaxActivateTime(),
                                 control_duration,
                                 adjust_duration) )
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " maximum activate violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxActivateViolation, paoName));
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkMinActivateTime()
{
    if( _lm_program.getMinActivateTime() == 0 ||
        _lm_group->getCurrentControlDuration() >= _lm_program.getMinActivateTime() )
    {
        return true;
    }
    else
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " minimum activate violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMinActivateViolation, paoName));
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMinRestartTime()
{
    if( _lm_program.getMinRestartTime() == 0 ||
        _current_time.seconds() - _lm_group->getControlCompleteTime().seconds() >= _lm_program.getMinRestartTime() )
    {
        return true;
    }
    else
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " minimum restart violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMinRestartViolation, paoName));
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxDailyOps()
{
    if( _lm_program.getMaxDailyOps() == 0 ||
        _lm_group->getDailyOps() < _lm_program.getMaxDailyOps() ||
        (_lm_group->getGroupControlState() == CtiLMGroupBase::ActiveState && _lm_group->getDailyOps() <= _lm_program.getMaxDailyOps()) )
    {
        return true;
    }
    else
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " max daily ops violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxDailyOpsViolation, paoName));
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursDaily(LONG& control_duration, bool adjust_duration)
{
    if( checkDurationConstraint(_lm_group->getCurrentHoursDaily(),
                                _lm_program.getMaxHoursDaily(),
                                control_duration,
                                adjust_duration) )
    {
        return true;
    }
    else
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " max hours daily violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxDailyHoursViolation, paoName));
    }
    return false;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursMonthly(LONG& control_duration, bool adjust_duration)
{
    if( !checkDurationConstraint(_lm_group->getCurrentHoursMonthly(),
                                 _lm_program.getMaxHoursMonthly(),
                                 control_duration,
                                 adjust_duration) )
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " max hours monthly violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxMonthlyHoursViolation, paoName));
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursSeasonal(LONG& control_duration, bool adjust_duration)
{
    if( !checkDurationConstraint(_lm_group->getCurrentHoursSeasonal(),
                                 _lm_program.getMaxHoursSeasonal(),
                                 control_duration,
                                 adjust_duration) )
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " max hours seasonal  violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxSeasonalHoursViolation, paoName));
        return false;
    }
    return true;
}

bool CtiLMGroupConstraintChecker::checkMaxHoursAnnually(LONG& control_duration, bool adjust_duration)
{
    if( !checkDurationConstraint(_lm_group->getCurrentHoursAnnually(),
                                 _lm_program.getMaxHoursAnnually(),
                                 control_duration,
                                 adjust_duration) )
    {
        string paoName = _lm_group->getPAOName();

        string result = "Load Group: " + paoName + " max hours annually violation.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupMaxAnnualHoursViolation, paoName));
        return false;
    }
    return true;
}

/****************************************************************
* checkControlAreaControlWindow

    Function checks the stop time of the control area ONLY and
    can adjust the stop time to fit the control window.
    This is not the same as checkProgramControlWindow
**/
bool CtiLMGroupConstraintChecker::checkControlAreaControlWindow(CtiLMControlArea* controlArea, LONG& control_duration, bool adjust_duration)
{
    bool retVal = false;

    CtiTime controlStart; // now
    CtiTime controlEnd(controlStart.seconds() + control_duration);
    CtiDate today; //only used to create today begin
    CtiDate yesterday;
    --yesterday;
    CtiTime todayBegin(today);
    CtiTime yesterdayBegin(yesterday);
    CtiTime controlAreaStart, controlAreaStop;

    if( controlArea != NULL )
    {
        if( controlArea->getCurrentDailyStartTime().is_special() || controlArea->getCurrentDailyStopTime().is_special() ||
          ( controlArea->getCurrentDailyStartTime() == controlArea->getCurrentDailyStopTime()) )
        {
            retVal = true;
        }
        else
        {
            if( controlArea->getCurrentDailyStopTime() < controlArea->getCurrentDailyStartTime() )
            {
                //backwords case
                CtiDate tomorrow;
                ++tomorrow;
                controlAreaStart = controlArea->getCurrentDailyStartTime();
                controlAreaStop = controlArea->getCurrentDailyStopTime().addDays(1);
                if( controlAreaStart < controlStart )
                {
                    //We are in the wrong 24 hour period, shift back 1 day
                    controlAreaStart = controlArea->getCurrentDailyStartTime().addDays(-1);
                    controlAreaStop = controlArea->getCurrentDailyStopTime();
                }
            }
            else
            {
                controlAreaStart = controlArea->getCurrentDailyStartTime();
                controlAreaStop = controlArea->getCurrentDailyStopTime();
            }

            if( controlStart >= controlAreaStart && controlEnd <= controlAreaStop )//were good to go
            {
                retVal = true;
            }
            else
            {
                if( adjust_duration && controlAreaStop > controlStart )
                {
                    control_duration = controlAreaStop.seconds() - controlStart.seconds();
                    retVal = true;
                }
                else
                {
                    string paoName = _lm_group->getPAOName();

                    string result = "Load Group: " + paoName + " not able to control in control area window.";
                    _results.push_back(result);
                    result = "CA start: " + controlAreaStart.asString() + " CA stop: " + controlAreaStop.asString() + " duration ";
                    result += CtiNumStr(control_duration) + " adjust: ";
                    result += adjust_duration ? "Y" : "N";
                    _results.push_back(result);
                    retVal = false;

                    _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupCannotControlInWindow,
                                                                        paoName));

                    ConstraintViolation::CV_Type_TimeTimeInt code = adjust_duration ? ConstraintViolation::CV_TTI_LoadGroupCannotControlInWindowAdjust : ConstraintViolation::CV_TTI_LoadGroupCannotControlInWindowNoAdjust;
                    _constraintViolations.push_back(ConstraintViolation(code,
                                                                        controlAreaStart,
                                                                        controlAreaStop,
                                                                        control_duration));
                }
            }
        }
    }


    return retVal;
}

bool CtiLMGroupConstraintChecker::checkProgramControlWindow(LONG& control_duration, bool adjust_duration)
{
    bool retVal = false;
    // If there are no control windows then i guess you can't violate any of them
    if( _lm_program.getLMProgramControlWindows().size() == 0 )
    {
        retVal = true;
    }
    else
    {
        LONG seconds_from_beginning_of_today = (_current_time.hour() * 3600) + (_current_time.minute() * 60) + _current_time.second();

        CtiLMProgramControlWindow* control_window = _lm_program.getControlWindow(seconds_from_beginning_of_today);
        if( control_window == 0 )
        {
            string result = "Load Group: " + _lm_group->getPAOName() + " not in program control window.";
            _results.push_back(result);

            _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupNotInProgramControlWindow, _lm_group->getPAOName()));
            retVal = false;
        }
        else
        {

            CtiTime currentTime(0,0,0);
            currentTime.addSeconds(seconds_from_beginning_of_today);
            int left_in_window = control_window->getAvailableStopTime().seconds() - currentTime.seconds();

            if( left_in_window > 0 )
            {
                if( adjust_duration )
                {
                    control_duration = std::min((ULONG) control_duration, (ULONG) left_in_window);
                    retVal = true;
                }
                else
                {
                    if( control_duration >= left_in_window )
                    {
                        string paoName = _lm_group->getPAOName();

                        string result = "Load Group: " + paoName + " not enough control time left in program control window.";
                        _results.push_back(result);

                        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_S_LoadGroupNotEnoughTimeLeftInWindow,
                                                                            paoName));
                        retVal = false;
                    }
                    else
                    {
                        retVal = true;
                    }
                }
            }
            else
            {
                retVal = false;
            }
        }
    }

    return retVal && checkControlAreaControlWindow(_lm_program.getControlArea(), control_duration, adjust_duration);
}

boost::optional<std::string> CtiLMGroupConstraintChecker::dumpViolations()
{
    if( _results.empty() )
    {
        return boost::none;
    }

    return "Load Group: " + _lm_group->getPAOName() + " constraint violations: " + boost::join(_results, " / ");
}

bool CtiLMGroupConstraintChecker::checkDurationConstraint(LONG current_duration, LONG max_duration, LONG& control_duration, bool adjust_duration)
{
    // 0 - no check
    if( max_duration == 0 )
    {
        return true;
    }
    ULONG total_duration = current_duration + control_duration;

    if( total_duration > max_duration )
    {
        if( adjust_duration &&
            max_duration > current_duration ) //Cannot adjust the control time negative!
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

bool CtiLMProgramConstraintChecker::checkManualGearChangeConstraints(ULONG proposed_gear, ULONG proposed_stop_seconds)
{
    bool ret_val = true;
    CtiLMProgramDirectGear* currentGearObject = _lm_program.getCurrentGearObject();

    if( proposed_stop_seconds > _lm_program.getDirectStopTime().seconds() )
    {
        CtiTime currentStopTime = _lm_program.getDirectStopTime();
        CtiTime proposedStop    = CtiTime(proposed_stop_seconds);

        string result = "Gear change does not support extending the stop time. Current stop time: ";
        result += currentStopTime.asString();
        result += " Requested stop time: ";
        result += proposedStop.asString();
        result += " You cannot override this constraint error.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_TT_CannotExtendStopTime,
                                                            currentStopTime,
                                                            proposedStop));

        ret_val = false;
    }

    if( proposed_gear == _lm_program.getCurrentGearNumber() )
    {
        string result = "New gear id is identical to the current running gear. You cannot override this constraint error.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_NP_IdenticalGearID));

        ret_val = false;
    }

    if( _lm_program.getProgramState() == CtiLMProgramBase::StoppingState || _lm_program.getProgramState() == CtiLMProgramBase::InactiveState )
    {
        string result = "Program state is set to: ";
        result += _lm_program.getProgramState() == CtiLMProgramBase::StoppingState ? "Stopping" : "Inactive";
        result += " Gear cannot be changed when program is not active. You cannot override this constraint error.";
        _results.push_back(result);

        ConstraintViolation::CV_Type_NoParameters error = _lm_program.getProgramState() == CtiLMProgramBase::StoppingState ? ConstraintViolation::CV_NP_GearCannotChangeStopping : ConstraintViolation::CV_NP_GearCannotChangeInactive;
        _constraintViolations.push_back(ConstraintViolation(error));

        ret_val = false;
    }

    if( currentGearObject != NULL && ciStringEqual(currentGearObject->getControlMethod(), CtiLMProgramDirectGear::LatchingMethod) )
    {
        string result = "Latching method currently in use, you cannot change from latching method.";
        _results.push_back(result);

        _constraintViolations.push_back(ConstraintViolation(ConstraintViolation::CV_NP_CannotChangeFromLatching));
    }

    return ret_val;

}

const std::vector<std::string>& CtiLMProgramConstraintChecker::getResults() const
{
    return _results;
}

