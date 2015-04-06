#pragma once

#include "yukon.h"

#include "AttributeService.h"
#include "PointValueHolder.h"
#include "msg_signal.h"
#include "msg_pcrequest.h"

class CtiPointDataMsg;


namespace Cti           {
namespace CapControl    {

struct Policy
{
    using IDSet = std::set<long>;
    using Action = std::pair<std::unique_ptr<CtiSignalMsg>,
                             std::unique_ptr<CtiRequestMsg>>;
    using Actions = std::vector<Action>;

    void loadAttributes( AttributeService & service, const long paoID );

    LitePoint getPointByAttribute( const PointAttribute & attribute ) const;
    double    getValueByAttribute( const PointAttribute & attribute ) const;

    void updatePointData( CtiPointDataMsg * message );

    IDSet getRegistrationPointIDs() const;

protected:

    using AttributeList = std::vector<PointAttribute>;
    using AttributeMap  = std::map<PointAttribute, LitePoint>;

    virtual AttributeList getSupportedAttributes() = 0;

    std::unique_ptr<CtiSignalMsg>   makeSignalTemplate( const long ID, const long pointValue, const std::string & description );
    std::unique_ptr<CtiRequestMsg>  makeRequestTemplate( const long ID, const std::string & command );

    Action makeStandardDigitalControl( const LitePoint & point, const std::string & description );

    AttributeMap        _pointMapping;
    IDSet               _pointIDs;
    PointValueHolder    _pointValues;
};


struct FailedAttributeLookup : public std::exception
{
    FailedAttributeLookup( const PointAttribute & attribute );

    const char * what() const override;

    const PointAttribute & attribute() const;

protected:

    const PointAttribute    _attribute;
    std::string             _description;
};


struct UninitializedPointValue : public std::exception
{
    UninitializedPointValue( const PointAttribute & attribute );

    const char * what() const override;

    const PointAttribute & attribute() const;

protected:

    const PointAttribute    _attribute;
    std::string             _description;
};

}
}

