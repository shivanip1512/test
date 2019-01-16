#pragma once

#include "dlldefs.h"
#include "critical_section.h"

#include <map>

class IM_EX_CTIBASE GlobalSettings 
{
public:

    enum class Strings {
        JmsBrokerHost,
        JmsBrokerPort
    };

    enum class Integers {
        ProducerWindowSize,
        MaxInactivityDuration,
        MaxLogFileSize,
    };

    enum class Booleans {
    };

    static std::string getString ( Strings  setting, std::string default );
    static int         getInteger( Integers setting, int default );
    static bool        getBoolean( Booleans setting, bool default );
    
    static void reload();

private:

    typedef std::map<std::string, std::string> SettingMap;
    SettingMap _settingMap;

    /** Private constructor for the getSingleton process */
    GlobalSettings();

    /** Private static singleton constructor.  Reads settings from database. */
    static GlobalSettings& GlobalSettings::getSingleton();

    /** Private accessors that initialize the singleton. */
    auto getStringImpl (Strings  setting, std::string default) -> std::string;
    auto getIntegerImpl(Integers setting, int default)         -> int;
    auto getBooleanImpl(Booleans setting, bool default)        -> bool;

    /** Private GlobalSetting reload tool implementation. */
    void reloadImpl();
};

extern IM_EX_CTIBASE std::unique_ptr<GlobalSettings> gGlobalSettings;


