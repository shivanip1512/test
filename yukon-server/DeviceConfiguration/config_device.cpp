#include "precompiled.h"

#include <boost/make_shared.hpp>

#include "std_helper.h"
#include "config_device.h"


namespace Cti       {
namespace Config    {


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

DeviceConfig::IndexedConfig DeviceConfig::getIndexedConfig( const std::string &prefix )
{
    // check if we already have this config
    boost::optional<IndexedConfig> cashedIndexedConfig = mapFind( _cashedIndexedConfig, prefix );
    if( cashedIndexedConfig )
    {
        return *cashedIndexedConfig;
    }

    IndexedConfig indexedConfig;

    const size_t prefixLen = prefix.length();

    std::istringstream iss;

    for each( ConfigValueMap::value_type p in _configurationValues )
    {
        const std::string configKey = p.first.getHashStr();

        // check if beginning of string matches prefix
        if( configKey.compare(0, prefixLen, prefix) == 0 )
        {
            // find the first non-digit character
            const size_t digitEnd = configKey.find_first_not_of("0123456789", prefixLen );

            if( digitEnd != std::string::npos )
            {
                int index = -1;
                iss.str( configKey.substr(prefixLen, digitEnd) );
                iss >> index;

                // make sure the index found is valid
                if( index >= 0 )
                {
                    while( indexedConfig.size() < index )
                    {
                        std::string name = _name + "_" + prefix + CtiNumStr( indexedConfig.size() );
                        indexedConfig.push_back( boost::make_shared<DeviceConfig>( 0, name ));
                    }

                    std::string identifier = configKey.substr(digitEnd, std::string::npos);
                    indexedConfig[index]->insertValue( identifier, p.second );
                }
            }
        }
    }

    if( ! indexedConfig.empty() )
    {
        _cashedIndexedConfig[prefix] = indexedConfig;
    }

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
