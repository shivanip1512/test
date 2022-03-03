#include "precompiled.h"

#include "SmartGearCyclingOption.h"
#include "std_helper.h"

namespace Cti::LoadManagement
{

SmartGearCyclingOption resolveCyclingOption( const std::string & key )
{
    static const std::map<std::string, SmartGearCyclingOption>  resolver
    {
        { "STANDARD",       SmartGearCyclingOption::StandardCycle   },
        { "TRUE_CYCLE",     SmartGearCyclingOption::TrueCycle       },
        { "SMART_CYCLE",    SmartGearCyclingOption::SmartCycle      }
    };

    return mapFindOrDefault( resolver, key, SmartGearCyclingOption::Unsupported );
}

}

