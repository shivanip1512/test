/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "ctitime.h"
#include "ctidate.h"

extern CtiTime GetTimeFromOffsetAndDate(LONG offsetFromMidnight, const CtiDate &startingDate);
extern string ControlWindowErrorMessage(const CtiTime &windowStartTime,
                                        const CtiTime &windowStopTime,
                                        const CtiTime &proposedTime,
                                        const string &timeType,
                                        const string &windowType);

