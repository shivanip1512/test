#include "precompiled.h"

#include "DeviceAttributeLookup.h"


namespace Cti
{

DeviceAttributeLookup::DeviceAttributeToPointTypeOffsetMap  DeviceAttributeLookup::_lookup;
DeviceAttributeLookup::DeviceAttributeNameMap DeviceAttributeLookup::_unknownAttributes;


void DeviceAttributeLookup::AddRelation( const DeviceTypes      deviceType,
                                         const Attribute &      attribute,
                                         const CtiPointType_t   pointType,
                                         const unsigned         pointOffset )
{
    _lookup[ DeviceAttribute( deviceType, attribute ) ] = PointTypeOffset( pointType, pointOffset );
}


void DeviceAttributeLookup::AddUnknownAttribute( const DeviceTypes      deviceType,
                                                  const std::string &    attributeName )
{
    _unknownAttributes.emplace( deviceType, attributeName );
}


boost::optional<DeviceAttributeLookup::PointTypeOffset> DeviceAttributeLookup::Lookup( const DeviceTypes     deviceType,
                                                                                       const Attribute &     attribute )
{
    DeviceAttributeToPointTypeOffsetMap::const_iterator searchResult = _lookup.find( DeviceAttribute( deviceType, attribute ) );

    if ( searchResult != _lookup.end() )
    {
        return searchResult->second;
    }

    return boost::none;
}

auto DeviceAttributeLookup::GetUnknownDeviceAttributes() -> DeviceAttributeNameMap
{
    return _unknownAttributes;
}


}

