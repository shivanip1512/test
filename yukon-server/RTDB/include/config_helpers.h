#pragma once


#include "config_device.h"
#include "config_exceptions.h"

#include <boost/optional.hpp>
#include <boost/lexical_cast.hpp>

#include <limits>
#include <string>
#include <sstream>


namespace Cti     {
namespace Devices {

namespace {

/**
 * retrieve data from a local config map, throws MissingConfigDataException() if no config value can be found
 */
template<class Map>
typename Map::mapped_type getConfigData( const Map & configMap, const std::string & configKey )
{
    boost::optional<Map::mapped_type> val = mapFind(configMap, configKey);

    if( ! val )
    {
        throw MissingConfigDataException( configKey );
    }

    return *val;
}

/**
 * throws MissingConfigDataException() if no config value exist
 */
template<typename T>
T getConfigData( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    boost::optional<T> val = deviceConfig->findValue<T>( configKey );

    if( ! val )
    {
        throw MissingConfigDataException( configKey );
    }

    return *val;
}


template<typename T>
T require( long input, const std::string & configKey )
{
    // Rudimentary check to see that we can coerce the 'long' into an unsigned type.
    if( input < 0 || input > std::numeric_limits<T>::max() )
    {
        std::ostringstream cause;
        cause << "invalid value " << input << " is out of range, expected [0 - " << std::numeric_limits<T>::max() << "]";

        throw InvalidConfigDataException( configKey, cause.str() );
    }

    return static_cast<T>( input );
}

/**
 * Specialization of getConfigData()
 * throws InvalidConfigDataException() if config data is out of range
 */
template<>
unsigned char getConfigData<unsigned char>( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    return require<unsigned char>( getConfigData<long>(deviceConfig, configKey), configKey );
}


/**
 * Specialization of getConfigData()
 * throws InvalidConfigDataException() if config data is negative
 */
template<>
unsigned getConfigData<unsigned>( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    return require<unsigned>( getConfigData<long>(deviceConfig, configKey), configKey );
}


/**
* throws MissingConfigDataException() if config value does not exist
* throws InvalidConfigDataException() if value is outside the min/max ranges
*/
template<typename T, T min, T max>
T getConfigData(const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey)
{
    T val = getConfigData<T>(deviceConfig, configKey);

    if( val < min || val > max )
    {
        std::ostringstream cause;
        cause << "invalid value " << val << " is out of range, expected [" << std::to_string(min) << " - " << std::to_string(max) << "]";

        throw InvalidConfigDataException(configKey, cause.str());
    }

    return val;
}


/**
 * getConfigData() for indexed config items. Retrieve a vector of config data for a given prefix and config key
 * throws MissingConfigDataException() if config data is missing
 */
template <typename T>
std::vector<T> getConfigDataVector( Config::DeviceConfigSPtr deviceConfig, const std::string &prefix )
{
    const boost::optional<Config::DeviceConfig::IndexedItem> indexed = deviceConfig->getIndexedItem(prefix);

    if( ! indexed )
    {
        throw MissingConfigDataException( prefix );
    }

    return std::vector<T>(
                indexed->begin(),
                indexed->end());
}


/**
 * get a set of values from and indexed configuration
 * throws MissingConfigDataException() if config data is missing
 * throws InvalidConfigDataException() if duplicated data is found or getConfigData() as encounter an error
 */
template <typename T>
std::set<T> getConfigDataSet( Config::DeviceConfigSPtr &deviceConfig, const std::string &prefix, const std::string &key )
{
    unsigned index = 0;
    std::set<T> result;

    std::vector<T> values = getConfigDataVector<T>( deviceConfig, prefix, key );

    for each( const T& val in values )
    {
        if( ! result.insert(val).second )
        {
            std::string configKey = prefix + "." + boost::lexical_cast<std::string>(index) + "."+ key;

            std::ostringstream cause;
            cause << "Unexpected duplicated config data \"" << val << "\"";

            throw InvalidConfigDataException( configKey, cause.str() );
        }

        index++;
   }

   return result;
}

/**
 * resolve config data for map or bimap mapview
 * throws InvalidConfigDataException() if data is not found
 */
template <class MapType, typename KeyType>
typename MapType::mapped_type resolveConfigData( MapType& m, KeyType& configData, const std::string & configKey )
{
    MapType::const_iterator itr = m.find(configData);

    if( itr == m.end() )
    {
        std::ostringstream cause;

        cause << "invalid value " << configData << ", expected [";

        for( itr = m.begin(); itr != m.end(); )
        {
            cause << itr->first;
            cause << ( ++itr == m.end() ? "]" : "; ");
        }

        throw InvalidConfigDataException( configKey, cause.str() );
    }

    return itr->second;
}

template <class MapType>
typename MapType::mapped_type getConfigDataEnum(Config::DeviceConfigSPtr deviceConfig, const std::string &configKey, const MapType &m)
{
    auto configValue = getConfigData<MapType::key_type>(deviceConfig, configKey);

    return resolveConfigData(m, configValue, configKey);
}


} // Anonymous

} // Devices
} // Cti

