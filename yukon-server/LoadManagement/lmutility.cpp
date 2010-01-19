/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "lmutility.h"

#include "lmprogramcontrolwindow.h"

#include <sstream>

/*
*   This is WALLCLOCK time - an offset of 14400 (+4 hours) is ALWAYS 4am.  This function
*    ignores any effect of a DST gain or loss of an hour.
*/
CtiTime GetTimeFromOffsetAndDate(LONG offsetFromMidnight, const CtiDate &startingDate)
{
    int days = offsetFromMidnight / 86400;

    if( offsetFromMidnight < 0 )
    {
        // if the offset contains a partial day ((x % 86400) != 0) we want to subtract the whole day.
        // The goal is for (0 <= offsetFromMidnight <= 86399)
        if( offsetFromMidnight % 86400 )
        {
            days--;
        }
        offsetFromMidnight -= ( days * 86400 );
    }

    offsetFromMidnight %= 86400;

    int hours   = offsetFromMidnight / 3600;
    offsetFromMidnight %= 3600;

    int minutes = offsetFromMidnight / 60;
    int seconds = offsetFromMidnight % 60;

    CtiTime startTime(startingDate, hours, minutes, seconds);

    startTime.addDays(days);

    return startTime;
}


string ControlWindowErrorMessage(const CtiTime &windowStartTime,
                                 const CtiTime &windowStopTime,
                                 const CtiTime &proposedTime,
                                 const string &timeType,
                                 const string &windowType)
{
    std::ostringstream   stream;

    stream << "The program cannot run outside of its prescribed control windows.  The proposed "
            << timeType
            << " time of "
            << proposedTime.asString()
            << " is outside the ";
        
    if( !windowType.empty() )
    {
        stream << windowType << " ";
    }
        
    stream << "control window that runs from "
            << windowStartTime.asString()
            << " to "
            << windowStopTime.asString();
            
    return stream.str();
}


// Given the requested start/stop time, tries to use program and control area windows
// and returns appropriate start/stop times.
// Returns true if successful, false if not.
bool FitTimeToWindows(CtiTime proposedStart, CtiTime proposedStop,
                      CtiTime &resultStart, CtiTime &resultStop,
                      CtiLMControlArea *controlArea,
                      CtiLMProgramBaseSPtr program)
{
    resultStart = proposedStart;
    resultStop = proposedStop;

    CtiTime caStart = CtiTime(CtiTime::neg_infin);
    CtiTime caStop = CtiTime(CtiTime::neg_infin);
    if( controlArea != NULL )
    {
        caStart = controlArea->getCurrentDailyStartTime(proposedStart.date());
        caStop = controlArea->getCurrentDailyStopTime(proposedStart.date());
    }

    CtiTime pr1Start = CtiTime(CtiTime::neg_infin), pr2Start = CtiTime(CtiTime::neg_infin),
            pr1Stop = CtiTime(CtiTime::neg_infin), pr2Stop = CtiTime(CtiTime::neg_infin);

    std::vector<CtiLMProgramControlWindow*>& control_windows = program->getLMProgramControlWindows();

    if( control_windows.size() )
    {
        pr1Start = control_windows[0]->getAvailableStartTime(proposedStart.date());
        pr1Stop = control_windows[0]->getAvailableStopTime(proposedStart.date());
    }

    if( control_windows.size() > 1 )
    {
        pr2Start = control_windows[1]->getAvailableStartTime(proposedStart.date());
        pr2Stop = control_windows[1]->getAvailableStopTime(proposedStart.date());
    }

    // Check if we have any windows at all!
    if( (!caStart.is_neg_infinity()  && !caStop.is_neg_infinity()) ||
        (!pr1Start.is_neg_infinity() && !pr1Stop.is_neg_infinity()) ||
        (!pr2Start.is_neg_infinity() && !pr2Stop.is_neg_infinity()) )
    {
        CtiTime newWindow1Start = pr1Start;
        CtiTime newWindow1Stop = pr1Stop;
        CtiTime newWindow2Start = pr2Start;
        CtiTime newWindow2Stop = pr2Stop;

        // First check, if there are no program windows, the window is the control area window.
        if( !caStart.is_neg_infinity() && !caStop.is_neg_infinity() &&
            pr1Start.is_neg_infinity() && pr1Stop.is_neg_infinity() &&
            pr2Start.is_neg_infinity() && pr2Stop.is_neg_infinity() )
        {
            newWindow1Start = caStart;
            newWindow1Stop = caStop;
        }
        else
        {
            // We have a 1 or more program based windows, if there is a control area
            // the program windows must be snapped down to the CA window.
            if( !newWindow1Start.is_neg_infinity() && !newWindow1Stop.is_neg_infinity() &&
                !caStart.is_neg_infinity() && !caStop.is_neg_infinity() )
            {
                if( caStart > newWindow1Start )
                {
                    newWindow1Start = caStart;
                }

                if( caStop < newWindow1Stop )
                {
                    newWindow1Stop = caStop;
                }
                
                // if this happens, the program is totally outside of the control area window.
                // If no overlap, the window is totally invalid.
                if( newWindow1Start >= newWindow1Stop )
                {
                    newWindow1Start = CtiTime(CtiTime::neg_infin);
                    newWindow1Stop = CtiTime(CtiTime::neg_infin);
                }
            }

            if( !newWindow2Start.is_neg_infinity() && !newWindow2Stop.is_neg_infinity() &&
                !caStart.is_neg_infinity() && !caStop.is_neg_infinity() )
            {
                if( caStart > newWindow2Start )
                {
                    newWindow2Start = caStart;
                }

                if( caStop < newWindow2Stop )
                {
                    newWindow2Stop = caStop;
                }
                
                // if this happens, the program is totally outside of the control area window.
                // If no overlap, the window is totally invalid.
                if( newWindow2Start >= newWindow2Stop )
                {
                    newWindow2Start = CtiTime(CtiTime::neg_infin);
                    newWindow2Stop = CtiTime(CtiTime::neg_infin);
                }
            }
        }

        if( newWindow1Start.is_neg_infinity() && newWindow2Start.is_neg_infinity() )
        {
            // The windows do not overlap, return no control found.
            resultStart = CtiTime(CtiTime::neg_infin);
            resultStop = CtiTime(CtiTime::neg_infin);
            return false;
        }


        int bothWindowsValid = false;
        if( !newWindow1Start.is_neg_infinity() && !newWindow2Start.is_neg_infinity() )
        {
            bothWindowsValid = true;
        }

        // We want window 1 to be < window 2, always.
        // We also want window 1 to be the valid window if there is only 1 valid
        if( (!newWindow2Start.is_neg_infinity() && newWindow1Start.is_neg_infinity()) || 
            (bothWindowsValid && newWindow1Stop > newWindow2Stop) )
        {
            //Swap them.
            CtiTime tempWindow1Start = newWindow2Start;
            CtiTime tempWindow1Stop = newWindow2Stop;
            newWindow2Start = newWindow1Start;
            newWindow2Stop = newWindow1Stop;
            newWindow1Start = tempWindow1Start;
            newWindow1Stop = tempWindow1Stop;
        }

        if( bothWindowsValid && newWindow1Stop > newWindow2Start )
        {
            //What we have here is 1 window! they span each other. Who set this up?
            newWindow1Stop = newWindow2Stop;
            newWindow2Start = CtiTime(CtiTime::neg_infin);
            newWindow2Stop = CtiTime(CtiTime::neg_infin);
        }

        // At this point, we have our two control windows, both of which are set up correctly
        // in order and invalid if necessary.
        if( newWindow1Stop > proposedStart )
        {
            resultStart = std::max(newWindow1Start, proposedStart);
            resultStop = std::min(newWindow1Stop, proposedStop);
        }
        else if( bothWindowsValid && newWindow2Stop > proposedStart )
        {
            resultStart = std::max(newWindow2Start, proposedStart);
            resultStop = std::min(newWindow2Stop, proposedStop);
        }
        else
        {
            newWindow1Start.addDays(1);
            newWindow1Stop.addDays(1);

            if( bothWindowsValid )
            {
                newWindow2Start.addDays(1);
                newWindow2Stop.addDays(1);
            }

            if( newWindow1Stop > proposedStart )
            {
                resultStart = std::max(newWindow1Start, proposedStart);
                resultStop = std::min(newWindow1Stop, proposedStop);
            }
            else if( bothWindowsValid && newWindow2Stop > proposedStart )
            {
                resultStart = std::max(newWindow2Start, proposedStart);
                resultStop = std::min(newWindow2Stop, proposedStop);
            }
            else
            {
                // Somehow we found nothing???
                resultStart = CtiTime(CtiTime::neg_infin);
                resultStop = CtiTime(CtiTime::neg_infin);
                return false;
            }
        }
        
        if( resultStart >= resultStop )
        {
            resultStart = CtiTime(CtiTime::neg_infin);
            resultStop = CtiTime(CtiTime::neg_infin);
            return false;
        }
        // We are done and successful!
    }

    return true;
}
