#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"

class DataAccessLoadProfile
{
    public:

       virtual int  getLastIntervalDemandRate() const =0;
       virtual int  getLoadProfileDemandRate() const =0;
       virtual int  getVoltageDemandInterval() const =0;
       virtual int  getVoltageProfileRate() const =0;
       virtual bool isChannelValid(int channel) const =0;
};

