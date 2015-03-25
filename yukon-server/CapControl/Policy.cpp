#include "precompiled.h"

#include "Policy.h"
#include "std_helper.h"
#include "msg_pdata.h"

extern unsigned long _MSG_PRIORITY;


namespace Cti           {
namespace CapControl    {

void Policy::loadAttributes( AttributeService & service, const long paoID )
{
    for ( const auto & attribute : _supportedAttributes )
    {
        LitePoint point = service.getPointByPaoAndAttribute( paoID, attribute );

        if ( point.getPointType() != InvalidPointType )
        {
            _pointMapping.insert( std::make_pair( attribute, point ) );
            _pointIDs.insert( point.getPointId() );
        }
    }
}

LitePoint Policy::getPointByAttribute( const PointAttribute & attribute ) const
{
    boost::optional<LitePoint>  point = mapFind( _pointMapping, attribute );

    if ( ! point )
    {
        throw FailedAttributeLookup( attribute );
    }

    return *point;
}

double Policy::getValueByAttribute( const PointAttribute & attribute ) const
{
    double currentValue;

    if ( ! _pointValues.getPointValue( getPointByAttribute( attribute ).getPointId(), currentValue ) )
    {
        throw UninitializedPointValue( attribute );
    }

    return currentValue;
}

void Policy::updatePointData( CtiPointDataMsg * message )
{
    if ( _pointIDs.count( message->getId() ) )
    {
        _pointValues.updatePointValue( message );
    }
}

Policy::IDSet Policy::getRegistrationPointIDs() const
{
    return _pointIDs;
}

std::unique_ptr<CtiSignalMsg> Policy::makeSignalTemplate( const long ID, const long pointValue )
{
    auto signal = std::make_unique<CtiSignalMsg>( ID,
                                                  0,
                                                  "",
                                                  "",
                                                  CapControlLogType,
                                                  SignalEvent,
                                                  "cap control" );

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

Policy::Action Policy::makeStandardDigitalControl( const LitePoint & point )
{
    return 
    {
        makeSignalTemplate( point.getPointId(), 0 ),
        makeRequestTemplate( point.getPaoId(),
                             point.getStateOneControl() + " select pointid " + std::to_string( point.getPointId() ) )
    };
}


FailedAttributeLookup::FailedAttributeLookup( const PointAttribute & attribute )
    :   _attribute( attribute ),
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
    :   _attribute( attribute ),
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

