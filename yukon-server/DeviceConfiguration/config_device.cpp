#include "precompiled.h"
#include "config_device.h"


namespace Cti       {
namespace Config    {


DeviceConfig::DeviceConfig( const long ID, const std::string & name, const std::string & type )
    :   _id(ID),
        _name(name),
        _type(type)
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


bool DeviceConfig::getBoolValue( const std::string & key , bool & value ) const
{
    boost::optional<std::string>    result = lookup( key );

    if ( ! result )
    {
        return false;
    }

    value = ciStringEqual( *result, "true" );
    return true;
}

///////////////////

ConfigurationCategory::ConfigurationCategory( const long ID, const std::string & name, const std::string & type )
    :   _ID( ID ),
        _name( name ),
        _type( type )
{

}


void ConfigurationCategory::addItem( const std::string & fieldName, const std::string & value )
{
    _items[ fieldName ] = value;
}


long ConfigurationCategory::getID() const
{
    return _ID;
}


std::string ConfigurationCategory::getName() const
{
    return _name;
}


ConfigurationCategory::const_iterator ConfigurationCategory::begin() const
{
    return _items.begin();
}


ConfigurationCategory::const_iterator ConfigurationCategory::end() const
{
    return _items.end();
}

///////////////////

Configuration::Configuration( const long ID, const std::string & name )
    :   _ID( ID ),
        _name( name )
{

}


void Configuration::addCategory( const long category )
{
    _categoryIDs.insert( category );
}


long Configuration::getID() const
{
    return _ID;
}


std::string Configuration::getName() const
{
    return _name;
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
