#pragma once

#include "AttributeService.h"
#include "PointValueHolder.h"

#include <vector>
#include <map>
#include <set>
#include <exception>

class CtiPointDataMsg;

namespace Cti           {
namespace CapControl    {

struct Policy
{
    using IDSet = std::set<long>;

    void loadAttributes( AttributeService & service, const long paoID );

    LitePoint getPointByAttribute( const PointAttribute & attribute ) const;
    double    getValueByAttribute( const PointAttribute & attribute ) const;

    void updatePointData( CtiPointDataMsg * message );

    IDSet getRegistrationPointIDs() const;

protected:

    using AttributeList = std::vector<PointAttribute>;
    using AttributeMap  = std::map<PointAttribute, LitePoint>;

    AttributeList       supportedAttributes;
    AttributeMap        pointMapping;
    IDSet               pointIDs;
    PointValueHolder    pointValues;
};


struct FailedAttributeLookup : public std::exception
{
    FailedAttributeLookup( const PointAttribute & attribute );

    virtual const char * what( ) const;

    const PointAttribute & attribute() const;

protected:

    const PointAttribute &  _attribute;
    std::string             _description;
};


struct UninitializedPointValue : public std::exception
{
    UninitializedPointValue( const PointAttribute & attribute );

    virtual const char * what( ) const;

    const PointAttribute & attribute() const;

protected:

    const PointAttribute &  _attribute;
    std::string             _description;
};

}
}

