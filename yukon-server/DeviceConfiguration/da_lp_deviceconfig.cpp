#include "yukon.h"

#include "da_lp_deviceconfig.h"
#include "config_data_mct.h"

#include "boostutil.h"

using namespace Cti;
using namespace Cti::Config;

const int DeviceConfigurationLoadProfileData::SecondsPerMinute = 60;

DeviceConfigurationLoadProfileData::DeviceConfigurationLoadProfileData()
{

}

int DeviceConfigurationLoadProfileData::getLastIntervalDemandRate() const
{
    long demand = _deviceConfig->getLongValueFromKey(MCTStrings::DemandInterval);

    return demand * SecondsPerMinute;
}

int DeviceConfigurationLoadProfileData::getLoadProfileDemandRate() const
{
    long lpInterval_minutes = _deviceConfig->getLongValueFromKey(MCTStrings::LoadProfileInterval);

    return lpInterval_minutes * SecondsPerMinute;
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
    return _lpTable->isChannelValid(channel);
}

void DeviceConfigurationLoadProfileData::setDeviceConfig(DeviceConfigSPtr deviceConfig)
{
    _deviceConfig = deviceConfig;
}

void DeviceConfigurationLoadProfileData::setLpTable(boost::shared_ptr<DataAccessLoadProfile> lpTable)
{
    _lpTable = lpTable;
}
