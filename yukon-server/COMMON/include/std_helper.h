#pragma once

#include "utility.h"

#include "boost/optional/optional.hpp"

namespace Cti {

template <class Map>
boost::optional<typename Map::mapped_type> mapFind( Map &m, const typename Map::key_type &key )
{
    Map::const_iterator itr = m.find(key);

    if( itr == m.end() )
    {
        return boost::none;
    }

    return itr->second;
}

// This needs a comment saying that it can be removed when we upgrade to Boost 1.50+.
template<typename MappedType, class MapViewType, typename KeyType>
boost::optional<MappedType> bimapFind( MapViewType mapView, KeyType key )
{
    MapViewType::const_iterator itr = mapView.find(key);

    if( itr == mapView.end() )
    {
        return boost::none;
    }

    return itr->second;
}

template <class Map>
boost::optional<typename Map::mapped_type &> mapFindRef( Map &m, const typename Map::key_type &key )
{
    Map::iterator itr = m.find(key);

    if( itr == m.end() )
    {
        return boost::none;
    }

    return itr->second;
}

namespace Logging {
namespace Vector {
namespace Hex {
namespace {

std::ostream &operator<<(std::ostream &logger, const std::vector<unsigned char> &buf)
{
    const std::ios_base::fmtflags oldflags = logger.flags( std::ios::hex );

    copy(buf.begin(), buf.end(), padded_output_iterator<int, std::ostream>(logger, '0', 2));

    logger.flags(oldflags);

    return logger;
}

}
}
}
}

}
