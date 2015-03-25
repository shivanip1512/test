#include "precompiled.h"

#include "IncrementingKeepAlivePolicy.h"

extern unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;


namespace Cti           {
namespace CapControl    {

IncrementingKeepAlivePolicy::IncrementingKeepAlivePolicy()
{
    _supportedAttributes = AttributeList
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
            actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( PointAttribute::AutoBlockEnable ) ) );
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

    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( PointAttribute::Terminate ) ) );

    return actions;
}

Policy::Action IncrementingKeepAlivePolicy::EnableRemoteControl( const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    return 
    {
        makeSignalTemplate( point.getPointId(), readKeepAliveValue() ),
        nullptr
    };
}

Policy::Action IncrementingKeepAlivePolicy::DisableRemoteControl()
{
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    return 
    {
        makeSignalTemplate( point.getPointId(), 0 ),
        nullptr
    };
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

    keepalive = ( static_cast<long>( keepalive ) + 1 ) % 32768;     // limit range and handle rollover.

    return keepalive; 
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

