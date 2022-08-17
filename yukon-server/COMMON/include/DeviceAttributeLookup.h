#pragma once

#include "devicetypes.h"
#include "pointtypes.h"
#include "Attribute.h"

#include <boost/optional.hpp>
#include <boost/multi_index_container.hpp>
#include <boost/multi_index/ordered_index.hpp>
#include <boost/multi_index/member.hpp>
#include <boost/multi_index/composite_key.hpp>


using namespace boost::multi_index;

namespace Cti
{

class IM_EX_CTIBASE DeviceAttributeLookup
{
public:

    struct PointTypeOffset
    {
        CtiPointType_t  type;
        unsigned        offset;
    };

    static void AddRelation( const DeviceTypes      deviceType,
                             const Attribute &      attribute,
                             const CtiPointType_t   pointType,
                             const unsigned         pointOffset );

    static void AddUnknownAttribute( const DeviceTypes      deviceType,
                                     const std::string &    attributeName );

    static boost::optional<PointTypeOffset> Lookup( const DeviceTypes    deviceType,
                                                    const Attribute &    attribute );

    static std::vector<Attribute> AttributeLookup( const DeviceTypes    deviceType,
                                                   const CtiPointType_t pointType,
                                                   const unsigned       pointOffset );

    using DeviceAttributeNameMap = std::multimap<DeviceTypes, std::string>;

    static DeviceAttributeNameMap GetUnknownDeviceAttributes();

private:

    static DeviceAttributeNameMap _unknownAttributes;

    struct AttributeMappingInfo
    {
        DeviceTypes     deviceType;
        Attribute       attribute;
        CtiPointType_t  type;
        unsigned        offset;
    };

    // Using a boost multi_index_container to hold the attribute information to support lookup for the
    //  following cases:
    //      1.  { DeviceType, Attribute } --> { PointType, Offset }
    //      2.  { DeviceType, PointType, Offset } --> { Attribute }

    // index tags to provide a nicer interface to get<N>()

    struct by_attribute    { };     // get<0>
    struct by_typeOffset   { };     // get<1>

    using AttributeMappingInfoCollection =
        multi_index_container<
            AttributeMappingInfo,
            indexed_by<
                ordered_unique<
                    tag<by_attribute>,
                    composite_key<
                        AttributeMappingInfo,
                        member<AttributeMappingInfo, DeviceTypes, &AttributeMappingInfo::deviceType>,
                        member<AttributeMappingInfo, Attribute,   &AttributeMappingInfo::attribute>
                        >
                    >,
                ordered_non_unique<
                    tag<by_typeOffset>,
                    composite_key<
                        AttributeMappingInfo,
                        member<AttributeMappingInfo, DeviceTypes,    &AttributeMappingInfo::deviceType>,
                        member<AttributeMappingInfo, CtiPointType_t, &AttributeMappingInfo::type>,
                        member<AttributeMappingInfo, unsigned,       &AttributeMappingInfo::offset>
                        >
                    >
                >
            >;

     static AttributeMappingInfoCollection  _lookup;
};

}

