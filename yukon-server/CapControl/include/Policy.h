#pragma once

#include "yukon.h"

#include "AttributeService.h"
#include "PointValueHolder.h"
#include "msg_signal.h"
#include "Requests.h"

class CtiPointDataMsg;
class CtiCCTwoWayPoints;

namespace Cti::CapControl {

enum class RequestType;

struct Policy
{
    using IDSet = std::set<long>;
    using Action = std::pair<std::unique_ptr<CtiSignalMsg>,
                             CategorizedRequest>;
    using Actions = std::vector<Action>;

    void loadAttributes( AttributeService & service, const long paoID );

    //  Old style point lookup that relies on the internal _pointValues map
    LitePoint getPointByAttribute( const Attribute & attribute ) const;
    //  New style point lookup that relies on twoWayPoints
    LitePoint getPointByAttribute( const Attribute & attribute, const CtiCCTwoWayPoints & twoWayPoints ) const;
    //  Old style point lookup that relies on the internal _pointValues map
    double    getValueByAttribute( const Attribute & attribute ) const;
    //  New style point lookup that relies on twoWayPoints
    double    getValueByAttribute( const Attribute & attribute, const CtiCCTwoWayPoints & twoWayPoints ) const;

    //  Old style point lookup that relies on the internal _pointValues map
    PointValue getCompleteValueByAttribute( const Attribute & attribute ) const;

    void updatePointData( const CtiPointDataMsg & message );

    IDSet getRegistrationPointIDs() const;

protected:

    using AttributeList = std::vector<Attribute>;
    using AttributeMap  = std::map<Attribute, LitePoint>;

    virtual AttributeList getSupportedAttributes() const = 0;

    std::unique_ptr<CtiSignalMsg>   makeSignalTemplate( const long ID, const long pointValue, const std::string & description );
    CategorizedRequest              makeRequestTemplate( const long ID, const std::string & command, const RequestType requestType );

    Action makeStandardDigitalControl( const LitePoint & point, const std::string & description, const RequestType requestType );

    template <typename T>
    std::string putvalueAnalogCommand( const LitePoint & point, const T value )
    {
        return "putvalue analog value " + std::to_string( value ) + " select pointid " + std::to_string( point.getPointId() );
    }

    AttributeMap        _pointMapping;
    IDSet               _pointIDs;
    PointValueHolder    _pointValues;
};


struct FailedAttributeLookup : public std::exception
{
    FailedAttributeLookup( const Attribute & attribute );

    const char * what() const override;

    const Attribute & attribute() const;

protected:

    const Attribute     _attribute;
    std::string         _description;
};


struct UninitializedPointValue : public std::exception
{
    UninitializedPointValue( const Attribute & attribute );

    const char * what() const override;

    const Attribute & attribute() const;

protected:

    const Attribute     _attribute;
    std::string         _description;
};

}