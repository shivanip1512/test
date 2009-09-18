#pragma warning( disable : 4786)
#pragma once

#include "yukon.h"

#include <boost/shared_ptr.hpp>

#include "da_load_profile.h"
#include "config_device.h"


namespace Cti {

class IM_EX_CONFIG DeviceConfigurationLoadProfileData : public DataAccessLoadProfile
{
    private:
        Cti::Config::CtiConfigDeviceSPtr _deviceConfig;
        boost::shared_ptr<DataAccessLoadProfile> _lpTable;
        static const int SecondsPerMinute;

    public:
        DeviceConfigurationLoadProfileData();

        /* Interface functions */
        virtual int  getLastIntervalDemandRate() const;
        virtual int  getLoadProfileDemandRate() const;
        virtual int  getVoltageDemandInterval() const;
        virtual int  getVoltageProfileRate() const;
        virtual bool isChannelValid(int channel) const;

        void setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr deviceConfig);
        void setLpTable(boost::shared_ptr<DataAccessLoadProfile> table);
};

}
