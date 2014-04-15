#pragma once


#include "config_device.h"
#include "config_exceptions.h"

#include <boost/optional.hpp>

#include <limits>
#include <string>
#include <sstream>


namespace Cti     {
namespace Devices {

namespace {

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


/**
 * Specialization of getConfigData()
 * throws InvalidConfigDataException() if config data is out of range
 */
template<>
unsigned char getConfigData<unsigned char>( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    long l_val = getConfigData<long>(deviceConfig, configKey);

    // Rudimentary check to see that we can coerce the 'long' into an 'unsigned char'.
    if( l_val < 0 || l_val > std::numeric_limits<unsigned char>::max() )
    {
        std::ostringstream cause;
        cause << "invalid value " << l_val << " is out range, expected [0 - " << std::numeric_limits<unsigned char>::max() << "]";

        throw InvalidConfigDataException( configKey, cause.str() );
    }

    return static_cast<unsigned char>( l_val );
}


/**
 * Specialization of getConfigData()
 * throws InvalidConfigDataException() if config data is negative
 */
template<>
unsigned getConfigData<unsigned>( const Config::DeviceConfigSPtr & deviceConfig, const std::string & configKey )
{
    long l_val = getConfigData<long>(deviceConfig, configKey);

    // Rudimentary check to see that we can coerce the 'long' into an 'unsigned'.
    if( l_val < 0 )
    {
        std::ostringstream cause;
        cause << "invalid value " << l_val << ", expected >= 0";

        throw InvalidConfigDataException( configKey, cause.str() );
    }

    return static_cast<unsigned>( l_val );
}

template <typename T>
T getConfigData( Config::DeviceConfigSPtr &deviceConfig, const std::string &prefix, const std::string &key );

template <>
std::set<std::string> getConfigData<std::set<std::string>>( Config::DeviceConfigSPtr &deviceConfig, const std::string &prefix, const std::string &key )
{
    std::set<std::string> result;

    std::vector<Config::DeviceConfigSPtr> indexedConfig = deviceConfig->getIndexedConfig( prefix );

    unsigned index = 0;
    for each( const Config::DeviceConfigSPtr& config in indexedConfig )
    {
        boost::optional<std::string> val = config->findValue<std::string>( key );

        if( ! val )
        {
            std::ostringstream configKey;
            configKey << prefix << index << key;

            throw MissingConfigDataException( configKey.str() );
        }

        if( ! result.insert(*val).second )
        {
            std::ostringstream configKey, cause;
            configKey << prefix << index << key;
            cause << "Unexpected duplicated config data \"" << val << "\"";

            throw InvalidConfigDataException( configKey.str(), cause.str() );
        }

        index++;
    }

    return result;
}

} // Anonymous

} // Devices
} // Cti

