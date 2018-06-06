#include "precompiled.h"

#include "config_device.h"

#include "DeviceConfigDescription.h"
#include "std_helper.h"

#include <boost/make_shared.hpp>
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string/case_conv.hpp>


namespace Cti {
namespace Config {

const std::string BoolTrue = "true";

std::atomic_size_t DeviceConfig::_instances;

DeviceConfig::DeviceConfig( int configId )
    :   _configId { configId }
{
    // empty
}

int DeviceConfig::getConfigId() const
{
    return _configId;
}

size_t DeviceConfig::getInstanceId() const
{
    return _instanceId;
}

// Inserts a value into the mapping, this is a protected function and is
// not meant to be called by devices
bool DeviceConfig::insertValue( std::string identifier, const std::string & value )
{
    std::pair<ItemsByName::iterator, bool> retVal = _items.insert(ItemsByName::value_type(identifier, value));

    return retVal.second;
}

void DeviceConfig::addCategory( const CategorySPtr & category )
{
    const Category::ItemMap items = category->getItems();

    _items.insert( items.begin(), items.end() );

    const Category::IndexedItemMap indexedItems = category->getIndexedItems();

    _indexedItems.insert( indexedItems.begin(), indexedItems.end() );
}


boost::optional<std::string> DeviceConfig::lookup( std::string key ) const
{
    return Cti::mapFind( _items, key );
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


/** 
Find the value associated with the key and then look it up in the map, and return the result.
Useful for decoding enumerated data from the device configuration 
*/
template <>
boost::optional<std::string> DeviceConfig::findValue<std::string>( const std::string & key, const std::map<std::string, std::string> &map ) const
{
    boost::optional<std::string>  result = lookup( key );

    if( ! result || result->empty() )
    {
        return boost::none;
    }

    return Cti::mapFind( map, result->c_str() );
}

/** 
Find the value associated with the key and then look it up in the map, and return the result.
Useful for decoding enumerated data from the device configuration 
*/
template <>
boost::optional<bool> DeviceConfig::findValue<bool>( const std::string & key, const std::map<std::string, bool> &map ) const
{
    boost::optional<std::string>  result = lookup( key );

    if( ! result || result->empty() )
    {
        return boost::none;
    }

    return Cti::mapFind( map, result->c_str() );
}


/**
Find the value associated with the key and then look it up in the map, and return the result.
Useful for decoding enumerated data from the device configuration
*/
template <>
boost::optional<uint8_t> DeviceConfig::findValue<uint8_t>(const std::string & key, const std::map<std::string, uint8_t> &map) const
{
    boost::optional<std::string>  result = lookup(key);

    if( !result || result->empty() )
    {
        return boost::none;
    }

    return Cti::mapFind(map, result->c_str());
}


/**
Find the value associated with the key and then look it up in the map, and return the result.
Useful for decoding enumerated data from the device configuration 
*/
template <>
boost::optional<long> DeviceConfig::findValue<long>( const std::string & key, const std::map<std::string, long> &map ) const
{
    boost::optional<std::string>  result = lookup( key );

    if( ! result || result->empty() )
    {
        return boost::none;
    }

    return Cti::mapFind( map, result->c_str() );
}


/** 
Find the value associated with the key and then look it up in the map, and return the result.
Useful for decoding enumerated data from the device configuration 
*/
template <>
boost::optional<double> DeviceConfig::findValue<double>( const std::string & key, const std::map<std::string, double> &map ) const
{
    boost::optional<std::string>  result = lookup( key );

    if( ! result || result->empty() )
    {
        return boost::none;
    }

    return Cti::mapFind( map, result->c_str() );
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

boost::optional<DeviceConfig::IndexedItem> DeviceConfig::getIndexedItem( const std::string & prefix )
{
    return mapFind( _indexedItems, prefix );
}


///////////////////


typedef DeviceConfigDescription::ItemDescription ItemDescription;
typedef boost::optional<std::string> OptionalString;


Category::Category( const std::string & type ) :
    _type(type)
{
}


Category::IndexedItem ConstructIndexedItem( const ItemDescription & itemDesc, const std::map<std::string, std::string> & items )
{
    Category::IndexedItem item;

    //  indexed item - check for the count, then validate we have all of the subitems
    OptionalString value = mapFind(items, itemDesc.name);

    if( ! value )
    {
        //  error
    }

    unsigned count = boost::lexical_cast<unsigned>(*value);

    if( ( ! itemDesc.minOccurs || count >= *itemDesc.minOccurs) &&
        ( ! itemDesc.maxOccurs || count <= *itemDesc.maxOccurs) )
    {
        item.resize(count);

        for( unsigned i = 0; i < count; ++i )
        {
            const std::string prefix =
                itemDesc.name
                + "."
                + boost::lexical_cast<std::string>(i)
                + ".";

            for each( const ItemDescription &subItemDesc in itemDesc.elements )
            {
                if( subItemDesc.isIndexed )
                {
                    //  error, we don't support nested indexed items yet
                }

                const std::string qualifiedName = prefix + subItemDesc.name;

                OptionalString subValue = mapFind(items, qualifiedName);

                if( subValue )
                {
                    item[i][subItemDesc.name] = *subValue;
                }
                else
                {
                    //  error
                }
            }
        }
    }
    else
    {
        //  error
    }

    return item;
}


CategorySPtr Category::ConstructCategory( const std::string & type, const std::map<std::string, std::string> & databaseItems )
{
    const DeviceConfigDescription::OptionalCategoryDescription categoryDescription =
            DeviceConfigDescription::GetCategoryDescription( type );

    if( ! categoryDescription )
    {
        return CategorySPtr();
    }

    try
    {
        CategorySPtr category = boost::make_shared<Config::Category>( type );

        for each( const ItemDescription & itemDesc in categoryDescription->elements )
        {
            if( itemDesc.isIndexed )
            {
                category->_indexedItems[itemDesc.name] =
                        ConstructIndexedItem( itemDesc, databaseItems );

                //  error if it doesn't create it?
            }
            else
            {
                if( const boost::optional<std::string> value = mapFind(databaseItems, itemDesc.name) )
                {
                    category->_items[itemDesc.name] = *value;
                }
                else
                {
                    //  error
                }
            }
        }

        return category;
    }
    catch( boost::bad_lexical_cast& )
    {
        //  error
    }

    return CategorySPtr();
}


std::string Category::getType() const
{
    return _type;
}

Category::ItemMap Category::getItems() const
{
    return _items;
}

Category::IndexedItemMap Category::getIndexedItems() const
{
    return _indexedItems;
}


}
}
