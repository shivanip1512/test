#pragma once

#include "dllbase.h"
#include "config_device.h"
#include "devicetypes.h"

#include <boost/ptr_container/ptr_map.hpp>

namespace Cti {

class IM_EX_CONFIG ConfigManager
{
private:

    typedef boost::ptr_map<long, Config::Configuration>            ConfigurationMap;
    typedef boost::ptr_map<long, Config::ConfigurationCategory>    CategoryMap;
    typedef std::map<long, long>                                   DeviceToConfigAssignmentMap;

    typedef std::map<long, std::map<DeviceTypes, Config::DeviceConfigSPtr> >   DeviceConfigCache;

    ConfigurationMap            _configurations;
    CategoryMap                 _categories;
    DeviceToConfigAssignmentMap _deviceAssignments;
    DeviceConfigCache           _cache;

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
};

extern IM_EX_CONFIG std::auto_ptr<ConfigManager> gConfigManager;

}
