#include "precompiled.h"

#include "GlobalSettings.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
#include "std_helper.h"
#include "mutex.h"
#include "guard.h"

#include <boost/algorithm/string.hpp>
#include "boost/optional/optional.hpp"

/** Public string accessor. */
static CtiMutex g_mux;

namespace Detail {

template <typename T>
using SettingNamesFor = std::map<T, std::string>;

using Strings  = GlobalSettings::Strings;
using Integers = GlobalSettings::Integers;
using Booleans = GlobalSettings::Booleans;

static const SettingNamesFor<Strings> StringNames {
    { Strings::JmsBrokerHost, "JMS_BROKER_HOST" },
    { Strings::JmsBrokerPort, "JMS_BROKER_PORT" } };

static const SettingNamesFor<Integers> IntegerNames{
    { Integers::MaxInactivityDuration, "MAX_INACTIVITY_DURATION" },
    { Integers::ProducerWindowSize,    "PRODUCER_WINDOW_SIZE" },
    { Integers::MaxLogFileSize,        "MAX_LOG_FILE_SIZE" },
    { Integers::LogRetentionDays,      "LOG_RETENTION_DAYS" } };

static const SettingNamesFor<Booleans> BooleanNames{ /* empty */ };

}

/**
 * Global singleton.  This is initialized by the private accessors or could be 
 * initialized ahead of time by unit tests. 
 */
IM_EX_CTIBASE std::unique_ptr<GlobalSettings> gGlobalSettings;

/** Private constructor for the getSingleton process */
IM_EX_CTIBASE GlobalSettings::GlobalSettings() {
    reloadImpl();
}

/** Private static singleton constructor.  Reads settings from database. */
IM_EX_CTIBASE GlobalSettings& GlobalSettings::getSingleton()
{
    CTILOCKGUARD(CtiMutex, guard, g_mux);

    if( ! gGlobalSettings )
    {
        gGlobalSettings.reset( new GlobalSettings() );
    }
    return *gGlobalSettings;
}

/** Public string accessor. */
IM_EX_CTIBASE std::string GlobalSettings::getString( const Strings setting, std::string default )
{
    return getSingleton().getStringImpl(setting, default);
}

/** Private string accessor that initializes the singleton. */
IM_EX_CTIBASE std::string GlobalSettings::getStringImpl( const Strings setting, std::string default )
{
    if( const auto name = Cti::mapFind(Detail::StringNames, setting) )
    {
        if( const auto value = Cti::mapFind( _settingMap, *name ) )
        {
            return *value;
        }
    }

    return default;
}

/** Public int accessor. */
IM_EX_CTIBASE int GlobalSettings::getInteger( const Integers setting, int default )
{
    return getSingleton().getIntegerImpl( setting, default );
}

/** Private int accessor that initializes the singleton. */
IM_EX_CTIBASE int GlobalSettings::getIntegerImpl( const Integers setting, int default )
{
    if( const auto name = Cti::mapFind(Detail::IntegerNames, setting) )
    {
        if( const auto value = Cti::mapFind(_settingMap, *name) )
        {
            return stoi(*value);
        }
    }

    return default;
}

/** Public bool accessor. */
IM_EX_CTIBASE bool GlobalSettings::getBoolean( const Booleans setting, bool default )
{
    return getSingleton().getBooleanImpl( setting, default );
}

/** Private bool accessor that initializes the singleton. */
IM_EX_CTIBASE bool GlobalSettings::getBooleanImpl( const Booleans setting, bool default )
{
    if( const auto name = Cti::mapFind(Detail::BooleanNames, setting) )
    {
        if( const auto value = Cti::mapFind(_settingMap, *name) )
        {
            if( boost::iequals(*value, "false") || (*value).compare("0") )
            {
                return false;
            }

            return true;
        }
    }

    return default;
}

/** Public GlobalSetting table reload tool. */
IM_EX_CTIBASE void GlobalSettings::reload()
{
    getSingleton().reloadImpl();
}

/** Public GlobalSetting table reload tool implementation. */
IM_EX_CTIBASE void GlobalSettings::reloadImpl()
{
    //  Create temporary map to be swapped later
    SettingMap _tempMap;

    //  Load current settings
    static const std::string sqlCore = "SELECT GS.NAME,GS.VALUE FROM GlobalSetting GS ";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sqlCore);

    rdr.execute();

    while (rdr())
    {
        std::string name, value;
        rdr["NAME"] >> name;
        rdr["VALUE"] >> value;

        _tempMap[name] = value;
    }

    CTILOCKGUARD(CtiMutex, guard, g_mux);
    _settingMap.swap(_tempMap);
}