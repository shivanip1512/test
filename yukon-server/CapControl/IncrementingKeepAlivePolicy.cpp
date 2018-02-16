#include "precompiled.h"

#include "IncrementingKeepAlivePolicy.h"

extern unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;


namespace Cti           {
namespace CapControl    {

Policy::AttributeList IncrementingKeepAlivePolicy_SendAutoBlock::getSupportedAttributes() const
{
    return
    {
        Attribute::AutoBlockEnable,
        Attribute::AutoRemoteControl,
        Attribute::KeepAlive,
        Attribute::Terminate
    };
}

Policy::Actions IncrementingKeepAlivePolicy_SendAutoBlock::SendKeepAlive( const long keepAliveValue )
{
// Ignore incoming parameter...

    Actions actions;

    actions.emplace_back( WriteKeepAliveValue( getNextKeepAliveValue() ) );

    auto & signal = actions[0].first;

    signal->setPointValue( _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY );

    try
    {
        if ( getOperatingMode() == RemoteMode )
        {
            bool sendAutoBlock = needsAutoBlockEnable();

            if ( sendAutoBlock )
            {
                actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::AutoBlockEnable ),
                                                                  "Auto Block Enable" ) );
            }
            else
            {
                signal->setPointValue( 0 );
            }
        }
    }
    catch ( UninitializedPointValue & )
    {
        // getOperatingMode() threw because the device hasn't been scanned yet
        //  
        //  nothing to do here
    }

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy_SendAutoBlock::StopKeepAlive()
{
    Actions actions;

    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::Terminate ),
                                                      KeepAliveText ) );

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy_SendAutoBlock::EnableRemoteControl( const long keepAliveValue )
{
    Actions actions;

    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), readKeepAliveValue(), EnableRemoteControlText ),
                          nullptr );

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy_SendAutoBlock::DisableRemoteControl()
{
    Actions actions;

    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), 0, DisableRemoteControlText ),
                          nullptr );

    return actions;
}

long IncrementingKeepAlivePolicy_SendAutoBlock::readKeepAliveValue()
try
{
    return getValueByAttribute( Attribute::KeepAlive );
}
catch ( UninitializedPointValue & )
{
    return 0;      // can't read value -- send default of 0
}

long IncrementingKeepAlivePolicy_SendAutoBlock::getNextKeepAliveValue()
try
{
    long keepalive = getValueByAttribute( Attribute::KeepAlive );

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

bool IncrementingKeepAlivePolicy_SendAutoBlock::needsAutoBlockEnable()
try
{
    return getValueByAttribute( Attribute::AutoBlockEnable ) == 0.0; 
}
catch ( UninitializedPointValue & )
{
    return false;   // can't read value -- assume we don't need it
}

bool IncrementingKeepAlivePolicy_SuppressAutoBlock::needsAutoBlockEnable()
{
    return false;
}

}
}

