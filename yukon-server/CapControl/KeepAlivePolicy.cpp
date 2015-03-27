#include "precompiled.h"

#include "KeepAlivePolicy.h"


namespace Cti           {
namespace CapControl    {

KeepAlivePolicy::OperatingMode KeepAlivePolicy::getOperatingMode()
{
    double value = getValueByAttribute( PointAttribute::AutoRemoteControl );

    return ( value == 1.0 )
                ? RemoteMode
                : LocalMode;
}

Policy::Action KeepAlivePolicy::WriteKeepAliveValue( const long keepAliveValue )
{
    LitePoint point = getPointByAttribute( PointAttribute::KeepAlive );

    const long pointOffset =
        point.getControlOffset() ?
            point.getControlOffset() :
            point.getPointOffset() % 10000;

    return
    {
        makeSignalTemplate( point.getPointOffset(), 0 ),
        makeRequestTemplate( point.getPaoId(),
                             "putvalue analog " + std::to_string( pointOffset ) + " " + std::to_string( keepAliveValue ) )
    };
}

}
}
