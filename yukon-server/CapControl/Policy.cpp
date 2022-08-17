#include "precompiled.h"

#include "Policy.h"
#include "std_helper.h"
#include "msg_pdata.h"
#include "ccutil.h"


extern unsigned long _MSG_PRIORITY;


namespace Cti           {
namespace CapControl    {

void Policy::loadAttributes( AttributeService & service, const long paoID )
{
    auto supportedAttributes = getSupportedAttributes();
    if( supportedAttributes.size() > 0 )
    {
        const auto pointMapping( std::move( service.getPointsByPaoAndAttributes( paoID, supportedAttributes ) ) );

        for (const auto & map : pointMapping)
        {
            if (map.second.getPointType() != InvalidPointType)
            {
                _pointMapping.insert(map);
                _pointIDs.insert(map.second.getPointId());
            }
        }
    }
}

LitePoint Policy::getPointByAttribute( const Attribute & attribute ) const
{
    boost::optional<LitePoint>  point = mapFind( _pointMapping, attribute );

    if ( ! point )
    {
        throw FailedAttributeLookup( attribute );
    }

    return *point;
}

LitePoint Policy::getPointByAttribute( const Attribute & attribute, const CtiCCTwoWayPoints & twoWayPoints ) const
{
    auto point = twoWayPoints.findPointByAttribute( attribute );

    if ( ! point )
    {
        throw FailedAttributeLookup( attribute );
    }

    return *point;
}

double Policy::getValueByAttribute( const Attribute & attribute ) const
{
    double currentValue;

    if ( ! _pointValues.getPointValue( getPointByAttribute( attribute ).getPointId(), currentValue ) )
    {
        throw UninitializedPointValue( attribute );
    }

    return currentValue;
}

double Policy::getValueByAttribute( const Attribute & attribute, const CtiCCTwoWayPoints & twoWayPoints ) const
{
    auto value = twoWayPoints.findPointValueByAttribute(attribute);

    if ( ! value )
    {
        throw UninitializedPointValue( attribute );
    }

    return *value;
}

PointValue Policy::getCompleteValueByAttribute( const Attribute & attribute ) const
{
    return _pointValues.getCompletePointInfo( getPointByAttribute( attribute ).getPointId() );
}

void Policy::updatePointData( const CtiPointDataMsg & message )
{
    if ( _pointIDs.count( message.getId() ) )
    {
        _pointValues.updatePointValue( message );
    }
}

Policy::IDSet Policy::getRegistrationPointIDs() const
{
    return _pointIDs;
}

std::unique_ptr<CtiSignalMsg> Policy::makeSignalTemplate( const long ID, const long pointValue, const std::string & description )
{
    auto signal = std::make_unique<CtiSignalMsg>( ID,
                                                  0,
                                                  description,
                                                  "",
                                                  CapControlLogType,
                                                  SignalEvent,
                                                  SystemUser );

    signal->setPointValue( pointValue );

    return signal;
}

std::unique_ptr<CtiRequestMsg> Policy::makeRequestTemplate( const long ID, const std::string & command )
{
    auto request = std::make_unique<CtiRequestMsg>( ID, command );

    request->setMessagePriority( _MSG_PRIORITY );
    request->setSOE( 5 );

    return request;
}

Policy::Action Policy::makeStandardDigitalControl( const LitePoint & point, const std::string & description )
{
    return 
    {
        makeSignalTemplate( point.getPointId(), 0, description ),
        makeRequestTemplate( point.getPaoId(),
                             point.getStateOneControl() + " select pointid " + std::to_string( point.getPointId() ) )
    };
}


FailedAttributeLookup::FailedAttributeLookup( const Attribute & attribute )
    :   _attribute( attribute ),
        _description( "Failed Point Attribute Lookup: '" + _attribute.getName() + "'" )
{
    // empty
}

const char * FailedAttributeLookup::what() const
{
    return _description.c_str();
}

const Attribute & FailedAttributeLookup::attribute() const
{
    return _attribute;
}


UninitializedPointValue::UninitializedPointValue( const Attribute & attribute )
    :   _attribute( attribute ),
        _description( "Uninitialized Point Value for Attribute: '" + _attribute.getName() + "'" )
{
    // empty
}

const char * UninitializedPointValue::what() const
{
    return _description.c_str();
}

const Attribute & UninitializedPointValue::attribute() const
{
    return _attribute;
}

}
}

