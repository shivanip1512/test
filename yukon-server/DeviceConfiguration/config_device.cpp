#include "precompiled.h"

#include <boost/make_shared.hpp>
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string/case_conv.hpp>

#include "std_helper.h"
#include "config_device.h"


namespace Cti       {
namespace Config    {

namespace {

// checks if the string contains the prefix
bool containsPrefix( const std::string &str, const std::string &prefix )
{
    return (prefix.length() <= str.length()) && (! str.compare(0, prefix.length(), prefix));
}

} // anonymous

const std::string BoolTrue = "true";


DeviceConfig::DeviceConfig( const long ID, const std::string & name )
    :   _id(ID),
        _name(name)
{

}


// Inserts a value into the mapping, this is a protected function and is
// not meant to be called by devices
bool DeviceConfig::insertValue( std::string identifier, const std::string & value )
{
    CtiToLower(identifier);
    CtiHashKey insertKey(identifier);

    std::pair<ConfigValueMap::iterator, bool> retVal = _configurationValues.insert(ConfigValueMap::value_type(insertKey, value));

    IndexedConfigMap::iterator itr = _cashedIndexedConfig.begin();
    while( itr != _cashedIndexedConfig.end() )
    {
        if( containsPrefix(itr->first, identifier) )
        {
            _cashedIndexedConfig.erase( itr );
            break;
        }
        itr++;
    }

    return retVal.second;
}


boost::optional<std::string> DeviceConfig::lookup( std::string key ) const
{
    CtiToLower(key);
    CtiHashKey findKey(key);

    ConfigValueMap::const_iterator searchResult = _configurationValues.find( findKey );

    if ( searchResult != _configurationValues.end() )
    {
        return searchResult->second;
    }

    return boost::none;
}


bool DeviceConfig::getLongValue( const std::string & key, long & value ) const
{
    boost::optional<std::string>    result = lookup( key );

    if ( ! result || result->length() == 0 )
    {
        value = std::numeric_limits<long>::min();
        return false;
    }

    value = std::strtol( result->c_str(), NULL, 0 );
    return true;
}


std::string DeviceConfig::getValueFromKey( const std::string & key ) const
{
    boost::optional<std::string>    result = lookup( key );

    if ( ! result )
    {
        return std::string();
    }

    return *result;
}

template <>
boost::optional<std::string> DeviceConfig::findValue<std::string>( const std::string & key ) const
{
    return lookup( key );
}

template <>
boost::optional<bool> DeviceConfig::findValue<bool>( const std::string & key ) const
{
    boost::optional<std::string> value = lookup( key );

    if( ! value )
    {
        return boost::none;
    }

    return *value == BoolTrue;
}

template <>
boost::optional<long> DeviceConfig::findValue<long>( const std::string & key ) const
{
    long value;

    if( ! getLongValue( key, value ) )
    {
        return boost::none;
    }

    return value;
}

template <>
boost::optional<double> DeviceConfig::findValue<double>( const std::string & key ) const
{
    boost::optional<std::string>  result = lookup( key );

    if( ! result || result->empty() )
    {
        return boost::none;
    }

    return std::atof( result->c_str() );
}



long DeviceConfig::getLongValueFromKey( const std::string & key ) const
{
    long value;

    getLongValue( key, value );

    return value;
}


double DeviceConfig::getFloatValueFromKey( const std::string & key ) const
{
    boost::optional<std::string>    result = lookup( key );

    if ( ! result || result->length() == 0 )
    {
        return std::numeric_limits<double>::min();
    }

    return std::atof( result->c_str() );
}

boost::optional<DeviceConfig::IndexedConfig> DeviceConfig::getIndexedConfig( const std::string & prefix )
{
    const std::string prefixLower = boost::algorithm::to_lower_copy( prefix );

    // check if we already have this config
    boost::optional<boost::optional<IndexedConfig>> cashedIndexedConfig = mapFind( _cashedIndexedConfig, prefixLower );
    if( cashedIndexedConfig )
    {
        return *cashedIndexedConfig;
    }

    IndexedConfig indexedConfig;

    const size_t prefixLen  = prefixLower.length();
    const size_t digitStart = prefixLen + 1; // prefix size + 1 char separator

    int numberOfIndex = -1;

    for each( ConfigValueMap::value_type p in _configurationValues )
    {
        const std::string configKey = p.first.getHashStr();

        // check if beginning of string matches prefix
        if( containsPrefix(configKey, prefixLower) )
        {
            if( prefixLen == configKey.length() )
            {
                try
                {
                    numberOfIndex = boost::lexical_cast<int>(p.second);
                }
                catch( boost::bad_lexical_cast& )
                {
                    _cashedIndexedConfig[prefixLower] = boost::none;
                    return boost::none;
                }
            }
            else if( digitStart < configKey.length() && configKey[ prefixLen ] == '.' )
            {
                // find second separator : corresponds to 1 char past the last digit
                const size_t digitEnd = configKey.find_first_of('.', digitStart );

                if( digitEnd != std::string::npos )
                {
                    const std::string indexStr = configKey.substr( digitStart, digitEnd - digitStart );

                    try
                    {
                        const int index = boost::lexical_cast<int>(indexStr);

                        if( index >= 0 )
                        {
                            // the length of the config key string is expected to be at least digitEnd + 1
                            const std::string identifier = configKey.substr(digitEnd + 1, std::string::npos);

                            if( ! identifier.empty() )
                            {
                                if( indexedConfig.size() < index + 1 )
                                {
                                    // resize with uninitialized shared_ptr
                                    indexedConfig.resize( index + 1 );
                                }

                                boost::shared_ptr<DeviceConfig>& devConfig = indexedConfig[index];

                                if( ! devConfig )
                                {
                                    const std::string name = _name + "_" + prefixLower + boost::lexical_cast<std::string>(index);
                                    devConfig = boost::make_shared<DeviceConfig>( 0, name );
                                }

                                devConfig->insertValue( identifier, p.second );
                            }
                        }
                    }
                    catch( boost::bad_lexical_cast& )
                    {
                        // this config key maybe not for us, let it continue
                    }
                }
            }
        }
    }

    // validate the size and make sure there are no null shared_ptr
    if( indexedConfig.size() != numberOfIndex || std::find(indexedConfig.begin(), indexedConfig.end(), boost::shared_ptr<DeviceConfig>()) != indexedConfig.end() )
    {
        _cashedIndexedConfig[prefixLower] = boost::none;
        return boost::none;
    }

    _cashedIndexedConfig[prefixLower] = indexedConfig;
    return indexedConfig;
}


///////////////////

ConfigurationCategory::ConfigurationCategory( const long ID, const std::string & name, const std::string & type )
    :   _id( ID ),
        _name( name ),
        _type( type )
{

}


void ConfigurationCategory::addItem( const std::string & fieldName, const std::string & value )
{
    _items[ fieldName ] = value;
}


long ConfigurationCategory::getId() const
{
    return _id;
}


std::string ConfigurationCategory::getName() const
{
    return _name;
}


std::string ConfigurationCategory::getType() const
{
    return _type;
}


ConfigurationCategory::const_iterator ConfigurationCategory::begin() const
{
    return _items.begin();
}


ConfigurationCategory::const_iterator ConfigurationCategory::end() const
{
    return _items.end();
}


ConfigurationCategory::const_iterator ConfigurationCategory::find( const std::string & fieldName ) const
{
    return _items.find( fieldName );
}

///////////////////

Configuration::Configuration( const long ID, const std::string & name )
    :   _id( ID ),
        _name( name )
{

}


void Configuration::addCategory( const long category )
{
    _categoryIDs.insert( category );
}


long Configuration::getId() const
{
    return _id;
}


std::string Configuration::getName() const
{
    return _name;
}


bool Configuration::hasCategory( const long category ) const
{
    return _categoryIDs.end() != _categoryIDs.find( category );
}


Configuration::const_iterator Configuration::begin() const
{
    return _categoryIDs.begin();
}


Configuration::const_iterator Configuration::end() const
{
    return _categoryIDs.end();
}


}
}
