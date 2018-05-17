#pragma once

#include "mgr_config.h"
#include "boost_test_helpers.h"

namespace Cti {
namespace Test {
namespace {

struct test_DeviceConfig : public Config::DeviceConfig
{
    test_DeviceConfig() : DeviceConfig( 271828 ) {}

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
    std::unique_ptr<ConfigManager> _oldConfigManager;

public:

    Override_ConfigManager(Config::DeviceConfigSPtr config)
    {
        _oldConfigManager = std::move(gConfigManager);

        gConfigManager.reset(new test_ConfigManager(config));
    }

    ~Override_ConfigManager()
    {
        gConfigManager = std::move(_oldConfigManager);
    }
};

}
}
}