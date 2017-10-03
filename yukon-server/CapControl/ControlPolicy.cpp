#include "precompiled.h"

#include "ControlPolicy.h"
#include "std_helper.h"


namespace Cti           {
namespace CapControl    {


/*
    This can throw a 'FailedAttributeLookup' exception which needs to be handled
        in application code.
*/
ControlPolicy::ControlModes ControlPolicy::getControlMode()
try
{
    // actual values need to be determined...
    static const std::map<long, ControlModes>   modeLookup
    {
        {   0, LockedForward            },
        {   1, LockedReverse            },
        {   2, ReverseIdle              },
        {   3, Bidirectional            },
        {   4, NeutralIdle              },
        {   5, Cogeneration             },
        {   6, ReactiveBidirectional    },
        {   7, BiasBidirectional        }
    };

    const long key = static_cast<long>( getValueByAttribute( Attribute::ControlMode ) );

    return mapFindOrDefault( modeLookup, key, Unknown );
}
catch ( UninitializedPointValue & )
{
    return Unknown;
}


}
}

