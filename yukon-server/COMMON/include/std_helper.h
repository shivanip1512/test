#pragma once

#include "utility.h"
#include "streamBuffer.h"

#include <boost/optional/optional.hpp>
#include <boost/range/iterator_range.hpp>
#include <boost/range/algorithm/copy.hpp>
#include <boost/range/algorithm_ext/insert.hpp>

#include <iostream>
#include <iomanip>
#include <type_traits>

namespace Cti {

template<typename... Lambdas>
struct lambda_overloads : Lambdas...
{
    lambda_overloads(Lambdas... lds)
        :   Lambdas{lds}...
    {}

    using Lambdas::operator()...;
};

template<typename... Lambdas> lambda_overloads(Lambdas... lambdas) -> lambda_overloads<Lambdas...>;

class ScopeExit 
{
    std::function<void()> l;

public:

    ScopeExit() = default;

    template<typename T>
    ScopeExit( T t ) 
        : l { t } 
    {
    }

    template<typename T>
    void reset( T t )
    {
        l = t;
    }

    ~ScopeExit() 
    { 
        if( l ) 
        {
            l(); 
        }
    }
};

template<class Enum>
constexpr std::underlying_type_t<Enum> as_underlying(Enum value)
{
    return static_cast<std::underlying_type_t<Enum>>(value);
}

template <class Map>
boost::optional<typename Map::mapped_type> mapFind( const Map &m, const typename Map::key_type &key )
{
    auto itr = m.find(key);

    if( itr == m.end() )
    {
        return boost::none;
    }

    return itr->second;
}

template <class Map, class PtrType=std::enable_if<std::is_pointer<Map::mapped_type>::value, Map::mapped_type>::type>
PtrType mapFindPtr( const Map &m, const typename Map::key_type &key )
{
    auto itr = m.find(key);

    if( itr == m.end() )
    {
        return nullptr;
    }

    return itr->second;
}

template <class Key, class Value>
Value* mapFindPtr( const std::map<Key, std::unique_ptr<Value>>& m, const Key& key )
{
    auto itr = m.find(key);

    if( itr == m.end() )
    {
        return nullptr;
    }

    return itr->second.get();
}

template <class Map>
typename Map::mapped_type mapFindOrDefault( const Map &m, const typename Map::key_type &key, const typename Map::mapped_type defaultValue )
{
    auto itr = m.find(key);

    if( itr != m.end() )
    {
        return itr->second;
    }

    return defaultValue;
}

template <class Map, typename ValueMapper>
typename Map::mapped_type mapFindOrCompute(Map &m, const typename Map::key_type &key, const ValueMapper valueProducer)
{
    auto itr = m.find(key);

    if( itr == m.end() )
    {
        itr = m.emplace(key, valueProducer(key)).first;
    }

    return itr->second;
}

// This needs a comment saying that it can be removed when we upgrade to Boost 1.50+.
template<typename MappedType, class MapViewType, typename KeyType>
boost::optional<MappedType> bimapFind( const MapViewType &mapView, KeyType key )
{
    auto itr = mapView.find(key);

    if( itr == mapView.end() )
    {
        return boost::none;
    }

    return itr->second;
}

template <class Map>
boost::optional<typename Map::mapped_type &> mapFindRef( Map &m, const typename Map::key_type &key )
{
    auto itr = m.find(key);

    if( itr == m.end() )
    {
        return boost::none;
    }

    return itr->second;
}

template <class Map>
boost::optional<const typename Map::mapped_type &> mapFindRef( const Map &m, const typename Map::key_type &key )
{
    auto itr = m.find(key);

    if( itr == m.end() )
    {
        return boost::none;
    }

    return itr->second;
}

template <class Cont, class UnaryPredicate>
boost::optional<typename Cont::value_type> findIf(const Cont& c, UnaryPredicate pred)
{
    auto itr = std::find_if(c.begin(), c.end(), pred);

    if( itr == c.end() )
    {
        return boost::none;
    }

    return *itr;
}

template <class Cont, class UnaryPredicate>
boost::optional<typename Cont::reference> findIfRef(Cont& c, UnaryPredicate pred)
{
    auto itr = std::find_if(c.begin(), c.end(), pred);

    if( itr == c.end() )
    {
        return boost::none;
    }

    return *itr;
}

template <typename T, typename U>
std::map<U, T> vectorToMap(const std::vector<T>& attributeList, U (T::*keyFunc)() const )
{
    std::map<U, T> m;
    auto asPair = [=](T input) { return std::make_pair((input.*keyFunc)(), input); };
    boost::insert(m, attributeList | boost::adaptors::transformed(asPair));
    return m;
}

template <typename T>
boost::iterator_range<T*> arrayToRange(T* arr, size_t len)
{
    return boost::make_iterator_range(arr, arr+len);
}

template <typename T, size_t Size>
boost::iterator_range<T*> arrayToRange(std::array<T, Size>& arr)
{
    return boost::make_iterator_range(arr.data(), arr.data() + arr.size());
}

template<size_t size>
static constexpr uint8_t log2()
{
    static_assert(size == 4 || size % 4 == 0, "size is not a power of 2");
    return log2<(size >> 1)>() + 1;
}
template<>
static constexpr uint8_t log2<2>()
{
    return 1;
}

template <typename T>
T clamp(T min, T input, T max)
{
    return std::clamp(input, min, max);
}

template <int Min, int Max>
int clamp(int input)
{
    return clamp(Min, input, Max);
}

namespace Logging {
namespace Set {

template<typename Logger, typename Val, typename = 
    std::enable_if_t<
        //  enable for ostreams and StreamBuffer/Sink
        (std::is_base_of_v<Logger, std::ostream> 
            || std::is_same_v<Logger, StreamBuffer> 
            || std::is_same_v<Logger, StreamBufferSink>)
        //  enable for all integral types and string (not floats/doubles)    
        && (std::is_integral_v<Val> || std::is_same_v<Val, std::string>)>>
inline Logger& operator<<(Logger& logger, const std::set<Val> &set)
{
    logger << "{";
    if( set.empty() )
    {
        logger << "<empty>";
    }
    else
    {
        auto itr = set.begin();

        logger << *itr;

        while( ++itr != set.end() )
        {
            logger << "," << *itr;
        }
    }
    return logger << "}";
}

}
    
namespace Map {

    template <typename Key, typename Value>
    inline std::ostream &operator<<(std::ostream &logger, const std::map<Key, Value> &map)
    {
        logger << "{";
        if( map.empty() )
        {
            logger << "<empty>";
        }
        else
        {
            auto itr = map.begin();

            logger << "{" << itr->first << ":" << itr->second << "}";

            while( ++itr != map.end() )
            {
                logger << ",{" << itr->first << ":" << itr->second << "}";
            }
        }
        return logger << "}";
    }
}

namespace Vector {

template<class T>
inline std::ostream &operator<<(std::ostream &logger, const std::vector<T> &buf)
{
    logger << "[";
    if( buf.empty() )
    {
        logger << "<empty>";
    }
    else
    {
        boost::range::copy(buf, std::ostream_iterator<T>{ logger, "," });
    }
    return logger << "]";
}

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

inline StreamBufferSink &operator<<(StreamBufferSink &logger, const std::vector<unsigned char> &buf)
{
    std::for_each(buf.begin(), buf.end(), [&](int ch) {
        logger << std::hex << std::setfill('0') << std::setw(2) << ch << " ";
    });

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

inline StreamBufferSink &operator<<(StreamBufferSink &logger, const boost::iterator_range<const unsigned char*> &buf)
{
    std::for_each(buf.begin(), buf.end(), [&](int ch) {
        logger << std::hex << std::setfill('0') << std::setw(2) << ch << " ";
    });

    return logger;
}

inline StreamBufferSink &operator<<(StreamBufferSink &logger, const boost::iterator_range<unsigned char*> &buf)
{
    std::for_each(buf.begin(), buf.end(), [&](int ch) {
        logger << std::hex << std::setfill('0') << std::setw(2) << ch << " ";
    });

    return logger;
}

}
}
} // namespace Logging::Range::Hex

} // namespace Cti
