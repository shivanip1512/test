#include "precompiled.h"

#include "mgr_config.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"
#include "DeviceConfigLookup.h"
#include "debug_timer.h"

#include <boost/tuple/tuple.hpp>



namespace   {

static const std::string configSql = 
    "SELECT "
        "DC.DeviceConfigurationID, "
        "DCCM.DeviceConfigCategoryID, "
        "DC.Name "
    "FROM "
        "DeviceConfiguration DC "
        "LEFT OUTER JOIN DeviceConfigCategoryMap DCCM "
        "ON DC.DeviceConfigurationID = DCCM.DeviceConfigurationID";


static const std::string categorySql = 
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


static const std::string deviceSql = 
    "SELECT "
        "D.DeviceID, "
        "D.DeviceConfigurationID "
    "FROM "
        "DeviceConfigurationDeviceMap D";

}


CtiConfigManager::CtiConfigManager()
{
    // empty
}


CtiConfigManager::~CtiConfigManager()
{
    // empty
}


void CtiConfigManager::initialize()
{
    loadAllConfigs();
    loadAllCategoryItems();
    loadAllDeviceAssignments();
}


void CtiConfigManager::loadAllConfigs()
{
    _configurations.clear();

    executeLoadConfig( configSql );
}


void CtiConfigManager::loadConfig( const long configID )
{
    const std::string sql = configSql +
        " WHERE "
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
        long        configID;
        std::string name;

        rdr["DeviceConfigurationID"]  >> configID;
        rdr["Name"]                   >> name;

        ConfigurationMap::iterator  configIter;

        boost::tie( configIter, boost::tuples::ignore ) =
            _configurations.insert( configID, new Cti::Config::Configuration( configID, name ) );

        if ( ! rdr["DeviceConfigCategoryID"].isNull() )
        {
            long    categoryID;

            rdr["DeviceConfigCategoryID"] >> categoryID;

            configIter->second->addCategory( categoryID );
        }
    }
}


void CtiConfigManager::loadAllCategoryItems()
{
    _categories.clear();

    executeLoadItems( categorySql );
}


void CtiConfigManager::loadCategoryItems( const long categoryID )
{
    const std::string sql = categorySql +
        " WHERE "
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


void CtiConfigManager::loadAllDeviceAssignments()
{
    _deviceAssignments.clear();

    executeLoadDeviceAssignments( deviceSql );
}


void CtiConfigManager::loadDeviceAssignment( const long deviceID )
{
    const std::string sql = deviceSql +
        " WHERE "
            "D.DeviceID = " + CtiNumStr( deviceID );

    _deviceAssignments.erase( deviceID );

    executeLoadDeviceAssignments( sql );
}


void CtiConfigManager::executeLoadDeviceAssignments( const std::string & sql )
{
    Cti::Timing::DebugTimer timer( "loading device configuration assignments", DebugLevel & 0x80000000, 5.0 );

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while( rdr() )
    {
        long    deviceID,
                configID;

        rdr["DeviceID"]              >> deviceID;
        rdr["DeviceConfigurationID"] >> configID;

        _deviceAssignments[ deviceID ] = configID;
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
            _configurations.erase( ID );
            _cache.erase( ID );

            switch ( updateType )
            {
                case ChangeTypeUpdate:
                case ChangeTypeAdd:
                {
                    loadConfig( ID );
                    break;
                }
                case ChangeTypeDelete:
                default:
                {
                    break;
                }
            }
        }
        else if ( objectType == "category" )
        {
            _categories.erase( ID );

            // de-cache any config that contains this (category)ID

            for each ( ConfigurationMap::value_type & configIter in _configurations )
            {
                const Cti::Config::Configuration & config = *configIter.second;

                if ( config.hasCategory( ID ) )     // found (category)ID in config
                {
                    _cache.erase( config.getId() );
                }
            }

            switch ( updateType )
            {
                case ChangeTypeUpdate:
                case ChangeTypeAdd:
                {
                    loadCategoryItems( ID );
                    break;
                }
                case ChangeTypeDelete:
                default:
                {
                    break;
                }
            }
        }
        else if ( objectType == "device" )
        {
            _deviceAssignments.erase( ID );

            switch ( updateType )
            {
                case ChangeTypeUpdate:
                case ChangeTypeAdd:
                {
                    loadDeviceAssignment( ID );
                    break;
                }
                case ChangeTypeDelete:
                default:
                {
                    break;
                }
            }
        }
    }
}


Cti::Config::DeviceConfigSPtr   CtiConfigManager::buildConfig( const long configID, const DeviceTypes deviceType )
{
    // grab device type categories
    Cti::DeviceConfigLookup::CategoryNames deviceCategories = Cti::DeviceConfigLookup::Lookup( deviceType );

    // grab the config
    ConfigurationMap::const_iterator configSearch = _configurations.find( configID );

    if ( configSearch != _configurations.end() )
    {
        const Cti::Config::Configuration & config = *configSearch->second;

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

        return deviceConfiguration;
    }

    return Cti::Config::DeviceConfigSPtr();
}


Cti::Config::DeviceConfigSPtr   CtiConfigManager::fetchConfig( const long deviceID, const DeviceTypes deviceType )
{
    // lookup config ID from DeviceID

    DeviceToConfigAssignmentMap::const_iterator configIDSearch = _deviceAssignments.find( deviceID );

    if ( configIDSearch != _deviceAssignments.end() )
    {
        const long configID = configIDSearch->second;

        Cti::Config::DeviceConfigSPtr   config = _cache[ configID ][ deviceType ];     // inserts null on failure

        if ( ! config )
        {
            config = buildConfig( configID, deviceType );

            _cache[ configID ][ deviceType ] = config;
        }

        return config;
    }

    return Cti::Config::DeviceConfigSPtr();
}

