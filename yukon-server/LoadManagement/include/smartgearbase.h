/*---------------------------------------------------------------------------
        SmartGearBase class. Base class for gears that have logic in them
        instead of relying on the lm program to do all of their work.
---------------------------------------------------------------------------*/
#pragma once

#include "lmgroupbase.h"

class SmartGearBase
{
public:
    SmartGearBase() {};

    virtual bool attemptControl(CtiLMGroupPtr currentLMGroup, long controlSeconds, DOUBLE &expectedLoadReduced) = 0;
    virtual bool stopControl(CtiLMGroupPtr currentLMGroup) = 0;
    virtual unsigned long estimateOffTime(long controlSeconds) = 0;
};
