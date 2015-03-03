#include "precompiled.h"

#include "Policy.h"
#include "std_helper.h"
#include "msg_pdata.h"


namespace Cti           {
namespace CapControl    {

void Policy::loadAttributes( AttributeService & service, const long paoID )
{
    for ( const PointAttribute & attribute : supportedAttributes )
    {
        LitePoint point = service.getPointByPaoAndAttribute( paoID, attribute );

        if ( point.getPointType() != InvalidPointType )
        {
            pointMapping.insert( std::make_pair( attribute, point ) );
            pointIDs.insert( point.getPointId() );
        }
    }
}

LitePoint Policy::getPointByAttribute( const PointAttribute & attribute ) const
{
    boost::optional<LitePoint>  point = mapFind( pointMapping, attribute );

    if ( ! point )
    {
        throw attribute;
    }

    return *point;
}

void Policy::updatePointData( CtiPointDataMsg * message )
{
    if ( pointIDs.count( message->getId() ) )
    {
        pointValues.updatePointValue( message );
    }
}

Policy::IDSet Policy::getRegistrationPointIDs() const
{
    return pointIDs;
}

}
}

