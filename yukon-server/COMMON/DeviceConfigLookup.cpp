#include "precompiled.h"

#include "DeviceConfigLookup.h"


namespace Cti
{

DeviceConfigLookup::CategoryLookup  DeviceConfigLookup::_lookup;


void DeviceConfigLookup::AddRelation( const DeviceTypes     deviceType,
                                      const CategoryNames & categories )
{
    CategoryNames  & configs = _lookup[ deviceType ];

    configs.insert( categories.begin(), categories.end() );
}


DeviceConfigLookup::CategoryNames & DeviceConfigLookup::Lookup( const DeviceTypes deviceType )
{
    return _lookup[ deviceType ];
}

}

