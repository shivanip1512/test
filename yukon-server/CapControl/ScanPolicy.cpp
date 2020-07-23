#include "precompiled.h"

#include "ScanPolicy.h"
#include "Requests.h"

#include "std_helper.h"
#include "logger.h"

#include <boost/range/join.hpp>

namespace Cti::CapControl {

Policy::AttributeList ScanPolicy::getSupportedAttributes() const
{
    return boost::copy_range<AttributeList>(
               boost::range::join(
                   getRequiredAttributes(), 
                   getOptionalAttributes()));
}

Policy::AttributeList ScanPolicy::getOptionalAttributes() const
{
    return
    {
        // Empty by default, child classes can override if needed
    };
}

Policy::Actions ScanPolicy::IntegrityScan()
{
    Actions actions;

    for ( const auto & attribute : getRequiredAttributes() )
    {
        auto point = getPointByAttribute( attribute );

        actions.emplace_back( makeIntegrityScanCommand( point ) );
    }

    for ( const auto & attribute : getOptionalAttributes() )
    {
        if( auto point = mapFindRef( _pointMapping, attribute ) )
        {
            actions.emplace_back( makeIntegrityScanCommand( *point ) );
        }
        else
        {
            CTILOG_DEBUG( dout, "Point not found for attribute " << attribute );
        }
    }

    return actions;
}

Policy::Action ScanPolicy::makeIntegrityScanCommand( const LitePoint & point )
{
    return
    {
        makeSignalTemplate( point.getPointId(), 0, "Integrity Scan" ),
        makeRequestTemplate( point.getPaoId(), "scan integrity", RequestType::Scan )
    };
}

}