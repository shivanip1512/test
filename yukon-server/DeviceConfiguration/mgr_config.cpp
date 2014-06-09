#include "precompiled.h"

#include "mgr_config.h"
#include "dbaccess.h"
#include "database_connection.h"
#include "database_reader.h"
#include "DeviceConfigDescription.h"
#include "debug_timer.h"
#include "logger.h"
#include "std_helper.h"

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
    readers_writer_lock_t::writer_lock_guard_t guard(gConfigManager->_lock);

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
        long configID;

        rdr["DeviceConfigurationID"] >> configID;

        CategoryIds &configCategories = _configurations[configID];

        if ( ! rdr["DeviceConfigCategoryID"].isNull() )
        {
            long categoryID;

            rdr["DeviceConfigCategoryID"] >> categoryID;

            configCategories.insert( categoryID );
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

    const std::string orderedSql = sql +
            " ORDER BY"
                " C.DeviceConfigCategoryID ASC";

    Database::DatabaseConnection   connection;
    Database::DatabaseReader       rdr(connection, orderedSql);

    rdr.execute();

    struct CategoryBuffer
    {
        std::string type;

        std::map<std::string, std::string> items;
    };

    typedef std::map<long, CategoryBuffer> CategoriesById;
    CategoriesById categories;

    boost::optional<long> currentCategoryId;
    boost::optional<CategoryBuffer &> currentCategory;

    while( rdr() )
    {
        long categoryId;

        rdr["DeviceConfigCategoryID"] >> categoryId;

        if( categoryId != currentCategoryId )
        {
            currentCategoryId = categoryId;
            currentCategory.reset(categories[categoryId]);

            rdr["CategoryType"] >> currentCategory->type;
        }

        std::string itemName;

        rdr["ItemName"]  >> itemName;
        rdr["ItemValue"] >> currentCategory->items[itemName];
    }

    for each( const std::pair<long, CategoryBuffer> &pair in categories )
    {
        Config::CategorySPtr category = Config::Category::ConstructCategory( pair.second.type, pair.second.items );

        if( category.get() )
        {
            boost::assign::insert( _categories )( pair.first, category );
        }
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
        readers_writer_lock_t::writer_lock_guard_t guard(_lock);

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

            for each ( const ConfigurationToCategoriesMap::value_type & configIter in _configurations )
            {
                const CategoryIds & configCategories = configIter.second;

                if ( configCategories.count( ID ) )     // found (category)ID in config
                {
                    _cache.erase( configIter.first );
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


Config::DeviceConfigSPtr  ConfigManager::buildConfig( const long configID, const DeviceTypes deviceType )
{
    // grab device type categories
    DeviceConfigDescription::CategoryNames deviceCategories = DeviceConfigDescription::GetCategoryNamesForDeviceType( deviceType );

    // grab the config
    if ( const boost::optional<CategoryIds> configCategoryIds = mapFind( _configurations, configID ) )
    {
        Config::DeviceConfigSPtr  deviceConfiguration( new Config::DeviceConfig );

        // iterate the config's categories
        for each ( const long categoryID in *configCategoryIds )
        {
            // grab the category
            if ( const boost::optional<Config::CategorySPtr> category = mapFind( _categories, categoryID ) )
            {
                // is it one of our device's categories?
                if ( deviceCategories.count( (*category)->getType() ) )
                {
                    deviceConfiguration->addCategory( *category );
                }
            }
        }

        return deviceConfiguration;
    }

    return Config::DeviceConfigSPtr();
}


Config::DeviceConfigSPtr  ConfigManager::getConfigForIdAndType( const long deviceID, const DeviceTypes deviceType )
{
    return gConfigManager->fetchConfig(deviceID, deviceType);
}

Config::DeviceConfigSPtr  ConfigManager::fetchConfig( const long deviceID, const DeviceTypes deviceType )
{
    Config::DeviceConfigSPtr builtConfig;
    long configID;

    {
        readers_writer_lock_t::reader_lock_guard_t guard(_lock);

        // lookup config ID from DeviceID

        DeviceToConfigAssignmentMap::const_iterator configIDSearch = _deviceAssignments.find( deviceID );

        if ( configIDSearch == _deviceAssignments.end() )
        {
            //  Device not assigned to a config
            return Config::DeviceConfigSPtr();
        }

        configID = configIDSearch->second;

        //  Do a read-only search first - we only have the reader lock
        if( const boost::optional<DeviceTypeToDeviceConfigMap &> configsPerType = mapFindRef(_cache, configID) )
        {
            if( const boost::optional<Config::DeviceConfigSPtr> cachedConfig = mapFind(*configsPerType, deviceType) )
            {
                //  Found it in our cache
                return *cachedConfig;
            }
        }

        //  Didn't find it - build it while protected by the reader lock (fast, parallel)
        builtConfig = buildConfig( configID, deviceType );
    }

    {
        readers_writer_lock_t::writer_lock_guard_t guard(_lock);

        //  now insert it into the cache with the writer lock (slow, serialized access)
        _cache[ configID ][ deviceType ] = builtConfig;
    }

    return builtConfig;
}

}
