#include "precompiled.h"

#include "StandardControlPolicy.h"
#include "logger.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList StandardControlPolicy::getSupportedAttributes()
{
    return
    {
        Attribute::ControlMode,
        Attribute::TapDown,
        Attribute::TapUp,
        Attribute::TapPosition,
        Attribute::ForwardSetPoint,
        Attribute::ForwardBandwidth,
        Attribute::ReverseBandwidth,
        Attribute::ReverseSetPoint,
        Attribute::ReverseFlowIndicator
    };
}

Policy::Action StandardControlPolicy::TapUp()
{
    return makeStandardDigitalControl( getPointByAttribute( Attribute::TapUp ),
                                       "Raise Tap Position" );
}

Policy::Action StandardControlPolicy::TapDown()
{
    return makeStandardDigitalControl( getPointByAttribute( Attribute::TapDown ),
                                       "Lower Tap Position" );
}

Policy::Action StandardControlPolicy::AdjustSetPoint( const double changeAmount )
{
    LitePoint point = getPointByAttribute( getSetPointAttribute() );

    const std::string description = changeAmount > 0 ? "Raise Set Point" : "Lower Set Point";

    const double newSetPoint = getSetPointValue() + changeAmount;

    std::string commandString;

    const double pointMultiplier = point.getMultiplier();

    if ( pointMultiplier == 0.0 || pointMultiplier == 1.0 )
    {
        if ( pointMultiplier == 0.0 )
        {
            CTILOG_ERROR( dout, "Forward SetPoint Multiplier is 0.0, treating as 1.0" );
        }

        commandString = putvalueAnalogCommand( point, newSetPoint );
    }
    else
    {
        const long adjustedSetPoint = std::lround( newSetPoint / pointMultiplier );

        commandString = putvalueAnalogCommand( point, adjustedSetPoint );
    }

    return 
    {
        makeSignalTemplate( point.getPointId(), 0, description ),
        makeRequestTemplate( point.getPaoId(), commandString )
    };
}

double StandardControlPolicy::getSetPointValue()
{
    return getValueByAttribute( getSetPointAttribute() );
}

double StandardControlPolicy::getSetPointBandwidth()
{
    return getValueByAttribute( getBandwidthAttribute() );
}

long StandardControlPolicy::getTapPosition()
{
    return getValueByAttribute( Attribute::TapPosition );
}

}
}

