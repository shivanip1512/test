#include "precompiled.h"

#include "DeviceAttributeLookup.h"

#include <boost/range/iterator_range.hpp>
#include <boost/range/adaptor/transformed.hpp>

namespace Cti
{

DeviceAttributeLookup::DeviceAttributeNameMap DeviceAttributeLookup::_unknownAttributes;
DeviceAttributeLookup::AttributeMappingInfoCollection DeviceAttributeLookup::_lookup;


void DeviceAttributeLookup::AddRelation( const DeviceTypes      deviceType,
                                         const Attribute &      attribute,
                                         const CtiPointType_t   pointType,
                                         const unsigned         pointOffset )
{
    _lookup.insert( AttributeMappingInfo{ deviceType, attribute, pointType, pointOffset } );
}

void DeviceAttributeLookup::AddUnknownAttribute( const DeviceTypes      deviceType,
                                                 const std::string &    attributeName )
{
    _unknownAttributes.emplace( deviceType, attributeName );
}

boost::optional<DeviceAttributeLookup::PointTypeOffset> DeviceAttributeLookup::Lookup( const DeviceTypes     deviceType,
                                                                                       const Attribute &     attribute )
{
    auto & index = _lookup.get<by_attribute>();

    auto result = index.find( std::make_tuple( deviceType, attribute ) );

    if ( result != index.end() )
    {
        return PointTypeOffset{ result->type, result->offset };
    }

    return boost::none;
}

auto DeviceAttributeLookup::GetUnknownDeviceAttributes() -> DeviceAttributeNameMap
{
    return _unknownAttributes;
}

std::vector<Attribute> DeviceAttributeLookup::AttributeLookup( const DeviceTypes    deviceType,
                                                               const CtiPointType_t pointType,
                                                               const unsigned       pointOffset )
{
    auto & index = _lookup.get<by_typeOffset>();

    return 
        boost::copy_range< std::vector<Attribute> >(
            index.equal_range( std::make_tuple( deviceType, pointType, pointOffset ) )
                | boost::adaptors::transformed( []( const AttributeMappingInfo & entry )
                                                {
                                                    return entry.attribute;
                                                } ) );
}

}

