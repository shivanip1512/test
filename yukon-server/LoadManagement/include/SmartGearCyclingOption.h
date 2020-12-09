#pragma once

/*
    All supported cycling control options for "Smart Gears", currently only
        used by EatonCloud but could be adapted to others.
*/

namespace Cti::LoadManagement
{

enum class SmartGearCyclingOption
{
    Unsupported,

    StandardCycle,
    TrueCycle,
    SmartCycle
};

SmartGearCyclingOption resolveCyclingOption( const std::string & key );

}

