#include "precompiled.h"

#include "ScanPolicy.h"


namespace Cti           {
namespace CapControl    {

Policy::Actions ScanPolicy::IntegrityScan()
{
    Actions actions;

    for ( const auto & attribute : getSupportedAttributes() )
    {
        actions.emplace_back( makeIntegrityScanCommand( attribute ) );
    }

    return actions;
}

Policy::Action ScanPolicy::makeIntegrityScanCommand( const PointAttribute & attribute )
{
    LitePoint point = getPointByAttribute( attribute );

    return
    {
        makeSignalTemplate( point.getPointId(), 0, "Integrity Scan" ),
        makeRequestTemplate( point.getPaoId(), "scan integrity" )
    };
}

}
}

