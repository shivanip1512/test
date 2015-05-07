#include "precompiled.h"

#include "IncrementingKeepAlivePolicy.h"

extern unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;


namespace Cti           {
namespace CapControl    {

Policy::AttributeList IncrementingKeepAlivePolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::AutoBlockEnable,
        PointAttribute::AutoRemoteControl,
        PointAttribute::KeepAlive,
        PointAttribute::Terminate
    };
}

Policy::Actions IncrementingKeepAlivePolicy::SendKeepAlive( const long keepAliveValue )
{
// Ignore incoming parameter...

    Actions actions;

    actions.emplace_back( WriteKeepAliveValue( getNextKeepAliveValue() ) );

    auto & signal = actions[0].first;

    signal->setPointValue( _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY );

    if ( getOperatingMode() == RemoteMode )
    {
        bool sendAutoBlock = needsAutoBlockEnable();

        if ( sendAutoBlock )
        {
            actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( PointAttribute::AutoBlockEnable ),
                                                              "Auto Block Enable" ) );
        }
        else
        {
            signal->setPointValue( 0 );
        }
    }

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy::StopKeepAlive()
{
    Actions actions;

    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( PointAttribute::Terminate ),
                                                      KeepAliveText ) );

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy::EnableRemoteControl( const long keepAliveValue )
{
    Actions actions;

    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), readKeepAliveValue(), EnableRemoteControlText ),
                          nullptr );

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy::DisableRemoteControl()
{
    Actions actions;

    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), 0, DisableRemoteControlText ),
                          nullptr );

    return actions;
}

long IncrementingKeepAlivePolicy::readKeepAliveValue()
try
{
    return getValueByAttribute( PointAttribute::KeepAlive );
}
catch ( UninitializedPointValue & )
{
    return 0;      // can't read value -- send default of 0
}

long IncrementingKeepAlivePolicy::getNextKeepAliveValue()
try
{
    long keepalive = getValueByAttribute( PointAttribute::KeepAlive );

    if ( 0 <= keepalive && keepalive <= 32767 )
    {
        return ( keepalive + 1 ) % 32768;     // limit range and handle rollover.
    }

    return 0;       // if we got some value outside the normal range -- send default of 0
}
catch ( UninitializedPointValue & )
{
    return 0;      // can't read value -- send default of 0
}

bool IncrementingKeepAlivePolicy::needsAutoBlockEnable()
try
{
    return getValueByAttribute( PointAttribute::AutoBlockEnable ) == 0.0; 
}
catch ( UninitializedPointValue & )
{
    return false;   // can't read value -- assume we don't need it
}

}
}

