#pragma warning( disable : 4786)

#ifndef __DA_LP_DEVICECONFIG_H__
#define __DA_LP_DEVICECONFIG_H__
#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include <exception>

#include "da_load_profile.h"
#include "config_device.h"

class IM_EX_CONFIG DeviceConfigurationLoadProfileData : public DataAccessLoadProfile
{
    private:
        Cti::Config::CtiConfigDeviceSPtr deviceConfig;

    public:
        DeviceConfigurationLoadProfileData();

        /* Interface functions */
        virtual int  getLastIntervalDemandRate() const;
        virtual int  getLoadProfileDemandRate() const;
        virtual int  getVoltageDemandInterval() const;
        virtual int  getVoltageProfileRate() const;
        virtual bool isChannelValid(int channel) const;

        Cti::Config::CtiConfigDeviceSPtr getDeviceConfig();
        void setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr deviceConfig);
};

#endif

