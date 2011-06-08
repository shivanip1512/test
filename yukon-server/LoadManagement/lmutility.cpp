/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "timeperiod.h"
#include "lmutility.h"

#include "lmprogramcontrolwindow.h"

#include <sstream>

using std::string;

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
// and returns appropriate start/stop times. If not successful, the result start/stop 
// may be anything.
// 
// Returns true if successful, false if not.
bool FitTimeToWindows(CtiTime proposedStart, CtiTime proposedStop,
                      CtiTime &resultStart, CtiTime &resultStop,
                      CtiLMControlArea *controlArea,
                      CtiLMProgramBaseSPtr program)
{
    using Cti::TimePeriod;
    TimePeriod result(proposedStart, proposedStop);

    TimePeriod caTime(CtiTime::CtiTime(CtiTime::neg_infin), CtiTime::CtiTime(CtiTime::neg_infin));
    if( controlArea != NULL )
    {
        caTime = TimePeriod(controlArea->getCurrentDailyStartTime(proposedStart.date()), 
                            controlArea->getCurrentDailyStopTime(proposedStart.date()));
    }

    TimePeriod program1Time = TimePeriod(CtiTime::CtiTime(CtiTime::neg_infin), CtiTime::CtiTime(CtiTime::neg_infin));
    TimePeriod program2Time = TimePeriod(CtiTime::CtiTime(CtiTime::neg_infin), CtiTime::CtiTime(CtiTime::neg_infin));

    std::vector<CtiLMProgramControlWindow*>& control_windows = program->getLMProgramControlWindows();

    if( control_windows.size() )
    {
        program1Time = TimePeriod(control_windows[0]->getAvailableStartTime(proposedStart.date()),
                                  control_windows[0]->getAvailableStopTime(proposedStart.date()));

        if( program1Time.begin().date() < program1Time.end().date() )
        {
            // If this is a date that loops over midnight, getAvailableStartTime returns
            // the window beginning today, and we want the window that ends today.
            program1Time.addDays(-1);
        }
    }

    if( control_windows.size() > 1 )
    {
        program2Time = TimePeriod(control_windows[1]->getAvailableStartTime(proposedStart.date()),
                                  control_windows[1]->getAvailableStopTime(proposedStart.date()));

        if( program2Time.begin().date() < program2Time.end().date() )
        {
            // If this is a date that loops over midnight, getAvailableStartTime returns
            // the window beginning today, and we want the window that ends today.
            program2Time.addDays(-1);
        }
    }

    // Check if we have any windows at all!
    if( !caTime.is_null() || !program1Time.is_null() || !program2Time.is_null() )
    {
        // Note that every path here must set result!

        // First check, if there are no program windows, the window is the control area window.
        if( !caTime.is_null() && program1Time.is_null() && program2Time.is_null() )
        {
            while( caTime.end() <= proposedStart )
            {
                caTime.addDays(1);
            }
            result = TimePeriod(std::max(caTime.begin(), proposedStart), std::min(caTime.end(), proposedStop));
        }
        else
        {
            TimePeriod possibleResult1 = TimePeriod(CtiTime::CtiTime(CtiTime::neg_infin), CtiTime::CtiTime(CtiTime::neg_infin));
            TimePeriod possibleResult2 = TimePeriod(CtiTime::CtiTime(CtiTime::neg_infin), CtiTime::CtiTime(CtiTime::neg_infin));
            // We have a 1 or more program based windows
            if( caTime.is_null() )
            {
                // No control area window, check the programs only. Easy mode!
                if( !program1Time.is_null() )
                {
                    while( program1Time.end() <= proposedStart )
                    {
                        program1Time.addDays(1);
                    }
                    possibleResult1 = program1Time;
                }

                if( !program2Time.is_null() )
                {
                    while( program2Time.end() <= proposedStart )
                    {
                        program2Time.addDays(1);
                    }
                    possibleResult2 = program2Time;
                }

            }
            else // Both programs and a control window! Hard mode begin!
            {
                bool result1Found = false, result2Found = false;;
                int progDays = 0;
                int caDays = 0;

                for( caDays = 0; caDays < 2 && (!result1Found || !result2Found); )
                {
                    for( progDays = 0; progDays < 3 && !result1Found && !program1Time.is_null(); )
                    {
                        TimePeriod possiblePeriod = caTime.intersection(program1Time);
                        if( !possiblePeriod.is_null() )
                        {
                            if( possiblePeriod.end() > proposedStart )
                            {
                                // This is the first and only allowed result from this program. Record it!
                                possibleResult1 = possiblePeriod;
                                result1Found = true;
                            }
                        }
                        
                        program1Time.addDays(1);
                        progDays++;
                    }

                    program1Time.addDays(-1*progDays);

                    for( progDays = 0; progDays < 3 && !result2Found && !program2Time.is_null(); )
                    {
                        TimePeriod possiblePeriod = caTime.intersection(program2Time);
                        if( !possiblePeriod.is_null() )
                        {
                            if( possiblePeriod.end() > proposedStart )
                            {
                                // This is the first and only allowed result from this program. Record it!
                                possibleResult2 = possiblePeriod;
                                result2Found = true;
                            }
                        }
                       
                        program2Time.addDays(1);
                        progDays++;
                    }

                    program2Time.addDays(-1*progDays);
                    caTime.addDays(1);
                    caDays ++;
                }
                caTime.addDays(-1*caDays);
            }

            if( !possibleResult1.is_null() && !possibleResult2.is_null() )
            {
                if( possibleResult1.intersects(possibleResult2) )
                {
                    result = possibleResult1.merge(possibleResult2).intersection(result);
                }
                else 
                {
                    // Try the one that has the first start time first.
                    if( possibleResult1.begin() > possibleResult2.begin() )
                    {
                        TimePeriod temp = possibleResult2;
                        possibleResult2 = possibleResult1;
                        possibleResult1 = temp;
                    }

                    if( possibleResult1.intersects(result) )
                    {
                        result = possibleResult1.intersection(result);
                    }
                    else
                    {
                        result = possibleResult2.intersection(result);
                    }
                }
            }
            else if( !possibleResult1.is_null() )
            {
                result = possibleResult1.intersection(result);
            }
            else if( !possibleResult2.is_null() )
            {
                result = possibleResult2.intersection(result);
            }
            else
            {
                result = TimePeriod(CtiTime(CtiTime::neg_infin), CtiTime(CtiTime::neg_infin));
            }
        }
    }

    resultStart = result.begin();
    resultStop = result.end();
    return !result.is_null();
}
