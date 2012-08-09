#pragma once

#include "yukon.h"

#include <boost/shared_ptr.hpp>

#include "da_load_profile.h"
#include "config_device.h"


namespace Cti {

class IM_EX_CONFIG DeviceConfigurationLoadProfileData : public DataAccessLoadProfile
{
    private:
        Cti::Config::DeviceConfigSPtr _deviceConfig;
        boost::shared_ptr<DataAccessLoadProfile> _lpTable;

    public:
        DeviceConfigurationLoadProfileData();

        /* Interface functions */
        virtual int  getLastIntervalDemandRate() const;
        virtual int  getLoadProfileDemandRate() const;
        virtual int  getVoltageDemandInterval() const;
        virtual int  getVoltageProfileRate() const;
        virtual bool isChannelValid(int channel) const;

        void setDeviceConfig(Cti::Config::DeviceConfigSPtr deviceConfig);
        void setLpTable(boost::shared_ptr<DataAccessLoadProfile> table);
};

}
