#pragma once

#include "dllbase.h"
#include "config_device.h"
#include "devicetypes.h"
#include "readers_writer_lock.h"

#include <boost/ptr_container/ptr_map.hpp>

namespace Cti {

class IM_EX_CONFIG ConfigManager
{
private:

    typedef std::set<long> CategoryIds;

    typedef std::map<long, CategoryIds>                      ConfigurationToCategoriesMap;
    typedef std::map<long, Config::CategorySPtr>             CategoryMap;
    typedef std::map<long, long>                             DeviceToConfigAssignmentMap;
    typedef std::map<DeviceTypes, Config::DeviceConfigSPtr>  DeviceTypeToDeviceConfigMap;
    typedef std::map<long, DeviceTypeToDeviceConfigMap>      DeviceConfigCache;

    ConfigurationToCategoriesMap  _configurations;
    CategoryMap                   _categories;
    DeviceToConfigAssignmentMap   _deviceAssignments;
    DeviceConfigCache             _cache;

    readers_writer_lock_t         _lock;

    void loadAllConfigs();
    void loadConfig( const long configID );
    void executeLoadConfig( const std::string & sql );

    void loadAllCategoryItems();
    void loadCategoryItems( const long categoryID );
    void executeLoadItems( const std::string & sql );

    void loadAllDeviceAssignments();
    void loadDeviceAssignment( const long deviceID );
    void executeLoadDeviceAssignments( const std::string & sql );

    Config::DeviceConfigSPtr   buildConfig( const long configID, const DeviceTypes deviceType );

    virtual Config::DeviceConfigSPtr fetchConfig( const long deviceID, const DeviceTypes deviceType );

    void processDBUpdate( const long            ID,
                          const std::string &   category,
                          const std::string &   objectType,
                          const int             updateType);

public:

    static void handleDbChange(
            const long            ID,
            const std::string &   category,
            const std::string &   objectType,
            const int             updateType);

    static void initialize();

    static Config::DeviceConfigSPtr getConfigForIdAndType( const long deviceID, const DeviceTypes deviceType );

    virtual ~ConfigManager() = default;  //  for unit test override
};

extern IM_EX_CONFIG std::auto_ptr<ConfigManager> gConfigManager;

}
