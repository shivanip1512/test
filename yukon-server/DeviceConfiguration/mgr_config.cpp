#include "precompiled.h"

#include "mgr_config.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"
#include "DeviceConfigLookup.h"
#include "debug_timer.h"
#include "logger.h"

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


namespace Cti {


IM_EX_CONFIG std::auto_ptr<ConfigManager> gConfigManager(new ConfigManager);


void ConfigManager::initialize()
{
    gConfigManager->loadAllConfigs();
    gConfigManager->loadAllCategoryItems();
    gConfigManager->loadAllDeviceAssignments();
}


void ConfigManager::loadAllConfigs()
{
    _configurations.clear();

    executeLoadConfig( configSql );
}


void ConfigManager::loadConfig( const long configID )
{
    const std::string sql = configSql +
        " WHERE "
            "DC.DeviceConfigurationID = " + CtiNumStr( configID );

    _configurations.erase( configID );

    executeLoadConfig( sql );
}


void ConfigManager::executeLoadConfig( const std::string & sql )
{
    Timing::DebugTimer timer( "loading device configurations", DebugLevel & 0x80000000, 5.0 );

    Database::DatabaseConnection   connection;
    Database::DatabaseReader       rdr(connection, sql);

    rdr.execute();

    while( rdr() )
    {
        long        configID;
        std::string name;

        rdr["DeviceConfigurationID"]  >> configID;
        rdr["Name"]                   >> name;

        ConfigurationMap::iterator  configIter;

        boost::tie( configIter, boost::tuples::ignore ) =
            _configurations.insert( configID, new Config::Configuration( configID, name ) );

        if ( ! rdr["DeviceConfigCategoryID"].isNull() )
        {
            long    categoryID;

            rdr["DeviceConfigCategoryID"] >> categoryID;

            configIter->second->addCategory( categoryID );
        }
    }
}


void ConfigManager::loadAllCategoryItems()
{
    _categories.clear();

    executeLoadItems( categorySql );
}


void ConfigManager::loadCategoryItems( const long categoryID )
{
    const std::string sql = categorySql +
        " WHERE "
            "C.DeviceConfigCategoryID = " + CtiNumStr( categoryID );

    _categories.erase( categoryID );

    executeLoadItems( sql );
}


void ConfigManager::executeLoadItems( const std::string & sql )
{
    Timing::DebugTimer timer( "loading device configuration category items", DebugLevel & 0x80000000, 5.0 );

    Database::DatabaseConnection   connection;
    Database::DatabaseReader       rdr(connection, sql);

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
            _categories.insert( categoryID, new Config::ConfigurationCategory( categoryID, name, categoryType ) );

        categoryIter->second->addItem( itemName, value );
    }
}


void ConfigManager::loadAllDeviceAssignments()
{
    _deviceAssignments.clear();

    executeLoadDeviceAssignments( deviceSql );
}


void ConfigManager::loadDeviceAssignment( const long deviceID )
{
    const std::string sql = deviceSql +
        " WHERE "
            "D.DeviceID = " + CtiNumStr( deviceID );

    _deviceAssignments.erase( deviceID );

    executeLoadDeviceAssignments( sql );
}


void ConfigManager::executeLoadDeviceAssignments( const std::string & sql )
{
    Timing::DebugTimer timer( "loading device configuration assignments", DebugLevel & 0x80000000, 5.0 );

    Database::DatabaseConnection   connection;
    Database::DatabaseReader       rdr(connection, sql);

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


void ConfigManager::handleDbChange( const long          ID,
                                     const std::string & category,
                                     const std::string & objectType,
                                     const int           updateType )
{
    gConfigManager->processDBUpdate(ID, category, objectType, updateType);
}

void ConfigManager::processDBUpdate( const long          ID,
                                     const std::string & category,
                                     const std::string & objectType,
                                     const int           updateType )
{
    const bool isUpdateOrAdd =
            (updateType == ChangeTypeUpdate ||
             updateType == ChangeTypeAdd);

    if ( category == "Device Config" )
    {
        if ( objectType == "config" )
        {
            _configurations.erase( ID );
            _cache.erase( ID );

            if( isUpdateOrAdd )
            {
                loadConfig( ID );
            }
        }
        else if ( objectType == "category" )
        {
            _categories.erase( ID );

            // de-cache any config that contains this (category)ID

            for each ( ConfigurationMap::value_type & configIter in _configurations )
            {
                const Config::Configuration & config = *configIter.second;

                if ( config.hasCategory( ID ) )     // found (category)ID in config
                {
                    _cache.erase( config.getId() );
                }
            }

            if( isUpdateOrAdd )
            {
                loadCategoryItems( ID );
            }
        }
        else if ( objectType == "device" )
        {
            _deviceAssignments.erase( ID );

            if( isUpdateOrAdd )
            {
                loadDeviceAssignment( ID );
            }
        }
    }
}


Config::DeviceConfigSPtr   ConfigManager::buildConfig( const long configID, const DeviceTypes deviceType )
{
    // grab device type categories
    DeviceConfigLookup::CategoryNames deviceCategories = DeviceConfigLookup::Lookup( deviceType );

    // grab the config
    ConfigurationMap::const_iterator configSearch = _configurations.find( configID );

    if ( configSearch != _configurations.end() )
    {
        const Config::Configuration & config = *configSearch->second;

        Config::DeviceConfigSPtr    deviceConfiguration( new Config::DeviceConfig( config.getId(), config.getName() ) );

        // iterate the configs categories
        for each ( const long categoryID in config )
        {
            // grab the category
            CategoryMap::const_iterator categorySearch = _categories.find( categoryID );

            if ( categorySearch != _categories.end() )
            {
                const Config::ConfigurationCategory & category = *categorySearch->second;

                // is it one of our actual physical devices categories?
                if ( deviceCategories.find( category.getType() ) != deviceCategories.end() )
                {
                    // do we have all of the category items?
                    DeviceConfigLookup::CategoryFieldIterPair   thePair =
                        DeviceConfigLookup::equal_range( category.getType() );

                    for ( ; thePair.first != thePair.second ; ++thePair.first )
                    {
                        const std::string & requiredFieldName = thePair.first->second;

                        Config::ConfigurationCategory::const_iterator fieldName = category.find( requiredFieldName );

                        if ( fieldName != category.end() )
                        {
                            deviceConfiguration->insertValue( fieldName->first, fieldName->second );
                        }
                        else
                        {
                            // print error message
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " - Error: missing configuration item: " << requiredFieldName << " in configuration: " << config.getName() << std::endl;

                            return Config::DeviceConfigSPtr();
                        }
                    }
                }
            }
        }

        return deviceConfiguration;
    }

    return Config::DeviceConfigSPtr();
}


Config::DeviceConfigSPtr ConfigManager::getConfigForIdAndType( const long deviceID, const DeviceTypes deviceType )
{
    return gConfigManager->fetchConfig(deviceID, deviceType);
}

Config::DeviceConfigSPtr   ConfigManager::fetchConfig( const long deviceID, const DeviceTypes deviceType )
{
    // lookup config ID from DeviceID

    DeviceToConfigAssignmentMap::const_iterator configIDSearch = _deviceAssignments.find( deviceID );

    if ( configIDSearch != _deviceAssignments.end() )
    {
        const long configID = configIDSearch->second;

        Config::DeviceConfigSPtr   config = _cache[ configID ][ deviceType ];     // inserts null on failure

        if ( ! config )
        {
            config = buildConfig( configID, deviceType );

            _cache[ configID ][ deviceType ] = config;
        }

        return config;
    }

    return Config::DeviceConfigSPtr();
}

}
