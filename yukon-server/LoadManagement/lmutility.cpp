/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "lmutility.h"

/*
*   This is WALLCLOCK time - an offset of 14400 (+4 hours) is ALWAYS 4am.  This function
*    ignores any effect of a DST gain or loss of an hour.
*/
CtiTime GetTimeFromOffsetAndDate(LONG offsetFromMidnight, CtiDate &startingDate)
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

