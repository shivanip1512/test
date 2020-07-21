#include "precompiled.h"

#include "IncrementingKeepAlivePolicy.h"
#include "Requests.h"

extern unsigned long _IVVC_REGULATOR_AUTO_MODE_MSG_DELAY;


namespace Cti::CapControl {
    
IncrementingKeepAlivePolicy::IncrementingKeepAlivePolicy( AutoBlock autoBlock )
    :   _autoBlockBehavior( autoBlock )
{
    // empty...
}

Policy::AttributeList IncrementingKeepAlivePolicy::getSupportedAttributes() const
{
    return
    {
        Attribute::AutoBlockEnable,
        Attribute::AutoRemoteControl,
        Attribute::KeepAlive,
        Attribute::Terminate
    };
}

Policy::Actions IncrementingKeepAlivePolicy::SendKeepAlive( const long keepAliveValue )
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
                                                                  "Auto Block Enable",
                                                                  RequestType::Heartbeat ) );
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

Policy::Actions IncrementingKeepAlivePolicy::StopKeepAlive()
{
    Actions actions;

    actions.emplace_back( makeStandardDigitalControl( getPointByAttribute( Attribute::Terminate ),
                                                      KeepAliveText,
                                                      RequestType::Heartbeat ) );

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy::EnableRemoteControl( const long keepAliveValue )
{
    Actions actions;

    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), readKeepAliveValue(), EnableRemoteControlText ),
                          PorterRequest::none() );

    return actions;
}

Policy::Actions IncrementingKeepAlivePolicy::DisableRemoteControl()
{
    Actions actions;

    LitePoint point = getPointByAttribute( Attribute::KeepAlive );

    actions.emplace_back( makeSignalTemplate( point.getPointId(), 0, DisableRemoteControlText ),
                          PorterRequest::none() );

    return actions;
}

long IncrementingKeepAlivePolicy::readKeepAliveValue()
try
{
    return getValueByAttribute( Attribute::KeepAlive );
}
catch ( UninitializedPointValue & )
{
    return 0;      // can't read value -- send default of 0
}

long IncrementingKeepAlivePolicy::getNextKeepAliveValue()
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

bool IncrementingKeepAlivePolicy::needsAutoBlockEnable()
try
{
    return _autoBlockBehavior == AutoBlock::Send && getValueByAttribute( Attribute::AutoBlockEnable ) == 0.0;
}
catch ( UninitializedPointValue & )
{
    return false;   // can't read value -- assume we don't need it
}

}