#pragma once

#include "devicetypes.h"
#include "pointtypes.h"
#include "PointAttribute.h"

#include <boost/optional.hpp>


namespace Cti
{

class IM_EX_CTIBASE DeviceConfigLookup
{
public:

    typedef std::set<std::string>                   CategoryNames;
    typedef std::multimap<std::string, std::string> CategoryToFieldMap;
    typedef std::pair<CategoryToFieldMap::iterator,
                      CategoryToFieldMap::iterator>   CategoryFieldIterPair;

    static void AddRelation( const DeviceTypes      deviceType,
                             const CategoryNames &  categories );

    static CategoryNames & Lookup( const DeviceTypes deviceType );

    static void AddRelation( const std::string & category,
                             const std::string & field );

    static CategoryFieldIterPair equal_range( const std::string & category );

private:

    typedef std::map<DeviceTypes, CategoryNames>    CategoryLookup;

    static CategoryLookup       _lookup;
    static CategoryToFieldMap   _fields;
};

}

