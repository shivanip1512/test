#pragma once

#include "dllbase.h"
#include "config_device.h"

#include <boost/ptr_container/ptr_map.hpp>


class CtiDeviceManager;



class IM_EX_DEVDB CtiConfigManager
{
private:

    typedef boost::ptr_map<long, Cti::Config::Configuration>            ConfigurationMap;
    typedef boost::ptr_map<long, Cti::Config::ConfigurationCategory>    CategoryMap;

    bool                isInitialized;
    CtiDeviceManager  * _devMgr;

    ConfigurationMap    _configurations;
    CategoryMap         _categories;

    void setDeviceManager( CtiDeviceManager & mgr );

    void loadAllConfigs();
    void loadConfig( const long configID );
    void executeLoadConfig( const std::string & sql );

    void loadAllCategoryItems();
    void loadCategoryItems( const long categoryID );
    void executeLoadItems ( const std::string & sql );

    void assignAllConfigsToDevices();
    void updateDevicesAssignedToConfig( const long configID );
    void updateConfigsContainingCategory( const long categoryID );
    void updateConfigForDevice( const long deviceID );
    void updateDeviceConfigurationAssignment( const long configID, const long deviceID );

public:

    CtiConfigManager();
    ~CtiConfigManager();

    void initialize( CtiDeviceManager & mgr );

    void processDBUpdate( const long            ID,
                          const std::string &   category,
                          const std::string &   objectType,
                          const int             updateType);

    void refreshConfigForDeviceId( const long deviceid );
};

