#pragma once

#include "yukon.h"
#include "dllbase.h"
#include "hashkey.h"

#include <boost/shared_ptr.hpp>
#include <boost/optional.hpp>




namespace Cti {

class ConfigManager;

namespace Config {

class IM_EX_CONFIG DeviceConfig
{
    friend class ConfigManager;

protected:

    bool insertValue( std::string identifier, const std::string & value );

private:

    typedef std::map<CtiHashKey, std::string> ConfigValueMap;
    ConfigValueMap _configurationValues;

    long _id;
    std::string _name;
    std::string _type;

    boost::optional<std::string> lookup( std::string key ) const;

public:
    typedef std::vector<boost::shared_ptr<DeviceConfig>> IndexedConfig;

private:
    typedef std::map<std::string, boost::optional<IndexedConfig>> IndexedConfigMap;
    IndexedConfigMap _cashedIndexedConfig;

public:

    DeviceConfig( const long ID, const std::string & name );

    template <typename T>
    boost::optional<T> findValue( const std::string & key ) const;

    std::string getValueFromKey( const std::string & key ) const;
    bool        getLongValue( const std::string & key, long & value ) const;
    long        getLongValueFromKey( const std::string & key ) const;
    double      getFloatValueFromKey( const std::string & key ) const;

    boost::optional<IndexedConfig> getIndexedConfig( const std::string & prefix );
};

typedef boost::shared_ptr< DeviceConfig > DeviceConfigSPtr;

template<> IM_EX_CONFIG boost::optional<std::string> DeviceConfig::findValue<std::string> ( const std::string & key ) const;
template<> IM_EX_CONFIG boost::optional<bool>        DeviceConfig::findValue<bool>        ( const std::string & key ) const;
template<> IM_EX_CONFIG boost::optional<long>        DeviceConfig::findValue<long>        ( const std::string & key ) const;
template<> IM_EX_CONFIG boost::optional<double>      DeviceConfig::findValue<double>      ( const std::string & key ) const;

///////////////////

class IM_EX_CONFIG ConfigurationCategory
{
    typedef std::map<std::string, std::string>  ConfigItemMap;  // FieldName --> Value

    long            _id;
    std::string     _name,
                    _type;
    ConfigItemMap   _items;

public:

    typedef ConfigItemMap::value_type       value_type;
    typedef ConfigItemMap::const_iterator   const_iterator;

    ConfigurationCategory( const long ID, const std::string & name, const std::string & type );

    void addItem( const std::string & fieldName, const std::string & value );

    long        getId() const;
    std::string getName() const;
    std::string getType() const;

    const_iterator begin() const;
    const_iterator end() const;

    const_iterator find( const std::string & fieldName ) const;
};

///////////////////

class IM_EX_CONFIG Configuration
{
    long            _id;
    std::string     _name;
    std::set<long>  _categoryIDs;

public:

    typedef std::set<long>::const_iterator   const_iterator;

    Configuration( const long ID, const std::string & name );

    void addCategory( const long category );

    long        getId() const;
    std::string getName() const;

    bool hasCategory( const long category ) const;

    const_iterator begin() const;
    const_iterator end() const;
};

}
}

