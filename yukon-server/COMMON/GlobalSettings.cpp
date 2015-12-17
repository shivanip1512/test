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

using namespace std;

/** Public string accessor. */
static CtiMutex g_mux;

/** 
 * Global singleton.  This is initialized by the private accessors or could be 
 * initialized ahead of time by unit tests. 
 */
IM_EX_CTIBASE std::unique_ptr<GlobalSettings> gGlobalSettings;

/** Private constructor for the getSingleton process */
IM_EX_CTIBASE GlobalSettings::GlobalSettings() {
    //  Load all settings to start
    static const string sqlCore = "SELECT GS.NAME,GS.VALUE FROM GlobalSetting GS ";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr( connection, sqlCore );

    rdr.execute();

    while( rdr() )
    {

        string name, value;
        rdr["NAME"] >> name;
        rdr["VALUE"] >> value;

        _settingMap[name] = value;
    }
}

/** Private static singleton constructor.  Reads settings from database. */
IM_EX_CTIBASE GlobalSettings& GlobalSettings::getSingleton()
{
    CtiLockGuard<CtiMutex> guard( g_mux );

    if( gGlobalSettings )
    {
        gGlobalSettings.reset( new GlobalSettings() );
    }
    return *gGlobalSettings;
}

/** Public string accessor. */
IM_EX_CTIBASE string GlobalSettings::getString( const std::string &name, string default )
{
    return getSingleton().getStringImpl( name, default );
}

/** Private string accessor that initializes the singleton. */
IM_EX_CTIBASE string GlobalSettings::getStringImpl( const std::string &name, string default )
{
    if( const auto value = Cti::mapFind( _settingMap, name ) )
    {
        return *value;
    }
    return default;
}

/** Public int accessor. */
IM_EX_CTIBASE int GlobalSettings::getInteger( const std::string &name, int default )
{
    return getSingleton().getIntegerImpl( name, default );
}

/** Private int accessor that initializes the singleton. */
IM_EX_CTIBASE int GlobalSettings::getIntegerImpl( const std::string &name, int default )
{
    if( const auto value = Cti::mapFind( _settingMap, name ) )
    {
        return stoi( *value );
    }
    return default;
}

/** Public bool accessor. */
IM_EX_CTIBASE bool GlobalSettings::getBoolean( const std::string &name, bool default )
{
    return getSingleton().getBooleanImpl( name, default );
}

/** Private bool accessor that initializes the singleton. */
IM_EX_CTIBASE bool GlobalSettings::getBooleanImpl( const std::string &name, bool default )
{
    const auto value = Cti::mapFind( _settingMap, name );
    if( !value ) 
    {
        return default;
    }

    if( boost::iequals( *value, "false" ) || ( *value ).compare( "0" ) ) 
    {
        return false;
    }

    return true;
}


