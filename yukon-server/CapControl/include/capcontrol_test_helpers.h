#pragma once

#include "mgr_config.h"

namespace Cti {
namespace Test {
namespace {

struct test_DeviceConfig : public Config::DeviceConfig
{
    using DeviceConfig::insertValue;
    using DeviceConfig::findValue;
    using DeviceConfig::addCategory;
};

struct test_ConfigManager : ConfigManager
{
    const Config::DeviceConfigSPtr config;

    void loadAllConfigs()           override  { }
    void loadAllCategoryItems()     override  { }
    void loadAllDeviceAssignments() override  { }

    test_ConfigManager(Config::DeviceConfigSPtr config_)
        : config(config_)
    {
    }

    virtual Config::DeviceConfigSPtr fetchConfig(const long deviceID, const DeviceTypes deviceType)
    {
        return config;
    }
};

class Override_ConfigManager
{
    std::auto_ptr<ConfigManager> _oldConfigManager;

public:

    Override_ConfigManager(Config::DeviceConfigSPtr config)
    {
        _oldConfigManager = gConfigManager;

        gConfigManager.reset(new test_ConfigManager(config));
    }

    ~Override_ConfigManager()
    {
        gConfigManager = _oldConfigManager;
    }
};

}
}
}