/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma once
#include "CtiTime.h"
#include "ctidate.h"

#include "lmprogrambase.h"
#include "lmcontrolarea.h"

extern CtiTime GetTimeFromOffsetAndDate(LONG offsetFromMidnight, const CtiDate &startingDate);

extern LONG GetOffsetFromTime(const CtiTime &time);

extern std::string ControlWindowErrorMessage(const CtiTime &windowStartTime,
                                        const CtiTime &windowStopTime,
                                        const CtiTime &proposedTime,
                                        const string &timeType,
                                        const string &windowType);

extern bool FitTimeToWindows(CtiTime proposedStart, CtiTime proposedStop,
                        CtiTime &resultStart, CtiTime &resultStop,
                        CtiLMControlArea *controlArea,
                        CtiLMProgramBaseSPtr program);
