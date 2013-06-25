#pragma once

#include "devicetypes.h"
#include "pointtypes.h"
#include "PointAttribute.h"

#include <boost/optional.hpp>


namespace Cti
{

class DeviceAttributeLookup
{
public:

    struct PointTypeOffset
    {
        CtiPointType_t  type;
        unsigned        offset;

        PointTypeOffset( const CtiPointType_t t = InvalidPointType, const unsigned o = 0 )
            :   type( t ),
                offset( o )
        {
            // empty...
        }
    };

    static void AddRelation( const DeviceTypes      deviceType,
                             const Attribute &      attribute,
                             const CtiPointType_t   pointType,
                             const unsigned         pointOffset );

    static boost::optional<PointTypeOffset> Lookup( const DeviceTypes    deviceType,
                                                    const Attribute &    attribute );

private:

    struct DeviceAttribute
    {
        DeviceTypes     deviceType;
        Attribute       attribute;

        DeviceAttribute( const DeviceTypes t, const Attribute & a )
            :   deviceType( t ),
                attribute( a )
        {
            // empty...
        }

        bool operator<( const DeviceAttribute & rhs ) const
        {
            return deviceType < rhs.deviceType || ( deviceType == rhs.deviceType && attribute < rhs.attribute );
        }
    };

    typedef std::map<DeviceAttribute, PointTypeOffset>  DeviceAttributeToPointTypeOffsetMap;

    static DeviceAttributeToPointTypeOffsetMap  _lookup;
};


// this guy should go into the yukon-server\DeviceConfiguration directory -- for now here he is...
class DeviceConfigLookup
{
public:

    typedef std::set<std::string>   CategoryNames;

    static void AddRelation( const DeviceTypes      deviceType,
                             const CategoryNames &  categories );

    static CategoryNames & Lookup( const DeviceTypes deviceType );

private:

    typedef std::map<DeviceTypes, CategoryNames>    CategoryLookup;

    static CategoryLookup   _lookup;
};

}

