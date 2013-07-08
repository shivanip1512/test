#include "precompiled.h"

#include "mgr_config.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"
#include "DeviceConfigLookup.h"
#include "mgr_device.h"
#include "debug_timer.h"

#include <boost/tuple/tuple.hpp>



CtiConfigManager::CtiConfigManager()
    :   isInitialized( false ),
        _devMgr( 0 )
{

}


CtiConfigManager::~CtiConfigManager()
{
    _devMgr = 0;
}


void CtiConfigManager::setDeviceManager( CtiDeviceManager & mgr )
{
    _devMgr = &mgr;
}


//This function was created so the initialization only happens once. I dont like it and
//will have it replaced as soon as I think of something better.
void CtiConfigManager::initialize(CtiDeviceManager &mgr)
{
    if ( ! isInitialized )
    {
        setDeviceManager( mgr );

        loadAllConfigs();
        loadAllCategoryItems();

        assignAllConfigsToDevices();

        isInitialized = true;
    }
}


void CtiConfigManager::refreshConfigForDeviceId( const long deviceid )
{
    updateConfigForDevice( deviceid );
}


void CtiConfigManager::loadAllConfigs()
{
    static const std::string sql = 
        "SELECT "
            "DC.DeviceConfigurationID, "
            "DCCM.DeviceConfigCategoryID, "
            "DC.Name "
        "FROM "
            "DeviceConfiguration DC "
            "JOIN DeviceConfigCategoryMap DCCM "
            "ON DC.DeviceConfigurationID = DCCM.DeviceConfigurationID";

    _configurations.clear();

    executeLoadConfig( sql );
}


void CtiConfigManager::loadConfig( const long configID )
{
    const std::string sql = 
        "SELECT "
            "DC.DeviceConfigurationID, "
            "DCCM.DeviceConfigCategoryID, "
            "DC.Name "
        "FROM "
            "DeviceConfiguration DC "
            "JOIN DeviceConfigCategoryMap DCCM "
            "ON DC.DeviceConfigurationID = DCCM.DeviceConfigurationID "
        "WHERE "
            "DC.DeviceConfigurationID = " + CtiNumStr( configID );

    _configurations.erase( configID );

    executeLoadConfig( sql );
}


void CtiConfigManager::executeLoadConfig( const std::string & sql ) 
{
    Cti::Timing::DebugTimer timer( "loading device configurations", DebugLevel & 0x80000000, 5.0 );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while( rdr() )
    {
        long        configID,
                    categoryID;
        std::string name;

        rdr["DeviceConfigurationID"]  >> configID;
        rdr["DeviceConfigCategoryID"] >> categoryID;
        rdr["Name"]                   >> name;

        ConfigurationMap::iterator  configIter;

        boost::tie( configIter, boost::tuples::ignore ) =
            _configurations.insert( configID, new Cti::Config::Configuration( configID, name ) );

        configIter->second->addCategory( categoryID );
    }
}


void CtiConfigManager::loadAllCategoryItems()
{
    static const std::string sql = 
        "SELECT "
            "C.DeviceConfigCategoryID, "
            "C.CategoryType, "
            "I.ItemName, "
            "I.ItemValue, "
            "C.Name "
        "FROM "
            "DeviceConfigCategory C "
            "JOIN DeviceConfigCategoryItem I "
            "ON C.DeviceConfigCategoryID = I.DeviceConfigCategoryID";

    _categories.clear();

    executeLoadItems( sql );
}


void CtiConfigManager::loadCategoryItems( const long categoryID )
{
    const std::string sql =
        "SELECT "
            "C.DeviceConfigCategoryID, "
            "C.CategoryType, "
            "I.ItemName, "
            "I.ItemValue, "
            "C.Name "
        "FROM "
            "DeviceConfigCategory C "
            "JOIN DeviceConfigCategoryItem I "
            "ON C.DeviceConfigCategoryID = I.DeviceConfigCategoryID "
        "WHERE "
            "C.DeviceConfigCategoryID = " + CtiNumStr( categoryID );

    _categories.erase( categoryID );

    executeLoadItems( sql );
}


void CtiConfigManager::executeLoadItems( const std::string & sql )
{
    Cti::Timing::DebugTimer timer( "loading device configuration category items", DebugLevel & 0x80000000, 5.0 );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while( rdr() )
    {
        long        categoryID;
        std::string categoryType,
                    itemName,
                    value,
                    name;

        rdr["DeviceConfigCategoryID"] >> categoryID;
        rdr["CategoryType"]           >> categoryType;
        rdr["ItemName"]               >> itemName;
        rdr["ItemValue"]              >> value;
        rdr["Name"]                   >> name;

        CategoryMap::iterator   categoryIter;

        boost::tie( categoryIter, boost::tuples::ignore ) =
            _categories.insert( categoryID, new Cti::Config::ConfigurationCategory( categoryID, name, categoryType ) );

        categoryIter->second->addItem( itemName, value );
    }
}


void CtiConfigManager::processDBUpdate( const long          ID,
                                        const std::string & category,
                                        const std::string & objectType,
                                        const int           updateType )
{
    if ( category == "Device Config" )
    {
        if ( objectType == "config" )
        {
            switch ( updateType )
            {
                case ChangeTypeUpdate:
                case ChangeTypeAdd:
                {
                    loadConfig( ID );
                    updateDevicesAssignedToConfig( ID );
                    break;
                }
                case ChangeTypeDelete:
                {
                    _configurations.erase( ID );
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        else if ( objectType == "category" )
        {
            switch ( updateType )
            {
                case ChangeTypeUpdate:
                case ChangeTypeAdd:
                {
                    loadCategoryItems( ID );
                    updateConfigsContainingCategory( ID );
                    break;
                }
                case ChangeTypeDelete:
                {
                    _categories.erase( ID );
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
        else if ( objectType == "device" )
        {
            switch ( updateType )
            {
                case ChangeTypeUpdate:
                case ChangeTypeAdd:
                {
                    updateConfigForDevice( ID );
                    break;
                }
                case ChangeTypeDelete:
                {
                    if ( CtiDeviceSPtr pDev = _devMgr->getDeviceByID( ID ) )
                    {
                        pDev->changeDeviceConfig( Cti::Config::DeviceConfigSPtr() );//set to null
                    }
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }
}


void CtiConfigManager::assignAllConfigsToDevices()
{
    static const std::string sql =
        "SELECT "
            "D.DeviceID, "
            "D.DeviceConfigurationID "
        "FROM "
            "DeviceConfigurationDeviceMap D";

    Cti::Timing::DebugTimer timer( "assigning configurations to devices", DebugLevel & 0x80000000, 5.0 );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while ( rdr() )
    {
        long    deviceID,
                configID;

        rdr["DeviceID"]              >> deviceID;
        rdr["DeviceConfigurationID"] >> configID;

        updateDeviceConfigurationAssignment( configID, deviceID );
    }
}


void CtiConfigManager::updateDevicesAssignedToConfig( const long configID )
{
    const std::string sql =
        "SELECT "
            "D.DeviceID "
        "FROM "
            "DeviceConfigurationDeviceMap D "
        "WHERE "
            "D.DeviceConfigurationID = " + CtiNumStr( configID );

    Cti::Timing::DebugTimer timer( "assigning configuration to devices", DebugLevel & 0x80000000, 5.0 );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while ( rdr() )
    {
        long    deviceID;

        rdr["DeviceID"] >> deviceID;

        updateDeviceConfigurationAssignment( configID, deviceID );
    }
}


void CtiConfigManager::updateConfigsContainingCategory( const long categoryID )
{
    const std::string sql =
        "SELECT "
            "D.DeviceConfigurationID "
        "FROM "
            "DeviceConfigCategoryMap D "
        "WHERE "
            "D.DeviceConfigCategoryID = " + CtiNumStr( categoryID );

    std::set<long>  configIDs;

    {
        Cti::Database::DatabaseConnection   connection;
        Cti::Database::DatabaseReader       rdr(connection, sql);

        rdr.execute();

        while ( rdr() )
        {
            long    configID;

            rdr["DeviceConfigurationID"] >> configID;

            configIDs.insert( configID );
        }
    }

    for each ( const long ID in configIDs )
    {
        updateDevicesAssignedToConfig( ID );
    }
}


void CtiConfigManager::updateConfigForDevice( const long deviceID )
{
    const std::string sql =
        "SELECT "
            "D.DeviceConfigurationID "
        "FROM "
            "DeviceConfigurationDeviceMap D "
        "WHERE "
            "D.DeviceID = " + CtiNumStr( deviceID );

    Cti::Timing::DebugTimer timer( "assigning configuration to device", DebugLevel & 0x80000000, 5.0 );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while ( rdr() )
    {
        long    configID;

        rdr["DeviceConfigurationID"] >> configID;

        updateDeviceConfigurationAssignment( configID, deviceID );
    }
}


void CtiConfigManager::updateDeviceConfigurationAssignment( const long configID, const long deviceID )
{
    if ( CtiDeviceSPtr pDev = _devMgr->getDeviceByID( deviceID ) )
    {
        // grab device type categories
        Cti::DeviceConfigLookup::CategoryNames deviceCategories =
            Cti::DeviceConfigLookup::Lookup( static_cast<DeviceTypes>( pDev->getType() ) );

        // grab the config
        ConfigurationMap::const_iterator configSearch = _configurations.find( configID );

        if ( configSearch != _configurations.end() )
        {
            const Cti::Config::Configuration & config = *configSearch->second;

            // this is the guy we'll assign to the device...
            Cti::Config::DeviceConfigSPtr    deviceConfiguration( new Cti::Config::DeviceConfig( config.getId(), config.getName() ) );

            // iterate the configs categories
            for each ( const long categoryID in config )
            {
                // grab the category
                CategoryMap::const_iterator categorySearch = _categories.find( categoryID );

                if ( categorySearch != _categories.end() )
                {
                    const Cti::Config::ConfigurationCategory & category = *categorySearch->second;

                    // is it one of our actual physical devices categories?
                    if ( deviceCategories.find( category.getType() ) != deviceCategories.end() )
                    {
                        // yes.. merge his items into the actual device config
                        for each ( Cti::Config::ConfigurationCategory::value_type item in category )
                        {
                            deviceConfiguration->insertValue( item.first, item.second );
                        }
                    }
                }
            }

            pDev->changeDeviceConfig( deviceConfiguration );
        }
        else
        {
            CtiLockGuard<CtiLogger> guard( dout );

            dout << CtiTime() << " - *** ERROR *** Invalid Configuration ID - configuration unchanged." << std::endl;
        }
    }
}

