#include "precompiled.h"

#include "DeviceConfigLookup.h"


namespace Cti
{

DeviceConfigLookup::CategoryLookup  DeviceConfigLookup::_lookup;
DeviceConfigLookup::CategoryToFieldMap   DeviceConfigLookup::_fields;


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


void DeviceConfigLookup::AddRelation( const std::string & category,
                                      const std::string & field )
{
    _fields.insert( std::make_pair( category, field ) );
}


DeviceConfigLookup::CategoryFieldIterPair DeviceConfigLookup::equal_range( const std::string & category )
{
    return _fields.equal_range( category );
}

}

