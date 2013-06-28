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

    typedef std::set<std::string>   CategoryNames;

    static void AddRelation( const DeviceTypes      deviceType,
                             const CategoryNames &  categories );

    static CategoryNames & Lookup( const DeviceTypes deviceType );

private:

    typedef std::map<DeviceTypes, CategoryNames>    CategoryLookup;

    static CategoryLookup   _lookup;
};

}

