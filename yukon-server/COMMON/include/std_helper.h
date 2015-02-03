#pragma once

#include "utility.h"

#include "boost/optional/optional.hpp"
#include "boost/range/iterator.hpp"

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
boost::optional<MappedType> bimapFind( const MapViewType &mapView, KeyType key )
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

template <class Map>
boost::optional<const typename Map::mapped_type &> mapFindRef( const Map &m, const typename Map::key_type &key )
{
    Map::const_iterator itr = m.find(key);

    if( itr == m.end() )
    {
        return boost::none;
    }

    return itr->second;
}

template <class Cont, class UnaryPredicate>
boost::optional<typename Cont::value_type> findIf(Cont& c, UnaryPredicate pred)
{
    Cont::const_iterator itr = std::find_if(c.begin(), c.end(), pred);

    if( itr == c.end() )
    {
        return boost::none;
    }

    return *itr;
}

template <class Cont, class UnaryPredicate>
boost::optional<typename Cont::reference> findIfRef(Cont& c, UnaryPredicate pred)
{
    Cont::iterator itr = std::find_if(c.begin(), c.end(), pred);

    if( itr == c.end() )
    {
        return boost::none;
    }

    return *itr;
}

template <typename T>
boost::iterator_range<T*> arrayToRange(T* arr, size_t len)
{
    return boost::make_iterator_range(arr, arr+len);
}

namespace Logging {
namespace Vector {
namespace Hex {

inline std::ostream &operator<<(std::ostream &logger, const std::vector<unsigned char> &buf)
{
    const std::ios_base::fmtflags oldflags = logger.flags( std::ios::hex );

    const char oldFill = logger.fill('0');

    std::for_each(buf.begin(), buf.end(), [&](int ch) {
        logger << std::setw(2) << ch << " ";
    });

    logger.fill(oldFill);

    logger.flags(oldflags);

    return logger;
}

}
} // namespace Vector::Hex

namespace Range {
namespace Hex {

inline std::ostream &operator<<(std::ostream &logger, const boost::iterator_range<unsigned char*> &buf)
{
    const std::ios_base::fmtflags oldflags = logger.flags( std::ios::hex );

    const char oldFill = logger.fill('0');

    std::for_each(buf.begin(), buf.end(), [&](int ch) {
        logger << std::setw(2) << ch << " ";
    });

    logger.fill(oldFill);

    logger.flags(oldflags);

    return logger;
}

}
}
} // namespace Logging::Range::Hex

} // namespace Cti
