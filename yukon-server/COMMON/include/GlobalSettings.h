#pragma once

#include "dlldefs.h"
#include "critical_section.h"

#include <windows.h>
#include <map>

class IM_EX_CTIBASE GlobalSettings 
{
private:
    typedef std::map<std::string, std::string> SettingMap;
    SettingMap _settingMap;
    CtiCriticalSection _critical_section;

public:
    GlobalSettings();

    std::string getString( std::string name, std::string default );
    int getInteger( std::string name, int default );
    int getBoolean( std::string name, boolean default );

    static GlobalSettings* instance();
};


