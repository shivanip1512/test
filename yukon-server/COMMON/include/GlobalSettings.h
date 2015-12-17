#pragma once

#include "dlldefs.h"
#include "critical_section.h"

#include <map>

class IM_EX_CTIBASE GlobalSettings 
{
private:
    typedef std::map<std::string, std::string> SettingMap;
    SettingMap _settingMap;

    /** Private constructor for the getSingleton process */
    GlobalSettings();

    /** Private static singleton constructor.  Reads settings from database. */
    static GlobalSettings& GlobalSettings::getSingleton();

    /** Private string accessor that initializes the singleton. */
    std::string getStringImpl( const std::string &name, std::string default );
    /** Private int accessor that initializes the singleton. */
    int getIntegerImpl( const std::string &name, int default );
    /** Private bool accessor that initializes the singleton. */
    bool getBooleanImpl( const std::string &name, bool default );

public:

    /** Public string accessor. */
    static std::string getString( const std::string &name, std::string default );
    /** Public string accessor. */
    static int getInteger( const std::string &name, int default );
    /** Public bool accessor. */
    static bool getBoolean( const std::string &name, bool default );
};

extern IM_EX_CTIBASE std::unique_ptr<GlobalSettings> gGlobalSettings;


