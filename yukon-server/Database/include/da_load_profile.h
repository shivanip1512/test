#pragma warning( disable : 4786)

#ifndef __DataAccessLoadProfile_H__
#define __DataAccessLoadProfile_H__
#include "yukon.h"

#include <boost/shared_ptr.hpp>

class DataAccessLoadProfile
{
    public:

       virtual int  getLastIntervalDemandRate() const =0;
       virtual int  getLoadProfileDemandRate() const =0;
       virtual int  getVoltageDemandInterval() const =0;
       virtual int  getVoltageProfileRate() const =0;
       virtual bool isChannelValid(int channel) const =0;

       //LONG getDeviceID() const;
};

#endif
