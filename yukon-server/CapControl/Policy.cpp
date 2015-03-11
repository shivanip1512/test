#include "precompiled.h"

#include "Policy.h"
#include "std_helper.h"
#include "msg_pdata.h"


namespace Cti           {
namespace CapControl    {

void Policy::loadAttributes( AttributeService & service, const long paoID )
{
    for ( const auto & attribute : supportedAttributes )
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
        throw FailedAttributeLookup( attribute );
    }

    return *point;
}

double Policy::getValueByAttribute( const PointAttribute & attribute ) const
{
    double currentValue;

    if ( ! pointValues.getPointValue( getPointByAttribute( attribute ).getPointId(), currentValue ) )
    {
        throw UninitializedPointValue( attribute );
    }

    return currentValue;
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


FailedAttributeLookup::FailedAttributeLookup( const PointAttribute & attribute )
    :   std::exception(),
        _attribute( attribute ),
        _description( "Failed Point Attribute Lookup: '" + _attribute.name() + "'" )
{
    // empty
}

const char * FailedAttributeLookup::what() const
{
    return _description.c_str();
}

const PointAttribute & FailedAttributeLookup::attribute() const
{
    return _attribute;
}


UninitializedPointValue::UninitializedPointValue( const PointAttribute & attribute )
    :   std::exception(),
        _attribute( attribute ),
        _description( "Uninitialized Point Value for Attribute: '" + _attribute.name() + "'" )
{
    // empty
}

const char * UninitializedPointValue::what() const
{
    return _description.c_str();
}

const PointAttribute & UninitializedPointValue::attribute() const
{
    return _attribute;
}

}
}

