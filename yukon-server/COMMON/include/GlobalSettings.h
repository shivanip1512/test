#pragma once

#include "dlldefs.h"
#include "critical_section.h"

#include <map>

namespace Cti {

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
        LogRetentionDays,
        FdrDnpSlaveApplicationFragmentSize
    };

    enum class Booleans {
    };

    static std::string getString ( Strings  setting, std::string default );
    static int         getInteger( Integers setting, int default );
    static bool        getBoolean( Booleans setting, bool default );
    
    static void reload();

protected:

    /** Private accessors that initialize the singleton. */
    virtual auto getStringImpl(Strings  setting, std::string default)->std::string;
    virtual auto getIntegerImpl(Integers setting, int default)         -> int;
    virtual auto getBooleanImpl(Booleans setting, bool default)        -> bool;

    /** Protected constructor for the getSingleton process */
    GlobalSettings();

private:

    typedef std::map<std::string, std::string> SettingMap;
    SettingMap _settingMap;

    /** Private static singleton constructor.  Reads settings from database. */
    static GlobalSettings& getSingleton();

    /** Private GlobalSetting reload tool implementation. */
    void reloadImpl();
};

extern IM_EX_CTIBASE std::unique_ptr<GlobalSettings> gGlobalSettings;

}