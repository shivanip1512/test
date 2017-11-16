#include "precompiled.h"

#include "ControlPolicy.h"
#include "std_helper.h"


namespace Cti           {
namespace CapControl    {


std::string resolveControlMode( ControlPolicy::ControlModes mode )
{
    static const std::map<ControlPolicy::ControlModes, std::string>   modeLookup
    {
        { ControlPolicy::ControlModes::LockedForward,         "LockedForward"         },
        { ControlPolicy::ControlModes::LockedReverse,         "LockedReverse"         },
        { ControlPolicy::ControlModes::ReverseIdle,           "ReverseIdle"           },
        { ControlPolicy::ControlModes::NeutralIdle,           "NeutralIdle"           },
        { ControlPolicy::ControlModes::Bidirectional,         "Bidirectional"         },
        { ControlPolicy::ControlModes::Cogeneration,          "Cogeneration"          },
        { ControlPolicy::ControlModes::ReactiveBidirectional, "ReactiveBidirectional" },
        { ControlPolicy::ControlModes::BiasBidirectional,     "BiasBidirectional"     }
    };

    return mapFindOrDefault( modeLookup, mode, "Unknown" );
}

/*
    If the ControlMode point is not mapped, we assume the regulator is in 'LockedForward' mode.
        This maintains backwards compatibility with 6.X versions.
*/ 
ControlPolicy::ControlModes ControlPolicy::getControlMode()
try
{
    static const std::map<long, ControlModes>   modeLookup
    {
        {   1, LockedForward            },
        {   2, LockedReverse            },
        {   3, ReverseIdle              },
        {   4, NeutralIdle              },
        {   5, Bidirectional            },
        {   6, Cogeneration             },
        {   7, ReactiveBidirectional    },
        {   8, BiasBidirectional        }
    };

    const long key = static_cast<long>( getValueByAttribute( Attribute::ControlMode ) );

    return mapFindOrDefault( modeLookup, key, LockedForward );
}
catch ( UninitializedPointValue & )
{
    return LockedForward;
}
catch ( FailedAttributeLookup & )
{
    return LockedForward;
}

/*
    If the ReverseFlowIndicator point is not mapped, we assume the regulator detects Forward Flow.
        This maintains backwards compatibility with 6.X versions.
*/ 
bool ControlPolicy::inReverseFlow() const
try
{
    return getValueByAttribute( Attribute::ReverseFlowIndicator ) > 0.0;
}
catch ( UninitializedPointValue & )
{
    return false;
}
catch ( FailedAttributeLookup & )
{
    return false;
}

Attribute ControlPolicy::getSetPointAttribute()
{
    switch ( getControlMode() )
    {
        case LockedReverse:
        {
            return Attribute::ReverseSetPoint;
        }
        case Cogeneration:
        {
            if ( inReverseFlow() )
            {
                return Attribute::ReverseSetPoint;
            }
        }
    }

    return Attribute::ForwardSetPoint;
}

Attribute ControlPolicy::getBandwidthAttribute()
{
    switch ( getControlMode() )
    {
        case LockedReverse:
        {
            return Attribute::ReverseBandwidth;
        }
        case Cogeneration:
        {
            if ( inReverseFlow() )
            {
                return Attribute::ReverseBandwidth;
            }
        }
    }

    return Attribute::ForwardBandwidth;
}


}
}

