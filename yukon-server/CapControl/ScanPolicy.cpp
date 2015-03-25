#include "precompiled.h"

#include "ScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::Actions ScanPolicy::IntegrityScan()
{
    Actions actions;

    for ( const auto & attribute : _supportedAttributes )
    {
        LitePoint point = getPointByAttribute( attribute );

        actions.emplace_back( makeSignalTemplate( point.getPointId(), 0 ),
                              makeRequestTemplate( point.getPaoId(), "scan integrity" ) );
    }

    return actions;
}

}
}

