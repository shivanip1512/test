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

#include <string.h>
#include <stdio.h>

using namespace std;

IM_EX_CTIBASE GlobalSettings::GlobalSettings() {
    
    //  Load all settings to start
    static const string sqlCore = "SELECT GS.NAME,GS.VALUE "
        "FROM GlobalSetting GS ";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr( connection, sqlCore );

    rdr.execute();

    while( rdr() )
    {

        string name, value;
        rdr["NAME"] >> name;
        rdr["VALUE"] >> value;

        {
            _critical_section.acquire();
            _settingMap[name] = value;
        }
    }
}

IM_EX_CTIBASE string GlobalSettings::getString( std::string name, string default )
{
    _critical_section.acquire();
    const boost::optional<string> value = Cti::mapFind( _settingMap, name );
    if( value ) return *value;
    return default;
}

IM_EX_CTIBASE int GlobalSettings::getInteger( std::string name, int default )
{
    _critical_section.acquire();
    const boost::optional<string> value = Cti::mapFind( _settingMap, name );
    if( value ) return stoi( *value );
    return default;
}

IM_EX_CTIBASE int GlobalSettings::getBoolean( std::string name, boolean default )
{
    _critical_section.acquire();
    const boost::optional<string> value = Cti::mapFind( _settingMap, name );
    if( !value ) return default;
    if( boost::iequals( *value, "false" ) || (*value).compare( "0" ) ) return false;
    return true;
}


static CtiMutex g_mux;

IM_EX_CTIBASE GlobalSettings* GlobalSettings::instance()
{
    static GlobalSettings* s_instance;

    if( s_instance == 0 )
    {
        CtiLockGuard<CtiMutex> guard( g_mux );
        if( s_instance == 0 )
        {
            s_instance = new GlobalSettings();
        }
    }
    return s_instance;
}
