#pragma once

#include "dllbase.h"

#include "mgr_device.h"
#include "config_device.h"

class IM_EX_DEVDB CtiConfigManager
{
private:

    typedef CtiSmartMap< Cti::Config::DeviceConfig > ConfigDeviceMap;

    CtiDeviceManager*     _devMgr;
    ConfigDeviceMap       _deviceConfig;

    bool insertValueIntoConfig(Cti::Config::DeviceConfigSPtr config, const std::string &value, const std::string &valueid);

    enum ConfigValue
    {
        NoConfigIdSpecified = -1000,
        NoDeviceIdSpecified =     0
    };

    void loadConfigurationItems(long configID);
    void loadAllConfigurationItems();
    void loadConfig(long configID);
    void loadAllConfigs();
    void updateDeviceConfigs(long configID, long deviceID);
    void removeFromMaps(long configID);

    void executeLoadConfig(const std::string &sql);
    void executeLoadItems (const std::string &sql);

    void refreshConfigurations();
    void setDeviceManager(CtiDeviceManager &mgr);

    bool isInitialized;

public:

    CtiConfigManager();
    ~CtiConfigManager();

    void initialize(CtiDeviceManager &mgr);
    void processDBUpdate(LONG identifer, std::string category, std::string objectType, int updateType);
    void refreshConfigForDeviceId(long deviceid);

    Cti::Config::DeviceConfigSPtr getDeviceConfigFromID(long configID);
};
