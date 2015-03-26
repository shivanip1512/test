#include "precompiled.h"

#include "StandardControlPolicy.h"


namespace Cti           {
namespace CapControl    {

StandardControlPolicy::StandardControlPolicy()
{
    _supportedAttributes = AttributeList
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
    return makeStandardDigitalControl( getPointByAttribute( PointAttribute::TapUp ) );
}

Policy::Action StandardControlPolicy::TapDown()
{
    return makeStandardDigitalControl( getPointByAttribute( PointAttribute::TapDown ) );
}

Policy::Action StandardControlPolicy::AdjustSetPoint( const double changeAmount )
{
    LitePoint point = getPointByAttribute( PointAttribute::ForwardSetPoint );

    const long pointOffset = point.getControlOffset()
                                ? point.getControlOffset()
                                : point.getPointOffset() % 10000;

    const double newSetPoint = getSetPointValue() + changeAmount;

    return 
    {
        makeSignalTemplate( point.getPointId(), 0 ),
        makeRequestTemplate( point.getPaoId(),
                             "putvalue analog " + std::to_string( pointOffset ) + " " + std::to_string( newSetPoint ) )
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

