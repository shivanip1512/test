#include "precompiled.h"

#include "LastControlReason.h"
#include "std_helper.h"
#include "dllyukon.h"



std::string LastControlReasonCbcDnp::getText( const long reason, const long stateGroup )
{
    return "Uninitialized";
}


// ------------------------------


std::string LastControlReasonCbc702x::getText( const long reason, const long stateGroup )
{
    static const std::map<long, std::string>    _lookup 
    {
        { 0x01, "Local"         },
        { 0x02, "Remote"        },
        { 0x04, "OvUv"          },
        { 0x08, "NeutralFault"  },
        { 0x10, "Schedule"      },
        { 0x20, "Digital"       },
        { 0x40, "Analog"        },
        { 0x80, "Temp"          }
    };

    if ( auto result = Cti::mapFind( _lookup, reason ) )
    {
        return *result;
    }

    return "Uninitialized";
}


// ------------------------------


std::string LastControlReasonCbc802x::getText( const long reason, const long stateGroup )
{
    return ResolveStateName( stateGroup, reason );
}

