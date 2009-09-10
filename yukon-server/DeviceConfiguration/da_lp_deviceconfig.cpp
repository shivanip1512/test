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
    return lpTable->isChannelValid(channel);
}

Cti::Config::CtiConfigDeviceSPtr DeviceConfigurationLoadProfileData::getDeviceConfig()
{
    return deviceConfig;
}

void DeviceConfigurationLoadProfileData::setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr deviceConfig)
{
    this->deviceConfig = deviceConfig;
}

boost::shared_ptr<DataAccessLoadProfile> DeviceConfigurationLoadProfileData::getLpTable()
{
    return lpTable;
}

void DeviceConfigurationLoadProfileData::setLpTable(boost::shared_ptr<DataAccessLoadProfile> lpTable)
{
    this->lpTable = lpTable;
}
