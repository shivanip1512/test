#pragma once

#include "CtiTime.h"
#include "ctidate.h"

#include "lmprogrambase.h"
#include "lmcontrolarea.h"

extern CtiTime GetTimeFromOffsetAndDate(LONG offsetFromMidnight, const CtiDate &startingDate);
extern std::string ControlWindowErrorMessage(const CtiTime &windowStartTime,
                                        const CtiTime &windowStopTime,
                                        const CtiTime &proposedTime,
                                        const std::string &timeType,
                                        const std::string &windowType);

extern bool FitTimeToWindows(CtiTime proposedStart, CtiTime proposedStop,
                        CtiTime &resultStart, CtiTime &resultStop,
                        CtiLMControlArea *controlArea,
                        CtiLMProgramBaseSPtr program);
