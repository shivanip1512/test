#include "yukon.h"

#include "da_lp_deviceconfig.h"
#include "config_data_mct.h"

using namespace Cti::Config;

DeviceConfigurationLoadProfileData::DeviceConfigurationLoadProfileData()
{

}

int DeviceConfigurationLoadProfileData::getLastIntervalDemandRate() const
{
    long demand = deviceConfig->getLongValueFromKey(MCTStrings::DemandInterval);

    return demand;

}

int DeviceConfigurationLoadProfileData::getLoadProfileDemandRate() const
{
    long loadProfile1 = deviceConfig->getLongValueFromKey(MCTStrings::LoadProfileInterval);

    // * 60 because the configs store in minutes, they are expected to be in seconds
    return loadProfile1*60;
}

int DeviceConfigurationLoadProfileData::getVoltageDemandInterval() const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Unsupported Operation. getVoltageDemandInterval() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return 0;
}

int DeviceConfigurationLoadProfileData::getVoltageProfileRate() const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Unsupported Operation. getVoltageProfileRate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return 0;
}

bool DeviceConfigurationLoadProfileData::isChannelValid(int channel) const
{
    long value;

    switch (channel)
    {
        case 1:
            value = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig1);
            break;
        case 2:
            value = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig2);
            break;
        case 3:
            value = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig3);
            break;
        case 4:
            value = deviceConfig->getLongValueFromKey(MCTStrings::ChannelConfig4);
            break;
        default:
            value = 0;
            break;
    }

    //The channel values are not zero and one, so we % it to get them to a boolean value.
    return (value%4 != 0);
}

Cti::Config::CtiConfigDeviceSPtr DeviceConfigurationLoadProfileData::getDeviceConfig()
{
    return deviceConfig;
}

void DeviceConfigurationLoadProfileData::setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr deviceConfig)
{
    this->deviceConfig = deviceConfig;
}
