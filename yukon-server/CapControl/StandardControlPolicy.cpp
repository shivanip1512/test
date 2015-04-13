#include "precompiled.h"

#include "StandardControlPolicy.h"
#include "logger.h"


namespace Cti           {
namespace CapControl    {

Policy::AttributeList StandardControlPolicy::getSupportedAttributes()
{
    return
    {
        PointAttribute::TapDown,
        PointAttribute::TapUp,
        PointAttribute::TapPosition,
        PointAttribute::ForwardSetPoint,
        PointAttribute::ForwardBandwidth
    };
}

Policy::Action StandardControlPolicy::TapUp()
{
    return makeStandardDigitalControl( getPointByAttribute( PointAttribute::TapUp ),
                                       "Raise Tap Position" );
}

Policy::Action StandardControlPolicy::TapDown()
{
    return makeStandardDigitalControl( getPointByAttribute( PointAttribute::TapDown ),
                                       "Lower Tap Position" );
}

Policy::Action StandardControlPolicy::AdjustSetPoint( const double changeAmount )
{
    LitePoint point = getPointByAttribute( PointAttribute::ForwardSetPoint );

    const long pointOffset = point.getControlOffset()
                                ? point.getControlOffset()
                                : point.getPointOffset() % 10000;

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

        commandString = putvalueAnalogCommand( pointOffset, newSetPoint );
    }
    else
    {
        const long adjustedSetPoint = std::lround( newSetPoint / pointMultiplier );

        commandString = putvalueAnalogCommand( pointOffset, adjustedSetPoint );
    }

    return 
    {
        makeSignalTemplate( point.getPointId(), 0, description ),
        makeRequestTemplate( point.getPaoId(), commandString )
    };
}

double StandardControlPolicy::getSetPointValue()
{
    return getValueByAttribute( PointAttribute::ForwardSetPoint );
}

double StandardControlPolicy::getSetPointBandwidth()
{
    return getValueByAttribute( PointAttribute::ForwardBandwidth );
}

long StandardControlPolicy::getTapPosition()
{
    return getValueByAttribute( PointAttribute::TapPosition );
}

}
}

