#include "precompiled.h"

#include "StandardControlPolicy.h"


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

    const double newSetPoint = getSetPointValue() + changeAmount;

    const std::string description = changeAmount > 0 ? "Raise Set Point" : "Lower Set Point";

    return 
    {
        makeSignalTemplate( point.getPointId(), 0, description ),
        makeRequestTemplate( point.getPaoId(), putvalueAnalogCommand( pointOffset, newSetPoint ) )
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

