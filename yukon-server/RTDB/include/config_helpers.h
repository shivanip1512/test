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

/**
 * getConfigData() for indexed config items. Retrieve a vector of config data for a given prefix and config key
 * throws MissingConfigDataException() if config data is missing
 */
template <typename T>
std::vector<T> getConfigData( Config::DeviceConfigSPtr &deviceConfig, const std::string &prefix, const std::string &key )
{
    unsigned index = 0;
    std::vector<T> result;

    std::vector<Config::DeviceConfigSPtr> indexedConfig = deviceConfig->getIndexedConfig( prefix );

    for each( const Config::DeviceConfigSPtr& config in indexedConfig )
    {
        if( ! config )
        {
            std::string configKey = prefix + boost::lexical_cast<std::string>(index) + key;

            // this is not expected, but better safe than sorry
            throw MissingConfigDataException( configKey );
        }

        try
        {
            result.push_back( getConfigData<T>( config, key ));
        }
        catch( MissingConfigDataException& )
        {
            std::string configKey = prefix + boost::lexical_cast<std::string>(index) + key;

            throw MissingConfigDataException( configKey );
        }
        catch( InvalidConfigDataException& ex )
        {
            std::string configKey = prefix + boost::lexical_cast<std::string>(index) + key;

            throw InvalidConfigDataException( configKey, ex.cause );
        }

        index++;
    }

    return result;
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

    std::vector<T> values = getConfigData<T>(deviceConfig, prefix, key );

    for each( const T& val in values )
    {
        if( ! result.insert(val).second )
        {
            std::string configKey = prefix + boost::lexical_cast<std::string>(index) + key;

            std::ostringstream cause;
            cause << "Unexpected duplicated config data \"" << val << "\"";

            throw InvalidConfigDataException( configKey, cause.str() );
        }

        index++;
   }

   return result;
}

} // Anonymous

} // Devices
} // Cti

