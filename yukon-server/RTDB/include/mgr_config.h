#pragma once

#include "dllbase.h"
#include "config_device.h"
#include "devicetypes.h"

#include <boost/ptr_container/ptr_map.hpp>



class IM_EX_DEVDB CtiConfigManager
{
private:

    typedef boost::ptr_map<long, Cti::Config::Configuration>            ConfigurationMap;
    typedef boost::ptr_map<long, Cti::Config::ConfigurationCategory>    CategoryMap;
    typedef std::map<long, long>                                        DeviceToConfigAssignmentMap;

    typedef std::map<long, std::map<DeviceTypes, Cti::Config::DeviceConfigSPtr> >   DeviceConfigCache;

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

    Cti::Config::DeviceConfigSPtr   buildConfig( const long configID, const DeviceTypes deviceType );

public:

    CtiConfigManager();
    ~CtiConfigManager();

    void processDBUpdate( const long            ID,
                          const std::string &   category,
                          const std::string &   objectType,
                          const int             updateType);

    void initialize();

    virtual Cti::Config::DeviceConfigSPtr   fetchConfig( const long deviceID, const DeviceTypes deviceType );
};

