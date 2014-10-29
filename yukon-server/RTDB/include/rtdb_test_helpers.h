#pragma once

#include "mgr_config.h"
#include "mgr_dyn_paoinfo.h"

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

    test_ConfigManager( Config::DeviceConfigSPtr config_ )
        : config( config_ )
    {
    }

    virtual Config::DeviceConfigSPtr fetchConfig( const long deviceID, const DeviceTypes deviceType )
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

struct test_DynamicPaoInfoManager : DynamicPaoInfoManager
{
    virtual void loadInfo(const long id)
    {
        loadedPaos.insert(id);
    }
};

class Override_DynamicPaoInfoManager
{
    std::auto_ptr<DynamicPaoInfoManager> _oldDynamicPaoInfoManager;

public:

    Override_DynamicPaoInfoManager()
    {
        _oldDynamicPaoInfoManager = gDynamicPaoInfoManager;

        gDynamicPaoInfoManager.reset(new test_DynamicPaoInfoManager);
    }

    ~Override_DynamicPaoInfoManager()
    {
        gDynamicPaoInfoManager = _oldDynamicPaoInfoManager;
    }
};

}
}
}
