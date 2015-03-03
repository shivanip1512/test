#include "precompiled.h"

#include "StandardControlPolicy.h"
#include "msg_signal.h"
#include "msg_pcrequest.h"

extern unsigned long _MSG_PRIORITY;


namespace Cti           {
namespace CapControl    {

StandardControlPolicy::StandardControlPolicy()
{
    supportedAttributes = AttributeList
    {
        PointAttribute::TapDown,
        PointAttribute::TapUp,
        PointAttribute::TapPosition,
        PointAttribute::ForwardSetPoint,
        PointAttribute::ForwardBandwidth
    };
}

ControlPolicy::ControlRequest StandardControlPolicy::TapUp()
{
    return manualTapControl( getPointByAttribute( PointAttribute::TapUp ) );
}

ControlPolicy::ControlRequest StandardControlPolicy::TapDown()
{
    return manualTapControl( getPointByAttribute( PointAttribute::TapDown ) );
}

ControlPolicy::ControlRequest StandardControlPolicy::manualTapControl( const LitePoint & point )
{
    return
    {
        makeSignalTemplate( point.getPointId() ),
        makeRequestTemplate( point.getPaoId(),
                             point.getStateOneControl() + " select pointid " + std::to_string( point.getPointId() ) )
    };
}

ControlPolicy::ControlRequest StandardControlPolicy::AdjustSetPoint( const double changeAmount )
{
    LitePoint point = getPointByAttribute( PointAttribute::ForwardSetPoint );

    const long pointOffset = point.getControlOffset()
                                ? point.getControlOffset()
                                : point.getPointOffset() % 10000;

    const double newSetPoint = getSetPointValue() + changeAmount;

    return 
    {
        makeSignalTemplate( point.getPointId() ),
        makeRequestTemplate( point.getPaoId(),
                             "putvalue analog " + std::to_string( pointOffset ) + " " + std::to_string( newSetPoint ) )
    };
}

CtiSignalMsg * StandardControlPolicy::makeSignalTemplate( const long ID )
{
    return new CtiSignalMsg( ID,
                             0,
                             "",
                             "",
                             CapControlLogType,
                             SignalEvent,
                             "cap control" );
}

CtiRequestMsg * StandardControlPolicy::makeRequestTemplate( const long ID, const std::string & command )
{
    CtiRequestMsg * request = new CtiRequestMsg( ID, command );

    request->setMessagePriority( _MSG_PRIORITY );
    request->setSOE( 5 );

    return request;
}

double StandardControlPolicy::getSetPointValue()
{
    double currentSetPoint = 120.0;     // sensible default in the case of no reading...

    pointValues.getPointValue( getPointByAttribute( PointAttribute::ForwardSetPoint ).getPointId(), currentSetPoint );

    return currentSetPoint;
}

double StandardControlPolicy::getSetPointBandwidth()
{
    double currentBandwidth = 2.0;      // sensible default in the case of no reading...

    pointValues.getPointValue( getPointByAttribute( PointAttribute::ForwardBandwidth ).getPointId(), currentBandwidth );

    return currentBandwidth;
}



}
}

