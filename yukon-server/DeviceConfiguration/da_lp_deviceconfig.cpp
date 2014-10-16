#include "precompiled.h"

#include "da_lp_deviceconfig.h"
#include "config_data_mct.h"
#include "logger.h"
#include "boostutil.h"

using namespace Cti;
using namespace Cti::Config;
using std::endl;

const int SecondsPerMinute = 60;

DeviceConfigurationLoadProfileData::DeviceConfigurationLoadProfileData(Cti::Config::DeviceConfigSPtr deviceConfig,
                                                                       boost::shared_ptr<DataAccessLoadProfile> table)
    :   _deviceConfig(deviceConfig),
        _lpTable(table)
{
}

int DeviceConfigurationLoadProfileData::getLastIntervalDemandRate() const
{
    long demand = _deviceConfig->getLongValueFromKey(MCTStrings::DemandInterval);

    return demand * SecondsPerMinute;
}

int DeviceConfigurationLoadProfileData::getLoadProfileDemandRate() const
{
    long lpInterval_minutes = _deviceConfig->getLongValueFromKey(MCTStrings::ProfileInterval);

    if( lpInterval_minutes <= 0 )
    {
        CTILOG_ERROR(dout, "\""<< MCTStrings::ProfileInterval <<"\" returned \""<< lpInterval_minutes <<"\", returning 0");

        return 0;
    }

    return lpInterval_minutes * SecondsPerMinute;
}

int DeviceConfigurationLoadProfileData::getVoltageDemandInterval() const
{
    CTILOG_ERROR(dout, "Unsupported Operation");

    return 0;
}

int DeviceConfigurationLoadProfileData::getVoltageProfileRate() const
{
    CTILOG_ERROR(dout, "Unsupported Operation");

    return 0;
}

bool DeviceConfigurationLoadProfileData::isChannelValid(int channel) const
{
    return _lpTable->isChannelValid(channel);
}

