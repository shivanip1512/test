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
    typedef std::map<long, long>                                        DeviceAssignmentMap;

    typedef std::map<long, std::map<DeviceTypes, Cti::Config::DeviceConfigSPtr> >   DeviceConfigCache;

    ConfigurationMap    _configurations;
    CategoryMap         _categories;
    DeviceAssignmentMap _deviceAssignments;
    DeviceConfigCache   _cache;

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

protected:

    CtiConfigManager( Cti::Config::DeviceConfigSPtr config );

public:

    CtiConfigManager();
    ~CtiConfigManager();

    void processDBUpdate( const long            ID,
                          const std::string &   category,
                          const std::string &   objectType,
                          const int             updateType);

    virtual Cti::Config::DeviceConfigSPtr   fetchConfig( const long deviceID, const DeviceTypes deviceType );
};

